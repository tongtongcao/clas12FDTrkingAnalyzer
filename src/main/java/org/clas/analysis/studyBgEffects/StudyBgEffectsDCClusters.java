package org.clas.analysis.studyBgEffects;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import org.jlab.groot.data.H1F;
import org.jlab.groot.data.H2F;
import org.jlab.groot.graphics.EmbeddedCanvasTabbed;
import org.jlab.jnp.hipo4.data.Event;
import org.jlab.jnp.hipo4.data.SchemaFactory;
import org.jlab.jnp.hipo4.io.HipoReader;
import org.jlab.utils.benchmark.ProgressPrintout;
import org.jlab.utils.options.OptionParser;

import org.clas.utilities.Constants;
import org.clas.element.Track;
import org.clas.element.Cluster;
import org.clas.element.Hit;
import org.clas.element.URWellHit;
import org.clas.element.URWellCluster;
import org.clas.element.URWellCross;
import org.clas.graph.HistoGroup;
import org.clas.physicsEvent.BasePhysicsEvent;
import org.clas.physicsEvent.EventForLumiScan;
import org.clas.analysis.BaseAnalysis;
import org.clas.reader.Banks;

/**
 *
 * @author Tongtong Cao
 */
public class StudyBgEffectsDCClusters extends BaseAnalysis{
    
    public StudyBgEffectsDCClusters(){}
    
    @Override
    public void createHistoGroupMap(){        
        HistoGroup histoClusterSizeGroup = new HistoGroup("clusterSize", 3, 2);
        
        List<H1F> h1_clusterSize_list1 = new ArrayList();
        List<H1F> h1_clusterSize_list2 = new ArrayList();
        for(int i = 0; i < 6; i++){
            H1F h1_clusterSize1 = new H1F("cluster size for SL" + Integer.toString(i + 1) + " for data1", 
                    "cluster size for SL" + Integer.toString(i + 1), 10, 0.5, 10.5);
            h1_clusterSize1.setTitleX("cluster size");
            h1_clusterSize1.setTitleY("Counts");
            h1_clusterSize1.setLineColor(1);
            histoClusterSizeGroup.addDataSet(h1_clusterSize1, i);
            H1F h1_clusterSize2 = new H1F("cluster size for SL" + Integer.toString(i + 1) + " for data2", 
                    "cluster size for SL" + Integer.toString(i + 1), 10, 0.5, 10.5);
            h1_clusterSize2.setTitleX("cluster size");
            h1_clusterSize2.setTitleY("Counts");
            h1_clusterSize2.setLineColor(2);
            histoClusterSizeGroup.addDataSet(h1_clusterSize2, i);
            
            h1_clusterSize_list1.add(h1_clusterSize1);
            h1_clusterSize_list2.add(h1_clusterSize2);
        }
                                                
        histoGroupMap.put(histoClusterSizeGroup.getName(), histoClusterSizeGroup);
    }
             
    public void processEvent(Event event1, Event event2, int trkType){
        List<Cluster> clusters1 = reader1.readClusters(event1, trkType);   
        List<Cluster> clusters2 = reader2.readClusters(event2, trkType);        
                
        HistoGroup histoClusterSizeGroup = histoGroupMap.get("clusterSize");
        
        for(Cluster cls : clusters1){
                histoClusterSizeGroup.getH1F("cluster size for SL" + cls.superlayer() + " for data1").fill(cls.size());
        }
        
        for(Cluster cls : clusters2){          
                histoClusterSizeGroup.getH1F("cluster size for SL" + cls.superlayer() + " for data2").fill(cls.size());
        }
        
    }
                        
    public static void main(String[] args){
        OptionParser parser = new OptionParser("studyBgEffectsDC");
        parser.setRequiresInputList(false);
        // valid options for event-base analysis
        parser.addOption("-o"          ,"",     "output file name prefix");
        parser.addOption("-n"          ,"-1",   "maximum number of events to process");
        parser.addOption("-energy"     ,"10.6", "beam energy");
        parser.addOption("-target"     ,"2212", "target PDG");
        parser.addOption("-trkType"      ,"22",   "tracking type: ConvHB(11) or ConvTB(12) or AIHB(21) or AITB(22)");
        
        parser.addOption("-plot"       ,"1",    "display histograms (0/1)");
        
        // histogram based analysis
        parser.addOption("-histo"      ,"0",    "read histogram file (0/1)");
        
        parser.parse(args);
        
        String namePrefix  = parser.getOption("-o").stringValue(); 
        int   maxEvents  = parser.getOption("-n").intValue();
        Constants.BEAMENERGY = parser.getOption("-energy").doubleValue();
        Constants.TARGETPID  = parser.getOption("-target").intValue();  
        int trkType = parser.getOption("-trkType").intValue();       
        boolean openWindow   = (parser.getOption("-plot").intValue()!=0);
        boolean readHistos   = (parser.getOption("-histo").intValue()!=0);   
        
        List<String> inputList = parser.getInputList();
        if(inputList.isEmpty()==true){
            parser.printUsage();
            inputList.add("/Users/caot/research/clas12/data/mc/uRWELL/upgradeTrackingWithuRWELL/rga-sidis-uRWell-2R_denoise/0nA/reconBg/0000.hipo");
            inputList.add("/Users/caot/research/clas12/data/mc/uRWELL/upgradeTrackingWithuRWELL/rga-sidis-uRWell-2R_denoise/50nA/reconBg/0000.hipo");
            maxEvents = 1000;
            //System.out.println("\n >>>> error: no input file is specified....\n");
            //System.exit(0);
        }

        String histoName   = "histo.hipo"; 
        if(!namePrefix.isEmpty()) {
            histoName  = namePrefix + "_" + histoName;
        }
        
        StudyBgEffectsDCClusters analysis = new StudyBgEffectsDCClusters();
        analysis.createHistoGroupMap();

        if(!readHistos) {                 
            HipoReader reader1 = new HipoReader();
            reader1.open(inputList.get(0));        
            HipoReader reader2 = new HipoReader();
            reader2.open(inputList.get(1));


            SchemaFactory schema1 = reader1.getSchemaFactory();
            SchemaFactory schema2 = reader2.getSchemaFactory();
            analysis.initReader(new Banks(schema1), new Banks(schema2));

            int counter=0;
            Event event1 = new Event();
            Event event2 = new Event();
        
            ProgressPrintout progress = new ProgressPrintout();
            while (reader1.hasNext() && reader2.hasNext()) {

                counter++;

                reader1.nextEvent(event1);
                reader2.nextEvent(event2);
                
                analysis.processEvent(event1, event2, trkType);

                progress.updateStatus();
                if(maxEvents>0){
                    if(counter>=maxEvents) break;
                }                    
            }

            progress.showStatus();
            reader1.close();            
            reader2.close();
            analysis.saveHistos(histoName);
        }
        else{
            analysis.readHistos(inputList.get(0)); 
        }
        
        if(openWindow) {
            JFrame frame = new JFrame();
            EmbeddedCanvasTabbed canvas = analysis.plotHistos();
            frame.setSize(1200, 750);
            frame.add(canvas);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        }
    }
    
}
