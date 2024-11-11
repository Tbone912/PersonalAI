package com.personalAI;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OpenAiChatController {

	private final ChatClient chatClient;

	@Autowired
	private VectorStore vectorStore;

	public OpenAiChatController(ChatClient chatClient) {
		this.chatClient = chatClient;
	}

	@GetMapping("/add-document")
	public String addDocument() throws InvalidPasswordException, IOException {

		File file = new File("src/main/resources/input.pdf");
		PDDocument doc = PDDocument.load(file);

		PDFTextStripper pdfStripper = new PDFTextStripper();

		String text = pdfStripper.getText(doc);
		System.out.println("Text in PDF");
//		System.out.println(text);

		doc.close();

		Document document = new Document(text);
		vectorStore.add(List.of(document));
		return text;
	}

	@GetMapping("/rag")
	public String ragQuery(@RequestParam String query) {
		List<Document> relevantDocs = vectorStore.similaritySearch(SearchRequest.query(query).withTopK(3));

		StringBuilder context = new StringBuilder();
		for (Document doc : relevantDocs) {
			context.append(doc.getContent()).append("\n");
		}

		String prompt = "Based on the following context, please answer the question: " + query + "\n\nContext: "
				+ context.toString();

		return chatClient.call(prompt);
	}

}
