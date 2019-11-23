/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import autoscaling.Analyzer;
import autoscaling.ExecutorSimple;
import autoscaling.Monitor;
import autoscaling.MonitorHaproxy;
import autoscaling.MonitorVms;
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
import log.AnalyzerHistory;
import log.Log;
import log.MonitorHistory;
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
    public static double[] result = new double[10];
    
    public static void main(String[] args) throws InterruptedException  {
        
        String ss = DefaultSettings.Method.SIMPLE.name();
        NewClass nCls = new NewClass();
        Thread thr = new Thread(nCls);
        thr.start();
        thr.join();
        int dd = nCls.index;
        dd =dd;
    }
    
}


