package org.clas.analysis.studyBgEffects;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import javax.swing.JFrame;
import org.jlab.groot.data.H1F;
import org.jlab.groot.data.H2F;
import org.jlab.groot.data.DataVector;
import org.jlab.groot.data.GraphErrors;
import org.jlab.groot.graphics.EmbeddedCanvasTabbed;
import org.jlab.jnp.hipo4.data.Event;
import org.jlab.jnp.hipo4.data.SchemaFactory;
import org.jlab.jnp.hipo4.io.HipoReader;
import org.jlab.utils.benchmark.ProgressPrintout;
import org.jlab.utils.options.OptionParser;
import org.jlab.groot.group.DataGroup;

import org.clas.utilities.Constants;
import org.clas.element.RunConfig;
import org.clas.element.Track;
import org.clas.element.AICandidate;
import org.clas.element.Cluster;
import org.clas.element.Hit;
import org.clas.element.TDC;
import org.clas.element.URWellHit;
import org.clas.element.URWellCluster;
import org.clas.element.URWellCross;
import org.clas.graph.HistoGroup;
import org.clas.graph.TrackHistoGroup;
import org.clas.physicsEvent.BasePhysicsEvent;
import org.clas.physicsEvent.EventForLumiScan;
import org.clas.analysis.BaseAnalysis;
import org.clas.reader.Banks;
import org.clas.reader.LocalEvent;
import org.clas.demo.DemoSuperlayer;
import org.clas.demo.DemoSector;

/**
 *
 * @author Tongtong Cao
 */
public class StudyBgEffectsDenoisingClustering extends BaseAnalysis {
    BgEffectsDenoising bgEffectsDenoising = new BgEffectsDenoising();
    BgEffectsClustering bgEffectsClustering = new BgEffectsClustering();
    
    public StudyBgEffectsDenoisingClustering() {}

    @Override
    public void createHistoGroupMap() {
        bgEffectsDenoising.createHistoGroupMap();
        for(String key : bgEffectsDenoising.getHistoGroupMap().keySet()){
            histoGroupMap.put(key, bgEffectsDenoising.getHistoGroupMap().get(key));
        }
        
        bgEffectsClustering.createHistoGroupMap();
        for(String key : bgEffectsClustering.getHistoGroupMap().keySet()){
            histoGroupMap.put(key, bgEffectsClustering.getHistoGroupMap().get(key));
        }         
    }

    public void processEvent(Event event1, Event event2, int trkType) {
        //Read banks
        LocalEvent localEvent1 = new LocalEvent(reader1, event1, trkType, true);
        LocalEvent localEvent2 = new LocalEvent(reader2, event2, trkType, true);        
                            
        //Denoising 
        bgEffectsDenoising.processEvent(localEvent1, localEvent2);
        
        //Clutering
        bgEffectsClustering.processEvent(localEvent1, localEvent2);
                
    }
            
    

    public void postEventProcess() {                
        // Denoising
        bgEffectsDenoising.postEventProcess();
        
        // Clustering
        bgEffectsClustering.postEventProcess();
        for(String key : bgEffectsClustering.getDemoGroupMap().keySet()){
                demoGroupMap.put(key, bgEffectsClustering.getDemoGroupMap().get(key));
        }                      
    }

    public static void main(String[] args) {
        OptionParser parser = new OptionParser("studyBgEffectsDC");
        parser.setRequiresInputList(false);
        // valid options for event-base analysis
        parser.addOption("-o", "", "output file name prefix");
        parser.addOption("-n", "-1", "maximum number of events to process");
        parser.addOption("-plot", "1", "display histograms (0/1)");
        parser.addOption("-demo", "1", "display case demo (0/1)");
        parser.addOption("-mDemo", "100", "maxium for number of demonstrated cases");
        parser.addOption("-trkType"      ,"22",   "tracking type: 12 (ConvTB) or 22 (AITB)");
        parser.addOption("-mc", "1", "if mc (0/1)");
        parser.addOption("-uRWell", "0", "if uRWell is included (0/1)");


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
        
        StudyBgEffectsDenoisingClustering analysis = new StudyBgEffectsDenoisingClustering();
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
            frame.setSize(1500, 900);
            frame.add(canvas);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
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
