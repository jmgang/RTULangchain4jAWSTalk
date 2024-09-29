package org.jugph;


import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.UrlDocumentLoader;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.transformer.jsoup.HtmlToTextDocumentTransformer;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;

import java.util.List;
import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_4_O_MINI;

public class PromptExample {

    interface Assistant {
        String chat(String prompt);
    }

    public static void main( String[] args ) {
        // prompt and model
        Document document = UrlDocumentLoader.load("https://www.rtu.edu.ph/about/",
                new TextDocumentParser());

        HtmlToTextDocumentTransformer textExtractor = new HtmlToTextDocumentTransformer();
        Document transformedDocument = textExtractor.transform(document);
        var rtuInformation = transformedDocument.text();
        var model = OpenAiChatModel.builder()
                .apiKey(ApiKeys.OPENAI_API_KEY)
                .modelName(GPT_4_O_MINI)
                .build();

        var questions = List.of(
                "Where is Rizal Technological University?",
                "When was Rizal Technological University established?",
                "How many campuses does Rizal Technological University have?");

        var assistant = AiServices.builder(Assistant.class)
                .chatLanguageModel(model)
                .build();

        for(String question: questions) {
            var answer = assistant.chat("Based on the information: " + rtuInformation + "\nAnswer the following question: " + question);
            System.out.println("\nAI: " + question + "\nAnswer: " + answer);
        }
    }
}
