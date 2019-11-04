package monitor;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import config.DefaultSettings;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author aslanpour
 */
public class MonitorCpuUtilization {
    
    /**
     * Make SSH from local to remote server, run a bash file to get CPU idle percentage, 
     * return the output to local and then to java program
     * @param serverName
     * @param serverIP
     * @return 
     */
    public static double getCpuUtilization(String serverName, String serverIP){
        double cpuUtilization = 0;
        
        try {
            Process p = null;
           // Get CPU idle percentage
            p = Runtime.getRuntime().exec("sshpass -p " + DefaultSettings.WEB_SERVER_PASSWORD + " ssh -o "
            + "StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null "
            + serverName + "@" + serverIP + " sudo bash " + DefaultSettings.CPU_UTILIZATION_FILE_LOCATION);
           
            p.waitFor();
            BufferedReader buf = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = "";
            String output = "";
            int counter = 0;
            while ((line = buf.readLine()) != null) {
                cpuUtilization = Double.valueOf(line);
                counter++;
                output += line + "\n";
            }
            if (counter> 1) System.out.println("ERROR - getCpuUtilization returned more than one output");
            System.out.println(output);
            p = null;
            
        } catch (InterruptedException ex) {
            Logger.getLogger(MonitorCpuUtilization.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MonitorCpuUtilization.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // change idle percentage to utilization
        cpuUtilization = 100 - cpuUtilization;
        // get only two decimal places
        cpuUtilization = Double.valueOf(new DecimalFormat("#.##").format(cpuUtilization));
        
        return cpuUtilization;
        
    }
    
}
