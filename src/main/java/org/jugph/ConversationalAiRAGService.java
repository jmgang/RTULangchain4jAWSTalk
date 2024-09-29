package org.jugph;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.UrlDocumentLoader;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.document.transformer.jsoup.HtmlToTextDocumentTransformer;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.chroma.ChromaEmbeddingStore;

import java.util.List;

public class ConversationalAiRAGService {

    interface Assistant {
        String chat(String userMessage);
    }

    public static void main(String[] args) {

        Document document = UrlDocumentLoader.load("https://www.rtu.edu.ph/about/",
                new TextDocumentParser());

        HtmlToTextDocumentTransformer textExtractor = new HtmlToTextDocumentTransformer();
        Document transformedDocument = textExtractor.transform(document);

        var model = BedrockModel.bedrockLlamaChatModel();

        EmbeddingStore<TextSegment> embeddingStore = ChromaEmbeddingStore
                .builder()
                .baseUrl("http://localhost:8000/")
                .collectionName("rtu-collection")
                .logRequests(true)
                .logResponses(true)
                .build();

        EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();

        EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                .documentSplitter(DocumentSplitters.recursive(1000, 0))
                .embeddingModel(embeddingModel)
                .embeddingStore(embeddingStore)
                .build();

        ingestor.ingest(transformedDocument);
//
        var chatbot = AiServices.builder(Assistant.class)
                .chatLanguageModel(model)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(9))
                .contentRetriever(EmbeddingStoreContentRetriever.from(embeddingStore))
                .build();

        var questions = List.of("Can you tell me what is Rizal Technological University?",
                "When was it established?",
                "How many campuses does it have?");

        for(String question: questions) {
            var response = chatbot.chat(question);
            System.out.println("\nQuestion: " + question + "\nAI: " + response);
        }

    }
}
