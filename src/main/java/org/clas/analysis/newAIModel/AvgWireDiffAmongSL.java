package org.clas.analysis.newAIModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Random;
import java.util.Collections;
import javax.swing.JFrame;

import org.jlab.geom.prim.Point3D;
import org.jlab.geom.prim.Line3D;
import org.jlab.geom.prim.Vector3D;

import org.jlab.groot.data.H1F;
import org.jlab.groot.data.H2F;
import org.jlab.groot.graphics.EmbeddedCanvasTabbed;
import org.jlab.jnp.hipo4.data.Event;
import org.jlab.jnp.hipo4.data.SchemaFactory;
import org.jlab.jnp.hipo4.io.HipoReader;
import org.jlab.utils.benchmark.ProgressPrintout;
import org.jlab.utils.options.OptionParser;
import org.jlab.groot.math.F1D;

import org.clas.utilities.Constants;
import org.clas.element.Track;
import org.clas.element.Cluster;
import org.clas.element.Hit;
import org.clas.element.URWellHit;
import org.clas.element.URWellCluster;
import org.clas.element.URWellCross;
import org.clas.element.MCParticle;
import org.clas.graph.HistoGroup;
import org.clas.physicsEvent.BasePhysicsEvent;
import org.clas.physicsEvent.EventForLumiScan;
import org.clas.analysis.BaseAnalysis;
import org.clas.demo.DemoSector;
import org.clas.element.RunConfig;
import org.clas.element.TDC;
import org.clas.reader.Banks;
import org.clas.reader.LocalEvent;
import org.clas.fit.ClusterFitLC;
import org.jlab.groot.data.GraphErrors;
import org.jlab.groot.group.DataGroup;
import org.clas.demo.DemoBase;

/**
 *
 * @author Tongtong Cao
 */
public class AvgWireDiffAmongSL extends BaseAnalysis{ 
    
    public AvgWireDiffAmongSL(){}
    
    @Override
    public void createHistoGroupMap(){
        HistoGroup histoGroupAvgWireNeighboredSL = new HistoGroup("avgWireNeighboredSL", 2, 3);                        
        for (int i = 0; i < 5; i++) {
            H1F h1_avgWireNeighboredSL = new H1F("avgWireNeighboredSL between SL" + Integer.toString(i + 1) + Integer.toString(i + 2),
                    "avgWireNeighboredSL between SL" + Integer.toString(i + 1) + Integer.toString(i + 2), 100, -60, 60);
            h1_avgWireNeighboredSL.setTitleX("Diff. of avgWire");
            h1_avgWireNeighboredSL.setTitleY("Counts");
            histoGroupAvgWireNeighboredSL.addDataSet(h1_avgWireNeighboredSL, i); 
        }        
        histoGroupMap.put(histoGroupAvgWireNeighboredSL.getName(), histoGroupAvgWireNeighboredSL);  
        
        HistoGroup histoGroupAvgWireCross1SL = new HistoGroup("avgWireCross1SL", 2, 3);
        for (int i = 0; i < 4; i++) {
            H1F h1_avgWireCross1SL = new H1F("avgWireCross1SL between SL" + Integer.toString(i + 1) + Integer.toString(i + 3),
                    "avgWireCross1SL between SL" + Integer.toString(i + 1) + Integer.toString(i + 3), 100, -60, 60);
            h1_avgWireCross1SL.setTitleX("Diff. of avgWire");
            h1_avgWireCross1SL.setTitleY("Counts");
            histoGroupAvgWireCross1SL.addDataSet(h1_avgWireCross1SL, i); 
        }        
        histoGroupMap.put(histoGroupAvgWireCross1SL.getName(), histoGroupAvgWireCross1SL);
        
        HistoGroup histoGroupAvgWireCross2SL = new HistoGroup("avgWireCross2SL", 2, 3);
        for (int i = 0; i < 3; i++) {
            H1F h1_avgWireCross2SL = new H1F("avgWireCross2SL between SL" + Integer.toString(i + 1) + Integer.toString(i + 4),
                    "avgWireCross2SL between SL" + Integer.toString(i + 1) + Integer.toString(i + 4), 100, -60, 60);
            h1_avgWireCross2SL.setTitleX("Diff. of avgWire");
            h1_avgWireCross2SL.setTitleY("Counts");
            histoGroupAvgWireCross2SL.addDataSet(h1_avgWireCross2SL, i); 
        }        
        histoGroupMap.put(histoGroupAvgWireCross2SL.getName(), histoGroupAvgWireCross2SL);
        
        HistoGroup histoGroupAvgWireCross3SL = new HistoGroup("avgWireCross3SL", 2, 3);
        for (int i = 0; i < 2; i++) {
            H1F h1_avgWireCross3SL = new H1F("avgWireCross3SL between SL" + Integer.toString(i + 1) + Integer.toString(i + 5),
                    "avgWireCross3SL between SL" + Integer.toString(i + 1) + Integer.toString(i + 5), 100, -60, 60);
            h1_avgWireCross3SL.setTitleX("Diff. of avgWire");
            h1_avgWireCross3SL.setTitleY("Counts");
            histoGroupAvgWireCross3SL.addDataSet(h1_avgWireCross3SL, i); 
        }        
        histoGroupMap.put(histoGroupAvgWireCross3SL.getName(), histoGroupAvgWireCross3SL);
        
        HistoGroup histoGroupAvgWireCross4SL = new HistoGroup("avgWireCross4SL", 2, 3);
        for (int i = 0; i < 1; i++) {
            H1F h1_avgWireCross4SL = new H1F("avgWireCross4SL between SL" + Integer.toString(i + 1) + Integer.toString(i + 6),
                    "avgWireCross4SL between SL" + Integer.toString(i + 1) + Integer.toString(i + 6), 100, -60, 60);
            h1_avgWireCross4SL.setTitleX("Diff. of avgWire");
            h1_avgWireCross4SL.setTitleY("Counts");
            histoGroupAvgWireCross4SL.addDataSet(h1_avgWireCross4SL, i); 
        }        
        histoGroupMap.put(histoGroupAvgWireCross4SL.getName(), histoGroupAvgWireCross4SL); 
        
        HistoGroup histoGroupNumTracks = new HistoGroup("numTracks", 2, 3);
        for (int i = 0; i < 6; i++) {
            H1F h1_numTracks = new H1F("numTracks for sector" + Integer.toString(i + 1), "numTracks for sector" + Integer.toString(i + 1), 6, -0.5, 5.5);
            h1_numTracks.setTitleX("# of tracks");
            h1_numTracks.setTitleY("Counts");
            histoGroupNumTracks.addDataSet(h1_numTracks, i);
        }
        histoGroupMap.put(histoGroupNumTracks.getName(), histoGroupNumTracks);
        
        HistoGroup histoGroupNumClusters = new HistoGroup("numClusters", 2, 3);
        for (int i = 0; i < 6; i++) {
            H1F h1_numClusters = new H1F("numClusters for sector" + Integer.toString(i + 1), "numClusters for sector" + Integer.toString(i + 1), 50, -0.5, 49.5);
            h1_numClusters.setTitleX("# of clusters");
            h1_numClusters.setTitleY("Counts");
            histoGroupNumClusters.addDataSet(h1_numClusters, i);
        }
        histoGroupMap.put(histoGroupNumClusters.getName(), histoGroupNumClusters);
        
        HistoGroup histoGroupAvgWireDiffClustersWithSharedHits= new HistoGroup("avgWireDiffWithSharedHits", 1, 1);
        H1F h1_avgWireDiffWithSharedHits = new H1F("avgWireDiffWithSharedHits", "avgWireDiffWithSharedHits", 100, -3, 3);
        h1_avgWireDiffWithSharedHits.setTitleX("avgWireDiff");
        h1_avgWireDiffWithSharedHits.setTitleY("Counts");
        histoGroupAvgWireDiffClustersWithSharedHits.addDataSet(h1_avgWireDiffWithSharedHits, 0);  
        histoGroupMap.put(histoGroupAvgWireDiffClustersWithSharedHits.getName(), histoGroupAvgWireDiffClustersWithSharedHits); 
        
        HistoGroup histoGroupSlopeDiffClustersWithSharedHits= new HistoGroup("slopeDiffWithSharedHits", 1, 1);
        H1F h1_slopeDiffWithSharedHits = new H1F("slopeDiffWithSharedHits", "slopeDiffWithSharedHits", 100, -2, 2);
        h1_slopeDiffWithSharedHits.setTitleX("slopeDiff");
        h1_slopeDiffWithSharedHits.setTitleY("Counts");
        histoGroupSlopeDiffClustersWithSharedHits.addDataSet(h1_slopeDiffWithSharedHits, 0);  
        histoGroupMap.put(histoGroupSlopeDiffClustersWithSharedHits.getName(), histoGroupSlopeDiffClustersWithSharedHits); 
        
        HistoGroup histoGroupAvgWireDiffVsSlopeDiffClustersWithSharedHits= new HistoGroup("avgWireDiffVsSlopeDiffClustersWithSharedHits", 1, 1);
        H2F h2_avgWireDiffVsSlopeDiffWithSharedHits = new H2F("avgWireDiffVsSlopeDiffWithSharedHits", "avgWireDiffVsSlopeDiffWithSharedHits", 100, -100, 100, 100, -4, 4);
        h2_avgWireDiffVsSlopeDiffWithSharedHits.setTitleX("avgWireDiff");
        h2_avgWireDiffVsSlopeDiffWithSharedHits.setTitleY("slopeDiff");
        histoGroupAvgWireDiffVsSlopeDiffClustersWithSharedHits.addDataSet(h2_avgWireDiffVsSlopeDiffWithSharedHits, 0);  
        histoGroupMap.put(histoGroupAvgWireDiffVsSlopeDiffClustersWithSharedHits.getName(), histoGroupAvgWireDiffVsSlopeDiffClustersWithSharedHits); 
        
    }
             
    public void processEvent(Event event){        
        //Read banks
        LocalEvent localEvent = new LocalEvent(reader, event, Constants.AITB, true);
        
        HistoGroup histoGroupAvgWireNeighboredSL = histoGroupMap.get("avgWireNeighboredSL");
        HistoGroup histoGroupAvgWireCross1SL = histoGroupMap.get("avgWireCross1SL");
        HistoGroup histoGroupAvgWireCross2SL = histoGroupMap.get("avgWireCross2SL");
        HistoGroup histoGroupAvgWireCross3SL = histoGroupMap.get("avgWireCross3SL");
        HistoGroup histoGroupAvgWireCross4SL = histoGroupMap.get("avgWireCross4SL");
        
        
        
        for(Track trk : localEvent.getTracksTB()){
            if(trk.isValid() && trk.getClusters().size() == 6){
                List<Cluster> clusters = trk.getClusters();
                for(int i = 0; i < 5; i++){
                    Cluster cls1 = clusters.get(i);
                    Cluster cls2 = clusters.get(i+1);
                    histoGroupAvgWireNeighboredSL.getH1F("avgWireNeighboredSL between SL" + Integer.toString(i + 1) + Integer.toString(i + 2)).fill(cls1.avgWire() - cls2.avgWire());
                }
                
                for(int i = 0; i < 4; i++){
                    Cluster cls1 = clusters.get(i);
                    Cluster cls2 = clusters.get(i+2);
                    histoGroupAvgWireCross1SL.getH1F("avgWireCross1SL between SL" + Integer.toString(i + 1) + Integer.toString(i + 3)).fill(cls1.avgWire() - cls2.avgWire());
                }
                
                for(int i = 0; i < 3; i++){
                    Cluster cls1 = clusters.get(i);
                    Cluster cls2 = clusters.get(i+3);
                    histoGroupAvgWireCross2SL.getH1F("avgWireCross2SL between SL" + Integer.toString(i + 1) + Integer.toString(i + 4)).fill(cls1.avgWire() - cls2.avgWire());
                }

                for(int i = 0; i < 2; i++){
                    Cluster cls1 = clusters.get(i);
                    Cluster cls2 = clusters.get(i+4);
                    histoGroupAvgWireCross3SL.getH1F("avgWireCross3SL between SL" + Integer.toString(i + 1) + Integer.toString(i + 5)).fill(cls1.avgWire() - cls2.avgWire());
                }

                for(int i = 0; i < 1; i++){
                    Cluster cls1 = clusters.get(i);
                    Cluster cls2 = clusters.get(i+5);
                    histoGroupAvgWireCross4SL.getH1F("avgWireCross4SL between SL" + Integer.toString(i + 1) + Integer.toString(i + 6)).fill(cls1.avgWire() - cls2.avgWire());
                }                
            }
        }
        
       
        HistoGroup histoGroupNumTracks = histoGroupMap.get("numTracks");
        
        int[] counts = new int[6];
        for(Track trk : localEvent.getTracksTB()){            
            if(trk.isValid()) counts[trk.sector()-1]++; 
        }
        
        for(int i = 0; i < 6; i++){
            histoGroupNumTracks.getH1F("numTracks for sector" + Integer.toString(i+1)).fill(counts[i]);
        }
        
        HistoGroup histoGroupNumClusters = histoGroupMap.get("numClusters");
        
        int[] countClusters = new int[6];
        for(Cluster cls : localEvent.getClusters()){            
            countClusters[cls.sector()-1]++; 
        }
        
        for(int i = 0; i < 6; i++){
            histoGroupNumClusters.getH1F("numClusters for sector" + Integer.toString(i+1)).fill(countClusters[i]);
        }
        
        HistoGroup histoGroupAvgWireDiffClustersWithSharedHits = histoGroupMap.get("avgWireDiffWithSharedHits");
        HistoGroup histoGroupSlopeDiffClustersWithSharedHits = histoGroupMap.get("slopeDiffWithSharedHits");
        HistoGroup histoGroupAvgWireDiffVsSlopeDiffClustersWithSharedHits = histoGroupMap.get("avgWireDiffVsSlopeDiffClustersWithSharedHits");
        List<Cluster> clusters = localEvent.getClusters();
        Collections.shuffle(clusters);
        int numClusters = clusters.size();
        for(int i = 0; i < numClusters; i++){
            Cluster cls1 = clusters.get(i);
            for(int j = i+1; j < numClusters; j++){
                Cluster cls2 = clusters.get(j);
                if(cls1.sector() == cls2.sector() && cls1.superlayer() == cls2.superlayer() && cls1.numMatchedHits(cls2) > -1){
                    histoGroupAvgWireDiffClustersWithSharedHits.getH1F("avgWireDiffWithSharedHits").fill(cls1.avgWire() - cls2.avgWire());
                    histoGroupSlopeDiffClustersWithSharedHits.getH1F("slopeDiffWithSharedHits").fill(cls1.fitSlope() - cls2.fitSlope());
                    histoGroupAvgWireDiffVsSlopeDiffClustersWithSharedHits.getH2F("avgWireDiffVsSlopeDiffWithSharedHits").fill(cls1.avgWire() - cls2.avgWire(), cls1.fitSlope() - cls2.fitSlope());
                }
            }
        }
        
    }
    
   
                            
    public static void main(String[] args){
        OptionParser parser = new OptionParser("avgWireDiffAmongSL");
        parser.setRequiresInputList(false);
        // valid options for event-base analysis
        parser.addOption("-o"          ,"",     "output file name prefix");
        parser.addOption("-n"          ,"-1",   "maximum number of events to process");        
        parser.addOption("-plot"       ,"1",    "display histograms (0/1)");
                
        // histogram based analysis
        parser.addOption("-histo"      ,"0",    "read histogram file (0/1)");
        
        parser.parse(args);
        
        String namePrefix  = parser.getOption("-o").stringValue(); 
        int   maxEvents  = parser.getOption("-n").intValue();    
        boolean displayPlots   = (parser.getOption("-plot").intValue()!=0);
        boolean readHistos   = (parser.getOption("-histo").intValue()!=0); 
        
        List<String> inputList = parser.getInputList();
        if(inputList.isEmpty()==true){
            parser.printUsage();
            inputList.add("/Users/caot/research/clas12/data/mc/uRWELL/upgradeTrackingWithuRWELL/rga-sidis-uRWell-2R_denoise/0nA/reconBg/0000.hipo");
            maxEvents = 1000;
            //System.out.println("\n >>>> error: no input file is specified....\n");
            //System.exit(0);
        }

        String histoName   = "histo.hipo"; 
        if(!namePrefix.isEmpty()) {
            histoName  = namePrefix + "_" + histoName;
        }
        
        AvgWireDiffAmongSL analysis = new AvgWireDiffAmongSL();
        analysis.createHistoGroupMap();
        
        if(!readHistos) {                 
            HipoReader reader = new HipoReader();
            reader.open(inputList.get(0));        

            SchemaFactory schema = reader.getSchemaFactory();
            analysis.initReader(new Banks(schema));

            int counter=0;
            Event event = new Event();
        
            ProgressPrintout progress = new ProgressPrintout();
            while (reader.hasNext()) {

                counter++;

                reader.nextEvent(event);                
                analysis.processEvent(event);
                progress.updateStatus();
                if(maxEvents>0){
                    if(counter>=maxEvents) break;
                }                    
            }            
            
            progress.showStatus();
            reader.close();            
            analysis.saveHistos(histoName);
        }
        else{
            analysis.readHistos(inputList.get(0)); 
        }
        
        if(displayPlots) {
            JFrame frame = new JFrame();
            EmbeddedCanvasTabbed canvas = analysis.plotHistos();
            if(canvas != null){
                frame.setSize(1000, 1200);
                frame.add(canvas);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        }               
    }
    
}
