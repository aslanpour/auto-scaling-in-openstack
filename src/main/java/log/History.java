/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package log;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author fafa
 */
public class History {

    String timestamp;
    int year;
    int month;
    int day;
    int hour;
    int minute;
    int second;
    int millisecond;
    
    public History(){
        Date currentDate = new java.util.Date();
        timestamp = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS").format(currentDate);
        String[] timeSplit = timestamp.split("-");
        year = Integer.valueOf(timeSplit[0]);
        month = Integer.valueOf(timeSplit[1]);
        day = Integer.valueOf(timeSplit[2]);
        hour = Integer.valueOf(timeSplit[3]);
        minute = Integer.valueOf(timeSplit[4]);
        second = Integer.valueOf(timeSplit[5]);
        millisecond = Integer.valueOf(timeSplit[6]);
    }

    public String getTimestamp() {
        return timestamp;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public int getSecond() {
        return second;
    }

    public int getMillisecond() {
        return millisecond;
    }
    
}
