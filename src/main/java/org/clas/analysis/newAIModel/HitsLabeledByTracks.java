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
 * Extract average wires of clusters for 6-cluster valid TB tracks, and save
 * them into a csv file The csv file will be input as training AI model of
 * average wire estimator for a missing cluster
 *
 * @author Tongtong
 */
public class HitsLabeledByTracks {

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
        parser.addOption("-trkType", "22", "tracking type: ConvTB(12), AITB(22)");

        parser.parse(args);

        String namePrefix = parser.getOption("-o").stringValue();
        int maxOutputEntries = parser.getOption("-n").intValue();
        int trkType = parser.getOption("-trkType").intValue();

        List<String> inputList = parser.getInputList();
        if (inputList.isEmpty() == true) {
            parser.printUsage();
            System.out.println("\n >>>> error: no input file is specified....\n");
            System.exit(0);
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
        
        
        for (String input : inputList) {
            HipoReader reader = new HipoReader();
            reader.open(input);
            SchemaFactory schema = reader.getSchemaFactory();

            Reader localReader = new Reader(new Banks(schema));
            Event event = new Event();
            while (reader.hasNext()) {                
                reader.nextEvent(event);
                LocalEvent localEvent = new LocalEvent(localReader, event, trkType);

                int[][][] tdcs = new int[6][NLAYERS][NWIRES];
                int[][][] tbHits = new int[6][NLAYERS][NWIRES];
                for(TDC tdc : localEvent.getTDCs()){
                    int sector = tdc.sector();
                    int superlayer = tdc.superlayer();
                    int layer = tdc.layer();
                    int wire = tdc.component();                            
                    if(tdc.isRemainedAfterDecoding()) tdcs[sector-1][(superlayer-1)*6 + layer-1][wire-1] = 1;
                }
                for(Hit hit : localEvent.getHitsTB()){
                    int sector = hit.sector();
                    int superlayer = hit.superlayer();
                    int layer = hit.layer();
                    int wire = hit.wire();                            
                    tbHits[sector-1][(superlayer-1)*6 + layer-1][wire-1] = 1;
                }
                
                for(int sector = 1; sector <= 6; sector++){
                    if(hasHits(tbHits[sector-1])){
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

                        // TBHits
                        for (int l = 0; l < NLAYERS; l++) {
                            for (int w = 0; w < NWIRES; w++) {
                                sectorWriters.get(sector).write(tbHits[sector-1][l][w] + (w < NWIRES - 1 ? "," : ""));
                            }
                            sectorWriters.get(sector).write("\n");
                        }
                        sectorWriters.get(sector).write("\n\n");
                    }
                } 
                
                if ((maxOutputEntries > 0 && sectorCounters.containsValue(maxOutputEntries))) break;
                progress.updateStatus();
            }    
            
            reader.close();
        }
        
        for (FileWriter writer : sectorWriters.values()) {
            writer.flush();
            writer.close();
        }
                
    }            
}
