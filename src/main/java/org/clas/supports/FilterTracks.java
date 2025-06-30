package org.clas.supports;

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
 *
 * @author Tongtong
 */

public class FilterTracks{   
    private static final Logger LOGGER = Logger.getLogger(Reader.class.getName()); 
    
    public static void main(String[] args) {
        OptionParser parser = new OptionParser("extractEvents");
        parser.setRequiresInputList(false);
        // valid options for event-base analysis
        parser.addOption("-o", "", "output file name prefix");
        parser.addOption("-n", "100000", "maximum number of events to process");
        
        parser.parse(args);

        String namePrefix = parser.getOption("-o").stringValue();
        int maxEvents = parser.getOption("-n").intValue();
        

        List<String> inputList = parser.getInputList();
        if (inputList.isEmpty() == true) {
            parser.printUsage();
            System.out.println("\n >>>> error: no input file is specified....\n");
            System.exit(0);
        }
        
        String outputName = "filterTracks.hipo";
        if (!namePrefix.isEmpty()) {
            outputName = namePrefix + "_" + outputName;
        }
        
        HipoReader reader = new HipoReader();
        reader.open(inputList.get(0));
        SchemaFactory schema = reader.getSchemaFactory();
        
        HipoWriterSorted writer = new HipoWriterSorted();
        writer.getSchemaFactory().copy(schema);
        writer.setCompressionType(2);   
        writer.open(outputName);
        Event event = new Event();   
                
        Reader localReader = new Reader(new Banks(schema));

        ProgressPrintout progress = new ProgressPrintout();
        int counter = 0;
        while(reader.hasNext()){
            reader.nextEvent(event);
                        
            LocalEvent localEvent = new LocalEvent(localReader, event, 12);
            List<Track> validTracks = new ArrayList();
            List<Cluster> clustersOnValidTracks = new ArrayList();
            for(Track trk : localEvent.getTracksTB()){
                if(trk.isValid(true)){
                    validTracks.add(trk);                    
                    clustersOnValidTracks.addAll(trk.getClusters());
                }
            }
            
            Bank newTrackBank = new Bank(schema.getSchema("TimeBasedTrkg::TBTracks"), validTracks.size());
            int numRow = 0;
            for(Track trk : validTracks){
                newTrackBank.putShort("id", numRow, (short) trk.id());                    
                newTrackBank.putShort("status", numRow, (short) trk.status());    
                newTrackBank.putByte("sector", numRow, (byte) trk.sector());
                newTrackBank.putByte("q", numRow, (byte) trk.charge());
                    
                newTrackBank.putFloat("Vtx0_x", numRow, (float)trk.vx());
                newTrackBank.putFloat("Vtx0_y", numRow, (float)trk.vy());
                newTrackBank.putFloat("Vtx0_z", numRow, (float)trk.vz());
                newTrackBank.putFloat("p0_x", numRow, (float)trk.px());
                newTrackBank.putFloat("p0_y", numRow, (float)trk.py());   
                newTrackBank.putFloat("p0_z", numRow, (float)trk.pz()); 

                for(int r = 0; r < 3; r++) {
                    newTrackBank.putShort("Cross"+String.valueOf(r+1)+"_ID", numRow, (short)trk.crossIds()[r]);
                }
                for(int r = 0; r < 6; r++) {
                    newTrackBank.putShort("Cluster"+String.valueOf(r+1)+"_ID", numRow, (short)trk.clusterIds()[r]);
                }                    
                newTrackBank.putFloat("chi2", numRow, (float) trk.chi2());
                newTrackBank.putShort("ndf", numRow, (short) trk.NDF());

                numRow++;
            }
                                    
            Bank newClusterBank = new Bank(schema.getSchema("HitBasedTrkg::Clusters"), clustersOnValidTracks.size());
            numRow = 0;
            for(Cluster cls : clustersOnValidTracks){
                newClusterBank.putShort("id", numRow, (short) cls.id());                    
                newClusterBank.putShort("status", numRow, (short) cls.status());    
                newClusterBank.putByte("sector", numRow, (byte) cls.sector());
                newClusterBank.putByte("superlayer", numRow, (byte) cls.superlayer());                
                newClusterBank.putByte("size", numRow, (byte) cls.size());
                newClusterBank.putFloat("avgWire", numRow, (float)cls.avgWire()); 
                newClusterBank.putFloat("fitSlope", numRow, (float)cls.fitSlope()); 
                newClusterBank.putFloat("fitSlopeErr", numRow, (float)cls.fitSlopeErr()); 
                newClusterBank.putFloat("fitInterc", numRow, (float)cls.fitInterc()); 
                newClusterBank.putFloat("fitIntercErr", numRow, (float)cls.fitIntercErr()); 
                
                for(int r = 0; r < 12; r++) {
                    newClusterBank.putShort("Hit"+String.valueOf(r+1)+"_ID", numRow, (short)cls.hitIds()[r]);
                }
                
                /*
                try{
                    for(Cluster clsOrg : localEvent.getClusters()){
                        if(cls.id() == clsOrg.id()){
                            newClusterBank.putFloat("lYL1", numRow, (float)clsOrg.getLYL1()); 
                            newClusterBank.putFloat("lYL6", numRow, (float)clsOrg.getLYL6()); 
                        }
                    }
                }
                catch(Exception e){
                    LOGGER.log(Level.FINER, "no items lYL1 & lYL6 in cluster bank!");
                } 
                */
                
                numRow++;
            }
            
            Event newEvent = new Event();
            if(numRow > 0){              
                Bank newRunConfig = new Bank(schema.getSchema("RUN::config"));
                event.read(newRunConfig).copyTo(newRunConfig);
                newEvent.write(newRunConfig);
                
                newEvent.write(newTrackBank);  
                newEvent.write(newClusterBank);
            
                writer.addEvent(newEvent);
            }
            
            progress.updateStatus();
            counter++;
            if ((maxEvents > 0 && counter >= maxEvents)) {
                break;
            }
        }
        progress.showStatus();
        reader.close();
        writer.close();        
    }
}