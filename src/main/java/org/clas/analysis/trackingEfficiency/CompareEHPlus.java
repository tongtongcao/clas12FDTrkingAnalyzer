package org.clas.analysis.trackingEfficiency;

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
import org.jlab.clas.physics.Particle;

import org.clas.utilities.Constants;
import org.clas.element.RunConfig;
import org.clas.element.Track;
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
import org.clas.analysis.compareTracks.CompareTracksHitLevel;
import org.clas.element.MCParticle;
import org.clas.reader.Banks;
import org.clas.reader.LocalEvent;
import org.jlab.clas.physics.Particle;

/**
 *
 * @author Tongtong Cao
 */
public class CompareEHPlus extends BaseAnalysis {

    private static int extraTracksSp1 = 0;
    private static int extraTracksSp2 = 0;

    public CompareEHPlus() {
    }

    @Override
    public void createHistoGroupMap() {
        HistoGroup histoGroupTriggerDiff = new HistoGroup("triggerDiff", 2, 1); 
        H2F h2_triggerDiff1 = new H2F("triggerDiff1", "triggerDiff1", 100, 0, 1, 100, 0, 10);
        h2_triggerDiff1.setTitleX("Dist. of mom (GeV)");
        h2_triggerDiff1.setTitleY("Dist. of vertex (cm)");
        histoGroupTriggerDiff.addDataSet(h2_triggerDiff1, 0);
        
        H2F h2_triggerDiff2 = new H2F("triggerDiff2", "triggerDiff2", 100, 0, 1, 100, 0, 10);
        h2_triggerDiff2.setTitleX("Dist. of mom (GeV)");
        h2_triggerDiff2.setTitleY("Dist. of vertex (cm)");
        histoGroupTriggerDiff.addDataSet(h2_triggerDiff2, 1);
        
        histoGroupMap.put(histoGroupTriggerDiff.getName(), histoGroupTriggerDiff);
    }

    public void processEvent(Event event1, Event event2, int trkType) {
        //// Read banks
        LocalEvent localEvent1 = new LocalEvent(reader1, event1, trkType, Constants.URWELL);
        LocalEvent localEvent2 = new LocalEvent(reader2, event2, trkType, Constants.URWELL);
        
        List<MCParticle> mcParts1 = localEvent1.getMCParticles();
        List<MCParticle> mcParts2 = localEvent2.getMCParticles();
        List<MCParticle> mcEles1 = new ArrayList();
        List<MCParticle> mcEles2 = new ArrayList();
        for(MCParticle mcPart : mcParts1){
            if(mcPart.pid() == 11) mcEles1.add(mcPart);
        }        
        for(MCParticle mcPart : mcParts2){
            if(mcPart.pid() == 11) mcEles2.add(mcPart);
        }
        
        EventForLumiScan eventEHPlus1 = new EventForLumiScan(localEvent1.getTracksTB());
        EventForLumiScan eventEHPlus2 = new EventForLumiScan(localEvent2.getTracksTB());
        
        Track triggerTrk1 = eventEHPlus1.getTriggerTrk();
        Track triggerTrk2 = eventEHPlus2.getTriggerTrk();
        HistoGroup histoGroupTriggerDiff = histoGroupMap.get("triggerDiff");
        if(triggerTrk1 != null &&  triggerTrk2 == null){
            double minDistMom = 9999;
            MCParticle closestMCEle = null;
            for(MCParticle mcEle : mcEles1){
                double distMom = mcEle.euclideanDistanceMom(triggerTrk1);
                if(distMom < minDistMom){
                    minDistMom = distMom;
                    closestMCEle = mcEle;
                }
                if(closestMCEle!=null) histoGroupTriggerDiff.getH2F("triggerDiff1").fill(closestMCEle.euclideanDistanceMom(triggerTrk1), closestMCEle.euclideanDistanceVertex(triggerTrk1));
            }
            //this.addDemoGroup(localEvent1, localEvent2, triggerTrk1.sector());
        }        
        if(triggerTrk1 == null &&  triggerTrk2 != null){
            double minDistMom = 9999;
            MCParticle closestMCEle = null;
            for(MCParticle mcEle : mcEles2){
                double distMom = mcEle.euclideanDistanceMom(triggerTrk2);
                if(distMom < minDistMom){
                    minDistMom = distMom;
                    closestMCEle = mcEle;
                }
                if(closestMCEle!=null) histoGroupTriggerDiff.getH2F("triggerDiff2").fill(closestMCEle.euclideanDistanceMom(triggerTrk2), closestMCEle.euclideanDistanceVertex(triggerTrk2));
                if(closestMCEle.euclideanDistanceMom(triggerTrk2) > 0.1 || closestMCEle.euclideanDistanceVertex(triggerTrk2) > 1) this.addDemoGroup(localEvent1, localEvent2, triggerTrk2.sector());
            }                        
        }
        
        List<Track> posTrks1 = eventEHPlus1.getPosTrks();
        List<Track> posTrks2 = eventEHPlus2.getPosTrks();
        Map<Track, Track> map_trk1_trk2 = new HashMap();
        for(Track trk1 : posTrks1){
            for(Track trk2 : posTrks2){
                int numMatchedHits = trk1.matchedWithTDCHits(trk2);
                if(numMatchedHits >= trk1.nHits() - 2 && numMatchedHits >= trk2.nHits() - 2){
                    map_trk1_trk2.put(trk1, trk2);
                    break;
                }
            }
        }
        
        // Remove the same tracks between two samples
        posTrks1.removeAll(map_trk1_trk2.keySet());
        posTrks2.removeAll(map_trk1_trk2.values());
        extraTracksSp1+=posTrks1.size();
        extraTracksSp2+=posTrks2.size();
        if(posTrks1.size() != posTrks2.size()){            
            for(Track trk1 : posTrks1){
                //this.addDemoGroup(localEvent1, localEvent2, trk1.sector());
            }
            for(Track trk2 : posTrks2){
                //this.addDemoGroup(localEvent1, localEvent2, trk2.sector());
            }
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
        parser.addOption("-trkType", "22", "tracking type: ConvHB(11) or ConvTB(12) or AIHB(21) or AITB(22)");
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

        CompareEHPlus analysis = new CompareEHPlus();
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

            progress.showStatus();
            reader1.close();
            reader2.close();
            analysis.saveHistos(histoName);
            System.out.println("Extra tracks in sample1: " + Integer.toString(CompareEHPlus.extraTracksSp1));
            System.out.println("Extra tracks in sample2: " + Integer.toString(CompareEHPlus.extraTracksSp2));
        } else {
            analysis.readHistos(inputList.get(0));
        }

        if (displayPlots) {
            JFrame frame = new JFrame();
            EmbeddedCanvasTabbed canvas = analysis.plotHistos();
            if(canvas != null){
                frame.setSize(1500, 900);
                frame.add(canvas);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        }

        if (displayDemos) {
            JFrame frame2 = new JFrame();
            EmbeddedCanvasTabbed canvas2 = analysis.plotDemos();
            if (canvas2 != null) {
                frame2.setSize(1200, 1500);
                frame2.add(canvas2);
                frame2.setLocationRelativeTo(null);
                frame2.setVisible(true);
            }
        }

    }
}
