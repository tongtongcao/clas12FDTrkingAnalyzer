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
public class StudyBgEffectsURWell extends BaseAnalysis{
    
    public StudyBgEffectsURWell(){}
    
    @Override
    public void createHistoGroupMap(){        
        HistoGroup histoGroup = new HistoGroup("comparison", 1, 1);
                
        H1F h1_numHits1 = new H1F("numHits1", "numHits", 100, 0, 5000);
        h1_numHits1.setTitleX("# of hits");
        h1_numHits1.setTitleY("Counts");
        h1_numHits1.setLineColor(1);
        histoGroup.addDataSet(h1_numHits1, 0);
        H1F h1_numHits2 = new H1F("numHits2", "numHits", 100, 0, 5000);
        h1_numHits2.setTitleX("# of hits");
        h1_numHits2.setTitleY("Counts");
        h1_numHits2.setLineColor(2);
        histoGroup.addDataSet(h1_numHits2, 0);
                                
        histoGroupMap.put(histoGroup.getName(), histoGroup);
    }
             
    public void processEvent(Event event1, Event event2){
        List<URWellHit> hits1 = reader1.readURWellHits(event1);   
        List<URWellHit> hits2 = reader2.readURWellHits(event2); 
        
        for(URWellHit hit : hits1){
            //System.out.println(hit.adc());
        }
        
        HistoGroup histoGroup = histoGroupMap.get("comparison");
        histoGroup.getH1F("numHits1").fill(hits1.size());
        histoGroup.getH1F("numHits2").fill(hits2.size());
                      
    }
                        
    public static void main(String[] args){
        OptionParser parser = new OptionParser("studyBgEffectsURWell");
        parser.setRequiresInputList(false);
        // valid options for event-base analysis
        parser.addOption("-o"          ,"",     "output file name prefix");
        parser.addOption("-n"          ,"-1",   "maximum number of events to process");
        parser.addOption("-energy"     ,"10.6", "beam energy");       
        parser.addOption("-plot"       ,"1",    "display histograms (0/1)");
        
        // histogram based analysis
        parser.addOption("-histo"      ,"0",    "read histogram file (0/1)");
        
        parser.parse(args);
        
        String namePrefix  = parser.getOption("-o").stringValue(); 
        int   maxEvents  = parser.getOption("-n").intValue();
        Constants.BEAMENERGY = parser.getOption("-energy").doubleValue();       
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
        
        StudyBgEffectsURWell analysis = new StudyBgEffectsURWell();
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
                
                analysis.processEvent(event1, event2);

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
