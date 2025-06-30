package org.clas.supports;

import java.util.List;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import javax.swing.JFrame;

import org.jlab.utils.options.OptionParser;
import org.jlab.utils.benchmark.ProgressPrintout;
import org.jlab.jnp.hipo4.io.HipoReader;
import org.jlab.jnp.hipo4.data.Event;
import org.jlab.jnp.hipo4.data.SchemaFactory;
import org.jlab.groot.graphics.EmbeddedCanvasTabbed;
import org.jlab.groot.data.GraphErrors;
import org.jlab.groot.group.DataGroup;

import org.clas.utilities.Constants;
import org.clas.reader.Reader;
import org.clas.reader.Banks;
import org.clas.reader.LocalEvent;
import org.clas.demo.DemoSector;
import org.clas.demo.DemoEvent;

/**
 *
 * @author Tongtong
 */

public class DisplayEvents{                  
    public static void main(String[] args) {
        OptionParser parser = new OptionParser("extractEvents");
        parser.setRequiresInputList(false);
        // valid options for event-base analysis
        parser.addOption("-n", "-1", "maximum number of events to process");
        parser.addOption("-events"       ,"1",     "(comma-separated) event list must be specified, e.g. \"5,20,100\"");
        parser.addOption("-sector", "-1", "specified sector");

        parser.parse(args);

        int maxEvents = parser.getOption("-n").intValue();
        List<Integer> eventList = new ArrayList();
        int sector = parser.getOption("-sector").intValue();
        
        if(parser.getOption("-events").stringValue().isEmpty()) {
            System.out.println("\n >>>> error: no event is specified....\n");
            System.exit(0);
        }
        else{
           String[] eventNumbers = parser.getOption("-events").stringValue().split(",");
           for(String eventNum : eventNumbers)
            eventList.add(Integer.parseInt(eventNum));
        }

        List<String> inputList = parser.getInputList();
        if (inputList.isEmpty() == true) {
            parser.printUsage();
            System.out.println("\n >>>> error: no input file is specified....\n");
            System.exit(0);
        }
                
        HipoReader reader = new HipoReader();
        reader.open(inputList.get(0));
        SchemaFactory schema = reader.getSchemaFactory();
        
        Event event = new Event();   
        
        Reader localReader = new Reader(new Banks(schema));
        LinkedHashMap<String, DataGroup> demoGroupMap = new LinkedHashMap(); 
        ProgressPrintout progress = new ProgressPrintout();
        int counter = 0;
        while(reader.hasNext()){
            reader.nextEvent(event);
            
            LocalEvent localEvent = new LocalEvent(localReader, event, Constants.AITB);
            int eventNum = localEvent.getRunConfig().event();
            if(eventList.contains(eventNum)){
                if(sector == -1){
                    DemoEvent demo = new DemoEvent("E" + eventNum, localEvent);
                    for(int sec = 1; sec <= 6; sec++){
                        DataGroup grGroup = new DataGroup("E" + eventNum + "S" + sec, 6,1);                
                        for(int sl : demo.getGraphs().get(sec).keySet()){
                            for(GraphErrors gr : demo.getGraphs().get(sec).get(sl)){
                                grGroup.addDataSet(gr, sl - 1);
                            }
                        }
                        demoGroupMap.put(grGroup.getName(), grGroup);
                    }
                }
                else{
                    DataGroup grGroup = new DataGroup("E" + eventNum + "S" + sector, 6,1);                

                    String str = "E" + eventNum + "S" + sector;
                    DemoSector demo = new DemoSector(str, localEvent, sector);
                    demo.addGraphsDenoisingClusteringAICandHBTB();
                    for (int sl : demo.getSLGraphListMap().keySet()) {
                        for (GraphErrors gr : demo.getSLGraphListMap().get(sl)) {
                            grGroup.addDataSet(gr, sl - 1);
                        }
                    }
                    demoGroupMap.put(grGroup.getName(), grGroup);  
                }
            }
            
            progress.updateStatus();
            if ((maxEvents > 0 && counter >= maxEvents) || eventNum == eventList.get(eventList.size()-1)) {
                break;
            }
        }
        progress.showStatus();
        reader.close();      
        
        JFrame frame = new JFrame();
        EmbeddedCanvasTabbed canvas = null;
        for(String key : demoGroupMap.keySet()) {
                if(canvas==null) canvas = new EmbeddedCanvasTabbed(key);
                else             canvas.addCanvas(key);
                canvas.getCanvas(key).draw(demoGroupMap.get(key));
        }
        if (canvas != null) {
            frame.setSize(1200, 750);
            frame.add(canvas);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        }
    }
    
}