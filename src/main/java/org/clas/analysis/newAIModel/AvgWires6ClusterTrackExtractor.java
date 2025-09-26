package org.clas.analysis.newAIModel;

import java.io.FileWriter;
import java.io.IOException;

import java.util.List;
import java.util.ArrayList;
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
import org.clas.element.Track;
import org.clas.element.Cluster;
import org.clas.reader.Reader;
import org.clas.reader.Banks;
import org.clas.reader.LocalEvent;

/**
 * Extract average wires of clusters for 6-cluster valid TB tracks, and save them into a csv file
 * The csv file will be input as training AI model of average wire estimator for a missing cluster
 * @author Tongtong
 */

public class AvgWires6ClusterTrackExtractor{   
    private static final Logger LOGGER = Logger.getLogger(Reader.class.getName()); 
    
    public static void main(String[] args) throws IOException {
        OptionParser parser = new OptionParser("extractEvents");
        parser.setRequiresInputList(false);
        // valid options for event-base analysis
        parser.addOption("-o", "", "output file name prefix");
        parser.addOption("-n", "100000", "maximum number of events to process");
        parser.addOption("-trkType"    ,"12",   "tracking type: ConvTB(12), AITB(22)");
        
        parser.parse(args);

        String namePrefix = parser.getOption("-o").stringValue();
        int maxEvents = parser.getOption("-n").intValue();
        int trkType = parser.getOption("-trkType").intValue();         

        List<String> inputList = parser.getInputList();
        if (inputList.isEmpty() == true) {
            parser.printUsage();
            System.out.println("\n >>>> error: no input file is specified....\n");
            System.exit(0);
        }
        
        String outputName = "avgWires.csv";
        if (!namePrefix.isEmpty()) {
            outputName = namePrefix + "_" + outputName;
        }
        

        ProgressPrintout progress = new ProgressPrintout();
        int counter = 0;
        try(FileWriter writer = new FileWriter(outputName)){
            for(String input : inputList){
                HipoReader reader = new HipoReader();
                reader.open(input);
                SchemaFactory schema = reader.getSchemaFactory();

                Reader localReader = new Reader(new Banks(schema));
                Event event = new Event();
                while(reader.hasNext()){
                    reader.nextEvent(event);

                    LocalEvent localEvent = new LocalEvent(localReader, event, trkType);
                    for(Track trk : localEvent.getTracksTB()){  
                        if(trk.isValid(true) && trk.getNumClusters() == 6){                  
                            for(Cluster cls : trk.getClusters()){                            
                                String avgWire = String.format("%.2f", cls.avgWire()); // 2 decimal places
                                writer.write(avgWire + (cls.superlayer() != 6 ? "," : ""));
                            } 
                            writer.write("\n");                        
                        }

                    }

                    progress.updateStatus();
                    counter++;
                    if ((maxEvents > 0 && counter >= maxEvents)) {
                        break;
                    }
                }
                progress.showStatus();
                reader.close(); 
            }
        }
    }
}