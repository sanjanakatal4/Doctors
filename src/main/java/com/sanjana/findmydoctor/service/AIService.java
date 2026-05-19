package com.sanjana.findmydoctor.service;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
@Service
public class AIService {
	private final ChatClient chatClient;

    public AIService(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    public AIResponse classifyComplaint(String text) {

        // Step 1: Build Prompt
        String prompt = """
        	    You are a strict AI Doctor that gives result from given symptoms.
        	    Classify the result into JSON ONLY.
        	    Fields:
        	    - message 
        	    - warnings
        	    - emergencyLevel
        	    - requiresPhysicianConsult
        	    - recommendations
        	    - topRelatedSpecialties
        	    Symptoms:
        	    """+text+"""

        	    Return ONLY JSON. No explanation.
        	    """;
        // Step 2: Call AI (Spring AI)
        String response = chatClient.prompt()
                .user(prompt)
                .call()
                .content();
//        System.out.println(response);
        return AIResponseParser.parse(response);
    }
}