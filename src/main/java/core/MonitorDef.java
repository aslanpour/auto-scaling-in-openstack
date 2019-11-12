/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author fafa
 */
public abstract class MonitorDef implements Runnable {
   public void run(){
       System.out.println("from run method");
       try {
           Thread.sleep(3000);
       } catch (InterruptedException ex) {
           Logger.getLogger(MonitorDef.class.getName()).log(Level.SEVERE, null, ex);
       }
   } 
}
