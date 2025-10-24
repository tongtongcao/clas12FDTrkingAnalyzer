package org.clas.reader;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import org.jlab.jnp.hipo4.data.Bank;
import org.jlab.jnp.hipo4.data.Event;

import org.clas.element.RunConfig;
import org.clas.element.MCParticle;
import org.clas.element.MCTrue;
import org.clas.element.MCTrueHit;
import org.clas.element.TDC;
import org.clas.element.Hit;
import org.clas.element.Cluster;
import org.clas.element.Cross;
import org.clas.element.Track;
import org.clas.element.AICandidate;
import org.clas.element.RecEvent;
import org.clas.element.URWellADC;
import org.clas.element.URWellHit;
import org.clas.element.URWellCluster;
import org.clas.element.URWellCross;
import org.clas.utilities.Constants;

/**
 *
 * @author Tongtong
 */
public class LocalEvent {
    private Reader reader;
    private Event event;
    private int trkType = 0;
    private boolean readURWell = false;
    
    
    private RunConfig runConfig = null;
    private List<MCParticle> mcParticles = new ArrayList();
    private MCTrue mcTrue = null;
    private List<TDC> tdcs = new ArrayList();
    private List<Hit> hits = new ArrayList(); //Hits in bank HitBasedTrkg::Hits, which are fetched after denoising and input into clustering
    private List<Cluster> clusters = new ArrayList();
    private List<Hit> hitsClustering = new ArrayList();
    private List<Hit> hitsAICands = new ArrayList();
    private List<Cluster> clustersAICands = new ArrayList();
    private List<AICandidate> aiCands = new ArrayList();   
    private List<Hit> hitsAllHB = new ArrayList();
    private List<Cluster> clustersAllHB = new ArrayList();
    private List<Cross> crossesAllHB = new ArrayList();
    private List<Hit> hitsHB = new ArrayList();
    private List<Cluster> clustersHB = new ArrayList();
    private List<Cross> crossesHB = new ArrayList();
    private List<Track> tracksHB = new ArrayList();
    private List<Hit> hitsAllTB = new ArrayList();
    private List<Cluster> clustersAllTB = new ArrayList();
    private List<Cross> crossesAllTB = new ArrayList();    
    private List<Hit> hitsTB = new ArrayList();
    private List<Cluster> clustersTB = new ArrayList();
    private List<Cross> crossesTB = new ArrayList();
    private List<Track> tracksTB = new ArrayList();   
    private List<URWellHit> uRWellHits = new ArrayList();
    private List<URWellCluster> uRWellClusters = new ArrayList();
    private List<URWellCross> uRWellCrosses = new ArrayList();
    private List<URWellCross> uRWellCrossesNoCuts = new ArrayList();
    private RecEvent recHBEvent = null;
    private RecEvent recTBEvent = null;
    
    public LocalEvent(Reader reader, Event event){
        this.reader = reader;
        this.event = event;
        readBanks();
    }
    
    public LocalEvent(Reader reader, Event event, int trkType){
        this.reader = reader;
        this.event = event;
        this.trkType = trkType;
        readBanks();
    }
    
    public LocalEvent(Reader reader, Event event, boolean readURWell){
        this.reader = reader;
        this.event = event;
        this.readURWell = readURWell;
        readBanks();
    }
    
    public LocalEvent(Reader reader, Event event, int trkType, boolean readURWell){
        this.reader = reader;
        this.event = event;
        this.trkType = trkType;
        this.readURWell = readURWell;
        readBanks();
    }
    
    private void readBanks(){
        //// Read banks        
        // Config
        runConfig = reader.readRunConfig(event);
        
        mcParticles = reader.readMCParticles(event);  
        mcTrue = reader.readMCTrue(event);
        
        if(readURWell){
            List<URWellHit> allURWellHits = reader.readURWellHits(event);
            List<URWellCluster> allURWellClusters = reader.readURWellClusters(event);
            List<URWellCross> allURWellCrosses = reader.readURWellCrosses(event);
            if(Constants.URWELLRegions == 1){
                for(URWellHit hit : allURWellHits){
                    if(hit.layer() == 1 || hit.layer() == 2) uRWellHits.add(hit);
                }
                for(URWellCluster cls : allURWellClusters){
                    if(cls.layer() == 1 || cls.layer() == 2) {
                        cls.setHits(uRWellHits);
                        uRWellClusters.add(cls);
                    }
                }
                for(URWellCross crs : allURWellCrosses){
                    if(crs.region() == 1) {
                        uRWellCrossesNoCuts.add(crs);
                        if(crs.status() == 0) uRWellCrosses.add(crs);
                    } // time and energy cuts for status as 0
                }
            }
            else if(Constants.URWELLRegions == 2){
                for(URWellHit hit : allURWellHits){
                    if(hit.layer() == 3 || hit.layer() == 4) uRWellHits.add(hit);
                }
                for(URWellCluster cls : allURWellClusters){
                    if(cls.layer() == 3 || cls.layer() == 4) {
                        cls.setHits(uRWellHits);
                        uRWellClusters.add(cls);
                    }
                }
                for(URWellCross crs : allURWellCrosses){
                    if(crs.region() == Constants.URWELLRegions) {
                        uRWellCrossesNoCuts.add(crs);
                        if(crs.status() == 0) uRWellCrosses.add(crs);
                    } // time and energy cuts for status as 0
                }
            }
            else{
                uRWellHits = allURWellHits;
                uRWellClusters = allURWellClusters;
                for(URWellCluster cls : allURWellClusters){
                    cls.setHits(allURWellHits);
                }                
                for(URWellCross crs : allURWellCrosses){
                    uRWellCrossesNoCuts.add(crs);
                    if(crs.status() == 0) uRWellCrosses.add(crs); // time and energy cuts for status as 0
                }
            }
        }
        
        for(URWellCluster cls : uRWellClusters){
            List<Integer> stripOrders = new ArrayList(); // could be multiple hits in a main strip
            for(URWellHit hit :  uRWellHits){
                if(cls.strip() == hit.strip()) stripOrders.add(hit.order());
            }
             cls.setStripOrder(stripOrders);
        }
        
        for(URWellCross crs : uRWellCrosses){
            for(URWellCluster cls : uRWellClusters){
                if(crs.cluster1Id() == cls.id()) crs.setCluster1(cls);
                if(crs.cluster2Id() == cls.id()) crs.setCluster2(cls);
                if(crs.getCluster1() != null && crs.getCluster2() != null) break;
            }
        }
        
        for(URWellCross crs : uRWellCrossesNoCuts){
            for(URWellCluster cls : uRWellClusters){
                if(crs.cluster1Id() == cls.id()) crs.setCluster1(cls);
                if(crs.cluster2Id() == cls.id()) crs.setCluster2(cls);
                if(crs.getCluster1() != null && crs.getCluster2() != null) break;
            }
        }
                
        // TDC
        tdcs = reader.readTDCs(event);       
              
        // Hits and clusters from clustring
        hits = reader.readHits(event, 0);
        clusters = reader.readClusters(event, hits, 0);       
        hitsClustering = new ArrayList();
        for(Cluster cls : clusters){
            hitsClustering.addAll(cls.getHits());
            List<URWellCross> matchedURWellCrosses = new ArrayList();             
            for (URWellCross uRWellCross : uRWellCrosses) {
                if (uRWellCross.id() == cls.getMatchedURWellCrossIds()[0]) {
                    matchedURWellCrosses.add(uRWellCross);
                    uRWellCross.setIsUsedClustering(true);
                    break;
                }
            }
            for (URWellCross uRWellCross : uRWellCrosses) {
                if (uRWellCross.id() == cls.getMatchedURWellCrossIds()[1]) {
                    matchedURWellCrosses.add(uRWellCross);
                    uRWellCross.setIsUsedClustering(true);
                    break;
                }
            }            
            cls.setMatchedURWellCrosses(matchedURWellCrosses);            
        }                      
        
        // Clusters in AI candidates
        if(trkType == Constants.AIHB || trkType == Constants.AITB){
            aiCands = reader.readAICandidates(event);
            for(AICandidate cand : aiCands) {
                cand.setHitsClusters(clusters);
                if(!cand.getClusters().isEmpty()) cand.setSector(cand.getClusters().get(0).sector()); // sector is not correct in bank ai::tracks, so need to be set by a cluster
                clustersAICands.addAll(cand.getClusters());
                for(Cluster cls : cand.getClusters()){
                    hitsAICands.addAll(cls.getHits());
                }
            }                       
        }
        
        if(trkType == Constants.CONVHB || trkType == Constants.AIHB){
            // Clusters in HB tracks
            hitsAllHB = reader.readHits(event, trkType);
            clustersAllHB = reader.readClusters(event, hitsAllHB, trkType);
            
            crossesAllHB = reader.readCrosses(event, trkType);
            for(Cross crs : crossesAllHB){
                for(Cluster cls : clustersAllHB){
                    if(cls.id() == crs.cluster1Id()) crs.setCluster1(cls);
                    if(cls.id() == crs.cluster2Id()) crs.setCluster2(cls);
                    if(crs.getCluster1() != null && crs.getCluster2() != null) break;
                }
            }
            
            
            tracksHB = reader.readTracks(event, trkType);                  
            for(Track trk : tracksHB) {
                trk.setCrosses(crossesAllHB);
                for(Cross crs : trk.getCrosses()){
                    if(!crossesHB.contains(crs)) crossesHB.add(crs);                    
                }
                trk.setHitsClusters(clustersAllHB);
                for(Cluster cls : trk.getClusters()){
                    if(!clustersHB.contains(cls)) clustersHB.add(cls);                    
                    for(Hit hit : cls.getHits()){
                        if(!hitsHB.contains(hit)) hitsHB.add(hit);
                    }
                }
            }
            
            recHBEvent = reader.readRecEvent(event, trkType);
            
            if(readURWell){
                for(Track trk : tracksHB){
                    List<URWellCross> matchedURWellCrosses = new ArrayList(); 
                    for(URWellCross uRWellCross : uRWellCrosses){
                        if(uRWellCross.id() == trk.uRWellCrossIds()[0]){
                            matchedURWellCrosses.add(uRWellCross);
                            uRWellCross.setIsUsedHB(true);
                            break;
                        }
                    }
                    for(URWellCross uRWellCross : uRWellCrosses){
                        if(uRWellCross.id() == trk.uRWellCrossIds()[1]){
                            matchedURWellCrosses.add(uRWellCross);
                            uRWellCross.setIsUsedHB(true);
                            break;
                        }
                    }                                        
                    trk.setURWellCrosses(matchedURWellCrosses);
                }
            }
        }
        else if(trkType == Constants.CONVTB || trkType == Constants.AITB){
            // Clusters in HB tracks
            hitsAllHB = reader.readHits(event, trkType-1);
            clustersAllHB = reader.readClusters(event, hitsAllHB, trkType-1);  
            
            crossesAllHB = reader.readCrosses(event, trkType - 1);
            for(Cross crs : crossesAllHB){
                for(Cluster cls : clustersAllHB){
                    if(cls.id() == crs.cluster1Id()) crs.setCluster1(cls);
                    if(cls.id() == crs.cluster2Id()) crs.setCluster2(cls);
                    if(crs.getCluster1() != null && crs.getCluster2() != null) break;
                }
            }
            
            tracksHB = reader.readTracks(event, trkType-1);                  
            for(Track trk : tracksHB) {
                trk.setCrosses(crossesAllHB);
                for(Cross crs : trk.getCrosses()){
                    if(!crossesHB.contains(crs)) crossesHB.add(crs);                    
                }
                trk.setHitsClusters(clustersAllHB);
                for(Cluster cls : trk.getClusters()){
                    if(!clustersHB.contains(cls)) clustersHB.add(cls);                    
                    for(Hit hit : cls.getHits()){
                        if(!hitsHB.contains(hit)) hitsHB.add(hit);
                    }
                }
            }
            
            recHBEvent = reader.readRecEvent(event, trkType-1);

            // Clusters in TB tracks
            hitsAllTB = reader.readHits(event, trkType);
            clustersAllTB = reader.readClusters(event, hitsAllTB, trkType);   
            
            crossesAllTB = reader.readCrosses(event, trkType);
            for(Cross crs : crossesAllTB){
                for(Cluster cls : clustersAllTB){
                    if(cls.id() == crs.cluster1Id()) crs.setCluster1(cls);
                    if(cls.id() == crs.cluster2Id()) crs.setCluster2(cls);
                    if(crs.getCluster1() != null && crs.getCluster2() != null) break;
                }
            }
            
            tracksTB = reader.readTracks(event, trkType);                 
            for(Track trk : tracksTB) {
                trk.setCrosses(crossesAllTB);
                for(Cross crs : trk.getCrosses()){
                    if(!crossesTB.contains(crs)) crossesTB.add(crs);                    
                }
                trk.setHitsClusters(clustersAllTB);                
                for(Cluster cls : trk.getClusters()){
                    if(!clustersTB.contains(cls)) clustersTB.add(cls);                    
                    for(Hit hit : cls.getHits()){
                        if(!hitsTB.contains(hit)) hitsTB.add(hit);
                    }
                }
            }
            
            recTBEvent = reader.readRecEvent(event, trkType);
           
            if(readURWell){
                for(Track trk : tracksHB){
                    List<URWellCross> matchedURWellCrosses = new ArrayList(); 
                    for(URWellCross uRWellCross : uRWellCrosses){
                        if(uRWellCross.id() == trk.uRWellCrossIds()[0]){
                            matchedURWellCrosses.add(uRWellCross);
                            uRWellCross.setIsUsedHB(true);
                            break;
                        }
                    }
                    for(URWellCross uRWellCross : uRWellCrosses){
                        if(uRWellCross.id() == trk.uRWellCrossIds()[1]){
                            matchedURWellCrosses.add(uRWellCross);
                            uRWellCross.setIsUsedHB(true);
                            break;
                        }
                    }                                        
                    trk.setURWellCrosses(matchedURWellCrosses);
                }
                
                for(Track trk : tracksTB){
                    List<URWellCross> matchedURWellCrosses = new ArrayList(); 
                    for(URWellCross uRWellCross : uRWellCrosses){
                        if(uRWellCross.id() == trk.uRWellCrossIds()[0]){
                            matchedURWellCrosses.add(uRWellCross);
                            uRWellCross.setIsUsedTB(true);
                            break;
                        }
                    }
                    for(URWellCross uRWellCross : uRWellCrosses){
                        if(uRWellCross.id() == trk.uRWellCrossIds()[1]){
                            matchedURWellCrosses.add(uRWellCross);
                            uRWellCross.setIsUsedTB(true);
                            break;
                        }
                    }                                        
                    trk.setURWellCrosses(matchedURWellCrosses);
                }
            }
        }
    }
    
    public RunConfig getRunConfig(){
        if(runConfig != null) return runConfig;
        else{
            System.err.println("Error: run config is empty!");
            return null;
        }
    }
    
    public List<MCParticle> getMCParticles(){
        return mcParticles;
    }

    public MCTrue getMCTrue(){
        return mcTrue;
    }    
    
    public List<URWellHit> getURWellHits(){
        return uRWellHits;
    }
    
    public List<URWellCluster> getURWellClusters(){
        return uRWellClusters;
    }
        
    public List<URWellCross> getURWellCrosses(){
        return uRWellCrosses;
    }  
    
    public List<URWellCross> getURWellCrossesNoCuts(){
        return uRWellCrossesNoCuts;
    } 
        
    public List<TDC> getTDCs(){
        return tdcs;
    }
    
    public List<Hit> getHits(){
        return hits;
    }
    
    public List<Cluster> getClusters(){
        return clusters;
    }
    
    public List<Cluster> getClustersInSector(int sector){
        List<Cluster> clustersInSector = new ArrayList();
        for(Cluster cls : clusters){
            if(cls.sector() == sector) clustersInSector.add(cls);
        }
        return clustersInSector;
    }
    
    public List<Hit> getHitsClustering(){
        return hitsClustering;
    }
    
    public List<Hit> getHitsAICands(){
        return hitsAICands;
    }
    
    public List<Cluster> getClustersAICands(){
        return clustersAICands;
    }
    
    public List<AICandidate> getAICands(){
        return aiCands;
    }
    
    public List<Hit> getHitsAllHB(){
        return hitsAllHB;
    }
    
    public List<Cluster> getClustersAllHB(){
        return clustersAllHB;
    }  
    
    public List<Cross> getCrossesAllHB(){
        return crossesAllHB;
    }     
    
    public List<Hit> getHitsHB(){
        return hitsHB;
    }
    
    public List<Cluster> getClustersHB(){
        return clustersHB;
    }
    
    public List<Cross> getCrossesHB(){
        return crossesHB;
    }    
    
    public List<Track> getTracksHB(){
        return tracksHB;
    }
    
    public List<Hit> getHitsAllTB(){
        return hitsAllTB;
    }
    
    public List<Cluster> getClustersAllTB(){
        return clustersAllTB;
    } 
    
    public List<Cross> getCrossesAllTB(){
        return crossesAllTB;
    } 
    
    public List<Hit> getHitsTB(){
        return hitsTB;
    }
    
    public List<Cluster> getClustersTB(){
        return clustersTB;
    }
    
    public List<Cross> getCrossesTB(){
        return crossesTB;
    }    
    
    public List<Track> getTracksTB(){
        return tracksTB;
    }
        
    public void separateNormalBgTDCs(List<TDC> allTDCs, List<TDC> normalTDCs, List<TDC> bgTDCs){
        for(TDC tdc : allTDCs){
            if (tdc.isNormalHit()) {
                normalTDCs.add(tdc);
            } else {
                bgTDCs.add(tdc);
            } 
        }
    }
    
    public void separateNormalBgHits(List<Hit> allHits, List<Hit> normalHits, List<Hit> bgHits){
        for(Hit hit : allHits){
            if (hit.isNormalHit()) {
                normalHits.add(hit);
            } else {
                bgHits.add(hit);
            } 
        }
    }
    
    public RecEvent getRecHBEvent(){
        return recHBEvent;
    }
    
    public RecEvent getRecTBEvent(){
        return recTBEvent;
    }
    
    
} 