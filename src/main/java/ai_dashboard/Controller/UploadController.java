package ai_dashboard.Controller;

import ai_dashboard.service.CsvService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(value = "/csv")
public class UploadController {

    @Autowired
    public CsvService csvService;
    @PostMapping(value = "/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile myfile){
        if(myfile==null)    return ResponseEntity.badRequest().body("File is Empty");
        if (!myfile.getOriginalFilename().endsWith(".csv")) {
            return ResponseEntity.badRequest().body("Only CSV files are allowed");
        }
        if (myfile.getSize() > 2 * 1024 * 1024) {
            return ResponseEntity.badRequest().
                    body("File size exceeds 2MB limit");
        }
        try{
            return ResponseEntity.ok().body(csvService.uploadCsvFile(myfile));
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error uploading CSV:" + e.getMessage());
        }
    }
}
