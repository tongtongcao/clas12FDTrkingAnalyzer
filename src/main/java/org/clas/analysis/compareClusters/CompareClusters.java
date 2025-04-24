package org.clas.analysis.compareClusters;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JFrame;

import org.jlab.groot.data.H1F;
import org.jlab.groot.data.H2F;

import org.clas.analysis.BaseAnalysis;
import org.clas.demo.DemoBase;
import org.clas.element.Cluster;
import org.clas.element.Hit;
import org.clas.element.Track;
import org.clas.element.URWellCross;
import org.clas.graph.HistoGroup;
import org.clas.reader.LocalEvent;
import org.clas.fit.ClusterFitLC;
import org.clas.graph.TrackHistoGroup;
import org.clas.reader.Banks;
import org.clas.utilities.Constants;
import org.jlab.groot.data.GraphErrors;
import org.jlab.groot.graphics.EmbeddedCanvasTabbed;
import org.jlab.groot.group.DataGroup;
import org.jlab.jnp.hipo4.data.Event;
import org.jlab.jnp.hipo4.data.SchemaFactory;
import org.jlab.jnp.hipo4.io.HipoReader;
import org.jlab.utils.benchmark.ProgressPrintout;
import org.jlab.utils.options.OptionParser;
import org.clas.demo.DemoSuperlayerWithURWell;
import org.clas.demo.DemoConstants;

/**
 *
 * @author Tongtong Cao
 */
public class CompareClusters extends BaseAnalysis {      
    @Override
    public void createHistoGroupMap() {        
        HistoGroup histoGroupClusterSize = new HistoGroup("clusterSize", 2, 3);
        HistoGroup histoGroupHitDistSample1 = new HistoGroup("hitDistSample1", 2, 3);
        HistoGroup histoGroupHitDistSample2 = new HistoGroup("hitDistSample2", 2, 3);
        HistoGroup histoGroupRatioNormalHitsSample1 = new HistoGroup("ratioNormalHitsSample1", 2, 3);
        HistoGroup histoGroupRatioNormalHitsSample2 = new HistoGroup("ratioNormalHitsSample2", 2, 3);
        HistoGroup histoGroupClusterMatching = new HistoGroup("clusterMatching", 2, 3);
        HistoGroup histoGroupNormalRatioExtraSample1 = new HistoGroup("normalRatioExtraSample1", 2, 3);  
        HistoGroup histoGroupNormalRatioExtraSample2 = new HistoGroup("normalRatioExtraSample2", 2, 3); 
        HistoGroup histoGroupMatchedHitRatioComp = new HistoGroup("matchedHitRatioComp", 2, 3);
        HistoGroup histoGroupIfPerfectMatch = new HistoGroup("ifPerfectMatch", 2, 3);
        HistoGroup histoGroupMatchedHitRatio = new HistoGroup("matchedHitRatio", 2, 3);        
        HistoGroup histoGroupNormalRatioMatchedClustersComp = new HistoGroup("normalRatioMatchedClustersComp", 2, 3);
        HistoGroup histoGroupIfBothNormalOrNoiseClusters = new HistoGroup("ifBothNormalOrNoiseClusters", 2, 3);
        HistoGroup histoGroupNormalRatioMatchedClusters = new HistoGroup("normalRatioMatchedClusters", 2, 3);        
        
        for (int i = 0; i < 6; i++) {
            H1F h1_clusterSizeSample1 = new H1F("clusterSizeSample1 for SL" + Integer.toString(i + 1),
                    "cluster size for SL" + Integer.toString(i + 1), 12, 0, 12);
            h1_clusterSizeSample1.setTitleX("cluster size");
            h1_clusterSizeSample1.setTitleY("Counts");
            h1_clusterSizeSample1.setLineColor(1);
            histoGroupClusterSize.addDataSet(h1_clusterSizeSample1, i);
            
            H1F h1_clusterSizeSample2 = new H1F("clusterSizeSample2 for SL" + Integer.toString(i + 1),
                    "cluster size for SL" + Integer.toString(i + 1), 12, 0, 12);
            h1_clusterSizeSample2.setTitleX("cluster size");
            h1_clusterSizeSample2.setTitleY("Counts");
            h1_clusterSizeSample2.setLineColor(2);
            histoGroupClusterSize.addDataSet(h1_clusterSizeSample2, i);
            
            H2F h2_hitDistSample1 = new H2F("hitDistSample1 for SL" + Integer.toString(i + 1),
                    "normal vs bg hits for SL" + Integer.toString(i + 1), 12, 0, 12, 12, 0, 12);
            h2_hitDistSample1.setTitleX("normal hits");
            h2_hitDistSample1.setTitleY("bg hits");
            histoGroupHitDistSample1.addDataSet(h2_hitDistSample1, i);
            
            H2F h2_hitDistSample2 = new H2F("hitDistSample2 for SL" + Integer.toString(i + 1),
                    "normal vs bg hits for SL" + Integer.toString(i + 1), 12, 0, 12, 12, 0, 12);
            h2_hitDistSample2.setTitleX("normal hits");
            h2_hitDistSample2.setTitleY("bg hits");
            histoGroupHitDistSample2.addDataSet(h2_hitDistSample2, i);
            
            H1F h1_ratioNormalHitsSample1 = new H1F("ratioNormalHitsSample1 for SL" + Integer.toString(i + 1),
                    "ratio of normal hits in clusters for SL" + Integer.toString(i + 1), 101, 0, 1.01);
            h1_ratioNormalHitsSample1.setTitleX("ratio of normal hits");
            h1_ratioNormalHitsSample1.setTitleY("Counts");
            histoGroupRatioNormalHitsSample1.addDataSet(h1_ratioNormalHitsSample1, i);            
            
            H1F h1_ratioNormalHitsSample2 = new H1F("ratioNormalHitsSample2 for SL" + Integer.toString(i + 1),
                    "ratio of normal hits in clusters for SL" + Integer.toString(i + 1), 101, 0, 1.01);
            h1_ratioNormalHitsSample2.setTitleX("ratio of normal hits");
            h1_ratioNormalHitsSample2.setTitleY("Counts");
            histoGroupRatioNormalHitsSample2.addDataSet(h1_ratioNormalHitsSample2, i);
            
            H1F h1_clusterMatching = new H1F("clusterMatching for SL" + Integer.toString(i + 1),
                    "clusterMatching for SL" + Integer.toString(i + 1), 3, -0.5, 2.5);
            h1_clusterMatching.setTitleX("if matched hits exist");
            h1_clusterMatching.setTitleY("Counts");
            histoGroupClusterMatching.addDataSet(h1_clusterMatching, i);
            
            H1F h1_normalRatioExtraSample1 = new H1F("normalRatioExtraSample1 for SL" + Integer.toString(i + 1),
                    "ratio of normal hits for SL" + Integer.toString(i + 1), 100, 0, 1.01);
            h1_normalRatioExtraSample1.setTitleX("ratio of normal hits");
            h1_normalRatioExtraSample1.setTitleY("Counts");
            histoGroupNormalRatioExtraSample1.addDataSet(h1_normalRatioExtraSample1, i);
            
            H1F h1_normalRatioExtraSample2 = new H1F("normalRatioExtraSample2 for SL" + Integer.toString(i + 1),
                    "ratio of normal hits for SL" + Integer.toString(i + 1), 100, 0, 1.01);
            h1_normalRatioExtraSample2.setTitleX("ratio of normal hits");
            h1_normalRatioExtraSample2.setTitleY("Counts");
            histoGroupNormalRatioExtraSample2.addDataSet(h1_normalRatioExtraSample2, i);
            
            H2F h2_matchedHitRatioComp = new H2F("matchedHitRatioComp for SL" + Integer.toString(i + 1),
                    "ratio of matched hits for SL" + Integer.toString(i + 1), 30, 0, 1.01, 30, 0, 1.01);
            h2_matchedHitRatioComp.setTitleX("ratioMatchedHits for sample 1");
            h2_matchedHitRatioComp.setTitleY("ratioMatchedHits for sample 2");
            histoGroupMatchedHitRatioComp.addDataSet(h2_matchedHitRatioComp, i);  

            H1F h1_ifPerfectMatch = new H1F("ifPerfectMatch for SL" + Integer.toString(i + 1),
                    "ifPerfectMatch for SL" + Integer.toString(i + 1), 2, -0.5, 1.5);
            h1_ifPerfectMatch.setTitleX("if perfect match");
            h1_ifPerfectMatch.setTitleY("Counts");
            histoGroupIfPerfectMatch.addDataSet(h1_ifPerfectMatch, i);            
            
            H1F h1_matchedHitRatioSample1MatchedClusters = new H1F("matchedHitRatioSample1MatchedClusters for SL" + Integer.toString(i + 1),
                    "ratio of normal hits for SL" + Integer.toString(i + 1), 101, 0, 1.01);
            h1_matchedHitRatioSample1MatchedClusters.setTitleX("ratio of matched hits");
            h1_matchedHitRatioSample1MatchedClusters.setTitleY("Counts");
            h1_matchedHitRatioSample1MatchedClusters.setLineColor(1);
            histoGroupMatchedHitRatio.addDataSet(h1_matchedHitRatioSample1MatchedClusters, i);
            H1F h1_matchedHitRatioSample2MatchedClusters = new H1F("matchedHitRatioSample2MatchedClusters for SL" + Integer.toString(i + 1),
                    "ratio of normal hits for SL" + Integer.toString(i + 1), 101, 0, 1.01);
            h1_matchedHitRatioSample2MatchedClusters.setTitleX("ratio of matched hits");
            h1_matchedHitRatioSample2MatchedClusters.setTitleY("Counts");
            h1_matchedHitRatioSample2MatchedClusters.setLineColor(2);
            histoGroupMatchedHitRatio.addDataSet(h1_matchedHitRatioSample2MatchedClusters, i);            
                        
            H2F h2_normalRatioMatchedClustersComp = new H2F("normalRatioMatchedClustersComp for SL" + Integer.toString(i + 1),
                    "ratio of normal hits for SL" + Integer.toString(i + 1), 30, 0, 1.01, 30, 0, 1.01);
            h2_normalRatioMatchedClustersComp.setTitleX("ratio of normal hits for sample 1");
            h2_normalRatioMatchedClustersComp.setTitleY("ratio of normal hits for sample 2");
            histoGroupNormalRatioMatchedClustersComp.addDataSet(h2_normalRatioMatchedClustersComp, i); 
            
            H1F h1_ifBothNormalOrNoiseClusters = new H1F("ifBothNormalOrNoiseClusters for SL" + Integer.toString(i + 1),
                    "ifBothNormalOrNoiseClusters for SL" + Integer.toString(i + 1), 3, -0.5, 2.5);
            h1_ifBothNormalOrNoiseClusters.setTitleX("if both normal or noise clusters");
            h1_ifBothNormalOrNoiseClusters.setTitleY("Counts");
            histoGroupIfBothNormalOrNoiseClusters.addDataSet(h1_ifBothNormalOrNoiseClusters, i);              
            
            H1F h1_normalRatioSample1MatchedClusters = new H1F("normalRatioSample1MatchedClusters for SL" + Integer.toString(i + 1),
                    "ratio of normal hits for SL" + Integer.toString(i + 1), 101, 0, 1.01);
            h1_normalRatioSample1MatchedClusters.setTitleX("ratio of normal hits");
            h1_normalRatioSample1MatchedClusters.setTitleY("Counts");
            h1_normalRatioSample1MatchedClusters.setLineColor(1);
            histoGroupNormalRatioMatchedClusters.addDataSet(h1_normalRatioSample1MatchedClusters, i);
            H1F h1_normalRatioSample2MatchedClusters = new H1F("normalRatioSample2MatchedClusters for SL" + Integer.toString(i + 1),
                    "ratio of normal hits for SL" + Integer.toString(i + 1), 101, 0, 1.01);
            h1_normalRatioSample2MatchedClusters.setTitleX("ratio of normal hits");
            h1_normalRatioSample2MatchedClusters.setTitleY("Counts");
            h1_normalRatioSample2MatchedClusters.setLineColor(2);
            histoGroupNormalRatioMatchedClusters.addDataSet(h1_normalRatioSample2MatchedClusters, i);
            
        }
        
        histoGroupMap.put(histoGroupClusterSize.getName(), histoGroupClusterSize);
        histoGroupMap.put(histoGroupHitDistSample1.getName(), histoGroupHitDistSample1);
        histoGroupMap.put(histoGroupHitDistSample2.getName(), histoGroupHitDistSample2);
        histoGroupMap.put(histoGroupRatioNormalHitsSample1.getName(), histoGroupRatioNormalHitsSample1); 
        histoGroupMap.put(histoGroupRatioNormalHitsSample2.getName(), histoGroupRatioNormalHitsSample2); 
        histoGroupMap.put(histoGroupClusterMatching.getName(), histoGroupClusterMatching);
        histoGroupMap.put(histoGroupNormalRatioExtraSample1.getName(), histoGroupNormalRatioExtraSample1);
        histoGroupMap.put(histoGroupNormalRatioExtraSample2.getName(), histoGroupNormalRatioExtraSample2);
        histoGroupMap.put(histoGroupMatchedHitRatioComp.getName(), histoGroupMatchedHitRatioComp); 
        histoGroupMap.put(histoGroupIfPerfectMatch.getName(), histoGroupIfPerfectMatch); 
        histoGroupMap.put(histoGroupMatchedHitRatio.getName(), histoGroupMatchedHitRatio); 
        histoGroupMap.put(histoGroupNormalRatioMatchedClustersComp.getName(), histoGroupNormalRatioMatchedClustersComp);  
        histoGroupMap.put(histoGroupIfBothNormalOrNoiseClusters.getName(), histoGroupIfBothNormalOrNoiseClusters); 
        histoGroupMap.put(histoGroupNormalRatioMatchedClusters.getName(), histoGroupNormalRatioMatchedClusters); 
    }
    
    public void processEvent(Event event1, Event event2, int trkType) {
        ////// Read banks
        LocalEvent localEvent1 = new LocalEvent(reader1, event1, trkType, true);
        LocalEvent localEvent2 = new LocalEvent(reader2, event2, trkType, true); 
        
        List<Cluster> clusters1 = localEvent1.getClusters();
        List<Cluster> clusters2 = localEvent2.getClusters();       
        
        ////// Make cluster map between two samples
        // Priorities for map most matched hits (priority 1), and closest avgWire (priority 2)  
        Map<Cluster, Cluster> map_cls1_cls2_matched = new HashMap();             
        for(Cluster cls1 : clusters1){
            List<Cluster> matchedClustersWithMostMatchedHits = new ArrayList();
            int maxMatchedHits = -1;
            for(Cluster cls2 : clusters2){
                int numMatchedHits = cls1.clusterMatchedHits(cls2);
                if(numMatchedHits > 0){
                    if(numMatchedHits > maxMatchedHits) {
                        maxMatchedHits = numMatchedHits;
                        matchedClustersWithMostMatchedHits.clear();
                        matchedClustersWithMostMatchedHits.add(cls2);                    
                    }
                    else if(numMatchedHits == maxMatchedHits){
                        matchedClustersWithMostMatchedHits.add(cls2);
                    }
                }
            }
                        
            if(matchedClustersWithMostMatchedHits.size() == 1) {
                Cluster matchedCluster2 = matchedClustersWithMostMatchedHits.get(0);                
                if(map_cls1_cls2_matched.values().contains(matchedCluster2)){ // If matchedCluster2 already has a matched cluster 1, compare that cluster 1 to this cluster 1
                    Cluster thatMatchedCluster1 = null;
                    for (Map.Entry<Cluster, Cluster> entry : map_cls1_cls2_matched.entrySet()) {
                        if (entry.getValue() == matchedCluster2) {
                            thatMatchedCluster1 = entry.getKey();
                            break;
                        }
                    }
                    
                    int numMatchedHitsThisClus1 = matchedCluster2.clusterMatchedHits(cls1);
                    int numMatchedHitsThatClus1 = matchedCluster2.clusterMatchedHits(thatMatchedCluster1);
                    if(numMatchedHitsThisClus1 > numMatchedHitsThatClus1){
                        map_cls1_cls2_matched.remove(thatMatchedCluster1);
                        map_cls1_cls2_matched.put(cls1, matchedCluster2);
                    }
                    else if(numMatchedHitsThisClus1 == numMatchedHitsThatClus1){
                        double absAvgWireDiffThisClus1 = Math.abs(matchedCluster2.avgWire() - cls1.avgWire());
                        double absAvgWireDiffThatClus1 = Math.abs(matchedCluster2.avgWire() - thatMatchedCluster1.avgWire());
                        if(absAvgWireDiffThisClus1 < absAvgWireDiffThatClus1){
                            map_cls1_cls2_matched.remove(thatMatchedCluster1);
                            map_cls1_cls2_matched.put(cls1, matchedCluster2);
                        }
                    }                                        
                } else {                
                    map_cls1_cls2_matched.put(cls1, matchedCluster2);
                }
                
            }
            else if (matchedClustersWithMostMatchedHits.size() > 1) {
                double closestAvgWire = 10000;
                Cluster clsWithCloestAvgWire = null;
                for (Cluster cls2 : matchedClustersWithMostMatchedHits) {
                    double absDiffAvgWire = Math.abs(cls1.avgWire() - cls2.avgWire());
                    if (absDiffAvgWire < closestAvgWire) {
                        closestAvgWire = absDiffAvgWire;
                        clsWithCloestAvgWire = cls2;
                    }
                }
                if (clsWithCloestAvgWire != null) {
                    if (map_cls1_cls2_matched.values().contains(clsWithCloestAvgWire)) { // If matchedCluster2 already has a matched cluster 1, compare that cluster 1 to this cluster 1
                        Cluster thatMatchedCluster1 = null;
                        for (Map.Entry<Cluster, Cluster> entry : map_cls1_cls2_matched.entrySet()) {
                            if (entry.getValue() == clsWithCloestAvgWire) {
                                thatMatchedCluster1 = entry.getKey();
                                break;
                            }
                        }
                        int numMatchedHitsThisClus1 = clsWithCloestAvgWire.clusterMatchedHits(cls1);
                        int numMatchedHitsThatClus1 = clsWithCloestAvgWire.clusterMatchedHits(thatMatchedCluster1);
                        if (numMatchedHitsThisClus1 > numMatchedHitsThatClus1) {
                            map_cls1_cls2_matched.remove(thatMatchedCluster1);
                            map_cls1_cls2_matched.put(cls1, clsWithCloestAvgWire);
                        } else if (numMatchedHitsThisClus1 == numMatchedHitsThatClus1) {
                            double absAvgWireDiffThisClus1 = Math.abs(clsWithCloestAvgWire.avgWire() - cls1.avgWire());
                            double absAvgWireDiffThatClus1 = Math.abs(clsWithCloestAvgWire.avgWire() - thatMatchedCluster1.avgWire());
                            if (absAvgWireDiffThisClus1 < absAvgWireDiffThatClus1) {
                                map_cls1_cls2_matched.remove(thatMatchedCluster1);
                                map_cls1_cls2_matched.put(cls1, clsWithCloestAvgWire);
                            }
                        }
                    } else {
                        map_cls1_cls2_matched.put(cls1, clsWithCloestAvgWire);
                    }
                }
            }
        }             
        
        
        List<Cluster> clsListExtraSample1 = new ArrayList(); // extra clusters in sample 1
        List<Cluster> clsListExtraSample2 = new ArrayList(); // extra clusters in sample 2  
        clsListExtraSample1.addAll(clusters1);
        clsListExtraSample1.removeAll(map_cls1_cls2_matched.keySet());        
        clsListExtraSample2.addAll(clusters2);
        clsListExtraSample2.removeAll(map_cls1_cls2_matched.values());
        
        Map<Cluster, Cluster> map_cls1_cls2_matched_added = new HashMap(); 
        List<Cluster> removedClsListExtraSample1 = new ArrayList();
        for(Cluster cls1: clsListExtraSample1){
            for(Cluster cls2 : map_cls1_cls2_matched.values()){
                if(cls1.clusterMatchedHits(cls2) >= 3){
                    map_cls1_cls2_matched_added.put(new Cluster(cls1), cls2);
                    removedClsListExtraSample1.add(cls1);
                }
            }
        }
        clsListExtraSample1.removeAll(removedClsListExtraSample1);
        
        List<Cluster> removedClsListExtraSample2 = new ArrayList();
        for(Cluster cls2: clsListExtraSample2){
            for(Cluster cls1 : map_cls1_cls2_matched.keySet()){
                if(cls2.clusterMatchedHits(cls1) >= 3){
                    map_cls1_cls2_matched_added.put(new Cluster(cls1), cls2);
                    removedClsListExtraSample2.add(cls2);
                }
            }
        }
        map_cls1_cls2_matched.putAll(map_cls1_cls2_matched_added);
        clsListExtraSample2.removeAll(removedClsListExtraSample2);
        
        ////// Matched clusters
        HistoGroup histoGroupClusterMatching = histoGroupMap.get("clusterMatching");          
        HistoGroup histoGroupNormalRatioExtraSample1 = histoGroupMap.get("normalRatioExtraSample1");  
        HistoGroup histoGroupNormalRatioExtraSample2 = histoGroupMap.get("normalRatioExtraSample2");  
        HistoGroup histoGroupMatchedHitRatioComp = histoGroupMap.get("matchedHitRatioComp");  
        HistoGroup histoGroupMatchedHitRatio = histoGroupMap.get("matchedHitRatio");  
        HistoGroup histoGroupIfPerfectMatch = histoGroupMap.get("ifPerfectMatch");
        HistoGroup histoGroupNormalRatioMatchedClustersComp = histoGroupMap.get("normalRatioMatchedClustersComp");  
        HistoGroup histoGroupIfBothNormalOrNoiseClusters = histoGroupMap.get("ifBothNormalOrNoiseClusters");
        HistoGroup histoGroupNormalRatioMatchedClusters = histoGroupMap.get("normalRatioMatchedClusters");
        
        for(Cluster cls1 : map_cls1_cls2_matched.keySet()){
            histoGroupClusterMatching.getH1F("clusterMatching for SL" + cls1.superlayer()).fill(1);
            Cluster cls2 = map_cls1_cls2_matched.get(cls1);
            int numMatchedHits = cls1.clusterMatchedHits(cls2);
            double ratioMatchedHits1 = (double)numMatchedHits/cls1.size();
            double ratioMatchedHits2 = (double)numMatchedHits/cls2.size();
            histoGroupMatchedHitRatioComp.getH2F("matchedHitRatioComp for SL" + cls1.superlayer()).fill(ratioMatchedHits1, ratioMatchedHits2);
            if(ratioMatchedHits1 == 1 && ratioMatchedHits2 == 1){
                histoGroupIfPerfectMatch.getH1F("ifPerfectMatch for SL" + cls1.superlayer()).fill(1);
            }
            else{
                histoGroupIfPerfectMatch.getH1F("ifPerfectMatch for SL" + cls1.superlayer()).fill(0);
                histoGroupMatchedHitRatio.getH1F("matchedHitRatioSample1MatchedClusters for SL" + cls1.superlayer()).fill(ratioMatchedHits1);
                histoGroupMatchedHitRatio.getH1F("matchedHitRatioSample2MatchedClusters for SL" + cls2.superlayer()).fill(ratioMatchedHits2);
            }
                        
            double ratioNormalHits1 = cls1.getRatioNormalHits();
            double ratioNormalHits2 = cls2.getRatioNormalHits();
            histoGroupNormalRatioMatchedClustersComp.getH2F("normalRatioMatchedClustersComp for SL" + cls1.superlayer()).fill(ratioNormalHits1, ratioNormalHits2);
            if(ratioNormalHits1 == 0 && ratioNormalHits2 == 0) {
                histoGroupIfBothNormalOrNoiseClusters.getH1F("ifBothNormalOrNoiseClusters for SL" + cls1.superlayer()).fill(0);
            }
            else if(ratioNormalHits1 == 1 && ratioNormalHits2 == 1) {
                histoGroupIfBothNormalOrNoiseClusters.getH1F("ifBothNormalOrNoiseClusters for SL" + cls1.superlayer()).fill(2);
            }
            else{
                histoGroupIfBothNormalOrNoiseClusters.getH1F("ifBothNormalOrNoiseClusters for SL" + cls1.superlayer()).fill(1);
                
                histoGroupNormalRatioMatchedClusters.getH1F("normalRatioSample1MatchedClusters for SL" + cls1.superlayer()).fill(ratioNormalHits1);
                histoGroupNormalRatioMatchedClusters.getH1F("normalRatioSample2MatchedClusters for SL" + cls2.superlayer()).fill(ratioNormalHits2);
                
                //if(ratioNormalHits1 == 1) addDemoGroupLoopClusters(localEvent1, localEvent2, cls1.sector(), "matched1R1.0");
                if(ratioNormalHits2 == 1) addDemoGroupLoopClusters(localEvent1, localEvent2, cls2.sector(), "matched2R1.0");
            }
        }        

        ////// Extra clusters
        for(Cluster cls1 : clsListExtraSample1){
            histoGroupClusterMatching.getH1F("clusterMatching for SL" + cls1.superlayer()).fill(0);
            histoGroupNormalRatioExtraSample1.getH1F("normalRatioExtraSample1 for SL" + cls1.superlayer()).fill(cls1.getRatioNormalHits());
            
            //if(cls1.getRatioNormalHits() == 0) addDemoGroupLoopClusters(localEvent1, localEvent2, cls1.sector(), "ex1R" + cls1.getRatioNormalHits());
            //if(cls1.getRatioNormalHits() == 1) addDemoGroupLoopClusters(localEvent1, localEvent2, cls1.sector(), "ex1R" + cls1.getRatioNormalHits());
        }
        for(Cluster cls2 : clsListExtraSample2){
            histoGroupClusterMatching.getH1F("clusterMatching for SL" + cls2.superlayer()).fill(2);
            histoGroupNormalRatioExtraSample2.getH1F("normalRatioExtraSample2 for SL" + cls2.superlayer()).fill(cls2.getRatioNormalHits());
            
            //if(cls2.getRatioNormalHits() == 0) addDemoGroupLoopClusters(localEvent1, localEvent2, cls2.sector(), "ex2R" + cls2.getRatioNormalHits());
            //if(cls2.getRatioNormalHits() == 1) addDemoGroupLoopClusters(localEvent1, localEvent2, cls2.sector(), "ex2R" + cls2.getRatioNormalHits());
        }                          
    }
    
    public void postEventProcess(){
    }               
    
    public static void main(String[] args) {
        OptionParser parser = new OptionParser("studyBgEffectsDC");
        parser.setRequiresInputList(false);
        // valid options for event-base analysis
        parser.addOption("-o", "", "output file name prefix");
        parser.addOption("-n", "-1", "maximum number of events to process");
        parser.addOption("-plot", "1", "display histograms (0/1)");
        parser.addOption("-demo", "1", "display case demo (0/1)");
        parser.addOption("-mDemo", "1000", "maxium for number of demonstrated cases");
        parser.addOption("-trkType"      ,"22",   "tracking type: 12 (ConvTB) or 22 (AITB)");
        parser.addOption("-mc", "1", "if mc (0/1)");
        parser.addOption("-uRWell", "1", "if uRWell is included (0/1)");


        // histogram based analysis
        parser.addOption("-histo", "0", "read histogram file (0/1)");

        parser.parse(args);

        String namePrefix = parser.getOption("-o").stringValue();
        int maxEvents = parser.getOption("-n").intValue();
        boolean displayPlots = (parser.getOption("-plot").intValue() != 0);
        boolean displayDemos = (parser.getOption("-demo").intValue() != 0);
        int maxDemoCases = parser.getOption("-mDemo").intValue();
        boolean readHistos = (parser.getOption("-histo").intValue() != 0);
        int trkType = parser.getOption("-trkType").intValue();
        boolean mc = (parser.getOption("-mc").intValue() != 0);
        boolean uRWell = (parser.getOption("-uRWell").intValue() != 0);
        Constants.MC = mc;
        Constants.URWELL = uRWell;
        Constants.MAXDEMOCASES = maxDemoCases;
        
        List<String> inputList = parser.getInputList();
        if (inputList.isEmpty() == true) {
            parser.printUsage();
            inputList.add("/Users/caot/research/clas12/data/mc/uRWELL/upgradeTrackingWithuRWELL/rga-sidis-uRWell-2R_denoise/0nA/reconBg/0000.hipo");
            inputList.add("/Users/caot/research/clas12/data/mc/uRWELL/upgradeTrackingWithuRWELL/rga-sidis-uRWell-2R_denoise/50nA/reconBg/0000.hipo");
            maxEvents = 1000;
            //System.out.println("\n >>>> error: no input file is specified....\n");
            //System.exit(0);
        }

        String histoName = "histo.hipo";
        if (!namePrefix.isEmpty()) {
            histoName = namePrefix + "_" + histoName;
        }
        
        CompareClusters analysis = new CompareClusters();
        analysis.createHistoGroupMap();
        

        if (!readHistos) {
            HipoReader reader1 = new HipoReader();
            reader1.open(inputList.get(0));
            HipoReader reader2 = new HipoReader();
            reader2.open(inputList.get(1));

            SchemaFactory schema1 = reader1.getSchemaFactory();
            SchemaFactory schema2 = reader2.getSchemaFactory();
            analysis.initReader(new Banks(schema1), new Banks(schema2));

            int counter = 0;
            Event event1 = new Event();
            Event event2 = new Event();

            ProgressPrintout progress = new ProgressPrintout();
            while (reader1.hasNext() && reader2.hasNext()) {

                counter++;

                reader1.nextEvent(event1);
                reader2.nextEvent(event2);

                analysis.processEvent(event1, event2, trkType);

                progress.updateStatus();
                if (maxEvents > 0) {
                    if (counter >= maxEvents) {
                        break;
                    }
                }
            }

            analysis.postEventProcess();

            progress.showStatus();
            reader1.close();
            reader2.close();
            analysis.saveHistos(histoName);
        } else {
            analysis.readHistos(inputList.get(0));
        }

        if (displayPlots) {
            JFrame frame = new JFrame();
            EmbeddedCanvasTabbed canvas = analysis.plotHistos();
            if(canvas != null){
                frame.setSize(800, 1200);
                frame.add(canvas);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        }
        
        if (displayDemos){
            JFrame frame2 = new JFrame();
            EmbeddedCanvasTabbed canvas2 = analysis.plotDemos();
            if(canvas2 != null){
                frame2.setSize(1200, 1500);
                frame2.add(canvas2);
                frame2.setLocationRelativeTo(null);
                frame2.setVisible(true);
            }
        }
    }

    
}