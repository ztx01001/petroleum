package com.university.springboot_petroleum.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

//跨域问题的解决
@Configuration
public class CorsConfig {

	@Bean
    public CorsFilter corsFilter() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        // 使用通配符* 允许所有的域请求
        corsConfiguration.addAllowedOrigin("*");
        // 使用通配符* 允许所有请求头字段
        corsConfiguration.addAllowedHeader("*");
        // 使用通配符* 允许所有请求头方法类型
        corsConfiguration.addAllowedMethod("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 处理请求映射
        source.registerCorsConfiguration("/**", corsConfiguration);
        
        return new CorsFilter(source);
    }

}
