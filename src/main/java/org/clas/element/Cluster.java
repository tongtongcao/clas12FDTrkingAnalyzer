package org.clas.element;

import java.util.List;
import java.util.ArrayList;


import org.jlab.geom.prim.Vector3D;

import org.clas.fit.ClusterFitLC;
import org.clas.fit.LineFitter;
import org.clas.utilities.Constants;
/**
 *
 * @author Tongtong
 */

public class Cluster implements Comparable<Cluster> {
    //ConvHB(11) or ConvTB(12) or AIHB(21) or AITB(22) or Cluster from clusterring(others)
    private int type = 22;
    
    private int id;
    private int status;
    private int sector;
    private int superlayer;
    private int size;
    private int[] hitIds = new int[12];
    private double avgWire;
    private double fitChisqProb;
    private double fitSlope;
    private double fitSlopeErr;
    private double fitInterc;
    private double fitIntercErr;  
    
    Vector3D dir;
    Vector3D normal;
    
    List<Hit> hits = null;
    List<Hit> normalHits = null;
    List<Hit> bgHits = null;
    private int numNormalHits = -1;
    private int numBgHits = -1;
    private double ratioNormalHits = -1;
    
    private int matchedURWellCrossId = -1;
    private URWellCross matchedURWellCross = null;
    
    private double linearFitSlope;
    private double linearFitIntercept;
    private double avgResidual;
    private double avgAbsResidual;   
    
    private double lYL1;
    private double lYL6;
    
    public Cluster(Cluster cls){
        this.copy(cls);
    }
    
    public Cluster(int type, int id, int status, int sector, int superlayer, int size, double avgWire, 
            double fitChisqProb, double fitSlope, double fitSlopeErr, double fitInterc, double fitIntercErr){
        this.type = type;
        this.id = id;
        this.status = status;
        this.sector = sector;
        this.superlayer = superlayer;
        this.size = size;
        this.avgWire = avgWire;
        this.fitChisqProb = fitChisqProb;
        this.fitSlope = fitSlope;
        this.fitSlopeErr = fitSlopeErr;
        this.fitInterc = fitInterc;
        this.fitIntercErr = fitIntercErr; 
        setDir();
        setNormal();
    }
    
    private void setDir(){
        dir = new Vector3D(fitSlope / Math.sqrt(1. + fitSlope * fitSlope), 0, 1. / Math.sqrt(1. + fitSlope * fitSlope));
        dir.scale(1./dir.mag());
    }
    
    public Vector3D getDir(){
        return dir;
    }
    
    private void setNormal(){
        double x = Math.pow(-1, (superlayer() - 1)) * Constants.SIN6;
        double y = Constants.COS6;
        Vector3D plDir = new Vector3D(x, y, 0);
        Vector3D refDir = new Vector3D(fitSlope / Math.sqrt(1. + fitSlope * fitSlope), 0, 1. / Math.sqrt(1. + fitSlope * fitSlope));         
        normal = plDir.cross(refDir);
        normal.scale(1./normal.mag());
    }
    
    public Vector3D getNormal(){
        return normal;
    }
    
    public void hitIds(int i1, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, int i10, int i11, int i12){
        this.hitIds[0] = i1;
        this.hitIds[1] = i2;
        this.hitIds[2] = i3;
        this.hitIds[3] = i4;
        this.hitIds[4] = i5;
        this.hitIds[5] = i6;
        this.hitIds[6] = i7;
        this.hitIds[7] = i8;
        this.hitIds[8] = i9;
        this.hitIds[9] = i10;
        this.hitIds[10] = i11;
        this.hitIds[11] = i12;
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
    
    public int size(){
        return size;
    }
        
    public int[] hitIds(){
        return hitIds;
    }
    
    public double avgWire(){
        return avgWire;
    }
    
    public double fitChisqProb(){
        return fitChisqProb;
    }
    
    public double fitSlope(){
        return fitSlope;
    }
    
    public double fitSlopeErr(){
        return fitSlopeErr;
    }
    
    public double fitInterc(){
        return fitInterc;
    }
    
    public double fitIntercErr(){
        return fitIntercErr;
    }  
    
    public void setHits(List<Hit> allHits){
        hits = new ArrayList();
        for(Hit hit: allHits){
            if(hit.ClusterID() == this.id) hits.add(hit);
        }
    }
    
    public List<Hit> getHits(){
        return hits;
    }               
    
    public boolean separateNormalBgHits(){
        normalHits = new ArrayList();
        bgHits = new ArrayList();
        
        if(hits == null) return false;
        else {
            for(Hit hit : hits){
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
    
    public List<Hit> getNormalHits(){
        return normalHits;
    }
    
    public List<Hit> getBgHits(){
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
    
    public int clusterMatchedHits(Cluster otherCls){
        if(this.hits == null || otherCls.hits == null) return -999;
        int matchedHits = 0;
        for(Hit hitThisCluster : this.hits){
            for(Hit hitOtherCluster : otherCls.hits){
                if(hitThisCluster.sector() == hitOtherCluster.sector() && hitThisCluster.superlayer()== hitOtherCluster.superlayer() && hitThisCluster.layer() == hitOtherCluster.layer()
                        && hitThisCluster.wire() == hitOtherCluster.wire() && hitThisCluster.TDC() == hitOtherCluster.TDC()){
                    matchedHits++;
                    break;
                }
            }
        }
        return matchedHits;
    }
    
    public int numLayers(List<Hit> hits){
        int[] numHitsonLayers = new int[6];
        for(Hit hit : hits){
            numHitsonLayers[hit.layer()-1]++;
        }
        
        int numLayers = 0;
        for(int i = 0; i < 6; i++){
            if(numHitsonLayers[i] > 0) numLayers++;
        }
        
        return numLayers;
    }
    
    public int numLayers(){
        int[] numHitsonLayers = new int[6];
        for(Hit hit : hits){
            numHitsonLayers[hit.layer()-1]++;
        }
        
        int numLayers = 0;
        for(int i = 0; i < 6; i++){
            if(numHitsonLayers[i] > 0) numLayers++;
        }
        
        return numLayers;
    }
    
    public List<Hit> getHitsAtMostLeftLayer(){
        int mostLeftLayer = 6;
        List<Hit> hitsAtMostLeftLayer = new ArrayList();
        for(Hit hit : hits){
            if(hit.layer() < mostLeftLayer){
                mostLeftLayer = hit.layer();
                hitsAtMostLeftLayer.clear();
                hitsAtMostLeftLayer.add(hit);
            }
            else if(hit.layer() == mostLeftLayer){
                hitsAtMostLeftLayer.add(hit);
            }            
        }
        
        return hitsAtMostLeftLayer;
        
    }
    
    public void setLinearFitParameters(LineFitter lineFitter){
        linearFitSlope = lineFitter.slope();
        linearFitIntercept = lineFitter.intercept();
        calAvgResidual();
        calAvgAbsResidual();
    }
    
    private void calAvgResidual(){
        double totalResidual = 0;
        for(Hit hit : this.getHits()){
            totalResidual += (hit.calcLocY() - (linearFitSlope * hit.layer() + linearFitIntercept));
        }
        this.avgResidual = totalResidual/this.getHits().size();
    }
    
    private void calAvgAbsResidual(){
        double totalAbsResidual = 0;
        for(Hit hit : this.getHits()){
            totalAbsResidual += Math.abs(hit.calcLocY() - (linearFitSlope * hit.layer() + linearFitIntercept));
        }
        this.avgAbsResidual = totalAbsResidual/this.getHits().size();
    }
    
    public double getLinearFitSlope(){
        return linearFitSlope;
    }
    
    public double getLinearFitIntercept(){
        return linearFitIntercept;
    }
    
    public double getAvgResidual(){
        return avgResidual;
    }
    
    public double getAvgAbsResidual(){
        return avgAbsResidual;
    }
    
    // Find matched uRWell cross based on DC fitting
    public URWellCross getMatchedURWell(List<URWellCross> crosses){
        ClusterFitLC fit = new ClusterFitLC(this);
        if(fit.lineFit()){        
            double slope = fit.getLineFitter().slope();                
            double intercept = fit.getLineFitter().intercept();

            double x = URWellCross.getXRelativeDCSL1LC();
            URWellCross selectedCross = null;
            double minABSResidual = 999;
            for(URWellCross crs : crosses){
                double y = crs.getYRelativeDCSL1LC();
                double absResidual = Math.abs(x * slope + intercept - y);
                if(absResidual < minABSResidual && absResidual < 1){
                    minABSResidual = absResidual;
                    selectedCross = crs;
                }
            }            
            return selectedCross;
        }        
        else return null;
    }
    
    public void setMatchedURWellCross(URWellCross uRWellCross){
        this.matchedURWellCross = uRWellCross;
    }
    
    public URWellCross getMatchedURWellCross(){
        return matchedURWellCross;
    }
    
    public void setMatchedURWellCrossId(int uRWellCrossId){
        this.matchedURWellCrossId = uRWellCrossId;
    }
    
    public int getMatchedURWellCrossId(){
        return matchedURWellCrossId;
    }
    
    public double getURWellResidualWithDCURWellFitting(){
        ClusterFitLC fit = new ClusterFitLC(this);
        if(this.matchedURWellCross != null){
            fit.addURWell(this.matchedURWellCross);
            fit.lineFit();
            double slope = fit.getLineFitter().slope();
            double intercept = fit.getLineFitter().intercept();
            double residual = slope * URWellCross.getXRelativeDCSL1LC() + intercept - this.matchedURWellCross.getYRelativeDCSL1LC();
            return residual;
        }
        else return -9999;
    }
    
    public double getURWellResidualWithDCURWellFitting(URWellCross crs){
        ClusterFitLC fit = new ClusterFitLC(this);
        if(crs != null){
            fit.addURWell(crs);
            fit.lineFit();
            double slope = fit.getLineFitter().slope();
            double intercept = fit.getLineFitter().intercept();
            double residual = slope * URWellCross.getXRelativeDCSL1LC() + intercept - crs.getYRelativeDCSL1LC();
            return residual;
        }
        else return -9999;
    }
    
    public double getAvgLy(){
        double totalLy = 0;
        for(Hit hit : this.getHits()){
            totalLy += hit.calcLocY();
        }
        return totalLy/this.getHits().size();
    }
    
    public double getMaxLy(){
        double maxLy = -999;
        for(Hit hit : this.getHits()){
            if(hit.calcLocY() > maxLy) maxLy = hit.calcLocY();
        }
        return maxLy;
    }
    
    public double getMinLy(){
        double minLy = 999;
        for(Hit hit : this.getHits()){
            if(hit.calcLocY() < minLy) minLy = hit.calcLocY();
        }
        return minLy;
    }
    
    public void setLYL1(double lYL1){
        this.lYL1 = lYL1;
    }
    
    public double getLYL1(){
        return lYL1;
    }
    
    public void setLYL6(double lYL6){
        this.lYL6 = lYL6;
    }
    
    public double getLYL6(){
        return lYL6;
    }
    
    
           
    public final void copy(Cluster cls) {
        this.type = cls.type();

        this.id = cls.id();
        this.status = cls.status();
        this.sector = cls.sector();
        this.superlayer = cls.superlayer();
        this.size = cls.size();
        this.hitIds = cls.hitIds();
        this.avgWire = cls.avgWire();
        this.fitChisqProb = cls.fitChisqProb();
        this.fitSlope = cls.fitSlope();
        this.fitSlopeErr = cls.fitSlopeErr();
        this.fitInterc = cls.fitInterc();
        this.fitIntercErr = cls.fitIntercErr();   

        this.hits = cls.getHits();
        this.normalHits = cls.getNormalHits();
        this.bgHits = cls.getBgHits();
        this.numNormalHits = cls.getNumNormalHits();
        this.numBgHits = cls.getNumBgHits();
        this.ratioNormalHits = cls.getRatioNormalHits();

        this.matchedURWellCrossId = cls.getMatchedURWellCrossId();
        this.matchedURWellCross = cls.getMatchedURWellCross();

        this.linearFitSlope = cls.getLinearFitSlope();
        this.linearFitIntercept = cls.getLinearFitIntercept();
        this.avgResidual = cls.getAvgResidual();
        this.avgAbsResidual = cls.getAvgAbsResidual();
    }
    
    
    @Override
    public int compareTo(Cluster o) {
        return this.id()<o.id() ? -1 : 1;
    }         
}