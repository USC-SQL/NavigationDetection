package edu.usc.ContextTree.Construction.AffinityCalculation;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.CosineSimilarity;
import org.apache.commons.lang3.StringUtils;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;

public class SentenceComparer {

    EmbeddingModel model;

    public SentenceComparer(){
        /*
        LoadConfig config_obj = new LoadConfig();
        String api_key = LoadConfig.prop.getProperty("huggingface_API_key");
        //Loads model via api key
        EmbeddingModel huggingFaceEmbeddingModel = HuggingFaceEmbeddingModel.builder()
                .accessToken(api_key)
                .modelId("sentence-transformers/all-MiniLM-L6-v2")
                .build();
        model = huggingFaceEmbeddingModel;

         */

        model = new AllMiniLmL6V2EmbeddingModel();
    }
    public double CompareSentences(String stringOne, String stringTwo){
        if(StringUtils.isBlank(stringOne)){
            return 0.5;
        }
        if(StringUtils.isBlank(stringTwo)){
            return 0.5;
        }
        Embedding StringOneEmbedding = model.embed(stringOne).content();
        Embedding StringTwoEmbedding = model.embed(stringTwo).content();
        double cosine_sim = CosineSimilarity.between(StringOneEmbedding, StringTwoEmbedding);
        return cosine_sim;
    }
}
