package org.fdfsfus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@SpringBootApplication(scanBasePackages = {"org.fdfsfus","org.n3r.idworker"})
//@EnableDiscoveryClient //用于注册服务（zookeeper）
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class,args);
    }

    //解决跨域问题
    @Configuration
    public static class CorsConfig {
        @Bean
        public CorsFilter corsFilter() {
            //1.添加CORS配置信息
            CorsConfiguration config = new CorsConfiguration();
            //放行哪些原始域
            config.addAllowedOrigin("*");
            //是否发送Cookie信息
            config.setAllowCredentials(true);
            //放行哪些原始域(请求方式)
            config.addAllowedMethod("*");
            //放行哪些原始域(头部信息)
            config.addAllowedHeader("*");
            //暴露哪些头部信息（因为跨域访问默认不能获取全部头部信息）
            config.addExposedHeader("content-type");
            //2.添加映射路径
            UrlBasedCorsConfigurationSource configSource = new UrlBasedCorsConfigurationSource();
            configSource.registerCorsConfiguration("/**", config);
            //3.返回新的CorsFilter.
            return new CorsFilter(configSource);
        }
    }
}
