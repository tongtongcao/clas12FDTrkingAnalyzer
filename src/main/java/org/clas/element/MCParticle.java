package org.clas.element;

import org.jlab.clas.physics.Particle;
import org.jlab.clas.physics.Vector3;

/**
 *
 * @author Tongtong
 */

public class MCParticle {    
    private int pid;
    private double px;
    private double py;
    private double pz;
    private double vx;
    private double vy;
    private double vz;
    private double vt;
    
    private Vector3 mom = new Vector3(0.0,0.0,0.0);
    private Vector3 vertex = new Vector3(0.0,0.0,0.0);
        
    public MCParticle(int pid, double px, double py, double pz, double vx, double vy, double vz, double vt){
        this.pid = pid;
        this.px = px;
        this.py = py;
        this.pz = pz;
        this.vx = vx;
        this.vy = vy;
        this.vz = vz;
        this.vt =vt;
        
        mom.setXYZ(px, py, pz);
        vertex.setXYZ(vx, vy, vz);
    }
    
    public int pid(){
        return pid;
    }    
    
    public double px(){
        return px;
    }
    
    public double py(){
        return py;
    }
    
    public double pz(){
        return pz;
    }

    public double vx(){
        return vx;
    }

    public double vy(){
        return vy;
    }

    public double vz(){
        return vz;
    }

    public double vt(){
        return vt;
    }
    
    public Vector3 mom(){
        return mom;
    }
    
    public Vector3 vertex(){
        return vertex;
    }

    public double euclideanDistanceMom(Track trk) {
        double xx = (this.px - trk.vector().px());
        double yy = (this.py - trk.vector().py());
        double zz = (this.pz - trk.vector().pz());
        return Math.sqrt(xx * xx + yy * yy + zz * zz);
    }
    

    public double euclideanDistanceVertex(Track trk) {
        double xx = (this.vx - trk.vertex().x());
        double yy = (this.vy - trk.vertex().y());
        double zz = (this.vz - trk.vertex().z());
        return Math.sqrt(xx * xx + yy * yy + zz * zz);
    }

    public Particle particle() {
        if(this.pid()!=0)
            return new Particle(this.pid(), this.px(), this.py(), this.pz(), this.vx(), this.vy(), this.vz());
        else
            return null;
    }    
        
        
}