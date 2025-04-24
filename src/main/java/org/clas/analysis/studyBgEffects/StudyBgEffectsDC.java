package org.clas.analysis.studyBgEffects;

import java.util.List;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import javax.swing.JFrame;
import org.jlab.groot.data.H1F;
import org.jlab.groot.data.H2F;
import org.jlab.groot.graphics.EmbeddedCanvasTabbed;
import org.jlab.jnp.hipo4.data.Event;
import org.jlab.jnp.hipo4.data.SchemaFactory;
import org.jlab.jnp.hipo4.io.HipoReader;
import org.jlab.utils.benchmark.ProgressPrintout;
import org.jlab.utils.options.OptionParser;

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
import org.clas.reader.Banks;

/**
 *
 * @author Tongtong Cao
 */
public class StudyBgEffectsDC extends BaseAnalysis{
    
    public StudyBgEffectsDC(){}
    
    @Override
    public void createHistoGroupMap(){                
        HistoGroup histoEventGroup = new HistoGroup("event", 3, 2); 
        H1F h1_numTracks1 = new H1F("numTracks1", "numTracks", 11, -0.5, 10.5);
        h1_numTracks1.setTitleX("# of tracks");
        h1_numTracks1.setTitleY("Counts");
        h1_numTracks1.setLineColor(1);
        histoEventGroup.addDataSet(h1_numTracks1, 0);
        H1F h1_numTracks2 = new H1F("numTracks2", "numTracks", 11, -0.5, 10.5);
        h1_numTracks2.setTitleX("# of tracks");
        h1_numTracks2.setTitleY("Counts");
        h1_numTracks2.setLineColor(2);
        histoEventGroup.addDataSet(h1_numTracks2, 0);         
        H1F h1_numPosTracks1 = new H1F("numPosTracks1", "numPosTracks", 6, -0.5, 5.5);
        h1_numPosTracks1.setTitleX("# of pos. tracks");
        h1_numPosTracks1.setTitleY("Counts");
        h1_numPosTracks1.setLineColor(1);
        histoEventGroup.addDataSet(h1_numPosTracks1, 1);
        H1F h1_numPosTracks2 = new H1F("numPosTracks2", "numPosTracks", 6, -0.5, 5.5);
        h1_numPosTracks2.setTitleX("# of pos. tracks");
        h1_numPosTracks2.setTitleY("Counts");
        h1_numPosTracks2.setLineColor(2);
        histoEventGroup.addDataSet(h1_numPosTracks2, 1);        
        H1F h1_numNegTracks1 = new H1F("numNegTracks1", "numNegTracks", 6, -0.5, 5.5);
        h1_numNegTracks1.setTitleX("# of neg. tracks");
        h1_numNegTracks1.setTitleY("Counts");
        h1_numNegTracks1.setLineColor(1);
        histoEventGroup.addDataSet(h1_numNegTracks1, 2);
        H1F h1_numNegTracks2 = new H1F("numNegTracks2", "numNegTracks", 6, -0.5, 5.5);
        h1_numNegTracks2.setTitleX("# of neg. tracks");
        h1_numNegTracks2.setTitleY("Counts");
        h1_numNegTracks2.setLineColor(2);
        histoEventGroup.addDataSet(h1_numNegTracks2, 2);        
        H1F h1_numTriggerTracks1 = new H1F("numTriggerTracks1", "numTriggerTrack", 2, -0.5, 1.5);
        h1_numTriggerTracks1.setTitleX("# of trigger tracks");
        h1_numTriggerTracks1.setTitleY("Counts");
        h1_numTriggerTracks1.setLineColor(1);
        histoEventGroup.addDataSet(h1_numTriggerTracks1, 3);
        H1F h1_numTriggerTracks2 = new H1F("numTriggerTracks2", "numTriggerTrack", 2, -0.5, 1.5);
        h1_numTriggerTracks2.setTitleX("# of trigger tracks");
        h1_numTriggerTracks2.setTitleY("Counts");
        h1_numTriggerTracks2.setLineColor(2);
        histoEventGroup.addDataSet(h1_numTriggerTracks2, 3);        
        H1F h1_numPosTracksTriggered1 = new H1F("numPosTracksTriggered1", "numPosTracksWithTrigger", 6, -0.5, 5.5);
        h1_numPosTracksTriggered1.setTitleX("# of pos. tracks");
        h1_numPosTracksTriggered1.setTitleY("Counts");
        h1_numPosTracksTriggered1.setLineColor(1);
        histoEventGroup.addDataSet(h1_numPosTracksTriggered1, 4);
        H1F h1_numPosTracksTriggered2 = new H1F("numPosTracksTriggered2", "numPosTracksWithTrigger", 6, -0.5, 5.5);
        h1_numPosTracksTriggered2.setTitleX("# of pos. tracks");
        h1_numPosTracksTriggered2.setTitleY("Counts");
        h1_numPosTracksTriggered2.setLineColor(2);
        histoEventGroup.addDataSet(h1_numPosTracksTriggered2, 4);        
        H1F h1_numNegTracksTriggered1 = new H1F("numNegTracksTriggered1", "numNegTracksWithTrigger", 6, -0.5, 5.5);
        h1_numNegTracksTriggered1.setTitleX("# of neg. tracks");
        h1_numNegTracksTriggered1.setTitleY("Counts");
        h1_numNegTracksTriggered1.setLineColor(1);
        histoEventGroup.addDataSet(h1_numNegTracksTriggered1, 5);
        H1F h1_numNegTracksTriggered2 = new H1F("numNegTracksTriggered2", "numNegTracksWithTrigger", 6, -0.5, 5.5);
        h1_numNegTracksTriggered2.setTitleX("# of neg. tracks");
        h1_numNegTracksTriggered2.setTitleY("Counts");
        h1_numNegTracksTriggered2.setLineColor(2);
        histoEventGroup.addDataSet(h1_numNegTracksTriggered2, 5);        
        histoGroupMap.put(histoEventGroup.getName(), histoEventGroup);  
        
        TrackHistoGroup histoTriggerGroup = new TrackHistoGroup("trigger", 4, 2);        
        H1F h1_isTriggered = new H1F("isTriggered", "isTriggered", 4, 0.5, 4.5);
        histoTriggerGroup.addDataSet(h1_isTriggered, 0);        
        histoTriggerGroup.addTrackHistos("1", 1, 1);
        histoTriggerGroup.addTrackHistos("2", 2, 1);                
        histoGroupMap.put(histoTriggerGroup.getName(), histoTriggerGroup); 
        
        TrackHistoGroup histoTriggerDiffGroup = new TrackHistoGroup("triggerDiff", 4, 2);  
        H1F h1_isTriggered2 = new H1F("isTriggered2", "isTriggered", 3, 1.5, 4.5);
        histoTriggerDiffGroup.addDataSet(h1_isTriggered2, 0); 
        histoTriggerDiffGroup.addTrackDiffHistos(1, 1);              
        histoGroupMap.put(histoTriggerDiffGroup.getName(), histoTriggerDiffGroup);
        
        
        HistoGroup histoTriggerExtra1Group = new HistoGroup("triggerExtra1", 4, 2);
        H1F h1_orderSample2 = new H1F("orderSample2", "order", 140, -20, 120);
        h1_orderSample2.setTitleX("order");
        h1_orderSample2.setTitleY("Counts");
        h1_orderSample2.setLineColor(1);
        histoTriggerExtra1Group.addDataSet(h1_orderSample2, 0);
        
        H1F h1_hitLostRatioDenoisingingSample2 = new H1F("hitLostRatioDenoisingSample2", "after merge&denoising", 100, 0, 1);
        h1_hitLostRatioDenoisingingSample2.setTitleX("Ratio of L/R hits");
        h1_hitLostRatioDenoisingingSample2.setTitleY("Counts");
        h1_hitLostRatioDenoisingingSample2.setLineColor(1);
        histoTriggerExtra1Group.addDataSet(h1_hitLostRatioDenoisingingSample2, 1);
        H1F h1_hitRemainRatioDenoisingSample2 = new H1F("hitRemainRatioDenoisingSample2", "after merge&denoising", 100, 0, 1);
        h1_hitRemainRatioDenoisingSample2.setTitleX("");
        h1_hitRemainRatioDenoisingSample2.setTitleY("Counts");
        h1_hitRemainRatioDenoisingSample2.setLineColor(2);
        histoTriggerExtra1Group.addDataSet(h1_hitRemainRatioDenoisingSample2, 1);
        

        
        
        H1F h1_hitLostRatioClusteringSample2 = new H1F("hitLostRatioClusteringSample2", "after clustering", 100, 0, 1);
        h1_hitLostRatioClusteringSample2.setTitleX("Ratio of L/R hits");
        h1_hitLostRatioClusteringSample2.setTitleY("Counts");
        h1_hitLostRatioClusteringSample2.setLineColor(1);
        histoTriggerExtra1Group.addDataSet(h1_hitLostRatioClusteringSample2, 2);
        H1F h1_hitRemainRatioClusteringSample2 = new H1F("hitRemainRatioClusteringSample2", "after clustering", 100, 0, 1);
        h1_hitRemainRatioClusteringSample2.setTitleX("");
        h1_hitRemainRatioClusteringSample2.setTitleY("Counts");
        h1_hitRemainRatioClusteringSample2.setLineColor(2);
        histoTriggerExtra1Group.addDataSet(h1_hitRemainRatioClusteringSample2, 2);
        
        H1F h1_hitLostRatioHBTrackingSample2 = new H1F("hitLostRatioHBTrackingSample2", "after HB tracking", 100, 0, 1);
        h1_hitLostRatioHBTrackingSample2.setTitleX("Ratio of L/R hits");
        h1_hitLostRatioHBTrackingSample2.setTitleY("Counts");
        h1_hitLostRatioHBTrackingSample2.setLineColor(1);
        histoTriggerExtra1Group.addDataSet(h1_hitLostRatioHBTrackingSample2, 3);
        H1F h1_hitRemainRatioHBTrackingSample2 = new H1F("hitRemainRatioHBTrackingSample2", "after HB tracking", 100, 0, 1);
        h1_hitRemainRatioHBTrackingSample2.setTitleX("");
        h1_hitRemainRatioHBTrackingSample2.setTitleY("Counts");
        h1_hitRemainRatioHBTrackingSample2.setLineColor(2);
        histoTriggerExtra1Group.addDataSet(h1_hitRemainRatioHBTrackingSample2, 3);
        
        H1F h1_hitLostRatioTBTrackingSample2 = new H1F("hitLostRatioTBTrackingSample2", "after TB tracking", 100, 0, 1);
        h1_hitLostRatioTBTrackingSample2.setTitleX("Ratio of L/R hits");
        h1_hitLostRatioTBTrackingSample2.setTitleY("Counts");
        h1_hitLostRatioTBTrackingSample2.setLineColor(1);
        histoTriggerExtra1Group.addDataSet(h1_hitLostRatioTBTrackingSample2, 4);
        H1F h1_hitRemainRatioTBTrackingSample2 = new H1F("hitRemainRatioTBTrackingSample2", "after TB tracking", 100, 0, 1);
        h1_hitRemainRatioTBTrackingSample2.setTitleX("");
        h1_hitRemainRatioTBTrackingSample2.setTitleY("Counts");
        h1_hitRemainRatioTBTrackingSample2.setLineColor(2);
        histoTriggerExtra1Group.addDataSet(h1_hitRemainRatioTBTrackingSample2, 4);
        
        H1F h1_hitsInClusters = new H1F("hitsInClusters", "remainingHitsInClustersSample1", 12, 0, 12);
        h1_hitsInClusters.setTitleX("hits in clusters");
        h1_hitsInClusters.setTitleY("Counts");
        h1_hitsInClusters.setLineColor(1);
        histoTriggerExtra1Group.addDataSet(h1_hitsInClusters, 5);         
        H1F h1_hitsInClustersDenoisingSample2 = new H1F("hitsInClustersDenoisingSample2", "remainingHitsInClustersSample1", 12, 0, 12);
        h1_hitsInClustersDenoisingSample2.setTitleX("hits in clusters");
        h1_hitsInClustersDenoisingSample2.setTitleY("Counts");
        h1_hitsInClustersDenoisingSample2.setLineColor(2);
        histoTriggerExtra1Group.addDataSet(h1_hitsInClustersDenoisingSample2, 5);  
        H1F h1_hitsInClustersClusteringSample2 = new H1F("hitsInClustersClusteringSample2", "remainingHitsInClustersSample1", 12, 0, 12);
        h1_hitsInClustersClusteringSample2.setTitleX("hits in clusters");
        h1_hitsInClustersClusteringSample2.setTitleY("Counts");
        h1_hitsInClustersClusteringSample2.setLineColor(3);
        histoTriggerExtra1Group.addDataSet(h1_hitsInClustersClusteringSample2, 5);  
        
        
        
        H1F h1_hitsInClustersDistirbuteDenoisingingSample2 = new H1F("hitsInClustersDistirbuteDenoisingingSample2", "clusters that hits distribute", 12, 0, 12);
        h1_hitsInClustersDistirbuteDenoisingingSample2.setTitleX("clusters that hits distribute");
        h1_hitsInClustersDistirbuteDenoisingingSample2.setTitleY("Counts");
        h1_hitsInClustersDistirbuteDenoisingingSample2.setLineColor(2);
        histoTriggerExtra1Group.addDataSet(h1_hitsInClustersDistirbuteDenoisingingSample2, 6);  
        
        
        
        
        histoGroupMap.put(histoTriggerExtra1Group.getName(), histoTriggerExtra1Group); 
        
        
        HistoGroup histoTriggerBothGroup = new HistoGroup("triggerBoth", 4, 2);        
        histoGroupMap.put(histoTriggerBothGroup.getName(), histoTriggerBothGroup); 
        
        
        
        
        
        
        
        
        
        TrackHistoGroup histoPosTracksGroup = new TrackHistoGroup("posTracks", 4, 2);              
        histoPosTracksGroup.addTrackHistos("1", 1, 0);
        histoPosTracksGroup.addTrackHistos("2", 2, 0);                
        histoGroupMap.put(histoPosTracksGroup.getName(), histoPosTracksGroup); 
        
        TrackHistoGroup histoNegTracksGroup = new TrackHistoGroup("negTracks", 4, 2);              
        histoNegTracksGroup.addTrackHistos("1", 1, 0);
        histoNegTracksGroup.addTrackHistos("2", 2, 0);                
        histoGroupMap.put(histoNegTracksGroup.getName(), histoNegTracksGroup); 
        
        
    }
             
    public void processEvent(Event event1, Event event2, int trkType){
        //// Read banks
        // TDC
        List<TDC> tdcs1 = reader1.readTDCs(event1);
        List<TDC> tdcs2 = reader2.readTDCs(event2);
        
        // Hits and clusters from clustring
        List<Hit> hitsClustering1 = reader1.readHits(event1, 0);
        List<Hit> hitsClustering2 = reader2.readHits(event2, 0);        
        List<Cluster> clustersClustering1 = reader1.readClusters(event1, hitsClustering1, 0);
        List<Cluster> clustersClustering2 = reader2.readClusters(event2, hitsClustering2, 0);
        
        // Hits, clusters and tracks from HB tracking
        List<Hit> hitsHB1 = reader1.readHits(event1, trkType-1);
        List<Hit> hitsHB2 = reader2.readHits(event2, trkType-1);
        List<Cluster> clustersHB1 = reader1.readClusters(event1, hitsHB1, trkType-1);
        List<Cluster> clustersHB2 = reader2.readClusters(event2, hitsHB2, trkType-1);        
        List<Track> tracksHB1 = reader1.readTracks(event1, trkType-1);           
        List<Track> tracksHB2 = reader2.readTracks(event2, trkType-1);           
        for(Track trk : tracksHB1) trk.setHitsClusters(clustersHB1);
        for(Track trk : tracksHB2) trk.setHitsClusters(clustersHB2);
        
        // Hits, clusters and tracks from TB tracking
        List<Hit> hitsTB1 = reader1.readHits(event1, trkType);
        List<Hit> hitsTB2 = reader2.readHits(event2, trkType);
        List<Cluster> clustersTB1 = reader1.readClusters(event1, hitsTB1, trkType);
        List<Cluster> clustersTB2 = reader2.readClusters(event2, hitsTB2, trkType);        
        List<Track> tracksTB1 = reader1.readTracks(event1, trkType);           
        List<Track> tracksTB2 = reader2.readTracks(event2, trkType);         
        for(Track trk : tracksTB1) trk.setHitsClusters(clustersTB1);
        for(Track trk : tracksTB2) trk.setHitsClusters(clustersTB2);
        
        ////// Build events
        EventForLumiScan localEvent1 = new EventForLumiScan(tracksTB1);
        EventForLumiScan localEvent2 = new EventForLumiScan(tracksTB2); 
        Track trigger1 = localEvent1.getTriggerTrk();
        Track trigger2 = localEvent2.getTriggerTrk();        
        List<Track> posTracks1 = localEvent1.getPosTrks();
        List<Track> posTracks2 = localEvent2.getPosTrks();        
        List<Track> negTracks1 = localEvent1.getNegTrks();
        List<Track> negTracks2 = localEvent2.getNegTrks();
        
        ////// Event
        HistoGroup histoEventGroup = histoGroupMap.get("event");
        histoEventGroup.getH1F("numTracks1").fill(localEvent1.getTracks().size());
        histoEventGroup.getH1F("numTracks2").fill(localEvent2.getTracks().size());
        histoEventGroup.getH1F("numPosTracks1").fill(posTracks1.size());
        histoEventGroup.getH1F("numNegTracks1").fill(negTracks1.size());
        histoEventGroup.getH1F("numPosTracks2").fill(posTracks2.size());                
        histoEventGroup.getH1F("numNegTracks2").fill(negTracks2.size());        
        if(trigger1 == null) histoEventGroup.getH1F("numTriggerTracks1").fill(0);
        else histoEventGroup.getH1F("numTriggerTracks1").fill(1); 
        if(trigger2 == null) histoEventGroup.getH1F("numTriggerTracks2").fill(0);
        else histoEventGroup.getH1F("numTriggerTracks2").fill(1);                
        if(trigger1 != null){
            histoEventGroup.getH1F("numPosTracksTriggered1").fill(posTracks1.size());
            histoEventGroup.getH1F("numNegTracksTriggered1").fill(negTracks1.size());
        }        
        if(trigger2 != null){
            histoEventGroup.getH1F("numPosTracksTriggered2").fill(posTracks2.size());                
            histoEventGroup.getH1F("numNegTracksTriggered2").fill(negTracks2.size());
        }
        
        ////// Trigger
        TrackHistoGroup histoTriggerGroup = (TrackHistoGroup)histoGroupMap.get("trigger");
        if(trigger1 == null && trigger2 == null) histoTriggerGroup.getH1F("isTriggered").fill(1);
        else if(trigger1 != null && trigger2 == null) histoTriggerGroup.getH1F("isTriggered").fill(2);
        else if(trigger1 == null && trigger2 != null) histoTriggerGroup.getH1F("isTriggered").fill(3);
        else histoTriggerGroup.getH1F("isTriggered").fill(4);
        if(trigger1 != null) {
            histoTriggerGroup.getHistoChi2overndf("1").fill(trigger1.chi2()/trigger1.NDF());
            histoTriggerGroup.getHistoP("1").fill(trigger1.p());
            histoTriggerGroup.getHistoTheta("1").fill(trigger1.theta());
            histoTriggerGroup.getHistoPhi("1").fill(trigger1.phi());
            histoTriggerGroup.getHistoVx("1").fill(trigger1.vx());
            histoTriggerGroup.getHistoVy("1").fill(trigger1.vy());
            histoTriggerGroup.getHistoVz("1").fill(trigger1.vz());
        }        
        if(trigger2 != null) {
            histoTriggerGroup.getHistoChi2overndf("2").fill(trigger2.chi2()/trigger2.NDF());
            histoTriggerGroup.getHistoP("2").fill(trigger2.p());
            histoTriggerGroup.getHistoTheta("2").fill(trigger2.theta());
            histoTriggerGroup.getHistoPhi("2").fill(trigger2.phi());
            histoTriggerGroup.getHistoVx("2").fill(trigger2.vx());
            histoTriggerGroup.getHistoVy("2").fill(trigger2.vy());
            histoTriggerGroup.getHistoVz("2").fill(trigger2.vz());
        }
        
        TrackHistoGroup histoTriggerDiffGroup = (TrackHistoGroup)histoGroupMap.get("triggerDiff");
        HistoGroup histoTriggerExtra1Group = histoGroupMap.get("triggerExtra1");
        
        if(trigger1 != null && trigger2 == null) { // trigger exisits in sample 1, while not in sample 2
            histoTriggerDiffGroup.getH1F("isTriggered2").fill(2);
            
            List<Hit> hitsTrigger1 = trigger1.getHits();
            LinkedHashMap<Integer, List<Hit>> map_clusterID_hitList_trigger1 = new LinkedHashMap();
            for(Hit hit1 : hitsTrigger1){
                boolean flag = false;
                for(int clusterID : map_clusterID_hitList_trigger1.keySet()){
                    if(hit1.ClusterID() == clusterID){
                        map_clusterID_hitList_trigger1.get(clusterID).add(hit1);
                        flag = true;
                        break;
                    }
                }
                if(!flag) {
                    List<Hit> newHitList = new ArrayList();
                    newHitList.add(hit1);
                    map_clusterID_hitList_trigger1.put(hit1.ClusterID(), newHitList);
                }                
            }
            for(int clusterID : map_clusterID_hitList_trigger1.keySet()){
                histoTriggerExtra1Group.getH1F("hitsInClusters").fill(map_clusterID_hitList_trigger1.get(clusterID).size());
            }
            
            // Check denoising
            List<Hit> hitsTrigger1Denoising2 = new ArrayList();
            for(Hit hit1 : hitsTrigger1){
                boolean flag = false;
                boolean flagOrder = false;
                for(TDC tdc2 : tdcs2){
                    if(hit1.sector() == tdc2.sector() && hit1.superlayer() == tdc2.superlayer() 
                            && hit1.layer() == tdc2.layer() && hit1.wire() == tdc2.component() && hit1.TDC() == tdc2.TDC()){
                        histoTriggerExtra1Group.getH1F("orderSample2").fill(tdc2.order());
                        flag = true;
                        if(tdc2.order() == 0 || tdc2.order() == 10 || tdc2.order() == 40 || tdc2.order() == 70) flagOrder = true;
                        break;
                    }
                }
                if(!flag) {
                    histoTriggerExtra1Group.getH1F("orderSample2").fill(-10);                    
                }
                if(flagOrder) hitsTrigger1Denoising2.add(hit1);
            }
            histoTriggerExtra1Group.getH1F("hitLostRatioDenoisingSample2").fill(1- (double)hitsTrigger1Denoising2.size()/hitsTrigger1.size());
            histoTriggerExtra1Group.getH1F("hitRemainRatioDenoisingSample2").fill((double)hitsTrigger1Denoising2.size()/hitsTrigger1.size());                                    
            
            LinkedHashMap<Integer, List<Hit>> map_clusterID_hitList_trigger1Denoising2 = new LinkedHashMap();
            for(Hit hit1 : hitsTrigger1Denoising2){
                boolean flag = false;
                for(int clusterID : map_clusterID_hitList_trigger1Denoising2.keySet()){
                    if(hit1.ClusterID() == clusterID){
                        map_clusterID_hitList_trigger1Denoising2.get(clusterID).add(hit1);
                        flag = true;
                        break;
                    }
                }
                if(!flag) {
                    List<Hit> newHitList = new ArrayList();
                    newHitList.add(hit1);
                    map_clusterID_hitList_trigger1Denoising2.put(hit1.ClusterID(), newHitList);
                }                
            }
            for(int clusterID : map_clusterID_hitList_trigger1Denoising2.keySet()){
                histoTriggerExtra1Group.getH1F("hitsInClustersDenoisingSample2").fill(map_clusterID_hitList_trigger1Denoising2.get(clusterID).size());
            }                        
            
            // Check clustering
            List<Hit> hitsTrigger1Clustering2 = new ArrayList();
            for(Hit hit1 : hitsTrigger1Denoising2){
                boolean flag = false;
                for(Hit hit2 : hitsClustering2){
                    if (hit1.sector() == hit2.sector() && hit1.superlayer() == hit2.superlayer()
                            && hit1.layer() == hit2.layer() && hit1.wire() == hit2.wire() && hit1.TDC() == hit2.TDC()) {
                        flag = true;
                        break;
                    }
                }
                if(flag) {
                    hitsTrigger1Clustering2.add(hit1);
                }
            }
            histoTriggerExtra1Group.getH1F("hitLostRatioClusteringSample2").fill((double)(hitsTrigger1Denoising2.size() - hitsTrigger1Clustering2.size())/hitsTrigger1.size());
            histoTriggerExtra1Group.getH1F("hitRemainRatioClusteringSample2").fill((double)hitsTrigger1Clustering2.size()/hitsTrigger1.size());
            
            LinkedHashMap<Integer, List<Hit>> map_clusterID_hitList_trigger1Clustering2 = new LinkedHashMap();
            for(Hit hit1 : hitsTrigger1Clustering2){
                boolean flag = false;
                for(int clusterID : map_clusterID_hitList_trigger1Clustering2.keySet()){
                    if(hit1.ClusterID() == clusterID){
                        map_clusterID_hitList_trigger1Clustering2.get(clusterID).add(hit1);
                        flag = true;
                        break;
                    }
                }
                if(!flag) {
                    List<Hit> newHitList = new ArrayList();
                    newHitList.add(hit1);
                    map_clusterID_hitList_trigger1Clustering2.put(hit1.ClusterID(), newHitList);
                }                
            }
            for(int clusterID : map_clusterID_hitList_trigger1Clustering2.keySet()){
                histoTriggerExtra1Group.getH1F("hitsInClustersClusteringSample2").fill(map_clusterID_hitList_trigger1Clustering2.get(clusterID).size());
            }  
            
            
            // Check HB tracking
            List<Hit> hitsTrigger1HBTracking2 = new ArrayList();
            for(Hit hit1 : hitsTrigger1Clustering2){
                boolean flag = false;
                for(Hit hit2 : hitsHB2){
                    if (hit1.sector() == hit2.sector() && hit1.superlayer() == hit2.superlayer()
                            && hit1.layer() == hit2.layer() && hit1.wire() == hit2.wire() && hit1.TDC() == hit2.TDC()) {
                        flag = true;
                        break;
                    }
                }
                if(flag) hitsTrigger1HBTracking2.add(hit1);
            }
            histoTriggerExtra1Group.getH1F("hitLostRatioHBTrackingSample2").fill((double)(hitsTrigger1Clustering2.size() - hitsTrigger1HBTracking2.size())/hitsTrigger1.size());
            histoTriggerExtra1Group.getH1F("hitRemainRatioHBTrackingSample2").fill((double)hitsTrigger1HBTracking2.size()/hitsTrigger1.size());
            
            // Check HB tracking
            List<Hit> hitsTrigger1TBTracking2 = new ArrayList();
            for(Hit hit1 : hitsTrigger1HBTracking2){
                boolean flag = false;
                for(Hit hit2 : hitsHB2){
                    if (hit1.sector() == hit2.sector() && hit1.superlayer() == hit2.superlayer()
                            && hit1.layer() == hit2.layer() && hit1.wire() == hit2.wire() && hit1.TDC() == hit2.TDC()) {
                        flag = true;
                        break;
                    }
                }
                if(flag) hitsTrigger1TBTracking2.add(hit1);
            }
            histoTriggerExtra1Group.getH1F("hitLostRatioTBTrackingSample2").fill((double)(hitsTrigger1HBTracking2.size() - hitsTrigger1TBTracking2.size())/hitsTrigger1.size());
            histoTriggerExtra1Group.getH1F("hitRemainRatioTBTrackingSample2").fill((double)hitsTrigger1TBTracking2.size()/hitsTrigger1.size());
            
        }
        else if(trigger1 == null && trigger2 != null) { // trigger exisits in sample 2, while not in sample 1
            histoTriggerDiffGroup.getH1F("isTriggered2").fill(3);
        }
        else if(trigger1 != null && trigger2 != null){ // trigger exisits in both samples
            histoTriggerDiffGroup.getH1F("isTriggered2").fill(4);
            histoTriggerDiffGroup.getHistoChi2overndfDiff().fill(trigger1.chi2()/trigger1.NDF() - trigger2.chi2()/trigger2.NDF());
            histoTriggerDiffGroup.getHistoPDiff().fill(trigger1.p() - trigger2.p());
            histoTriggerDiffGroup.getHistoThetaDiff().fill(trigger1.theta() - trigger2.theta());
            histoTriggerDiffGroup.getHistoPhiDiff().fill(trigger1.phi() - trigger2.phi());
            histoTriggerDiffGroup.getHistoVxDiff().fill(trigger1.vx() - trigger2.vx());
            histoTriggerDiffGroup.getHistoVyDiff().fill(trigger1.vy() - trigger2.vy());
            histoTriggerDiffGroup.getHistoVzDiff().fill(trigger1.vz() - trigger2.vz());
        }
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        ////// Pos and neg tracks with trigger
        TrackHistoGroup histoPosTracksGroup = (TrackHistoGroup)histoGroupMap.get("posTracks");
        TrackHistoGroup histoNegTracksGroup = (TrackHistoGroup)histoGroupMap.get("negTracks");
        if(trigger1 != null){
            for(Track trk : posTracks1){
                histoPosTracksGroup.getHistoChi2overndf("1").fill(trk.chi2()/trk.NDF());
                histoPosTracksGroup.getHistoP("1").fill(trk.p());
                histoPosTracksGroup.getHistoTheta("1").fill(trk.theta());
                histoPosTracksGroup.getHistoPhi("1").fill(trk.phi());
                histoPosTracksGroup.getHistoVx("1").fill(trk.vx());
                histoPosTracksGroup.getHistoVy("1").fill(trk.vy());
                histoPosTracksGroup.getHistoVz("1").fill(trk.vz());
            }
            
            for(Track trk : negTracks1){
                histoNegTracksGroup.getHistoChi2overndf("1").fill(trk.chi2()/trk.NDF());
                histoNegTracksGroup.getHistoP("1").fill(trk.p());
                histoNegTracksGroup.getHistoTheta("1").fill(trk.theta());
                histoNegTracksGroup.getHistoPhi("1").fill(trk.phi());
                histoNegTracksGroup.getHistoVx("1").fill(trk.vx());
                histoNegTracksGroup.getHistoVy("1").fill(trk.vy());
                histoNegTracksGroup.getHistoVz("1").fill(trk.vz());
            }
        }
        
        if(trigger2 != null){
            for(Track trk : posTracks2){
                histoPosTracksGroup.getHistoChi2overndf("2").fill(trk.chi2()/trk.NDF());
                histoPosTracksGroup.getHistoP("2").fill(trk.p());
                histoPosTracksGroup.getHistoTheta("2").fill(trk.theta());
                histoPosTracksGroup.getHistoPhi("2").fill(trk.phi());
                histoPosTracksGroup.getHistoVx("2").fill(trk.vx());
                histoPosTracksGroup.getHistoVy("2").fill(trk.vy());
                histoPosTracksGroup.getHistoVz("2").fill(trk.vz());
            }
            
            for(Track trk : negTracks2){
                histoNegTracksGroup.getHistoChi2overndf("2").fill(trk.chi2()/trk.NDF());
                histoNegTracksGroup.getHistoP("2").fill(trk.p());
                histoNegTracksGroup.getHistoTheta("2").fill(trk.theta());
                histoNegTracksGroup.getHistoPhi("2").fill(trk.phi());
                histoNegTracksGroup.getHistoVx("2").fill(trk.vx());
                histoNegTracksGroup.getHistoVy("2").fill(trk.vy());
                histoNegTracksGroup.getHistoVz("2").fill(trk.vz());
            }
        }                    
        
    }
                        
    public static void main(String[] args){
        OptionParser parser = new OptionParser("studyBgEffectsDC");
        parser.setRequiresInputList(false);
        // valid options for event-base analysis
        parser.addOption("-o"          ,"",     "output file name prefix");
        parser.addOption("-n"          ,"-1",   "maximum number of events to process");
        parser.addOption("-energy"     ,"10.6", "beam energy");
        parser.addOption("-target"     ,"2212", "target PDG");
        parser.addOption("-trkType"      ,"22",   "tracking type: ConvTB(12) or AITB(22)");
        parser.addOption("-edge"       ,"",     "colon-separated DC, FTOF, ECAL edge cuts in cm (e.g. 5:10:5)");
        
        parser.addOption("-plot"       ,"1",    "display histograms (0/1)");
        
        // histogram based analysis
        parser.addOption("-histo"      ,"0",    "read histogram file (0/1)");
        
        parser.parse(args);
        
        String namePrefix  = parser.getOption("-o").stringValue(); 
        int   maxEvents  = parser.getOption("-n").intValue();
        Constants.BEAMENERGY = parser.getOption("-energy").doubleValue();
        Constants.TARGETPID  = parser.getOption("-target").intValue();  
        int trkType = parser.getOption("-trkType").intValue(); 
        String[] edge  = parser.getOption("-edge").stringValue().split(":");
        if(!parser.getOption("-edge").stringValue().isBlank() && edge.length != 3) {
            System.out.println("\n >>>> error: incorrect edge parameters...\n");
            System.exit(0);
        }
        else if(edge.length==3) {
            Constants.EDGE[0] = Double.parseDouble(edge[0]);
            Constants.EDGE[1] = Double.parseDouble(edge[1]);
            Constants.EDGE[2] = Double.parseDouble(edge[2]);
        }        
        boolean openWindow   = (parser.getOption("-plot").intValue()!=0);
        boolean readHistos   = (parser.getOption("-histo").intValue()!=0);   
        
        List<String> inputList = parser.getInputList();
        if(inputList.isEmpty()==true){
            parser.printUsage();
            inputList.add("/Users/caot/research/clas12/data/mc/uRWELL/upgradeTrackingWithuRWELL/rga-sidis-uRWell-2R_denoise/0nA/reconBg/0000.hipo");
            inputList.add("/Users/caot/research/clas12/data/mc/uRWELL/upgradeTrackingWithuRWELL/rga-sidis-uRWell-2R_denoise/50nA/reconBg/0000.hipo");
            maxEvents = 1000;
            //System.out.println("\n >>>> error: no input file is specified....\n");
            //System.exit(0);
        }

        String histoName   = "histo.hipo"; 
        if(!namePrefix.isEmpty()) {
            histoName  = namePrefix + "_" + histoName;
        }
        
        StudyBgEffectsDC analysis = new StudyBgEffectsDC();
        analysis.createHistoGroupMap();

        if(!readHistos) {                 
            HipoReader reader1 = new HipoReader();
            reader1.open(inputList.get(0));        
            HipoReader reader2 = new HipoReader();
            reader2.open(inputList.get(1));


            SchemaFactory schema1 = reader1.getSchemaFactory();
            SchemaFactory schema2 = reader2.getSchemaFactory();
            analysis.initReader(new Banks(schema1), new Banks(schema2));

            int counter=0;
            Event event1 = new Event();
            Event event2 = new Event();
        
            ProgressPrintout progress = new ProgressPrintout();
            while (reader1.hasNext() && reader2.hasNext()) {

                counter++;

                reader1.nextEvent(event1);
                reader2.nextEvent(event2);
                
                analysis.processEvent(event1, event2, trkType);

                progress.updateStatus();
                if(maxEvents>0){
                    if(counter>=maxEvents) break;
                }                    
            }

            progress.showStatus();
            reader1.close();            
            reader2.close();
            analysis.saveHistos(histoName);
        }
        else{
            analysis.readHistos(inputList.get(0)); 
        }
        
        if(openWindow) {
            JFrame frame = new JFrame();
            EmbeddedCanvasTabbed canvas = analysis.plotHistos();
            frame.setSize(1200, 750);
            frame.add(canvas);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        }
    }
    
}
