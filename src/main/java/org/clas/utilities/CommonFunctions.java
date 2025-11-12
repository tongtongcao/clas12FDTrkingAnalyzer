package org.clas.utilities;

import org.jlab.geom.prim.Point3D;

import org.clas.utilities.Constants;

/**
 *
 * @author Tongtong
 */
public class CommonFunctions {  
    public static Point3D getCoordsInLocal(double X, double Y, double Z, int sector) {
        double cosSector = Constants.COSSECTOR60[sector - 1];
        double sinSector = Constants.SINSECTOR60[sector - 1];
        double cosTilt = Constants.COS25;
        double sinTilt = Constants.SIN25;
        
        double rx = X * cosSector + Y * sinSector;
        double ry = -X * sinSector + Y * cosSector;

        double rrz = rx * sinTilt + Z * cosTilt;
        double rrx = rx * cosTilt - Z * sinTilt;

        return new Point3D(rrx, ry, rrz);
    }
    
    public Point3D getCoordsInGlobal(double rrx, double ry, double rrz, int sector) {
        double cosSector = Constants.COSSECTOR60[sector - 1];
        double sinSector= Constants.SINSECTOR60[sector - 1];
        double cosTilt = Constants.COS25;
        double sinTilt = Constants.SIN25;

        double rx = rrx * cosTilt + rrz * sinTilt;
        double Z  = -rrx * sinTilt + rrz * cosTilt;

        double X = rx * cosSector - ry * sinSector;
        double Y = rx * sinSector + ry * cosSector;

        return new Point3D(X, Y, Z);
    }

}