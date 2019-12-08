/*
 * Title:        AutoScaleSim Toolkit
 * Description:  AutoScaleSim (Auto-Scaling Simulation) Toolkit for Modeling and Simulation
 *               of Autonomous Systems for Web Applications in Cloud
 *
 * Copyright (c) 2018, Islamic Azad University, Jahrom, Iran
 *
 * Authors: Mohammad Sadegh Aslanpour, Adel Nadjaran Toosi, Javid Taheri
 * 
 */

package autoscaling;

import core.DefaultSettings;
import core.Main;
import core.Vm;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import log.Log;
import log.MonitorHistory;
/**
 * SurplusVmSelectionPolioy class is called if the executor wants to execute an scale-down decision.
 * In this situation, this class selects a VM as surplus. There are some policies here.
 */
public class SurplusVmSelection {
       
       
    public static Vm policy(DefaultSettings.SurplusVMSelectionPolicy policy, ArrayList<Vm> candidateVms){
        Vm selectedVm = null;
        
        MonitorHistory history = Main.getMonitor().latestHistory();
        
        switch(policy){
            case RANDOM: selectedVm = random(candidateVms);
            break;
            
            case THE_OLDEST: selectedVm = theOldest(candidateVms);
            break;
            
            case THE_YOUNGEST: selectedVm = theYoungest(candidateVms);
            break;
            
            case RESOURCE_AWARE:
                double[][] cpuUtilPerVm = history.getCpuUtilizationPerVm();
                selectedVm = resourceAware(candidateVms, cpuUtilPerVm);
            break;
            
            case LOAD_AWARE:
                int[][] sessionsPerVm = history.getSessions();
                selectedVm = loadAware(candidateVms, sessionsPerVm);
            break;
            
            case COST_AWARE: selectedVm = costAware(candidateVms);
            break;
//            case COST_AWARE_PROFESSIONAL: selectedVm = costAwareProfessional(candidateVms);
//            break;
            default:
                //Error
                break;
        }
        
        return selectedVm;
    }
    
    private static Vm random(ArrayList<Vm> candidateVms){

        Vm selectedVm = candidateVms.get(0);

        Random random = new Random();

        int randomIndex = random.nextInt(candidateVms.size());
        selectedVm = candidateVms.get(randomIndex);
        
        return selectedVm;
    }
    
    /**
     * 
     * @param candidateVms
     * @return 
     */
    private static Vm theOldest(ArrayList<Vm> candidateVms){
        Vm selectedvm = candidateVms.get(0);

        for(Vm vm: candidateVms){
            if(vm.getTimeCreated().before(selectedvm.getTimeCreated())){
                selectedvm = vm;
            }
            
        }
        return selectedvm;
    }
    
    private static Vm theYoungest(ArrayList<Vm> candidateVms){
        Vm selectedvm = candidateVms.get(0);

        for(Vm vm: candidateVms){
            if(vm.getTimeCreated().after(selectedvm.getTimeCreated())){
                selectedvm = vm;
            }
        }
        return selectedvm;
    }
    
    private static Vm resourceAware (ArrayList<Vm> candidateVms, double[][] cpuUtilPerVm){
        Vm selectedVm = candidateVms.get(0);
        
        // search the Vm by CPU utilization
        int vmIndex = -1;
        double cpuMin = Double.MAX_VALUE;
        
        for (int i=0; i< cpuUtilPerVm.length; i++){
            if (cpuUtilPerVm[i][1] < cpuMin){
                vmIndex = (int)cpuUtilPerVm[i][0];
                cpuMin = cpuUtilPerVm[i][1];
            }
            Log.printLine1("vmIndex= " + cpuUtilPerVm[i][0] + " cpuUtil= " + cpuUtilPerVm[i][1]) ;
        }
        
        // select the vm by its selected index
        for (Vm vm : candidateVms){
            if (vm.getIndex() == vmIndex){
                selectedVm = vm;
                Log.printLine1("selected vmIndex = " + vm.getIndex());
                break;
            }
        }
        
        return selectedVm;
    }
    
    private static Vm loadAware (ArrayList<Vm> candidateVms, int[][] sessionsPerVm){
        Vm selectedVm = candidateVms.get(0);
        
        int vmIndex = -1;
        int sessionsMin = Integer.MAX_VALUE;
        
        for (int i =0; i < sessionsPerVm.length; i++){
            if(sessionsPerVm[i][1] < sessionsMin){
                vmIndex = sessionsPerVm[i][0];
                sessionsMin = sessionsPerVm[i][1];
            }
            Log.printLine1("vmIndex= " + sessionsPerVm[i][0] + " sessions= " + sessionsPerVm[i][1]);
        }
        
        // select the vm by its selected index
        for (Vm vm : candidateVms){
            if (vm.getIndex() == vmIndex){
                selectedVm = vm;
                Log.printLine1("selected vmIndex= " + vm.getIndex());
                break;
            }
        }
        
        return selectedVm;
    }
    
    private static Vm costAware (ArrayList<Vm> candidateVms){
        Vm selectedVm = candidateVms.get(0);
        
        double remainedTimeToFulfillBill = Integer.MAX_VALUE;

        for (Vm vm : candidateVms){
            // created time
            long dateStart = vm.getTimeCreated().getTime();
            //now
            long dateEnd = Log.getTimestamp().getTime();
            
            long duration = dateEnd - dateStart;

            double seconds = duration / 1000;
            double remainedTime = seconds % 3600;
            
            if (remainedTime < remainedTimeToFulfillBill){
                selectedVm = vm;
                remainedTimeToFulfillBill = remainedTime;
            }
            Log.printLine1("IP= " + vm.getPrivateIP() + " remained time = " + remainedTime);
        }
        
        Log.printLine1("selected Vm = " + selectedVm.getPrivateIP());
        return selectedVm;
    }

//    
//    /**
//     * Selects the surplus VM in a cost-saving way
//     * @param condidateVmList
//     * @param exceptList
//     * @return 
//     */    
//    private static Vm costAwareSimple(List<Vm> condidateVmList, ArrayList<Integer> exceptList){
//        // har kodam ke az akharin saatash bishtarin estefade shode entekhab mishavad
//        Vm selectedVm = condidateVmList.get(0);
//        double maxPassedTimeFromLastHour = Integer.MIN_VALUE;
//        for (Vm vm : condidateVmList) {
//            if(exceptList.contains(vm.getId()))
//                continue;
//            
//            double passedTimeFromLastHour;
//            double availabletime = CloudSim.clock() - vm.getRequestTime();
//            
//            passedTimeFromLastHour = availabletime % AutoScaleSimTags.anHour;
//            if(passedTimeFromLastHour == 0)
//                passedTimeFromLastHour = AutoScaleSimTags.anHour;
//            
//            // agar vm req dar 0 bood, in vm hamin hala request shode va yek saat az zamanash baghi mande
//            if(availabletime == 0)
//                passedTimeFromLastHour = 0;
//            
//             if (passedTimeFromLastHour > maxPassedTimeFromLastHour){
//                 maxPassedTimeFromLastHour = passedTimeFromLastHour;
//                 selectedVm = vm;
//             }
//             // agar halate barabar bod va in vm req bood in rntekhab shavad, chon dar halate takhir va cloudlet ham nadrad
////             }else if (passedTimeFromLastHour == maxPassedTimeFromLastHour)
////                 if(vm.getStartTime() == -1)
////                     selectedVm = vm;
//                 
//        }
//        return selectedVm;
//    }
//    
//    /**
//     * Selects the VM in a cost-saving and load-aware way
//     * @param condidateVmList
//     * @param exceptList
//     * @return 
//     */
//    private static Vm costAwareProfessional(List<Vm> condidateVmList, ArrayList<Integer> exceptList){
//        Vm selectedVm = condidateVmList.get(0);
//        double maxTime = Integer.MIN_VALUE;
//        double cpuUtilization = Integer.MAX_VALUE;
//        for (Vm vm : condidateVmList) {
//            if(exceptList.contains(vm.getId()))
//                continue;
//            
//            double availableTime = DateTime.tick()- vm.getRequestTime();
//            double passedSecondsFromLastHour = availableTime % AutoScaleSimTags.anHour;
//
//            if(passedSecondsFromLastHour == 0){// Excaxtly X hour(s)
//                passedSecondsFromLastHour = AutoScaleSimTags.anHour; 
//            }
//
//            if(passedSecondsFromLastHour > maxTime){
//                cpuUtilization = (vm.getTotalUtilizationOfCpuMips(CloudSim.clock()) /
//                        (vm.getMips() * vm.getNumberOfPes()))
//                        * 100;
//                maxTime = passedSecondsFromLastHour;
//                selectedVm = vm;
//            }else if (passedSecondsFromLastHour == maxTime){
//                double vmCPUUtilization = (vm.getTotalUtilizationOfCpuMips(CloudSim.clock()) /
//                        (vm.getMips() * vm.getNumberOfPes()))
//                        * 100;
//                if(vmCPUUtilization < cpuUtilization){
//                    cpuUtilization = vmCPUUtilization;
//                    selectedVm = vm;
//                }
//            }
//        }
//        
//        return selectedVm;
//    }
//    
//    
        
}
