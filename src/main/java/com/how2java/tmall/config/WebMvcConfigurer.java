package com.how2java.tmall.config;
 
import com.how2java.tmall.interceptor.LoginInterceptor;
import com.how2java.tmall.interceptor.OtherInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
 
@Configuration
class WebMvcConfigurer extends WebMvcConfigurerAdapter{
     
    @Bean
    public LoginInterceptor getLoginIntercepter() {
        return new LoginInterceptor();
    }
    @Bean
    public OtherInterceptor getOtherInterCeptor(){return  new OtherInterceptor();}
    @Override
    public void addInterceptors(InterceptorRegistry registry){
        registry.addInterceptor(getLoginIntercepter())
        .addPathPatterns("/**");
        registry.addInterceptor((getOtherInterCeptor()))
        .addPathPatterns("/**");
    }
}