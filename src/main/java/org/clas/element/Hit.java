package org.clas.element;

import java.util.Arrays;
import org.clas.utilities.Constants;

/**
 *
 * @author Tongtong
 */

public class Hit implements Comparable<Hit> {
    //ConvHB(11) or ConvTB(12) or AIHB(21) or AITB(22) or Cluster from clusterring(others)
    private int type = 22;
    
    private int id;
    private int status;
    private int sector;
    private int superlayer;
    private int layer;
    private int wire;
    private int TDC;
    private int ClusterID;
    private double x;
    private double z;
    private double trkDoca;
    private double docaErr;
    private int LR;
    private double dafWeight = -999.;
    
    private int tid = -999; // track id
    private double tProp = -999.;
    private double tFlight = -999.;
    private double t0 = -999.;
    private double beta = -999.;
    
    private int indexTDC = -1;
    private int order = -1;
        
    public Hit(int type, int id, int status, int sector, int superlayer, int layer, int wire, int TDC, int ClusterID, double x, double z, double trkDoca, double docaErr, int LR){
        this.type = type;
        this.id = id;
        this.status = status;
        this.sector = sector;
        this.superlayer = superlayer;
        this.layer = layer;
        this.wire = wire;
        this.TDC = TDC;
        this.ClusterID = ClusterID; 
        this.x = x;
        this.z = z;
        this.trkDoca = trkDoca;
        this.docaErr = docaErr;
        this.LR = LR;
    }
    
    public int type(){
        return type;
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
    
    public int superlayer(){
        return superlayer;
    }
    
    public int layer(){
        return layer;
    }
    
    public int wire(){
        return wire;
    }
    
    public int TDC(){
        return TDC;
    }
    
    public int ClusterID(){
        return ClusterID;
    }
    
    public double x(){
        return x;
    }
    
    public double z(){
        return z;
    }    
    
    public double trkDoca(){
        return trkDoca;
    }

    public double docaErr(){
        return docaErr;
    }
    
    public int LR(){
        return LR;
    }
    
    public void setTid(int tid){
        this.tid = tid;
    }
    
    public int tid(){
        return tid;
    }
    
    public void setTProp(double tProp){
        this.tProp = tProp;
    }
    
    public double tProp(){
        return tProp;
    }
    
    public void setTFlight(double tFlight){
        this.tFlight = tFlight;
    }
    
    public double tFlight(){
        return tFlight;
    }    
    
    public void setT0(double t0){
        this.t0 = t0;
    }     
    
    public double t0(){
        return t0;
    }
    
    public void setBeta(double beta){
        this.beta = beta;
    }     
    
    public double beta(){
        return beta;
    }    
    
    public void indexTDC(int indexTDC){
        this.indexTDC = indexTDC;
    }
    
    public int indexTDC(){
        return indexTDC;
    }
    
    public void order(int order){
        this.order = order;
    }
    
    public int order(){
        return order;
    }
    
    public double dafWeight(){
        return dafWeight;
    }
    
    public void dafWeight(double weight){
        this.dafWeight = weight;
    }
    
    public boolean isNormalHit(){
        for(int accptedOrder : Constants.NORMALHITORDERS){
            if(order == accptedOrder) return true;
        }
        return false;
    }
    
    public boolean hitMatched(TDC tdc){
        return sector == tdc.sector() && superlayer == tdc.superlayer() && layer == tdc.layer() && wire == tdc.component() && TDC == tdc.TDC();
    }
    
    public boolean hitMatched(Hit hit){ 
        return sector == hit.sector() && superlayer == hit.superlayer() && layer == hit.layer() && wire == hit.wire() && TDC == hit.TDC();
    }
    
    public boolean hitMatchedNoRequireTDC(Hit hit){ 
        return sector == hit.sector() && superlayer == hit.superlayer() && layer == hit.layer() && wire == hit.wire();
    }    
    
    /**
     *
     * @param layer layer number from 1 to 6
     * @param wire wire number from 1 to 112 calculates the center of the cell
     * as a function of wire number in the local superlayer coordinate system.
     * @return y
     */
    public double calcLocY(int layer, int wire) {

        // in old mc, layer 1 is closer to the beam than layer 2, in hardware it is the opposite
        //double  brickwallPattern = GeometryLoader.dcDetector.getSector(0).getSuperlayer(0).getLayer(1).getComponent(1).getMidpoint().x()
        //		- GeometryLoader.dcDetector.getSector(0).getSuperlayer(0).getLayer(0).getComponent(1).getMidpoint().x();
        //double brickwallPattern = GeometryLoader.getDcDetector().getWireMidpoint(0, 1, 1).x
        //        - GeometryLoader.getDcDetector().getWireMidpoint(0, 0, 1).x;

        //double brickwallSign = Math.signum(brickwallPattern);
        double brickwallSign = -1;

        //center of the cell asfcn wire num
        //double y= (double)wire*(1.+0.25*Math.sin(Math.PI/3.)/(1.+Math.sin(Math.PI/6.)));
        double y = (double) wire * 2 * Math.tan(Math.PI / 6.);
        if (layer % 2 == 1) {
            //y = y-brickwallSign*Math.sin(Math.PI/3.)/(1.+Math.sin(Math.PI/6.));
            y -= brickwallSign * Math.tan(Math.PI / 6.);
        }
        return y;

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
        double y = (double) wire * 2 * Math.tan(Math.PI / 6.);
        if (layer % 2 == 1) {
            //y = y-brickwallSign*Math.sin(Math.PI/3.)/(1.+Math.sin(Math.PI/6.));
            y -= brickwallSign * Math.tan(Math.PI / 6.);
        }
        return y;

    }
    
    @Override
    public int compareTo(Hit o) {
        return this.id()<o.id() ? -1 : 1;
    }         
}