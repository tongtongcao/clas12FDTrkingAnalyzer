package org.clas.element;

import java.util.ArrayList;
import java.util.List;

import org.jlab.geom.prim.Vector3D;
import org.jlab.geom.prim.Path3D;

import org.clas.fit.ClusterFitLC;
import org.clas.fit.LineFitter;
import org.clas.utilities.Constants;
import org.jlab.geom.prim.Point3D;
import org.clas.utilities.CommonFunctions;
/**
 *
 * @author Tongtong
 */

public class CovMat implements Comparable<CovMat> {
    // ConvTB(12) or AITB(22)
    private int trkType = 22;
    
    private int id;

    private double covXX = -999;
    private double covXY = -999;
    private double covXZ = -999;
    private double covXTheta = -999;
    private double covXPhi = -999;
    private double covXP = -999;
    
    private double covYX = -999;
    private double covYY = -999;
    private double covYZ = -999;
    private double covYTheta = -999;
    private double covYPhi = -999;
    private double covYP = -999;   
    
    private double covZX = -999;
    private double covZY = -999;
    private double covZZ = -999;
    private double covZTheta = -999;
    private double covZPhi = -999;
    private double covZP = -999;   

    private double covThetaX = -999;
    private double covThetaY = -999;
    private double covThetaZ = -999;
    private double covThetaTheta = -999;
    private double covThetaPhi = -999;
    private double covThetaP = -999;  

    private double covPhiX = -999;
    private double covPhiY = -999;
    private double covPhiZ = -999;
    private double covPhiTheta = -999;
    private double covPhiPhi = -999;
    private double covPhiP = -999;  

    private double covPX = -999;
    private double covPY = -999;
    private double covPZ = -999;
    private double covPTheta = -999;
    private double covPPhi = -999;
    private double covPP = -999; 
    
    private double[][] covMat;
    
    
            
    public CovMat(int trkType, int id, 
            double covXX, double covXY, double covXZ, double covXTheta, double covXPhi, double covXP,
            double covYX, double covYY, double covYZ, double covYTheta, double covYPhi, double covYP,
            double covZX, double covZY, double covZZ, double covZTheta, double covZPhi, double covZP,
            double covThetaX, double covThetaY, double covThetaZ, double covThetaTheta, double covThetaPhi, double covThetaP,
            double covPhiX, double covPhiY, double covPhiZ, double covPhiTheta, double covPhiPhi, double covPhiP,
            double covPX, double covPY, double covPZ, double covPTheta, double covPPhi, double covPP){
        
        this.trkType = trkType;
        this.id = id;
        
        this.covXX = covXX;
        this.covXY = covXY;
        this.covXZ = covXZ;
        this.covXTheta = covXTheta;
        this.covXPhi = covXPhi;
        this.covXP = covXP;
        
        this.covYX = covYX;
        this.covYY = covYY;
        this.covYZ = covYZ;
        this.covYTheta = covYTheta;
        this.covYPhi = covYPhi;
        this.covYP = covYP;
        
        this.covZX = covZX;
        this.covZY = covZY;
        this.covZZ = covZZ;
        this.covZTheta = covZTheta;
        this.covZPhi = covZPhi;
        this.covZP = covZP;

        this.covThetaX = covThetaX;
        this.covThetaY = covThetaY;
        this.covThetaZ = covThetaZ;
        this.covThetaTheta = covThetaTheta;
        this.covThetaPhi = covThetaPhi;
        this.covThetaP = covThetaP;

        this.covPhiX = covPhiX;
        this.covPhiY = covPhiY;
        this.covPhiZ = covPhiZ;
        this.covPhiTheta = covPhiTheta;
        this.covPhiPhi = covPhiPhi;
        this.covXPhi = covPhiP;

        this.covPX = covPX;
        this.covPY = covPY;
        this.covPZ = covPZ;
        this.covPTheta = covPTheta;
        this.covPPhi = covPPhi;
        this.covPP = covPP;   
        
        double[][] mat = {
                {covXX, covXY, covXZ, covXTheta, covXPhi, covXP},
                {covYX, covYY, covYZ, covYTheta, covYPhi, covYP},
                {covZX, covZY, covZZ, covZTheta, covZPhi, covZP},
                {covThetaX, covThetaY, covThetaZ, covThetaTheta, covThetaPhi, covThetaP},
                {covPhiX, covPhiY, covPhiZ, covPhiTheta, covPhiPhi, covPhiP},
                {covPX, covPY, covPZ, covPTheta, covPPhi, covPP}
            };
        
        this.covMat = mat;
    }
    
    public int trkType(){
        return trkType;
    }    
    
    public int id(){
        return id;
    }
    
    public double covXX(){
        return covXX;
    }
    
    public double covXY(){
        return covXY;
    }

    public double covXZ(){
        return covXZ;
    }

    public double covXTheta(){
        return covXTheta;
    }

    public double covXPhi(){
        return covXPhi;
    }

    public double covXP(){
        return covXP;
    }

    public double covYX(){
        return covYX;
    }
    
    public double covYY(){
        return covYY;
    }

    public double covYZ(){
        return covYZ;
    }

    public double covYTheta(){
        return covYTheta;
    }

    public double covYPhi(){
        return covYPhi;
    }

    public double covYP(){
        return covYP;
    }

    public double covZX(){
        return covZX;
    }
    
    public double covZY(){
        return covZY;
    }

    public double covZZ(){
        return covZZ;
    }

    public double covZTheta(){
        return covZTheta;
    }

    public double covZPhi(){
        return covZPhi;
    }

    public double covZP(){
        return covZP;
    }
    
    public double covThetaX(){
        return covThetaX;
    }
    
    public double covThetaY(){
        return covThetaY;
    }

    public double covThetaZ(){
        return covThetaZ;
    }

    public double covThetaTheta(){
        return covThetaTheta;
    }

    public double covThetaPhi(){
        return covThetaPhi;
    }

    public double covThetaP(){
        return covThetaP;
    }

    public double covPhiX(){
        return covPhiX;
    }
    
    public double covPhiY(){
        return covPhiY;
    }

    public double covPhiZ(){
        return covPhiZ;
    }

    public double covPhiTheta(){
        return covPhiTheta;
    }

    public double covPhiPhi(){
        return covPhiPhi;
    }

    public double covPhiP(){
        return covPhiP;
    } 
    
    public double covPX(){
        return covPX;
    }
    
    public double covPY(){
        return covPY;
    }

    public double covPZ(){
        return covPZ;
    }

    public double covPTheta(){
        return covPTheta;
    }

    public double covPPhi(){
        return covPPhi;
    }

    public double covPP(){
        return covPP;
    }
    
    public double[][] getCovMat(){
        return covMat;
    }                      
    
    @Override
    public int compareTo(CovMat o) {
        return this.id()<o.id() ? -1 : 1;
    }         
}