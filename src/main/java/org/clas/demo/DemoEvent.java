package org.clas.demo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.clas.reader.LocalEvent;
import org.jlab.groot.data.GraphErrors;

/**
 *
 * @author Tongtong
 */

public class DemoEvent{  
    private String name = "hitDemo";    
    private LocalEvent localEvent;
    
    private Map<Integer, Map<Integer, List<GraphErrors>>> map_sector_map_sl_graphList = new HashMap();
    
    
    public DemoEvent(String name, LocalEvent localEvent){ 
        this.name = name;
        this.localEvent = localEvent;
        
        for(int sec = 1; sec <=6; sec++){
            String str = name + "S" + sec;
            DemoSector demo = new DemoSector(str, localEvent, sec);
            demo.addGraphsDenoisingClusteringAICandHBTB();
            map_sector_map_sl_graphList.put(sec, demo.getSLGraphListMap());
        }
    }

    public Map<Integer, Map<Integer, List<GraphErrors>>> getGraphs(){
        return map_sector_map_sl_graphList;
    }
}