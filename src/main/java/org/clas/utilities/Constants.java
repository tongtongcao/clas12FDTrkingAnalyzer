package org.clas.utilities;

import java.io.*;
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
    
    // Orders for normal hits
    public static final int[] NORMALHITORDERS = {0, 20, 40, 50, 60};
    
    // Acceptable orders after decoding
    public static final int[] DECODINGHITORDERS = {0, 10};
    
    // Acceptable orders after denoising
    public static final int[] DENOISINGHITORDERS = {0, 10, 40, 70};
    
    // CalCulate uRWell crosses in LC
    public static double URWELLZTSC[] = {226.0464, 227.3464}; // cm
    public static double INTERVALDCSL1L1L2TSC = 1.15848; // cm
    public static double DCSL1L1ZTSC = 229.27948; // cm
    public static double DCSL1L1W1XTSC = -83.7509153; // cm
    public static double YDCSL1L1W1LC = 1.732051;
    
    public static int URWELLRegions = 12;
    
    // -----------------------------------
    // DC geometry arrays
    // -----------------------------------
    public static final int N_SECTORS = 6;
    public static final int N_SUPERLAYERS = 6;
    public static final int N_LAYERS = 6;
    public static final int N_WIRES = 112;

    public static double[][][][] xm = new double[N_SECTORS][N_SUPERLAYERS][N_LAYERS][N_WIRES];
    public static double[][][][] xr = new double[N_SECTORS][N_SUPERLAYERS][N_LAYERS][N_WIRES];
    public static double[][][][] yr = new double[N_SECTORS][N_SUPERLAYERS][N_LAYERS][N_WIRES];
    public static double[][][][] z  = new double[N_SECTORS][N_SUPERLAYERS][N_LAYERS][N_WIRES];
    
    
    // flag if DC geomery is loaded
    private static boolean geometryLoaded = false;

    /**
     * load geometry if it has not been loaded
     */
    public static void initGeometry() {
        if (geometryLoaded) {
            System.out.println("[Constants] Geometry already loaded.");
            return;
        }
        try {
            InputStream dc_geometry = Constants.class.getResourceAsStream("/org/clas/utilities/dc_geometry.txt");
            if (dc_geometry == null) {
                System.err.println("[Constants] Error: Cannot find dc_geometry.txt in resources folder!");
                System.exit(1);
            }
            loadGeometry(dc_geometry);
            geometryLoaded = true;
        } catch (IOException e) {
            System.err.println("[Constants] Error loading geometry: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
    

    /**
     * Load DC geometry from a CSV-like text file (sector,superlayer,layer,wire,xm,xR,yR,z)
     */
    public static void loadGeometry(InputStream geoStream) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(geoStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                
                String[] parts = line.split(",");
                if (parts.length < 8) continue;

                int sector = Integer.parseInt(parts[0]) - 1;
                int superlayer = Integer.parseInt(parts[1]) - 1;
                int layer = Integer.parseInt(parts[2]) - 1;
                int wire = Integer.parseInt(parts[3]) - 1;

                xm[sector][superlayer][layer][wire] = Double.parseDouble(parts[4]);
                xr[sector][superlayer][layer][wire] = Double.parseDouble(parts[5]);
                yr[sector][superlayer][layer][wire] = Double.parseDouble(parts[6]);
                z [sector][superlayer][layer][wire] = Double.parseDouble(parts[7]);
            }
        }
        System.out.println("[Constants] DC geometry loaded successfully.");
    }
}
