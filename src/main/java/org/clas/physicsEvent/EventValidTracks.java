package org.clas.physicsEvent;

import java.util.ArrayList;

import org.clas.element.Track;

/**
 * Tracks are cut by Track::isValid()
 * @author Tongtong
 */
public class EventValidTracks extends BasePhysicsEvent{ 
    
    public EventValidTracks(ArrayList<Track> tracks) {
        super(tracks);
    }
    
    @Override
    public Track getTriggerTrk(){
        if(triggerTrk!=null && triggerTrk.isValid())
            return triggerTrk;
        else return null;
    }
    
    @Override
    public ArrayList<Track> getElectronTrks(){
        ArrayList<Track> selectedTrks = new ArrayList<>();
        for(Track trk : electronTrks){
            if(trk.isValid()) selectedTrks.add(trk);
        }
        return selectedTrks;
    }
    
    @Override
    public ArrayList<Track> getPosTrks(){
        ArrayList<Track> selectedTrks = new ArrayList<>();
        for(Track trk : posTrks){
            if(trk.isValid()) selectedTrks.add(trk);
        }
        return selectedTrks;
    }
    
    @Override
    public ArrayList<Track> getNegTrks(){
        ArrayList<Track> selectedTrks = new ArrayList<>();
        for(Track trk : negTrks){
            if(trk.isValid()) selectedTrks.add(trk);
        }    
        return selectedTrks;
    }
    
    @Override
    public ArrayList<Track> getPiPlusTrks(){
        ArrayList<Track> selectedTrks = new ArrayList<>();
        for(Track trk : piPlusTrks){
            if(trk.isValid()) selectedTrks.add(trk);
        }  
        return selectedTrks;
    }
    
    @Override
    public ArrayList<Track> getPiMinusTrks(){
        ArrayList<Track> selectedTrks = new ArrayList<>();
        for(Track trk : piMinusTrks){
            if(trk.isValid()) selectedTrks.add(trk);
        }         
        return selectedTrks;
    }
    
    @Override
    public ArrayList<Track> getProtonTrks(){
        ArrayList<Track> selectedTrks = new ArrayList<>();
        for(Track trk : protonTrks){
            if(trk.isValid()) selectedTrks.add(trk);
        }         
        return selectedTrks;
    }
    
    @Override
    public ArrayList<Track> getKaonPlusTrks(){
        ArrayList<Track> selectedTrks = new ArrayList<>();
        for(Track trk : kaonPlusTrks){
            if(trk.isValid()) selectedTrks.add(trk);
        }  
        return selectedTrks;
    }
    
    @Override
    public ArrayList<Track> getKaonMinusTrks(){
        ArrayList<Track> selectedTrks = new ArrayList<>();
        for(Track trk : kaonMinusTrks){
            if(trk.isValid()) selectedTrks.add(trk);
        }         
        return selectedTrks;
    }
}