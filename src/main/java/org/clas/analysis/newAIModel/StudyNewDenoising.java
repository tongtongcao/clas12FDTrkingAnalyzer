package org.clas.analysis.newAIModel;

import org.clas.aiModel.Denoising;

import java.io.*;
import java.nio.file.Files;

public class StudyNewDenoising {

    public static void main(String[] args) throws Exception {

        String modelPath;
        try (InputStream in = StudyNewDenoising.class.getResourceAsStream("/org/clas/aiModel/cnn_autoenc_sector1_2b_48f_4x6k.pt")) {

            if (in == null) {
                throw new RuntimeException("Model file not found in resources!");
            }

            File tempFile = Files.createTempFile("cnn_model", ".pt").toFile();
            tempFile.deleteOnExit(); // auto exit when program done

            try (OutputStream out = new FileOutputStream(tempFile)) {
                in.transferTo(out);
            }

            modelPath = tempFile.getAbsolutePath();
        }

        Denoising net = new Denoising(modelPath);

        float[][] input = new float[36][112];
        input[10][50] = 1.0f;

        float[][] output = net.predict(input);

        System.out.println("Output shape: [" + output.length + "," + output[0].length + "]");
        System.out.println("Sample output values:");
        for (int i = 0; i < 36; i++) {
            for (int j = 0; j < 112; j++) {
                System.out.printf("%.3f ", output[i][j]);
            }
            System.out.println();
        }
        
    }
}
