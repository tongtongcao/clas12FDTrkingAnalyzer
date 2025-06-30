package org.clas.analysis.pass3Tracking;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import javax.swing.JFrame;
import org.jlab.groot.data.H1F;
import org.jlab.groot.data.H2F;
import org.jlab.groot.graphics.EmbeddedCanvasTabbed;
import org.jlab.jnp.hipo4.data.Event;
import org.jlab.jnp.hipo4.data.SchemaFactory;
import org.jlab.jnp.hipo4.io.HipoReader;
import org.jlab.utils.benchmark.ProgressPrintout;
import org.jlab.utils.options.OptionParser;

import org.clas.element.Hit;
import org.clas.element.Track;
import org.clas.element.RecEvent;
import org.clas.element.MCParticle;
import org.clas.graph.HistoGroup;
import org.clas.analysis.BaseAnalysis;
import org.clas.reader.Banks;
import org.clas.reader.LocalEvent;

/**
 *
 * @author Tongtong Cao
 */
public class ExplorePass3Tracking extends BaseAnalysis{
        
    public ExplorePass3Tracking(){}

    public ExplorePass3Tracking(Banks banks){
        super(banks);
    }
    
    @Override
    public void createHistoGroupMap(){        
        HistoGroup histoGroupHitComp = new HistoGroup("trkMCPartMap", 4, 3);
        
        H1F h1_tStartHBHitComp = new H1F("tStartHBHitComp", "tStart for HB hits", 100, 650, 800);
        h1_tStartHBHitComp.setTitleX("tStart for HB");
        h1_tStartHBHitComp.setTitleY("counts");
        histoGroupHitComp.addDataSet(h1_tStartHBHitComp, 0);        
        H1F h1_tStartTBHitComp = new H1F("tStartTBHitComp", "tStart for TB hits", 100, 650, 800);
        h1_tStartTBHitComp.setTitleX("tStart for TB");
        h1_tStartTBHitComp.setTitleY("counts");
        histoGroupHitComp.addDataSet(h1_tStartTBHitComp, 1);        
        H2F h2_tStarthitComp = new H2F("tStarthitComp", "tStart comp", 100, 650, 800, 100, 650, 800);
        h2_tStarthitComp.setTitleX("tStart for HB");
        h2_tStarthitComp.setTitleY("tStart for TB");
        histoGroupHitComp.addDataSet(h2_tStarthitComp, 2);        
        H1F h1_tStartDiffHitComp = new H1F("tStartDiffHitComp", "tStart diff", 100, -2, 2);
        h1_tStartDiffHitComp.setTitleX("tStart diff");
        h1_tStartDiffHitComp.setTitleY("counts");
        histoGroupHitComp.addDataSet(h1_tStartDiffHitComp, 3);
        
        H1F h1_tFlightTimesBetaHBHitComp = new H1F("tFlightTimesBetaHBHitComp", "tFlight*beta for HB hits", 100, 0, 50);
        h1_tFlightTimesBetaHBHitComp.setTitleX("tFlight*beta for HB");
        h1_tFlightTimesBetaHBHitComp.setTitleY("counts");
        histoGroupHitComp.addDataSet(h1_tFlightTimesBetaHBHitComp, 4);        
        H1F h1_tFlightTimesBetaTBHitComp = new H1F("tFlightTimesBetaTBHitComp", "tFlight*beta for TB hits", 100, 0, 50);
        h1_tFlightTimesBetaTBHitComp.setTitleX("tFlight*beta for TB");
        h1_tFlightTimesBetaTBHitComp.setTitleY("counts");
        histoGroupHitComp.addDataSet(h1_tFlightTimesBetaTBHitComp, 5);      
        H2F h2_tFlightTimesBetaHitComp = new H2F("tFlightTimesBetaHitComp", "tFlight*beta comp", 100, 0, 50, 100, 0, 50);
        h2_tFlightTimesBetaHitComp.setTitleX("tFlight*beta for HB");
        h2_tFlightTimesBetaHitComp.setTitleY("tFlight*beta for TB");    
        histoGroupHitComp.addDataSet(h2_tFlightTimesBetaHitComp, 6);        
        H1F h1_tFlightTimesBetaDiffHitComp = new H1F("tFlightTimesBetaDiffHitComp", "tFlight*beta diff", 100, -2, 2);
        h1_tFlightTimesBetaDiffHitComp.setTitleX("tFlight*beta diff");
        h1_tFlightTimesBetaDiffHitComp.setTitleY("counts");
        histoGroupHitComp.addDataSet(h1_tFlightTimesBetaDiffHitComp, 7);
        
        H1F h1_tPropHBHitComp = new H1F("tPropHBHitComp", "tProp for HB hits", 100, 0, 20);
        h1_tPropHBHitComp.setTitleX("tProp for HB");
        h1_tPropHBHitComp.setTitleY("counts");
        histoGroupHitComp.addDataSet(h1_tPropHBHitComp, 8);        
        H1F h1_tPropTBHitComp = new H1F("tPropTBHitComp", "tProp for TB hits", 100, 0, 20);
        h1_tPropTBHitComp.setTitleX("tProp for TB");
        h1_tPropTBHitComp.setTitleY("counts");
        histoGroupHitComp.addDataSet(h1_tPropTBHitComp, 9);      
        H2F h2_tPropHitComp = new H2F("tPropHitComp", "tProp comp", 100, 0, 20, 100, 0, 20);
        h2_tPropHitComp.setTitleX("tProp for HB");
        h2_tPropHitComp.setTitleY("tProp for TB");    
        histoGroupHitComp.addDataSet(h2_tPropHitComp, 10);        
        H1F h1_tPropDiffHitComp = new H1F("tPropDiffHitComp", "tProp diff", 100, -2, 2);
        h1_tPropDiffHitComp.setTitleX("tProp diff");
        h1_tPropDiffHitComp.setTitleY("counts");
        histoGroupHitComp.addDataSet(h1_tPropDiffHitComp, 11);

        
        histoGroupMap.put(histoGroupHitComp.getName(), histoGroupHitComp);        
    }
             
    public void processEvent(Event event, int trkType){
         //////// Read banks
        LocalEvent localEvent = new LocalEvent(reader, event, trkType, true);
        RecEvent recHBEvent = localEvent.getRecHBEvent();
        RecEvent recTBEvent = localEvent.getRecTBEvent();
        

        
        HistoGroup histoGroupHitComp = histoGroupMap.get("trkMCPartMap");
        if(recHBEvent != null && recTBEvent != null){
            histoGroupHitComp.getH1F("tStartHBHitComp").fill(recHBEvent.startTime());
            histoGroupHitComp.getH1F("tStartTBHitComp").fill(recTBEvent.startTime());
            histoGroupHitComp.getH2F("tStarthitComp").fill(recHBEvent.startTime(), recTBEvent.startTime());
            histoGroupHitComp.getH1F("tStartDiffHitComp").fill(recHBEvent.startTime() - recTBEvent.startTime());
        }
        
        List<Track> trksHB = localEvent.getTracksHB();
        List<Track> trksTB = localEvent.getTracksTB();
        
        for(Track trkTB : trksTB){
            for(Track trkHB : trksHB){
                for(Hit hitTB : trkTB.getHits()){
                    for(Hit hitHB : trkHB.getHits()){
                        if(hitHB.id() == hitTB.id() && hitHB.tid() == hitTB.tid()){
                            //System.out.println(hitHB.tFlight() + "  " + hitTB.tFlight());
                            histoGroupHitComp.getH1F("tFlightTimesBetaHBHitComp").fill(hitHB.tFlight());
                            histoGroupHitComp.getH1F("tFlightTimesBetaTBHitComp").fill(hitTB.tFlight()*hitTB.beta());
                            histoGroupHitComp.getH2F("tFlightTimesBetaHitComp").fill(hitHB.tFlight(), hitTB.tFlight()*hitTB.beta());
                            histoGroupHitComp.getH1F("tFlightTimesBetaDiffHitComp").fill(hitHB.tFlight() - hitTB.tFlight()*hitTB.beta());


                            histoGroupHitComp.getH1F("tPropHBHitComp").fill(hitHB.tProp());
                            histoGroupHitComp.getH1F("tPropTBHitComp").fill(hitTB.tProp());
                            histoGroupHitComp.getH2F("tPropHitComp").fill(hitHB.tProp(), hitTB.tProp());
                            histoGroupHitComp.getH1F("tPropDiffHitComp").fill(hitHB.tProp() - hitTB.tProp());

                            break;
                        }
                    }
                }
            }
        }
        
              
    }    
                        
    public static void main(String[] args){
        OptionParser parser = new OptionParser("studyBgEffectsDC");
        parser.setRequiresInputList(false);
        // valid options for event-base analysis
        parser.addOption("-o"          ,"",     "output file name prefix");
        parser.addOption("-n"          ,"-1",   "maximum number of events to process");
        parser.addOption("-trkType"      ,"22",   "tracking type: ConvHB(11) or ConvTB(12) or AIHB(21) or AITB(22)");        
        parser.addOption("-plot"       ,"1",    "display histograms (0/1)");        
        // histogram based analysis
        parser.addOption("-histo"      ,"0",    "read histogram file (0/1)");
        
        parser.parse(args);
        
        String namePrefix  = parser.getOption("-o").stringValue(); 
        int   maxEvents  = parser.getOption("-n").intValue();
        int trkType = parser.getOption("-trkType").intValue();       
        boolean openWindow   = (parser.getOption("-plot").intValue()!=0);
        boolean readHistos   = (parser.getOption("-histo").intValue()!=0);   
        
        List<String> inputList = parser.getInputList();
        if(inputList.isEmpty()==true){
            parser.printUsage();
            inputList.add("/Users/caot/research/clas12/data/mc/uRWELL/upgradeTrackingWithuRWELL/rga-sidis-uRWell-2R_denoise/0nA/reconBg/0000.hipo");
            maxEvents = 1000;
            //System.out.println("\n >>>> error: no input file is specified....\n");
            //System.exit(0);
        }

        String histoName   = "histo.hipo"; 
        if(!namePrefix.isEmpty()) {
            histoName  = namePrefix + "_" + histoName;
        }
        
        ExplorePass3Tracking analysis = new ExplorePass3Tracking();
        analysis.createHistoGroupMap();

        if(!readHistos) {                 
            HipoReader reader = new HipoReader();
            reader.open(inputList.get(0));        


            SchemaFactory schema = reader.getSchemaFactory();
            analysis.initReader(new Banks(schema));

            int counter=0;
            Event event = new Event();
        
            ProgressPrintout progress = new ProgressPrintout();
            while (reader.hasNext()) {

                counter++;

                reader.nextEvent(event);
                
                analysis.processEvent(event, trkType);

                progress.updateStatus();
                if(maxEvents>0){
                    if(counter>=maxEvents) break;
                }                    
            }
            
            progress.showStatus();
            reader.close();            
            analysis.saveHistos(histoName);
        }
        else{
            analysis.readHistos(inputList.get(0)); 
        }
        
        if(openWindow) {
            JFrame frame = new JFrame();
            EmbeddedCanvasTabbed canvas = analysis.plotHistos();
            frame.setSize(1200, 900);
            frame.add(canvas);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        }
    }
    
}
