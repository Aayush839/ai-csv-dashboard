package ai_dashboard.service;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DataProcessingService {

    public List<Map<String, String>> filter(List<Map<String, String>> data, String column, String value) {
        return data.stream()
                .filter(row -> row.get(column) != null &&
                        row.get(column).equalsIgnoreCase(value))
                .collect(Collectors.toList());
    }

    public Map<String, List<Map<String, String>>> groupBy(List<Map<String, String>> data,String column) {
        return data.stream()
                .collect(Collectors.groupingBy(row -> row.get(column)));
    }
//    aggregate
    public int count(List<Map<String, String>> data) {
        return data.size();
    }

    public double sum(List<Map<String, String>> data, String column) {
        return data.stream()
                .mapToDouble(row -> Double.parseDouble(row.get(column)))
                .sum();
    }
    public double average(List<Map<String, String>> data, String column) {
        return data.stream()
                .mapToDouble(row -> Double.parseDouble(row.get(column)))
                .average()
                .orElse(0.0);
    }

    public Object process(List<Map<String, String>> data, String question) {

        String q = question.toLowerCase();

        if (q.contains("count") || q.contains("how many")) {
            return count(data);
        }

        // =========================
        // 2. SUM REQUEST
        // =========================
        if (q.contains("sum")) {
            // example: "sum price"
            String column = extractColumn(data,q);
            return sum(data, column);
        }

        // =========================
        // 3. AVERAGE REQUEST
        // =========================
        if (q.contains("average") || q.contains("avg")) {
            String column = extractColumn(data,q);
            return average(data, column);
        }

        // =========================
        // 4. GROUP BY REQUEST
        // =========================
        if (q.contains("group")) {
            String column = extractColumn(data,q);
            return groupBy(data, column);
        }

        // =========================
        // 5. FILTER REQUEST
        // =========================
        if (q.contains("show") || q.contains("filter")) {
            String column = extractColumn(data,q);
            List<String> value = extractValues(q);
            return filter(data, column, value.toString());
        }

        // =========================
        // DEFAULT
        // =========================
        return data;
    }
    private String extractColumn(List<Map<String, String>> data, String q) {

        if (data == null || data.isEmpty()) return null;

        Set<String> columns = data.get(0).keySet();

        q = q.toLowerCase();

        for (String col : columns) {
            String normalizedCol = col.toLowerCase();
            if (q.contains(normalizedCol)) {
                return col;
            }
        }
        return null;
    }

    private List<String> extractValues(String q) {

        List<String> values = new ArrayList<>();

        String[] words = q.toLowerCase().split(" ");

        for (String w : words) {

            // numbers (price, stock etc.)
            if (w.matches("\\d+")) {
                values.add(w);
            } else {
                values.add(w);
            }
        }

        return values;
    }
    public List<Map<String, String>> applyDynamic(List<Map<String, String>> data, Map aiQuery) {

        List<Map<String, String>> conditions =
                (List<Map<String, String>>) aiQuery.getOrDefault("conditions", new ArrayList<>());

        return data.stream()
                .filter(row -> {

                    for (Map<String, String> cond : conditions) {

                        String col = cond.get("column");
                        Object val = cond.get("value");

                        if (!row.get(col).equalsIgnoreCase(val.toString())) {
                            return false;
                        }
                    }
                    return true;
                })
                .toList();
    }
    public Map<String, Object> aggregate(List<Map<String, String>> data, Map<String, Object> aiQuery) {

        String operation = (String) aiQuery.get("operation");
        String column = (String) aiQuery.get("column");

        Map<String, Object> result = new HashMap<>();

        if ("count".equalsIgnoreCase(operation)) {
            result.put("value", data.size());
        }

        else if ("sum".equalsIgnoreCase(operation)) {
            int sum = data.stream()
                    .mapToInt(row -> Integer.parseInt(row.get(column)))
                    .sum();

            result.put("value", sum);
        }

        else if ("group".equalsIgnoreCase(operation)) {

            Map<String, Integer> grouped = new HashMap<>();

            for (Map<String, String> row : data) {
                String key = row.get(column);
                grouped.put(key, grouped.getOrDefault(key, 0) + 1);
            }

            result.put("groupedData", grouped);
        }

        return result;
    }

    public Map<String, Object> generateChart(Map<String, Object> aggResult, String operation) {

        Map<String, Object> chart = new HashMap<>();

        if (aggResult.containsKey("groupedData")) {

            Map<String, Integer> grouped = (Map<String, Integer>) aggResult.get("groupedData");

            chart.put("type", "bar");
            chart.put("labels", new ArrayList<>(grouped.keySet()));
            chart.put("data", new ArrayList<>(grouped.values()));
        }

        else if (aggResult.containsKey("value")) {

            chart.put("type", "bar");
            chart.put("labels", List.of("Total"));
            chart.put("data", List.of(aggResult.get("value")));
        }

        return chart;
    }
}
