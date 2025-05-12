package org.clas.analysis.studyDCCross;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Random;
import javax.swing.JFrame;

import org.jlab.groot.data.H1F;
import org.jlab.groot.data.H2F;
import org.jlab.groot.graphics.EmbeddedCanvasTabbed;
import org.jlab.jnp.hipo4.data.Event;
import org.jlab.jnp.hipo4.data.SchemaFactory;
import org.jlab.jnp.hipo4.io.HipoReader;
import org.jlab.utils.benchmark.ProgressPrintout;
import org.jlab.utils.options.OptionParser;

import org.clas.utilities.Constants;
import org.clas.element.Track;
import org.clas.element.Cluster;
import org.clas.element.Cross;
import org.clas.graph.HistoGroup;
import org.clas.analysis.BaseAnalysis;
import org.clas.reader.Banks;
import org.clas.reader.LocalEvent;
import org.jlab.geom.prim.Point3D;

/**
 *
 * @author Tongtong Cao
 */
public class StudyDCCross extends BaseAnalysis{ 
    
    public StudyDCCross(){}
    
    @Override
    public void createHistoGroupMap(){                
        HistoGroup histoGroupAvgWireDiff = new HistoGroup("avgWireDiff", 2, 2);
        HistoGroup histoGroupSlopeDiff = new HistoGroup("slopeDiff", 2, 2);
        HistoGroup histoGroupAngleNoPseudo = new HistoGroup("angleNoPseudo", 2, 2);
        for(int i = 0; i < 3; i++){
            H2F h2_avgWireDiffVsAvgWire = new H2F("avgWireDiffVsAvgWire for R" + (i+1), "avg. wire diff. vs avg. wire", 50, 0, 120, 50, 0, 10);
            histoGroupAvgWireDiff.addDataSet(h2_avgWireDiffVsAvgWire, i);
            
            
            H2F h2_slopeDiffVsAvgWire = new H2F("slopeDiffVsAvgWire for R" + (i+1), "difference of slope vs avg. wire", 50, 0, 120,50, -15, 15);
            histoGroupSlopeDiff.addDataSet(h2_slopeDiffVsAvgWire, i);   
            
            H1F h1_angleNoPseudo = new H1F("angleNoPseudo for R" + (i+1), "angle", 100, -40, 40);
            histoGroupAngleNoPseudo.addDataSet(h1_angleNoPseudo, i); 
        }       
        histoGroupMap.put(histoGroupAvgWireDiff.getName(), histoGroupAvgWireDiff);
        histoGroupMap.put(histoGroupSlopeDiff.getName(), histoGroupSlopeDiff);  
        histoGroupMap.put(histoGroupAngleNoPseudo.getName(), histoGroupAngleNoPseudo);    
    }
             
    public void processEvent(Event event){        
        //Read banks
        LocalEvent localEvent = new LocalEvent(reader, event, Constants.AITB);  
        
        List<Cluster> clustersOrig = localEvent.getClustersHB();
        
        HistoGroup histoGroupAvgWire = histoGroupMap.get("avgWireDiff");
        HistoGroup histoGroupSlopeDiff = histoGroupMap.get("slopeDiff");
        HistoGroup histoGroupAngleNoPseudo = histoGroupMap.get("angleNoPseudo");
        for(Track trk : localEvent.getTracksTB()){
            if(Math.abs(trk.chi2pid()) < 3 && trk.vz() > -15 && trk.vz() < 5){
                List<Cluster> clusters = trk.getClusters();
                Map<Integer, List<Cluster>> map_region_clusters = new HashMap();
                for(Cluster cls : clusters){
                    if(cls.id() > 0) {
                        int region = (cls.superlayer() - 1) / 2 + 1;
                        if(map_region_clusters.keySet().contains(region)){
                            for(Cluster clsOrig : clustersOrig){
                                if(clsOrig.id() == cls.id()){
                                    map_region_clusters.get(region).add(clsOrig);
                                    break;
                                }
                            }
                        }
                        else{
                            List<Cluster> clsList = new ArrayList();
                            for(Cluster clsOrig : clustersOrig){
                                if(clsOrig.id() == cls.id()){
                                    clsList.add(clsOrig);
                                    map_region_clusters.put(region, clsList);
                                    break;
                                }
                            }
                        }
                    }
                }
                
                for(int region : map_region_clusters.keySet()){
                    List<Cluster> clsList = map_region_clusters.get(region);
                    if(clsList.size() == 2){                        
                        double avgWireDiff = clsList.get(0).avgWire() - clsList.get(1).avgWire();
                        histoGroupAvgWire.getH2F("avgWireDiffVsAvgWire for R" + region).fill(clsList.get(0).avgWire(), Math.abs(avgWireDiff));
                        histoGroupSlopeDiff.getH2F("slopeDiffVsAvgWire for R" + region).fill(clsList.get(0).avgWire(), Math.toDegrees(Math.acos(clsList.get(1).getNormal().dot(clsList.get(0).getNormal()))) - 2*Constants.STEREOANGLE);                                                                                                
                    }
                }
            }
        }
        
        for(Cross crs : localEvent.getCrossesAllHB()){
            Point3D pointInSector = crs.getCoordsInSector(crs.x(), crs.y(), crs.z());
            double angle = Math.toDegrees(Math.atan2(pointInSector.y(), pointInSector.x()));
            if(crs.id() != -1){
                histoGroupAngleNoPseudo.getH1F("angleNoPseudo for R" + crs.region()).fill(angle);
            }
        }                                        
    }    
    
                            
    public static void main(String[] args){
        OptionParser parser = new OptionParser("studyBgEffectsDC");
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
        
        StudyDCCross analysis = new StudyDCCross();
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
            frame.setSize(1000, 800);
            frame.add(canvas);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        }
        
        if (displayDemos){
            JFrame frame2 = new JFrame();
            EmbeddedCanvasTabbed canvas2 = analysis.plotDemos();
            if(canvas2 != null){
                frame2.setSize(300, 1500);
                frame2.add(canvas2);
                frame2.setLocationRelativeTo(null);
                frame2.setVisible(true);
            }
        }        
    }
    
}
