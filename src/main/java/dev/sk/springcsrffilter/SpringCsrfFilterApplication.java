package dev.sk.springcsrffilter;

import jakarta.servlet.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@SpringBootApplication
public class SpringCsrfFilterApplication {

    @Component
    static class CSRFLogger implements Filter {
        @Override
        public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
            Object csrf = servletRequest.getAttribute("_csrf");
            CsrfToken csrfToken = (CsrfToken) csrf;
            System.out.println("CSRF Token = "+csrfToken.getToken());
            filterChain.doFilter(servletRequest,servletResponse);
        }
    }

    @RestController
    static class MyController{
        @RequestMapping("/csrf")
        public String main(){
            return "Hello CSRF Filter...";
        }
    }

    @Configuration
    static class Config{
        @Autowired
        CSRFLogger csrfLogger;
        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity security)throws Exception{
            security.addFilterAfter(csrfLogger, CsrfFilter.class);
            security.authorizeRequests(e->e.anyRequest().permitAll());
            return security.build();
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringCsrfFilterApplication.class, args);
    }

}
