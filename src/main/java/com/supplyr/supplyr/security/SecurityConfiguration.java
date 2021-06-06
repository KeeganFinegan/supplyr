package com.supplyr.supplyr.security;

import com.supplyr.supplyr.jwt.JwtConfiguration;
import com.supplyr.supplyr.jwt.JwtTokenVerifier;
import com.supplyr.supplyr.jwt.JwtUsernameAndPasswordAuthenticationFilter;
import com.supplyr.supplyr.service.SupplyrUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.crypto.SecretKey;


@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final PasswordEncoder passwordEncoder;

    private final SecretKey secretKey;

    private final JwtConfiguration jwtConfiguration;

    @Autowired
    public SecurityConfiguration(PasswordEncoder passwordEncoder, SecretKey secretKey, JwtConfiguration jwtConfiguration) {
        this.passwordEncoder = passwordEncoder;
        this.secretKey = secretKey;
        this.jwtConfiguration = jwtConfiguration;
    }

    @Autowired
    SupplyrUserDetailsService supplyrUserDetailsService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(daoAuthenticationProvider());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors()
                .and()
                .csrf().disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilter(new JwtUsernameAndPasswordAuthenticationFilter(authenticationManager(),
                        jwtConfiguration,
                        secretKey))
                .addFilterAfter(new JwtTokenVerifier(secretKey, jwtConfiguration),
                        JwtUsernameAndPasswordAuthenticationFilter.class)
                .authorizeRequests()
                .mvcMatchers("/api/v1/login").permitAll()
                .mvcMatchers("/static/**").permitAll()
                .mvcMatchers("/login").permitAll()
                .mvcMatchers("/home").permitAll()
                .mvcMatchers("/account").permitAll()
                .mvcMatchers("/admin").permitAll()
                .mvcMatchers("/api/v1/assets").hasAnyRole("USER", "ADMIN")
                .mvcMatchers(HttpMethod.GET, "/api/v1/assets").hasAnyRole("USER", "ADMIN")
                .mvcMatchers(HttpMethod.POST, "/api/v1/assets").hasRole("ADMIN")
                .mvcMatchers(HttpMethod.PUT, "/api/v1/assets").hasRole("ADMIN")
                .mvcMatchers(HttpMethod.PUT, "/api/v1/assets/asset-info").hasAnyRole("USER", "ADMIN")
                .mvcMatchers(HttpMethod.GET, "/api/v1/users/**").hasAnyRole("USER", "ADMIN")
                .mvcMatchers(HttpMethod.POST, "/api/v1/users/**").hasRole("ADMIN")
                .mvcMatchers("/api/v1/offers/**").hasAnyRole("USER", "ADMIN")
                .mvcMatchers(HttpMethod.POST, "/api/v1/organisational-unit").hasRole("ADMIN")
                .mvcMatchers(HttpMethod.PUT, "/api/v1/organisational-unit").hasRole("ADMIN")
                .mvcMatchers(HttpMethod.GET, "/api/v1/organisational-unit").hasAnyRole("USER", "ADMIN");


    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder);
        provider.setUserDetailsService(supplyrUserDetailsService);
        return provider;

    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").allowedOrigins("https://localhost:8443").allowCredentials(true)
                        .allowedMethods("GET", "POST", "PUT", "DELETE");
            }
        };
    }

}


