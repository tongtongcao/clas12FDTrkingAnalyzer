package org.clas.analysis.uRWellUpdate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Random;
import javax.swing.JFrame;

import org.jlab.groot.data.H1F;
import org.jlab.groot.data.H2F;
import org.jlab.groot.graphics.EmbeddedCanvasTabbed;
import org.jlab.jnp.hipo4.data.Event;
import org.jlab.jnp.hipo4.data.SchemaFactory;
import org.jlab.jnp.hipo4.io.HipoReader;
import org.jlab.utils.benchmark.ProgressPrintout;
import org.jlab.utils.options.OptionParser;
import org.jlab.groot.math.F1D;

import org.clas.utilities.Constants;
import org.clas.element.Track;
import org.clas.element.Cluster;
import org.clas.element.Hit;
import org.clas.element.URWellHit;
import org.clas.element.URWellCluster;
import org.clas.element.URWellCross;
import org.clas.element.MCParticle;
import org.clas.graph.HistoGroup;
import org.clas.physicsEvent.BasePhysicsEvent;
import org.clas.physicsEvent.EventForLumiScan;
import org.clas.analysis.BaseAnalysis;
import org.clas.demo.DemoSector;
import org.clas.element.RunConfig;
import org.clas.element.TDC;
import org.clas.reader.Banks;
import org.clas.reader.LocalEvent;
import org.clas.fit.ClusterFitLC;
import org.jlab.groot.data.GraphErrors;
import org.jlab.groot.group.DataGroup;
import org.clas.demo.DemoBase;

/**
 *
 * @author Tongtong Cao
 */
public class ExploreDCDoubleURWellsClustering extends BaseAnalysis{ 
    
    public ExploreDCDoubleURWellsClustering(){}
    
    @Override
    public void createHistoGroupMap(){
        HistoGroup histoURWellCrossPair = new HistoGroup("uRWellCrossPair", 2, 2);
        H1F h1_xDiff = new H1F("xDiff", "Diff of x", 100, -2, 2);
        h1_xDiff.setTitleX("Diff of x");
        h1_xDiff.setTitleY("Counts");
        h1_xDiff.setLineColor(1);
        histoURWellCrossPair.addDataSet(h1_xDiff, 0);        
        H1F h1_yDiff = new H1F("yDiff", "Diff of y", 100, -2, 2);
        h1_yDiff.setTitleX("Diff of y");
        h1_yDiff.setTitleY("Counts");
        h1_yDiff.setLineColor(1);
        histoURWellCrossPair.addDataSet(h1_yDiff, 1);        
        H2F h2_xyDiff = new H2F("xyDiff", "Diff of x vs diff of y", 100, -2, 2, 100, -2, 2);
        h2_xyDiff.setTitleX("Diff of x");
        h2_xyDiff.setTitleY("Diff of y");
        histoURWellCrossPair.addDataSet(h2_xyDiff, 2);
        
        histoGroupMap.put(histoURWellCrossPair.getName(), histoURWellCrossPair);
        
        
        HistoGroup histoGroupFit = new HistoGroup("fit", 3, 3);
        H1F h1_residualR1 = new H1F("residualR1", "residualR1", 100, -2, 2);
        h1_residualR1.setTitleX("residual in LC");
        h1_residualR1.setTitleY("Counts");
        h1_residualR1.setLineColor(1);
        h1_residualR1.setOptStat(1100);
        histoGroupFit.addDataSet(h1_residualR1, 0);                 
        H1F h1_residualFittingWithURWellR1 = new H1F("residualFittingWithURWellR1", "residualFittingWithURWellR1", 100, -0.2, 0.2);
        h1_residualFittingWithURWellR1.setTitleX("residual in LC");
        h1_residualFittingWithURWellR1.setTitleY("Counts");
        h1_residualFittingWithURWellR1.setLineColor(1);
        h1_residualFittingWithURWellR1.setOptStat(1100);
        histoGroupFit.addDataSet(h1_residualFittingWithURWellR1, 1);                           
        H1F h1_lyDistToMostLeftDCHitR1= new H1F("lyDistToMostLeftDCHitR1", "lyDistToMostLeftDCHitR1", 100, -4, 4);
        h1_lyDistToMostLeftDCHitR1.setTitleX("wireDist");
        h1_lyDistToMostLeftDCHitR1.setTitleY("Counts");
        h1_lyDistToMostLeftDCHitR1.setLineColor(1);
        histoGroupFit.addDataSet(h1_lyDistToMostLeftDCHitR1, 2);        
        H1F h1_residualR2 = new H1F("residualR2", "residualR2", 100, -2, 2);
        h1_residualR2.setTitleX("residual in LC");
        h1_residualR2.setTitleY("Counts");
        h1_residualR2.setLineColor(1);
        h1_residualR2.setOptStat(1100);
        histoGroupFit.addDataSet(h1_residualR2, 3);                 
        H1F h1_residualFittingWithURWellR2 = new H1F("residualFittingWithURWellR2", "residualFittingWithURWellR2", 100, -0.2, 0.2);
        h1_residualFittingWithURWellR2.setTitleX("residual in LC");
        h1_residualFittingWithURWellR2.setTitleY("Counts");
        h1_residualFittingWithURWellR2.setLineColor(1);
        h1_residualFittingWithURWellR2.setOptStat(1100);
        histoGroupFit.addDataSet(h1_residualFittingWithURWellR2, 4);                           
        H1F h1_lyDistToMostLeftDCHitR2= new H1F("lyDistToMostLeftDCHitR2", "lyDistToMostLeftDCHitR2", 100, -4, 4);
        h1_lyDistToMostLeftDCHitR2.setTitleX("wireDist");
        h1_lyDistToMostLeftDCHitR2.setTitleY("Counts");
        h1_lyDistToMostLeftDCHitR2.setLineColor(1);
        histoGroupFit.addDataSet(h1_lyDistToMostLeftDCHitR2, 5);                 
        H1F h1_residualR1FittingWithURWellR1R2 = new H1F("residualR1FittingWithURWellR1R2", "residualR1FittingWithURWellR1R2", 100, -0.2, 0.2);
        h1_residualR1FittingWithURWellR1R2.setTitleX("residual in LC");
        h1_residualR1FittingWithURWellR1R2.setTitleY("Counts");
        h1_residualR1FittingWithURWellR1R2.setLineColor(1);
        h1_residualR1FittingWithURWellR1R2.setOptStat(1100);
        histoGroupFit.addDataSet(h1_residualR1FittingWithURWellR1R2, 7);  
        H1F h1_residualR2FittingWithURWellR1R2 = new H1F("residualR2FittingWithURWellR1R2", "residualR2FittingWithURWellR1R2", 100, -0.2, 0.2);
        h1_residualR2FittingWithURWellR1R2.setTitleX("residual in LC");
        h1_residualR2FittingWithURWellR1R2.setTitleY("Counts");
        h1_residualR2FittingWithURWellR1R2.setLineColor(1);
        h1_residualR2FittingWithURWellR1R2.setOptStat(1100);
        histoGroupFit.addDataSet(h1_residualR2FittingWithURWellR1R2, 8);                          
        histoGroupMap.put(histoGroupFit.getName(), histoGroupFit);
                
        
        HistoGroup histoGroupFitProb = new HistoGroup("fitProb", 2, 3);
        H1F h1_fitProb2Layers = new H1F("fitProb2Layers", "fitProb2Layers", 100, 0, 1.01);
        h1_fitProb2Layers.setTitleX("prob");
        h1_fitProb2Layers.setTitleY("Counts");
        h1_fitProb2Layers.setLineColor(1);
        histoGroupFitProb.addDataSet(h1_fitProb2Layers, 0);   
        H1F h1_fitProb3Layers = new H1F("fitProb3Layers", "fitProb3Layers", 100, 0, 1.01);
        h1_fitProb3Layers.setTitleX("prob");
        h1_fitProb3Layers.setTitleY("Counts");
        h1_fitProb3Layers.setLineColor(1);
        histoGroupFitProb.addDataSet(h1_fitProb3Layers, 1);          
        H1F h1_fitProb4Layers = new H1F("fitProb4Layers", "fitProb4Layers", 100, 0, 1.01);
        h1_fitProb4Layers.setTitleX("prob");
        h1_fitProb4Layers.setTitleY("Counts");
        h1_fitProb4Layers.setLineColor(1);
        histoGroupFitProb.addDataSet(h1_fitProb4Layers, 2);          
        H1F h1_fitProb5Layers = new H1F("fitProb5Layers", "fitProb5Layers", 100, 0, 1.01);
        h1_fitProb5Layers.setTitleX("prob");
        h1_fitProb5Layers.setTitleY("Counts");
        h1_fitProb5Layers.setLineColor(1);
        histoGroupFitProb.addDataSet(h1_fitProb5Layers, 3);          
        H1F h1_fitProb6Layers = new H1F("fitProb6Layers", "fitProb6Layers", 100, 0, 1.01);
        h1_fitProb6Layers.setTitleX("prob");
        h1_fitProb6Layers.setTitleY("Counts");
        h1_fitProb6Layers.setLineColor(1);
        histoGroupFitProb.addDataSet(h1_fitProb6Layers, 4);         
        histoGroupMap.put(histoGroupFitProb.getName(), histoGroupFitProb); 
        
        HistoGroup histoGroupFitProbWithURWellR1 = new HistoGroup("fitProbWithURWellR1", 2, 3);
        H1F h1_fitProb2LayersWithURWellR1 = new H1F("fitProb2LayersWithURWellR1", "fitProb2LayersWithURWell", 100, 0, 1.01);
        h1_fitProb2LayersWithURWellR1.setTitleX("prob");
        h1_fitProb2LayersWithURWellR1.setTitleY("Counts");
        h1_fitProb2LayersWithURWellR1.setLineColor(1);
        histoGroupFitProbWithURWellR1.addDataSet(h1_fitProb2LayersWithURWellR1, 0);   
        H1F h1_fitProb3LayersWithURWellR1 = new H1F("fitProb3LayersWithURWellR1", "fitProb3LayersWithURWell", 100, 0, 1.01);
        h1_fitProb3LayersWithURWellR1.setTitleX("prob");
        h1_fitProb3LayersWithURWellR1.setTitleY("Counts");
        h1_fitProb3LayersWithURWellR1.setLineColor(1);
        histoGroupFitProbWithURWellR1.addDataSet(h1_fitProb3LayersWithURWellR1, 1);          
        H1F h1_fitProb4LayersWithURWellR1 = new H1F("fitProb4LayersWithURWellR1", "fitProb4LayersWithURWell", 100, 0, 1.01);
        h1_fitProb4LayersWithURWellR1.setTitleX("prob");
        h1_fitProb4LayersWithURWellR1.setTitleY("Counts");
        h1_fitProb4LayersWithURWellR1.setLineColor(1);
        histoGroupFitProbWithURWellR1.addDataSet(h1_fitProb4LayersWithURWellR1, 2);          
        H1F h1_fitProb5LayersWithURWellR1 = new H1F("fitProb5LayersWithURWellR1", "fitProb5LayersWithURWell", 100, 0, 1.01);
        h1_fitProb5LayersWithURWellR1.setTitleX("prob");
        h1_fitProb5LayersWithURWellR1.setTitleY("Counts");
        h1_fitProb5LayersWithURWellR1.setLineColor(1);
        histoGroupFitProbWithURWellR1.addDataSet(h1_fitProb5LayersWithURWellR1, 3);          
        H1F h1_fitProb6LayersWithURWellR1 = new H1F("fitProb6LayersWithURWellR1", "fitProb6LayersWithURWell", 100, 0, 1.01);
        h1_fitProb6LayersWithURWellR1.setTitleX("prob");
        h1_fitProb6LayersWithURWellR1.setTitleY("Counts");
        h1_fitProb6LayersWithURWellR1.setLineColor(1);
        histoGroupFitProbWithURWellR1.addDataSet(h1_fitProb6LayersWithURWellR1, 4);         
        histoGroupMap.put(histoGroupFitProbWithURWellR1.getName(), histoGroupFitProbWithURWellR1);
        
        HistoGroup histoGroupFitProbWithURWellR2 = new HistoGroup("fitProbWithURWellR2", 2, 3);
        H1F h1_fitProb2LayersWithURWellR2 = new H1F("fitProb2LayersWithURWellR2", "fitProb2LayersWithURWell", 100, 0, 1.01);
        h1_fitProb2LayersWithURWellR2.setTitleX("prob");
        h1_fitProb2LayersWithURWellR2.setTitleY("Counts");
        h1_fitProb2LayersWithURWellR2.setLineColor(1);
        histoGroupFitProbWithURWellR2.addDataSet(h1_fitProb2LayersWithURWellR2, 0);   
        H1F h1_fitProb3LayersWithURWellR2 = new H1F("fitProb3LayersWithURWellR2", "fitProb3LayersWithURWell", 100, 0, 1.01);
        h1_fitProb3LayersWithURWellR2.setTitleX("prob");
        h1_fitProb3LayersWithURWellR2.setTitleY("Counts");
        h1_fitProb3LayersWithURWellR2.setLineColor(1);
        histoGroupFitProbWithURWellR2.addDataSet(h1_fitProb3LayersWithURWellR2, 1);          
        H1F h1_fitProb4LayersWithURWellR2 = new H1F("fitProb4LayersWithURWellR2", "fitProb4LayersWithURWell", 100, 0, 1.01);
        h1_fitProb4LayersWithURWellR2.setTitleX("prob");
        h1_fitProb4LayersWithURWellR2.setTitleY("Counts");
        h1_fitProb4LayersWithURWellR2.setLineColor(1);
        histoGroupFitProbWithURWellR2.addDataSet(h1_fitProb4LayersWithURWellR2, 2);          
        H1F h1_fitProb5LayersWithURWellR2 = new H1F("fitProb5LayersWithURWellR2", "fitProb5LayersWithURWell", 100, 0, 1.01);
        h1_fitProb5LayersWithURWellR2.setTitleX("prob");
        h1_fitProb5LayersWithURWellR2.setTitleY("Counts");
        h1_fitProb5LayersWithURWellR2.setLineColor(1);
        histoGroupFitProbWithURWellR2.addDataSet(h1_fitProb5LayersWithURWellR2, 3);          
        H1F h1_fitProb6LayersWithURWellR2 = new H1F("fitProb6LayersWithURWellR2", "fitProb6LayersWithURWell", 100, 0, 1.01);
        h1_fitProb6LayersWithURWellR2.setTitleX("prob");
        h1_fitProb6LayersWithURWellR2.setTitleY("Counts");
        h1_fitProb6LayersWithURWellR2.setLineColor(1);
        histoGroupFitProbWithURWellR2.addDataSet(h1_fitProb6LayersWithURWellR2, 4);         
        histoGroupMap.put(histoGroupFitProbWithURWellR2.getName(), histoGroupFitProbWithURWellR2); 
        
        HistoGroup histoGroupFitProbWithURWellR1R2 = new HistoGroup("fitProbWithURWellR1R2", 2, 3);
        H1F h1_fitProb2LayersWithURWellR1R2 = new H1F("fitProb2LayersWithURWellR1R2", "fitProb2LayersWithURWell", 100, 0, 1.01);
        h1_fitProb2LayersWithURWellR1R2.setTitleX("prob");
        h1_fitProb2LayersWithURWellR1R2.setTitleY("Counts");
        h1_fitProb2LayersWithURWellR1R2.setLineColor(1);
        histoGroupFitProbWithURWellR1R2.addDataSet(h1_fitProb2LayersWithURWellR1R2, 0);   
        H1F h1_fitProb3LayersWithURWellR1R2 = new H1F("fitProb3LayersWithURWellR1R2", "fitProb3LayersWithURWell", 100, 0, 1.01);
        h1_fitProb3LayersWithURWellR1R2.setTitleX("prob");
        h1_fitProb3LayersWithURWellR1R2.setTitleY("Counts");
        h1_fitProb3LayersWithURWellR1R2.setLineColor(1);
        histoGroupFitProbWithURWellR1R2.addDataSet(h1_fitProb3LayersWithURWellR1R2, 1);          
        H1F h1_fitProb4LayersWithURWellR1R2 = new H1F("fitProb4LayersWithURWellR1R2", "fitProb4LayersWithURWell", 100, 0, 1.01);
        h1_fitProb4LayersWithURWellR1R2.setTitleX("prob");
        h1_fitProb4LayersWithURWellR1R2.setTitleY("Counts");
        h1_fitProb4LayersWithURWellR1R2.setLineColor(1);
        histoGroupFitProbWithURWellR1R2.addDataSet(h1_fitProb4LayersWithURWellR1R2, 2);          
        H1F h1_fitProb5LayersWithURWellR1R2 = new H1F("fitProb5LayersWithURWellR1R2", "fitProb5LayersWithURWell", 100, 0, 1.01);
        h1_fitProb5LayersWithURWellR1R2.setTitleX("prob");
        h1_fitProb5LayersWithURWellR1R2.setTitleY("Counts");
        h1_fitProb5LayersWithURWellR1R2.setLineColor(1);
        histoGroupFitProbWithURWellR1R2.addDataSet(h1_fitProb5LayersWithURWellR1R2, 3);          
        H1F h1_fitProb6LayersWithURWellR1R2 = new H1F("fitProb6LayersWithURWellR1R2", "fitProb6LayersWithURWell", 100, 0, 1.01);
        h1_fitProb6LayersWithURWellR1R2.setTitleX("prob");
        h1_fitProb6LayersWithURWellR1R2.setTitleY("Counts");
        h1_fitProb6LayersWithURWellR1R2.setLineColor(1);
        histoGroupFitProbWithURWellR1R2.addDataSet(h1_fitProb6LayersWithURWellR1R2, 4);         
        histoGroupMap.put(histoGroupFitProbWithURWellR1R2.getName(), histoGroupFitProbWithURWellR1R2);        
    }
             
    public void processEvent(Event event){        
        //Read banks
        LocalEvent localEvent = new LocalEvent(reader, event, Constants.CONVTB, true);
        
        List<Cluster> validClustersSL1 = new ArrayList();
        for(Track trk : localEvent.getTracksTB()){
            if(trk.isValid()) {
                for(Cluster cls : trk.getClusters()){
                    if(cls.superlayer() == 1) validClustersSL1.add(cls);
                }
            }
        }
        List<URWellCross> uRWellCrosses = localEvent.getURWellCrosses();       
        List<URWellCross> uRWellCrossesR1 = new ArrayList();
        List<URWellCross> uRWellCrossesR2 = new ArrayList();
        for(URWellCross crs : uRWellCrosses){
            if(crs.region() == 1) uRWellCrossesR1.add(crs);
            else if(crs.region() == 2) uRWellCrossesR2.add(crs);
        }
        
        
        
        HistoGroup histoURWellCrossPair = histoGroupMap.get("uRWellCrossPair");
        for(URWellCross crs1 : uRWellCrossesR1){
            for(URWellCross crs2 : uRWellCrossesR2){
                if(crs1.sector() == crs2.sector()){
                    histoURWellCrossPair.getH1F("xDiff").fill(crs2.pointLocal().x() - crs1.pointLocal().x());
                    histoURWellCrossPair.getH1F("yDiff").fill(crs2.pointLocal().y() - crs1.pointLocal().y());
                    histoURWellCrossPair.getH2F("xyDiff").fill(crs2.pointLocal().x() - crs1.pointLocal().x(), crs2.pointLocal().y() - crs1.pointLocal().y());
                }
            }
        }
        
        
        
        Map<Cluster, URWellCross> map_cluster_uRWellCrossR1 = new HashMap();
        Map<Cluster, URWellCross> map_cluster_uRWellCrossR2 = new HashMap();
        for(Cluster cls : validClustersSL1){
            ClusterFitLC fitClus = new ClusterFitLC(cls);
            if(fitClus.lineFit()){
                double slope = fitClus.getLineFitter().slope();
                double intercept = fitClus.getLineFitter().intercept();
                double prob = fitClus.getLineFitter().getProb();                
                double minAbsResidual = 9999;
                for(URWellCross crs : uRWellCrossesR1){
                    if(crs.sector() == cls.sector()){
                        double x = URWellCross.getXRelativeDCSL1LC(crs.region());
                        double y = crs.getYRelativeDCSL1LC();
                        double absResidual = Math.abs(slope * x + intercept - y);
                        if(absResidual < minAbsResidual){
                            minAbsResidual = absResidual;
                            if(map_cluster_uRWellCrossR1.keySet().contains(cls)) map_cluster_uRWellCrossR1.replace(cls, crs);
                            else map_cluster_uRWellCrossR1.put(cls, crs);
                        }
                    }
                }
                for(URWellCross crs : uRWellCrossesR2){
                    if(crs.sector() == cls.sector()){
                        double x = URWellCross.getXRelativeDCSL1LC(crs.region());
                        double y = crs.getYRelativeDCSL1LC();
                        double absResidual = Math.abs(slope * x + intercept - y);
                        if(absResidual < minAbsResidual){
                            minAbsResidual = absResidual;
                            if(map_cluster_uRWellCrossR2.keySet().contains(cls)) map_cluster_uRWellCrossR2.replace(cls, crs);
                            else map_cluster_uRWellCrossR2.put(cls, crs);
                        }
                    }
                }                
            }
        }
        
        HistoGroup histoGroupFit = histoGroupMap.get("fit");
        for(Cluster cls : map_cluster_uRWellCrossR1.keySet()){
            URWellCross crs = map_cluster_uRWellCrossR1.get(cls);                                              
            double x = URWellCross.getXRelativeDCSL1LC(crs.region());
            double y = crs.getYRelativeDCSL1LC();     
            
            for(Hit hit : cls.getHitsAtMostLeftLayer()){
                histoGroupFit.getH1F("lyDistToMostLeftDCHitR1").fill(y - hit.calcLocY());
            }            
            
            ClusterFitLC fitClus = new ClusterFitLC(cls);
            fitClus.lineFit();
            double slope = fitClus.getLineFitter().slope();
            double intercept = fitClus.getLineFitter().intercept();
            double residual = slope * x + intercept - y;
            histoGroupFit.getH1F("residualR1").fill(residual);           
            
            
            fitClus.addURWell(crs);
            fitClus.lineFit();
            double slopeFittingWithURWell = fitClus.getLineFitter().slope();
            double interceptFittingWithURWell = fitClus.getLineFitter().intercept();
            double residualFittingWithURWell = slopeFittingWithURWell * x + interceptFittingWithURWell - y;
            histoGroupFit.getH1F("residualFittingWithURWellR1").fill(residualFittingWithURWell);   
            if(Math.abs(residual) > 1 && Math.abs(residualFittingWithURWell) > 0.1) {
                //addDemoGroup(cls, crs);
                addDemoGroup(localEvent, cls.sector(), "", true);
            }                       
        }
        
        for(Cluster cls : map_cluster_uRWellCrossR2.keySet()){
            URWellCross crs = map_cluster_uRWellCrossR2.get(cls);                                              
            double x = URWellCross.getXRelativeDCSL1LC(crs.region());
            double y = crs.getYRelativeDCSL1LC();     
            
            for(Hit hit : cls.getHitsAtMostLeftLayer()){
                histoGroupFit.getH1F("lyDistToMostLeftDCHitR2").fill(y - hit.calcLocY());
            }            
            
            ClusterFitLC fitClus = new ClusterFitLC(cls);
            fitClus.lineFit();
            double slope = fitClus.getLineFitter().slope();
            double intercept = fitClus.getLineFitter().intercept();
            double residual = slope * x + intercept - y;
            histoGroupFit.getH1F("residualR2").fill(residual);           
            
            
            fitClus.addURWell(crs);
            fitClus.lineFit();
            double slopeFittingWithURWell = fitClus.getLineFitter().slope();
            double interceptFittingWithURWell = fitClus.getLineFitter().intercept();
            double residualFittingWithURWell = slopeFittingWithURWell * x + interceptFittingWithURWell - y;
            histoGroupFit.getH1F("residualFittingWithURWellR2").fill(residualFittingWithURWell);   
            if(Math.abs(residual) > 1 && Math.abs(residualFittingWithURWell) > 0.1) {
                //addDemoGroup(cls, crs);
                addDemoGroup(localEvent, cls.sector(), "", true);
            }                       
        }

        for(Cluster cls : map_cluster_uRWellCrossR1.keySet()){
            if(map_cluster_uRWellCrossR2.keySet().contains(cls)){
                URWellCross crsR1 = map_cluster_uRWellCrossR1.get(cls);                                              
                double xR1 = URWellCross.getXRelativeDCSL1LC(crsR1.region());
                double yR1 = crsR1.getYRelativeDCSL1LC(); 
                
                URWellCross crsR2 = map_cluster_uRWellCrossR2.get(cls);                                              
                double xR2 = URWellCross.getXRelativeDCSL1LC(crsR2.region());
                double yR2 = crsR2.getYRelativeDCSL1LC(); 

                ClusterFitLC fitClus = new ClusterFitLC(cls);
                fitClus.lineFit();
                fitClus.addURWell(crsR1);
                fitClus.addURWell(crsR2);
                fitClus.lineFit();
                double slopeFittingWithURWell = fitClus.getLineFitter().slope();
                double interceptFittingWithURWell = fitClus.getLineFitter().intercept();
                double residualFittingWithURWellR1 = slopeFittingWithURWell * xR1 + interceptFittingWithURWell - yR1;
                double residualFittingWithURWellR2 = slopeFittingWithURWell * xR2 + interceptFittingWithURWell - yR2;
                histoGroupFit.getH1F("residualR1FittingWithURWellR1R2").fill(residualFittingWithURWellR1); 
                histoGroupFit.getH1F("residualR2FittingWithURWellR1R2").fill(residualFittingWithURWellR2);
                if(Math.abs(residualFittingWithURWellR1) > 0.11 && Math.abs(residualFittingWithURWellR2) > 0.14) {
                    //addDemoGroup(cls, crs);
                    addDemoGroup(localEvent, cls.sector(), "", true);
                } 
            }
        }        
        
        HistoGroup histoGroupFitProb = histoGroupMap.get("fitProb");
        HistoGroup histoGroupFitProbWithURWellR1 = histoGroupMap.get("fitProbWithURWellR1");
        Random random = new Random(); 
        for(Cluster cls : map_cluster_uRWellCrossR1.keySet()){
            URWellCross crs = map_cluster_uRWellCrossR1.get(cls);
            ClusterFitLC fitClus = new ClusterFitLC(cls);
            if(fitClus.lineFit()){                            
                List<Integer> indexList = new ArrayList();
                List<Hit> randomhits = new ArrayList();
                if(cls.numLayers(cls.getHits()) >= 2){
                    while(cls.numLayers(randomhits) < 2){                
                        int ranNum = random.nextInt(cls.getHits().size());
                        if(!indexList.contains(ranNum)) {
                            indexList.add(ranNum);
                            randomhits.add(cls.getHits().get(ranNum));
                        }
                    }

                    ClusterFitLC fit = new ClusterFitLC(randomhits);
                    fit.lineFit();
                    histoGroupFitProb.getH1F("fitProb2Layers").fill(fit.getLineFitter().getProb());
                    
                    fit.addURWell(crs);
                    if(fit.lineFit()){
                        histoGroupFitProbWithURWellR1.getH1F("fitProb2LayersWithURWellR1").fill(fit.getLineFitter().getProb());
                    }
                }
                
                if(cls.numLayers(cls.getHits()) >= 3){
                    while(cls.numLayers(randomhits) < 3){                
                        int ranNum = random.nextInt(cls.getHits().size());
                        if(!indexList.contains(ranNum)) {
                            indexList.add(ranNum);
                            randomhits.add(cls.getHits().get(ranNum));
                        }
                    }

                    ClusterFitLC fit = new ClusterFitLC(randomhits);
                    fit.lineFit();
                    histoGroupFitProb.getH1F("fitProb3Layers").fill(fit.getLineFitter().getProb());
                    
                    fit.addURWell(crs);
                    if(fit.lineFit()){
                        histoGroupFitProbWithURWellR1.getH1F("fitProb3LayersWithURWellR1").fill(fit.getLineFitter().getProb());
                    }
                }

                if(cls.numLayers(cls.getHits()) >= 4){
                    while(cls.numLayers(randomhits) < 4){                
                        int ranNum = random.nextInt(cls.getHits().size());
                        if(!indexList.contains(ranNum)) {
                            indexList.add(ranNum);
                            randomhits.add(cls.getHits().get(ranNum));
                        }
                    }

                    ClusterFitLC fit = new ClusterFitLC(randomhits);
                    fit.lineFit();
                    histoGroupFitProb.getH1F("fitProb4Layers").fill(fit.getLineFitter().getProb());
                    
                    fit.addURWell(crs);
                    if(fit.lineFit()){
                        histoGroupFitProbWithURWellR1.getH1F("fitProb4LayersWithURWellR1").fill(fit.getLineFitter().getProb());
                    }
                }

                if(cls.numLayers(cls.getHits()) >= 5){
                    while(cls.numLayers(randomhits) < 5){                
                        int ranNum = random.nextInt(cls.getHits().size());
                        if(!indexList.contains(ranNum)) {
                            indexList.add(ranNum);
                            randomhits.add(cls.getHits().get(ranNum));
                        }
                    }

                    ClusterFitLC fit = new ClusterFitLC(randomhits);
                    fit.lineFit();
                    histoGroupFitProb.getH1F("fitProb5Layers").fill(fit.getLineFitter().getProb());                    
                    
                    fit.addURWell(crs);
                    if(fit.lineFit()){
                        histoGroupFitProbWithURWellR1.getH1F("fitProb5LayersWithURWellR1").fill(fit.getLineFitter().getProb());
                    }
                }

                if(cls.numLayers(cls.getHits()) >= 6){
                    while(cls.numLayers(randomhits) < 6){                
                        int ranNum = random.nextInt(cls.getHits().size());
                        if(!indexList.contains(ranNum)) {
                            indexList.add(ranNum);
                            randomhits.add(cls.getHits().get(ranNum));
                        }

                    }

                    ClusterFitLC fit = new ClusterFitLC(randomhits);
                    fit.lineFit();
                    histoGroupFitProb.getH1F("fitProb6Layers").fill(fit.getLineFitter().getProb());
                    
                    fit.addURWell(crs);
                    if(fit.lineFit()){
                        histoGroupFitProbWithURWellR1.getH1F("fitProb6LayersWithURWellR1").fill(fit.getLineFitter().getProb());
                    }
                }
                        
            }
        }
        
        HistoGroup histoGroupFitProbWithURWellR2 = histoGroupMap.get("fitProbWithURWellR2");
        HistoGroup histoGroupFitProbWithURWellR1R2 = histoGroupMap.get("fitProbWithURWellR1R2");
        for(Cluster cls : map_cluster_uRWellCrossR2.keySet()){
            if(map_cluster_uRWellCrossR1.keySet().contains(cls)){
                URWellCross crsR2 = map_cluster_uRWellCrossR2.get(cls);
                URWellCross crsR1 = map_cluster_uRWellCrossR1.get(cls);
                ClusterFitLC fitClus = new ClusterFitLC(cls);
                if(fitClus.lineFit()){                            
                    List<Integer> indexList = new ArrayList();
                    List<Hit> randomhits = new ArrayList();
                    if(cls.numLayers(cls.getHits()) >= 2){
                        while(cls.numLayers(randomhits) < 2){                
                            int ranNum = random.nextInt(cls.getHits().size());
                            if(!indexList.contains(ranNum)) {
                                indexList.add(ranNum);
                                randomhits.add(cls.getHits().get(ranNum));
                            }
                        }

                        ClusterFitLC fit = new ClusterFitLC(randomhits);                    
                        fit.addURWell(crsR2);
                        if(fit.lineFit()){
                            histoGroupFitProbWithURWellR2.getH1F("fitProb2LayersWithURWellR2").fill(fit.getLineFitter().getProb());
                            
                            fit.addURWell(crsR1);
                            if(fit.lineFit()){
                                histoGroupFitProbWithURWellR1R2.getH1F("fitProb2LayersWithURWellR1R2").fill(fit.getLineFitter().getProb());
                            }
                        }
                    }

                    if(cls.numLayers(cls.getHits()) >= 3){
                        while(cls.numLayers(randomhits) < 3){                
                            int ranNum = random.nextInt(cls.getHits().size());
                            if(!indexList.contains(ranNum)) {
                                indexList.add(ranNum);
                                randomhits.add(cls.getHits().get(ranNum));
                            }
                        }

                        ClusterFitLC fit = new ClusterFitLC(randomhits);                    
                        fit.addURWell(crsR2);
                        if(fit.lineFit()){
                            histoGroupFitProbWithURWellR2.getH1F("fitProb3LayersWithURWellR2").fill(fit.getLineFitter().getProb());
                            
                            fit.addURWell(crsR1);
                            if(fit.lineFit()){
                                histoGroupFitProbWithURWellR1R2.getH1F("fitProb3LayersWithURWellR1R2").fill(fit.getLineFitter().getProb());
                            }
                        }
                    }

                    if(cls.numLayers(cls.getHits()) >= 4){
                        while(cls.numLayers(randomhits) < 4){                
                            int ranNum = random.nextInt(cls.getHits().size());
                            if(!indexList.contains(ranNum)) {
                                indexList.add(ranNum);
                                randomhits.add(cls.getHits().get(ranNum));
                            }
                        }

                        ClusterFitLC fit = new ClusterFitLC(randomhits);
                        fit.addURWell(crsR2);
                        if(fit.lineFit()){
                            histoGroupFitProbWithURWellR2.getH1F("fitProb4LayersWithURWellR2").fill(fit.getLineFitter().getProb());
                            
                            fit.addURWell(crsR1);
                            if(fit.lineFit()){
                                histoGroupFitProbWithURWellR1R2.getH1F("fitProb4LayersWithURWellR1R2").fill(fit.getLineFitter().getProb());
                            }
                        }
                    }

                    if(cls.numLayers(cls.getHits()) >= 5){
                        while(cls.numLayers(randomhits) < 5){                
                            int ranNum = random.nextInt(cls.getHits().size());
                            if(!indexList.contains(ranNum)) {
                                indexList.add(ranNum);
                                randomhits.add(cls.getHits().get(ranNum));
                            }
                        }

                        ClusterFitLC fit = new ClusterFitLC(randomhits);                
                        fit.addURWell(crsR2);
                        if(fit.lineFit()){
                            histoGroupFitProbWithURWellR2.getH1F("fitProb5LayersWithURWellR2").fill(fit.getLineFitter().getProb());
                            
                            fit.addURWell(crsR1);
                            if(fit.lineFit()){
                                histoGroupFitProbWithURWellR1R2.getH1F("fitProb5LayersWithURWellR1R2").fill(fit.getLineFitter().getProb());
                            }
                        }
                    }

                    if(cls.numLayers(cls.getHits()) >= 6){
                        while(cls.numLayers(randomhits) < 6){                
                            int ranNum = random.nextInt(cls.getHits().size());
                            if(!indexList.contains(ranNum)) {
                                indexList.add(ranNum);
                                randomhits.add(cls.getHits().get(ranNum));
                            }

                        }

                        ClusterFitLC fit = new ClusterFitLC(randomhits);
                        fit.addURWell(crsR2);
                        if(fit.lineFit()){
                            histoGroupFitProbWithURWellR2.getH1F("fitProb6LayersWithURWellR2").fill(fit.getLineFitter().getProb());
                            
                            fit.addURWell(crsR1);
                            if(fit.lineFit()){
                                histoGroupFitProbWithURWellR1R2.getH1F("fitProb6LayersWithURWellR1R2").fill(fit.getLineFitter().getProb());
                            }
                        }
                    }

                }
            }
        }                                                        
    }
    
    public void postEventProcess() {
                      
    }    
    
    public void addDemoGroup(Cluster cls, URWellCross uRWellCross){
        if(countDemoCases >= Constants.MAXDEMOCASES) return;
        
        DataGroup grGroup = new DataGroup("Case" + countDemoCases, 1,1 );  
                               
        String str = "Case" + countDemoCases;
        DemoBase demoCls = new DemoBase(str, str, cls.getHits());
        demoCls.addBaseLocalSuperlayer();
        demoCls.addHitGraphs(1);
        
        List<URWellCross> uRWellCrossList = new ArrayList();
        uRWellCrossList.add(uRWellCross);
        demoCls.addURWellGraph(uRWellCrossList, 1);
        for(GraphErrors gr : demoCls.getGraphList()){
                grGroup.addDataSet(gr, 0);
        }        

        demoGroupMap.put(grGroup.getName(), grGroup);

        countDemoCases++;
    }
    
    public void addDemoGroup(Cluster cls, List<Hit> pickedHits, URWellCross uRWellCross){
        if(countDemoCases >= Constants.MAXDEMOCASES) return;
        
        DataGroup grGroup = new DataGroup("Case" + countDemoCases, 1,1 );  
                               
        String str = "Case" + countDemoCases;
        DemoBase demoCls = new DemoBase(str, str, cls.getHits());
        demoCls.addBaseLocalSuperlayer();
        demoCls.addHitGraphs(1);
        demoCls.addHitGraphs("pickedHits", pickedHits, 2);
        
        List<URWellCross> uRWellCrossList = new ArrayList();
        uRWellCrossList.add(uRWellCross);
        demoCls.addURWellGraph(uRWellCrossList, 1);
        for(GraphErrors gr : demoCls.getGraphList()){
                grGroup.addDataSet(gr, 0);
        }        

        demoGroupMap.put(grGroup.getName(), grGroup);

        countDemoCases++;
    }    
                            
    public static void main(String[] args){
        OptionParser parser = new OptionParser("studyBgEffectsDC");
        parser.setRequiresInputList(false);
        // valid options for event-base analysis
        parser.addOption("-o"          ,"",     "output file name prefix");
        parser.addOption("-n"          ,"-1",   "maximum number of events to process");
        parser.addOption("-uR"          ,"12",   "regions of uRWell: 1 (region 1); 2 (region 2); other (both)");        
        parser.addOption("-plot"       ,"1",    "display histograms (0/1)");
        parser.addOption("-demo", "1", "display case demo (0/1)");
        parser.addOption("-mDemo", "100", "maxium for number of demonstrated cases");
                
        // histogram based analysis
        parser.addOption("-histo"      ,"0",    "read histogram file (0/1)");
        
        parser.parse(args);
        
        String namePrefix  = parser.getOption("-o").stringValue(); 
        int   maxEvents  = parser.getOption("-n").intValue();    
        boolean displayPlots   = (parser.getOption("-plot").intValue()!=0);
        boolean displayDemos = (parser.getOption("-demo").intValue() != 0);
        int maxDemoCases = parser.getOption("-mDemo").intValue();
        boolean readHistos   = (parser.getOption("-histo").intValue()!=0); 
        Constants.MAXDEMOCASES = maxDemoCases;
        Constants.URWELLRegions = parser.getOption("-uR").intValue(); 
        
        List<String> inputList = parser.getInputList();
        if(inputList.isEmpty()==true){
            parser.printUsage();
            inputList.add("/Users/caot/research/clas12/data/mc/uRWELL/upgradeTrackingWithuRWELL/rga-sidis-uRWell-2R_denoise/0nA/reconBg/0000.hipo");
            maxEvents = 1000;
            //System.out.println("\n >>>> error: no input file is specified....\n");
            //System.exit(0);
        }

        String histoName   = "histo.hipo"; 
        if(!namePrefix.isEmpty()) {
            histoName  = namePrefix + "_" + histoName;
        }
        
        ExploreDCDoubleURWellsClustering analysis = new ExploreDCDoubleURWellsClustering();
        analysis.createHistoGroupMap();
        
        if(!readHistos) {                 
            HipoReader reader = new HipoReader();
            reader.open(inputList.get(0));        

            SchemaFactory schema = reader.getSchemaFactory();
            analysis.initReader(new Banks(schema));

            int counter=0;
            Event event = new Event();
        
            ProgressPrintout progress = new ProgressPrintout();
            while (reader.hasNext()) {

                counter++;

                reader.nextEvent(event);                
                analysis.processEvent(event);
                progress.updateStatus();
                if(maxEvents>0){
                    if(counter>=maxEvents) break;
                }                    
            }
            
            analysis.postEventProcess();
            
            progress.showStatus();
            reader.close();            
            analysis.saveHistos(histoName);
        }
        else{
            analysis.readHistos(inputList.get(0)); 
        }
        
        if(displayPlots) {
            JFrame frame = new JFrame();
            EmbeddedCanvasTabbed canvas = analysis.plotHistos();
            if(canvas != null){
                frame.setSize(1000, 1000);
                frame.add(canvas);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        }
        
        if (displayDemos){
            JFrame frame2 = new JFrame();
            EmbeddedCanvasTabbed canvas2 = analysis.plotDemos();
            if(canvas2 != null){
                frame2.setSize(1200, 750);
                frame2.add(canvas2);
                frame2.setLocationRelativeTo(null);
                frame2.setVisible(true);
            }
        }        
    }
    
}
