package monitor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.json.simple.parser.ParseException;

import provision.ProvisionerHaproxy;
import sun.misc.BASE64Encoder;
import config.ASProperties;

public class HaProxyMonitor extends Monitor {

	public HaProxyMonitor(String monitorName, int monitorInterval) {
		super(monitorName, monitorInterval);
	}

	@Override
	public void doMonitoring() throws Exception {
		long requests = 0;
		String msg = callAPI(
				ASProperties.RESOLUTION_HAPROXY_STAT_CHECKING.getValueAsInt(),
				20000);
		requests = parse(msg);
		ProvisionerHaproxy.autoscale(requests);

	}

	public long parse(String msg) throws ParseException {
		System.out.println(msg);
		String lines[] = msg.split("http-in");
		for(String line:lines){
			String[] items = line.split(",");
			if(items[1].equals("BACKEND"))
				return Long.parseLong(items[7]);
				
		}
		return -1;
	}

	private String callAPI(int resolution, int timeout) {
		String msg = "";

		try {
			
			
			///**
//			 Registry<ConnectionSocketFactory> reg = RegistryBuilder.<ConnectionSocketFactory>create()
//        .register("http", PlainConnectionSocketFactory.INSTANCE)
//        .build();
//PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(reg);
//HttpClient client = HttpClients.custom()
//        .setConnectionManager(cm)
//        .build();
//
//InetSocketAddress socksaddr = new InetSocketAddress("mysockshost", 4000);
//HttpClientContext context = HttpClientContext.create();
//context.setAttribute("socks.address", socksaddr);
//
//HttpHost target = new HttpHost("localhost", 80, "http");
			 //*/

			
			
			
			HttpClient client = HttpClients.createDefault();

			String req = "http://localhost/haproxy?stats;csv";
			log.debug(req);

			HttpGet request = new HttpGet(req);

			// Set a timeout for the request
			RequestConfig config = RequestConfig.custom()
					.setConnectTimeout(timeout).build();
			request.setConfig(config);

			String enc = ASProperties.CREDENTIALS_HAPROXY_USERNAME.getValue()
					+ ":"
					+ ASProperties.CREDENTIALS_HAPROXY_PASSWORD.getValue();

			request.addHeader("Authorization",
					"Basic " + new BASE64Encoder().encode(enc.getBytes()));

			request.addHeader("Accept", "application/csv");

			//HttpResponse response = client.execute(target,request);
			HttpResponse response = client.execute(request);
			log.debug(response);
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
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return msg;
	}

}
