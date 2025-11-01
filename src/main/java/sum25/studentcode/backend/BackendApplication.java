package sum25.studentcode.backend;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.TimeZone;

@SpringBootApplication(scanBasePackages = "sum25.studentcode.backend")
@EnableScheduling
public class BackendApplication {

    @PostConstruct
    public void init() {
        // ✅ Ép toàn bộ JVM dùng múi giờ Việt Nam
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        System.out.println("✅ Default timezone set to: " + TimeZone.getDefault().getID());
    }

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
        System.out.println("Hello World !!!");
    }
}
