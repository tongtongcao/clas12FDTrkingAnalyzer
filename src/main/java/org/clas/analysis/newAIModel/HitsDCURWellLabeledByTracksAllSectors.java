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
import org.clas.element.URWellADC;
import org.clas.element.URWellHit;
import org.clas.element.URWellCluster;
import org.clas.element.URWellCross;
import org.clas.reader.Reader;
import org.clas.reader.Banks;
import org.clas.reader.LocalEvent;

/**
 * Label DC&uRWell hits based on tracks
 *
 * @author Tongtong
 */
public class HitsDCURWellLabeledByTracksAllSectors {

    private static final int NLAYERS = 36;
    private static final int NWIRES  = 112;
    
    private static final int NLAYERSURWell = 4;
    private static final int NWIRESURWell  = 1485;
    
    private static final Logger LOGGER = Logger.getLogger(Reader.class.getName());
    
    private static boolean hasHits(int[][] arr) {
        for (int l = 0; l < NLAYERS; l++) {
            for (int w = 0; w < NWIRES; w++) {
                if (arr[l][w] != 0) return true;
            }
        }
        return false;
    }
    
    private static boolean hasHitsURWell(int[][] arr) {
        for (int l = 0; l < NLAYERSURWell; l++) {
            for (int w = 0; w < NWIRESURWell; w++) {
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
        
        FileWriter writer = new FileWriter(outputName);
        int counter = 0;        

        ProgressPrintout progress = new ProgressPrintout();
        
        
        for (String input : inputList) {
            HipoReader reader = new HipoReader();
            reader.open(input);
            SchemaFactory schema = reader.getSchemaFactory();

            Reader localReader = new Reader(new Banks(schema));
            Event event = new Event();
            while (reader.hasNext()) {                
                reader.nextEvent(event);
                LocalEvent localEvent = new LocalEvent(localReader, event, trkType, true);

                // DC
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
                
                // uRWell
                int[][][] adcsURWell = new int[6][NLAYERSURWell][NWIRESURWell];
                int[][][] tbHitsURWell = new int[6][NLAYERSURWell][NWIRESURWell];
                for(URWellADC adc : localEvent.getURWellADCs()){
                    int sector = adc.sector();
                    int layer = adc.layer();
                    int strip = adc.component();                            
                    if(adc.isRemainedAfterDecoding()) adcsURWell[sector-1][layer-1][strip-1] = 1;
                }
                for(URWellCross urcrs : localEvent.getURWellCrossesTB()){
                    for(URWellHit hit : urcrs.getCluster1().getHits()){
                        int sector = hit.sector();
                        int layer = hit.layer();
                        int strip = hit.strip();                            
                        tbHitsURWell[sector-1][layer-1][strip-1] = 1;
                    }
                    for(URWellHit hit : urcrs.getCluster2().getHits()){
                        int sector = hit.sector();
                        int layer = hit.layer();
                        int strip = hit.strip();                            
                        tbHitsURWell[sector-1][layer-1][strip-1] = 1;
                    }                    
                }                
                
                // Save information                
                for(int sector = 1; sector <= 6; sector++){
                    if(hasHits(tbHits[sector-1])){
                        counter++;
                                          
                        // DC::tdc
                        for (int l = 0; l < NLAYERS; l++) {
                            for (int w = 0; w < NWIRES; w++) {
                                writer.write(tdcs[sector-1][l][w] + (w < NWIRES - 1 ? "," : ""));
                            }
                            writer.write("\n");
                        }
                        writer.write("\n");

                        // TBHits on DC
                        for (int l = 0; l < NLAYERS; l++) {
                            for (int w = 0; w < NWIRES; w++) {
                                writer.write(tbHits[sector-1][l][w] + (w < NWIRES - 1 ? "," : ""));
                            }
                            writer.write("\n");
                        }
                        writer.write("\n");
                        
                        // URWELL::adc
                        for (int l = 0; l < NLAYERSURWell; l++) {
                            for (int s = 0; s < NWIRESURWell; s++) {
                                writer.write(adcsURWell[sector-1][l][s] + (s < NWIRES - 1 ? "," : ""));
                            }
                            writer.write("\n");
                        }
                        writer.write("\n");

                        // TBHits on uRWells
                        for (int l = 0; l < NLAYERSURWell; l++) {
                            for (int s = 0; s < NWIRESURWell; s++) {
                                writer.write(tbHitsURWell[sector-1][l][s] + (s < NWIRES - 1 ? "," : ""));
                            }
                            writer.write("\n");
                        }
                        
                        
                        writer.write("\n\n");
                    }
                } 
                
                if ((maxOutputEntries > 0 && counter >= maxOutputEntries)) break;
                progress.updateStatus();
            }    
            
            reader.close();
        }
                
        writer.flush();
        writer.close();                        
    }            
}
