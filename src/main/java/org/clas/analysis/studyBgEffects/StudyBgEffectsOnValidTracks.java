package org.clas.analysis.studyBgEffects;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import javax.swing.JFrame;
import org.jlab.groot.data.H1F;
import org.jlab.groot.data.H2F;
import org.jlab.groot.data.DataVector;
import org.jlab.groot.data.GraphErrors;
import org.jlab.groot.graphics.EmbeddedCanvasTabbed;
import org.jlab.jnp.hipo4.data.Event;
import org.jlab.jnp.hipo4.data.SchemaFactory;
import org.jlab.jnp.hipo4.io.HipoReader;
import org.jlab.utils.benchmark.ProgressPrintout;
import org.jlab.utils.options.OptionParser;
import org.jlab.groot.group.DataGroup;

import org.clas.utilities.Constants;
import org.clas.element.RunConfig;
import org.clas.element.Track;
import org.clas.element.AICandidate;
import org.clas.element.Cluster;
import org.clas.element.Hit;
import org.clas.element.TDC;
import org.clas.element.URWellHit;
import org.clas.element.URWellCluster;
import org.clas.element.URWellCross;
import org.clas.graph.HistoGroup;
import org.clas.graph.TrackHistoGroup;
import org.clas.physicsEvent.BasePhysicsEvent;
import org.clas.physicsEvent.EventForLumiScan;
import org.clas.analysis.BaseAnalysis;
import org.clas.reader.Banks;
import org.clas.reader.LocalEvent;
import org.clas.demo.DemoSuperlayer;
import org.clas.demo.DemoSector;
import org.clas.fit.ClusterFitLC;

/**
 *
 * @author Tongtong Cao
 */
public class StudyBgEffectsOnValidTracks extends BaseAnalysis {
    private double ratioNormalHitsCut = 0.;
    private int[] numAllNormalHits = new int[6];
    private int[] numLostClustersWith3MoreNormalHits = new int[6];
    private int[] numMatchedClustersNotAllNormalHits = new int[6];
    
    private List<Integer> numLostEvents = new ArrayList();   
    
    // histogram group for post-event process
    private HistoGroup histoGroupEstimatePotentialRestorableClusters = new HistoGroup("estimatePotentialRestorableClusters", 1, 2);;
    
    public StudyBgEffectsOnValidTracks() {}

    @Override
    public void createHistoGroupMap() {  
        ////// Denoising and clustering levels        
        HistoGroup histoGroupClusterMatchingStatus = new HistoGroup("clusterMatchingStatus", 2, 3);        
        HistoGroup histoGroupNormalHitsLeftForLostCluters= new HistoGroup("normalHitsLeftForLostCluters", 2, 3);
        HistoGroup histoGroupProbVsNHitsFor3MoreNormalHitsLeftForLostCluters = new HistoGroup("probVsNHitsFor3MoreNormalHitsLeftForLostCluters", 2, 3);
        HistoGroup histoGroupMatchedHitRatio = new HistoGroup("matchedHitRatio", 2, 3);
        HistoGroup histoGroupIfAllNormalHitsMatchedCluster = new HistoGroup("ifAllNormalHitsMatchedCluster", 2, 3);
        HistoGroup histoGroupClusterSituationSample2 = new HistoGroup("clusterSituationSample2", 2, 3);
        HistoGroup histoGroupNormalHitRatioForNotAllNormalHitsMatchedCluster = new HistoGroup("normalHitRatioForNotAllNormalHitsMatchedCluster", 2, 3);
        HistoGroup histoGroupNormalHitRatioVsSizeForNotAllNormalHitsMatchedCluster = new HistoGroup("normalHitRatioVsSizeForNotAllNormalHitsMatchedCluster", 2, 3);
        HistoGroup histoGroupProbVsNormalHitRatioForNotAllNormalHitsMatchedCluster = new HistoGroup("probVsNormalHitRatioForNotAllNormalHitsMatchedCluster", 2, 3);
                
        for (int i = 0; i < 6; i++) {
            H1F h1_clusterMatchingStatus = new H1F("clusterMatchingStatus for SL" + Integer.toString(i + 1),
                    "if matched hits exist for SL" + Integer.toString(i + 1), 2, -0.5, 1.5);
            h1_clusterMatchingStatus.setTitleX("if matched hits exist");
            h1_clusterMatchingStatus.setTitleY("Counts");
            histoGroupClusterMatchingStatus.addDataSet(h1_clusterMatchingStatus, i);            
            
            H1F h1_normalHitsLeftForLostCluters = new H1F("normalHitsLeftForLostCluters for SL" + Integer.toString(i + 1),
                    "normal hits left after denoising for lost clusters at SL" + Integer.toString(i + 1), 13, -0.5, 12.5);
            h1_normalHitsLeftForLostCluters.setTitleX("# of hits in sp2 for lost cls in sp1");
            h1_normalHitsLeftForLostCluters.setTitleY("Counts");
            histoGroupNormalHitsLeftForLostCluters.addDataSet(h1_normalHitsLeftForLostCluters, i);
                        
            
            H2F h2_probVsNHitsFor3MoreNormalHitsLeftForLostCluters = new H2F("probVsNHitsFor3MoreNormalHitsLeftForLostCluters for SL" + Integer.toString(i + 1),
                    "prob vs nLayers for >=3 normal hits left after denoising for lost clusters for SL" + Integer.toString(i + 1), 12, 0.5, 12.5, 101, 0, 1.01);
            h2_probVsNHitsFor3MoreNormalHitsLeftForLostCluters.setTitleX("# of hits");
            h2_probVsNHitsFor3MoreNormalHitsLeftForLostCluters.setTitleY("prob of linear fitting");                                    
            histoGroupProbVsNHitsFor3MoreNormalHitsLeftForLostCluters.addDataSet(h2_probVsNHitsFor3MoreNormalHitsLeftForLostCluters, i);
            
            H2F h2_matchedHitRatio = new H2F("matchedHitRatio for SL" + Integer.toString(i + 1),
                    "ratio of matched hits for SL" + Integer.toString(i + 1), 30, 0, 1.01, 30, 0, 1.01);
            h2_matchedHitRatio.setTitleX("ratio of matched Hits to all for sample 1");
            h2_matchedHitRatio.setTitleY("ratio of matched Hits to all for sample 2");
            histoGroupMatchedHitRatio.addDataSet(h2_matchedHitRatio, i); 
            
            
            
            H1F h1_ifAllNormalHitsMatchedCluster = new H1F("ifAllNormalHitsMatchedCluster for SL" + Integer.toString(i + 1),
                    "if all normal hits for matched clusters at SL" + Integer.toString(i + 1), 2, -0.5, 1.5);
            h1_ifAllNormalHitsMatchedCluster.setTitleX("if all normal hits");
            h1_ifAllNormalHitsMatchedCluster.setTitleY("Counts");
            histoGroupIfAllNormalHitsMatchedCluster.addDataSet(h1_ifAllNormalHitsMatchedCluster, i);
            
            H1F h1_clusterSituationSample2 = new H1F("clusterSituationSample2 for SL" + Integer.toString(i + 1),
                    "cluster situation at SL" + Integer.toString(i + 1), 3,0.5, 3.5);
            h1_clusterSituationSample2.setTitleX("cluster situation");
            h1_clusterSituationSample2.setTitleY("counts");
            histoGroupClusterSituationSample2.addDataSet(h1_clusterSituationSample2, i);             
            
            H1F h1_normalHitRatioForNotAllNormalHitsMatchedCluster = new H1F("normalHitRatioForNotAllNormalHitsMatchedCluster for SL" + Integer.toString(i + 1),
                    "normal hit ratio for not-all-normal-hits matched clusters at SL" + Integer.toString(i + 1), 101, 0, 1.01);
            h1_normalHitRatioForNotAllNormalHitsMatchedCluster.setTitleX("normal hit ratio");
            h1_normalHitRatioForNotAllNormalHitsMatchedCluster.setTitleY("Counts");
            histoGroupNormalHitRatioForNotAllNormalHitsMatchedCluster.addDataSet(h1_normalHitRatioForNotAllNormalHitsMatchedCluster, i);
            
            H2F h2_normalHitRatioVsSizeForNotAllNormalHitsMatchedCluster = new H2F("normalHitRatioVsSizeForNotAllNormalHitsMatchedCluster for SL" + Integer.toString(i + 1),
                    "normalHitRatio vs size for not-all-normal-hits matched cluster at SL" + Integer.toString(i + 1), 12, 0.5, 12.5, 101, 0, 1.01);
            h2_normalHitRatioVsSizeForNotAllNormalHitsMatchedCluster.setTitleX("size");
            h2_normalHitRatioVsSizeForNotAllNormalHitsMatchedCluster.setTitleY("ratio of normal hits");
            histoGroupNormalHitRatioVsSizeForNotAllNormalHitsMatchedCluster.addDataSet(h2_normalHitRatioVsSizeForNotAllNormalHitsMatchedCluster, i);      
            
            
            H2F h2_probVsNormalHitRatioForNotAllNormalHitsMatchedCluster = new H2F("probVsNormalHitRatioForNotAllNormalHitsMatchedCluster for SL" + Integer.toString(i + 1),
                    "prob vs normalHitRatio for not-all-normal-hits matched cluster at SL" + Integer.toString(i + 1), 101,0, 1.01, 101, 0, 1.01);
            h2_probVsNormalHitRatioForNotAllNormalHitsMatchedCluster.setTitleX("ratio of normal hits");
            h2_probVsNormalHitRatioForNotAllNormalHitsMatchedCluster.setTitleY("prob");
            histoGroupProbVsNormalHitRatioForNotAllNormalHitsMatchedCluster.addDataSet(h2_probVsNormalHitRatioForNotAllNormalHitsMatchedCluster, i);               
        }
        
        histoGroupMap.put(histoGroupClusterMatchingStatus.getName(), histoGroupClusterMatchingStatus);   
        
        HistoGroup histoGroupLostClusters= new HistoGroup("lostClusters", 1, 1);
        H1F h1_lostClusters = new H1F("lostClusters", "lost clusters", 6, 0.5, 6.5);
        h1_lostClusters.setTitleX("SL");
        h1_lostClusters.setTitleY("Counts");
        histoGroupLostClusters.addDataSet(h1_lostClusters, 0);
        histoGroupMap.put(histoGroupLostClusters.getName(), histoGroupLostClusters);                 
        histoGroupMap.put(histoGroupNormalHitsLeftForLostCluters.getName(), histoGroupNormalHitsLeftForLostCluters);                                       
        histoGroupMap.put(histoGroupProbVsNHitsFor3MoreNormalHitsLeftForLostCluters.getName(), histoGroupProbVsNHitsFor3MoreNormalHitsLeftForLostCluters);  
        
        
        HistoGroup histoGroupMatchedClusters= new HistoGroup("matchedClusters", 1, 1);
        H1F h1_matchedClusters = new H1F("matchedClusters", "matched clusters", 6, 0.5, 6.5);
        h1_matchedClusters.setTitleX("SL");
        h1_matchedClusters.setTitleY("Counts");
        histoGroupMatchedClusters.addDataSet(h1_matchedClusters, 0);
        histoGroupMap.put(histoGroupMatchedClusters.getName(), histoGroupMatchedClusters);
        histoGroupMap.put(histoGroupMatchedHitRatio.getName(), histoGroupMatchedHitRatio);  
        histoGroupMap.put(histoGroupIfAllNormalHitsMatchedCluster.getName(), histoGroupIfAllNormalHitsMatchedCluster);   
        histoGroupMap.put(histoGroupNormalHitRatioForNotAllNormalHitsMatchedCluster.getName(), histoGroupNormalHitRatioForNotAllNormalHitsMatchedCluster);  
        histoGroupMap.put(histoGroupClusterSituationSample2.getName(), histoGroupClusterSituationSample2);         
        histoGroupMap.put(histoGroupNormalHitRatioVsSizeForNotAllNormalHitsMatchedCluster.getName(), histoGroupNormalHitRatioVsSizeForNotAllNormalHitsMatchedCluster);          
        histoGroupMap.put(histoGroupProbVsNormalHitRatioForNotAllNormalHitsMatchedCluster.getName(), histoGroupProbVsNormalHitRatioForNotAllNormalHitsMatchedCluster);  
                
        histoGroupMap.put(histoGroupEstimatePotentialRestorableClusters.getName(), histoGroupEstimatePotentialRestorableClusters);   
        
        HistoGroup histoGroupClusterLostDenoising= new HistoGroup("clusterLostDenoising", 2, 4);
        H1F h1_numClustersOnValidTracks = new H1F("numClustersOnValidTracks", "5-cluster and 6-cluster tracks", 2, 4.5, 6.5);
        h1_numClustersOnValidTracks.setTitleX("5-cluster and 6-cluster tracks");
        h1_numClustersOnValidTracks.setTitleY("Counts");
        histoGroupClusterLostDenoising.addDataSet(h1_numClustersOnValidTracks, 0);    
        H1F h1_numLostClusterLostDenoising = new H1F("numLostClusterLostDenoising", "# of lost clusters in denoising", 7, -0.5, 6.5);
        h1_numLostClusterLostDenoising.setTitleX("# of lost clusters on valid tracks");
        h1_numLostClusterLostDenoising.setTitleY("Counts");
        histoGroupClusterLostDenoising.addDataSet(h1_numLostClusterLostDenoising, 1);           
        H1F h1_numLostClustersOn6ClustersValidTracksDenoising = new H1F("numLostClustersOn6ClustersValidTracksDenoising", "# of lost clusters on 6-clusters valid tracks in denoising", 7, -0.5, 6.5);
        h1_numLostClustersOn6ClustersValidTracksDenoising.setTitleX("# of lost clusters on 6-clusters valid tracks");
        h1_numLostClustersOn6ClustersValidTracksDenoising.setTitleY("Counts");
        histoGroupClusterLostDenoising.addDataSet(h1_numLostClustersOn6ClustersValidTracksDenoising, 2);          
        H1F h1_numLostClustersOn5ClustersValidTracksDenoising = new H1F("numLostClustersOn5ClustersValidTracksDenoising", "# of lost clusters on 5-clusters valid tracks in denoising", 7, -0.5, 6.5);
        h1_numLostClustersOn5ClustersValidTracksDenoising.setTitleX("# of lost clusters on 5-clusters valid tracks");
        h1_numLostClustersOn5ClustersValidTracksDenoising.setTitleY("Counts");
        histoGroupClusterLostDenoising.addDataSet(h1_numLostClustersOn5ClustersValidTracksDenoising, 3);         
        H1F h1_5or6ClustersLeftDenoising = new H1F("5or6ClustersLeftDenoising", "5 or 6 clusters left after denoising", 2, 4.5, 6.5);
        h1_5or6ClustersLeftDenoising.setTitleX("5 or 6 clusters left after denoising");
        h1_5or6ClustersLeftDenoising.setTitleY("Counts");   
        histoGroupClusterLostDenoising.addDataSet(h1_5or6ClustersLeftDenoising, 4);           
        histoGroupMap.put(histoGroupClusterLostDenoising.getName(), histoGroupClusterLostDenoising); 
        
        HistoGroup histoGroupClustersOnValidTracks= new HistoGroup("clustersOnValidTracks", 2, 5);     
        H1F h1_numLostClustersOnValidTracks = new H1F("numLostClustersOnValidTracks", "# of lost clusters on valid tracks", 7, -0.5, 6.5);
        h1_numLostClustersOnValidTracks.setTitleX("# of lost clusters on valid tracks");
        h1_numLostClustersOnValidTracks.setTitleY("Counts");
        histoGroupClustersOnValidTracks.addDataSet(h1_numLostClustersOnValidTracks, 1);           
        H1F h1_numLostClustersOn6ClustersValidTracks = new H1F("numLostClustersOn6ClustersValidTracks", "# of lost clusters on 6-clusters valid tracks", 7, -0.5, 6.5);
        h1_numLostClustersOn6ClustersValidTracks.setTitleX("# of lost clusters on 6-clusters valid tracks");
        h1_numLostClustersOn6ClustersValidTracks.setTitleY("Counts");
        histoGroupClustersOnValidTracks.addDataSet(h1_numLostClustersOn6ClustersValidTracks, 2);          
        H1F h1_numLostClustersOn5ClustersValidTracks = new H1F("numLostClustersOn5ClustersValidTracks", "# of lost clusters on 5-clusters valid tracks", 7, -0.5, 6.5);
        h1_numLostClustersOn5ClustersValidTracks.setTitleX("# of lost clusters on 5-clusters valid tracks");
        h1_numLostClustersOn5ClustersValidTracks.setTitleY("Counts");
        histoGroupClustersOnValidTracks.addDataSet(h1_numLostClustersOn5ClustersValidTracks, 3);                  
        H1F h1_SLDistFor1LostClusterOnValidTracks = new H1F("SLDistFor1LostClusterOnValidTracks", "SL distribution for 1 lost cluster on valid tracks", 6, 0.5, 6.5);
        h1_SLDistFor1LostClusterOnValidTracks.setTitleX("SL");
        h1_SLDistFor1LostClusterOnValidTracks.setTitleY("Counts");
        histoGroupClustersOnValidTracks.addDataSet(h1_SLDistFor1LostClusterOnValidTracks, 4);        
        H1F h1_SLDistFor2LostClustersOnValidTracks = new H1F("SLDistFor2LostClustersOnValidTracks", "SL distribution for 2 lost clusters on valid tracks", 15, 0.5, 15.5);
        h1_SLDistFor2LostClustersOnValidTracks.setTitleX("type for 2 lost clusters");
        h1_SLDistFor2LostClustersOnValidTracks.setTitleY("Counts");
        histoGroupClustersOnValidTracks.addDataSet(h1_SLDistFor2LostClustersOnValidTracks, 5);         
        H1F h1_5or6MatchedClustersWithValidTracks = new H1F("5or6MatchedClustersWithValidTracks", "5 or 6 matched clusters with valid tracks", 2, 4.5, 6.5);
        h1_5or6MatchedClustersWithValidTracks.setTitleX("5 or 6 matched clusters with valid tracks");
        h1_5or6MatchedClustersWithValidTracks.setTitleY("Counts");   
        histoGroupClustersOnValidTracks.addDataSet(h1_5or6MatchedClustersWithValidTracks, 6);           
        H1F h1_numNoiseClustersfor6MatchedClustersWithValidTracks = new H1F("numNoiseClustersfor6MatchedClustersWithValidTracks", "# of noise clusters for 6 matched clusters with valid tracks", 7, -0.5, 6.5);
        h1_numNoiseClustersfor6MatchedClustersWithValidTracks.setTitleX("# of noise clusters for 6 matched clusters with valid tracks");
        h1_numNoiseClustersfor6MatchedClustersWithValidTracks.setTitleY("Counts");
        histoGroupClustersOnValidTracks.addDataSet(h1_numNoiseClustersfor6MatchedClustersWithValidTracks, 8);         
        H1F h1_numNoiseClustersfor5MatchedClustersWithValidTracks = new H1F("numNoiseClustersfor5MatchedClustersWithValidTracks", "# of noise clusters for 5 matched clusters with valid tracks", 7, -0.5, 6.5);
        h1_numNoiseClustersfor5MatchedClustersWithValidTracks.setTitleX("# of noise clusters for 5 matched clusters with valid tracks");
        h1_numNoiseClustersfor5MatchedClustersWithValidTracks.setTitleY("Counts");
        histoGroupClustersOnValidTracks.addDataSet(h1_numNoiseClustersfor5MatchedClustersWithValidTracks, 9);                 
        histoGroupMap.put(histoGroupClustersOnValidTracks.getName(), histoGroupClustersOnValidTracks);
        
        HistoGroup histoGroupNormalHitRatioNoiseClustersfor5or6MatchedClustersWithValidTracks= new HistoGroup("normalHitRatioNoiseClustersfor5or6MatchedClustersWithValidTracks", 2, 3);
        for (int i = 0; i < 6; i++) {
            H1F h1_normalHitRatioNoiseClustersfor5or6MatchedClustersWithValidTracks = new H1F("normalHitRatioNoiseClustersfor5or6MatchedClustersWithValidTracks for SL" + Integer.toString(i + 1),
                    "normal hit ratio of noise clusters for 5 or 6 matched clusters with valid tracks at SL" + Integer.toString(i + 1), 101, 0, 1.01);
            h1_normalHitRatioNoiseClustersfor5or6MatchedClustersWithValidTracks.setTitleX("normal hit ratio");
            h1_normalHitRatioNoiseClustersfor5or6MatchedClustersWithValidTracks.setTitleY("Counts");
            histoGroupNormalHitRatioNoiseClustersfor5or6MatchedClustersWithValidTracks.addDataSet(h1_normalHitRatioNoiseClustersfor5or6MatchedClustersWithValidTracks, i);
        }
        histoGroupMap.put(histoGroupNormalHitRatioNoiseClustersfor5or6MatchedClustersWithValidTracks.getName(), histoGroupNormalHitRatioNoiseClustersfor5or6MatchedClustersWithValidTracks);
        
        ////// AI prediction level
        HistoGroup histoGroupAICandidates= new HistoGroup("aICandidates", 2, 4);
        H1F h1_numClustersAICandidates = new H1F("numClustersAICandidates", "# of clusters in HB tracks", 2, 4.5, 6.5);
        h1_numClustersAICandidates.setTitleX("# of clusters in AI candidates");
        h1_numClustersAICandidates.setTitleY("Counts");
        histoGroupAICandidates.addDataSet(h1_numClustersAICandidates, 0);          
        H1F h1_numMatchedClustersAICandidates = new H1F("numMatchedClustersAICandidates", "# of matched clusters in AI candidates", 7, -0.5, 6.5);
        h1_numMatchedClustersAICandidates.setTitleX("# of matched clusters in AI candidates");
        h1_numMatchedClustersAICandidates.setTitleY("Counts");
        histoGroupAICandidates.addDataSet(h1_numMatchedClustersAICandidates, 1);        
        H1F h1_numNomatchedClustersAICandidates = new H1F("numNomatchedClustersAICandidates", "# of nomatched clusters in AI candidates", 7, -0.5, 6.5);
        h1_numNomatchedClustersAICandidates.setTitleX("# of nomatched clusters in AI candidates");
        h1_numNomatchedClustersAICandidates.setTitleY("Counts");
        histoGroupAICandidates.addDataSet(h1_numNomatchedClustersAICandidates, 2);            
        H2F h2_numMatchedAICandidatesVsNumMatchedClustering = new H2F("numMatchedAICandidatesVsNumMatchedClustering", "# of matched clusters in AI candidates vs # of matched clusters in clustering", 7, -0.5, 6.5, 7, -0.5, 6.5);
        h2_numMatchedAICandidatesVsNumMatchedClustering.setTitleX("# of matched clusters in clustering");
        h2_numMatchedAICandidatesVsNumMatchedClustering.setTitleY("# of matched clusters in AI candidates");
        histoGroupAICandidates.addDataSet(h2_numMatchedAICandidatesVsNumMatchedClustering, 3);  
        H2F h2_numNomatchedAICandidatesVsNumMatchedClustering = new H2F("numNomatchedAICandidatesVsNumMatchedClustering", "# of nomatched clusters in AI candidates vs # of matched clusters in clustering", 7, -0.5, 6.5, 7, -0.5, 6.5);
        h2_numNomatchedAICandidatesVsNumMatchedClustering.setTitleX("# of matched clusters in clustering");
        h2_numNomatchedAICandidatesVsNumMatchedClustering.setTitleY("# of nomatched clusters in AI candidates");
        histoGroupAICandidates.addDataSet(h2_numNomatchedAICandidatesVsNumMatchedClustering, 4);         
        H1F h1_numClustersFullyMatchedAICandidates = new H1F("numClustersFullyMatchedAICandidates", "# of clusters in AI candidates for fully matched", 2, 4.5, 6.5);
        h1_numClustersFullyMatchedAICandidates.setTitleX("# of clusters in AI candidates for fully matched");
        h1_numClustersFullyMatchedAICandidates.setTitleY("Counts");
        histoGroupAICandidates.addDataSet(h1_numClustersFullyMatchedAICandidates, 5);        
        H1F h1_numNoiseClustersfor6ClustersFullyMatchedAICandidates = new H1F("numNoiseClustersfor6ClustersFullyMatchedAICandidates", "# of mixed-clusters for fully matched 6-cluster AI candidates", 7, -0.5, 6.5);
        h1_numNoiseClustersfor6ClustersFullyMatchedAICandidates.setTitleX("# of mixed-clusters for fully matched 6-cluster AI candidates");
        h1_numNoiseClustersfor6ClustersFullyMatchedAICandidates.setTitleY("Counts");
        histoGroupAICandidates.addDataSet(h1_numNoiseClustersfor6ClustersFullyMatchedAICandidates, 6);           
        H1F h1_numNoiseClustersfor5ClustersFullyMatchedAICandidates = new H1F("numNoiseClustersfor5ClustersFullyMatchedAICandidates", "# of mixed-clusters for fully matched 5-cluster AI candidates", 7, -0.5, 6.5);
        h1_numNoiseClustersfor5ClustersFullyMatchedAICandidates.setTitleX("# of mixed-clusters for fully matched 5-cluster AI candidates");
        h1_numNoiseClustersfor5ClustersFullyMatchedAICandidates.setTitleY("Counts");
        histoGroupAICandidates.addDataSet(h1_numNoiseClustersfor5ClustersFullyMatchedAICandidates, 7);         
        histoGroupMap.put(histoGroupAICandidates.getName(), histoGroupAICandidates);
        
        HistoGroup histoGroupNormalHitRatioNoiseClustersforFullyMatchedAICandidates= new HistoGroup("normalHitRatioNoiseClustersforFullyMatchedAICandidates", 2, 3);
        for (int i = 0; i < 6; i++) {
            H1F h1_normalHitRatioNoiseClustersInFullyMatchedAICandidates = new H1F("normalHitRatioNoiseClustersInFullyMatchedAICandidates for SL" + Integer.toString(i + 1),
                    "normal hit ratio of noise clusters in fully matched AI candidates" + Integer.toString(i + 1), 101, 0, 1.01);
            h1_normalHitRatioNoiseClustersInFullyMatchedAICandidates.setTitleX("normal hit ratio");
            h1_normalHitRatioNoiseClustersInFullyMatchedAICandidates.setTitleY("Counts");
            histoGroupNormalHitRatioNoiseClustersforFullyMatchedAICandidates.addDataSet(h1_normalHitRatioNoiseClustersInFullyMatchedAICandidates, i);
        }
        histoGroupMap.put(histoGroupNormalHitRatioNoiseClustersforFullyMatchedAICandidates.getName(), histoGroupNormalHitRatioNoiseClustersforFullyMatchedAICandidates);        

        ////// HB tracking level
        HistoGroup histoGroupHBTracks= new HistoGroup("HBTracks", 2, 4);
        H1F h1_numClustersHBTracks = new H1F("numClustersHBTracks", "# of clusters in HB tracks", 2, 4.5, 6.5);
        h1_numClustersHBTracks.setTitleX("# of clusters in HB tracks");
        h1_numClustersHBTracks.setTitleY("Counts");
        histoGroupHBTracks.addDataSet(h1_numClustersHBTracks, 0);          
        H1F h1_numMatchedClustersHBTracks = new H1F("numMatchedClustersHBTracks", "# of matched clusters in HB tracks", 7, -0.5, 6.5);
        h1_numMatchedClustersHBTracks.setTitleX("# of matched clusters in HB tracks");
        h1_numMatchedClustersHBTracks.setTitleY("Counts");
        histoGroupHBTracks.addDataSet(h1_numMatchedClustersHBTracks, 1);        
        H1F h1_numNomatchedClustersHBTracks = new H1F("numNomatchedClustersHBTracks", "# of nomatched clusters in HB tracks", 7, -0.5, 6.5);
        h1_numNomatchedClustersHBTracks.setTitleX("# of nomatched clusters in HB tracks");
        h1_numNomatchedClustersHBTracks.setTitleY("Counts");
        histoGroupHBTracks.addDataSet(h1_numNomatchedClustersHBTracks, 2);                 
        H2F h2_numMatchedHBTracksVsNumMatchedClustering = new H2F("numMatchedHBTracksVsNumMatchedClustering", "# of matched clusters in HB tracks vs # of matched clusters in clustering", 7, -0.5, 6.5, 7, -0.5, 6.5);
        h2_numMatchedHBTracksVsNumMatchedClustering.setTitleX("# of matched clusters in clustering");
        h2_numMatchedHBTracksVsNumMatchedClustering.setTitleY("# of matched clusters in HB tracks");
        histoGroupHBTracks.addDataSet(h2_numMatchedHBTracksVsNumMatchedClustering, 3);
        H2F h2_numNomatchedHBTracksVsNumMatchedClustering = new H2F("numNomatchedHBTracksVsNumMatchedClustering", "# of nomatched clusters in HB tracks vs # of matched clusters in clustering", 7, -0.5, 6.5, 7, -0.5, 6.5);
        h2_numNomatchedHBTracksVsNumMatchedClustering.setTitleX("# of matched clusters in clustering");
        h2_numNomatchedHBTracksVsNumMatchedClustering.setTitleY("# of nomatched clusters in HB tracks");
        histoGroupHBTracks.addDataSet(h2_numNomatchedHBTracksVsNumMatchedClustering, 4);  
        H1F h1_numClustersFullyMatchedHBTracks = new H1F("numClustersFullyMatchedHBTracks", "# of clusters in HB tracks for fully matched", 2, 4.5, 6.5);
        h1_numClustersFullyMatchedHBTracks.setTitleX("# of clusters in HB tracks for fully matched");
        h1_numClustersFullyMatchedHBTracks.setTitleY("Counts");
        histoGroupHBTracks.addDataSet(h1_numClustersFullyMatchedHBTracks, 5);        
        H1F h1_numNoiseClustersfor6ClustersFullyMatchedHBTracks = new H1F("numNoiseClustersfor6ClustersFullyMatchedHBTracks", "# of mixed-clusters for fully matched 6-cluster HB tracks", 7, -0.5, 6.5);
        h1_numNoiseClustersfor6ClustersFullyMatchedHBTracks.setTitleX("# of mixed-clusters for fully matched 6-cluster HB tracks");
        h1_numNoiseClustersfor6ClustersFullyMatchedHBTracks.setTitleY("Counts");
        histoGroupHBTracks.addDataSet(h1_numNoiseClustersfor6ClustersFullyMatchedHBTracks, 6);           
        H1F h1_numNoiseClustersfor5ClustersFullyMatchedHBTracks = new H1F("numNoiseClustersfor5ClustersFullyMatchedHBTracks", "# of mixed-clusters for fully matched 5-cluster HB tracks", 7, -0.5, 6.5);
        h1_numNoiseClustersfor5ClustersFullyMatchedHBTracks.setTitleX("# of mixed-clusters for fully matched 5-cluster HB tracks");
        h1_numNoiseClustersfor5ClustersFullyMatchedHBTracks.setTitleY("Counts");
        histoGroupHBTracks.addDataSet(h1_numNoiseClustersfor5ClustersFullyMatchedHBTracks, 7);         
        histoGroupMap.put(histoGroupHBTracks.getName(), histoGroupHBTracks);
        
        HistoGroup histoGroupNormalHitRatioNoiseClustersforFullyMatchedHBTracks= new HistoGroup("normalHitRatioNoiseClustersforFullyMatchedHBTracks", 2, 3);
        for (int i = 0; i < 6; i++) {
            H1F h1_normalHitRatioNoiseClustersInFullyMatchedHBTracks = new H1F("normalHitRatioNoiseClustersInFullyMatchedHBTracks for SL" + Integer.toString(i + 1),
                    "normal hit ratio of noise clusters in fully matched HB tracks" + Integer.toString(i + 1), 101, 0, 1.01);
            h1_normalHitRatioNoiseClustersInFullyMatchedHBTracks.setTitleX("normal hit ratio");
            h1_normalHitRatioNoiseClustersInFullyMatchedHBTracks.setTitleY("Counts");
            histoGroupNormalHitRatioNoiseClustersforFullyMatchedHBTracks.addDataSet(h1_normalHitRatioNoiseClustersInFullyMatchedHBTracks, i);
        }
        histoGroupMap.put(histoGroupNormalHitRatioNoiseClustersforFullyMatchedHBTracks.getName(), histoGroupNormalHitRatioNoiseClustersforFullyMatchedHBTracks);  
                                      
        HistoGroup histoGroupHBTrackComp= new HistoGroup("HBTrackComp", 3, 4);
        H2F h2_chi2overndfHBTrackComp = new H2F("chi2overndfHBTrackComp", "Comparison for chi2/ndf", 100, 0, 100, 100, 0, 100);
        h2_chi2overndfHBTrackComp.setTitleX("chi2/ndf in sample1");
        h2_chi2overndfHBTrackComp.setTitleY("chi2/ndf in sample2");
        histoGroupHBTrackComp.addDataSet(h2_chi2overndfHBTrackComp, 0);        
        H2F h2_chi2pidHBTrackComp = new H2F("chi2pidHBTrackComp", "Comparison for chi2pid", 100, -15, 15, 100, -15, 15);
        h2_chi2pidHBTrackComp.setTitleX("chi2pid in sample1");
        h2_chi2pidHBTrackComp.setTitleY("chi2pid in sample2");
        histoGroupHBTrackComp.addDataSet(h2_chi2pidHBTrackComp, 1);            
        H2F h2_pHBTrackComp = new H2F("pHBTrackComp", "Comparison for p", 100, 0, 12, 100, 0, 12);
        h2_pHBTrackComp.setTitleX("p in sample1 (GeV/c)");
        h2_pHBTrackComp.setTitleY("p in sample2 (GeV/c)");
        histoGroupHBTrackComp.addDataSet(h2_pHBTrackComp, 3); 
        H2F h2_thetaHBTrackComp = new H2F("thetaHBTrackComp", "Comparison for theta", 100, 0, 1, 100, 0, 1);
        h2_thetaHBTrackComp.setTitleX("theta in sample1 (rad)");
        h2_thetaHBTrackComp.setTitleY("theta in sample2 (rad)");
        histoGroupHBTrackComp.addDataSet(h2_thetaHBTrackComp, 4); 
        H2F h2_phiHBTrackComp = new H2F("phiHBTrackComp", "Comparison for phi", 100, -Math.PI, Math.PI, 100, -Math.PI, Math.PI);
        h2_phiHBTrackComp.setTitleX("phi in sample1 (rad)");
        h2_phiHBTrackComp.setTitleY("phi in sample2 (rad)");
        histoGroupHBTrackComp.addDataSet(h2_phiHBTrackComp, 5);         
        H2F h2_vxHBTrackComp = new H2F("vxHBTrackComp", "Comparison for vx", 100, -25, 25, 100, -25, 25);
        h2_vxHBTrackComp.setTitleX("vx in sample1 (cm)");
        h2_vxHBTrackComp.setTitleY("vx in sample2 (cm)");
        histoGroupHBTrackComp.addDataSet(h2_vxHBTrackComp, 6);         
        H2F h2_vyHBTrackComp = new H2F("vyHBTrackComp", "Comparison for vy", 100, -25, 25, 100, -25, 25);
        h2_vyHBTrackComp.setTitleX("vy in sample1 (cm)");
        h2_vyHBTrackComp.setTitleY("vy in sample2 (cm)");
        histoGroupHBTrackComp.addDataSet(h2_vyHBTrackComp, 7); 
        H2F h2_vzHBTrackComp = new H2F("vzHBTrackComp", "Comparison for vz", 100, -25, 25, 100, -25, 25);
        h2_vzHBTrackComp.setTitleX("vz in sample1 (cm)");
        h2_vzHBTrackComp.setTitleY("vz in sample2 (cm)");
        histoGroupHBTrackComp.addDataSet(h2_vzHBTrackComp, 8);  
        H2F h2_pidHBTrackComp = new H2F("pidHBTrackComp", "Comparison for pid", 50, -25, 25, 50, -25, 25);
        h2_pidHBTrackComp.setTitleX("pid in sample1");
        h2_pidHBTrackComp.setTitleY("pid in sample2");
        histoGroupHBTrackComp.addDataSet(h2_pidHBTrackComp, 9);         
        H1F h1_pidStatusHBTrack = new H1F("pidStatusHBTrack", "if pid the same", 2, -0.5, 1.5);
        h1_pidStatusHBTrack.setTitleX("pid status");
        h1_pidStatusHBTrack.setTitleY("counts");
        histoGroupHBTrackComp.addDataSet(h1_pidStatusHBTrack, 10);
        H1F h1_ratioNormalHitsDiffPIdHBTrack = new H1F("ratioNormalHitsDiffPIdHBTrack", "ratio of normal hits for cases with diff. pid", 100, 0, 1);
        h1_ratioNormalHitsDiffPIdHBTrack.setTitleX("ratio of normal hits");
        h1_ratioNormalHitsDiffPIdHBTrack.setTitleY("counts");
        histoGroupHBTrackComp.addDataSet(h1_ratioNormalHitsDiffPIdHBTrack, 11);        
        histoGroupMap.put(histoGroupHBTrackComp.getName(), histoGroupHBTrackComp); 
        
        HistoGroup histoGroupHBTrackDiff= new HistoGroup("HBTrackDiff", 3, 4);
        H1F h1_chi2overndfHBTrackDiff = new H1F("chi2overndfHBTrackDiff", "Difference of chi2/ndf", 100, -10, 10);
        h1_chi2overndfHBTrackDiff.setTitleX("Difference of chi2/ndf");
        h1_chi2overndfHBTrackDiff.setTitleY("counts");
        histoGroupHBTrackDiff.addDataSet(h1_chi2overndfHBTrackDiff, 0);        
        H1F h1_chi2pidHBTrackDiff = new H1F("chi2pidHBTrackDiff", "Difference of chi2pid", 100, -2, 2);
        h1_chi2pidHBTrackDiff.setTitleX("Difference of chi2pid");
        h1_chi2pidHBTrackDiff.setTitleY("counts");
        histoGroupHBTrackDiff.addDataSet(h1_chi2pidHBTrackDiff, 1);         
        H1F h1_pHBTrackDiff = new H1F("pHBTrackDiff", "Difference of p", 100, -0.5, 0.5);
        h1_pHBTrackDiff.setTitleX("Difference of p (GeV/c)");
        h1_pHBTrackDiff.setTitleY("counts");
        histoGroupHBTrackDiff.addDataSet(h1_pHBTrackDiff, 3); 
        H1F h1_thetaHBTrackDiff = new H1F("thetaHBTrackDiff", "Difference of theta", 100, -0.1, 0.1);
        h1_thetaHBTrackDiff.setTitleX("Difference of theta (rad)");
        h1_thetaHBTrackDiff.setTitleY("counts");
        histoGroupHBTrackDiff.addDataSet(h1_thetaHBTrackDiff, 4); 
        H1F h1_phiHBTrackDiff = new H1F("phiHBTrackDiff", "Difference of phi", 100, -0.2, 0.2);
        h1_phiHBTrackDiff.setTitleX("Difference of phi (rad)");
        h1_phiHBTrackDiff.setTitleY("counts");
        histoGroupHBTrackDiff.addDataSet(h1_phiHBTrackDiff, 5);         
        H1F h1_vxHBTrackDiff = new H1F("vxHBTrackDiff", "Difference of vx", 100, -5, 5);
        h1_vxHBTrackDiff.setTitleX("Difference of vx (cm)");
        h1_vxHBTrackDiff.setTitleY("counts");
        histoGroupHBTrackDiff.addDataSet(h1_vxHBTrackDiff, 6);         
        H1F h1_vyHBTrackDiff = new H1F("vyHBTrackDiff", "Difference of vy", 100, -10, 10);
        h1_vyHBTrackDiff.setTitleX("Difference of vy (cm)");
        h1_vyHBTrackDiff.setTitleY("counts");
        histoGroupHBTrackDiff.addDataSet(h1_vyHBTrackDiff, 7); 
        H1F h1_vzHBTrackDiff = new H1F("vzHBTrackDiff", "Difference of vz", 100, -10, 10);
        h1_vzHBTrackDiff.setTitleX("Difference of vz (cm)");
        h1_vzHBTrackDiff.setTitleY("counts");
        histoGroupHBTrackDiff.addDataSet(h1_vzHBTrackDiff, 8);                  
        histoGroupMap.put(histoGroupHBTrackDiff.getName(), histoGroupHBTrackDiff); 
        
        HistoGroup histoGroupMatchNoMatchCompHBTracks= new HistoGroup("matchNoMatchCompHBTracks", 2, 3);
        histoGroupMatchNoMatchCompHBTracks.addDataSet(h2_numMatchedHBTracksVsNumMatchedClustering, 0);
        histoGroupMatchNoMatchCompHBTracks.addDataSet(h2_numNomatchedHBTracksVsNumMatchedClustering, 1);                  
        H1F h1_chi2OverNdfMatchHBTracks = new H1F("chi2OverNdfMatchHBTracks", "chi2/ndf for matched HB tracks", 100, 0, 100);
        h1_chi2OverNdfMatchHBTracks.setTitleX("chi2/ndf");
        h1_chi2OverNdfMatchHBTracks.setTitleY("Counts");
        h1_chi2OverNdfMatchHBTracks.setLineColor(1);
        histoGroupMatchNoMatchCompHBTracks.addDataSet(h1_chi2OverNdfMatchHBTracks, 2);         
        H1F h1_chi2OverNdfNomatchHBTracks = new H1F("chi2OverNdfNomatchHBTracks", "chi2/ndf for nomatched HB tracks", 100, 0, 100);
        h1_chi2OverNdfNomatchHBTracks.setTitleX("chi2/ndf");
        h1_chi2OverNdfNomatchHBTracks.setTitleY("Counts");
        h1_chi2OverNdfNomatchHBTracks.setLineColor(2);
        histoGroupMatchNoMatchCompHBTracks.addDataSet(h1_chi2OverNdfNomatchHBTracks, 2);                                                
        H1F h1_only1NoMatchedClusterHBTracks = new H1F("only1NoMatchedClusterHBTracks", "occp. for only-1-noshared-cluster track", 2, -0.5, 1.5);
        h1_only1NoMatchedClusterHBTracks.setTitleX("occp. for only-1-nomatched-cluster track");
        h1_only1NoMatchedClusterHBTracks.setTitleY("Counts");
        histoGroupMatchNoMatchCompHBTracks.addDataSet(h1_only1NoMatchedClusterHBTracks, 3);         
        H2F h2_SLCompOnly1NoMatchedClusterHBTracks = new H2F("SLCompOnly1NoMatchedClusterHBTracks", "SL comp of no shared cluster for only-1-noshared-cluster track", 6, 0.5, 6.5, 6, 0.5, 6.5);
        h2_SLCompOnly1NoMatchedClusterHBTracks.setTitleX("SL for no shared cluster in matched clusters");
        h2_SLCompOnly1NoMatchedClusterHBTracks.setTitleY("SL for no shared cluster in track");
        histoGroupMatchNoMatchCompHBTracks.addDataSet(h2_SLCompOnly1NoMatchedClusterHBTracks, 4);  
        
        H1F h1_only1NoMatchedClusterSameSLHBTracks = new H1F("only1NoMatchedClusterSameSLHBTracks", "occp. for only-1-noshared-cluster-sameSL track", 2, -0.5, 1.5);
        h1_only1NoMatchedClusterSameSLHBTracks.setTitleX("occp. for only-1-nomatched-cluster-sameSL track");
        h1_only1NoMatchedClusterSameSLHBTracks.setTitleY("Counts");
        histoGroupMatchNoMatchCompHBTracks.addDataSet(h1_only1NoMatchedClusterSameSLHBTracks, 5); 
        
        histoGroupMap.put(histoGroupMatchNoMatchCompHBTracks.getName(), histoGroupMatchNoMatchCompHBTracks);
        
        HistoGroup histoGroup1NoMatchedCluster5ClustersHBTrackComp= new HistoGroup("1NoMatchedCluster5ClustersHBTrackComp", 3, 4);
        H2F h2_chi2overndf1NoMatchedCluster5ClustersHBTrackComp = new H2F("chi2overndf1NoMatchedCluster5ClustersHBTrackComp", "Comparison for chi2/ndf", 100, 0, 100, 100, 0, 100);
        h2_chi2overndf1NoMatchedCluster5ClustersHBTrackComp.setTitleX("chi2/ndf in sample1");
        h2_chi2overndf1NoMatchedCluster5ClustersHBTrackComp.setTitleY("chi2/ndf in sample2");
        histoGroup1NoMatchedCluster5ClustersHBTrackComp.addDataSet(h2_chi2overndf1NoMatchedCluster5ClustersHBTrackComp, 0);        
        H2F h2_chi2pid1NoMatchedCluster5ClustersHBTrackComp = new H2F("chi2pid1NoMatchedCluster5ClustersHBTrackComp", "Comparison for chi2pid", 100, -15, 15, 100, -15, 15);
        h2_chi2pid1NoMatchedCluster5ClustersHBTrackComp.setTitleX("chi2pid in sample1");
        h2_chi2pid1NoMatchedCluster5ClustersHBTrackComp.setTitleY("chi2pid in sample2");
        histoGroup1NoMatchedCluster5ClustersHBTrackComp.addDataSet(h2_chi2pid1NoMatchedCluster5ClustersHBTrackComp, 1);            
        H2F h2_p1NoMatchedCluster5ClustersHBTrackComp = new H2F("p1NoMatchedCluster5ClustersHBTrackComp", "Comparison for p", 100, 0, 12, 100, 0, 12);
        h2_p1NoMatchedCluster5ClustersHBTrackComp.setTitleX("p in sample1 (GeV/c)");
        h2_p1NoMatchedCluster5ClustersHBTrackComp.setTitleY("p in sample2 (GeV/c)");
        histoGroup1NoMatchedCluster5ClustersHBTrackComp.addDataSet(h2_p1NoMatchedCluster5ClustersHBTrackComp, 3); 
        H2F h2_theta1NoMatchedCluster5ClustersHBTrackComp = new H2F("theta1NoMatchedCluster5ClustersHBTrackComp", "Comparison for theta", 100, 0, 1, 100, 0, 1);
        h2_theta1NoMatchedCluster5ClustersHBTrackComp.setTitleX("theta in sample1 (rad)");
        h2_theta1NoMatchedCluster5ClustersHBTrackComp.setTitleY("theta in sample2 (rad)");
        histoGroup1NoMatchedCluster5ClustersHBTrackComp.addDataSet(h2_theta1NoMatchedCluster5ClustersHBTrackComp, 4); 
        H2F h2_phi1NoMatchedCluster5ClustersHBTrackComp = new H2F("phi1NoMatchedCluster5ClustersHBTrackComp", "Comparison for phi", 100, -Math.PI, Math.PI, 100, -Math.PI, Math.PI);
        h2_phi1NoMatchedCluster5ClustersHBTrackComp.setTitleX("phi in sample1 (rad)");
        h2_phi1NoMatchedCluster5ClustersHBTrackComp.setTitleY("phi in sample2 (rad)");
        histoGroup1NoMatchedCluster5ClustersHBTrackComp.addDataSet(h2_phi1NoMatchedCluster5ClustersHBTrackComp, 5);         
        H2F h2_vx1NoMatchedCluster5ClustersHBTrackComp = new H2F("vx1NoMatchedCluster5ClustersHBTrackComp", "Comparison for vx", 100, -25, 25, 100, -25, 25);
        h2_vx1NoMatchedCluster5ClustersHBTrackComp.setTitleX("vx in sample1 (cm)");
        h2_vx1NoMatchedCluster5ClustersHBTrackComp.setTitleY("vx in sample2 (cm)");
        histoGroup1NoMatchedCluster5ClustersHBTrackComp.addDataSet(h2_vx1NoMatchedCluster5ClustersHBTrackComp, 6);         
        H2F h2_vy1NoMatchedCluster5ClustersHBTrackComp = new H2F("vy1NoMatchedCluster5ClustersHBTrackComp", "Comparison for vy", 100, -25, 25, 100, -25, 25);
        h2_vy1NoMatchedCluster5ClustersHBTrackComp.setTitleX("vy in sample1 (cm)");
        h2_vy1NoMatchedCluster5ClustersHBTrackComp.setTitleY("vy in sample2 (cm)");
        histoGroup1NoMatchedCluster5ClustersHBTrackComp.addDataSet(h2_vy1NoMatchedCluster5ClustersHBTrackComp, 7); 
        H2F h2_vz1NoMatchedCluster5ClustersHBTrackComp = new H2F("vz1NoMatchedCluster5ClustersHBTrackComp", "Comparison for vz", 100, -25, 25, 100, -25, 25);
        h2_vz1NoMatchedCluster5ClustersHBTrackComp.setTitleX("vz in sample1 (cm)");
        h2_vz1NoMatchedCluster5ClustersHBTrackComp.setTitleY("vz in sample2 (cm)");
        histoGroup1NoMatchedCluster5ClustersHBTrackComp.addDataSet(h2_vz1NoMatchedCluster5ClustersHBTrackComp, 8);  
        H2F h2_pid1NoMatchedCluster5ClustersHBTrackComp = new H2F("pid1NoMatchedCluster5ClustersHBTrackComp", "Comparison for pid", 50, -25, 25, 50, -25, 25);
        h2_pid1NoMatchedCluster5ClustersHBTrackComp.setTitleX("pid in sample1");
        h2_pid1NoMatchedCluster5ClustersHBTrackComp.setTitleY("pid in sample2");
        histoGroup1NoMatchedCluster5ClustersHBTrackComp.addDataSet(h2_pid1NoMatchedCluster5ClustersHBTrackComp, 9);         
        H1F h1_pidStatus1NoMatchedCluster5ClustersHBTrack = new H1F("pidStatus1NoMatchedCluster5ClustersHBTrack", "if pid the same", 2, -0.5, 1.5);
        h1_pidStatus1NoMatchedCluster5ClustersHBTrack.setTitleX("pid status");
        h1_pidStatus1NoMatchedCluster5ClustersHBTrack.setTitleY("counts");
        histoGroup1NoMatchedCluster5ClustersHBTrackComp.addDataSet(h1_pidStatus1NoMatchedCluster5ClustersHBTrack, 10);
        H1F h1_ratioNormalHitsDiffPId1NoMatchedCluster5ClustersHBTrack = new H1F("ratioNormalHitsDiffPId1NoMatchedCluster5ClustersHBTrack", "ratio of normal hits for cases with diff. pid", 100, 0, 1);
        h1_ratioNormalHitsDiffPId1NoMatchedCluster5ClustersHBTrack.setTitleX("ratio of normal hits");
        h1_ratioNormalHitsDiffPId1NoMatchedCluster5ClustersHBTrack.setTitleY("counts");
        histoGroup1NoMatchedCluster5ClustersHBTrackComp.addDataSet(h1_ratioNormalHitsDiffPId1NoMatchedCluster5ClustersHBTrack, 11);        
        histoGroupMap.put(histoGroup1NoMatchedCluster5ClustersHBTrackComp.getName(), histoGroup1NoMatchedCluster5ClustersHBTrackComp); 
        
        HistoGroup histoGroupNoSharedClusterCompOnly1NoMatchedClusterSameSLHBTracks = new HistoGroup("noSharedClusterCompOnly1NoMatchedClusterSameSLHBTracks", 2, 3);
        H2F h2_avgWireCompNoSharedClusterCompOnly1NoMatchedClusterSameSLHBTracks = new H2F("avgWireCompNoSharedClusterCompOnly1NoMatchedClusterSameSLHBTracks", "avg wire comp", 30, 0, 120, 30, 0, 120);
        h2_avgWireCompNoSharedClusterCompOnly1NoMatchedClusterSameSLHBTracks.setTitleX("avgWire for no shared cluster in matched clusters");
        h2_avgWireCompNoSharedClusterCompOnly1NoMatchedClusterSameSLHBTracks.setTitleY("avgWire for no shared cluster in track ");
        histoGroupNoSharedClusterCompOnly1NoMatchedClusterSameSLHBTracks.addDataSet(h2_avgWireCompNoSharedClusterCompOnly1NoMatchedClusterSameSLHBTracks, 0);        
        H2F h2_slopeCompNoSharedClusterCompOnly1NoMatchedClusterSameSLHBTracks = new H2F("slopeCompNoSharedClusterCompOnly1NoMatchedClusterSameSLHBTracks", "slope comp", 30, -2, 2, 30, -2, 2);
        h2_slopeCompNoSharedClusterCompOnly1NoMatchedClusterSameSLHBTracks.setTitleX("slope for no shared cluster in matched clusters");
        h2_slopeCompNoSharedClusterCompOnly1NoMatchedClusterSameSLHBTracks.setTitleY("slope for no shared cluster in track ");
        histoGroupNoSharedClusterCompOnly1NoMatchedClusterSameSLHBTracks.addDataSet(h2_slopeCompNoSharedClusterCompOnly1NoMatchedClusterSameSLHBTracks, 2);        
        H2F h2_ratioNormalHitsCompNoSharedClusterCompOnly1NoMatchedClusterSameSLHBTracks = new H2F("ratioNormalHitsCompNoSharedClusterCompOnly1NoMatchedClusterSameSLHBTracks", "ratio of norma hits comp", 30, 0, 1.01, 30, 0, 1.01);
        h2_ratioNormalHitsCompNoSharedClusterCompOnly1NoMatchedClusterSameSLHBTracks.setTitleX("ratio of normal hits for no shared cluster in matched clusters");
        h2_ratioNormalHitsCompNoSharedClusterCompOnly1NoMatchedClusterSameSLHBTracks.setTitleY("ratio of normal hits for no shared cluster in track ");
        histoGroupNoSharedClusterCompOnly1NoMatchedClusterSameSLHBTracks.addDataSet(h2_ratioNormalHitsCompNoSharedClusterCompOnly1NoMatchedClusterSameSLHBTracks, 4);        
        H1F h1_avgWireDiffNoSharedClusterCompOnly1NoMatchedClusterSameSLHBTracks = new H1F("avgWireDiffNoSharedClusterCompOnly1NoMatchedClusterSameSLHBTracks", "avg wire diff", 30, -5, 5);
        h1_avgWireDiffNoSharedClusterCompOnly1NoMatchedClusterSameSLHBTracks.setTitleX("avg wire diff");
        h1_avgWireDiffNoSharedClusterCompOnly1NoMatchedClusterSameSLHBTracks.setTitleY("counts");
        histoGroupNoSharedClusterCompOnly1NoMatchedClusterSameSLHBTracks.addDataSet(h1_avgWireDiffNoSharedClusterCompOnly1NoMatchedClusterSameSLHBTracks, 1);        
        H1F h1_slopeDiffNoSharedClusterCompOnly1NoMatchedClusterSameSLHBTracks = new H1F("slopeDiffNoSharedClusterCompOnly1NoMatchedClusterSameSLHBTracks", "slope diff", 30, -2, 2);
        h1_slopeDiffNoSharedClusterCompOnly1NoMatchedClusterSameSLHBTracks.setTitleX("slope diff");
        h1_slopeDiffNoSharedClusterCompOnly1NoMatchedClusterSameSLHBTracks.setTitleY("counts");
        histoGroupNoSharedClusterCompOnly1NoMatchedClusterSameSLHBTracks.addDataSet(h1_slopeDiffNoSharedClusterCompOnly1NoMatchedClusterSameSLHBTracks, 3);        
        H1F h1_numSharedHitsNoSharedClusterCompOnly1NoMatchedClusterSameSLHBTracks = new H1F("numSharedHitsNoSharedClusterCompOnly1NoMatchedClusterSameSLHBTracks", "# of shared hits", 10, 0, 10);
        h1_numSharedHitsNoSharedClusterCompOnly1NoMatchedClusterSameSLHBTracks.setTitleX("# of shared hits");
        h1_numSharedHitsNoSharedClusterCompOnly1NoMatchedClusterSameSLHBTracks.setTitleY("counts");
        histoGroupNoSharedClusterCompOnly1NoMatchedClusterSameSLHBTracks.addDataSet(h1_numSharedHitsNoSharedClusterCompOnly1NoMatchedClusterSameSLHBTracks, 5);
        
        histoGroupMap.put(histoGroupNoSharedClusterCompOnly1NoMatchedClusterSameSLHBTracks.getName(), histoGroupNoSharedClusterCompOnly1NoMatchedClusterSameSLHBTracks); 
        
        ////// TB tracking level
        HistoGroup histoGroupTBTracks= new HistoGroup("TBTracks", 2, 4);
        H1F h1_numClustersTBTracks = new H1F("numClustersTBTracks", "# of clusters in TB tracks", 2, 4.5, 6.5);
        h1_numClustersTBTracks.setTitleX("# of clusters in TB tracks");
        h1_numClustersTBTracks.setTitleY("Counts");
        histoGroupTBTracks.addDataSet(h1_numClustersTBTracks, 0);          
        H1F h1_numMatchedClustersTBTracks = new H1F("numMatchedClustersTBTracks", "# of matched clusters in TB tracks", 7, -0.5, 6.5);
        h1_numMatchedClustersTBTracks.setTitleX("# of matched clusters in TB tracks");
        h1_numMatchedClustersTBTracks.setTitleY("Counts");
        histoGroupTBTracks.addDataSet(h1_numMatchedClustersTBTracks, 1);        
        H1F h1_numNomatchedClustersTBTracks = new H1F("numNomatchedClustersTBTracks", "# of nomatched clusters in TB tracks", 7, -0.5, 6.5);
        h1_numNomatchedClustersTBTracks.setTitleX("# of nomatched clusters in TB tracks");
        h1_numNomatchedClustersTBTracks.setTitleY("Counts");
        histoGroupTBTracks.addDataSet(h1_numNomatchedClustersTBTracks, 2);         
        H2F h2_numMatchedTBTracksVsNumMatchedClustering = new H2F("numMatchedTBTracksVsNumMatchedClustering", "# of matched clusters in TB tracks vs # of matched clusters in clustering", 7, -0.5, 6.5, 7, -0.5, 6.5);
        h2_numMatchedTBTracksVsNumMatchedClustering.setTitleX("# of matched clusters in clustering");
        h2_numMatchedTBTracksVsNumMatchedClustering.setTitleY("# of matched clusters in TB tracks");
        histoGroupTBTracks.addDataSet(h2_numMatchedTBTracksVsNumMatchedClustering, 3); 
        H2F h2_numNomatchedTBTracksVsNumMatchedClustering = new H2F("numNomatchedTBTracksVsNumMatchedClustering", "# of nomatched clusters in TB tracks vs # of matched clusters in clustering", 7, -0.5, 6.5, 7, -0.5, 6.5);
        h2_numNomatchedTBTracksVsNumMatchedClustering.setTitleX("# of matched clusters in clustering");
        h2_numNomatchedTBTracksVsNumMatchedClustering.setTitleY("# of nomatched clusters in TB tracks");
        histoGroupTBTracks.addDataSet(h2_numNomatchedTBTracksVsNumMatchedClustering, 4);         
        H1F h1_numClustersFullyMatchedTBTracks = new H1F("numClustersFullyMatchedTBTracks", "# of clusters in TB tracks for fully matched", 2, 4.5, 6.5);
        h1_numClustersFullyMatchedTBTracks.setTitleX("# of clusters in TB tracks for fully matched");
        h1_numClustersFullyMatchedTBTracks.setTitleY("Counts");
        histoGroupTBTracks.addDataSet(h1_numClustersFullyMatchedTBTracks, 5);        
        H1F h1_numNoiseClustersfor6ClustersFullyMatchedTBTracks = new H1F("numNoiseClustersfor6ClustersFullyMatchedTBTracks", "# of mixed-clusters for fully matched 6-cluster TB tracks", 7, -0.5, 6.5);
        h1_numNoiseClustersfor6ClustersFullyMatchedTBTracks.setTitleX("# of mixed-clusters for fully matched 6-cluster TB tracks");
        h1_numNoiseClustersfor6ClustersFullyMatchedTBTracks.setTitleY("Counts");
        histoGroupTBTracks.addDataSet(h1_numNoiseClustersfor6ClustersFullyMatchedTBTracks, 6);           
        H1F h1_numNoiseClustersfor5ClustersFullyMatchedTBTracks = new H1F("numNoiseClustersfor5ClustersFullyMatchedTBTracks", "# of mixed-clusters for fully matched 5-cluster TB tracks", 7, -0.5, 6.5);
        h1_numNoiseClustersfor5ClustersFullyMatchedTBTracks.setTitleX("# of mixed-clusters for fully matched 5-cluster TB tracks");
        h1_numNoiseClustersfor5ClustersFullyMatchedTBTracks.setTitleY("Counts");
        histoGroupTBTracks.addDataSet(h1_numNoiseClustersfor5ClustersFullyMatchedTBTracks, 7);         
        histoGroupMap.put(histoGroupTBTracks.getName(), histoGroupTBTracks);
        
        HistoGroup histoGroupNormalHitRatioNoiseClustersforFullyMatchedTBTracks= new HistoGroup("normalHitRatioNoiseClustersforFullyMatchedTBTracks", 2, 3);
        for (int i = 0; i < 6; i++) {
            H1F h1_normalHitRatioNoiseClustersInFullyMatchedTBTracks = new H1F("normalHitRatioNoiseClustersInFullyMatchedTBTracks for SL" + Integer.toString(i + 1),
                    "normal hit ratio of noise clusters in fully matched TB tracks" + Integer.toString(i + 1), 101, 0, 1.01);
            h1_normalHitRatioNoiseClustersInFullyMatchedTBTracks.setTitleX("normal hit ratio");
            h1_normalHitRatioNoiseClustersInFullyMatchedTBTracks.setTitleY("Counts");
            histoGroupNormalHitRatioNoiseClustersforFullyMatchedTBTracks.addDataSet(h1_normalHitRatioNoiseClustersInFullyMatchedTBTracks, i);
        }
        histoGroupMap.put(histoGroupNormalHitRatioNoiseClustersforFullyMatchedTBTracks.getName(), histoGroupNormalHitRatioNoiseClustersforFullyMatchedTBTracks); 
        
        HistoGroup histoGroupDAFWeightNormalClustersforFullyMatchedTBTracks= new HistoGroup("DAFWeightNormalClustersforFullyMatchedTBTracks", 2, 3);
        for (int i = 0; i < 6; i++) {
            H1F h1_DAFWeightNormalClustersInFullyMatchedTBTracks = new H1F("DAFWeightNormalClustersInFullyMatchedTBTracks for SL" + Integer.toString(i + 1),
                    "DAF weight of normal hits in fully matched TB tracks" + Integer.toString(i + 1), 101, 0, 1.01);
            h1_DAFWeightNormalClustersInFullyMatchedTBTracks.setTitleX("DAF weight");
            h1_DAFWeightNormalClustersInFullyMatchedTBTracks.setTitleY("Counts");
            histoGroupDAFWeightNormalClustersforFullyMatchedTBTracks.addDataSet(h1_DAFWeightNormalClustersInFullyMatchedTBTracks, i);
        }
        histoGroupMap.put(histoGroupDAFWeightNormalClustersforFullyMatchedTBTracks.getName(), histoGroupDAFWeightNormalClustersforFullyMatchedTBTracks);
        
        HistoGroup histoGroupDAFWeightNoiseClustersforFullyMatchedTBTracks= new HistoGroup("DAFWeightNoiseClustersforFullyMatchedTBTracks", 2, 3);
        for (int i = 0; i < 6; i++) {
            H1F h1_DAFWeightNoiseClustersInFullyMatchedTBTracks = new H1F("DAFWeightNoiseClustersInFullyMatchedTBTracks for SL" + Integer.toString(i + 1),
                    "DAF weight of noise hits in fully matched TB tracks" + Integer.toString(i + 1), 101, 0, 1.01);
            h1_DAFWeightNoiseClustersInFullyMatchedTBTracks.setTitleX("DAF weight");
            h1_DAFWeightNoiseClustersInFullyMatchedTBTracks.setTitleY("Counts");
            histoGroupDAFWeightNoiseClustersforFullyMatchedTBTracks.addDataSet(h1_DAFWeightNoiseClustersInFullyMatchedTBTracks, i);
        }
        histoGroupMap.put(histoGroupDAFWeightNoiseClustersforFullyMatchedTBTracks.getName(), histoGroupDAFWeightNoiseClustersforFullyMatchedTBTracks);         
                
        HistoGroup histoGroupTBTrackComp= new HistoGroup("TBTrackComp", 3, 4);
        H2F h2_chi2overndfTBTrackComp = new H2F("chi2overndfTBTrackComp", "Comparison for chi2/ndf", 100, 0, 100, 100, 0, 100);
        h2_chi2overndfTBTrackComp.setTitleX("chi2/ndf in sample1");
        h2_chi2overndfTBTrackComp.setTitleY("chi2/ndf in sample2");
        histoGroupTBTrackComp.addDataSet(h2_chi2overndfTBTrackComp, 0);        
        H2F h2_chi2pidTBTrackComp = new H2F("chi2pidTBTrackComp", "Comparison for chi2pid", 100, -15, 15, 100, -15, 15);
        h2_chi2pidTBTrackComp.setTitleX("chi2pid in sample1");
        h2_chi2pidTBTrackComp.setTitleY("chi2pid in sample2");
        histoGroupTBTrackComp.addDataSet(h2_chi2pidTBTrackComp, 1); 
        H1F h1_ratioNormalHitsTBTrack = new H1F("ratioNormalHitsTBTrack", "ratio of normal hits for TB tracks", 101, 0, 1.01);
        h1_ratioNormalHitsTBTrack.setTitleX("ratio of normal hits");
        h1_ratioNormalHitsTBTrack.setTitleY("counts");
        histoGroupTBTrackComp.addDataSet(h1_ratioNormalHitsTBTrack, 2);        
        histoGroupMap.put(histoGroupTBTrackComp.getName(), histoGroupTBTrackComp); 
        H2F h2_pTBTrackComp = new H2F("pTBTrackComp", "Comparison for p", 100, 0, 12, 100, 0, 12);
        h2_pTBTrackComp.setTitleX("p in sample1 (GeV/c)");
        h2_pTBTrackComp.setTitleY("p in sample2 (GeV/c)");
        histoGroupTBTrackComp.addDataSet(h2_pTBTrackComp, 3); 
        H2F h2_thetaTBTrackComp = new H2F("thetaTBTrackComp", "Comparison for theta", 100, 0, 1, 100, 0, 1);
        h2_thetaTBTrackComp.setTitleX("theta in sample1 (rad)");
        h2_thetaTBTrackComp.setTitleY("theta in sample2 (rad)");
        histoGroupTBTrackComp.addDataSet(h2_thetaTBTrackComp, 4); 
        H2F h2_phiTBTrackComp = new H2F("phiTBTrackComp", "Comparison for phi", 100, -Math.PI, Math.PI, 100, -Math.PI, Math.PI);
        h2_phiTBTrackComp.setTitleX("phi in sample1 (rad)");
        h2_phiTBTrackComp.setTitleY("phi in sample2 (rad)");
        histoGroupTBTrackComp.addDataSet(h2_phiTBTrackComp, 5);         
        H2F h2_vxTBTrackComp = new H2F("vxTBTrackComp", "Comparison for vx", 100, -25, 25, 100, -25, 25);
        h2_vxTBTrackComp.setTitleX("vx in sample1 (cm)");
        h2_vxTBTrackComp.setTitleY("vx in sample2 (cm)");
        histoGroupTBTrackComp.addDataSet(h2_vxTBTrackComp, 6);         
        H2F h2_vyTBTrackComp = new H2F("vyTBTrackComp", "Comparison for vy", 100, -25, 25, 100, -25, 25);
        h2_vyTBTrackComp.setTitleX("vy in sample1 (cm)");
        h2_vyTBTrackComp.setTitleY("vy in sample2 (cm)");
        histoGroupTBTrackComp.addDataSet(h2_vyTBTrackComp, 7); 
        H2F h2_vzTBTrackComp = new H2F("vzTBTrackComp", "Comparison for vz", 100, -25, 25, 100, -25, 25);
        h2_vzTBTrackComp.setTitleX("vz in sample1 (cm)");
        h2_vzTBTrackComp.setTitleY("vz in sample2 (cm)");
        histoGroupTBTrackComp.addDataSet(h2_vzTBTrackComp, 8);  
        H2F h2_pidTBTrackComp = new H2F("pidTBTrackComp", "Comparison for pid", 50, -25, 25, 50, -25, 25);
        h2_pidTBTrackComp.setTitleX("pid in sample1");
        h2_pidTBTrackComp.setTitleY("pid in sample2");
        histoGroupTBTrackComp.addDataSet(h2_pidTBTrackComp, 9);         
        H1F h1_pidStatusTBTrack = new H1F("pidStatusTBTrack", "if pid the same", 2, -0.5, 1.5);
        h1_pidStatusTBTrack.setTitleX("pid status");
        h1_pidStatusTBTrack.setTitleY("counts");
        histoGroupTBTrackComp.addDataSet(h1_pidStatusTBTrack, 10);
        H1F h1_ratioNormalHitsDiffPIdTBTrack = new H1F("ratioNormalHitsDiffPIdTBTrack", "ratio of normal hits for cases with diff. pid", 101, 0, 1.01);
        h1_ratioNormalHitsDiffPIdTBTrack.setTitleX("ratio of normal hits");
        h1_ratioNormalHitsDiffPIdTBTrack.setTitleY("counts");
        histoGroupTBTrackComp.addDataSet(h1_ratioNormalHitsDiffPIdTBTrack, 11);        
        histoGroupMap.put(histoGroupTBTrackComp.getName(), histoGroupTBTrackComp); 
        
        HistoGroup histoGroupTBTrackDiff= new HistoGroup("TBTrackDiff", 3, 4);
        H1F h1_chi2overndfTBTrackDiff = new H1F("chi2overndfTBTrackDiff", "Difference of chi2/ndf", 100, -2, 2);
        h1_chi2overndfTBTrackDiff.setTitleX("Difference of chi2/ndf");
        h1_chi2overndfTBTrackDiff.setTitleY("counts");
        histoGroupTBTrackDiff.addDataSet(h1_chi2overndfTBTrackDiff, 0);        
        H1F h1_chi2pidTBTrackDiff = new H1F("chi2pidTBTrackDiff", "Difference of chi2pid", 100, -0.3, 0.3);
        h1_chi2pidTBTrackDiff.setTitleX("Difference of chi2pid");
        h1_chi2pidTBTrackDiff.setTitleY("counts");
        histoGroupTBTrackDiff.addDataSet(h1_chi2pidTBTrackDiff, 1);         
        H1F h1_pTBTrackDiff = new H1F("pTBTrackDiff", "Difference of p", 100, -0.1, 0.1);
        h1_pTBTrackDiff.setTitleX("Difference of p (GeV/c)");
        h1_pTBTrackDiff.setTitleY("counts");
        histoGroupTBTrackDiff.addDataSet(h1_pTBTrackDiff, 3); 
        H1F h1_thetaTBTrackDiff = new H1F("thetaTBTrackDiff", "Difference of theta", 100, -0.02, 0.02);
        h1_thetaTBTrackDiff.setTitleX("Difference of theta (rad)");
        h1_thetaTBTrackDiff.setTitleY("counts");
        histoGroupTBTrackDiff.addDataSet(h1_thetaTBTrackDiff, 4); 
        H1F h1_phiTBTrackDiff = new H1F("phiTBTrackDiff", "Difference of phi", 100, -0.02, 0.02);
        h1_phiTBTrackDiff.setTitleX("Difference of phi (rad)");
        h1_phiTBTrackDiff.setTitleY("counts");
        histoGroupTBTrackDiff.addDataSet(h1_phiTBTrackDiff, 5);         
        H1F h1_vxTBTrackDiff = new H1F("vxTBTrackDiff", "Difference of vx", 100, -1, 1);
        h1_vxTBTrackDiff.setTitleX("Difference of vx (cm)");
        h1_vxTBTrackDiff.setTitleY("counts");
        histoGroupTBTrackDiff.addDataSet(h1_vxTBTrackDiff, 6);         
        H1F h1_vyTBTrackDiff = new H1F("vyTBTrackDiff", "Difference of vy", 100, -1, 1);
        h1_vyTBTrackDiff.setTitleX("Difference of vy (cm)");
        h1_vyTBTrackDiff.setTitleY("counts");
        histoGroupTBTrackDiff.addDataSet(h1_vyTBTrackDiff, 7); 
        H1F h1_vzTBTrackDiff = new H1F("vzTBTrackDiff", "Difference of vz", 100, -1, 1);
        h1_vzTBTrackDiff.setTitleX("Difference of vz (cm)");
        h1_vzTBTrackDiff.setTitleY("counts");
        histoGroupTBTrackDiff.addDataSet(h1_vzTBTrackDiff, 8);                  
        histoGroupMap.put(histoGroupTBTrackDiff.getName(), histoGroupTBTrackDiff); 
        
        HistoGroup histoGroupTBTrackDiffDiffPId= new HistoGroup("TBTrackDiffDiffPId", 3, 4);
        H1F h1_chi2overndfTBTrackDiffDiffPId = new H1F("chi2overndfTBTrackDiffDiffPId", "Diff of chi2/ndf with diff pid", 100, -2, 2);
        h1_chi2overndfTBTrackDiffDiffPId.setTitleX("Difference of chi2/ndf");
        h1_chi2overndfTBTrackDiffDiffPId.setTitleY("counts");
        histoGroupTBTrackDiffDiffPId.addDataSet(h1_chi2overndfTBTrackDiffDiffPId, 0);        
        H1F h1_chi2pidTBTrackDiffDiffPId = new H1F("chi2pidTBTrackDiffDiffPId", "Diff of chi2pid with diff pid", 100, -0.3, 0.3);
        h1_chi2pidTBTrackDiffDiffPId.setTitleX("Diff of chi2pid");
        h1_chi2pidTBTrackDiffDiffPId.setTitleY("counts");
        histoGroupTBTrackDiffDiffPId.addDataSet(h1_chi2pidTBTrackDiffDiffPId, 1);         
        H1F h1_pTBTrackDiffDiffPId = new H1F("pTBTrackDiffDiffPId", "Diff of p", 100, -0.1, 0.1);
        h1_pTBTrackDiffDiffPId.setTitleX("Diff of p (GeV/c)");
        h1_pTBTrackDiffDiffPId.setTitleY("counts");
        histoGroupTBTrackDiffDiffPId.addDataSet(h1_pTBTrackDiffDiffPId, 3); 
        H1F h1_thetaTBTrackDiffDiffPId = new H1F("thetaTBTrackDiffDiffPId", "Diff of theta with diff pid", 100, -0.02, 0.02);
        h1_thetaTBTrackDiffDiffPId.setTitleX("Diff of theta (rad)");
        h1_thetaTBTrackDiffDiffPId.setTitleY("counts");
        histoGroupTBTrackDiffDiffPId.addDataSet(h1_thetaTBTrackDiffDiffPId, 4); 
        H1F h1_phiTBTrackDiffDiffPId = new H1F("phiTBTrackDiffDiffPId", "Diff of phi with diff pid", 100, -0.02, 0.02);
        h1_phiTBTrackDiffDiffPId.setTitleX("Diff of phi (rad)");
        h1_phiTBTrackDiffDiffPId.setTitleY("counts");
        histoGroupTBTrackDiffDiffPId.addDataSet(h1_phiTBTrackDiffDiffPId, 5);         
        H1F h1_vxTBTrackDiffDiffPId = new H1F("vxTBTrackDiffDiffPId", "Diff of vx with diff pid", 100, -1, 1);
        h1_vxTBTrackDiffDiffPId.setTitleX("Diff of vx (cm)");
        h1_vxTBTrackDiffDiffPId.setTitleY("counts");
        histoGroupTBTrackDiffDiffPId.addDataSet(h1_vxTBTrackDiffDiffPId, 6);         
        H1F h1_vyTBTrackDiffDiffPId = new H1F("vyTBTrackDiffDiffPId", "Diff of vy with diff pid", 100, -1, 1);
        h1_vyTBTrackDiffDiffPId.setTitleX("Diff of vy (cm)");
        h1_vyTBTrackDiffDiffPId.setTitleY("counts");
        histoGroupTBTrackDiffDiffPId.addDataSet(h1_vyTBTrackDiffDiffPId, 7); 
        H1F h1_vzTBTrackDiffDiffPId = new H1F("vzTBTrackDiffDiffPId", "Diff of vz with diff pid", 100, -1, 1);
        h1_vzTBTrackDiffDiffPId.setTitleX("Diff of vz (cm)");
        h1_vzTBTrackDiffDiffPId.setTitleY("counts");
        histoGroupTBTrackDiffDiffPId.addDataSet(h1_vzTBTrackDiffDiffPId, 8);                 
        histoGroupMap.put(histoGroupTBTrackDiffDiffPId.getName(), histoGroupTBTrackDiffDiffPId);        
                
        HistoGroup histoGroupTBTrackDiffVsRatioNormalHits= new HistoGroup("TBTrackDiffVsRatioNormalHits", 3, 4);
        H2F h2_chi2overndfTBTrackDiffVsRatioNormalHits = new H2F("chi2overndfTBTrackDiffVsRatioNormalHits", "Diff of chi2/ndf", 100, 0.6, 1.01, 100, -2, 2);
        h2_chi2overndfTBTrackDiffVsRatioNormalHits.setTitleX("ratio of normal hits");
        h2_chi2overndfTBTrackDiffVsRatioNormalHits.setTitleY("Diff of chi2/ndf");
        histoGroupTBTrackDiffVsRatioNormalHits.addDataSet(h2_chi2overndfTBTrackDiffVsRatioNormalHits, 0);        
        H2F h2_chi2pidTBTrackDiffVsRatioNormalHits = new H2F("chi2pidTBTrackDiffVsRatioNormalHits", "Diff of chi2pid", 100, 0.6, 1.01, 100, -0.3, 0.3);
        h2_chi2pidTBTrackDiffVsRatioNormalHits.setTitleX("ratio of normal hits");
        h2_chi2pidTBTrackDiffVsRatioNormalHits.setTitleY("Diff of chi2pid");
        histoGroupTBTrackDiffVsRatioNormalHits.addDataSet(h2_chi2pidTBTrackDiffVsRatioNormalHits, 1);         
        H2F h2_pTBTrackDiffVsRatioNormalHits = new H2F("pTBTrackDiffVsRatioNormalHits", "Diff of p", 100, 0.6, 1.01, 100, -0.1, 0.1);
        h2_pTBTrackDiffVsRatioNormalHits.setTitleX("ratio of normal hits");
        h2_pTBTrackDiffVsRatioNormalHits.setTitleY("Diff of p (GeV/c)");
        histoGroupTBTrackDiffVsRatioNormalHits.addDataSet(h2_pTBTrackDiffVsRatioNormalHits, 3); 
        H2F h2_thetaTBTrackDiffVsRatioNormalHits = new H2F("thetaTBTrackDiffVsRatioNormalHits", "Diff of theta", 100, 0.6, 1.01, 100, -0.02, 0.02);
        h2_thetaTBTrackDiffVsRatioNormalHits.setTitleX("ratio of normal hits");
        h2_thetaTBTrackDiffVsRatioNormalHits.setTitleY("Diff of theta (rad)");
        histoGroupTBTrackDiffVsRatioNormalHits.addDataSet(h2_thetaTBTrackDiffVsRatioNormalHits, 4); 
        H2F h2_phiTBTrackDiffVsRatioNormalHits = new H2F("phiTBTrackDiffVsRatioNormalHits", "Diff of phi", 100, 0.6, 1.01, 100, -0.02, 0.02);
        h2_phiTBTrackDiffVsRatioNormalHits.setTitleX("ratio of normal hits");
        h2_phiTBTrackDiffVsRatioNormalHits.setTitleY("Diff of phi (rad)");
        histoGroupTBTrackDiffVsRatioNormalHits.addDataSet(h2_phiTBTrackDiffVsRatioNormalHits, 5);         
        H2F h2_vxTBTrackDiffVsRatioNormalHits = new H2F("vxTBTrackDiffVsRatioNormalHits", "Diff of vx", 100, 0.6, 1.01, 100, -1, 1);
        h2_vxTBTrackDiffVsRatioNormalHits.setTitleX("ratio of normal hits");
        h2_vxTBTrackDiffVsRatioNormalHits.setTitleY("Diff of vx (cm)");
        histoGroupTBTrackDiffVsRatioNormalHits.addDataSet(h2_vxTBTrackDiffVsRatioNormalHits, 6);         
        H2F h2_vyTBTrackDiffVsRatioNormalHits = new H2F("vyTBTrackDiffVsRatioNormalHits", "Diff of vy", 100, 0.6, 1.01, 100, -1, 1);
        h2_vyTBTrackDiffVsRatioNormalHits.setTitleX("ratio of normal hits");
        h2_vyTBTrackDiffVsRatioNormalHits.setTitleY("Diff of vy (cm)");
        histoGroupTBTrackDiffVsRatioNormalHits.addDataSet(h2_vyTBTrackDiffVsRatioNormalHits, 7); 
        H2F h2_vzTBTrackDiffVsRatioNormalHits = new H2F("vzTBTrackDiffVsRatioNormalHits", "Diff of vz", 100, 0.6, 1.01, 100, -1, 1);
        h2_vzTBTrackDiffVsRatioNormalHits.setTitleX("ratio of normal hits");
        h2_vzTBTrackDiffVsRatioNormalHits.setTitleY("Diff of vz (cm)");
        histoGroupTBTrackDiffVsRatioNormalHits.addDataSet(h2_vzTBTrackDiffVsRatioNormalHits, 8);                 
        histoGroupMap.put(histoGroupTBTrackDiffVsRatioNormalHits.getName(), histoGroupTBTrackDiffVsRatioNormalHits); 
        
        // Valid TB tracks
        HistoGroup histoGroupValidTBTracks= new HistoGroup("ValidTBTracks", 2, 3);       
        H1F h1_numClustersFullyMatchedValidTBTracks = new H1F("numClustersFullyMatchedValidTBTracks", "# of clusters in valid TB tracks for fully matched", 2, 4.5, 6.5);
        h1_numClustersFullyMatchedValidTBTracks.setTitleX("# of clusters in valid TB tracks for fully matched");
        h1_numClustersFullyMatchedValidTBTracks.setTitleY("Counts");
        histoGroupValidTBTracks.addDataSet(h1_numClustersFullyMatchedValidTBTracks, 3);        
        H1F h1_numNoiseClustersfor6ClustersFullyMatchedValidTBTracks = new H1F("numNoiseClustersfor6ClustersFullyMatchedValidTBTracks", "# of mixed-clusters for fully matched 6-cluster valid TB tracks", 7, -0.5, 6.5);
        h1_numNoiseClustersfor6ClustersFullyMatchedValidTBTracks.setTitleX("# of mixed-clusters for fully matched 6-cluster vaid TB tracks");
        h1_numNoiseClustersfor6ClustersFullyMatchedValidTBTracks.setTitleY("Counts");
        histoGroupValidTBTracks.addDataSet(h1_numNoiseClustersfor6ClustersFullyMatchedValidTBTracks, 4);           
        H1F h1_numNoiseClustersfor5ClustersFullyMatchedValidTBTracks = new H1F("numNoiseClustersfor5ClustersFullyMatchedValidTBTracks", "# of mixed-clusters for fully matched 5-cluster valid TB tracks", 7, -0.5, 6.5);
        h1_numNoiseClustersfor5ClustersFullyMatchedValidTBTracks.setTitleX("# of mixed-clusters for fully matched 5-cluster valid TB tracks");
        h1_numNoiseClustersfor5ClustersFullyMatchedValidTBTracks.setTitleY("Counts");
        histoGroupValidTBTracks.addDataSet(h1_numNoiseClustersfor5ClustersFullyMatchedValidTBTracks, 5);         
        histoGroupMap.put(histoGroupValidTBTracks.getName(), histoGroupValidTBTracks);
        
        HistoGroup histoGroupNormalHitRatioNoiseClustersforFullyMatchedValidTBTracks= new HistoGroup("normalHitRatioNoiseClustersforFullyMatchedValidTBTracks", 2, 3);
        for (int i = 0; i < 6; i++) {
            H1F h1_normalHitRatioNoiseClustersInFullyMatchedValidTBTracks = new H1F("normalHitRatioNoiseClustersInFullyMatchedValidTBTracks for SL" + Integer.toString(i + 1),
                    "normal hit ratio of noise clusters in fully matched TB tracks" + Integer.toString(i + 1), 101, 0, 1.01);
            h1_normalHitRatioNoiseClustersInFullyMatchedValidTBTracks.setTitleX("normal hit ratio");
            h1_normalHitRatioNoiseClustersInFullyMatchedValidTBTracks.setTitleY("Counts");
            histoGroupNormalHitRatioNoiseClustersforFullyMatchedValidTBTracks.addDataSet(h1_normalHitRatioNoiseClustersInFullyMatchedValidTBTracks, i);
        }
        histoGroupMap.put(histoGroupNormalHitRatioNoiseClustersforFullyMatchedValidTBTracks.getName(), histoGroupNormalHitRatioNoiseClustersforFullyMatchedValidTBTracks); 
        
        HistoGroup histoGroupDAFWeightNormalClustersforFullyMatchedValidTBTracks= new HistoGroup("DAFWeightNormalClustersforFullyMatchedValidTBTracks", 2, 3);
        for (int i = 0; i < 6; i++) {
            H1F h1_DAFWeightNormalClustersInFullyMatchedValidTBTracks = new H1F("DAFWeightNormalClustersInFullyMatchedValidTBTracks for SL" + Integer.toString(i + 1),
                    "DAF weight of noise clusters in fully matched TB tracks" + Integer.toString(i + 1), 101, 0, 1.01);
            h1_DAFWeightNormalClustersInFullyMatchedValidTBTracks.setTitleX("DAF weight");
            h1_DAFWeightNormalClustersInFullyMatchedValidTBTracks.setTitleY("Counts");
            histoGroupDAFWeightNormalClustersforFullyMatchedValidTBTracks.addDataSet(h1_DAFWeightNormalClustersInFullyMatchedValidTBTracks, i);
        }
        histoGroupMap.put(histoGroupDAFWeightNormalClustersforFullyMatchedValidTBTracks.getName(), histoGroupDAFWeightNormalClustersforFullyMatchedValidTBTracks);
        
        HistoGroup histoGroupDAFWeightNoiseClustersforFullyMatchedValidTBTracks= new HistoGroup("DAFWeightNoiseClustersforFullyMatchedValidTBTracks", 2, 3);
        for (int i = 0; i < 6; i++) {
            H1F h1_DAFWeightNoiseClustersInFullyMatchedValidTBTracks = new H1F("DAFWeightNoiseClustersInFullyMatchedValidTBTracks for SL" + Integer.toString(i + 1),
                    "DAF weight of noise clusters in fully matched TB tracks" + Integer.toString(i + 1), 101, 0, 1.01);
            h1_DAFWeightNoiseClustersInFullyMatchedValidTBTracks.setTitleX("DAF weight");
            h1_DAFWeightNoiseClustersInFullyMatchedValidTBTracks.setTitleY("Counts");
            histoGroupDAFWeightNoiseClustersforFullyMatchedValidTBTracks.addDataSet(h1_DAFWeightNoiseClustersInFullyMatchedValidTBTracks, i);
        }
        histoGroupMap.put(histoGroupDAFWeightNoiseClustersforFullyMatchedValidTBTracks.getName(), histoGroupDAFWeightNoiseClustersforFullyMatchedValidTBTracks); 
                        
        HistoGroup histoGroupValidTBTrackComp= new HistoGroup("ValidTBTrackComp", 3, 4);
        H2F h2_chi2overndfValidTBTrackComp = new H2F("chi2overndfValidTBTrackComp", "Comparison for chi2/ndf", 100, 0, 100, 100, 0, 100);
        h2_chi2overndfValidTBTrackComp.setTitleX("chi2/ndf in sample1");
        h2_chi2overndfValidTBTrackComp.setTitleY("chi2/ndf in sample2");
        histoGroupValidTBTrackComp.addDataSet(h2_chi2overndfValidTBTrackComp, 0);        
        H2F h2_chi2pidValidTBTrackComp = new H2F("chi2pidValidTBTrackComp", "Comparison for chi2pid", 100, -15, 15, 100, -15, 15);
        h2_chi2pidValidTBTrackComp.setTitleX("chi2pid in sample1");
        h2_chi2pidValidTBTrackComp.setTitleY("chi2pid in sample2");
        histoGroupValidTBTrackComp.addDataSet(h2_chi2pidValidTBTrackComp, 1);            
        H2F h2_pValidTBTrackComp = new H2F("pValidTBTrackComp", "Comparison for p", 100, 0, 12, 100, 0, 12);
        h2_pValidTBTrackComp.setTitleX("p in sample1 (GeV/c)");
        h2_pValidTBTrackComp.setTitleY("p in sample2 (GeV/c)");
        histoGroupValidTBTrackComp.addDataSet(h2_pValidTBTrackComp, 3); 
        H2F h2_thetaValidTBTrackComp = new H2F("thetaValidTBTrackComp", "Comparison for theta", 100, 0, 1, 100, 0, 1);
        h2_thetaValidTBTrackComp.setTitleX("theta in sample1 (rad)");
        h2_thetaValidTBTrackComp.setTitleY("theta in sample2 (rad)");
        histoGroupValidTBTrackComp.addDataSet(h2_thetaValidTBTrackComp, 4); 
        H2F h2_phiValidTBTrackComp = new H2F("phiValidTBTrackComp", "Comparison for phi", 100, -Math.PI, Math.PI, 100, -Math.PI, Math.PI);
        h2_phiValidTBTrackComp.setTitleX("phi in sample1 (rad)");
        h2_phiValidTBTrackComp.setTitleY("phi in sample2 (rad)");
        histoGroupValidTBTrackComp.addDataSet(h2_phiValidTBTrackComp, 5);         
        H2F h2_vxValidTBTrackComp = new H2F("vxValidTBTrackComp", "Comparison for vx", 100, -25, 25, 100, -25, 25);
        h2_vxValidTBTrackComp.setTitleX("vx in sample1 (cm)");
        h2_vxValidTBTrackComp.setTitleY("vx in sample2 (cm)");
        histoGroupValidTBTrackComp.addDataSet(h2_vxValidTBTrackComp, 6);         
        H2F h2_vyValidTBTrackComp = new H2F("vyValidTBTrackComp", "Comparison for vy", 100, -25, 25, 100, -25, 25);
        h2_vyValidTBTrackComp.setTitleX("vy in sample1 (cm)");
        h2_vyValidTBTrackComp.setTitleY("vy in sample2 (cm)");
        histoGroupValidTBTrackComp.addDataSet(h2_vyValidTBTrackComp, 7); 
        H2F h2_vzValidTBTrackComp = new H2F("vzValidTBTrackComp", "Comparison for vz", 100, -25, 25, 100, -25, 25);
        h2_vzValidTBTrackComp.setTitleX("vz in sample1 (cm)");
        h2_vzValidTBTrackComp.setTitleY("vz in sample2 (cm)");
        histoGroupValidTBTrackComp.addDataSet(h2_vzValidTBTrackComp, 8);  
        H2F h2_pidValidTBTrackComp = new H2F("pidValidTBTrackComp", "Comparison for pid", 50, -25, 25, 50, -25, 25);
        h2_pidValidTBTrackComp.setTitleX("pid in sample1");
        h2_pidValidTBTrackComp.setTitleY("pid in sample2");
        histoGroupValidTBTrackComp.addDataSet(h2_pidValidTBTrackComp, 9);         
        H1F h1_pidStatusValidTBTrack = new H1F("pidStatusValidTBTrack", "if pid the same", 2, -0.5, 1.5);
        h1_pidStatusValidTBTrack.setTitleX("pid status");
        h1_pidStatusValidTBTrack.setTitleY("counts");
        histoGroupValidTBTrackComp.addDataSet(h1_pidStatusValidTBTrack, 10);
        H1F h1_ratioNormalHitsDiffPIdValidTBTrack = new H1F("ratioNormalHitsDiffPIdValidTBTrack", "ratio of normal hits for cases with diff. pid", 100, 0, 1);
        h1_ratioNormalHitsDiffPIdValidTBTrack.setTitleX("ratio of normal hits");
        h1_ratioNormalHitsDiffPIdValidTBTrack.setTitleY("counts");
        histoGroupValidTBTrackComp.addDataSet(h1_ratioNormalHitsDiffPIdValidTBTrack, 11);        
        histoGroupMap.put(histoGroupValidTBTrackComp.getName(), histoGroupValidTBTrackComp); 
        
        HistoGroup histoGroupValidTBTrackDiff= new HistoGroup("ValidTBTrackDiff", 3, 4);
        H1F h1_chi2overndfValidTBTrackDiff = new H1F("chi2overndfValidTBTrackDiff", "Difference of chi2/ndf", 100, -2, 2);
        h1_chi2overndfValidTBTrackDiff.setTitleX("Difference of chi2/ndf");
        h1_chi2overndfValidTBTrackDiff.setTitleY("counts");
        histoGroupValidTBTrackDiff.addDataSet(h1_chi2overndfValidTBTrackDiff, 0);        
        H1F h1_chi2pidValidTBTrackDiff = new H1F("chi2pidValidTBTrackDiff", "Difference of chi2pid", 100, -0.3, 0.3);
        h1_chi2pidValidTBTrackDiff.setTitleX("Difference of chi2pid");
        h1_chi2pidValidTBTrackDiff.setTitleY("counts");
        histoGroupValidTBTrackDiff.addDataSet(h1_chi2pidValidTBTrackDiff, 1);         
        H1F h1_pValidTBTrackDiff = new H1F("pValidTBTrackDiff", "Difference of p", 100, -0.1, 0.1);
        h1_pValidTBTrackDiff.setTitleX("Difference of p (GeV/c)");
        h1_pValidTBTrackDiff.setTitleY("counts");
        histoGroupValidTBTrackDiff.addDataSet(h1_pValidTBTrackDiff, 3); 
        H1F h1_thetaValidTBTrackDiff = new H1F("thetaValidTBTrackDiff", "Difference of theta", 100, -0.02, 0.02);
        h1_thetaValidTBTrackDiff.setTitleX("Difference of theta (rad)");
        h1_thetaValidTBTrackDiff.setTitleY("counts");
        histoGroupValidTBTrackDiff.addDataSet(h1_thetaValidTBTrackDiff, 4); 
        H1F h1_phiValidTBTrackDiff = new H1F("phiValidTBTrackDiff", "Difference of phi", 100, -0.02, 0.02);
        h1_phiValidTBTrackDiff.setTitleX("Difference of phi (rad)");
        h1_phiValidTBTrackDiff.setTitleY("counts");
        histoGroupValidTBTrackDiff.addDataSet(h1_phiValidTBTrackDiff, 5);         
        H1F h1_vxValidTBTrackDiff = new H1F("vxValidTBTrackDiff", "Difference of vx", 100, -1, 1);
        h1_vxValidTBTrackDiff.setTitleX("Difference of vx (cm)");
        h1_vxValidTBTrackDiff.setTitleY("counts");
        histoGroupValidTBTrackDiff.addDataSet(h1_vxValidTBTrackDiff, 6);         
        H1F h1_vyValidTBTrackDiff = new H1F("vyValidTBTrackDiff", "Difference of vy", 100, -1, 1);
        h1_vyValidTBTrackDiff.setTitleX("Difference of vy (cm)");
        h1_vyValidTBTrackDiff.setTitleY("counts");
        histoGroupValidTBTrackDiff.addDataSet(h1_vyValidTBTrackDiff, 7); 
        H1F h1_vzValidTBTrackDiff = new H1F("vzValidTBTrackDiff", "Difference of vz", 100, -1, 1);
        h1_vzValidTBTrackDiff.setTitleX("Difference of vz (cm)");
        h1_vzValidTBTrackDiff.setTitleY("counts");
        histoGroupValidTBTrackDiff.addDataSet(h1_vzValidTBTrackDiff, 8);                  
        histoGroupMap.put(histoGroupValidTBTrackDiff.getName(), histoGroupValidTBTrackDiff); 
        
        HistoGroup histoGroupValidTBTrackDiffDiffPId= new HistoGroup("ValidTBTrackDiffDiffPId", 3, 4);
        H1F h1_chi2overndfValidTBTrackDiffDiffPId = new H1F("chi2overndfValidTBTrackDiffDiffPId", "Diff of chi2/ndf with diff pid", 100, -2, 2);
        h1_chi2overndfValidTBTrackDiffDiffPId.setTitleX("Difference of chi2/ndf");
        h1_chi2overndfValidTBTrackDiffDiffPId.setTitleY("counts");
        histoGroupValidTBTrackDiffDiffPId.addDataSet(h1_chi2overndfValidTBTrackDiffDiffPId, 0);        
        H1F h1_chi2pidValidTBTrackDiffDiffPId = new H1F("chi2pidValidTBTrackDiffDiffPId", "Diff of chi2pid with diff pid", 100, -0.3, 0.3);
        h1_chi2pidValidTBTrackDiffDiffPId.setTitleX("Diff of chi2pid");
        h1_chi2pidValidTBTrackDiffDiffPId.setTitleY("counts");
        histoGroupValidTBTrackDiffDiffPId.addDataSet(h1_chi2pidValidTBTrackDiffDiffPId, 1);         
        H1F h1_pValidTBTrackDiffDiffPId = new H1F("pValidTBTrackDiffDiffPId", "Diff of p", 100, -0.1, 0.1);
        h1_pValidTBTrackDiffDiffPId.setTitleX("Diff of p (GeV/c)");
        h1_pValidTBTrackDiffDiffPId.setTitleY("counts");
        histoGroupValidTBTrackDiffDiffPId.addDataSet(h1_pValidTBTrackDiffDiffPId, 3); 
        H1F h1_thetaValidTBTrackDiffDiffPId = new H1F("thetaValidTBTrackDiffDiffPId", "Diff of theta with diff pid", 100, -0.02, 0.02);
        h1_thetaValidTBTrackDiffDiffPId.setTitleX("Diff of theta (rad)");
        h1_thetaValidTBTrackDiffDiffPId.setTitleY("counts");
        histoGroupValidTBTrackDiffDiffPId.addDataSet(h1_thetaValidTBTrackDiffDiffPId, 4); 
        H1F h1_phiValidTBTrackDiffDiffPId = new H1F("phiValidTBTrackDiffDiffPId", "Diff of phi with diff pid", 100, -0.02, 0.02);
        h1_phiValidTBTrackDiffDiffPId.setTitleX("Diff of phi (rad)");
        h1_phiValidTBTrackDiffDiffPId.setTitleY("counts");
        histoGroupValidTBTrackDiffDiffPId.addDataSet(h1_phiValidTBTrackDiffDiffPId, 5);         
        H1F h1_vxValidTBTrackDiffDiffPId = new H1F("vxValidTBTrackDiffDiffPId", "Diff of vx with diff pid", 100, -1, 1);
        h1_vxValidTBTrackDiffDiffPId.setTitleX("Diff of vx (cm)");
        h1_vxValidTBTrackDiffDiffPId.setTitleY("counts");
        histoGroupValidTBTrackDiffDiffPId.addDataSet(h1_vxValidTBTrackDiffDiffPId, 6);         
        H1F h1_vyValidTBTrackDiffDiffPId = new H1F("vyValidTBTrackDiffDiffPId", "Diff of vy with diff pid", 100, -1, 1);
        h1_vyValidTBTrackDiffDiffPId.setTitleX("Diff of vy (cm)");
        h1_vyValidTBTrackDiffDiffPId.setTitleY("counts");
        histoGroupValidTBTrackDiffDiffPId.addDataSet(h1_vyValidTBTrackDiffDiffPId, 7); 
        H1F h1_vzValidTBTrackDiffDiffPId = new H1F("vzValidTBTrackDiffDiffPId", "Diff of vz with diff pid", 100, -1, 1);
        h1_vzValidTBTrackDiffDiffPId.setTitleX("Diff of vz (cm)");
        h1_vzValidTBTrackDiffDiffPId.setTitleY("counts");
        histoGroupValidTBTrackDiffDiffPId.addDataSet(h1_vzValidTBTrackDiffDiffPId, 8);                 
        histoGroupMap.put(histoGroupValidTBTrackDiffDiffPId.getName(), histoGroupValidTBTrackDiffDiffPId);        
        
        
        HistoGroup histoGroupValidTBTrackDiffVsRatioNormalHits= new HistoGroup("ValidTBTrackDiffVsRatioNormalHits", 3, 4);
        H2F h2_chi2overndfValidTBTrackDiffVsRatioNormalHits = new H2F("chi2overndfValidTBTrackDiffVsRatioNormalHits", "Diff of chi2/ndf", 100, 0.6, 1.01, 100, -2, 2);
        h2_chi2overndfValidTBTrackDiffVsRatioNormalHits.setTitleX("ratio of normal hits");
        h2_chi2overndfValidTBTrackDiffVsRatioNormalHits.setTitleY("Diff of chi2/ndf");
        histoGroupValidTBTrackDiffVsRatioNormalHits.addDataSet(h2_chi2overndfValidTBTrackDiffVsRatioNormalHits, 0);        
        H2F h2_chi2pidValidTBTrackDiffVsRatioNormalHits = new H2F("chi2pidValidTBTrackDiffVsRatioNormalHits", "Diff of chi2pid", 100, 0.6, 1.01, 100, -0.3, 0.3);
        h2_chi2pidValidTBTrackDiffVsRatioNormalHits.setTitleX("ratio of normal hits");
        h2_chi2pidValidTBTrackDiffVsRatioNormalHits.setTitleY("Diff of chi2pid");
        histoGroupValidTBTrackDiffVsRatioNormalHits.addDataSet(h2_chi2pidValidTBTrackDiffVsRatioNormalHits, 1);         
        H2F h2_pValidTBTrackDiffVsRatioNormalHits = new H2F("pValidTBTrackDiffVsRatioNormalHits", "Diff of p", 100, 0.6, 1.01, 100, -0.1, 0.1);
        h2_pValidTBTrackDiffVsRatioNormalHits.setTitleX("ratio of normal hits");
        h2_pValidTBTrackDiffVsRatioNormalHits.setTitleY("Diff of p (GeV/c)");
        histoGroupValidTBTrackDiffVsRatioNormalHits.addDataSet(h2_pValidTBTrackDiffVsRatioNormalHits, 3); 
        H2F h2_thetaValidTBTrackDiffVsRatioNormalHits = new H2F("thetaValidTBTrackDiffVsRatioNormalHits", "Diff of theta", 100, 0.6, 1.01, 100, -0.02, 0.02);
        h2_thetaValidTBTrackDiffVsRatioNormalHits.setTitleX("ratio of normal hits");
        h2_thetaValidTBTrackDiffVsRatioNormalHits.setTitleY("Diff of theta (rad)");
        histoGroupValidTBTrackDiffVsRatioNormalHits.addDataSet(h2_thetaValidTBTrackDiffVsRatioNormalHits, 4); 
        H2F h2_phiValidTBTrackDiffVsRatioNormalHits = new H2F("phiValidTBTrackDiffVsRatioNormalHits", "Diff of phi", 100, 0.6, 1.01, 100, -0.02, 0.02);
        h2_phiValidTBTrackDiffVsRatioNormalHits.setTitleX("ratio of normal hits");
        h2_phiValidTBTrackDiffVsRatioNormalHits.setTitleY("Diff of phi (rad)");
        histoGroupValidTBTrackDiffVsRatioNormalHits.addDataSet(h2_phiValidTBTrackDiffVsRatioNormalHits, 5);         
        H2F h2_vxValidTBTrackDiffVsRatioNormalHits = new H2F("vxValidTBTrackDiffVsRatioNormalHits", "Diff of vx", 100, 0.6, 1.01, 100, -1, 1);
        h2_vxValidTBTrackDiffVsRatioNormalHits.setTitleX("ratio of normal hits");
        h2_vxValidTBTrackDiffVsRatioNormalHits.setTitleY("Diff of vx (cm)");
        histoGroupValidTBTrackDiffVsRatioNormalHits.addDataSet(h2_vxValidTBTrackDiffVsRatioNormalHits, 6);         
        H2F h2_vyValidTBTrackDiffVsRatioNormalHits = new H2F("vyValidTBTrackDiffVsRatioNormalHits", "Diff of vy", 100, 0.6, 1.01, 100, -1, 1);
        h2_vyValidTBTrackDiffVsRatioNormalHits.setTitleX("ratio of normal hits");
        h2_vyValidTBTrackDiffVsRatioNormalHits.setTitleY("Diff of vy (cm)");
        histoGroupValidTBTrackDiffVsRatioNormalHits.addDataSet(h2_vyValidTBTrackDiffVsRatioNormalHits, 7); 
        H2F h2_vzValidTBTrackDiffVsRatioNormalHits = new H2F("vzValidTBTrackDiffVsRatioNormalHits", "Diff of vz", 100, 0.6, 1.01, 100, -1, 1);
        h2_vzValidTBTrackDiffVsRatioNormalHits.setTitleX("ratio of normal hits");
        h2_vzValidTBTrackDiffVsRatioNormalHits.setTitleY("Diff of vz (cm)");
        histoGroupValidTBTrackDiffVsRatioNormalHits.addDataSet(h2_vzValidTBTrackDiffVsRatioNormalHits, 8);                 
        histoGroupMap.put(histoGroupValidTBTrackDiffVsRatioNormalHits.getName(), histoGroupValidTBTrackDiffVsRatioNormalHits); 
        
        // Summary
        HistoGroup histoGroupTrackLostSummary= new HistoGroup("trackLostSummary", 2, 4);
        h1_numClustersOnValidTracks.setLineColor(1);
        histoGroupTrackLostSummary.addDataSet(h1_numClustersOnValidTracks, 0);  
        h1_5or6ClustersLeftDenoising.setLineColor(2);
        histoGroupTrackLostSummary.addDataSet(h1_5or6ClustersLeftDenoising, 1); 
        h1_5or6MatchedClustersWithValidTracks.setLineColor(3);
        histoGroupTrackLostSummary.addDataSet(h1_5or6MatchedClustersWithValidTracks, 2);
        h1_numClustersFullyMatchedAICandidates.setLineColor(4);
        histoGroupTrackLostSummary.addDataSet(h1_numClustersFullyMatchedAICandidates, 3); 
        h1_numClustersFullyMatchedHBTracks.setLineColor(5);
        histoGroupTrackLostSummary.addDataSet(h1_numClustersFullyMatchedHBTracks, 4); 
        h1_numClustersFullyMatchedTBTracks.setLineColor(6);
        histoGroupTrackLostSummary.addDataSet(h1_numClustersFullyMatchedTBTracks, 5);  
        h1_numClustersFullyMatchedValidTBTracks.setLineColor(7);
        histoGroupTrackLostSummary.addDataSet(h1_numClustersFullyMatchedValidTBTracks, 6);         
        histoGroupMap.put(histoGroupTrackLostSummary.getName(), histoGroupTrackLostSummary); 
        
        
        HistoGroup histoGroupTrackLostSummary2= new HistoGroup("trackLostSummary2", 2, 3);
        histoGroupTrackLostSummary2.addDataSet(h1_numClustersOnValidTracks, 0);
        histoGroupTrackLostSummary2.addDataSet(h1_5or6ClustersLeftDenoising, 0);
        histoGroupTrackLostSummary2.addDataSet(h1_5or6MatchedClustersWithValidTracks, 0);
        histoGroupTrackLostSummary2.addDataSet(h1_numClustersFullyMatchedAICandidates, 0); 
        histoGroupTrackLostSummary2.addDataSet(h1_numClustersFullyMatchedHBTracks, 0);
        histoGroupTrackLostSummary2.addDataSet(h1_numClustersFullyMatchedTBTracks, 0);
        histoGroupTrackLostSummary2.addDataSet(h1_numClustersFullyMatchedValidTBTracks, 0);
        histoGroupMap.put(histoGroupTrackLostSummary2.getName(), histoGroupTrackLostSummary2);
        
        HistoGroup histoGroupTrackLostSummary3= new HistoGroup("trackLostSummary3", 1, 3);
        histoGroupMap.put(histoGroupTrackLostSummary3.getName(), histoGroupTrackLostSummary3);
        
    }

    public void processEvent(Event event1, Event event2, int trkType) {
        //////// Read banks
        LocalEvent localEvent1 = new LocalEvent(reader1, event1, trkType, true);
        LocalEvent localEvent2 = new LocalEvent(reader2, event2, trkType, true);  
                
        // Clusters in valid TB tracks with vz, pMin and chipid cuts
        List<Cluster> clustersInValidTBTracks1 = new ArrayList();
        for(Track trk : localEvent1.getTracksTB()){
            if(trk.isValid(true)){
                clustersInValidTBTracks1.addAll(trk.getClusters());
            }
        }
                  
        ////// Make cluster map between clusters in TB tracks of sample1 and clusters from clustering of sample2
        // Priorities for map most matched hits (priority 1), highest normal ratio (priority 2) and closest avgWire (priority 3)  
        Map<Cluster, Cluster> map_cls1_cls2_matched = new HashMap();             
        for(Cluster cls1 : clustersInValidTBTracks1){
            numAllNormalHits[cls1.superlayer()-1]++;
            List<Cluster> matchedClustersWithMostMatchedHits = new ArrayList();
            int maxMatchedHits = -1;
            for(Cluster cls2 : localEvent2.getClusters()){
                if(cls2.getRatioNormalHits() >= ratioNormalHitsCut){
                    int numMatchedHits = cls1.numMatchedHits(cls2);
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
                   if(clsWithCloestAvgWire != null) {
                       map_cls1_cls2_matched.put(cls1, clsWithCloestAvgWire);
                   }
                }
            }             
        }
        
        List<Cluster> clsListExtraSample1 = new ArrayList(); // extra clusters in sample 1
        List<Cluster> clsListExtraSample2 = new ArrayList(); // extra clusters in sample 2  
        clsListExtraSample1.addAll(clustersInValidTBTracks1);
        clsListExtraSample1.removeAll(map_cls1_cls2_matched.keySet());        
        clsListExtraSample2.addAll(localEvent2.getClusters());
        clsListExtraSample2.removeAll(map_cls1_cls2_matched.values());
        
        HistoGroup histoGroupClusterMatchingStatus = histoGroupMap.get("clusterMatchingStatus");                           
        for(Cluster cls1 : clsListExtraSample1){
            histoGroupClusterMatchingStatus.getH1F("clusterMatchingStatus for SL" + cls1.superlayer()).fill(0);
        }
        for(Cluster cls1 : map_cls1_cls2_matched.keySet()){
            histoGroupClusterMatchingStatus.getH1F("clusterMatchingStatus for SL" + cls1.superlayer()).fill(1);
        }
        
        ////// Study clusters exit in sample 1, but lost in sample 2
        HistoGroup histoGroupLostClusters = histoGroupMap.get("lostClusters");
        HistoGroup histoGroupNormalHitsLeftForLostCluters = histoGroupMap.get("normalHitsLeftForLostCluters");  
        Map<Cluster, List<Hit>> map_cls1_hits2 = new HashMap();
        for(Cluster cls : clsListExtraSample1){
            histoGroupLostClusters.getH1F("lostClusters").fill(cls.superlayer());
            
            int numMatchedHits = 0;
            map_cls1_hits2.put(cls, new ArrayList());
            for(Hit hit1 : cls.getHits()){
                for(Hit hit2 : localEvent2.getHits()){
                    if(hit1.hitMatched(hit2)) {
                        map_cls1_hits2.get(cls).add(hit2);
                        numMatchedHits++;
                        break;
                    }
                }
            }
            histoGroupNormalHitsLeftForLostCluters.getH1F("normalHitsLeftForLostCluters for SL"  + cls.superlayer()).fill(numMatchedHits);
            if(numMatchedHits >= 3) numLostClustersWith3MoreNormalHits[cls.superlayer()-1]++;
        }
                                              
        HistoGroup histoGroupProbVsNHitsFor3MoreNormalHitsLeftForLostCluters = histoGroupMap.get("probVsNHitsFor3MoreNormalHitsLeftForLostCluters");
        for(Cluster cls1 : map_cls1_hits2.keySet()){
            if(map_cls1_hits2.get(cls1).size() >= 3){                
                ClusterFitLC fitter = new ClusterFitLC(map_cls1_hits2.get(cls1));
                if(fitter.lineFit()){
                    histoGroupProbVsNHitsFor3MoreNormalHitsLeftForLostCluters.getH2F("probVsNHitsFor3MoreNormalHitsLeftForLostCluters for SL"+cls1.superlayer()).fill(map_cls1_hits2.get(cls1).size(), fitter.getLineFitter().getProb()); 
                }
            }
        }
               
        // Demo special cases      
        List<Integer> demoSectorList = new ArrayList();
        for(Cluster cls1 : map_cls1_hits2.keySet()){
            int sector = cls1.sector();
            if(!demoSectorList.contains(sector) && map_cls1_hits2.get(cls1).size() == 0 && countDemoCases < Constants.MAXDEMOCASES){ 
                numLostEvents.add(localEvent1.getRunConfig().event());
            }
            if(!demoSectorList.contains(sector) && cls1.superlayer() == 1 && map_cls1_hits2.get(cls1).size() >= 3 && countDemoCases < Constants.MAXDEMOCASES){               
                demoSectorList.add(sector);
                //addDemoGroup(localEvent1, localEvent2, sector, "Lost");                
            }
        }
        
        HistoGroup histoGroupClusterSituationSample2 = histoGroupMap.get("clusterSituationSample2");
        for(Cluster cls2: clsListExtraSample2){
            histoGroupClusterSituationSample2.getH1F("clusterSituationSample2 for SL" + cls2.superlayer()).fill(3);
        }
        
        // Study matched clusters
        List<Cluster> allNormalHitsMatchedClusters2 = new ArrayList();
        HistoGroup histoGroupMatchedClusters = histoGroupMap.get("matchedClusters");
        HistoGroup histoGroupMatchedHitRatio = histoGroupMap.get("matchedHitRatio"); 
        HistoGroup histoGroupIfAllNormalHitsMatchedCluster = histoGroupMap.get("ifAllNormalHitsMatchedCluster");
        HistoGroup histoGroupNormalHitRatioForNotAllNormalHitsMatchedCluster = histoGroupMap.get("normalHitRatioForNotAllNormalHitsMatchedCluster");
        HistoGroup histoGroupNormalHitRatioVsSizeForNotAllNormalHitsMatchedCluster = histoGroupMap.get("normalHitRatioVsSizeForNotAllNormalHitsMatchedCluster");
        HistoGroup histoGroupProbVsNormalHitRatioForNotAllNormalHitsMatchedCluster = histoGroupMap.get("probVsNormalHitRatioForNotAllNormalHitsMatchedCluster");
        for(Cluster cls1 : map_cls1_cls2_matched.keySet()){            
            Cluster cls2 = map_cls1_cls2_matched.get(cls1);
            histoGroupMatchedClusters.getH1F("matchedClusters").fill(cls2.superlayer());
            int numMatchedHits = cls1.numMatchedHits(cls2);
            histoGroupMatchedHitRatio.getH2F("matchedHitRatio for SL" + cls1.superlayer()).fill((double)numMatchedHits/cls1.size(), (double)numMatchedHits/cls2.size());
            
            if(cls2.getRatioNormalHits() == 1) {
                histoGroupClusterSituationSample2.getH1F("clusterSituationSample2 for SL" + cls2.superlayer()).fill(1);
                histoGroupIfAllNormalHitsMatchedCluster.getH1F("ifAllNormalHitsMatchedCluster for SL" + cls2.superlayer()).fill(1);
            }
            else {
                histoGroupClusterSituationSample2.getH1F("clusterSituationSample2 for SL" + cls2.superlayer()).fill(2);
                histoGroupIfAllNormalHitsMatchedCluster.getH1F("ifAllNormalHitsMatchedCluster for SL" + cls2.superlayer()).fill(0);
                histoGroupNormalHitRatioForNotAllNormalHitsMatchedCluster.getH1F("normalHitRatioForNotAllNormalHitsMatchedCluster for SL" + cls2.superlayer()).fill(cls2.getRatioNormalHits());                                    
            
                histoGroupNormalHitRatioVsSizeForNotAllNormalHitsMatchedCluster.getH2F("normalHitRatioVsSizeForNotAllNormalHitsMatchedCluster for SL" + cls2.superlayer()).fill(cls2.size(), cls2.getRatioNormalHits());
                ClusterFitLC fitter = new ClusterFitLC(cls2);
                if(fitter.lineFit()){
                    histoGroupProbVsNormalHitRatioForNotAllNormalHitsMatchedCluster.getH2F("probVsNormalHitRatioForNotAllNormalHitsMatchedCluster for SL" + cls2.superlayer()).fill(cls2.getRatioNormalHits(), fitter.getLineFitter().getProb()); 
                }
                
                numMatchedClustersNotAllNormalHits[cls2.superlayer()-1]++;
            }
        }
        
        // Demo special cases    
        for(Cluster cls2 : map_cls1_cls2_matched.values()){
            int sector = cls2.sector();
            if(!demoSectorList.contains(sector) && cls2.superlayer() == 1 && cls2.getRatioNormalHits() < 0.5 && countDemoCases < Constants.MAXDEMOCASES){   
                demoSectorList.add(sector);
                //addDemoGroup(localEvent1, localEvent2, sector, "Mix");                
            }
        }
                
  
       // map between valid track and matched/nomatched cluters
       Map<Track, List<Cluster>> map_validTrack_matchedClusters_clustering = new HashMap();
       Map<Track, List<Integer>> map_validTrack_nomatchedClusters_clustering = new HashMap();
        for(Track trk1 : localEvent1.getTracksTB()){
            if(trk1.isValid(true)){
               map_validTrack_matchedClusters_clustering.put(trk1, new ArrayList());
               map_validTrack_nomatchedClusters_clustering.put(trk1, new ArrayList());
               for(Cluster cls1 : trk1.getClusters()){
                    boolean flag = false;
                    for(Cluster cls1Matched : map_cls1_cls2_matched.keySet()){
                        if(cls1.equals(cls1Matched)){
                            map_validTrack_matchedClusters_clustering.get(trk1).add(map_cls1_cls2_matched.get(cls1Matched));
                            flag = true;
                            break;
                        } 
                    }
                    if(!flag) map_validTrack_nomatchedClusters_clustering.get(trk1).add(cls1.superlayer());
                } 
            }
        }
        
        // Check how much clusters left for valid tracks
        HistoGroup histoGroupClusterLostDenoising = histoGroupMap.get("clusterLostDenoising");  
        
        for(Track trk1 : localEvent1.getTracksTB()){
            if(trk1.isValid(true)){
                histoGroupClusterLostDenoising.getH1F("numClustersOnValidTracks").fill(trk1.nClusters());
                
                int numLostClusters = 0;
                for(Cluster cls1 : map_cls1_hits2.keySet()){
                    if(trk1.getClusters().contains(cls1) && map_cls1_hits2.get(cls1).size() <= 2){
                       numLostClusters++;
                    }
                }  
                histoGroupClusterLostDenoising.getH1F("numLostClusterLostDenoising").fill(numLostClusters);
                
                if(trk1.getClusters().size() == 6){
                    histoGroupClusterLostDenoising.getH1F("numLostClustersOn6ClustersValidTracksDenoising").fill(numLostClusters);
                    if(numLostClusters == 0) histoGroupClusterLostDenoising.getH1F("5or6ClustersLeftDenoising").fill(6);
                    if(numLostClusters == 1) histoGroupClusterLostDenoising.getH1F("5or6ClustersLeftDenoising").fill(5);
                    
                }
                else if(trk1.getClusters().size() == 5){
                    histoGroupClusterLostDenoising.getH1F("numLostClustersOn5ClustersValidTracksDenoising").fill(numLostClusters);
                    if(numLostClusters == 0) histoGroupClusterLostDenoising.getH1F("5or6ClustersLeftDenoising").fill(5);
                }
                    
            }
        }

        // Cluserting level
        HistoGroup histoGroupClustersOnValidTracks = histoGroupMap.get("clustersOnValidTracks");   
        HistoGroup histoGroupNormalHitRatioNoiseClustersfor5or6MatchedClustersWithValidTracks = histoGroupMap.get("normalHitRatioNoiseClustersfor5or6MatchedClustersWithValidTracks"); 
        for(Track trk1 : map_validTrack_matchedClusters_clustering.keySet()){            
            List<Cluster> matchedClusters2 = map_validTrack_matchedClusters_clustering.get(trk1);
            List<Integer> nomatchedClusterSLs = map_validTrack_nomatchedClusters_clustering.get(trk1);
          
                
            histoGroupClustersOnValidTracks.getH1F("numLostClustersOnValidTracks").fill(nomatchedClusterSLs.size());
            if(trk1.nClusters() == 6) histoGroupClustersOnValidTracks.getH1F("numLostClustersOn6ClustersValidTracks").fill(nomatchedClusterSLs.size());
            if(trk1.nClusters() == 5) histoGroupClustersOnValidTracks.getH1F("numLostClustersOn5ClustersValidTracks").fill(nomatchedClusterSLs.size());

            if(nomatchedClusterSLs.size() == 1){
                histoGroupClustersOnValidTracks.getH1F("SLDistFor1LostClusterOnValidTracks").fill(nomatchedClusterSLs.get(0));                                        
            }

            if(nomatchedClusterSLs.size() == 2){
                int lostSL1 = nomatchedClusterSLs.get(0);
                int lostSL2 = nomatchedClusterSLs.get(1);

                if(lostSL1 == 1 && lostSL2 == 2)
                    histoGroupClustersOnValidTracks.getH1F("SLDistFor2LostClustersOnValidTracks").fill(1);  
                else if(lostSL1 == 3 && lostSL2 == 4)
                    histoGroupClustersOnValidTracks.getH1F("SLDistFor2LostClustersOnValidTracks").fill(2);  
                else if(lostSL1 == 5 && lostSL2 == 6)
                    histoGroupClustersOnValidTracks.getH1F("SLDistFor2LostClustersOnValidTracks").fill(3); 

                else if(lostSL1 == 1 && lostSL2 == 3)
                    histoGroupClustersOnValidTracks.getH1F("SLDistFor2LostClustersOnValidTracks").fill(4); 
                else if(lostSL1 == 1 && lostSL2 == 5)
                    histoGroupClustersOnValidTracks.getH1F("SLDistFor2LostClustersOnValidTracks").fill(5); 
                else if(lostSL1 == 2 && lostSL2 == 4)
                    histoGroupClustersOnValidTracks.getH1F("SLDistFor2LostClustersOnValidTracks").fill(6);
                else if(lostSL1 == 2 && lostSL2 == 6)
                    histoGroupClustersOnValidTracks.getH1F("SLDistFor2LostClustersOnValidTracks").fill(7);                    
                else if(lostSL1 == 3 && lostSL2 == 5)
                    histoGroupClustersOnValidTracks.getH1F("SLDistFor2LostClustersOnValidTracks").fill(8); 
                else if(lostSL1 == 4 && lostSL2 == 6)
                    histoGroupClustersOnValidTracks.getH1F("SLDistFor2LostClustersOnValidTracks").fill(9);

                else if(lostSL1 == 1 && lostSL2 == 4)
                    histoGroupClustersOnValidTracks.getH1F("SLDistFor2LostClustersOnValidTracks").fill(10); 
                else if(lostSL1 == 1 && lostSL2 == 6)
                    histoGroupClustersOnValidTracks.getH1F("SLDistFor2LostClustersOnValidTracks").fill(11); 
                else if(lostSL1 == 2 && lostSL2 == 3)
                    histoGroupClustersOnValidTracks.getH1F("SLDistFor2LostClustersOnValidTracks").fill(12); 
                else if(lostSL1 == 2 && lostSL2 == 5)
                    histoGroupClustersOnValidTracks.getH1F("SLDistFor2LostClustersOnValidTracks").fill(13); 
                else if(lostSL1 == 3 && lostSL2 == 6)
                    histoGroupClustersOnValidTracks.getH1F("SLDistFor2LostClustersOnValidTracks").fill(14); 
                else if(lostSL1 == 4 && lostSL2 == 5)
                    histoGroupClustersOnValidTracks.getH1F("SLDistFor2LostClustersOnValidTracks").fill(15); 

            }

            int numNoiseClusters = numNoiseClusters(matchedClusters2);
            if(matchedClusters2.size() == 6){
                histoGroupClustersOnValidTracks.getH1F("numNoiseClustersfor6MatchedClustersWithValidTracks").fill(numNoiseClusters);
                histoGroupClustersOnValidTracks.getH1F("5or6MatchedClustersWithValidTracks").fill(6);  
                for(Cluster cls2 : matchedClusters2){
                    if(cls2.getRatioNormalHits() < 1) 
                        histoGroupNormalHitRatioNoiseClustersfor5or6MatchedClustersWithValidTracks.getH1F("normalHitRatioNoiseClustersfor5or6MatchedClustersWithValidTracks for SL" + cls2.superlayer()).fill(cls2.getRatioNormalHits());
                }
            } 
            
            if(matchedClusters2.size() == 5){
                histoGroupClustersOnValidTracks.getH1F("numNoiseClustersfor5MatchedClustersWithValidTracks").fill(numNoiseClusters);  
                histoGroupClustersOnValidTracks.getH1F("5or6MatchedClustersWithValidTracks").fill(5); 
                for(Cluster cls2 : matchedClusters2){
                    if(cls2.getRatioNormalHits() < 1) 
                        histoGroupNormalHitRatioNoiseClustersfor5or6MatchedClustersWithValidTracks.getH1F("normalHitRatioNoiseClustersfor5or6MatchedClustersWithValidTracks for SL" + cls2.superlayer()).fill(cls2.getRatioNormalHits());
                }
            }             
        }
        
        ////// AI prediction level                
        Map<Track, List<Cluster>> map_validTrack_matchedClusters_AIPrediction = new HashMap(); // map between valid track and matched clusters in cluster combo predicted by AI
        Map<Track, List<Cluster>> map_validTrack_nomatchedClusters_AIPrediction = new HashMap(); // map between valid track and non-matched clusters in cluster combo predicted by AI
        if(trkType == Constants.AITB){
            HistoGroup histoGroupAICandidates = histoGroupMap.get("aICandidates"); 
            HistoGroup histoGroupNormalHitRatioNoiseClustersforFullyMatchedAICandidates = histoGroupMap.get("normalHitRatioNoiseClustersforFullyMatchedAICandidates"); 
            List<AICandidate> aiCandidates2 = localEvent2.getAICands();
            for(AICandidate cand2 : aiCandidates2){                                
                histoGroupAICandidates.getH1F("numClustersAICandidates").fill(cand2.getClusters().size());
            }
            
            // map between matchedCluster and AI predicted cluster combos with most shared clusters
            for(Track trk1 : map_validTrack_matchedClusters_clustering.keySet()){
                List<Cluster> matchedClusters2 = map_validTrack_matchedClusters_clustering.get(trk1);
                List<Cluster> sharedClusters = new ArrayList();
                List<Cluster> nomatchedClusters = new ArrayList();
                boolean flag = false;
                for(AICandidate cand2 : aiCandidates2){ 
                    if(!matchedClusters2.isEmpty() && cand2.sector() == matchedClusters2.get(0).sector()){
                        List<Cluster> sharedClustersTemp = getSharedClusters(matchedClusters2, cand2.getClusters());
                        if(sharedClustersTemp.size() > sharedClusters.size()){
                            flag = true;
                            sharedClusters.clear();
                            sharedClusters.addAll(sharedClustersTemp);
                            nomatchedClusters.clear();
                            nomatchedClusters.addAll(cand2.getClusters());
                            nomatchedClusters.removeAll(sharedClusters);
                        }                            
                    }
                }
                map_validTrack_matchedClusters_AIPrediction.put(trk1, sharedClusters);
                map_validTrack_nomatchedClusters_AIPrediction.put(trk1, nomatchedClusters);
                                                
                if(flag){
                    histoGroupAICandidates.getH1F("numMatchedClustersAICandidates").fill(sharedClusters.size());
                    histoGroupAICandidates.getH2F("numMatchedAICandidatesVsNumMatchedClustering").fill(matchedClusters2.size(), sharedClusters.size());
                    histoGroupAICandidates.getH1F("numNomatchedClustersAICandidates").fill(nomatchedClusters.size());
                    histoGroupAICandidates.getH2F("numNomatchedAICandidatesVsNumMatchedClustering").fill(matchedClusters2.size(), nomatchedClusters.size());
                } else{
                    histoGroupAICandidates.getH1F("numMatchedClustersAICandidates").fill(0);
                    histoGroupAICandidates.getH2F("numMatchedAICandidatesVsNumMatchedClustering").fill(matchedClusters2.size(), 0);
                    histoGroupAICandidates.getH1F("numNomatchedClustersAICandidates").fill(trk1.nClusters());
                    histoGroupAICandidates.getH2F("numNomatchedAICandidatesVsNumMatchedClustering").fill(matchedClusters2.size(), trk1.nClusters());
                }                
                                
                if(sharedClusters.size() >= 5 && nomatchedClusters.size() == 0){
                    histoGroupAICandidates.getH1F("numClustersFullyMatchedAICandidates").fill(sharedClusters.size());
                    
                    for(Cluster cls2 : sharedClusters){
                        if(cls2.getRatioNormalHits() < 1)
                            histoGroupNormalHitRatioNoiseClustersforFullyMatchedAICandidates.getH1F("normalHitRatioNoiseClustersInFullyMatchedAICandidates for SL" + cls2.superlayer()).fill(cls2.getRatioNormalHits());
                    }
                    
                    int numNoiseClusters = numNoiseClusters(sharedClusters);
                    if(sharedClusters.size() == 6){
                        histoGroupAICandidates.getH1F("numNoiseClustersfor6ClustersFullyMatchedAICandidates").fill(numNoiseClusters);                                                
                    }
                    if(sharedClusters.size() == 5){
                        histoGroupAICandidates.getH1F("numNoiseClustersfor5ClustersFullyMatchedAICandidates").fill(numNoiseClusters);                            
                    }                    
                } 
            } 
        }
        else{
           map_validTrack_matchedClusters_AIPrediction.putAll(map_validTrack_matchedClusters_clustering);
           for(Track trk1 : map_validTrack_matchedClusters_clustering.keySet()){
               map_validTrack_nomatchedClusters_AIPrediction.put(trk1, new ArrayList());
           }
        }
        
        ////// HB tracking level               
        Map<Track, List<Cluster>> map_validTrack_matchedClusters_HBTracks = new HashMap(); // map between valid track and matched clusters in clusters of HB tracks
        Map<Track, List<Cluster>> map_validTrack_nomatchedClusters_HBTracks = new HashMap(); // map between valid track and non-matched clusters in clusters of HB tracks
        HistoGroup histoGroupHBTracks = histoGroupMap.get("HBTracks"); 
        HistoGroup histoGroupNormalHitRatioNoiseClustersforFullyMatchedHBTracks = histoGroupMap.get("normalHitRatioNoiseClustersforFullyMatchedHBTracks");         
        HistoGroup histoGroupMatchNoMatchCompHBTracks = histoGroupMap.get("matchNoMatchCompHBTracks");   
        HistoGroup histoGroupHBTrackComp = histoGroupMap.get("HBTrackComp");
        HistoGroup histoGroupHBTrackDiff = histoGroupMap.get("HBTrackDiff");
        HistoGroup histoGroup1NoMatchedCluster5ClustersHBTrackComp = histoGroupMap.get("1NoMatchedCluster5ClustersHBTrackComp");
        HistoGroup histoGroupNoSharedClusterCompOnly1NoMatchedClusterSameSLHBTracks = histoGroupMap.get("noSharedClusterCompOnly1NoMatchedClusterSameSLHBTracks");                
        List<Track> hbTracks2 = localEvent2.getTracksHB();
        for(Track trk2 : hbTracks2){                                
            histoGroupHBTracks.getH1F("numClustersHBTracks").fill(trk2.getClusters().size());
        }

        // map between matchedCluster and clusters on HB tracks with most shared clusters
        for(Track trk1 : map_validTrack_matchedClusters_clustering.keySet()){
            List<Cluster> matchedClusters2 = map_validTrack_matchedClusters_clustering.get(trk1);
            Map<Cluster, Cluster> sharedClusterMap = new HashMap();
            List<Cluster> sharedClusters = new ArrayList();
            List<Cluster> nosharedClustersTrk2 = new ArrayList();
            List<Cluster> nosharedClusters2 = new ArrayList();
            boolean flag = false;
            Track matchedTrk2 = null;
            for(Track trk2 : hbTracks2){ 
                if(!matchedClusters2.isEmpty() && trk2.sector() == matchedClusters2.get(0).sector()){
                    Map<Cluster, Cluster> sharedClusterMapTemp = getSharedClusterMap(matchedClusters2, trk2.getClusters());                    
                    if(sharedClusterMapTemp.size() > sharedClusterMap.size()){
                        flag = true;
                        sharedClusterMap.clear();
                        sharedClusterMap.putAll(sharedClusterMapTemp);
                        nosharedClustersTrk2.clear();
                        nosharedClustersTrk2.addAll(trk2.getClusters());
                        nosharedClustersTrk2.removeAll(sharedClusterMapTemp.values());
                        matchedTrk2 = trk2;
                        
                        nosharedClusters2.clear();
                        nosharedClusters2.addAll(matchedClusters2);
                        nosharedClusters2.removeAll(sharedClusterMapTemp.keySet());
                    }                            
                }
            }
            
            sharedClusters.addAll(sharedClusterMap.values());
            map_validTrack_matchedClusters_HBTracks.put(trk1, sharedClusters);
            map_validTrack_nomatchedClusters_HBTracks.put(trk1, nosharedClustersTrk2);
                        
            if(flag){
                histoGroupHBTracks.getH1F("numMatchedClustersHBTracks").fill(sharedClusters.size());
                histoGroupHBTracks.getH2F("numMatchedHBTracksVsNumMatchedClustering").fill(matchedClusters2.size(), sharedClusters.size());
                histoGroupHBTracks.getH1F("numNomatchedClustersHBTracks").fill(nosharedClustersTrk2.size());            
                histoGroupHBTracks.getH2F("numNomatchedHBTracksVsNumMatchedClustering").fill(matchedClusters2.size(), nosharedClustersTrk2.size());
                                
                if(matchedClusters2.size() >= 5 && nosharedClustersTrk2.size() >= 1){
                    histoGroupMatchNoMatchCompHBTracks.getH1F("chi2OverNdfNomatchHBTracks").fill(matchedTrk2.chi2()/matchedTrk2.NDF());
                }
                
                
                if((matchedClusters2.size() == 6 && sharedClusters.size() == 5 && nosharedClustersTrk2.size() == 1) || (matchedClusters2.size() == 5 && sharedClusters.size() == 4 && nosharedClustersTrk2.size() == 1)){
                    histoGroupMatchNoMatchCompHBTracks.getH1F("only1NoMatchedClusterHBTracks").fill(1);                                        
                    histoGroupMatchNoMatchCompHBTracks.getH2F("SLCompOnly1NoMatchedClusterHBTracks").fill(nosharedClusters2.get(0).superlayer(), nosharedClustersTrk2.get(0).superlayer());
                    
                    if(nosharedClusters2.get(0).superlayer() == nosharedClustersTrk2.get(0).superlayer()){
                        histoGroupMatchNoMatchCompHBTracks.getH1F("only1NoMatchedClusterSameSLHBTracks").fill(1);
                        
                        histoGroupNoSharedClusterCompOnly1NoMatchedClusterSameSLHBTracks.getH2F("avgWireCompNoSharedClusterCompOnly1NoMatchedClusterSameSLHBTracks").fill(nosharedClusters2.get(0).avgWire(), nosharedClustersTrk2.get(0).avgWire());
                        histoGroupNoSharedClusterCompOnly1NoMatchedClusterSameSLHBTracks.getH2F("slopeCompNoSharedClusterCompOnly1NoMatchedClusterSameSLHBTracks").fill(nosharedClusters2.get(0).fitSlope(), nosharedClustersTrk2.get(0).fitSlope());
                        histoGroupNoSharedClusterCompOnly1NoMatchedClusterSameSLHBTracks.getH2F("ratioNormalHitsCompNoSharedClusterCompOnly1NoMatchedClusterSameSLHBTracks").fill(nosharedClusters2.get(0).getRatioNormalHits(), nosharedClustersTrk2.get(0).getRatioNormalHits());
                                                
                        
                        histoGroupNoSharedClusterCompOnly1NoMatchedClusterSameSLHBTracks.getH1F("avgWireDiffNoSharedClusterCompOnly1NoMatchedClusterSameSLHBTracks").fill(nosharedClusters2.get(0).avgWire() - nosharedClustersTrk2.get(0).avgWire());
                        histoGroupNoSharedClusterCompOnly1NoMatchedClusterSameSLHBTracks.getH1F("slopeDiffNoSharedClusterCompOnly1NoMatchedClusterSameSLHBTracks").fill(nosharedClusters2.get(0).fitSlope() - nosharedClustersTrk2.get(0).fitSlope());
                        histoGroupNoSharedClusterCompOnly1NoMatchedClusterSameSLHBTracks.getH1F("numSharedHitsNoSharedClusterCompOnly1NoMatchedClusterSameSLHBTracks").fill(nosharedClusters2.get(0).numMatchedHits(nosharedClustersTrk2.get(0)));                        
                        
                        addDemoGroup(localEvent1, localEvent2, trk1.sector(), "SameSL" + nosharedClusters2.get(0).superlayer());
                    }
                    else{
                       histoGroupMatchNoMatchCompHBTracks.getH1F("only1NoMatchedClusterSameSLHBTracks").fill(0); 
                       addDemoGroup(localEvent1, localEvent2, trk1.sector(), "DiffSL" + nosharedClusters2.get(0).superlayer()+"SL"+nosharedClustersTrk2.get(0).superlayer());
                    }
                    
                    //addDemoGroup(localEvent1, localEvent2, trk1.sector(), "contaminatedHBTracksSL"+nosharedClustersTrk2.get(0).superlayer()+"ch"+(matchedTrk2.chi2()/matchedTrk2.NDF()));
                    if((matchedClusters2.size() == 5 && sharedClusters.size() == 4 && nosharedClustersTrk2.size() == 1)){
                        
                        histoGroup1NoMatchedCluster5ClustersHBTrackComp.getH2F("chi2overndf1NoMatchedCluster5ClustersHBTrackComp").fill(trk1.chi2()/trk1.NDF(), matchedTrk2.chi2()/matchedTrk2.NDF());                    
                        histoGroup1NoMatchedCluster5ClustersHBTrackComp.getH2F("chi2pid1NoMatchedCluster5ClustersHBTrackComp").fill(trk1.chi2pid(), matchedTrk2.chi2pid());                       
                        histoGroup1NoMatchedCluster5ClustersHBTrackComp.getH2F("p1NoMatchedCluster5ClustersHBTrackComp").fill(trk1.p(), matchedTrk2.p());                    
                        histoGroup1NoMatchedCluster5ClustersHBTrackComp.getH2F("theta1NoMatchedCluster5ClustersHBTrackComp").fill(trk1.theta(), matchedTrk2.theta());                    
                        histoGroup1NoMatchedCluster5ClustersHBTrackComp.getH2F("phi1NoMatchedCluster5ClustersHBTrackComp").fill(trk1.phi(), matchedTrk2.phi());                    
                        histoGroup1NoMatchedCluster5ClustersHBTrackComp.getH2F("vx1NoMatchedCluster5ClustersHBTrackComp").fill(trk1.vx(), matchedTrk2.vx());                    
                        histoGroup1NoMatchedCluster5ClustersHBTrackComp.getH2F("vy1NoMatchedCluster5ClustersHBTrackComp").fill(trk1.vy(), matchedTrk2.vy());                    
                        histoGroup1NoMatchedCluster5ClustersHBTrackComp.getH2F("vz1NoMatchedCluster5ClustersHBTrackComp").fill(trk1.vz(), matchedTrk2.vz()); 
                        histoGroup1NoMatchedCluster5ClustersHBTrackComp.getH2F("pid1NoMatchedCluster5ClustersHBTrackComp").fill(trk1.pid()/100., matchedTrk2.pid()/100.);                                                                  

                        if(trk1.pid() == matchedTrk2.pid()){
                            histoGroup1NoMatchedCluster5ClustersHBTrackComp.getH1F("pidStatus1NoMatchedCluster5ClustersHBTrack").fill(1); 
                        }
                        else{
                            histoGroup1NoMatchedCluster5ClustersHBTrackComp.getH1F("pidStatus1NoMatchedCluster5ClustersHBTrack").fill(0);                                        
                        }                        
                                                 
                    }                                          
                }
                else if(matchedClusters2.size() <= 4 || (matchedClusters2.size() == 6 && sharedClusters.size() < 5 && nosharedClustersTrk2.size() > 1) | (matchedClusters2.size() == 5 && sharedClusters.size() < 4 && nosharedClustersTrk2.size() > 1)){
                    histoGroupMatchNoMatchCompHBTracks.getH1F("only1NoMatchedClusterHBTracks").fill(0);
                    
                    addDemoGroup(localEvent1, localEvent2, trk1.sector(), "#M" + matchedClusters2.size() + "#S" + sharedClusters.size() + "#NC" + nosharedClusters2.size() + "#NT" + nosharedClustersTrk2.size());
                }
            } else {                
                histoGroupMatchNoMatchCompHBTracks.getH1F("only1NoMatchedClusterHBTracks").fill(0);
                histoGroupHBTracks.getH1F("numMatchedClustersHBTracks").fill(0);
                histoGroupHBTracks.getH2F("numMatchedHBTracksVsNumMatchedClustering").fill(matchedClusters2.size(), 0);                
                histoGroupHBTracks.getH1F("numNomatchedClustersHBTracks").fill(trk1.nClusters());            
                histoGroupHBTracks.getH2F("numNomatchedHBTracksVsNumMatchedClustering").fill(matchedClusters2.size(), trk1.nClusters());
            }            

            if(sharedClusters.size() >= 5 && nosharedClustersTrk2.size() == 0){
                histoGroupHBTracks.getH1F("numClustersFullyMatchedHBTracks").fill(sharedClusters.size());                 
                histoGroupMatchNoMatchCompHBTracks.getH1F("chi2OverNdfMatchHBTracks").fill(matchedTrk2.chi2()/matchedTrk2.NDF());
                
                //if(matchedTrk2.chi2()/matchedTrk2.NDF() > 20) addDemoGroup(localEvent1, localEvent2, trk1.sector(), "fullyMatchedHBTracksLarger20RNH" + matchedTrk2.getRatioNormalHits()); 
                
                if(matchedTrk2 != null){
                    histoGroupHBTrackComp.getH2F("chi2overndfHBTrackComp").fill(trk1.chi2()/trk1.NDF(), matchedTrk2.chi2()/matchedTrk2.NDF());                    
                    histoGroupHBTrackComp.getH2F("chi2pidHBTrackComp").fill(trk1.chi2pid(), matchedTrk2.chi2pid());                       
                    histoGroupHBTrackComp.getH2F("pHBTrackComp").fill(trk1.p(), matchedTrk2.p());                    
                    histoGroupHBTrackComp.getH2F("thetaHBTrackComp").fill(trk1.theta(), matchedTrk2.theta());                    
                    histoGroupHBTrackComp.getH2F("phiHBTrackComp").fill(trk1.phi(), matchedTrk2.phi());                    
                    histoGroupHBTrackComp.getH2F("vxHBTrackComp").fill(trk1.vx(), matchedTrk2.vx());                    
                    histoGroupHBTrackComp.getH2F("vyHBTrackComp").fill(trk1.vy(), matchedTrk2.vy());                    
                    histoGroupHBTrackComp.getH2F("vzHBTrackComp").fill(trk1.vz(), matchedTrk2.vz()); 
                    histoGroupHBTrackComp.getH2F("pidHBTrackComp").fill(trk1.pid()/100., matchedTrk2.pid()/100.);  
                                        
                    histoGroupHBTrackDiff.getH1F("chi2overndfHBTrackDiff").fill(trk1.chi2()/trk1.NDF() - matchedTrk2.chi2()/matchedTrk2.NDF());                    
                    histoGroupHBTrackDiff.getH1F("chi2pidHBTrackDiff").fill(trk1.chi2pid() - matchedTrk2.chi2pid());                    
                    histoGroupHBTrackDiff.getH1F("pHBTrackDiff").fill(trk1.p() - matchedTrk2.p());                    
                    histoGroupHBTrackDiff.getH1F("thetaHBTrackDiff").fill(trk1.theta() - matchedTrk2.theta());                    
                    histoGroupHBTrackDiff.getH1F("phiHBTrackDiff").fill(trk1.phi() - matchedTrk2.phi());                    
                    histoGroupHBTrackDiff.getH1F("vxHBTrackDiff").fill(trk1.vx() - matchedTrk2.vx());                    
                    histoGroupHBTrackDiff.getH1F("vyHBTrackDiff").fill(trk1.vy() - matchedTrk2.vy());                    
                    histoGroupHBTrackDiff.getH1F("vzHBTrackDiff").fill(trk1.vz() - matchedTrk2.vz());
                    
                    if(trk1.pid() == matchedTrk2.pid()){
                        histoGroupHBTrackComp.getH1F("pidStatusHBTrack").fill(1); 
                    }
                    else{
                        histoGroupHBTrackComp.getH1F("pidStatusHBTrack").fill(0);
                        histoGroupHBTrackComp.getH1F("ratioNormalHitsDiffPIdHBTrack").fill(matchedTrk2.getRatioNormalHits());                    
                    }
                }
                
                

                for(Cluster cls2 : sharedClusters){
                    if(cls2.getRatioNormalHits() < 1)
                        histoGroupNormalHitRatioNoiseClustersforFullyMatchedHBTracks.getH1F("normalHitRatioNoiseClustersInFullyMatchedHBTracks for SL" + cls2.superlayer()).fill(cls2.getRatioNormalHits());
                }

                int numNoiseClusters = numNoiseClusters(sharedClusters);
                if(sharedClusters.size() == 6){
                    histoGroupHBTracks.getH1F("numNoiseClustersfor6ClustersFullyMatchedHBTracks").fill(numNoiseClusters);                                                
                }
                if(sharedClusters.size() == 5){
                    histoGroupHBTracks.getH1F("numNoiseClustersfor5ClustersFullyMatchedHBTracks").fill(numNoiseClusters);                            
                }                    
            } 
            
                       
        }         
        
        ////// TB tracking level               
        Map<Track, List<Cluster>> map_validTrack_matchedClusters_TBTracks = new HashMap(); // map between valid track and matched clusters in clusters of TB tracks
        Map<Track, List<Cluster>> map_validTrack_nomatchedClusters_TBTracks = new HashMap(); // map between valid track and non-matched clusters in clusters of TB tracks
        Map<Track, Track> map_validTrack_matchedValidTrack = new HashMap(); // map between valid track and matched valid track between two samples
        HistoGroup histoGroupTBTracks = histoGroupMap.get("TBTracks"); 
        HistoGroup histoGroupNormalHitRatioNoiseClustersforFullyMatchedTBTracks = histoGroupMap.get("normalHitRatioNoiseClustersforFullyMatchedTBTracks"); 
        HistoGroup histoGroupDAFWeightNormalClustersforFullyMatchedTBTracks = histoGroupMap.get("DAFWeightNormalClustersforFullyMatchedTBTracks"); 
        HistoGroup histoGroupDAFWeightNoiseClustersforFullyMatchedTBTracks = histoGroupMap.get("DAFWeightNoiseClustersforFullyMatchedTBTracks"); 
        HistoGroup histoGroupTBTrackComp = histoGroupMap.get("TBTrackComp");
        HistoGroup histoGroupTBTrackDiff = histoGroupMap.get("TBTrackDiff");
        HistoGroup histoGroupTBTrackDiffDiffPId = histoGroupMap.get("TBTrackDiffDiffPId");
        HistoGroup histoGroupTBTrackDiffVsRatioNormalHits = histoGroupMap.get("TBTrackDiffVsRatioNormalHits");
        
        List<Track> tbTracks2 = localEvent2.getTracksTB();       
        for(Track trk2 : tbTracks2){                                
            histoGroupTBTracks.getH1F("numClustersTBTracks").fill(trk2.getClusters().size());
        }

        // map between matchedCluster and clusters on TB tracks with most shared clusters
        for(Track trk1 : map_validTrack_matchedClusters_clustering.keySet()){
            List<Cluster> matchedClusters2 = map_validTrack_matchedClusters_clustering.get(trk1);
            List<Cluster> sharedClusters = new ArrayList();
            List<Cluster> nomatchedClusters = new ArrayList();
            boolean flag = false;
            Track matchedTrk2 = null;
            for(Track trk2 : tbTracks2){ 
                if(!matchedClusters2.isEmpty() && trk2.sector() == matchedClusters2.get(0).sector()){
                    List<Cluster> sharedClustersTemp = getSharedClusters(matchedClusters2, trk2.getClusters());
                    if(sharedClustersTemp.size() > sharedClusters.size()){
                        flag = true;
                        sharedClusters.clear();
                        sharedClusters.addAll(sharedClustersTemp);
                        nomatchedClusters.clear();
                        nomatchedClusters.addAll(trk2.getClusters());
                        nomatchedClusters.removeAll(sharedClusters);  
                        matchedTrk2 = trk2;
                    }                            
                }
            }
            map_validTrack_matchedClusters_TBTracks.put(trk1, sharedClusters);
            map_validTrack_nomatchedClusters_TBTracks.put(trk1, nomatchedClusters);          
            
            if(flag){
                histoGroupTBTracks.getH1F("numMatchedClustersTBTracks").fill(sharedClusters.size());
                histoGroupTBTracks.getH2F("numMatchedTBTracksVsNumMatchedClustering").fill(matchedClusters2.size(), sharedClusters.size());
                histoGroupTBTracks.getH1F("numNomatchedClustersTBTracks").fill(nomatchedClusters.size());            
                histoGroupTBTracks.getH2F("numNomatchedTBTracksVsNumMatchedClustering").fill(matchedClusters2.size(), nomatchedClusters.size());
            } else {
                histoGroupTBTracks.getH1F("numMatchedClustersTBTracks").fill(0);
                histoGroupTBTracks.getH2F("numMatchedTBTracksVsNumMatchedClustering").fill(matchedClusters2.size(), 0);
                histoGroupTBTracks.getH1F("numNomatchedClustersTBTracks").fill(trk1.nClusters());            
                histoGroupTBTracks.getH2F("numNomatchedTBTracksVsNumMatchedClustering").fill(matchedClusters2.size(), trk1.nClusters());
            }

            if(sharedClusters.size() >= 5 && nomatchedClusters.size() == 0){
                histoGroupTBTracks.getH1F("numClustersFullyMatchedTBTracks").fill(sharedClusters.size());
                map_validTrack_matchedValidTrack.put(trk1, matchedTrk2);
                
                if(matchedTrk2 != null){
                    histoGroupTBTrackComp.getH2F("chi2overndfTBTrackComp").fill(trk1.chi2()/trk1.NDF(), matchedTrk2.chi2()/matchedTrk2.NDF());                    
                    histoGroupTBTrackComp.getH2F("chi2pidTBTrackComp").fill(trk1.chi2pid(), matchedTrk2.chi2pid()); 
                    histoGroupTBTrackComp.getH1F("ratioNormalHitsTBTrack").fill(matchedTrk2.getRatioNormalHits());                     
                    histoGroupTBTrackComp.getH2F("pTBTrackComp").fill(trk1.p(), matchedTrk2.p());                    
                    histoGroupTBTrackComp.getH2F("thetaTBTrackComp").fill(trk1.theta(), matchedTrk2.theta());                    
                    histoGroupTBTrackComp.getH2F("phiTBTrackComp").fill(trk1.phi(), matchedTrk2.phi());                    
                    histoGroupTBTrackComp.getH2F("vxTBTrackComp").fill(trk1.vx(), matchedTrk2.vx());                    
                    histoGroupTBTrackComp.getH2F("vyTBTrackComp").fill(trk1.vy(), matchedTrk2.vy());                    
                    histoGroupTBTrackComp.getH2F("vzTBTrackComp").fill(trk1.vz(), matchedTrk2.vz()); 
                    histoGroupTBTrackComp.getH2F("pidTBTrackComp").fill(trk1.pid()/100., matchedTrk2.pid()/100.);   
                                                               
                    histoGroupTBTrackDiff.getH1F("chi2overndfTBTrackDiff").fill(trk1.chi2()/trk1.NDF() - matchedTrk2.chi2()/matchedTrk2.NDF());                    
                    histoGroupTBTrackDiff.getH1F("chi2pidTBTrackDiff").fill(trk1.chi2pid() - matchedTrk2.chi2pid());                    
                    histoGroupTBTrackDiff.getH1F("pTBTrackDiff").fill(trk1.p() - matchedTrk2.p());                    
                    histoGroupTBTrackDiff.getH1F("thetaTBTrackDiff").fill(trk1.theta() - matchedTrk2.theta());                    
                    histoGroupTBTrackDiff.getH1F("phiTBTrackDiff").fill(trk1.phi() - matchedTrk2.phi());                    
                    histoGroupTBTrackDiff.getH1F("vxTBTrackDiff").fill(trk1.vx() - matchedTrk2.vx());                    
                    histoGroupTBTrackDiff.getH1F("vyTBTrackDiff").fill(trk1.vy() - matchedTrk2.vy());                    
                    histoGroupTBTrackDiff.getH1F("vzTBTrackDiff").fill(trk1.vz() - matchedTrk2.vz());
                    
                    histoGroupTBTrackDiffVsRatioNormalHits.getH2F("chi2overndfTBTrackDiffVsRatioNormalHits").fill(matchedTrk2.getRatioNormalHits(), trk1.chi2()/trk1.NDF() - matchedTrk2.chi2()/matchedTrk2.NDF());                    
                    histoGroupTBTrackDiffVsRatioNormalHits.getH2F("chi2pidTBTrackDiffVsRatioNormalHits").fill(matchedTrk2.getRatioNormalHits(), trk1.chi2pid() - matchedTrk2.chi2pid());                    
                    histoGroupTBTrackDiffVsRatioNormalHits.getH2F("pTBTrackDiffVsRatioNormalHits").fill(matchedTrk2.getRatioNormalHits(), trk1.p() - matchedTrk2.p());                    
                    histoGroupTBTrackDiffVsRatioNormalHits.getH2F("thetaTBTrackDiffVsRatioNormalHits").fill(matchedTrk2.getRatioNormalHits(), trk1.theta() - matchedTrk2.theta());                    
                    histoGroupTBTrackDiffVsRatioNormalHits.getH2F("phiTBTrackDiffVsRatioNormalHits").fill(matchedTrk2.getRatioNormalHits(), trk1.phi() - matchedTrk2.phi());                    
                    histoGroupTBTrackDiffVsRatioNormalHits.getH2F("vxTBTrackDiffVsRatioNormalHits").fill(matchedTrk2.getRatioNormalHits(), trk1.vx() - matchedTrk2.vx());                    
                    histoGroupTBTrackDiffVsRatioNormalHits.getH2F("vyTBTrackDiffVsRatioNormalHits").fill(matchedTrk2.getRatioNormalHits(), trk1.vy() - matchedTrk2.vy());                    
                    histoGroupTBTrackDiffVsRatioNormalHits.getH2F("vzTBTrackDiffVsRatioNormalHits").fill(matchedTrk2.getRatioNormalHits(), trk1.vz() - matchedTrk2.vz());
                    
                    if(trk1.pid() == matchedTrk2.pid()){
                        histoGroupTBTrackComp.getH1F("pidStatusTBTrack").fill(1); 
                    }
                    else{
                        histoGroupTBTrackComp.getH1F("pidStatusTBTrack").fill(0);
                        histoGroupTBTrackComp.getH1F("ratioNormalHitsDiffPIdTBTrack").fill(matchedTrk2.getRatioNormalHits());
                        
                        histoGroupTBTrackDiffDiffPId.getH1F("chi2overndfTBTrackDiffDiffPId").fill(trk1.chi2()/trk1.NDF() - matchedTrk2.chi2()/matchedTrk2.NDF());                    
                        histoGroupTBTrackDiffDiffPId.getH1F("chi2pidTBTrackDiffDiffPId").fill(trk1.chi2pid() - matchedTrk2.chi2pid());                    
                        histoGroupTBTrackDiffDiffPId.getH1F("pTBTrackDiffDiffPId").fill(trk1.p() - matchedTrk2.p());                    
                        histoGroupTBTrackDiffDiffPId.getH1F("thetaTBTrackDiffDiffPId").fill(trk1.theta() - matchedTrk2.theta());                    
                        histoGroupTBTrackDiffDiffPId.getH1F("phiTBTrackDiffDiffPId").fill(trk1.phi() - matchedTrk2.phi());                    
                        histoGroupTBTrackDiffDiffPId.getH1F("vxTBTrackDiffDiffPId").fill(trk1.vx() - matchedTrk2.vx());                    
                        histoGroupTBTrackDiffDiffPId.getH1F("vyTBTrackDiffDiffPId").fill(trk1.vy() - matchedTrk2.vy());                    
                        histoGroupTBTrackDiffDiffPId.getH1F("vzTBTrackDiffDiffPId").fill(trk1.vz() - matchedTrk2.vz());                        
                    }
                }

                for(Cluster cls2 : sharedClusters){
                    if(cls2.getRatioNormalHits() < 1)
                        histoGroupNormalHitRatioNoiseClustersforFullyMatchedTBTracks.getH1F("normalHitRatioNoiseClustersInFullyMatchedTBTracks for SL" + cls2.superlayer()).fill(cls2.getRatioNormalHits());
                    
                    for(Hit hit2 : cls2.getHits()){
                        if(hit2.isNormalHit()) 
                            histoGroupDAFWeightNormalClustersforFullyMatchedTBTracks.getH1F("DAFWeightNormalClustersInFullyMatchedTBTracks for SL" + cls2.superlayer()).fill(hit2.dafWeight());
                        else
                            histoGroupDAFWeightNoiseClustersforFullyMatchedTBTracks.getH1F("DAFWeightNoiseClustersInFullyMatchedTBTracks for SL" + cls2.superlayer()).fill(hit2.dafWeight());
                    }
                }

                int numNoiseClusters = numNoiseClusters(sharedClusters);
                if(sharedClusters.size() == 6){
                    histoGroupTBTracks.getH1F("numNoiseClustersfor6ClustersFullyMatchedTBTracks").fill(numNoiseClusters);                                                
                }
                if(sharedClusters.size() == 5){
                    histoGroupTBTracks.getH1F("numNoiseClustersfor5ClustersFullyMatchedTBTracks").fill(numNoiseClusters);                            
                }                    
            }
        }
        
        // Valid TB tracks
        HistoGroup histoGroupValidTBTracks = histoGroupMap.get("ValidTBTracks"); 
        HistoGroup histoGroupNormalHitRatioNoiseClustersforFullyMatchedValidTBTracks = histoGroupMap.get("normalHitRatioNoiseClustersforFullyMatchedValidTBTracks"); 
        HistoGroup histoGroupDAFWeightNormalClustersforFullyMatchedValidTBTracks = histoGroupMap.get("DAFWeightNormalClustersforFullyMatchedValidTBTracks"); 
        HistoGroup histoGroupDAFWeightNoiseClustersforFullyMatchedValidTBTracks = histoGroupMap.get("DAFWeightNoiseClustersforFullyMatchedValidTBTracks"); 
        HistoGroup histoGroupValidTBTrackComp = histoGroupMap.get("ValidTBTrackComp");
        HistoGroup histoGroupValidTBTrackDiff = histoGroupMap.get("ValidTBTrackDiff");
        HistoGroup histoGroupValidTBTrackDiffDiffPId = histoGroupMap.get("ValidTBTrackDiffDiffPId");
        HistoGroup histoGroupValidTBTrackDiffVsRatioNormalHits = histoGroupMap.get("ValidTBTrackDiffVsRatioNormalHits");
        for(Track trk1 : map_validTrack_matchedValidTrack.keySet()){
            Track matchedTrk2 = map_validTrack_matchedValidTrack.get(trk1);
            if(matchedTrk2.isValid()){
                histoGroupValidTBTracks.getH1F("numClustersFullyMatchedValidTBTracks").fill(matchedTrk2.nClusters());
                
                histoGroupValidTBTrackComp.getH2F("chi2overndfValidTBTrackComp").fill(trk1.chi2()/trk1.NDF(), matchedTrk2.chi2()/matchedTrk2.NDF());                    
                histoGroupValidTBTrackComp.getH2F("chi2pidValidTBTrackComp").fill(trk1.chi2pid(), matchedTrk2.chi2pid());                       
                histoGroupValidTBTrackComp.getH2F("pValidTBTrackComp").fill(trk1.p(), matchedTrk2.p());                    
                histoGroupValidTBTrackComp.getH2F("thetaValidTBTrackComp").fill(trk1.theta(), matchedTrk2.theta());                    
                histoGroupValidTBTrackComp.getH2F("phiValidTBTrackComp").fill(trk1.phi(), matchedTrk2.phi());                    
                histoGroupValidTBTrackComp.getH2F("vxValidTBTrackComp").fill(trk1.vx(), matchedTrk2.vx());                    
                histoGroupValidTBTrackComp.getH2F("vyValidTBTrackComp").fill(trk1.vy(), matchedTrk2.vy());                    
                histoGroupValidTBTrackComp.getH2F("vzValidTBTrackComp").fill(trk1.vz(), matchedTrk2.vz()); 
                histoGroupValidTBTrackComp.getH2F("pidValidTBTrackComp").fill(trk1.pid()/100., matchedTrk2.pid()/100.);   

                histoGroupValidTBTrackDiff.getH1F("chi2overndfValidTBTrackDiff").fill(trk1.chi2()/trk1.NDF() - matchedTrk2.chi2()/matchedTrk2.NDF());                    
                histoGroupValidTBTrackDiff.getH1F("chi2pidValidTBTrackDiff").fill(trk1.chi2pid() - matchedTrk2.chi2pid());                    
                histoGroupValidTBTrackDiff.getH1F("pValidTBTrackDiff").fill(trk1.p() - matchedTrk2.p());                    
                histoGroupValidTBTrackDiff.getH1F("thetaValidTBTrackDiff").fill(trk1.theta() - matchedTrk2.theta());                    
                histoGroupValidTBTrackDiff.getH1F("phiValidTBTrackDiff").fill(trk1.phi() - matchedTrk2.phi());                    
                histoGroupValidTBTrackDiff.getH1F("vxValidTBTrackDiff").fill(trk1.vx() - matchedTrk2.vx());                    
                histoGroupValidTBTrackDiff.getH1F("vyValidTBTrackDiff").fill(trk1.vy() - matchedTrk2.vy());                    
                histoGroupValidTBTrackDiff.getH1F("vzValidTBTrackDiff").fill(trk1.vz() - matchedTrk2.vz());

                histoGroupValidTBTrackDiffVsRatioNormalHits.getH2F("chi2overndfValidTBTrackDiffVsRatioNormalHits").fill(matchedTrk2.getRatioNormalHits(), trk1.chi2()/trk1.NDF() - matchedTrk2.chi2()/matchedTrk2.NDF());                    
                histoGroupValidTBTrackDiffVsRatioNormalHits.getH2F("chi2pidValidTBTrackDiffVsRatioNormalHits").fill(matchedTrk2.getRatioNormalHits(), trk1.chi2pid() - matchedTrk2.chi2pid());                    
                histoGroupValidTBTrackDiffVsRatioNormalHits.getH2F("pValidTBTrackDiffVsRatioNormalHits").fill(matchedTrk2.getRatioNormalHits(), trk1.p() - matchedTrk2.p());                    
                histoGroupValidTBTrackDiffVsRatioNormalHits.getH2F("thetaValidTBTrackDiffVsRatioNormalHits").fill(matchedTrk2.getRatioNormalHits(), trk1.theta() - matchedTrk2.theta());                    
                histoGroupValidTBTrackDiffVsRatioNormalHits.getH2F("phiValidTBTrackDiffVsRatioNormalHits").fill(matchedTrk2.getRatioNormalHits(), trk1.phi() - matchedTrk2.phi());                    
                histoGroupValidTBTrackDiffVsRatioNormalHits.getH2F("vxValidTBTrackDiffVsRatioNormalHits").fill(matchedTrk2.getRatioNormalHits(), trk1.vx() - matchedTrk2.vx());                    
                histoGroupValidTBTrackDiffVsRatioNormalHits.getH2F("vyValidTBTrackDiffVsRatioNormalHits").fill(matchedTrk2.getRatioNormalHits(), trk1.vy() - matchedTrk2.vy());                    
                histoGroupValidTBTrackDiffVsRatioNormalHits.getH2F("vzValidTBTrackDiffVsRatioNormalHits").fill(matchedTrk2.getRatioNormalHits(), trk1.vz() - matchedTrk2.vz());

                if(trk1.pid() == matchedTrk2.pid()){
                    histoGroupValidTBTrackComp.getH1F("pidStatusValidTBTrack").fill(1); 
                }
                else{
                    histoGroupValidTBTrackComp.getH1F("pidStatusValidTBTrack").fill(0);
                    histoGroupValidTBTrackComp.getH1F("ratioNormalHitsDiffPIdValidTBTrack").fill(matchedTrk2.getRatioNormalHits());

                    histoGroupValidTBTrackDiffDiffPId.getH1F("chi2overndfValidTBTrackDiffDiffPId").fill(trk1.chi2()/trk1.NDF() - matchedTrk2.chi2()/matchedTrk2.NDF());                    
                    histoGroupValidTBTrackDiffDiffPId.getH1F("chi2pidValidTBTrackDiffDiffPId").fill(trk1.chi2pid() - matchedTrk2.chi2pid());                    
                    histoGroupValidTBTrackDiffDiffPId.getH1F("pValidTBTrackDiffDiffPId").fill(trk1.p() - matchedTrk2.p());                    
                    histoGroupValidTBTrackDiffDiffPId.getH1F("thetaValidTBTrackDiffDiffPId").fill(trk1.theta() - matchedTrk2.theta());                    
                    histoGroupValidTBTrackDiffDiffPId.getH1F("phiValidTBTrackDiffDiffPId").fill(trk1.phi() - matchedTrk2.phi());                    
                    histoGroupValidTBTrackDiffDiffPId.getH1F("vxValidTBTrackDiffDiffPId").fill(trk1.vx() - matchedTrk2.vx());                    
                    histoGroupValidTBTrackDiffDiffPId.getH1F("vyValidTBTrackDiffDiffPId").fill(trk1.vy() - matchedTrk2.vy());                    
                    histoGroupValidTBTrackDiffDiffPId.getH1F("vzValidTBTrackDiffDiffPId").fill(trk1.vz() - matchedTrk2.vz());                        
                }
                
                for(Cluster cls2 : matchedTrk2.getClusters()){
                    if(cls2.getRatioNormalHits() < 1)
                        histoGroupNormalHitRatioNoiseClustersforFullyMatchedValidTBTracks.getH1F("normalHitRatioNoiseClustersInFullyMatchedValidTBTracks for SL" + cls2.superlayer()).fill(cls2.getRatioNormalHits());
                    
                    for(Hit hit2 : cls2.getHits()){
                        if(hit2.isNormalHit()) 
                            histoGroupDAFWeightNormalClustersforFullyMatchedValidTBTracks.getH1F("DAFWeightNormalClustersInFullyMatchedValidTBTracks for SL" + cls2.superlayer()).fill(hit2.dafWeight());
                        else
                            histoGroupDAFWeightNoiseClustersforFullyMatchedValidTBTracks.getH1F("DAFWeightNoiseClustersInFullyMatchedValidTBTracks for SL" + cls2.superlayer()).fill(hit2.dafWeight());
                    }
                }
                
                int numNoiseClusters = numNoiseClusters(matchedTrk2.getClusters());
                if(matchedTrk2.nClusters() == 6){
                    histoGroupValidTBTracks.getH1F("numNoiseClustersfor6ClustersFullyMatchedValidTBTracks").fill(numNoiseClusters);                                                
                }
                if(matchedTrk2.nClusters() == 5){
                    histoGroupValidTBTracks.getH1F("numNoiseClustersfor5ClustersFullyMatchedValidTBTracks").fill(numNoiseClusters);                            
                } 
            }
            else{
                //addDemoGroup(localEvent1, localEvent2, matchedTrk2.sector(), "noValid"); 
            }
            
        }
                                               
                
    }
    
    public int numNoiseClusters(List<Cluster> clusters){
        int numNoiseClusters = 0;
        for(Cluster cls : clusters){
            if(cls.getRatioNormalHits() < 1) {
                numNoiseClusters++;
            }
        }
        return numNoiseClusters;
    }
    
    public List<Cluster> getSharedClusters(List<Cluster> clusters1, List<Cluster> clusters2){
        List<Cluster> sharedClusters = new ArrayList();
        for(Cluster cls1 : clusters1){
            for(Cluster cls2 : clusters2){
                if(cls2.id() == cls1.id()){
                    sharedClusters.add(cls2);
                    break;
                }
            }
        }
        
        return sharedClusters;
    }
    
    public Map<Cluster, Cluster> getSharedClusterMap(List<Cluster> clusters1, List<Cluster> clusters2){
        Map<Cluster, Cluster> sharedClusterMap = new HashMap();
        for(Cluster cls1 : clusters1){
            for(Cluster cls2 : clusters2){
                if(cls2.id() == cls1.id()){
                    sharedClusterMap.put(cls1, cls2);
                    break;
                }
            }
        }
        
        return sharedClusterMap;
    }

    public void postEventProcess() {                         
        H1F h1_allNormalClusters = new H1F("allNormalClusters", "normal clusters", 6, 0.5, 6.5);
        h1_allNormalClusters.setTitleX("SL");
        h1_allNormalClusters.setTitleY("Counts");
        h1_allNormalClusters.setLineColor(1);
        for(int i = 0; i < 6; i++){
            h1_allNormalClusters.setBinContent(i, numAllNormalHits[i]);
        }
        histoGroupEstimatePotentialRestorableClusters.addDataSet(h1_allNormalClusters, 0);
        H1F h1_lostClustersWith3MoreNormalHits = new H1F("lostClustersWith3MoreNormalHits", "lost clusters with 3 more normal hits", 6, 0.5, 6.5);
        h1_lostClustersWith3MoreNormalHits.setTitleX("SL");
        h1_lostClustersWith3MoreNormalHits.setTitleY("Counts");
        h1_lostClustersWith3MoreNormalHits.setLineColor(2);
        for(int i = 0; i < 6; i++){
            h1_lostClustersWith3MoreNormalHits.setBinContent(i, numLostClustersWith3MoreNormalHits[i]);
        }        
        histoGroupEstimatePotentialRestorableClusters.addDataSet(h1_lostClustersWith3MoreNormalHits, 0);
        H1F h1_matchedClustersNotAllNormalHits = new H1F("matchedClustersNotAllNormalHits", "not-all-normal-hits matched clusters ", 6, 0.5, 6.5);
        h1_matchedClustersNotAllNormalHits.setTitleX("SL");
        h1_matchedClustersNotAllNormalHits.setTitleY("Counts");
        h1_matchedClustersNotAllNormalHits.setLineColor(3);
        for(int i = 0; i < 6; i++){
            h1_matchedClustersNotAllNormalHits.setBinContent(i, numMatchedClustersNotAllNormalHits[i]);
        }         
        histoGroupEstimatePotentialRestorableClusters.addDataSet(h1_matchedClustersNotAllNormalHits, 0);
        
        H1F h1_ratioPotentialRestorableClusters = new H1F("ratioPotentialRestorableClusters", "ratio for potential restorable clusters ", 6, 0.5, 6.5);
        h1_ratioPotentialRestorableClusters.setTitleX("SL");
        h1_ratioPotentialRestorableClusters.setTitleY("ratio");
        histoGroupEstimatePotentialRestorableClusters.addDataSet(h1_ratioPotentialRestorableClusters, 1);    
        for(int i = 0; i < 6; i++){
            h1_ratioPotentialRestorableClusters.setBinContent(i, (numLostClustersWith3MoreNormalHits[i]+numMatchedClustersNotAllNormalHits[i])/(double)numAllNormalHits[i]);
        } 
        
        HistoGroup histoGroupTrackLostSummary2 = histoGroupMap.get("trackLostSummary2");
        H1F[] h1_trackLostSummary2 = new H1F[7];        
        h1_trackLostSummary2[0] = histoGroupTrackLostSummary2.getH1F("numClustersOnValidTracks").histClone("numClustersOnValidTracks");
        h1_trackLostSummary2[1] = histoGroupTrackLostSummary2.getH1F("5or6ClustersLeftDenoising").histClone("5or6ClustersLeftDenoising");        
        h1_trackLostSummary2[2] = histoGroupTrackLostSummary2.getH1F("5or6MatchedClustersWithValidTracks").histClone("5or6MatchedClustersWithValidTracks");
        h1_trackLostSummary2[3] = histoGroupTrackLostSummary2.getH1F("numClustersFullyMatchedAICandidates").histClone("numClustersFullyMatchedAICandidates");
        h1_trackLostSummary2[4] = histoGroupTrackLostSummary2.getH1F("numClustersFullyMatchedHBTracks").histClone("numClustersFullyMatchedHBTracks");
        h1_trackLostSummary2[5] = histoGroupTrackLostSummary2.getH1F("numClustersFullyMatchedTBTracks").histClone("numClustersFullyMatchedTBTracks");
        h1_trackLostSummary2[6] = histoGroupTrackLostSummary2.getH1F("numClustersFullyMatchedValidTBTracks").histClone("numClustersFullyMatchedValidTBTracks");

        H1F h1_num5ClusterTrackLostSummary2 = new H1F("num5ClusterTrackLostSummary2", "# of 5-cluster tracks", 7, 0.5, 7.5);
        H1F h1_num6ClusterTrackLostSummary2 = new H1F("num6ClusterTrackLostSummary2", "# of 6-cluster tracks", 7, 0.5, 7.5);
        H1F h1_numTrackLostSummary2 = new H1F("numTrackLostSummary2", "# of tracks", 7, 0.5, 7.5);
        double[] numAllTrackLostSummary2 = new double[7];
        for(int i = 0; i < 7; i++){
            h1_num5ClusterTrackLostSummary2.setBinContent(i, h1_trackLostSummary2[i].getBinContent(0));
            h1_num6ClusterTrackLostSummary2.setBinContent(i, h1_trackLostSummary2[i].getBinContent(1));
            numAllTrackLostSummary2[i] = h1_trackLostSummary2[i].getBinContent(0) + h1_trackLostSummary2[i].getBinContent(1);
            h1_numTrackLostSummary2.setBinContent(i, numAllTrackLostSummary2[i]);
        }
        h1_num5ClusterTrackLostSummary2.setTitleX("# of 5-cluster tracks");
        h1_num5ClusterTrackLostSummary2.setTitleY("counts");
        histoGroupTrackLostSummary2.addDataSet(h1_num5ClusterTrackLostSummary2, 1); 
        h1_num6ClusterTrackLostSummary2.setTitleX("# of 6-cluster tracks");
        h1_num6ClusterTrackLostSummary2.setTitleY("counts");
        histoGroupTrackLostSummary2.addDataSet(h1_num6ClusterTrackLostSummary2, 2); 
        h1_numTrackLostSummary2.setTitleX("# of tracks");
        h1_numTrackLostSummary2.setTitleY("counts");
        histoGroupTrackLostSummary2.addDataSet(h1_numTrackLostSummary2, 3);
        
        H1F h1_ratioRemainingMatchedTrackLostSummary2 = new H1F("ratioRemainingMatchedTrackLostSummary2", "ratio of remaining matched tracks", 7, 0.5, 7.5);
        double[] ratio = new double[7];
        double[] ratio_err = new double[7];
        for(int i = 0; i < 7; i++){
            ratio[i] = numAllTrackLostSummary2[i]/numAllTrackLostSummary2[0];
            ratio_err[i] = Math.sqrt(numAllTrackLostSummary2[i] * numAllTrackLostSummary2[0] * numAllTrackLostSummary2[0] + numAllTrackLostSummary2[i] * numAllTrackLostSummary2[i] * numAllTrackLostSummary2[0]) / Math.pow(numAllTrackLostSummary2[0], 2);
            h1_ratioRemainingMatchedTrackLostSummary2.setBinContent(i, numAllTrackLostSummary2[i]/numAllTrackLostSummary2[0]);
        }
        
        h1_ratioRemainingMatchedTrackLostSummary2.setTitleX("ratio of remaining matched tracks");
        h1_ratioRemainingMatchedTrackLostSummary2.setTitleY("ratio");  
        histoGroupTrackLostSummary2.addDataSet(h1_ratioRemainingMatchedTrackLostSummary2, 4);
        
        HistoGroup histoGroupTrackLostSummary3 = histoGroupMap.get("trackLostSummary3");
        histoGroupTrackLostSummary3.addDataSet(h1_ratioRemainingMatchedTrackLostSummary2, 0);
        
        System.out.println("+-----------------------------------------------------------------------------------------------------+");
        System.out.println("                                   Evolution of tracking efficiency                                   ");
        System.out.println("           |     origin |  denoising | clustering |         AI |HB tracking |TB tracking | valid cuts |");        
        System.out.println(String.format("efficiency " + "| %10.4f | % 10.4f | %10.4f | %10.4f | %10.4f | %10.4f | %10.4f |", 
                ratio[0], ratio[1], ratio[2], ratio[3], ratio[4], ratio[5], ratio[6]));
        System.out.println(String.format("error      " + "| %10.4f | % 10.4f | %10.4f | %10.4f | %10.4f | %10.4f | %10.4f |", 
                ratio_err[0], ratio_err[1], ratio_err[2], ratio_err[3], ratio_err[4], ratio_err[5], ratio_err[6]));

        
    }
    
    public void setRatioNormalHitsCut(double ratioNormalHitsCut){
        this.ratioNormalHitsCut = ratioNormalHitsCut;
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
        parser.addOption("-ratioNormalHitsCut", "0", "ratio of normal hits cut for matched clustered between nobg and bg samples");
        parser.addOption("-uRWell", "0", "if uRWell is included (0/1)");


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
        double ratioNormalHitsCut = parser.getOption("-ratioNormalHitsCut").doubleValue();
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
        
        StudyBgEffectsOnValidTracks analysis = new StudyBgEffectsOnValidTracks();
        analysis.setRatioNormalHitsCut(ratioNormalHitsCut);
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

            progress.showStatus();
            reader1.close();
            reader2.close();
            analysis.saveHistos(histoName);
            analysis.postEventProcess();
        } else {
            analysis.readHistos(inputList.get(0));
        }

        if (displayPlots) {
            JFrame frame = new JFrame();
            EmbeddedCanvasTabbed canvas = analysis.plotHistos();
            if(canvas != null){
                frame.setSize(900, 1200);
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
