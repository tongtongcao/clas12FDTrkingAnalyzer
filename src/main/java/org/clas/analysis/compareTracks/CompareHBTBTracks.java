package org.clas.analysis.compareTracks;

import java.util.ArrayList;
import java.util.List;
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
import org.clas.graph.HistoGroup;
import org.clas.physicsEvent.BasePhysicsEvent;
import org.clas.physicsEvent.EventForLumiScan;
import org.clas.analysis.BaseAnalysis;
import org.clas.graph.TrackHistoGroup;
import org.clas.reader.Banks;

/**
 *
 * @author Tongtong Cao
 */
public class CompareHBTBTracks extends BaseAnalysis{
    
    public CompareHBTBTracks(){}

    public CompareHBTBTracks(Banks banks){
        super(banks);
    }
    
    @Override
    public void createHistoGroupMap(){ 
        HistoGroup histoGroupKinematicsComp= new HistoGroup("kinematicsComp", 3, 2); 
        H2F h2_chi2OverNDF_comp = new H2F("chi2OverNDF_comp", "comp. for chi2/NDF", 100, 0, 20, 100, 0, 20);
        h2_chi2OverNDF_comp.setTitleX("chi2/NDF for HB");
        h2_chi2OverNDF_comp.setTitleY("chi2/NDF for TB");
        histoGroupKinematicsComp.addDataSet(h2_chi2OverNDF_comp, 0);
        
        H2F h2_z_comp = new H2F("z_comp", "comp. for z", 100, -50, 50, 100, -50, 50);
        h2_z_comp.setTitleX("z for HB");
        h2_z_comp.setTitleY("z for TB");
        histoGroupKinematicsComp.addDataSet(h2_z_comp, 1);
        
        H2F h2_p_comp = new H2F("p_comp", "comp. for p", 100, 0, 12, 100, 0, 12);
        h2_p_comp.setTitleX("p for HB");
        h2_p_comp.setTitleY("p for TB");
        histoGroupKinematicsComp.addDataSet(h2_p_comp, 2);
        
        H1F h1_chi2OverNDF_noMatchedHB = new H1F("chi2OverNDF_noMatchedHB", "chi2/NDF for no matched HB", 100, 0, 20);
        h1_chi2OverNDF_noMatchedHB.setTitleX("chi2/NDF");
        h1_chi2OverNDF_noMatchedHB.setTitleY("counts");
        histoGroupKinematicsComp.addDataSet(h1_chi2OverNDF_noMatchedHB, 3);
        
        H1F h1_z_noMatchedHB = new H1F("z_noMatchedHB", "cz for no matched HB", 100, -50, 50);
        h1_z_noMatchedHB.setTitleX("z");
        h1_z_noMatchedHB.setTitleY("counts");
        histoGroupKinematicsComp.addDataSet(h1_z_noMatchedHB, 4);
        
        H1F h1_p_noMatchedHB = new H1F("p_noMatchedHB", "p for no matched HB", 100, 0, 12);
        h1_p_noMatchedHB.setTitleX("p");
        h1_p_noMatchedHB.setTitleY("counts");
        histoGroupKinematicsComp.addDataSet(h1_p_noMatchedHB, 5);
        
        histoGroupMap.put(histoGroupKinematicsComp.getName(), histoGroupKinematicsComp);
        
        TrackHistoGroup histoGroupDiff = new TrackHistoGroup("diff", 3, 2);
        histoGroupDiff.addTrackDiffHistos(1, -1);
        histoGroupMap.put(histoGroupDiff.getName(), histoGroupDiff);
  
    }
             
    public void processEvent(Event event, int trkType){        
        List<Track> tracksHB = reader.readTracks(event, trkType - 1);  
        List<Track> tracksTB = reader.readTracks(event, trkType);
        
        HistoGroup histoGroupKinematicsComp = histoGroupMap.get("kinematicsComp");
        TrackHistoGroup histoGroupDiff = (TrackHistoGroup) histoGroupMap.get("diff");
        boolean flag = false;
        for(Track trkHB : tracksHB){
           for(Track trkTB : tracksTB){ 
               int nMatchedClusters = trkHB.matchedClusters(trkTB);
               if(nMatchedClusters == 6 || nMatchedClusters == 5){
                   histoGroupKinematicsComp.getH2F("chi2OverNDF_comp").fill(trkHB.chi2()/trkHB.NDF(), trkTB.chi2()/trkTB.NDF());
                   histoGroupKinematicsComp.getH2F("z_comp").fill(trkHB.vz(), trkTB.vz());
                   histoGroupKinematicsComp.getH2F("p_comp").fill(trkHB.p(), trkTB.p());
                   flag = true;
                   
                   if(trkTB.isValid()){
                        histoGroupDiff.getHistoChi2overndfDiff().fill(trkTB.chi2()/trkTB.NDF() - trkHB.chi2()/trkHB.NDF());
                        histoGroupDiff.getHistoPDiff().fill(trkTB.p() - trkHB.p());
                        histoGroupDiff.getHistoThetaDiff().fill(trkTB.theta() - trkHB.theta());
                        histoGroupDiff.getHistoPhiDiff().fill(trkTB.phi() - trkHB.phi());
                        histoGroupDiff.getHistoVxDiff().fill(trkTB.vx() - trkHB.vx());
                        histoGroupDiff.getHistoVyDiff().fill(trkTB.vy() - trkHB.vy());
                        histoGroupDiff.getHistoVzDiff().fill(trkTB.vz() - trkHB.vz());
                   }                   
                   
                   break;
               }
           }
           if(!flag){
               histoGroupKinematicsComp.getH1F("chi2OverNDF_noMatchedHB").fill(trkHB.chi2()/trkHB.NDF());
               histoGroupKinematicsComp.getH1F("z_noMatchedHB").fill(trkHB.vz());
               histoGroupKinematicsComp.getH1F("p_noMatchedHB").fill(trkHB.p());
           }
     
        }           
        
    }
                        
    public static void main(String[] args){
        OptionParser parser = new OptionParser("studyBgEffectsDC");
        parser.setRequiresInputList(false);
        // valid options for event-base analysis
        parser.addOption("-o"          ,"",     "output file name prefix");
        parser.addOption("-n"          ,"-1",   "maximum number of events to process");
        parser.addOption("-energy"     ,"10.6", "beam energy");
        parser.addOption("-target"     ,"2212", "target PDG");
        parser.addOption("-trkType"    ,"22",   "tracking type: ConvTB(12), AITB(22)");
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
        
        CompareHBTBTracks analysis = new CompareHBTBTracks();
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
                
                analysis.processEvent(event, trkType);

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
            frame.setSize(900, 600);
            frame.add(canvas);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        }
    }
    
}
