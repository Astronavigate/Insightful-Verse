package tech.ravon;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@MapperScan("tech.ravon.mapper")
@SpringBootApplication
@EnableScheduling
public class RavonApplication {

    public static void main(String[] args) {
        SpringApplication.run(RavonApplication.class, args);
    }

}
