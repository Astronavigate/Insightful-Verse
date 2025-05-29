package tech.ravon.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 指定静态资源的路径（可以是绝对路径）
        String staticPath = Paths.get(System.getProperty("user.dir"), "src", "main", "resources", "static").toUri().toString();
        registry.addResourceHandler("/**")
                .addResourceLocations(staticPath)
                .setCachePeriod(0); // 禁用缓存

        // 指定Code的路径（可以是绝对路径）
        String testPath = Paths.get(System.getProperty("user.dir"), "src", "main", "resources", "test").toUri().toString();
        registry.addResourceHandler("/**")
                .addResourceLocations(staticPath)
                .setCachePeriod(0); // 禁用缓存
    }
}
