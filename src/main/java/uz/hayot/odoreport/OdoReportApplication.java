package uz.hayot.odoreport;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class OdoReportApplication {

    public static void main(String[] args) {
        SpringApplication.run(OdoReportApplication.class, args);
    }

}
