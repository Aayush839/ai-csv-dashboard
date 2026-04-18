package ai_dashboard.Controller;

import ai_dashboard.dto.QueryRequest;
import ai_dashboard.service.CsvService;
import ai_dashboard.service.DataProcessingService;
import ai_dashboard.service.MainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

    @CrossOrigin(origins = "http://localhost:3000")
    @RestController
    @RequestMapping("/api/query")
    public class QueryController {

        @Autowired
        private DataProcessingService service;

        @Autowired
        private MainService mainService;

        @Autowired
        private CsvService csvService;

        @PostMapping("/ask")
        public Map<String, Object> ask(@RequestBody QueryRequest ques) {
            try{
                return (Map<String, Object>)mainService.processQuery(ques.getQuestion(),csvService.getAllData());
            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }

        @PostMapping("/filter")
        public List<Map<String, String>> filter(@RequestParam String column,@RequestParam String value) {
            try{

                return service.filter(csvService.getAllData(), column, value);
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @GetMapping("/group")
        public Map<String, List<Map<String, String>>> group(@RequestParam String column) {
            try{
                return service.groupBy(csvService.getAllData(), column);
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }
        @GetMapping("/sum")
        public  double sum(@RequestParam String column)  {
            try{

                return service.sum(csvService.getAllData(),column);
            }catch (Exception e){
                e.printStackTrace();
            }
            return 0.0d;
        }
    }

