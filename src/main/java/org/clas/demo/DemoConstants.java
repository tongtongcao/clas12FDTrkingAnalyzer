package org.clas.demo;

/**
 *
 * @author Tongtong
 */

public class DemoConstants{
    public static final int MARKERSIZE = 3;
    public static final int MARKERSTYLE = 1;
    public static final int NORMALHITMARKERSTYLE = 2;
    public static final int BGHITMARKERSTYLE = 3;
    public static final int URWELLMARKERSTYLE = 1;
        
    public static enum MarkerColor {
        TDC         (  1),  // Raw hits
        DENOISING ( 2),     // Remained hits after SRN, AI-denoising, timing cuts
        CLUSTERING       ( 3), // Hits on clusters by clustering
        AICAND       ( 9), // Hits on clusters in AI candidates
        HBTRK       ( 4), // Hits on clusters in HB tracks
        TBTRK      ( 5); // Hits on clusters in TB tracks
        private final int markerColor;
        private MarkerColor(int markerColor){ this.markerColor = markerColor; }
        public int getMarkerColor() { return markerColor;}    
    }
}  