package org.clas.analysis.studyCovMat;

import java.util.List;
import javax.swing.JFrame;

import org.jlab.groot.data.H1F;
import org.jlab.groot.data.H2F;

import org.clas.analysis.BaseAnalysis;
import org.clas.element.Track;
import org.clas.graph.HistoGroup;
import org.clas.reader.LocalEvent;
import org.clas.reader.Banks;
import org.jlab.groot.graphics.EmbeddedCanvasTabbed;
import org.jlab.jnp.hipo4.data.Event;
import org.jlab.jnp.hipo4.data.SchemaFactory;
import org.jlab.jnp.hipo4.io.HipoReader;
import org.jlab.utils.benchmark.ProgressPrintout;
import org.jlab.utils.options.OptionParser;

/**
 *
 * @author Tongtong Cao
 */
public class CompareCovMat extends BaseAnalysis {      
    @Override
    public void createHistoGroupMap() {    
        HistoGroup histoGroupCovMatComp = new HistoGroup("covMatComp", 6, 6);
        String[][] title = {
            {"cov(x,x)", "cov(x,y)", "cov(x,z)", "cov(x,#theta)", "cov(x,#phi)", "cov(x,p)"},
            {"cov(y,x)", "cov(y,y)", "cov(y,z)", "cov(y,#theta)", "cov(y,#phi)", "cov(y,p)"},
            {"cov(z,x)", "cov(z,y)", "cov(z,z)", "cov(z,#theta)", "cov(z,#phi)", "cov(z,p)"},
            {"cov(#theta,x)", "cov(#theta,y)", "cov(#theta,z)", "cov(#theta,#theta)", "cov(#theta,#phi)", "cov(#theta,p)"},
            {"cov(#phi,x)", "cov(#phi,y)", "cov(#phi,z)", "cov(#phi,#theta)", "cov(#phi,#phi)", "cov(#phi,p)"},
            {"cov(p,x)", "cov(p,y)", "cov(p,z)", "cov(p,#theta)", "cov(p,#phi)", "cov(p,p)"}
        };
        
        double[][] min = {
            {0, -1, -0.5, -0.01, -0.05, -0.05},
            {-1, 0, -0.5, -0.01, -0.02, -0.02},
            {-0.5, -0.5, 0, -0.001, -0.01, -0.01},
            {-0.01, -0.01, -0.001, 0, -0.00002, -0.00004},
            {-0.05, -0.02, -0.01, -0.00002, 0, -0.0001},
            {-0.05, -0.02, -0.01, -0.00004, -0.0001, 0}
        };
        
        double[][] max = {
            {1, 1, 0.5, 0.01, 0.05, 0.05},
            {1, 1, 0.5, 0.01, 0.02, 0.02},
            {0.5, 0.5, 0.2, 0.001, 0.01, 0.01},
            {0.01, 0.01, 0.001, 0.000003, 0.00002, 0.00004},
            {0.05, 0.02, 0.01, 0.00002, 0.0005, 0.0001},
            {0.05, 0.02, 0.01, 0.00004, 0.0001, 0.0005}
        };        
        
        for(int i = 0; i < 6; i++){
            for(int j = 0; j < 6; j++){
                H1F h1_covMat = new H1F("C" + Integer.toString(i+1) + Integer.toString(j+1), "C" + Integer.toString(i+1) + Integer.toString(j+1), 100, min[i][j], max[i][j]);
                h1_covMat.setTitleX(title[i][j]);
                h1_covMat.setTitleY("");
                h1_covMat.setLineColor(1);
                histoGroupCovMatComp.addDataSet(h1_covMat, i*6 + j);
                
                H1F h1_covMat2 = new H1F("2C" + Integer.toString(i+1) + Integer.toString(j+1), "2C" + Integer.toString(i+1) + Integer.toString(j+1), 100, min[i][j], max[i][j]);
                h1_covMat2.setTitleX(title[i][j]);
                h1_covMat2.setTitleY("");
                h1_covMat2.setLineColor(2);
                histoGroupCovMatComp.addDataSet(h1_covMat2, i*6 + j);                
            }            
        }    
        
        histoGroupMap.put(histoGroupCovMatComp.getName(), histoGroupCovMatComp);
    }
    
    public void processEvent(Event event1, Event event2, int trkType) {
        ////// Read banks
        LocalEvent localEvent1 = new LocalEvent(reader1, event1, trkType);
        LocalEvent localEvent2 = new LocalEvent(reader2, event2, trkType); 
        
        List<Track> tracks1 = localEvent1.getTracksTB();
        List<Track> tracks2 = localEvent2.getTracksTB(); 
        
        HistoGroup histoGroupCovMatComp = histoGroupMap.get("covMatComp");  
        
        for(Track trk1 : tracks1){
            if(trk1.isValid(true)){
                double covMat[][] = trk1.getCovMat().getCovMat();
                for(int i = 0; i < 6; i++){
                    for(int j = 0; j < 6; j++){
                            histoGroupCovMatComp.getH1F("C" + Integer.toString(i+1) + Integer.toString(j+1)).fill(covMat[i][j]);
                        }
                }
            }
        }
        
        for(Track trk2 : tracks2){
            if(trk2.isValid(true)){
                double covMat[][] = trk2.getCovMat().getCovMat();
                for(int i = 0; i < 6; i++){
                    for(int j = 0; j < 6; j++){
                            histoGroupCovMatComp.getH1F("2C" + Integer.toString(i+1) + Integer.toString(j+1)).fill(covMat[i][j]);
                        }
                }
            }
        }        
        
        
    }                 
    
    public static void main(String[] args) {
        OptionParser parser = new OptionParser("studyBgEffectsDC");
        parser.setRequiresInputList(false);
        // valid options for event-base analysis
        parser.addOption("-o", "", "output file name prefix");
        parser.addOption("-n", "-1", "maximum number of events to process");
        parser.addOption("-plot", "1", "display histograms (0/1)");
        parser.addOption("-trkType"      ,"22",   "tracking type: 12 (ConvTB) or 22 (AITB)");


        // histogram based analysis
        parser.addOption("-histo", "0", "read histogram file (0/1)");

        parser.parse(args);

        String namePrefix = parser.getOption("-o").stringValue();
        int maxEvents = parser.getOption("-n").intValue();
        boolean displayPlots = (parser.getOption("-plot").intValue() != 0);
        boolean readHistos = (parser.getOption("-histo").intValue() != 0);
        int trkType = parser.getOption("-trkType").intValue();
        
        List<String> inputList = parser.getInputList();
        if (inputList.isEmpty() == true) {
            parser.printUsage();
            inputList.add("/Users/caot/research/clas12/data/mc/uRWELL/upgradeTrackingWithuRWELL/rga-sidis-uRWell-2R_denoise/0nA/reconBg/0000.hipo");
            inputList.add("/Users/caot/research/clas12/data/mc/uRWELL/upgradeTrackingWithuRWELL/rga-sidis-uRWell-2R_denoise/50nA/reconBg/0000.hipo");
            maxEvents = 1000;
            //System.out.println("\n >>>> error: no input file is specified....\n");
            //System.exit(0);
        }

        String histoName = "histo.hipo";
        if (!namePrefix.isEmpty()) {
            histoName = namePrefix + "_" + histoName;
        }
        
        CompareCovMat analysis = new CompareCovMat();
        analysis.createHistoGroupMap();
        

        if (!readHistos) {
            HipoReader reader1 = new HipoReader();
            reader1.open(inputList.get(0));
            HipoReader reader2 = new HipoReader();
            reader2.open(inputList.get(1));

            SchemaFactory schema1 = reader1.getSchemaFactory();
            SchemaFactory schema2 = reader2.getSchemaFactory();
            analysis.initReader(new Banks(schema1), new Banks(schema2));

            int counter = 0;
            Event event1 = new Event();
            Event event2 = new Event();

            ProgressPrintout progress = new ProgressPrintout();
            while (reader1.hasNext() && reader2.hasNext()) {

                counter++;

                reader1.nextEvent(event1);
                reader2.nextEvent(event2);

                analysis.processEvent(event1, event2, trkType);

                progress.updateStatus();
                if (maxEvents > 0) {
                    if (counter >= maxEvents) {
                        break;
                    }
                }
            }

            progress.showStatus();
            reader1.close();
            reader2.close();
            analysis.saveHistos(histoName);
        } else {
            analysis.readHistos(inputList.get(0));
        }

        if (displayPlots) {
            JFrame frame = new JFrame();
            EmbeddedCanvasTabbed canvas = analysis.plotHistos();
            if(canvas != null){
                frame.setSize(1500, 1200);
                frame.add(canvas);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        }        
    }

    
}