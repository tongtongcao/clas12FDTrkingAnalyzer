package org.clas.physicsEvent;

import java.util.List;
import java.util.ArrayList;

import org.clas.element.Track;

/**
 * Tracks are cut by Track::isForLumiScan()
 * @author Tongtong
 */
public class EventForLumiScan extends BasePhysicsEvent{ 
    
    public EventForLumiScan(List<Track> tracks) {
        super(tracks);
    }
    
    @Override
    public Track getTriggerTrk(){
        if(triggerTrk!=null && triggerTrk.isForLumiScan())
            return triggerTrk;
        else return null;
    }
    
    @Override
    public List<Track> getElectronTrks(){
        List<Track> selectedTrks = new ArrayList<>();
        for(Track trk : electronTrks){
            if(trk.isForLumiScan()) selectedTrks.add(trk);
        }
        return selectedTrks;
    }
    
    @Override
    public List<Track> getPosTrks(){
        List<Track> selectedTrks = new ArrayList<>();
        for(Track trk : posTrks){
            if(trk.isForLumiScan()) selectedTrks.add(trk);
        }
        return selectedTrks;
    }
    
    @Override
    public List<Track> getNegTrks(){
        List<Track> selectedTrks = new ArrayList<>();
        for(Track trk : negTrks){
            if(trk.isForLumiScan()) selectedTrks.add(trk);
        }    
        return selectedTrks;
    }
    
    @Override
    public List<Track> getPiPlusTrks(){
        List<Track> selectedTrks = new ArrayList<>();
        for(Track trk : piPlusTrks){
            if(trk.isForLumiScan()) selectedTrks.add(trk);
        }  
        return selectedTrks;
    }
    
    @Override
    public List<Track> getPiMinusTrks(){
        List<Track> selectedTrks = new ArrayList<>();
        for(Track trk : piMinusTrks){
            if(trk.isForLumiScan()) selectedTrks.add(trk);
        }         
        return selectedTrks;
    }
    
    @Override
    public List<Track> getProtonTrks(){
        List<Track> selectedTrks = new ArrayList<>();
        for(Track trk : protonTrks){
            if(trk.isForLumiScan()) selectedTrks.add(trk);
        }         
        return selectedTrks;
    }
    
    @Override
    public List<Track> getKaonPlusTrks(){
        List<Track> selectedTrks = new ArrayList<>();
        for(Track trk : kaonPlusTrks){
            if(trk.isForLumiScan()) selectedTrks.add(trk);
        }  
        return selectedTrks;
    }
    
    @Override
    public List<Track> getKaonMinusTrks(){
        List<Track> selectedTrks = new ArrayList<>();
        for(Track trk : kaonMinusTrks){
            if(trk.isForLumiScan()) selectedTrks.add(trk);
        }         
        return selectedTrks;
    }
}