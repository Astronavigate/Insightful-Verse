package tech.eagloxis;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@MapperScan("tech.eagloxis.mapper")
@SpringBootApplication
@EnableScheduling
public class EagloxisApplication {

    public static void main(String[] args) {
        SpringApplication.run(EagloxisApplication.class, args);
    }

}
