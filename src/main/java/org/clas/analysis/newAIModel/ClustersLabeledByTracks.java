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
public class ClustersLabeledByTracks {

    private static final Logger LOGGER = Logger.getLogger(Reader.class.getName());

    public static void main(String[] args) throws IOException {
        OptionParser parser = new OptionParser("extractEvents");
        parser.setRequiresInputList(false);
        // valid options for event-base analysis
        parser.addOption("-o", "", "output file name prefix");
        parser.addOption("-n", "10000000", "maximum output entries");
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

        String outputName = "clusters.csv";
        if (!namePrefix.isEmpty()) {
            outputName = namePrefix + "_" + outputName;
        }
        
        // Prepare FileWriters for all 6 sectors
        Map<Integer, FileWriter> sectorWriters = new HashMap<>();
        Map<Integer, Integer> sectorCounters = new HashMap();
        for (int sector = 1; sector <= 6; sector++) {
            String sectorOutputName = outputName.replace(".csv", "_sector" + sector + ".csv");
            FileWriter writer = new FileWriter(sectorOutputName);
            writer.write("eventIdx,clusterIdx,avgWire,superlayer,trkIds\n");
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

                Map<Integer, List<Track>> map_sector_tracks = new HashMap();
                Map<Integer, List<Cluster>> map_sector_clusters = new HashMap();

                for (Track trk : localEvent.getTracksTB()) {
                    if (trk.isValid(true)) {
                        map_sector_tracks.computeIfAbsent(trk.sector(), k -> new ArrayList<>()).add(trk);
                    }
                }
                
                for(Cluster cls : localEvent.getClusters()){
                     map_sector_clusters.computeIfAbsent(cls.sector(), k -> new ArrayList<>()).add(cls);
                }
                
                for(int sector = 1; sector <= 6; sector++){
                    if(map_sector_clusters.containsKey(sector)){
                        int counter = sectorCounters.get(sector) + 1;
                        sectorCounters.put(sector, counter);
                        
                        if(!map_sector_tracks.containsKey(sector)){
                            int clusterIdx = 1;
                            for(Cluster cls : map_sector_clusters.get(sector)){
                                StringBuilder sb = new StringBuilder();
                                sb.append(counter).append(",")
                                        .append(clusterIdx).append(",")
                                        .append(String.format("%.4f", cls.avgWire())).append(",")
                                        .append(cls.superlayer()).append(",")
                                        .append(-1);
                                sectorWriters.get(sector).write(sb.toString() + "\n");
                                clusterIdx++;
                            }                                                        
                        }
                        else{
                            int trkIdx = 1;
                            for(Track trk : map_sector_tracks.get(sector)){
                                trk.setId(trkIdx);
                                trkIdx++;
                            }
                            
                            int clusterIdx = 1;
                            for(Cluster cls : map_sector_clusters.get(sector)){
                                StringBuilder sb = new StringBuilder();
                                sb.append(counter).append(",")
                                        .append(clusterIdx).append(",")
                                        .append(String.format("%.4f", cls.avgWire())).append(",")
                                        .append(cls.superlayer()).append(",");                                        
                                List<Integer> trkIds = new ArrayList();
                                for(Track trk : map_sector_tracks.get(sector)){
                                    boolean flag = false;
                                    for(Cluster clsOnTrk : trk.getClusters()){
                                        if(cls.id() == clsOnTrk.id()){
                                            trkIds.add(trk.id());
                                            flag = true;
                                            break;
                                        }
                                    }
                                    if(flag) break;
                                }
                                
                                if(trkIds.isEmpty()) sb.append(-1);
                                else{
                                    for (int i = 0; i < trkIds.size(); i++) {
                                        sb.append(trkIds.get(i));
                                        if (i < trkIds.size() - 1) sb.append(";");
                                    }
                                }
                                
                                sectorWriters.get(sector).write(sb.toString() + "\n");
                                clusterIdx++;
                            } 
                        }
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
