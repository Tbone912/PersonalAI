package com.personalAI;

import org.springframework.ai.embedding.EmbeddingClient;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chroma.ChromaApi;
import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.vectorsore.ChromaVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfiguration {
	
	@Bean
	  ChatClient chatClient(@Value("${spring.ai.openai.api-key}") String apiKey) {
	    return new OpenAiChatClient(new OpenAiApi(apiKey));
	  }
	
	
	
	@Bean
	public VectorStore chromaVectorStore(EmbeddingClient embeddingClient, ChromaApi chromaApi) {
	    String collectionName = "SpringAiCollection";
	    chromaApi.getCollection(collectionName);
	    return new ChromaVectorStore(embeddingClient, chromaApi, collectionName);
	}
	

}
