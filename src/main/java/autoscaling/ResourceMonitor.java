package autoscaling;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import provision.ProvisionerCPUUTIL;
import sun.misc.BASE64Encoder;
import config.ASProperties;
import core.AutoScalerMain;
import core.Node;
import core.NodeStatus;

public class ResourceMonitor extends MonitorDef {

	public ResourceMonitor(String monitorName, int monitorInterval) {
		super(monitorName, monitorInterval);
	}

	@Override
	public void doMonitoring() throws Exception {
		ArrayList<Node> nodes = AutoScalerMain.getNodes();
		double utilsum = 0;
		synchronized (nodes) {
			for (Node node : nodes) {
				if (node.getStatus().equals(NodeStatus.ON)) {
					utilsum += (1 - get_cpu_idle(node));
				}
			}
		}
		ProvisionerCPUUTIL.autoscale(utilsum);
	}

	private double get_cpu_idle(Node node) throws ParseException {
		String msg = callAPI(node,
				ASProperties.RESOLUTION_CPU_UTILIZATION_SAMPLING
						.getValueAsInt(), 20000);
		return parse(msg, node);
	}

	public double parse(String msg, Node node) throws ParseException {
		JSONObject object = null;

		JSONParser parser = new JSONParser();

		object = (JSONObject) parser.parse(msg);
		JSONArray array = (JSONArray) object.get("items");
		JSONObject obj = (JSONObject) array.get(0);
		Long resolution = (Long) obj.get("resolution");
		String hostname = (String) obj.get("hostname");
		log.info("Resolution:" + resolution);

		JSONArray values = (JSONArray) obj.get("values");
		for (int i = values.size() - 1; i >= 0; i--) {
			Double cpu_idle = (Double) values.get(i);
			if (cpu_idle != null) {
				log.info(hostname + ":cpu_idle:" + cpu_idle);
				node.setCpu_idle(cpu_idle / 100);
				break;
			}
		}

		return node.getCpu_idle();
	}

	@SuppressWarnings("deprecation")
	private String callAPI(Node node, int resolution, int timeout) {
		String msg = "";
		try {

			SSLContextBuilder builder = new SSLContextBuilder();
			builder.loadTrustMaterial(null, new TrustStrategy() {
				@Override
				public boolean isTrusted(X509Certificate[] chain,
						String authType) throws CertificateException {
					return true;
				}
			});

			SSLConnectionSocketFactory sslSF;
			sslSF = new SSLConnectionSocketFactory(builder.build(),
					SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

			HttpClient client = HttpClients.custom().setSSLSocketFactory(sslSF)
					.build();

			String req = "https://api.grid5000.fr/sid/sites/" + node.getSite()
					+ "/metrics/cpu_idle/timeseries?resolution=" + resolution
					+ "&only=" + node.getAddress();
			log.debug(req);

			HttpGet request = new HttpGet(req);

			// Set a timeout for the request
			RequestConfig config = RequestConfig.custom()
					.setConnectTimeout(timeout).build();
			request.setConfig(config);

			String enc = ASProperties.CREDENTIALS_GRID5K_USERNAME.getValue() + ":"
					+ ASProperties.CREDENTIALS_GRID5K_PASSWORD.getValue();

			request.addHeader("Authorization",
					"Basic " + new BASE64Encoder().encode(enc.getBytes()));

			request.addHeader("Accept", "application/json");

			HttpResponse response = client.execute(request);
			//log.debug(response);
			if (response.getStatusLine().getStatusCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code :"
						+ response.getStatusLine().getStatusCode());
			}
			BufferedReader rd = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));
			String line = "";
			while ((line = rd.readLine()) != null) {
				msg += line;
			}
		} catch (KeyManagementException e) {
			e.printStackTrace();
		} catch (ConnectTimeoutException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyStoreException e) {
			e.printStackTrace();
		}
		//log.info(msg);
		return msg;
	}

}
