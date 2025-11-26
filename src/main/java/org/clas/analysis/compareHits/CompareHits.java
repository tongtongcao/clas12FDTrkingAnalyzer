package org.clas.analysis.compareHits;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JFrame;

import org.jlab.groot.data.H1F;
import org.jlab.groot.data.H2F;

import org.clas.analysis.BaseAnalysis;
import org.clas.demo.DemoBase;
import org.clas.element.Cluster;
import org.clas.element.TDC;
import org.clas.element.Hit;
import org.clas.element.Track;
import org.clas.element.URWellCross;
import org.clas.graph.HistoGroup;
import org.clas.reader.LocalEvent;
import org.clas.fit.ClusterFitLC;
import org.clas.graph.TrackHistoGroup;
import org.clas.reader.Banks;
import org.clas.utilities.Constants;
import org.jlab.groot.data.GraphErrors;
import org.jlab.groot.graphics.EmbeddedCanvasTabbed;
import org.jlab.groot.group.DataGroup;
import org.jlab.jnp.hipo4.data.Event;
import org.jlab.jnp.hipo4.data.SchemaFactory;
import org.jlab.jnp.hipo4.io.HipoReader;
import org.jlab.utils.benchmark.ProgressPrintout;
import org.jlab.utils.options.OptionParser;
import org.clas.demo.DemoSuperlayerWithURWell;
import org.clas.demo.DemoConstants;

/**
 *
 * @author Tongtong Cao
 */
public class CompareHits extends BaseAnalysis {      
    @Override
    public void createHistoGroupMap() {        
        HistoGroup histoGroupHitOrderComp = new HistoGroup("hitOrderComp", 2, 3);     
        
        for (int i = 0; i < 6; i++) {
            H2F h2_hitOrderComp = new H2F("hitOrderComp for SL" + Integer.toString(i + 1),
                    "hitOrderComp for SL" + Integer.toString(i + 1), 13, -0.5, 12.5, 13, -0.5, 12.5);
            h2_hitOrderComp.setTitleX("order in sp1 / 10");
            h2_hitOrderComp.setTitleY("order in sp2 / 10");
            histoGroupHitOrderComp.addDataSet(h2_hitOrderComp, i);                                                
        }
                
        histoGroupMap.put(histoGroupHitOrderComp.getName(), histoGroupHitOrderComp);
        
        HistoGroup histoGroupHitStatus = new HistoGroup("hitStatus", 2, 3); 
        H1F h1_normalHits = new H1F("normalHits", "normal hits", 6, 0.5, 6.5);
        h1_normalHits.setTitleX("Superlayer");
        h1_normalHits.setTitleY("Counts");
        histoGroupHitStatus.addDataSet(h1_normalHits, 0);         
        H1F h1_noiseHits = new H1F("noiseHits", "noise hits", 6, 0.5, 6.5);
        h1_noiseHits.setTitleX("Superlayer");
        h1_noiseHits.setTitleY("Counts");
        histoGroupHitStatus.addDataSet(h1_noiseHits, 1);         
        H1F h1_remainingNormalHitsSp1 = new H1F("remainingNormalHitsSp1", "remaining normal hits in sample1", 6, 0.5, 6.5);
        h1_remainingNormalHitsSp1.setTitleX("Superlayer");
        h1_remainingNormalHitsSp1.setTitleY("Counts");
        histoGroupHitStatus.addDataSet(h1_remainingNormalHitsSp1, 2);         
        H1F h1_remainingNoiseHitsSp1 = new H1F("remainingNoiseHitsSp1", "remaining noise hits in sample1", 6, 0.5, 6.5);
        h1_remainingNoiseHitsSp1.setTitleX("Superlayer");
        h1_remainingNoiseHitsSp1.setTitleY("Counts");
        histoGroupHitStatus.addDataSet(h1_remainingNoiseHitsSp1, 3);                
        H1F h1_remainingNormalHitsSp2 = new H1F("remainingNormalHitsSp2", "remaining normal hits in sample2", 6, 0.5, 6.5);
        h1_remainingNormalHitsSp2.setTitleX("Superlayer");
        h1_remainingNormalHitsSp2.setTitleY("Counts");
        histoGroupHitStatus.addDataSet(h1_remainingNormalHitsSp2, 4);         
        H1F h1_remainingNoiseHitsSp2 = new H1F("remainingNoiseHitsSp2", "remaining noise hits in sample2", 6, 0.5, 6.5);
        h1_remainingNoiseHitsSp2.setTitleX("Superlayer");
        h1_remainingNoiseHitsSp2.setTitleY("Counts");
        histoGroupHitStatus.addDataSet(h1_remainingNoiseHitsSp2, 5); 
        
        histoGroupMap.put(histoGroupHitStatus.getName(), histoGroupHitStatus);
        
    }
    
    public void processEvent(Event event1, Event event2, int trkType) {
        ////// Read banks
        LocalEvent localEvent1 = new LocalEvent(reader1, event1, trkType, true);
        LocalEvent localEvent2 = new LocalEvent(reader2, event2, trkType, true);
        
        List<TDC> tdcs1 = localEvent1.getTDCs();
        List<TDC> tdcs2 = localEvent2.getTDCs();
        HistoGroup histoGroupHitOrderComp = histoGroupMap.get("hitOrderComp");
        for(TDC tdc1: tdcs1){
            for(TDC tdc2 : tdcs2){
                if(tdc2.matchTDC(tdc1)){
                    histoGroupHitOrderComp.getH2F("hitOrderComp for SL" + Integer.toString(tdc2.superlayer())).fill(tdc1.order()/10., tdc2.order()/10.);
                    break;
                }
            }
        }
        
        HistoGroup histoGroupHitStatus = histoGroupMap.get("hitStatus");
        for(TDC tdc1: tdcs1){
            if(tdc1.isNormalHit()) histoGroupHitStatus.getH1F("normalHits").fill(tdc1.superlayer());
            else histoGroupHitStatus.getH1F("noiseHits").fill(tdc1.superlayer());
            
            if(tdc1.isRemainedAfterAIDenoising()){
                if(tdc1.isNormalHit()) histoGroupHitStatus.getH1F("remainingNormalHitsSp1").fill(tdc1.superlayer());
                else histoGroupHitStatus.getH1F("remainingNoiseHitsSp1").fill(tdc1.superlayer());
            }
        }
        
        
        for(TDC tdc2: tdcs2){
            if(tdc2.isRemainedAfterAIDenoising()){
                if(tdc2.isNormalHit()) histoGroupHitStatus.getH1F("remainingNormalHitsSp2").fill(tdc2.superlayer());
                else histoGroupHitStatus.getH1F("remainingNoiseHitsSp2").fill(tdc2.superlayer());
            } 
        }
        
    }
    
    public void postEventProcess(){
    }               
    
    public static void main(String[] args) {
        OptionParser parser = new OptionParser("studyBgEffectsDC");
        parser.setRequiresInputList(false);
        // valid options for event-base analysis
        parser.addOption("-o", "", "output file name prefix");
        parser.addOption("-n", "-1", "maximum number of events to process");
        parser.addOption("-plot", "1", "display histograms (0/1)");
        parser.addOption("-demo", "1", "display case demo (0/1)");
        parser.addOption("-mDemo", "1000", "maxium for number of demonstrated cases");
        parser.addOption("-trkType"      ,"22",   "tracking type: 12 (ConvTB) or 22 (AITB)");
        parser.addOption("-mc", "1", "if mc (0/1)");
        parser.addOption("-uRWell", "1", "if uRWell is included (0/1)");


        // histogram based analysis
        parser.addOption("-histo", "0", "read histogram file (0/1)");

        parser.parse(args);

        String namePrefix = parser.getOption("-o").stringValue();
        int maxEvents = parser.getOption("-n").intValue();
        boolean displayPlots = (parser.getOption("-plot").intValue() != 0);
        boolean displayDemos = (parser.getOption("-demo").intValue() != 0);
        int maxDemoCases = parser.getOption("-mDemo").intValue();
        boolean readHistos = (parser.getOption("-histo").intValue() != 0);
        int trkType = parser.getOption("-trkType").intValue();
        boolean mc = (parser.getOption("-mc").intValue() != 0);
        boolean uRWell = (parser.getOption("-uRWell").intValue() != 0);
        Constants.MC = mc;
        Constants.URWELL = uRWell;
        Constants.MAXDEMOCASES = maxDemoCases;
        
        List<String> inputList = parser.getInputList();
        if (inputList.isEmpty() == true) {
            parser.printUsage();
            inputList.add("/Users/caot/research/clas12/data/mc/uRWELL/upgradeTrackingWithuRWELL/rga-sidis-uRWell-2R_denoise/0nA/reconBg/0000.hipo");
            inputList.add("/Users/caot/research/clas12/data/mc/uRWELL/upgradeTrackingWithuRWELL/rga-sidis-uRWell-2R_denoise/50nA/reconBg/0000.hipo");
            maxEvents = 1000;
            //System.out.println("\n >>>> error: no input file is specified....\n");
            //System.exit(0);
        }

        String histoName = "histo.hipo";
        if (!namePrefix.isEmpty()) {
            histoName = namePrefix + "_" + histoName;
        }
        
        CompareHits analysis = new CompareHits();
        analysis.createHistoGroupMap();
        

        if (!readHistos) {
            HipoReader reader1 = new HipoReader();
            reader1.open(inputList.get(0));
            HipoReader reader2 = new HipoReader();
            reader2.open(inputList.get(1));

            SchemaFactory schema1 = reader1.getSchemaFactory();
            SchemaFactory schema2 = reader2.getSchemaFactory();
            analysis.initReader(new Banks(schema1), new Banks(schema2));

            int counter = 0;
            Event event1 = new Event();
            Event event2 = new Event();

            ProgressPrintout progress = new ProgressPrintout();
            while (reader1.hasNext() && reader2.hasNext()) {

                counter++;

                reader1.nextEvent(event1);
                reader2.nextEvent(event2);

                analysis.processEvent(event1, event2, trkType);

                progress.updateStatus();
                if (maxEvents > 0) {
                    if (counter >= maxEvents) {
                        break;
                    }
                }
            }

            analysis.postEventProcess();

            progress.showStatus();
            reader1.close();
            reader2.close();
            analysis.saveHistos(histoName);
        } else {
            analysis.readHistos(inputList.get(0));
        }

        if (displayPlots) {
            JFrame frame = new JFrame();
            EmbeddedCanvasTabbed canvas = analysis.plotHistos();
            if(canvas != null){
                frame.setSize(800, 1200);
                frame.add(canvas);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        }
        
        if (displayDemos){
            JFrame frame2 = new JFrame();
            EmbeddedCanvasTabbed canvas2 = analysis.plotDemos();
            if(canvas2 != null){
                frame2.setSize(1200, 1500);
                frame2.add(canvas2);
                frame2.setLocationRelativeTo(null);
                frame2.setVisible(true);
            }
        }
    }

    
}