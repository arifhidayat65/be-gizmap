package com.apps.config;

import com.apps.security.interceptor.AuthenticationInterceptor;
import com.apps.security.middleware.DashboardMiddleware;
import com.apps.security.middleware.LoginMiddleware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Autowired
    private AuthenticationInterceptor authenticationInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authenticationInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                    "/api/auth/signin",
                    "/api/auth/signup",
                    "/api/auth/refreshtoken",
                    "/error"
                );
    }

    @Bean
    public FilterRegistrationBean<LoginMiddleware> loginMiddlewareRegistration(LoginMiddleware loginMiddleware) {
        FilterRegistrationBean<LoginMiddleware> registration = new FilterRegistrationBean<>();
        registration.setFilter(loginMiddleware);
        registration.addUrlPatterns("/api/auth/signin");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);  // Execute first
        return registration;
    }

    @Bean
    public FilterRegistrationBean<DashboardMiddleware> dashboardMiddlewareRegistration(DashboardMiddleware dashboardMiddleware) {
        FilterRegistrationBean<DashboardMiddleware> registration = new FilterRegistrationBean<>();
        registration.setFilter(dashboardMiddleware);
        registration.addUrlPatterns("/api/dashboard/*", "/api/admin/*", "/api/reports/*");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE + 1);  // Execute after login middleware
        return registration;
    }
}
