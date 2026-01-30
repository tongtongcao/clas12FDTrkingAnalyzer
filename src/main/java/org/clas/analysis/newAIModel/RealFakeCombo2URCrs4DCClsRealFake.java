package org.clas.analysis.newAIModel;

import java.io.FileWriter;
import java.io.IOException;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;
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
import org.clas.utilities.Constants;
import org.clas.element.URWellCross;

/**
 * Extract xy of 2 uRWell crosses and average wires from valid TB tracks, randomly abandon 2 DC cluster, and label such combos as 1
 * Then, for a valid TB tracks, use a random uRWell cross or a random cluster in the same sector 
 * to replace the corresponding uRWell cross or cluster at the same region or superlayer, label such combos as 0
 * Save them into a csv file
 * The csv file will be input as training AI model of cluster-combo classification
 * @author Tongtong
 */

public class RealFakeCombo2URCrs4DCClsRealFake{              
    private static final Logger LOGGER = Logger.getLogger(Reader.class.getName()); 
    
    public static void main(String[] args) throws IOException {
        Constants.URWELL = true;
        
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
        
        String outputName = "2URCrs4DCClsCombo.csv";
        if (!namePrefix.isEmpty()) {
            outputName = namePrefix + "_" + outputName;
        }
        

        ProgressPrintout progress = new ProgressPrintout();
        int counter = 0;
        int counterReal = 0;
        int counterFake = 0;
        int counterFakeUr = 0;
        Random rand = new Random();
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

                    LocalEvent localEvent = new LocalEvent(localReader, event, trkType, true);
                    for(Track trk : localEvent.getTracksTB()){  
                        if(trk.isValid(true) && trk.getNumClusters() == 6 && trk.getURWellCrosses().size() == 2){                            
                            List<Integer> clsIdsInTrack = new ArrayList();
                            for(Cluster clsInTrack : trk.getClusters()){
                                clsIdsInTrack.add(clsInTrack.id());
                            }                            
                            List<Cluster> clustersInSector = localEvent.getClustersInSector(trk.sector());
                            List<Cluster> clustersInTrack = new ArrayList();
                            for(Cluster cls : clustersInSector){
                                for(int clsIdInTrack : clsIdsInTrack){
                                    if(cls.id() == clsIdInTrack) {
                                        clustersInTrack.add(cls);
                                        break;
                                    }
                                }
                                if(clustersInTrack.size() == clsIdsInTrack.size()) break;
                            }
                            
                            List<Integer> urCrossesIdsInTrack = new ArrayList();
                            for(URWellCross urCross : trk.getURWellCrosses()){
                                urCrossesIdsInTrack.add(urCross.id());
                            }                            
                            List<URWellCross> urCrossesInSector = localEvent.getURWellCrossesInSector(trk.sector());
                            List<URWellCross> urCrossesInTrack = new ArrayList();
                            for(URWellCross urCrs : urCrossesInSector){
                                for(int urCrossIdInTrack : urCrossesIdsInTrack){
                                    if(urCrs.id() == urCrossIdInTrack){
                                        urCrossesInTrack.add(urCrs);
                                        break;
                                    }
                                }
                                if(urCrossesInTrack.size() == urCrossesIdsInTrack.size()) break;
                            }                            
                            
                            int removedSL1 = (int) (Math.random() * 6) + 1;   
                            int removedSL2;
                            do {
                                removedSL2 = (int) (Math.random() * 6) + 1;  
                            } while (removedSL2 == removedSL1);
                            
                            if(removedSL1 > removedSL2){
                                int temp = removedSL1;
                                removedSL1 = removedSL2;
                                removedSL2 = temp;
                            }
                            
                            // Real combos
                            for(Cluster cls : clustersInTrack){
                                if(cls.superlayer() != removedSL1 && cls.superlayer() != removedSL2){
                                    String avgWire = String.format("%.4f", cls.avgWire());
                                    writer.write(avgWire + ",");
                                }
                            }
                            for(Cluster cls : clustersInTrack){  
                                if(cls.superlayer() != removedSL1 && cls.superlayer() != removedSL2){
                                    String slope = String.format("%.4f", cls.fitSlope());
                                    writer.write(slope + ",");
                                }
                            }
                            for(URWellCross urCrs : urCrossesInTrack){
                                String xy = String.format("%.4f,%.4f,", urCrs.pointLocal().x(), urCrs.pointLocal().y());
                                writer.write(xy);
                            }
                            
                            writer.write(removedSL1 + "," + removedSL2 + ",1\n");
                            counterReal++;
                            counter++;
                            
                            
                            // Fale combos with a fake DC cluster
                            List<Cluster> clustersNotInTrack = new ArrayList();
                            clustersNotInTrack.addAll(clustersInSector);
                            clustersNotInTrack.removeAll(clustersInTrack);
                                                       
                            for(Cluster cls : clustersNotInTrack){
                                if(cls.superlayer() != removedSL1 && cls.superlayer() != removedSL2){
                                    List<Cluster> clustersInFakeTrack = new ArrayList(); 
                                    clustersInFakeTrack.addAll(clustersInTrack);                                
                                    clustersInFakeTrack.set(cls.superlayer() - 1, cls);

                                    for(Cluster clsInFakeTrack : clustersInFakeTrack){   
                                        if(clsInFakeTrack.superlayer() != removedSL1 && clsInFakeTrack.superlayer() != removedSL2){
                                            String avgWire = String.format("%.4f", clsInFakeTrack.avgWire()); 
                                            writer.write(avgWire + ",");
                                        }
                                    }
                                    for(Cluster clsInFakeTrack : clustersInFakeTrack){ 
                                        if(clsInFakeTrack.superlayer() != removedSL1 && clsInFakeTrack.superlayer() != removedSL2){
                                            String slope = String.format("%.4f", clsInFakeTrack.fitSlope());
                                            writer.write(slope + ",");
                                        }
                                    }
                                    for(URWellCross urCrs : urCrossesInTrack){
                                        String xy = String.format("%.4f,%.4f,", urCrs.pointLocal().x(), urCrs.pointLocal().y());
                                        writer.write(xy);
                                    }    
                                    
                                    writer.write(removedSL1 + "," + removedSL2 + ",0\n");
                                    counterFake++;
                                    counter++;
                                }
                            }
                            
                            // Fake combos with a fake uRWell cross
                            List<URWellCross> urCrossesNotInTrack = new ArrayList();
                            urCrossesNotInTrack.addAll(urCrossesInSector);
                            urCrossesNotInTrack.removeAll(urCrossesInTrack);                            
                            
                            URWellCross fakeURCrs1 = writeClosestURWellFakeCombo(clustersInTrack, urCrossesInTrack, urCrossesNotInTrack, writer, rand, removedSL1, removedSL2);
                            if(fakeURCrs1 != null) {
                                urCrossesNotInTrack.remove(fakeURCrs1);
                                counterFakeUr++;
                                counter++;
                            }
                            
                            URWellCross fakeURCrs2 = writeRandomURWellFakeCombo(clustersInTrack, urCrossesInTrack, urCrossesNotInTrack, writer, rand, removedSL1, removedSL2);
                            if(fakeURCrs2 != null){
                                urCrossesNotInTrack.remove(fakeURCrs2);
                                counterFakeUr++;
                                counter++;
                            }                            
                                                        
                            if ((maxOutputEntries > 0 && counter >= maxOutputEntries)) {
                                flag = true;
                                break;
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
        
        System.out.println("# of real combos: " + counterReal);
        System.out.println("# of fake combos: " + counterFake);
        System.out.println("# of fake uRWell combos: " + counterFakeUr);
    }
    
    // Write a fake combo with closest fake uRWell cross
    static private URWellCross writeClosestURWellFakeCombo(List<Cluster> clustersInTrack, List<URWellCross> urCrossesInTrack, List<URWellCross> urCrossesNotInTrack, FileWriter writer, Random rand, int removedSL1, int removedSL2) throws IOException {
        URWellCross fakeURCrs = null;
        if (!urCrossesNotInTrack.isEmpty()) {
            for (Cluster cls : clustersInTrack) {
                if (cls.superlayer() != removedSL1 && cls.superlayer() != removedSL2) {
                    String avgWire = String.format("%.4f", cls.avgWire());
                    writer.write(avgWire + ",");
                }
            }
            for (Cluster cls : clustersInTrack) {
                if (cls.superlayer() != removedSL1 && cls.superlayer() != removedSL2) {
                    String slope = String.format("%.4f", cls.fitSlope());
                    writer.write(slope + ",");
                }
            }

            List<URWellCross> urCrossesNotInTrackR1 = new ArrayList();
            List<URWellCross> urCrossesNotInTrackR2 = new ArrayList();
            for (URWellCross urCrs : urCrossesNotInTrack) {
                if (urCrs.region() == 1) {
                    urCrossesNotInTrackR1.add(urCrs);
                } else {
                    urCrossesNotInTrackR2.add(urCrs);
                }
            }

            int fakeCrsRegion = rand.nextInt(2) + 1;
            if (fakeCrsRegion == 1 && urCrossesNotInTrackR1.isEmpty()) {
                fakeCrsRegion = 2;
            } else if (fakeCrsRegion == 2 && urCrossesNotInTrackR2.isEmpty()) {
                fakeCrsRegion = 1;
            }

            if (fakeCrsRegion == 1) {
                fakeURCrs = urCrossesInTrack.get(0).getClosetURWellCross(urCrossesNotInTrackR1);
                String xy1 = String.format("%.4f,%.4f,", fakeURCrs.pointLocal().x(), fakeURCrs.pointLocal().y());
                writer.write(xy1);

                String xy2 = String.format("%.4f,%.4f,", urCrossesInTrack.get(1).pointLocal().x(), urCrossesInTrack.get(1).pointLocal().y());
                writer.write(xy2);
            } else if (fakeCrsRegion == 2) {
                String xy1 = String.format("%.4f,%.4f,", urCrossesInTrack.get(0).pointLocal().x(), urCrossesInTrack.get(0).pointLocal().y());
                writer.write(xy1);

                fakeURCrs = urCrossesInTrack.get(1).getClosetURWellCross(urCrossesNotInTrackR2);
                String xy2 = String.format("%.4f,%.4f,", fakeURCrs.pointLocal().x(), fakeURCrs.pointLocal().y());
                writer.write(xy2);
            }

            writer.write(removedSL1 + "," + removedSL2 + ",0\n");
        }

        return fakeURCrs;
    }
    
    // Write a fake combo with random fake uRWell cross
    static private URWellCross writeRandomURWellFakeCombo(List<Cluster> clustersInTrack, List<URWellCross> urCrossesInTrack, List<URWellCross> urCrossesNotInTrack, FileWriter writer, Random rand, int removedSL1, int removedSL2) throws IOException{
        URWellCross fakeURCrs = null;
        if (!urCrossesNotInTrack.isEmpty()) {
            for (Cluster cls : clustersInTrack) {
                if (cls.superlayer() != removedSL1 && cls.superlayer() != removedSL2) {
                    String avgWire = String.format("%.4f", cls.avgWire());
                    writer.write(avgWire + ",");
                }
            }
            for (Cluster cls : clustersInTrack) {
                if (cls.superlayer() != removedSL1 && cls.superlayer() != removedSL2) {
                    String slope = String.format("%.4f", cls.fitSlope());
                    writer.write(slope + ",");
                }
            }

            int fakeCrsIndex = rand.nextInt(urCrossesNotInTrack.size());
            fakeURCrs = urCrossesNotInTrack.get(fakeCrsIndex);
            
            if (fakeURCrs.region() == 1) {
                String xy1 = String.format("%.4f,%.4f,", fakeURCrs.pointLocal().x(), fakeURCrs.pointLocal().y());
                writer.write(xy1);

                String xy2 = String.format("%.4f,%.4f,", urCrossesInTrack.get(1).pointLocal().x(), urCrossesInTrack.get(1).pointLocal().y());
                writer.write(xy2);
            } else  {
                String xy1 = String.format("%.4f,%.4f,", urCrossesInTrack.get(0).pointLocal().x(), urCrossesInTrack.get(0).pointLocal().y());
                writer.write(xy1);

                String xy2 = String.format("%.4f,%.4f,", fakeURCrs.pointLocal().x(), fakeURCrs.pointLocal().y());
                writer.write(xy2);
            }
            
            writer.write(removedSL1 + "," + removedSL2 + ",0\n");           
        }
        
       return fakeURCrs;
    }    
}