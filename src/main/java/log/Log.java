/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package log;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author fafa
 */
public class Log {
    
    public static Timestamp getTimestamp(){
//        Date currentDate = new java.util.Date();
//        String timestamp = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS").format(currentDate);
        Timestamp timestamp = new Timestamp(new Date().getTime());
        return timestamp;
    }
    
    public static String getTimestampStr(){
//        Date currentDate = new java.util.Date();
//        String timestamp = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS").format(currentDate);
        Timestamp timestamp = new Timestamp(new Date().getTime());
        return timestamp.toString();
    }
    public static void printTimestamp(){
        Date currentDate = new java.util.Date();
//        String timestamp = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS").format(currentDate);
        Timestamp timestamp = new Timestamp(new Date().getTime());
        printLine1(timestamp.toString());
    }
    
    public static void printLine1(String log){
        System.out.println(getTimestampStr() + log);
    }
    
    public static void printLine2(String log){
        System.out.println(getTimestampStr() + log);
    }
    
    public static void printLine3(String log){
        System.out.println(getTimestampStr() + log);
    }
    
    public static void printLine4(String log){
        System.out.println(getTimestampStr() + log);
    }
    
    public static void printLine1(String className, String methodName, String command){
        System.out.println(getTimestampStr() + 
                "---#" + className + "/" + methodName + "#---" + command);
    }
    
    public static void printLine2(String className, String methodName, String command){
        System.out.println(getTimestampStr() +
                "---#" + className + "/" + methodName + "#---" + command);
    }
    
    public static void printLine3(String className, String methodName, String command){
        System.out.println(getTimestampStr() +
                "---#" + className + "/" + methodName + "#---" + command);
    }
    
    public static void printLine4(String className, String methodName, String command){
        System.out.println(getTimestampStr() + 
                "---#" + className + "/" + methodName + "#---" + command);
    }
}
