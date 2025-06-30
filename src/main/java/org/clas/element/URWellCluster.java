package org.clas.element;

import java.util.ArrayList;
import java.util.List;
import org.clas.utilities.Constants;
import org.jlab.geom.prim.Point3D;

/**
 *
 * @author Tongtong
 */

public class URWellCluster implements Comparable<URWellCluster> {    
    private int id;
    private int status;
    private int sector;
    private int layer;
    private int strip;
    private int size;
    private double energy;
    private double time;
    private Point3D originalPoint = null;
    private Point3D endPoint = null;
    private Point3D originalPointLocal = null;
    private Point3D endPointLocal = null;
    
    private List<URWellHit> hits = null;
    List<URWellHit> normalHits = null;
    List<URWellHit> bgHits = null;
    private int numNormalHits = -1;
    private int numBgHits = -1;
    private double ratioNormalHits = -1;    
    
    private List<Integer> stripOrders; // could be multiple hits in a main strip
    
    public URWellCluster(int id, int status, int sector, int layer, int strip, int size,
            double energy, double time, double xo, double yo, double zo, double xe, double ye, double ze){
        this.id = id;
        this.status = status;
        this.sector = sector;
        this.layer = layer;
        this.strip = strip;
        this.size = size;
        this.energy = energy;
        this.time = time;
        this.originalPoint = new Point3D(xo, yo, zo);
        this.endPoint = new Point3D(xe, ye, ze);
        
        this.originalPointLocal = new Point3D(xo, yo, zo);
        originalPointLocal.rotateZ(Math.toRadians(-60 * (sector - 1)));
        originalPointLocal.rotateY(Math.toRadians(-25));
        
        this.endPointLocal = new Point3D(xe, ye, ze);
        endPointLocal.rotateZ(Math.toRadians(-60 * (sector - 1)));
        endPointLocal.rotateY(Math.toRadians(-25));
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
        
    public int size(){
        return size;
    }
    
    public double energy(){
        return energy;
    }
    
    public double time(){
        return time;
    }
    
    public Point3D originalPoint(){
        return originalPoint;
    }
    
    public Point3D endPoint(){
        return endPoint;
    } 
    
    public Point3D originalPointLocal(){
        return originalPointLocal;
    }
    
    public Point3D endPointLocal(){
        return endPointLocal;
    }
    
    public void setStripOrder(List<Integer> orders){
        stripOrders = orders;
    }
    
    public List<Integer> stripOrders(){
        return stripOrders;
    }
    
    public boolean isMainStripNormal(){ // If one of orders in main strip is normal, then the cluster is regarded as normal
        for(int order : stripOrders){
            for(int accptedOrder : Constants.NORMALHITORDERS){
                if(order == accptedOrder) return true;
            }
        }
        return false;
    }
    
    public void setHits(List<URWellHit> allHits){
        hits = new ArrayList();
        for(URWellHit hit: allHits){
            if(hit.clusterId() == this.id) hits.add(hit);
        }
    }
    
    public List<URWellHit> getHits(){
        return hits;
    }
    
    public boolean separateNormalBgHits(){
        normalHits = new ArrayList();
        bgHits = new ArrayList();
        
        if(hits == null) return false;
        else {
            for(URWellHit hit : hits){
                if(hit.isNormalHit())
                    normalHits.add(hit);                
                else bgHits.add(hit);
            }
            
            numNormalHits = normalHits.size();
            numBgHits = bgHits.size();
            ratioNormalHits = (double) numNormalHits/size;
            return true;
        }
    }
    
    public List<URWellHit> getNormalHits(){
        return normalHits;
    }
    
    public List<URWellHit> getBgHits(){
        return bgHits;
    }
    
    public int getNumNormalHits(){
        return numNormalHits;
    } 
    
    public int getNumBgHits(){
        return numBgHits;
    }
    
    public double getRatioNormalHits(){
        return ratioNormalHits;
    }    
    
    @Override
    public int compareTo(URWellCluster o) {
        return this.id()<o.id() ? -1 : 1;
    } 
}