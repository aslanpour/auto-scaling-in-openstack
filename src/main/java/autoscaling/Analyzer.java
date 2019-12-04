/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package autoscaling;

import core.Main;
import static core.Main.getMonitor;
import java.util.ArrayList;
import log.AnalyzerHistory;
import log.Log;
import log.MonitorHistory;

/**
 *
 * @author fafa
 */
public class Analyzer {
    private String analysisMethodCpu;
    private String analysisMethodRT;
    private String analysisMethodReq;
    private double sesAlpha;
    private int timeWindow; 
    private ArrayList<AnalyzerHistory> historyList;
    private double oldSESOutput;
    
    private double analyzedCpuUtilization;
    private double analyzedResponseTime;
    private double analyzedRequests;
    
    public Analyzer(String analysisMethodCpu, String analysisMethodRT, String analysisMethodReq, double sesAlpha, int timeWindow) {
        this.analysisMethodCpu = analysisMethodCpu;
        this.analysisMethodRT = analysisMethodRT;
        this.analysisMethodReq = analysisMethodReq;
        
        this.sesAlpha = sesAlpha;
        this.timeWindow = timeWindow;
        
        this.historyList = new ArrayList<AnalyzerHistory>();
        this.oldSESOutput = Double.MIN_VALUE;
    }

    /**
    * Analyzing effective parameters
    */
    public void doAnalysis(){
        Log.printLine();
        Log.printLine2("Analyzer", "doAnalysis", "Analyzing phase started...");
/* initialing Analysis Parameters */
        
        /* calculation of analysis parameters */
        
        // RESOURCE-AWARE analysis
        analyzedCpuUtilization = ANALAYZE_CPUUtil(analysisMethodCpu); 
        // SLA-AWARE analysis
        analyzedResponseTime = ANALAYZE_ResponseTime(analysisMethodRT);
        
        //LOAD-AWARE analysis
        analyzedRequests = ANALAYZE_Requests(analysisMethodReq);
        
        /* SAVE analysis results to history */
        AnalyzerHistory analyzerHistory = new AnalyzerHistory(analyzedCpuUtilization, analyzedResponseTime, analyzedRequests);
        getHistoryList().add(analyzerHistory);
//        Log.printLine2("Save Analyed results in the history");
        Log.printLine2("Analyzed CPU util.=" + analyzedCpuUtilization
                        + "\n       Analyzed response time= " + analyzedResponseTime
                        + "\n       Analyzed requests= " + analyzedRequests);
    }
    
    /**
     * Analyzing CPU utilization
     * @return 
     */
    private double ANALAYZE_CPUUtil(String analysisMethod){
//        Log.printLine3("Analyzing CPU util. . .");
        double analyzedCPUUtilization = -1;
        // Get VM monitor history
        ArrayList<MonitorHistory> tmpHistoryList = Main.getMonitor().getMonitorHistory();
        int sizeHistory = tmpHistoryList.size();
        // Set the latest monitored Cpu utilization item
        double cpuUtil = tmpHistoryList.get(sizeHistory - 1).getCpuUtilizationAvg();
        
        // Set a list of monitored Cpu utilization
        int window = timeWindow;
        double cpuUtilList[];
        if (sizeHistory < timeWindow){ // for the begining of the test
            window = sizeHistory;
        }
        
        cpuUtilList = new double[window];
        int j = 0;
        for(int i = (sizeHistory - window); i< sizeHistory;i++){
            cpuUtilList[j] = tmpHistoryList.get(i).getCpuUtilizationAvg();
            j++;
        }
        
        switch(analysisMethod){
            // Simple
            case "SIMPLE": 
                analyzedCPUUtilization = cpuUtil;
                break;
                
            // Moving Average
            case "COMPLEX_MA": 
                    analyzedCPUUtilization = calculateMovingAverage(cpuUtilList);
                break;
                
            // Weighted Moving Average
            case "COMPLEX_WMA": 
                    analyzedCPUUtilization = calculateWeightedMovingAverage(cpuUtilList);
                break;
                
            // Weighted Moving Average (weighting by Fibonacci numbers)
            case "COMPLEX_WMAfibo": 
                analyzedCPUUtilization = calculateWeightedMovingAverageFibonacci(cpuUtilList);
                break;
                
            // Single exponential smoothing
            case "COMPLEX_SES":
                analyzedCPUUtilization = calculateSingleExponentialSmoothing(cpuUtil, oldSESOutput, sesAlpha);
                oldSESOutput = analyzedCPUUtilization;
                break;
            
            default:
                Log.printLine1("Error (Analyzer class, ANALYZE_CPUUTIL method) - analysis method not found");
        }
        
        return analyzedCPUUtilization;
    }
    
    /**
     * Analyzing Response Time
     * @return 
     */
    private double ANALAYZE_ResponseTime(String analysisMethod){
//        Log.printLine3("Analyzing response time . . .");
        double analyzedResponseTime = -1;
        
        // Get monitor history
        ArrayList<MonitorHistory> tmpHistoryList = getMonitor().getMonitorHistory();
        int sizeHistory = tmpHistoryList.size();
        // Set the latest monitored Response Time item
        double responseTime = tmpHistoryList.get(sizeHistory - 1).getResponseTimeAvg();
        
        // Set a list of monitored Response Time
        int window = timeWindow;
        double responseTimeList[];
        if (sizeHistory < timeWindow){ // for the begining of the test
            window = sizeHistory;
        }
        
        responseTimeList = new double[window];
        
        int j = 0;
        for(int i = (sizeHistory - window); i< sizeHistory;i++){
            responseTimeList[j] = tmpHistoryList.get(i).getResponseTimeAvg();
            j++;
        }
                     
        switch(analysisMethod){
            // Simple
            case "SIMPLE": 
                analyzedResponseTime = responseTime;
                break;
                
            // Moving Average
            case "COMPLEX_MA": 
                    analyzedResponseTime = calculateMovingAverage(responseTimeList);
                break;
                
            // Weighted Moving Average
            case "COMPLEX_WMA": 
                    analyzedResponseTime = calculateWeightedMovingAverage(responseTimeList);
                break;
                
            // Weighted Moving Average (weighting by Fibonacci numbers)
            case "COMPLEX_WMAfibo": 
                analyzedResponseTime = calculateWeightedMovingAverageFibonacci(responseTimeList);
                break;
                
            // Single exponential smoothing
            case "COMPLEX_SES":
                analyzedResponseTime = calculateSingleExponentialSmoothing(responseTime, oldSESOutput, sesAlpha);
                oldSESOutput = analyzedResponseTime;
                break;
            
            default:
                Log.printLine1("Error (Analyzer class, ANALYZE_ResponseTime method) - analysis method not found");
        }
        return analyzedResponseTime;
    }
    
    
    private double ANALAYZE_Requests(String analysisMethod){
//        Log.printLine3("Analyzing requests . . .");
        double analyzedRequests = -1;
        
        // Get monitor history
        ArrayList<MonitorHistory> tmpHistoryList = getMonitor().getMonitorHistory();
        int sizeHistory = tmpHistoryList.size();
        // Set the latest monitored Requests item
        double requests = tmpHistoryList.get(sizeHistory - 1).getCurrentSessionsSum();
        
        // Set a list of monitored Requests
        int window = timeWindow;
        double requestsList[];
        if (sizeHistory < timeWindow){ // for the begining of the test
            window = sizeHistory;
        }
        
        requestsList = new double[window];

        int j = 0;
        for(int i = (sizeHistory - window); i< sizeHistory;i++){
            requestsList[j] = tmpHistoryList.get(i).getCurrentSessionsSum();
            j++;
        }
                     
        switch(analysisMethod){
            // Simple
            case "SIMPLE": 
                analyzedRequests = requests;
                break;
                
            // Moving Average
            case "COMPLEX_MA": 
                    analyzedRequests = calculateMovingAverage(requestsList);
                break;
                
            // Weighted Moving Average
            case "COMPLEX_WMA": 
                    analyzedRequests = calculateWeightedMovingAverage(requestsList);
                break;
                
            // Weighted Moving Average (weighting by Fibonacci numbers)
            case "COMPLEX_WMAfibo": 
                analyzedRequests = calculateWeightedMovingAverageFibonacci(requestsList);
                break;
                
            // Single exponential smoothing
            case "COMPLEX_SES":
                analyzedRequests = calculateSingleExponentialSmoothing(requests, oldSESOutput, sesAlpha);
                oldSESOutput = analyzedRequests;
                break;
            
            default:
                Log.printLine1("Error (Analyzer class, ANALYZE_Requests method) - analysis method not found");
        }
        return analyzedRequests;
    }
    
    /**
     * Analyzes the indicated parameter by Moving Average method
     * @param parameterList
     * @return 
     */
    private double calculateMovingAverage(double[] parameterList){
        double sumMovingAverage = 0;
        for(int i = 0; i < parameterList.length; i++){
            sumMovingAverage += parameterList[i];
        }
        return sumMovingAverage / parameterList.length;
    }
    
    /**
     * Analyzes the indicated parameter by Weighted Moving average method
     * @param parameterList
     * @return 
     */
    public double calculateWeightedMovingAverage(double[] parameterList){
        double sumWeightedItem = 0;
        double sumWeight = 0;
        int weight = 1;
        
        for(int i = 0; i < parameterList.length; i++){
            sumWeightedItem += parameterList[i] * weight;  

            sumWeight += weight;
            weight++;
        }
        return sumWeightedItem / sumWeight;
    }
    
    /**
     * Analyzes the indicated parameter by Weighted Moving average method.
     * This method uses Fibonacci technique.
     * @param parameterList
     * @return 
     */
    private double calculateWeightedMovingAverageFibonacci(double[] parameterList){
        double sumWeightedItems = 0;
        double sumWeight = 0;
        int weight;
        int fibo1 = 0; 
        int fibo2 = 1;
        for(int i = 0; i < parameterList.length; i++){
            weight = fibo1 + fibo2;
            sumWeightedItems += parameterList[i] * weight;
            sumWeight += weight;
            fibo1 = fibo2; fibo2 = weight;                
        }
        return sumWeightedItems / sumWeight;
    }
    
    /**
     * Analyzes the indicated parameter by Single Exponential Parameter method
     * @param parameter
     * @param oldSESOutput
     * @param alpha
     * @return 
     */
    private double calculateSingleExponentialSmoothing(double parameter, double oldSESOutput, double alpha){
        if (oldSESOutput == Double.MIN_VALUE) 
            oldSESOutput = parameter;
        
        return (alpha * parameter) + ((1 - alpha) * oldSESOutput);
    }
    
    public String getAnalysisMethodCpu() {
        return analysisMethodCpu;
    }

    public String getAnalysisMethodRT() {
        return analysisMethodRT;
    }

    public String getAnalysisMethodReq() {
        return analysisMethodReq;
    }

    public void setAnalysisMethodReq(String analysisMethodReq) {
        this.analysisMethodReq = analysisMethodReq;
    }
       
    public double getSesAlpha() {
        return sesAlpha;
    }

    public ArrayList<AnalyzerHistory> getHistoryList() {
        return historyList;
    }

    public void setHistoryList(ArrayList<AnalyzerHistory> historyList) {
        this.historyList = historyList;
    }

    public AnalyzerHistory latestHistory (){
        return getHistoryList().get(getHistoryList().size() - 1);
    }

    
    
    
}
