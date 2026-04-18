package ai_dashboard.repo;

import ai_dashboard.entity.CsvData;
import ai_dashboard.service.CsvService;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CsvDataRepository extends JpaRepository<CsvData,Long> {
}
