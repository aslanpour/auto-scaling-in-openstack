/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package log;

import core.DefaultSettings;
import core.Main;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author fafa
 */
public class SaveResults {

    public static DecimalFormat dft = new DecimalFormat("#.##");
    
    public static void printToConsole(){
        Log.printLine1("SaveResults", "printToConsole", "Experimental Results:");
        // monitor
        Log.printLine2("Monitoring Metrics: ");
        ArrayList<MonitorHistory> monitorHistory = Main.getMonitor().getMonitorHistory();
        double[] cpuUtil = new double[monitorHistory.size()];
        double[] responseTime = new double[monitorHistory.size()];
        int maxRunningVms = 0;
        
        for (int i=0; i < monitorHistory.size(); i++){
            cpuUtil[i] = monitorHistory.get(i).getCpuUtilizationAvg();
            responseTime[i] = monitorHistory.get(i).getResponseTimeAvg();
            if (monitorHistory.get(i).getVms() > maxRunningVms)
                maxRunningVms = monitorHistory.get(i).getVms();
        }
        DescriptiveStatistics.analyze(cpuUtil, "Cpu Utilization");
        DescriptiveStatistics.analyze(responseTime, "Response Time");
        Log.printLine3("Max Running Vms: " + maxRunningVms);
        
        //analyze
        Log.printLine2("Analyzing Metrics: ");
        ArrayList<AnalyzerHistory> analyzerHistory = Main.getAnalyzer().getHistoryList();
        double[] cpuUtilAnalyzed = new double[analyzerHistory.size()];
        double[] responseTimeAnalyzed = new double[analyzerHistory.size()];
        
        for (int i = 0; i < analyzerHistory.size(); i ++){
            cpuUtilAnalyzed[i] = analyzerHistory.get(i).getCpuUtilization();
            responseTimeAnalyzed[i] = analyzerHistory.get(i).getResponseTime();
        }
        
        DescriptiveStatistics.analyze(cpuUtilAnalyzed, "Analyzed Cpu Utilization");
        DescriptiveStatistics.analyze(responseTimeAnalyzed, "Analyzed Response Time");
        
        // planner
        Log.printLine2("Planning Metrics: ");
        ArrayList<PlannerHistory> plannerHistory = Main.getPlanner().getHistoryList();
        int sumScaleUpDecision = 0;
        int sumScaleDownDecision = 0;
        for (PlannerHistory history : plannerHistory){
            if (history.getDecision() == DefaultSettings.PlannerDecision.SCALE_UP)
                sumScaleUpDecision++;
            else if (history.getDecision() == DefaultSettings.PlannerDecision.SCALE_DOWN)
                sumScaleDownDecision++;
        }
        
        Log.printLine3("Sum scale up decisions: " + sumScaleUpDecision);
        Log.printLine3("Sum scale down decisions: " + sumScaleDownDecision);
        
        // Executor
        Log.printLine2("Executing Metrics: ");
        ArrayList<ExecutorHistory> executorHistory = Main.getExecutor().getHistoryList();
        int sumProvisionedVm = 0;
        int sumDeprovisionedVm = 0;
        for (ExecutorHistory history : executorHistory){
            sumProvisionedVm += history.getProvisioned();
            sumDeprovisionedVm += history.getDeprovisioned();
        }
        Log.printLine3("Sum provisioned Vms: " + sumProvisionedVm);
        Log.printLine3("Sum deprovisioned Vms: " + sumDeprovisionedVm);
        
        // Cost
        Log.printLine2("Cost Metrics: ");
        double sumCost = 0;
        for (core.Vm vm : Main.vmsDeprovisioned){
            sumCost += vm.getBill();
        }
        Log.printLine3("Sum Cost: " + sumCost + " $");
        Log.printLine1("vms No. in vmsDeprovisioned are " + Main.vmsDeprovisioned.size());
        if (Main.vmsProvisioned.size() > 0)
            Log.printLine1("Error Error----vmsProvisioned has items");
        // count all requests (GET, 200, 400, 404, 500, 503)??????????
        // from wikijector server
        // ssh to wikijector and run grep -o 'GET' path/mylog.log | wc -l
        // ip and file name from DefaultSettings
    }
    
    public static void saveMonitorHistory(ArrayList<MonitorHistory> historyList, String filePath){
        Log.printLine1("SaveResults", "saveMonitorHistory", "Save monitoring history to CSV file");
        ArrayList dataList = new ArrayList();
        for (MonitorHistory history : historyList){
            double cpuUtilizationAvg = history.getCpuUtilizationAvg();
            double responseTimeAvg = history.getResponseTimeAvg();
            int sessions = 0;
            //sum sessions
            for (int i = 0; i < history.getSessions().length; i++){
                sessions += history.getSessions()[i][1];
            }
            
            int vms = history.getVms();
            int quarantineeVms = history.getQuarantineeVms();

            ArrayList<Double> row = new ArrayList<>();
            row.add((double)history.getYear());
            row.add((double)history.getDay());
            row.add((double)history.getHour());
            row.add((double)history.getMinute());
            row.add(cpuUtilizationAvg);
            row.add(responseTimeAvg);
            row.add((double)sessions);
            row.add((double) vms);
            row.add((double) quarantineeVms);
            
            dataList.add(row);
        }
        
        try {
            ReadWriteCSV.writeCSV(dataList, filePath, "monitor_log.csv");
        } catch (IOException ex) {
            Logger.getLogger(SaveResults.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public static void saveAnalyzerHistory(ArrayList<AnalyzerHistory> historyList, String filePath){
        Log.printLine1("SaveResults", "saveAnalyzerHistory", "Save analyzing history to CSV file");
        ArrayList dataList = new ArrayList();
        for (AnalyzerHistory history : historyList){
            double cpuUtilizationAvg = history.getCpuUtilization();
            double responseTimeAvg = history.getResponseTime();
            double requests = history.getRequests();
            
            ArrayList<Double> row = new ArrayList<>();
            row.add((double)history.getYear());
            row.add((double)history.getDay());
            row.add((double)history.getHour());
            row.add((double)history.getMinute());
            row.add(cpuUtilizationAvg);
            row.add(responseTimeAvg);
            row.add(requests);
            
            dataList.add(row);
        }
        
        try {
            ReadWriteCSV.writeCSV(dataList, filePath, "analyzer_log.csv");
        } catch (IOException ex) {
            Logger.getLogger(SaveResults.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public static void savePlannerHistory(ArrayList<PlannerHistory> historyList, String filePath){
        Log.printLine1("SaveResults", "savePlannerHistory", "Save planning history to CSV file");
        ArrayList dataList = new ArrayList();
        for (PlannerHistory history : historyList){
            double decision = history.getDecision().ordinal();
            double stepSize = history.getStepSize();
            
            ArrayList<Double> row = new ArrayList<>();
            row.add((double)history.getYear());
            row.add((double)history.getDay());
            row.add((double)history.getHour());
            row.add((double)history.getMinute());
            row.add(decision);
            row.add(stepSize);
                        
            dataList.add(row);
        }
        
        try {
            ReadWriteCSV.writeCSV(dataList, filePath, "planner_log.csv");
        } catch (IOException ex) {
            Logger.getLogger(SaveResults.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public static void saveExecutorHistory(ArrayList<ExecutorHistory> historyList, String filePath){
        Log.printLine1("SaveResults", "saveExecutorHistory", "Save executing history to CSV file");
        ArrayList dataList = new ArrayList();
        for (ExecutorHistory history : historyList){
            double action = history.getAction().ordinal();
            double provisioned = history.getProvisioned();
            double deprovisioned = history.getDeprovisioned();
            double quarantineed = history.getQuarantined();
            double flavorID = history.getFlavorID();
            
            ArrayList<Double> row = new ArrayList<>();
            row.add((double)history.getYear());
            row.add((double)history.getDay());
            row.add((double)history.getHour());
            row.add((double)history.getMinute());
            row.add(action);
            row.add(provisioned);
            row.add(deprovisioned);
            row.add(quarantineed);
            row.add(flavorID);
            dataList.add(row);
        }
        
        try {
            ReadWriteCSV.writeCSV(dataList, filePath, "executor_log.csv");
        } catch (IOException ex) {
            Logger.getLogger(SaveResults.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}

