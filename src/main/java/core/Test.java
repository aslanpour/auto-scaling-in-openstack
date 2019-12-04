/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import log.Log;

/**
 *
 * @author aslanpour
 */
public class Test {
    public static void main(String[] args) throws IOException {
        int[] rr = new int[4];
        Log.printLine1(String.valueOf(rr[1]));
        Log.printLine();
        System.out.println("core.Test.main()");
        Log.printLine();
        System.err.println("sdgsg");
        try {
            Process p = null;
            System.out.println(Log.getTimestampStr());
            // Get CPU idle percentage
            p = Runtime.getRuntime().exec("sshpass -p " + DefaultSettings.WEB_SERVER_PASSWORD +
                    " ssh -o " + "StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null "
                    + DefaultSettings.WEB_SERVER_USERNAME + "@10.10.0.101" +
                    " -i " + DefaultSettings.FILE_LOCATION_HAPROXY_PRIVATE_KEY
                    + " sudo " + "tail -n " + DefaultSettings.CPU_LOG_ITEMS + " " + DefaultSettings.FILE_LOCATION_CPU_UTILIZATION);
//                + " sudo bash " + DefaultSettings.FILE_LOCATION_CPU_UTILIZATION);
//                p.waitFor(10, TimeUnit.DAYS)
            p.waitFor(15000, TimeUnit.MILLISECONDS);
            System.out.println(Log.getTimestampStr());
            BufferedReader buf = new BufferedReader(new InputStreamReader(p.getInputStream()));

        } catch (InterruptedException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
   
}
