package org.clas.analysis.newAIModel;

import java.io.FileWriter;
import java.io.IOException;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

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
public class HitsLabeledByTracksMC {

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
    
    private static List<String> getInputListFromFile(String listFile) throws IOException {
        List<String> files = new ArrayList<>();
        try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(listFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty() && !line.startsWith("#")) {
                    files.add(line);
                }
            }
        }
        return files;
    }
    
    /**
     * Resolve input path: can be a .hipo file, multiple files separated by
     * commas or spaces, a directory, or a wildcard pattern like /path/*.hipo.
     */
    private static List<String> resolveInputFiles(String pathPattern) throws IOException {
        List<String> files = new ArrayList<>();

        if (pathPattern == null || pathPattern.isEmpty()) {
            return files;
        }

        java.nio.file.Path path = java.nio.file.Paths.get(pathPattern);
        java.io.File f = path.toFile();

        // Case 1: exact file path
        if (f.exists() && f.isFile()) {
            files.add(f.getAbsolutePath());
            return files;
        }

        // Case 2: directory -> add all .hipo files
        if (f.exists() && f.isDirectory()) {
            java.io.File[] hipoFiles = f.listFiles((dir, name) -> name.endsWith(".hipo"));
            if (hipoFiles != null) {
                for (java.io.File hf : hipoFiles) {
                    files.add(hf.getAbsolutePath());
                }
            }
            return files;
        }

        // Case 3: wildcard pattern (e.g. /path/*.hipo)
        if (pathPattern.contains("*") || pathPattern.contains("?")) {
            java.nio.file.Path parent = path.getParent();
            if (parent == null) {
                parent = java.nio.file.Paths.get(".");
            }
            final String patternOnly = path.getFileName().toString();
            java.nio.file.DirectoryStream<java.nio.file.Path> stream
                    = java.nio.file.Files.newDirectoryStream(parent, patternOnly);
            for (java.nio.file.Path p : stream) {
                files.add(p.toAbsolutePath().toString());
            }
            stream.close();
            return files;
        }

        // Case 4: multiple files separated by commas or spaces
        for (String token : pathPattern.split("[,\\s]+")) {
            if (!token.isEmpty()) {
                files.add(token);
            }
        }

        return files;
    }


    public static void main(String[] args) throws IOException {
        OptionParser parser = new OptionParser("extractEvents");
        parser.setRequiresInputList(false);
        // valid options for event-base analysis
        parser.addOption("-o", "", "output file name prefix");
        parser.addOption("-n", "1000000", "maximum output entries");
        parser.addOption("-p", "", "input file list 1 (pure samples)");
        parser.addOption("-b", "", "input file list 2 (bg samples)");
        parser.addOption("-trkType"    ,"12",   "tracking type: ConvTB(12), AITB(22)");

        parser.parse(args);

        String namePrefix = parser.getOption("-o").stringValue();
        int maxOutputEntries = parser.getOption("-n").intValue();
        int trkType = parser.getOption("-trkType").intValue(); 
        
        String pureFileList   = parser.getOption("-p").stringValue();
        String bgFileList = parser.getOption("-b").stringValue();
        
        List<String> pureFiles = new ArrayList<>();
        List<String> bgFiles = new ArrayList<>();

        pureFiles.addAll(resolveInputFiles(pureFileList));
        bgFiles.addAll(resolveInputFiles(bgFileList));

        if (pureFiles.size() != bgFiles.size()) {
            LOGGER.log(Level.WARNING, String.format(
                "Warning: number of pure files (%d) != bg files (%d). Will process up to the smaller size.",
                pureFiles.size(), bgFiles.size()));
        }

        String outputName = "hits.csv";
        if (!namePrefix.isEmpty()) {
            outputName = namePrefix + "_" + outputName;
        }
        
        // Prepare FileWriters for all 6 sectors
        Map<Integer, FileWriter> sectorWriters = new HashMap<>();
        Map<Integer, Integer> sectorCounters = new HashMap();
        for (int sector = 1; sector <= 6; sector++) {
            String sectorOutputName = outputName.replace(".csv", "_sector" + sector + ".csv");
            FileWriter writer = new FileWriter(sectorOutputName);
            sectorWriters.put(sector, writer);            
            sectorCounters.put(sector, 0);
        }

        ProgressPrintout progress = new ProgressPrintout();
        
        
        for (int iFile = 0; iFile < pureFiles.size(); iFile++) {
            String filePure = pureFiles.get(iFile);   
            String fileBg = bgFiles.get(iFile);
            
            HipoReader readerPure = new HipoReader();
            readerPure.open(filePure);
            SchemaFactory schemaPure = readerPure.getSchemaFactory();
            Reader localReaderPure = new Reader(new Banks(schemaPure));
            
            HipoReader readerBg = new HipoReader();
            readerBg.open(fileBg);
            SchemaFactory schemaBg = readerBg.getSchemaFactory();
            Reader localReaderBg = new Reader(new Banks(schemaBg));
            
            Event eventPure = new Event();
            Event eventBg = new Event();
            while (readerPure.hasNext() && readerBg.hasNext()) {                
                readerPure.nextEvent(eventPure);
                readerBg.nextEvent(eventBg);
                LocalEvent localEventPure = new LocalEvent(localReaderPure, eventPure, trkType);
                LocalEvent localEventBg = new LocalEvent(localReaderBg, eventBg, trkType);                
                
                // Hits on HB tracks from pure sample
                List<Hit> hitsInTracksPure = new ArrayList();                
                for(Track trkPure : localEventPure.getTracksHB()){
                    hitsInTracksPure.addAll(trkPure.getHits());
                }
                // Hits on TB tracks with most noise hits from bg sample
                List<Hit> hitsInTracksBg = new ArrayList();
                for(Track trkBg : localEventBg.getTracksTB()){
                    if(trkBg.chi2()/trkBg.NDF() < 4 && trkBg.getRatioNormalHits() < 0.1){
                        hitsInTracksBg.addAll(trkBg.getHits());
                    }
                }
                // All hits on tracks
                List<Hit> hitsInTracks = new ArrayList();
                hitsInTracks.addAll(hitsInTracksPure);
                hitsInTracks.addAll(hitsInTracksBg);
                                
                int[][][] tdcs = new int[6][NLAYERS][NWIRES];
                int[][][] tdcsInTracks = new int[6][NLAYERS][NWIRES];                                
                for(TDC tdc : localEventBg.getTDCs()){
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
                        int counter = sectorCounters.get(sector) + 1;
                        sectorCounters.put(sector, counter);
                                          
                        // DC::tdc
                        for (int l = 0; l < NLAYERS; l++) {
                            for (int w = 0; w < NWIRES; w++) {
                                sectorWriters.get(sector).write(tdcs[sector-1][l][w] + (w < NWIRES - 1 ? "," : ""));
                            }
                            sectorWriters.get(sector).write("\n");
                        }
                        sectorWriters.get(sector).write("\n");

                        // Hits on tracks
                        for (int l = 0; l < NLAYERS; l++) {
                            for (int w = 0; w < NWIRES; w++) {
                                sectorWriters.get(sector).write(tdcsInTracks[sector-1][l][w] + (w < NWIRES - 1 ? "," : ""));
                            }
                            sectorWriters.get(sector).write("\n");
                        }
                        sectorWriters.get(sector).write("\n\n");
                    }
                } 
                
                if ((maxOutputEntries > 0 && sectorCounters.containsValue(maxOutputEntries))) break;
                progress.updateStatus();
            }    
            
            readerPure.close();
            readerBg.close();
        }
        
        for (FileWriter writer : sectorWriters.values()) {
            writer.flush();
            writer.close();
        }
                
    }            
}
