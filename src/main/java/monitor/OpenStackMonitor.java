/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package monitor;

import config.ASProperties;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import static java.lang.Math.log;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import static org.apache.log4j.LogMF.log;
import sun.misc.BASE64Encoder;

/**
 *
 * @author aslanpour
 */
public class OpenStackMonitor {
    public static final String OS_COMPUTE_URI = "http://192.168.0.1:8774/v2.1/192ac977c8034bbd947efac59cdb4725";
    public static final String OS_COMPUTE_URI_V2_48 = "http://192.168.0.1:8774/v2.48/192ac977c8034bbd947efac59cdb4725";
    public static final String OS_COMPUTE_URI_V3 = "http://192.168.0.1:8774/v3/192ac977c8034bbd947efac59cdb4725";
    public static final String OS_IDENTITY_URI = "http://192.168.0.1:5000/v3";
    public static String OS_TOKEN;
    public static HttpClient httpClient = HttpClients.createDefault();
    public static final String WEB_SERVER_SNAPSHOT_ID = "98cbc1ae-9607-45c1-8632-f2879bc21852";
    
    public static void main(String[] args) {
        try{
            authentication();
            try {
                performeScaleUp();
            } catch (URISyntaxException ex) {
                Logger.getLogger(OpenStackMonitor.class.getName()).log(Level.SEVERE, null, ex);
            }
//            performScaleDown();
            
            
            
        } catch (ClientProtocolException e) {
                e.printStackTrace();
        } catch (IOException e) {
                e.printStackTrace();
        }
        
    }
    
    public static void authentication() throws UnsupportedEncodingException, IOException{
        //Create Token
            HttpPost httpPost = new HttpPost(OS_IDENTITY_URI + "/auth/tokens?nocatalog");

            // Set a timeout for the request
            RequestConfig config = RequestConfig.custom()
                            .setConnectTimeout(30000).build();
            httpPost.setConfig(config);

            String json = "{ \"auth\": { \"identity\": { \"methods\": [ \"password\" ], \"password\": { \"user\": { \"domain\": { \"name\": \"Default\" }, \"name\": \"mohammad\", \"password\": \"GreenCloud\" } } }, \"scope\": { \"project\": { \"domain\": { \"name\": \"Default\" }, \"name\": \"Auto-scaling\" }}}}";
            StringEntity entity = new StringEntity(json);
            httpPost.setEntity(entity);
            
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            
            HttpResponse httpResponse = httpClient.execute(httpPost);
            if (httpResponse.getStatusLine().getStatusCode() != 201)
                System.out.println("Token was not created properly.");

            Header[] responseHeader = httpResponse.getAllHeaders();
            String tokenName = responseHeader[2].getName();
            // The authentication code is returned in the HTTP header
            OS_TOKEN = responseHeader[2].getValue();
            
            httpPost.releaseConnection();
            httpPost.reset();
    }
    
    public static void performeScaleUp() throws URISyntaxException{
        try{
            String IP_ADDRESS = "10.10.0.24";
            HttpPost httpPost = new HttpPost();

            //set URI
            URI uri = new URI(OS_COMPUTE_URI + "/servers");
            httpPost.setURI(uri);
            // Set a timeout for the request
            RequestConfig config = RequestConfig.custom()
                            .setConnectTimeout(30000).build();
            httpPost.setConfig(config);
            
            String json = "{\"server\": {"
                + "\"min_count\": 1, "
                + "\"flavorRef\": \"3\", "
                + "\"name\": \"foobar4\", "
                + "\"imageRef\": \"" + WEB_SERVER_SNAPSHOT_ID
                + "\", \"max_count\": 1, "
                + "\"networks\": [{\"fixed_ip\": \""+ IP_ADDRESS + "\", \"uuid\": \"e291c471-deca-4dc6-a593-f2e089bb6d86\"}],"
//                + "\"networks\": [{\"uuid\":\"e291c471-deca-4dc6-a593-f2e089bb6d86\"}], "
                + "\"security_groups\": [{\"name\": \"AutoscalingSecurityGroup\"}], "
                + "\"key_name\": \"mykeypair\"}}";
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
            String vmID = headerValueSplitter[headerValueSplitter.length - 1];
            
            httpPost.releaseConnection();
            httpPost.reset();
            
        } catch (ClientProtocolException e) {
                e.printStackTrace();
        } catch (IOException e) {
                e.printStackTrace();
        } catch (URISyntaxException ex){
            Logger.getLogger(OpenStackMonitor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void performScaleDown(){
        try {
            HttpDelete httpDelete = new HttpDelete();
            String vmID = surplusVmSelection();
            //set URI
            URI uri = new URI(OS_COMPUTE_URI + "/servers" + "/" + vmID);
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
            
        } catch (ClientProtocolException e) {
                e.printStackTrace();
        } catch (IOException e) {
                e.printStackTrace();
        } catch (URISyntaxException e){
            e.printStackTrace();
        }
    }
    
    private static String surplusVmSelection(){
        String surplusVmSelectionPolicy;
        String surplusVmID = "2b1cf3fb-5a44-406d-a359-e6d3abc0170e";
        return surplusVmID;
    }
}
