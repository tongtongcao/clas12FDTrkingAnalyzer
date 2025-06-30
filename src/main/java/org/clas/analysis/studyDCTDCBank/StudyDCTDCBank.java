package org.clas.analysis.studyDCTDCBank;

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

import org.clas.utilities.Constants;
import org.clas.element.Track;
import org.clas.element.Cluster;
import org.clas.element.Hit;
import org.clas.element.URWellHit;
import org.clas.element.URWellCluster;
import org.clas.element.URWellCross;
import org.clas.element.MCParticle;
import org.clas.graph.HistoGroup;
import org.clas.physicsEvent.BasePhysicsEvent;
import org.clas.physicsEvent.EventForLumiScan;
import org.clas.analysis.BaseAnalysis;
import org.clas.element.RunConfig;
import org.clas.element.TDC;
import org.clas.reader.Banks;

/**
 *
 * @author Tongtong Cao
 */
public class StudyDCTDCBank extends BaseAnalysis{
    private int numCorrectPIDElectrons = 0;
    private int numMisPIDPiminusElectrons = 0;
    
    
    public StudyDCTDCBank(){}
    
    @Override
    public void createHistoGroupMap(){        
        HistoGroup histoTDCGroup = new HistoGroup("TDCGroup", 2, 2);
        H1F h1_numHits = new H1F("numHits", "# of hits", 100, 0, 10000);
        h1_numHits.setTitleX("# of hits");
        h1_numHits.setTitleY("Counts");
        histoTDCGroup.addDataSet(h1_numHits, 0);   
        
        H1F h1_numHits_aiDenoising = new H1F("numHits_aiDenoising", "# of hits afeter AI denoising", 100, 0, 10000);
        h1_numHits_aiDenoising.setTitleX("# of hits");
        h1_numHits_aiDenoising.setTitleY("Counts");
        histoTDCGroup.addDataSet(h1_numHits_aiDenoising, 1); 
        
        histoGroupMap.put(histoTDCGroup.getName(), histoTDCGroup);         
    }
             
    public void processEvent(Event event){        
        // TDC
        List<TDC> tdcs = reader.readTDCs(event);
        
        List<TDC> tdcs_aiDenoising = new ArrayList();
        for(TDC tdc : tdcs){
            if(tdc.isRemainedAfterAIDenoising()){
                tdcs_aiDenoising.add(tdc);
            }
        }  
        
        
        HistoGroup histoTDCGroup = histoGroupMap.get("TDCGroup");

        histoTDCGroup.getH1F("numHits").fill(tdcs.size());
        
        histoTDCGroup.getH1F("numHits_aiDenoising").fill(tdcs_aiDenoising.size());
                
    }
                            
    public static void main(String[] args){
        OptionParser parser = new OptionParser("studyBgEffectsDC");
        parser.setRequiresInputList(false);
        // valid options for event-base analysis
        parser.addOption("-o"          ,"",     "output file name prefix");
        parser.addOption("-n"          ,"-1",   "maximum number of events to process");        
        parser.addOption("-plot"       ,"1",    "display histograms (0/1)");
        
        // histogram based analysis
        parser.addOption("-histo"      ,"0",    "read histogram file (0/1)");
        
        parser.parse(args);
        
        String namePrefix  = parser.getOption("-o").stringValue(); 
        int   maxEvents  = parser.getOption("-n").intValue();    
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
        
        StudyDCTDCBank analysis = new StudyDCTDCBank();
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
                analysis.processEvent(event);
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
            frame.setSize(1000, 800);
            frame.add(canvas);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        }
    }
    
}
