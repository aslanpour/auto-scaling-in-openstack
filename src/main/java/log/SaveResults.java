/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package log;

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
    
    public static void PrintToConsole(){
        
    }
    public static void saveMonitorHistory(ArrayList<MonitorHistory> historyList){
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
            ReadWriteCSV.writeCSV(dataList, "/src/log/", "monitor_log.csv");
        } catch (IOException ex) {
            Logger.getLogger(SaveResults.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public static void saveAnalyzerHistory(ArrayList<AnalyzerHistory> historyList){
        ArrayList dataList = new ArrayList();
        for (AnalyzerHistory history : historyList){
            double cpuUtilizationAvg = history.getCpuUtilization();
            double responseTimeAvg = history.getResponseTime();
            
            ArrayList<Double> row = new ArrayList<>();
            row.add((double)history.getYear());
            row.add((double)history.getDay());
            row.add((double)history.getHour());
            row.add((double)history.getMinute());
            row.add(cpuUtilizationAvg);
            row.add(responseTimeAvg);
                        
            dataList.add(row);
        }
        
        try {
            ReadWriteCSV.writeCSV(dataList, "/src/log/", "analyzer_log.csv");
        } catch (IOException ex) {
            Logger.getLogger(SaveResults.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public static void savePlannerHistory(ArrayList<PlannerHistory> historyList){
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
            ReadWriteCSV.writeCSV(dataList, "/src/log/", "analyzer_log.csv");
        } catch (IOException ex) {
            Logger.getLogger(SaveResults.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public static void saveExecutorHistory(ArrayList<ExecutorHistory> historyList){
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
            ReadWriteCSV.writeCSV(dataList, "/src/log/", "analyzer_log.csv");
        } catch (IOException ex) {
            Logger.getLogger(SaveResults.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}

