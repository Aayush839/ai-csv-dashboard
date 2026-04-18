package ai_dashboard.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AiService {

    @Value("${ai.api.key}")
    private String apikey;
    public Map<String, Object> askAI(String question,List<Map<String, String>> data) {
        try {
            List<Map<String, String>> sample = data.stream().toList();

            String prompt = """
                    Convert user query into JSON.
                    Convert user query into structured JSON based on given table schema.
                             ALWAYS return this structure:
                             Table is %s
                             {
                               "operation": "count | sum | avg | group | filter",
                               column:""
                               "conditions": [
                                 {"column": "...", "value": "..."}
                               ]
                             }
                    
                             Rules:
                             - Infer operation from query
                             - MUST choose operation from allowed values
                             - If no filtering is needed, return empty conditions: []
                             - Do NOT return null
                             - Do NOT skip conditions field
                             - Always include "conditions"
                    
                             Query: %s
                    """.formatted(sample.toString(),question);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apikey);
            headers.set("HTTP-Referer", "https://ai-chat-k2f2.onrender.com");
            headers.set("X-Title", "AI Chat App");
            headers.set("Accept", "application/json");

            List<Map<String, String>> messages = new ArrayList<>();
            Map<String, String> msg = new HashMap<>();
            msg.put("role", "user");
            msg.put("content", prompt);
            messages.add(msg);

            Map<String, Object> body = new HashMap<>();
            body.put("messages",messages );
            body.put("model", "arcee-ai/trinity-large-preview:free");
            body.put("temperature", 0.7);
            body.put("max_tokens", 150);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            RestTemplate restTemplate = new RestTemplate();

            ResponseEntity<Map> response = restTemplate.exchange("https://openrouter.ai/api/v1/chat/completions", HttpMethod.POST,request,Map.class);

            Map responseBody = response.getBody();
            List choices = (List) responseBody.get("choices");
            Map firstChoice = (Map) choices.get(0);
            Map message = (Map) firstChoice.get("message");

            String content = (String) message.get("content");
            content = content.trim();
            content = content.replaceAll("```json", "")
                    .replaceAll("```", "")
                    .trim();

            int start = content.indexOf("{");
            int end = content.lastIndexOf("}");

            if (start != -1 && end != -1) {
                content = content.substring(start, end + 1);
            }

            ObjectMapper mapper = new ObjectMapper();

            return mapper.readValue(content, Map.class);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public String generateExplanation(String question, List<Map<String, String>> data) {

        try {
            RestTemplate restTemplate = new RestTemplate();

            List<Map<String, String>> sample = data.stream().limit(10).toList();

            String prompt = """
                You are a data analyst.
                User question:
               %s
                Query result (JSON):
                %s
                Explain the result in simple English.
                Keep it short and clear.
                """.formatted(question, sample.toString());

            List<Map<String, String>> messages = new ArrayList<>();
            Map<String, String> msg = new HashMap<>();
            msg.put("role", "user");
            msg.put("content", prompt);
            messages.add(msg);

            Map<String, Object> body = new HashMap<>();
            body.put("model", "arcee-ai/trinity-large-preview:free");
            body.put("messages", messages);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apikey);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    "https://openrouter.ai/api/v1/chat/completions",
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            Map choice = (Map) ((List) response.getBody().get("choices")).get(0);
            Map message = (Map) choice.get("message");

            return (String) message.get("content");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "Unable to generate explanation";
    }



}