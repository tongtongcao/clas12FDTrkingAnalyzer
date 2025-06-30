package org.clas.element;

import java.util.List;
import java.util.ArrayList;

/**
 *
 * @author Tongtong
 */

public class AICandidate implements Comparable<AICandidate> {
    private int id;
    private int sector; // sector is not correct in bank ai::tracks
    private double prob;
    
    private int[] clusterIds = new int[6];
    private int numSL;
    
    private List<Cluster> clusters = null;
    private List<Hit> hits = null;
    List<Hit> normalHits = null;
    List<Hit> bgHits = null;
    private int numNormalHits = -1;
    private int numBgHits = -1;
    private double ratioNormalHits = -1;
    
    public AICandidate(int id, int sector, double prob){
        this.id = id;
        this.sector = sector;
        this.prob = prob;
    }
    
    public void clusters(int i1, int i2, int i3, int i4, int i5, int i6) {
        this.clusterIds[0] = i1;
        this.clusterIds[1] = i2;
        this.clusterIds[2] = i3;
        this.clusterIds[3] = i4;
        this.clusterIds[4] = i5;
        this.clusterIds[5] = i6;
        for(int i=0; i<6; i++) {
            if(this.clusterIds[i]<=0) this.clusterIds[i]=-1; //change 0 to -1 to allow matching of candidates to tracks
            if(this.clusterIds[i]>0)  this.numSL++;
        }
    }
    
    public int id(){
        return id;
    }
        
    public int sector(){
        return sector;
    }
    
    public int setSector(int sector){
        return this.sector = sector;
    }
    
    public double prob(){
        return prob;
    }
    
    public int numSL(){
        return numSL;
    }
    
    public void setClusters(List<Cluster> allClusters){
        clusters = new ArrayList();
        for(int i = 0; i < 6; i++){
            int clusterId = clusterIds[i];
            if(clusterId > 0){
                for(Cluster cls : allClusters){
                    if(clusterId == cls.id()) {
                        clusters.add(cls);
                        break;
                    }
                }
            }
        }
    }
    
    public void setHits(List<Hit> allHits){
        hits = new ArrayList();
        for(int i = 0; i < 6; i++){
            int clusterId = clusterIds[i];
            if(clusterId > 0){
                for(Hit hit: allHits){
                    if(hit.ClusterID() == clusterId) hits.add(hit);
                }
            }
        }
    }
    
    public void setHitsClusters(List<Cluster> allClusters, List<Hit> allHits){
        clusters = new ArrayList();
        for(int i = 0; i < 6; i++){
            int clusterId = clusterIds[i];
            if(clusterId > 0){
                for(Cluster cls : allClusters){
                    if(clusterId == cls.id()) {
                        clusters.add(cls);
                        break;
                    }
                }
            }
        }
        
        hits = new ArrayList();
        for(int i = 0; i < 6; i++){
            int clusterId = clusterIds[i];
            if(clusterId > 0){
                for(Hit hit: allHits){
                    if(hit.ClusterID() == clusterId) hits.add(hit);
                }
            }
        }
    }
        
    public void setHitsClusters(List<Cluster> allClusters){
        clusters = new ArrayList();
        hits = new ArrayList();
        for(int i = 0; i < 6; i++){
            int clusterId = clusterIds[i];
            if(clusterId > 0){
                for(Cluster cls : allClusters){
                    if(clusterId == cls.id()) {
                        clusters.add(cls);
                        if(cls.getHits() != null) hits.addAll(cls.getHits());
                        break;
                    }
                }
            }
        } 
    } 
    
    public List<Cluster> getClusters(){
        return clusters;
    } 
    
    public List<Hit> getHits(){
        return hits;
    }
    
    public boolean separateNormalBgHits(){
        if(hits == null) return false;
        else {
            normalHits = new ArrayList();
            bgHits = new ArrayList();
            for(Hit hit : hits){
                if(hit.isNormalHit())
                    normalHits.add(hit);                
                else bgHits.add(hit);
            }
            
            numNormalHits = normalHits.size();
            numBgHits = bgHits.size();
            ratioNormalHits = (double) numNormalHits/hits.size();
            return true;
        }
    }
    
    public List<Hit> getNormalHits(){
        return normalHits;
    }
    
    public List<Hit> getBgHits(){
        return bgHits;
    }
    
    public int getNumNormalHits(){
        return numNormalHits;
    } 
    
    public int getNumBgHits(){
        return numBgHits;
    }
    
    public double getRatioNormalHits(){
        return ratioNormalHits;
    }
    
    @Override
    public int compareTo(AICandidate o) {
        return this.id()<o.id() ? -1 : 1;
    } 
    
}