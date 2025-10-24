package org.clas.element;

import java.util.Arrays;
import org.clas.utilities.Constants;
        
/**
 *
 * @author Tongtong
 */

public class TDC {
    private int sector;
    private int layer; // 1 to 36
    private int component;
    private int order;
    private int TDC;    
        
    public TDC(int sector, int layer, int component, int order, int TDC){
        this.sector = sector;
        this.layer = layer;
        this.component = component;
        this.order = order;  
        this.TDC = TDC;
    }
    
    public int sector(){
        return sector;
    }
    
    // 1 to 6
    public int superlayer(){
        return (this.layer-1)/6 + 1;
    }

    // 1 to 6
    public int layer(){
        return (this.layer-1)%6 + 1;
    }
    
    public int component(){
        return component;
    }

    public int order(){
        return order;
    } 
    
    public int TDC(){
       return TDC;
    } 
    
    public boolean isNormalHit(){
        for(int accptedOrder : Constants.NORMALHITORDERS){
            if(order == accptedOrder) return true;
        }
        return false;
    }
    
    public boolean isRemainedAfterAIDenoising(){
        for(int accptedOrder : Constants.FilterdHITORDERS){
            if(order == accptedOrder) return true;
        }
        return false;
    }
    
    public boolean matchHit(Hit hit){
        return sector == hit.sector() && superlayer() == hit.superlayer() && layer() == hit.layer() && component == hit.wire() && TDC == hit.TDC() + hit.jitter();
    }
    
    public boolean matchTDC(TDC tdc){
        return this.sector == tdc.sector() && this.superlayer() == tdc.superlayer() && this.layer() == tdc.layer() && this.component == tdc.component() && this.TDC == tdc.TDC();
    }
    
    
    public double calcLocY() {

        // in old mc, layer 1 is closer to the beam than layer 2, in hardware it is the opposite
        //double  brickwallPattern = GeometryLoader.dcDetector.getSector(0).getSuperlayer(0).getLayer(1).getComponent(1).getMidpoint().x()
        //		- GeometryLoader.dcDetector.getSector(0).getSuperlayer(0).getLayer(0).getComponent(1).getMidpoint().x();
        //double brickwallPattern = GeometryLoader.getDcDetector().getWireMidpoint(0, 1, 1).x
        //        - GeometryLoader.getDcDetector().getWireMidpoint(0, 0, 1).x;

        //double brickwallSign = Math.signum(brickwallPattern);
        double brickwallSign = -1;

        //center of the cell asfcn wire num
        //double y= (double)wire*(1.+0.25*Math.sin(Math.PI/3.)/(1.+Math.sin(Math.PI/6.)));
        double y = (double) component * 2 * Math.tan(Math.PI / 6.);
        if (layer() % 2 == 1) {
            //y = y-brickwallSign*Math.sin(Math.PI/3.)/(1.+Math.sin(Math.PI/6.));
            y -= brickwallSign * Math.tan(Math.PI / 6.);
        }
        return y;

    }
}