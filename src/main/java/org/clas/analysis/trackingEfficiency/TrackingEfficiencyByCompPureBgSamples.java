package org.clas.analysis.trackingEfficiency;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
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
import org.clas.element.MCParticle;
import org.clas.reader.Banks;
import org.clas.reader.LocalEvent;
import org.jlab.geom.prim.Point3D;
import org.clas.utilities.CommonFunctions;

/**
 *
 * @author Tongtong Cao
 */
public class TrackingEfficiencyByCompPureBgSamples extends BaseAnalysis {    
    private static String[] postflixes = {"Pure", "Bg"};
    
    private static int totalTracks = 0;
    private static int tracksNoSharedHits = 0;
    private static int tracksCut = 0;
    private static int matchedTracksAfterCut = 0;       

    private static double PURITYCUT = 0.85;    
    
    private static double PURITYCUTLOWER = 0.7;
    private static int PURITYCUTBINS = 31;
    private double[] purityList = new double[PURITYCUTBINS];
    private int[] tracksPurityCutList = new int[PURITYCUTBINS];

    public TrackingEfficiencyByCompPureBgSamples() {
    }

    public TrackingEfficiencyByCompPureBgSamples(Banks banks) {
        super(banks);
    }

    @Override
    public void createHistoGroupMap() {
        HistoGroup histoGroupClusterMatchingStuatus = new HistoGroup("clusterMatchingStuatus", 4, 2);
        H1F h1_noMatchedClusters = new H1F("noMatchedClustersPure", "non-matched clusters in pure samples", 6, 0.5, 6.5);
        h1_noMatchedClusters.setTitleX("Superlayer");
        h1_noMatchedClusters.setTitleY("Counts");
        h1_noMatchedClusters.setLineColor(1);
        histoGroupClusterMatchingStuatus.addDataSet(h1_noMatchedClusters, 0);
        
        H1F h1_mixedClusters = new H1F("mixedClustersBg", "mixed clusters in bg samples", 6, 0.5, 6.5);
        h1_mixedClusters.setTitleX("Superlayer");
        h1_mixedClusters.setTitleY("Counts");
        h1_mixedClusters.setLineColor(1);
        histoGroupClusterMatchingStuatus.addDataSet(h1_mixedClusters, 1);        
        
        H1F h1_pureClusters = new H1F("pureClustersBg", "pure clusters in bg samples", 6, 0.5, 6.5);
        h1_pureClusters.setTitleX("Superlayer");
        h1_pureClusters.setTitleY("Counts");
        h1_pureClusters.setLineColor(1);
        histoGroupClusterMatchingStuatus.addDataSet(h1_pureClusters, 2); 
        
        H1F h1_noMatchedClustersBg = new H1F("noMatchedClustersBg", "non-matched clusters in bg sample", 6, 0.5, 6.5);
        h1_noMatchedClustersBg.setTitleX("Superlayer");
        h1_noMatchedClustersBg.setTitleY("Counts");
        h1_noMatchedClustersBg.setLineColor(1);
        histoGroupClusterMatchingStuatus.addDataSet(h1_noMatchedClustersBg, 3);
        
        histoGroupMap.put(histoGroupClusterMatchingStuatus.getName(), histoGroupClusterMatchingStuatus); 
        
        HistoGroup histoGroupPurityMixedClusters = new HistoGroup("purityMixedClusters", 2, 3);
        HistoGroup histoGroupEfficiencyMixedClusters = new HistoGroup("efficiencyMixedClusters", 2, 3);
        HistoGroup histoGroupEfficiencyVsPurityMixedClusters = new HistoGroup("efficiencyVsPurityMixedClusters", 2, 3);
        for(int i = 0; i < 6; i++){
            H1F h1_purityMixedClusters = new H1F("purityMixedClusters for SL" + Integer.toString(i + 1),
                    "purity of matched clusters for SL" + Integer.toString(i + 1), 99, 0, 0.99);
            h1_purityMixedClusters.setTitleX("purity");
            h1_purityMixedClusters.setTitleY("Counts");
            histoGroupPurityMixedClusters.addDataSet(h1_purityMixedClusters, i); 
            
            H1F h1_efficiencyMixedClusters = new H1F("efficiencyMixedClusters for SL" + Integer.toString(i + 1),
                    "efficiency of matched clusters for SL" + Integer.toString(i + 1), 99, 0, 0.99);
            h1_efficiencyMixedClusters.setTitleX("efficiency");
            h1_efficiencyMixedClusters.setTitleY("Counts");
            histoGroupEfficiencyMixedClusters.addDataSet(h1_efficiencyMixedClusters, i);  
            
            H2F h2_efficiencyVsPurityMixedClusters = new H2F("efficiencyVsPurityMixedClusters for SL" + Integer.toString(i + 1),
                    "efficiency vs purity of matched clusters for SL" + Integer.toString(i + 1), 99, 0, 0.99, 99, 0, 0.99);
            h2_efficiencyVsPurityMixedClusters.setTitleX("purity");
            h2_efficiencyVsPurityMixedClusters.setTitleY("efficiency");
            histoGroupEfficiencyVsPurityMixedClusters.addDataSet(h2_efficiencyVsPurityMixedClusters, i);               
        }        
        histoGroupMap.put(histoGroupPurityMixedClusters.getName(), histoGroupPurityMixedClusters); 
        histoGroupMap.put(histoGroupEfficiencyMixedClusters.getName(), histoGroupEfficiencyMixedClusters);
        histoGroupMap.put(histoGroupEfficiencyVsPurityMixedClusters.getName(), histoGroupEfficiencyVsPurityMixedClusters);
        
        TrackHistoGroup histoGroupTrackComp = new TrackHistoGroup("trackComp", 3, 3);
        histoGroupTrackComp.addTrackHistos(postflixes[0], 1, 0);
        histoGroupTrackComp.addTrackHistos(postflixes[1], 2, 0);
        histoGroupMap.put(histoGroupTrackComp.getName(), histoGroupTrackComp); 
        
        HistoGroup histoGroupMatchCuts = new HistoGroup("matchCuts", 2, 2);
        H2F h2_matchedDCHitsVsNumDCClusters= new H2F("matchedDCHitsVsNumDCClusters", "# of matched DC hits vs # of DC clusters", 3, 3.5, 6.5, 50, 0, 49.5);
        h2_matchedDCHitsVsNumDCClusters.setTitleX("# of DC clusters");
        h2_matchedDCHitsVsNumDCClusters.setTitleY("# of matched DC hits");  
        histoGroupMatchCuts.addDataSet(h2_matchedDCHitsVsNumDCClusters, 0);        
        H2F h2_dcHitsPurityVsEfficiency= new H2F("dcHitsPurityVsEfficiency", "purity vs efficiency for DC hits", 101, 0, 1.01, 101, 0, 1.01);
        h2_dcHitsPurityVsEfficiency.setTitleX("purity");
        h2_dcHitsPurityVsEfficiency.setTitleY("efficiency");  
        histoGroupMatchCuts.addDataSet(h2_dcHitsPurityVsEfficiency, 1);        
        H2F h2_uRWellClustersPurityVsEfficiency= new H2F("uRWellClustersPurityVsEfficiency", "purity vs efficiency for uRWell clusters", 10, 0, 1.01, 10, 0, 1.01);
        h2_uRWellClustersPurityVsEfficiency.setTitleX("purity");
        h2_uRWellClustersPurityVsEfficiency.setTitleY("efficiency");  
        histoGroupMatchCuts.addDataSet(h2_uRWellClustersPurityVsEfficiency, 2);                
        H2F h2_momDistVsVtxDist= new H2F("momDistVsVtxDist", "mom dist vs vtx dist", 100, 0, 0.4, 100, 0, 0.004);
        h2_momDistVsVtxDist.setTitleX("vtx dist (cm)");
        h2_momDistVsVtxDist.setTitleY("mom dist (GeV/c)");
        histoGroupMatchCuts.addDataSet(h2_momDistVsVtxDist, 3);        
        histoGroupMap.put(histoGroupMatchCuts.getName(), histoGroupMatchCuts); 
        
        TrackHistoGroup histoGroupTrackEfficiencyVsPurityCut = new TrackHistoGroup("trackEfficiencyVsPurityCut", 1, 1);
        histoGroupMap.put(histoGroupTrackEfficiencyVsPurityCut.getName(), histoGroupTrackEfficiencyVsPurityCut);         
                 
        HistoGroup histoGroupMatchingOverview = new HistoGroup("matchingOverview", 2, 2);
        H1F h1_sharedStatus = new H1F("matchingStatus", "matching status", 3, -1.5, 1.5);
        h1_sharedStatus.setTitleX("matching status");
        h1_sharedStatus.setTitleY("Counts");
        h1_sharedStatus.setLineColor(1);
        histoGroupMatchingOverview.addDataSet(h1_sharedStatus, 0);
        H2F h2_dcHitsPurityVsEfficiencyAfterCut= new H2F("dcHitsPurityVsEfficiencyAfterCut", "purity vs efficiency for DC hits", 101, 0, 1.01, 101, 0, 1.01);
        h2_dcHitsPurityVsEfficiencyAfterCut.setTitleX("purity");
        h2_dcHitsPurityVsEfficiencyAfterCut.setTitleY("efficiency");  
        histoGroupMatchingOverview.addDataSet(h2_dcHitsPurityVsEfficiencyAfterCut, 1);        
        H2F h2_uRWellClustersPurityVsEfficiencyAfterCut= new H2F("uRWellClustersPurityVsEfficiencyAfterCut", "purity vs efficiency for uRWell clusters", 10, 0, 1.01, 10, 0, 1.01);
        h2_uRWellClustersPurityVsEfficiencyAfterCut.setTitleX("purity");
        h2_uRWellClustersPurityVsEfficiencyAfterCut.setTitleY("efficiency");  
        histoGroupMatchingOverview.addDataSet(h2_uRWellClustersPurityVsEfficiencyAfterCut, 2);                
        H2F h2_momDistVsVtxDistAfterCut= new H2F("momDistVsVtxDistAfterCut", "mom dist vs vtx dist", 100, 0, 0.4, 100, 0, 0.004);
        h2_momDistVsVtxDistAfterCut.setTitleX("vtx dist (cm)");
        h2_momDistVsVtxDistAfterCut.setTitleY("mom dist (GeV/c)");
        histoGroupMatchingOverview.addDataSet(h2_momDistVsVtxDistAfterCut, 3);      
        histoGroupMap.put(histoGroupMatchingOverview.getName(), histoGroupMatchingOverview);    

        TrackHistoGroup histoGroupDiffTracksMatched = new TrackHistoGroup("diffTracksMatched", 3, 3);
        histoGroupDiffTracksMatched.addTrackDiffHistos(1, 0, 0.02);
        histoGroupMap.put(histoGroupDiffTracksMatched.getName(), histoGroupDiffTracksMatched);
        
        TrackHistoGroup histoGroupDiffTracksMatchedLocal = new TrackHistoGroup("diffTracksMatchedLocal", 3, 2);
        histoGroupDiffTracksMatchedLocal.addTrackDiffHistos(1, -3, 0.02);
        histoGroupMap.put(histoGroupDiffTracksMatchedLocal.getName(), histoGroupDiffTracksMatchedLocal);
        
        TrackHistoGroup histoGroupNoSharedHitsTracks = new TrackHistoGroup("noSharedHitsTracks", 3, 5);
        histoGroupNoSharedHitsTracks.addTrackHistos(1, 0);        
        H2F h2_thetaVsPNoMatched = new H2F("thetaVsPExtraTracks", "#theta vs p", 100, 0, 12, 100, 0, Math.PI*50./180.);
        h2_thetaVsPNoMatched.setTitleX("p (GeV/c)");
        h2_thetaVsPNoMatched.setTitleY("#theta (rad)");
        histoGroupNoSharedHitsTracks.addDataSet(h2_thetaVsPNoMatched, 9);     
        H2F h2_phiVsPNoMatched = new H2F("phiVsPExtraTracks", "#phi vs p", 100, 0, 12, 100, -Math.PI, Math.PI);
        h2_phiVsPNoMatched.setTitleX("p (GeV/c)");
        h2_phiVsPNoMatched.setTitleY("#phi (rad)");
        histoGroupNoSharedHitsTracks.addDataSet(h2_phiVsPNoMatched, 10);          
        H2F h2_thetaVsPhiNoMatched = new H2F("thetaVsPhiExtraTracks", "#theta vs #phi", 100, -Math.PI, Math.PI, 100, 0, Math.PI*50./180.);
        h2_thetaVsPhiNoMatched.setTitleX("#phi (rad)");
        h2_thetaVsPhiNoMatched.setTitleY("#theta (rad)");
        histoGroupNoSharedHitsTracks.addDataSet(h2_thetaVsPhiNoMatched, 11);                                 
        H2F h2_chi2OverNDFVsNormalHitRatioNoMatched = new H2F("chi2OverNDFVsNormalHitRatioExtraTracks", "chi2/ndf vs ratio of normal hits", 30, 0, 1.05, 30, 0, 100);
        h2_chi2OverNDFVsNormalHitRatioNoMatched.setTitleX("ratio of normal hits");
        h2_chi2OverNDFVsNormalHitRatioNoMatched.setTitleY("chi2/ndf");
        histoGroupNoSharedHitsTracks.addDataSet(h2_chi2OverNDFVsNormalHitRatioNoMatched, 12); 
        histoGroupMap.put(histoGroupNoSharedHitsTracks.getName(), histoGroupNoSharedHitsTracks); 
        
        HistoGroup histoGroupCompareCutTrackPairs = new HistoGroup("compareCutTrackPairs", 2, 2);
        H2F h2_pid_compareCutTrackPairs = new H2F("pid_compareCutTrackPairs", "comp of pid", 35, -35, 35, 35, -35, 35);
        h2_pid_compareCutTrackPairs.setTitleX("pid in pure sample");
        h2_pid_compareCutTrackPairs.setTitleY("pid in bg sample");
        histoGroupCompareCutTrackPairs.addDataSet(h2_pid_compareCutTrackPairs, 0);                  
        H2F h2_z_compareCutTrackPairs = new H2F("z_compareCutTrackPairs", "comp of z", 100, -50, 50, 100, -50, 50);
        h2_z_compareCutTrackPairs.setTitleX("z in pure sample (cm)");
        h2_z_compareCutTrackPairs.setTitleY("z in bg sample (cm)");
        histoGroupCompareCutTrackPairs.addDataSet(h2_z_compareCutTrackPairs, 1);           
        H2F h2_p_compareCutTrackPairs = new H2F("p_compareCutTrackPairs", "comp of p", 100, 0, 10, 100, 0, 10);
        h2_p_compareCutTrackPairs.setTitleX("p in pure sample (GeV/c)");
        h2_p_compareCutTrackPairs.setTitleY("p in bg sample (GeV/c)");
        histoGroupCompareCutTrackPairs.addDataSet(h2_p_compareCutTrackPairs, 2);                 
        H2F h2_chi2pid_compareCutTrackPairs = new H2F("chi2pid_compareCutTrackPairs", "comp of chi2pid", 100, -100, 100, 100, -100, 100);
        h2_chi2pid_compareCutTrackPairs.setTitleX("chi2pid in pure sample");
        h2_chi2pid_compareCutTrackPairs.setTitleY("chi2pid in bg sample");
        histoGroupCompareCutTrackPairs.addDataSet(h2_chi2pid_compareCutTrackPairs, 3); 
        histoGroupMap.put(histoGroupCompareCutTrackPairs.getName(), histoGroupCompareCutTrackPairs);
        
        HistoGroup histoGroupDiffVsPurityCutTrackPairs = new HistoGroup("diffVsPurityCutTrackPairs", 2, 2);
        H2F h2_pid_diffVsPurityCutTrackPairs = new H2F("pid_diffVsPurityCutTrackPairs", "pid diff vs purity", 101, 0, 1.01, 35, -35, 35);
        h2_pid_diffVsPurityCutTrackPairs.setTitleX("purity");
        h2_pid_diffVsPurityCutTrackPairs.setTitleY("pid diff");
        histoGroupDiffVsPurityCutTrackPairs.addDataSet(h2_pid_diffVsPurityCutTrackPairs, 0); 
        
        H2F h2_z_diffVsPurityCutTrackPairs = new H2F("z_diffVsPurityCutTrackPairs", "z diff vs purity", 101, 0, 1.01, 100, -5, 5);
        h2_z_diffVsPurityCutTrackPairs.setTitleX("purity");
        h2_z_diffVsPurityCutTrackPairs.setTitleY("z diff (cm)");
        histoGroupDiffVsPurityCutTrackPairs.addDataSet(h2_z_diffVsPurityCutTrackPairs, 1);
        
        H2F h2_p_diffVsPurityCutTrackPairs = new H2F("p_diffVsPurityCutTrackPairs", "p diff vs purity", 101, 0, 1.01, 100, -1, 1);
        h2_p_diffVsPurityCutTrackPairs.setTitleX("purity");
        h2_p_diffVsPurityCutTrackPairs.setTitleY("p diff (GeV/c)");
        histoGroupDiffVsPurityCutTrackPairs.addDataSet(h2_p_diffVsPurityCutTrackPairs, 2);  

        H2F h2_chi2pid_diffVsPurityCutTrackPairs = new H2F("chi2pid_diffVsPurityCutTrackPairs", "chi2pid diff vs purity", 101, 0, 1.01, 100, -100, 100);
        h2_chi2pid_diffVsPurityCutTrackPairs.setTitleX("purity");
        h2_chi2pid_diffVsPurityCutTrackPairs.setTitleY("chi2pid diff");
        histoGroupDiffVsPurityCutTrackPairs.addDataSet(h2_chi2pid_diffVsPurityCutTrackPairs, 3);  
        histoGroupMap.put(histoGroupDiffVsPurityCutTrackPairs.getName(), histoGroupDiffVsPurityCutTrackPairs);        
    }

    public void processEvent(Event event1, Event event2, int trkType) {
        //// Read banks
        LocalEvent localEvent1 = new LocalEvent(reader1, event1, trkType, Constants.URWELL);
        LocalEvent localEvent2 = new LocalEvent(reader2, event2, trkType, Constants.URWELL);
        
        List<Track> trackList1 = new ArrayList();    
        List<Track> trackList2 = new ArrayList();  
        List<Cluster> clusterList2 = localEvent2.getClusters();
        
        if(trkType == Constants.CONVHB || trkType == Constants.AIHB){
            for(Track trk : localEvent1.getTracksHB()){
                if(trk.isValid()) trackList1.add(trk);
            }
            trackList2 = localEvent2.getTracksHB();
        } else{
            for(Track trk : localEvent1.getTracksTB()){
                if(trk.isValid()) trackList1.add(trk); // Valid tracks for pure samples
            }
            trackList2 = localEvent2.getTracksTB(); // All tracks for bg samples
        }
        
        totalTracks += trackList1.size();
        
        TrackHistoGroup histoGroupTrackComp = (TrackHistoGroup) histoGroupMap.get("trackComp");
        for (Track trk1 : trackList1) {
                histoGroupTrackComp.getHistoCategory(postflixes[0]).fill(trk1.getTrackCategory());
                histoGroupTrackComp.getHistoNDF0(postflixes[0]).fill(trk1.NDF0());
                histoGroupTrackComp.getHistoChi2overndf(postflixes[0]).fill(trk1.chi2()/trk1.NDF());
                histoGroupTrackComp.getHistoP(postflixes[0]).fill(trk1.p());
                histoGroupTrackComp.getHistoTheta(postflixes[0]).fill(trk1.theta());
                histoGroupTrackComp.getHistoPhi(postflixes[0]).fill(trk1.phi());
                histoGroupTrackComp.getHistoVx(postflixes[0]).fill(trk1.vx());
                histoGroupTrackComp.getHistoVy(postflixes[0]).fill(trk1.vy());
                histoGroupTrackComp.getHistoVz(postflixes[0]).fill(trk1.vz());                                              
        }
        
        for (Track trk2 : trackList2) {
                histoGroupTrackComp.getHistoCategory(postflixes[1]).fill(trk2.getTrackCategory());
                histoGroupTrackComp.getHistoNDF0(postflixes[1]).fill(trk2.NDF0());
                histoGroupTrackComp.getHistoChi2overndf(postflixes[1]).fill(trk2.chi2()/trk2.NDF());
                histoGroupTrackComp.getHistoP(postflixes[1]).fill(trk2.p());
                histoGroupTrackComp.getHistoTheta(postflixes[1]).fill(trk2.theta());
                histoGroupTrackComp.getHistoPhi(postflixes[1]).fill(trk2.phi());
                histoGroupTrackComp.getHistoVx(postflixes[1]).fill(trk2.vx());
                histoGroupTrackComp.getHistoVy(postflixes[1]).fill(trk2.vy());
                histoGroupTrackComp.getHistoVz(postflixes[1]).fill(trk2.vz());                             
        } 
        
        // Cluster matching with most shared hits
        Map<List<Cluster>, List<Cluster>> map_clusterList1_clusterList2 = new HashMap();                
        for (Track trk1 : trackList1) {
            List<Cluster> matchedClusterList2 = new ArrayList();
            for(Cluster cls1 : trk1.getClusters()){
                int maxMatched = 0;
                Cluster matchedCluster = null;
                for(Cluster cls2 : clusterList2){
                    int numMatchedHits = cls1.numMatchedHits(cls2);
                    if(numMatchedHits > maxMatched){
                        maxMatched = numMatchedHits;
                        matchedCluster = cls2;
                    }
                }
                matchedClusterList2.add(matchedCluster);                                
            } 
            map_clusterList1_clusterList2.put(trk1.getClusters(), matchedClusterList2);
        }
                
        // Study status for matching
        HistoGroup histoGroupClusterMatchingStuatus = histoGroupMap.get("clusterMatchingStuatus");
        HistoGroup histoGroupPurityMixedClusters = histoGroupMap.get("purityMixedClusters");
        HistoGroup histoGroupEfficiencyMixedClusters = histoGroupMap.get("efficiencyMixedClusters");
        HistoGroup histoGroupEfficiencyVsPurityMixedClusters = histoGroupMap.get("efficiencyVsPurityMixedClusters");
        List<Cluster> matchedClusters2 = new ArrayList();
        for(List<Cluster> clsList1 : map_clusterList1_clusterList2.keySet()){
            List<Cluster> clsList2 = map_clusterList1_clusterList2.get(clsList1);
            for(int i = 0; i < clsList1.size(); i++){
                Cluster cls1 = clsList1.get(i);
                Cluster cls2 = clsList2.get(i);
                if(cls2 == null) histoGroupClusterMatchingStuatus.getH1F("noMatchedClustersPure").fill(cls1.superlayer());
                else {
                    matchedClusters2.add(cls2);
                    int numMatchedHits = cls1.numMatchedHits(cls2);
                    double purity = cls2.getRatioNormalHits();
                    double efficiency = (double) numMatchedHits / cls1.getNumHits();
                    
                    if(purity == 1) {
                        histoGroupClusterMatchingStuatus.getH1F("pureClustersBg").fill(cls1.superlayer());
                    } else {
                        histoGroupClusterMatchingStuatus.getH1F("mixedClustersBg").fill(cls2.superlayer());                                                            
                        histoGroupPurityMixedClusters.getH1F("purityMixedClusters for SL" + cls1.superlayer()).fill(purity);
                        histoGroupEfficiencyMixedClusters.getH1F("efficiencyMixedClusters for SL" + cls1.superlayer()).fill(efficiency);
                        histoGroupEfficiencyVsPurityMixedClusters.getH2F("efficiencyVsPurityMixedClusters for SL" + cls1.superlayer()).fill(purity, efficiency);
                    }
                }
            }
        }
        
        // non-matched clusters in bg samples
        List<Cluster> noMatchedClusters2 = new ArrayList();
        noMatchedClusters2.addAll(clusterList2);
        noMatchedClusters2.removeAll(matchedClusters2);
        
        for(Cluster nonMatchedCls2 : noMatchedClusters2){
            histoGroupClusterMatchingStuatus.getH1F("noMatchedClustersBg").fill(nonMatchedCls2.superlayer());
        }
        
        
        // Track matching with most shared hits
        Map<Track, Track> map_track1_track2 = new HashMap();
        for (Track trk1 : trackList1) {            
            int maxMatched = 0;
            Track matchedTrack = null;
            for (Track trk2 : trackList2) {
                int numMatchedHits = trk1.matchedHits(trk2);
                int numMatchedURWellClusters = trk1.matchedURWellClusters(trk2);
                int totalMatched = numMatchedHits + numMatchedURWellClusters;
                if (totalMatched > maxMatched) {
                    maxMatched = totalMatched;
                    matchedTrack = trk2;
                }                
            }            
            if (matchedTrack != null) {                              
                map_track1_track2.put(trk1, matchedTrack);
            }
        }
        
        HistoGroup histoGroupMatchingOverview = histoGroupMap.get("matchingOverview");
        
        // Extrac tracks in pure samples, where no tracks with shared hits are found in bg samples
        List<Track> trackListNoSharedHits = new ArrayList();
        for (Track trk1 : trackList1) {
            if (!map_track1_track2.containsKey(trk1)) {
                histoGroupMatchingOverview.getH1F("matchingStatus").fill(-1);
                trackListNoSharedHits.add(trk1);                
            }
        }
        
        tracksNoSharedHits += trackListNoSharedHits.size();
                   
        HistoGroup histoGroupMatchCuts = histoGroupMap.get("matchCuts");
        Map<Track, Track> map_track1_track2_cut = new HashMap();
        Map<Track, Track> map_track1_track2_removed = new HashMap();
        for (Track trk1 : map_track1_track2.keySet()) {
            Track trk2 = map_track1_track2.get(trk1);
            int numMatchedDChits = trk1.matchedHits(trk2);
            //double purityDChits = numMatchedDChits/(double) trk2.getNumHits();
            double purityDChits = trk2.getRatioNormalHits();
            double efficiencyDChits = numMatchedDChits/(double) trk1.getNumHits();            
            
            histoGroupMatchCuts.getH2F("matchedDCHitsVsNumDCClusters").fill(trk2.getNumClusters(), numMatchedDChits);                        
            histoGroupMatchCuts.getH2F("dcHitsPurityVsEfficiency").fill(purityDChits, efficiencyDChits);            
            histoGroupMatchCuts.getH2F("momDistVsVtxDist").fill(trk1.euclideanVtxDistance(trk2), trk1.euclideanMomDistance(trk2));
            
            if(Constants.URWELL){
                int numMatchedURWellClusters = trk1.matchedURWellClusters(trk2);
                double purityURWellClusters = numMatchedURWellClusters/(double)(trk2.getURWellCrosses().size()*2);
                double efficiencyURWellClusters = numMatchedURWellClusters/(double)(trk1.getURWellCrosses().size()*2);  
                histoGroupMatchCuts.getH2F("uRWellClustersPurityVsEfficiency").fill(purityURWellClusters, efficiencyURWellClusters);
            } 
            
            // for tracking efficiency vs purity cut
            for(int i = 0; i < PURITYCUTBINS; i++){
                purityList[i] = PURITYCUTLOWER + (1-PURITYCUTLOWER)/PURITYCUTBINS * i;
                if(purityDChits < purityList[i]) tracksPurityCutList[i]++;
            }            
            
            // Purity cut
            //if(purityDChits > PURITYCUT){    
            if(trk2.isValid()){
                map_track1_track2_cut.put(trk1, trk2);                
            }
            else{
                tracksCut++;
                
                map_track1_track2_removed.put(trk1, trk2);
                
                this.addDemoGroup(localEvent1, localEvent2, trk1.sector(), " purity: " + Double.toString(purityDChits), true);
            }
            
        }
        
        // Matched tracks with shared hits after purity cuts
        TrackHistoGroup histoGroupDiffTracksMatched = (TrackHistoGroup) histoGroupMap.get("diffTracksMatched");
        TrackHistoGroup histoGroupDiffTracksMatchedLocal = (TrackHistoGroup) histoGroupMap.get("diffTracksMatchedLocal");                        
        for (Track trk1 : trackList1) {
            if (map_track1_track2_cut.containsKey(trk1)) {
                histoGroupMatchingOverview.getH1F("matchingStatus").fill(1);
                
                matchedTracksAfterCut++;
                
                Track trk2 = map_track1_track2.get(trk1);
                int numMatchedDChits = trk1.matchedHits(trk2);
                //double purityDChits = numMatchedDChits/(double) trk2.getNumHits();
                double purityDChits = trk2.getRatioNormalHits();
                double efficiencyDChits = numMatchedDChits/(double) trk1.getNumHits();            
                       
                histoGroupMatchingOverview.getH2F("dcHitsPurityVsEfficiencyAfterCut").fill(purityDChits, efficiencyDChits);            
                histoGroupMatchingOverview.getH2F("momDistVsVtxDistAfterCut").fill(trk1.euclideanVtxDistance(trk2), trk1.euclideanMomDistance(trk2));
            
                if(Constants.URWELL){
                    int numMatchedURWellClusters = trk1.matchedURWellClusters(trk2);
                    double purityURWellClusters = numMatchedURWellClusters/(double)(trk2.getURWellCrosses().size()*2);
                    double efficiencyURWellClusters = numMatchedURWellClusters/(double)(trk1.getURWellCrosses().size()*2);  
                    histoGroupMatchingOverview.getH2F("uRWellClustersPurityVsEfficiencyAfterCut").fill(purityURWellClusters, efficiencyURWellClusters);
                }      

                histoGroupDiffTracksMatched.getHistoCategoryComp().fill(trk1.getTrackCategory(), trk2.getTrackCategory());
                histoGroupDiffTracksMatched.getHistoNDF0Diff().fill(trk1.NDF0() - trk2.NDF0());
                histoGroupDiffTracksMatched.getHistoChi2overndfDiff().fill(trk1.chi2()/trk1.NDF() - trk2.chi2()/trk2.NDF());
                histoGroupDiffTracksMatched.getHistoPDiff().fill(trk1.p() - trk2.p());
                histoGroupDiffTracksMatched.getHistoThetaDiff().fill(trk1.theta() - trk2.theta());
                histoGroupDiffTracksMatched.getHistoPhiDiff().fill(trk1.phi() - trk2.phi());
                histoGroupDiffTracksMatched.getHistoVxDiff().fill(trk1.vx() - trk2.vx());
                histoGroupDiffTracksMatched.getHistoVyDiff().fill(trk1.vy() - trk2.vy());
                histoGroupDiffTracksMatched.getHistoVzDiff().fill(trk1.vz() - trk2.vz());            

                Point3D momLocalTrk1 = CommonFunctions.getCoordsInLocal(trk1.px(), trk1.py(), trk1.pz(), trk1.sector());
                Point3D vtxLocalTrk1 = CommonFunctions.getCoordsInLocal(trk1.vx(), trk1.vy(), trk1.vz(), trk1.sector());            
                Point3D momLocalTrk2 = CommonFunctions.getCoordsInLocal(trk2.px(), trk2.py(), trk2.pz(), trk2.sector());
                Point3D vtxLocalTrk2 = CommonFunctions.getCoordsInLocal(trk2.vx(), trk2.vy(), trk2.vz(), trk2.sector());

                double[] momLocalTrk1Spherical = CommonFunctions.toSpherical(momLocalTrk1);
                double[] momLocalTrk2Spherical = CommonFunctions.toSpherical(momLocalTrk2);

                histoGroupDiffTracksMatchedLocal.getHistoPDiff().fill(momLocalTrk1Spherical[0] - momLocalTrk2Spherical[0]);
                histoGroupDiffTracksMatchedLocal.getHistoThetaDiff().fill(momLocalTrk1Spherical[1] - momLocalTrk2Spherical[1]);
                histoGroupDiffTracksMatchedLocal.getHistoPhiDiff().fill(momLocalTrk1Spherical[2] - momLocalTrk2Spherical[2]);
                histoGroupDiffTracksMatchedLocal.getHistoVxDiff().fill(vtxLocalTrk1.x() - vtxLocalTrk2.x());
                histoGroupDiffTracksMatchedLocal.getHistoVyDiff().fill(vtxLocalTrk1.y() - vtxLocalTrk2.y());
                histoGroupDiffTracksMatchedLocal.getHistoVzDiff().fill(vtxLocalTrk1.z() - vtxLocalTrk2.z());
                
            } else {
                if(!trackListNoSharedHits.contains(trk1)){ // Tracks in pure samples, where matched tracks in bg samples are cut off by purity
                    histoGroupMatchingOverview.getH1F("matchingStatus").fill(0);
                }
            }
        }
        
        // Non-shared hits tracks in pure samples
        TrackHistoGroup histoGroupNoSharedHitsTracks = (TrackHistoGroup) histoGroupMap.get("noSharedHitsTracks");
        for(Track trk1 : trackListNoSharedHits){                                  
            histoGroupNoSharedHitsTracks.getHistoCategory().fill(trk1.getTrackCategory());
            histoGroupNoSharedHitsTracks.getHistoNDF0().fill(trk1.NDF0());
            histoGroupNoSharedHitsTracks.getHistoChi2overndf().fill(trk1.chi2()/trk1.NDF());
            histoGroupNoSharedHitsTracks.getHistoP().fill(trk1.p());
            histoGroupNoSharedHitsTracks.getHistoTheta().fill(trk1.theta());
            histoGroupNoSharedHitsTracks.getHistoPhi().fill(trk1.phi());
            histoGroupNoSharedHitsTracks.getHistoVx().fill(trk1.vx());
            histoGroupNoSharedHitsTracks.getHistoVy().fill(trk1.vy());
            histoGroupNoSharedHitsTracks.getHistoVz().fill(trk1.vz());

            histoGroupNoSharedHitsTracks.getH2F("thetaVsPExtraTracks").fill(trk1.p(), trk1.theta()); 
            histoGroupNoSharedHitsTracks.getH2F("phiVsPExtraTracks").fill(trk1.p(), trk1.phi());
            histoGroupNoSharedHitsTracks.getH2F("thetaVsPhiExtraTracks").fill(trk1.phi(), trk1.theta());                  

            histoGroupNoSharedHitsTracks.getH2F("chi2OverNDFVsNormalHitRatioExtraTracks").fill(trk1.getRatioNormalHits(), trk1.chi2() / trk1.NDF());
            
            this.addDemoGroup(localEvent1, localEvent2, trk1.sector(), "noSharedHitTrack", true);            
        }
        
        // Track pair for tracks in bg samples, which are cut off 
        HistoGroup histoGroupCompareCutTrackPairs = histoGroupMap.get("compareCutTrackPairs");
        HistoGroup histoGroupDiffVsPurityCutTrackPairs = histoGroupMap.get("diffVsPurityCutTrackPairs");
                        
        for(Track trk1 : map_track1_track2_removed.keySet()){
            Track trk2 = map_track1_track2_removed.get(trk1);
            double purity = trk2.getRatioNormalHits();
            
            histoGroupCompareCutTrackPairs.getH2F("pid_compareCutTrackPairs").fill(trk1.pid()/100., trk2.pid()/100.);
            histoGroupCompareCutTrackPairs.getH2F("z_compareCutTrackPairs").fill(trk1.vz(), trk2.vz());
            histoGroupCompareCutTrackPairs.getH2F("p_compareCutTrackPairs").fill(trk1.p(), trk2.p());
            histoGroupCompareCutTrackPairs.getH2F("chi2pid_compareCutTrackPairs").fill(trk1.chi2pid(), trk2.chi2pid());
            
            histoGroupDiffVsPurityCutTrackPairs.getH2F("pid_diffVsPurityCutTrackPairs").fill(purity, (trk2.pid() - trk1.pid())/100.);
            histoGroupDiffVsPurityCutTrackPairs.getH2F("z_diffVsPurityCutTrackPairs").fill(purity, trk2.vz() - trk1.vz());
            histoGroupDiffVsPurityCutTrackPairs.getH2F("p_diffVsPurityCutTrackPairs").fill(purity, trk2.p() - trk1.p());
            histoGroupDiffVsPurityCutTrackPairs.getH2F("chi2pid_diffVsPurityCutTrackPairs").fill(purity, trk2.chi2pid() - trk1.chi2pid());            
        }
    }
    
    public void postEventProcess(){ 
        HistoGroup histoGroupTrackEfficiencyVsPurityCut = histoGroupMap.get("trackEfficiencyVsPurityCut");
        
        List<Double> xList = new ArrayList();
        List<Double> yList = new ArrayList();
        for(int i = 0; i < PURITYCUTBINS; i++){
            xList.add(purityList[i]);
            yList.add(1.0 - (double)(tracksNoSharedHits+tracksPurityCutList[i])/totalTracks);
        }
        
        GraphErrors graph = new GraphErrors("trackEfficencyVsPurityCut", new DataVector(xList), new DataVector(yList));
        graph.setTitle("track efficency vs. purity cut");
        graph.setTitleX("purity cut");
        graph.setTitleY("track efficiency");
        //graph.setMarkerStyle();
        //graph.setMarkerColor(markerColor);
        //graph.setMarkerSize(markerSize);
        histoGroupTrackEfficiencyVsPurityCut.addDataSet(graph, 0);
        
        HistoGroup histoGroupClusterMatchingStuatus = histoGroupMap.get("clusterMatchingStuatus");
        H1F h1_noMatchedClusters =  histoGroupClusterMatchingStuatus.getH1F("noMatchedClustersPure");
        H1F h1_mixedClusters = histoGroupClusterMatchingStuatus.getH1F("mixedClustersBg");
        H1F h1_pureClusters =  histoGroupClusterMatchingStuatus.getH1F("pureClustersBg");
        
        H1F h1_allClusters = h1_pureClusters.histClone("allClusters");        
        h1_allClusters.add(h1_noMatchedClusters);        
        h1_allClusters.add(h1_mixedClusters); 
        
        H1F h1_ratioNoMatchedClusters = h1_noMatchedClusters.histClone("ratioNoMatchedClustersPure");
        h1_ratioNoMatchedClusters.setTitle("ratio of non-matched clusters in pure samples");
        h1_ratioNoMatchedClusters.divide(h1_allClusters); 
        histoGroupClusterMatchingStuatus.addDataSet(h1_ratioNoMatchedClusters, 4);
                        
        H1F h1_ratioMixedClusters = h1_mixedClusters.histClone("ratioMixedClustersBg");
        h1_ratioMixedClusters.setTitle("ratio of mixed clusters in bg samples");
        h1_ratioMixedClusters.divide(h1_allClusters); 
        histoGroupClusterMatchingStuatus.addDataSet(h1_ratioMixedClusters, 5);
        
        H1F h1_ratioPureClusters = h1_pureClusters.histClone("ratioPureClustersBg");
        h1_ratioPureClusters.setTitle("ratio of matched clusters in bg samples");
        h1_ratioPureClusters.divide(h1_allClusters); 
        histoGroupClusterMatchingStuatus.addDataSet(h1_ratioPureClusters, 6);  
        
        H1F h1_noMatchedClustersBg =  histoGroupClusterMatchingStuatus.getH1F("noMatchedClustersBg");
        H1F h1_ratioNoMatchedClustersBg = h1_noMatchedClustersBg.histClone("ratioNoMatchedClustersBg");
        h1_ratioNoMatchedClustersBg.setTitle("ratio of non-matched clusters in bg samples");
        h1_ratioNoMatchedClustersBg.divide(h1_allClusters); 
        histoGroupClusterMatchingStuatus.addDataSet(h1_ratioNoMatchedClustersBg, 7);        
        
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
        parser.addOption("-trkType", "22", "tracking type: ConvHB(11) or ConvTB(12) or AIHB(21) or AITB(22)");
        parser.addOption("-mc", "0", "if mc (0/1)");
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

        TrackingEfficiencyByCompPureBgSamples analysis = new TrackingEfficiencyByCompPureBgSamples();
        analysis.createHistoGroupMap();
        if (!readHistos) {
            File input1 = new File(inputList.get(0));
            File input2 = new File(inputList.get(1));

            List<File> fileList1 = new ArrayList<>();
            List<File> fileList2 = new ArrayList<>();

            if (input1.isFile() && input2.isFile()) {

                fileList1.add(input1);
                fileList2.add(input2);
            } else if (input1.isDirectory() && input2.isDirectory()) {

                File[] files1 = input1.listFiles((dir, name) -> name.endsWith(".hipo"));
                File[] files2 = input2.listFiles((dir, name) -> name.endsWith(".hipo"));

                Arrays.sort(files1, Comparator.comparing(File::getName));
                Arrays.sort(files2, Comparator.comparing(File::getName));

                if (files1.length != files2.length) {
                    System.err.println("Error: folders contain different number of files.");
                    System.exit(1);
                }

                for (int i = 0; i < files1.length; i++) {

                    if (!files1[i].getName().equals(files2[i].getName())) {
                        System.err.println("File name mismatch: "
                                + files1[i].getName() + " vs "
                                + files2[i].getName());
                        System.exit(1);
                    }

                    fileList1.add(files1[i]);
                    fileList2.add(files2[i]);
                }

            } else {
                System.err.println("Error: inputs must be both files or both directories.");
                System.exit(1);
            }

            int nFiles = Math.min(fileList1.size(), fileList2.size());
            if (fileList1.size() != fileList2.size()) {
                System.out.println("Warning: folder sizes differ. "
                + "Using first " + nFiles + " matched pairs.");
            }
                        
            int counter = 0;
            for (int i = 0; i < nFiles; i++) {
                String name1 = fileList1.get(i).getName();
                String name2 = fileList2.get(i).getName();

                if (!name1.equals(name2)) {
                    System.out.println("Warning: file name mismatch at index " + i
                        + " : " + name1 + " vs " + name2);
                }

                File f1 = fileList1.get(i);
                File f2 = fileList2.get(i);

                System.out.println("Processing: " + f1.getAbsolutePath());
                
                HipoReader reader1 = new HipoReader();
                reader1.open(f1.getAbsolutePath());
                HipoReader reader2 = new HipoReader();
                reader2.open(f2.getAbsolutePath());

                SchemaFactory schema1 = reader1.getSchemaFactory();
                SchemaFactory schema2 = reader2.getSchemaFactory();
                analysis.initReader(new Banks(schema1), new Banks(schema2));                
                
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
            }
            analysis.saveHistos(histoName);
            analysis.postEventProcess();
            int noSharedHitTracks = TrackingEfficiencyByCompPureBgSamples.tracksNoSharedHits;
            int cutTracks = TrackingEfficiencyByCompPureBgSamples.tracksCut;
            int matchedTracksAfterCut = TrackingEfficiencyByCompPureBgSamples.matchedTracksAfterCut;
            System.out.println("no-shared-hit tracks: " + noSharedHitTracks);
            System.out.println("tracks by cut: " + Integer.toString(cutTracks));
            System.out.println("matched tracks: " + Integer.toString(matchedTracksAfterCut));
            System.out.println("Efficiency: " + Double.toString((double)matchedTracksAfterCut/(matchedTracksAfterCut + noSharedHitTracks + cutTracks)));
                         
        }else {
            analysis.readHistos(inputList.get(0));
        }

        if (displayPlots) {
            JFrame frame = new JFrame();
            EmbeddedCanvasTabbed canvas = analysis.plotHistos();
            frame.setSize(1200, 1200);
            frame.add(canvas);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        }

        if (displayDemos) {
            JFrame frame2 = new JFrame();
            EmbeddedCanvasTabbed canvas2 = analysis.plotDemos();
            if (canvas2 != null) {
                frame2.setSize(1200, 1500);
                frame2.add(canvas2);
                frame2.setLocationRelativeTo(null);
                frame2.setVisible(true);
            }
        }

    }

}
