package org.clas.analysis.studyBgEffects;

import java.util.List;
import java.util.ArrayList;

import org.jlab.groot.data.H1F;
import org.jlab.groot.data.H2F;

import org.clas.analysis.BaseAnalysis;
import org.clas.element.TDC;
import org.clas.graph.HistoGroup;
import org.clas.reader.LocalEvent;


/**
 *
 * @author Tongtong Cao
 */
public class BgEffectsDenoising extends BaseAnalysis {      
    @Override
    public void createHistoGroupMap() {
        ////// AIDenoising
        HistoGroup histoGroupHitAIDenoising = new HistoGroup("hitAIDenoising", 3, 2);
        H1F h1_hitSLSamples1 = new H1F("hitSLSamples1", "hits for sample 1", 6, 0.5, 6.5);
        h1_hitSLSamples1.setTitleX("SL");
        h1_hitSLSamples1.setTitleY("Counts");
        h1_hitSLSamples1.setLineColor(1);
        histoGroupHitAIDenoising.addDataSet(h1_hitSLSamples1, 0);
        H1F h1_remainingHitSLAIDenoisingSample1 = new H1F("remainingHitSLAIDenoisingSample1", "hits for sample 1", 6, 0.5, 6.5);
        h1_remainingHitSLAIDenoisingSample1.setTitleX("SL");
        h1_remainingHitSLAIDenoisingSample1.setTitleY("Counts");
        h1_remainingHitSLAIDenoisingSample1.setLineColor(2);
        histoGroupHitAIDenoising.addDataSet(h1_remainingHitSLAIDenoisingSample1, 0);
        H1F h1_hitSLSamples2 = new H1F("hitSLSamples2", "hits for sample 2", 6, 0.5, 6.5);
        h1_hitSLSamples2.setTitleX("SL");
        h1_hitSLSamples2.setTitleY("Counts");
        h1_hitSLSamples2.setLineColor(1);
        histoGroupHitAIDenoising.addDataSet(h1_hitSLSamples2, 1);
        H1F h1_remainingHitSLAIDenoisingSample2 = new H1F("remainingHitSLAIDenoisingSample2", "hits for sample 2", 6, 0.5, 6.5);
        h1_remainingHitSLAIDenoisingSample2.setTitleX("SL");
        h1_remainingHitSLAIDenoisingSample2.setTitleY("Counts");
        h1_remainingHitSLAIDenoisingSample2.setLineColor(2);
        histoGroupHitAIDenoising.addDataSet(h1_remainingHitSLAIDenoisingSample2, 1);
        H1F h1_hitSLNormalSamples2 = new H1F("hitSLNormalSamples2", "normal hits for sample 2", 6, 0.5, 6.5);
        h1_hitSLNormalSamples2.setTitleX("SL");
        h1_hitSLNormalSamples2.setTitleY("Counts");
        h1_hitSLNormalSamples2.setLineColor(1);
        histoGroupHitAIDenoising.addDataSet(h1_hitSLNormalSamples2, 3);
        H1F h1_remainingHitSLAIDenoisingNormalSample2 = new H1F("remainingHitSLAIDenoisingNormalSample2", "normal hits for sample 2", 6, 0.5, 6.5);
        h1_remainingHitSLAIDenoisingNormalSample2.setTitleX("SL");
        h1_remainingHitSLAIDenoisingNormalSample2.setTitleY("Counts");
        h1_remainingHitSLAIDenoisingNormalSample2.setLineColor(2);
        histoGroupHitAIDenoising.addDataSet(h1_remainingHitSLAIDenoisingNormalSample2, 3);
        H1F h1_hitSLBgSamples2 = new H1F("hitSLBgSamples2", "bg hits for sample 2", 6, 0.5, 6.5);
        h1_hitSLBgSamples2.setTitleX("SL");
        h1_hitSLBgSamples2.setTitleY("Counts");
        h1_hitSLBgSamples2.setLineColor(1);
        histoGroupHitAIDenoising.addDataSet(h1_hitSLBgSamples2, 4);
        H1F h1_remainingHitSLAIDenoisingBgSample2 = new H1F("remainingHitSLAIDenoisingBgSample2", "bg hits for sample 2", 6, 0.5, 6.5);
        h1_remainingHitSLAIDenoisingBgSample2.setTitleX("SL");
        h1_remainingHitSLAIDenoisingBgSample2.setTitleY("Counts");
        h1_remainingHitSLAIDenoisingBgSample2.setLineColor(2);
        histoGroupHitAIDenoising.addDataSet(h1_remainingHitSLAIDenoisingBgSample2, 4);
        histoGroupMap.put(histoGroupHitAIDenoising.getName(), histoGroupHitAIDenoising);
        
        HistoGroup histoGroupOrderSample1 = new HistoGroup("orderSample1", 3, 2);
        HistoGroup histoGroupOrderSample2 = new HistoGroup("orderSample2", 3, 2);
        HistoGroup histoGroupOrderNormalSample2 = new HistoGroup("orderNormalSample2", 3, 2);
        HistoGroup histoGroupOrderBgSample2 = new HistoGroup("orderBgSample2", 3, 2);
        HistoGroup histoGroupOrderNormalComp = new HistoGroup("orderNormalComp", 3, 2);
        HistoGroup histoGroupIsSameOrderNormalComp = new HistoGroup("isSameOrderNormalComp", 3, 2);
        HistoGroup histoGroupareBothRemainedNormalComp = new HistoGroup("areBothRemainedNormalComp", 3, 2);        
        for (int i = 0; i < 6; i++) {
            H1F h1_order1 = new H1F("order for SL" + Integer.toString(i + 1) + " for sample1",
                    "order for SL" + Integer.toString(i + 1), 130, 0, 130);
            h1_order1.setTitleX("order");
            h1_order1.setTitleY("Counts");
            h1_order1.setLineColor(1);
            histoGroupOrderSample1.addDataSet(h1_order1, i);

            H1F h1_order2 = new H1F("order for SL" + Integer.toString(i + 1) + " for sample2",
                    "order for SL" + Integer.toString(i + 1), 130, 0, 130);
            h1_order2.setTitleX("order");
            h1_order2.setTitleY("Counts");
            h1_order2.setLineColor(1);
            histoGroupOrderSample2.addDataSet(h1_order2, i);

            H1F h1_orderNormal2 = new H1F("order for SL" + Integer.toString(i + 1) + " for normal of sample2",
                    "order for SL" + Integer.toString(i + 1), 130, 0, 130);
            h1_orderNormal2.setTitleX("order");
            h1_orderNormal2.setTitleY("Counts");
            h1_orderNormal2.setLineColor(1);
            histoGroupOrderNormalSample2.addDataSet(h1_orderNormal2, i);

            H1F h1_orderBg2 = new H1F("order for SL" + Integer.toString(i + 1) + " for bg of sample2",
                    "order for SL" + Integer.toString(i + 1), 130, 0, 130);
            h1_orderBg2.setTitleX("order");
            h1_orderBg2.setTitleY("Counts");
            h1_orderBg2.setLineColor(1);
            histoGroupOrderBgSample2.addDataSet(h1_orderBg2, i);

            H2F h2_orderNormalComp = new H2F("order comp for SL" + Integer.toString(i + 1) + " for normal",
                    "order for SL" + Integer.toString(i + 1), 13, 0, 13, 13, 0, 13);
            h2_orderNormalComp.setTitleX("order/10 in sample 1");
            h2_orderNormalComp.setTitleY("order/10 in sample 2");
            //h2_orderNormalComp.getAttributes().setMarkerSize(20);
            histoGroupOrderNormalComp.addDataSet(h2_orderNormalComp, i);

            H1F h1_isSameOrderNormalComp = new H1F("isSameOrder for SL" + Integer.toString(i + 1),
                    "isSameOrder for SL" + Integer.toString(i + 1), 2, -0.5, 1.5);
            h1_isSameOrderNormalComp.setTitleX("is same order");
            h1_isSameOrderNormalComp.setTitleY("Counts");
            h1_isSameOrderNormalComp.setLineColor(1);
            histoGroupIsSameOrderNormalComp.addDataSet(h1_isSameOrderNormalComp, i);
            
            H1F h1_areBothRemainedNormalCompNormalComp = new H1F("areBothRemained for SL" + Integer.toString(i + 1),
                    "areBothRemained for SL" + Integer.toString(i + 1), 4, -0.5, 3.5);
            h1_areBothRemainedNormalCompNormalComp.setTitleX("are both remained");
            h1_areBothRemainedNormalCompNormalComp.setTitleY("Counts");
            h1_areBothRemainedNormalCompNormalComp.setLineColor(1);
            histoGroupareBothRemainedNormalComp.addDataSet(h1_areBothRemainedNormalCompNormalComp, i);
        }
        histoGroupMap.put(histoGroupOrderSample1.getName(), histoGroupOrderSample1);
        histoGroupMap.put(histoGroupOrderSample2.getName(), histoGroupOrderSample2);
        histoGroupMap.put(histoGroupOrderNormalSample2.getName(), histoGroupOrderNormalSample2);
        histoGroupMap.put(histoGroupOrderBgSample2.getName(), histoGroupOrderBgSample2);
        histoGroupMap.put(histoGroupOrderNormalComp.getName(), histoGroupOrderNormalComp);
        histoGroupMap.put(histoGroupIsSameOrderNormalComp.getName(), histoGroupIsSameOrderNormalComp);
        
        HistoGroup histoGroupRatioSameOrderSLNormalComp = new HistoGroup("ratioSameOrderSLNormalComp", 1, 1);
        H1F h1_ratioSameOrderSLNormalComp = new H1F("ratioSameOrderSL", "ratio for normal hits with same order", 6, 0.5, 6.5);
        h1_ratioSameOrderSLNormalComp.setTitleX("SL");
        h1_ratioSameOrderSLNormalComp.setTitleY("Counts");
        h1_ratioSameOrderSLNormalComp.setLineColor(1);
        histoGroupRatioSameOrderSLNormalComp.addDataSet(h1_ratioSameOrderSLNormalComp, 0);
        histoGroupMap.put(histoGroupRatioSameOrderSLNormalComp.getName(), histoGroupRatioSameOrderSLNormalComp);                
        histoGroupMap.put(histoGroupareBothRemainedNormalComp.getName(), histoGroupareBothRemainedNormalComp);
    }
    
    public void processEvent(LocalEvent localEvent1, LocalEvent localEvent2) {
        List<TDC> normalTDC2 = new ArrayList();
        List<TDC> bgTDC2 = new ArrayList();
        localEvent2.separateNormalBgTDCs(localEvent2.getTDCs(), normalTDC2, bgTDC2);
        
        HistoGroup histoGroupHitAIDenoising = histoGroupMap.get("hitAIDenoising");
        // order in sample 1
        HistoGroup histoGroupOrderSample1 = histoGroupMap.get("orderSample1");
        for (TDC tdc : localEvent1.getTDCs()) {
            histoGroupOrderSample1.getH1F("order for SL" + tdc.superlayer() + " for sample1").fill(tdc.order());
            histoGroupHitAIDenoising.getH1F("hitSLSamples1").fill(tdc.superlayer());

            if (tdc.isRemainedAfterAIDenoising()) {
                histoGroupHitAIDenoising.getH1F("remainingHitSLAIDenoisingSample1").fill(tdc.superlayer());
            }            
        }

        // order in sample 2
        HistoGroup histoGroupOrderSample2 = histoGroupMap.get("orderSample2");
        for (TDC tdc : localEvent2.getTDCs()) {
            histoGroupOrderSample2.getH1F("order for SL" + tdc.superlayer() + " for sample2").fill(tdc.order());
            histoGroupHitAIDenoising.getH1F("hitSLSamples2").fill(tdc.superlayer());

            if (tdc.isRemainedAfterAIDenoising()) {
                histoGroupHitAIDenoising.getH1F("remainingHitSLAIDenoisingSample2").fill(tdc.superlayer());
            }
        }

        // order for normal hits in sample 2
        HistoGroup histoGroupOrderNormalSample2 = histoGroupMap.get("orderNormalSample2");
        for (TDC tdc : normalTDC2) {
            histoGroupOrderNormalSample2.getH1F("order for SL" + tdc.superlayer() + " for normal of sample2").fill(tdc.order());
            histoGroupHitAIDenoising.getH1F("hitSLNormalSamples2").fill(tdc.superlayer());

            if (tdc.isRemainedAfterAIDenoising()) {
                histoGroupHitAIDenoising.getH1F("remainingHitSLAIDenoisingNormalSample2").fill(tdc.superlayer());
            }
        }
        
        // order for bg hits in sample 2
        HistoGroup histoGroupOrderBgSample2 = histoGroupMap.get("orderBgSample2");
        for (TDC tdc : bgTDC2) {
            histoGroupOrderBgSample2.getH1F("order for SL" + tdc.superlayer() + " for bg of sample2").fill(tdc.order());

            histoGroupHitAIDenoising.getH1F("hitSLBgSamples2").fill(tdc.superlayer());

            if (tdc.isRemainedAfterAIDenoising()) {
                histoGroupHitAIDenoising.getH1F("remainingHitSLAIDenoisingBgSample2").fill(tdc.superlayer());
            }
        }
        
        // comparison for order of normal hits between sample 1 and sample 2
        HistoGroup histoGroupOrderNormalComp = histoGroupMap.get("orderNormalComp");
        HistoGroup histoGroupIsSameOrderNormalComp = histoGroupMap.get("isSameOrderNormalComp");
        HistoGroup histoGroupareBothRemainedNormalComp = histoGroupMap.get("areBothRemainedNormalComp");
        for (TDC tdc1 : localEvent1.getTDCs()) {
            for (TDC tdc2 : normalTDC2) {
                if (tdc2.matchTDC(tdc1)) {
                    histoGroupOrderNormalComp.getH2F("order comp for SL" + tdc2.superlayer() + " for normal").fill(tdc1.order() / 10., tdc2.order() / 10.);
                    
                    if (tdc2.order() == tdc1.order()) {
                        histoGroupIsSameOrderNormalComp.getH1F("isSameOrder for SL" + tdc2.superlayer()).fill(1);
                    } else {
                        histoGroupIsSameOrderNormalComp.getH1F("isSameOrder for SL" + tdc2.superlayer()).fill(0);
                    }
                    
                    if(tdc1.isRemainedAfterAIDenoising() && tdc2.isRemainedAfterAIDenoising()){
                        histoGroupareBothRemainedNormalComp.getH1F("areBothRemained for SL" + tdc2.superlayer()).fill(1);
                    }
                    else if(tdc1.isRemainedAfterAIDenoising() && !tdc2.isRemainedAfterAIDenoising()){
                        histoGroupareBothRemainedNormalComp.getH1F("areBothRemained for SL" + tdc2.superlayer()).fill(0);
                    }
                    else if(!tdc1.isRemainedAfterAIDenoising() && tdc2.isRemainedAfterAIDenoising()){
                        histoGroupareBothRemainedNormalComp.getH1F("areBothRemained for SL" + tdc2.superlayer()).fill(2);
                    }
                    else {
                        histoGroupareBothRemainedNormalComp.getH1F("areBothRemained for SL" + tdc2.superlayer()).fill(3);
                    }                                                                       
                    break;
                }
            }
        }
    } 
    
    
    public void postEventProcess() {
        HistoGroup histoGroupHitAIDenoising = histoGroupMap.get("hitAIDenoising");
        H1F histoGroupHitRatioSLAIDenoisingSample2 = histoGroupHitAIDenoising.getH1F("hitSLNormalSamples2").histClone("hitRatioSLAIDenoisingSample2");        
        histoGroupHitRatioSLAIDenoisingSample2.divide(histoGroupHitAIDenoising.getH1F("hitSLSamples2"));
        histoGroupHitRatioSLAIDenoisingSample2.setTitle("ratio of normal hits for sample 2");
        histoGroupHitRatioSLAIDenoisingSample2.setTitleX("SL");
        histoGroupHitRatioSLAIDenoisingSample2.setTitleY("Counts");
        histoGroupHitRatioSLAIDenoisingSample2.setLineColor(1);
        histoGroupHitAIDenoising.addDataSet(histoGroupHitRatioSLAIDenoisingSample2, 5);
        
        H1F histoGroupRemainingHitRatioSLAIDenoisingSample2 = histoGroupHitAIDenoising.getH1F("remainingHitSLAIDenoisingNormalSample2").histClone("remainingHitRatioSLAIDenoisingSample2");
        histoGroupRemainingHitRatioSLAIDenoisingSample2.divide(histoGroupHitAIDenoising.getH1F("remainingHitSLAIDenoisingSample2"));
        histoGroupRemainingHitRatioSLAIDenoisingSample2.setTitle("ratio of normal hits for sample 2");
        histoGroupRemainingHitRatioSLAIDenoisingSample2.setTitleX("SL");
        histoGroupRemainingHitRatioSLAIDenoisingSample2.setTitleY("Counts");
        histoGroupRemainingHitRatioSLAIDenoisingSample2.setLineColor(2);
        histoGroupHitAIDenoising.addDataSet(histoGroupRemainingHitRatioSLAIDenoisingSample2, 5);
        
        HistoGroup histoGroupRatioSameOrderSLNormalComp = histoGroupMap.get("ratioSameOrderSLNormalComp");
        HistoGroup histoGroupIsSameOrderNormalComp = histoGroupMap.get("isSameOrderNormalComp");
        for(int i = 0; i < 6; i++){
            double bin1 = histoGroupIsSameOrderNormalComp.getH1F("isSameOrder for SL" + Integer.toString(i + 1)).getBinContent(0);
            double bin2 = histoGroupIsSameOrderNormalComp.getH1F("isSameOrder for SL" + Integer.toString(i + 1)).getBinContent(1);
            double ratio = bin2/(bin1+bin2);
            histoGroupRatioSameOrderSLNormalComp.getH1F("ratioSameOrderSL").setBinContent(i, ratio);
        }
    }
}