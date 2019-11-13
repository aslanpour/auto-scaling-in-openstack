/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import autoscaling.Analyzer;
import autoscaling.Executor;
import autoscaling.ExecutorSimple;
import autoscaling.Monitor;
import autoscaling.Planner;
import autoscaling.PlannerRuleBased;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import log.SaveResults;

/**
 *
 * @author fafa
 */
public class Main {
    //Vm
    public static ArrayList<Vm> vmsProvisioned = new ArrayList<Vm>();
    public static ArrayList<Vm> vmsDeprovisioned = new ArrayList<Vm>();
    
    public static Monitor monitor = new Monitor();
    public static Analyzer analyzer = new Analyzer(DefaultSettings.ANALYSIS_METHOD_CPU, 
                                                    DefaultSettings.ANALYSIS_METhOD_RT, 
                                                    DefaultSettings.ANALYSIS_SES_ALPHA, 
                                                    DefaultSettings.ANALYSIS_TIME_WINDOW);
    
    public static Planner planner = new PlannerRuleBased(DefaultSettings.rule, 
                                                        DefaultSettings.PLANNER_CPU_UP, 
                                                        DefaultSettings.PLANNER_CPU_DOWN, 
                                                        DefaultSettings.PLANNER_RT_UP, 
                                                        DefaultSettings.PLANNER_RT_DOWN, 
                                                        DefaultSettings.PLANNER_StEP_SIZE);
    
    public static Executor executor = new ExecutorSimple(DefaultSettings.SurplusVMSelectionPolicy.THE_OLDEST, 
                                            DefaultSettings.COOLDOWN, 
                                            DefaultSettings.MAX_ALLOWED_SCALE_UP, 
                                            Integer.valueOf(DefaultSettings.EXECUTOR_SCALING_FLAVOR_ID),
                                            DefaultSettings.alreadyAllocatedIPs);
    
    private static int terminationCounter = 0;
    
    public static void main(String[] args) {
        // add initial vms in vmsprovisioned
        int timeToRunScaler = DefaultSettings.SCALING_INTERVAL;
        while (!exit()){
            try {
                Thread.sleep(DefaultSettings.MONITORING_INTERVAL);
                String ff= "/h";
                if(ff.startsWith("/mediawiki/images"))
                // call monitor
                monitor.doMonitoring();
               
                getExecutor().setRemainedCooldown(getExecutor().getRemainedCooldown() - DefaultSettings.MONITORING_INTERVAL);
                timeToRunScaler -= DefaultSettings.MONITORING_INTERVAL;
               
                if (timeToRunScaler <= 0){
                    // call analyzer
                    analyzer.doAnalysis();
                    // call planner
                    planner.doPlanning();
                    // call executor
                    
                    timeToRunScaler = DefaultSettings.SCALING_INTERVAL;
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        // destroy running vms
        getExecutor().performScaleDown(vmsProvisioned.size());
        
        // print logs to CSV file
        SaveResults.saveMonitorHistory(getMonitor().getMonitorHistory());
        SaveResults.saveAnalyzerHistory(getAnalyzer().getHistoryList());
        SaveResults.savePlannerHistory(getPlanner().getHistoryList());
        SaveResults.saveExecutorHistory(getExecutor().getHistoryList());
        // print results to console
        
    }
    
    /**
     * Terminate if 30 subsequent monitoring intervals with captured response time of 0.
     * @return 
     */
    private static boolean exit (){
        
        if (monitor.getResponseTimeAvg() == 0)
            terminationCounter++;
        else
            terminationCounter = 0;
        
        return terminationCounter >= 30;
    }

    public static Monitor getMonitor() {
        return monitor;
    }

    public static Analyzer getAnalyzer() {
        return analyzer;
    }

    public static Planner getPlanner() {
        return planner;
    }

    public static Executor getExecutor() {
        return executor;
    }
    
    
}
