package org.clas.element;

/**
 *
 * @author Tongtong
 */

public class RecEvent {
    //ConvHB(11) or ConvTB(12) or AIHB(21) or AITB(22)
    private int type = 22;
    
    private double startTime = -999;
    private double RFTime = -999;
    
            
    public RecEvent(int type, double startTime, double RFTime){
        this.type = type;
        this.startTime = startTime;
        this.RFTime =RFTime;       
    }

    public int type(){
        return type;
    }
    
    public double startTime(){
        return startTime;
    }
    
    public double RFTime(){
        return RFTime;
    }    
            
}