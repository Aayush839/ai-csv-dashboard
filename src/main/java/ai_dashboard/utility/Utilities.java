package ai_dashboard.utility;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class Utilities {
    @Autowired
    private ObjectMapper objectMapper;

    public Map<String,String> getJsonAsMap(String json) throws JsonProcessingException {
        return objectMapper.readValue(json, Map.class);
    }
    public String getJsonStringFromMap(Map<String, String> mp) throws JsonProcessingException {
        return objectMapper.writeValueAsString(mp);
    }

}
