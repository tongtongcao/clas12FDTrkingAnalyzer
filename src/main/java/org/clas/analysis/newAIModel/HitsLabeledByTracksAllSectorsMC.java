package org.clas.analysis.newAIModel;

import java.io.FileWriter;
import java.io.IOException;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.nio.file.*;
import java.io.File;  

import org.jlab.utils.options.OptionParser;
import org.jlab.jnp.hipo4.io.HipoReader;
import org.jlab.jnp.hipo4.data.Event;
import org.jlab.jnp.hipo4.data.Bank;
import org.jlab.jnp.hipo4.data.SchemaFactory;
import org.jlab.jnp.hipo4.io.HipoWriterSorted;
import org.jlab.utils.benchmark.ProgressPrintout;

import org.clas.element.RunConfig;
import org.clas.element.TDC;
import org.clas.element.Hit;
import org.clas.element.Track;
import org.clas.element.Cluster;
import org.clas.reader.Reader;
import org.clas.reader.Banks;
import org.clas.reader.LocalEvent;

/**
 * Extract average wires of clusters for 6-cluster valid TB tracks from MC samples, and save
 * them into a csv file The csv file will be input as training AI model of
 * average wire estimator for a missing cluster
 *
 * @author Tongtong
 */
public class HitsLabeledByTracksAllSectorsMC {

    private static final int NLAYERS = 36;
    private static final int NWIRES  = 112;
    
    private static final Logger LOGGER = Logger.getLogger(Reader.class.getName());
    
    private static boolean hasHits(int[][] arr) {
        for (int l = 0; l < NLAYERS; l++) {
            for (int w = 0; w < NWIRES; w++) {
                if (arr[l][w] != 0) return true;
            }
        }
        return false;
    }
    
    public static void main(String[] args) throws IOException {
        OptionParser parser = new OptionParser("extractEvents");
        parser.setRequiresInputList(false);
        // valid options for event-base analysis
        parser.addOption("-o", "", "output file name prefix");
        parser.addOption("-n", "1000000", "maximum output entries");
        parser.addOption("-trkType"    ,"12",   "tracking type: ConvTB(12), AITB(22)");
        
        // Optional single file mode
        parser.addOption("-p", "", "single pure file or pure file folder");
        parser.addOption("-b", "", "single bg file");
        parser.setRequiresInputList(true); // for multiple bg files mode

        parser.parse(args);

        String namePrefix = parser.getOption("-o").stringValue();
        int maxOutputEntries = parser.getOption("-n").intValue();
        int trkType = parser.getOption("-trkType").intValue(); 
        
        List<String> pureFiles;
        List<String> bgFiles;

        if (!parser.getOption("-b").stringValue().isEmpty()) {
            // -p/-b single file mode
            pureFiles = Arrays.asList(parser.getOption("-p").stringValue());
            bgFiles   = Arrays.asList(parser.getOption("-b").stringValue());
        } else {
            // Multiple pure files + background folder mode
            bgFiles = parser.getInputList();
            String pureFolderStr = parser.getOption("-p").stringValue();
            File pureFolder = new File(pureFolderStr);

            pureFiles = new ArrayList<>();
            for (String bgPath : bgFiles) {
                File bgFile = new File(bgPath);
                File pureFile = new File(pureFolder, bgFile.getName());
                if (!pureFile.exists())
                    throw new IllegalArgumentException("pure file not found: " + pureFile.getName());
                pureFiles.add(pureFile.getAbsolutePath());
            }
        }

        // Process up to min size if both provided
        int nPairs = pureFiles.size();
        if (!bgFiles.isEmpty()) {
            nPairs = Math.min(pureFiles.size(), bgFiles.size());
            if (pureFiles.size() != bgFiles.size()) {
                LOGGER.log(Level.WARNING, String.format(
                    "Warning: number of pure files (%d) != bg files (%d). Will process %d pairs.",
                    pureFiles.size(), bgFiles.size(), nPairs));
            }
        } else {
            // If no bgFiles, process pureFiles alone (read from pureFiles but bg not available)
            LOGGER.log(Level.INFO, "Processing " + pureFiles.size() + " pure files (no bg files).");
        }

        String outputName = "hits.csv";
        if (namePrefix != null && !namePrefix.isEmpty()) {
            outputName = namePrefix + "_" + outputName;
        }
        
        // Prepare FileWriters for all 6 sectors
        FileWriter writer = new FileWriter(outputName);
        int counter = 0; 

        ProgressPrintout progress = new ProgressPrintout();
        
        
        for (int iFile = 0; iFile < nPairs; iFile++) {
            String filePure = pureFiles.get(iFile);   
            String fileBg = bgFiles.isEmpty() ? null : bgFiles.get(iFile);
            
            LOGGER.info("Processing pair " + (iFile+1) + ": pure=" + filePure + " bg=" + fileBg);
            
            HipoReader readerPure = null;
            HipoReader readerBg = null;
            try {
                readerPure = new HipoReader();
                readerPure.open(filePure);
                SchemaFactory schemaPure = readerPure.getSchemaFactory();
                Reader localReaderPure = new Reader(new Banks(schemaPure));
                
                if (fileBg != null) {
                    readerBg = new HipoReader();
                    readerBg.open(fileBg);
                }
                SchemaFactory schemaBg = readerBg != null ? readerBg.getSchemaFactory() : null;
                Reader localReaderBg = schemaBg != null ? new Reader(new Banks(schemaBg)) : null;
                
                Event eventPure = new Event();
                Event eventBg = new Event();
                while ((readerPure.hasNext()) && (readerBg == null || readerBg.hasNext())) {                
                    readerPure.nextEvent(eventPure);
                    if (readerBg != null) readerBg.nextEvent(eventBg);

                    LocalEvent localEventPure = new LocalEvent(localReaderPure, eventPure, trkType);
                    LocalEvent localEventBg = readerBg != null ? new LocalEvent(localReaderBg, eventBg, trkType) : null;                
                
                    // Hits on HB tracks from pure sample
                    List<Hit> hitsInTracksPure = new ArrayList<>();                
                    for(Track trkPure : localEventPure.getTracksHB()){
                        hitsInTracksPure.addAll(trkPure.getHits());
                    }
                    // Hits on TB tracks with most noise hits from bg sample (if available)
                    List<Hit> hitsInTracksBg = new ArrayList<>();
                    if (localEventBg != null) {
                        for(Track trkBg : localEventBg.getTracksTB()){
                            if(trkBg.chi2()/trkBg.NDF() < 8 && trkBg.getRatioNormalHits() < 0.1){
                                hitsInTracksBg.addAll(trkBg.getHits());
                            }
                        }
                    }
                    // All hits on tracks
                    List<Hit> hitsInTracks = new ArrayList<>();
                    hitsInTracks.addAll(hitsInTracksPure);
                    hitsInTracks.addAll(hitsInTracksBg);
                                    
                    int[][][] tdcs = new int[6][NLAYERS][NWIRES];
                    int[][][] tdcsInTracks = new int[6][NLAYERS][NWIRES];                                
                    // If bg reader present use its TDCs; otherwise use pure reader's TDCs
                    Iterable<TDC> tdcIterable = (localEventBg != null) ? localEventBg.getTDCs() : localEventPure.getTDCs();
                    for(TDC tdc : tdcIterable){
                        int sector = tdc.sector();
                        int superlayer = tdc.superlayer();
                        int layer = tdc.layer();
                        int wire = tdc.component();                            
                        tdcs[sector-1][(superlayer-1)*6 + layer-1][wire-1] = 1;
                        
                        for(Hit hitInTracks : hitsInTracks){
                            if(tdc.matchHit(hitInTracks)){
                                tdcsInTracks[sector-1][(superlayer-1)*6 + layer-1][wire-1] = 1;
                                break;
                            }
                        }
                    } 
                    
                    for(int sector = 1; sector <= 6; sector++){
                        if(hasHits(tdcsInTracks[sector-1])){
                            counter++;
                                              
                            // DC::tdc
                            for (int l = 0; l < NLAYERS; l++) {
                                for (int w = 0; w < NWIRES; w++) {
                                    writer.write(tdcs[sector-1][l][w] + (w < NWIRES - 1 ? "," : ""));
                                }
                                writer.write("\n");
                            }
                            writer.write("\n");

                            // Hits on tracks
                            for (int l = 0; l < NLAYERS; l++) {
                                for (int w = 0; w < NWIRES; w++) {
                                    writer.write(tdcsInTracks[sector-1][l][w] + (w < NWIRES - 1 ? "," : ""));
                                }
                                writer.write("\n");
                            }
                            writer.write("\n\n");
                        }
                    } 
                    
                    // Break if any sector reached maxOutputEntries
                    if (maxOutputEntries > 0 && counter >= maxOutputEntries) break;
                    progress.updateStatus();
                } // end while events
                
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Error processing files: " + filePure + " / " + fileBg, e);
            } finally {
                try {
                    if (readerPure != null) readerPure.close();
                } catch (Exception e) {
                    LOGGER.log(Level.FINE, "Error closing readerPure", e);
                }
                try {
                    if (readerBg != null) readerBg.close();
                } catch (Exception e) {
                    LOGGER.log(Level.FINE, "Error closing readerBg", e);
                }
            }
        } // end for files
        
        try {
            writer.flush();
            writer.close();
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Error closing writer", e);
        }
                
    }            
}
