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
import com.sun.xml.internal.ws.api.streaming.XMLStreamReaderFactory;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import log.SaveResults;
import log.Log;

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
        addInitialVms();
        // wait until initial vms are active.
        try {
            Log.printLine1("Main", "main", "wait for 2 min untill initial vms are active");
            Thread.sleep(120 * 1000);
            Log.printLine1("Now, you should run wikijector in 10 sec");
            Thread.sleep(10 * 1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        Log.printLine1("Auto-scaling started working");
        int timeToRunScaler = DefaultSettings.SCALING_INTERVAL;
        while (!exit()){
            try {
                Log.printLine1("Main", "main", "Autoscaler is sleeping until the next monitoring interval");
                Thread.sleep(DefaultSettings.MONITORING_INTERVAL);
                
                // call monitor
                monitor.doMonitoring();
               
                getExecutor().setRemainedCooldown(getExecutor().getRemainedCooldown() - DefaultSettings.MONITORING_INTERVAL);
                timeToRunScaler -= DefaultSettings.MONITORING_INTERVAL;
               
                if (timeToRunScaler > 0)
                    Log.printLine1("Main", "main", "Time to scaling: " + (timeToRunScaler / 1000) + " sec");
                else if (timeToRunScaler <= 0){
                    Log.printLine2("Main", "main", "Full autoscaling started");
                    // call analyzer
                    analyzer.doAnalysis();
                    // call planner
                    planner.doPlanning();
                    // call executor
                    executor.doExecution();
                    
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
    
    private static void addInitialVms(){
        Log.printLine1("Main", "addInitialVms", "Initial " + DefaultSettings.INITIAL_WEB_SERVERS + " Vm(s)");
        getExecutor().performScaleUp(DefaultSettings.INITIAL_WEB_SERVERS, 
                                    Integer.valueOf(DefaultSettings.EXECUTOR_SCALING_FLAVOR_ID));
    }
    
    /**
     * Terminate if 30 subsequent monitoring intervals with captured response time of 0.
     * @return 
     */
    private static boolean exit (){
        Log.printLine1("Main", "exit", "Terminating the experiment");
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
