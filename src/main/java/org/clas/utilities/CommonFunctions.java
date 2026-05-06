package org.clas.utilities;

import org.jlab.geom.prim.Point3D;
import org.jlab.clas.physics.Vector3;
import org.clas.utilities.CommonFunctions;

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
    
    public static Point3D getCoordsInLocal(Point3D pointGlobal, int sector) {
        double cosSector = Constants.COSSECTOR60[sector - 1];
        double sinSector = Constants.SINSECTOR60[sector - 1];
        double cosTilt = Constants.COS25;
        double sinTilt = Constants.SIN25;
        
        double rx = pointGlobal.x() * cosSector + pointGlobal.y() * sinSector;
        double ry = -pointGlobal.x() * sinSector + pointGlobal.y() * cosSector;

        double rrz = rx * sinTilt + pointGlobal.z() * cosTilt;
        double rrx = rx * cosTilt - pointGlobal.z() * sinTilt;

        return new Point3D(rrx, ry, rrz);
    }
    
    public static Vector3 getCoordsInLocal(Vector3 vectorGlobal, int sector) {
        double cosSector = Constants.COSSECTOR60[sector - 1];
        double sinSector = Constants.SINSECTOR60[sector - 1];
        double cosTilt = Constants.COS25;
        double sinTilt = Constants.SIN25;
        
        double rx = vectorGlobal.x() * cosSector + vectorGlobal.y() * sinSector;
        double ry = -vectorGlobal.x() * sinSector + vectorGlobal.y() * cosSector;

        double rrz = rx * sinTilt + vectorGlobal.z() * cosTilt;
        double rrx = rx * cosTilt - vectorGlobal.z() * sinTilt;

        return new Vector3(rrx, ry, rrz);
    }    
    
    public static Point3D getCoordsInGlobal(double rrx, double ry, double rrz, int sector) {
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
    
    public static Point3D getCoordsInGlobal(Point3D pointLocal, int sector) {
        double cosSector = Constants.COSSECTOR60[sector - 1];
        double sinSector= Constants.SINSECTOR60[sector - 1];
        double cosTilt = Constants.COS25;
        double sinTilt = Constants.SIN25;

        double rx = pointLocal.x() * cosTilt + pointLocal.z() * sinTilt;
        double Z  = -pointLocal.x() * sinTilt + pointLocal.z() * cosTilt;

        double X = rx * cosSector - pointLocal.y() * sinSector;
        double Y = rx * sinSector + pointLocal.y() * cosSector;

        return new Point3D(X, Y, Z);
    }

    public static Vector3 getCoordsInGlobal(Vector3 vectorLocal, int sector) {
        double cosSector = Constants.COSSECTOR60[sector - 1];
        double sinSector= Constants.SINSECTOR60[sector - 1];
        double cosTilt = Constants.COS25;
        double sinTilt = Constants.SIN25;

        double rx = vectorLocal.x() * cosTilt + vectorLocal.z() * sinTilt;
        double Z  = -vectorLocal.x() * sinTilt + vectorLocal.z() * cosTilt;

        double X = rx * cosSector - vectorLocal.y() * sinSector;
        double Y = rx * sinSector + vectorLocal.y() * cosSector;

        return new Vector3(X, Y, Z);
    }    
    
    public static double[] toSpherical(Point3D p) {
        double x = p.x();
        double y = p.y();
        double z = p.z();

        double r = Math.sqrt(x * x + y * y + z * z);
        double theta = Math.acos(z / r);    
        double phi = Math.atan2(y, x);                 

        return new double[]{r, theta, phi};
    }
    
    public static Vector3 toSpherical(Vector3 p) {
        double x = p.x();
        double y = p.y();
        double z = p.z();

        double r = Math.sqrt(x * x + y * y + z * z);
        double theta = Math.acos(z / r);    
        double phi = Math.atan2(y, x);                 

        return new Vector3(r, theta, phi);
    }
}