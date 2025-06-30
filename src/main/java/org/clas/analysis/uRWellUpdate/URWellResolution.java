package org.clas.analysis.uRWellUpdate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Random;
import javax.swing.JFrame;

import org.jlab.groot.data.H1F;
import org.jlab.groot.data.H2F;
import org.jlab.groot.graphics.EmbeddedCanvasTabbed;
import org.jlab.jnp.hipo4.data.Event;
import org.jlab.jnp.hipo4.data.SchemaFactory;
import org.jlab.jnp.hipo4.io.HipoReader;
import org.jlab.utils.benchmark.ProgressPrintout;
import org.jlab.utils.options.OptionParser;
import org.jlab.groot.math.F1D;

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
import org.clas.demo.DemoSector;
import org.clas.element.RunConfig;
import org.clas.element.TDC;
import org.clas.reader.Banks;
import org.clas.reader.LocalEvent;
import org.clas.fit.ClusterFitLC;
import org.jlab.groot.data.GraphErrors;
import org.jlab.groot.group.DataGroup;
import org.clas.demo.DemoBase;

/**
 *
 * @author Tongtong Cao
 */
public class URWellResolution extends BaseAnalysis{ 
    
    public URWellResolution(){}
    
    @Override
    public void createHistoGroupMap(){
        HistoGroup histoGroupURWellResolution = new HistoGroup("uRWellResolution", 2, 2);
        H1F h1_xURWellResolutionHB= new H1F("xURWellResolutionHB","uRWell x resolution at HB level" , 100, -2, 2);
        h1_xURWellResolutionHB.setTitleX("Diff. between measurent and projection");
        h1_xURWellResolutionHB.setTitleY("Counts");
        histoGroupURWellResolution.addDataSet(h1_xURWellResolutionHB, 0);  
        
        
        H1F h1_yURWellResolutionHB= new H1F("yURWellResolutionHB","uRWell y resolution at HB level" , 100, -10, 10);
        h1_yURWellResolutionHB.setTitleX("Diff. between measurent and projection");
        h1_yURWellResolutionHB.setTitleY("Counts");
        histoGroupURWellResolution.addDataSet(h1_yURWellResolutionHB, 1);  
        
        
        H1F h1_xURWellResolutionTB= new H1F("xURWellResolutionTB","uRWell x resolution at TB level" , 100, -0.4, 0.4);
        h1_xURWellResolutionTB.setTitleX("Diff. between measurent and projection");
        h1_xURWellResolutionTB.setTitleY("Counts");
        histoGroupURWellResolution.addDataSet(h1_xURWellResolutionTB, 2);  
        
        
        H1F h1_yURWellResolutionTB= new H1F("yURWellResolutionTB","uRWell y resolution at TB level" , 100, -2, 2);
        h1_yURWellResolutionTB.setTitleX("Diff. between measurent and projection");
        h1_yURWellResolutionTB.setTitleY("Counts");
        histoGroupURWellResolution.addDataSet(h1_yURWellResolutionTB, 3);  
        
        
        histoGroupMap.put(histoGroupURWellResolution.getName(), histoGroupURWellResolution);
        
         
               
 

    }
             
    public void processEvent(Event event){        
        //Read banks
        LocalEvent localEvent = new LocalEvent(reader, event, Constants.AITB, true);
        
        List<Track> tracksHB = localEvent.getTracksHB();
        List<Track> tracksTB = localEvent.getTracksTB();
        
        List<URWellCross> crosses = localEvent.getURWellCrosses();
        
        
        HistoGroup histoGroupURWellResolution = histoGroupMap.get("uRWellResolution");
        if(tracksHB.size() == 1 && crosses.size() == 1 && tracksHB.get(0).chi2()/tracksHB.get(0).NDF() < 5){
            histoGroupURWellResolution.getH1F("xURWellResolutionHB").fill(tracksHB.get(0).getURWellProjectionLocal().x() - crosses.get(0).pointLocal().x());
            histoGroupURWellResolution.getH1F("yURWellResolutionHB").fill(tracksHB.get(0).getURWellProjectionLocal().y() - crosses.get(0).pointLocal().y());
        }
        
        if(tracksTB.size() == 1 && crosses.size() == 1 && tracksTB.get(0).chi2()/tracksTB.get(0).NDF() < 5){
            histoGroupURWellResolution.getH1F("xURWellResolutionTB").fill(tracksTB.get(0).getURWellProjectionLocal().x() - crosses.get(0).pointLocal().x());
            histoGroupURWellResolution.getH1F("yURWellResolutionTB").fill(tracksTB.get(0).getURWellProjectionLocal().y() - crosses.get(0).pointLocal().y());
        }
        

        


        
        
        
        
    }
    
    public void postEventProcess() {
        HistoGroup histoGroupURWellResolution = histoGroupMap.get("uRWellResolution");
        
        F1D func_xHB  = new F1D("func_p","[amp]*gaus(x,[mean],[sigma])", -0.3,0.3);
        func_xHB.setParameter(0, 700);
        func_xHB.setParameter(1, 0.0);
        func_xHB.setParameter(2, 0.15);
        func_xHB.setLineColor(2);
        func_xHB.setOptStat(1110);
        func_xHB.setLineWidth(2);
        histoGroupURWellResolution.getH1F("xURWellResolutionHB").fit(func_xHB);
        
        F1D func_yHB  = new F1D("func_p","[amp]*gaus(x,[mean],[sigma])", -2,2);
        func_yHB.setParameter(0, 500);
        func_yHB.setParameter(1, 0.0);
        func_yHB.setParameter(2, 1);
        func_yHB.setLineColor(2);
        func_yHB.setOptStat(1110);
        func_yHB.setLineWidth(2);
        histoGroupURWellResolution.getH1F("yURWellResolutionHB").fit(func_yHB);
        
        F1D func_xTB  = new F1D("func_p","[amp]*gaus(x,[mean],[sigma])", -0.06,0.06);
        func_xTB.setParameter(0, 850);
        func_xTB.setParameter(1, 0.0);
        func_xTB.setParameter(2, 0.03);
        func_xTB.setLineColor(2);
        func_xTB.setOptStat(1110);
        func_xTB.setLineWidth(2);
        histoGroupURWellResolution.getH1F("xURWellResolutionTB").fit(func_xTB);        
                
        F1D func_yTB  = new F1D("func_p","[amp]*gaus(x,[mean],[sigma])", -0.4,0.4);
        func_yTB.setParameter(0, 700);
        func_yTB.setParameter(1, 0.0);
        func_yTB.setParameter(2, 0.2);
        func_yTB.setLineColor(2);
        func_yTB.setOptStat(1110);
        func_yTB.setLineWidth(2);
        histoGroupURWellResolution.getH1F("yURWellResolutionTB").fit(func_yTB);
                      
    }            
                            
    public static void main(String[] args){
        OptionParser parser = new OptionParser("uRWellReconstruction");
        parser.setRequiresInputList(false);
        // valid options for event-base analysis
        parser.addOption("-o"          ,"",     "output file name prefix");
        parser.addOption("-n"          ,"-1",   "maximum number of events to process");        
        parser.addOption("-plot"       ,"1",    "display histograms (0/1)");
        parser.addOption("-demo", "1", "display case demo (0/1)");
        parser.addOption("-mDemo", "100", "maxium for number of demonstrated cases");
                
        // histogram based analysis
        parser.addOption("-histo"      ,"0",    "read histogram file (0/1)");
        
        parser.parse(args);
        
        String namePrefix  = parser.getOption("-o").stringValue(); 
        int   maxEvents  = parser.getOption("-n").intValue();    
        boolean displayPlots   = (parser.getOption("-plot").intValue()!=0);
        boolean displayDemos = (parser.getOption("-demo").intValue() != 0);
        int maxDemoCases = parser.getOption("-mDemo").intValue();
        boolean readHistos   = (parser.getOption("-histo").intValue()!=0); 
        Constants.MAXDEMOCASES = maxDemoCases;
        
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
        
        URWellResolution analysis = new URWellResolution();
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
            
            analysis.postEventProcess();
            
            progress.showStatus();
            reader.close();            
            analysis.saveHistos(histoName);
        }
        else{
            analysis.readHistos(inputList.get(0)); 
        }
        
        if(displayPlots) {
            JFrame frame = new JFrame();
            EmbeddedCanvasTabbed canvas = analysis.plotHistos();
            if(canvas != null){
                frame.setSize(1750, 1050);
                frame.add(canvas);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        }
        
        if (displayDemos){
            JFrame frame2 = new JFrame();
            EmbeddedCanvasTabbed canvas2 = analysis.plotDemos();
            if(canvas2 != null){
                frame2.setSize(1200, 750);
                frame2.add(canvas2);
                frame2.setLocationRelativeTo(null);
                frame2.setVisible(true);
            }
        }        
    }
    
}
