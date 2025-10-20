package sum25.studentcode.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "sum25.studentcode.backend")
@EnableScheduling
public class BackendApplication {

    public static void main(String[] args)
    {
        SpringApplication.run(BackendApplication.class, args);
        System.out.println("Hello world!");
    }

}
