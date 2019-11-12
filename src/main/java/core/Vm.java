/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import static core.DefaultSettings.FLAVOR_TABLE;
import java.sql.Timestamp;
/**
 *
 * @author fafa
 */
public class Vm {
    private int index;
    private String name;
    
    private String osServerID;
    private String imageID;
    private String privateIP;
    private String publicIP;
    
    private String flavorID;
    private String flavorName;
    // CPU, RAM, DISK
    private int vcpu;
    private int ram;
    private int disk;
    private double priceUnitPerHour;
    
    private String status;
    private String availibilityZone;
    private Timestamp timeCreated;
    private Timestamp timeDestroyed;
    
    private double bill;
    
    public Vm(int index,
            String name,
            String osServerID,
            String imageID,
            String privateIP,
            String publicIP,
            String flavorID,
            String status,
            String availibilityZone,
            Timestamp timeCreated){
        setIndex(index);
        setName(name);
        setOsServerID(osServerID);
        setImageID(imageID);
        setPrivateIP(privateIP);
        setPublicIP(publicIP);
        setFlavorID(flavorID);
        setFlavorRelatedProperties(flavorID);
        setStatus(status);
        setAvailibilityZone(availibilityZone);
        setTimeCreated(timeCreated);
        //set one hour bill
        setBill(getPriceUnitPerHour());
    }
    
      
    public void setFlavorRelatedProperties(String flavorID){
        for (String[] flavor : FLAVOR_TABLE){
            if (flavor[0] == flavorID){
                this.flavorName = flavor [1];
                this.vcpu = Integer.valueOf(flavor[2]);
                this.ram = Integer.valueOf(flavor[3]);
                this.disk = Integer.valueOf(flavor [4]);
                this.priceUnitPerHour = Double.valueOf(flavor [5]);
                break;
            }
        } 
    }

    public void billCalculator(){
        long dateStart = getTimeCreated().getTime();
        long dateEnd = getTimeDestroyed().getTime();
        long duration = dateEnd - dateStart;

        double seconds = duration / 1000;
        double hours = seconds / 3600;
        int hourRounded = (int)Math.ceil(hours);
        setBill(getPriceUnitPerHour() * hourRounded);
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getOsServerID() {
        return osServerID;
    }

    public void setOsServerID(String osServerID) {
        this.osServerID = osServerID;
    }

    
    public String getImageID() {
        return imageID;
    }

    public void setImageID(String imageID) {
        this.imageID = imageID;
    }

    public String getPrivateIP() {
        return privateIP;
    }

    public void setPrivateIP(String privateIP) {
        this.privateIP = privateIP;
    }

    public String getPublicIP() {
        return publicIP;
    }

    public void setPublicIP(String publicIP) {
        this.publicIP = publicIP;
    }

    public String getFlavorID() {
        return flavorID;
    }

    public void setFlavorID(String flavorID) {
        this.flavorID = flavorID;
    }

    public String getFlavorName() {
        return flavorName;
    }

    public void setFlavorName(String flavorName) {
        this.flavorName = flavorName;
    }

    public int getVcpu() {
        return vcpu;
    }

    public void setVcpu(int vcpu) {
        this.vcpu = vcpu;
    }

    public int getRam() {
        return ram;
    }

    public void setRam(int ram) {
        this.ram = ram;
    }

    public int getDisk() {
        return disk;
    }

    public void setDisk(int disk) {
        this.disk = disk;
    }

    public double getPriceUnitPerHour() {
        return priceUnitPerHour;
    }

    public void setPriceUnitPerHour(double priceUnitPerHour) {
        this.priceUnitPerHour = priceUnitPerHour;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAvailibilityZone() {
        return availibilityZone;
    }

    public void setAvailibilityZone(String availibilityZone) {
        this.availibilityZone = availibilityZone;
    }

    public Timestamp getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Timestamp timeCreated) {
        this.timeCreated = timeCreated;
    }

    public Timestamp getTimeDestroyed() {
        return timeDestroyed;
    }

    public void setTimeDestroyed(Timestamp timeDestroyed) {
        this.timeDestroyed = timeDestroyed;
    }

    public double getBill() {
        return bill;
    }

    public void setBill(double bill) {
        this.bill = bill;
    }
    
    
}
