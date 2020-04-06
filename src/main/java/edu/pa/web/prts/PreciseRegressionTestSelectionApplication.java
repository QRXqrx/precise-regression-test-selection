package edu.pa.web.prts;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.util.unit.DataSize;

import javax.servlet.MultipartConfigElement;

@SpringBootApplication
public class PreciseRegressionTestSelectionApplication {

    public static void main(String[] args) {
        SpringApplication.run(PreciseRegressionTestSelectionApplication.class, args);
    }

    // 配置文件夹上传，设置最大上传大小为4096MB
    @Bean
    public MultipartConfigElement getMultipartConfig() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize(DataSize.parse("4096MB")); // 和示例不一样，这里要用DataSize包装一下
        factory.setMaxRequestSize(DataSize.parse("4096MB"));
        return factory.createMultipartConfig();
    }

}
