package org.clas.demo;

import java.util.List;
import java.util.ArrayList;

import org.clas.element.Hit;
import org.clas.element.TDC;
import org.clas.element.Cluster;
import org.clas.element.URWellCross;
import org.clas.reader.LocalEvent;

/**
 *
 * @author Tongtong
 */

public class DemoSuperlayerWithURWell extends DemoSuperlayer{  
    private List<URWellCross> uRWellCrosses = new ArrayList();
    private List<URWellCross> uRWellCrossesClustering = new ArrayList();
    private List<URWellCross> uRWellCrossesHB = new ArrayList();
    private List<URWellCross> uRWellCrossesTB = new ArrayList();        
    
    /**
     * @param hits hits after SNR, AI-denoising, timing-cut, i.e., hits input into clustering
     * @param clsClustering clusters from clustering     
    */    
    public DemoSuperlayerWithURWell(String name, String title, List<Hit> hits, List<URWellCross> uRWellCrosses){
        super(name, title, hits);
        this.uRWellCrosses =uRWellCrosses;
        makeHBTBURWellCrossList();
    }
    
    /**
     * @param hits hits after SNR, AI-denoising, timing-cut, i.e., hits input into clustering
     * @param clsClustering clusters from clustering     
    */    
    public DemoSuperlayerWithURWell(String name, String title, List<Hit> hits, List<Cluster> clsListClustering, List<URWellCross> uRWellCrosses){
        super(name, title, hits, clsListClustering);
        this.uRWellCrosses =uRWellCrosses;
        makeHBTBURWellCrossList();
    }
    
    /**
     * @param tdcs hits from DC::tdc bank
     * @param hits hits after SNR, AI-denoising, timing-cut, i.e., hits input into clustering
     * @param clsClustering clusters from clustering     
    */    
    public DemoSuperlayerWithURWell(String name, String title, List<TDC> tdcs, List<Hit> hits, List<Cluster> clsListClustering, List<URWellCross> uRWellCrosses){
        super(name, title, tdcs, hits, clsListClustering);
        this.uRWellCrosses =uRWellCrosses;
        makeHBTBURWellCrossList();
    }
    
    /**
     * @param tdcs hits from DC::tdc bank
     * @param hits hits after SNR, AI-denoising, timing-cut, i.e., hits input into clustering
     * @param clsClustering clusters from clustering  
     * @param clsListHB clusters in HB tracks 
     * @param clsListTB clusters in TB tracks 
    */    
    public DemoSuperlayerWithURWell(String name, String title, List<TDC> tdcs, List<Hit> hits, List<Cluster> clsListClustering, List<Cluster> clsListHB, List<Cluster> clsListTB, List<URWellCross> uRWellCrosses){
        super(name, title, tdcs, hits, clsListClustering, clsListHB, clsListTB);
        this.uRWellCrosses =uRWellCrosses;
        makeHBTBURWellCrossList();
    }
    
    /**
     * @param tdcs hits from DC::tdc bank
     * @param hits hits after SNR, AI-denoising, timing-cut, i.e., hits input into clustering
     * @param clsClustering clusters from clustering  
     * @param clsListAICand clusters from AI candidates
     * @param clsListHB clusters in HB tracks 
     * @param clsListTB clusters in TB tracks 
    */    
    public DemoSuperlayerWithURWell(String name, String title, List<TDC> tdcs, List<Hit> hits, List<Cluster> clsListClustering, List<Cluster> clsListAICand, List<Cluster> clsListHB, List<Cluster> clsListTB, List<URWellCross> uRWellCrosses){
        super(name, title, tdcs, hits, clsListClustering, clsListAICand, clsListHB, clsListTB);
        this.uRWellCrosses =uRWellCrosses;
        makeHBTBURWellCrossList();
    } 
    
    private void makeHBTBURWellCrossList(){
        for(URWellCross crs : uRWellCrosses){
            if(crs.isUsedClustering()) uRWellCrossesClustering.add(crs);
            if(crs.isUsedHB()) uRWellCrossesHB.add(crs);
            if(crs.isUsedTB()) uRWellCrossesTB.add(crs);
        }
    }
    
    public void addGraphsDenoisingWithURWell(){        
        addGraphsDenoising();
        addURWellGraph(uRWellCrosses, DemoConstants.MarkerColor.DENOISING.getMarkerColor()); 
    }
    
    public void addGraphsDenoisingClusteringWithURWell(){        
        addGraphsDenoisingClustering();
        addURWellGraph(uRWellCrosses, DemoConstants.MarkerColor.DENOISING.getMarkerColor()); 
        addURWellGraph(uRWellCrossesClustering, DemoConstants.MarkerColor.CLUSTERING.getMarkerColor());  
    }
    
    public void addGraphsRawDenoisingClusteringWithURWell(){
        addGraphsRawDenoisingClustering();
        addURWellGraph(uRWellCrosses, DemoConstants.MarkerColor.DENOISING.getMarkerColor()); 
        addURWellGraph(uRWellCrossesClustering, DemoConstants.MarkerColor.CLUSTERING.getMarkerColor());  
    }
    
    public void addGraphsDenoisingClusteringHBTBWithURWell(){
        addGraphsDenoisingClusteringHBTB();
        addURWellGraph(uRWellCrosses, DemoConstants.MarkerColor.DENOISING.getMarkerColor());  
        addURWellGraph(uRWellCrossesClustering, DemoConstants.MarkerColor.CLUSTERING.getMarkerColor());  
        addURWellGraph(uRWellCrossesHB, DemoConstants.MarkerColor.HBTRK.getMarkerColor());  
        addURWellGraph(uRWellCrossesTB, DemoConstants.MarkerColor.TBTRK.getMarkerColor());  
    }
    
    public void addGraphsRawDenoisingClusteringHBTBWithURWell(){
        addGraphsRawDenoisingClusteringHBTB();
        addURWellGraph(uRWellCrosses, DemoConstants.MarkerColor.DENOISING.getMarkerColor()); 
        addURWellGraph(uRWellCrossesClustering, DemoConstants.MarkerColor.CLUSTERING.getMarkerColor());  
        addURWellGraph(uRWellCrossesHB, DemoConstants.MarkerColor.HBTRK.getMarkerColor());  
        addURWellGraph(uRWellCrossesTB, DemoConstants.MarkerColor.TBTRK.getMarkerColor());  
    }
    
    public void addGraphsDenoisingClusteringAICandHBTBWithURWell(){
        addGraphsDenoisingClusteringAICandHBTB();
        addURWellGraph(uRWellCrosses, DemoConstants.MarkerColor.DENOISING.getMarkerColor()); 
        addURWellGraph(uRWellCrossesClustering, DemoConstants.MarkerColor.CLUSTERING.getMarkerColor());  
        addURWellGraph(uRWellCrossesHB, DemoConstants.MarkerColor.HBTRK.getMarkerColor());  
        addURWellGraph(uRWellCrossesTB, DemoConstants.MarkerColor.TBTRK.getMarkerColor());  
    }
    
    public void addGraphsRawDenoisingClusteringAICandHBTBWithURWell(){
        addGraphsRawDenoisingClusteringAICandHBTB();
        addURWellGraph(uRWellCrosses, DemoConstants.MarkerColor.DENOISING.getMarkerColor());  
        addURWellGraph(uRWellCrossesClustering, DemoConstants.MarkerColor.CLUSTERING.getMarkerColor());  
        addURWellGraph(uRWellCrossesHB, DemoConstants.MarkerColor.HBTRK.getMarkerColor());  
        addURWellGraph(uRWellCrossesTB, DemoConstants.MarkerColor.TBTRK.getMarkerColor());  
    }
                                  
}