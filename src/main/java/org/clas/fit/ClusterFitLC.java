package org.clas.fit;

import java.util.ArrayList;
import java.util.List;
import org.clas.element.Hit;
import org.clas.element.Cluster;
import org.clas.element.URWellCross;

/**
 * @author Tongtong Cao
 */
public class ClusterFitLC{
    private int nPoints = 0;
    private List<Double> locX = new ArrayList();
    private List<Double> locY = new ArrayList();
    private List<Double> locXErr = new ArrayList();
    private List<Double> locYErr = new ArrayList();
    
    LineFitter fitter = new LineFitter();
    
    public ClusterFitLC(Cluster cls){
        nPoints = cls.getHits().size();
        for(Hit hit : cls.getHits()){
            locX.add((double)hit.layer());
            locY.add(hit.calcLocY());
            locXErr.add(0.);
            locYErr.add(1.);
        }                
    }
    
    public ClusterFitLC(List<Hit> hits){
        nPoints = hits.size();
        for(Hit hit : hits){
            locX.add((double)hit.layer());
            locY.add(hit.calcLocY());
            locXErr.add(0.);
            locYErr.add(1.);
        }
    }
    
    public void addURWell(URWellCross crs){
        locX.add(URWellCross.getXRelativeDCSL1LC());
        locY.add(crs.getYRelativeDCSL1LC());
        locXErr.add(0.);
        locYErr.add(0.5);
        nPoints++;
    }
    
    public boolean lineFit(){
       return fitter.fitStatus(locX, locY, locXErr, locYErr, nPoints);
    }
    
    public LineFitter getLineFitter(){
        return fitter;
    }        
}