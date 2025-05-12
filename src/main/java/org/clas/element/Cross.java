package org.clas.element;

import java.util.ArrayList;
import java.util.List;

import org.jlab.geom.prim.Vector3D;
import org.jlab.geom.prim.Path3D;

import org.clas.fit.ClusterFitLC;
import org.clas.fit.LineFitter;
import org.clas.utilities.Constants;
import org.jlab.geom.prim.Point3D;
/**
 *
 * @author Tongtong
 */

public class Cross implements Comparable<Cross> {
    //ConvHB(11) or ConvTB(12) or AIHB(21) or AITB(22)
    private int type = 22;
    
    private int id;
    private int status;
    private int sector;
    private int region;

    private double x;
    private double y;
    private double z;    
    private double xErr;
    private double yErr;
    private double zErr;
    
    private double ux;
    private double uy;
    private double uz;    
    private double uxErr;
    private double uyErr;
    private double uzErr;
    
    private int cluster1Id = -999;
    private int cluster2Id = -999;
    private Cluster cluster1 = null;
    private Cluster cluster2 = null;
    
            
    public Cross(int type, int id, int status, int sector, int region, double x, double y, double z, double xErr, double yErr, double zErr, 
            double ux, double uy, double uz, double uxErr, double uyErr, double uzErr, int cluster1Id, int cluster2Id){
        this.type = type;
        this.id = id;
        this.status = status;
        this.sector = sector;
        this.region = region;        
        this.x = x;
        this.y = y;
        this.z = z;
        this.xErr = xErr;
        this.yErr = yErr;
        this.zErr = zErr;
        this.ux = ux;
        this.uy = uy;
        this.uz = uz;
        this.uxErr = uxErr;
        this.uyErr = uyErr;
        this.uzErr = uzErr;        
        this.cluster1Id = cluster1Id;
        this.cluster2Id = cluster2Id;
    }
    

   
    public int type(){
        return type;
    }
    
    public int id(){
        return id;
    }
    
    public int status(){
        return status;
    }
    
    public int sector(){
        return sector;
    }
    
    public int region(){
        return region;
    }
    
    public double x(){
        return x;
    }
    
    public double y(){
        return y;
    }
    
    public double z(){
        return z;
    }
    
    public double xErr(){
        return xErr;
    }
    
    public double yErr(){
        return yErr;
    }
    
    public double zErr(){
        return zErr;
    }    
    
    public double ux(){
        return ux;
    }
    
    public double uy(){
        return uy;
    }
    
    public double uz(){
        return uz;
    }
    
    public double uxErr(){
        return uxErr;
    }
    
    public double uyErr(){
        return uyErr;
    }
    
    public double uzErr(){
        return uzErr;
    }     
    
    public int cluster1Id(){
        return cluster1Id;
    }
    
    public int cluster2Id(){
        return cluster2Id;
    }    
    
    public void setCluster1(Cluster cls){
        cluster1 = cls;
    }
    
    public Cluster getCluster1(){
        return cluster1;
    }
        
    public void setCluster2(Cluster cls){
        cluster2 = cls;
    }
    
    public Cluster getCluster2(){
        return cluster2;
    }


    /**
     *
     * @param X
     * @param Y
     * @param Z
     * @return rotated coords from tilted sector coordinate system to the sector
     * coordinate system
     */
    public Point3D getCoordsInSector(double X, double Y, double Z) {
        double rz = -X * Constants.SIN25 + Z * Constants.COS25;
        double rx = X * Constants.COS25 + Z * Constants.SIN25;

        return new Point3D(rx, Y, rz);
    }

    /**
     *
     * @param X
     * @param Y
     * @param Z
     * @return rotated coords from tilted sector coordinate system to the lab
     * frame
     */
    public Point3D getCoordsInLab(double X, double Y, double Z) {
        Point3D PointInSec = this.getCoordsInSector(X, Y, Z);
        double rx = PointInSec.x() * Constants.COSSECTOR60[this.sector() - 1] - PointInSec.y() * Constants.SINSECTOR60[this.sector() - 1];
        double ry = PointInSec.x() * Constants.SINSECTOR60[this.sector() - 1] + PointInSec.y() * Constants.COSSECTOR60[this.sector() - 1];
        return new Point3D(rx, ry, PointInSec.z());
    }    
                  
    
    @Override
    public int compareTo(Cross o) {
        return this.id()<o.id() ? -1 : 1;
    }         
}