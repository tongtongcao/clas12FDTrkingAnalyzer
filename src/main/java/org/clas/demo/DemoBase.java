package org.clas.demo;

import java.util.List;
import java.util.ArrayList;

import org.jlab.groot.data.DataVector;
import org.jlab.groot.data.GraphErrors;

import org.clas.element.Cluster;
import org.clas.element.Hit;
import org.clas.element.TDC;
import org.clas.element.URWellCross;

/**
 *
 * @author Tongtong
 */

public class DemoBase{
    protected String name = "hitDemo";
    protected String title = "hitDemo";
        
    private List<TDC> tdcs;
    private List<Hit> hits;  
    protected List<GraphErrors> graphList = new ArrayList();
    
    private List<TDC> normalTDCs;
    private List<TDC> bgTDCs;
    private List<Hit> normalHits;
    private List<Hit> bgHits;
    
    public DemoBase(){}
    
    public DemoBase(String name, String title){
        this.name = name;
        this.title = title;
        this.tdcs = tdcs;
    }  

    public DemoBase(String name, String title, List<Hit> hits){
        this.name = name;
        this.title = title;
        this.hits = hits;
        separateNormalBg();
    }
          
    public DemoBase(String name, String title, List<TDC> tdcs, List<Hit> hits){
        this.name = name;
        this.title = title;
        this.tdcs = tdcs;
        this.hits = hits;
        separateNormalBg();
    }
    
    public void setName(String name){
        this.name = name;
    }
    
    public void setTitle(String title){
        this.title = title;
    }
    
    public void setTDCs(List<TDC> tdcs){
        this.tdcs = tdcs;
        separateNormalBgTDCs();
    }
    
    public void setHits(List<Hit> hits){
        this.hits = hits;
        separateNormalBgHits();
    }
    
    private void separateNormalBg(){
        separateNormalBgTDCs();
        separateNormalBgHits();
    }
    
    private boolean separateNormalBgTDCs(){
        if(tdcs == null) return false;
        else {
            normalTDCs = new ArrayList();
            bgTDCs = new ArrayList();
            for(TDC tdc : tdcs){
                if(tdc.isNormalHit())
                    normalTDCs.add(tdc);                
                else bgTDCs.add(tdc);
            }            
            return true;
        }        
    }
    
    private boolean separateNormalBgHits(){
        if(hits == null) return false;
        else {
            normalHits = new ArrayList();
            bgHits = new ArrayList();
            for(Hit hit : hits){
                if(hit.isNormalHit())
                    normalHits.add(hit);                
                else bgHits.add(hit);
            }            
            return true;
        }
    }
    
    public void addBaseLocalSuperlayer(){
        graphList.add(createBaseLocalSuperlayer(name, title, 1, 1, 0));
    }
    
    public void addBaseLocalSuperlayerWithURWell(){
        graphList.add(createBaseLocalSuperlayerWithURWell(name, title, 1, 1, 0));
    }
    
    public void addTDCGraphs(){
        if(!normalTDCs.isEmpty()) graphList.add(this.demonTDCClumpSuperlayer(name+"normalTDCs", "normalTDCs", normalTDCs, DemoConstants.NORMALHITMARKERSTYLE, DemoConstants.MarkerColor.TDC.getMarkerColor(), DemoConstants.MARKERSIZE));        
        if(!bgTDCs.isEmpty()) graphList.add(this.demonTDCClumpSuperlayer(name+"bgTDCs", "bgTDCs", bgTDCs, DemoConstants.BGHITMARKERSTYLE, DemoConstants.MarkerColor.TDC.getMarkerColor(), DemoConstants.MARKERSIZE));
        if(normalTDCs.size() == 0  &&  bgTDCs.size() == 0 && hits.size() != 0) graphList.add(this.demonTDCClumpSuperlayer(name+"tdcs", "tdcs", tdcs, DemoConstants.MARKERSTYLE, DemoConstants.MarkerColor.TDC.getMarkerColor(), DemoConstants.MARKERSIZE));
        
    }
    
    public void addHitGraphs(int markerColor){
        if(!normalHits.isEmpty()) graphList.add(this.demonHitClumpSuperlayer(name+"normalHits", "normalHits", normalHits, DemoConstants.NORMALHITMARKERSTYLE, markerColor, DemoConstants.MARKERSIZE));
        if(!bgHits.isEmpty()) graphList.add(this.demonHitClumpSuperlayer(name+"bgHits", "bgHits", bgHits, DemoConstants.BGHITMARKERSTYLE, markerColor, DemoConstants.MARKERSIZE));        
        if(normalHits.size() == 0 && bgHits.size() == 0 && hits.size() != 0) graphList.add(this.demonHitClumpSuperlayer(name+"hits", "hits", hits, DemoConstants.MARKERSTYLE, markerColor, DemoConstants.MARKERSIZE));
    }
    
    public void addHitGraphs(String namePostflix, List<Hit> hits, int markerColor){
        if(!hits.isEmpty()) graphList.add(this.demonHitClumpSuperlayer(name+namePostflix, "hits", hits, DemoConstants.MARKERSTYLE, markerColor, DemoConstants.MARKERSIZE));
    }
    
    public void addURWellGraph(URWellCross uRWellCross, int markerColor){
        if(uRWellCross != null) {
            if(uRWellCross.isBothClustersNormal()) graphList.add(this.demonURWellCrossesSuperlayer(name+"uRWell", "uRWell", uRWellCross, DemoConstants.NORMALHITMARKERSTYLE, markerColor, DemoConstants.MARKERSIZE));
            else graphList.add(this.demonURWellCrossesSuperlayer(name+"uRWell", "uRWell", uRWellCross, DemoConstants.BGHITMARKERSTYLE, markerColor, DemoConstants.MARKERSIZE));
        } 
    }
    
    public void addURWellGraph(List<URWellCross> uRWellCrosses, int markerColor){
        if(!uRWellCrosses.isEmpty()){
            List<URWellCross> uRWellCrossesNormal = new ArrayList();
            List<URWellCross> uRWellCrossesBg = new ArrayList();
            for(URWellCross crs : uRWellCrosses){
                if(crs.isBothClustersNormal()) uRWellCrossesNormal.add(crs);
                else uRWellCrossesBg.add(crs);
            }
            
            if(!uRWellCrossesNormal.isEmpty()) graphList.add(this.demonURWellCrossesSuperlayer(name+"uRWell", "uRWell", uRWellCrosses, DemoConstants.NORMALHITMARKERSTYLE, markerColor, DemoConstants.MARKERSIZE));
            if(!uRWellCrossesBg.isEmpty()) graphList.add(this.demonURWellCrossesSuperlayer(name+"uRWell", "uRWell", uRWellCrosses, DemoConstants.BGHITMARKERSTYLE, markerColor, DemoConstants.MARKERSIZE));
        }
    }
    
    public void addClusterGraphs(Cluster cls, int makerColor){
        if(cls.getNormalHits() == null || cls.getBgHits() == null) cls.separateNormalBgHits();        
        if(!cls.getNormalHits().isEmpty()) graphList.add(this.demonHitClumpSuperlayer(name+"normalHitsCluster", "normalHitsCluster", cls.getNormalHits(), DemoConstants.NORMALHITMARKERSTYLE, makerColor, DemoConstants.MARKERSIZE));
        if(!cls.getBgHits().isEmpty()) graphList.add(this.demonHitClumpSuperlayer(name+"bgHitsCluster", "bgHits", cls.getBgHits(), DemoConstants.BGHITMARKERSTYLE, makerColor, DemoConstants.MARKERSIZE));        
    }    
    
    public void addClusterGraphs(List<Cluster> clsList, int makerColor){
        ArrayList<Hit> normalHitsInClsList = new ArrayList();
        ArrayList<Hit> bgHitsInClsList = new ArrayList();
        for(Cluster cls : clsList){
            if(cls.getNormalHits() == null || cls.getBgHits() == null) cls.separateNormalBgHits();
            normalHitsInClsList.addAll(cls.getNormalHits());
            bgHitsInClsList.addAll(cls.getBgHits());
        }
        if(!normalHitsInClsList.isEmpty()) graphList.add(this.demonHitClumpSuperlayer(name+"normalHitsCluster", "normalHits", normalHitsInClsList, DemoConstants.NORMALHITMARKERSTYLE, makerColor, DemoConstants.MARKERSIZE));
        if(!bgHitsInClsList.isEmpty()) graphList.add(this.demonHitClumpSuperlayer(name+"bgHitsCluster", "bgHits", bgHitsInClsList, DemoConstants.BGHITMARKERSTYLE, makerColor, DemoConstants.MARKERSIZE));        
    }
    
    
    public GraphErrors createBaseLocalSuperlayer(String name, String title, int markerStyle, int markerColor, int markerSize){
        double[] locX = {0., 0., 7., 7.};
        double[] locY = {0, 130, 0, 130};
        GraphErrors graph = new GraphErrors(name, locX, locY);
        graph.setTitle(title);
        graph.setMarkerStyle(markerStyle);
        graph.setMarkerColor(markerColor);
        graph.setMarkerColor(markerSize);
        
        return graph;
    }
    
    public GraphErrors createBaseLocalSuperlayerWithURWell(String name, String title, int markerStyle, int markerColor, int markerSize){
        double[] locX = {-2, -2, 7., 7.};
        double[] locY = {0, 130, 0, 130};
        GraphErrors graph = new GraphErrors(name, locX, locY);
        graph.setTitle(title);
        graph.setMarkerStyle(markerStyle);
        graph.setMarkerColor(markerColor);
        graph.setMarkerColor(markerSize);
        
        return graph;
    }
    
    
    public GraphErrors demonHitClumpSuperlayer(String name, String title, List<Hit> hits, int markerStyle, int markerColor, int markerSize){
        List<Double> locX = new ArrayList();
        List<Double> locY = new ArrayList();
        for(Hit hit : hits){
            locX.add((double)hit.layer());
            locY.add(hit.calcLocY());
        }
        
        GraphErrors graph = new GraphErrors(name, new DataVector(locX), new DataVector(locY));
        graph.setTitle(title);
        graph.setMarkerStyle(markerStyle);
        graph.setMarkerColor(markerColor);
        graph.setMarkerSize(markerSize);
        
        return graph;        
    }
    
    public GraphErrors demonTDCClumpSuperlayer(String name, String title, List<TDC> tdcs, int markerStyle, int markerColor, int markerSize){
        List<Double> locX = new ArrayList();
        List<Double> locY = new ArrayList();
        for(TDC tdc : tdcs){
            locX.add((double)tdc.layer());
            locY.add(tdc.calcLocY());
        }
        
        GraphErrors graph = new GraphErrors(name, new DataVector(locX), new DataVector(locY));
        graph.setTitle(title);
        graph.setMarkerStyle(markerStyle);
        graph.setMarkerColor(markerColor);
        graph.setMarkerSize(markerSize);
        
        return graph;        
    }    
    
    public GraphErrors demonURWellCrossesSuperlayer(String name, String title, URWellCross uRWellCross, int markerStyle, int markerColor, int markerSize){
        List<Double> locX = new ArrayList();
        List<Double> locY = new ArrayList();
        
        locX.add(URWellCross.getXRelativeDCSL1LC());
        locY.add(uRWellCross.getYRelativeDCSL1LC());
                
        GraphErrors graph = new GraphErrors(name, new DataVector(locX), new DataVector(locY));
        graph.setTitle(title);
        graph.setMarkerStyle(markerStyle);
        graph.setMarkerColor(markerColor);
        graph.setMarkerSize(markerSize);
        
        return graph;        
    }    
    
    public GraphErrors demonURWellCrossesSuperlayer(String name, String title, List<URWellCross> uRWellCrosses, int markerStyle, int markerColor, int markerSize){
        List<Double> locX = new ArrayList();
        List<Double> locY = new ArrayList();
        for(URWellCross crs : uRWellCrosses){
            locX.add(URWellCross.getXRelativeDCSL1LC());
            locY.add(crs.getYRelativeDCSL1LC());
        }
        
        GraphErrors graph = new GraphErrors(name, new DataVector(locX), new DataVector(locY));
        graph.setTitle(title);
        graph.setMarkerStyle(markerStyle);
        graph.setMarkerColor(markerColor);
        graph.setMarkerSize(markerSize);
        
        return graph;        
    }
    
    public List<GraphErrors> getGraphList(){
        return graphList;
    }            
}