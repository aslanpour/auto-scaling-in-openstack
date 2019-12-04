package autoscaling;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import core.DefaultSettings;
import core.Main;
import core.Vm;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import log.Log;
import log.MonitorHistory;

/**
 *
 * @author aslanpour
 */
public class Monitor {
    
    private ArrayList<MonitorHistory> monitorHistory;
    private double cpuUtilizationAvg;
    // Vm ID and cpu Utilization
    private double [][] cpuUtilizationPerVm; 
    // Vm ID and current sessions
    private int [][] currentSessions;
    private int currentSessionSum;
    private double responseTimeAvg;
    private int vms;
    private int quarantined;
    
    private int maxProvisionedVM = 0;
    
    public Monitor(){
        this.cpuUtilizationAvg = 0;
        this.responseTimeAvg = 0;
        this.cpuUtilizationPerVm = new double[Main.vmsProvisioned.size()][];
        this.currentSessions = null;
        this.currentSessionSum = 0;
        this.monitorHistory = new ArrayList<MonitorHistory>();
        this.vms = Main.vmsProvisioned.size();
        this.quarantined = 0;
        this.maxProvisionedVM = Main.vmsProvisioned.size();
    }
       
    public void doMonitoring(){
        try {
            Log.printLine1("Monitor", "doMonitoring", "Monitoring is active...");
            cpuUtilizationAvg = 0;
            cpuUtilizationPerVm = new double[Main.vmsProvisioned.size()][2];
            vms = Main.vmsProvisioned.size();
            quarantined = 0;
            
            currentSessions = new int[Main.vmsProvisioned.size()][2];
            currentSessionSum = 0;
            responseTimeAvg = 0;
            
            /*Monitor Vms and Haproxy in parallel */
            //monitor vms
            MonitorVms monitorVms = new MonitorVms();
            Thread monitorVmsThread = new Thread(monitorVms);
            monitorVmsThread.setDaemon(true);
            monitorVmsThread.start();
//            Log.printLine2("Vm monitoring thread started");
            // monitor Haproxy
            MonitorHaproxy monitorHaproxy = new MonitorHaproxy();
            Thread monitorHaproxyThread = new Thread(monitorHaproxy);
            monitorHaproxyThread.setDaemon(true);
            monitorHaproxyThread.start();
//            Log.printLine2("Haproxy monitoring thread started");
            monitorVmsThread.join();
            monitorHaproxyThread.join();
            
            /* monitoring is done */
            Log.printLine2("Monitor", "doMonitoring", "Monitoring is done");
            // return calculated cpu for vms, index 0 is vm index and 1 is its cpu util.
            // vm index is used in surplus vm selection based on vm cpu utilization
            cpuUtilizationPerVm = monitorVms.getCpuUtilizationPerVm();
            for (double[] cpuUtil : cpuUtilizationPerVm){
                cpuUtilizationAvg += cpuUtil[1];
            }
            
            if (Main.vmsProvisioned.size() > 0)
                cpuUtilizationAvg /= Main.vmsProvisioned.size();
            
            // return calculate current sessions and response time
            
            // for current session, replace vm name with vm id
            for (int i = 0; i < monitorHaproxy.getCurrentSessionsPerVm().length; i ++){
                for (Vm vm : Main.vmsProvisioned){
                    if (vm.getName().equals(monitorHaproxy.getCurrentSessionsPerVm()[i][0])){
                        currentSessions[i][0] = vm.getIndex();
                        currentSessions[i][1] = Integer.valueOf(monitorHaproxy.getCurrentSessionsPerVm()[i][1]);
//                        Log.printLine1("current sessions: " + vm.getName() + " " + currentSessions[i][1]);
                        break;
                    }
                }
            }
            
            currentSessionSum = monitorHaproxy.getCurrentSessionsSum();
            
            // set response time avg
            responseTimeAvg = monitorHaproxy.getRespnseTimeAvg();
            
            //set max provisioned vms
            if (vms > maxProvisionedVM)
                maxProvisionedVM = vms;
            
            // write to history
            MonitorHistory monitorHistory = new MonitorHistory(cpuUtilizationAvg, 
                                                                cpuUtilizationPerVm, 
                                                                responseTimeAvg, 
                                                                currentSessions,
                                                                currentSessionSum,
                                                                vms, 
                                                                quarantined);
            getMonitorHistory().add(monitorHistory);
            Log.printLine2("Monitored data:"
                    + "\nCPU util. avg= " + cpuUtilizationAvg 
                    + "\nResponseTime avg= " + responseTimeAvg
                    + "\nCurrentSessions sum= " + currentSessionSum
                    + "\nVms No.= " + vms 
                    + "\nquarantineed Vms No.= " + quarantined
                    + "\nMax Vms No.= " + maxProvisionedVM);
            
        } catch (InterruptedException ex) {
            Logger.getLogger(Monitor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void loadBalancingChecker(){
        // after measurements, check if the load is equally balanced between vms. ?????
    }
    
    

    public ArrayList<MonitorHistory> getMonitorHistory() {
        return monitorHistory;
    }

    public double getCpuUtilizationAvg() {
        return cpuUtilizationAvg;
    }

    public double[][] getCpuUtilizationPerVm() {
        return cpuUtilizationPerVm;
    }

    public int[][] getCurrentSessions() {
        return currentSessions;
    }

    public int getCurrentSessionSum() {
        return currentSessionSum;
    }

    public double getResponseTimeAvg() {
        return responseTimeAvg;
    }

    public int getVms() {
        return vms;
    }

    public int getQuarantined() {
        return quarantined;
    }
    
    public MonitorHistory latestHistory (){
        return getMonitorHistory().get(getMonitorHistory().size() - 1);
    }
}
