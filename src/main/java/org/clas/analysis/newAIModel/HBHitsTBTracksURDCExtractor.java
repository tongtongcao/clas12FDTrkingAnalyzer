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
import org.clas.element.Track;
import org.clas.element.Cluster;
import org.clas.element.Hit;
import org.clas.element.URWellCross;
import org.clas.element.URWellCluster;
import org.clas.reader.Reader;
import org.clas.reader.Banks;
import org.clas.reader.LocalEvent;
import org.clas.utilities.Constants;
import org.clas.utilities.CommonFunctions;
import org.jlab.geom.prim.Point3D;
import org.jlab.clas.physics.Vector3;

/**
 * For each valid TB uRWell-DC track, find its corresponding HB track, and store all hits in the HB track, and TB track parameters
 * Samples are used to train a model for estimation of HB track state at z = 222 cm with inputs of all hits in titled sector coordinates
 * @author Tongtong
 */

public class HBHitsTBTracksURDCExtractor{   
    private static final Logger LOGGER = Logger.getLogger(Reader.class.getName());
    private static final int PRESCALE6DC2UR = 10;
    private static final int PRESCALE6DC1UR = 5;
    private static final int PRESCALE5DC2UR = 3;
       
    public static void main(String[] args) throws IOException {
        Constants.initGeometry();
        
        OptionParser parser = new OptionParser("extractEvents");
        parser.setRequiresInputList(false);
        // valid options for event-base analysis
        parser.addOption("-o", "", "output file name prefix");
        parser.addOption("-n", "1000000", "maximum output entries");
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
            int cycle6DC2UR = 0;
            int cycle6DC1UR = 0;
            int cycle5DC2UR = 0;
            
            int totalNum6DC2UR = 0;
            int totalNum6DC1UR = 0;
            int totalNum5DC2UR = 0;
            int totalNum5DC1UR = 0;
            int totalNum4DC2UR = 0;
            int totalNum4DC1UR = 0;
            
            for(String input : inputList){
                HipoReader reader = new HipoReader();
                reader.open(input);
                SchemaFactory schema = reader.getSchemaFactory();

                Reader localReader = new Reader(new Banks(schema));
                Event event = new Event();
                while(reader.hasNext()){
                    boolean flag = false;
                    
                    reader.nextEvent(event);

                    LocalEvent localEvent = new LocalEvent(localReader, event, trkType, true);
                    
                    Map<Integer, Track> map_id_trackHB = new HashMap();
                    Map<Integer, Track> map_id_trackTB = new HashMap();
                    
                    for(Track trk : localEvent.getTracksHB()){  
                        map_id_trackHB.put(trk.id(), trk);
                    }
                    
                    for(Track trk : localEvent.getTracksTB()){  
                        map_id_trackTB.put(trk.id(), trk);
                    }
                    
                    for(int trkId : map_id_trackTB.keySet()){  
                        Track trkTB = map_id_trackTB.get(trkId); 
                        
                        if(trkTB.isValid(true)){  
                            Track trkHB = map_id_trackHB.get(trkId); 
                                                        
                            if(trkHB.getURWellCrosses() != null &&  trkTB.getURWellCrosses() != null 
                                    && trkHB.getNumClusters() == trkTB.getNumClusters()
                                    && trkTB.getURWellCrosses().size() >= 1 && trkHB.getURWellCrosses().size() == trkTB.getURWellCrosses().size()
                                    && Math.abs(trkHB.getNumHits() - trkTB.getNumHits()) <= 3){

                                int numURWellCrosses = trkTB.getURWellCrosses().size();
                                int numDCClusters = trkTB.getNumClusters();
                                if(numDCClusters == 6){
                                    if(numURWellCrosses == 2){
                                        cycle6DC2UR++;
                                        if(cycle6DC2UR % PRESCALE6DC2UR == 0) {
                                            cycle6DC2UR = 0;
                                            totalNum6DC2UR++;
                                        }
                                        else continue;
                                    }
                                    else if(numURWellCrosses == 1){
                                        cycle6DC1UR++;
                                        if(cycle6DC1UR % PRESCALE6DC1UR == 0) {
                                            cycle6DC1UR = 0;
                                            totalNum6DC1UR++;
                                        }
                                        else continue;
                                        
                                    }
                                }
                                else if(numDCClusters == 5){
                                    if(numURWellCrosses == 2){
                                        cycle5DC2UR++;
                                        if(cycle5DC2UR % PRESCALE5DC2UR == 0) {
                                            cycle5DC2UR = 0;
                                            totalNum5DC2UR++;
                                        }
                                        else continue;
                                    }
                                    else if(numURWellCrosses == 1) totalNum5DC1UR++;
                                }
                                else if(numDCClusters == 4){
                                    if(numURWellCrosses == 2) totalNum4DC2UR ++;
                                    else if(numURWellCrosses == 1) totalNum4DC1UR++;
                                }
                                
                                
                                for(int i = 0; i < numURWellCrosses; i++){
                                    URWellCross crs = trkTB.getURWellCrosses().get(i);
                                    
                                    String cls1Info = String.format("%.4f,%.4f,%.4f,%.4f,%.4f", 
                                            crs.getCluster1().originalPointLocal().x(), 
                                            crs.getCluster1().originalPointLocal().y(),
                                            crs.getCluster1().endPointLocal().x(),
                                            crs.getCluster1().endPointLocal().y(),
                                            crs.getCluster1().originalPointLocal().z());
                                    String cls2Info = String.format("%.4f,%.4f,%.4f,%.4f,%.4f", 
                                            crs.getCluster2().originalPointLocal().x(), 
                                            crs.getCluster2().originalPointLocal().y(),
                                            crs.getCluster2().endPointLocal().x(),
                                            crs.getCluster2().endPointLocal().y(),
                                            crs.getCluster2().originalPointLocal().z()); 
                                    
                                    if(i < numURWellCrosses - 1) writer.write(cls1Info + "," + cls2Info + ",");
                                    else writer.write(cls1Info + "," + cls2Info);
                                }
                                writer.write("\n"); 

                                int numHits = trkHB.getHits().size();
                                for(int i = 0; i < numHits; i++){
                                    Hit hitHB = trkHB.getHits().get(i);
                                    String hitInfo = String.format("%.4f,%.4f,%.4f,%.4f,%.4f", hitHB.trkDoca(), 
                                            Constants.xm[hitHB.sector()-1][hitHB.superlayer()-1][hitHB.layer()-1][hitHB.wire()-1],
                                            Constants.xr[hitHB.sector()-1][hitHB.superlayer()-1][hitHB.layer()-1][hitHB.wire()-1],
                                            Constants.yr[hitHB.sector()-1][hitHB.superlayer()-1][hitHB.layer()-1][hitHB.wire()-1],
                                            hitHB.z());
                                    if(i < numHits -1) writer.write(hitInfo + ",");
                                    else writer.write(hitInfo);
                                }
                                writer.write("\n"); 

                                Point3D pos = trkTB.getURWellProjectionLocalR1();
                                Vector3 mom = trkTB.getURWellMomentumLocalR1();  

                                String trackParameters = String.format("%.4f,%.4f,%.4f,%.4f,%.4f", 
                                         pos.x(), pos.y(), mom.x()/mom.z(), mom.y()/mom.z(), (float)trkTB.charge()/mom.mag());
                                writer.write(trackParameters + "\n");

                                counter++;
                                if ((maxOutputEntries > 0 && counter >= maxOutputEntries)) {
                                    flag = true;
                                    break;
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
            
            String info = String.format("N_6DC2UR = %d, N_6DC1UR = %d, N_5DC2UR = %d, N_5DC1UR = %d, N_4DC2UR = %d, N_4DC1UR = %d", 
                                         totalNum6DC2UR, totalNum6DC1UR, totalNum5DC2UR, totalNum5DC1UR, totalNum4DC2UR, totalNum4DC1UR);
            System.out.println(info);                                                                        
                    
        }
    }
}