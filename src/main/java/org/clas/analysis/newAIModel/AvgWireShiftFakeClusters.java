package org.clas.analysis.newAIModel;

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
import org.clas.element.MCParticle;
import org.clas.reader.Banks;
import org.clas.reader.LocalEvent;

/**
 *
 * @author Tongtong Cao
 */
public class AvgWireShiftFakeClusters extends BaseAnalysis {   
    private double ratioNormalHitsCut = 0.8;
    
    public AvgWireShiftFakeClusters() {
    }

    public AvgWireShiftFakeClusters(Banks banks) {
        super(banks);
    }

    @Override
    public void createHistoGroupMap() {
        HistoGroup histoGroupAvgWireDiff= new HistoGroup("avgWireDiff", 3, 1);
        for (int i = 0; i < 3; i++) {
            H1F h1_avgWireDiff = new H1F("avgWireDiff for R" + Integer.toString(i + 1), "avgWireDiff for R" + Integer.toString(i + 1), 100, -100, 100);
            h1_avgWireDiff.setTitleX("avgWireDiff");
            h1_avgWireDiff.setTitleY("Counts");
            histoGroupAvgWireDiff.addDataSet(h1_avgWireDiff, i);  
        }
        
        
        
        histoGroupMap.put(histoGroupAvgWireDiff.getName(), histoGroupAvgWireDiff);    
        
    }

    public void processEvent(Event event1, Event event2) {
        
        //// Read banks
        LocalEvent localEvent1 = new LocalEvent(reader, event1, 12);
        LocalEvent localEvent2 = new LocalEvent(reader, event2, 11);
        
        List<Track> trackList1 = new ArrayList();    
        List<Track> trackList2 = new ArrayList();
        
        List<Cluster> clusterList1 = new ArrayList(); // Clusters on valid TB tracks in sample 1
        List<Cluster> clusterList2 = localEvent2.getClusters(); // all clusters from clustering

        for(Track trk : localEvent1.getTracksTB()){
            if(trk.isValid()) {
                trackList1.add(trk);
                for(Cluster cls : trk.getClusters()){
                    if(!clusterList1.contains(cls)) clusterList1.add(cls);
                }
            }
        }
         
        ////// Make cluster map between clusters in TB tracks of sample1 and clusters from clustering of sample2
        // Priorities for map most matched hits (priority 1), highest normal ratio (priority 2) and closest avgWire (priority 3)  
        Map<Cluster, Cluster> map_cls1_cls2_matched = new HashMap();             
        for(Cluster cls1 : clusterList2){
            List<Cluster> matchedClustersWithMostMatchedHits = new ArrayList();
            int maxMatchedHits = -1;
            for(Cluster cls2 : localEvent2.getClusters()){
                if(cls2.getRatioNormalHits() >= ratioNormalHitsCut){
                    int numMatchedHits = cls1.clusterMatchedHits(cls2);
                    if(numMatchedHits > 0){
                        if(numMatchedHits > maxMatchedHits) {
                            maxMatchedHits = numMatchedHits;
                            matchedClustersWithMostMatchedHits.clear();
                            matchedClustersWithMostMatchedHits.add(cls2);                    
                        }
                        else if(numMatchedHits == maxMatchedHits){
                            matchedClustersWithMostMatchedHits.add(cls2);
                        }
                    }
                }
            }            
            
            if(matchedClustersWithMostMatchedHits.size() == 1) map_cls1_cls2_matched.put(cls1, matchedClustersWithMostMatchedHits.get(0));
            else if(matchedClustersWithMostMatchedHits.size() > 1){
                List<Cluster> clustersWithMaxNormalRatio = new ArrayList();
                double maxNormalRatio = -1;                                            
                for(Cluster cls2 : matchedClustersWithMostMatchedHits){
                    double normalRatio = cls2.getRatioNormalHits();
                    if(normalRatio > maxNormalRatio) {
                        maxNormalRatio = normalRatio;
                        clustersWithMaxNormalRatio.clear();
                        clustersWithMaxNormalRatio.add(cls2);                    
                    }
                    else if(normalRatio == maxNormalRatio){
                        clustersWithMaxNormalRatio.add(cls2);
                    }
                }
                
                if(clustersWithMaxNormalRatio.size() == 1) map_cls1_cls2_matched.put(cls1, clustersWithMaxNormalRatio.get(0));
                else if(clustersWithMaxNormalRatio.size() > 1){
                   double closestAvgWire = 10000;
                   Cluster clsWithCloestAvgWire = null;
                   for(Cluster cls2 : clustersWithMaxNormalRatio){ 
                       double absDiffAvgWire = Math.abs(cls1.avgWire() - cls2.avgWire());
                       if(absDiffAvgWire < closestAvgWire) {
                           closestAvgWire = absDiffAvgWire;
                           clsWithCloestAvgWire = cls2;
                       }
                   }          
                   if(clsWithCloestAvgWire != null) {
                       map_cls1_cls2_matched.put(cls1, clsWithCloestAvgWire);
                   }
                }
            }             
        }
        
        ////// Make a map between matched cluster and unmatched cluster list with same sector and same superlayer in sample 2
        List<Cluster> unmatchedClusterList2 = new ArrayList();
        unmatchedClusterList2.addAll(clusterList2);
        unmatchedClusterList2.removeAll(map_cls1_cls2_matched.values());

        Map<Cluster, List<Cluster>> map_matchedCls2_unmatchedCls2List = new HashMap();
        for(Cluster matchedCls : map_cls1_cls2_matched.values()){
            List<Cluster> unmatchedCls2List = new ArrayList();
            for(Cluster unmatchedCls : unmatchedClusterList2){
                if(unmatchedCls.sector() == matchedCls.sector() && unmatchedCls.superlayer() == matchedCls.superlayer()){
                    unmatchedCls2List.add(unmatchedCls);
                }
            }
            if(!unmatchedCls2List.isEmpty()) map_matchedCls2_unmatchedCls2List.put(matchedCls, unmatchedCls2List);
        }
        
        ////// Make plots for diffenrce of average wire between matched and unmatched clusters
        HistoGroup histoGroupAvgWireDiff = histoGroupMap.get("avgWireDiff");  
        for(Cluster matchedCls2 : map_matchedCls2_unmatchedCls2List.keySet()){
            List<Cluster> unmatchedCls2List = map_matchedCls2_unmatchedCls2List.get(matchedCls2);
            for(Cluster unmatchedCls2 : unmatchedCls2List){
                histoGroupAvgWireDiff.getH1F("avgWireDiff for R" + Integer.toString((matchedCls2.superlayer()+ 1)/2)).fill(unmatchedCls2.avgWire() - matchedCls2.avgWire());
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
        parser.addOption("-mDemo", "1000", "maxium for number of demonstrated cases");
        parser.addOption("-mc", "0", "if mc (0/1)");

        // histogram based analysis
        parser.addOption("-histo", "0", "read histogram file (0/1)");

        parser.parse(args);

        String namePrefix = parser.getOption("-o").stringValue();
        int maxEvents = parser.getOption("-n").intValue();
        boolean displayPlots = (parser.getOption("-plot").intValue() != 0);
        boolean displayDemos = (parser.getOption("-demo").intValue() != 0);
        int maxDemoCases = parser.getOption("-mDemo").intValue();
        boolean readHistos = (parser.getOption("-histo").intValue() != 0);
        boolean mc = (parser.getOption("-mc").intValue() != 0);
        Constants.MC = mc;
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

        AvgWireShiftFakeClusters analysis = new AvgWireShiftFakeClusters();
        analysis.createHistoGroupMap();
        if (!readHistos) {
            HipoReader reader1 = new HipoReader();
            reader1.open(inputList.get(0));
            HipoReader reader2 = new HipoReader();
            reader2.open(inputList.get(1));

            SchemaFactory schema = reader1.getSchemaFactory();
            analysis.initReader(new Banks(schema));

            int counter = 0;
            Event event1 = new Event();
            Event event2 = new Event();

            ProgressPrintout progress = new ProgressPrintout();
            while (reader1.hasNext() && reader2.hasNext()) {

                counter++;

                reader1.nextEvent(event1);
                reader2.nextEvent(event2);

                analysis.processEvent(event1, event2);

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
        } else {
            analysis.readHistos(inputList.get(0));
        }

        if (displayPlots) {
            JFrame frame = new JFrame();
            EmbeddedCanvasTabbed canvas = analysis.plotHistos();
            frame.setSize(1200, 500);
            frame.add(canvas);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
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
