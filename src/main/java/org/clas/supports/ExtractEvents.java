package org.clas.supports;

import java.util.List;
import java.util.ArrayList;

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

public class ExtractEvents{            
    public static void main(String[] args) {
        OptionParser parser = new OptionParser("extractEvents");
        parser.setRequiresInputList(false);
        // valid options for event-base analysis
        parser.addOption("-o", "", "output file name prefix");
        parser.addOption("-n", "100000", "maximum number of events to process");
        parser.addOption("-events"       ,"1",     "event list must be specified by with comma separation, e.g. \"5,20,100\", or sequence event list, like \"20-100\" ");

        parser.parse(args);

        String namePrefix = parser.getOption("-o").stringValue();
        int maxEvents = parser.getOption("-n").intValue();
        List<Integer> eventList = new ArrayList();
        
        if(parser.getOption("-events").stringValue().isEmpty()) {
            System.out.println("\n >>>> error: no event is specified....\n");
            System.exit(0);
        }
        else if(parser.getOption("-events").stringValue().contains(",")){
            String[] eventNumbers = parser.getOption("-events").stringValue().split(",");
            for(String eventNum : eventNumbers)
                eventList.add(Integer.valueOf(eventNum));
        }
        else if(parser.getOption("-events").stringValue().contains("-")){
            String[] eventNumbers = parser.getOption("-events").stringValue().split("-");            
            if(eventNumbers.length != 2) {
               System.out.println("\n >>>> error: wrong assignment for sequence event list ....\n");
               System.exit(0);
            }
            else{
                int firstEvent = Integer.parseInt(eventNumbers[0]);
                int lastEvent = Integer.parseInt(eventNumbers[1]);
                for(int i = firstEvent; i <=lastEvent; i++){
                    eventList.add(i);
                }
            }
        }

        List<String> inputList = parser.getInputList();
        if (inputList.isEmpty() == true) {
            parser.printUsage();
            System.out.println("\n >>>> error: no input file is specified....\n");
            System.exit(0);
        }
        
        String outputName = "extractedEvents.hipo";
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
        Event event = new Event();   
        
        Reader localReader = new Reader(new Banks(schema));

        ProgressPrintout progress = new ProgressPrintout();
        int counter = 0;
        while(reader.hasNext()){
            reader.nextEvent(event);
            
            RunConfig runConfig = localReader.readRunConfig(event);
            int eventNum = runConfig.event();
            if(eventList.contains(eventNum)) writer.addEvent(event);
            
            progress.updateStatus();
            counter++;
            if ((maxEvents > 0 && counter >= maxEvents) || eventNum == eventList.get(eventList.size()-1)) {
                break;
            }
        }
        progress.showStatus();
        reader.close();
        writer.close();        
    }
}