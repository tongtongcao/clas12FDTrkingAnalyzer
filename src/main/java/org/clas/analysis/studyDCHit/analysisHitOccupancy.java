package org.clas.analysis.studyDCHit;

import java.util.List;
import java.util.ArrayList;
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
import org.jlab.groot.data.GraphErrors;

import org.clas.utilities.Constants;
import org.clas.element.Track;
import org.clas.element.Hit;
import org.clas.element.TDC;
import org.clas.graph.HistoGroup;
import org.clas.analysis.BaseAnalysis;
import org.clas.reader.Banks;
import org.clas.reader.LocalEvent;


/**
 *
 * @author Tongtong Cao
 */
public class analysisHitOccupancy extends BaseAnalysis{ 
    
    private int numEvents = 0;
    
    private static final double TIMEWINDOW = 250;
    //private static final double[] RWINDOWS = {500, 1400, 1200}; 
    private static final double[] RWINDOWS = {250, 250, 250};  
    
    private int totalHitsRegions[] = {0, 0, 0};
    private int totalHitsRegionsDenoising[] = {0, 0, 0};
    private int totalSignalHitsRegionsDenoising[] = {0, 0, 0};
    private int totalNoiseHitsRegionsDenoising[] = {0, 0, 0};
    
    public analysisHitOccupancy(){}
    
    @Override
    public void createHistoGroupMap(){     
        HistoGroup histoGroupNumTotalHits = new HistoGroup("numTotalHits", 2, 1); 
        H1F h1_numTotalHits = new H1F("numTotalHits", "# of total hits", 100, 0, 10000);
        h1_numTotalHits.setTitleX("# of total hits");
        h1_numTotalHits.setTitleY("counts");
        histoGroupNumTotalHits.addDataSet(h1_numTotalHits, 0); 
        
        H1F h1_numTotalHitsDenoising = new H1F("numTotalHitsDenoising", "# of total hits after denoising", 100, 0, 5000);
        h1_numTotalHitsDenoising.setTitleX("# of total hits");
        h1_numTotalHitsDenoising.setTitleY("counts");
        histoGroupNumTotalHits.addDataSet(h1_numTotalHitsDenoising, 1); 
        histoGroupMap.put(histoGroupNumTotalHits.getName(), histoGroupNumTotalHits);
        
       
        
        HistoGroup histoGroupHitOccupancyInSectors = new HistoGroup("hitOccupancyInSectors", 3, 2);        
        for (int i = 0; i < 6; i++) {
            H2F h2_hitOccupancy = new H2F("hit occupancy in sector" + Integer.toString(i + 1),
                    "hit occupancy (%) in sector" + Integer.toString(i + 1), 36, 0.5, 36.5, 112, 0.5, 112.5);
            h2_hitOccupancy.setTitleX("layer");
            h2_hitOccupancy.setTitleY("wire");
            histoGroupHitOccupancyInSectors.addDataSet(h2_hitOccupancy, i);                                                          
        }
        histoGroupMap.put(histoGroupHitOccupancyInSectors.getName(), histoGroupHitOccupancyInSectors);
        
        HistoGroup histoGroupHitOccupancyInRegions = new HistoGroup("hitOccupancyInRegions", 1, 1);        
        for (int i = 0; i < 3; i++) {
            H1F h1_hitOccupancy = new H1F("hit occupancy in region" + Integer.toString(i + 1),
                    "hit occupancy in region" + Integer.toString(i + 1), 6, 0.5, 6.5);
            h1_hitOccupancy.setTitleX("sector");
            h1_hitOccupancy.setTitleY("hit occupancy (%)");
            h1_hitOccupancy.setLineColor(i+1);
            histoGroupHitOccupancyInRegions.addDataSet(h1_hitOccupancy, 0);                                                          
        }
        histoGroupMap.put(histoGroupHitOccupancyInRegions.getName(), histoGroupHitOccupancyInRegions);  
        
        HistoGroup histoGroupHitOccupancyInSectorsDenoising = new HistoGroup("hitOccupancyInSectorsDenoising", 3, 2);        
        for (int i = 0; i < 6; i++) {
            H2F h2_hitOccupancy = new H2F("hit occupancy after denoising in sector" + Integer.toString(i + 1),
                    "hit occupancy (%) after denoising in sector" + Integer.toString(i + 1), 36, 0.5, 36.5, 112, 0.5, 112.5);
            h2_hitOccupancy.setTitleX("layer");
            h2_hitOccupancy.setTitleY("wire");
            histoGroupHitOccupancyInSectorsDenoising.addDataSet(h2_hitOccupancy, i);                                                          
        }
        histoGroupMap.put(histoGroupHitOccupancyInSectorsDenoising.getName(), histoGroupHitOccupancyInSectorsDenoising);
        
        HistoGroup histoGroupHitOccupancyInRegionsDenoising = new HistoGroup("hitOccupancyInRegionsDenoising", 1, 1);        
        for (int i = 0; i < 3; i++) {
            H1F h1_hitOccupancy = new H1F("hit occupancy after denoising in region" + Integer.toString(i + 1),
                    "hit occupancy after denoising in region" + Integer.toString(i + 1), 6, 0.5, 6.5);
            h1_hitOccupancy.setTitleX("sector");
            h1_hitOccupancy.setTitleY("hit occupancy (%)");
            h1_hitOccupancy.setLineColor(i+1);
            histoGroupHitOccupancyInRegionsDenoising.addDataSet(h1_hitOccupancy, 0);                                                          
        }
        histoGroupMap.put(histoGroupHitOccupancyInRegionsDenoising.getName(), histoGroupHitOccupancyInRegionsDenoising); 
        
        HistoGroup histoGroupSignalHitOccupancyInSectorsDenoising = new HistoGroup("signalHitOccupancyInSectorsDenoising", 3, 2);        
        for (int i = 0; i < 6; i++) {
            H2F h2_hitOccupancy = new H2F("signal hit occupancy after denoising in sector" + Integer.toString(i + 1),
                    "signal hit occupancy (%) after denoising in sector" + Integer.toString(i + 1), 36, 0.5, 36.5, 112, 0.5, 112.5);
            h2_hitOccupancy.setTitleX("layer");
            h2_hitOccupancy.setTitleY("wire");
            histoGroupSignalHitOccupancyInSectorsDenoising.addDataSet(h2_hitOccupancy, i);                                                          
        }
        histoGroupMap.put(histoGroupSignalHitOccupancyInSectorsDenoising.getName(), histoGroupSignalHitOccupancyInSectorsDenoising);
        
        HistoGroup histoGroupSignalHitOccupancyInRegionsDenoising = new HistoGroup("signalHitOccupancyInRegionsDenoising", 1, 1);        
        for (int i = 0; i < 3; i++) {
            H1F h1_hitOccupancy = new H1F("signal hit occupancy after denoising in region" + Integer.toString(i + 1),
                    "signal hit occupancy after denoising in region" + Integer.toString(i + 1), 6, 0.5, 6.5);
            h1_hitOccupancy.setTitleX("sector");
            h1_hitOccupancy.setTitleY("hit occupancy (%)");
            h1_hitOccupancy.setLineColor(i+1);
            histoGroupSignalHitOccupancyInRegionsDenoising.addDataSet(h1_hitOccupancy, 0);                                                          
        }
        histoGroupMap.put(histoGroupSignalHitOccupancyInRegionsDenoising.getName(), histoGroupSignalHitOccupancyInRegionsDenoising); 

        HistoGroup histoGroupNoiseHitOccupancyInSectorsDenoising = new HistoGroup("noiseHitOccupancyInSectorsDenoising", 3, 2);        
        for (int i = 0; i < 6; i++) {
            H2F h2_hitOccupancy = new H2F("noise hit occupancy after denoising in sector" + Integer.toString(i + 1),
                    "noise hit occupancy (%) after denoising in sector" + Integer.toString(i + 1), 36, 0.5, 36.5, 112, 0.5, 112.5);
            h2_hitOccupancy.setTitleX("layer");
            h2_hitOccupancy.setTitleY("wire");
            histoGroupNoiseHitOccupancyInSectorsDenoising.addDataSet(h2_hitOccupancy, i);                                                          
        }
        histoGroupMap.put(histoGroupNoiseHitOccupancyInSectorsDenoising.getName(), histoGroupNoiseHitOccupancyInSectorsDenoising);
        
        HistoGroup histoGroupNoiseHitOccupancyInRegionsDenoising = new HistoGroup("noiseHitOccupancyInRegionsDenoising", 1, 1);        
        for (int i = 0; i < 3; i++) {
            H1F h1_hitOccupancy = new H1F("noise hit occupancy after denoising in region" + Integer.toString(i + 1),
                    "noise hit occupancy after denoising in region" + Integer.toString(i + 1), 6, 0.5, 6.5);
            h1_hitOccupancy.setTitleX("sector");
            h1_hitOccupancy.setTitleY("hit occupancy (%)");
            h1_hitOccupancy.setLineColor(i+1);
            histoGroupNoiseHitOccupancyInRegionsDenoising.addDataSet(h1_hitOccupancy, 0);                                                          
        }
        histoGroupMap.put(histoGroupNoiseHitOccupancyInRegionsDenoising.getName(), histoGroupNoiseHitOccupancyInRegionsDenoising);          
    }
             
    public void processEvent(Event event, int trkType){  
        numEvents++;
        
        //Read banks
        LocalEvent localEvent = new LocalEvent(reader, event);        
        List<TDC> tdcs = localEvent.getTDCs();
        
        HistoGroup histoGroupNumTotalHits = histoGroupMap.get("numTotalHits");
        
        HistoGroup histoGroupHitOccupancyInSectors = histoGroupMap.get("hitOccupancyInSectors");
        HistoGroup histoGroupHitOccupancyInRegions = histoGroupMap.get("hitOccupancyInRegions");
        
        HistoGroup histoGroupHitOccupancyInSectorsDenoising = histoGroupMap.get("hitOccupancyInSectorsDenoising");
        HistoGroup histoGroupHitOccupancyInRegionsDenoising = histoGroupMap.get("hitOccupancyInRegionsDenoising");

        HistoGroup histoGroupSignalHitOccupancyInSectorsDenoising = histoGroupMap.get("signalHitOccupancyInSectorsDenoising");
        HistoGroup histoGroupSignalHitOccupancyInRegionsDenoising = histoGroupMap.get("signalHitOccupancyInRegionsDenoising");        
        
        HistoGroup histoGroupNoiseHitOccupancyInSectorsDenoising = histoGroupMap.get("noiseHitOccupancyInSectorsDenoising");
        HistoGroup histoGroupNoiseHitOccupancyInRegionsDenoising = histoGroupMap.get("noiseHitOccupancyInRegionsDenoising"); 
        
        histoGroupNumTotalHits.getH1F("numTotalHits").fill(tdcs.size());
        
        int numTotalHitsDenoising = 0;
        for(TDC tdc : tdcs){
            int sector = tdc.sector();
            int superlayer = tdc.superlayer();
            int layer = tdc.layer();
            int wire = tdc.component();
            int region = (superlayer+1)/2;
            
            if(Constants.MC){
                histoGroupHitOccupancyInSectors.getH2F("hit occupancy in sector" + Integer.toString(sector)).fill((superlayer-1)*6 + layer, wire, RWINDOWS[region - 1] / TIMEWINDOW);
                histoGroupHitOccupancyInRegions.getH1F("hit occupancy in region" + Integer.toString(region)).fill(sector, RWINDOWS[region - 1] / TIMEWINDOW); 
                totalHitsRegions[region - 1] += RWINDOWS[region - 1] / TIMEWINDOW;
            } else {
                histoGroupHitOccupancyInSectors.getH2F("hit occupancy in sector" + Integer.toString(sector)).fill((superlayer-1)*6 + layer, wire);
                histoGroupHitOccupancyInRegions.getH1F("hit occupancy in region" + Integer.toString(region)).fill(sector);   
                totalHitsRegions[region - 1] ++;
            }
            
            if(tdc.isRemainedAfterAIDenoising()){
                numTotalHitsDenoising++;
                if(Constants.MC){
                    histoGroupHitOccupancyInSectorsDenoising.getH2F("hit occupancy after denoising in sector" + Integer.toString(sector)).fill((superlayer-1)*6 + layer, wire, RWINDOWS[region - 1] / TIMEWINDOW);
                    histoGroupHitOccupancyInRegionsDenoising.getH1F("hit occupancy after denoising in region" + Integer.toString(region)).fill(sector, RWINDOWS[region - 1] / TIMEWINDOW); 
                    totalHitsRegionsDenoising[region - 1] += RWINDOWS[region - 1] / TIMEWINDOW;
                    
                    if(tdc.isNormalHit()){
                        histoGroupSignalHitOccupancyInSectorsDenoising.getH2F("signal hit occupancy after denoising in sector" + Integer.toString(sector)).fill((superlayer-1)*6 + layer, wire, RWINDOWS[region - 1] / TIMEWINDOW);
                        histoGroupSignalHitOccupancyInRegionsDenoising.getH1F("signal hit occupancy after denoising in region" + Integer.toString(region)).fill(sector, RWINDOWS[region - 1] / TIMEWINDOW); 
                        totalSignalHitsRegionsDenoising[region - 1] += RWINDOWS[region - 1] / TIMEWINDOW;
                    } else {
                        histoGroupNoiseHitOccupancyInSectorsDenoising.getH2F("noise hit occupancy after denoising in sector" + Integer.toString(sector)).fill((superlayer-1)*6 + layer, wire, RWINDOWS[region - 1] / TIMEWINDOW);
                        histoGroupNoiseHitOccupancyInRegionsDenoising.getH1F("noise hit occupancy after denoising in region" + Integer.toString(region)).fill(sector, RWINDOWS[region - 1] / TIMEWINDOW); 
                        totalNoiseHitsRegionsDenoising[region - 1] += RWINDOWS[region - 1] / TIMEWINDOW;
                    }
                    
                } else {
                    histoGroupHitOccupancyInSectorsDenoising.getH2F("hit occupancy after denoising in sector" + Integer.toString(sector)).fill((superlayer-1)*6 + layer, wire);
                    histoGroupHitOccupancyInRegionsDenoising.getH1F("hit occupancy after denoising in region" + Integer.toString(region)).fill(sector); 
                    totalHitsRegionsDenoising[region - 1] ++;
                }                
            }
        }
        
        histoGroupNumTotalHits.getH1F("numTotalHitsDenoising").fill(numTotalHitsDenoising);
                                       
    }

    public void postEventProcess(){
        HistoGroup histoGroupHitOccupancyInSectors = histoGroupMap.get("hitOccupancyInSectors");
        for (int i = 0; i < 6; i++) {
            histoGroupHitOccupancyInSectors.getH2F("hit occupancy in sector" + Integer.toString(i + 1)).normalize(numEvents/100.);
        }
        
        double[] averageHitOccupancyRegions = new double[3];
        HistoGroup histoGroupHitOccupancyInRegions = histoGroupMap.get("hitOccupancyInRegions");
        for (int i = 0; i < 3; i++) {
            histoGroupHitOccupancyInRegions.getH1F("hit occupancy in region" + Integer.toString(i + 1)).normalize(numEvents * 112 * 12/100.);
            
            averageHitOccupancyRegions[i] = totalHitsRegions[i]/((double)numEvents * 112 * 12 * 6);
            
            System.out.println("Hit occupancy for region " + (i + 1) + ": " + averageHitOccupancyRegions[i]);
        } 
        
        HistoGroup histoGroupHitOccupancyInSectorsDenoising = histoGroupMap.get("hitOccupancyInSectorsDenoising");
        for (int i = 0; i < 6; i++) {
            histoGroupHitOccupancyInSectorsDenoising.getH2F("hit occupancy after denoising in sector" + Integer.toString(i + 1)).normalize(numEvents/100.);            
        }
        
        double[] averageHitOccupancyRegionsDenoising = new double[3];
        HistoGroup histoGroupHitOccupancyInRegionsDenoising = histoGroupMap.get("hitOccupancyInRegionsDenoising");
        for (int i = 0; i < 3; i++) {
            histoGroupHitOccupancyInRegionsDenoising.getH1F("hit occupancy after denoising in region" + Integer.toString(i + 1)).normalize(numEvents * 112 * 12/100.);
            
            averageHitOccupancyRegionsDenoising[i] = totalHitsRegionsDenoising[i]/((double)numEvents * 112 * 12 * 6);
            
            System.out.println("Hit occupancy after denoising for region " + (i + 1) + ": " + averageHitOccupancyRegionsDenoising[i]);
        }
        
        HistoGroup histoGroupSignalHitOccupancyInSectorsDenoising = histoGroupMap.get("signalHitOccupancyInSectorsDenoising");
        for (int i = 0; i < 6; i++) {
            histoGroupSignalHitOccupancyInSectorsDenoising.getH2F("signal hit occupancy after denoising in sector" + Integer.toString(i + 1)).normalize(numEvents/100.);            
        }
        
        double[] averageSignalHitOccupancyRegionsDenoising = new double[3];
        HistoGroup histoGroupSignalHitOccupancyInRegionsDenoising = histoGroupMap.get("signalHitOccupancyInRegionsDenoising");
        for (int i = 0; i < 3; i++) {
            histoGroupSignalHitOccupancyInRegionsDenoising.getH1F("signal hit occupancy after denoising in region" + Integer.toString(i + 1)).normalize(numEvents * 112 * 12/100.);
            
            averageSignalHitOccupancyRegionsDenoising[i] = totalSignalHitsRegionsDenoising[i]/((double)numEvents * 112 * 12 * 6);
            
            System.out.println("Signal hit occupancy after denoising for region " + (i + 1) + ": " + averageSignalHitOccupancyRegionsDenoising[i]);
        }

        HistoGroup histoGroupNoiseHitOccupancyInSectorsDenoising = histoGroupMap.get("noiseHitOccupancyInSectorsDenoising");
        for (int i = 0; i < 6; i++) {
            histoGroupNoiseHitOccupancyInSectorsDenoising.getH2F("noise hit occupancy after denoising in sector" + Integer.toString(i + 1)).normalize(numEvents/100.);            
        }
        
        double[] averageNoiseHitOccupancyRegionsDenoising = new double[3];
        HistoGroup histoGroupNoiseHitOccupancyInRegionsDenoising = histoGroupMap.get("noiseHitOccupancyInRegionsDenoising");
        for (int i = 0; i < 3; i++) {
            histoGroupNoiseHitOccupancyInRegionsDenoising.getH1F("noise hit occupancy after denoising in region" + Integer.toString(i + 1)).normalize(numEvents * 112 * 12/100.);
            
            averageNoiseHitOccupancyRegionsDenoising[i] = totalNoiseHitsRegionsDenoising[i]/((double)numEvents * 112 * 12 * 6);
            
            System.out.println("Noise hit occupancy after denoising for region " + (i + 1) + ": " + averageNoiseHitOccupancyRegionsDenoising[i]);
        }         
      
    }
                          
    public static void main(String[] args){
        OptionParser parser = new OptionParser("extractHitError");
        parser.setRequiresInputList(false);
        // valid options for event-base analysis
        parser.addOption("-o"          ,"",     "output file name prefix");
        parser.addOption("-n"          ,"-1",   "maximum number of events to process");  
        parser.addOption("-trkType", "12", "tracking type: ConvTB(12) or AITB(22)");
        parser.addOption("-plot"       ,"1",    "display histograms (0/1)");
        parser.addOption("-mc", "0", "if mc (0/1)");
                
        // histogram based analysis
        parser.addOption("-histo"      ,"0",    "read histogram file (0/1)");
        
        parser.parse(args);
        
        String namePrefix  = parser.getOption("-o").stringValue(); 
        int   maxEvents  = parser.getOption("-n").intValue();   
        int trkType = parser.getOption("-trkType").intValue();
        boolean displayPlots   = (parser.getOption("-plot").intValue()!=0);
        boolean readHistos   = (parser.getOption("-histo").intValue()!=0);  
        boolean mc = (parser.getOption("-mc").intValue() != 0);
        Constants.MC = mc;
        
        List<String> inputList = parser.getInputList();
        if(inputList.isEmpty()==true){
            parser.printUsage();
            System.out.println("\n >>>> error: no input file is specified....\n");
            System.exit(0);
        }

        String histoName   = "histo.hipo"; 
        if(!namePrefix.isEmpty()) {
            histoName  = namePrefix + "_" + histoName;
        }
        
        analysisHitOccupancy analysis = new analysisHitOccupancy();
        analysis.createHistoGroupMap();
        
        if(!readHistos) {
            ProgressPrintout progress = new ProgressPrintout();             
            int counter=0;
            
            outerLoop: 
            for (String input : inputList) {
                HipoReader reader = new HipoReader();
                reader.open(input);        

                SchemaFactory schema = reader.getSchemaFactory();
                analysis.initReader(new Banks(schema));

                Event event = new Event();
                
                while (reader.hasNext()) {

                    counter++;

                    reader.nextEvent(event);                
                    analysis.processEvent(event, trkType);
                    progress.updateStatus();
                    
                    if(maxEvents>0 && counter >= maxEvents) break outerLoop;                                        
                }
                reader.close();  
            }
            progress.showStatus();
            
            analysis.postEventProcess(); 
            analysis.saveHistos(histoName);
                      
        }
        else{
            analysis.readHistos(inputList.get(0)); 
        }
        
        if(displayPlots) {
            JFrame frame = new JFrame();
            EmbeddedCanvasTabbed canvas = analysis.plotHistos();
            frame.setSize(1800, 1200);
            frame.add(canvas);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        }        
    }
    
}
