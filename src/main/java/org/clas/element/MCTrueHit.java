package org.clas.element;

import org.jlab.geom.prim.Point3D;

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
        
    public MCTrueHit(int pid, int detector, int hitnum, double x, double y, double z){
        this.pid = pid;
        this.detector = detector;
        this.hitnum = hitnum;
        this.positionGlobal = new Point3D(x, y, z);
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
    
    public Point3D positionGlobal(){
        return positionGlobal;
    }
    

    
    @Override
    public int compareTo(MCTrueHit o) {
        return this.hitnum()<o.hitnum() ? -1 : 1;
    }         
}