/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import autoscaling.ExecutorSimple;
import autoscaling.SurplusVmSelection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import log.Log;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;

/**
 *
 * @author aslanpour
 */
public class test {
    static int vmIndexGenerator = 0;
    
    public static void main(String[] args) {
        performScaleUp(DefaultSettings.INITIAL_WEB_SERVERS, 
                                    Integer.valueOf(DefaultSettings.EXECUTOR_SCALING_FLAVOR_ID));
        performScaleDown(2);
    }
    
    static DefaultSettings.SurplusVMSelectionPolicy surplusVMSelectionPolicy = DefaultSettings.SurplusVMSelectionPolicy.THE_OLDEST;
    
    public static DefaultSettings.SurplusVMSelectionPolicy getSurplusVMSelectionPolicy() {
        return surplusVMSelectionPolicy;
    }
    public static void performScaleDown(int stepSize){
        try {
            //OpenStack Authentication
            String OS_TOKEN = authentication();
            
            for (int i = 0; i < stepSize; i++){
                HttpClient httpClient = HttpClients.createDefault();
                HttpDelete httpDelete = new HttpDelete();
                // select surplus vm
                Vm vm = SurplusVmSelection.policy(getSurplusVMSelectionPolicy(), Main.vmsProvisioned);
                
                //set URI
                URI uri = new URI(DefaultSettings.OS_COMPUTE_API + "/servers" + "/" + vm.getOsServerID());
                httpDelete.setURI(uri);
                // Set a timeout for the request
                RequestConfig config = RequestConfig.custom()
                                .setConnectTimeout(30000).build();
                httpDelete.setConfig(config);

                httpDelete.setHeader("Accept", "application/json");
                httpDelete.setHeader("Content-type", "application/json");
                httpDelete.addHeader("X-Auth-Token", OS_TOKEN);

                HttpResponse httpResponse = httpClient.execute(httpDelete);
                if (httpResponse.getStatusLine().getStatusCode() != 204)
                    System.out.println("Server was not Deleted.");

                Header[] responseHeader = httpResponse.getAllHeaders(); 
                String headerValue = responseHeader[1].getValue();
                String []headerValueSplitter = headerValue.split("/");

                HeaderElement[] df = responseHeader[1].getElements();
                httpDelete.releaseConnection();
                httpDelete.reset();

                // update haproxy, needsthread ???
//                haproxyReconfigurationLocally("REMOVE", vm.getName(), vm.getPrivateIP());
                
                // remove from vmsProvisioned
                Vm tmpVm = vm;
                Main.vmsProvisioned.remove(vm);
                //release th ip
                allocatedIPs.remove(Integer.valueOf((vm.getPrivateIP().replace(".", "-")).split("-")[3]));
                //add to vmsDeprovisioned
                tmpVm.setStatus("Destroyed");
                tmpVm.setTimeDestroyed(Log.getTimestamp());
                tmpVm.billCalculator();
                Main.vmsDeprovisioned.add(tmpVm);
            }
            
            
        } catch (ClientProtocolException e) {
                e.printStackTrace();
        } catch (IOException e) {
                e.printStackTrace();
        } catch (URISyntaxException e){
            e.printStackTrace();
        }
    }
    
    public static int generateVmIndex(){
        vmIndexGenerator++;
        return vmIndexGenerator;
    }
    
    static protected List<Integer> allocatedIPs = new ArrayList<Integer>();
    
    public static String allocateIP(){
        // get net IP
        String netIP = DefaultSettings.netIP;
        
        // get host IP
        for (int ip = 100; ip < 254; ip++){
            if (allocatedIPs.contains(ip) == false){
                allocatedIPs.add(ip);
                return (netIP + String.valueOf(ip));
            }
        }
        
        return null; // error
    }
    
    
  
    
    public static void performScaleUp(int stepSize, int flavorID){
        try{
            //OpenStack Authentication
            String OS_TOKEN = authentication();
            
            for (int i = 0; i < stepSize; i++){
                HttpClient httpClient = HttpClients.createDefault();
                
                HttpPost httpPost = new HttpPost();

                //set URI
                URI uri = new URI(DefaultSettings.OS_COMPUTE_API + "/servers");
                httpPost.setURI(uri);
                // Set a timeout for the request
                RequestConfig config = RequestConfig.custom()
                                .setConnectTimeout(30000).build();
                httpPost.setConfig(config);

                String vmIndex = String.valueOf(generateVmIndex());
                String vmName = "webserver" + "#" + vmIndex;
                // allocate an ip and add it to allocatedIps list
                String ip = allocateIP();
                
                String json = "{\"server\": {"
                    + "\"min_count\": 1, "
                    + "\"flavorRef\": \"" + String.valueOf(flavorID) + "\", "
                    + "\"name\": \"" + vmName + "\", "
                    + "\"imageRef\": \"" + DefaultSettings.OS_COMPUTE_IMAGE_ID
                    + "\", \"max_count\": 1, "
                    + "\"networks\": [{\"fixed_ip\": \""+ ip + "\", "
                        + "\"uuid\": \"" + DefaultSettings.OS_NEUTRON_NETWORK_UUID_PRIVATE + "\"}],"
    //                + "\"networks\": [{\"uuid\":\"e291c471-deca-4dc6-a593-f2e089bb6d86\"}], "
                    + "\"security_groups\": [{\"name\": \"" + DefaultSettings.OS_NEUTRON_SECURITYGROUP_NAME + "\"}], "
                    + "\"key_name\": \"" + DefaultSettings.OS_COMPUTE_KEYPAIRS_NAME + "\"}}";

                StringEntity entity = new StringEntity(json);
                httpPost.setEntity(entity);
                
                

                httpPost.setHeader("Accept", "application/json");
                httpPost.setHeader("Content-type", "application/json");
                httpPost.addHeader("X-Auth-Token", OS_TOKEN);

                HttpResponse httpResponse = httpClient.execute(httpPost);
                if (httpResponse.getStatusLine().getStatusCode() != 202)
                    System.out.println("Server was not created.");

                Header[] responseHeader = httpResponse.getAllHeaders(); 
                String headerValue = responseHeader[1].getValue();
                String []headerValueSplitter = headerValue.split("/");
                String OS_SERVER_ID = headerValueSplitter[headerValueSplitter.length - 1];

                httpPost.releaseConnection();
                httpPost.reset();

                //reconfigure haproxy, it needs thread???
//                haproxyReconfigurationLocally("ADD", vmName, ip);
                
                // add to vmsprovisioned
                    //create vm object
                Vm vm = new Vm(Integer.valueOf(vmIndex), vmName, OS_SERVER_ID, DefaultSettings.OS_COMPUTE_IMAGE_ID, ip, 
                                "", String.valueOf(flavorID), "runnung", "nova", Log.getTimestamp());
                                
                Main.vmsProvisioned.add(vm);
            }
        } catch (ClientProtocolException e) {
                e.printStackTrace();
        } catch (IOException e) {
                e.printStackTrace();
        } catch (URISyntaxException ex){
            
        }
    }
    
    public static void haproxyReconfigurationLocally(String addRemove, String serverName, String serverIP) {
        try {
            //Script Inputs: COMMAND {ADD or REMOVE}, SERVER_NAME, SERVER_IP
            ////sudo bash /home/ubuntu/haproxy_reconfiguration.sh PARA1 PARA2 PARA3
            String command[]= {"sudo", 
                "bash", 
                DefaultSettings.FILE_LOCATION_HAPROXY_RECONFIGURATION, 
                addRemove, 
                serverName, 
                serverIP};
            
            ProcessBuilder builder = new ProcessBuilder(command);

            builder.redirectErrorStream(true); // redirect error stream to
            // output stream
            builder.redirectOutput(ProcessBuilder.Redirect.INHERIT);

            Process p = null;

            try {
                p = builder.start();
                StringBuilder output = new StringBuilder();

		BufferedReader reader = new BufferedReader(
				new InputStreamReader(p.getInputStream()));

		String line;
		while ((line = reader.readLine()) != null) {
			output.append(line + "\n");
		}

		int exitVal = p.waitFor();
		if (exitVal == 0) {
			System.out.println("Success!");
			System.out.println(output);
			System.exit(0);
		} else {
			//abnormal...
		}
            } catch (IOException e) {
                    System.out.println(e);
            }
            p.waitFor();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    public static String authentication(){
        try {
            //Create Token
            HttpClient httpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(DefaultSettings.OS_IDENTITY_API + "/auth/tokens?nocatalog");
            
            // Set a timeout for the request
            RequestConfig config = RequestConfig.custom()
                    .setConnectTimeout(30000).build();
            httpPost.setConfig(config);
            
            String json = "{ \"auth\": { \"identity\": { \"methods\": [ \"password\" ], \"password\": "
                    + "{ \"user\": { \"domain\": { \"name\": \"Default\" }, \"name\": \"mohammad\", "
                    + "\"password\": \"GreenCloud\" } } }, \"scope\": { \"project\": "
                    + "{ \"domain\": { \"name\": \"Default\" }, \"name\": \"Auto-scaling\" }}}}";
            
            StringEntity entity = new StringEntity(json);
            httpPost.setEntity(entity);
            
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            
            HttpResponse httpResponse = null;
            try {
                httpResponse = httpClient.execute(httpPost);
            } catch (IOException ex) {
                Logger.getLogger(ExecutorSimple.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (httpResponse.getStatusLine().getStatusCode() != 201)
                System.out.println("Token was not created properly.");
            
            Header[] responseHeader = httpResponse.getAllHeaders();
            String tokenName = responseHeader[2].getValue();
            
            httpPost.releaseConnection();
            httpPost.reset();
            
            // The authentication code is returned in the HTTP header
            return tokenName;
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(ExecutorSimple.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null; //error
    }
}
