package org.clas.analysis.trackingEfficiency;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
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
import org.clas.physicsEvent.EventForLumiScan;
import org.clas.analysis.BaseAnalysis;
/**
 *
 * @author Tongtong Cao
 */
public class TrackingEfficiency extends BaseAnalysis{
    
    public TrackingEfficiency(){}
    
    @Override
    public void createHistoGroupMap(){                        
        // eh+/-
        HistoGroup histoGroup = new HistoGroup("eh", 3, 1);
        double rmin = 0.5;
        double rmax = 4.0;
        H1F hi_we = new H1F("We" + "_" + histoGroup.getName(), "", 100, rmin, rmax);     
        hi_we.setTitleX("W(ep#rarrow e'X) (GeV)");
        hi_we.setTitleY("Counts");
        H1F hi_wehp = new H1F("Wehp" + "_" + histoGroup.getName(), "", 100, rmin, rmax);     
        hi_wehp.setTitleX("W(ep#rarrow e'h^+X) (GeV)");
        hi_wehp.setTitleY("Counts");
        H1F hi_wehm = new H1F("Wehm" + "_" + histoGroup.getName(), "", 100, rmin, rmax);     
        hi_wehm.setTitleX("W(ep#rarrow e'h^-X) (GeV)");
        hi_wehm.setTitleY("Counts");
        histoGroup.addDataSet(hi_we,   0);
        histoGroup.addDataSet(hi_wehp, 1);
        histoGroup.addDataSet(hi_wehm, 2);
                                
        histoGroupMap.put(histoGroup.getName(), histoGroup);
    }
      
    public void processEvent(Event event, int trkType){
        List<Track> tracks = reader.readTracks(event, trkType);   
        
        EventForLumiScan localEvent = new EventForLumiScan(tracks);
        
        if(localEvent.getTriggerTrk() != null){
            Particle trigger = localEvent.getTriggerTrk().particle();
            
            Particle W = new Particle();
            W.copy(localEvent.getTarget());
            W.combine(localEvent.getBeam(), +1);

            W.combine(trigger, -1);
            histoGroupMap.get("eh").getH1F("We_eh").fill(W.mass());
            for(int i=0; i<localEvent.getPosTrks().size(); i++) histoGroupMap.get("eh").getH1F("Wehp_eh").fill(W.mass());
            for(int i=0; i<localEvent.getNegTrks().size(); i++) histoGroupMap.get("eh").getH1F("Wehm_eh").fill(W.mass());              
        }
    }
    
    public void loadStatistics(){
        int ne = (int) histoGroupMap.get("eh").getH1F("We_eh").getIntegral();
        int nehp = (int) histoGroupMap.get("eh").getH1F("Wehp_eh").getIntegral();
        int nehm = (int) histoGroupMap.get("eh").getH1F("Wehm_eh").getIntegral();
        
        System.out.println("+--------------------------------------------------------------------------------------+");
        System.out.println("|            e |          eh+ |          eh- |      eh+/e |       eh-/e |  eh+/e err |   eh-/e err |");        
        System.out.println(String.format("| %12d | % 12d | %12d | %10.4f |  %10.4f | %10.4f |  %10.4f |",
                                                ne,
                                                nehp,
                                                nehm,
                                                (double)nehp/ne,
                                                (double)nehm/ne,
                                                Math.sqrt(nehp * ne * ne + nehp * nehp * ne) / Math.pow(ne, 2), 
                                                Math.sqrt(nehm * ne * ne + nehm * nehm * ne) / Math.pow(ne, 2) ));   
    }
                        
    public static void main(String[] args){
        OptionParser parser = new OptionParser("trackingEfficiency");
        parser.setRequiresInputList(false);
        // valid options for event-base analysis
        parser.addOption("-o"          ,"",     "output file name prefix");
        parser.addOption("-n"          ,"-1",   "maximum number of events to process");
        parser.addOption("-energy"     ,"10.6", "beam energy");
        parser.addOption("-target"     ,"2212", "target PDG");
        parser.addOption("-trkType"      ,"22",   "tracking type: ConvHB(11) or ConvTB(12) or AIHB(21) or AITB(22)");
        parser.addOption("-edge"       ,"",     "colon-separated DC, FTOF, ECAL edge cuts in cm (e.g. 5:10:5)");
        
        parser.addOption("-plot"       ,"1",    "display histograms (0/1)");
        
        // histogram based analysis
        parser.addOption("-histo"      ,"0",    "read histogram file (0/1)");
        
        parser.parse(args);
        
        String namePrefix  = parser.getOption("-o").stringValue(); 
        int   maxEvents  = parser.getOption("-n").intValue();
        Constants.BEAMENERGY = parser.getOption("-energy").doubleValue();
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
        
        TrackingEfficiency analysis = new TrackingEfficiency();
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

                    analysis.processEvent(event, trkType);

                    progress.updateStatus();
                    if(maxEvents>0){
                        if(counter>=maxEvents) break;
                    }                    
                }

                progress.showStatus();
                reader.close();
            }
            analysis.saveHistos(histoName);
        }
        else {
            analysis.readHistos(inputList.get(0)); 
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
