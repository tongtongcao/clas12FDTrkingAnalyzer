package org.clas.analysis.studyClustering;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Random;
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
import org.clas.demo.DemoSector;
import org.clas.element.RunConfig;
import org.clas.element.TDC;
import org.clas.reader.Banks;
import org.clas.reader.LocalEvent;
import org.clas.fit.ClusterFitLC;
import org.jlab.groot.data.GraphErrors;
import org.jlab.groot.group.DataGroup;
import org.clas.demo.DemoBase;

/**
 *
 * @author Tongtong Cao
 */
public class StudyClustering extends BaseAnalysis{ 
    private double probCut = 0.55;
    
    public StudyClustering(){}
    
    @Override
    public void createHistoGroupMap(){                
        HistoGroup histoGroupFitProbClusters = new HistoGroup("fitProbClusters", 1, 1);
        H1F h1_fitProbClusters = new H1F("fitProbClusters", "fitProbClusters", 30, 0, 1.01);
        h1_fitProbClusters.setTitleX("prob");
        h1_fitProbClusters.setTitleY("Counts");
        h1_fitProbClusters.setLineColor(1);
        histoGroupFitProbClusters.addDataSet(h1_fitProbClusters, 0); 
        histoGroupMap.put(histoGroupFitProbClusters.getName(), histoGroupFitProbClusters);
        
        HistoGroup histoGroupFitProbVSNLayersClusters = new HistoGroup("fitProbVSNLayersClusters", 1, 1);
        H2F h2_fitProbVSNLayersClusters = new H2F("fitProbVSNLayersClusters", "fitProbVSNLayersClusters", 6,0.5, 6.5, 100, 0, 1.01);
        h2_fitProbVSNLayersClusters.setTitleX("prob");
        h2_fitProbVSNLayersClusters.setTitleY("Counts");
        histoGroupFitProbVSNLayersClusters.addDataSet(h2_fitProbVSNLayersClusters, 0); 
        histoGroupMap.put(histoGroupFitProbVSNLayersClusters.getName(), histoGroupFitProbVSNLayersClusters);
        
        HistoGroup histoGroupFitProb = new HistoGroup("fitProb", 2, 3);
        H1F h1_fitProb2Layers = new H1F("fitProb2Layers", "fitProb2Layers", 100, 0, 1.01);
        h1_fitProb2Layers.setTitleX("prob");
        h1_fitProb2Layers.setTitleY("Counts");
        h1_fitProb2Layers.setLineColor(1);
        histoGroupFitProb.addDataSet(h1_fitProb2Layers, 0);   
        H1F h1_fitProb3Layers = new H1F("fitProb3Layers", "fitProb3Layers", 100, 0, 1.01);
        h1_fitProb3Layers.setTitleX("prob");
        h1_fitProb3Layers.setTitleY("Counts");
        h1_fitProb3Layers.setLineColor(1);
        histoGroupFitProb.addDataSet(h1_fitProb3Layers, 1);          
        H1F h1_fitProb4Layers = new H1F("fitProb4Layers", "fitProb4Layers", 100, 0, 1.01);
        h1_fitProb4Layers.setTitleX("prob");
        h1_fitProb4Layers.setTitleY("Counts");
        h1_fitProb4Layers.setLineColor(1);
        histoGroupFitProb.addDataSet(h1_fitProb4Layers, 2);          
        H1F h1_fitProb5Layers = new H1F("fitProb5Layers", "fitProb5Layers", 100, 0, 1.01);
        h1_fitProb5Layers.setTitleX("prob");
        h1_fitProb5Layers.setTitleY("Counts");
        h1_fitProb5Layers.setLineColor(1);
        histoGroupFitProb.addDataSet(h1_fitProb5Layers, 3);          
        H1F h1_fitProb6Layers = new H1F("fitProb6Layers", "fitProb6Layers", 100, 0, 1.01);
        h1_fitProb6Layers.setTitleX("prob");
        h1_fitProb6Layers.setTitleY("Counts");
        h1_fitProb6Layers.setLineColor(1);
        histoGroupFitProb.addDataSet(h1_fitProb6Layers, 4);         
        histoGroupMap.put(histoGroupFitProb.getName(), histoGroupFitProb); 
        
        HistoGroup histoGroupFitProbVsSize = new HistoGroup("fitProbVsSize", 2, 3);
        H2F h2_fitProbVsSize2Layers = new H2F("fitProbVsSize2Layers", "fitProbVsSize2Layers", 12, 0.5, 12.5, 100, 0, 1.01);
        h2_fitProbVsSize2Layers.setTitleX("size");
        h2_fitProbVsSize2Layers.setTitleY("prob");
        histoGroupFitProbVsSize.addDataSet(h2_fitProbVsSize2Layers, 0);           
        H2F h2_fitProbVsSize3Layers = new H2F("fitProbVsSize3Layers", "fitProbVsSize3Layers", 12, 0.5, 12.5, 100, 0, 1.01);
        h2_fitProbVsSize3Layers.setTitleX("size");
        h2_fitProbVsSize3Layers.setTitleY("prob");
        histoGroupFitProbVsSize.addDataSet(h2_fitProbVsSize3Layers, 1);           
        H2F h2_fitProbVsSize4Layers = new H2F("fitProbVsSize4Layers", "fitProbVsSize4Layers", 12, 0.5, 12.5, 100, 0, 1.01);
        h2_fitProbVsSize4Layers.setTitleX("size");
        h2_fitProbVsSize4Layers.setTitleY("prob");
        histoGroupFitProbVsSize.addDataSet(h2_fitProbVsSize4Layers, 2);           
        H2F h2_fitProbVsSize5Layers = new H2F("fitProbVsSize5Layers", "fitProbVsSize5Layers", 12, 0.5, 12.5, 100, 0, 1.01);
        h2_fitProbVsSize5Layers.setTitleX("size");
        h2_fitProbVsSize5Layers.setTitleY("prob");
        histoGroupFitProbVsSize.addDataSet(h2_fitProbVsSize5Layers, 3);           
        H2F h2_fitProbVsSize6Layers = new H2F("fitProbVsSize6Layers", "fitProbVsSize6Layers", 12, 0.5, 12.5, 100, 0, 1.01);
        h2_fitProbVsSize6Layers.setTitleX("size");
        h2_fitProbVsSize6Layers.setTitleY("prob");
        histoGroupFitProbVsSize.addDataSet(h2_fitProbVsSize6Layers, 4);           
        histoGroupMap.put(histoGroupFitProbVsSize.getName(), histoGroupFitProbVsSize);  
        
        HistoGroup histoGroupResidual = new HistoGroup("residual", 3, 2);
        for (int i = 0; i < 6; i++) {
            H1F h1_residual = new H1F("residual for SL" + Integer.toString(i + 1),
                    "residual for SL" + Integer.toString(i + 1), 60, -2, 2);
            h1_residual.setTitleX("if matched hits exist");
            h1_residual.setTitleY("Counts");
            h1_residual.setOptStat(1100);
            histoGroupResidual.addDataSet(h1_residual, i);  
            
        }
        histoGroupMap.put(histoGroupResidual.getName(), histoGroupResidual);
        
        
        
        HistoGroup histoGroupAvgResidual = new HistoGroup("avgResidual", 2, 2);
        H1F h1_avgResidual = new H1F("avgResidual", "avgResidual", 100, -0.01, 0.01);
        h1_avgResidual.setTitleX("avg. residual");
        h1_avgResidual.setTitleY("Counts");
        histoGroupAvgResidual.addDataSet(h1_avgResidual, 0); 
        
        H1F h1_avgAbsResidual = new H1F("avgAbsResidual", "avgAbsResidual", 100, 0, 1);
        h1_avgAbsResidual.setTitleX("avg. abs. residual");
        h1_avgAbsResidual.setTitleY("Counts");
        histoGroupAvgResidual.addDataSet(h1_avgAbsResidual, 1); 
        
        H2F h2_avgAbsResidualVsNLayers = new H2F("avgAbsResidualVsNLayers", "avgAbsResidual vs # of layers", 6, 0.5, 6.5, 100, 0, 1);
        h2_avgAbsResidualVsNLayers.setTitleX("# of layers");
        h2_avgAbsResidualVsNLayers.setTitleY("avg. abs. residual");
        histoGroupAvgResidual.addDataSet(h2_avgAbsResidualVsNLayers, 3); 
        
        histoGroupMap.put(histoGroupAvgResidual.getName(), histoGroupAvgResidual);
        
                
    }
             
    public void processEvent(Event event){        
        //Read banks
        LocalEvent localEvent = new LocalEvent(reader, event, Constants.AITB, true);        
        List<Cluster> validClusters = new ArrayList();
        for(Track trk : localEvent.getTracksTB()){
            if(trk.isValid()) validClusters.addAll(trk.getClusters());
        }
        
        HistoGroup histoGroupFitProbClusters = histoGroupMap.get("fitProbClusters"); 
        HistoGroup histoGroupFitProbVSNLayersClusters = histoGroupMap.get("fitProbVSNLayersClusters");
        HistoGroup histoGroupAvgResidual = histoGroupMap.get("avgResidual");
        for(Cluster cls : validClusters){
            ClusterFitLC fitClus = new ClusterFitLC(cls);
            if(fitClus.lineFit()){
                histoGroupFitProbClusters.getH1F("fitProbClusters").fill(fitClus.getLineFitter().getProb());
                histoGroupFitProbVSNLayersClusters.getH2F("fitProbVSNLayersClusters").fill(cls.numLayers(cls.getHits()), fitClus.getLineFitter().getProb());
                cls.setLinearFitParameters(fitClus.getLineFitter());
                histoGroupAvgResidual.getH1F("avgResidual").fill(cls.getAvgResidual());
                histoGroupAvgResidual.getH1F("avgAbsResidual").fill(cls.getAvgAbsResidual());
                histoGroupAvgResidual.getH2F("avgAbsResidualVsNLayers").fill(cls.numLayers(cls.getHits()), cls.getAvgAbsResidual());
            }
        }
                                                       
        HistoGroup histoGroupFitProb = histoGroupMap.get("fitProb");  
        HistoGroup histoGroupFitProbVsSize = histoGroupMap.get("fitProbVsSize");  
        Random random = new Random(); 
        for(Cluster cls : validClusters){
            ClusterFitLC fitClus = new ClusterFitLC(cls);
            if(fitClus.lineFit()){                            
                List<Integer> indexList = new ArrayList();
                List<Hit> randomhits = new ArrayList();                
                if(cls.numLayers(cls.getHits()) >= 2){                    
                    while(cls.numLayers(randomhits) < 2){                
                        int ranNum = random.nextInt(cls.getHits().size());
                        if(!indexList.contains(ranNum)) {
                            indexList.add(ranNum);
                            randomhits.add(cls.getHits().get(ranNum));
                        }

                    }

                    ClusterFitLC fit = new ClusterFitLC(randomhits);
                    if(fit.lineFit()){
                        histoGroupFitProb.getH1F("fitProb2Layers").fill(fit.getLineFitter().getProb());
                        histoGroupFitProbVsSize.getH2F("fitProbVsSize2Layers").fill(randomhits.size(), fit.getLineFitter().getProb());

                        if(fit.getLineFitter().getProb() < probCut){
                            addDemoGroup(cls, fitClus.getLineFitter().getProb(), randomhits, fit.getLineFitter().getProb());
                        }
                    }
                }
                
                if(cls.numLayers(cls.getHits()) >= 3){
                    while(cls.numLayers(randomhits) < 3){                
                        int ranNum = random.nextInt(cls.getHits().size());
                        if(!indexList.contains(ranNum)) {
                            indexList.add(ranNum);
                            randomhits.add(cls.getHits().get(ranNum));
                        }

                    }

                    ClusterFitLC fit = new ClusterFitLC(randomhits);
                    if(fit.lineFit()){
                        histoGroupFitProb.getH1F("fitProb3Layers").fill(fit.getLineFitter().getProb());
                        histoGroupFitProbVsSize.getH2F("fitProbVsSize3Layers").fill(randomhits.size(), fit.getLineFitter().getProb());

                        if(fit.getLineFitter().getProb() < probCut){
                            addDemoGroup(cls, fitClus.getLineFitter().getProb(), randomhits, fit.getLineFitter().getProb());
                        }
                    }
                }

                if(cls.numLayers(cls.getHits()) >= 4){
                    while(cls.numLayers(randomhits) < 4){                
                        int ranNum = random.nextInt(cls.getHits().size());
                        if(!indexList.contains(ranNum)) {
                            indexList.add(ranNum);
                            randomhits.add(cls.getHits().get(ranNum));
                        }

                    }

                    ClusterFitLC fit = new ClusterFitLC(randomhits);
                    if(fit.lineFit()){
                        histoGroupFitProb.getH1F("fitProb4Layers").fill(fit.getLineFitter().getProb());
                        histoGroupFitProbVsSize.getH2F("fitProbVsSize4Layers").fill(randomhits.size(), fit.getLineFitter().getProb());
                    }
                }

                if(cls.numLayers(cls.getHits()) >= 5){
                    while(cls.numLayers(randomhits) < 5){                
                        int ranNum = random.nextInt(cls.getHits().size());
                        if(!indexList.contains(ranNum)) {
                            indexList.add(ranNum);
                            randomhits.add(cls.getHits().get(ranNum));
                        }

                    }

                    ClusterFitLC fit = new ClusterFitLC(randomhits);
                    if(fit.lineFit()){
                        histoGroupFitProb.getH1F("fitProb5Layers").fill(fit.getLineFitter().getProb());
                        histoGroupFitProbVsSize.getH2F("fitProbVsSize5Layers").fill(randomhits.size(), fit.getLineFitter().getProb());
                    }
                }

                if(cls.numLayers(cls.getHits()) >= 6){
                    while(cls.numLayers(randomhits) < 6){                
                        int ranNum = random.nextInt(cls.getHits().size());
                        if(!indexList.contains(ranNum)) {
                            indexList.add(ranNum);
                            randomhits.add(cls.getHits().get(ranNum));
                        }

                    }

                    ClusterFitLC fit = new ClusterFitLC(randomhits);
                    if(fit.lineFit()){
                        histoGroupFitProb.getH1F("fitProb6Layers").fill(fit.getLineFitter().getProb());
                        histoGroupFitProbVsSize.getH2F("fitProbVsSize6Layers").fill(randomhits.size(), fit.getLineFitter().getProb());
                    }
                }
                        
            }
        }
        
        ////// Residual
        HistoGroup histoGroupResidual = histoGroupMap.get("residual");  
        for(Cluster cls : validClusters){
            List<Hit> hits = cls.getHits(); 
            if(hits.size() >= 4){
                List<Integer> ranNumList = new ArrayList();
                while(ranNumList.size() < hits.size()){
                    int ranNum = random.nextInt(hits.size());
                    if(!ranNumList.contains(ranNum)){
                        ranNumList.add(ranNum);
                        Hit removedHit = hits.get(ranNum);
                        List<Hit> newHitList = new ArrayList();
                        newHitList.addAll(hits);
                        newHitList.remove(removedHit);

                        ClusterFitLC fitClus = new ClusterFitLC(newHitList);          
                        if(fitClus.lineFit()){
                            double x = removedHit.layer();
                            double y = removedHit.calcLocY(removedHit.layer(), removedHit.wire());
                            double residual = fitClus.getLineFitter().slope() * x + fitClus.getLineFitter().intercept() - y;

                            histoGroupResidual.getH1F("residual for SL" + cls.superlayer()).fill(residual);
                        } 
                    }
                }
            }
        
        }
                        
    }    
    
    public void addDemoGroup(Cluster cls, double fitProbCls, List<Hit> hits, double fitProbHits){
        if(countDemoCases >= Constants.MAXDEMOCASES) return;
        
        DataGroup grGroup = new DataGroup("Case" + countDemoCases, 1,2 );  
                               
        String str = "Case" + countDemoCases;
        DemoBase demoCls = new DemoBase(str+"Cluser", "prob: " + fitProbCls, cls.getHits());
        demoCls.addBaseLocalSuperlayer();
        demoCls.addHitGraphs(1);
        for(GraphErrors gr : demoCls.getGraphList()){
                grGroup.addDataSet(gr, 0);
        }
        
        DemoBase demoHits = new DemoBase(str+"Hits", "prob: " + fitProbHits, hits);
        demoHits.addBaseLocalSuperlayer();
        demoHits.addHitGraphs(1);
        for(GraphErrors gr : demoHits.getGraphList()){
                grGroup.addDataSet(gr, 1);
        }

        demoGroupMap.put(grGroup.getName(), grGroup);

        countDemoCases++;
    }
    
    public void setProbCut(double cut){
        probCut = cut;
    }
                            
    public static void main(String[] args){
        OptionParser parser = new OptionParser("studyBgEffectsDC");
        parser.setRequiresInputList(false);
        // valid options for event-base analysis
        parser.addOption("-o"          ,"",     "output file name prefix");
        parser.addOption("-n"          ,"-1",   "maximum number of events to process");        
        parser.addOption("-plot"       ,"1",    "display histograms (0/1)");
        parser.addOption("-demo", "1", "display case demo (0/1)");
        parser.addOption("-mDemo", "100", "maxium for number of demonstrated cases");
        parser.addOption("-probCut", "0.55", "prob. cut to pick up demos");
                
        // histogram based analysis
        parser.addOption("-histo"      ,"0",    "read histogram file (0/1)");
        
        parser.parse(args);
        
        String namePrefix  = parser.getOption("-o").stringValue(); 
        int   maxEvents  = parser.getOption("-n").intValue();    
        boolean displayPlots   = (parser.getOption("-plot").intValue()!=0);
        boolean displayDemos = (parser.getOption("-demo").intValue() != 0);
        int maxDemoCases = parser.getOption("-mDemo").intValue();
        double probCut = parser.getOption("-probCut").doubleValue();
        boolean readHistos   = (parser.getOption("-histo").intValue()!=0);  
        Constants.MAXDEMOCASES = maxDemoCases;
        
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
        
        StudyClustering analysis = new StudyClustering();
        analysis.createHistoGroupMap();
        analysis.setProbCut(probCut);
        
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
            frame.setSize(1000, 800);
            frame.add(canvas);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        }
        
        if (displayDemos){
            JFrame frame2 = new JFrame();
            EmbeddedCanvasTabbed canvas2 = analysis.plotDemos();
            if(canvas2 != null){
                frame2.setSize(300, 1500);
                frame2.add(canvas2);
                frame2.setLocationRelativeTo(null);
                frame2.setVisible(true);
            }
        }        
    }
    
}
