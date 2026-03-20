package org.clas.element;

import java.util.List;
import java.util.ArrayList;

import org.jlab.geom.prim.Point3D;

import org.clas.utilities.Constants;

/**
 *
 * @author Tongtong
 */

public class URWellCross implements Comparable<URWellCross> {    
    private int id;
    private int tid;
    private int status;
    private int sector;
    private int region;
    private int cluster1Id;
    private int cluster2Id;
    private double energy;
    private double time;
    private Point3D point = null;
    private Point3D pointLocal = null;
    
    private double cluster1_x_state = -999;
    private double cluster1_y_state = -999;
    private double cluster1_B = -999;
    private double cluster1_pathLength = -999;
    private double cluster1_DAFWeight = -999;
    
    private double cluster2_x_state = -999;
    private double cluster2_y_state = -999;
    private double cluster2_B = -999;
    private double cluster2_pathLength = -999; 
    private double cluster2_DAFWeight = -999;
    
    private URWellCluster cluster1 = null;
    private URWellCluster cluster2 = null;
    
    private boolean isUsedClustering = false;
    private boolean isUsedHB = false;
    private boolean isUsedTB = false;
    
    private double xRelativeToDCSL1LC;
    private double yRelativeToDCSL1LC;
    
    public URWellCross(int id, int status, int sector, int region, int cluster1Id, int cluster2Id, 
            double energy, double time, double x, double y, double z){
        this.id = id;
        this.status = status;
        this.sector = sector;
        this.region = region;
        this.cluster1Id = cluster1Id;
        this.cluster2Id = cluster2Id;
        this.energy = energy;
        this.time = time;
        this.point = new Point3D(x, y, z);
        
        this.pointLocal = new Point3D(x, y, z);
        pointLocal.rotateZ(Math.toRadians(-60 * (sector - 1)));
        pointLocal.rotateY(Math.toRadians(-25));
    }
    
    public URWellCross(int id, int tid, int status, int sector, int region, int cluster1Id, int cluster2Id, 
            double energy, double time, double x, double y, double z, 
            double cluster1_x_state, double cluster1_y_state, double cluster1_B, double cluster1_pathLength,
            double cluster2_x_state, double cluster2_y_state, double cluster2_B, double cluster2_pathLength){
        this.id = id;
        this.tid = tid;
        this.status = status;
        this.sector = sector;
        this.region = region;
        this.cluster1Id = cluster1Id;
        this.cluster2Id = cluster2Id;
        this.energy = energy;
        this.time = time;
        this.point = new Point3D(x, y, z);
        
        this.pointLocal = new Point3D(x, y, z);
        pointLocal.rotateZ(Math.toRadians(-60 * (sector - 1)));
        pointLocal.rotateY(Math.toRadians(-25));
        
        this.cluster1_x_state = cluster1_x_state;
        this.cluster1_y_state = cluster1_y_state;
        this.cluster1_B = cluster1_B;
        this.cluster1_pathLength = cluster1_pathLength;
        
        this.cluster2_x_state = cluster2_x_state;
        this.cluster2_y_state = cluster2_y_state;
        this.cluster2_B = cluster2_B;
        this.cluster2_pathLength = cluster2_pathLength;       
    }     
    
    public URWellCross(int id, int tid, int status, int sector, int region, int cluster1Id, int cluster2Id, 
            double energy, double time, double x, double y, double z, 
            double cluster1_x_state, double cluster1_y_state, double cluster1_B, double cluster1_pathLength, double cluster1_DAFWeight,
            double cluster2_x_state, double cluster2_y_state, double cluster2_B, double cluster2_pathLength, double cluster2_DAFWeight){
        this.id = id;
        this.tid = tid;
        this.status = status;
        this.sector = sector;
        this.region = region;
        this.cluster1Id = cluster1Id;
        this.cluster2Id = cluster2Id;
        this.energy = energy;
        this.time = time;
        this.point = new Point3D(x, y, z);
        
        this.pointLocal = new Point3D(x, y, z);
        pointLocal.rotateZ(Math.toRadians(-60 * (sector - 1)));
        pointLocal.rotateY(Math.toRadians(-25));
        
        this.cluster1_x_state = cluster1_x_state;
        this.cluster1_y_state = cluster1_y_state;
        this.cluster1_B = cluster1_B;
        this.cluster1_pathLength = cluster1_pathLength;
        this.cluster1_DAFWeight = cluster1_DAFWeight;
        
        this.cluster2_x_state = cluster2_x_state;
        this.cluster2_y_state = cluster2_y_state;
        this.cluster2_B = cluster2_B;
        this.cluster2_pathLength = cluster2_pathLength;
        this.cluster2_DAFWeight = cluster2_DAFWeight;        
    }    
    
    public void setIsUsedClustering(boolean isUsedClustering){
        this.isUsedClustering = isUsedClustering;
    }
    
    public boolean isUsedClustering(){
        return isUsedClustering;
    }
    
    public void setIsUsedHB(boolean isUsedHB){
        this.isUsedHB = isUsedHB;
    }
    
    public boolean isUsedHB(){
        return isUsedHB;
    }
    
    public void setIsUsedTB(boolean isUsedTB){
        this.isUsedTB = isUsedTB;
    }
    
    public boolean isUsedTB(){
        return isUsedTB;
    } 
    
    public void setCluster1(URWellCluster cls){
        cluster1 = cls;
    }
    
    public URWellCluster getCluster1(){
        return cluster1;
    }
        
    public void setCluster2(URWellCluster cls){
        cluster2 = cls;
    }
    
    public URWellCluster getCluster2(){
        return cluster2;
    }
    
    public boolean isCluster1Normal(){
        if(cluster1 != null) return cluster1.isMainStripNormal();
        else return false;
    }
    
    public boolean isCluster2Normal(){
        if(cluster2 != null) return cluster2.isMainStripNormal();
        else return false;
    }
    
    public boolean isBothClustersNormal(){
        if(cluster1 != null && cluster2 != null) return cluster1.isMainStripNormal() && cluster2.isMainStripNormal();
        else return false;
    }
    
    public boolean isBothClustersNoise(){
        if(cluster1 != null && cluster2 != null) return (!cluster1.isMainStripNormal()) && (!cluster2.isMainStripNormal());
        else return false;
    }
    
    
    public int id(){
        return id;
    }
    
    public int tid(){
        return tid;
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
    
    public int cluster1Id(){
        return cluster1Id;
    }
        
    public int cluster2Id(){
        return cluster2Id;
    }
    
    public double energy(){
        return energy;
    }
    
    public double time(){
        return time;
    }
    
    public Point3D point(){
        return point;
    }
    
    public Point3D pointLocal(){
        return pointLocal;
    }
    
    public double cluster1_x_state(){
        return cluster1_x_state;
    }
    
    public double cluster1_y_state(){
        return cluster1_y_state;
    }

    public double cluster1_B(){
        return cluster1_B;
    }

    public double cluster1_pathLength(){
        return cluster1_pathLength;
    }

    public double cluster1_DAFWeight(){
        return cluster1_DAFWeight;
    } 
    
    public double cluster2_x_state(){
        return cluster2_x_state;
    }
    
    public double cluster2_y_state(){
        return cluster2_y_state;
    }

    public double cluster2_B(){
        return cluster2_B;
    }

    public double cluster2_pathLength(){
        return cluster2_pathLength;
    }

    public double cluster2_DAFWeight(){
        return cluster2_DAFWeight;
    }    
        
    public URWellCross getClosetURWellCross(List<URWellCross> crosses){
        URWellCross urCrsMinDist = null;
        double minDist = 9999;
        for(URWellCross thatCrs : crosses){
            if(this.region() == thatCrs.region()){
                double dist = this.pointLocal.distance(thatCrs.pointLocal);
                if(dist < minDist) {
                    minDist = dist;
                    urCrsMinDist = thatCrs;
                }
            }
        }
        
        return urCrsMinDist;
    }
    
    public boolean isMatchedCross(URWellCross o){
        if(this.point().x() == o.point().x() && this.point().y() == o.point().y() && this.point().z() == o.point().z()
                && this.energy() == o.energy() && this.time() == o.time()) return true;
        else return false;
    }
    
    public double getXRelativeDCSL1LC(){
        xRelativeToDCSL1LC = 1 - (Constants.DCSL1L1ZTSC - pointLocal.z())/Constants.INTERVALDCSL1L1L2TSC;
        return xRelativeToDCSL1LC;
    }
    
    // Calculate x along DC SL1 direction at the plane y = 0 in TSC
    public double calculateXAlongDCSL1PlaneY0TSC(){
        double xAlongDCSL1PlaneY0TSC = -9999;
        if(pointLocal != null) xAlongDCSL1PlaneY0TSC = pointLocal.x()  - pointLocal.y() * Math.tan(Math.toRadians(6));
        return xAlongDCSL1PlaneY0TSC;
    }
    
    public double getYRelativeDCSL1LC(){
        double xAlongDCSL1PlaneY0TSC = calculateXAlongDCSL1PlaneY0TSC();
        yRelativeToDCSL1LC = (xAlongDCSL1PlaneY0TSC - Constants.DCSL1L1W1XTSC) * Math.cos(Math.toRadians(6))/Constants.INTERVALDCSL1L1L2TSC + Constants.YDCSL1L1W1LC;
        return yRelativeToDCSL1LC;
    }

    @Override
    public int compareTo(URWellCross o) {
        return this.id()<o.id() ? -1 : 1;
    }      
}