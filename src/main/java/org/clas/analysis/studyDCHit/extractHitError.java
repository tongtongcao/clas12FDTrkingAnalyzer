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
import org.clas.graph.HistoGroup;
import org.clas.analysis.BaseAnalysis;
import org.clas.reader.Banks;
import org.clas.reader.LocalEvent;


/**
 *
 * @author Tongtong Cao
 */
public class extractHitError extends BaseAnalysis{ 
    
    private final double CutDAFWeightSingleHit = 0.95;
    private final double CutDAFWeightDoubleHit = 0.4;    
    private final double[] CellSize = {0.6725, 0.704, 1.0831, 1.147, 1.6286, 1.7032};
    
    private final double XMinSingleHit = 0;
    private final double XMaxSingleHit = 0.9;
    private final int XNBinsSingleHit = 48; 
    private final int SLICEHISTOGROUPCOLSSingleHit = 8;
    private final int SLICEHISTOGROUPROWSSingleHit = 6;
    private final double CutSigmaSingleHit = 0.05;
    
    private final double XMinDoubleHit = 0.7;
    private final double XMaxDoubleHit = 1.2;
    private final int XNBinsDoubleHit = 48; 
    private final int SLICEHISTOGROUPCOLSDoubleHit = 8;
    private final int SLICEHISTOGROUPROWSDoubleHit = 6;
    private final double CutSigmaDoubleHit = 0.1;    
    
    public extractHitError(){}
    
    @Override
    public void createHistoGroupMap(){                         
        HistoGroup histoGroupDafWeightSingleHit = new HistoGroup("dafWeightSingleHit", 3, 2);
        HistoGroup histoGroupDafWeightDoubleHit = new HistoGroup("dafWeightDoubleHit", 3, 2);
        HistoGroup histoGroupXVsResidual = new HistoGroup("xVsResidual", 3, 2); 
        HistoGroup histoGroupXVsResidualFullSingleHit = new HistoGroup("xVsResidualFullSingleHit", 3, 2); 
        HistoGroup histoGroupXVsResidualFullDoubleHit = new HistoGroup("xVsResidualFullDoubleHit", 3, 2);          
        HistoGroup histoGroupXVsResidualSingleHit = new HistoGroup("xVsResidualSingleHit", 3, 2); 
        HistoGroup histoGroupXVsResidualDoubleHit = new HistoGroup("xVsResidualDoubleHit", 3, 2); 
        
        for (int iSL = 0; iSL < 6; iSL++) {
            H1F h1_dafWeightSingleHit = new H1F("DAF weight for single hit in SL" + Integer.toString(iSL + 1),
                    "DAF weight for single hit in SL" + Integer.toString(iSL + 1), 101, 0, 1.01);
            h1_dafWeightSingleHit.setTitleX("residual (cm)");
            h1_dafWeightSingleHit.setTitleY("Counts");
            histoGroupDafWeightSingleHit.addDataSet(h1_dafWeightSingleHit, iSL);
            
            H1F h1_dafWeightDoubleHit = new H1F("DAF weight for double hit in SL" + Integer.toString(iSL + 1),
                    "DAF weight for double hit in SL" + Integer.toString(iSL + 1), 101, 0, 1.01);
            h1_dafWeightDoubleHit.setTitleX("residual (cm)");
            h1_dafWeightDoubleHit.setTitleY("Counts");
            histoGroupDafWeightDoubleHit.addDataSet(h1_dafWeightDoubleHit, iSL);

            H2F h2_xVsResidual = new H2F("residual vs x in SL" + Integer.toString(iSL + 1),
                    "residual for SL" + Integer.toString(iSL + 1), 100, 0, 1.4, 100, -0.2, 0.2);
            h2_xVsResidual.setTitleX("x");
            h2_xVsResidual.setTitleY("residual (cm)");
            histoGroupXVsResidual.addDataSet(h2_xVsResidual, iSL);            
            
            H2F h2_xVsResidualFullSingleHit = new H2F("residual vs full x for single hit in SL" + Integer.toString(iSL + 1),
                    "residual for SL" + Integer.toString(iSL + 1), 100, 0, 1.4, 100, -0.2, 0.2);
            h2_xVsResidualFullSingleHit.setTitleX("x");
            h2_xVsResidualFullSingleHit.setTitleY("residual (cm)");
            histoGroupXVsResidualFullSingleHit.addDataSet(h2_xVsResidualFullSingleHit, iSL);
            
            H2F h2_xVsResidualFullDoubleHit = new H2F("residual vs full x for double hit in SL" + Integer.toString(iSL + 1),
                    "residual for SL" + Integer.toString(iSL + 1), 100, 0, 1.4, 100, -0.2, 0.2);
            h2_xVsResidualFullDoubleHit.setTitleX("x");
            h2_xVsResidualFullDoubleHit.setTitleY("residual (cm)");
            histoGroupXVsResidualFullDoubleHit.addDataSet(h2_xVsResidualFullDoubleHit, iSL); 
            
            H2F h2_xVsResidualSingleHit = new H2F("residual vs x for single hit in SL" + Integer.toString(iSL + 1),
                    "residual for SL" + Integer.toString(iSL + 1), XNBinsSingleHit, XMinSingleHit, XMaxSingleHit, 100, -0.2, 0.2);
            h2_xVsResidualSingleHit.setTitleX("x");
            h2_xVsResidualSingleHit.setTitleY("residual (cm)");
            histoGroupXVsResidualSingleHit.addDataSet(h2_xVsResidualSingleHit, iSL);  
            
            H2F h2_xVsResidualDoubleHit = new H2F("residual vs x for double hit in SL" + Integer.toString(iSL + 1),
                    "residual for SL" + Integer.toString(iSL + 1), XNBinsDoubleHit, XMinDoubleHit, XMaxDoubleHit, 100, -0.2, 0.2);
            h2_xVsResidualDoubleHit.setTitleX("x");
            h2_xVsResidualDoubleHit.setTitleY("residual (cm)");
            histoGroupXVsResidualDoubleHit.addDataSet(h2_xVsResidualDoubleHit, iSL);             
                                    
        }
        histoGroupMap.put(histoGroupDafWeightSingleHit.getName(), histoGroupDafWeightSingleHit);
        histoGroupMap.put(histoGroupDafWeightDoubleHit.getName(), histoGroupDafWeightDoubleHit); 
        histoGroupMap.put(histoGroupXVsResidual.getName(), histoGroupXVsResidual);
        histoGroupMap.put(histoGroupXVsResidualFullSingleHit.getName(), histoGroupXVsResidualFullSingleHit);  
        histoGroupMap.put(histoGroupXVsResidualFullDoubleHit.getName(), histoGroupXVsResidualFullDoubleHit); 
        histoGroupMap.put(histoGroupXVsResidualSingleHit.getName(), histoGroupXVsResidualSingleHit);  
        histoGroupMap.put(histoGroupXVsResidualDoubleHit.getName(), histoGroupXVsResidualDoubleHit);  
    }
             
    public void processEvent(Event event, int trkType){        
        //Read banks
        LocalEvent localEvent = new LocalEvent(reader, event, trkType, true);        
        
        HistoGroup histoGroupDafWeightSingleHit = histoGroupMap.get("dafWeightSingleHit");
        HistoGroup histoGroupDafWeightDoubleHit = histoGroupMap.get("dafWeightDoubleHit");
        HistoGroup histoGroupXVsResidual = histoGroupMap.get("xVsResidual");
        HistoGroup histoGroupXVsResidualFullSingleHit = histoGroupMap.get("xVsResidualFullSingleHit");
        HistoGroup histoGroupXVsResidualFullDoubleHit = histoGroupMap.get("xVsResidualFullDoubleHit");        
        HistoGroup histoGroupXVsResidualSingleHit = histoGroupMap.get("xVsResidualSingleHit");
        HistoGroup histoGroupXVsResidualDoubleHit = histoGroupMap.get("xVsResidualDoubleHit");
        
        for(Track trk : localEvent.getTracksTB()){
            if(trk.isValid()){
                for(Hit hit : trk.getHits()){
                    int sl = hit.superlayer();
                    double doca = hit.trkDoca();
                    double x = doca/CellSize[sl - 1];
                   
                    double dafWeight = hit.dafWeight();
                    int firtBitStatus = hit.status() & 1;
                    
                    
                    if(firtBitStatus == 0) {
                        histoGroupDafWeightSingleHit.getH1F("DAF weight for single hit in SL" + Integer.toString(hit.superlayer())).fill(dafWeight);
                        if(dafWeight > CutDAFWeightSingleHit){
                            histoGroupXVsResidual.getH2F("residual vs x in SL" + Integer.toString(hit.superlayer())).fill(x, hit.fitResidual());
                            histoGroupXVsResidualFullSingleHit.getH2F("residual vs full x for single hit in SL" + Integer.toString(hit.superlayer())).fill(x, hit.fitResidual());
                            histoGroupXVsResidualSingleHit.getH2F("residual vs x for single hit in SL" + Integer.toString(hit.superlayer())).fill(x, hit.fitResidual());
                        }
                    } 
                    else{
                        histoGroupDafWeightDoubleHit.getH1F("DAF weight for double hit in SL" + Integer.toString(hit.superlayer())).fill(dafWeight);
                        if(dafWeight > CutDAFWeightDoubleHit){
                            histoGroupXVsResidual.getH2F("residual vs x in SL" + Integer.toString(hit.superlayer())).fill(x, hit.fitResidual());
                            histoGroupXVsResidualFullDoubleHit.getH2F("residual vs full x for double hit in SL" + Integer.toString(hit.superlayer())).fill(x, hit.fitResidual());
                            histoGroupXVsResidualDoubleHit.getH2F("residual vs x for double hit in SL" + Integer.toString(hit.superlayer())).fill(x, hit.fitResidual());
                        }
                    }                      
                }
            }
        }                                        
    }

    public void postEventProcess(){
        
        ////// Single hits
        HistoGroup histoGroupXVsResidualSingleHit = histoGroupMap.get("xVsResidualSingleHit");
        
        // Group of GraphErrors for error vs x
        HistoGroup histoGroupErrorSingleHit = new HistoGroup("errorVsXSingleHit", 3, 2);         
                
        for (int iSL = 0; iSL < 6; iSL++) {
            
            GraphErrors gr_error = new GraphErrors("Error vs x for SL" + Integer.toString(iSL + 1));
            gr_error.setTitleX("x");
            gr_error.setTitleY("error (cm)");  
            gr_error.setMarkerSize(3);
            
            H2F h2 = histoGroupXVsResidualSingleHit.getH2F("residual vs x for single hit in SL" + Integer.toString(iSL + 1));

            int nBinsX = h2.getXAxis().getNBins();

            HistoGroup histoGroupSliceSingleHit = new HistoGroup("sliceSingleHitSL" + Integer.toString(iSL + 1), SLICEHISTOGROUPCOLSSingleHit, SLICEHISTOGROUPROWSSingleHit);
            int numSavedSlices = 0;
            
            for (int bx = 0; bx < nBinsX; bx++) {
                H1F slice = h2.sliceX(bx);
                histoGroupSliceSingleHit.addDataSet(slice, numSavedSlices++);
                                                
                // Skip slice with low statistic
                if (slice.getMax() < 80) continue;

                double xCenter = h2.getXAxis().getBinCenter(bx);               

                // Fit by Gaus
                F1D fgaus = new F1D("fgaus", "[amp]*exp(-0.5*((x-[mean])/[sigma])^2)", -0.1, 0.1);

                fgaus.setParameter(0, slice.getMax());
                fgaus.setParameter(1, 0.0);
                fgaus.setParameter(2, 0.05);
                fgaus.setLineColor(2);

                slice.fit(fgaus, "Q");  // Q = quiet mode                

                double sigma = fgaus.getParameter(2);
                double sigmaErr = fgaus.parameter(2).error();                

                if(Math.abs(sigma) < CutSigmaSingleHit) // Exclude slice with bad fitting
                    gr_error.addPoint(xCenter, sigma, 0.0, sigmaErr);
                
                histoGroupErrorSingleHit.addDataSet(gr_error, iSL);
                
                // Add a curve for reference function
                F1D f_ref = new F1D("f_ref_singleHit_SL" + (iSL + 1),"0.06 - 0.14*x^(1.5) + 0.18*x^(2.5)",XMinSingleHit, XMaxSingleHit);
                f_ref.setLineColor(2);   // red
                f_ref.setLineWidth(2);
                f_ref.setLineStyle(2);   // broken line
                histoGroupErrorSingleHit.addDataSet(f_ref, iSL);
                
                F1D f_ref2 = new F1D("f_ref2_singleHit_SL" + (iSL + 1),"(0.06 - 0.14*x^(1.5) + 0.18*x^(2.5))/2.",XMinSingleHit, XMaxSingleHit);
                f_ref2.setLineColor(3);   // green
                f_ref2.setLineWidth(2);
                f_ref2.setLineStyle(2);   // broken line
                histoGroupErrorSingleHit.addDataSet(f_ref2, iSL);
            }
            
            histoGroupMap.put(histoGroupSliceSingleHit.getName(), histoGroupSliceSingleHit);
        }
        
        histoGroupMap.put(histoGroupErrorSingleHit.getName(), histoGroupErrorSingleHit);
                
        ////// Double hits
        HistoGroup histoGroupXVsResidualDoubleHit = histoGroupMap.get("xVsResidualDoubleHit");
        
        // Group of GraphErrors for error vs x
        HistoGroup histoGroupErrorDoubleHit = new HistoGroup("errorVsXDoubleHit", 3, 2);         
                
        for (int iSL = 0; iSL < 6; iSL++) {
            
            GraphErrors gr_error = new GraphErrors("Error vs x for SL" + Integer.toString(iSL + 1));
            gr_error.setTitleX("x");
            gr_error.setTitleY("error (cm)");  
            gr_error.setMarkerSize(3);
            
            H2F h2 = histoGroupXVsResidualDoubleHit.getH2F("residual vs x for double hit in SL" + Integer.toString(iSL + 1));

            int nBinsX = h2.getXAxis().getNBins();

            HistoGroup histoGroupSliceDoubleHit = new HistoGroup("sliceDoubleHitSL" + Integer.toString(iSL + 1), SLICEHISTOGROUPCOLSDoubleHit, SLICEHISTOGROUPROWSDoubleHit);
            int numSavedSlices = 0;
            
            for (int bx = 0; bx < nBinsX; bx++) {
                H1F slice = h2.sliceX(bx);
                histoGroupSliceDoubleHit.addDataSet(slice, numSavedSlices++);
                                                
                // Skip slice with low statistic
                if (slice.getMax() < 80) continue;

                double xCenter = h2.getXAxis().getBinCenter(bx);               

                // Fit by Gaus
                F1D fgaus = new F1D("fgaus", "[amp]*exp(-0.5*((x-[mean])/[sigma])^2)", -0.1, 0.1);

                fgaus.setParameter(0, slice.getMax());
                fgaus.setParameter(1, 0.0);
                fgaus.setParameter(2, 0.05);
                fgaus.setLineColor(2);

                slice.fit(fgaus, "Q");  // Q = quiet mode                

                double sigma = fgaus.getParameter(2);
                double sigmaErr = fgaus.parameter(2).error();                

                if(Math.abs(sigma) < 0.05) // Exclude slice with bad fitting
                    gr_error.addPoint(xCenter, sigma, 0.0, sigmaErr);
                
                histoGroupErrorDoubleHit.addDataSet(gr_error, iSL);
                
                // Add a curve for reference function
                F1D f_ref = new F1D("f_ref_doubleHit_SL" + (iSL + 1),"0.06 - 0.14*x^(1.5) + 0.18*x^(2.5)",XMinDoubleHit, XMaxDoubleHit);
                f_ref.setLineColor(2);   // red
                f_ref.setLineWidth(2);
                f_ref.setLineStyle(2);   // broken line
                histoGroupErrorDoubleHit.addDataSet(f_ref, iSL);
                
                F1D f_ref2 = new F1D("f_ref2_doubleHit_SL" + (iSL + 1),"(0.06 - 0.14*x^(1.5) + 0.18*x^(2.5))/2.",XMinDoubleHit, XMaxDoubleHit);
                f_ref2.setLineColor(3);   // green
                f_ref2.setLineWidth(2);
                f_ref2.setLineStyle(2);   // broken line
                histoGroupErrorDoubleHit.addDataSet(f_ref2, iSL);                
            }
            
            histoGroupMap.put(histoGroupSliceDoubleHit.getName(), histoGroupSliceDoubleHit);
        }
        
        histoGroupMap.put(histoGroupErrorDoubleHit.getName(), histoGroupErrorDoubleHit);         
    }
                          
    public static void main(String[] args){
        OptionParser parser = new OptionParser("extractHitError");
        parser.setRequiresInputList(false);
        // valid options for event-base analysis
        parser.addOption("-o"          ,"",     "output file name prefix");
        parser.addOption("-n"          ,"-1",   "maximum number of events to process");  
        parser.addOption("-trkType", "12", "tracking type: ConvTB(12) or AITB(22)");
        parser.addOption("-plot"       ,"1",    "display histograms (0/1)");
                
        // histogram based analysis
        parser.addOption("-histo"      ,"0",    "read histogram file (0/1)");
        
        parser.parse(args);
        
        String namePrefix  = parser.getOption("-o").stringValue(); 
        int   maxEvents  = parser.getOption("-n").intValue();   
        int trkType = parser.getOption("-trkType").intValue();
        boolean displayPlots   = (parser.getOption("-plot").intValue()!=0);
        boolean readHistos   = (parser.getOption("-histo").intValue()!=0);  
        
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
        
        extractHitError analysis = new extractHitError();
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
            
            analysis.saveHistos(histoName);
            analysis.postEventProcess();           
        }
        else{
            analysis.readHistos(inputList.get(0)); 
            analysis.postEventProcess();
        }
        
        if(displayPlots) {
            JFrame frame = new JFrame();
            EmbeddedCanvasTabbed canvas = analysis.plotHistos();
            for (int iSL = 0; iSL < 6; iSL++) {
                canvas.getCanvas("errorVsXSingleHit").getPad(iSL).getAxisY().setRange(0.0, 0.15);
                canvas.getCanvas("errorVsXDoubleHit").getPad(iSL).getAxisY().setRange(0.0, 0.15);
            }
            frame.setSize(1800, 1200);
            frame.add(canvas);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        }        
    }
    
}
