package org.clas.element;

import org.clas.utilities.CommonFunctions;
import org.jlab.geom.prim.Point3D;
import org.jlab.clas.physics.Vector3;

/**
 *
 * @author Tongtong
 */

public class MCTrueHit implements Comparable<MCTrueHit> {
    private int pid;
    private int detector;
    private int hitnum;
    private Point3D positionGlobal;
    private Point3D positionLocal;
    private Vector3 momGlobal;
    private Vector3 momLocal;
    private double time;
        
    public MCTrueHit(int pid, int detector, int hitnum, double x, double y, double z, double px, double py, double pz, double time){
        this.pid = pid;
        this.detector = detector;
        this.hitnum = hitnum;
        this.positionGlobal = new Point3D(x, y, z);
        this.momGlobal = new Vector3(px, py, pz);
        this.time = time;
    }
    
    public int pid(){
        return pid;
    }    
    
    public int detector(){
        return detector;
    }
        
    public int hitnum(){
        return hitnum;
    }
    
    public Point3D getPositionGlobal(){
        return positionGlobal;
    }
    
    public Vector3 getMomGlobal(){
        return momGlobal;
    }
    
    public Point3D getPositionLocal(int sector){
        return CommonFunctions.getCoordsInLocal(positionGlobal, sector);
    }
    
    public Vector3 getMomLocal(int sector){
        return CommonFunctions.getCoordsInLocal(momGlobal, sector);
    }
    
    public double getTime(){
        return time;
    }    

    
    @Override
    public int compareTo(MCTrueHit o) {
        return this.hitnum()<o.hitnum() ? -1 : 1;
    }         
}