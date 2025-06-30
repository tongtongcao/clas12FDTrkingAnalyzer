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
import org.clas.reader.Banks;

/**
 *
 * @author Tongtong Cao
 */
public class CompareTracks extends BaseAnalysis{
    
    public CompareTracks(){}

    public CompareTracks(Banks banks){
        super(banks);
    }
    
    @Override
    public void createHistoGroupMap(){                
        HistoGroup histoData1Group = new HistoGroup("data1", 4, 2); 
        H1F h1_HB1 = new H1F("HB1", "HB/TB", 4, 3.5, 7.5);
        h1_HB1.setTitleX("# of clusters in track");
        h1_HB1.setTitleY("Counts");
        h1_HB1.setLineColor(1);
        histoData1Group.addDataSet(h1_HB1, 0);
        H1F h1_TB1 = new H1F("TB1", "HB/TB", 4, 3.5, 7.5);
        h1_TB1.setTitleX("# of clusters in track");
        h1_TB1.setTitleY("Counts");
        h1_TB1.setLineColor(2);        
        histoData1Group.addDataSet(h1_TB1, 0);
        H1F h1_chi2overndf_matchedHBTracks1 = new H1F("chi2overndf_matchedHBTracks1", "#Chi^2/ndf", 100, 0, 100);
        h1_chi2overndf_matchedHBTracks1.setTitleX("#Chi^2/ndf");
        h1_chi2overndf_matchedHBTracks1.setTitleY("Counts");
        h1_chi2overndf_matchedHBTracks1.setLineColor(1);
        histoData1Group.addDataSet(h1_chi2overndf_matchedHBTracks1, 1);
        H1F h1_z_matchedHBTracks1 = new H1F("z_matchedHBTracks1", "z", 100, -50, 50);
        h1_z_matchedHBTracks1.setTitleX("z (cm)");
        h1_z_matchedHBTracks1.setTitleY("Counts");
        h1_z_matchedHBTracks1.setLineColor(1);
        histoData1Group.addDataSet(h1_z_matchedHBTracks1, 2);
        H2F h2_xy_matchedHBTracks1 = new H2F("xy_matchedHBTracks1", "xy for matched HB tracks", 100, -50, 50, 100, -50, 50);
        h2_xy_matchedHBTracks1.setTitleX("x (cm)");
        h2_xy_matchedHBTracks1.setTitleY("y (cm)");
        histoData1Group.addDataSet(h2_xy_matchedHBTracks1, 3);
        H1F h1_p_matchedHBTracks1 = new H1F("p_matchedHBTracks1", "p", 100, 0, 12);
        h1_p_matchedHBTracks1.setTitleX("p (GeV)");
        h1_p_matchedHBTracks1.setTitleY("Counts");
        h1_p_matchedHBTracks1.setLineColor(1);
        histoData1Group.addDataSet(h1_p_matchedHBTracks1, 4);
        H1F h1_theta_matchedHBTracks1 = new H1F("theta_matchedHBTracks1", "#theta", 100, 0, Math.PI/2.);
        h1_theta_matchedHBTracks1.setTitleX("#theta (rad)");
        h1_theta_matchedHBTracks1.setTitleY("Counts");
        h1_theta_matchedHBTracks1.setLineColor(1);
        histoData1Group.addDataSet(h1_theta_matchedHBTracks1, 5);
        H1F h1_phi_matchedHBTracks1 = new H1F("phi_matchedHBTracks1", "#phi", 100, -Math.PI, Math.PI);
        h1_phi_matchedHBTracks1.setTitleX("#phi (rad)");
        h1_phi_matchedHBTracks1.setTitleY("Counts");
        h1_phi_matchedHBTracks1.setLineColor(1);
        histoData1Group.addDataSet(h1_phi_matchedHBTracks1, 6);
        
        
        H1F h1_chi2overndf_noMatchedHBTracks1 = new H1F("chi2overndf_noMatchedHBTracks1", "#Chi^2/ndf", 100, 0, 50);
        h1_chi2overndf_noMatchedHBTracks1.setTitleX("#Chi^2/ndf");
        h1_chi2overndf_noMatchedHBTracks1.setTitleY("Counts");
        h1_chi2overndf_noMatchedHBTracks1.setLineColor(2);
        histoData1Group.addDataSet(h1_chi2overndf_noMatchedHBTracks1, 1);
        H1F h1_z_noMatchedHBTracks1 = new H1F("z_noMatchedHBTracks1", "z", 100, -50, 50);
        h1_z_noMatchedHBTracks1.setTitleX("z (cm)");
        h1_z_noMatchedHBTracks1.setTitleY("Counts");
        h1_z_noMatchedHBTracks1.setLineColor(2);
        histoData1Group.addDataSet(h1_z_noMatchedHBTracks1, 2);
        H2F h2_xy_noMatchedHBTracks1 = new H2F("xy_noMatchedHBTracks1", "xy for no matched HB tracks", 100, -50, 50, 100, -50, 50);
        h2_xy_noMatchedHBTracks1.setTitleX("x (cm)");
        h2_xy_noMatchedHBTracks1.setTitleY("y (cm)");
        histoData1Group.addDataSet(h2_xy_noMatchedHBTracks1, 7);
        H1F h1_p_noMatchedHBTracks1 = new H1F("p_noMatchedHBTracks1", "p", 100, 0, 12);
        h1_p_noMatchedHBTracks1.setTitleX("p (GeV)");
        h1_p_noMatchedHBTracks1.setTitleY("Counts");
        h1_p_noMatchedHBTracks1.setLineColor(2);
        histoData1Group.addDataSet(h1_p_noMatchedHBTracks1, 4);
        H1F h1_theta_noMatchedHBTracks1 = new H1F("theta_noMatchedHBTracks1", "#theta", 100, 0, Math.PI/2.);
        h1_theta_noMatchedHBTracks1.setTitleX("#theta (rad)");
        h1_theta_noMatchedHBTracks1.setTitleY("Counts");
        h1_theta_noMatchedHBTracks1.setLineColor(2);
        histoData1Group.addDataSet(h1_theta_noMatchedHBTracks1, 5);
        H1F h1_phi_noMatchedHBTracks1 = new H1F("phi_noMatchedHBTracks1", "#phi", 100, -Math.PI, Math.PI);
        h1_phi_noMatchedHBTracks1.setTitleX("#phi (rad)");
        h1_phi_noMatchedHBTracks1.setTitleY("Counts");
        h1_phi_noMatchedHBTracks1.setLineColor(2);
        histoData1Group.addDataSet(h1_phi_noMatchedHBTracks1, 6);
        histoGroupMap.put(histoData1Group.getName(), histoData1Group);
        
        HistoGroup histoData2Group = new HistoGroup("data2", 4, 2); 
        H1F h1_HB2 = new H1F("HB2", "HB/TB", 4, 3.5, 7.5);
        h1_HB2.setTitleX("# of clusters in track");
        h1_HB2.setTitleY("Counts");
        h1_HB2.setLineColor(1);
        histoData2Group.addDataSet(h1_HB2, 0);
        H1F h1_TB2 = new H1F("TB2", "HB/TB", 4, 3.5, 7.5);
        h1_TB2.setTitleX("# of clusters in track");
        h1_TB2.setTitleY("Counts");
        h1_TB2.setLineColor(2);        
        histoData2Group.addDataSet(h1_TB2, 0);
        H1F h1_chi2overndf_matchedHBTracks2 = new H1F("chi2overndf_matchedHBTracks2", "#Chi^2/ndf", 100, 0, 50);
        h1_chi2overndf_matchedHBTracks2.setTitleX("#Chi^2/ndf");
        h1_chi2overndf_matchedHBTracks2.setTitleY("Counts");
        h1_chi2overndf_matchedHBTracks2.setLineColor(1);
        histoData2Group.addDataSet(h1_chi2overndf_matchedHBTracks2, 1);
        H1F h1_z_matchedHBTracks2 = new H1F("z_matchedHBTracks2", "z", 100, -50, 50);
        h1_z_matchedHBTracks2.setTitleX("z (cm)");
        h1_z_matchedHBTracks2.setTitleY("Counts");
        h1_z_matchedHBTracks2.setLineColor(1);
        histoData2Group.addDataSet(h1_z_matchedHBTracks2, 2);
        H2F h2_xy_matchedHBTracks2 = new H2F("xy_matchedHBTracks2", "xy for matched HB tracks", 100, -50, 50, 100, -50, 50);
        h2_xy_matchedHBTracks2.setTitleX("x (cm)");
        h2_xy_matchedHBTracks2.setTitleY("y (cm)");
        histoData2Group.addDataSet(h2_xy_matchedHBTracks2, 3);
        H1F h1_p_matchedHBTracks2 = new H1F("p_matchedHBTracks2", "p", 100, 0, 12);
        h1_p_matchedHBTracks2.setTitleX("p (GeV)");
        h1_p_matchedHBTracks2.setTitleY("Counts");
        h1_p_matchedHBTracks2.setLineColor(1);
        histoData2Group.addDataSet(h1_p_matchedHBTracks2, 4);
        H1F h1_theta_matchedHBTracks2 = new H1F("theta_matchedHBTracks2", "#theta", 100, 0, Math.PI/2.);
        h1_theta_matchedHBTracks2.setTitleX("#theta (rad)");
        h1_theta_matchedHBTracks2.setTitleY("Counts");
        h1_theta_matchedHBTracks2.setLineColor(1);
        histoData2Group.addDataSet(h1_theta_matchedHBTracks2, 5);
        H1F h1_phi_matchedHBTracks2 = new H1F("phi_matchedHBTracks2", "#phi", 100, -Math.PI, Math.PI);
        h1_phi_matchedHBTracks2.setTitleX("#phi (rad)");
        h1_phi_matchedHBTracks2.setTitleY("Counts");
        h1_phi_matchedHBTracks2.setLineColor(1);
        histoData2Group.addDataSet(h1_phi_matchedHBTracks2, 6);
        
        
        H1F h1_chi2overndf_noMatchedHBTracks2 = new H1F("chi2overndf_noMatchedHBTracks2", "#Chi^2/ndf", 100, 0, 100);
        h1_chi2overndf_noMatchedHBTracks2.setTitleX("#Chi^2/ndf");
        h1_chi2overndf_noMatchedHBTracks2.setTitleY("Counts");
        h1_chi2overndf_noMatchedHBTracks2.setLineColor(2);
        histoData2Group.addDataSet(h1_chi2overndf_noMatchedHBTracks2, 1);
        H1F h1_z_noMatchedHBTracks2 = new H1F("z_noMatchedHBTracks2", "z", 100, -50, 50);
        h1_z_noMatchedHBTracks2.setTitleX("z (cm)");
        h1_z_noMatchedHBTracks2.setTitleY("Counts");
        h1_z_noMatchedHBTracks2.setLineColor(2);
        histoData2Group.addDataSet(h1_z_noMatchedHBTracks2, 2);
        H2F h2_xy_noMatchedHBTracks2 = new H2F("xy_noMatchedHBTracks2", "xy for no matched HB tracks", 100, -50, 50, 100, -50, 50);
        h2_xy_noMatchedHBTracks2.setTitleX("x (cm)");
        h2_xy_noMatchedHBTracks2.setTitleY("y (cm)");
        histoData2Group.addDataSet(h2_xy_noMatchedHBTracks2, 7);
        H1F h1_p_noMatchedHBTracks2 = new H1F("p_noMatchedHBTracks2", "p", 100, 0, 12);
        h1_p_noMatchedHBTracks2.setTitleX("p (GeV)");
        h1_p_noMatchedHBTracks2.setTitleY("Counts");
        h1_p_noMatchedHBTracks2.setLineColor(2);
        histoData2Group.addDataSet(h1_p_noMatchedHBTracks2, 4);
        H1F h1_theta_noMatchedHBTracks2 = new H1F("theta_noMatchedHBTracks2", "#theta", 100, 0, Math.PI/2.);
        h1_theta_noMatchedHBTracks2.setTitleX("#theta (rad)");
        h1_theta_noMatchedHBTracks2.setTitleY("Counts");
        h1_theta_noMatchedHBTracks2.setLineColor(2);
        histoData2Group.addDataSet(h1_theta_noMatchedHBTracks2, 5);
        H1F h1_phi_noMatchedHBTracks2 = new H1F("phi_noMatchedHBTracks2", "#phi", 100, -Math.PI, Math.PI);
        h1_phi_noMatchedHBTracks2.setTitleX("#phi (rad)");
        h1_phi_noMatchedHBTracks2.setTitleY("Counts");
        h1_phi_noMatchedHBTracks2.setLineColor(2);
        histoData2Group.addDataSet(h1_phi_noMatchedHBTracks2, 6);
        histoGroupMap.put(histoData2Group.getName(), histoData2Group);        
    }
             
    public void processEvent(Event event1, Event event2, int trkType){        
        List<Track> tracksHB1 = reader.readTracks(event1, trkType-1);  
        List<Track> tracksTB1 = reader.readTracks(event1, trkType);
        List<Track> tracksHB2 = reader.readTracks(event2, trkType-1);  
        List<Track> tracksTB2 = reader.readTracks(event2, trkType); 
        
        HistoGroup histoData1Group = histoGroupMap.get("data1");
        HistoGroup histoData2Group = histoGroupMap.get("data2");
        
        for(Track trk : tracksHB1){
            histoData1Group.getH1F("HB1").fill(trk.SL());  
        }  
        
        for(Track trk : tracksTB1){
            histoData1Group.getH1F("TB1").fill(trk.SL());  
        } 
        
        for(Track trk : tracksHB2){
            histoData2Group.getH1F("HB2").fill(trk.SL());  
        }  
        
        for(Track trk : tracksTB2){
            histoData2Group.getH1F("TB2").fill(trk.SL());  
        }
        
        for(Track trkHB : tracksHB1){
           boolean flag = false;
           for(Track trkTB : tracksTB1){ 
               int nMatchedClusters = trkHB.matchedClusters(trkTB);
               if(nMatchedClusters == 6 || nMatchedClusters == 5){
                   flag = true;
                   break;
               }
           }
           if(flag) {
               histoData1Group.getH1F("chi2overndf_matchedHBTracks1").fill(trkHB.chi2()/trkHB.NDF());
               histoData1Group.getH1F("z_matchedHBTracks1").fill(trkHB.vz());
               histoData1Group.getH2F("xy_matchedHBTracks1").fill(trkHB.vx(), trkHB.vy());
               histoData1Group.getH1F("p_matchedHBTracks1").fill(trkHB.p());
               histoData1Group.getH1F("theta_matchedHBTracks1").fill(trkHB.theta());
               histoData1Group.getH1F("phi_matchedHBTracks1").fill(trkHB.phi());
           }
           else {
               histoData1Group.getH1F("chi2overndf_noMatchedHBTracks1").fill(trkHB.chi2()/trkHB.NDF());
               histoData1Group.getH1F("z_noMatchedHBTracks1").fill(trkHB.vz());
               histoData1Group.getH2F("xy_noMatchedHBTracks1").fill(trkHB.vx(), trkHB.vy());
               histoData1Group.getH1F("p_noMatchedHBTracks1").fill(trkHB.p());
               histoData1Group.getH1F("theta_noMatchedHBTracks1").fill(trkHB.theta());
               histoData1Group.getH1F("phi_noMatchedHBTracks1").fill(trkHB.phi());
           }
        }
        
        for(Track trkHB : tracksHB2){
           boolean flag = false;
           for(Track trkTB : tracksTB2){ 
               int nMatchedClusters = trkHB.matchedClusters(trkTB);
               if(nMatchedClusters == 6 || nMatchedClusters == 5){
                   flag = true;
                   break;
               }
           }
           if(flag) {
               histoData2Group.getH1F("chi2overndf_matchedHBTracks2").fill(trkHB.chi2()/trkHB.NDF());
               histoData2Group.getH1F("z_matchedHBTracks2").fill(trkHB.vz());
               histoData2Group.getH2F("xy_matchedHBTracks2").fill(trkHB.vx(), trkHB.vy());
               histoData2Group.getH1F("p_matchedHBTracks2").fill(trkHB.p());
               histoData2Group.getH1F("theta_matchedHBTracks2").fill(trkHB.theta());
               histoData2Group.getH1F("phi_matchedHBTracks2").fill(trkHB.phi());
           }
           else {
               histoData2Group.getH1F("chi2overndf_noMatchedHBTracks2").fill(trkHB.chi2()/trkHB.NDF());
               histoData2Group.getH1F("z_noMatchedHBTracks2").fill(trkHB.vz());
               histoData2Group.getH2F("xy_noMatchedHBTracks2").fill(trkHB.vx(), trkHB.vy());
               histoData2Group.getH1F("p_noMatchedHBTracks2").fill(trkHB.p());
               histoData2Group.getH1F("theta_noMatchedHBTracks2").fill(trkHB.theta());
               histoData2Group.getH1F("phi_noMatchedHBTracks2").fill(trkHB.phi());
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
            inputList.add("/Users/caot/research/clas12/data/mc/uRWELL/upgradeTrackingWithuRWELL/rga-sidis-uRWell-2R_denoise/0nA/reconBg/0000.hipo");
            inputList.add("/Users/caot/research/clas12/data/mc/uRWELL/upgradeTrackingWithuRWELL/rga-sidis-uRWell-2R_denoise/50nA/reconBg/0000.hipo");
            maxEvents = 1000;
            //System.out.println("\n >>>> error: no input file is specified....\n");
            //System.exit(0);
        }

        String histoName   = "histo.hipo"; 
        if(!namePrefix.isEmpty()) {
            histoName  = namePrefix + "_" + histoName;
        }
        
        CompareTracks analysis = new CompareTracks();
        analysis.createHistoGroupMap();

        if(!readHistos) {                 
            HipoReader reader1 = new HipoReader();
            reader1.open(inputList.get(0));        
            HipoReader reader2 = new HipoReader();
            reader2.open(inputList.get(1));


            SchemaFactory schema = reader1.getSchemaFactory();
            analysis.initReader(new Banks(schema));

            int counter=0;
            Event event1 = new Event();
            Event event2 = new Event();
        
            ProgressPrintout progress = new ProgressPrintout();
            while (reader1.hasNext() && reader2.hasNext()) {

                counter++;

                reader1.nextEvent(event1);
                reader2.nextEvent(event2);
                
                analysis.processEvent(event1, event2, trkType);

                progress.updateStatus();
                if(maxEvents>0){
                    if(counter>=maxEvents) break;
                }                    
            }

            progress.showStatus();
            reader1.close();            
            reader2.close();
            analysis.saveHistos(histoName);
        }
        else{
            analysis.readHistos(inputList.get(0)); 
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
