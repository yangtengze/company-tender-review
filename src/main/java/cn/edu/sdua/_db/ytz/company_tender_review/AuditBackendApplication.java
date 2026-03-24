package cn.edu.sdua._db.ytz.company_tender_review;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class AuditBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuditBackendApplication.class, args);
    }
}
