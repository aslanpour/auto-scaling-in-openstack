/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package autoscaling;

import core.DefaultSettings;
import core.Main;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.json.simple.parser.ParseException;
import log.Log;

/**
 *
 * @author fafa
 */
public class MonitorHaproxy implements Runnable{
    // vm name and its current sessions
    private String [][] currentSessionsPerVm;
    private int currentSessionsSum;
    private double respnseTimeAvg;
    
    public void run (){
        Log.printLine3("Monitor Haproxy is active...");
        try {
            currentSessionsPerVm = new String[Main.vmsProvisioned.size()][2];
            currentSessionsSum = 0;
            respnseTimeAvg = 0;
            
            String stats = callAPI();
            // parse stats and set metrics
            parse(stats);

        } catch (ParseException ex) {
            Logger.getLogger(MonitorHaproxy.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private String callAPI() {
        String msg = "";
        int timeout = 30000;
        try {
            HttpClient client = HttpClients.createDefault();

            String req = DefaultSettings.HAPROXY_API;
            HttpGet request = new HttpGet(req);

            // Set a timeout for the request
            RequestConfig config = RequestConfig.custom()
                            .setConnectTimeout(timeout).build();
            request.setConfig(config);

//            String enc = "username" + ":" + "password";
//            
//            request.addHeader("Authorization",
//                            "Basic " + new BASE64Encoder().encode(enc.getBytes()));
//
//            request.addHeader("Accept", "application/csv");

            HttpResponse response = client.execute(request);

            if (response.getStatusLine().getStatusCode() != 200) {
                    throw new RuntimeException("Failed : HTTP error code :"
                                    + response.getStatusLine().getStatusCode());
            }
            BufferedReader rd = new BufferedReader(new InputStreamReader(
                            response.getEntity().getContent()));
            String line = "";
            while ((line = rd.readLine()) != null) {
                    msg += line + "LineBreaker";
            }
        } catch (ClientProtocolException e) {
                e.printStackTrace();
        } catch (IOException e) {
                e.printStackTrace();
        }
        return msg;
    }
    
    private void parse(String msg) throws ParseException {
        
        String lines[] = msg.split("LineBreaker");
        
        String[] titles = null;
        String[] frontend = null;
        //line index 2 is the first backend server
        String [][] backend = new String[Main.vmsProvisioned.size()][lines[2].split(",").length];// 2 or more untill n-1 (they are web servers) 
        String[] backendTotal = null;
        
        for(int row = 0; row < lines.length; row++){
            if (row==0){ // title 
                titles = lines[row].split(",");
            }else if (row == 1){ // frontend 
                frontend = lines[row].split(",");
            }else if (row != lines.length - 1){ // backend per server 
                backend[row - 2] = lines[row].split(",");
            }else{ //backend total 
                backendTotal = lines[row].split(",");
            }
        }
        
        String currentSession = "scur";
        String totalTime = "ttime";
        // get metrics' indexes from title row
        int indexCurrentSession = -1;
        int indexTotalTime = -1;
        for (int index = 0; index < titles.length; index++){
            if (titles[index].equals(currentSession))
                indexCurrentSession = index;
            else if (titles[index].equals(totalTime))
                indexTotalTime = index;
        }
        // set current sessions per vms
        for (int backendRowsIndex = 0; backendRowsIndex < backend.length; backendRowsIndex++){
            String name = backend[backendRowsIndex][1]; // vm name
            String sessions = backend[backendRowsIndex][indexCurrentSession];// current sessions

            currentSessionsPerVm[backendRowsIndex] = new String[]{name, sessions};
        }
        
        //set total response time
        respnseTimeAvg = Double.valueOf(backendTotal[indexTotalTime]);
        // set sum current sessions
        currentSessionsSum = Integer.valueOf(backendTotal[indexCurrentSession]);
    }

    public String[][] getCurrentSessionsPerVm() {
        return currentSessionsPerVm;
    }

    public int getCurrentSessionsSum() {
        return currentSessionsSum;
    }

    
    public double getRespnseTimeAvg() {
        return respnseTimeAvg;
    }
    
    
}
