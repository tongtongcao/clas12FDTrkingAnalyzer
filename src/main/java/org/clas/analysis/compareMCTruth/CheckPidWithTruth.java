package org.clas.analysis.compareMCTruth;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
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
import org.clas.element.MCParticle;
import org.clas.graph.HistoGroup;
import org.clas.physicsEvent.BasePhysicsEvent;
import org.clas.physicsEvent.EventForLumiScan;
import org.clas.analysis.BaseAnalysis;
import org.clas.reader.Banks;

/**
 *
 * @author Tongtong Cao
 */
public class CheckPidWithTruth extends BaseAnalysis{
    private int numCorrectPIDElectrons = 0;
    private int numMisPIDPiminusElectrons = 0;
    
    
    public CheckPidWithTruth(){}

    public CheckPidWithTruth(Banks banks){
        super(banks);
    }
    
    @Override
    public void createHistoGroupMap(){        
        HistoGroup histoTrkMCPartMapGroup = new HistoGroup("trkMCPartMap", 1, 1);
        H2F h2_trkMCPartMap = new H2F("trkMCPartMap", "trkMCPartMap", 100, 0, 1, 100, 0, 10);
        h2_trkMCPartMap.setTitleX("dist. for mom. (GeV)");
        h2_trkMCPartMap.setTitleY("dist. for vtx (cm)");
        histoTrkMCPartMapGroup.addDataSet(h2_trkMCPartMap, 0);                                                               
        histoGroupMap.put(histoTrkMCPartMapGroup.getName(), histoTrkMCPartMapGroup);
        
        HistoGroup histoPIDCompGroup = new HistoGroup("PIDComp", 1, 1);
        H2F h2_PIDComp = new H2F("PIDComp", "PIDComp", 100, -25, 25, 100, -25, 25);
        h2_PIDComp.setTitleX("pid/100 for MC partilce");
        h2_PIDComp.setTitleY("pid/100 for track");
        histoPIDCompGroup.addDataSet(h2_PIDComp, 0);                                                               
        histoGroupMap.put(histoPIDCompGroup.getName(), histoPIDCompGroup);
        
        HistoGroup histoMisPIDPiminusElectronsGroup = new HistoGroup("misPIDPiminusElectrons", 3, 2);
        H1F h1_p_diff = new H1F("pDiff", "Diff. of p", 100, -0.2, 0.2);
        h1_p_diff.setTitleX("Diff. of p (GeV/c)");
        h1_p_diff.setTitleY("Counts");
        H1F h1_theta_diff = new H1F("thetaDiff", "Diff. of #theta", 100, -0.02, 0.02);
        h1_theta_diff.setTitleX("Diff. of #theta (rad)");
        h1_theta_diff.setTitleY("Counts");
        H1F h1_phi_diff = new H1F("phiDiff", "Diff. of #phi", 100, -0.2, 0.2);
        h1_phi_diff.setTitleX("Diff. of #phi (rad)");
        h1_phi_diff.setTitleY("Counts");
        H1F h1_vx_diff = new H1F("vxDiff", "Diff. of vx", 100, -2, 2);
        h1_vx_diff.setTitleX("Diff. of vx (cm)");
        h1_vx_diff.setTitleY("Counts");     
        H1F h1_vy_diff = new H1F("vyDiff", "Diff. of vy", 100, -2, 2);
        h1_vy_diff.setTitleX("Diff. of vy (cm)");
        h1_vy_diff.setTitleY("Counts");                
        H1F h1_vz_diff = new H1F("vzDiff", "Diff. of vz", 100, -4, 4);
        h1_vz_diff.setTitleX("Diff. of vz (cm)");
        h1_vz_diff.setTitleY("Counts");
        
        histoMisPIDPiminusElectronsGroup.addDataSet(h1_p_diff, 0);
        histoMisPIDPiminusElectronsGroup.addDataSet(h1_theta_diff, 1);
        histoMisPIDPiminusElectronsGroup.addDataSet(h1_phi_diff, 2);
        histoMisPIDPiminusElectronsGroup.addDataSet(h1_vx_diff, 3);
        histoMisPIDPiminusElectronsGroup.addDataSet(h1_vy_diff, 4);
        histoMisPIDPiminusElectronsGroup.addDataSet(h1_vz_diff, 5);
        
        histoGroupMap.put(histoMisPIDPiminusElectronsGroup.getName(), histoMisPIDPiminusElectronsGroup);        
    }
             
    public void processEvent(Event event, int trkType){
        List<MCParticle> mcParts = reader.readMCParticles(event);
        List<Track> tracks = reader.readTracks(event, trkType); 
        
        HistoGroup histoTrkMCPartMapGroup = histoGroupMap.get("trkMCPartMap");
        Map<MCParticle, List<Track>> map_mcPart_trkList = new HashMap();
        for(MCParticle mcPart : mcParts){
            for(Track trk : tracks){
                double distMom = mcPart.euclideanDistanceMom(trk);
                double distVtx = mcPart.euclideanDistanceVertex(trk);
                histoTrkMCPartMapGroup.getH2F("trkMCPartMap").fill(distMom, distVtx);
                if(distMom < 0.015 && distVtx < 1.5){
                    if(map_mcPart_trkList.get(mcPart) == null){
                        List<Track> trkList = new ArrayList();
                        trkList.add(trk);
                        map_mcPart_trkList.put(mcPart, trkList);
                    }
                    else {
                        map_mcPart_trkList.get(mcPart).add(trk);
                    }
                }
            }
        }
        
        Map<MCParticle, Track> map_mcPart_trk = new HashMap();
        for(MCParticle mcPart : map_mcPart_trkList.keySet()){
            double minDistMom = 9999;
            Track cloestTrk = null;
            for(Track trk : map_mcPart_trkList.get(mcPart)){
                double distMom = mcPart.euclideanDistanceMom(trk);
                if(distMom < minDistMom){
                    minDistMom = distMom;
                    cloestTrk = trk;
                }
            }
            map_mcPart_trk.put(mcPart, cloestTrk);
        }
        
        HistoGroup histoPIDCompGroup = histoGroupMap.get("PIDComp");
        HistoGroup histoMisPIDPiminusElectronsGroup = histoGroupMap.get("misPIDPiminusElectrons");
        for(MCParticle mcPart : map_mcPart_trk.keySet()){
            histoPIDCompGroup.getH2F("PIDComp").fill(mcPart.pid()/100., map_mcPart_trk.get(mcPart).pid()/100.);
            
            if(mcPart.pid() == 11 && map_mcPart_trk.get(mcPart).pid() == 11) numCorrectPIDElectrons++;
            if(mcPart.pid() == 11 && map_mcPart_trk.get(mcPart).pid() == -211) {
                numMisPIDPiminusElectrons++;
                
                histoMisPIDPiminusElectronsGroup.getH1F("pDiff").fill(mcPart.mom().mag() - map_mcPart_trk.get(mcPart).vector().p());
                histoMisPIDPiminusElectronsGroup.getH1F("thetaDiff").fill(mcPart.mom().theta() - map_mcPart_trk.get(mcPart).vector().theta());
                histoMisPIDPiminusElectronsGroup.getH1F("phiDiff").fill(mcPart.mom().phi() - map_mcPart_trk.get(mcPart).vector().phi());
                histoMisPIDPiminusElectronsGroup.getH1F("vxDiff").fill(mcPart.vertex().x() - map_mcPart_trk.get(mcPart).vertex().x());
                histoMisPIDPiminusElectronsGroup.getH1F("vyDiff").fill(mcPart.vertex().y() - map_mcPart_trk.get(mcPart).vertex().y());
                histoMisPIDPiminusElectronsGroup.getH1F("vzDiff").fill(mcPart.vertex().z() - map_mcPart_trk.get(mcPart).vertex().z());
            }
        }                
    }
    
    public void postEventProcessing(){
        System.out.println("# of electron tracks with mis-identified as pi-: " + numMisPIDPiminusElectrons);
        System.out.println("# of electron tracks with correct PID: " + numCorrectPIDElectrons);
        System.out.println("ratio: " + ((double)numMisPIDPiminusElectrons/numCorrectPIDElectrons));
    }
                        
    public static void main(String[] args){
        OptionParser parser = new OptionParser("studyBgEffectsDC");
        parser.setRequiresInputList(false);
        // valid options for event-base analysis
        parser.addOption("-o"          ,"",     "output file name prefix");
        parser.addOption("-n"          ,"-1",   "maximum number of events to process");
        parser.addOption("-energy"     ,"10.6", "beam energy");
        parser.addOption("-target"     ,"2212", "target PDG");
        parser.addOption("-trkType"      ,"22",   "tracking type: ConvHB(11) or ConvTB(12) or AIHB(21) or AITB(22)");
        
        parser.addOption("-plot"       ,"1",    "display histograms (0/1)");
        
        // histogram based analysis
        parser.addOption("-histo"      ,"0",    "read histogram file (0/1)");
        
        parser.parse(args);
        
        String namePrefix  = parser.getOption("-o").stringValue(); 
        int   maxEvents  = parser.getOption("-n").intValue();
        Constants.BEAMENERGY = parser.getOption("-energy").doubleValue();
        Constants.TARGETPID  = parser.getOption("-target").intValue();  
        int trkType = parser.getOption("-trkType").intValue();       
        boolean openWindow   = (parser.getOption("-plot").intValue()!=0);
        boolean readHistos   = (parser.getOption("-histo").intValue()!=0);   
        
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
        
        CheckPidWithTruth analysis = new CheckPidWithTruth();
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
            
            analysis.postEventProcessing();
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
            frame.setSize(1000, 1000);
            frame.add(canvas);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        }
    }
    
}
