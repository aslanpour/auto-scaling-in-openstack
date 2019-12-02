/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import log.Log;

/**
 *
 * @author aslanpour
 */
public class Test {
    public static void main(String[] args) {
        double[] aaa = new double[4];
        aaa[0] = 0;
        aaa[1] =1;
        aaa[2] = 2;
        aaa[3] = 3;
        
        double[] bbb = new double[1];
        bbb[0] = 5;
        aaa = bbb.clone();
        
        int a =300000;
        int b = 60000;
        int c = a/b;
        c =c;
        System.out.println("tt\n\nt");
        String testStartTime = Log.getTimeStr().substring(0, 5);
        double currentLoad = Math.ceil(0 / 8);
        c=c;
    }
   
}
