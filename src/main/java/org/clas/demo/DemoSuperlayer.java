package org.clas.demo;

import java.util.List;

import org.clas.element.Hit;
import org.clas.element.TDC;
import org.clas.demo.DemoConstants.MarkerColor;
import org.clas.element.Cluster;

/**
 *
 * @author Tongtong
 */

public class DemoSuperlayer extends DemoBase{            
    private List<Cluster> clsListClustering;
    private List<Cluster> clsListAICand;
    private List<Cluster> clsListHB;
    private List<Cluster> clsListTB;
    
    /**
     * @param hits hits after SNR, AI-denoising, timing-cut, i.e., hits input into clustering
     * @param clsClustering clusters from clustering     
    */    
    public DemoSuperlayer(String name, String title, List<Hit> hits){
        super(name, title, hits);
        this.clsListClustering = clsListClustering;
    }
    
    /**
     * @param hits hits after SNR, AI-denoising, timing-cut, i.e., hits input into clustering
     * @param clsClustering clusters from clustering     
    */    
    public DemoSuperlayer(String name, String title, List<Hit> hits, List<Cluster> clsListClustering){
        super(name, title, hits);
        this.clsListClustering = clsListClustering;
    }
    
    /**
     * @param tdcs hits from DC::tdc bank
     * @param hits hits after SNR, AI-denoising, timing-cut, i.e., hits input into clustering
     * @param clsClustering clusters from clustering     
    */    
    public DemoSuperlayer(String name, String title, List<TDC> tdcs, List<Hit> hits, List<Cluster> clsListClustering){
        super(name, title, tdcs, hits);
        this.clsListClustering = clsListClustering;
    }
    
    /**
     * @param tdcs hits from DC::tdc bank
     * @param hits hits after SNR, AI-denoising, timing-cut, i.e., hits input into clustering
     * @param clsClustering clusters from clustering  
     * @param clsListHB clusters in HB tracks 
     * @param clsListTB clusters in TB tracks 
    */    
    public DemoSuperlayer(String name, String title, List<TDC> tdcs, List<Hit> hits, List<Cluster> clsListClustering, List<Cluster> clsListHB, List<Cluster> clsListTB){
        super(name, title, tdcs, hits);
        this.clsListClustering = clsListClustering;
        this.clsListHB = clsListHB;
        this.clsListTB = clsListTB;
    }
    
    /**
     * @param tdcs hits from DC::tdc bank
     * @param hits hits after SNR, AI-denoising, timing-cut, i.e., hits input into clustering
     * @param clsClustering clusters from clustering  
     * @param clsListAICand clusters from AI candidates
     * @param clsListHB clusters in HB tracks 
     * @param clsListTB clusters in TB tracks 
    */    
    public DemoSuperlayer(String name, String title, List<TDC> tdcs, List<Hit> hits, List<Cluster> clsListClustering, List<Cluster> clsListAICand, List<Cluster> clsListHB, List<Cluster> clsListTB){
        super(name, title, tdcs, hits);
        this.clsListClustering = clsListClustering;
        this.clsListAICand = clsListAICand;
        this.clsListHB = clsListHB;
        this.clsListTB = clsListTB;
    }  
    
    public void addGraphsDenoising(){
        addHitGraphs(MarkerColor.DENOISING.getMarkerColor());
    }
    
    public void addGraphsDenoisingClustering(){
        addHitGraphs(MarkerColor.DENOISING.getMarkerColor());
        addClusterGraphs(clsListClustering, MarkerColor.CLUSTERING.getMarkerColor());
    }
    
    public void addGraphsRawDenoisingClustering(){
        addTDCGraphs();
        addHitGraphs(MarkerColor.DENOISING.getMarkerColor());
        addClusterGraphs(clsListClustering, MarkerColor.CLUSTERING.getMarkerColor());
    }
    
    public void addGraphsDenoisingClusteringHBTB(){
        addHitGraphs(MarkerColor.DENOISING.getMarkerColor());
        addClusterGraphs(clsListClustering, MarkerColor.CLUSTERING.getMarkerColor());
        addClusterGraphs(clsListHB, MarkerColor.HBTRK.getMarkerColor());
        addClusterGraphs(clsListTB, MarkerColor.TBTRK.getMarkerColor());
    }
    
    public void addGraphsRawDenoisingClusteringHBTB(){
        addTDCGraphs();
        addHitGraphs(MarkerColor.DENOISING.getMarkerColor());
        addClusterGraphs(clsListClustering, MarkerColor.CLUSTERING.getMarkerColor());
        addClusterGraphs(clsListHB, MarkerColor.HBTRK.getMarkerColor());
        addClusterGraphs(clsListTB, MarkerColor.TBTRK.getMarkerColor());
    }
    
    public void addGraphsDenoisingClusteringAICandHBTB(){
        addHitGraphs(MarkerColor.DENOISING.getMarkerColor());
        addClusterGraphs(clsListClustering, MarkerColor.CLUSTERING.getMarkerColor());
        addClusterGraphs(clsListAICand, MarkerColor.AICAND.getMarkerColor());
        addClusterGraphs(clsListHB, MarkerColor.HBTRK.getMarkerColor());
        addClusterGraphs(clsListTB, MarkerColor.TBTRK.getMarkerColor());
    }
    
    public void addGraphsRawDenoisingClusteringAICandHBTB(){
        addTDCGraphs();
        addHitGraphs(MarkerColor.DENOISING.getMarkerColor());
        addClusterGraphs(clsListClustering, MarkerColor.CLUSTERING.getMarkerColor());
        addClusterGraphs(clsListAICand, MarkerColor.AICAND.getMarkerColor());
        addClusterGraphs(clsListHB, MarkerColor.HBTRK.getMarkerColor());
        addClusterGraphs(clsListTB, MarkerColor.TBTRK.getMarkerColor());
    }
                                  
}