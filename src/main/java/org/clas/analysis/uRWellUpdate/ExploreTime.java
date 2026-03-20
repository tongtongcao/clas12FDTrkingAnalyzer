package org.clas.analysis.uRWellUpdate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Random;
import javax.swing.JFrame;

import org.jlab.geom.prim.Point3D;
import org.jlab.geom.prim.Line3D;
import org.jlab.geom.prim.Vector3D;
import org.jlab.clas.physics.Particle;

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
public class ExploreTime extends BaseAnalysis{ 
    
    public ExploreTime(){}
    
    @Override
    public void createHistoGroupMap(){
        HistoGroup histoGroupTimeHB = new HistoGroup("timeHB", 4, 4);
        H1F h1_startTimeHB= new H1F("startTimeHB","start time for HB" , 100, 120, 130);
        h1_startTimeHB.setTitleX("start time");
        h1_startTimeHB.setTitleY("Counts");
        histoGroupTimeHB.addDataSet(h1_startTimeHB, 0);           
        for (int i = 0; i < 4; i++) {
            H1F h1_clusterTime = new H1F("clusterTimeLayer" + Integer.toString(i + 1),
                    "cluster time for layer" + Integer.toString(i + 1), 100, 100, 160);
            h1_clusterTime.setTitleX("cluster time");
            h1_clusterTime.setTitleY("Counts");
            histoGroupTimeHB.addDataSet(h1_clusterTime, i+4);            

            H1F h1_propTime = new H1F("propTimeLayer" + Integer.toString(i + 1),
                    "propagation time for layer" + Integer.toString(i + 1), 100, 8, 12);
            h1_propTime.setTitleX("propagation time");
            h1_propTime.setTitleY("Counts");
            histoGroupTimeHB.addDataSet(h1_propTime, i+8); 
            
            H1F h1_timeDiff = new H1F("timeDiffLayer" + Integer.toString(i + 1),
                    "time difference for layer" + Integer.toString(i + 1), 100, -20, 20);
            h1_timeDiff.setTitleX("time difference");
            h1_timeDiff.setTitleY("Counts");
            histoGroupTimeHB.addDataSet(h1_timeDiff, i+12);            
        }           
        histoGroupMap.put(histoGroupTimeHB.getName(), histoGroupTimeHB);
        
        HistoGroup histoGroupTimeTB = new HistoGroup("timeTB", 4, 4);
        H1F h1_startTimeTB= new H1F("startTimeTB","start time for TB" , 100, 120, 130);
        h1_startTimeTB.setTitleX("start time");
        h1_startTimeTB.setTitleY("Counts");
        histoGroupTimeTB.addDataSet(h1_startTimeTB, 0);           
        for (int i = 0; i < 4; i++) {
            H1F h1_clusterTime = new H1F("clusterTimeLayer" + Integer.toString(i + 1),
                    "cluster time for layer" + Integer.toString(i + 1), 100, 100, 160);
            h1_clusterTime.setTitleX("cluster time");
            h1_clusterTime.setTitleY("Counts");
            histoGroupTimeTB.addDataSet(h1_clusterTime, i+4);            

            H1F h1_propTime = new H1F("propTimeLayer" + Integer.toString(i + 1),
                    "propagation time for layer" + Integer.toString(i + 1), 100, 8, 12);
            h1_propTime.setTitleX("propagation time");
            h1_propTime.setTitleY("Counts");
            histoGroupTimeTB.addDataSet(h1_propTime, i+8); 
            
            H1F h1_timeDiff = new H1F("timeDiffLayer" + Integer.toString(i + 1),
                    "time difference for layer" + Integer.toString(i + 1), 100, -20, 20);
            h1_timeDiff.setTitleX("time difference");
            h1_timeDiff.setTitleY("Counts");
            histoGroupTimeTB.addDataSet(h1_timeDiff, i+12);            
        }           
        histoGroupMap.put(histoGroupTimeTB.getName(), histoGroupTimeTB);        
                 
    }
             
    public void processEvent(Event event){        
        LocalEvent localEvent = new LocalEvent(reader, event, Constants.AITB, true);
        
        HistoGroup histoGroupTimeHB = histoGroupMap.get("timeHB");         
        if(localEvent.getRecHBEvent()!=null){
            double startTime = localEvent.getRecHBEvent().startTime();
            histoGroupTimeHB.getH1F("startTimeHB").fill(startTime);
            
            List<Track> tracksHB = localEvent.getTracksHB();
            
            for(Track trk : localEvent.getTracksHB()){
                Particle particle = trk.particle();
                if(particle != null){
                    double beta = particle.p()/particle.e();                    

                    for(URWellCross crs : trk.getURWellCrosses()){ 
                        int layerCluster1 = crs.getCluster1().layer();
                        double timeCluster1 = crs.getCluster1().time();
                        histoGroupTimeHB.getH1F("clusterTimeLayer" + layerCluster1).fill(timeCluster1); 
                        
                        double propTimeCluster1 = crs.cluster1_pathLength()/(beta * Constants.LIGHTSPEED);
                        histoGroupTimeHB.getH1F("propTimeLayer" + layerCluster1).fill(propTimeCluster1); 

                        histoGroupTimeHB.getH1F("timeDiffLayer" + layerCluster1).fill(timeCluster1 - startTime - propTimeCluster1);

                        int layerCluster2 = crs.getCluster2().layer();
                        double timeCluster2 = crs.getCluster2().time();
                        histoGroupTimeHB.getH1F("clusterTimeLayer" + layerCluster2).fill(timeCluster2);
                        
                        double propTimeCluster2 = crs.cluster2_pathLength()/(beta * Constants.LIGHTSPEED);
                        histoGroupTimeHB.getH1F("propTimeLayer" + layerCluster2).fill(propTimeCluster2); 
                        
                        histoGroupTimeHB.getH1F("timeDiffLayer" + layerCluster2).fill(timeCluster2 - startTime - propTimeCluster1);                    
                    } 
                }
            }
        }
        
        HistoGroup histoGroupTimeTB = histoGroupMap.get("timeTB");         
        if(localEvent.getRecTBEvent()!=null){
            double startTime = localEvent.getRecTBEvent().startTime();
            histoGroupTimeTB.getH1F("startTimeTB").fill(startTime);
            
            List<Track> tracksTB = localEvent.getTracksTB();
            
            for(Track trk : localEvent.getTracksTB()){
                Particle particle = trk.particle();
                if(particle != null){
                    double beta = particle.p()/particle.e();
                
                    for(URWellCross crs : trk.getURWellCrosses()){ 
                        int layerCluster1 = crs.getCluster1().layer();
                        double cluster1Time = crs.getCluster1().time();
                        histoGroupTimeTB.getH1F("clusterTimeLayer" + layerCluster1).fill(cluster1Time);  

                        double propTimeCluster1 = crs.cluster1_pathLength()/(beta * Constants.LIGHTSPEED);
                        histoGroupTimeTB.getH1F("propTimeLayer" + layerCluster1).fill(propTimeCluster1); 

                        histoGroupTimeTB.getH1F("timeDiffLayer" + layerCluster1).fill(cluster1Time - startTime - propTimeCluster1);

                        int layerCluster2 = crs.getCluster2().layer();
                        double cluster2Time = crs.getCluster2().time();
                        histoGroupTimeTB.getH1F("clusterTimeLayer" + layerCluster2).fill(cluster2Time);                
                        
                        double propTimeCluster2 = crs.cluster2_pathLength()/(beta * Constants.LIGHTSPEED);
                        histoGroupTimeTB.getH1F("propTimeLayer" + layerCluster2).fill(propTimeCluster2); 
                        
                        histoGroupTimeTB.getH1F("timeDiffLayer" + layerCluster2).fill(cluster1Time - startTime - propTimeCluster2);                    
                    }            
                }
            }
        }

    }
    
    public void postEventProcess() {
                                   
    }            
                            
    public static void main(String[] args){
        OptionParser parser = new OptionParser("ExploreTime");
        parser.setRequiresInputList(false);
        // valid options for event-base analysis
        parser.addOption("-o"          ,"",     "output file name prefix");
        parser.addOption("-n"          ,"-1",   "maximum number of events to process");        
        parser.addOption("-plot"       ,"1",    "display histograms (0/1)");
        parser.addOption("-demo", "1", "display case demo (0/1)");
        parser.addOption("-mDemo", "100", "maxium for number of demonstrated cases");
                
        // histogram based analysis
        parser.addOption("-histo"      ,"0",    "read histogram file (0/1)");
        
        parser.parse(args);
        
        String namePrefix  = parser.getOption("-o").stringValue(); 
        int   maxEvents  = parser.getOption("-n").intValue();    
        boolean displayPlots   = (parser.getOption("-plot").intValue()!=0);
        boolean displayDemos = (parser.getOption("-demo").intValue() != 0);
        int maxDemoCases = parser.getOption("-mDemo").intValue();
        boolean readHistos   = (parser.getOption("-histo").intValue()!=0); 
        Constants.MAXDEMOCASES = maxDemoCases;
        //Constants.URWELLRegions = 1;
        
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
        
        ExploreTime analysis = new ExploreTime();
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
            
            analysis.postEventProcess();
            
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
                frame.setSize(1750, 1050);
                frame.add(canvas);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        }
        
        if (displayDemos){
            JFrame frame2 = new JFrame();
            EmbeddedCanvasTabbed canvas2 = analysis.plotDemos();
            if(canvas2 != null){
                frame2.setSize(1200, 750);
                frame2.add(canvas2);
                frame2.setLocationRelativeTo(null);
                frame2.setVisible(true);
            }
        }        
    }
    
}
