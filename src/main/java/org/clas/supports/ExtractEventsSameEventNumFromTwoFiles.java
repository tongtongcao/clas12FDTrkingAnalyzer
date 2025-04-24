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

public class ExtractEventsSameEventNumFromTwoFiles{            
    public static void main(String[] args) {
        OptionParser parser = new OptionParser("extractEventsSameEventNumFromTwoFiles");
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
        
        String outputName1 = "eventNumMatched1.hipo";
        String outputName2 = "eventNumMatched2.hipo";
        if (!namePrefix.isEmpty()) {
            outputName1 = namePrefix + "_" + outputName1;
            outputName2 = namePrefix + "_" + outputName2;
        }
        
        HipoReader reader1 = new HipoReader();
        reader1.open(inputList.get(0));
        SchemaFactory schema1 = reader1.getSchemaFactory();        
        HipoWriterSorted writer1 = new HipoWriterSorted();
        writer1.getSchemaFactory().copy(schema1);
        writer1.setCompressionType(2);   
        writer1.open(outputName1);                                
        Reader localReader1 = new Reader(new Banks(schema1));
        
        HipoReader reader2 = new HipoReader();
        reader2.open(inputList.get(1));
        SchemaFactory schema2 = reader2.getSchemaFactory();        
        HipoWriterSorted writer2 = new HipoWriterSorted();
        writer2.getSchemaFactory().copy(schema2);
        writer2.setCompressionType(2);   
        writer2.open(outputName2);                                
        Reader localReader2 = new Reader(new Banks(schema2));

        ProgressPrintout progress = new ProgressPrintout();
        Event event1 = new Event(); 
        Event event2 = new Event(); 
        int eventNum1 = -1;
        int eventNum2 = -1;
        
        boolean flagReader1 = true; // if reader next event
        boolean flagReader2 = true; // if reader next event
        int counter = 0;
        while(reader1.hasNext() && reader2.hasNext()){
            if(flagReader1){
                event1 = new Event();   
                reader1.nextEvent(event1);                                    
                eventNum1 = localReader1.readRunConfig(event1).event();
            }
            
            if(flagReader2){
                event2 = new Event();   
                reader2.nextEvent(event2);                                    
                eventNum2 = localReader2.readRunConfig(event2).event();
            }
            
            if(eventNum1 == eventNum2){
                writer1.addEvent(event1);
                writer2.addEvent(event2);
                flagReader1 = true;
                flagReader2 = true;
            }
            else if(eventNum1 > eventNum2){
                while(reader2.hasNext()){
                    reader2.nextEvent(event2);                                    
                    eventNum2 = localReader2.readRunConfig(event2).event();
                    if(eventNum1 == eventNum2){
                        writer1.addEvent(event1);
                        writer2.addEvent(event2);
                        flagReader1 = true;
                        flagReader2 = true;
                        break;
                    }
                    else if(eventNum1 < eventNum2){
                        flagReader1 = true;
                        flagReader2 = false;
                        break;
                    }
                }
            }
            else{
                while(reader1.hasNext()){
                    reader1.nextEvent(event1);                                    
                    eventNum1 = localReader1.readRunConfig(event1).event();
                    if(eventNum1 == eventNum2){
                        writer1.addEvent(event1);
                        writer2.addEvent(event2);
                        flagReader1 = true;
                        flagReader2 = true;
                        break;
                    }
                    else if(eventNum1 > eventNum2){
                        flagReader1 = false;
                        flagReader2 = true;
                        break;
                    }
                } 
            }
            
            progress.updateStatus();
            
            counter++;
            if (counter >= maxEvents) break;
        }
        

        
        progress.showStatus();
        reader1.close();
        writer1.close(); 
        reader2.close();
        writer2.close();  
    }
}