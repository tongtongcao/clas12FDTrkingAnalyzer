package org.clas.aiModel;

import ai.djl.MalformedModelException;
import ai.djl.inference.Predictor;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.NDManager;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ModelNotFoundException;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.training.util.ProgressBar;
import ai.djl.translate.*;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * Wrapper for Two Classifiers:
 * - SixClsComboClf (12 features)
 * - FiveClsComboClf (11 features)
 */
public class ClsComboClf {

    /* ============================================================
     *  SIX CLASS MODEL (12-dim input)
     * ============================================================ */
    public static class SixClsComboClf {

        private final ZooModel<float[][], float[]> model;
        private final Predictor<float[][], float[]> predictor;

        public SixClsComboClf(String modelFile)
                throws IOException, ModelNotFoundException, MalformedModelException {

            Translator<float[][], float[]> translator = new Translator<>() {
                @Override
                public NDList processInput(TranslatorContext ctx, float[][] batchInput) {
                    NDManager manager = ctx.getNDManager();
                    int batch = batchInput.length;
                    int dim = 12;

                    float[][] normalized = new float[batch][dim];

                    // Normalize for each sample
                    for (int b = 0; b < batch; b++) {
                        for (int i = 0; i < 6; i++) normalized[b][i] = batchInput[b][i] / 112f;
                        for (int i = 6; i < 12; i++) normalized[b][i] = batchInput[b][i];
                    }

                    NDArray x = manager.create(normalized);   // shape: (batch, 12)
                    return new NDList(x);
                }

                @Override
                public float[] processOutput(TranslatorContext ctx, NDList out) {
                    return out.get(0).toFloatArray();
                }

                @Override
                public Batchifier getBatchifier() { return Batchifier.STACK; }
            };

            Criteria<float[][], float[]> c =
                    Criteria.builder()
                            .setTypes(float[][].class, float[].class)
                            .optModelPath(Paths.get(modelFile))
                            .optEngine("PyTorch")
                            .optTranslator(translator)
                            .optProgress(new ProgressBar())
                            .build();

            model = c.loadModel();
            predictor = model.newPredictor();
        }

        public float predict(float[] f12) throws TranslateException {
            return predictor.predict(new float[][]{ f12 })[0];
        }

        public float[] predictBatch(float[][] batch) throws TranslateException {
            return predictor.predict(batch);
        }

        public void close() {
            predictor.close();
            model.close();
        }
    }


    /* ============================================================
     *  FIVE CLASS MODEL (11-dim input)
     * ============================================================ */
    public static class FiveClsComboClf {

        private final ZooModel<float[][], float[]> model;
        private final Predictor<float[][], float[]> predictor;

        public FiveClsComboClf(String modelFile)
                throws IOException, ModelNotFoundException, MalformedModelException {

            Translator<float[][], float[]> translator = new Translator<>() {
                @Override
                public NDList processInput(TranslatorContext ctx, float[][] batchInput) {
                    NDManager manager = ctx.getNDManager();
                    int batch = batchInput.length;
                    int dim = 11;

                    float[][] normalized = new float[batch][dim];

                    // Normalize for each sample
                    for (int b = 0; b < batch; b++) {
                        for (int i = 0; i < 5; i++) normalized[b][i] = batchInput[b][i] / 112f;
                        for (int i = 5; i < 10; i++) normalized[b][i] = batchInput[b][i];
                        normalized[b][10] = batchInput[b][10]/6.0f;
                    }

                    NDArray x = manager.create(normalized);   // shape: (batch, 11)
                    return new NDList(x);
                }

                @Override
                public float[] processOutput(TranslatorContext ctx, NDList out) {
                    return out.get(0).toFloatArray();
                }

                @Override
                public Batchifier getBatchifier() { return Batchifier.STACK; }
            };

            Criteria<float[][], float[]> c =
                    Criteria.builder()
                            .setTypes(float[][].class, float[].class)
                            .optModelPath(Paths.get(modelFile))
                            .optEngine("PyTorch")
                            .optTranslator(translator)
                            .optProgress(new ProgressBar())
                            .build();

            model = c.loadModel();
            predictor = model.newPredictor();
        }

        public float predict(float[] f11) throws TranslateException {
            return predictor.predict(new float[][]{ f11 })[0];
        }

        public float[] predictBatch(float[][] batch) throws TranslateException {
            return predictor.predict(batch);
        }

        public void close() {
            predictor.close();
            model.close();
        }
    }
}

