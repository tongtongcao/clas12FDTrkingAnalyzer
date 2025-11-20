package org.clas.reader;

import java.util.ArrayList;
import java.util.Collections;
import org.jlab.jnp.hipo4.data.Bank;
import org.jlab.jnp.hipo4.data.Event;
import org.jlab.jnp.hipo4.data.SchemaFactory;

import org.clas.utilities.Constants;

/**
 *
 * @author Tongtong
 */
public class Banks {    
    private Bank runConfig;
    
    // MC Particle and true
    private Bank mcParticleBank;
    private Bank mcTrueBank;
    
    // DC banks
    private Bank dcTDCBank;
    private Bank dcHitBank;
    private Bank dcClusterBank;  
    
    // uRWell banks
    private Bank uRWellADCBank;
    private Bank uRWellHitBank;
    private Bank uRWellClusterBank;
    private Bank uRWellCrossBank;
    
    // uRWell-DC bank
    private Bank uRWellDCClusterBank;
        
    // conventional banks
    private Bank cvHBParticleBank;
    private Bank cvHBTrajectoryBank;
    private Bank cvHBTrackBank;
    private Bank cvHBTrackingBank;
    private Bank cvHBCrossBank;
    private Bank cvHBSegmentBank;
    private Bank cvHBClusterBank;
    private Bank cvHBHitBank;
    private Bank cvHBHitTrkIdBank;
    private Bank cvTBParticleBank;
    private Bank cvTBTrajectoryBank;
    private Bank cvTBTrackBank;
    private Bank cvTBTrackingBank;
    private Bank cvTBCrossBank;
    private Bank cvTBSegmentBank;
    private Bank cvTBClusterBank;
    private Bank cvTBHitBank;
    private Bank cvRecHBEventBank;
    private Bank cvRecTBEventBank;
    
    // ai banks
    private Bank aiCandidates;
    private Bank aiHBParticleBank;
    private Bank aiHBTrajectoryBank;
    private Bank aiHBTrackBank;
    private Bank aiHBTrackingBank;
    private Bank aiHBCrossBank;
    private Bank aiHBSegmentBank;
    private Bank aiHBClusterBank;
    private Bank aiHBHitBank;
    private Bank aiHBHitTrkIdBank;
    private Bank aiTBParticleBank;
    private Bank aiTBTrajectoryBank;
    private Bank aiTBTrackBank;
    private Bank aiTBTrackingBank;
    private Bank aiTBCrossBank;
    private Bank aiTBSegmentBank;
    private Bank aiTBClusterBank;
    private Bank aiTBHitBank;
    private Bank aiRecHBEventBank;
    private Bank aiRecTBEventBank;
    

    public Banks(SchemaFactory schema) {
        if(schema.hasSchema("RUN::config"))
            this.runConfig        = new Bank(schema.getSchema("RUN::config"));

        // MC banks
        if(schema.hasSchema("MC::Particle"))
            this.mcParticleBank        = new Bank(schema.getSchema("MC::Particle"));        
        if(schema.hasSchema("MC::True"))
            this.mcTrueBank        = new Bank(schema.getSchema("MC::True"));
                
        // DC banks
        if(schema.hasSchema("DC::tot"))
            this.dcTDCBank        = new Bank(schema.getSchema("DC::tot"));
        else if(schema.hasSchema("DC::tdc"))
            this.dcTDCBank        = new Bank(schema.getSchema("DC::tdc"));
        if(schema.hasSchema("HitBasedTrkg::Hits"))
            this.dcHitBank        = new Bank(schema.getSchema("HitBasedTrkg::Hits"));
        if(schema.hasSchema("HitBasedTrkg::Clusters"))
            this.dcClusterBank    = new Bank(schema.getSchema("HitBasedTrkg::Clusters"));     
        
        // uRWell banks
        if(schema.hasSchema("URWELL::adc"))
            this.uRWellADCBank    = new Bank(schema.getSchema("URWELL::adc"));        
        if(schema.hasSchema("URWELL::hits"))
            this.uRWellHitBank    = new Bank(schema.getSchema("URWELL::hits"));
        if(schema.hasSchema("URWELL::clusters"))
            this.uRWellClusterBank= new Bank(schema.getSchema("URWELL::clusters"));
        if(schema.hasSchema("URWELL::crosses"))
            this.uRWellCrossBank  = new Bank(schema.getSchema("URWELL::crosses"));  
        
        // uRWell-DC bank
        if(schema.hasSchema("HitBasedTrkg::URWellDCClusters"))
            this.uRWellDCClusterBank = new Bank(schema.getSchema("HitBasedTrkg::URWellDCClusters"));  
        
        // Conventional HB banks
        if(schema.hasSchema("REC::Particle"))
            this.cvHBParticleBank   = new Bank(schema.getSchema("RECHB::Particle"));
        if(schema.hasSchema("REC::Traj"))
            this.cvHBTrajectoryBank = new Bank(schema.getSchema("RECHB::Traj"));
        if(schema.hasSchema("REC::Track"))
            this.cvHBTrackBank      = new Bank(schema.getSchema("RECHB::Track"));
        if(schema.hasSchema("HitBasedTrkg::HBTracks")) {
            this.cvHBTrackingBank   = new Bank(schema.getSchema("HitBasedTrkg::HBTracks"));
        }
        if(schema.hasSchema("HitBasedTrkg::HBCrosses")) {
            this.cvHBCrossBank        = new Bank(schema.getSchema("HitBasedTrkg::HBCrosses"));
        }
        if(schema.hasSchema("HitBasedTrkg::HBSegments")) {
            this.cvHBSegmentBank        = new Bank(schema.getSchema("HitBasedTrkg::HBSegments"));
        }
        if(schema.hasSchema("HitBasedTrkg::HBClusters")) {
            this.cvHBClusterBank        = new Bank(schema.getSchema("HitBasedTrkg::HBClusters"));
        }            
        if(schema.hasSchema("HitBasedTrkg::HBHits")) {
            this.cvHBHitBank        = new Bank(schema.getSchema("HitBasedTrkg::HBHits"));
        }
        if(schema.hasSchema("HitBasedTrkg::HBHitTrkId")) {
            this.cvHBHitTrkIdBank   = new Bank(schema.getSchema("HitBasedTrkg::HBHitTrkId"));
        }
        if(schema.hasSchema("RECHB::Event")) {
            this.cvRecHBEventBank   = new Bank(schema.getSchema("RECHB::Event"));
        }        

        // Conventional TB banks
        if(schema.hasSchema("REC::Particle"))
            this.cvTBParticleBank   = new Bank(schema.getSchema("REC::Particle"));
        if(schema.hasSchema("REC::Traj"))
            this.cvTBTrajectoryBank = new Bank(schema.getSchema("REC::Traj"));
        if(schema.hasSchema("REC::Track"))
            this.cvTBTrackBank      = new Bank(schema.getSchema("REC::Track"));
        if(schema.hasSchema("TimeBasedTrkg::TBTracks")) {
            this.cvTBTrackingBank   = new Bank(schema.getSchema("TimeBasedTrkg::TBTracks"));
        }
        if(schema.hasSchema("TimeBasedTrkg::TBCrosses")) {
            this.cvTBCrossBank        = new Bank(schema.getSchema("TimeBasedTrkg::TBCrosses"));
        }
        if(schema.hasSchema("TimeBasedTrkg::TBSegments")) {
            this.cvTBSegmentBank        = new Bank(schema.getSchema("TimeBasedTrkg::TBSegments"));
        }
        if(schema.hasSchema("TimeBasedTrkg::TBClusters")) {
            this.cvTBClusterBank        = new Bank(schema.getSchema("TimeBasedTrkg::TBClusters"));
        }            
        if(schema.hasSchema("TimeBasedTrkg::TBHits")) {
            this.cvTBHitBank        = new Bank(schema.getSchema("TimeBasedTrkg::TBHits"));
        }
        if(schema.hasSchema("REC::Event")) {
            this.cvRecTBEventBank   = new Bank(schema.getSchema("REC::Event"));
        }   

        // AI HB banks
        if(schema.hasSchema("ai::tracks"))
            this.aiCandidates     = new Bank(schema.getSchema("ai::tracks"));
        if(schema.hasSchema("RECHBAI::Particle"))
            this.aiHBParticleBank   = new Bank(schema.getSchema("RECHBAI::Particle"));
        if(schema.hasSchema("RECHBAI::Traj"))
            this.aiHBTrajectoryBank = new Bank(schema.getSchema("RECHBAI::Traj"));
        if(schema.hasSchema("RECHBAI::Track"))
            this.aiHBTrackBank      = new Bank(schema.getSchema("RECHBAI::Track"));
        if(schema.hasSchema("HitBasedTrkg::AITracks")) {
            this.aiHBTrackingBank   = new Bank(schema.getSchema("HitBasedTrkg::AITracks"));
        }
        if(schema.hasSchema("HitBasedTrkg::AICrosses")) {
            this.aiHBCrossBank        = new Bank(schema.getSchema("HitBasedTrkg::AICrosses"));
        }
        if(schema.hasSchema("HitBasedTrkg::AISegments")) {
            this.aiHBSegmentBank        = new Bank(schema.getSchema("HitBasedTrkg::AISegments"));
        }
        if(schema.hasSchema("HitBasedTrkg::AIClusters")) {
            this.aiHBClusterBank        = new Bank(schema.getSchema("HitBasedTrkg::AIClusters"));
        }            
        if(schema.hasSchema("HitBasedTrkg::AIHits")) {
            this.aiHBHitBank        = new Bank(schema.getSchema("HitBasedTrkg::AIHits"));
        }
        if(schema.hasSchema("HitBasedTrkg::AIHitTrkId")) {
            this.aiHBHitTrkIdBank   = new Bank(schema.getSchema("HitBasedTrkg::AIHitTrkId"));
        }
        if(schema.hasSchema("RECHBAI::Event")) {
            this.aiRecHBEventBank   = new Bank(schema.getSchema("RECHBAI::Event"));
        }         

        // AI TB banks
        if(schema.hasSchema("RECAI::Particle"))
            this.aiTBParticleBank   = new Bank(schema.getSchema("RECAI::Particle"));
        if(schema.hasSchema("RECAI::Traj"))
            this.aiTBTrajectoryBank = new Bank(schema.getSchema("RECAI::Traj"));
        if(schema.hasSchema("RECAI::Track"))
            this.aiTBTrackBank      = new Bank(schema.getSchema("RECAI::Track"));
        if(schema.hasSchema("TimeBasedTrkg::AITracks")) {
            this.aiTBTrackingBank   = new Bank(schema.getSchema("TimeBasedTrkg::AITracks"));
        }
        if(schema.hasSchema("TimeBasedTrkg::AICrosses")) {
            this.aiTBCrossBank        = new Bank(schema.getSchema("TimeBasedTrkg::AICrosses"));
        }
        if(schema.hasSchema("TimeBasedTrkg::AISegments")) {
            this.aiTBSegmentBank        = new Bank(schema.getSchema("TimeBasedTrkg::AISegments"));
        }
        if(schema.hasSchema("TimeBasedTrkg::AIClusters")) {
            this.aiTBClusterBank        = new Bank(schema.getSchema("TimeBasedTrkg::AIClusters"));
        }            
        if(schema.hasSchema("TimeBasedTrkg::AIHits")) {
            this.aiTBHitBank        = new Bank(schema.getSchema("TimeBasedTrkg::AIHits"));
        }
        if(schema.hasSchema("RECAI::Event")) {
            this.aiRecTBEventBank   = new Bank(schema.getSchema("RECAI::Event"));
        } 
    }

    public Bank getRunConfig() {
	return runConfig;
    }
    
    public Bank getMCParticle() {
	return mcParticleBank;
    }
    
    public Bank getMCTrue() {
	return mcTrueBank;
    }
    
    public Bank getDCTDCBank() {
	return dcTDCBank;
    }
    
    public Bank getDCHitBank() {
	return dcHitBank;
    }
    
    public Bank getDCClusterBank() {
	return dcClusterBank;
    }
    
    public Bank getURWellADCBank() {
	return uRWellADCBank;
    }
    
    public Bank getURWellHitBank() {
	return uRWellHitBank;
    }
    
    public Bank getURWellClusterBank() {
	return uRWellClusterBank;
    }

    public Bank getURWellCrossBank() {
	return uRWellCrossBank;
    } 
    
    public Bank getURWellDCClusterBank() {
	return uRWellDCClusterBank;
    }

    public Bank getRecParticleBank(int type) {
        switch(type) {
            case Constants.CONVHB:
                return cvHBParticleBank;
            case Constants.CONVTB:
                return cvTBParticleBank;
            case Constants.AIHB:
                return aiHBParticleBank;
            case Constants.AITB:
                return aiTBParticleBank;
            default:
                return null;
        }        
    }

    public Bank getRecTrajectoryBank(int type) {
        switch(type) {
            case Constants.CONVHB:
                return cvHBTrajectoryBank;
            case Constants.CONVTB:
                return cvTBTrajectoryBank;
            case Constants.AIHB:
                return aiHBTrajectoryBank;
            case Constants.AITB:
                return aiTBTrajectoryBank;
            default:
                return null;        
        }        
    }

    public Bank getRecTrackBank(int type) {
        switch(type) {
            case Constants.CONVHB:
                return cvHBTrackBank;
            case Constants.CONVTB:
                return cvTBTrackBank;
            case Constants.AIHB:
                return aiHBTrackBank;
            case Constants.AITB:
                return aiTBTrackBank;
            default:
                return null; 
        } 
    }

    public Bank getTrackingBank(int type) {
        switch(type) {
            case Constants.CONVHB:
                return cvHBTrackingBank;
            case Constants.CONVTB:
                return cvTBTrackingBank;
            case Constants.AIHB:
                return aiHBTrackingBank;
            case Constants.AITB:
                return aiTBTrackingBank;
            default:
                return null;         
        }         
    }
    
    public Bank getTrackingCrossBank(int type) {
        switch(type) {
            case Constants.CONVHB:
                return cvHBCrossBank;
            case Constants.CONVTB:
                return cvTBCrossBank;
            case Constants.AIHB:
                return aiHBCrossBank;
            case Constants.AITB:
                return aiTBCrossBank;
            default:
                return null; 
        }  
    }

    public Bank getTrackingSegmentBank(int type) {
        switch(type) {
            case Constants.CONVHB:
                return cvHBSegmentBank;
            case Constants.CONVTB:
                return cvTBSegmentBank;
            case Constants.AIHB:
                return aiHBSegmentBank;
            case Constants.AITB:
                return aiTBSegmentBank; 
            default:
                return null;                 
        }  
    } 
    
    public Bank getTrackingClusterBank(int type) {
        switch(type) {
            case Constants.CONVHB:
                return cvHBClusterBank;
            case Constants.CONVTB:
                return cvTBClusterBank;
            case Constants.AIHB:
                return aiHBClusterBank;
            case Constants.AITB:
                return aiTBClusterBank;
            default:
                return null;                 
        }  
    }    
    
    public Bank getTrackingHitBank(int type) {
        switch(type) {
            case Constants.CONVHB:
                return cvHBHitBank;
            case Constants.CONVTB:
                return cvTBHitBank;
            case Constants.AIHB:
                return aiHBHitBank;
            case Constants.AITB:
                return aiTBHitBank;
            default:
                return null;                 
        }  
    }
    
    public Bank getHBTrackingHitTrkIdBank(int type){
        switch(type){
            case Constants.CONVHB:
                return cvHBHitTrkIdBank;
            case Constants.AIHB:
                return aiHBHitTrkIdBank;
            default:
                return null; 
            
        }
    }
    
    public Bank getRecEventBank(int type) {
        switch(type) {
            case Constants.CONVHB:
                return cvRecHBEventBank;
            case Constants.CONVTB:
                return cvRecTBEventBank;
            case Constants.AIHB:
                return aiRecHBEventBank;
            case Constants.AITB:
                return aiRecTBEventBank;
            default:
                return null;                 
        }  
    }
    
    public Bank getAICandidateBank() {
	return aiCandidates;
    }
}