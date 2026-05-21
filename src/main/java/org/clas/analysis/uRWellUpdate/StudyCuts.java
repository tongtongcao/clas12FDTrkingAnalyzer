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
import org.clas.element.MCTrueHit;
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
import org.clas.element.MCTrueHit;

/**
 *
 * @author Tongtong Cao
 */
public class StudyCuts extends BaseAnalysis{ 
    
    public StudyCuts(){}
    
    @Override
    public void createHistoGroupMap(){
        HistoGroup histoGroupTime = new HistoGroup("time", 2, 3);
        for (int i = 0; i < 4; i++) {            
            H1F h1_clusterTime = new H1F("clusterTime for L" + Integer.toString(i + 1),
                    "clusterTime for L" + Integer.toString(i + 1), 100, 50, 160);
            h1_clusterTime.setTitleX("time (ns)");
            h1_clusterTime.setTitleY("Counts");
            histoGroupTime.addDataSet(h1_clusterTime, i);            
        }
        for (int i = 0; i < 2; i++) {            
            H1F h1_crossTime = new H1F("crossTime for R" + Integer.toString(i + 1),
                    "crossTime for R" + Integer.toString(i + 1), 100, 50, 160);
            h1_crossTime.setTitleX("time (ns)");
            h1_crossTime.setTitleY("Counts");
            histoGroupTime.addDataSet(h1_crossTime, i+4);            
        }
        histoGroupMap.put(histoGroupTime.getName(), histoGroupTime); 
        
        
        HistoGroup histoGroupCrossTimeComp = new HistoGroup("crossTimeComp", 2, 2);
        for (int i = 0; i < 2; i++) {
            H2F h2_crossTimeComp = new H2F("crossTimeComp for R" + Integer.toString(i + 1),
                    "crossTimeComp for R" + Integer.toString(i + 1), 100, 50, 160, 100, 50, 160);
            h2_crossTimeComp.setTitleX("time for cluster1 (ns)");
            h2_crossTimeComp.setTitleY("time for cluster2 (ns)");
            histoGroupCrossTimeComp.addDataSet(h2_crossTimeComp, i); 
            
            H1F h1_crossTimeDiff = new H1F("crossTimeDiff for R" + Integer.toString(i + 1),
                    "crossTimeDiff for R" + Integer.toString(i + 1), 100, -100, 100);
            h1_crossTimeDiff.setTitleX("time diff (ns)");
            h1_crossTimeDiff.setTitleY("Counts");
            histoGroupCrossTimeComp.addDataSet(h1_crossTimeDiff, i+2);            
        }
        histoGroupMap.put(histoGroupCrossTimeComp.getName(), histoGroupCrossTimeComp); 
        
        HistoGroup histoGroupCrossEnergyComp = new HistoGroup("crossEnergyComp", 2, 2);
        for (int i = 0; i < 2; i++) {
            H2F h2_crossEnergyComp = new H2F("crossEnergyComp for R" + Integer.toString(i + 1),
                    "crossEnergyComp for R" + Integer.toString(i + 1), 100, 0, 3000, 100, 0, 3000);
            h2_crossEnergyComp.setTitleX("energy for cluster1");
            h2_crossEnergyComp.setTitleY("energy for cluster2");
            histoGroupCrossEnergyComp.addDataSet(h2_crossEnergyComp, i); 
            
            H1F h1_crossEnergyDiff = new H1F("crossEnergyDiff for R" + Integer.toString(i + 1),
                    "crossEnergyDiff for R" + Integer.toString(i + 1), 100, -6000, 6000);
            h1_crossEnergyDiff.setTitleX("energy diff");
            h1_crossEnergyDiff.setTitleY("Counts");
            histoGroupCrossEnergyComp.addDataSet(h1_crossEnergyDiff, i+2);            
        }
        histoGroupMap.put(histoGroupCrossEnergyComp.getName(), histoGroupCrossEnergyComp); 
        
        
        HistoGroup histoGroupCrossPairPosComp = new HistoGroup("crossPairPosComp", 2, 2);
        H2F h2_crossPairXComp = new H2F("crossPairXComp", "crossPairXComp", 100, -100, 100, 100, -100, 100);
        h2_crossPairXComp.setTitleX("x for cross1 (cm)");
        h2_crossPairXComp.setTitleY("x for cross2 (cm)");
        histoGroupCrossPairPosComp.addDataSet(h2_crossPairXComp, 0);
        H2F h2_crossPairYComp = new H2F("crossPairYComp", "crossPairYComp", 100, -100, 100, 100, -100, 100);
        h2_crossPairYComp.setTitleX("y for cross1 (cm)");
        h2_crossPairYComp.setTitleY("y for cross2 (cm)");
        histoGroupCrossPairPosComp.addDataSet(h2_crossPairYComp, 1);
        H1F h1_crossPairXDiff = new H1F("crossPairXDiff", "crossPairXDiff", 100, -1.5, 1.5);
        h1_crossPairXDiff.setTitleX("x diff (cm)");
        h1_crossPairXDiff.setTitleY("Counts");
        histoGroupCrossPairPosComp.addDataSet(h1_crossPairXDiff, 2);
        H1F h1_crossPairYDiff = new H1F("crossPairYDiff", "crossPairYDiff", 100, -1, 1);
        h1_crossPairYDiff.setTitleX("y diff (cm)");
        h1_crossPairYDiff.setTitleY("Counts");
        histoGroupCrossPairPosComp.addDataSet(h1_crossPairYDiff, 3);                   
        histoGroupMap.put(histoGroupCrossPairPosComp.getName(), histoGroupCrossPairPosComp); 

        HistoGroup histoGroupCrossPairTimeComp = new HistoGroup("crossPairTimeComp", 2, 2);
        H2F h2_crossPairTimeComp = new H2F("crossPairTimeComp", "crossPairTimeComp", 100, 50, 160, 100, 50, 160);
        h2_crossPairTimeComp.setTitleX("time for cross1 (ns)");
        h2_crossPairTimeComp.setTitleY("time for cross2 (ns)");
        histoGroupCrossPairTimeComp.addDataSet(h2_crossPairTimeComp, 0);
        H1F h1_crossPairTimeDiff = new H1F("crossPairTimeDiff", "crossPairTimeDiff", 100, -50, 50);
        h1_crossPairTimeDiff.setTitleX("time diff (ns)");
        h1_crossPairTimeDiff.setTitleY("Counts");
        histoGroupCrossPairTimeComp.addDataSet(h1_crossPairTimeDiff, 2);                   
        histoGroupMap.put(histoGroupCrossPairTimeComp.getName(), histoGroupCrossPairTimeComp);         
                
    }
             
    public void processEvent(Event event){        
        LocalEvent localEvent = new LocalEvent(reader, event, Constants.AITB, true);
        
        HistoGroup histoGroupTime = histoGroupMap.get("time");      
        HistoGroup histoGroupCrossTimeComp = histoGroupMap.get("crossTimeComp");        
        HistoGroup histoGroupCrossEnergyComp = histoGroupMap.get("crossEnergyComp");  

        
        for(URWellCross crs : localEvent.getURWellCrossesTB()){
            histoGroupTime.getH1F("clusterTime for L" + Integer.toString(crs.getCluster1().layer())).fill(crs.getCluster1().time());
            histoGroupTime.getH1F("clusterTime for L" + Integer.toString(crs.getCluster2().layer())).fill(crs.getCluster2().time());            
            histoGroupTime.getH1F("crossTime for R" + Integer.toString(crs.region())).fill(crs.time());
            
            histoGroupCrossTimeComp.getH2F("crossTimeComp for R" + Integer.toString(crs.region())).fill(crs.getCluster1().time(), crs.getCluster2().time());
            histoGroupCrossTimeComp.getH1F("crossTimeDiff for R" + Integer.toString(crs.region())).fill(crs.getCluster2().time() - crs.getCluster1().time());
            
            histoGroupCrossEnergyComp.getH2F("crossEnergyComp for R" + Integer.toString(crs.region())).fill(crs.getCluster1().energy(), crs.getCluster2().energy());
            histoGroupCrossEnergyComp.getH1F("crossEnergyDiff for R" + Integer.toString(crs.region())).fill(crs.getCluster2().energy() - crs.getCluster1().energy());            
        }   
        
        HistoGroup histoGroupCrossPairPosComp = histoGroupMap.get("crossPairPosComp"); 
        HistoGroup histoGroupCrossPairTimeComp = histoGroupMap.get("crossPairTimeComp"); 
        if(localEvent.getURWellCrossesTB().size() == 2){
            URWellCross crsR1 = localEvent.getURWellCrossesTB().get(0);
            URWellCross crsR2 = localEvent.getURWellCrossesTB().get(1);
            histoGroupCrossPairPosComp.getH2F("crossPairXComp").fill(crsR1.pointLocal().x(), crsR2.pointLocal().x());
            histoGroupCrossPairPosComp.getH2F("crossPairYComp").fill(crsR1.pointLocal().y(), crsR2.pointLocal().y());
            histoGroupCrossPairPosComp.getH1F("crossPairXDiff").fill(crsR2.pointLocal().x() - crsR1.pointLocal().x());
            histoGroupCrossPairPosComp.getH1F("crossPairYDiff").fill(crsR2.pointLocal().y() - crsR1.pointLocal().y());
            
            histoGroupCrossPairTimeComp.getH2F("crossPairTimeComp").fill(crsR1.time(), crsR2.time());
            histoGroupCrossPairTimeComp.getH1F("crossPairTimeDiff").fill(crsR2.time() - crsR1.time());
        }
    }
    
    public void postEventProcess() {
        HistoGroup histoGroupTime = histoGroupMap.get("time"); 
        for(int i = 0; i < 4; i++){            
            F1D func_time  = new F1D("func_clusterTime" +  Integer.toString(i + 1),"[amp]*gaus(x,[mean],[sigma])", 90,120);
            func_time.setParameter(0, histoGroupTime.getH1F("clusterTime for L" + Integer.toString(i + 1)).getMax());
            func_time.setParameter(1, 110);
            func_time.setParameter(2, 10);
            func_time.setLineColor(2);
            func_time.setOptStat(1110);
            histoGroupTime.getH1F("clusterTime for L" + Integer.toString(i + 1)).fit(func_time); 
        }
        for(int i = 0; i < 2; i++){            
            F1D func_time  = new F1D("func_crossTime" +  Integer.toString(i + 1),"[amp]*gaus(x,[mean],[sigma])", 90,120);
            func_time.setParameter(0, histoGroupTime.getH1F("crossTime for R" + Integer.toString(i + 1)).getMax());
            func_time.setParameter(1, 110);
            func_time.setParameter(2, 10);
            func_time.setLineColor(2);
            func_time.setOptStat(1110);
            histoGroupTime.getH1F("crossTime for R" + Integer.toString(i + 1)).fit(func_time); 
        }        
        
        HistoGroup histoGroupCrossTimeComp = histoGroupMap.get("crossTimeComp");
        for(int i = 0; i < 2; i++){            
            F1D func_crossTimeDiff  = new F1D("func_crossTimeComp for R" +  Integer.toString(i + 1),"[amp]*gaus(x,[mean],[sigma])", -50,50);
            func_crossTimeDiff.setParameter(0, histoGroupCrossTimeComp.getH1F("crossTimeDiff for R" + Integer.toString(i + 1)).getMax());
            func_crossTimeDiff.setParameter(1, 0);
            func_crossTimeDiff.setParameter(2, 10);
            func_crossTimeDiff.setLineColor(2);
            func_crossTimeDiff.setOptStat(1110);
            histoGroupCrossTimeComp.getH1F("crossTimeDiff for R" + Integer.toString(i + 1)).fit(func_crossTimeDiff); 
        }
                
        HistoGroup histoGroupCrossEnergyComp = histoGroupMap.get("crossEnergyComp"); 
        for(int i = 0; i < 2; i++){            
            F1D func_crossEnergyDiff  = new F1D("func_crossEnergyComp for R" +  Integer.toString(i + 1),"[amp]*gaus(x,[mean],[sigma])", -1000,1000);
            func_crossEnergyDiff.setParameter(0, histoGroupCrossEnergyComp.getH1F("crossEnergyDiff for R" + Integer.toString(i + 1)).getMax());
            func_crossEnergyDiff.setParameter(1, 0);
            func_crossEnergyDiff.setParameter(2, 800);
            func_crossEnergyDiff.setLineColor(2);
            func_crossEnergyDiff.setOptStat(1110);
            histoGroupCrossEnergyComp.getH1F("crossEnergyDiff for R" + Integer.toString(i + 1)).fit(func_crossEnergyDiff); 
        }
        
        /*
        HistoGroup histoGroupCrossPairComp = histoGroupMap.get("crossPairComp");
        F1D func_crossPairXDiff = new F1D("func_crossPairXDiff", "[amp]*gaus(x,[mean],[sigma])", -3, 3);
        func_crossPairXDiff.setParameter(0, histoGroupCrossPairComp.getH1F("crossPairXDiff").getMax());
        func_crossPairXDiff.setParameter(1, 0);
        func_crossPairXDiff.setParameter(2, 10);
        func_crossPairXDiff.setLineColor(2);
        func_crossPairXDiff.setOptStat(1110);
        histoGroupCrossPairComp.getH1F("crossPairXDiff").fit(func_crossPairXDiff);
        F1D func_crossPairYDiff = new F1D("func_crossPairYDiff", "[amp]*gaus(x,[mean],[sigma])", -3, 3);
        func_crossPairYDiff.setParameter(0, histoGroupCrossPairComp.getH1F("crossPairYDiff").getMax());
        func_crossPairYDiff.setParameter(1, 0);
        func_crossPairYDiff.setParameter(2, 10);
        func_crossPairYDiff.setLineColor(2);
        func_crossPairYDiff.setOptStat(1110);
        histoGroupCrossPairComp.getH1F("crossPairYDiff").fit(func_crossPairYDiff);
        */ 
        
        HistoGroup histoGroupCrossPairTimeComp = histoGroupMap.get("crossPairTimeComp");
        F1D func_crossPairTimeDiff = new F1D("func_crossPairTimeDiff", "[amp]*gaus(x,[mean],[sigma])", -30, 30);
        func_crossPairTimeDiff.setParameter(0, histoGroupCrossPairTimeComp.getH1F("crossPairTimeDiff").getMax());
        func_crossPairTimeDiff.setParameter(1, 0);
        func_crossPairTimeDiff.setParameter(2, 10);
        func_crossPairTimeDiff.setLineColor(2);
        func_crossPairTimeDiff.setOptStat(1110);
        histoGroupCrossPairTimeComp.getH1F("crossPairTimeDiff").fit(func_crossPairTimeDiff);
    }            
                            
    public static void main(String[] args){
        OptionParser parser = new OptionParser("ExploreTime");
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
        
        StudyCuts analysis = new StudyCuts();
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
                frame.setSize(1000, 1000);
                frame.add(canvas);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        }                
    }
    
}
