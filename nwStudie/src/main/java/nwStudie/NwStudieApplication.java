package nwStudie;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@ComponentScan
@SpringBootApplication
@EnableTransactionManagement
public class NwStudieApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(NwStudieApplication.class, args);
    }
}
