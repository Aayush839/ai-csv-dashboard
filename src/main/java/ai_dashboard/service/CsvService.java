package ai_dashboard.service;

import ai_dashboard.entity.CsvData;
import ai_dashboard.exceptions.CsvException;
import ai_dashboard.repo.CsvDataRepository;
import ai_dashboard.utility.Utilities;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.*;

@Service
public class CsvService {


    @Autowired
    private CsvDataRepository repository;

    @Autowired
    private Utilities utilities;

    public String uploadCsvFile(MultipartFile file) throws IOException {

        this.deleteAllDataFromDb();
        Reader reader = new InputStreamReader(file.getInputStream());
        CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader());

        List<String> headers = csvParser.getHeaderNames();
        if (headers.isEmpty()) {
            throw new CsvException("CSV must have headers");
        }

        int rowCount = 0;
        for (CSVRecord record : csvParser) {
            Map<String, String> row = new HashMap<>();

            for (Map.Entry<String, String> entry : record.toMap().entrySet()) {
                String value = entry.getValue();
                row.put(entry.getKey(), value!=null?value:"");
            }

            CsvData data = new CsvData();
            data.setJsonData(utilities.getJsonStringFromMap(row));
            repository.save(data);
            rowCount++;
        }
        return "CSV uploaded successfully Rows processed: " + rowCount;
    }

    public List<Map<String, String>> getAllData() throws Exception {

        List<CsvData> list = repository.findAll();
        List<Map<String, String>> result = new ArrayList<>();

        for (CsvData data : list) {
            String jsonData = data.getJsonData();
            Map<String, String> jsonasMap = utilities.getJsonAsMap(jsonData);
            result.add(jsonasMap);
        }

        return result;
    }
    public void deleteAllDataFromDb()throws IOException{
        repository.deleteAll();
    }
}
