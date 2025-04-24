package org.clas.fiducials;

import org.jlab.clas.physics.Vector3;
import org.jlab.detector.base.DetectorType;

import org.clas.element.Track;

/**
 *
 * @author devita
 */
public class Fiducial {
    
    private EleDCFiducial    electronFiducial = new EleDCFiducial();
    private HadronDCFiducial hadronFiducial   = new HadronDCFiducial();

    public Fiducial() {
    }

    public boolean inFiducial(Track track) {
        
        boolean inside = true;
        
        // DC fiducialsonly for TB tracks
        if(track.type()==1) {
            for(int region=1; region<=3; region++) {
                inside = inside && inFiducial(track.sector(),region, track.trajectory(DetectorType.DC.getDetectorId(),12*region),track.pid(),track.charge(),track.isinbending());
            }
        }
        return inside;
    }
    

    public boolean inFiducial(int sector, int region, Vector3 traj, int pid, int  charge, boolean inbending) {

        if(pid==11){
            return electronFiducial.DC_fiducial_cut_XY(sector,region,traj.x(),traj.y(),pid,inbending); 
        } else {
            return hadronFiducial.DC_fiducial_cut_theta_phi(sector,region,traj.x(),traj.y(),traj.z(),pid,charge,inbending);
        } 
    }

}
