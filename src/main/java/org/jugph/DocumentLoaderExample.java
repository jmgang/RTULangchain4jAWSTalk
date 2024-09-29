package org.jugph;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.UrlDocumentLoader;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.transformer.jsoup.HtmlToTextDocumentTransformer;

public class DocumentLoaderExample {
    public static void main(String[] args) {
        Document document = UrlDocumentLoader.load("https://www.rtu.edu.ph/about/",
                        new TextDocumentParser());

        HtmlToTextDocumentTransformer textExtractor = new HtmlToTextDocumentTransformer();
        Document transformedDocument = textExtractor.transform(document);

        System.out.println(document.text()+"\n===========Transformed Document===================\n"+transformedDocument.text());
    }
}
