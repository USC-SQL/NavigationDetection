package edu.usc.WinnTree.Construction.AffinityCalculation;

import ai.djl.huggingface.tokenizers.HuggingFaceTokenizer;
import ai.djl.huggingface.tokenizers.Encoding;
import ai.onnxruntime.*;

import java.util.HashMap;
import java.util.Map;

public class SentenceComparer {

    HuggingFaceTokenizer tokenizer;
    OrtSession session;
    OrtEnvironment environment;

    public SentenceComparer() throws OrtException {
        this.tokenizer = HuggingFaceTokenizer.newInstance("sentence-transformers/all-mpnet-base-v2");
        this.environment = OrtEnvironment.getEnvironment();
        this.session = environment.createSession("C:\\Users\\rober\\Documents\\firstpaper\\model.onnx", new OrtSession.SessionOptions());
    }

    public double CompareSentences(String stringOne, String stringTwo) throws OrtException {
        String[] sentences = new String[]{stringOne,stringTwo};
        //HuggingFaceTokenizer tokenizer = HuggingFaceTokenizer.newInstance("sentence-transformers/all-mpnet-base-v2");
        Encoding[] encodings = tokenizer.batchEncode(sentences);
        //OrtEnvironment environment = OrtEnvironment.getEnvironment();
        //OrtSession session = environment.createSession("C:\\Users\\rober\\Documents\\firstpaper\\model.onnx", new OrtSession.SessionOptions());
        long[][] input_ids0 = new long[encodings.length][];
        long[][] attention_mask0 = new long[encodings.length][];

        for(int i =0; i<encodings.length; i++){
            input_ids0[i] = encodings[i].getIds();
            attention_mask0[i] = encodings[i].getAttentionMask();
        }

        OnnxTensor inputIds = OnnxTensor.createTensor(environment, input_ids0);
        OnnxTensor attentionMask = OnnxTensor.createTensor(environment, attention_mask0);

        Map<String, OnnxTensor> inputs = new HashMap<>();
        inputs.put("input_ids", inputIds);
        inputs.put("attention_mask", attentionMask);
        try(OrtSession.Result results = session.run(inputs)){
            OnnxValue lastHiddenState = results.get(0);
            float[][][] tokenEmbeddings = (float[][][]) lastHiddenState.getValue();
            float[] sentence1Embedding = averageEmbeddings(tokenEmbeddings[0]);
            float[] sentence2Embedding = averageEmbeddings(tokenEmbeddings[1]);

            double similarity = cosineSimilarity(sentence1Embedding, sentence2Embedding);
            return similarity;
        } catch (Error e){
            return 0.5;
        }
    }

    private static float[] averageEmbeddings(float[][] tokenEmbeddings){
        int length = tokenEmbeddings.length;
        int dimension = tokenEmbeddings[0].length;
        float[] average = new float[dimension];

        for(float[] embedding: tokenEmbeddings){
            for(int i = 0; i<dimension; i++){
                average[i] += embedding[i];
            }
        }
        for(int i=0; i<dimension; i++){
            average[i] /= length;
        }

        return average;
    }

    private static double cosineSimilarity(float[] vectorA, float[] vectorB){
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        for(int i=0; i<vectorA.length; i++){
            dotProduct += vectorA[i] * vectorB[i];
            normA  += vectorA[i] * vectorA[i];
            normB += vectorB[i] * vectorB[i];
        }
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }


}


















