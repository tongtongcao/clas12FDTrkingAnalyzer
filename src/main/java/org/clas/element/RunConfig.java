package org.clas.element;

/**
 *
 * @author Tongtong
 */

public class RunConfig {
    private int run;
    private int event;
    private int unixtime;
    private long trigger;
    private long timestamp;  
    private int type;
    private int mode;
    private double torus;
    private double solenoid;
        
    public RunConfig(int run, int event, int unixtime, long trigger, long timestamp, int type, int mode, double torus, double solenoid){
        this.run = run;
        this.event = event;
        this.unixtime = unixtime;
        this.trigger = trigger;  
        this.timestamp = timestamp;
        this.type = type;
        this.mode = mode;
        this.torus = torus;
        this.solenoid = solenoid;
    }
    
    public int run(){
        return run;
    }
    
    public int event(){
        return event;
    }

    public int unixtime(){
        return unixtime;
    }

    public long trigger(){
        return trigger;
    }

    public long timestamp(){
        return timestamp;
    }
    
    public int type(){
        return type;
    }

    public int mode(){
        return mode;
    }

    public double torus(){
        return torus;
    }    
    

    public double solenoid(){
        return solenoid;
    }  
}