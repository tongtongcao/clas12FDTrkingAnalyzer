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

public class Denoising {

    private final ZooModel<float[][], float[][]> model;
    private final Predictor<float[][], float[][]> predictor;

    /** Constructor: load model + create translator */
    public Denoising(String modelFile)
            throws IOException, ModelNotFoundException, MalformedModelException {

        // 单线程模式
        System.setProperty("ai.djl.pytorch.num_interop_threads", "1");
        System.setProperty("ai.djl.pytorch.num_threads", "1");
        System.setProperty("ai.djl.pytorch.graph_optimizer", "false");

        Translator<float[][], float[][]> translator = buildTranslator();

        Criteria<float[][], float[][]> criteria =
                Criteria.builder()
                        .setTypes(float[][].class, float[][].class)
                        .optModelPath(Paths.get(modelFile))
                        .optEngine("PyTorch")
                        .optTranslator(translator)
                        .optProgress(new ProgressBar())
                        .build();

        model = criteria.loadModel();
        predictor = model.newPredictor();
    }

    /** Predict wrapper */
    public float[][] predict(float[][] input) throws TranslateException {
        return predictor.predict(input);
    }

    /** Manually close resources */
    public void close() {
        predictor.close();
        model.close();
    }

    /** Build the translator used by this autoencoder */
    private Translator<float[][], float[][]> buildTranslator() {
        return new Translator<float[][], float[][]>() {
            @Override
            public NDList processInput(TranslatorContext ctx, float[][] input) {
                NDManager manager = ctx.getNDManager();
                int height = input.length;
                int width = input[0].length;

                float[] flat = new float[height * width];
                for (int i = 0; i < height; i++) {
                    System.arraycopy(input[i], 0, flat, i * width, width);
                }

                NDArray x = manager.create(flat, new Shape(height, width));
                x = x.expandDims(0).expandDims(0); // [1,1,H,W]
                return new NDList(x);
            }

            @Override
            public float[][] processOutput(TranslatorContext ctx, NDList list) {
                NDArray result = list.get(0).squeeze(); // remove [1,1]

                float[] flat = result.toFloatArray();
                long[] s = result.getShape().getShape();
                int h = (int) s[0];
                int w = (int) s[1];

                float[][] out = new float[h][w];
                for (int i = 0; i < h; i++) {
                    System.arraycopy(flat, i * w, out[i], 0, w);
                }
                return out;
            }

            @Override
            public Batchifier getBatchifier() {
                return null; // no batching
            }
        };
    }
}
