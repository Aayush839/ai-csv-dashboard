package ai_dashboard.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class CsvData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String jsonData;

}
