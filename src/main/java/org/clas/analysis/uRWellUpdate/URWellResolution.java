package org.clas.analysis.uRWellUpdate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Random;
import javax.swing.JFrame;

import org.jlab.geom.prim.Point3D;
import org.jlab.geom.prim.Line3D;
import org.jlab.geom.prim.Vector3D;

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
public class URWellResolution extends BaseAnalysis{ 
    
    public URWellResolution(){}
    
    @Override
    public void createHistoGroupMap(){
        HistoGroup histoGroupURWellResolutionR1 = new HistoGroup("uRWellResolutionR1", 2, 2);
        H1F h1_xURWellResolutionHBR1= new H1F("xURWellResolutionHBR1","uRWell x resolution at HB level" , 100, -2, 2);
        h1_xURWellResolutionHBR1.setTitleX("Diff. between measurent and projection");
        h1_xURWellResolutionHBR1.setTitleY("Counts");
        histoGroupURWellResolutionR1.addDataSet(h1_xURWellResolutionHBR1, 0);                  
        H1F h1_yURWellResolutionHBR1= new H1F("yURWellResolutionHBR1","uRWell y resolution at HB level" , 100, -10, 10);
        h1_yURWellResolutionHBR1.setTitleX("Diff. between measurent and projection");
        h1_yURWellResolutionHBR1.setTitleY("Counts");
        histoGroupURWellResolutionR1.addDataSet(h1_yURWellResolutionHBR1, 1);  
        H1F h1_xURWellResolutionTBR1= new H1F("xURWellResolutionTBR1","uRWell x resolution at TB level" , 100, -0.4, 0.4);
        h1_xURWellResolutionTBR1.setTitleX("Diff. between measurent and projection");
        h1_xURWellResolutionTBR1.setTitleY("Counts");
        histoGroupURWellResolutionR1.addDataSet(h1_xURWellResolutionTBR1, 2);  
        H1F h1_yURWellResolutionTBR1= new H1F("yURWellResolutionTBR1","uRWell y resolution at TB level" , 100, -2, 2);
        h1_yURWellResolutionTBR1.setTitleX("Diff. between measurent and projection");
        h1_yURWellResolutionTBR1.setTitleY("Counts");
        histoGroupURWellResolutionR1.addDataSet(h1_yURWellResolutionTBR1, 3);                  
        histoGroupMap.put(histoGroupURWellResolutionR1.getName(), histoGroupURWellResolutionR1);
                      
        HistoGroup histoGroupURWellResolutionR2 = new HistoGroup("uRWellResolutionR2", 2, 2);
        H1F h1_xURWellResolutionHBR2= new H1F("xURWellResolutionHBR2","uRWell x resolution at HB level" , 100, -2, 2);
        h1_xURWellResolutionHBR2.setTitleX("Diff. between measurent and projection");
        h1_xURWellResolutionHBR2.setTitleY("Counts");
        histoGroupURWellResolutionR2.addDataSet(h1_xURWellResolutionHBR2, 0);                  
        H1F h1_yURWellResolutionHBR2= new H1F("yURWellResolutionHBR2","uRWell y resolution at HB level" , 100, -10, 10);
        h1_yURWellResolutionHBR2.setTitleX("Diff. between measurent and projection");
        h1_yURWellResolutionHBR2.setTitleY("Counts");
        histoGroupURWellResolutionR2.addDataSet(h1_yURWellResolutionHBR2, 1);  
        H1F h1_xURWellResolutionTBR2= new H1F("xURWellResolutionTBR2","uRWell x resolution at TB level" , 100, -0.4, 0.4);
        h1_xURWellResolutionTBR2.setTitleX("Diff. between measurent and projection");
        h1_xURWellResolutionTBR2.setTitleY("Counts");
        histoGroupURWellResolutionR2.addDataSet(h1_xURWellResolutionTBR2, 2);  
        H1F h1_yURWellResolutionTBR2= new H1F("yURWellResolutionTBR2","uRWell y resolution at TB level" , 100, -2, 2);
        h1_yURWellResolutionTBR2.setTitleX("Diff. between measurent and projection");
        h1_yURWellResolutionTBR2.setTitleY("Counts");
        histoGroupURWellResolutionR2.addDataSet(h1_yURWellResolutionTBR2, 3);                  
        histoGroupMap.put(histoGroupURWellResolutionR2.getName(), histoGroupURWellResolutionR2); 
        
        HistoGroup histoGroupMeasResolutionR1 = new HistoGroup("measResolutionR1", 2, 2);
        H1F h1_c1MeasResolutionHBR1= new H1F("c1MeasResolutionHBR1","c1 meas. resolution at HB level" , 100, -2, 2);
        h1_c1MeasResolutionHBR1.setTitleX("Tracking projection to cluster line");
        h1_c1MeasResolutionHBR1.setTitleY("Counts");
        histoGroupMeasResolutionR1.addDataSet(h1_c1MeasResolutionHBR1, 0);                  
        H1F h1_c2MeasResolutionHBR1= new H1F("c2MeasResolutionHBR1","c2 meas. resolution at HB level" , 100, -2, 2);
        h1_c2MeasResolutionHBR1.setTitleX("Tracking projection to cluster line");
        h1_c2MeasResolutionHBR1.setTitleY("Counts");
        histoGroupMeasResolutionR1.addDataSet(h1_c2MeasResolutionHBR1, 1);         
        H1F h1_c1MeasResolutionTBR1= new H1F("c1MeasResolutionTBR1","c1 meas. resolution at TB level" , 100, -0.4, 0.4);
        h1_c1MeasResolutionTBR1.setTitleX("Tracking projection to cluster line");
        h1_c1MeasResolutionTBR1.setTitleY("Counts");
        histoGroupMeasResolutionR1.addDataSet(h1_c1MeasResolutionTBR1, 2);                  
        H1F h1_c2MeasResolutionTBR1= new H1F("c2MeasResolutionTBR1","c2 meas. resolution at TB level" , 100, -0.4, 0.4);
        h1_c2MeasResolutionTBR1.setTitleX("Tracking projection to cluster line");
        h1_c2MeasResolutionTBR1.setTitleY("Counts");
        histoGroupMeasResolutionR1.addDataSet(h1_c2MeasResolutionTBR1, 3);                 
        histoGroupMap.put(histoGroupMeasResolutionR1.getName(), histoGroupMeasResolutionR1);          
        
        HistoGroup histoGroupMeasResolutionR2 = new HistoGroup("measResolutionR2", 2, 2);
        H1F h1_c1MeasResolutionHBR2= new H1F("c1MeasResolutionHBR2","c1 meas. resolution at HB level" , 100, -2, 2);
        h1_c1MeasResolutionHBR2.setTitleX("Tracking projection to cluster line");
        h1_c1MeasResolutionHBR2.setTitleY("Counts");
        histoGroupMeasResolutionR2.addDataSet(h1_c1MeasResolutionHBR2, 0);                  
        H1F h1_c2MeasResolutionHBR2= new H1F("c2MeasResolutionHBR2","c2 meas. resolution at HB level" , 100, -2, 2);
        h1_c2MeasResolutionHBR2.setTitleX("Tracking projection to cluster line");
        h1_c2MeasResolutionHBR2.setTitleY("Counts");
        histoGroupMeasResolutionR2.addDataSet(h1_c2MeasResolutionHBR2, 1);         
        H1F h1_c1MeasResolutionTBR2= new H1F("c1MeasResolutionTBR2","c1 meas. resolution at TB level" , 100, -0.4, 0.4);
        h1_c1MeasResolutionTBR2.setTitleX("Tracking projection to cluster line");
        h1_c1MeasResolutionTBR2.setTitleY("Counts");
        histoGroupMeasResolutionR2.addDataSet(h1_c1MeasResolutionTBR2, 2);                  
        H1F h1_c2MeasResolutionTBR2= new H1F("c2MeasResolutionTBR2","c2 meas. resolution at TB level" , 100, -0.4, 0.4);
        h1_c2MeasResolutionTBR2.setTitleX("Tracking projection to cluster line");
        h1_c2MeasResolutionTBR2.setTitleY("Counts");
        histoGroupMeasResolutionR2.addDataSet(h1_c2MeasResolutionTBR2, 3);                 
        histoGroupMap.put(histoGroupMeasResolutionR2.getName(), histoGroupMeasResolutionR2);          
    }
             
    public void processEvent(Event event){        
        //Read banks
        LocalEvent localEvent = new LocalEvent(reader, event, Constants.AITB, true);
        
        List<Track> tracksHB = localEvent.getTracksHB();
        List<Track> tracksTB = localEvent.getTracksTB();
        
        List<URWellCross> crosses = localEvent.getURWellCrosses();
        
        
        HistoGroup histoGroupURWellResolutionR1 = histoGroupMap.get("uRWellResolutionR1");
        HistoGroup histoGroupURWellResolutionR2 = histoGroupMap.get("uRWellResolutionR2");

        HistoGroup histoGroupMeasResolutionR1 = histoGroupMap.get("measResolutionR1");
        HistoGroup histoGroupMeasResolutionR2 = histoGroupMap.get("measResolutionR2");   
        
        Vector3D norm = new Vector3D(0, 0, 1);
        
        if(tracksHB.size() == 1 && crosses.size() == 2 && tracksHB.get(0).chi2()/tracksHB.get(0).NDF() < 5){
            Point3D projR1 = tracksHB.get(0).getURWellProjectionLocalR1();
            Point3D projR2 = tracksHB.get(0).getURWellProjectionLocalR2();
            for(URWellCross crs : crosses){
                if(crs.region() == 1){
                    histoGroupURWellResolutionR1.getH1F("xURWellResolutionHBR1").fill(tracksHB.get(0).getURWellProjectionLocalR1().x() - crs.pointLocal().x());
                    histoGroupURWellResolutionR1.getH1F("yURWellResolutionHBR1").fill(tracksHB.get(0).getURWellProjectionLocalR1().y() - crs.pointLocal().y());
                    
                    Line3D lineC1 = new Line3D(crs.getCluster1().originalPointLocal(), crs.getCluster1().endPointLocal());
                    histoGroupMeasResolutionR1.getH1F("c1MeasResolutionHBR1").fill(lineC1.distance(projR1).length() * Math.signum(lineC1.direction().dot(norm)));
                    Line3D lineC2 = new Line3D(crs.getCluster2().originalPointLocal(), crs.getCluster2().endPointLocal());
                    histoGroupMeasResolutionR1.getH1F("c2MeasResolutionHBR1").fill(lineC2.distance(projR1).length() * Math.signum(lineC2.direction().dot(norm)));
                }
                if(crs.region() == 2){
                    histoGroupURWellResolutionR2.getH1F("xURWellResolutionHBR2").fill(tracksHB.get(0).getURWellProjectionLocalR2().x() - crs.pointLocal().x());
                    histoGroupURWellResolutionR2.getH1F("yURWellResolutionHBR2").fill(tracksHB.get(0).getURWellProjectionLocalR2().y() - crs.pointLocal().y());
                    
                    Line3D lineC1 = new Line3D(crs.getCluster1().originalPointLocal(), crs.getCluster1().endPointLocal());
                    histoGroupMeasResolutionR2.getH1F("c1MeasResolutionHBR2").fill(lineC1.distance(projR2).length() * Math.signum(lineC1.direction().dot(norm)));
                    Line3D lineC2 = new Line3D(crs.getCluster2().originalPointLocal(), crs.getCluster2().endPointLocal());
                    histoGroupMeasResolutionR2.getH1F("c2MeasResolutionHBR2").fill(lineC2.distance(projR2).length() * Math.signum(lineC2.direction().dot(norm)));
                }
            }   
        }
        
        if(tracksTB.size() == 1 && crosses.size() == 2 && tracksTB.get(0).chi2()/tracksTB.get(0).NDF() < 5){
            Point3D projR1 = tracksTB.get(0).getURWellProjectionLocalR1();
            Point3D projR2 = tracksTB.get(0).getURWellProjectionLocalR2();
            for(URWellCross crs : crosses){
                if(crs.region() == 1){
                    histoGroupURWellResolutionR1.getH1F("xURWellResolutionTBR1").fill(tracksTB.get(0).getURWellProjectionLocalR1().x() - crs.pointLocal().x());
                    histoGroupURWellResolutionR1.getH1F("yURWellResolutionTBR1").fill(tracksTB.get(0).getURWellProjectionLocalR1().y() - crs.pointLocal().y());
                    
                    Line3D lineC1 = new Line3D(crs.getCluster1().originalPointLocal(), crs.getCluster1().endPointLocal());
                    histoGroupMeasResolutionR1.getH1F("c1MeasResolutionTBR1").fill(lineC1.distance(projR1).length() * Math.signum(lineC1.direction().cross(lineC1.distance(projR1).direction()).dot(norm)));
                    Line3D lineC2 = new Line3D(crs.getCluster2().originalPointLocal(), crs.getCluster2().endPointLocal());
                    histoGroupMeasResolutionR1.getH1F("c2MeasResolutionTBR1").fill(lineC2.distance(projR1).length() * Math.signum(lineC2.direction().cross(lineC2.distance(projR1).direction()).dot(norm)));                    
                }
                if(crs.region() == 2){
                    histoGroupURWellResolutionR2.getH1F("xURWellResolutionTBR2").fill(tracksTB.get(0).getURWellProjectionLocalR2().x() - crs.pointLocal().x());
                    histoGroupURWellResolutionR2.getH1F("yURWellResolutionTBR2").fill(tracksTB.get(0).getURWellProjectionLocalR2().y() - crs.pointLocal().y());
                    
                    Line3D lineC1 = new Line3D(crs.getCluster1().originalPointLocal(), crs.getCluster1().endPointLocal());
                    histoGroupMeasResolutionR2.getH1F("c1MeasResolutionTBR2").fill(lineC1.distance(projR2).length() * Math.signum(lineC1.direction().cross(lineC1.distance(projR2).direction()).dot(norm)));
                    Line3D lineC2 = new Line3D(crs.getCluster2().originalPointLocal(), crs.getCluster2().endPointLocal());
                    histoGroupMeasResolutionR2.getH1F("c2MeasResolutionTBR2").fill(lineC2.distance(projR2).length() * Math.signum(lineC2.direction().cross(lineC2.distance(projR2).direction()).dot(norm)));                                        
                }
            }
        }   
    }
    
    public void postEventProcess() {
        HistoGroup histoGroupURWellResolutionR1 = histoGroupMap.get("uRWellResolutionR1");        
        F1D func_xHBR1  = new F1D("func_xHBR1","[amp]*gaus(x,[mean],[sigma])", -0.3,0.3);
        func_xHBR1.setParameter(0, histoGroupURWellResolutionR1.getH1F("xURWellResolutionHBR1").getMax());
        func_xHBR1.setParameter(1, 0.0);
        func_xHBR1.setParameter(2, 0.15);
        func_xHBR1.setLineColor(2);
        func_xHBR1.setOptStat(1110);
        func_xHBR1.setLineWidth(2);
        histoGroupURWellResolutionR1.getH1F("xURWellResolutionHBR1").fit(func_xHBR1);        
        F1D func_yHBR1  = new F1D("func_yHBR1","[amp]*gaus(x,[mean],[sigma])", -2,2);
        func_yHBR1.setParameter(0, histoGroupURWellResolutionR1.getH1F("yURWellResolutionHBR1").getMax());
        func_yHBR1.setParameter(1, 0.0);
        func_yHBR1.setParameter(2, 1);
        func_yHBR1.setLineColor(2);
        func_yHBR1.setOptStat(1110);
        func_yHBR1.setLineWidth(2);
        histoGroupURWellResolutionR1.getH1F("yURWellResolutionHBR1").fit(func_yHBR1);        
        F1D func_xTBR1  = new F1D("func_xTBR1","[amp]*gaus(x,[mean],[sigma])", -0.06,0.06);
        func_xTBR1.setParameter(0, histoGroupURWellResolutionR1.getH1F("xURWellResolutionTBR1").getMax());
        func_xTBR1.setParameter(1, 0.0);
        func_xTBR1.setParameter(2, 0.03);
        func_xTBR1.setLineColor(2);
        func_xTBR1.setOptStat(1110);
        func_xTBR1.setLineWidth(2);
        histoGroupURWellResolutionR1.getH1F("xURWellResolutionTBR1").fit(func_xTBR1);                        
        F1D func_yTBR1  = new F1D("func_yTBR1","[amp]*gaus(x,[mean],[sigma])", -0.4,0.4);
        func_yTBR1.setParameter(0, histoGroupURWellResolutionR1.getH1F("yURWellResolutionTBR1").getMax());
        func_yTBR1.setParameter(1, 0.0);
        func_yTBR1.setParameter(2, 0.2);
        func_yTBR1.setLineColor(2);
        func_yTBR1.setOptStat(1110);
        func_yTBR1.setLineWidth(2);
        histoGroupURWellResolutionR1.getH1F("yURWellResolutionTBR1").fit(func_yTBR1);
        
        HistoGroup histoGroupURWellResolutionR2 = histoGroupMap.get("uRWellResolutionR2");        
        F1D func_xHBR2  = new F1D("func_xHBR2","[amp]*gaus(x,[mean],[sigma])", -0.3,0.3);
        func_xHBR2.setParameter(0, histoGroupURWellResolutionR2.getH1F("xURWellResolutionHBR2").getMax());
        func_xHBR2.setParameter(1, 0.0);
        func_xHBR2.setParameter(2, 0.15);
        func_xHBR2.setLineColor(2);
        func_xHBR2.setOptStat(1110);
        func_xHBR2.setLineWidth(2);
        histoGroupURWellResolutionR2.getH1F("xURWellResolutionHBR2").fit(func_xHBR2);        
        F1D func_yHBR2  = new F1D("func_yHBR2","[amp]*gaus(x,[mean],[sigma])", -2,2);
        func_yHBR2.setParameter(0, histoGroupURWellResolutionR2.getH1F("yURWellResolutionHBR2").getMax());
        func_yHBR2.setParameter(1, 0.0);
        func_yHBR2.setParameter(2, 1);
        func_yHBR2.setLineColor(2);
        func_yHBR2.setOptStat(1110);
        func_yHBR2.setLineWidth(2);
        histoGroupURWellResolutionR2.getH1F("yURWellResolutionHBR2").fit(func_yHBR2);        
        F1D func_xTBR2  = new F1D("func_xTBR2","[amp]*gaus(x,[mean],[sigma])", -0.06,0.06);
        func_xTBR2.setParameter(0, histoGroupURWellResolutionR2.getH1F("xURWellResolutionTBR2").getMax());
        func_xTBR2.setParameter(1, 0.0);
        func_xTBR2.setParameter(2, 0.03);
        func_xTBR2.setLineColor(2);
        func_xTBR2.setOptStat(1110);
        func_xTBR2.setLineWidth(2);
        histoGroupURWellResolutionR2.getH1F("xURWellResolutionTBR2").fit(func_xTBR2);                        
        F1D func_yTBR2  = new F1D("func_yTBR2","[amp]*gaus(x,[mean],[sigma])", -0.4,0.4);
        func_yTBR2.setParameter(0, histoGroupURWellResolutionR2.getH1F("yURWellResolutionTBR2").getMax());
        func_yTBR2.setParameter(1, 0.0);
        func_yTBR2.setParameter(2, 0.2);
        func_yTBR2.setLineColor(2);
        func_yTBR2.setOptStat(1110);
        func_yTBR2.setLineWidth(2);
        histoGroupURWellResolutionR2.getH1F("yURWellResolutionTBR2").fit(func_yTBR2);  
        
        HistoGroup histoGroupMeasResolutionR1 = histoGroupMap.get("measResolutionR1");       
        F1D func_c1HBR1  = new F1D("func_c1HBR1","[amp]*gaus(x,[mean],[sigma])", -0.6,0.6);
        func_c1HBR1.setParameter(0, histoGroupMeasResolutionR1.getH1F("c1MeasResolutionHBR1").getMax());
        func_c1HBR1.setParameter(1, 0.0);
        func_c1HBR1.setParameter(2, 0.15);
        func_c1HBR1.setLineColor(2);
        func_c1HBR1.setOptStat(1110);
        func_c1HBR1.setLineWidth(2);
        histoGroupMeasResolutionR1.getH1F("c1MeasResolutionHBR1").fit(func_c1HBR1);        
        F1D func_c2HBR1  = new F1D("func_c2HBR1","[amp]*gaus(x,[mean],[sigma])", -0.6,0.6);
        func_c2HBR1.setParameter(0, histoGroupMeasResolutionR1.getH1F("c2MeasResolutionHBR1").getMax());
        func_c2HBR1.setParameter(1, 0.0);
        func_c2HBR1.setParameter(2, 0.15);
        func_c2HBR1.setLineColor(2);
        func_c2HBR1.setOptStat(1110);
        func_c2HBR1.setLineWidth(2);
        histoGroupMeasResolutionR1.getH1F("c2MeasResolutionHBR1").fit(func_c2HBR1);        
        F1D func_c1TBR1  = new F1D("func_c1TBR1","[amp]*gaus(x,[mean],[sigma])", -0.12,0.12);
        func_c1TBR1.setParameter(0, histoGroupMeasResolutionR1.getH1F("c1MeasResolutionTBR1").getMax());
        func_c1TBR1.setParameter(1, 0.0);
        func_c1TBR1.setParameter(2, 0.03);
        func_c1TBR1.setLineColor(2);
        func_c1TBR1.setOptStat(1110);
        func_c1TBR1.setLineWidth(2);
        histoGroupMeasResolutionR1.getH1F("c1MeasResolutionTBR1").fit(func_c1TBR1);                        
        F1D func_c2TBR1  = new F1D("func_c2TBR1","[amp]*gaus(x,[mean],[sigma])", -0.12,0.12);
        func_c2TBR1.setParameter(0, histoGroupMeasResolutionR1.getH1F("c2MeasResolutionTBR1").getMax());
        func_c2TBR1.setParameter(1, 0.0);
        func_c2TBR1.setParameter(2, 0.03);
        func_c2TBR1.setLineColor(2);
        func_c2TBR1.setOptStat(1110);
        func_c2TBR1.setLineWidth(2);
        histoGroupMeasResolutionR1.getH1F("c2MeasResolutionTBR1").fit(func_c2TBR1); 
        
        HistoGroup histoGroupMeasResolutionR2 = histoGroupMap.get("measResolutionR2");       
        F1D func_c1HBR2  = new F1D("func_c1HBR2","[amp]*gaus(x,[mean],[sigma])", -0.6,0.6);
        func_c1HBR2.setParameter(0, histoGroupMeasResolutionR2.getH1F("c1MeasResolutionHBR2").getMax());
        func_c1HBR2.setParameter(1, 0.0);
        func_c1HBR2.setParameter(2, 0.15);
        func_c1HBR2.setLineColor(2);
        func_c1HBR2.setOptStat(1110);
        func_c1HBR2.setLineWidth(2);
        histoGroupMeasResolutionR2.getH1F("c1MeasResolutionHBR2").fit(func_c1HBR2);        
        F1D func_c2HBR2  = new F1D("func_c2HBR2","[amp]*gaus(x,[mean],[sigma])", -0.6,0.6);
        func_c2HBR2.setParameter(0, histoGroupMeasResolutionR2.getH1F("c2MeasResolutionHBR2").getMax());
        func_c2HBR2.setParameter(1, 0.0);
        func_c2HBR2.setParameter(2, 0.15);
        func_c2HBR2.setLineColor(2);
        func_c2HBR2.setOptStat(1110);
        func_c2HBR2.setLineWidth(2);
        histoGroupMeasResolutionR2.getH1F("c2MeasResolutionHBR2").fit(func_c2HBR2);        
        F1D func_c1TBR2  = new F1D("func_c1TBR2","[amp]*gaus(x,[mean],[sigma])", -0.12,0.12);
        func_c1TBR2.setParameter(0, histoGroupMeasResolutionR2.getH1F("c1MeasResolutionTBR2").getMax());
        func_c1TBR2.setParameter(1, 0.0);
        func_c1TBR2.setParameter(2, 0.03);
        func_c1TBR2.setLineColor(2);
        func_c1TBR2.setOptStat(1110);
        func_c1TBR2.setLineWidth(2);
        histoGroupMeasResolutionR2.getH1F("c1MeasResolutionTBR2").fit(func_c1TBR2);                        
        F1D func_c2TBR2  = new F1D("func_c2TBR2","[amp]*gaus(x,[mean],[sigma])", -0.12,0.12);
        func_c2TBR2.setParameter(0, histoGroupMeasResolutionR2.getH1F("c2MeasResolutionTBR2").getMax());
        func_c2TBR2.setParameter(1, 0.0);
        func_c2TBR2.setParameter(2, 0.03);
        func_c2TBR2.setLineColor(2);
        func_c2TBR2.setOptStat(1110);
        func_c2TBR2.setLineWidth(2);
        histoGroupMeasResolutionR2.getH1F("c2MeasResolutionTBR2").fit(func_c2TBR2);                                               
    }            
                            
    public static void main(String[] args){
        OptionParser parser = new OptionParser("uRWellReconstruction");
        parser.setRequiresInputList(false);
        // valid options for event-base analysis
        parser.addOption("-o"          ,"",     "output file name prefix");
        parser.addOption("-n"          ,"-1",   "maximum number of events to process");        
        parser.addOption("-plot"       ,"1",    "display histograms (0/1)");
        parser.addOption("-demo", "1", "display case demo (0/1)");
        parser.addOption("-mDemo", "100", "maxium for number of demonstrated cases");
                
        // histogram based analysis
        parser.addOption("-histo"      ,"0",    "read histogram file (0/1)");
        
        parser.parse(args);
        
        String namePrefix  = parser.getOption("-o").stringValue(); 
        int   maxEvents  = parser.getOption("-n").intValue();    
        boolean displayPlots   = (parser.getOption("-plot").intValue()!=0);
        boolean displayDemos = (parser.getOption("-demo").intValue() != 0);
        int maxDemoCases = parser.getOption("-mDemo").intValue();
        boolean readHistos   = (parser.getOption("-histo").intValue()!=0); 
        Constants.MAXDEMOCASES = maxDemoCases;
        //Constants.URWELLRegions = 1;
        
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
        
        URWellResolution analysis = new URWellResolution();
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
                analysis.processEvent(event);
                progress.updateStatus();
                if(maxEvents>0){
                    if(counter>=maxEvents) break;
                }                    
            }
            
            analysis.postEventProcess();
            
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
            if(canvas != null){
                frame.setSize(1750, 1050);
                frame.add(canvas);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        }
        
        if (displayDemos){
            JFrame frame2 = new JFrame();
            EmbeddedCanvasTabbed canvas2 = analysis.plotDemos();
            if(canvas2 != null){
                frame2.setSize(1200, 750);
                frame2.add(canvas2);
                frame2.setLocationRelativeTo(null);
                frame2.setVisible(true);
            }
        }        
    }
    
}
