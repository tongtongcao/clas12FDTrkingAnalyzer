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
public class ExploreDCURWellClustering extends BaseAnalysis{ 
    
    public ExploreDCURWellClustering(){}
    
    @Override
    public void createHistoGroupMap(){
        HistoGroup histoGroupFit = new HistoGroup("fit", 3, 4);
        H1F h1_residual = new H1F("residual", "residual", 100, -2, 2);
        h1_residual.setTitleX("residual in LC");
        h1_residual.setTitleY("Counts");
        h1_residual.setLineColor(1);
        h1_residual.setOptStat(1100);
        histoGroupFit.addDataSet(h1_residual, 0); 
        
        H2F h2_residualVsSlope = new H2F("residualVsSlope", "residualVsSlope", 100, -1, 1, 100, -2, 2);
        h2_residualVsSlope.setTitleX("slope");
        h2_residualVsSlope.setTitleY("residual");
        histoGroupFit.addDataSet(h2_residualVsSlope, 1); 
        
        H1F h1_residualFittingWithURWell = new H1F("residualFittingWithURWell", "residualFittingWithURWell", 100, -0.2, 0.2);
        h1_residualFittingWithURWell.setTitleX("residual in LC");
        h1_residualFittingWithURWell.setTitleY("Counts");
        h1_residualFittingWithURWell.setLineColor(1);
        h1_residualFittingWithURWell.setOptStat(1100);
        histoGroupFit.addDataSet(h1_residualFittingWithURWell, 3); 
        
        H2F h2_residualVsSlopeFittingWithURWell = new H2F("residualVsSlopeFittingWithURWell", "residualVsSlopeFittingWithURWell", 100, -1, 1, 100, -0.2, 0.2);
        h2_residualVsSlopeFittingWithURWell.setTitleX("slope");
        h2_residualVsSlopeFittingWithURWell.setTitleY("residual");
        histoGroupFit.addDataSet(h2_residualVsSlopeFittingWithURWell, 4); 
        
        
        H2F h2_residualWitoutWithURWell = new H2F("residualWitoutWithURWell", "residualWitoutWithURWell", 100, -2, 2, 100, -0.5, 0.5);
        h2_residualWitoutWithURWell.setTitleX("residual without URWell");
        h2_residualWitoutWithURWell.setTitleY("residual with URWell");
        histoGroupFit.addDataSet(h2_residualWitoutWithURWell, 5); 
        
        H2F h2_probComp = new H2F("probComp", "probComp", 100, 0, 1, 100, 0, 1);
        h2_probComp.setTitleX("prob for only DC");
        h2_probComp.setTitleY("prob with uRWell");
        histoGroupFit.addDataSet(h2_probComp, 6); 
        
        H1F h1_probDiff = new H1F("probDiff", "probDiff", 100, -0.2, 0.2);
        h1_probDiff.setTitleX("diff of prob");
        h1_probDiff.setTitleY("Counts");
        h1_probDiff.setLineColor(1);
        histoGroupFit.addDataSet(h1_probDiff, 7);    
        
        H1F h1_lyDistToMostLeftDCHit= new H1F("lyDistToMostLeftDCHit", "lyDistToMostLeftDCHit", 100, -4, 4);
        h1_lyDistToMostLeftDCHit.setTitleX("wireDist");
        h1_lyDistToMostLeftDCHit.setTitleY("Counts");
        h1_lyDistToMostLeftDCHit.setLineColor(1);
        histoGroupFit.addDataSet(h1_lyDistToMostLeftDCHit, 8);
        
        H1F h1_lyDistToAvgLy= new H1F("lyDistToAvgLy", "lyDistToAvgLy", 100, -4, 4);
        h1_lyDistToAvgLy.setTitleX("wireDist");
        h1_lyDistToAvgLy.setTitleY("Counts");
        h1_lyDistToAvgLy.setLineColor(1);
        histoGroupFit.addDataSet(h1_lyDistToAvgLy, 9);
        
        H1F h1_lyDistToMaxLy= new H1F("lyDistToMaxLy", "lyDistToMaxLy", 100, -4, 4);
        h1_lyDistToMaxLy.setTitleX("wireDist");
        h1_lyDistToMaxLy.setTitleY("Counts");
        h1_lyDistToMaxLy.setLineColor(1);
        histoGroupFit.addDataSet(h1_lyDistToMaxLy, 10);
        
        H1F h1_lyDistToMinLy= new H1F("lyDistToMinLy", "lyDistToMinLy", 100, -4, 4);
        h1_lyDistToMinLy.setTitleX("wireDist");
        h1_lyDistToMinLy.setTitleY("Counts");
        h1_lyDistToMinLy.setLineColor(1);
        histoGroupFit.addDataSet(h1_lyDistToMinLy, 11);        
        
        histoGroupMap.put(histoGroupFit.getName(), histoGroupFit);
        
        HistoGroup histoGroupResidual = new HistoGroup("residual", 2, 3);
        H1F h1_residual2Layers= new H1F("residual2Layers", "residual2Layers", 100, -2, 2);
        h1_residual2Layers.setTitleX("residual");
        h1_residual2Layers.setTitleY("Counts");
        histoGroupResidual.addDataSet(h1_residual2Layers, 0);        
        H1F h1_residual3Layers= new H1F("residual3Layers", "residual3Layers", 100, -2, 2);
        h1_residual3Layers.setTitleX("residual");
        h1_residual3Layers.setTitleY("Counts");
        histoGroupResidual.addDataSet(h1_residual3Layers, 1);         
        H1F h1_residual4Layers= new H1F("residual4Layers", "residual4Layers", 100, -2, 2);
        h1_residual4Layers.setTitleX("residual");
        h1_residual4Layers.setTitleY("Counts");
        histoGroupResidual.addDataSet(h1_residual4Layers, 2);         
        H1F h1_residual5Layers= new H1F("residual5Layers", "residual5Layers", 100, -2, 2);
        h1_residual5Layers.setTitleX("residual");
        h1_residual5Layers.setTitleY("Counts");
        histoGroupResidual.addDataSet(h1_residual5Layers, 3);         
        H1F h1_residual6Layers= new H1F("residual6Layers", "residual6Layers", 100, -2, 2);
        h1_residual6Layers.setTitleX("residual");
        h1_residual6Layers.setTitleY("Counts");
        histoGroupResidual.addDataSet(h1_residual6Layers, 4); 
        histoGroupMap.put(histoGroupResidual.getName(), histoGroupResidual);        
        
        HistoGroup histoGroupResidualWithURWell = new HistoGroup("residualWithURWell", 2, 3);
        H1F h1_residual2LayersWithURWell= new H1F("residual2LayersWithURWell", "residual2LayersWithURWell", 100, -0.5, 0.5);
        h1_residual2LayersWithURWell.setTitleX("residual");
        h1_residual2LayersWithURWell.setTitleY("Counts");
        histoGroupResidualWithURWell.addDataSet(h1_residual2LayersWithURWell, 0);                
        H1F h1_residual3LayersWithURWell= new H1F("residual3LayersWithURWell", "residual3LayersWithURWell", 100, -0.5, 0.5);
        h1_residual3LayersWithURWell.setTitleX("residual");
        h1_residual3LayersWithURWell.setTitleY("Counts");
        histoGroupResidualWithURWell.addDataSet(h1_residual3LayersWithURWell, 1);          
        H1F h1_residual4LayersWithURWell= new H1F("residual4LayersWithURWell", "residual4LayersWithURWell", 100, -0.5, 0.5);
        h1_residual4LayersWithURWell.setTitleX("residual");
        h1_residual4LayersWithURWell.setTitleY("Counts");
        histoGroupResidualWithURWell.addDataSet(h1_residual4LayersWithURWell, 2);          
        H1F h1_residual5LayersWithURWell= new H1F("residual5LayersWithURWell", "residual5LayersWithURWell", 100, -0.5, 0.5);
        h1_residual5LayersWithURWell.setTitleX("residual");
        h1_residual5LayersWithURWell.setTitleY("Counts");
        histoGroupResidualWithURWell.addDataSet(h1_residual5LayersWithURWell, 3);          
        H1F h1_residual6LayersWithURWell= new H1F("residual6LayersWithURWell", "residual6LayersWithURWell", 100, -0.5, 0.5);
        h1_residual6LayersWithURWell.setTitleX("residual");
        h1_residual6LayersWithURWell.setTitleY("Counts");
        histoGroupResidualWithURWell.addDataSet(h1_residual6LayersWithURWell, 4);  
        histoGroupMap.put(histoGroupResidualWithURWell.getName(), histoGroupResidualWithURWell);
        
        
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
        
        HistoGroup histoGroupFitProbWithURWell = new HistoGroup("fitProbWithURWell", 2, 3);
        H1F h1_fitProb2LayersWithURWell = new H1F("fitProb2LayersWithURWell", "fitProb2LayersWithURWell", 100, 0, 1.01);
        h1_fitProb2LayersWithURWell.setTitleX("prob");
        h1_fitProb2LayersWithURWell.setTitleY("Counts");
        h1_fitProb2LayersWithURWell.setLineColor(1);
        histoGroupFitProbWithURWell.addDataSet(h1_fitProb2LayersWithURWell, 0);   
        H1F h1_fitProb3LayersWithURWell = new H1F("fitProb3LayersWithURWell", "fitProb3LayersWithURWell", 100, 0, 1.01);
        h1_fitProb3LayersWithURWell.setTitleX("prob");
        h1_fitProb3LayersWithURWell.setTitleY("Counts");
        h1_fitProb3LayersWithURWell.setLineColor(1);
        histoGroupFitProbWithURWell.addDataSet(h1_fitProb3LayersWithURWell, 1);          
        H1F h1_fitProb4LayersWithURWell = new H1F("fitProb4LayersWithURWell", "fitProb4LayersWithURWell", 100, 0, 1.01);
        h1_fitProb4LayersWithURWell.setTitleX("prob");
        h1_fitProb4LayersWithURWell.setTitleY("Counts");
        h1_fitProb4LayersWithURWell.setLineColor(1);
        histoGroupFitProbWithURWell.addDataSet(h1_fitProb4LayersWithURWell, 2);          
        H1F h1_fitProb5LayersWithURWell = new H1F("fitProb5LayersWithURWell", "fitProb5LayersWithURWell", 100, 0, 1.01);
        h1_fitProb5LayersWithURWell.setTitleX("prob");
        h1_fitProb5LayersWithURWell.setTitleY("Counts");
        h1_fitProb5LayersWithURWell.setLineColor(1);
        histoGroupFitProbWithURWell.addDataSet(h1_fitProb5LayersWithURWell, 3);          
        H1F h1_fitProb6LayersWithURWell = new H1F("fitProb6LayersWithURWell", "fitProb6LayersWithURWell", 100, 0, 1.01);
        h1_fitProb6LayersWithURWell.setTitleX("prob");
        h1_fitProb6LayersWithURWell.setTitleY("Counts");
        h1_fitProb6LayersWithURWell.setLineColor(1);
        histoGroupFitProbWithURWell.addDataSet(h1_fitProb6LayersWithURWell, 4);         
        histoGroupMap.put(histoGroupFitProbWithURWell.getName(), histoGroupFitProbWithURWell);
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
               
        Map<Cluster, URWellCross> map_cluster_uRWellCross = new HashMap();
        List<URWellCross> uRWellCrosses = localEvent.getURWellCrosses();
        for(Cluster cls : validClustersSL1){
            ClusterFitLC fitClus = new ClusterFitLC(cls);
            if(fitClus.lineFit()){
                double slope = fitClus.getLineFitter().slope();
                double intercept = fitClus.getLineFitter().intercept();
                double prob = fitClus.getLineFitter().getProb();                
                double minAbsResidual = 9999;
                for(URWellCross crs : uRWellCrosses){
                    if(crs.sector() == cls.sector()){
                        double x = URWellCross.getXRelativeDCSL1LC();
                        double y = crs.getYRelativeDCSL1LC();
                        double absResidual = Math.abs(slope * x + intercept - y);
                        if(absResidual < minAbsResidual){
                            minAbsResidual = absResidual;
                            if(map_cluster_uRWellCross.keySet().contains(cls)) map_cluster_uRWellCross.replace(cls, crs);
                            else map_cluster_uRWellCross.put(cls, crs);
                        }
                    }
                }
            }
        }
        
        HistoGroup histoGroupFit = histoGroupMap.get("fit");
        for(Cluster cls : map_cluster_uRWellCross.keySet()){
            URWellCross crs = map_cluster_uRWellCross.get(cls);                                              
            double x = URWellCross.getXRelativeDCSL1LC();
            double y = crs.getYRelativeDCSL1LC();     
            
            for(Hit hit : cls.getHitsAtMostLeftLayer()){
                histoGroupFit.getH1F("lyDistToMostLeftDCHit").fill(y - hit.calcLocY());
            }
            
            histoGroupFit.getH1F("lyDistToAvgLy").fill(y - cls.getAvgLy());
            
            histoGroupFit.getH1F("lyDistToMaxLy").fill(y - cls.getMaxLy());
            histoGroupFit.getH1F("lyDistToMinLy").fill(y - cls.getMinLy());
            
            ClusterFitLC fitClus = new ClusterFitLC(cls);
            fitClus.lineFit();
            double slope = fitClus.getLineFitter().slope();
            double intercept = fitClus.getLineFitter().intercept();
            double prob = fitClus.getLineFitter().getProb(); 

            double residual = slope * x + intercept - y;
            histoGroupFit.getH1F("residual").fill(residual);
            histoGroupFit.getH2F("residualVsSlope").fill(slope,residual);            
            
            
            fitClus.addURWell(crs);
            fitClus.lineFit();
            double slopeFittingWithURWell = fitClus.getLineFitter().slope();
            double interceptFittingWithURWell = fitClus.getLineFitter().intercept();
            double probWithURWell = fitClus.getLineFitter().getProb(); 
            double residualFittingWithURWell = slopeFittingWithURWell * x + interceptFittingWithURWell - y;
            histoGroupFit.getH1F("residualFittingWithURWell").fill(residualFittingWithURWell);   
            histoGroupFit.getH2F("residualVsSlopeFittingWithURWell").fill(slopeFittingWithURWell,residualFittingWithURWell);  

            histoGroupFit.getH2F("residualWitoutWithURWell").fill(residual,residualFittingWithURWell); 
            if(Math.abs(residual) > 1 && Math.abs(residualFittingWithURWell) > 0.1) {
                //addDemoGroup(cls, crs);
                addDemoGroup(localEvent, cls.sector(), "", true);
            }
            
            histoGroupFit.getH2F("probComp").fill(prob, probWithURWell);
            histoGroupFit.getH1F("probDiff").fill(probWithURWell-prob);            
            
        }
        
        HistoGroup histoGroupResidual = histoGroupMap.get("residual");
        HistoGroup histoGroupResidualWithURWell = histoGroupMap.get("residualWithURWell");
        HistoGroup histoGroupFitProb = histoGroupMap.get("fitProb");
        HistoGroup histoGroupFitProbWithURWell = histoGroupMap.get("fitProbWithURWell");
        Random random = new Random(); 
        for(Cluster cls : map_cluster_uRWellCross.keySet()){
            URWellCross crs = map_cluster_uRWellCross.get(cls);
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
                    double slope = fit.getLineFitter().slope();
                    double intercept = fit.getLineFitter().intercept();
                    double residual = slope * URWellCross.getXRelativeDCSL1LC() + intercept - crs.getYRelativeDCSL1LC();
                    histoGroupResidual.getH1F("residual2Layers").fill(residual);
                    histoGroupFitProb.getH1F("fitProb2Layers").fill(fit.getLineFitter().getProb());
                    
                    fit.addURWell(crs);
                    if(fit.lineFit()){
                        slope = fit.getLineFitter().slope();
                        intercept = fit.getLineFitter().intercept();
                        residual = slope * URWellCross.getXRelativeDCSL1LC() + intercept - crs.getYRelativeDCSL1LC();
                        histoGroupResidualWithURWell.getH1F("residual2LayersWithURWell").fill(residual);
                        histoGroupFitProbWithURWell.getH1F("fitProb2LayersWithURWell").fill(fit.getLineFitter().getProb());
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
                    double slope = fit.getLineFitter().slope();
                    double intercept = fit.getLineFitter().intercept();
                    double residual = slope * URWellCross.getXRelativeDCSL1LC() + intercept - crs.getYRelativeDCSL1LC();
                    histoGroupResidual.getH1F("residual3Layers").fill(residual);
                    histoGroupFitProb.getH1F("fitProb3Layers").fill(fit.getLineFitter().getProb());
                    
                    fit.addURWell(crs);
                    if(fit.lineFit()){
                        slope = fit.getLineFitter().slope();
                        intercept = fit.getLineFitter().intercept();
                        residual = slope * URWellCross.getXRelativeDCSL1LC() + intercept - crs.getYRelativeDCSL1LC();
                        histoGroupResidualWithURWell.getH1F("residual3LayersWithURWell").fill(residual);
                        histoGroupFitProbWithURWell.getH1F("fitProb3LayersWithURWell").fill(fit.getLineFitter().getProb());
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
                    double slope = fit.getLineFitter().slope();
                    double intercept = fit.getLineFitter().intercept();
                    double residual = slope * URWellCross.getXRelativeDCSL1LC() + intercept - crs.getYRelativeDCSL1LC();
                    histoGroupResidual.getH1F("residual4Layers").fill(residual);
                    histoGroupFitProb.getH1F("fitProb4Layers").fill(fit.getLineFitter().getProb());
                    
                    fit.addURWell(crs);
                    if(fit.lineFit()){
                        slope = fit.getLineFitter().slope();
                        intercept = fit.getLineFitter().intercept();
                        residual = slope * URWellCross.getXRelativeDCSL1LC() + intercept - crs.getYRelativeDCSL1LC();
                        histoGroupResidualWithURWell.getH1F("residual4LayersWithURWell").fill(residual);
                        histoGroupFitProbWithURWell.getH1F("fitProb4LayersWithURWell").fill(fit.getLineFitter().getProb());
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
                    double slope = fit.getLineFitter().slope();
                    double intercept = fit.getLineFitter().intercept();
                    double residual = slope * URWellCross.getXRelativeDCSL1LC() + intercept - crs.getYRelativeDCSL1LC();
                    histoGroupResidual.getH1F("residual5Layers").fill(residual);
                    histoGroupFitProb.getH1F("fitProb5Layers").fill(fit.getLineFitter().getProb());                    
                    
                    fit.addURWell(crs);
                    if(fit.lineFit()){
                        slope = fit.getLineFitter().slope();
                        intercept = fit.getLineFitter().intercept();
                        residual = slope * URWellCross.getXRelativeDCSL1LC() + intercept - crs.getYRelativeDCSL1LC();
                        histoGroupResidualWithURWell.getH1F("residual5LayersWithURWell").fill(residual);
                        histoGroupFitProbWithURWell.getH1F("fitProb5LayersWithURWell").fill(fit.getLineFitter().getProb());
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
                    double slope = fit.getLineFitter().slope();
                    double intercept = fit.getLineFitter().intercept();
                    double residual = slope * URWellCross.getXRelativeDCSL1LC() + intercept - crs.getYRelativeDCSL1LC();
                    histoGroupResidual.getH1F("residual6Layers").fill(residual);
                    histoGroupFitProb.getH1F("fitProb6Layers").fill(fit.getLineFitter().getProb());
                    
                    fit.addURWell(crs);
                    if(fit.lineFit()){
                        slope = fit.getLineFitter().slope();
                        intercept = fit.getLineFitter().intercept();
                        residual = slope * URWellCross.getXRelativeDCSL1LC() + intercept - crs.getYRelativeDCSL1LC();
                        histoGroupResidualWithURWell.getH1F("residual6LayersWithURWell").fill(residual);
                        histoGroupFitProbWithURWell.getH1F("fitProb6LayersWithURWell").fill(fit.getLineFitter().getProb());
                    }
                }
                        
            }
        }                                                        
    }
    
    public void postEventProcess() {
        HistoGroup histoGroupFit = histoGroupMap.get("fit");
        H1F h1_residual = histoGroupFit.getH1F("residual");
        F1D func  = new F1D("f1d","[amp]*gaus(x,[mean],[sigma])",-0.3,0.3);
        func.setParameter(0, 500.);
        func.setParameter(1, 0.);
        func.setParameter(2, 0.2);
        //h1_residual.fit(func);
                      
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
        
        ExploreDCURWellClustering analysis = new ExploreDCURWellClustering();
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
