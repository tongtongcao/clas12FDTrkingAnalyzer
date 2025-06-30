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
import org.jlab.groot.math.F1D;

import org.clas.utilities.Constants;
import org.clas.element.Track;
import org.clas.element.Cluster;
import org.clas.element.Hit;
import org.clas.element.URWellHit;
import org.clas.element.URWellCluster;
import org.clas.element.URWellCross;
import org.clas.element.MCParticle;
import org.clas.element.MCTrue;
import org.clas.element.MCTrueHit;
import org.clas.graph.HistoGroup;
import org.clas.physicsEvent.BasePhysicsEvent;
import org.clas.physicsEvent.EventForLumiScan;
import org.clas.analysis.BaseAnalysis;
import org.clas.reader.Banks;
import org.jlab.groot.fitter.DataFitter;

/**
 *
 * @author Tongtong Cao
 */
public class SingleParticleMC extends BaseAnalysis{
    private int numCorrectPIDElectrons = 0;
    private int numMisPIDPiminusElectrons = 0;
    private static int goodTracks = 0;
    
    public SingleParticleMC(){}

    public SingleParticleMC(Banks banks){
        super(banks);
    }
    
    @Override
    public void createHistoGroupMap(){ 
        HistoGroup histoGroupMCTrue = new HistoGroup("MCTrue", 3, 2);
        H1F h1_numDCHitsMCTrue = new H1F("numDCHitsMCTrue", "# of DC hits", 100, 0, 80);
        h1_numDCHitsMCTrue.setTitleX("# of DC hits");
        h1_numDCHitsMCTrue.setTitleY("Counts");
        H1F h1_numURWellHitsMCTrue = new H1F("numURWellHitsMCTrue", "# of uRWell hits", 100, 0, 40);
        h1_numURWellHitsMCTrue.setTitleX("# of uRWell hits");
        h1_numURWellHitsMCTrue.setTitleY("Counts");        
        H2F h2_numDCHitsVsnumURWellHitsMCTrue = new H2F("numDCHitsVsnumURWellHitsMCTrue", "# of DC hits vs # of uRWell hits", 100, 0, 40, 100, 0, 80);
        h2_numDCHitsVsnumURWellHitsMCTrue.setTitleX("# of uRWell hits");
        h2_numDCHitsVsnumURWellHitsMCTrue.setTitleY("# of DC hits");
        histoGroupMCTrue.addDataSet(h1_numDCHitsMCTrue, 0);
        histoGroupMCTrue.addDataSet(h1_numURWellHitsMCTrue, 1);
        histoGroupMCTrue.addDataSet(h2_numDCHitsVsnumURWellHitsMCTrue, 2);
        histoGroupMap.put(histoGroupMCTrue.getName(), histoGroupMCTrue);  
        
        
        HistoGroup histoGroupMCParticle = new HistoGroup("MCParticle", 3, 2);
        H1F h1_pMCParticle = new H1F("pMCParticle", "p", 100, 0, 11);
        h1_pMCParticle.setTitleX("p (GeV/c)");
        h1_pMCParticle.setTitleY("Counts");
        H1F h1_thetaMCParticle = new H1F("thetaMCParticle", "#theta", 100, 0, 35);
        h1_thetaMCParticle.setTitleX("#theta (deg)");
        h1_thetaMCParticle.setTitleY("Counts");
        H1F h1_phiMCParticle = new H1F("phiMCParticle", "#phi", 100, -180, 180);
        h1_phiMCParticle.setTitleX("#phi (deg)");
        h1_phiMCParticle.setTitleY("Counts");
        H1F h1_vxMCParticle = new H1F("vxMCParticle", "vx", 100, -5, 5);
        h1_vxMCParticle.setTitleX("vx (cm)");
        h1_vxMCParticle.setTitleY("Counts");     
        H1F h1_vyMCParticle = new H1F("vyMCParticle", "vy", 100, -5, 5);
        h1_vyMCParticle.setTitleX("vy (cm)");
        h1_vyMCParticle.setTitleY("Counts");                
        H1F h1_vzMCParticle = new H1F("vzMCParticle", "vz", 100, -15, 10);
        h1_vzMCParticle.setTitleX("vz (cm)");
        h1_vzMCParticle.setTitleY("Counts");
        histoGroupMCParticle.addDataSet(h1_pMCParticle, 0);
        histoGroupMCParticle.addDataSet(h1_thetaMCParticle, 1);
        histoGroupMCParticle.addDataSet(h1_phiMCParticle, 2);
        histoGroupMCParticle.addDataSet(h1_vxMCParticle, 3);
        histoGroupMCParticle.addDataSet(h1_vyMCParticle, 4);
        histoGroupMCParticle.addDataSet(h1_vzMCParticle, 5);        
        histoGroupMap.put(histoGroupMCParticle.getName(), histoGroupMCParticle);  
        
        HistoGroup histoGroupMapping = new HistoGroup("Mapping", 2, 1);
        H2F h2_trkMCPartMap = new H2F("trkMCPartMap", "trkMCPartMap", 100, 0, 1, 100, 0, 10);
        h2_trkMCPartMap.setTitleX("dist. for mom. (GeV)");
        h2_trkMCPartMap.setTitleY("dist. for vtx (cm)");
        histoGroupMapping.addDataSet(h2_trkMCPartMap, 0);          
        H2F h2_PIDComp = new H2F("PIDComp", "PIDComp", 100, -2.5, 2.5, 100, -2.5, 2.5);
        h2_PIDComp.setTitleX("pid/100 for MC partilce");
        h2_PIDComp.setTitleY("pid/100 for track");
        histoGroupMapping.addDataSet(h2_PIDComp, 1);         
        histoGroupMap.put(histoGroupMapping.getName(), histoGroupMapping);
        
        
        HistoGroup histoDiffGroup = new HistoGroup("Diff", 3, 2);
        H1F h1_p_diff = new H1F("pDiff", "#Deltap/p", 100, -1, 1);
        h1_p_diff.setTitleX("#Deltap/p");
        h1_p_diff.setTitleY("Counts");
        H1F h1_theta_diff = new H1F("thetaDiff", "#Delta#theta", 100, -20, 20);
        h1_theta_diff.setTitleX("#Delta#theta (deg)");
        h1_theta_diff.setTitleY("Counts");
        H1F h1_phi_diff = new H1F("phiDiff", "#Delta#phi", 100, -30, 30);
        h1_phi_diff.setTitleX("#Delta#phi (deg)");
        h1_phi_diff.setTitleY("Counts");
        H1F h1_vx_diff = new H1F("vxDiff", "#DeltaV_x", 100, -40, 40);
        h1_vx_diff.setTitleX("#DeltaV_x (cm)");
        h1_vx_diff.setTitleY("Counts");     
        H1F h1_vy_diff = new H1F("vyDiff", "#DeltaV_y", 100, -40, 40);
        h1_vy_diff.setTitleX("#DeltaV_y (cm)");
        h1_vy_diff.setTitleY("Counts");                
        H1F h1_vz_diff = new H1F("vzDiff", "#DeltaV_z", 100, -100, 100);
        h1_vz_diff.setTitleX("#DeltaV_z (cm)");
        h1_vz_diff.setTitleY("Counts");        
        histoDiffGroup.addDataSet(h1_p_diff, 0);
        histoDiffGroup.addDataSet(h1_theta_diff, 1);
        histoDiffGroup.addDataSet(h1_phi_diff, 2);
        histoDiffGroup.addDataSet(h1_vx_diff, 3);
        histoDiffGroup.addDataSet(h1_vy_diff, 4);
        histoDiffGroup.addDataSet(h1_vz_diff, 5);        
        histoGroupMap.put(histoDiffGroup.getName(), histoDiffGroup);        
    }
             
    public void processEvent(Event event, int trkType){
        MCTrue mcTrue = reader.readMCTrue(event);
        
        List<MCParticle> mcParts = reader.readMCParticles(event);
        List<Track> tracks = reader.readTracks(event, trkType);         
        
        HistoGroup histoGroupMCTrue = histoGroupMap.get("MCTrue");
        int numDCHits = mcTrue.getHitsDC().size();
        int numURWellHits = mcTrue.getHitsURWell().size();
        histoGroupMCTrue.getH1F("numDCHitsMCTrue").fill(numDCHits);
        histoGroupMCTrue.getH1F("numURWellHitsMCTrue").fill(numURWellHits);
        histoGroupMCTrue.getH2F("numDCHitsVsnumURWellHitsMCTrue").fill(numURWellHits, numDCHits);
        if(numURWellHits > 2 && numDCHits > 20) goodTracks++;
                
        HistoGroup histoGroupMCParticle = histoGroupMap.get("MCParticle");
        for(MCParticle mcPart : mcParts){
            histoGroupMCParticle.getH1F("pMCParticle").fill(mcPart.mom().mag());
            histoGroupMCParticle.getH1F("thetaMCParticle").fill(mcPart.mom().theta()/Math.PI*180.);
            histoGroupMCParticle.getH1F("phiMCParticle").fill(mcPart.mom().phi()/Math.PI*180.);
            histoGroupMCParticle.getH1F("vxMCParticle").fill(mcPart.vertex().x());
            histoGroupMCParticle.getH1F("vyMCParticle").fill(mcPart.vertex().y());
            histoGroupMCParticle.getH1F("vzMCParticle").fill(mcPart.vertex().z()); 
        }
        
        HistoGroup histoGroupMapping = histoGroupMap.get("Mapping");        
        Map<MCParticle, List<Track>> map_mcPart_trkList = new HashMap();
        for(MCParticle mcPart : mcParts){
            for(Track trk : tracks){                                
                //if(Math.abs(trk.chi2pid()) < 3) {
                    double distMom = mcPart.euclideanDistanceMom(trk);
                    double distVtx = mcPart.euclideanDistanceVertex(trk);
                    histoGroupMapping.getH2F("trkMCPartMap").fill(distMom, distVtx);
                    if(map_mcPart_trkList.get(mcPart) == null){
                        List<Track> trkList = new ArrayList();
                        trkList.add(trk);
                        map_mcPart_trkList.put(mcPart, trkList);
                    }
                    else {
                        map_mcPart_trkList.get(mcPart).add(trk);
                    }
                //}
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
        
        HistoGroup histoDiffGroup = histoGroupMap.get("Diff");
        for(MCParticle mcPart : map_mcPart_trk.keySet()){
            Track trk = map_mcPart_trk.get(mcPart);
            histoGroupMapping.getH2F("PIDComp").fill(mcPart.pid()/100., trk.pid()/100.);
            if(mcPart.pid() == 11 && map_mcPart_trk.get(mcPart).pid() == 11) numCorrectPIDElectrons++;
            if(mcPart.pid() == 11 && map_mcPart_trk.get(mcPart).pid() == -211) numMisPIDPiminusElectrons++;
            
            histoDiffGroup.getH1F("pDiff").fill((mcPart.mom().mag() - trk.vector().p())/mcPart.mom().mag());
            histoDiffGroup.getH1F("thetaDiff").fill((mcPart.mom().theta() - trk.vector().theta())/Math.PI*180.);
            histoDiffGroup.getH1F("phiDiff").fill((mcPart.mom().phi() - trk.vector().phi())/Math.PI*180.);
            histoDiffGroup.getH1F("vxDiff").fill(mcPart.vertex().x() - trk.vertex().x());
            histoDiffGroup.getH1F("vyDiff").fill(mcPart.vertex().y() - trk.vertex().y());
            histoDiffGroup.getH1F("vzDiff").fill(mcPart.vertex().z() - trk.vertex().z()); 
        }                
    }
    
    public void postEventProcessing(){
        /*
        HistoGroup histoDiffGroup = histoGroupMap.get("Diff");
        F1D func_p  = new F1D("func_p","[amp]*gaus(x,[mean],[sigma])", -0.015,0.01);
        func_p.setParameter(0, histoDiffGroup.getH1F("pDiff").getMax());
        func_p.setParameter(1, 0.004);
        func_p.setParameter(2, 0.003);
        func_p.setParLimits(1, -0.01, 0.01);
        func_p.setParLimits(2, -0.05, 0.05); 
        func_p.setLineColor(2);
        func_p.setOptStat(1110);
        histoDiffGroup.getH1F("pDiff").fit(func_p);
        
        F1D func_theta  = new F1D("func_theta","[amp]*gaus(x,[mean],[sigma])", -0.09, 0.12);
        func_p.setParameter(0, histoDiffGroup.getH1F("thetaDiff").getMax());
        func_theta.setParameter(1, 0.02);
        func_theta.setParameter(2, 0.02);
        func_theta.setParLimits(1, -0.05, 0.05);
        func_theta.setParLimits(2, -0.2, 0.2); 
        func_theta.setLineColor(2);
        func_theta.setOptStat(1110);
        histoDiffGroup.getH1F("thetaDiff").fit(func_theta);
        
        F1D func_phi  = new F1D("func_phi","[amp]*gaus(x,[mean],[sigma])", -0.35, 0.4);
        func_phi.setParameter(0, histoDiffGroup.getH1F("phiDiff").getMax());
        func_phi.setParameter(1, 0.04);
        func_phi.setParameter(2, 0.1);
        func_phi.setParLimits(1, -0.1, 0.1);
        func_phi.setParLimits(2, -3, 3); 
        func_phi.setLineColor(2);
        func_phi.setOptStat(1110);
        histoDiffGroup.getH1F("phiDiff").fit(func_phi);
        
        F1D func_vx  = new F1D("func_vx","[amp]*gaus(x,[mean],[sigma])", -0.15, 0.15);
        func_vx.setParameter(0, histoDiffGroup.getH1F("vxDiff").getMax());
        func_vx.setParameter(1, 0.);
        func_vx.setParameter(2, 0.1);
        //func_vx.setParLimits(1, -0.1, 0.1);
        //func_vx.setParLimits(2, -1, 1); 
        func_vx.setLineColor(2);
        func_vx.setOptStat(1110);
        histoDiffGroup.getH1F("vxDiff").fit(func_vx);
        
        F1D func_vy  = new F1D("func_vy","[amp]*gaus(x,[mean],[sigma])", -0.15, 0.15);
        func_vy.setParameter(0, histoDiffGroup.getH1F("vyDiff").getMax());
        func_vy.setParameter(1, 0.);
        func_vy.setParameter(2, 0.1);
        //func_vy.setParLimits(1, -0.1, 0.1);
        //func_vy.setParLimits(2, -1, 1); 
        func_vy.setLineColor(2);
        func_vy.setOptStat(1110);
        histoDiffGroup.getH1F("vyDiff").fit(func_vy);
        
        
        F1D func_vz  = new F1D("func_vz","[amp]*gaus(x,[mean],[sigma])", -1., 1.);
        func_vz.setParameter(0, histoDiffGroup.getH1F("vzDiff").getMax());
        func_vz.setParameter(1, 0.01);
        func_vz.setParameter(2, 0.2);
        //func_vz.setParLimits(1, -0.1, 0.1);
        //func_vz.setParLimits(2, -3, 3); 
        func_vz.setLineColor(2);
        func_vz.setOptStat(1110);
        histoDiffGroup.getH1F("vzDiff").fit(func_vz);
        
        System.out.println("Good tracks: " + Integer.toString(SingleParticleMC.goodTracks));
        System.out.println("# of electron tracks with mis-identified as pi-: " + numMisPIDPiminusElectrons);
        System.out.println("# of electron tracks with correct PID: " + numCorrectPIDElectrons);
        System.out.println("ratio: " + ((double)numMisPIDPiminusElectrons/numCorrectPIDElectrons));
*/
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
        
        SingleParticleMC analysis = new SingleParticleMC();
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
            frame.setSize(1500, 750);
            frame.add(canvas);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        }
    }
    
}
