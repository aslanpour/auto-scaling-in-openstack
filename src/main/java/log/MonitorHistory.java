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
public class MonitorHistory extends History {
    private double cpuUtilizationAvg;
    // vm indexand cpu util
    private double[][] cpuUtilizationPerVm;
    private double responseTimeAvg;
    // vm index and sessions
    private int sessions[][];
    private int currentSessionsSum;
    private int vms;
    
    private int quarantineeVms;

    public MonitorHistory(double cpuUtilizationAvg, 
                            double[][] cpuUtilizationPerVm, 
                            double responseTimeAvg, 
                            int[][] sessions, 
                            int currentSessionsSum,
                            int vms, 
                            int quarantineeVms) {
        super();
        this.cpuUtilizationAvg = cpuUtilizationAvg;
        this.cpuUtilizationPerVm = cpuUtilizationPerVm;
        this.responseTimeAvg = responseTimeAvg;
        this.sessions = sessions;
        this.currentSessionsSum = currentSessionsSum;
        this.vms = vms;
        this.quarantineeVms = quarantineeVms;
    }

    public double getCpuUtilizationAvg() {
        return cpuUtilizationAvg;
    }

    public void setCpuUtilizationAvg(double cpuUtilizationAvg) {
        this.cpuUtilizationAvg = cpuUtilizationAvg;
    }

    public double[][] getCpuUtilizationPerVm() {
        return cpuUtilizationPerVm;
    }

    public void setCpuUtilizationPerVm(double[][] cpuUtilizationPerVm) {
        this.cpuUtilizationPerVm = cpuUtilizationPerVm;
    }

    public double getResponseTimeAvg() {
        return responseTimeAvg;
    }

    public void setResponseTimeAvg(double responseTimeAvg) {
        this.responseTimeAvg = responseTimeAvg;
    }

    public int[][] getSessions() {
        return sessions;
    }

    public void setSessions(int[][] sessions) {
        this.sessions = sessions;
    }

    public int getCurrentSessionsSum() {
        return currentSessionsSum;
    }

    public void setCurrentSessionsSum(int currentSessionsSum) {
        this.currentSessionsSum = currentSessionsSum;
    }

    
    public int getVms() {
        return vms;
    }

    public void setVms(int vms) {
        this.vms = vms;
    }

    public int getQuarantineeVms() {
        return quarantineeVms;
    }

    public void setQuarantineeVms(int quarantineeVms) {
        this.quarantineeVms = quarantineeVms;
    }

    

    
    
}
