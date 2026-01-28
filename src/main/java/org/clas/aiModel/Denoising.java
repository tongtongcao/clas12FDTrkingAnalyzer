package org.clas.aiModel;

import ai.djl.MalformedModelException;
import ai.djl.inference.Predictor;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.NDManager;
import ai.djl.ndarray.types.Shape;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ModelNotFoundException;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.training.util.ProgressBar;
import ai.djl.translate.Batchifier;
import ai.djl.translate.Translator;
import ai.djl.translate.TranslatorContext;
import ai.djl.translate.TranslateException;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * Application of denoising model
 */
public class Denoising {

    private final ZooModel<float[][][], float[][][]> model;
    private final Predictor<float[][][], float[][][]> predictor;

    public Denoising(String modelFile)
            throws IOException, ModelNotFoundException, MalformedModelException {

        // PyTorch thread
        //System.setProperty("ai.djl.pytorch.num_interop_threads", "1");
        //System.setProperty("ai.djl.pytorch.num_threads", "1");
        System.setProperty("ai.djl.pytorch.graph_optimizer", "false");

        Translator<float[][][], float[][][]> translator = buildBatchTranslator();

        Criteria<float[][][], float[][][]> criteria =
                Criteria.builder()
                        .setTypes(float[][][].class, float[][][].class)
                        .optModelPath(Paths.get(modelFile))
                        .optEngine("PyTorch")
                        .optTranslator(translator)
                        .optProgress(new ProgressBar())
                        .build();

        model = criteria.loadModel();
        predictor = model.newPredictor();
    }

    /** batch predict: input[6][36][112] */
    public float[][][] predict(float[][][] batchInput) throws TranslateException {
        return predictor.predict(batchInput);
    }

    public void close() {
        predictor.close();
        model.close();
    }

    private Translator<float[][][], float[][][]> buildBatchTranslator() {
        return new Translator<float[][][], float[][][]>() {

            @Override
            public NDList processInput(TranslatorContext ctx, float[][][] input) {
                NDManager manager = ctx.getNDManager();
                int batch = input.length;
                int height = input[0].length;
                int width = input[0][0].length;

                float[] flat = new float[batch * height * width];
                int pos = 0;

                for (int b = 0; b < batch; b++) {
                    for (int h = 0; h < height; h++) {
                        System.arraycopy(input[b][h], 0, flat, pos, width);
                        pos += width;
                    }
                }

                NDArray x = manager.create(flat, new Shape(batch, 1, height, width));
                return new NDList(x);
            }

            @Override
            public float[][][] processOutput(TranslatorContext ctx, NDList list) {
                NDArray result = list.get(0);

                long[] s = result.getShape().getShape();

                int batch = (int) s[0];
                int height, width;

                if (s.length == 4 && s[1] == 1) {
                    height = (int) s[2];
                    width = (int) s[3];
                    result = result.squeeze(1);
                } else {
                    throw new IllegalStateException(
                            "Unexpected output shape: " + java.util.Arrays.toString(s));
                }

                float[] flat = result.toFloatArray();
                float[][][] out = new float[batch][height][width];

                int pos = 0;
                for (int b = 0; b < batch; b++) {
                    for (int h = 0; h < height; h++) {
                        System.arraycopy(flat, pos, out[b][h], 0, width);
                        pos += width;
                    }
                }

                return out;
            }

            @Override
            public Batchifier getBatchifier() {
                return null;
            }
        };
    }
}
