package org.clas.demo;

import java.util.ArrayList;
import java.util.List;

import org.clas.reader.LocalEvent;
import org.clas.element.URWellCross;

/**
 *
 * @author Tongtong
 */

public class DemoSectorWithURWell extends DemoSector{  
    List<URWellCross> uRWellCrosses = new ArrayList();
    
    /**
     * @param name name
     * @param localEvent events with all banks    
     * @param sector sector
    */    
    public DemoSectorWithURWell(String name, LocalEvent localEvent, int sector){
        super(name, localEvent, sector);
        makeURWellCrossList();
    }
    
    private void makeURWellCrossList(){        
        for(URWellCross crs: localEvent.getURWellCrosses()){
            if(crs.sector() == sector) uRWellCrosses.add(crs);
        }
    }
    
    public void addGraphsDenoisingClusteringWithURWell(){
        String str = name + ": SL" + 1;
        DemoSuperlayerWithURWell demoSLWithURWell = new DemoSuperlayerWithURWell(str, "SL" + 1, map_sl_tdcs.get(1), map_sl_hits.get(1), map_sl_clsListClustering.get(1), uRWellCrosses);
        demoSLWithURWell.addBaseLocalSuperlayerWithURWell();
        demoSLWithURWell.addGraphsDenoisingClusteringWithURWell();
        map_sl_graphList.put(1, demoSLWithURWell.getGraphList());
            
        for(int i = 2; i <=6; i++){
            str = name + ": SL" + i;
            DemoSuperlayer demoSL = new DemoSuperlayer(str, "SL" + i, map_sl_tdcs.get(i), map_sl_hits.get(i), map_sl_clsListClustering.get(i));
            demoSL.addBaseLocalSuperlayer();
            demoSL.addGraphsDenoisingClustering();
            map_sl_graphList.put(i, demoSL.getGraphList());
        }                
    }
    
    public void addGraphsRawDenoisingClusteringWithURWell(){
        String str = name + ": SL" + 1;
        DemoSuperlayerWithURWell demoSLWithURWell = new DemoSuperlayerWithURWell(str, "SL" + 1, map_sl_tdcs.get(1), map_sl_hits.get(1), map_sl_clsListClustering.get(1), uRWellCrosses);
        demoSLWithURWell.addBaseLocalSuperlayerWithURWell();
        demoSLWithURWell.addGraphsRawDenoisingClusteringWithURWell();
        map_sl_graphList.put(1, demoSLWithURWell.getGraphList());
        
        for(int i = 2; i <=6; i++){
            str = name + ": SL" + i;
            DemoSuperlayer demoSL = new DemoSuperlayer(str, "SL" + i, map_sl_tdcs.get(i), map_sl_hits.get(i), map_sl_clsListClustering.get(i));
            demoSL.addBaseLocalSuperlayer();
            demoSL.addGraphsRawDenoisingClustering();
            map_sl_graphList.put(i, demoSL.getGraphList());
        }                
    }
    
    public void addGraphsDenoisingClusteringHBTBWithURWell(){
        String str = name + ": SL" + 1;
        DemoSuperlayerWithURWell demoSLWithURWell = new DemoSuperlayerWithURWell(str, "SL" + 1, map_sl_tdcs.get(1), map_sl_hits.get(1), map_sl_clsListClustering.get(1), map_sl_clsListHB.get(1), map_sl_clsListTB.get(1), uRWellCrosses);
        demoSLWithURWell.addBaseLocalSuperlayerWithURWell();
        demoSLWithURWell.addGraphsDenoisingClusteringHBTBWithURWell();
        map_sl_graphList.put(1, demoSLWithURWell.getGraphList());
        
        for(int i = 2; i <=6; i++){
            str = name + ": SL" + i;
            DemoSuperlayer demoSL = new DemoSuperlayer(str, "SL" + i, map_sl_tdcs.get(i), map_sl_hits.get(i), map_sl_clsListClustering.get(i), map_sl_clsListHB.get(i), map_sl_clsListTB.get(i));
            demoSL.addBaseLocalSuperlayer();
            demoSL.addGraphsDenoisingClusteringHBTB();
            map_sl_graphList.put(i, demoSL.getGraphList());
        }                
    }
    
    public void addGraphsRawDenoisingClusteringHBTBWithURWell(){
        String str = name + ": SL" + 1;
        DemoSuperlayerWithURWell demoSLWithURWell = new DemoSuperlayerWithURWell(str, "SL" + 1, map_sl_tdcs.get(1), map_sl_hits.get(1), map_sl_clsListClustering.get(1), map_sl_clsListHB.get(1), map_sl_clsListTB.get(1), uRWellCrosses);
        demoSLWithURWell.addBaseLocalSuperlayerWithURWell();
        demoSLWithURWell.addGraphsRawDenoisingClusteringHBTBWithURWell();
        map_sl_graphList.put(1, demoSLWithURWell.getGraphList());
        
        for(int i = 2; i <=6; i++){
            str = name + ": SL" + i;
            DemoSuperlayer demoSL = new DemoSuperlayer(str, "SL" + i, map_sl_tdcs.get(i), map_sl_hits.get(i), map_sl_clsListClustering.get(i), map_sl_clsListHB.get(i), map_sl_clsListTB.get(i));
            demoSL.addBaseLocalSuperlayer();
            demoSL.addGraphsRawDenoisingClusteringHBTB();
            map_sl_graphList.put(i, demoSL.getGraphList());
        }                
    }
    
    public void addGraphsDenoisingClusteringAICandHBTBWithURWell(){
        String str = name + ": SL" + 1;
        DemoSuperlayerWithURWell demoSLWithURWell = new DemoSuperlayerWithURWell(str, "SL" + 1, map_sl_tdcs.get(1), map_sl_hits.get(1), map_sl_clsListClustering.get(1), map_sl_clsListAICand.get(1), map_sl_clsListHB.get(1), map_sl_clsListTB.get(1), uRWellCrosses);
        demoSLWithURWell.addBaseLocalSuperlayerWithURWell();
        demoSLWithURWell.addGraphsDenoisingClusteringAICandHBTBWithURWell();
        map_sl_graphList.put(1, demoSLWithURWell.getGraphList());        
        
        for(int i = 2; i <=6; i++){
            str = name + ": SL" + i;
            DemoSuperlayer demoSL = new DemoSuperlayer(str, "SL" + i, map_sl_tdcs.get(i), map_sl_hits.get(i), map_sl_clsListClustering.get(i),  map_sl_clsListAICand.get(i), map_sl_clsListHB.get(i), map_sl_clsListTB.get(i));
            demoSL.addBaseLocalSuperlayer();
            demoSL.addGraphsDenoisingClusteringAICandHBTB();
            map_sl_graphList.put(i, demoSL.getGraphList());
        }                
    }
    
    public void addGraphsRawDenoisingClusteringAICandHBTBWithURWell(){
        String str = name + ": SL" + 1;
        DemoSuperlayerWithURWell demoSLWithURWell = new DemoSuperlayerWithURWell(str, "SL" + 1, map_sl_tdcs.get(1), map_sl_hits.get(1), map_sl_clsListClustering.get(1), map_sl_clsListAICand.get(1), map_sl_clsListHB.get(1), map_sl_clsListTB.get(1),  uRWellCrosses);
        demoSLWithURWell.addBaseLocalSuperlayerWithURWell();
        demoSLWithURWell.addGraphsRawDenoisingClusteringAICandHBTBWithURWell();
        map_sl_graphList.put(1, demoSLWithURWell.getGraphList());
        
        for(int i = 2; i <=6; i++){
            str = name + ": SL" + i;
            DemoSuperlayer demoSL = new DemoSuperlayer(str, "SL" + i, map_sl_tdcs.get(i), map_sl_hits.get(i), map_sl_clsListClustering.get(i), map_sl_clsListAICand.get(i), map_sl_clsListHB.get(i), map_sl_clsListTB.get(i));
            demoSL.addBaseLocalSuperlayer();
            demoSL.addGraphsRawDenoisingClusteringAICandHBTB();
            map_sl_graphList.put(i, demoSL.getGraphList());
        }                
    }      
                                  
}