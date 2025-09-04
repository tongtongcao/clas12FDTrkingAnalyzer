package org.clas.analysis;

import java.awt.Color;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.clas.demo.DemoConstants;

import org.jlab.groot.base.GStyle;
import org.jlab.groot.data.TDirectory;
import org.jlab.groot.graphics.EmbeddedCanvasTabbed;
import org.jlab.groot.data.IDataSet;
import org.jlab.groot.data.GraphErrors;
import org.jlab.groot.data.DataLine;
import org.jlab.groot.group.DataGroup;

import org.clas.graph.HistoGroup;
import org.clas.reader.Banks;
import org.clas.reader.LocalEvent;
import org.clas.reader.Reader;
import org.clas.demo.DemoSectorWithURWell;
import org.clas.demo.DemoSuperlayerWithURWell;
import org.clas.element.Cluster;
import org.clas.utilities.Constants;
import org.clas.element.Hit;
import org.clas.element.URWellCross;

/**
 *
 * @author Tongtong Cao
 */
public abstract class BaseAnalysis { 
    private static final Logger LOGGER = Logger.getLogger(Reader.class.getName());    
    
    protected Reader reader = null;   
    protected Reader reader1 = null;   
    protected Reader reader2 = null;   
    protected LinkedHashMap<String, HistoGroup> histoGroupMap;    
    
    protected LinkedHashMap<String, DataGroup> demoGroupMap = new LinkedHashMap(); 
    protected int countDemoCases = 0;
    
    public BaseAnalysis() {
        histoGroupMap = new LinkedHashMap<>();
    }
    
    public void initReader(Banks banks){
        this.reader = new Reader(banks);
    }
    
    public void initReader(Banks banks1, Banks banks2){
        this.reader1 = new Reader(banks1);
        this.reader2 = new Reader(banks2);
    }
    
    public void initReader(Reader reader){
        this.reader = reader;
    }
    
    public void initReader(Reader reader1, Reader reader2){
        this.reader1 = reader1;
        this.reader2 = reader2;
    }
    
    public BaseAnalysis(Banks banks) {
        this.reader = new Reader(banks);
        histoGroupMap = new LinkedHashMap<>();
    }
    
    public BaseAnalysis(Banks banks1, Banks banks2) {
        this.reader1 = new Reader(banks1);
        this.reader2 = new Reader(banks2);
        histoGroupMap = new LinkedHashMap<>();
    }
    
    public BaseAnalysis(Reader reader) {
        this.reader = reader;
        histoGroupMap = new LinkedHashMap<>();
    }
    
    public BaseAnalysis(Reader reader1, Reader reader2) {
        this.reader1 = reader1;
        this.reader2 = reader2;
        histoGroupMap = new LinkedHashMap<>();
    }
    
    public LinkedHashMap<String, HistoGroup> getHistoGroupMap(){
        return histoGroupMap;
    }
    
    public LinkedHashMap<String, DataGroup> getDemoGroupMap(){
        return demoGroupMap;
    } 
    
    public abstract void createHistoGroupMap(); 
        
    public void saveHistos(String fileName) {
        TDirectory dir = new TDirectory();
        for(String key : histoGroupMap.keySet()) {
            writeHistoGroup(dir, key);
        }
        System.out.println("Saving histograms to file " + fileName);
        dir.writeFile(fileName);
    }
    
    public void writeHistoGroup(TDirectory dir, String key) {
        String folder = "/" + key;
        dir.mkdir(folder);
        dir.cd(folder); 
        int nrows = histoGroupMap.get(key).getRows();
        int ncols = histoGroupMap.get(key).getColumns();
        int nds   = nrows*ncols;
        for(int i = 0; i < nds; i++){
            List<IDataSet> dsList = histoGroupMap.get(key).getData(i);
            for(IDataSet ds : dsList){
                dir.addDataSet(ds);                
            }          
        }
    } 
    
    public void readHistos(String fileName) {
        TDirectory dir = new TDirectory();
        dir.readFile(fileName);
        for(String key : histoGroupMap.keySet()) {
            histoGroupMap.replace(key, readHistoGroup(dir, key));
        } 
    }
    
    public HistoGroup readHistoGroup(TDirectory dir, String key) {
        int nrows = histoGroupMap.get(key).getRows();
        int ncols = histoGroupMap.get(key).getColumns();
        int nds   = nrows*ncols;
        HistoGroup newGroup = new HistoGroup(key, ncols, nrows);
        for(int i = 0; i < nds; i++){
            List<IDataSet> dsList = histoGroupMap.get(key).getData(i);            
            for(IDataSet ds : dsList){
                if(dir.getObject(key, ds.getName())!=null) {
                    newGroup.addDataSet(dir.getObject(key, ds.getName()),i);
                }
            }
        }
        return newGroup;        
    }    
        
    public EmbeddedCanvasTabbed plotHistos() {
        setStyle();
        EmbeddedCanvasTabbed canvas  = null;
        for(String key : histoGroupMap.keySet()) {
            if(canvas==null) canvas = new EmbeddedCanvasTabbed(key);
            else             canvas.addCanvas(key);
            canvas.getCanvas(key).draw(histoGroupMap.get(key));
        }
                        
        return canvas;       
    }
    
    public EmbeddedCanvasTabbed plotDemos() {
        setStyle();
        EmbeddedCanvasTabbed canvas  = null;
        for(String key : demoGroupMap.keySet()) {
            if(canvas==null) canvas = new EmbeddedCanvasTabbed(key);
            else             canvas.addCanvas(key);
            canvas.getCanvas(key).draw(demoGroupMap.get(key));
        }
                        
        return canvas;       
    }     
    
    /*
    public EmbeddedCanvasTabbed plotDemos() {
        setStyle();
        EmbeddedCanvasTabbed canvas  = null;
        for(String key : demoGroupMap.keySet()) {
            if(canvas==null) canvas = new EmbeddedCanvasTabbed(key);
            else             canvas.addCanvas(key);
            
            DataGroup group = demoGroupMap.get(key);
            int nrows = group.getRows();
            int ncols = group.getColumns();
            canvas.getCanvas(key).divide(ncols, nrows);

            int nds = nrows * ncols;
            for (int i = 0; i < nds; i++) {
                List<IDataSet> dsList = group.getData(i);          
                canvas.getCanvas(key).cd(i);
                for (int iDs = 0; iDs < dsList.size(); iDs++) {
                    IDataSet ds = dsList.get(iDs);
                    canvas.getCanvas(key).draw(ds, "same"); 
                    if(iDs > 2 && ds.getDataSize(0) >= 2){
                        //for(int iHit = 0; iHit < ds.getDataSize(0) -1 ; iHit++){
                                double xo = ds.getDataX(0);
                                if(xo > 0.5 && xo < 5.5){
                                    double yo = ds.getDataY(0);
                                    double xe = ds.getDataX(ds.getDataSize(0)-1);
                                    double ye = ds.getDataY(ds.getDataSize(0)-1);
                                    DataLine line = new DataLine(xo, yo, xe, ye);
                                    line.setLineWidth(1);
                                    canvas.getCanvas(key).draw(line);
                                }
                        //} 
                    }
                   
                }
            }

        }
                        
        return canvas;       
    }    
    */
    
    public void addDemoGroup(LocalEvent localEvent, int sector){    
        addDemoGroup(localEvent, sector, "", false);
    }
    
    public void addDemoGroup(LocalEvent localEvent, int sector, String postflix){    
        addDemoGroup(localEvent, sector, postflix, false);
    }
    
    public void addDemoGroup(LocalEvent localEvent, int sector, String postflix, boolean raw){
        if(countDemoCases >= Constants.MAXDEMOCASES) return;
        
        DataGroup grGroup = new DataGroup("E" + localEvent.getRunConfig().event() + "S" + sector + postflix, 6,1);                
                
        String str1 = "E" + localEvent.getRunConfig().event() + "S" + sector +"SP1";
        DemoSectorWithURWell demon = new DemoSectorWithURWell(str1, localEvent, sector);
        if(raw){
            if(Constants.URWELL) demon.addGraphsRawDenoisingClusteringAICandHBTBWithURWell();
            else demon.addGraphsRawDenoisingClusteringAICandHBTB();
        }
        else{
            if(Constants.URWELL) demon.addGraphsDenoisingClusteringAICandHBTBWithURWell();
            else demon.addGraphsDenoisingClusteringAICandHBTB();
        }
        for(int sl : demon.getSLGraphListMap().keySet()){
            for(GraphErrors gr : demon.getSLGraphListMap().get(sl)){
                grGroup.addDataSet(gr, sl - 1);
            }
        }

        demoGroupMap.put(grGroup.getName(), grGroup);

        countDemoCases++;
    }    
    
    public void addDemoGroup(LocalEvent localEvent1, LocalEvent localEvent2, int sector){    
        addDemoGroup(localEvent1, localEvent2, sector, "", false);
    }
    
    public void addDemoGroup(LocalEvent localEvent1, LocalEvent localEvent2, int sector, String postflix){    
        addDemoGroup(localEvent1, localEvent2, sector, postflix, false);
    }
    
    public void addDemoGroup(LocalEvent localEvent1, LocalEvent localEvent2, int sector, String postflix, boolean raw){
        if(countDemoCases >= Constants.MAXDEMOCASES) return;
        
        DataGroup grGroup = new DataGroup("E" + localEvent1.getRunConfig().event() + "S" + sector + postflix, 6,2);                
                
        String str1 = "E" + localEvent1.getRunConfig().event() + "S" + sector +"SP1";
        DemoSectorWithURWell demonSP1 = new DemoSectorWithURWell(str1, localEvent1, sector);
        if(raw){
            if(Constants.URWELL) demonSP1.addGraphsRawDenoisingClusteringAICandHBTBWithURWell();
            else demonSP1.addGraphsRawDenoisingClusteringAICandHBTB();
        }
        else{
            if(Constants.URWELL) demonSP1.addGraphsDenoisingClusteringAICandHBTBWithURWell();
            else demonSP1.addGraphsDenoisingClusteringAICandHBTB();
        }
        for(int sl : demonSP1.getSLGraphListMap().keySet()){
            for(GraphErrors gr : demonSP1.getSLGraphListMap().get(sl)){
                grGroup.addDataSet(gr, sl - 1);
            }
        }

        String str2 = "E" + localEvent2.getRunConfig().event() + "S" + sector + "SL" + "SP2";
        DemoSectorWithURWell demonSP2 = new DemoSectorWithURWell(str2, localEvent2, sector);
        if(raw){
            if(Constants.URWELL) demonSP2.addGraphsRawDenoisingClusteringAICandHBTBWithURWell();
            else demonSP2.addGraphsRawDenoisingClusteringAICandHBTB();
        }
        else{
            if(Constants.URWELL) demonSP2.addGraphsDenoisingClusteringAICandHBTBWithURWell();
            else demonSP2.addGraphsDenoisingClusteringAICandHBTB();
        }
        for(int sl : demonSP2.getSLGraphListMap().keySet()){
            for(GraphErrors gr : demonSP2.getSLGraphListMap().get(sl)){
                grGroup.addDataSet(gr, sl+5);
            }
        }

        demoGroupMap.put(grGroup.getName(), grGroup);

        countDemoCases++;
    }


    public void addDemoGroupLoopClusters(LocalEvent localEvent1, LocalEvent localEvent2, int sector, String postflix){
        if(countDemoCases >= Constants.MAXDEMOCASES) return;
        List<Hit> hits1SL1 = new ArrayList();
        List<Hit> hits2SL1 = new ArrayList();
        for(Hit hit : localEvent1.getHits()){
            if(hit.sector() == sector && hit.superlayer() == 1) hits1SL1.add(hit);
        }
        for(Hit hit : localEvent2.getHits()){
            if(hit.sector() == sector && hit.superlayer() == 1) hits2SL1.add(hit);
        }
        
        List<URWellCross> uRWellCrosses1 = new ArrayList();
        List<URWellCross> uRWellCrosses2 = new ArrayList();
        for(URWellCross crs : localEvent1.getURWellCrosses()){
            if(crs.sector() == sector) uRWellCrosses1.add(crs);
        }
        for(URWellCross crs : localEvent2.getURWellCrosses()){
            if(crs.sector() == sector) uRWellCrosses2.add(crs);
        }        
        
        
        List<Cluster> clusters1 = localEvent1.getClusters();
        List<Cluster> clusters2 = localEvent2.getClusters();
        
        List<Cluster> clusters1SL1 = new ArrayList();
        List<Cluster> clusters2SL1 = new ArrayList();
        
        for(Cluster cls1: clusters1){
            if(cls1.sector() == sector && cls1.superlayer() == 1) clusters1SL1.add(cls1);
        }        
        for(Cluster cls2: clusters2){
            if(cls2.sector() == sector && cls2.superlayer() == 1) clusters2SL1.add(cls2);
        }
        
        int nColumns = clusters1SL1.size() > clusters2SL1.size() ? clusters1SL1.size() : clusters2SL1.size();
        if(nColumns < 6) nColumns = 6;
        DataGroup grGroup = new DataGroup("E" + localEvent1.getRunConfig().event() + "S" + sector + postflix, nColumns ,2 );  
        
        for(int i = 0; i < clusters1SL1.size(); i++){
            Cluster cls1 = clusters1SL1.get(i);
            String str = "cluster" + cls1.id();
            DemoSuperlayerWithURWell demo = new DemoSuperlayerWithURWell(str, str, hits1SL1, uRWellCrosses1);
            demo.addBaseLocalSuperlayerWithURWell();
            demo.addGraphsDenoisingWithURWell();
            demo.addClusterGraphs(cls1, DemoConstants.MarkerColor.CLUSTERING.getMarkerColor());
            demo.addURWellGraph(cls1.getMatchedURWellCrosses(), DemoConstants.MarkerColor.CLUSTERING.getMarkerColor());
            if(cls1.getMatchedURWellCrossIds()[0] != -1 && cls1.getMatchedURWellCrossIds()[1] == -1) 
                demo.getGraphList().get(0).setTitle("cls" + cls1.id() + " size" + cls1.size() + " u1Id" + cls1.getMatchedURWellCrossIds()[0]);
            else if(cls1.getMatchedURWellCrossIds()[0] == -1 && cls1.getMatchedURWellCrossIds()[1] != -1) 
                demo.getGraphList().get(0).setTitle("cls" + cls1.id() + " size" + cls1.size() + " u2Id" + cls1.getMatchedURWellCrossIds()[1]);
            else if(cls1.getMatchedURWellCrossIds()[0] != -1 && cls1.getMatchedURWellCrossIds()[1] != -1) 
                demo.getGraphList().get(0).setTitle("cls" + cls1.id() + " size" + cls1.size() + " uIds" + cls1.getMatchedURWellCrossIds()[0] + "," + cls1.getMatchedURWellCrossIds()[1]);
            else demo.getGraphList().get(0).setTitle("cluster" + cls1.id() + " size" + cls1.size());
            for(GraphErrors gr : demo.getGraphList()){
                grGroup.addDataSet(gr, i);
            }           
        }
        
        for(int i = 0; i < clusters2SL1.size(); i++){
            Cluster cls2 = clusters2SL1.get(i);
            String str = "cluster" + cls2.id();
            DemoSuperlayerWithURWell demo = new DemoSuperlayerWithURWell(str, str, hits2SL1, uRWellCrosses2);
            demo.addBaseLocalSuperlayerWithURWell();
            demo.addGraphsDenoisingWithURWell();
            demo.addClusterGraphs(cls2, DemoConstants.MarkerColor.CLUSTERING.getMarkerColor());
            demo.addURWellGraph(cls2.getMatchedURWellCrosses(), DemoConstants.MarkerColor.CLUSTERING.getMarkerColor());
            if(cls2.getMatchedURWellCrossIds()[0] != -1 && cls2.getMatchedURWellCrossIds()[1] == -1) 
                demo.getGraphList().get(0).setTitle("cls" + cls2.id() + " size" + cls2.size() + " u1Id" + cls2.getMatchedURWellCrossIds()[0]);
            else if(cls2.getMatchedURWellCrossIds()[0] == -1 && cls2.getMatchedURWellCrossIds()[1] != -1) 
                demo.getGraphList().get(0).setTitle("cls" + cls2.id() + " size" + cls2.size() + " u2Id" + cls2.getMatchedURWellCrossIds()[1]);
            else if(cls2.getMatchedURWellCrossIds()[0] != -1 && cls2.getMatchedURWellCrossIds()[1] != -1) 
                demo.getGraphList().get(0).setTitle("cls" + cls2.id() + " size" + cls2.size() + " uIds" + cls2.getMatchedURWellCrossIds()[0] + "," + cls2.getMatchedURWellCrossIds()[1]);
            else demo.getGraphList().get(0).setTitle("cluster" + cls2.id() + " size" + cls2.size());
            for(GraphErrors gr : demo.getGraphList()){
                grGroup.addDataSet(gr, i + nColumns);
            }           
        } 
        
 
        demoGroupMap.put(grGroup.getName(), grGroup);

        countDemoCases++;
    }
    
    
    
    public void setStyle(){      
        GStyle.setCanvasBackgroundColor(Color.lightGray);
        GStyle.getH1FAttributes().setOptStat("r");
        GStyle.getH1FAttributes().setLineWidth(2);
        //GStyle.getH2FAttributes().setDrawOptions("colz");
        GStyle.setGraphicsFrameLineWidth(1);
        GStyle.getAxisAttributesX().setTitleFontSize(18);
        GStyle.getAxisAttributesX().setLabelFontSize(14);
        GStyle.getAxisAttributesY().setTitleFontSize(18);
        GStyle.getAxisAttributesY().setLabelFontSize(14);
        GStyle.getAxisAttributesZ().setLabelFontSize(14);
        GStyle.getAxisAttributesX().setLabelFontName("Arial");
        GStyle.getAxisAttributesY().setLabelFontName("Arial");
        GStyle.getAxisAttributesZ().setLabelFontName("Arial");
        GStyle.getAxisAttributesX().setTitleFontName("Arial");
        GStyle.getAxisAttributesY().setTitleFontName("Arial");
        GStyle.getAxisAttributesZ().setTitleFontName("Arial");
        GStyle.getAxisAttributesZ().setLog(true);
    }    
} 