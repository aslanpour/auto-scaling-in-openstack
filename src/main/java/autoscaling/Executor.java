/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package autoscaling;

import core.DefaultSettings;
import core.DefaultSettings.SurplusVMSelectionPolicy;
import core.Vm;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;
import log.ExecutorHistory;

/**
 *
 * @author fafa
 */
public abstract class Executor {
    private DefaultSettings.Action action;
    private int provisioned;
    private int deprovisioned;
    private int quarantineed;
    SurplusVMSelectionPolicy surplusVMSelectionPolicy;
    private int cooldown;
    private int remainedCooldown;
    private int maxAllowedScaleUp;
    private int flavorID;
    private int vmIndexGenerator;
    
    protected List<Integer> allocatedIPs = new ArrayList<Integer>();
    
    private ArrayList<ExecutorHistory> historyList;

    public Executor(SurplusVMSelectionPolicy surplusVMSelectionPolicy, 
                    int cooldown, 
                    int maxAllowedScaleUp, 
                    int flavorID, int[] alreadyAllocatedIPs) {
        // executor needs to obtain these parameters
        action = DefaultSettings.Action.DO_NOTHING;
        provisioned = 0;
        deprovisioned = 0; 
        quarantineed = 0;
        this.surplusVMSelectionPolicy = surplusVMSelectionPolicy;
        this.cooldown = cooldown;
        this.remainedCooldown = 0;
        this.maxAllowedScaleUp = maxAllowedScaleUp;
        this.flavorID = flavorID;
        this.vmIndexGenerator = 0;
        // exclude the ip of db server, haproxy, etc.
        for (int ip: alreadyAllocatedIPs){
            allocatedIPs.add(ip);
        }
        
        this.historyList = new ArrayList<ExecutorHistory>();
    }

    /**
     * Executes the planner's decision
     * @return 
     */
    public abstract void doExecution();

    public abstract void performScaleUp(int stepSize, int flavorID);
    
    public abstract void performScaleDown(int stepSize);
    
    public abstract String authentication();
    
    public abstract void haproxyReconfigurationLocally(String addRemove, String serverName, String serverIP);
    
    public abstract void haproxyReconfigurationRemotely(String addRemove, String serverName, String serverIP);
    
    public int generateVmIndex(){
        vmIndexGenerator++;
        return vmIndexGenerator;
    }
    
    public String allocateIP(){
        // get net IP
        String netIP = DefaultSettings.netIP;
        
        // get host IP (ip 1 is not allowed)
        for (int ip = 100; ip < 200; ip++){
            if (allocatedIPs.contains(ip) == false){
                allocatedIPs.add(ip);
                return (netIP + String.valueOf(ip));
            }
        }
        
        return null; // error
    }
    
    /**
     * remove the footpath of sshs to this ip by haproxy server 
     * which is making ssh to webservers to get cpu usage.
     * If not executed this command, once a new vm is launched with the same ip, making ssh to that 
     * is not accessible.
     * @param ip 
     */
    public void updateSshKnownHosts (final String ip){ 
        
        Thread updateSshKnownHosts = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Process p = null;

                    String command = "ssh-keygen -f \"/home/ubuntu/.ssh/known_hosts\" -R " + ip;
                   // Reset KnonwHosts
                    p = Runtime.getRuntime().exec(command);

                    p.waitFor();
                    BufferedReader buf = new BufferedReader(new InputStreamReader(p.getInputStream()));
                    String line = "";
                    String output = "";
                    int counter = 0;
                    while ((line = buf.readLine()) != null) {
                        // return the bash output
                        System.out.println(Double.valueOf(line));
                    }
                    System.out.println(output);
                    p = null;

                } catch (InterruptedException ex) {
                    Logger.getLogger(Monitor.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(Monitor.class.getName()).log(Level.SEVERE, null, ex);
                }
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        });
        // start the thread
        updateSshKnownHosts.start();
        

    }
    public DefaultSettings.Action getAction() {
        return action;
    }

    public void setAction(DefaultSettings.Action action) {
        this.action = action;
    }

    public int getProvisioned() {
        return provisioned;
    }

    public void setProvisioned(int provisioned) {
        this.provisioned = provisioned;
    }

    public int getDeprovisioned() {
        return deprovisioned;
    }

    public void setDeprovisioned(int deprovisioned) {
        this.deprovisioned = deprovisioned;
    }

    public int getQuarantineed() {
        return quarantineed;
    }

    public void setQuarantineed(int quarantineed) {
        this.quarantineed = quarantineed;
    }

    public SurplusVMSelectionPolicy getSurplusVMSelectionPolicy() {
        return surplusVMSelectionPolicy;
    }

    public void setSurplusVMSelectionPolicy(SurplusVMSelectionPolicy surplusVMSelectionPolicy) {
        this.surplusVMSelectionPolicy = surplusVMSelectionPolicy;
    }

    public int getCooldown() {
        return cooldown;
    }

    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
    }

    public int getRemainedCooldown() {
        return remainedCooldown;
    }

    public void setRemainedCooldown(int remainedCooldown) {
        this.remainedCooldown = remainedCooldown;
    }

    public int getMaxAllowedScaleUp() {
        return maxAllowedScaleUp;
    }

    public void setMaxAllowedScaleUp(int maxAllowedScaleUp) {
        this.maxAllowedScaleUp = maxAllowedScaleUp;
    }

    public int getFlavorID() {
        return flavorID;
    }

    public void setFlavorID(int flavorID) {
        this.flavorID = flavorID;
    }

    public ArrayList<ExecutorHistory> getHistoryList() {
        return historyList;
    }

    public void setHistoryList(ArrayList<ExecutorHistory> historyList) {
        this.historyList = historyList;
    }
    
    public ExecutorHistory latestHistory (){
        return getHistoryList().get(getHistoryList().size() - 1);
    }
}
