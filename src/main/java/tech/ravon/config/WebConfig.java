/**
 * Copyright 2025 Astronavigate
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tech.ravon.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 指定静态资源的路径
        String staticPath = Paths.get(System.getProperty("user.dir"), "src", "main", "resources", "static").toUri().toString();

        // 指定 Data 的路径
        String dataPath = Paths.get(System.getProperty("user.dir"), "data").toUri().toString();

        // 指定 Test 的路径
        String testPath = Paths.get(System.getProperty("user.dir"), "src", "main", "resources", "test").toUri().toString();
        registry.addResourceHandler("/**")
                .addResourceLocations(
                        staticPath,
                        dataPath,
                        testPath
                )
                .setCachePeriod(0); // 禁用缓存
    }
}
