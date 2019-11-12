/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package autoscaling;

import core.DefaultSettings;
import core.DefaultSettings.PlannerDecision;
import java.util.ArrayList;
import log.PlannerHistory;

/**
 *
 * @author fafa
 */
public abstract class Planner {
    
    DefaultSettings.PlannerDecision decision;
    int stepSize;
    
    private ArrayList<PlannerHistory> historyList;
    
    public Planner(int stepSize) {
        this.decision = DefaultSettings.PlannerDecision.DO_NOTHING;
        this.stepSize = stepSize;
        
        this.historyList = new ArrayList<>();
    }
    
    
    
    public abstract void doPlanning();

    public PlannerDecision getDecision() {
        return decision;
    }

    public void setDecision(PlannerDecision decision) {
        this.decision = decision;
    }

    public int getStepSize() {
        return stepSize;
    }

    public void setStepSize(int stepSize) {
        this.stepSize = stepSize;
    }

    public ArrayList<PlannerHistory> getHistoryList() {
        return historyList;
    }

    public PlannerHistory latestHistory (){
        return getHistoryList().get(getHistoryList().size() - 1);
    }
}
