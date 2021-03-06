/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package autoscaling;

import autoscaling.MonitorVms.CpuUtilizationCalculator;
import core.DefaultSettings;
import core.Main;
import core.Vm;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import log.Log;

/**
 *
 * @author fafa
 */
public class MonitorVms implements Runnable{
    // Vm ID and cpu Utilization
    private double [][] cpuUtilizationPerVm = new double[Main.vmsProvisioned.size()][2];
    
    public void run(){
        // create sub threads
        Thread[] thread = new Thread[Main.vmsProvisioned.size()];
        int i = 0;
        for (Vm vm : Main.vmsProvisioned){
            thread[i] = new Thread( 
                new CpuUtilizationCalculator(i, vm.getIndex(), vm.getName(), vm.getPrivateIP()));
            i++;    
        }
        //run threads
        Log.printLine3("Monitor Vms is active    (starting " + Main.vmsProvisioned.size() + " threads)");
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

    public double[][] getCpuUtilizationPerVm() {
        return cpuUtilizationPerVm;
    }
   
   //---------------------------------------------------------------------------------------------------- 
   
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
        
        @Override
        public void run(){
            cpuUtilizationPerVm [arrayIndex][0] = vmIndex; // vm Index
            cpuUtilizationPerVm [arrayIndex] [1] = getCpuUtilization(privateIP); // cpu utilization
        }
        
            /**
         * Make SSH from local to remote server, run a bash file to get CPU idle percentage, 
         * return the output to local and then to java program
         * @param serverName
         * @param serverIP
         * @return 
         */
        private double getCpuUtilization(String serverIP){
            double cpuIdleAvg = 0;
            double[] cpuIdleList = new double[Integer.valueOf(DefaultSettings.CPU_LOG_ITEMS)];
            double cpuUtilization = 0;
            
            try {
                Process p = null;
               // Get CPU idle percentage
                p = Runtime.getRuntime().exec("sshpass -p " + DefaultSettings.WEB_SERVER_PASSWORD + 
                " ssh -o " + "StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null "
                + DefaultSettings.WEB_SERVER_USERNAME + "@" + serverIP + 
                " -i " + DefaultSettings.FILE_LOCATION_HAPROXY_PRIVATE_KEY 
                + " sudo " + "tail -n " + DefaultSettings.CPU_LOG_ITEMS + " " + DefaultSettings.FILE_LOCATION_CPU_UTILIZATION);
//                + " sudo bash " + DefaultSettings.FILE_LOCATION_CPU_UTILIZATION);
//                p.waitFor(10, TimeUnit.DAYS)
                p.waitFor();
                BufferedReader buf = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String line = "";
                String output = "";
                int counter = 0;
                while ((line = buf.readLine()) != null) {
                    // return the bash output
                    cpuIdleList[counter] = Double.valueOf(line);
//                    System.out.println("cpu " + counter + "= " + cpuIdleList[counter]);
                    counter++;
                    output += line + "\n";
                }
                if (counter> Integer.valueOf(DefaultSettings.CPU_LOG_ITEMS)) // remove > and check time out for when the vm is not ready yet (see if java waits for the timeout or runs)?????
                                                                                // add -o ConnectTimeout=10
                    System.out.println("ERROR - getCpuUtilization returned more than one output");
                else if (counter < Integer.valueOf(DefaultSettings.CPU_LOG_ITEMS)){
                    double[] cpuIdleListTmp = new double[counter];
                    for(int i=0; i <counter; i++)
                        cpuIdleListTmp[i] = cpuIdleList[i];
                    
                    cpuIdleList = cpuIdleListTmp.clone();
                }
                p = null;

            } catch (InterruptedException ex) {
                Logger.getLogger(Monitor.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Monitor.class.getName()).log(Level.SEVERE, null, ex);
            }

            //calculate average cpu idle
            cpuIdleAvg = Main.getAnalyzer().calculateWeightedMovingAverage(cpuIdleList);
            
            // change idle percentage to utilization
            cpuUtilization = 100 - cpuIdleAvg;
            // get only two decimal places
            cpuUtilization = Double.valueOf(new DecimalFormat("#.##").format(cpuUtilization));
            
//            Log.printLine4("CpuUtilizationCalculator", "getCpuUtilization", 
//                    "cpu util:  " + vmName + " is " + cpuUtilization + " %");
            return cpuUtilization;

        }
    }
    
}
