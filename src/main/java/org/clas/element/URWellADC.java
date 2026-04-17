package org.clas.element;

import org.clas.utilities.Constants;
import org.jlab.geom.prim.Point3D;

/**
 *
 * @author Tongtong
 */

public class URWellADC{    
    private int sector;
    private int layer;
    private int component;
    private int order;
    private int adc;
    private int ped;
    private double time;
    
    public URWellADC(int sector, int layer, int component, int order, int adc, int ped, double time){
        this.sector = sector;
        this.layer = layer;
        this.component = component;
        this.order = order;
        this.adc = adc;
        this.ped = ped;
        this.time = time;
    }
    
    public int sector(){
        return sector;
    }
    
    public int layer(){
        return layer;
    }
    
    public int component(){
        return component;
    }
        
    public int order(){
        return order;
    }
    
    public int adc(){
        return adc;
    }
        
    public int ped(){
        return ped;
    }    
        
    public double time(){
        return time;
    }  
    
    public boolean isRemainedAfterDecoding(){
        boolean flagRejection = false;
        for(int rejectedOrder : Constants.DECODINGREJECTEDHITORDERS){
            if(order == rejectedOrder) {
                flagRejection = true;
                break;
            }
        }
        
        if(flagRejection) return false;
        else return true;
    } 
}