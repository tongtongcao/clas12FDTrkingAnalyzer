package org.clas.element;

import java.util.List;
import java.util.ArrayList; 

import org.jlab.clas.physics.LorentzVector;
import org.jlab.clas.physics.Particle;
import org.jlab.clas.physics.Vector3;
import org.jlab.detector.base.DetectorType;
import org.jlab.geom.prim.Point3D;

import org.clas.utilities.Constants;

/**
 *
 * @author Tongtong
 */

public class Track implements Comparable<Track> {
    
    // ConvHB(11) or ConvTB(12) or AIHB(21) or AITB(22)
    private int trackType = 22;
    
    private int id = -1;
    private int trackIndex = 0;
    // from tracking bank
    private LorentzVector trackVector = new LorentzVector(0.0,0.0,0.0,0);
    private Vector3 trackVertex = new Vector3(0.0,0.0,0.0);
    private int trackCharge = 0;
    private int trackSector = 0;
    private double trackPolarity = 0;
    private double trackChi2 = 0;
    private int trackNDF = 0;
    private Vector3[] trackCrosses = new Vector3[3];
    private int[] clusterIds = new int[6];
    private int[] crossIds = new int[6];    
    private int uRWellCrossIds[] = new int[2];
    private int   trackSL = 0;
    
    private Point3D preC1Pos;
    private Point3D preC1Dir;
    
    // from particle bank
    private int    trackStatus  = 0;
    private double trackChi2pid = 0;
    private int    trackPid = 0;
    
    // from trajectory bank
    private Vector3[] trackTrajectory = new Vector3[9]; // size set to contain 3 DC regions, 3 FTOF and ECAL layers
    private double[]  trackEdge       = new double[9];  // size set to contain 3 DC regions, 3 FTOF and ECAL layers
    private boolean inFiducial = true;
    
    private List<URWellCross> uRWellCrosses = null;
    private boolean trackMatch   = false;
    private boolean trackPredict = false;  
    private List<Cross> crosses = null;
    private List<Cluster> clusters = null;
    private List<Hit> hits = null;
    private List<Hit> normalHits = null;
    private List<Hit> bgHits = null;
    private int numClusters = -1;
    private int numHits = -1;
    private int numNormalHits = -1;
    private int numBgHits = -1;
    private double ratioNormalHits = -1;
    
    private Point3D uRWellProjectionGlobalR1 = null;
    private Point3D uRWellProjectionLocalR1 = null;
    private Point3D uRWellProjectionGlobalR2 = null;
    private Point3D uRWellProjectionLocalR2 = null;    
    
    public Track() {
        this.initTrack(1, -1, 0, 0., 0., 0., 0., 0., 0.);
    }

    public Track(Track t) {
        this.initTrack(t.type(), t.id(), t.charge(), t.px(), t.py(), t.pz(), t.vertex().x(), t.vertex().y(), t.vertex().z());
    }
    
    public Track(int type, int id, int charge, double px, double py, double pz, double vx, double vy, double vz) {
        this.initTrack(type, id, charge, px, py, pz, vx, vy, vz);
    }
            
    public static Track copyFrom(Track p){
        Track newp = new Track();
        newp.charge(p.charge());
        newp.vector().copy(p.vector());
        newp.vertex().copy(p.vertex());
        newp.type(p.type());
        return newp;
    }
    
    public void copy(Track track) {
        this.trackVector.setPxPyPzM(track.vector().px(), track.vector().py(), track.vector().pz(), track.vector().mass());
        this.trackVertex.setXYZ(track.vertex().x(), track.vertex().y(), track.vertex().z());        
        this.trackCharge = track.charge(); 
        this.id = track.id();
        this.trackType = track.type();
    }
    
    public void reset(){
        this.trackType= 22;
        this.trackCharge = 0;
        this.trackStatus = 0;
        this.trackChi2 = 0;
        this.trackNDF = 0;
        this.vector().setPxPyPzE(0.0, 0.0, 0.0, 0.0);
    }
    
    public final void initTrack(int type, int id, int charge, double px, double py, double pz, double vx, double vy, double vz) {
        this.trackType   = type;
        this.id = id;
        this.trackCharge = charge;
        this.trackVector.setPxPyPzM(px, py, pz, 0);
        this.trackVertex.setXYZ(vx, vy, vz);
        for(int i=0; i<this.trackTrajectory.length; i++) {
            this.trackTrajectory[i] = new Vector3(0,0,0);
        }
        for(int i=0; i<this.trackCrosses.length; i++) {
            this.trackCrosses[i] = new Vector3(0,0,0);
        }
    }
    
    public int id() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }

    public int index() {
        return trackIndex;
    }

    public void index(int trackIndex) {
        this.trackIndex = trackIndex;
    }

    public int type() {
        return trackType;
    }
    
    public void type(int m) {
        this.trackType=m;
    }
    
    public void setP(double mom) {
        double mag = this.vector().p();
        double factor = mom / mag;
        this.vector().setPxPyPzM(this.vector().vect().x() * factor, this.vector().vect().y() * factor, this.vector().vect().z() * factor, 0);
    }
    
    public void setTheta(double theta) {
        this.vector().vect().setMagThetaPhi(this.vector().p(), theta, this.vector().phi());
    }
    
    
    public void setVector(int charge, double px, double py, double pz, double vx, double vy, double vz) {
        trackVector.setPxPyPzM(px, py, pz, 0);
        trackVertex.setXYZ(vx, vy, vz);
        trackCharge = charge;
        
    }
    
    public double px() {
        return this.vector().px();
    }
    
    public double py() {
        return this.vector().py();
    }
    
    public double pz() {
        return this.vector().pz();
    }
    
    public double p() {
        return this.vector().p();
    }
    
    public double theta() {
        return this.vector().theta();
    }
    
    public double phi() {
        return this.vector().phi();
    }
    
    public double e() {
        return this.vector().e();
    }
    
    public double vx() {
        return this.trackVertex.x();
    }
    
    public double vy() {
        return this.trackVertex.y();
    }
    
    public double vz() {
        return this.trackVertex.z();
    }
    
    public int sector() {
        return trackSector;
    }

    public void sector(int sec) {
        this.trackSector = sec;
    }

    public double x(int detector, int layer) {
	int i = this.trajIndex(detector, layer);
        return this.trackTrajectory[i].x();
    }

    public double y(int detector, int layer) {
        int i = this.trajIndex(detector, layer);
	return this.trackTrajectory[i].y();
    }

    public double z(int detector, int layer) {
        int i = this.trajIndex(detector, layer);
	return this.trackTrajectory[i].z();
    }

    public void setVector(int charge, Vector3 nvect, Vector3 nvert) {
        trackVector.setVectM(nvect, 0);
        trackVertex.setXYZ(nvert.x(), nvert.y(), nvert.z());
        trackCharge = charge;
        
    }
    
    public double euclideanDistance(Track part) {
        double xx = (this.vector().px() - part.vector().px());
        double yy = (this.vector().py() - part.vector().py());
        double zz = (this.vector().pz() - part.vector().pz());
        return Math.sqrt(xx * xx + yy * yy + zz * zz);
    }
    
    public double cosTheta(Track part) {
        if (part.vector().p() == 0 || this.vector().p() == 0)
            return -1;
        return part.vector().vect().dot(trackVector.vect()) / (part.vector().vect().mag() * trackVector.vect().mag());
    }
    
    
    public void setVector(LorentzVector nvec, Vector3 nvert) {
        trackVector = nvec;
        trackVertex = nvert;
    }

    public int charge() {
        return (int) trackCharge;
    }
    
    public void charge(int charge){
        this.trackCharge = charge;
    }

    public double chi2() {
        return trackChi2;
    }

    public void chi2(double trackChi2) {
        this.trackChi2 = trackChi2;
    }

    public int NDF() {
        return trackNDF;
    }

    public void NDF(int trackNDF) {
        this.trackNDF = trackNDF;
    }
    
    public int[] uRWellCrossIds(){
        return this.uRWellCrossIds;
    }
    
    public void uRWellCrossIds(int uRWellCross1Id, int uRWellCross2Id){
        this.uRWellCrossIds[0] = uRWellCross1Id;
        this.uRWellCrossIds[1] = uRWellCross2Id;
    }
    
    public void setURWellCrosses(List<URWellCross> uRWellCrosses){
        this.uRWellCrosses = new ArrayList();
        this.uRWellCrosses.addAll(uRWellCrosses);
        
    }
    
    public List<URWellCross> getURWellCrosses(){
        return this.uRWellCrosses;
    }
    
    public int[] crossIds() {
        return this.crossIds;
    }
    
    public void crosses(int i1, int i2, int i3) {
        this.crossIds[0] = i1;
        this.crossIds[1] = i2;
        this.crossIds[2] = i3;
    }

    public void setCrosses(List<Cross> allCrosses){
        crosses = new ArrayList();
        for(int i = 0; i < 3; i++){
            int crossId = crossIds[i];
            if(crossId > 0){
                for(Cross crs : allCrosses){
                    if(crossId == crs.id()) {
                        crosses.add(crs);
                        break;
                    }
                }
            }
        }
    }

    public int[] clusterIds() {
        return this.clusterIds;
    }
    
    public void clusters(int i1, int i2, int i3, int i4, int i5, int i6) {
        this.clusterIds[0] = i1;
        this.clusterIds[1] = i2;
        this.clusterIds[2] = i3;
        this.clusterIds[3] = i4;
        this.clusterIds[4] = i5;
        this.clusterIds[5] = i6;
        for(int i=0; i<6; i++) {
            if(this.clusterIds[i]<=0) this.clusterIds[i]=-1; //change 0 to -1 to allow matching of candidates to tracks
            if(this.clusterIds[i]>0)  this.trackSL++;
        }
        numClusters = this.trackSL;
    }     
    
    public void setClusters(ArrayList<Cluster> allClusters){
        clusters = new ArrayList();
        for(int i = 0; i < 6; i++){
            int clusterId = clusterIds[i];
            if(clusterId > 0){
                for(Cluster cls : allClusters){
                    if(clusterId == cls.id()) {
                        clusters.add(cls);
                        break;
                    }
                }
            }
        }
    }    
    
    public void setHits(List<Hit> allHits){
        hits = new ArrayList();
        for(int i = 0; i < 6; i++){
            int clusterId = clusterIds[i];
            if(clusterId > 0){
                for(Hit hit: allHits){
                    if(hit.ClusterID() == clusterId) hits.add(hit);
                }
            }
        }
        numHits = hits.size();
        separateNormalBgHits();
    }
    
    public void setHitsClusters(List<Cluster> allClusters, List<Hit> allHits){
        clusters = new ArrayList();
        for(int i = 0; i < 6; i++){
            int clusterId = clusterIds[i];
            if(clusterId > 0){
                for(Cluster cls : allClusters){
                    if(clusterId == cls.id()) {
                        clusters.add(cls);
                        break;
                    }
                }
            }
        }
        
        hits = new ArrayList();
        for(int i = 0; i < 6; i++){
            int clusterId = clusterIds[i];
            if(clusterId > 0){
                for(Hit hit: allHits){
                    if(hit.ClusterID() == clusterId) hits.add(hit);
                }
            }
        }
        numHits = hits.size();
        separateNormalBgHits();
    }
        
    public void setHitsClusters(List<Cluster> allClusters){
        clusters = new ArrayList();
        hits = new ArrayList();
        for(int i = 0; i < 6; i++){
            int clusterId = clusterIds[i];
            if(clusterId > 0){
                for(Cluster cls : allClusters){
                    if(clusterId == cls.id()) {
                        clusters.add(cls);
                        if(cls.getHits() != null) hits.addAll(cls.getHits());
                        break;
                    }
                }
            }
        } 
        numHits = hits.size();
        separateNormalBgHits();
    } 
    
    public List<Cross> getCrosses(){
        return crosses;
    } 
    
    
    public List<Cluster> getClusters(){
        return clusters;
    } 
    
    public int getNumClusters(){
        return numClusters;
    }
    
    public List<Hit> getHits(){
        return hits;
    }

    public int getNumHits(){
        return numHits;
    }
    
    public int SL() {
        return trackSL;
    }

    private boolean hasSL(){
       if(Constants.NSUPERLAYERS==0) return true;
       else return (this.SL()==Constants.NSUPERLAYERS);
    }
    
    public void polarity(double polarity){
	this.trackPolarity = polarity;
    }

    public double polarity(){
	return this.trackPolarity;
    }

    public void status(int status){
        this.trackStatus = status;
    }
    
    public int status(){
        return this.trackStatus;
    }
    
    public void chi2pid(double chi2pid) {
        this.trackChi2pid = chi2pid;
    }
    
    public double chi2pid() {
        return this.trackChi2pid;
    }
    
    public void pid(int pid) {
        this.trackPid = pid;
    }
    
    public int pid() {
        return this.trackPid;
    }
    
    public void preC1(double x, double y, double z, double ux, double uy, double uz){
        preC1Pos = new Point3D(x, y, z);
        preC1Dir = new Point3D(ux, uy, uz);
    }
    
    public Point3D getPreC1Pos(){
        return preC1Pos;
    }
    
    public Point3D getPreC1Dir(){
        return preC1Dir;
    }
    
    public void cross(double x, double y, double z, int region) {
        this.trackCrosses[region-1] = new Vector3(x, y, z);
    }
    
    public Vector3 cross(int region) {
        return this.trackCrosses[region-1];
    }
    
    public void trajectory(double x, double y, double z, int detector, int layer) {
        this.trajectory(x, y, z, 100, detector, layer);
    }
    
    public void trajectory(double x, double y, double z, double edge, int detector, int layer) {
        int i = this.trajIndex(detector, layer);
        if(i>=0) {
            this.trackTrajectory[i] = new Vector3(x, y, z);
            this.trackEdge[i] = edge;
        }
    }
    
    public Vector3 trajectory(int detector, int layer) {
        int i = this.trajIndex(detector, layer);
        if(i>=0) return this.trackTrajectory[i];
        else     return null;
    }
    
    public int trajIndex(int detector, int layer) {
        int i=-1;
        if(detector == DetectorType.DC.getDetectorId()) {
            i = ((int) (layer-1)/12);
        }
        else if(detector == DetectorType.FTOF.getDetectorId()) {
            i = layer-1 + 3;
        }
        else if(detector == DetectorType.ECAL.getDetectorId()) {
            i = ((int) (layer-1)/3) + 6;
        }
        return i;
    }

    public boolean isInDetector() {
        if(this.minWire()<Constants.WIREMIN) 
            return false;
        // check DC
        for(int i=0; i<3; i++) {
            if(this.trackEdge[i]<Constants.getEdge(DetectorType.DC)) 
                return false;
        }
        // check FTOF
        if(this.trackEdge[3]<Constants.getEdge(DetectorType.FTOF) 
        && this.trackEdge[4]<Constants.getEdge(DetectorType.FTOF) 
//        && this.trackEdge[5]<Constants.getEdge(DetectorType.FTOF)
          )
            return false;
        // check ECAL for electrons
        if(this.pid()==11) {
           for(int i=6; i<9; i++) {
            if(this.trackEdge[i]<Constants.getEdge(DetectorType.ECAL)) 
                return false;
            } 
        }
        return true;
    }
    
    public boolean isInFiducial() {
        return inFiducial;
    }

    public void isInFiducial(boolean inFiducial) {
        this.inFiducial = inFiducial;
    }
    
    public void setMatch(boolean match) {
        this.trackMatch = match;
    }
    
    public boolean isMatched() {
        return trackMatch;
    }
    
    public void setPrediction(boolean predict) {
        this.trackPredict = predict;
    }
    
    public boolean isPredicted() {
        return trackPredict;
    }
    
    public final LorentzVector vector() {
        return trackVector;
    }
    
    public final Vector3 vertex() {
        return trackVertex;
    }
    
    public double get(String pname) {
        if (pname.compareTo("theta") == 0)
            return trackVector.theta();
        if (pname.compareTo("phi") == 0)
            return trackVector.phi();
        if (pname.compareTo("p") == 0)
            return trackVector.p();
        if (pname.compareTo("mom") == 0)
            return trackVector.p();
        if (pname.compareTo("px") == 0)
            return trackVector.px();
        if (pname.compareTo("py") == 0)
            return trackVector.py();
        if (pname.compareTo("pz") == 0)
            return trackVector.pz();
        if (pname.compareTo("vx") == 0)
            return trackVertex.x();
        if (pname.compareTo("vy") == 0)
            return trackVertex.y();
        if (pname.compareTo("vz") == 0)
            return trackVertex.z();
        if (pname.compareTo("vertx") == 0)
            return trackVertex.x();
        if (pname.compareTo("verty") == 0)
            return trackVertex.y();
        if (pname.compareTo("vertz") == 0)
            return trackVertex.z();
        
        System.out.println("[Track::get] ERROR ----> variable " + pname + "  is not defined");
        return 0.0;
    }

    public boolean isP(double mean, double sigma){
        return (Math.abs(trackVector.p()-mean) < sigma);
    }
    
    public boolean isThetaDeg(double mean, double sigma){
        return (Math.abs(Math.toDegrees(trackVector.theta())-mean) < sigma);
    }
    
    public boolean isTheta(double mean, double sigma){
        return (Math.abs(trackVector.theta()-mean) < sigma);
    }
    
    public boolean isPhi(double mean, double sigma){
        return (Math.abs(trackVector.phi()-mean) < sigma);
    }
    public boolean isPhiDeg(double mean, double sigma){
        return (Math.abs(Math.toDegrees(trackVector.phi())-mean) < sigma);
    }
    
    public boolean isinbending() {

        if(this.polarity() < 0) {
            return true;
	}
        else {
            return false;
        }
    }

    public boolean isValid() {
        return this.isValid(true);
    }

    public boolean isValid(boolean zcut) {
        boolean value = false;
        if((this.vz()>Constants.ZMIN && this.vz()<Constants.ZMAX || !zcut)
        && this.p()>Constants.PMIN
        && this.chi2()/this.NDF()<Constants.CHI2MAX 
        && Math.abs(this.chi2pid())<3
        && this.isInFiducial()
        && this.isInDetector()
        && (Constants.SECTOR==0 || this.sector()==Constants.SECTOR)
        && this.hasSL()
        ) value=true;
        return value;
    }

     public boolean isForLumiScan() {
        boolean value = false;
        if(this.pid()==11 && this.status()<0) value = this.p()>2.5 && this.p()<5.2;
        else                                  value = this.p()>Constants.PMIN
                                                   && Math.abs(this.chi2pid())<3
                                                   && this.theta()<Math.toRadians(40.);
        value = value && this.vz()>Constants.ZMIN && this.vz()<Constants.ZMAX
                      && this.isInDetector();
        return value;
    }

    public int matchedClusters(Track t) {
        int nmatch = 0;
        for(int i=0; i<6; i++) {
            if(this.clusterIds()[i]==t.clusterIds()[i] && this.clusterIds()[i]!=0) nmatch++;            
        }
        return nmatch;
    }

    public int nClusters() {
        int nclus = 0;
        for(int i=0; i<6; i++) {
            if(this.clusterIds()[i]>=0) nclus++;            
        }
        return nclus;
    }
        
    public int minWire() {
        int miniWire = 112;
        
        if(this.hits != null) {            
            for(Hit hit : hits){
                if(hit.wire() < miniWire) miniWire = hit.wire();                    
            }                        
        }
        
        return miniWire;
    }
    
    // Match: same sector, superlayer, layer and wire
    public int matchedHits(Track t) {
        int nmatch = 0;
        if(this.getHits() != null && t.getHits() != null){
            for(Hit thisHit : this.getHits()){
                for(Hit hit : t.getHits()){
                    if(thisHit.sector() == hit.sector() && thisHit.superlayer() == hit.superlayer() 
                            && thisHit.layer() == hit.layer() && thisHit.wire() == hit.wire()) {
                        nmatch++;
                        break;
                    }
                }
            }
        }
        return nmatch;
    }
    
    // Match: same sector, superlayer, layer, wire and TDC
    public int matchedWithTDCHits(Track t) {
        int nmatch = 0;
        if(this.getHits() != null && t.getHits() != null){
            for(Hit thisHit : this.getHits()){
                for(Hit hit : t.getHits()){
                    if(thisHit.sector() == hit.sector() && thisHit.superlayer() == hit.superlayer() 
                            && thisHit.layer() == hit.layer() && thisHit.wire() == hit.wire() && thisHit.TDC() == hit.TDC()) {
                        nmatch++;
                        break;
                    }
                }
            }
        }
        return nmatch;
    }
    
    public int nHits() {
        if(this.hits != null) return hits.size();
        else return -1;    
    }
    
    public boolean diff(Track t) {
        if(Math.abs(this.p()-t.p())>0.001) return true;
        if(Math.abs(this.theta()-t.theta())>0.01) return true;
        if(Math.abs(this.phi()-t.phi())>0.05) return true;
        return false;
    }
    
    public Particle particle() {
        if(this.pid()!=0)
            return new Particle(this.pid(), this.px(), this.py(), this.pz(), this.vx(), this.vy(), this.vz());
        else
            return null;
    }
                      
    public boolean equals(Track o) {
        if(Constants.HITMATCH)
            return this.matchedHits(o)>0.6*this.nHits();
        else
            return this.matchedClusters(o)==6;
    }
    
    public boolean isContainedIn(Track o) {
        boolean value = true;
        if(Constants.HITMATCH) {
            if(this.matchedHits(o)<o.nHits()) value=false;
        }
        else {
            for(int i=0; i<6; i++) {
                if(this.clusterIds()[i]!=-1 && this.clusterIds()[i]!=o.clusterIds()[i]) value=false;           
            }
        }
        return value;
    }
    
    public boolean separateNormalBgHits(){
        if(hits == null) return false;
        else {
            normalHits = new ArrayList();
            bgHits = new ArrayList();
            for(Hit hit : hits){
                if(hit.isNormalHit())
                    normalHits.add(hit);                
                else bgHits.add(hit);
            }
            
            numNormalHits = normalHits.size();
            numBgHits = bgHits.size();
            ratioNormalHits = (double) numNormalHits/hits.size();
            return true;
        }
    }
    
    public List<Hit> getNormalHits(){
        return normalHits;
    }
    
    public List<Hit> getBgHits(){
        return bgHits;
    }
    
    public int getNumNormalHits(){
        return numNormalHits;
    } 
    
    public int getNumBgHits(){
        return numBgHits;
    }
    
    public double getRatioNormalHits(){
        return ratioNormalHits;
    }
    
    public void setURWellProjectionR1(double xGlobal, double yGlobal, double zGlobal){
        uRWellProjectionGlobalR1 = new Point3D(xGlobal, yGlobal, zGlobal);
        uRWellProjectionLocalR1 = getCoordsInLocal(xGlobal, yGlobal, zGlobal);
    }
    
    public Point3D getURWellProjectionGlobalR1(){
        return uRWellProjectionGlobalR1;
    }
    
    public Point3D getURWellProjectionLocalR1(){
        return uRWellProjectionLocalR1;
    }
    
    public void setURWellProjectionR2(double xGlobal, double yGlobal, double zGlobal){
        uRWellProjectionGlobalR2 = new Point3D(xGlobal, yGlobal, zGlobal);
        uRWellProjectionLocalR2 = getCoordsInLocal(xGlobal, yGlobal, zGlobal);
    }
    
    public Point3D getURWellProjectionGlobalR2(){
        return uRWellProjectionGlobalR2;
    }
    
    public Point3D getURWellProjectionLocalR2(){
        return uRWellProjectionLocalR2;
    }    
    

    /**
     *
     * @param X
     * @param Y
     * @param Z
     * @return rotated coords from tilted sector coordinate system to the lab
     * frame
     */
    public Point3D getCoordsInLocal(double X, double Y, double Z) {
                        
        double rx = X * Constants.COSSECTOR60[this.sector() - 1] + Y * Constants.SINSECTOR60[this.sector() - 1];
        double ry = -X * Constants.SINSECTOR60[this.sector() - 1] + Y * Constants.COSSECTOR60[this.sector() - 1];
        
        double rrz = rx * Constants.SIN25 + Z * Constants.COS25;
        double rrx = rx * Constants.COS25 - Z * Constants.SIN25;
        
        return new Point3D(rrx, ry, rrz);
    }
    
    public int numMatchedClusters(Track otherTrk){
        if(this.getClusters() == null || otherTrk.getClusters() == null) return -999;
        int matchedClusters = 0;
        for(Cluster thisCls : this.getClusters()){
            for(Cluster otherCls : otherTrk.getClusters()){
                if(thisCls.isFullMatchedCluster(otherCls)){
                    matchedClusters++;
                    break;
                }
            }
        }        
        return matchedClusters;
    }

    public int numMatchedClustersNoRequireTDC(Track otherTrk){
        if(this.getClusters() == null || otherTrk.getClusters() == null) return -999;
        int matchedClusters = 0;
        for(Cluster thisCls : this.getClusters()){
            for(Cluster otherCls : otherTrk.getClusters()){
                if(thisCls.isFullMatchedClusterNoRequireTDC(otherCls)){
                    matchedClusters++;
                    break;
                }
            }
        }        
        return matchedClusters;
    }    
    
    public int numMatchedHits(Track otherTrk){
        if(this.hits == null || otherTrk.getHits() == null) return -999;
        int matchedHits = 0;
        for(Hit hitThisTrk : this.hits){
            for(Hit hitOtherTrk : otherTrk.hits){
                if(hitThisTrk.hitMatched(hitOtherTrk)){
                    matchedHits++;
                    break;
                }
            }
        }
        return matchedHits;
    }
                
    public int numMatchedHitsNoRequireTDC(Track otherTrk){
        if(this.hits == null || otherTrk.getHits() == null) return -999;
        int matchedHits = 0;
        for(Hit hitThisTrk : this.hits){
            for(Hit hitOtherTrk : otherTrk.hits){
                if(hitThisTrk.hitMatchedNoRequireTDC(hitOtherTrk)){
                    matchedHits++;
                    break;
                }
            }
        }
        return matchedHits;
    }     
    
    public void show(){
        System.out.println(this.toString());
    }    

    @Override
    public String  toString(){
        StringBuilder str = new StringBuilder();
        
        str.append(String.format("\tcharge: %4d\n", this.trackCharge));            
        str.append(String.format("\tpx: %9.5f",   this.px()));
        str.append(String.format("\tpy: %9.5f",   this.py()));
        str.append(String.format("\tpz: %9.5f\n", this.pz()));
        str.append(String.format("\tvx: %9.5f",   this.vx()));
        str.append(String.format("\tvy: %9.5f",   this.vy()));
        str.append(String.format("\tvz: %9.5f\n", this.vz()));
        str.append("\t");
        for(int i=0; i<this.clusterIds().length; i++) str.append(String.format("clus%1d:%3d\t", (i+1), this.clusterIds()[i]));
        str.append("\n");
        str.append(String.format("\tchi2: %7.3f",   this.chi2()));
        str.append(String.format("\tNDF:  %4d\n",   this.NDF()));            
        str.append(String.format("\tminWire: %4d",  this.minWire()));            
        str.append(String.format("\tpid: %4d",      this.pid()));            
        str.append(String.format("\tchi2pid: %.1f", this.chi2pid()));            
        str.append(String.format("\tstatus: %4d\n", this.status()));            
        str.append(String.format("\tmatch:  %b\t",  this.isMatched()));            
        str.append(String.format("\tvalid:  %b\n",  this.isValid()));            
        for(int i=0; i<this.trackTrajectory.length; i++) {
            str.append("\t");
            str.append(String.format("traj%1d: ", (i+1)));
            if(this.trackTrajectory[i]!=null) str.append(this.trackTrajectory[i].toString());
            str.append("\t");
            str.append(String.format("edge: %.1f", this.trackEdge[i]));
            str.append("\n");
        }
        return str.toString();
    }    
    

    @Override
    public int compareTo(Track o) {
        return this.index()<o.index() ? -1 : 1;
    }    
}
