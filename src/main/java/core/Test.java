/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import log.DescriptiveStatistics;
import log.Log;
import log.ReadWriteCSV;

/**
 *
 * @author aslanpour
 */
public class Test {
    public static void main(String[] args) throws IOException, InterruptedException {
        Timestamp ts = Log.getTimestamp();
        Thread.sleep(1000);
        Timestamp ts2 = Log.getTimestamp();
        
        long t1 = 0;
        long t2 = 3601000;
        long duration = t2 - t1;
        double seconds = duration / 1000;
        
        double hours = seconds % 3600;
        double lastHour = seconds - (hours * 3600);
        
        
        String path="src/main/java/core/";
        String name= "test.txt";
        ArrayList array = ReadWriteCSV.readCSV(path, name, false);
        double[] items = ReadWriteCSV.pickAnItemList(array, 0);
        DescriptiveStatistics.analyze(items, "rt");
        
    }
   
}
