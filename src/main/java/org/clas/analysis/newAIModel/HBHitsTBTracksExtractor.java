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
import org.clas.element.Hit;
import org.clas.reader.Reader;
import org.clas.reader.Banks;
import org.clas.reader.LocalEvent;
import org.clas.utilities.Constants;
import org.clas.utilities.CommonFunctions;
import org.jlab.geom.prim.Point3D;

/**
 * For each valid TB track, find its corresponding HB track, and store doca and z of all hits in the HB track, and TB track parameters
 * Samples are used to train a model for estimation of HB track state at veretx with inputs of all hits
 * @author Tongtong
 */

public class HBHitsTBTracksExtractor{   
    private static final Logger LOGGER = Logger.getLogger(Reader.class.getName()); 
    private static final double CHI2OVERNDFCUT = 10;
       
    public static void main(String[] args) throws IOException {
        Constants.initGeometry();
        
        OptionParser parser = new OptionParser("extractEvents");
        parser.setRequiresInputList(false);
        // valid options for event-base analysis
        parser.addOption("-o", "", "output file name prefix");
        parser.addOption("-n", "10000000", "maximum output entries");
        parser.addOption("-trkType"    ,"12",   "tracking type: ConvTB(12), AITB(22)");
        
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
        
        String outputName = "hitsTracks.csv";
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
                    boolean flag = false;
                    
                    reader.nextEvent(event);

                    LocalEvent localEvent = new LocalEvent(localReader, event, trkType);
                    for(Track trkTB : localEvent.getTracksTB()){  
                        if(trkTB.chi2()/trkTB.NDF() < CHI2OVERNDFCUT){  
                            for(Track trkHB : localEvent.getTracksHB()){
                                if(trkHB.id() == trkTB.id() && trkHB.getNumClusters() == trkTB.getNumClusters() && Math.abs(trkHB.getNumHits() - trkTB.getNumHits()) <= 3){
                                    int numHits = trkHB.getHits().size();
                                    for(int i = 0; i < numHits; i++){
                                        Hit hitHB = trkHB.getHits().get(i);
                                        String docaz = String.format("%.4f,%.4f,%.4f,%.4f,%.4f,%.4f", hitHB.trkDoca(), 
                                                Constants.xo[hitHB.sector()-1][hitHB.superlayer()-1][hitHB.layer()-1][hitHB.wire()-1],
                                                Constants.yo[hitHB.sector()-1][hitHB.superlayer()-1][hitHB.layer()-1][hitHB.wire()-1],
                                                Constants.xe[hitHB.sector()-1][hitHB.superlayer()-1][hitHB.layer()-1][hitHB.wire()-1],
                                                Constants.ye[hitHB.sector()-1][hitHB.superlayer()-1][hitHB.layer()-1][hitHB.wire()-1],
                                                hitHB.z());
                                        if(i < numHits -1) writer.write(docaz + ",");
                                        else writer.write(docaz);
                                    }
                                    writer.write("\n"); 
                                    
                                    Point3D preC1Pos = trkTB.getPreC1Pos();
                                    Point3D preC1Dir = trkTB.getPreC1Dir();                                    
                                    double[] preC1DirSpherical = CommonFunctions.toSpherical(preC1Dir); 
                                    
                                    String trackParameters = String.format("%.4f,%.4f,%.4f,%.4f,%.4f", 
                                             preC1Pos.x(), preC1Pos.y(), preC1Dir.x()/preC1Dir.z(), preC1Dir.y()/preC1Dir.z(), (float)trkTB.charge()/trkTB.p());
                                    writer.write(trackParameters + "\n");
                                    
                                    counter++;
                                    if ((maxOutputEntries > 0 && counter >= maxOutputEntries)) {
                                        flag = true;
                                        break;
                                    }
                                }                                                                            
                            }
                        }
                    }
                    
                    if(flag) break;
                    progress.updateStatus();
                                        
                }
                progress.showStatus();
                reader.close(); 
            }
        }
    }
}