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
public class AllChannels extends BaseAnalysis {
    EP2EPIpX eP2EPIpX = new EP2EPIpX();
    EP2EPIpPImX eP2EPIpPImX = new EP2EPIpPImX();
    EP2EPX eP2EPX = new EP2EPX();
    
    public AllChannels() {}    

    @Override
    public void createHistoGroupMap() {
        eP2EPIpX.createHistoGroupMap();
        for(String key : eP2EPIpX.getHistoGroupMap().keySet()){
            histoGroupMap.put(key, eP2EPIpX.getHistoGroupMap().get(key));
        }
        
        eP2EPIpPImX.createHistoGroupMap();
        for(String key : eP2EPIpPImX.getHistoGroupMap().keySet()){
            histoGroupMap.put(key, eP2EPIpPImX.getHistoGroupMap().get(key));
        }   
        
        eP2EPX.createHistoGroupMap();
        for(String key : eP2EPX.getHistoGroupMap().keySet()){
            histoGroupMap.put(key, eP2EPX.getHistoGroupMap().get(key));
        }          
    }

    public void processEvent(EventValidTracks localEvent) {
        eP2EPIpX.processEvent(localEvent);
        eP2EPIpPImX.processEvent(localEvent);   
        eP2EPX.processEvent(localEvent); 
    }

    public void postEventProcess() {                
        eP2EPIpX.postEventProcess();
        eP2EPIpPImX.postEventProcess(); 
        eP2EPX.postEventProcess(); 
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
        
        AllChannels analysis = new AllChannels();
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
