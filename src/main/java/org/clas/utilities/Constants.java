package org.clas.utilities;

import org.jlab.detector.base.DetectorType;

/**
 *
 * @author Tongtong
 */
public class Constants {       
    // CONSTATNS for TRANSFORMATION
    public static final double SIN25 = Math.sin(Math.toRadians(25.));
    public static final double COS25 = Math.cos(Math.toRadians(25.));
    public static final double COS30 = Math.cos(Math.toRadians(30.)); 
    public static final double STEREOANGLE = 6.;
    public static final double SIN6 = Math.sin(Math.toRadians(STEREOANGLE));
    public static final double COS6 = Math.cos(Math.toRadians(STEREOANGLE));
    public static final double TAN6 = Math.tan(Math.toRadians(STEREOANGLE));
    public static final double CTAN6 = 1/TAN6;
    public static final double[] SINSECTOR60 = {0, Math.sin(Math.toRadians(60.)), Math.sin(Math.toRadians(120.)), 0, 
        Math.sin(Math.toRadians(240.)), Math.sin(Math.toRadians(300.))};
    public static final double[] COSSECTOR60 = {1, 0.5, -0.5, -1, -0.5, 0.5};
    public static final double[] SINSECTORNEG60 = {0, Math.sin(Math.toRadians(-60.)), Math.sin(Math.toRadians(-120.)), 0, 
        Math.sin(Math.toRadians(-240.)), Math.sin(Math.toRadians(-300.))};
    public static final double[] COSSECTORNEG60 = {1, 0.5, -0.5, -1, -0.5, 0.5};
    
    
    public static int MAXDEMOCASES = 1000;  
        
    public static boolean MC = true;
    public static boolean URWELL = true;
    
    public static double CHI2MAX = Double.POSITIVE_INFINITY;
    public static double ZMIN = -15;
    public static double ZMAX = 5;
    public static double PMIN = 0.5;
    public static double[] EDGE = {-99, -99, -99};
    public static int    WIREMIN = 0;
    public static int    SECTOR = 0;
    
    public static double BEAMENERGY = 10.6;
    public static int    TARGETPID = 2212;
    
    public static int NSUPERLAYERS = 0;
    
    public static boolean HITMATCH = false;
    
    
    public static double getEdge(DetectorType type)            {
        if(null==type)
            return -99;
        else switch (type) {
            case DC:
                return EDGE[0];
            case FTOF:
                return EDGE[1];
            case ECAL:
                return EDGE[2];
            default:
                return -99;
        }
    }
    
    // Tracking type
    public static final int CONVHB = 11;
    public static final int CONVTB = 12;
    public static final int AIHB = 21;
    public static final int AITB = 22;
    
    // Order for normal hits
    public static final int[] NORMALHITORDERS = {0, 20, 40, 50, 60};
    public static final int[] FilterdHITORDERS = {0, 10, 40, 70};
    
    // CalCulate uRWell crosses in LC
    public static double URWELLZTSC[] = {226.0464, 227.3464}; // cm
    public static double INTERVALDCSL1L1L2TSC = 1.15848; // cm
    public static double DCSL1L1ZTSC = 229.27948; // cm
    public static double DCSL1L1W1XTSC = -83.7509153; // cm
    public static double YDCSL1L1W1LC = 1.732051;
    
    public static int URWELLRegions = 12;
}
