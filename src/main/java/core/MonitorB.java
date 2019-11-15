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
        // create sub threads
        Thread[] thread = new Thread[Main.vmsProvisioned.size()];
        int i = 0;
        for (Vm vm : Main.vmsProvisioned){
            thread[i] = new Thread( 
                    new MonitorVms.CpuUtilizationCalculator(i, vm.getIndex(), vm.getName(), vm.getPrivateIP()));
            i++;    
        }
        //run threads
        for (i = 0; i <thread.length; i++){
           thread[i].start();
        }
        //wait for all threads being done
        for (i = 0; i <thread.length; i++){
            try {
                thread[i].join();
            } catch (InterruptedException ex) {
                Logger.getLogger(MonitorVms.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
    
    //-------------------------------------------
    class CpuUtilizationCalculator implements Runnable{
        private int arrayIndex;
        private int vmIndex;
        private String vmName;
        private String privateIP;

        private CpuUtilizationCalculator(int arrayIndex, int vmIndex, String vmName,String privateIP) {
            this.arrayIndex = arrayIndex;
            this.vmIndex = vmIndex;
            this.vmName = vmName;
            this.privateIP = privateIP;
        }
        
        public void run(){

        }
        
            /**
         * Make SSH from local to remote server, run a bash file to get CPU idle percentage, 
         * return the output to local and then to java program
         * @param serverName
         * @param serverIP
         * @return 
         */
        private double getCpuUtilization(String serverName, String serverIP){
            double cpuIdle = 0;
            double cpuUtilization = 0;

            try {
                Process p = null;
               // Get CPU idle percentage
                p = Runtime.getRuntime().exec("sshpass -p " + DefaultSettings.WEB_SERVER_PASSWORD + 
                " ssh -o " + "StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null "
                + serverName + "@" + serverIP + " sudo bash " 
                + DefaultSettings.FILE_LOCATION_CPU_UTILIZATION);

                p.waitFor();
                BufferedReader buf = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String line = "";
                String output = "";
                int counter = 0;
                while ((line = buf.readLine()) != null) {
                    // return the bash output
                    cpuIdle = Double.valueOf(line);
                    counter++;
                    output += line + "\n";
                }
                if (counter> 1) System.out.println("ERROR - getCpuUtilization returned more than one output");
                System.out.println(output);
                p = null;

            } catch (InterruptedException ex) {
                Logger.getLogger(Monitor.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Monitor.class.getName()).log(Level.SEVERE, null, ex);
            }

            // change idle percentage to utilization
            cpuUtilization = 100 - cpuIdle;
            // get only two decimal places
            cpuUtilization = Double.valueOf(new DecimalFormat("#.##").format(cpuUtilization));

            return cpuUtilization;

        }
    }
}
