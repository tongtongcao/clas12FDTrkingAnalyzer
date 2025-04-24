package org.clas.analysis.studyBgEffects;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.jlab.groot.data.H1F;
import org.jlab.groot.data.H2F;

import org.clas.analysis.BaseAnalysis;
import org.clas.element.Cluster;
import org.clas.element.Hit;
import org.clas.element.Track;
import org.clas.graph.HistoGroup;
import org.clas.reader.LocalEvent;
import org.clas.fit.ClusterFitLC;
import org.clas.graph.TrackHistoGroup;

/**
 *
 * @author Tongtong Cao
 */
public class BgEffectsClustering extends BaseAnalysis {      
    @Override
    public void createHistoGroupMap() {
        HistoGroup histoGroupClustering = new HistoGroup("clustering", 4, 2);
        H1F h1_numClustersSLSamples1 = new H1F("numClustersSLSamples1", "# of clusters for sample 1", 6, 0.5, 6.5);
        h1_numClustersSLSamples1.setTitleX("SL");
        h1_numClustersSLSamples1.setTitleY("Counts");
        h1_numClustersSLSamples1.setLineColor(3);
        histoGroupClustering.addDataSet(h1_numClustersSLSamples1, 0);
        H1F h1_remainingHitSLDenoisingSample1 = new H1F("remainingHitSLDenoisingSample1", "hits for sample 1", 6, 0.5, 6.5);
        h1_remainingHitSLDenoisingSample1.setTitleX("SL");
        h1_remainingHitSLDenoisingSample1.setTitleY("Counts");
        h1_remainingHitSLDenoisingSample1.setLineColor(2);
        histoGroupClustering.addDataSet(h1_remainingHitSLDenoisingSample1, 1);
        H1F h1_remainingHitSLClusteringSample1 = new H1F("remainingHitSLClusteringSample1", "hits after clustering for sample 1", 6, 0.5, 6.5);
        h1_remainingHitSLClusteringSample1.setTitleX("SL");
        h1_remainingHitSLClusteringSample1.setTitleY("Counts");
        h1_remainingHitSLClusteringSample1.setLineColor(3);
        histoGroupClustering.addDataSet(h1_remainingHitSLClusteringSample1, 1);        
        H1F h1_numClustersSLSamples2 = new H1F("numClustersSLSamples2", "# of clusters for sample 2", 6, 0.5, 6.5);
        h1_numClustersSLSamples2.setTitleX("SL");
        h1_numClustersSLSamples2.setTitleY("Counts");
        h1_numClustersSLSamples2.setLineColor(3);
        histoGroupClustering.addDataSet(h1_numClustersSLSamples2, 2);
        H1F h1_remainingHitSLDenoisingSample2 = new H1F("remainingHitSLDenoisingSample2", "hits for sample 2", 6, 0.5, 6.5);
        h1_remainingHitSLDenoisingSample2.setTitleX("SL");
        h1_remainingHitSLDenoisingSample2.setTitleY("Counts");
        h1_remainingHitSLDenoisingSample2.setLineColor(2);         
        histoGroupClustering.addDataSet(h1_remainingHitSLDenoisingSample2, 3);
        H1F h1_remainingHitSLClusteringSample2 = new H1F("remainingHitSLClusteringSample2", "hits after clustering for sample 2", 6, 0.5, 6.5);
        h1_remainingHitSLClusteringSample2.setTitleX("SL");
        h1_remainingHitSLClusteringSample2.setTitleY("Counts");
        h1_remainingHitSLClusteringSample2.setLineColor(3);
        histoGroupClustering.addDataSet(h1_remainingHitSLClusteringSample2, 3); 
        H1F h1_remainingHitSLDenoisingNormalSample2 = new H1F("remainingHitSLDenoisingNormalSample2", "normal hits for sample 2", 6, 0.5, 6.5);
        h1_remainingHitSLDenoisingNormalSample2.setTitleX("SL");
        h1_remainingHitSLDenoisingNormalSample2.setTitleY("Counts");
        h1_remainingHitSLDenoisingNormalSample2.setLineColor(2);
        histoGroupClustering.addDataSet(h1_remainingHitSLDenoisingNormalSample2, 4);
        H1F h1_remainingHitSLClusteringNormalSample2 = new H1F("remainingHitSLClusteringNormalSample2", "normal hits after clustering for sample 2", 6, 0.5, 6.5);
        h1_remainingHitSLClusteringNormalSample2.setTitleX("SL");
        h1_remainingHitSLClusteringNormalSample2.setTitleY("Counts");
        h1_remainingHitSLClusteringNormalSample2.setLineColor(3);
        histoGroupClustering.addDataSet(h1_remainingHitSLClusteringNormalSample2, 4);
        H1F h1_remainingHitSLDenoisingBgSample2 = new H1F("remainingHitSLDenoisingBgSample2", "bg hits for sample 2", 6, 0.5, 6.5);
        h1_remainingHitSLDenoisingBgSample2.setTitleX("SL");
        h1_remainingHitSLDenoisingBgSample2.setTitleY("Counts");
        h1_remainingHitSLDenoisingBgSample2.setLineColor(2);
        histoGroupClustering.addDataSet(h1_remainingHitSLDenoisingBgSample2, 5);
        H1F h1_remainingHitSLClusteringBgSample2 = new H1F("remainingHitSLClusteringBgSample2", "bg hits after clustering for sample 2", 6, 0.5, 6.5);
        h1_remainingHitSLClusteringBgSample2.setTitleX("SL");
        h1_remainingHitSLClusteringBgSample2.setTitleY("Counts");
        h1_remainingHitSLClusteringBgSample2.setLineColor(3);        
        histoGroupClustering.addDataSet(h1_remainingHitSLClusteringBgSample2, 5);                                
        histoGroupMap.put(histoGroupClustering.getName(), histoGroupClustering);
        
        HistoGroup histoGroupClusterSize = new HistoGroup("clusterSize", 3, 2);
        HistoGroup histoGroupHitDistInClusterClusteringSample2 = new HistoGroup("hitDistInClusterClusteringSample2", 3, 2);
        HistoGroup histoGroupRatioNormalHitsInClusterClusteringSample2 = new HistoGroup("ratioNormalHitsInClusterClusteringSample2", 3, 2);
        HistoGroup histoGroupClusterMatching = new HistoGroup("clusterMatching", 3, 2);
        HistoGroup histoGroupMatchedHitRatioMatchedClusterMatching = new HistoGroup("matchedHitRatioMatchedClusterMatching", 3, 2);
        HistoGroup histoGroupNormalRatioExtraSample2ClusterMatching = new HistoGroup("normalRatioExtraSample2ClusterMatching", 3, 2);        
        
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
            
            H2F h2_hitDistInClusterClusteringSample2 = new H2F("hitDistInClusterClusteringSample2 for SL" + Integer.toString(i + 1),
                    "normal vs bg hits for SL" + Integer.toString(i + 1), 12, 0, 12, 12, 0, 12);
            h2_hitDistInClusterClusteringSample2.setTitleX("normal hits");
            h2_hitDistInClusterClusteringSample2.setTitleY("bg hits");
            histoGroupHitDistInClusterClusteringSample2.addDataSet(h2_hitDistInClusterClusteringSample2, i);
            
            H1F h1_ratioNormalHitsInClusterClusteringSample2 = new H1F("ratioNormalHitsInClusterClusteringSample2 for SL" + Integer.toString(i + 1),
                    "ratio of normal hits in clusters for SL" + Integer.toString(i + 1), 101, 0, 1.01);
            h1_ratioNormalHitsInClusterClusteringSample2.setTitleX("ratio of normal hits");
            h1_ratioNormalHitsInClusterClusteringSample2.setTitleY("Counts");
            histoGroupRatioNormalHitsInClusterClusteringSample2.addDataSet(h1_ratioNormalHitsInClusterClusteringSample2, i);
            
            H1F h1_clusterMatching = new H1F("clusterMatching for SL" + Integer.toString(i + 1),
                    "clusterMatching for SL" + Integer.toString(i + 1), 3, -0.5, 2.5);
            h1_clusterMatching.setTitleX("if matched hits exist");
            h1_clusterMatching.setTitleY("Counts");
            histoGroupClusterMatching.addDataSet(h1_clusterMatching, i);
            
            H1F h1_normalRatioExtraSample2ClusterMatching = new H1F("normalRatioExtraSample2ClusterMatching for SL" + Integer.toString(i + 1),
                    "ratio of normal hits for SL" + Integer.toString(i + 1), 100, 0, 1.01);
            h1_normalRatioExtraSample2ClusterMatching.setTitleX("ratio of normal hits");
            h1_normalRatioExtraSample2ClusterMatching.setTitleY("Counts");
            histoGroupNormalRatioExtraSample2ClusterMatching.addDataSet(h1_normalRatioExtraSample2ClusterMatching, i);
            
            H2F h2_matchedHitRatioMatchedClusterMatching = new H2F("matchedHitRatioMatchedClusterMatching for SL" + Integer.toString(i + 1),
                    "ratio of matched hits for SL" + Integer.toString(i + 1), 30, 0, 1.01, 30, 0, 1.01);
            h2_matchedHitRatioMatchedClusterMatching.setTitleX("ratioMatchedHits for sample 1");
            h2_matchedHitRatioMatchedClusterMatching.setTitleY("ratioMatchedHits for sample 2");
            histoGroupMatchedHitRatioMatchedClusterMatching.addDataSet(h2_matchedHitRatioMatchedClusterMatching, i);                                                             
            
        }
        histoGroupMap.put(histoGroupClusterSize.getName(), histoGroupClusterSize);
        histoGroupMap.put(histoGroupHitDistInClusterClusteringSample2.getName(), histoGroupHitDistInClusterClusteringSample2);
        histoGroupMap.put(histoGroupRatioNormalHitsInClusterClusteringSample2.getName(), histoGroupRatioNormalHitsInClusterClusteringSample2); 
        histoGroupMap.put(histoGroupClusterMatching.getName(), histoGroupClusterMatching);
        histoGroupMap.put(histoGroupNormalRatioExtraSample2ClusterMatching.getName(), histoGroupNormalRatioExtraSample2ClusterMatching);
        histoGroupMap.put(histoGroupMatchedHitRatioMatchedClusterMatching.getName(), histoGroupMatchedHitRatioMatchedClusterMatching);                                                
    }
    
    public void processEvent(LocalEvent localEvent1, LocalEvent localEvent2) {
        List<Hit> normalHits2 = new ArrayList();
        List<Hit> bgHits2 = new ArrayList();
        localEvent2.separateNormalBgHits(localEvent2.getHits(), normalHits2, bgHits2); 
        
        List<Hit> normalHitsClustering2 = new ArrayList();
        List<Hit> bgHitsClustering2 = new ArrayList();
        localEvent2.separateNormalBgHits(localEvent2.getHitsClustering(), normalHitsClustering2, bgHitsClustering2);        
        
        HistoGroup histoGroupClustering = histoGroupMap.get("clustering");  
        HistoGroup histoGroupClusterSize = histoGroupMap.get("clusterSize");
        // Sample 1                      
        for(Cluster cls : localEvent1.getClusters()){
            histoGroupClustering.getH1F("numClustersSLSamples1").fill(cls.superlayer());               
            histoGroupClusterSize.getH1F("clusterSizeSample1 for SL" + cls.superlayer()).fill(cls.size());
        } 
        for(Hit hit : localEvent1.getHits()){
            histoGroupClustering.getH1F("remainingHitSLDenoisingSample1").fill(hit.superlayer());            
        }        
        for(Hit hit : localEvent1.getHitsClustering()){
            histoGroupClustering.getH1F("remainingHitSLClusteringSample1").fill(hit.superlayer());            
        }
        
        // Sample 2           
        HistoGroup histoGroupHitDistInClusterClusteringSample2 = histoGroupMap.get("hitDistInClusterClusteringSample2");  
        HistoGroup histoGroupRatioNormalHitsInClusterClusteringSample2 = histoGroupMap.get("ratioNormalHitsInClusterClusteringSample2"); 
        for(Cluster cls : localEvent2.getClusters()){
            histoGroupClustering.getH1F("numClustersSLSamples2").fill(cls.superlayer());   
            histoGroupClusterSize.getH1F("clusterSizeSample2 for SL" + cls.superlayer()).fill(cls.size()); 
            histoGroupHitDistInClusterClusteringSample2.getH2F("hitDistInClusterClusteringSample2 for SL" + cls.superlayer()).fill(cls.getNumNormalHits(), cls.getNumBgHits());
            histoGroupRatioNormalHitsInClusterClusteringSample2.getH1F("ratioNormalHitsInClusterClusteringSample2 for SL" + cls.superlayer()).fill(cls.getRatioNormalHits());
        } 
        for(Hit hit : localEvent2.getHits()){
            histoGroupClustering.getH1F("remainingHitSLDenoisingSample2").fill(hit.superlayer());            
        }
        for(Hit hit : localEvent2.getHitsClustering()){
            histoGroupClustering.getH1F("remainingHitSLClusteringSample2").fill(hit.superlayer());            
        }
        
        // Normal hits in sample 2
        for(Hit hit : normalHits2){
            histoGroupClustering.getH1F("remainingHitSLDenoisingNormalSample2").fill(hit.superlayer());            
        }  
        for(Hit hit : normalHitsClustering2){
            histoGroupClustering.getH1F("remainingHitSLClusteringNormalSample2").fill(hit.superlayer());            
        }  
        
        // Bg hits in sample 2
        for(Hit hit : bgHits2){
            histoGroupClustering.getH1F("remainingHitSLDenoisingBgSample2").fill(hit.superlayer());            
        }
        for(Hit hit : bgHitsClustering2){
            histoGroupClustering.getH1F("remainingHitSLClusteringBgSample2").fill(hit.superlayer());            
        }
        
        // Make cluster map between two samples
        // Priorities for map most matched hits (priority 1), highest normal ratio (priority 2) and closest avgWire (priority 3)  
        Map<Cluster, Cluster> map_cls1_cls2_matched = new HashMap();             
        for(Cluster cls1 : localEvent1.getClusters()){
            List<Cluster> matchedClustersWithMostMatchedHits = new ArrayList();
            int maxMatchedHits = -1;
            for(Cluster cls2 : localEvent2.getClusters()){
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
            
            
            if(matchedClustersWithMostMatchedHits.size() == 1) map_cls1_cls2_matched.put(cls1, matchedClustersWithMostMatchedHits.get(0));
            else if(matchedClustersWithMostMatchedHits.size() > 1){
                List<Cluster> clustersWithMaxNormalRatio = new ArrayList();
                double maxNormalRatio = -1;                                            
                for(Cluster cls2 : matchedClustersWithMostMatchedHits){
                    double normalRatio = cls2.getRatioNormalHits();
                    if(normalRatio > maxNormalRatio) {
                        maxNormalRatio = normalRatio;
                        clustersWithMaxNormalRatio.clear();
                        clustersWithMaxNormalRatio.add(cls2);                    
                    }
                    else if(normalRatio == maxNormalRatio){
                        clustersWithMaxNormalRatio.add(cls2);
                    }
                }
                
                if(clustersWithMaxNormalRatio.size() == 1) map_cls1_cls2_matched.put(cls1, clustersWithMaxNormalRatio.get(0));
                else if(clustersWithMaxNormalRatio.size() > 1){
                   double closestAvgWire = 10000;
                   Cluster clsWithCloestAvgWire = null;
                   for(Cluster cls2 : clustersWithMaxNormalRatio){ 
                       double absDiffAvgWire = Math.abs(cls1.avgWire() - cls2.avgWire());
                       if(absDiffAvgWire < closestAvgWire) {
                           closestAvgWire = absDiffAvgWire;
                           clsWithCloestAvgWire = cls2;
                       }
                   }                   
                   if(clsWithCloestAvgWire != null) map_cls1_cls2_matched.put(cls1, clsWithCloestAvgWire);
                }
            }             
        }
        
        List<Cluster> clsListExtraSample1 = new ArrayList(); // extra clusters in sample 1
        List<Cluster> clsListExtraSample2 = new ArrayList(); // extra clusters in sample 2  
        clsListExtraSample1.addAll(localEvent1.getClusters());
        clsListExtraSample1.removeAll(map_cls1_cls2_matched.keySet());        
        clsListExtraSample2.addAll(localEvent2.getClusters());
        clsListExtraSample2.removeAll(map_cls1_cls2_matched.values());
        
        HistoGroup histoGroupClusterMatching = histoGroupMap.get("clusterMatching");  
        HistoGroup histoGroupMatchedHitRatioMatchedClusterMatching = histoGroupMap.get("matchedHitRatioMatchedClusterMatching");  
        
        HistoGroup histoGroupNormalRatioExtraSample2ClusterMatching = histoGroupMap.get("normalRatioExtraSample2ClusterMatching");  
        
        for(Cluster cls1 : map_cls1_cls2_matched.keySet()){
            histoGroupClusterMatching.getH1F("clusterMatching for SL" + cls1.superlayer()).fill(1);
            Cluster cls2 = map_cls1_cls2_matched.get(cls1);
            int numMatchedHits = cls1.clusterMatchedHits(cls2);
            histoGroupMatchedHitRatioMatchedClusterMatching.getH2F("matchedHitRatioMatchedClusterMatching for SL" + cls1.superlayer()).fill((double)numMatchedHits/cls1.size(), (double)numMatchedHits/cls2.size());
        }        
        for(Cluster cls1 : clsListExtraSample1){
            histoGroupClusterMatching.getH1F("clusterMatching for SL" + cls1.superlayer()).fill(0);
            //addDemoGroup(localEvent1, localEvent2, cls1.sector(), "extra1");
        }
        for(Cluster cls2 : clsListExtraSample2){
            histoGroupClusterMatching.getH1F("clusterMatching for SL" + cls2.superlayer()).fill(2);
            histoGroupNormalRatioExtraSample2ClusterMatching.getH1F("normalRatioExtraSample2ClusterMatching for SL" + cls2.superlayer()).fill(cls2.getRatioNormalHits());
            //addDemoGroup(localEvent1, localEvent2, cls2.sector(), "extra2");
        }                          
    }
    
    public void postEventProcess(){
        HistoGroup histoGroupClustering = histoGroupMap.get("clustering");
        
        H1F histoGroupRemainingHitRatioSLDenoisingSample2 = histoGroupClustering.getH1F("remainingHitSLDenoisingNormalSample2").histClone("remainingHitRatioSLDenoisingSample2");
        histoGroupRemainingHitRatioSLDenoisingSample2.divide(histoGroupClustering.getH1F("remainingHitSLClusteringSample2"));
        histoGroupRemainingHitRatioSLDenoisingSample2.setTitleX("SL");
        histoGroupRemainingHitRatioSLDenoisingSample2.setTitleY("Counts");
        histoGroupRemainingHitRatioSLDenoisingSample2.setLineColor(2); 
        histoGroupClustering.addDataSet(histoGroupRemainingHitRatioSLDenoisingSample2, 6);
        
        H1F histoGroupRemainingHitRatioSLClusteringSample2 = histoGroupClustering.getH1F("remainingHitSLClusteringNormalSample2").histClone("remainingHitRatioSLClusteringSample2");
        histoGroupRemainingHitRatioSLClusteringSample2.divide(histoGroupClustering.getH1F("remainingHitSLClusteringSample2"));
        histoGroupRemainingHitRatioSLClusteringSample2.setTitleX("SL");
        histoGroupRemainingHitRatioSLClusteringSample2.setTitleY("Counts");
        histoGroupRemainingHitRatioSLClusteringSample2.setLineColor(3);        
        histoGroupClustering.addDataSet(histoGroupRemainingHitRatioSLClusteringSample2, 6);  
    }                
}