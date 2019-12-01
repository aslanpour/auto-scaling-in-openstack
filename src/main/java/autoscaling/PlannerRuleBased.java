/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package autoscaling;

import core.DefaultSettings;
import core.DefaultSettings.ScalingRule;
import core.Main;
import log.AnalyzerHistory;
import log.Log;
import log.PlannerHistory;


/**
 *
 * @author fafa
 */
public class PlannerRuleBased extends Planner{
    private final ScalingRule rule;
    private final double cpuScaleUpThreshold;
    private final double cpuScaleDownThreshold;
    private final double delayTimeMax;
    private final double delayTimeMin;

    public PlannerRuleBased(DefaultSettings.ScalingRule rule, 
                            double cpuScaleUpThreshold, 
                            double cpuScaleDownThreshold, 
                            double delayTimeMax, 
                            double delayTimeMin, 
                            int stepSize) {
        super(stepSize); // history is created by Planner class
        this.rule = rule;
        this.cpuScaleUpThreshold = cpuScaleUpThreshold;
        this.cpuScaleDownThreshold = cpuScaleDownThreshold;
        this.delayTimeMax = delayTimeMax;
        this.delayTimeMin = delayTimeMin;
    }
    
    @Override
    public void doPlanning(){
        Log.printLine2("\nPlannerRuleBased", "doMonitoring", "Planner started... (rule= " 
                + rule.name().toString() + ")");
        /* Planner's outputs - initialing output parameters */
        decision = DefaultSettings.PlannerDecision.DO_NOTHING;
        stepSize = DefaultSettings.PLANNER_StEP_SIZE;
        
        /* Planner's Inputs - parameters ready to contribute to decision making */
        AnalyzerHistory analyzerHistory = Main.getAnalyzer().latestHistory();

        double analyzedCpuUtilization = analyzerHistory.getCpuUtilization();
        double analyzedResponseTime = analyzerHistory.getResponseTime();

        /* Select the rule */
        switch(rule){
            case RESOURCE_AWARE  : rule_ResourceAware(analyzedCpuUtilization); break;
            case SLA_AWARE  : rule_SLAAware(analyzedResponseTime); break;
            case HYBRID : rule_HYBRID(analyzedCpuUtilization, analyzedResponseTime); break;
            case UT_1Al : rule_UT_1Al(analyzedCpuUtilization); break;
            case UT_2Al : rule_UT_2Al(analyzedCpuUtilization); break;
            case LAT_1Al : rule_LAT_1Al(analyzedCpuUtilization, analyzedResponseTime); break;
            case LAT_2Al : rule_LAT_2Al(analyzedCpuUtilization, analyzedResponseTime); break;
            default:
                Log.printLine1("Error (PlannerRuleBased class, doPlanning method) rule not found");
        }

        // Saving PlannerRuleBased results in its History
        PlannerHistory plannerHistory = new PlannerHistory(getDecision(),getStepSize());
        getHistoryList().add(plannerHistory);
        
//        Log.printLine2("Save planning results in history:");
        Log.printLine2("decision= " + getDecision().name().toString() +
                        "\nstep-size= " + getStepSize());
    }

    /**
     * Resource-aware rule
     * @param cpuUtil 
     */
    private void rule_ResourceAware(double cpuUtil){
        if(cpuUtil > cpuScaleUpThreshold)
            setDecision( DefaultSettings.PlannerDecision.SCALE_UP);
        else if (cpuUtil < cpuScaleDownThreshold)
            setDecision(DefaultSettings.PlannerDecision.SCALE_DOWN);
    }
    
    /**
     * SLA-aware rule
     * @param delayTime 
     */
    private void rule_SLAAware(double delayTime){
        if(delayTime > delayTimeMax)
            setDecision( DefaultSettings.PlannerDecision.SCALE_UP);
        else if (delayTime < delayTimeMin)
            setDecision(DefaultSettings.PlannerDecision.SCALE_DOWN);
    }
    
    /**
     * Hybrid rule, both Resource-and-SLA-aware
     * @param cpuUtil
     * @param delayTime 
     */
    private void rule_HYBRID(double cpuUtil, double delayTime){
        if(cpuUtil > cpuScaleUpThreshold && delayTime > delayTimeMax)
            setDecision( DefaultSettings.PlannerDecision.SCALE_UP);
        else if (cpuUtil < cpuScaleDownThreshold && delayTime < delayTimeMin)
            setDecision(DefaultSettings.PlannerDecision.SCALE_DOWN);
    }
    
    /**
     * Casalicchio, E. and Silvestri, L., 2013. 
     * Mechanisms for SLA provisioning in cloud-based service providers. 
     * Computer Networks, 57(3), pp.795-810. 
     * @param cpuUtil 
     */
    private void rule_UT_1Al(double cpuUtil){
        if(cpuUtil > 62)
            setDecision( DefaultSettings.PlannerDecision.SCALE_UP);
        else if (cpuUtil < 50)
            setDecision(DefaultSettings.PlannerDecision.SCALE_DOWN);
    }
    
    /**
     * Casalicchio, E. and Silvestri, L., 2013. 
     * Mechanisms for SLA provisioning in cloud-based service providers. 
     * Computer Networks, 57(3), pp.795-810.
     * @param cpuUtil
     * @param delayTime 
     */
    private void rule_LAT_1Al(double cpuUtil, double delayTime){
        if(delayTime > 0.2) 
            setDecision( DefaultSettings.PlannerDecision.SCALE_UP);
        else if (cpuUtil < 50) 
            setDecision(DefaultSettings.PlannerDecision.SCALE_DOWN);
    }
    
    /**
     * Casalicchio, E. and Silvestri, L., 2013. 
     * Mechanisms for SLA provisioning in cloud-based service providers. 
     * Computer Networks, 57(3), pp.795-810.   
     * @param cpuUtil 
     */
    private void rule_UT_2Al(double cpuUtil){
        if(cpuUtil > 70){
            setDecision( DefaultSettings.PlannerDecision.SCALE_UP);
            setStepSize(2);
        }else if (cpuUtil <= 70 && cpuUtil > 62){
            setDecision( DefaultSettings.PlannerDecision.SCALE_UP);
        // down
        }else if (cpuUtil < 50 && cpuUtil >= 25){
            setDecision(DefaultSettings.PlannerDecision.SCALE_DOWN);
        }else if (cpuUtil < 25){
            setDecision(DefaultSettings.PlannerDecision.SCALE_DOWN);
            setStepSize(2); 
        }
    }
    
    /**
     * Casalicchio, E. and Silvestri, L., 2013. 
     * Mechanisms for SLA provisioning in cloud-based service providers. 
     * Computer Networks, 57(3), pp.795-810.   
     * @param cpuUtil
     * @param delayTime 
     */
    private void rule_LAT_2Al(double cpuUtil, double delayTime){
        if(delayTime > 0.5){
            setDecision( DefaultSettings.PlannerDecision.SCALE_UP);
            setStepSize(2);
        }else if (delayTime > 0.2){
            setDecision( DefaultSettings.PlannerDecision.SCALE_UP);
        // down
        }else if (cpuUtil < 50 && cpuUtil >= 25){
            setDecision(DefaultSettings.PlannerDecision.SCALE_DOWN);
        }else if (cpuUtil <= 25){
            setDecision(DefaultSettings.PlannerDecision.SCALE_DOWN);
            setStepSize(2);
        }
    }
    
    
    
}
