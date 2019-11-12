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
public class PlannerHistory extends History{
    // -1 = scale down, 0 = do nothing, and 1 = scale up
    DefaultSettings.PlannerDecision decision;
    int stepSize;

    public PlannerHistory(DefaultSettings.PlannerDecision decision, int stepSize) {
        super();
        
        this.decision = decision;
        this.stepSize = stepSize;
    }

    

    public DefaultSettings.PlannerDecision getDecision() {
        return decision;
    }

    public void setDecision(DefaultSettings.PlannerDecision decision) {
        this.decision = decision;
    }

    public int getStepSize() {
        return stepSize;
    }

    public void setStepSize(int stepSize) {
        this.stepSize = stepSize;
    }

}
