package ai_dashboard.service;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MainService {

    @Autowired
    private AiService aiService;

    @Autowired
    private DataProcessingService dataProcessingService;

    public Map<String, Object> processQuery(String question, List<Map<String, String>> data) {

        Map<String, Object> result = new HashMap<>();
        Map aiJson = aiService.askAI(question, data);

        List<Map<String, String>> filteredData = dataProcessingService.applyDynamic(data, aiJson);

        String explanation = aiService.generateExplanation(question, filteredData);

        result.put("text", explanation);
        result.put("data", filteredData);

        String operation = (String) aiJson.get("operation");

        if (!operation.equalsIgnoreCase("filter")) {
            Map<String, Object> aggResult = dataProcessingService.aggregate(filteredData, aiJson);

            Map<String, Object> chart = dataProcessingService.generateChart(aggResult, operation);

            result.put("chart", chart);
        }


        return result;
    }
}
