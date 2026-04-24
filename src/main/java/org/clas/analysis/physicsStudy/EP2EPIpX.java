package org.clas.analysis.physicsStudy;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import org.jlab.groot.data.H1F;
import org.jlab.groot.graphics.EmbeddedCanvasTabbed;
import org.jlab.jnp.hipo4.data.Event;
import org.jlab.jnp.hipo4.data.SchemaFactory;
import org.jlab.jnp.hipo4.io.HipoReader;
import org.jlab.utils.benchmark.ProgressPrintout;
import org.jlab.utils.options.OptionParser;
import org.jlab.clas.physics.Particle;

import org.clas.utilities.Constants;
import org.clas.element.Track;
import org.clas.reader.Banks;
import org.clas.graph.HistoGroup;
import org.clas.physicsEvent.EventValidTracks;
import org.clas.analysis.BaseAnalysis;
import org.jlab.groot.math.F1D;
/**
 *
 * @author Tongtong Cao
 */
public class EP2EPIpX extends BaseAnalysis{
    
    public EP2EPIpX(){}
    
    @Override
    public void createHistoGroupMap(){                        
        // eh+/-
        HistoGroup histoGroup = new HistoGroup("Mx(ep#rarrow e'#pi+X)", 1, 1);
        H1F hi_mx = new H1F("Mx(ep#rarrow e'#pi+X)", "", 100, 0.7, 1.4);     
        hi_mx.setTitleX("Mx(ep#rarrow e'#pi+X) (GeV)");
        hi_mx.setTitleY("Counts");
        histoGroup.addDataSet(hi_mx,   0);
                                
        histoGroupMap.put(histoGroup.getName(), histoGroup);
    }
      
    public void processEvent(EventValidTracks localEvent){
        
        if(localEvent.getTriggerTrk() != null){
            Particle beam = localEvent.getBeam();
            Particle target = localEvent.getTarget();
            Particle trigger = localEvent.getTriggerTrk().particle();  
                        
            for(Track piPlus : localEvent.getPiPlusTrks()){
                Particle X = new Particle();
                X.copy(target);
                X.combine(beam, +1);

                X.combine(trigger, -1);
                X.combine(piPlus.particle(), -1);
                histoGroupMap.get("Mx(ep#rarrow e'#pi+X)").getH1F("Mx(ep#rarrow e'#pi+X)").fill(X.mass()); 
            }
        }
    }
    
    public void postEventProcess(){        
        HistoGroup histoGroup = histoGroupMap.get("Mx(ep#rarrow e'#pi+X)");
        H1F hi_mx =  histoGroup.getH1F("Mx(ep#rarrow e'#pi+X)");
        F1D func  = new F1D("func","[amp]*gaus(x,[mean],[sigma]) + + [p0] + [p1]*x", 0.8, 1.2);
        func.setParameter(0, hi_mx.getMax());
        func.setParameter(1, 0.94);
        func.setParameter(2, 0.04);
        func.setLineColor(2);
        hi_mx.setOptStat("1111111111111");
        hi_mx.fit(func);
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("fit_results_ep2epi+X.txt"))) {
            writer.write("Amp\tAmpErr\tMean\tMeanErr\tSigma\tSigmaErr\n");            
            writer.write(String.format("%f\t%f\t%f\t%f\t%f\t%f\n",
                    hi_mx.getFunction().getParameter(0), 
                    hi_mx.getFunction().parameter(0).error(),
                    hi_mx.getFunction().getParameter(1),
                    hi_mx.getFunction().parameter(1).error(),
                    hi_mx.getFunction().getParameter(2),   
                    hi_mx.getFunction().parameter(2).error()));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void loadStatistics(){
    }
                        
    public static void main(String[] args){
        OptionParser parser = new OptionParser("trackingEfficiency");
        parser.setRequiresInputList(false);
        // valid options for event-base analysis
        parser.addOption("-o"          ,"",     "output file name prefix");
        parser.addOption("-n"          ,"-1",   "maximum number of events to process");
        parser.addOption("-beam"     ,"10.6", "beam energy");
        parser.addOption("-target"     ,"2212", "target PDG");
        parser.addOption("-trkType"      ,"22",   "tracking type: ConvHB(11) or ConvTB(12) or AIHB(21) or AITB(22)");
        parser.addOption("-edge"       ,"",     "colon-separated DC, FTOF, ECAL edge cuts in cm (e.g. 5:10:5)");
        
        parser.addOption("-plot"       ,"1",    "display histograms (0/1)");
        
        // histogram based analysis
        parser.addOption("-histo"      ,"0",    "read histogram file (0/1)");
        
        parser.parse(args);
        
        String namePrefix  = parser.getOption("-o").stringValue(); 
        int   maxEvents  = parser.getOption("-n").intValue();
        Constants.BEAMENERGY = parser.getOption("-beam").doubleValue();
        Constants.TARGETPID  = parser.getOption("-target").intValue();  
        int trkType = parser.getOption("-trkType").intValue(); 
        String[] edge  = parser.getOption("-edge").stringValue().split(":");
        if(!parser.getOption("-edge").stringValue().isBlank() && edge.length != 3) {
            System.out.println("\n >>>> error: incorrect edge parameters...\n");
            System.exit(0);
        }
        else if(edge.length==3) {
            Constants.EDGE[0] = Double.parseDouble(edge[0]);
            Constants.EDGE[1] = Double.parseDouble(edge[1]);
            Constants.EDGE[2] = Double.parseDouble(edge[2]);
        }        
        boolean openWindow   = (parser.getOption("-plot").intValue()!=0);
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
        
        EP2EPIpX analysis = new EP2EPIpX();
        analysis.createHistoGroupMap();

        if(!readHistos) {
            for(String inputFile : inputList){
                HipoReader reader = new HipoReader();
                reader.open(inputFile);        
                SchemaFactory schema = reader.getSchemaFactory();
                //Reader localReader = new Reader(schema);
                analysis.initReader(new Banks(schema));

                int counter=0;
                Event event = new Event();

                ProgressPrintout progress = new ProgressPrintout();

                while (reader.hasNext()) {

                    counter++;

                    reader.nextEvent(event);
                    
                    List<Track> tracks = analysis.reader.readTracks(event, trkType);           
                    EventValidTracks localEvent = new EventValidTracks(tracks);
                    analysis.processEvent(localEvent);

                    progress.updateStatus();
                    if(maxEvents>0){
                        if(counter>=maxEvents) break;
                    }                    
                }

                progress.showStatus();
                reader.close();
            }
            analysis.saveHistos(histoName);
            analysis.postEventProcess();
        }
        else {
            analysis.readHistos(inputList.get(0)); 
            analysis.postEventProcess();
        }
        
        analysis.loadStatistics();
        
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
