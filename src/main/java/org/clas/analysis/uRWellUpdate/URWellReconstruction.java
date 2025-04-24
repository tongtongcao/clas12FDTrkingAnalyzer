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
public class URWellReconstruction extends BaseAnalysis{ 
    
    public URWellReconstruction(){}
    
    @Override
    public void createHistoGroupMap(){
        HistoGroup histoGroupHits = new HistoGroup("hits", 5, 3); 
        H1F h1_stripNormalHits = new H1F("stripNormalHits", "strip for normal hits", 100, 0, 2000);
        h1_stripNormalHits.setTitleX("strip");
        h1_stripNormalHits.setTitleY("Counts");
        histoGroupHits.addDataSet(h1_stripNormalHits, 0); 
        H1F h1_energyNormalHits = new H1F("energyNormalHits", "energy for normal hits", 100, 0, 3000);
        h1_energyNormalHits.setTitleX("energy");
        h1_energyNormalHits.setTitleY("Counts");
        histoGroupHits.addDataSet(h1_energyNormalHits, 1); 
        H1F h1_timeNormalHits = new H1F("timeNormalHits", "time for normal hits", 100, 500, 1000);
        h1_timeNormalHits.setTitleX("time");
        h1_timeNormalHits.setTitleY("Counts");
        histoGroupHits.addDataSet(h1_timeNormalHits, 2); 
        H2F h2_energyVsStripNormalHits = new H2F("energyVsStripNormalHits", "energy vs strip for normal hits", 100, 0, 2000, 100, 0, 3000);
        h2_energyVsStripNormalHits.setTitleX("strip");
        h2_energyVsStripNormalHits.setTitleY("energy");
        histoGroupHits.addDataSet(h2_energyVsStripNormalHits, 3);
        H2F h2_timeVsStripNormalHits = new H2F("timeVsStripNormalHits", "time vs strip for normal hits", 100, 0, 2000, 100, 500, 1000);
        h2_timeVsStripNormalHits.setTitleX("strip");
        h2_timeVsStripNormalHits.setTitleY("time");
        histoGroupHits.addDataSet(h2_timeVsStripNormalHits, 4);         
        H1F h1_stripNoiseHits = new H1F("stripNoiseHits", "strip for noise hits", 100, 0, 2000);
        h1_stripNoiseHits.setTitleX("strip");
        h1_stripNoiseHits.setTitleY("Counts");
        histoGroupHits.addDataSet(h1_stripNoiseHits, 5); 
        H1F h1_energyNoiseHits = new H1F("energyNoiseHits", "energy for noise hits", 100, 0, 3000);
        h1_energyNoiseHits.setTitleX("energy");
        h1_energyNoiseHits.setTitleY("Counts");
        histoGroupHits.addDataSet(h1_energyNoiseHits, 6); 
        H1F h1_timeNoiseHits = new H1F("timeNoiseHits", "time for noise hits", 100, 500, 1000);
        h1_timeNoiseHits.setTitleX("time");
        h1_timeNoiseHits.setTitleY("Counts");
        histoGroupHits.addDataSet(h1_timeNoiseHits, 7); 
        H2F h2_energyVsStripNoiseHits = new H2F("energyVsStripNoiseHits", "energy vs strip for noise hits", 100, 0, 2000, 100, 0, 3000);
        h2_energyVsStripNoiseHits.setTitleX("strip");
        h2_energyVsStripNoiseHits.setTitleY("energy");
        histoGroupHits.addDataSet(h2_energyVsStripNoiseHits, 8);
        H2F h2_timeVsStripNoiseHits = new H2F("timeVsStripNoiseHits", "time vs strip for noise hits", 100, 0, 2000, 100, 500, 1000);
        h2_timeVsStripNoiseHits.setTitleX("strip");
        h2_timeVsStripNoiseHits.setTitleY("time");
        histoGroupHits.addDataSet(h2_timeVsStripNoiseHits, 9); 
        H1F h1_stripAllHits = new H1F("stripAllHits", "strip for all hits", 100, 0, 2000);
        h1_stripAllHits.setTitleX("strip");
        h1_stripAllHits.setTitleY("Counts");
        histoGroupHits.addDataSet(h1_stripAllHits, 10); 
        H1F h1_energyAllHits = new H1F("energyAllHits", "energy for all hits", 100, 0, 3000);
        h1_energyAllHits.setTitleX("energy");
        h1_energyAllHits.setTitleY("Counts");
        histoGroupHits.addDataSet(h1_energyAllHits, 11); 
        H1F h1_timeAllHits = new H1F("timeAllHits", "time for all hits", 100, 500, 1000);
        h1_timeAllHits.setTitleX("time");
        h1_timeAllHits.setTitleY("Counts");
        histoGroupHits.addDataSet(h1_timeAllHits, 12); 
        H2F h2_energyVsStripAllHits = new H2F("energyVsStripAllHits", "energy vs strip for all hits", 100, 0, 2000, 100, 0, 3000);
        h2_energyVsStripAllHits.setTitleX("strip");
        h2_energyVsStripAllHits.setTitleY("energy");
        histoGroupHits.addDataSet(h2_energyVsStripAllHits, 13);
        H2F h2_timeVsStripAllHits = new H2F("timeVsStripAllHits", "time vs strip for all hits", 100, 0, 2000, 100, 500, 1000);
        h2_timeVsStripAllHits.setTitleX("strip");
        h2_timeVsStripAllHits.setTitleY("time");
        histoGroupHits.addDataSet(h2_timeVsStripAllHits, 14);         
        histoGroupMap.put(histoGroupHits.getName(), histoGroupHits);        
        
        HistoGroup histoGroupCluster1 = new HistoGroup("cluster1", 5, 3);        
        H1F h1_mainStripNormalCluster1 = new H1F("mainStripNormalCluster1", "main strip for normal cluster1", 100, 0, 2000);
        h1_mainStripNormalCluster1.setTitleX("main strip");
        h1_mainStripNormalCluster1.setTitleY("Counts");
        histoGroupCluster1.addDataSet(h1_mainStripNormalCluster1, 0);         
        H1F h1_sizeNormalCluster1 = new H1F("sizeNormalCluster1", "size for normal cluster1", 20, 0.5, 20.5);
        h1_sizeNormalCluster1.setTitleX("size");
        h1_sizeNormalCluster1.setTitleY("Counts");
        histoGroupCluster1.addDataSet(h1_sizeNormalCluster1, 1);         
        H1F h1_ratioNormalHitsCluster1 = new H1F("ratioNormalHitsCluster1", "ratio of norma hits for normal cluster1", 101, 0, 1.01);
        h1_ratioNormalHitsCluster1.setTitleX("ratio of normal hits");
        h1_ratioNormalHitsCluster1.setTitleY("Counts");
        histoGroupCluster1.addDataSet(h1_ratioNormalHitsCluster1, 2);        
        H1F h1_energyNormalCluster1 = new H1F("energyNormalCluster1", "energy for normal cluster1", 100, 0, 3000);
        h1_energyNormalCluster1.setTitleX("energy");
        h1_energyNormalCluster1.setTitleY("Counts");
        histoGroupCluster1.addDataSet(h1_energyNormalCluster1, 3);        
        H1F h1_timeNormalCluster1 = new H1F("timeNormalCluster1", "time for normal cluster1", 100, 500, 1000);
        h1_timeNormalCluster1.setTitleX("time");
        h1_timeNormalCluster1.setTitleY("Counts");
        histoGroupCluster1.addDataSet(h1_timeNormalCluster1, 4);         
        H1F h1_mainStripNoiseCluster1 = new H1F("mainStripNoiseCluster1", "main strip for noise cluster1", 100, 0, 2000);
        h1_mainStripNoiseCluster1.setTitleX("main strip");
        h1_mainStripNoiseCluster1.setTitleY("Counts");
        histoGroupCluster1.addDataSet(h1_mainStripNoiseCluster1, 5);         
        H1F h1_sizeNoiseCluster1 = new H1F("sizeNoiseCluster1", "size for noise cluster1", 20, 0.5, 20.5);
        h1_sizeNoiseCluster1.setTitleX("size");
        h1_sizeNoiseCluster1.setTitleY("Counts");
        histoGroupCluster1.addDataSet(h1_sizeNoiseCluster1, 6);         
        H1F h1_ratioNoiseHitsCluster1 = new H1F("ratioNoiseHitsCluster1", "ratio of norma hits for noise cluster1", 101, 0, 1.01);
        h1_ratioNoiseHitsCluster1.setTitleX("ratio of normal hits");
        h1_ratioNoiseHitsCluster1.setTitleY("Counts");
        histoGroupCluster1.addDataSet(h1_ratioNoiseHitsCluster1, 7);        
        H1F h1_energyNoiseCluster1 = new H1F("energyNoiseCluster1", "energy for noise cluster1", 100, 0, 3000);
        h1_energyNoiseCluster1.setTitleX("energy");
        h1_energyNoiseCluster1.setTitleY("Counts");
        histoGroupCluster1.addDataSet(h1_energyNoiseCluster1, 8);        
        H1F h1_timeNoiseCluster1 = new H1F("timeNoiseCluster1", "time for noise cluster1", 100, 500, 1000);
        h1_timeNoiseCluster1.setTitleX("time");
        h1_timeNoiseCluster1.setTitleY("Counts");
        histoGroupCluster1.addDataSet(h1_timeNoiseCluster1, 9);         
        H1F h1_mainStripAllCluster1 = new H1F("mainStripAllCluster1", "main strip for all cluster1", 100, 0, 2000);
        h1_mainStripAllCluster1.setTitleX("main strip");
        h1_mainStripAllCluster1.setTitleY("Counts");
        histoGroupCluster1.addDataSet(h1_mainStripAllCluster1, 10);         
        H1F h1_sizeAllCluster1 = new H1F("sizeAllCluster1", "size for all cluster1", 20, 0.5, 20.5);
        h1_sizeAllCluster1.setTitleX("size");
        h1_sizeAllCluster1.setTitleY("Counts");
        histoGroupCluster1.addDataSet(h1_sizeAllCluster1, 11);         
        H1F h1_ratioAllHitsCluster1 = new H1F("ratioAllHitsCluster1", "ratio of norma hits for all cluster1", 101, 0, 1.01);
        h1_ratioAllHitsCluster1.setTitleX("ratio of normal hits");
        h1_ratioAllHitsCluster1.setTitleY("Counts");
        histoGroupCluster1.addDataSet(h1_ratioAllHitsCluster1, 12);        
        H1F h1_energyAllCluster1 = new H1F("energyAllCluster1", "energy for all cluster1", 100, 0, 3000);
        h1_energyAllCluster1.setTitleX("energy");
        h1_energyAllCluster1.setTitleY("Counts");
        histoGroupCluster1.addDataSet(h1_energyAllCluster1, 13);        
        H1F h1_timeAllCluster1 = new H1F("timeAllCluster1", "time for all cluster1", 100, 500, 1000);
        h1_timeAllCluster1.setTitleX("time");
        h1_timeAllCluster1.setTitleY("Counts");
        histoGroupCluster1.addDataSet(h1_timeAllCluster1, 14);                      
        histoGroupMap.put(histoGroupCluster1.getName(), histoGroupCluster1);
        
        HistoGroup histoGroupCluster2 = new HistoGroup("cluster2", 5, 3);        
        H1F h1_mainStripNormalCluster2 = new H1F("mainStripNormalCluster2", "main strip for normal cluster2", 100, 0, 2000);
        h1_mainStripNormalCluster2.setTitleX("main strip");
        h1_mainStripNormalCluster2.setTitleY("Counts");
        histoGroupCluster2.addDataSet(h1_mainStripNormalCluster2, 0);         
        H1F h1_sizeNormalCluster2 = new H1F("sizeNormalCluster2", "size for normal cluster2", 20, 0.5, 20.5);
        h1_sizeNormalCluster2.setTitleX("size");
        h1_sizeNormalCluster2.setTitleY("Counts");
        histoGroupCluster2.addDataSet(h1_sizeNormalCluster2, 1);         
        H1F h1_ratioNormalHitsCluster2 = new H1F("ratioNormalHitsCluster2", "ratio of norma hits for normal cluster2", 101, 0, 1.01);
        h1_ratioNormalHitsCluster2.setTitleX("ratio of normal hits");
        h1_ratioNormalHitsCluster2.setTitleY("Counts");
        histoGroupCluster2.addDataSet(h1_ratioNormalHitsCluster2, 2);        
        H1F h1_energyNormalCluster2 = new H1F("energyNormalCluster2", "energy for normal cluster2", 100, 0, 3000);
        h1_energyNormalCluster2.setTitleX("energy");
        h1_energyNormalCluster2.setTitleY("Counts");
        histoGroupCluster2.addDataSet(h1_energyNormalCluster2, 3);        
        H1F h1_timeNormalCluster2 = new H1F("timeNormalCluster2", "time for normal cluster2", 100, 500, 1000);
        h1_timeNormalCluster2.setTitleX("time");
        h1_timeNormalCluster2.setTitleY("Counts");
        histoGroupCluster2.addDataSet(h1_timeNormalCluster2, 4);         
        H1F h1_mainStripNoiseCluster2 = new H1F("mainStripNoiseCluster2", "main strip for noise cluster2", 100, 0, 2000);
        h1_mainStripNoiseCluster2.setTitleX("main strip");
        h1_mainStripNoiseCluster2.setTitleY("Counts");
        histoGroupCluster2.addDataSet(h1_mainStripNoiseCluster2, 5);         
        H1F h1_sizeNoiseCluster2 = new H1F("sizeNoiseCluster2", "size for noise cluster2", 20, 0.5, 20.5);
        h1_sizeNoiseCluster2.setTitleX("size");
        h1_sizeNoiseCluster2.setTitleY("Counts");
        histoGroupCluster2.addDataSet(h1_sizeNoiseCluster2, 6);         
        H1F h1_ratioNoiseHitsCluster2 = new H1F("ratioNoiseHitsCluster2", "ratio of norma hits for noise cluster2", 101, 0, 1.01);
        h1_ratioNoiseHitsCluster2.setTitleX("ratio of normal hits");
        h1_ratioNoiseHitsCluster2.setTitleY("Counts");
        histoGroupCluster2.addDataSet(h1_ratioNoiseHitsCluster2, 7);        
        H1F h1_energyNoiseCluster2 = new H1F("energyNoiseCluster2", "energy for noise cluster2", 100, 0, 3000);
        h1_energyNoiseCluster2.setTitleX("energy");
        h1_energyNoiseCluster2.setTitleY("Counts");
        histoGroupCluster2.addDataSet(h1_energyNoiseCluster2, 8);        
        H1F h1_timeNoiseCluster2 = new H1F("timeNoiseCluster2", "time for noise cluster2", 100, 500, 1000);
        h1_timeNoiseCluster2.setTitleX("time");
        h1_timeNoiseCluster2.setTitleY("Counts");
        histoGroupCluster2.addDataSet(h1_timeNoiseCluster2, 9);         
        H1F h1_mainStripAllCluster2 = new H1F("mainStripAllCluster2", "main strip for all cluster2", 100, 0, 2000);
        h1_mainStripAllCluster2.setTitleX("main strip");
        h1_mainStripAllCluster2.setTitleY("Counts");
        histoGroupCluster2.addDataSet(h1_mainStripAllCluster2, 10);         
        H1F h1_sizeAllCluster2 = new H1F("sizeAllCluster2", "size for all cluster2", 20, 0.5, 20.5);
        h1_sizeAllCluster2.setTitleX("size");
        h1_sizeAllCluster2.setTitleY("Counts");
        histoGroupCluster2.addDataSet(h1_sizeAllCluster2, 11);         
        H1F h1_ratioAllHitsCluster2 = new H1F("ratioAllHitsCluster2", "ratio of norma hits for all cluster2", 101, 0, 1.01);
        h1_ratioAllHitsCluster2.setTitleX("ratio of normal hits");
        h1_ratioAllHitsCluster2.setTitleY("Counts");
        histoGroupCluster2.addDataSet(h1_ratioAllHitsCluster2, 12);        
        H1F h1_energyAllCluster2 = new H1F("energyAllCluster2", "energy for all cluster2", 100, 0, 3000);
        h1_energyAllCluster2.setTitleX("energy");
        h1_energyAllCluster2.setTitleY("Counts");
        histoGroupCluster2.addDataSet(h1_energyAllCluster2, 13);        
        H1F h1_timeAllCluster2 = new H1F("timeAllCluster2", "time for all cluster2", 100, 500, 1000);
        h1_timeAllCluster2.setTitleX("time");
        h1_timeAllCluster2.setTitleY("Counts");
        histoGroupCluster2.addDataSet(h1_timeAllCluster2, 14);                      
        histoGroupMap.put(histoGroupCluster2.getName(), histoGroupCluster2);
        
        HistoGroup histoGroupCrossStatusNoCuts = new HistoGroup("crossStatusNoCuts", 5, 3);         
        H1F h1_crossStatusNoCuts = new H1F("crossStatusNoCuts", "uRWell cross status", 4, -0.5, 3.5);
        h1_crossStatusNoCuts.setTitleX("cross status");
        h1_crossStatusNoCuts.setTitleY("Counts");
        histoGroupCrossStatusNoCuts.addDataSet(h1_crossStatusNoCuts, 0); 
        histoGroupMap.put(histoGroupCrossStatusNoCuts.getName(), histoGroupCrossStatusNoCuts);
        
        HistoGroup histoGroupCrossesNoCuts = new HistoGroup("crossesNoCuts", 7, 5); 
        H2F h2_xyCrossesBothNormalNoCuts = new H2F("xyCrossesBothNormalNoCuts", "x vs y", 100, -200, 200, 100, -200, 200);
        h2_xyCrossesBothNormalNoCuts.setTitleX("x (cm)");
        h2_xyCrossesBothNormalNoCuts.setTitleY("y (cm)");
        histoGroupCrossesNoCuts.addDataSet(h2_xyCrossesBothNormalNoCuts, 0); 
        H2F h2_sizeCompBothNormalNoCuts = new H2F("sizeCompBothNormalNoCuts", "size comparison", 20, 0.5, 20.5, 20, 0.5, 20.5);
        h2_sizeCompBothNormalNoCuts.setTitleX("size of cluster1");
        h2_sizeCompBothNormalNoCuts.setTitleY("size of cluster2");
        histoGroupCrossesNoCuts.addDataSet(h2_sizeCompBothNormalNoCuts, 1);        
        H2F h2_energyCompCrossesBothNormalNoCuts = new H2F("energyCompCrossesBothNormalNoCuts", "energy comparison", 100, 0, 3000, 100, 0, 3000);
        h2_energyCompCrossesBothNormalNoCuts.setTitleX("energy of cluster1");
        h2_energyCompCrossesBothNormalNoCuts.setTitleY("energy of cluster2");
        histoGroupCrossesNoCuts.addDataSet(h2_energyCompCrossesBothNormalNoCuts, 2);
        H1F h1_energyDiffCrossesBothNormalNoCuts = new H1F("energyDiffCrossesBothNormalNoCuts", "energy difference", 100, -1000, 1000);
        h1_energyDiffCrossesBothNormalNoCuts.setTitleX("energy difference");
        h1_energyDiffCrossesBothNormalNoCuts.setTitleY("counts");
        histoGroupCrossesNoCuts.addDataSet(h1_energyDiffCrossesBothNormalNoCuts, 3);
        H2F h2_timeCompCrossesBothNormalNoCuts = new H2F("timeCompCrossesBothNormalNoCuts", "time comparison", 100, 500, 1000, 100, 500, 1000);
        h2_timeCompCrossesBothNormalNoCuts.setTitleX("time of cluster1");
        h2_timeCompCrossesBothNormalNoCuts.setTitleY("time of cluster2");
        histoGroupCrossesNoCuts.addDataSet(h2_timeCompCrossesBothNormalNoCuts, 4);                
        H1F h1_timeDiffCrossesBothNormalNoCuts = new H1F("timeDiffCrossesBothNormalNoCuts", "time difference", 100, -200, 200);
        h1_timeDiffCrossesBothNormalNoCuts.setTitleX("time difference");
        h1_timeDiffCrossesBothNormalNoCuts.setTitleY("counts");
        histoGroupCrossesNoCuts.addDataSet(h1_timeDiffCrossesBothNormalNoCuts, 5);        
        H1F h1_timeCrossesBothNormalNoCuts = new H1F("timeCrossesBothNormalNoCuts", "time", 100, 500, 1000);
        h1_timeCrossesBothNormalNoCuts.setTitleX("time");
        h1_timeCrossesBothNormalNoCuts.setTitleY("counts");
        histoGroupCrossesNoCuts.addDataSet(h1_timeCrossesBothNormalNoCuts, 6);
        H2F h2_xyCrossesOnlyCluster1NormalNoCuts = new H2F("xyCrossesOnlyCluster1NormalNoCuts", "x vs y", 100, -200, 200, 100, -200, 200);
        h2_xyCrossesOnlyCluster1NormalNoCuts.setTitleX("x (cm)");
        h2_xyCrossesOnlyCluster1NormalNoCuts.setTitleY("y (cm)");
        histoGroupCrossesNoCuts.addDataSet(h2_xyCrossesOnlyCluster1NormalNoCuts, 7); 
        H2F h2_sizeCompOnlyCluster1NormalNoCuts = new H2F("sizeCompOnlyCluster1NormalNoCuts", "size comparison", 20, 0.5, 20.5, 20, 0.5, 20.5);
        h2_sizeCompOnlyCluster1NormalNoCuts.setTitleX("size of cluster1");
        h2_sizeCompOnlyCluster1NormalNoCuts.setTitleY("size of cluster2");
        histoGroupCrossesNoCuts.addDataSet(h2_sizeCompOnlyCluster1NormalNoCuts, 8);        
        H2F h2_energyCompCrossesOnlyCluster1NormalNoCuts = new H2F("energyCompCrossesOnlyCluster1NormalNoCuts", "energy comparison", 100, 0, 3000, 100, 0, 3000);
        h2_energyCompCrossesOnlyCluster1NormalNoCuts.setTitleX("energy of cluster1");
        h2_energyCompCrossesOnlyCluster1NormalNoCuts.setTitleY("energy of cluster2");
        histoGroupCrossesNoCuts.addDataSet(h2_energyCompCrossesOnlyCluster1NormalNoCuts, 9);
        H1F h1_energyDiffCrossesOnlyCluster1NormalNoCuts = new H1F("energyDiffCrossesOnlyCluster1NormalNoCuts", "energy difference", 100, -1000, 1000);
        h1_energyDiffCrossesOnlyCluster1NormalNoCuts.setTitleX("energy difference");
        h1_energyDiffCrossesOnlyCluster1NormalNoCuts.setTitleY("counts");
        histoGroupCrossesNoCuts.addDataSet(h1_energyDiffCrossesOnlyCluster1NormalNoCuts, 10);
        H2F h2_timeCompCrossesOnlyCluster1NormalNoCuts = new H2F("timeCompCrossesOnlyCluster1NormalNoCuts", "time comparison", 100, 500, 1000, 100, 500, 1000);
        h2_timeCompCrossesOnlyCluster1NormalNoCuts.setTitleX("time of cluster1");
        h2_timeCompCrossesOnlyCluster1NormalNoCuts.setTitleY("time of cluster2");
        histoGroupCrossesNoCuts.addDataSet(h2_timeCompCrossesOnlyCluster1NormalNoCuts, 11);                
        H1F h1_timeDiffCrossesOnlyCluster1NormalNoCuts = new H1F("timeDiffCrossesOnlyCluster1NormalNoCuts", "time difference", 100, -200, 200);
        h1_timeDiffCrossesOnlyCluster1NormalNoCuts.setTitleX("time difference");
        h1_timeDiffCrossesOnlyCluster1NormalNoCuts.setTitleY("counts");
        histoGroupCrossesNoCuts.addDataSet(h1_timeDiffCrossesOnlyCluster1NormalNoCuts, 12);        
        H1F h1_timeCrossesOnlyCluster1NormalNoCuts = new H1F("timeCrossesOnlyCluster1NormalNoCuts", "time", 100, 500, 1000);
        h1_timeCrossesOnlyCluster1NormalNoCuts.setTitleX("time");
        h1_timeCrossesOnlyCluster1NormalNoCuts.setTitleY("counts");
        histoGroupCrossesNoCuts.addDataSet(h1_timeCrossesOnlyCluster1NormalNoCuts, 13);        
        H2F h2_xyCrossesOnlyCluster2NormalNoCuts = new H2F("xyCrossesOnlyCluster2NormalNoCuts", "x vs y", 100, -200, 200, 100, -200, 200);
        h2_xyCrossesOnlyCluster2NormalNoCuts.setTitleX("x (cm)");
        h2_xyCrossesOnlyCluster2NormalNoCuts.setTitleY("y (cm)");
        histoGroupCrossesNoCuts.addDataSet(h2_xyCrossesOnlyCluster2NormalNoCuts, 14); 
        H2F h2_sizeCompOnlyCluster2NormalNoCuts = new H2F("sizeCompOnlyCluster2NormalNoCuts", "size comparison", 20, 0.5, 20.5, 20, 0.5, 20.5);
        h2_sizeCompOnlyCluster2NormalNoCuts.setTitleX("size of cluster1");
        h2_sizeCompOnlyCluster2NormalNoCuts.setTitleY("size of cluster2");
        histoGroupCrossesNoCuts.addDataSet(h2_sizeCompOnlyCluster2NormalNoCuts, 15);        
        H2F h2_energyCompCrossesOnlyCluster2NormalNoCuts = new H2F("energyCompCrossesOnlyCluster2NormalNoCuts", "energy comparison", 100, 0, 3000, 100, 0, 3000);
        h2_energyCompCrossesOnlyCluster2NormalNoCuts.setTitleX("energy of cluster1");
        h2_energyCompCrossesOnlyCluster2NormalNoCuts.setTitleY("energy of cluster2");
        histoGroupCrossesNoCuts.addDataSet(h2_energyCompCrossesOnlyCluster2NormalNoCuts, 16);
        H1F h1_energyDiffCrossesOnlyCluster2NormalNoCuts = new H1F("energyDiffCrossesOnlyCluster2NormalNoCuts", "energy difference", 100, -1000, 1000);
        h1_energyDiffCrossesOnlyCluster2NormalNoCuts.setTitleX("energy difference");
        h1_energyDiffCrossesOnlyCluster2NormalNoCuts.setTitleY("counts");
        histoGroupCrossesNoCuts.addDataSet(h1_energyDiffCrossesOnlyCluster2NormalNoCuts, 17);
        H2F h2_timeCompCrossesOnlyCluster2NormalNoCuts = new H2F("timeCompCrossesOnlyCluster2NormalNoCuts", "time comparison", 100, 500, 1000, 100, 500, 1000);
        h2_timeCompCrossesOnlyCluster2NormalNoCuts.setTitleX("time of cluster1");
        h2_timeCompCrossesOnlyCluster2NormalNoCuts.setTitleY("time of cluster2");
        histoGroupCrossesNoCuts.addDataSet(h2_timeCompCrossesOnlyCluster2NormalNoCuts, 18);                
        H1F h1_timeDiffCrossesOnlyCluster2NormalNoCuts = new H1F("timeDiffCrossesOnlyCluster2NormalNoCuts", "time difference", 100, -200, 200);
        h1_timeDiffCrossesOnlyCluster2NormalNoCuts.setTitleX("time difference");
        h1_timeDiffCrossesOnlyCluster2NormalNoCuts.setTitleY("counts");
        histoGroupCrossesNoCuts.addDataSet(h1_timeDiffCrossesOnlyCluster2NormalNoCuts, 19);        
        H1F h1_timeCrossesOnlyCluster2NormalNoCuts = new H1F("timeCrossesOnlyCluster2NormalNoCuts", "time", 100, 500, 1000);
        h1_timeCrossesOnlyCluster2NormalNoCuts.setTitleX("time");
        h1_timeCrossesOnlyCluster2NormalNoCuts.setTitleY("counts");
        histoGroupCrossesNoCuts.addDataSet(h1_timeCrossesOnlyCluster2NormalNoCuts, 20);
        H2F h2_xyCrossesBothNoiseNoCuts = new H2F("xyCrossesBothNoiseNoCuts", "x vs y", 100, -200, 200, 100, -200, 200);
        h2_xyCrossesBothNoiseNoCuts.setTitleX("x (cm)");
        h2_xyCrossesBothNoiseNoCuts.setTitleY("y (cm)");
        histoGroupCrossesNoCuts.addDataSet(h2_xyCrossesBothNoiseNoCuts, 21); 
        H2F h2_sizeCompBothNoiseNoCuts = new H2F("sizeCompBothNoiseNoCuts", "size comparison", 20, 0.5, 20.5, 20, 0.5, 20.5);
        h2_sizeCompBothNoiseNoCuts.setTitleX("size of cluster1");
        h2_sizeCompBothNoiseNoCuts.setTitleY("size of cluster2");
        histoGroupCrossesNoCuts.addDataSet(h2_sizeCompBothNoiseNoCuts, 22);        
        H2F h2_energyCompCrossesBothNoiseNoCuts = new H2F("energyCompCrossesBothNoiseNoCuts", "energy comparison", 100, 0, 3000, 100, 0, 3000);
        h2_energyCompCrossesBothNoiseNoCuts.setTitleX("energy of cluster1");
        h2_energyCompCrossesBothNoiseNoCuts.setTitleY("energy of cluster2");
        histoGroupCrossesNoCuts.addDataSet(h2_energyCompCrossesBothNoiseNoCuts, 23);
        H1F h1_energyDiffCrossesBothNoiseNoCuts = new H1F("energyDiffCrossesBothNoiseNoCuts", "energy difference", 100, -1000, 1000);
        h1_energyDiffCrossesBothNoiseNoCuts.setTitleX("energy difference");
        h1_energyDiffCrossesBothNoiseNoCuts.setTitleY("counts");
        histoGroupCrossesNoCuts.addDataSet(h1_energyDiffCrossesBothNoiseNoCuts, 24);
        H2F h2_timeCompCrossesBothNoiseNoCuts = new H2F("timeCompCrossesBothNoiseNoCuts", "time comparison", 100, 500, 1000, 100, 500, 1000);
        h2_timeCompCrossesBothNoiseNoCuts.setTitleX("time of cluster1");
        h2_timeCompCrossesBothNoiseNoCuts.setTitleY("time of cluster2");
        histoGroupCrossesNoCuts.addDataSet(h2_timeCompCrossesBothNoiseNoCuts, 25);                
        H1F h1_timeDiffCrossesBothNoiseNoCuts = new H1F("timeDiffCrossesBothNoiseNoCuts", "time difference", 100, -200, 200);
        h1_timeDiffCrossesBothNoiseNoCuts.setTitleX("time difference");
        h1_timeDiffCrossesBothNoiseNoCuts.setTitleY("counts");
        histoGroupCrossesNoCuts.addDataSet(h1_timeDiffCrossesBothNoiseNoCuts, 26);        
        H1F h1_timeCrossesBothNoiseNoCuts = new H1F("timeCrossesBothNoiseNoCuts", "time", 100, 500, 1000);
        h1_timeCrossesBothNoiseNoCuts.setTitleX("time");
        h1_timeCrossesBothNoiseNoCuts.setTitleY("counts");
        histoGroupCrossesNoCuts.addDataSet(h1_timeCrossesBothNoiseNoCuts, 27);                  
        H2F h2_xyCrossesAllNoCuts = new H2F("xyCrossesAllNoCuts", "x vs y", 100, -200, 200, 100, -200, 200);
        h2_xyCrossesAllNoCuts.setTitleX("x (cm)");
        h2_xyCrossesAllNoCuts.setTitleY("y (cm)");
        histoGroupCrossesNoCuts.addDataSet(h2_xyCrossesAllNoCuts, 28); 
        H2F h2_sizeCompAllNoCuts = new H2F("sizeCompAllNoCuts", "size comparison", 20, 0.5, 20.5, 20, 0.5, 20.5);
        h2_sizeCompAllNoCuts.setTitleX("size of cluster1");
        h2_sizeCompAllNoCuts.setTitleY("size of cluster2");
        histoGroupCrossesNoCuts.addDataSet(h2_sizeCompAllNoCuts, 29);        
        H2F h2_energyCompCrossesAllNoCuts = new H2F("energyCompCrossesAllNoCuts", "energy comparison", 100, 0, 3000, 100, 0, 3000);
        h2_energyCompCrossesAllNoCuts.setTitleX("energy of cluster1");
        h2_energyCompCrossesAllNoCuts.setTitleY("energy of cluster2");
        histoGroupCrossesNoCuts.addDataSet(h2_energyCompCrossesAllNoCuts, 30);
        H1F h1_energyDiffCrossesAllNoCuts = new H1F("energyDiffCrossesAllNoCuts", "energy difference", 100, -1000, 1000);
        h1_energyDiffCrossesAllNoCuts.setTitleX("energy difference");
        h1_energyDiffCrossesAllNoCuts.setTitleY("counts");
        histoGroupCrossesNoCuts.addDataSet(h1_energyDiffCrossesAllNoCuts, 31);
        H2F h2_timeCompCrossesAllNoCuts = new H2F("timeCompCrossesAllNoCuts", "time comparison", 100, 500, 1000, 100, 500, 1000);
        h2_timeCompCrossesAllNoCuts.setTitleX("time of cluster1");
        h2_timeCompCrossesAllNoCuts.setTitleY("time of cluster2");
        histoGroupCrossesNoCuts.addDataSet(h2_timeCompCrossesAllNoCuts, 32);                
        H1F h1_timeDiffCrossesAllNoCuts = new H1F("timeDiffCrossesAllNoCuts", "time difference", 100, -200, 200);
        h1_timeDiffCrossesAllNoCuts.setTitleX("time difference");
        h1_timeDiffCrossesAllNoCuts.setTitleY("counts");
        histoGroupCrossesNoCuts.addDataSet(h1_timeDiffCrossesAllNoCuts, 33);        
        H1F h1_timeCrossesAllNoCuts = new H1F("timeCrossesAllNoCuts", "time", 100, 500, 1000);
        h1_timeCrossesAllNoCuts.setTitleX("time");
        h1_timeCrossesAllNoCuts.setTitleY("counts");
        histoGroupCrossesNoCuts.addDataSet(h1_timeCrossesAllNoCuts, 34); 
        histoGroupMap.put(histoGroupCrossesNoCuts.getName(), histoGroupCrossesNoCuts);
        
        
        HistoGroup histoGroupCrossStatus = new HistoGroup("crossStatus", 5, 3);         
        H1F h1_crossStatus = new H1F("crossStatus", " uRWell cross status", 4, -0.5, 3.5);
        h1_crossStatus.setTitleX("cross status");
        h1_crossStatus.setTitleY("Counts");
        histoGroupCrossStatus.addDataSet(h1_crossStatus, 0); 
        histoGroupMap.put(histoGroupCrossStatus.getName(), histoGroupCrossStatus);
        

    }
             
    public void processEvent(Event event){        
        //Read banks
        LocalEvent localEvent = new LocalEvent(reader, event, Constants.CONVTB, true);
        
        List<URWellHit> hits = localEvent.getURWellHits();        
        
        List<URWellCluster> clusters = localEvent.getURWellClusters();
        List<URWellCluster> clusters1 = new ArrayList();
        List<URWellCluster> clusters2 = new ArrayList();
        for(URWellCluster cls : clusters){
            cls.separateNormalBgHits();
            if(cls.layer() == 1) clusters1.add(cls);
            else if(cls.layer() == 2) clusters2.add(cls);
        }
        
        List<URWellCross> crossesNoCuts = localEvent.getURWellCrossesNoCuts();

        List<URWellCross> crosses = localEvent.getURWellCrosses();
        
        
        HistoGroup histoGroupHits = histoGroupMap.get("hits");
        for(URWellHit hit : hits){
            if(hit.isNormalHit()){
                histoGroupHits.getH1F("stripNormalHits").fill(hit.strip());
                histoGroupHits.getH1F("energyNormalHits").fill(hit.energy());
                histoGroupHits.getH1F("timeNormalHits").fill(hit.time());
                histoGroupHits.getH2F("energyVsStripNormalHits").fill(hit.strip(), hit.energy());
                histoGroupHits.getH2F("timeVsStripNormalHits").fill(hit.strip(), hit.time());
            }
            else{
                histoGroupHits.getH1F("stripNoiseHits").fill(hit.strip());
                histoGroupHits.getH1F("energyNoiseHits").fill(hit.energy());
                histoGroupHits.getH1F("timeNoiseHits").fill(hit.time());
                histoGroupHits.getH2F("energyVsStripNoiseHits").fill(hit.strip(), hit.energy());
                histoGroupHits.getH2F("timeVsStripNoiseHits").fill(hit.strip(), hit.time());                
            }
            
            histoGroupHits.getH1F("stripAllHits").fill(hit.strip());
            histoGroupHits.getH1F("energyAllHits").fill(hit.energy());
            histoGroupHits.getH1F("timeAllHits").fill(hit.time());
            histoGroupHits.getH2F("energyVsStripAllHits").fill(hit.strip(), hit.energy());
            histoGroupHits.getH2F("timeVsStripAllHits").fill(hit.strip(), hit.time());            
        }
        
        
        HistoGroup histoGroupCluster1 = histoGroupMap.get("cluster1");
        for(URWellCluster cls1 : clusters1){
            if(cls1.isMainStripNormal()) {
                histoGroupCluster1.getH1F("mainStripNormalCluster1").fill(cls1.strip());
                histoGroupCluster1.getH1F("sizeNormalCluster1").fill(cls1.size());
                histoGroupCluster1.getH1F("ratioNormalHitsCluster1").fill(cls1.getRatioNormalHits());
                histoGroupCluster1.getH1F("energyNormalCluster1").fill(cls1.energy());
                histoGroupCluster1.getH1F("timeNormalCluster1").fill(cls1.time());
            }
            else{
                histoGroupCluster1.getH1F("mainStripNoiseCluster1").fill(cls1.strip());
                histoGroupCluster1.getH1F("sizeNoiseCluster1").fill(cls1.size());
                histoGroupCluster1.getH1F("ratioNoiseHitsCluster1").fill(cls1.getRatioNormalHits());
                histoGroupCluster1.getH1F("energyNoiseCluster1").fill(cls1.energy());
                histoGroupCluster1.getH1F("timeNoiseCluster1").fill(cls1.time());
            }
            
            histoGroupCluster1.getH1F("mainStripAllCluster1").fill(cls1.strip());
            histoGroupCluster1.getH1F("sizeAllCluster1").fill(cls1.size());
            histoGroupCluster1.getH1F("ratioAllHitsCluster1").fill(cls1.getRatioNormalHits());
            histoGroupCluster1.getH1F("energyAllCluster1").fill(cls1.energy());
            histoGroupCluster1.getH1F("timeAllCluster1").fill(cls1.time());
        }
        
        HistoGroup histoGroupCluster2 = histoGroupMap.get("cluster2");
        for(URWellCluster cls2 : clusters2){
            if(cls2.isMainStripNormal()) {
                histoGroupCluster2.getH1F("mainStripNormalCluster2").fill(cls2.strip());
                histoGroupCluster2.getH1F("sizeNormalCluster2").fill(cls2.size());
                histoGroupCluster2.getH1F("ratioNormalHitsCluster2").fill(cls2.getRatioNormalHits());
                histoGroupCluster2.getH1F("energyNormalCluster2").fill(cls2.energy());
                histoGroupCluster2.getH1F("timeNormalCluster2").fill(cls2.time());
            }
            else{
                histoGroupCluster2.getH1F("mainStripNoiseCluster2").fill(cls2.strip());
                histoGroupCluster2.getH1F("sizeNoiseCluster2").fill(cls2.size());
                histoGroupCluster2.getH1F("ratioNoiseHitsCluster2").fill(cls2.getRatioNormalHits());
                histoGroupCluster2.getH1F("energyNoiseCluster2").fill(cls2.energy());
                histoGroupCluster2.getH1F("timeNoiseCluster2").fill(cls2.time());
            }
            
            histoGroupCluster2.getH1F("mainStripAllCluster2").fill(cls2.strip());
            histoGroupCluster2.getH1F("sizeAllCluster2").fill(cls2.size());
            histoGroupCluster2.getH1F("ratioAllHitsCluster2").fill(cls2.getRatioNormalHits());
            histoGroupCluster2.getH1F("energyAllCluster2").fill(cls2.energy());
            histoGroupCluster2.getH1F("timeAllCluster2").fill(cls2.time());
        }
        
        HistoGroup histoGroupCrossStatusNoCuts = histoGroupMap.get("crossStatusNoCuts");
        HistoGroup histoGroupCrossesNoCuts = histoGroupMap.get("crossesNoCuts");
        for(URWellCross crs : crossesNoCuts){
            if(crs.isBothClustersNoise()) {
                histoGroupCrossStatusNoCuts.getH1F("crossStatusNoCuts").fill(0);
                
                histoGroupCrossesNoCuts.getH2F("xyCrossesBothNoiseNoCuts").fill(crs.pointLocal().x(), crs.pointLocal().y());
                histoGroupCrossesNoCuts.getH2F("sizeCompBothNoiseNoCuts").fill(crs.getCluster1().size(), crs.getCluster2().size());                                
                histoGroupCrossesNoCuts.getH2F("energyCompCrossesBothNoiseNoCuts").fill(crs.getCluster1().energy(), crs.getCluster2().energy());                
                histoGroupCrossesNoCuts.getH1F("energyDiffCrossesBothNoiseNoCuts").fill(crs.getCluster1().energy() - crs.getCluster2().energy());                
                histoGroupCrossesNoCuts.getH2F("timeCompCrossesBothNoiseNoCuts").fill(crs.getCluster1().time(), crs.getCluster2().time());                
                histoGroupCrossesNoCuts.getH1F("timeDiffCrossesBothNoiseNoCuts").fill(crs.getCluster1().time() - crs.getCluster2().time());                
                histoGroupCrossesNoCuts.getH1F("timeCrossesBothNoiseNoCuts").fill(crs.time());                                
            }
            else if(crs.isCluster1Normal() && !crs.isCluster2Normal()) {
                histoGroupCrossStatusNoCuts.getH1F("crossStatusNoCuts").fill(1);
                
                histoGroupCrossesNoCuts.getH2F("xyCrossesOnlyCluster1NormalNoCuts").fill(crs.pointLocal().x(), crs.pointLocal().y());
                histoGroupCrossesNoCuts.getH2F("sizeCompOnlyCluster1NormalNoCuts").fill(crs.getCluster1().size(), crs.getCluster2().size());                                
                histoGroupCrossesNoCuts.getH2F("energyCompCrossesOnlyCluster1NormalNoCuts").fill(crs.getCluster1().energy(), crs.getCluster2().energy());                
                histoGroupCrossesNoCuts.getH1F("energyDiffCrossesOnlyCluster1NormalNoCuts").fill(crs.getCluster1().energy() - crs.getCluster2().energy());                
                histoGroupCrossesNoCuts.getH2F("timeCompCrossesOnlyCluster1NormalNoCuts").fill(crs.getCluster1().time(), crs.getCluster2().time());                
                histoGroupCrossesNoCuts.getH1F("timeDiffCrossesOnlyCluster1NormalNoCuts").fill(crs.getCluster1().time() - crs.getCluster2().time());                
                histoGroupCrossesNoCuts.getH1F("timeCrossesOnlyCluster1NormalNoCuts").fill(crs.time());
            }
            else if(crs.isCluster2Normal() && !crs.isCluster1Normal()) {
                histoGroupCrossStatusNoCuts.getH1F("crossStatusNoCuts").fill(2);
                
                histoGroupCrossesNoCuts.getH2F("xyCrossesOnlyCluster2NormalNoCuts").fill(crs.pointLocal().x(), crs.pointLocal().y());
                histoGroupCrossesNoCuts.getH2F("sizeCompOnlyCluster2NormalNoCuts").fill(crs.getCluster1().size(), crs.getCluster2().size());                                
                histoGroupCrossesNoCuts.getH2F("energyCompCrossesOnlyCluster2NormalNoCuts").fill(crs.getCluster1().energy(), crs.getCluster2().energy());                
                histoGroupCrossesNoCuts.getH1F("energyDiffCrossesOnlyCluster2NormalNoCuts").fill(crs.getCluster1().energy() - crs.getCluster2().energy());                
                histoGroupCrossesNoCuts.getH2F("timeCompCrossesOnlyCluster2NormalNoCuts").fill(crs.getCluster1().time(), crs.getCluster2().time());                
                histoGroupCrossesNoCuts.getH1F("timeDiffCrossesOnlyCluster2NormalNoCuts").fill(crs.getCluster1().time() - crs.getCluster2().time());                
                histoGroupCrossesNoCuts.getH1F("timeCrossesOnlyCluster2NormalNoCuts").fill(crs.time());                
            }
            else {
                histoGroupCrossStatusNoCuts.getH1F("crossStatusNoCuts").fill(3);
                
                histoGroupCrossesNoCuts.getH2F("xyCrossesBothNormalNoCuts").fill(crs.pointLocal().x(), crs.pointLocal().y());
                histoGroupCrossesNoCuts.getH2F("sizeCompBothNormalNoCuts").fill(crs.getCluster1().size(), crs.getCluster2().size());                                
                histoGroupCrossesNoCuts.getH2F("energyCompCrossesBothNormalNoCuts").fill(crs.getCluster1().energy(), crs.getCluster2().energy());                
                histoGroupCrossesNoCuts.getH1F("energyDiffCrossesBothNormalNoCuts").fill(crs.getCluster1().energy() - crs.getCluster2().energy());                
                histoGroupCrossesNoCuts.getH2F("timeCompCrossesBothNormalNoCuts").fill(crs.getCluster1().time(), crs.getCluster2().time());                
                histoGroupCrossesNoCuts.getH1F("timeDiffCrossesBothNormalNoCuts").fill(crs.getCluster1().time() - crs.getCluster2().time());                
                histoGroupCrossesNoCuts.getH1F("timeCrossesBothNormalNoCuts").fill(crs.time());
            }
            
            histoGroupCrossesNoCuts.getH2F("xyCrossesAllNoCuts").fill(crs.pointLocal().x(), crs.pointLocal().y());
            histoGroupCrossesNoCuts.getH2F("sizeCompAllNoCuts").fill(crs.getCluster1().size(), crs.getCluster2().size());                                
            histoGroupCrossesNoCuts.getH2F("energyCompCrossesAllNoCuts").fill(crs.getCluster1().energy(), crs.getCluster2().energy());                
            histoGroupCrossesNoCuts.getH1F("energyDiffCrossesAllNoCuts").fill(crs.getCluster1().energy() - crs.getCluster2().energy());                
            histoGroupCrossesNoCuts.getH2F("timeCompCrossesAllNoCuts").fill(crs.getCluster1().time(), crs.getCluster2().time());                
            histoGroupCrossesNoCuts.getH1F("timeDiffCrossesAllNoCuts").fill(crs.getCluster1().time() - crs.getCluster2().time());                
            histoGroupCrossesNoCuts.getH1F("timeCrossesAllNoCuts").fill(crs.time());
        }
        
        HistoGroup histoGroupCrossStatus = histoGroupMap.get("crossStatus");
        for(URWellCross crs : crosses){
            if(crs.isBothClustersNoise()) {
                histoGroupCrossStatus.getH1F("crossStatus").fill(0);
            }
            else if(crs.isCluster1Normal() && !crs.isCluster2Normal()) {
                histoGroupCrossStatus.getH1F("crossStatus").fill(1);
            }
            else if(crs.isCluster2Normal() && !crs.isCluster1Normal()) {
                histoGroupCrossStatus.getH1F("crossStatus").fill(2);
            }
            else {
                histoGroupCrossStatus.getH1F("crossStatus").fill(3);
            }
        }
    }
    
    public void postEventProcess() {

                      
    }            
                            
    public static void main(String[] args){
        OptionParser parser = new OptionParser("uRWellReconstruction");
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
        
        URWellReconstruction analysis = new URWellReconstruction();
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
                frame.setSize(1750, 1050);
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
