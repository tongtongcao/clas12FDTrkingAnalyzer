package org.clas.analysis.newAIModel;

import java.io.*;
import java.util.*;
import java.nio.file.Files;
import javax.swing.JFrame;
import org.jlab.jnp.hipo4.data.Event;

import org.clas.aiModel.Denoising;
import org.clas.analysis.BaseAnalysis;
import org.clas.reader.LocalEvent;
import org.clas.utilities.Constants;
import org.clas.element.TDC;
import org.clas.element.Hit;
import org.clas.element.Track;
import org.clas.graph.HistoGroup;
import org.clas.reader.Banks;
import org.jlab.groot.data.H1F;
import org.jlab.groot.data.H2F;
import org.jlab.groot.graphics.EmbeddedCanvasTabbed;
import org.jlab.jnp.hipo4.data.SchemaFactory;
import org.jlab.jnp.hipo4.io.HipoReader;
import org.jlab.utils.benchmark.ProgressPrintout;
import org.jlab.utils.options.OptionParser;

/**
 * Input reconstruction file with old denoising model
 * Apply new denoising model to get probability and set new order for hits
 * Compare orders between old and new models
 * @author tongtong
 */

public class StudyNewDenoising extends BaseAnalysis{
    Denoising net;
    private double threshold = 0.053;
    
    public StudyNewDenoising(){}
    
    public void init() throws Exception{
        String modelPath;
        try (InputStream in = StudyNewDenoising.class.getResourceAsStream("/org/clas/aiModel/cnn_autoenc_sector1_2b_48f_4x6k.pt")) {

            if (in == null) {
                throw new RuntimeException("Model file not found in resources!");
            }

            File tempFile = Files.createTempFile("cnn_model", ".pt").toFile();
            tempFile.deleteOnExit(); // auto exit when program done

            try (OutputStream out = new FileOutputStream(tempFile)) {
                in.transferTo(out);
            }

            modelPath = tempFile.getAbsolutePath();
        }

        net = new Denoising(modelPath);
    }
    
    @Override
    public void createHistoGroupMap(){ 
        HistoGroup histoGroupProbabilityNormalHits = new HistoGroup("probabilityNormalHits", 2, 3);
        HistoGroup histoGroupProbabilityNoiseHits = new HistoGroup("probabilityNoiseHits", 2, 3);
        HistoGroup histoGroupOrderComp = new HistoGroup("orderComp", 2, 3);
        HistoGroup histoGroupOrderCompHitsOnValidTBTracks = new HistoGroup("orderCompHitsOnValidTBTracks", 2, 3);
        for(int i = 0; i < 6; i++){
            H1F h1_probabilityNormalHits = new H1F("probabilityNormalHits for sector" + Integer.toString(i + 1),
                    "probabilityNormalHits for sector" + Integer.toString(i + 1), 101, 0, 1);
            h1_probabilityNormalHits.setTitleX("Probability");
            h1_probabilityNormalHits.setTitleY("Counts");
            histoGroupProbabilityNormalHits.addDataSet(h1_probabilityNormalHits, i);
            
            H1F h1_probabilityNoiseHits = new H1F("probabilityNoiseHits for sector" + Integer.toString(i + 1),
                    "probabilityNoiseHits for sector" + Integer.toString(i + 1), 101, 0, 1);
            h1_probabilityNoiseHits.setTitleX("Probability");
            h1_probabilityNoiseHits.setTitleY("Counts");
            histoGroupProbabilityNoiseHits.addDataSet(h1_probabilityNoiseHits, i);
            
            H2F h2_orderComp = new H2F("orderComp for sector" + Integer.toString(i + 1),
                    "orderComp for sector" + Integer.toString(i + 1), 13, -0.5, 12.5, 13, -0.5, 12.5);
            h2_orderComp.setTitleX("old order / 10");
            h2_orderComp.setTitleY("new order / 10");
            histoGroupOrderComp.addDataSet(h2_orderComp, i);
            
            H2F h2_orderCompHitsOnValidTBTracks = new H2F("orderCompHitsOnValidTBTracks for sector" + Integer.toString(i + 1),
                    "orderCompHitsOnValidTBTracks for sector" + Integer.toString(i + 1), 13, -0.5, 12.5, 13, -0.5, 12.5);
            h2_orderCompHitsOnValidTBTracks.setTitleX("old order / 10");
            h2_orderCompHitsOnValidTBTracks.setTitleY("new order / 10");
            histoGroupOrderCompHitsOnValidTBTracks.addDataSet(h2_orderCompHitsOnValidTBTracks, i);            
        }
        histoGroupMap.put(histoGroupProbabilityNormalHits.getName(), histoGroupProbabilityNormalHits);
        histoGroupMap.put(histoGroupProbabilityNoiseHits.getName(), histoGroupProbabilityNoiseHits);
        histoGroupMap.put(histoGroupOrderComp.getName(), histoGroupOrderComp);
        histoGroupMap.put(histoGroupOrderCompHitsOnValidTBTracks.getName(), histoGroupOrderCompHitsOnValidTBTracks);
        
        
    }
    
    public void processEvent(Event event){        
        //Read banks
        LocalEvent localEvent = new LocalEvent(reader, event, Constants.AITB);  
        
        List<TDC> tdcs = localEvent.getTDCs();
        
        float[][][] input = new float[6][36][112];
        for(TDC tdc : tdcs){
            if(tdc.order()!=20 && tdc.order()!=30){
                input[tdc.sector()-1][tdc.originLayer()-1][tdc.component()-1] = 1.0f;
            }
        }
        
        float[][][] output;
        try {
            output = net.predict(input);
        } catch (Exception e) {
            throw new RuntimeException("Error in DenoisingBatch prediction", e);
        }
                        
        for(TDC tdc : tdcs){
            if(tdc.order()!=20 && tdc.order()!=30) {
                tdc.setProbability(output[tdc.sector()-1][tdc.originLayer()-1][tdc.component()-1]);
                if(tdc.isNormalHit()){
                    if(tdc.getProbability() < threshold) tdc.setNewOrder(60);
                    else tdc.setNewOrder(0);
                }
                else{
                    if(tdc.getProbability() < threshold) tdc.setNewOrder(90);
                    else tdc.setNewOrder(10);
                }
                
            }
        }
        
        HistoGroup histoGroupProbabilityNormalHits = histoGroupMap.get("probabilityNormalHits");
        HistoGroup histoGroupProbabilityNoiseHits = histoGroupMap.get("probabilityNoiseHits");
        HistoGroup histoGroupOrderComp = histoGroupMap.get("orderComp");
        for(TDC tdc : tdcs){
            if(tdc.order()!=20 && tdc.order()!=30){
                if(tdc.isNormalHit()) histoGroupProbabilityNormalHits.getH1F("probabilityNormalHits for sector" + Integer.toString(tdc.sector())).fill(tdc.getProbability());
                else histoGroupProbabilityNoiseHits.getH1F("probabilityNoiseHits for sector" + Integer.toString(tdc.sector())).fill(tdc.getProbability());
                
                histoGroupOrderComp.getH2F("orderComp for sector" + Integer.toString(tdc.sector())).fill(tdc.order()/10., tdc.getNewOrder()/10.);
            }
        }
        
        
        List<Hit> hitsOnValidTBTracks = new ArrayList();
        for(Track trk : localEvent.getTracksTB()){
            if(trk.isValid(true)) hitsOnValidTBTracks.addAll(trk.getHits());
        }
        
        HistoGroup histoGroupOrderCompHitsOnValidTBTracks = histoGroupMap.get("orderCompHitsOnValidTBTracks");
        for(Hit hit : hitsOnValidTBTracks){
            for(TDC tdc : tdcs){
                if(hit.hitMatched(tdc)){
                    histoGroupOrderCompHitsOnValidTBTracks.getH2F("orderCompHitsOnValidTBTracks for sector" + Integer.toString(tdc.sector())).fill(tdc.order()/10., tdc.getNewOrder()/10.);
                    break;
                }
            }
        }
    }
    
    public void setThreshold(double thr){
        this.threshold = thr;
    }

    public static void main(String[] args) throws Exception {
        OptionParser parser = new OptionParser("studyNewDenoising");
        parser.setRequiresInputList(false);
        // valid options for event-base analysis
        parser.addOption("-o"          ,"",     "output file name prefix");
        parser.addOption("-n"          ,"-1",   "maximum number of events to process");        
        parser.addOption("-plot"       ,"1",    "display histograms (0/1)");
        parser.addOption("-histo"      ,"0",    "read histogram file (0/1)");
        parser.addOption("-th", "0.053", "threshold for denoising");
        
        parser.parse(args);
        
        String namePrefix  = parser.getOption("-o").stringValue(); 
        int   maxEvents  = parser.getOption("-n").intValue();    
        boolean displayPlots   = (parser.getOption("-plot").intValue()!=0);
        boolean readHistos   = (parser.getOption("-histo").intValue()!=0); 
        double thr = parser.getOption("-th").doubleValue();
        
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
        
        StudyNewDenoising analysis = new StudyNewDenoising();
        analysis.init();
        analysis.setThreshold(thr);
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
        
        if(displayPlots) {
            JFrame frame = new JFrame();
            EmbeddedCanvasTabbed canvas = analysis.plotHistos();
            if(canvas != null){
                frame.setSize(1000, 1200);
                frame.add(canvas);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        }               
    }
}
