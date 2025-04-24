package org.clas.element;

import org.clas.utilities.Constants;
import org.jlab.geom.prim.Point3D;

/**
 *
 * @author Tongtong
 */

public class URWellHit implements Comparable<URWellHit> {    
    private int id;
    private int status;
    private int sector;
    private int layer;
    private int strip;
    private int clusterId;
    private double energy;
    private double time;
    
    private int adc;
    private int order;
    private int ped;
    
    public URWellHit(int id, int status, int sector, int layer, int strip, int clusterId, 
            double energy, double time){
        this.id = id;
        this.status = status;
        this.sector = sector;
        this.layer = layer;
        this.strip = strip;
        this.clusterId = clusterId;
        this.energy = energy;
        this.time = time;
    }
    
    public int id(){
        return id;
    }
    
    public int status(){
        return status;
    }
    
    public int sector(){
        return sector;
    }
    
    public int layer(){
        return layer;
    }
    
    public int strip(){
        return strip;
    }
        
    public int clusterId(){
        return clusterId;
    }
    
    public double energy(){
        return energy;
    }
    
    public double time(){
        return time;
    }
    
    public int adc(){
        return adc;
    }
         
    public void adc(int adc){
        this.adc = adc;
    }
    
    public int order(){
        return order;
    }
    
    public void order(int order){
        this.order = order;
    }
    
    public int ped(){
        return ped;
    }
    
    public void ped(int ped){
        this.ped = ped;
    }
    
    public boolean isNormalHit(){
        for(int accptedOrder : Constants.NORMALHITORDERS){
            if(order == accptedOrder) return true;
        }
        return false;
    }
    
    @Override
    public int compareTo(URWellHit o) {
        return this.id()<o.id() ? -1 : 1;
    }     
}