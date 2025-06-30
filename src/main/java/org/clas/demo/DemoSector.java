package org.clas.demo;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import org.jlab.groot.data.GraphErrors;

import org.clas.element.Hit;
import org.clas.element.TDC;
import org.clas.element.Cluster;
import org.clas.reader.LocalEvent;


/**
 *
 * @author Tongtong
 */

public class DemoSector{
    protected String name = "hitDemo";
    
    protected LocalEvent localEvent;
    protected int sector;
        
    protected Map<Integer, List<TDC>> map_sl_tdcs = new HashMap();
    protected Map<Integer, List<Hit>> map_sl_hits = new HashMap();
    protected Map<Integer, List<Cluster>> map_sl_clsListClustering = new HashMap();
    protected Map<Integer, List<Cluster>> map_sl_clsListAICand = new HashMap();
    protected Map<Integer, List<Cluster>> map_sl_clsListHB= new HashMap();
    protected Map<Integer, List<Cluster>> map_sl_clsListTB = new HashMap();
    
    protected Map<Integer, List<GraphErrors>> map_sl_graphList = new HashMap();
    
    /**
     * @param name name
     * @param localEvent events with all banks    
     * @param sector sector
    */    
    public DemoSector(String name, LocalEvent localEvent, int sector){        
        this.name = name;
        this.localEvent = localEvent;
        this.sector = sector;
        
        makeTDCMap();
        makeHitMap();
        makeClsListClusteringMap();
        makeClsListAICandMap();
        makeClsListHBMap();
        makeClsListTBMap();
    }       
    
    private void makeTDCMap(){
        for(int i = 1; i <=6; i++){
            map_sl_tdcs.put(i, new ArrayList<>());
        }
        
        for(TDC tdc: localEvent.getTDCs()){
            if(tdc.sector() == sector) map_sl_tdcs.get(tdc.superlayer()).add(tdc);
        }
    }
    
    private void makeHitMap(){
        for(int i = 1; i <=6; i++){
            map_sl_hits.put(i, new ArrayList<>());
        }
        
        for(Hit hit: localEvent.getHits()){
            if(hit.sector() == sector) map_sl_hits.get(hit.superlayer()).add(hit);
        }
    }
    
    private void makeClsListClusteringMap(){
        for(int i = 1; i <=6; i++){
            map_sl_clsListClustering.put(i, new ArrayList<>());
        }
        
        for(Cluster cls: localEvent.getClusters()){
            if(cls.sector() == sector) map_sl_clsListClustering.get(cls.superlayer()).add(cls);
        }
    }
    
    private void makeClsListAICandMap(){
        for(int i = 1; i <=6; i++){
            map_sl_clsListAICand.put(i, new ArrayList<>());
        }
        
        for(Cluster cls: localEvent.getClustersAICands()){
            if(cls.sector() == sector) map_sl_clsListAICand.get(cls.superlayer()).add(cls);
        }
    }
    
    private void makeClsListHBMap(){
        for(int i = 1; i <=6; i++){
            map_sl_clsListHB.put(i, new ArrayList<>());
        }
        
        for(Cluster cls: localEvent.getClustersHB()){
            if(cls.sector() == sector) map_sl_clsListHB.get(cls.superlayer()).add(cls);
        }
    }
    
    private void makeClsListTBMap(){
        for(int i = 1; i <=6; i++){
            map_sl_clsListTB.put(i, new ArrayList<>());
        }
        
        for(Cluster cls: localEvent.getClustersTB()){
            if(cls.sector() == sector) map_sl_clsListTB.get(cls.superlayer()).add(cls);
        }
    }    
    
    public void addGraphsDenoisingClustering(){
        for(int i = 1; i <=6; i++){
            String str = name + ": SL" + i;
            DemoSuperlayer demoSL = new DemoSuperlayer(str, "SL" + i, map_sl_tdcs.get(i), map_sl_hits.get(i), map_sl_clsListClustering.get(i));
            demoSL.addBaseLocalSuperlayer();
            demoSL.addGraphsDenoisingClustering();
            map_sl_graphList.put(i, demoSL.getGraphList());
        }                
    }
    
    public void addGraphsRawDenoisingClustering(){
        for(int i = 1; i <=6; i++){
            String str = name + ": SL" + i;
            DemoSuperlayer demoSL = new DemoSuperlayer(str, "SL" + i, map_sl_tdcs.get(i), map_sl_hits.get(i), map_sl_clsListClustering.get(i));
            demoSL.addBaseLocalSuperlayer();
            demoSL.addGraphsRawDenoisingClustering();
            map_sl_graphList.put(i, demoSL.getGraphList());
        }                
    }
    
    public void addGraphsDenoisingClusteringHBTB(){
        for(int i = 1; i <=6; i++){
            String str = name + ": SL" + i;
            DemoSuperlayer demoSL = new DemoSuperlayer(str, "SL" + i, map_sl_tdcs.get(i), map_sl_hits.get(i), map_sl_clsListClustering.get(i), map_sl_clsListHB.get(i), map_sl_clsListTB.get(i));
            demoSL.addBaseLocalSuperlayer();
            demoSL.addGraphsDenoisingClusteringHBTB();
            map_sl_graphList.put(i, demoSL.getGraphList());
        }                
    }
    
    public void addGraphsRawDenoisingClusteringHBTB(){
        for(int i = 1; i <=6; i++){
            String str = name + ": SL" + i;
            DemoSuperlayer demoSL = new DemoSuperlayer(str, "SL" + i, map_sl_tdcs.get(i), map_sl_hits.get(i), map_sl_clsListClustering.get(i), map_sl_clsListHB.get(i), map_sl_clsListTB.get(i));
            demoSL.addBaseLocalSuperlayer();
            demoSL.addGraphsRawDenoisingClusteringHBTB();
            map_sl_graphList.put(i, demoSL.getGraphList());
        }                
    }
    
    public void addGraphsDenoisingClusteringAICandHBTB(){
        for(int i = 1; i <=6; i++){
            String str = name + ": SL" + i;
            DemoSuperlayer demoSL = new DemoSuperlayer(str, "SL" + i, map_sl_tdcs.get(i), map_sl_hits.get(i), map_sl_clsListClustering.get(i),  map_sl_clsListAICand.get(i), map_sl_clsListHB.get(i), map_sl_clsListTB.get(i));
            demoSL.addBaseLocalSuperlayer();
            demoSL.addGraphsDenoisingClusteringAICandHBTB();
            map_sl_graphList.put(i, demoSL.getGraphList());
        }                
    }
    
    public void addGraphsRawDenoisingClusteringAICandHBTB(){
        for(int i = 1; i <=6; i++){
            String str = name + ": SL" + i;
            DemoSuperlayer demoSL = new DemoSuperlayer(str, "SL" + i, map_sl_tdcs.get(i), map_sl_hits.get(i), map_sl_clsListClustering.get(i), map_sl_clsListAICand.get(i), map_sl_clsListHB.get(i), map_sl_clsListTB.get(i));
            demoSL.addBaseLocalSuperlayer();
            demoSL.addGraphsRawDenoisingClusteringAICandHBTB();
            map_sl_graphList.put(i, demoSL.getGraphList());
        }                
    }
    
    public Map<Integer, List<GraphErrors>> getSLGraphListMap(){
        return map_sl_graphList;
    }    
                                  
}