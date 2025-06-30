package org.clas.supports;

import java.util.*;

import org.jlab.utils.options.OptionParser;
import org.jlab.jnp.hipo4.io.HipoReader;
import org.jlab.jnp.hipo4.data.Event;
import org.jlab.jnp.hipo4.data.SchemaFactory;
import org.jlab.jnp.hipo4.io.HipoWriterSorted;
import org.jlab.utils.benchmark.ProgressPrintout;

import org.clas.element.RunConfig;
import org.clas.reader.Reader;
import org.clas.reader.Banks;

/**
 *
 * @author Tongtong
 */

public class OrderEvents{            
    public static void main(String[] args) {
        OptionParser parser = new OptionParser("orderEvents");
        parser.setRequiresInputList(false);
        // valid options for event-base analysis
        parser.addOption("-o", "", "output file name prefix");
        parser.addOption("-n", "100000", "maximum number of events to process");
        parser.parse(args);

        String namePrefix = parser.getOption("-o").stringValue();
        int maxEvents = parser.getOption("-n").intValue();
        
        List<String> inputList = parser.getInputList();
        if (inputList.isEmpty() == true) {
            parser.printUsage();
            System.out.println("\n >>>> error: no input file is specified....\n");
            System.exit(0);
        }
        
        String outputName = "orderedEvents.hipo";
        if (!namePrefix.isEmpty()) {
            outputName = namePrefix + "_" + outputName;
        }
        
        HipoReader reader = new HipoReader();
        reader.open(inputList.get(0));
        SchemaFactory schema = reader.getSchemaFactory();
        
        HipoWriterSorted writer = new HipoWriterSorted();
        writer.getSchemaFactory().copy(schema);
        writer.setCompressionType(2);   
        writer.open(outputName);
        
        
        Reader localReader = new Reader(new Banks(schema));

        ProgressPrintout progress = new ProgressPrintout();
        Map<Integer, Event> map_eventNum_event = new HashMap();
        int counter = 0;
        while(reader.hasNext()){
            Event event = new Event();   
            reader.nextEvent(event);
            
            RunConfig runConfig = localReader.readRunConfig(event);
            int eventNum = runConfig.event();
            map_eventNum_event.put(eventNum, event);
            progress.updateStatus();
            
            counter++;
            if (counter >= maxEvents) break;
        }
        
        List<Integer> eventNumList = new ArrayList();
        eventNumList.addAll(map_eventNum_event.keySet());
        Collections.sort(eventNumList);
        
        for(int eventNum : eventNumList){
            writer.addEvent(map_eventNum_event.get(eventNum));
            
            progress.updateStatus();
        }
        
        progress.showStatus();
        reader.close();
        writer.close();        
    }
}