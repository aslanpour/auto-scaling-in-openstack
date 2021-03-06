/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package log;

/**
 *
 * @author fafa
 */
public class AnalyzerHistory extends History {
    private double cpuUtilization;
    private double responseTime;
    private double requests;

    public AnalyzerHistory(double cpuUtilization, double responseTime, double requests) {
        super();
        
        this.cpuUtilization = cpuUtilization;
        this.responseTime = responseTime;
        this.requests = requests;
    }

    public double getCpuUtilization() {
        return cpuUtilization;
    }

    public void setCpuUtilization(double cpuUtilization) {
        this.cpuUtilization = cpuUtilization;
    }

    public double getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(double responseTime) {
        this.responseTime = responseTime;
    }

    public double getRequests() {
        return requests;
    }

    public void setRequests(double requests) {
        this.requests = requests;
    }
    
    
}
