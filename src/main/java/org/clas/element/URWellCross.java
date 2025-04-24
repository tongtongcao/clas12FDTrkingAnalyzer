package org.clas.element;

import org.jlab.geom.prim.Point3D;

import org.clas.utilities.Constants;

/**
 *
 * @author Tongtong
 */

public class URWellCross implements Comparable<URWellCross> {    
    private int id;
    private int status;
    private int sector;
    private int region;
    private int cluster1Id;
    private int cluster2Id;
    private double energy;
    private double time;
    private Point3D point = null;
    private Point3D pointLocal = null;
    
    private URWellCluster cluster1 = null;
    private URWellCluster cluster2 = null;
    
    private boolean isUsedClustering = false;
    private boolean isUsedHB = false;
    private boolean isUsedTB = false;
    
    private static double xRelativeDCSL1LC = 1 - (Constants.DCSL1L1ZTSC - Constants.URWELLR1ZTSC)/Constants.INTERVALDCSL1L1L2TSC; // Layer number relative to DC SL1
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
    
    public boolean isMatchedCross(URWellCross o){
        if(this.point().x() == o.point().x() && this.point().y() == o.point().y() && this.point().z() == o.point().z()
                && this.energy() == o.energy() && this.time() == o.time()) return true;
        else return false;
    }
    
    public static double getXRelativeDCSL1LC(){
        return xRelativeDCSL1LC;
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