package org.clas.physicsEvent;

import java.util.List;
import java.util.ArrayList;
import org.jlab.clas.physics.Particle;

import org.clas.utilities.Constants;
import org.clas.element.Track;

/**
 *
 * @author Tongtong
 */
public class BasePhysicsEvent{
    
    private Particle beam   = null;
    private Particle target = null;

    protected Track triggerTrk = null;
    
    protected List<Track> tracks = null;
    protected List<Track> electronTrks   = null;  // electron tracks excluding trigger
    protected List<Track> posTrks   = null;  
    protected List<Track> negTrks   = null; // negative tracks excluding trigger     
    protected List<Track> piPlusTrks = null;
    protected List<Track> piMinusTrks = null;
    protected List<Track> protonTrks = null;    
    protected List<Track> kaonPlusTrks = null;  
    protected List<Track> kaonMinusTrks = null;  
    
    public BasePhysicsEvent(List<Track> tracks) {
        this.tracks = tracks;
        
        this.beam   = new Particle(11, 0,0,Constants.BEAMENERGY, 0,0,0);
        this.target = Particle.createWithPid(Constants.TARGETPID, 0,0,0, 0,0,0);         

        electronTrks = new ArrayList<>();  
        posTrks   = new ArrayList<>();  
        negTrks   = new ArrayList<>(); 
        piPlusTrks   = new ArrayList<>();
        piMinusTrks   = new ArrayList<>();
        protonTrks   = new ArrayList<>();
        kaonPlusTrks = new ArrayList<>();
        kaonMinusTrks = new ArrayList<>();
        
        for(Track track : tracks) {
            if(triggerTrk==null && track.pid()==11 && track.status()<0) {
                triggerTrk = track;
            }
            if(track.pid()==11 && track.status()>0) {
                electronTrks.add(track);
            }              
            if(track.charge()>0 && track.status()>0)  {
                posTrks.add(track);
            }
            if(track.charge()<0 && track.status()>0)  {
                negTrks.add(track);
            }
            if(track.pid()==211) {
                piPlusTrks.add(track);
            }
            if(track.pid()==-211)  {
                piMinusTrks.add(track);
            }
            if(track.pid()==2212)  {
                protonTrks.add(track);
            }             
            if(track.pid()==321)  {
                kaonPlusTrks.add(track);
            }
            if(track.pid()==-321)  {
                kaonMinusTrks.add(track);
            } 

        }
    }
    
    public List<Track> getTracks(){
        return tracks;
    }
    
    public Particle getBeam(){
        return beam;
    }
    
    public Particle getTarget(){
        return target;
    }
    
    public Track getTriggerTrk(){
        return triggerTrk;
    }
    
    public List<Track> getElectronTrks(){
        return electronTrks;
    }
        
    public List<Track> getPosTrks(){
        return posTrks;
    }
    
    public List<Track> getNegTrks(){
        return negTrks;
    }
    
    public List<Track> getPiPlusTrks(){
        return piPlusTrks;
    }
    
    public List<Track> getPiMinusTrks(){
        return piMinusTrks;
    }
    
    public List<Track> getProtonTrks(){
        return protonTrks;
    }
    
    public List<Track> getKaonPlusTrks(){
        return kaonPlusTrks;
    }
    
    public List<Track> getKaonMinusTrks(){
        return kaonMinusTrks;
    }
}
