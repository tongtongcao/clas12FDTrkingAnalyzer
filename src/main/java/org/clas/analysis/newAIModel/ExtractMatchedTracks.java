package org.clas.analysis.newAIModel;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import org.clas.analysis.BaseAnalysis;

import org.jlab.utils.options.OptionParser;
import org.jlab.jnp.hipo4.io.HipoReader;
import org.jlab.jnp.hipo4.data.Event;
import org.jlab.jnp.hipo4.data.Bank;
import org.jlab.jnp.hipo4.data.SchemaFactory;
import org.jlab.jnp.hipo4.io.HipoWriterSorted;
import org.jlab.utils.benchmark.ProgressPrintout;

import org.clas.element.RunConfig;
import org.clas.element.Track;
import org.clas.element.Cluster;
import org.clas.graph.HistoGroup;
import org.clas.reader.Reader;
import org.clas.reader.Banks;
import org.clas.reader.LocalEvent;
import org.jlab.groot.data.H1F;
import org.jlab.groot.data.H2F;
import org.jlab.groot.graphics.EmbeddedCanvasTabbed;

/**
 *
 * @author Tongtong
 */

public class ExtractMatchedTracks extends BaseAnalysis{   
    private static final Logger LOGGER = Logger.getLogger(Reader.class.getName()); 
    private static final int NUMMATCHEDCLUSTERSCUT = 3;
    
    public ExtractMatchedTracks() {
    }

    @Override
    public void createHistoGroupMap() {
        HistoGroup histoGroupOverview= new HistoGroup("overview", 4, 2);
        
        H1F h1_numMatchedTracks = new H1F("numMatchedTracks", "# of matched tracks", 10, 0.5, 10.5);
        h1_numMatchedTracks.setTitleX("# of matched tracks");
        h1_numMatchedTracks.setTitleY("counts");
        histoGroupOverview.addDataSet(h1_numMatchedTracks, 0);   
        
        H2F h2_purityVsEfficiencyTrack = new H2F("purityVsEfficiencyTrack", "purity vs efficiency for matched tracks", 50, 0, 1.01, 50, 0, 1.01);
        h2_purityVsEfficiencyTrack.setTitleX("efficiency");
        h2_purityVsEfficiencyTrack.setTitleY("purity");
        histoGroupOverview.addDataSet(h2_purityVsEfficiencyTrack, 1); 
        
        H2F h2_chi2OverNDFVsPurityTrack = new H2F("chi2OverNDFVsPurityTrack", "chi2/ndf vs purity for matched tracks", 100, 0, 1.01, 100, 0, 100);
        h2_chi2OverNDFVsPurityTrack.setTitleX("purity");
        h2_chi2OverNDFVsPurityTrack.setTitleY("chi2/ndf");
        histoGroupOverview.addDataSet(h2_chi2OverNDFVsPurityTrack, 2);  
        
        H2F h2_chi2OverNDFVsEfficiencyTrack = new H2F("chi2OverNDFVsEfficiencyTrack", "chi2/ndf vs purity for matched tracks", 100, 0, 1.01, 100, 0, 100);
        h2_chi2OverNDFVsEfficiencyTrack.setTitleX("efficiency");
        h2_chi2OverNDFVsEfficiencyTrack.setTitleY("chi2/ndf");
        histoGroupOverview.addDataSet(h2_chi2OverNDFVsEfficiencyTrack, 3);         
        
        H1F h1_numMatchedClusters = new H1F("numMatchedClusters", "# of matched clusters for matched tracks", 7, -0.5, 6.5);
        h1_numMatchedClusters.setTitleX("# of fully matched clusters for matched tracks");
        h1_numMatchedClusters.setTitleY("counts");
        histoGroupOverview.addDataSet(h1_numMatchedClusters, 4);          
        
        H2F h2_purityVsEfficiencyCluster = new H2F("purityVsEfficiencyCluster", "purity vs efficiency for clusters of matched tracks", 25, 0, 1.01, 25, 0, 1.01);
        h2_purityVsEfficiencyCluster.setTitleX("efficiency");
        h2_purityVsEfficiencyCluster.setTitleY("purity");
        histoGroupOverview.addDataSet(h2_purityVsEfficiencyCluster, 5);
        
        H1F h1_shiftAvgWireClusters = new H1F("shiftAvgWireClusters", "shift of avgWire for non-fully matched clusters of matched tracks", 100, -5, 5);
        h1_shiftAvgWireClusters.setTitleX("shift of avgWire for non-fully matched clusters of matched tracks");
        h1_shiftAvgWireClusters.setTitleY("counts");
        histoGroupOverview.addDataSet(h1_shiftAvgWireClusters, 6);         
          
        histoGroupMap.put(histoGroupOverview.getName(), histoGroupOverview); 
        
    }
    
    public void processEvent(Event event1, Event event2, Map<Track, List<Track>> map_trk1_trk2List) {  
        //// Read banks
        LocalEvent localEvent1 = new LocalEvent(reader1, event1, 12);
        LocalEvent localEvent2 = new LocalEvent(reader2, event2, 11);
        
        List<Track> trackList1 = new ArrayList();    
        List<Track> trackList2 = localEvent2.getTracksHB();

        for(Track trk : localEvent1.getTracksTB()){
            if(trk.isValid()) {
                trackList1.add(trk);
            }
        }
        
        for(Track trk2 : trackList2){
            if(trk2.getNumClusters() == 6){
                int mostMatchedHits = 0;
                int indexMatchedTrack = -1;
                for(int i = 0; i < trackList1.size(); i++){
                    Track trk1 = trackList1.get(i);
                    if(trk1.getNumClusters() == 6 && trk1.sector() == trk2.sector()){
                        int numMatchedHits = trk1.numMatchedHitsNoRequireTDC(trk2);
                        if(numMatchedHits > mostMatchedHits){
                            mostMatchedHits = numMatchedHits;
                            indexMatchedTrack = i;
                        }
                    }
                }            
                
                if(indexMatchedTrack != -1){
                    Track trk1 = trackList1.get(indexMatchedTrack);
                    if(trk1.numMatchedClustersNoRequireTDC(trk2) >= NUMMATCHEDCLUSTERSCUT) {
                        if(map_trk1_trk2List.keySet().contains(trk1)) {
                            map_trk1_trk2List.get(trk1).add(trk2);                        
                        }
                        else{
                            List<Track> trk2List = new ArrayList();
                            trk2List.add(trk2);
                            map_trk1_trk2List.put(trk1, trk2List);
                        }
                    }

                }
            }
        }
        
        HistoGroup histoGroupOverview = histoGroupMap.get("overview");
        for(Track trk1 : map_trk1_trk2List.keySet()){
            List<Track> trk2List = map_trk1_trk2List.get(trk1);
            histoGroupOverview.getH1F("numMatchedTracks").fill(trk2List.size());
            for(Track trk2 : trk2List){
                histoGroupOverview.getH1F("numMatchedClusters").fill(trk2.numMatchedClustersNoRequireTDC(trk1));
                double purityTrk = trk2.numMatchedHitsNoRequireTDC(trk1)/(double)trk2.getHits().size();
                double efficiencyTrk = trk2.numMatchedHitsNoRequireTDC(trk1)/(double)trk1.getHits().size();
                histoGroupOverview.getH2F("purityVsEfficiencyTrack").fill(efficiencyTrk, purityTrk);
                histoGroupOverview.getH2F("chi2OverNDFVsPurityTrack").fill(purityTrk, trk2.chi2()/trk2.NDF());
                histoGroupOverview.getH2F("chi2OverNDFVsEfficiencyTrack").fill(efficiencyTrk, trk2.chi2()/trk2.NDF());
                for(Cluster cls2 : trk2.getClusters()){
                    for(Cluster cls1 : trk1.getClusters()){
                        if(cls2.superlayer()== cls1.superlayer()){
                            double purityCls = cls2.numMatchedHitsNoRequireTDC(cls1)/(double)cls2.getHits().size();
                            double efficiencyCls = cls2.numMatchedHitsNoRequireTDC(cls1)/(double)cls1.getHits().size();
                            histoGroupOverview.getH2F("purityVsEfficiencyCluster").fill(efficiencyCls, purityCls);
                            if(!cls2.isFullMatchedClusterNoRequireTDC(cls1))
                                histoGroupOverview.getH1F("shiftAvgWireClusters").fill(cls2.avgWire() - cls1.avgWire());                            
                            break;
                        }
                    }
                }
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
        parser.addOption("-histo", "0", "read histogram file (0/1)");
        parser.parse(args);

        String namePrefix = parser.getOption("-o").stringValue();
        int maxEvents = parser.getOption("-n").intValue();
        boolean displayPlots = (parser.getOption("-plot").intValue() != 0);
        boolean readHistos = (parser.getOption("-histo").intValue() != 0);
        
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
                
        ExtractMatchedTracks analysis = new ExtractMatchedTracks();
        analysis.createHistoGroupMap();
        
        if (!readHistos) {
            HipoReader reader1 = new HipoReader();
            reader1.open(inputList.get(0));
            HipoReader reader2 = new HipoReader();
            reader2.open(inputList.get(1));

            SchemaFactory schema1 = reader1.getSchemaFactory();
            SchemaFactory schema2 = reader2.getSchemaFactory();
            analysis.initReader(new Banks(schema1), new Banks(schema2));
            
            HipoWriterSorted writer1 = new HipoWriterSorted();
            writer1.getSchemaFactory().copy(schema1);
            writer1.setCompressionType(2);   
            writer1.open("sample1.hipo");
            
            HipoWriterSorted writer2 = new HipoWriterSorted();
            writer2.getSchemaFactory().copy(schema2);
            writer2.setCompressionType(2);   
            writer2.open("sample2.hipo");

            int counter = 0;
            Event event1 = new Event();
            Event event2 = new Event();

            ProgressPrintout progress = new ProgressPrintout();
            while (reader1.hasNext() && reader2.hasNext()) {

                counter++;

                reader1.nextEvent(event1);
                reader2.nextEvent(event2);
                
                Map<Track, List<Track>> map_trk1_trk2List = new HashMap();

                analysis.processEvent(event1, event2, map_trk1_trk2List);
                
                int numTrk1 = 0;
                int numTrk2 = 0;
                int numCls1 = 0;
                int numCls2 = 0;
                for(Track trk1 : map_trk1_trk2List.keySet()){
                    numTrk1++;
                    numCls1+= trk1.getNumClusters();
                    numTrk2 += map_trk1_trk2List.get(trk1).size();
                    for(Track trk2 : map_trk1_trk2List.get(trk1)){
                        numCls2+= trk2.getNumClusters();
                    }
                }
                                
                Bank newTrackBank1 = new Bank(schema1.getSchema("TimeBasedTrkg::TBTracks"), numTrk1);
                Bank newTrackBank2 = new Bank(schema2.getSchema("HitBasedTrkg::HBTracks"), numTrk2); 
                Bank newClusterBank1 = new Bank(schema1.getSchema("HitBasedTrkg::Clusters"), numCls1);
                Bank newClusterBank2 = new Bank(schema2.getSchema("HitBasedTrkg::Clusters"), numCls2);
                int rowTrk1 = 0;
                int rowTrk2 = 0;
                int rowCls1 = 0;
                int rowCls2 = 0;
                for(Track trk1 : map_trk1_trk2List.keySet()){                    
                    newTrackBank1.putShort("id", rowTrk1, (short) trk1.id());                     
                    newTrackBank1.putByte("sector", rowTrk1, (byte) trk1.sector());
                    newTrackBank1.putByte("q", rowTrk1, (byte) trk1.charge());
                    newTrackBank1.putFloat("Vtx0_x", rowTrk1, (float)trk1.vx());
                    newTrackBank1.putFloat("Vtx0_y", rowTrk1, (float)trk1.vy());
                    newTrackBank1.putFloat("Vtx0_z", rowTrk1, (float)trk1.vz());
                    newTrackBank1.putFloat("p0_x", rowTrk1, (float)trk1.px());
                    newTrackBank1.putFloat("p0_y", rowTrk1, (float)trk1.py());   
                    newTrackBank1.putFloat("p0_z", rowTrk1, (float)trk1.pz()); 
                    newTrackBank1.putFloat("chi2", rowTrk1, (float) trk1.chi2());
                    newTrackBank1.putShort("ndf", rowTrk1, (short) trk1.NDF());
                    
                    for(int r = 0; r < 6; r++) {
                        newTrackBank1.putShort("Cluster"+String.valueOf(r+1)+"_ID", rowTrk1, (short)trk1.clusterIds()[r]);
                    }                    
                    
                    rowTrk1++;
                    
                    for(Cluster cls1 : trk1.getClusters()){
                        newClusterBank1.putShort("id", rowCls1, (short) cls1.id());                    
                        newClusterBank1.putShort("status", rowCls1, (short) cls1.status());    
                        newClusterBank1.putByte("sector", rowCls1, (byte) cls1.sector());
                        newClusterBank1.putByte("superlayer", rowCls1, (byte) cls1.superlayer());                
                        newClusterBank1.putByte("size", rowCls1, (byte) cls1.size());
                        newClusterBank1.putFloat("avgWire", rowCls1, (float)cls1.avgWire()); 
                        newClusterBank1.putFloat("fitSlope", rowCls1, (float)cls1.fitSlope()); 
                        
                        rowCls1++;
                    }
                    
                    for(Track trk2 : map_trk1_trk2List.get(trk1)){
                        newTrackBank2.putShort("id", rowTrk2, (short) trk2.id());                    
                        newTrackBank2.putShort("status", rowTrk2, (short) trk1.id());    
                        newTrackBank2.putByte("sector", rowTrk2, (byte) trk2.sector());
                        newTrackBank2.putByte("q", rowTrk2, (byte) trk2.charge());
                        newTrackBank2.putFloat("Vtx0_x", rowTrk2, (float)trk2.vx());
                        newTrackBank2.putFloat("Vtx0_y", rowTrk2, (float)trk2.vy());
                        newTrackBank2.putFloat("Vtx0_z", rowTrk2, (float)trk2.vz());
                        newTrackBank2.putFloat("p0_x", rowTrk2, (float)trk2.px());
                        newTrackBank2.putFloat("p0_y", rowTrk2, (float)trk2.py());   
                        newTrackBank2.putFloat("p0_z", rowTrk2, (float)trk2.pz());
                        newTrackBank2.putFloat("chi2", rowTrk2, (float) trk2.chi2());
                        newTrackBank2.putShort("ndf", rowTrk2, (short) trk2.NDF());
                        double purity = trk2.numMatchedHitsNoRequireTDC(trk1)/(double)trk2.getHits().size();
                        double efficiency = trk2.numMatchedHitsNoRequireTDC(trk1)/(double)trk1.getHits().size();
                        newTrackBank2.putFloat("c1_x", rowTrk2, (float) purity);
                        newTrackBank2.putFloat("c1_y", rowTrk2, (float) efficiency);

                        int numMatchedClusters = trk2.numMatchedClusters(trk1);
                        newTrackBank2.putShort("Cross1_ID", rowTrk2, (short) numMatchedClusters); 
                        
                        for(int r = 0; r < 6; r++) {
                            newTrackBank2.putShort("Cluster"+String.valueOf(r+1)+"_ID", rowTrk2, (short)trk2.clusterIds()[r]);
                        }
                        
                        rowTrk2++;
                        
                        for(Cluster cls2 : trk2.getClusters()){
                            newClusterBank2.putShort("id", rowCls2, (short) cls2.id());                                                  
                            newClusterBank2.putByte("sector", rowCls2, (byte) cls2.sector());
                            newClusterBank2.putByte("superlayer", rowCls2, (byte) cls2.superlayer());                
                            newClusterBank2.putByte("size", rowCls2, (byte) cls2.size());
                            newClusterBank2.putFloat("avgWire", rowCls2, (float)cls2.avgWire()); 
                            newClusterBank2.putFloat("fitSlope", rowCls2, (float)cls2.fitSlope());
                            
                            int mappedCls1Id = -1;
                            double purityCls = -999.;
                            double efficiencyCls = -999.;
                            double shiftAvgWire = -999.;
                            for(Cluster cls1 : trk1.getClusters()){
                                if(cls2.superlayer()== cls1.superlayer()){                                    
                                    mappedCls1Id = cls1.id();
                                    purityCls = cls2.numMatchedHitsNoRequireTDC(cls1)/(double)cls2.getHits().size();
                                    efficiencyCls = cls2.numMatchedHitsNoRequireTDC(cls1)/(double)cls1.getHits().size();
                                    shiftAvgWire = cls2.avgWire() - cls1.avgWire();                           
                                    break;
                                }
                            }
                            newClusterBank2.putShort("status", rowCls2, (short) mappedCls1Id);  
                            newClusterBank2.putFloat("fitChisqProb", rowCls2, (float)shiftAvgWire); 
                            newClusterBank2.putFloat("fitInterc", rowCls2, (float)purityCls); 
                            newClusterBank2.putFloat("fitIntercErr", rowCls2, (float)efficiencyCls); 

                            rowCls2++;
                        }
                    }
                }                
                
                Event newEvent1 = new Event();
                Event newEvent2 = new Event();
                if(rowTrk2 > 0){              
                    newEvent1.write(newTrackBank1); 
                    newEvent1.write(newClusterBank1); 
                    newEvent2.write(newTrackBank2);
                    newEvent2.write(newClusterBank2); 

                    writer1.addEvent(newEvent1);
                    writer2.addEvent(newEvent2);
                }
                
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
            writer1.close();
            writer2.close();
            analysis.saveHistos(histoName);
        } else {
            analysis.readHistos(inputList.get(0));
        }

        if (displayPlots) {
            JFrame frame = new JFrame();
            EmbeddedCanvasTabbed canvas = analysis.plotHistos();
            if(canvas != null){
                frame.setSize(1600, 800);
                frame.add(canvas);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        }  
    }
}