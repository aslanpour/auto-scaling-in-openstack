/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package log;

import core.DefaultSettings;

/**
 *
 * @author fafa
 */
public class ExecutorHistory extends History{
    DefaultSettings.Action action;
    int provisioned;
    int deprovisioned;
    int quarantined;
    int flavorID;

    public ExecutorHistory(DefaultSettings.Action action, int provisioned, int deprovisioned, int quarantined, int flavorID) {
        super();
        
        this.action = action;
        this.provisioned = provisioned;
        this.deprovisioned = deprovisioned;
        this.quarantined = quarantined;
        this.flavorID = flavorID;
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

    public int getQuarantined() {
        return quarantined;
    }

    public void setQuarantined(int quarantined) {
        this.quarantined = quarantined;
    }

    public int getFlavorID() {
        return flavorID;
    }

    public void setFlavorID(int flavorID) {
        this.flavorID = flavorID;
    }
    
    
}
