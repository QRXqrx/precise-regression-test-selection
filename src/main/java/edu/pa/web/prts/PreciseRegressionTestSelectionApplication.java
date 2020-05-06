package edu.pa.web.prts;

import org.apache.coyote.ProtocolHandler;
import org.apache.coyote.http11.AbstractHttp11Protocol;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
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

    // 处理上传文件大于10M的问题
    // 配置上传单个文件
    @Bean
    public TomcatServletWebServerFactory tomcatEmbedded() {
        // 定制的Tomcat服务器，为了解决上传文件大于10M时出现的连接重置问题
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
        tomcat.addConnectorCustomizers((connector) -> {
            // 协议处理器
            ProtocolHandler protocolHandler = connector.getProtocolHandler();

            if(protocolHandler instanceof AbstractHttp11Protocol<?>) {
                AbstractHttp11Protocol<?> http11Protocol = (AbstractHttp11Protocol<?>) protocolHandler;
                // -1 means unlimited
                http11Protocol.setMaxSwallowSize(-1);
            }
        });
        return tomcat;
    }

}
