package org.clas.element;

import java.util.List;

/**
 *
 * @author Tongtong
 */

public class MCTrue {    
    private List<MCTrueHit> hitsDC;
    private List<MCTrueHit> hitsURWell;
    
        
    public MCTrue(List<MCTrueHit> hitsDC, List<MCTrueHit> hitsURWell){
        this.hitsDC = hitsDC;
        this.hitsURWell = hitsURWell;
    }
    
    public List<MCTrueHit> getHitsDC(){
        return hitsDC;
    }    
    
    public List<MCTrueHit> getHitsURWell(){
        return hitsURWell;
    }       
}