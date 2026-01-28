package org.clas.analysis.newAIModel;

import java.io.*;
import java.util.*;
import java.nio.file.Files;
import javax.swing.JFrame;
import org.jlab.jnp.hipo4.data.Event;

import org.clas.aiModel.ClsComboClf;
import org.clas.analysis.BaseAnalysis;
import org.clas.reader.LocalEvent;
import org.clas.utilities.Constants;
import org.clas.element.TDC;
import org.clas.element.Hit;
import org.clas.element.Cluster;
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
 * Study cluster combo classifiers
 * @author tongtong
 */

public class StudyClsComboClf extends BaseAnalysis{
    ClsComboClf.SixClsComboClf clf6;
    ClsComboClf.FiveClsComboClf clf5;
    private double threshold = 0.03;
    
    public StudyClsComboClf(){}
    
    public void init() throws Exception{
        String modelPath6Cls;
        try (InputStream in = StudyNewDenoising.class.getResourceAsStream("/org/clas/aiModel/mlp_64h_4l_6cls.pt")) {

            if (in == null) {
                throw new RuntimeException("Model file not found in resources!");
            }

            File tempFile = Files.createTempFile("cnn_model", ".pt").toFile();
            tempFile.deleteOnExit(); // auto exit when program done

            try (OutputStream out = new FileOutputStream(tempFile)) {
                in.transferTo(out);
            }

            modelPath6Cls = tempFile.getAbsolutePath();
        }
        clf6 = new ClsComboClf.SixClsComboClf(modelPath6Cls);
        
        String modelPath5Cls;
        try (InputStream in = StudyNewDenoising.class.getResourceAsStream("/org/clas/aiModel/mlp_64h_3l_5cls.pt")) {

            if (in == null) {
                throw new RuntimeException("Model file not found in resources!");
            }

            File tempFile = Files.createTempFile("cnn_model", ".pt").toFile();
            tempFile.deleteOnExit(); // auto exit when program done

            try (OutputStream out = new FileOutputStream(tempFile)) {
                in.transferTo(out);
            }

            modelPath5Cls = tempFile.getAbsolutePath();
        }

        clf5 = new ClsComboClf.FiveClsComboClf(modelPath5Cls);
    }
    
    @Override
    public void createHistoGroupMap(){                 
    }
    
public void processEvent(Event event) {        
    LocalEvent localEvent = new LocalEvent(reader, event, Constants.AITB);

    // Collect all track inputs of this event
    List<float[]> batchInputs6Cls = new ArrayList<>();
    List<Track> batchTracks6Cls = new ArrayList<>();
    
    List<float[]> batchInputs5Cls = new ArrayList<>();
    List<Track> batchTracks5Cls = new ArrayList<>();

    for (Track trk : localEvent.getTracksTB()) {
        if (trk.isValid()){
            List<Cluster> clusters = trk.getClusters();
            
            if(trk.getNumClusters() == 6){
                float[] input = new float[12];
                for (int i = 0; i < 6; i++) {
                    input[i]   = (float) clusters.get(i).avgWire();
                    input[i+6] = (float) clusters.get(i).fitSlope();
                }

                batchInputs6Cls.add(input);
                batchTracks6Cls.add(trk);
            }
            else if(trk.getNumClusters() == 5) {                
                float[] input = new float[11];
                for (int i = 0; i < 5; i++) {
                    input[i]   = (float) clusters.get(i).avgWire();
                    input[i+5] = (float) clusters.get(i).fitSlope();
                }
                input[10] = (float)trk.getMissingSL();

                batchInputs5Cls.add(input);
                batchTracks5Cls.add(trk);
            }
        }
    }

    try {
        // ===== Batch prediction for 6-cluster combos =====
        if(!batchInputs6Cls.isEmpty()){
            //System.out.println("6-cluster combos");
            float[][] batchArray6Cls = new float[batchInputs6Cls.size()][];
            for (int i = 0; i < batchInputs6Cls.size(); i++) {
                batchArray6Cls[i] = batchInputs6Cls.get(i);
            }
            float[] outputs6Cls = clf6.predictBatch(batchArray6Cls);

            // Print or process predictions
            for (int i = 0; i < outputs6Cls.length; i++) {
                float score = outputs6Cls[i];
                Track trk = batchTracks6Cls.get(i);

                //System.out.printf("TrackID=%d  score=%.4f  chi2/ndf=%.4f%n", trk.id(), score, trk.chi2()/trk.NDF());
            }
        }
        
        // ===== Batch prediction for 5-cluster combos =====
        if(!batchInputs5Cls.isEmpty()){
            //System.out.println("5-cluster combos");            
            float[][] batchArray5Cls = new float[batchInputs5Cls.size()][];
            for (int i = 0; i < batchInputs5Cls.size(); i++) {
                batchArray5Cls[i] = batchInputs5Cls.get(i);
            }
            float[] outputs5Cls = clf5.predictBatch(batchArray5Cls);

            // Print or process predictions
            for (int i = 0; i < outputs5Cls.length; i++) {
                float score = outputs5Cls[i];
                Track trk = batchTracks5Cls.get(i);

                //System.out.printf("TrackID=%d  score=%.4f  chi2/ndf=%.4f%n", trk.id(), score, trk.chi2()/trk.NDF());
            }
        }        

    } catch (Exception e) {
        throw new RuntimeException("Batch prediction error in SixClsComboClf", e);
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
        
        StudyClsComboClf analysis = new StudyClsComboClf();
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
