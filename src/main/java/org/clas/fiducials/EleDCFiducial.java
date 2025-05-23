package org.clas.fiducials;

import javax.swing.JFrame;
import org.jlab.detector.base.DetectorType;
import org.jlab.groot.data.H2F;
import org.jlab.groot.graphics.EmbeddedCanvas;
import org.jlab.groot.group.DataGroup;
import org.jlab.jnp.hipo4.data.Bank;
import org.jlab.jnp.hipo4.data.Event;
import org.jlab.jnp.hipo4.data.SchemaFactory;
import org.jlab.jnp.hipo4.io.HipoReader;

/**
 *
 * @author devita
 */

public class EleDCFiducial {


// new cut parameters for the linear cut based on x and y coordinates (inbending field):
// replace it in the function: bool DC_fiducial_cut_XY(int j, int region)
// (optimized for electrons, do not use it for hadrons)
//


        double[][][][] maxparams_in = {
            {   {{-14.563,0.60032},{-19.6768,0.58729},{-22.2531,0.544896}},{{-12.7486,0.587631},{-18.8093,0.571584},{-19.077,0.519895}},
                {{-11.3481,0.536385},{-18.8912,0.58099},{-18.8584,0.515956}},{{-10.7248,0.52678},{-18.2058,0.559429},{-22.0058,0.53808}},
                {{-16.9644,0.688637},{-17.1012,0.543961},{-21.3974,0.495489}},{{-13.4454,0.594051},{-19.4173,0.58875},{-22.8771,0.558029}}
            },
            {   {{-6.2928,0.541828},{-16.7759,0.57962},{-32.5232,0.599023}},{{-6.3996,0.543619},{-16.7429,0.578472},{-32.5408,0.600826}},
                {{-5.49712,0.53463},{-16.1294,0.576928},{-32.5171,0.597735}},{{-6.4374,0.54839},{-16.9511,0.582143},{-33.0501,0.59995}},
                {{-5.30128,0.529377},{-16.1229,0.579019},{-30.7768,0.593861}},{{-5.89201,0.541124},{-16.1245,0.575001},{-32.2617,0.601506}}
            },
            {   {{-6.3618,0.546384},{-17.0277,0.582344},{-34.9276,0.612875}},{{-6.36432,0.546268},{-15.8404,0.574102},{-33.0627,0.599142}},
                {{-6.34357,0.548411},{-16.0496,0.575913},{-34.8535,0.610211}},{{-5.8568,0.541784},{-16.1124,0.576473},{-32.8547,0.599033}},
                {{-5.91941,0.536801},{-15.726,0.575211},{-34.0964,0.606777}},{{-5.55498,0.536609},{-15.9853,0.579705},{-33.4886,0.606439}}
            },
            {   {{-12.594,0.613062},{-18.4504,0.588136},{-16.3157,0.529461}},{{-12.3417,0.61231},{-18.1498,0.590748},{-13.8106,0.52335}},
                {{-12.1761,0.609307},{-15.919,0.572156},{-13.0598,0.5194}},{{-12.5467,0.612645},{-16.2129,0.572974},{-12.8611,0.51252}},
                {{-13.0976,0.615928},{-16.9233,0.580972},{-13.0906,0.519738}},{{-12.884,0.622133},{-17.2566,0.585572},{-12.1874,0.510124}}
            },
            {   {{-6.51157,0.545763},{-16.4246,0.583603},{-32.2001,0.60425}},{{-6.21169,0.541872},{-16.8484,0.591172},{-31.7785,0.606234}},
                {{-5.89452,0.54464},{-16.612,0.591506},{-29.9143,0.589656}},{{-6.68908,0.553374},{-16.2993,0.585165},{-30.252,0.59519}},
                {{-6.17185,0.540496},{-16.7197,0.591664},{-31.619,0.608306}},{{-5.7526,0.541761},{-16.2054,0.587326},{-31.3653,0.604081}}
            },
            {   {{-11.8798,0.62389},{-20.2212,0.610786},{-16.4137,0.51337}},{{-12.0817,0.631621},{-20.7511,0.610844},{-16.9407,0.522958}},
                {{-9.72746,0.605471},{-20.4903,0.622337},{-15.3363,0.520589}},{{-12.4566,0.627481},{-20.238,0.606098},{-20.7651,0.56974}},
                {{-11.6712,0.622265},{-18.2649,0.591062},{-19.2569,0.580894}},{{-12.0943,0.630674},{-22.4432,0.633366},{-17.2197,0.537965}}
            }
        };

        double[][][][] minparams_in = {
            {   {{12.2692,-0.583057},{17.6233,-0.605722},{19.7018,-0.518429}},{{12.1191,-0.582662},{16.8692,-0.56719},{20.9153,-0.534871}},
                {{11.4562,-0.53549},{19.3201,-0.590815},{20.1025,-0.511234}},{{13.202,-0.563346},{20.3542,-0.575843},{23.6495,-0.54525}},
                {{12.0907,-0.547413},{17.1319,-0.537551},{17.861,-0.493782}},{{13.2856,-0.594915},{18.5707,-0.597428},{21.6804,-0.552287}}
            },
            {   {{5.35616,-0.531295},{16.9702,-0.583819},{36.3388,-0.612192}},{{6.41665,-0.543249},{17.3455,-0.584322},{37.1294,-0.61791}},
                {{6.86336,-0.550492},{17.2747,-0.575263},{39.6389,-0.625934}},{{6.82938,-0.558897},{17.8618,-0.599931},{39.3376,-0.631517}},
                {{6.05547,-0.54347},{15.7765,-0.569165},{35.6589,-0.611349}},{{6.3468,-0.544882},{16.7144,-0.578363},{38.2501,-0.617055}}
            },
            {   {{6.70668,-0.558853},{17.0627,-0.587751},{36.1194,-0.617417}},{{6.3848,-0.542992},{16.6355,-0.581708},{34.6781,-0.609794}},
                {{6.36802,-0.539521},{15.9829,-0.569165},{32.5691,-0.59588}},{{5.94912,-0.546191},{18.0321,-0.601764},{36.5238,-0.619185}},
                {{5.65108,-0.541684},{15.5009,-0.567131},{34.0489,-0.602048}},{{6.71064,-0.547956},{16.4449,-0.577051},{34.4375,-0.602515}}
            },
            {   {{12.4734,-0.608063},{16.1064,-0.575034},{16.0751,-0.536452}},{{12.1936,-0.6034},{15.9302,-0.571271},{14.2791,-0.520157}},
                {{12.216,-0.600017},{14.8741,-0.56304},{11.1766,-0.498955}},{{12.7941,-0.616044},{17.1516,-0.583616},{11.6077,-0.500028}},
                {{12.7448,-0.611315},{16.2814,-0.572461},{13.1033,-0.506663}},{{12.7949,-0.612051},{16.1565,-0.569143},{12.9295,-0.504203}}
            },
            {   {{7.19022,-0.562083},{16.5946,-0.591266},{31.9033,-0.589167}},{{7.80002,-0.571429},{17.8587,-0.595543},{36.5772,-0.630136}},
                {{7.96121,-0.569485},{17.8085,-0.592936},{37.553,-0.632848}},{{7.52041,-0.566112},{17.3385,-0.603462},{33.7712,-0.606047}},
                {{7.35796,-0.562782},{15.2865,-0.57433},{29.8283,-0.574685}},{{7.80003,-0.571429},{16.1751,-0.583286},{39.1972,-0.642803}}
            },
            {   {{13.4466,-0.633911},{22.0097,-0.62205},{18.8862,-0.519652}},{{13.0534,-0.626648},{20.2994,-0.60581},{19.3973,-0.573994}},
                {{12.547,-0.62145},{18.9322,-0.596491},{16.2331,-0.546036}},{{14.5339,-0.64585},{20.0211,-0.608462},{19.0405,-0.563914}},
                {{12.7388,-0.617954},{21.1677,-0.621012},{15.4502,-0.525165}},{{13.4019,-0.63075},{16.6584,-0.554797},{19.0302,-0.55004}}
            }
        };


        //double maxparams_out[6][6][3][2] =
        double[][][][] maxparams_out = {
            {   {{-9.86221, 0.565985},{-16.4397, 0.569087},{-29.7787, 0.586842}},
                {{-10.2065, 0.565541},{-16.5554, 0.571394},{-28.933, 0.582078}},
                {{-8.48034, 0.550706},{-16.4397, 0.569087},{-27.1037, 0.563767}},
                {{-6.77188, 0.53062},{-16.4397, 0.569087},{-30.485, 0.587534}},
                {{-8.00705, 0.543502},{-16.4038, 0.571178},{-27.7934, 0.573472}},
                {{-10.3328, 0.571942},{-16.69, 0.575252},{-30.8177, 0.592418}}
            },
            {   {{-5.43811, 0.550931},{-17.1906, 0.57936},{-18.552, 0.546789}},
                {{-5.46281, 0.549659},{-18.0351, 0.588876},{-17.6981, 0.549803}},
                {{-3.26087, 0.531677},{-16.3762, 0.578005},{-17.6831, 0.55049}},
                {{-4.5985, 0.542017},{-17.2735, 0.581566},{-16.7013, 0.538853}},
                {{-6.83053, 0.561019},{-16.5082, 0.579816},{-18.0846, 0.553592}},
                {{-5.67358, 0.5558},{-18.8196, 0.594965},{-19.4333, 0.560965}}
            },
            {   {{-12.6317, 0.611023},{-16.5644, 0.578978},{-11.5882, 0.496324}},
                {{-12.8886, 0.614807},{-17.0847, 0.584072},{-14.9561, 0.532125}},
                {{-11.4504, 0.600574},{-16.3862, 0.57885},{-12.3309, 0.515431}},
                {{-12.2256, 0.609801},{-16.2134, 0.574306},{-12.7661, 0.515787}},
                {{-12.6311, 0.611069},{-16.2486, 0.577577},{-12.6783, 0.519597}},
                {{-12.6937, 0.615423},{-16.1427, 0.57847},{-11.5156, 0.509458}}
            },
            {   {{-5.95834, 0.538479},{-15.8909, 0.570164},{-30.2922, 0.586335}},
                {{-6.15277, 0.542134},{-16.1129, 0.573794},{-31.6024, 0.592681}},
                {{-6.12341, 0.542023},{-16.1611, 0.575971},{-29.8604, 0.581528}},
                {{-6.37691, 0.546536},{-16.8501, 0.580239},{-30.0623, 0.580497}},
                {{-5.96605, 0.537402},{-15.7154, 0.5704},{-31.2955, 0.594146}},
                {{-5.86704, 0.539556},{-16.2268, 0.580945},{-31.2345, 0.590849}}
            },
            {   {{-11.7796, 0.614043},{-19.0763, 0.595015},{-18.804, 0.559538}},
                {{-12.4399, 0.623126},{-19.1733, 0.600646},{-17.675, 0.557016}},
                {{-10.4158, 0.605483},{-18.0044, 0.595497},{-17.5441, 0.556504}},
                {{-12.1552, 0.617782},{-19.7134, 0.603519},{-17.3756, 0.549676}},
                {{-11.3901, 0.612121},{-18.2429, 0.596796},{-10.0097, 0.482578}},
                {{-12.5004, 0.626384},{-19.9266, 0.60993},{-16.4668, 0.543148}}
            },
            {   {{-5.60572, 0.537153},{-16.3196, 0.582537},{-32.4336, 0.601487}},
                {{-5.52369, 0.532985},{-15.2055, 0.568935},{-31.9046, 0.600079}},
                {{-5.78558, 0.546316},{-16.3328, 0.583765},{-36.0074, 0.617008}},
                {{-5.82321, 0.542839},{-15.9551, 0.580441},{-31.4304, 0.597132}},
                {{-5.36526, 0.535923},{-15.9219, 0.586886},{-30.4245, 0.599613}},
                {{-5.14766, 0.53037},{-14.1986, 0.561504},{-31.7548, 0.60233}}
            }
        };


        //double minparams_out[6][6][3][2] =
        double[][][][] minparams_out = {
            {   {{8.07831, -0.548881},{16.4382, -0.569075},{33.7768, -0.607402}},
                {{8.51057, -0.551773},{16.7782, -0.571381},{32.2613, -0.600686}},
                {{8.5232, -0.552628},{16.4274, -0.56775},{31.1516, -0.584708}},
                {{7.98845, -0.544571},{16.4381, -0.569077},{31.8093, -0.595237}},
                {{7.46705, -0.538557},{16.7414, -0.573345},{31.1888, -0.586751}},
                {{7.82627, -0.538957},{16.2409, -0.565872},{32.1089, -0.596846}}
            },
            {   {{7.1519, -0.563678},{16.1038, -0.571795},{20.0449, -0.559802}},
                {{6.38228, -0.553174},{16.4526, -0.576382},{19.3523, -0.556484}},
                {{7.11359, -0.561586},{17.2815, -0.578095},{14.9667, -0.53314}},
                {{5.89053, -0.556406},{17.4946, -0.585038},{17.3607, -0.545739}},
                {{7.08253, -0.562099},{15.1516, -0.569192},{16.9665, -0.545949}},
                {{5.53089, -0.546315},{16.4962, -0.574014},{17.9593, -0.545788}}
            },
            {   {{12.4879, -0.610527},{16.7782, -0.575065},{11.7704, -0.511182}},
                {{12.1931, -0.604779},{15.6443, -0.560967},{12.7304, -0.515606}},
                {{12.206, -0.602999},{16.5979, -0.573274},{12.3971, -0.513795}},
                {{11.5538, -0.604186},{16.6974, -0.576753},{12.7385, -0.517811}},
                {{12.9718, -0.611968},{17.7233, -0.583943},{10.6601, -0.49233}},
                {{12.2966, -0.607592},{15.923, -0.564133},{13.9314, -0.525363}}
            },
            {   {{5.92493, -0.539308},{17.4444, -0.586183},{31.6974, -0.591988}},
                {{5.467, -0.525876},{16.0649, -0.570869},{30.5937, -0.590071}},
                {{5.67798, -0.531096},{16.5072, -0.57205},{30.7922, -0.586727}},
                {{6.85795, -0.558336},{14.9425, -0.545596},{31.3159, -0.592865}},
                {{6.0155, -0.545283},{16.0649, -0.570869},{30.6644, -0.587002}},
                {{6.18343, -0.539055},{17.4516, -0.583221},{32.6264, -0.594317}}
            },
            {   {{12.9118, -0.618907},{19.7061, -0.60171},{18.9352, -0.559461}},
                {{13.0612, -0.618743},{19.0954, -0.595406},{19.7019, -0.568119}},
                {{12.4007, -0.613459},{17.544, -0.581147},{12.8175, -0.511017}},
                {{13.3144, -0.625596},{18.9225, -0.594001},{15.1524, -0.530046}},
                {{13.101, -0.620887},{18.5616, -0.595279},{14.8807, -0.533111}},
                {{12.2964, -0.613529},{19.0686, -0.595276},{19.2596, -0.562706}}
            },
            {   {{5.34118, -0.530584},{16.3015, -0.585185},{38.7808, -0.641362}},
                {{6.68051, -0.548747},{16.4236, -0.583598},{38.4718, -0.630423}},
                {{6.87, -0.552602},{16.4285, -0.57977},{36.8889, -0.624053}},
                {{7.15338, -0.565067},{16.9387, -0.595922},{37.2398, -0.624177}},
                {{6.06995, -0.550001},{15.7376, -0.577755},{32.6004, -0.601595}},
                {{6.20459, -0.543148},{14.6326, -0.561623},{39.2154, -0.631762}}
            }
        };


    /**
     * @param dc_sector sector of hits in DC
     * @param region specify fiducial cuts for which region to use
     * @param x x for region 1 or 2 or 3 from REC::Traj
     * @param y y for region 1 or 2 or 3 from REC::Traj
     * @param partpid pid assigned to particle candidate
     * @param isinbending True if magnetic field is inbending
     */

    public boolean DC_fiducial_cut_XY(int dc_sector, int region, double x, double y, int partpid, boolean isinbending) {
        double[][][][] minparams = isinbending ? minparams_in : minparams_out;
        double[][][][] maxparams = isinbending ? maxparams_in : maxparams_out;

        double X = x;
        double Y = y;

        double X_new = X * Math.cos(Math.toRadians(-60*(dc_sector-1))) - Y * Math.sin(Math.toRadians(-60*(dc_sector-1)));
        Y = X * Math.sin(Math.toRadians(-60*(dc_sector-1))) + Y * Math.cos(Math.toRadians(-60*(dc_sector-1)));
        X = X_new;

        int pid = 0;

        switch (partpid)
        {
        case 11:
            pid = 0;
            break;
        case 2212:
            pid = 1;
            break;
        case 211:
            pid = 2;
            break;
        case -211:
            pid = 3;
            break;
        case 321:
            pid = 4;
            break;
        case -321:
            pid = 5;
            break;
        default:
            return false;
        }

        //if(inbending == true) pid = 0; // use only for electrons in inbending case

        double calc_min = minparams[pid][dc_sector - 1][region-1][0] + minparams[pid][dc_sector - 1][region-1][1] * X;
        double calc_max = maxparams[pid][dc_sector - 1][region-1][0] + maxparams[pid][dc_sector - 1][region-1][1] * X;

        return (Y > calc_min) && (Y < calc_max);
    }
    
    public static void main(String[] args){
        DataGroup dg = new DataGroup(3,2);
        for(int i=0; i<3; i++)  {
            int region = i+1;
            double range = 200+i*100;
            H2F h2nocut = new H2F("h2nocut"+region,"h2nocut"+region, 200,-range,range,  200,-range,range);
            H2F h2cut   = new H2F("h2cut"+region,  "h2cut"+region,   200,-range,range,  200,-range,range);
            dg.addDataSet(h2nocut, i);
            dg.addDataSet(h2cut,   i+3);
        }
        EleDCFiducial fid = new EleDCFiducial();
        
        HipoReader reader = new HipoReader();
        reader.open("/Users/devita/ai_rec_clas_005038.small.hipo");
        
        SchemaFactory schema = reader.getSchemaFactory();
                    
        Bank particleBank   = new Bank(schema.getSchema("REC::Particle"));
        Bank trajectoryBank = new Bank(schema.getSchema("REC::Traj"));
        Bank trackBank      = new Bank(schema.getSchema("REC::Track"));
        
        int counter=-1;
        Event event = new Event();
        while (reader.hasNext()) {
            counter++;
            reader.nextEvent(event);
            event.read(particleBank);
            event.read(trajectoryBank);
            event.read(trackBank);
            
            for(int loop = 0; loop < trajectoryBank.getRows(); loop++){
                int pindex   = trajectoryBank.getShort("pindex", loop);
                int detector = trajectoryBank.getByte("detector", loop);
                int pid      = particleBank.getInt("pid", pindex);
                
                if((detector==DetectorType.DC.getDetectorId()) && pid==11) { 
                    int layer = trajectoryBank.getByte("layer", loop);
                    double x  = trajectoryBank.getFloat("x", loop);
                    double y  = trajectoryBank.getFloat("y", loop);
                    double z  = trajectoryBank.getFloat("z", loop);
                    int region = ((int) (layer-1)/12) +1;
                    int sector = 0;
                    for(int j=0;j<trackBank.getRows(); j++) {
                        if(pindex==trackBank.getShort("pindex", j)) sector = trackBank.getByte("sector", j);
                    }
                    boolean fiducial = fid.DC_fiducial_cut_XY(sector, region, x, y, 11, true);
                    dg.getH2F("h2nocut"+region).fill(x, y);
                    if(fiducial) dg.getH2F("h2cut"+region).fill(x, y);
                }
            }
        }
        reader.close();
        
        for(int i=0; i<3; i++)  {
            System.out.println(dg.getH2F("h2nocut"+(i+1)).getEntries() + " " + dg.getH2F("h2cut"+(i+1)).getEntries());
        }
        
        EmbeddedCanvas canvas = new EmbeddedCanvas();
        canvas.draw(dg);
        JFrame frame = new JFrame("DC");
        frame.setSize(1200, 1000);
        frame.add(canvas);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

}
