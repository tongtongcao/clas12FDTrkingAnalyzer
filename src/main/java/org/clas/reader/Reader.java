package org.clas.reader;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

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
import org.clas.fiducials.Fiducial;
import org.clas.utilities.Constants;

/**
 *
 * @author Tongtong
 */
public class Reader {    
    Banks banks = null;
    
    public Reader(Banks banks) {
        this.banks = banks;
    }
    
    private static final Logger LOGGER = Logger.getLogger(Reader.class.getName());    
    
    public RunConfig readRunConfig(Event event) {
        Bank runConfigBank = banks.getRunConfig();
        if(runConfigBank != null){
            event.read(runConfigBank);           
            RunConfig runconfig = new RunConfig(
                    runConfigBank.getInt("run", 0),
                    runConfigBank.getInt("event", 0),
                    runConfigBank.getInt("unixtime", 0),
                    runConfigBank.getLong("trigger", 0),
                    runConfigBank.getLong("timestamp", 0),
                    runConfigBank.getInt("type", 0),
                    runConfigBank.getInt("mode", 0),
                    runConfigBank.getFloat("torus", 0),
                    runConfigBank.getFloat("solenoid", 0)
            );                            
            return runconfig;
        }               
        else return null;   
    }
    
    //ConvHB(11) or ConvTB(12) or AIHB(21) or AITB(22)
    public RecEvent readRecEvent(Event event, int type) {
        Bank recEventBank = banks.getRecEventBank(type);
        
        RecEvent recEvent = null;
        
        if(recEventBank != null){
            event.read(recEventBank);
            if(recEventBank.getRows() == 1){
                recEvent = new RecEvent(type,
                                recEventBank.getFloat("startTime", 0),
                                  recEventBank.getFloat("RFTime", 0));
            }
        }
                   
        
        return recEvent;
    }    
    
    public List<Track> readTracks(Event event) {
        return readTracks(event, Constants.AITB);
    }
    
    // ConvHB(11) or ConvTB(12) or AIHB(21) or AITB(22)
    public List<Track> readTracks(Event event, int type) {
        
        List<Track> tracks = new ArrayList();
        Bank runConfig      = banks.getRunConfig();
        Bank particleBank   = banks.getRecParticleBank(type);
        Bank trajectoryBank = banks.getRecTrajectoryBank(type);
        Bank trackBank      = banks.getRecTrackBank(type);
        Bank trackingBank   = banks.getTrackingBank(type);
        if(runConfig!=null)      event.read(runConfig);
        if(particleBank!=null)   event.read(particleBank);
        if(trajectoryBank!=null) event.read(trajectoryBank);
        if(trackBank!=null)      event.read(trackBank);
        if(trackingBank!=null)   event.read(trackingBank);

        double torusScale = -1;
        if(runConfig!=null && runConfig.getRows()>0) torusScale = runConfig.getFloat("torus",0);
        
        // use tracking back if available 
        if(trackingBank!=null && trackingBank.getRows()>0) {
            // create tracks list from track bank
            for(int it = 0; it < trackingBank.getRows(); it++){
                Track track = new Track(type,
                                        trackingBank.getInt("id", it),
                                        trackingBank.getByte("q", it),
                                        trackingBank.getFloat("p0_x", it),
                                        trackingBank.getFloat("p0_y", it),
                                        trackingBank.getFloat("p0_z", it),
                                        trackingBank.getFloat("Vtx0_x", it),
                                        trackingBank.getFloat("Vtx0_y", it),
                                        trackingBank.getFloat("Vtx0_z", it));
                track.index(it);
                track.sector(trackingBank.getByte("sector", it));
                track.NDF(trackingBank.getShort("ndf", it));
                track.chi2(trackingBank.getFloat("chi2", it));
                for(int i=0; i<2; i++) {
                    track.cross(trackingBank.getFloat("c" + (i*2+1) + "_x", it),
                                trackingBank.getFloat("c" + (i*2+1) + "_y", it),
                                trackingBank.getFloat("c" + (i*2+1) + "_z", it),
                                (i*2+1));
                }

                track.clusters(trackingBank.getShort("Cluster1_ID", it),
                               trackingBank.getShort("Cluster2_ID", it),
                               trackingBank.getShort("Cluster3_ID", it),
                               trackingBank.getShort("Cluster4_ID", it),
                               trackingBank.getShort("Cluster5_ID", it),
                               trackingBank.getShort("Cluster6_ID", it));
                
                track.crosses(trackingBank.getShort("Cross1_ID", it),
                              trackingBank.getShort("Cross2_ID", it),
                              trackingBank.getShort("Cross3_ID", it));
                
                try{
                    track.uRWellCrossIds(trackingBank.getInt("URWellCross1_ID", it), trackingBank.getInt("URWellCross2_ID", it));
                    track.setURWellProjectionR1(trackingBank.getFloat("URWell_R1_x", it),
                                              trackingBank.getFloat("URWell_R1_y", it),
                                              trackingBank.getFloat("URWell_R1_z", it));
                    track.setURWellProjectionR2(trackingBank.getFloat("URWell_R2_x", it),
                                              trackingBank.getFloat("URWell_R2_y", it),
                                              trackingBank.getFloat("URWell_R2_z", it));
                    
                    
                }
                catch(Exception e){
                    LOGGER.log(Level.FINER, "no items for URWell in track bank!");
                }
                track.polarity(torusScale);
                tracks.add(track);
            }
            // add information from particle bank
            if(trackBank!=null && particleBank!=null) {
                for(int loop = 0; loop < trackBank.getRows(); loop++){
                    int pindex = trackBank.getShort("pindex", loop);
                    int status = particleBank.getShort("status", pindex);
                    // Forward Detector only
                    if(((int) Math.abs(status)/1000)==2) { 
                        int index = trackBank.getShort("index", loop);
                        Track track  = tracks.get(index); 
                        track.status(status);
                        track.pid(particleBank.getInt("pid", pindex));                
                        track.chi2pid(particleBank.getFloat("chi2pid", pindex));                
                    }
                }
            }
        }
        else if(trackBank!=null && particleBank!=null) {
            for(int it = 0; it < trackBank.getRows(); it++){
                int pindex = trackBank.getShort("pindex", it);
                int status = particleBank.getShort("status", pindex);
                // Forward Detector only
                if(((int) Math.abs(status)/1000)==2) { 
                    int index = trackBank.getShort("index", it);
                    Track track = new Track(type,
                                        -1,
                                        particleBank.getByte("charge", pindex),
                                        particleBank.getFloat("px", pindex),
                                        particleBank.getFloat("py", pindex),
                                        particleBank.getFloat("pz", pindex),
                                        particleBank.getFloat("vx", pindex),
                                        particleBank.getFloat("vy", pindex),
                                        particleBank.getFloat("vz", pindex));
                    track.index(index);
                    track.sector(trackBank.getByte("sector", it));
                    track.NDF(trackBank.getShort("NDF", it));
                    track.chi2(trackBank.getFloat("chi2", it)/trackBank.getShort("NDF", it));
                    track.status(status);
                    track.pid(particleBank.getInt("pid", pindex));                
                    track.chi2pid(particleBank.getFloat("chi2pid", pindex));    
                    track.polarity(torusScale);
                    tracks.add(track);
                }
            }
            Collections.sort(tracks);
        }
        if(tracks!=null) {
            // add information from trajectory bank
            if(trajectoryBank!=null) {
                for(int loop = 0; loop < trajectoryBank.getRows(); loop++){
                    int pindex = trajectoryBank.getShort("pindex", loop);
                    int status = particleBank.getShort("status", pindex);
                    // Forward Detector only
                    if(((int) Math.abs(status)/1000)==2) { 
                        int index = trajectoryBank.getShort("index", loop);
                        Track track  = tracks.get(index); 
                        if(trajectoryBank.getSchema().hasEntry("edge"))
                            track.trajectory(trajectoryBank.getFloat("x", loop),
                                             trajectoryBank.getFloat("y", loop),
                                             trajectoryBank.getFloat("z", loop),
                                             trajectoryBank.getFloat("edge", loop),
                                             trajectoryBank.getByte("detector", loop),
                                             trajectoryBank.getByte("layer", loop));
                        else
                            track.trajectory(trajectoryBank.getFloat("x", loop),
                                             trajectoryBank.getFloat("y", loop),
                                             trajectoryBank.getFloat("z", loop),
                                             trajectoryBank.getByte("detector", loop),
                                             trajectoryBank.getByte("layer", loop));                            
                    }
                }
            }
        }
        
        // add fiducial information and selection on number of superlayers
        Fiducial fiducial = new Fiducial();
        for(Track tr : tracks) {
            tr.isInFiducial(fiducial.inFiducial(tr));
        }
        
        return tracks;
    }
    
    public List<Cross> readCrosses(Event event) {
        return readCrosses(event, Constants.AITB);
    }
    
    
    //ConvHB(11) or ConvTB(12) or AIHB(21) or AITB(22)
    public List<Cross> readCrosses(Event event, int type) {
        Bank crossBank = banks.getTrackingCrossBank(type);
        
        ArrayList<Cross> crosses = new ArrayList();
        
        if(crossBank != null){
            event.read(crossBank);
            
            for(int loop = 0; loop < crossBank.getRows(); loop++){

                Cross crs = new Cross(type, 
                        crossBank.getInt("id", loop),
                        crossBank.getInt("status", loop),
                        crossBank.getInt("sector", loop),
                        crossBank.getInt("region", loop),                        
                        crossBank.getFloat("x", loop),
                        crossBank.getFloat("y", loop),
                        crossBank.getFloat("z", loop),
                        crossBank.getFloat("err_x", loop),
                        crossBank.getFloat("err_y", loop),
                        crossBank.getFloat("err_z", loop),
                        crossBank.getFloat("ux", loop),
                        crossBank.getFloat("uy", loop),
                        crossBank.getFloat("uz", loop),
                        crossBank.getFloat("err_ux", loop),
                        crossBank.getFloat("err_uy", loop),
                        crossBank.getFloat("err_ux", loop), 
                        crossBank.getInt("Segment1_ID", loop), 
                        crossBank.getInt("Segment2_ID", loop)                        
                        );
                               
                crosses.add(crs); // AIHB pseudo cluster is saved
            }
        }        
        
        return crosses;
    }
      
    public List<Cluster> readClusters(Event event) {
        return readClusters(event, Constants.AITB);
    }
    
    //ConvHB(11) or ConvTB(12) or AIHB(21) or AITB(22) or Cluster from clusterring(others)
    public List<Cluster> readClusters(Event event, int type) {
        Bank clusterBank = null;
        if(type!=Constants.CONVHB && type!=Constants.CONVTB && type!=Constants.AIHB && type!=Constants.AITB){
            clusterBank = banks.getDCClusterBank();
        }
        else {
            clusterBank = banks.getTrackingClusterBank(type);           
        }
        

        Bank uRWellDCClusterBank = banks.getURWellDCClusterBank();
        Map<Integer, List<Integer>> map_clsId_uRWellCrossIds = new HashMap();
        if(uRWellDCClusterBank != null){
            event.read(uRWellDCClusterBank);            
            for(int loop = 0; loop < uRWellDCClusterBank.getRows(); loop++){
                int clsId = uRWellDCClusterBank.getInt("id", loop);
                int uRWellCross1Id = uRWellDCClusterBank.getInt("URWell_Cross1_ID", loop);
                int uRWellCross2Id = uRWellDCClusterBank.getInt("URWell_Cross2_ID", loop);
                List<Integer> uRWellCrossIds = new ArrayList();
                uRWellCrossIds.add(uRWellCross1Id);
                uRWellCrossIds.add(uRWellCross2Id);
                map_clsId_uRWellCrossIds.put(clsId, uRWellCrossIds);
            }
        }
        
        ArrayList<Cluster> clusters = new ArrayList();
        
        if(clusterBank != null){
            event.read(clusterBank);
            
            for(int loop = 0; loop < clusterBank.getRows(); loop++){

                Cluster cls = new Cluster(type, 
                        clusterBank.getInt("id", loop),
                        clusterBank.getInt("status", loop),
                        clusterBank.getInt("sector", loop),
                        clusterBank.getInt("superlayer", loop),
                        clusterBank.getInt("size", loop),
                        clusterBank.getFloat("avgWire", loop),
                        clusterBank.getFloat("fitChisqProb", loop),
                        clusterBank.getFloat("fitSlope", loop),
                        clusterBank.getFloat("fitSlopeErr", loop),
                        clusterBank.getFloat("fitInterc", loop),
                        clusterBank.getFloat("fitIntercErr", loop)
                        );
                cls.separateNormalBgHits();
                
                cls.hitIds(clusterBank.getInt("Hit1_ID", loop),
                               clusterBank.getInt("Hit2_ID", loop),
                               clusterBank.getInt("Hit3_ID", loop),
                               clusterBank.getInt("Hit4_ID", loop),
                               clusterBank.getInt("Hit5_ID", loop),
                               clusterBank.getInt("Hit6_ID", loop),
                               clusterBank.getInt("Hit7_ID", loop),
                               clusterBank.getInt("Hit8_ID", loop),
                               clusterBank.getInt("Hit9_ID", loop),
                               clusterBank.getInt("Hit10_ID", loop),
                               clusterBank.getInt("Hit11_ID", loop),
                               clusterBank.getInt("Hit12_ID", loop));                
                
                if(type!=Constants.CONVHB && type!=Constants.CONVTB && type!=Constants.AIHB && type!=Constants.AITB){
                    if(map_clsId_uRWellCrossIds.keySet().contains(cls.id())) {
                        cls.setMatchedURWellCrossIds(map_clsId_uRWellCrossIds.get(cls.id()).get(0), map_clsId_uRWellCrossIds.get(cls.id()).get(1));
                    }
                }                
                               
                if(cls.superlayer() != 0) clusters.add(cls); // AIHB pseudo cluster is saved
            }
        }        
        
        return clusters;
    }
    
    public List<Cluster> readClusters(Event event, List<Hit> hits){
        return readClusters(event, hits, Constants.AITB);
    }
    
    public List<Cluster> readClusters(Event event, List<Hit> hits, int type) {        
        List<Cluster> clusters = readClusters(event, type);
        for(Cluster cls: clusters) {
            cls.setHits(hits);
            cls.separateNormalBgHits();
        }
        
        return clusters;
    }
    
    public List<Hit> readHits(Event event) {
        return readHits(event, Constants.AITB);
    }
    
    //ConvHB(11) or ConvTB(12) or AIHB(21) or AITB(22) or Cluster from clusterring(others)
    public List<Hit> readHits(Event event, int type) {
        Bank dcTDCBank = banks.getDCTDCBank();
        ArrayList<TDC> tdcs = new ArrayList();
        if(dcTDCBank != null){
            tdcs = readTDCs(event);
        }
        

        List<Integer> idList = new ArrayList();
        List<Integer> tidList = new ArrayList();
        List<Float> tPropList = new ArrayList();
        List<Float> tFlightList = new ArrayList();
        if(type==Constants.CONVHB || type==Constants.AIHB) {
            Bank hitTrkIdBank = banks.getHBTrackingHitTrkIdBank(type);
            event.read(hitTrkIdBank);
            for(int loop = 0; loop < hitTrkIdBank.getRows(); loop++){
                idList.add(hitTrkIdBank.getInt("id", loop));
                tidList.add(hitTrkIdBank.getInt("tid", loop));
                tPropList.add(hitTrkIdBank.getFloat("TProp", loop));
                tFlightList.add(hitTrkIdBank.getFloat("TFlight", loop));                
            }
        }
        
        Bank hitBank = null;
        if(type!=Constants.CONVHB && type!=Constants.CONVTB && type!=Constants.AIHB && type!=Constants.AITB){
            hitBank = banks.getDCHitBank();
        }
        else {
            hitBank = banks.getTrackingHitBank(type);           
        }
        
        ArrayList<Hit> hits = new ArrayList();
        
        if(hitBank != null){
            event.read(hitBank);
            
            for(int loop = 0; loop < hitBank.getRows(); loop++){

                Hit hit = new Hit(type, 
                        hitBank.getInt("id", loop),
                        hitBank.getInt("status", loop),
                        hitBank.getInt("sector", loop),
                        hitBank.getInt("superlayer", loop),
                        hitBank.getInt("layer", loop),
                        hitBank.getInt("wire", loop),
                        hitBank.getInt("TDC", loop),
                        hitBank.getInt("jitter", loop),
                        hitBank.getInt("clusterID", loop),
                        hitBank.getFloat("X", loop),
                        hitBank.getFloat("Z", loop),
                        hitBank.getFloat("trkDoca", loop),
                        hitBank.getFloat("docaError", loop),
                        hitBank.getInt("LR", loop)
                        );
                
                if(type==Constants.CONVTB || type==Constants.AITB){  
                    hit.setTid(hitBank.getInt("trkID", loop));
                    hit.setT0(hitBank.getFloat("T0", loop));
                    hit.setTFlight(hitBank.getFloat("TFlight", loop));
                    hit.setTProp(hitBank.getFloat("TProp", loop));
                    hit.setBeta(hitBank.getFloat("beta", loop));
                    
                    try{
                        hit.dafWeight(hitBank.getFloat("DAFWeight", loop));
                    }              
                    catch(Exception e){
                       LOGGER.log(Level.FINER, "no item DAFWeight in DC hit bank!");
                    }
                }
                
                if(type!=Constants.CONVHB && type!=Constants.CONVTB && type!=Constants.AIHB && type!=Constants.AITB){                 
                    try{
                        hit.indexTDC(hitBank.getInt("indexTDC", loop));
                    }              
                    catch(Exception e){
                        LOGGER.log(Level.FINER, "no item indexTDC in DC hit bank!");
                    }
                }
                for(TDC tdc : tdcs){
                    if(hit.hitMatched(tdc)) {
                        hit.order(tdc.order());  
                        break;
                    }
                }
                
                if((type==Constants.CONVHB || type==Constants.AIHB) && !idList.isEmpty()) { 
                    for(int i = 0; i < idList.size(); i++){
                        if(hit.id() == idList.get(i)){
                            hit.setTid(tidList.get(i));
                            hit.setTProp(tPropList.get(i));
                            hit.setTFlight(tFlightList.get(i));
                            break;
                        }
                    }
                } 

                hits.add(hit);
            }
        }
        
        return hits;
    }  
    
    public ArrayList<TDC> readTDCs(Event event) {
        ArrayList<TDC> tdcs = new ArrayList();
        
        Bank dcTOTBank = banks.getDCTOTBank();
        if(dcTOTBank != null){
            event.read(dcTOTBank);
            
            for(int loop = 0; loop < dcTOTBank.getRows(); loop++){
                TDC tdc = new TDC(
                        dcTOTBank.getInt("sector", loop),
                        dcTOTBank.getInt("layer", loop),
                        dcTOTBank.getInt("component", loop),
                        dcTOTBank.getInt("order", loop),
                        dcTOTBank.getInt("TDC", loop)
                );
                
                tdcs.add(tdc);
            }
        }
        
        if(tdcs.size() > 0) return tdcs;
        
        Bank dcTDCBank = banks.getDCTDCBank();
        if(dcTDCBank != null){
            event.read(dcTDCBank);
            
            for(int loop = 0; loop < dcTDCBank.getRows(); loop++){
                TDC tdc = new TDC(
                        dcTDCBank.getInt("sector", loop),
                        dcTDCBank.getInt("layer", loop),
                        dcTDCBank.getInt("component", loop),
                        dcTDCBank.getInt("order", loop),
                        dcTDCBank.getInt("TDC", loop)
                );
                
                tdcs.add(tdc);
            }
        }         
                
        return tdcs;
    } 
    
    public ArrayList<AICandidate> readAICandidates(Event event) {
        Bank aiCandidateBank = banks.getAICandidateBank();
        ArrayList<AICandidate> cands = new ArrayList();
        
        if(aiCandidateBank != null){
            event.read(aiCandidateBank);
            
            for(int loop = 0; loop < aiCandidateBank.getRows(); loop++){

                AICandidate cand = new AICandidate(
                        aiCandidateBank.getInt("id", loop),
                        aiCandidateBank.getInt("sector", loop),
                        aiCandidateBank.getFloat("prob", loop)
                        );
                
                cand.clusters(aiCandidateBank.getInt("c1", loop),
                               aiCandidateBank.getInt("c2", loop),
                               aiCandidateBank.getInt("c3", loop),
                               aiCandidateBank.getInt("c4", loop),
                               aiCandidateBank.getInt("c5", loop),
                               aiCandidateBank.getInt("c6", loop)
                );
                
                cands.add(cand);
            }
        }
        
        return cands;
    }
    
    public ArrayList<URWellCross> readURWellCrosses(Event event){
        Bank uRWellCrossBank = banks.getURWellCrossBank();
        
        ArrayList<URWellCross> crosses = new ArrayList();
        
        if(uRWellCrossBank != null){
            event.read(uRWellCrossBank);
            
            for(int loop = 0; loop < uRWellCrossBank.getRows(); loop++){

                URWellCross cross = new URWellCross( 
                        uRWellCrossBank.getInt("id", loop),
                        uRWellCrossBank.getInt("status", loop),
                        uRWellCrossBank.getInt("sector", loop),
                        uRWellCrossBank.getInt("region", loop),
                        uRWellCrossBank.getInt("cluster1", loop),
                        uRWellCrossBank.getInt("cluster2", loop),
                        uRWellCrossBank.getFloat("energy", loop),
                        uRWellCrossBank.getFloat("time", loop),
                        uRWellCrossBank.getFloat("x", loop),
                        uRWellCrossBank.getFloat("y", loop),
                        uRWellCrossBank.getFloat("z", loop)
                        );
                              
                crosses.add(cross);
            }
        }                
        
        return crosses;
    }
    
    public ArrayList<URWellCluster> readURWellClusters(Event event){
        Bank uRWellClusterBank = banks.getURWellClusterBank();
        
        ArrayList<URWellCluster> clusters = new ArrayList();
        
        if(uRWellClusterBank != null){
            event.read(uRWellClusterBank);
            
            for(int loop = 0; loop < uRWellClusterBank.getRows(); loop++){

                URWellCluster cluster = new URWellCluster( 
                        uRWellClusterBank.getInt("id", loop),
                        uRWellClusterBank.getInt("status", loop),
                        uRWellClusterBank.getInt("sector", loop),
                        uRWellClusterBank.getInt("layer", loop),
                        uRWellClusterBank.getInt("strip", loop),
                        uRWellClusterBank.getInt("size", loop),
                        uRWellClusterBank.getFloat("energy", loop),
                        uRWellClusterBank.getFloat("time", loop),
                        uRWellClusterBank.getFloat("xo", loop),
                        uRWellClusterBank.getFloat("yo", loop),
                        uRWellClusterBank.getFloat("zo", loop),
                        uRWellClusterBank.getFloat("xe", loop),
                        uRWellClusterBank.getFloat("ye", loop),
                        uRWellClusterBank.getFloat("ze", loop)
                        );
                              
                clusters.add(cluster);
            }
        }
        
        return clusters;
    }
    
    public ArrayList<URWellHit> readURWellHits(Event event){
        Bank uRWellADCBank = banks.getURWellADCBank();
        
        ArrayList<URWellADC> adcs = new ArrayList();
        
        if(uRWellADCBank != null){
            event.read(uRWellADCBank);
            
            for(int loop = 0; loop < uRWellADCBank.getRows(); loop++){

                URWellADC adc = new URWellADC( 
                        uRWellADCBank.getInt("sector", loop),
                        uRWellADCBank.getInt("layer", loop),
                        uRWellADCBank.getInt("component", loop),
                        uRWellADCBank.getInt("order", loop),
                        uRWellADCBank.getInt("ADC", loop),
                        uRWellADCBank.getInt("ped", loop),
                        uRWellADCBank.getFloat("time", loop)
                        );                
                
                adcs.add(adc);
            }
        }        
        
        Bank uRWellHitBank = banks.getURWellHitBank();
        
        ArrayList<URWellHit> hits = new ArrayList();
        
        if(uRWellHitBank != null){
            event.read(uRWellHitBank);
            
            for(int loop = 0; loop < uRWellHitBank.getRows(); loop++){

                URWellHit hit = new URWellHit( 
                        uRWellHitBank.getInt("id", loop),
                        uRWellHitBank.getInt("status", loop),
                        uRWellHitBank.getInt("sector", loop),
                        uRWellHitBank.getInt("layer", loop),
                        uRWellHitBank.getInt("strip", loop),
                        uRWellHitBank.getInt("clusterId", loop),
                        uRWellHitBank.getFloat("energy", loop),
                        uRWellHitBank.getFloat("time", loop)
                        );
                
                hit.adc(adcs.get(hit.id()-1).adc());
                hit.order(adcs.get(hit.id()-1).order());
                hit.ped(adcs.get(hit.id()-1).ped());
                
                hits.add(hit);
            }
        }        
        
        return hits;
    }
    
    
    public ArrayList<MCParticle> readMCParticles(Event event){
        Bank mcParticleBank = banks.getMCParticle();
        
        ArrayList<MCParticle> mcParticles = new ArrayList();
        
        if(mcParticleBank != null){
            event.read(mcParticleBank);
            
            for(int loop = 0; loop < mcParticleBank.getRows(); loop++){

                MCParticle particle = new MCParticle( 
                        mcParticleBank.getInt("pid", loop),
                        mcParticleBank.getFloat("px", loop),
                        mcParticleBank.getFloat("py", loop),
                        mcParticleBank.getFloat("pz", loop),
                        mcParticleBank.getFloat("vx", loop),
                        mcParticleBank.getFloat("vy", loop),
                        mcParticleBank.getFloat("vz", loop),
                        mcParticleBank.getFloat("vt", loop)
                        );
                              
                mcParticles.add(particle);
            }
        }
        
        return mcParticles;
    }
    
    public MCTrue readMCTrue(Event event){
        Bank mcTrueBank = banks.getMCTrue();
        
        List<MCTrueHit> hitsDC = new ArrayList();
        List<MCTrueHit> hitsURWell = new ArrayList();
        
        if(mcTrueBank != null){
            event.read(mcTrueBank);
            
            for(int loop = 0; loop < mcTrueBank.getRows(); loop++){
                int detector = mcTrueBank.getInt("detector", loop);
                MCTrueHit hit = new MCTrueHit(                            
                            mcTrueBank.getInt("pid", loop),
                            detector,
                            mcTrueBank.getInt("hitn", loop),
                            mcTrueBank.getFloat("avgX", loop),
                            mcTrueBank.getFloat("avgY", loop),
                            mcTrueBank.getFloat("avgZ", loop)
                            );
                if(detector == 6) hitsDC.add(hit);
                else if(detector == 23) hitsURWell.add(hit);
            }
        }
        
        return new MCTrue(hitsDC, hitsURWell);
    }
}
