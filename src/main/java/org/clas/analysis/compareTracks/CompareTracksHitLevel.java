package org.clas.analysis.compareTracks;

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

/**
 *
 * @author Tongtong Cao
 */
public class CompareTracksHitLevel extends BaseAnalysis {
    
    private static int goodTracks = 0;    
    private static int tracksSp1 = 0;
    private static int tracksSp2 = 0;
    private static int extraTracksSp1 = 0;
    private static int extraTracksSp2 = 0; 
    private static int validTracksSp1 = 0;
    private static int validTracksSp2 = 0;
    private static int extraValidTracksSp1 = 0;
    private static int extraValidTracksSp2 = 0;
    private static int matchedValidTracks = 0;

    public CompareTracksHitLevel() {
    }

    public CompareTracksHitLevel(Banks banks) {
        super(banks);
    }

    @Override
    public void createHistoGroupMap() {
        ////// All tracks
        HistoGroup histoGroupMatchingOverview = new HistoGroup("matchingOverview", 2, 2);
        H1F h1_sharedStatus = new H1F("ifHaveSharedHits", "Status for tracks with shared hits", 3, -0.5, 2.5);
        h1_sharedStatus.setTitleX("if have shared hits");
        h1_sharedStatus.setTitleY("Counts");
        h1_sharedStatus.setLineColor(1);
        histoGroupMatchingOverview.addDataSet(h1_sharedStatus, 0);
        H2F h2_matchedHitRatio = new H2F("matchedHitRatio", "Ratio of matched hits between two tracks", 30, 0, 1.05, 30, 0, 1.05);
        h2_matchedHitRatio.setTitleX("ratio of matched hits in sp1");
        h2_matchedHitRatio.setTitleY("ratio of matched hits in sp2");
        histoGroupMatchingOverview.addDataSet(h2_matchedHitRatio, 1);
        H2F h2_normalHitRatio = new H2F("normalHitRatio", "Ratio of normal hits for tracks with shared hits", 30, 0, 1.05, 30, 0, 1.05);
        h2_normalHitRatio.setTitleX("ratio of normal hits in sp1");
        h2_normalHitRatio.setTitleY("ratio of normal hits in sp2");
        histoGroupMatchingOverview.addDataSet(h2_normalHitRatio, 2);       
        histoGroupMap.put(histoGroupMatchingOverview.getName(), histoGroupMatchingOverview);

        TrackHistoGroup histoGroupDiffTracksWithSharedHits = new TrackHistoGroup("diffTracksWithSharedHits", 4, 2);
        histoGroupDiffTracksWithSharedHits.addTrackDiffHistos(1, 0);
        histoGroupMap.put(histoGroupDiffTracksWithSharedHits.getName(), histoGroupDiffTracksWithSharedHits);
        
        TrackHistoGroup histoGroupExtraTracks1 = new TrackHistoGroup("extraTracks1", 4, 2);
        histoGroupExtraTracks1.addTrackHistos(1, 0);
        H2F h2_chi2OverNDFVsNormalHitRatioExtraTracks1 = new H2F("chi2OverNDFVsNormalHitRatioExtraTracks1", "chi2/ndf vs ratio of normal hits for extra tracks1", 30, 0, 1.05, 30, 0, 100);
        h2_chi2OverNDFVsNormalHitRatioExtraTracks1.setTitleX("ratio of normal hits");
        h2_chi2OverNDFVsNormalHitRatioExtraTracks1.setTitleY("chi2/ndf");
        histoGroupExtraTracks1.addDataSet(h2_chi2OverNDFVsNormalHitRatioExtraTracks1, 7); 
        histoGroupMap.put(histoGroupExtraTracks1.getName(), histoGroupExtraTracks1);
        
        TrackHistoGroup histoGroupExtraTracks2 = new TrackHistoGroup("extraTracks2", 4, 2);
        histoGroupExtraTracks2.addTrackHistos(1, 0);
        H2F h2_chi2OverNDFVsNormalHitRatioExtraTracks2 = new H2F("chi2OverNDFVsNormalHitRatioExtraTracks2", "chi2/ndf vs ratio of normal hits for extra tracks2", 30, 0, 1.05, 30, 0, 100);
        h2_chi2OverNDFVsNormalHitRatioExtraTracks2.setTitleX("ratio of normal hits");
        h2_chi2OverNDFVsNormalHitRatioExtraTracks2.setTitleY("chi2/ndf");
        histoGroupExtraTracks2.addDataSet(h2_chi2OverNDFVsNormalHitRatioExtraTracks2, 7); 
        histoGroupMap.put(histoGroupExtraTracks2.getName(), histoGroupExtraTracks2);
        
        HistoGroup histoGroupValidCutParameterCompTracksWithSharedHits= new HistoGroup("validCutParameterCompTracksWithSharedHits", 2, 2);
        H2F h2_pidCompTracksWithSharedHits = new H2F("pidCompTracksWithSharedHits", "pid comp. for tracks with shared hits", 100, -35, 35, 100, -35, 35);
        h2_pidCompTracksWithSharedHits.setTitleX("pid in sp1");
        h2_pidCompTracksWithSharedHits.setTitleY("pid in sp2");
        histoGroupValidCutParameterCompTracksWithSharedHits.addDataSet(h2_pidCompTracksWithSharedHits, 0);  
        H2F h2_zCompTracksWithSharedHits = new H2F("zCompTracksWithSharedHits", "z comp. for tracks with shared hits", 100, -50, 50, 100, -50, 50);
        h2_zCompTracksWithSharedHits.setTitleX("z in sp1 (cm)");
        h2_zCompTracksWithSharedHits.setTitleY("z in sp2 (cm)");
        histoGroupValidCutParameterCompTracksWithSharedHits.addDataSet(h2_zCompTracksWithSharedHits, 1);         
        H2F h2_pCompTracksWithSharedHits = new H2F("pCompTracksWithSharedHits", "p comp. for tracks with shared hits", 100, 0, 12, 100, 0, 12);
        h2_pCompTracksWithSharedHits.setTitleX("p in sp1 (GeV)");
        h2_pCompTracksWithSharedHits.setTitleY("p in sp2 (GeV)");
        histoGroupValidCutParameterCompTracksWithSharedHits.addDataSet(h2_pCompTracksWithSharedHits, 2);        
        H2F h2_chi2pidCompTracksWithSharedHits = new H2F("chi2pidCompTracksWithSharedHits", "chi2pid comp. for tracks with shared hits", 100, -10, 10, 100, -10, 10);
        h2_chi2pidCompTracksWithSharedHits.setTitleX("chi2pid in sp1");
        h2_chi2pidCompTracksWithSharedHits.setTitleY("chi2pid in sp2");
        histoGroupValidCutParameterCompTracksWithSharedHits.addDataSet(h2_chi2pidCompTracksWithSharedHits, 3); 
        
        histoGroupMap.put(histoGroupValidCutParameterCompTracksWithSharedHits.getName(), histoGroupValidCutParameterCompTracksWithSharedHits);
        
        ////// Valid tracks
        HistoGroup histoGroupMatchingOverviewValidTracks = new HistoGroup("matchingOverviewValidTracks", 2, 2);
        H1F h1_sharedStatusValidTracks = new H1F("ifHaveSharedHitsValidTracks", "Status for tracks with shared hits for valid tracks", 3, -0.5, 2.5);
        h1_sharedStatusValidTracks.setTitleX("if have shared hits");
        h1_sharedStatusValidTracks.setTitleY("Counts");
        h1_sharedStatusValidTracks.setLineColor(1);
        histoGroupMatchingOverviewValidTracks.addDataSet(h1_sharedStatusValidTracks, 0);
        H2F h2_matchedHitRatioValidTracks = new H2F("matchedHitRatioValidTracks", "Ratio of matched hits between two tracks for valid tracks", 30, 0, 1.05, 30, 0, 1.05);
        h2_matchedHitRatioValidTracks.setTitleX("ratio of matched hits in sp1");
        h2_matchedHitRatioValidTracks.setTitleY("ratio of matched hits in sp2");
        histoGroupMatchingOverviewValidTracks.addDataSet(h2_matchedHitRatioValidTracks, 1);
        H2F h2_normalHitRatioValidTracks = new H2F("normalHitRatioValidTracks", "Ratio of normal hits for tracks with shared hits for valid tracks", 30, 0, 1.05, 30, 0, 1.05);
        h2_normalHitRatioValidTracks.setTitleX("ratio of normal hits in sp1");
        h2_normalHitRatioValidTracks.setTitleY("ratio of normal hits in sp2");
        histoGroupMatchingOverviewValidTracks.addDataSet(h2_normalHitRatioValidTracks, 2);       
        histoGroupMap.put(histoGroupMatchingOverviewValidTracks.getName(), histoGroupMatchingOverviewValidTracks);
        
        TrackHistoGroup histoGroupDiffTracksWithSharedHitsValidTracks = new TrackHistoGroup("diffTracksWithSharedHitsValidTracks", 4, 2);
        histoGroupDiffTracksWithSharedHitsValidTracks.addTrackDiffHistos(1, 0);
        histoGroupMap.put(histoGroupDiffTracksWithSharedHitsValidTracks.getName(), histoGroupDiffTracksWithSharedHitsValidTracks);
        
        TrackHistoGroup histoGroupExtraValidTracks1 = new TrackHistoGroup("extraValidTracks1", 4, 2);
        histoGroupExtraValidTracks1.addTrackHistos(1, 0);
        H2F h2_chi2OverNDFVsNormalHitRatioExtraValidTracks1 = new H2F("chi2OverNDFVsNormalHitRatioExtraValidTracks1", "chi2/ndf vs ratio of normal hits for extra valid tracks1", 30, 0, 1.05, 30, 0, 100);
        h2_chi2OverNDFVsNormalHitRatioExtraValidTracks1.setTitleX("ratio of normal hits");
        h2_chi2OverNDFVsNormalHitRatioExtraValidTracks1.setTitleY("chi2/ndf");
        histoGroupExtraValidTracks1.addDataSet(h2_chi2OverNDFVsNormalHitRatioExtraValidTracks1, 7);         
        histoGroupMap.put(histoGroupExtraValidTracks1.getName(), histoGroupExtraValidTracks1);
        
        TrackHistoGroup histoGroupExtraValidTracks2 = new TrackHistoGroup("extraValidTracks2", 4, 2);
        histoGroupExtraValidTracks2.addTrackHistos(1, 0);
        H2F h2_chi2OverNDFVsNormalHitRatioExtraValidTracks2 = new H2F("chi2OverNDFVsNormalHitRatioExtraValidTracks2", "chi2/ndf vs ratio of normal hits for extra valid tracks2", 30, 0, 1.05, 30, 0, 100);
        h2_chi2OverNDFVsNormalHitRatioExtraValidTracks2.setTitleX("ratio of normal hits");
        h2_chi2OverNDFVsNormalHitRatioExtraValidTracks2.setTitleY("chi2/ndf");
        histoGroupExtraValidTracks2.addDataSet(h2_chi2OverNDFVsNormalHitRatioExtraValidTracks2, 7); 
        histoGroupMap.put(histoGroupExtraValidTracks2.getName(), histoGroupExtraValidTracks2);
        
        
        // Ratio of normal hits for tracks
        HistoGroup histoGroupRatioNormalHitsTracks = new HistoGroup("ratioNormalHitsTracks", 2, 2);
        H1F h1_ratioNormalHitsTracks1 = new H1F("ratioNormalHitsTracks1", "ratio of norma hits for tracks in sp1", 101, 0, 1.01);
        h1_ratioNormalHitsTracks1.setTitleX("ratio of normal hits");
        h1_ratioNormalHitsTracks1.setTitleY("Counts");
        h1_ratioNormalHitsTracks1.setLineColor(1);
        histoGroupRatioNormalHitsTracks.addDataSet(h1_ratioNormalHitsTracks1, 0);
        H1F h1_ratioNormalHitsTracks2 = new H1F("ratioNormalHitsTracks2", "ratio of norma hits for tracks in sp2", 101, 0, 1.01);
        h1_ratioNormalHitsTracks2.setTitleX("ratio of normal hits");
        h1_ratioNormalHitsTracks2.setTitleY("Counts");
        h1_ratioNormalHitsTracks2.setLineColor(1);
        histoGroupRatioNormalHitsTracks.addDataSet(h1_ratioNormalHitsTracks2, 1);     
        H1F h1_ratioNormalHitsValidTracks1 = new H1F("ratioNormalHitsValidTracks1", "ratio of norma hits for valid tracks in sp1", 101, 0, 1.01);
        h1_ratioNormalHitsValidTracks1.setTitleX("ratio of normal hits");
        h1_ratioNormalHitsValidTracks1.setTitleY("Counts");
        h1_ratioNormalHitsValidTracks1.setLineColor(1);
        histoGroupRatioNormalHitsTracks.addDataSet(h1_ratioNormalHitsValidTracks1, 2);
        H1F h1_ratioNormalHitsValidTracks2 = new H1F("ratioNormalHitsValidTracks2", "ratio of norma hits for valid tracks in sp2", 101, 0, 1.01);
        h1_ratioNormalHitsValidTracks2.setTitleX("ratio of normal hits");
        h1_ratioNormalHitsValidTracks2.setTitleY("Counts");
        h1_ratioNormalHitsValidTracks2.setLineColor(1);
        histoGroupRatioNormalHitsTracks.addDataSet(h1_ratioNormalHitsValidTracks2, 3);  
        histoGroupMap.put(histoGroupRatioNormalHitsTracks.getName(), histoGroupRatioNormalHitsTracks);
        
    }

    public void processEvent(Event event1, Event event2, int trkType) {
        //// Read banks
        LocalEvent localEvent1 = new LocalEvent(reader, event1, trkType, Constants.URWELL);
        LocalEvent localEvent2 = new LocalEvent(reader, event2, trkType, Constants.URWELL);
        
        List<Track> trackList1 = new ArrayList();    
        List<Track> trackList2 = new ArrayList();    
        if(trkType == Constants.CONVHB || trkType == Constants.AIHB){
            trackList1 = localEvent1.getTracksHB();
            trackList2 = localEvent2.getTracksHB();
        } else{
            trackList1 = localEvent1.getTracksTB();
            trackList2 = localEvent2.getTracksTB();
        }
        
        //// All tracks
        Map<Track, Track> map_track1_track2 = new HashMap();
        for (Track trk1 : trackList1) {
            int maxMatchedHits = 0;
            Track matchedTrack = null;
            for (Track trk2 : trackList2) {
                int numMatchedHits = trk1.matchedHits(trk2);
                if(Constants.URWELL && !trk1.getURWellCrosses().isEmpty() && !trk2.getURWellCrosses().isEmpty()){
                    for(URWellCross crs1 : trk1.getURWellCrosses()){
                        for(URWellCross crs2 : trk2.getURWellCrosses()){
                            if(crs1.isMatchedCross(crs2)) numMatchedHits+=2;
                        }
                    }
                }
                if (numMatchedHits > maxMatchedHits) {
                    maxMatchedHits = numMatchedHits;
                    matchedTrack = trk2;
                }
            }
            if (matchedTrack != null) {
                map_track1_track2.put(trk1, matchedTrack);
            }
        }
        
        HistoGroup histoGroupMatchingOverview = histoGroupMap.get("matchingOverview");
        TrackHistoGroup histoGroupDiffTracksWithSharedHits = (TrackHistoGroup) histoGroupMap.get("diffTracksWithSharedHits");
        HistoGroup histoGroupValidCutParameterCompTracksWithSharedHits = histoGroupMap.get("validCutParameterCompTracksWithSharedHits");
        for (Track trk1 : map_track1_track2.keySet()) {
            Track trk2 = map_track1_track2.get(trk1);
            int numMatchedHits = trk1.matchedHits(trk2);
            histoGroupMatchingOverview.getH2F("matchedHitRatio").fill((double) numMatchedHits / trk1.nHits(), (double) numMatchedHits / trk2.nHits());
            if (Constants.MC) {
                histoGroupMatchingOverview.getH2F("normalHitRatio").fill(trk1.getRatioNormalHits(), trk2.getRatioNormalHits());
            }
            
            if(trk1.getRatioNormalHits() > 0.85 && trk2.getRatioNormalHits() > 0.85){
                goodTracks++;
            }

            histoGroupDiffTracksWithSharedHits.getHistoChi2overndfDiff().fill(trk1.chi2()/trk1.NDF() - trk2.chi2()/trk2.NDF());
            histoGroupDiffTracksWithSharedHits.getHistoPDiff().fill(trk1.p() - trk2.p());
            histoGroupDiffTracksWithSharedHits.getHistoThetaDiff().fill(trk1.theta() - trk2.theta());
            histoGroupDiffTracksWithSharedHits.getHistoPhiDiff().fill(trk1.phi() - trk2.phi());
            histoGroupDiffTracksWithSharedHits.getHistoVxDiff().fill(trk1.vx() - trk2.vx());
            histoGroupDiffTracksWithSharedHits.getHistoVyDiff().fill(trk1.vy() - trk2.vy());
            histoGroupDiffTracksWithSharedHits.getHistoVzDiff().fill(trk1.vz() - trk2.vz());
            
            histoGroupValidCutParameterCompTracksWithSharedHits.getH2F("pidCompTracksWithSharedHits").fill(trk1.pid()/100., trk2.pid()/100.);
            histoGroupValidCutParameterCompTracksWithSharedHits.getH2F("zCompTracksWithSharedHits").fill(trk1.vz(), trk2.vz());
            histoGroupValidCutParameterCompTracksWithSharedHits.getH2F("pCompTracksWithSharedHits").fill(trk1.p(), trk2.p());
            histoGroupValidCutParameterCompTracksWithSharedHits.getH2F("chi2pidCompTracksWithSharedHits").fill(trk1.chi2pid(), trk2.chi2pid());
        }

        TrackHistoGroup histoGroupExtraTracks1 = (TrackHistoGroup) histoGroupMap.get("extraTracks1");
        List<Track> trkList_extraSample1 = new ArrayList();
        for (Track trk1 : trackList1) {
            if (map_track1_track2.containsKey(trk1)) {
                histoGroupMatchingOverview.getH1F("ifHaveSharedHits").fill(1);
            } else {
                histoGroupMatchingOverview.getH1F("ifHaveSharedHits").fill(0);
                //this.addDemoGroup(localEvent1, localEvent2, trk1.sector(), "extra1");
                extraTracksSp1++;
                
                trkList_extraSample1.add(trk1);
                histoGroupExtraTracks1.getHistoChi2overndf().fill(trk1.chi2()/trk1.NDF());
                histoGroupExtraTracks1.getHistoP().fill(trk1.p());
                histoGroupExtraTracks1.getHistoTheta().fill(trk1.theta());
                histoGroupExtraTracks1.getHistoPhi().fill(trk1.phi());
                histoGroupExtraTracks1.getHistoVx().fill(trk1.vx());
                histoGroupExtraTracks1.getHistoVy().fill(trk1.vy());
                histoGroupExtraTracks1.getHistoVz().fill(trk1.vz());
                
                if (Constants.MC) histoGroupExtraTracks1.getH2F("chi2OverNDFVsNormalHitRatioExtraTracks1").fill(trk1.getRatioNormalHits(), trk1.chi2() / trk1.NDF());
            }
        }
        
        TrackHistoGroup histoGroupExtraTracks2 = (TrackHistoGroup) histoGroupMap.get("extraTracks2");
        List<Track> trkList_extraSample2 = new ArrayList();
        for (Track trk2 : trackList2) {
            if (!map_track1_track2.containsValue(trk2)) {
                histoGroupMatchingOverview.getH1F("ifHaveSharedHits").fill(2);
                //this.addDemoGroup(localEvent1, localEvent2, trk2.sector(), "extra2");
                extraTracksSp2++;
                
                trkList_extraSample2.add(trk2);
                histoGroupExtraTracks2.getHistoChi2overndf().fill(trk2.chi2()/trk2.NDF());
                histoGroupExtraTracks2.getHistoP().fill(trk2.p());
                histoGroupExtraTracks2.getHistoTheta().fill(trk2.theta());
                histoGroupExtraTracks2.getHistoPhi().fill(trk2.phi());
                histoGroupExtraTracks2.getHistoVx().fill(trk2.vx());
                histoGroupExtraTracks2.getHistoVy().fill(trk2.vy());
                histoGroupExtraTracks2.getHistoVz().fill(trk2.vz()); 
                
                if (Constants.MC) histoGroupExtraTracks2.getH2F("chi2OverNDFVsNormalHitRatioExtraTracks2").fill(trk2.getRatioNormalHits(), trk2.chi2() / trk2.NDF());
            }
        }
        
        ////// Valid Tracks
        HistoGroup histoGroupRatioNormalHitsTracks = histoGroupMap.get("ratioNormalHitsTracks");        
        List<Track> validTrackListSp1 = new ArrayList();
        for (Track trk1 : trackList1) {
            histoGroupRatioNormalHitsTracks.getH1F("ratioNormalHitsTracks1").fill(trk1.getRatioNormalHits());
            tracksSp1++;
            if(trk1.isValid()) {
                validTracksSp1++;
                validTrackListSp1.add(trk1);
                histoGroupRatioNormalHitsTracks.getH1F("ratioNormalHitsValidTracks1").fill(trk1.getRatioNormalHits());
            }
        }
        
        List<Track> validTrackListSp2 = new ArrayList();
        for (Track trk2 : trackList2) {
            histoGroupRatioNormalHitsTracks.getH1F("ratioNormalHitsTracks2").fill(trk2.getRatioNormalHits());
            tracksSp2++;
            if(trk2.isValid()) {
                validTracksSp2++;
                validTrackListSp2.add(trk2);
                histoGroupRatioNormalHitsTracks.getH1F("ratioNormalHitsValidTracks2").fill(trk2.getRatioNormalHits());
            }
        }
                        
        Map<Track, Track> map_validTrack1_validTrack2 = new HashMap();
        for (Track trk1 : validTrackListSp1) {
            int maxMatchedHits = 0;
            Track matchedTrack = null;
            for (Track trk2 : validTrackListSp2) {
                int numMatchedHits = trk1.matchedHits(trk2);
                if(Constants.URWELL && !trk1.getURWellCrosses().isEmpty() && !trk2.getURWellCrosses().isEmpty()){
                    for(URWellCross crs1 : trk1.getURWellCrosses()){
                        for(URWellCross crs2 : trk2.getURWellCrosses()){
                            if(crs1.isMatchedCross(crs2)) numMatchedHits+=2;
                        }
                    }
                }
                if (numMatchedHits > maxMatchedHits) {
                    maxMatchedHits = numMatchedHits;
                    matchedTrack = trk2;
                }
            }
            if (matchedTrack != null) {
                map_validTrack1_validTrack2.put(trk1, matchedTrack);
            }
        }
        
        matchedValidTracks += map_validTrack1_validTrack2.keySet().size();

        HistoGroup histoGroupMatchingOverviewValidTracks = histoGroupMap.get("matchingOverviewValidTracks");
        TrackHistoGroup histoGroupDiffTracksWithSharedHitsValidTracks = (TrackHistoGroup) histoGroupMap.get("diffTracksWithSharedHitsValidTracks");        
        for (Track trk1 : map_validTrack1_validTrack2.keySet()) {
            Track trk2 = map_validTrack1_validTrack2.get(trk1);
            int numMatchedHits = trk1.matchedHits(trk2);
            histoGroupMatchingOverviewValidTracks.getH2F("matchedHitRatioValidTracks").fill((double) numMatchedHits / trk1.nHits(), (double) numMatchedHits / trk2.nHits());
            if (Constants.MC) {
                histoGroupMatchingOverviewValidTracks.getH2F("normalHitRatioValidTracks").fill(trk1.getRatioNormalHits(), trk2.getRatioNormalHits());
            }
            
            histoGroupDiffTracksWithSharedHitsValidTracks.getHistoChi2overndfDiff().fill(trk1.chi2()/trk1.NDF() - trk2.chi2()/trk2.NDF());
            histoGroupDiffTracksWithSharedHitsValidTracks.getHistoPDiff().fill(trk1.p() - trk2.p());
            histoGroupDiffTracksWithSharedHitsValidTracks.getHistoThetaDiff().fill(trk1.theta() - trk2.theta());
            histoGroupDiffTracksWithSharedHitsValidTracks.getHistoPhiDiff().fill(trk1.phi() - trk2.phi());
            histoGroupDiffTracksWithSharedHitsValidTracks.getHistoVxDiff().fill(trk1.vx() - trk2.vx());
            histoGroupDiffTracksWithSharedHitsValidTracks.getHistoVyDiff().fill(trk1.vy() - trk2.vy());
            histoGroupDiffTracksWithSharedHitsValidTracks.getHistoVzDiff().fill(trk1.vz() - trk2.vz());
        }

        List<Track> trkList_validExtraSample1 = new ArrayList();
        for (Track trk1 : validTrackListSp1) {
            if (map_validTrack1_validTrack2.containsKey(trk1)) {
                histoGroupMatchingOverviewValidTracks.getH1F("ifHaveSharedHitsValidTracks").fill(1);
            } else {
                histoGroupMatchingOverviewValidTracks.getH1F("ifHaveSharedHitsValidTracks").fill(0);                
                extraValidTracksSp1++;                
                trkList_validExtraSample1.add(trk1);
            }
        }
        
        List<Track> trkList_validExtraSample2 = new ArrayList();
        for (Track trk2 : validTrackListSp2) {
            if (!map_validTrack1_validTrack2.containsValue(trk2)) {
                histoGroupMatchingOverviewValidTracks.getH1F("ifHaveSharedHitsValidTracks").fill(2);                
                extraValidTracksSp2++;                
                trkList_validExtraSample2.add(trk2);              
            }
        }
        
        TrackHistoGroup histoGroupExtraValidTracks1 = (TrackHistoGroup) histoGroupMap.get("extraValidTracks1");
        TrackHistoGroup histoGroupExtraValidTracks2 = (TrackHistoGroup) histoGroupMap.get("extraValidTracks2");
        if (Constants.MC) {
            for (Track trk1 : trkList_validExtraSample1) {                                     
                histoGroupExtraValidTracks1.getHistoChi2overndf().fill(trk1.chi2() / trk1.NDF());
                histoGroupExtraValidTracks1.getHistoP().fill(trk1.p());
                histoGroupExtraValidTracks1.getHistoTheta().fill(trk1.theta());
                histoGroupExtraValidTracks1.getHistoPhi().fill(trk1.phi());
                histoGroupExtraValidTracks1.getHistoVx().fill(trk1.vx());
                histoGroupExtraValidTracks1.getHistoVy().fill(trk1.vy());
                histoGroupExtraValidTracks1.getHistoVz().fill(trk1.vz());
                
                histoGroupExtraValidTracks1.getH2F("chi2OverNDFVsNormalHitRatioExtraValidTracks1").fill(trk1.getRatioNormalHits(), trk1.chi2() / trk1.NDF());

                if (trk1.getRatioNormalHits() > 0.9){
                    this.addDemoGroup(localEvent1, localEvent2, trk1.sector(), "SP1");
                }
                
            }
            for (Track trk2 : trkList_validExtraSample2) {
                histoGroupExtraValidTracks2.getHistoChi2overndf().fill(trk2.chi2() / trk2.NDF());
                histoGroupExtraValidTracks2.getHistoP().fill(trk2.p());
                histoGroupExtraValidTracks2.getHistoTheta().fill(trk2.theta());
                histoGroupExtraValidTracks2.getHistoPhi().fill(trk2.phi());
                histoGroupExtraValidTracks2.getHistoVx().fill(trk2.vx());
                histoGroupExtraValidTracks2.getHistoVy().fill(trk2.vy());
                histoGroupExtraValidTracks2.getHistoVz().fill(trk2.vz());
                
                histoGroupExtraValidTracks2.getH2F("chi2OverNDFVsNormalHitRatioExtraValidTracks2").fill(trk2.getRatioNormalHits(), trk2.chi2() / trk2.NDF());
                
                if (trk2.getRatioNormalHits() > 0.9) {
                    this.addDemoGroup(localEvent1, localEvent2, trk2.sector(), "SP2");
                }

            }
        } else {
            for (Track trk1 : trkList_validExtraSample1) {
                histoGroupExtraValidTracks1.getHistoChi2overndf().fill(trk1.chi2() / trk1.NDF());
                histoGroupExtraValidTracks1.getHistoP().fill(trk1.p());
                histoGroupExtraValidTracks1.getHistoTheta().fill(trk1.theta());
                histoGroupExtraValidTracks1.getHistoPhi().fill(trk1.phi());
                histoGroupExtraValidTracks1.getHistoVx().fill(trk1.vx());
                histoGroupExtraValidTracks1.getHistoVy().fill(trk1.vy());
                histoGroupExtraValidTracks1.getHistoVz().fill(trk1.vz());

                this.addDemoGroup(localEvent1, localEvent2, trk1.sector(), "SP1");

            }
            for (Track trk2 : trkList_validExtraSample2) {
                histoGroupExtraValidTracks2.getHistoChi2overndf().fill(trk2.chi2() / trk2.NDF());
                histoGroupExtraValidTracks2.getHistoP().fill(trk2.p());
                histoGroupExtraValidTracks2.getHistoTheta().fill(trk2.theta());
                histoGroupExtraValidTracks2.getHistoPhi().fill(trk2.phi());
                histoGroupExtraValidTracks2.getHistoVx().fill(trk2.vx());
                histoGroupExtraValidTracks2.getHistoVy().fill(trk2.vy());
                histoGroupExtraValidTracks2.getHistoVz().fill(trk2.vz());

                this.addDemoGroup(localEvent1, localEvent2, trk2.sector(), "SP2");
            }
        }
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

        CompareTracksHitLevel analysis = new CompareTracksHitLevel();
        analysis.createHistoGroupMap();
        if (!readHistos) {
            HipoReader reader1 = new HipoReader();
            reader1.open(inputList.get(0));
            HipoReader reader2 = new HipoReader();
            reader2.open(inputList.get(1));

            SchemaFactory schema = reader1.getSchemaFactory();
            analysis.initReader(new Banks(schema));

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
            System.out.println("Tracks in sample1: " + Integer.toString(CompareTracksHitLevel.tracksSp1));
            System.out.println("Tracks in sample2: " + Integer.toString(CompareTracksHitLevel.tracksSp2));
            System.out.println("Good tracks: " + Integer.toString(CompareTracksHitLevel.goodTracks));
            System.out.println("Valid tracks in sample1: " + Integer.toString(CompareTracksHitLevel.validTracksSp1));
            System.out.println("Valid tracks in sample2: " + Integer.toString(CompareTracksHitLevel.validTracksSp2));
            System.out.println("Extra tracks in sample1: " + Integer.toString(CompareTracksHitLevel.extraTracksSp1));
            System.out.println("Extra tracks in sample2: " + Integer.toString(CompareTracksHitLevel.extraTracksSp2));                                    
            System.out.println("Matched valid tracks         : " + Integer.toString(CompareTracksHitLevel.matchedValidTracks));
            System.out.println("Extra valid tracks in sample1: " + Integer.toString(CompareTracksHitLevel.extraValidTracksSp1));
            System.out.println("Extra valid tracks in sample2: " + Integer.toString(CompareTracksHitLevel.extraValidTracksSp2));
        } else {
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
