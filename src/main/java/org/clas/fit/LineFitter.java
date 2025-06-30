package org.clas.fit;

import java.util.ArrayList;
import java.util.List;
import trackfitter.fitter.utilities.ProbChi2perNDF;

/**
 * A least square fitting method For a linear fit,f(a,b)=a+bx taking y errors
 * into account
 */
public class LineFitter {

    private double _slope;      // line slope
    private double _interc;     // line y-intercept
    private double _slopeErr;   // error on the slope
    private double _intercErr;  // error in the intercept
    private double _SlIntCov;   // covariance matrix off-diagonal term
    private double _chi2;        // fit chi^2 
    private double[] _pointchi2; // fit chi^2 for each point
    private int _ndf;

    // the constructor
    public LineFitter() {
    }
    private final List<Double> w = new ArrayList<Double>();
    // fit status

    public boolean fitStatus(List<Double> x, List<Double> y, List<Double> sigma_x, List<Double> sigma_y, int nbpoints) {
        boolean fitStat = false;

        if (nbpoints >= 2) {  // must have enough points to do the fit
            // now do the fit
            // initialize weight-sum and moments
            double Sw, Sx, Sy, Sxx, Sxy;
            Sw = Sx = Sy = Sxx = Sxy = 0.;

            w.clear();
            ((ArrayList<Double>) w).ensureCapacity(nbpoints);

            for (int i = 0; i < nbpoints; i++) {
                if ((sigma_y.get(i) * sigma_y.get(i) + sigma_x.get(i) * sigma_x.get(i)) == 0) {
                    return false;
                }
                w.add(i, 1. / (sigma_y.get(i) * sigma_y.get(i) + sigma_x.get(i) * sigma_x.get(i)));
                //w.get(i) = 1./(sigma_x.get(i)*sigma_x.get(i)); 
                Sw += w.get(i);
                // the moments
                Sx += x.get(i) * w.get(i);
                Sy += y.get(i) * w.get(i);
                Sxy += x.get(i) * y.get(i) * w.get(i);
                Sxx += x.get(i) * x.get(i) * w.get(i);
            }
            // the determinant
            double determ = Sw * Sxx - Sx * Sx;  // the determinant; must be >0

            if (determ < 1e-19) {
                determ = 1e-19; //straight track approximation
            }
            _slope = (Sw * Sxy - Sx * Sy) / determ;
            _interc = (Sy * Sxx - Sx * Sxy) / determ;
            // the errors on these parameters
            _slopeErr = Math.sqrt(Sw / determ);
            _intercErr = Math.sqrt(Sxx / determ);
            _SlIntCov = -Sx / determ;

            if (Math.abs(_slope) >= 0 && Math.abs(_interc) >= 0) {

                // calculate the chi^2
                _pointchi2 = new double[nbpoints];
                for (int j = 0; j < nbpoints; j++) {
                    _chi2 += ((y.get(j) - (_slope * x.get(j) + _interc)) * (y.get(j) - (_slope * x.get(j) + _interc))) * w.get(j);
                    _pointchi2[j] = ((y.get(j) - (_slope * x.get(j) + _interc)) * (y.get(j) - (_slope * x.get(j) + _interc))) * w.get(j);
                }
                // the number of degrees of freedom
                _ndf = nbpoints - 2;

                fitStat = true;
            }
        }

        // if there is a fit return true
        return fitStat;
    }

    public double slope() {
        return _slope;
    }

    public double slopeErr() {
        return _slopeErr;
    }

    public double intercept() {
        return _interc;
    }

    public double interceptErr() {
        return _intercErr;
    }

    public double SlopeIntercCov() {
        return _SlIntCov;
    }

    public double chisq() {
        return _chi2;
    }

    public int NDF() {
        return _ndf;
    }

    public double[] get_pointchi2() {
        return _pointchi2;
    }

    public double getProb() {
        double chi2 = this.chisq();
        int ndf = this.NDF();
        return ProbChi2perNDF.prob(chi2, ndf);
    }

}
