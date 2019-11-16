/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import autoscaling.Monitor;
import autoscaling.MonitorVms;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author fafa
 */
public class MonitorB implements Runnable{
    
    
    public void run(){
        for (int i =0; i< 3; i++)  
            System.out.println("monitorB" + "  i=" +i);
        
        System.out.println("monitorB is done");
        
    }
    
 
}
