package org.jugph;

import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.UrlDocumentLoader;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.transformer.jsoup.HtmlToTextDocumentTransformer;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;

import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_4_O_MINI;


public class JavaNewsRetrieverToolsExample {
    static class JavaNewsRetriever {

        @Tool("Retrieves the latest java news. Strictly limit to the 3 latest news")
        String retrieveJavaNews() {
            Document javaNews = UrlDocumentLoader.load("https://dev.java/news/", new TextDocumentParser());
            Document transformedJavaNews = new HtmlToTextDocumentTransformer(".container", null, true)
                    .transform(javaNews);
            System.out.println(transformedJavaNews.text().replaceAll("\n", " "));
            return transformedJavaNews.text().replaceAll("\n", " ");
        }
    }

    interface Assistant {
        String chat(String userMessage);
    }

    public static void main(String[] args) {
        var model = OpenAiChatModel.builder()
                .apiKey(ApiKeys.OPENAI_API_KEY)
                .modelName(GPT_4_O_MINI)
                .build();

        var assistant = AiServices.builder(Assistant.class)
                .chatLanguageModel(model)
                .tools(new JavaNewsRetriever())
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .build();
        var question = "What are latest java news?";

        var answer = assistant.chat(question);

        System.out.println(answer);
    }
}
