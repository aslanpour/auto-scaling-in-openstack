/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import autoscaling.MonitorHaproxy;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;


/**
 *
 * @author fafa
 */
public class test {
    
    public static void main(String[] args) throws InterruptedException {
        int dd= DefaultSettings.PlannerDecision.DO_NOTHING.ordinal();
        int dd2= DefaultSettings.PlannerDecision.SCALE_UP.ordinal();
        int dd3= DefaultSettings.PlannerDecision.SCALE_DOWN.ordinal();
        String rr = null;
        Timestamp timestamp1 = new Timestamp(new Date().getTime());
        long dateStart = timestamp1.getTime();
        
        Thread.sleep(3000);
        Timestamp timestamp2 = new Timestamp(new Date().getTime());
        long dateEnd = timestamp2.getTime();
        
        long duration = dateEnd - dateStart;

        double durationSec = duration / 1000;
        durationSec = 3601;
        double durationHour = durationSec / 3600;
        int hour = (int)Math.ceil(durationHour);
        double fff= Double.MAX_VALUE;
        final int tmp = 13; // the below expression requires a final variable.
            boolean contains = IntStream.of(DefaultSettings.allocatedIPs).anyMatch(x -> x == tmp);

            if (contains == false)
               rr = DefaultSettings.netID + String.valueOf(tmp);
            
         contains = IntStream.of(DefaultSettings.allocatedIPs).anyMatch(x -> x == 12);

        if (contains == false)
            System.out.println("core.test.main()");
        
        Date currentDate = new java.util.Date();
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS").format(currentDate);
        System.out.println(timeStamp + " main thrad hello");
        for (int i =0; i < 10; i ++)
        {
            for (int j = 1; j < 4; j ++){
                if (j ==2)
                break;
            }
        }
        int g = 9;
        // monitor Haproxy
        MonitorHaproxy monitorHaproxy = new MonitorHaproxy();
        Thread monitorHaproxyThread = new Thread(monitorHaproxy);
        monitorHaproxyThread.setDaemon(true);
        monitorHaproxyThread.start();
        monitorHaproxyThread.join();
        System.out.println("");
        System.out.println(monitorHaproxy.getCurrentSessions()[0][0]);
        System.out.println(monitorHaproxy.getCurrentSessions()[0][1]);
        System.out.println(monitorHaproxy.getCurrentSessions()[1][0]);
        System.out.println(monitorHaproxy.getCurrentSessions()[1][1]);
        System.out.println(monitorHaproxy.getRespnseTimeAvg());
        
        
    }
}
