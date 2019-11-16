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
public class MonitorA implements Runnable{
    
    
    public void run(){
        // create sub threads
        Thread[] thread = new Thread[5];

        for (int i = 0; i < 5; i++){
            thread[i] = new Thread( 
                    new MonitorA.CpuUtilizationCalculator(i));
        }
        //run threads
        for (int i = 0; i <thread.length; i++){
           thread[i].start();
        }
        //wait for all threads being done
        for (int i = 0; i <thread.length; i++){
            try {
                thread[i].join();
            } catch (InterruptedException ex) {
                Logger.getLogger("ex------monitor a- join");
            }
        }
        System.out.println("Monitor A is done. ");
       
        
    }
    
    //-------------------------------------------
    class CpuUtilizationCalculator implements Runnable{
        private int index;

        private CpuUtilizationCalculator(int index) {
            this.index = index;
        }
        
        public void run(){
            test.result[index] = getA(index);
        }
        
        public synchronized double getA(int index){
            double out;
            switch(index){
                case 0: out=00;break;
                case 1: out=11; break;
                case 2: out=22;break;
                case 3:out=33;break;
                default:out=44;break;
            }
            return out;
        }
    }
}
