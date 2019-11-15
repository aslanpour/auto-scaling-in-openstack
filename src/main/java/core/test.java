/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import autoscaling.ExecutorSimple;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import log.Log;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;

/**
 *
 * @author aslanpour
 */
public class test {
    public static void main(String[] args) {
        performScaleUp(1, 3);
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

                String vmName = "webserver4";//
                String ip = "10.10.0.52";  
                
                String json = "{\"server\": {"
                    + "\"min_count\": 1, "
                    + "\"flavorRef\": \"" + String.valueOf(flavorID) + "\", "
                    + "\"name\": \"" + vmName + "\", "
                    + "\"imageRef\": \"" + "e0cd13f1-c644-4376-99ce-02a130382d7d"
                    + "\", \"max_count\": 1, " 
                        + " \"user_data\":" + " #cloud-config\n" +
"password: ubuntu\n" +
"chpasswd: { expire: False }\n" +
"ssh_pwauth: True "
//                        + "\"user_data\"" + ": " + "#cloud-config\n" +
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
            }
        } catch (ClientProtocolException e) {
                e.printStackTrace();
        } catch (IOException e) {
                e.printStackTrace();
        } catch (URISyntaxException ex){
            
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
