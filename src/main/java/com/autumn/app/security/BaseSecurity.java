package com.autumn.app.security;

import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

public class BaseSecurity extends WebSecurityConfigurerAdapter {

    /*@SuppressWarnings("deprecation")
    @Bean
    public static NoOpPasswordEncoder passwordEncoder() {
        return (NoOpPasswordEncoder) NoOpPasswordEncoder.getInstance();
    }*/

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/").permitAll()
                .antMatchers("/css/**").permitAll()
                .antMatchers("fragments/**").permitAll()
                .antMatchers("/img/**").permitAll()
                .antMatchers("/note/**").permitAll()
                .antMatchers("/search/**").permitAll()
                .antMatchers("/**/configuration").authenticated()
                .antMatchers("/feed").authenticated()
                .antMatchers("/notifications").authenticated()
                .antMatchers("/mentions").authenticated()
                .antMatchers("/deleteUser").authenticated()
                .antMatchers("/users/**/blocked").authenticated()
                .antMatchers("/users/**").permitAll()
                .antMatchers("/createNote/**").authenticated()
                .antMatchers("/createNote").authenticated()
                .antMatchers("/login").anonymous()
                .antMatchers("/registerUser").anonymous()
                .antMatchers("/profile/**").permitAll()
                .antMatchers("/loginError").anonymous()

                .anyRequest().permitAll()//hasRole("ADMIN")
                .and()
                .formLogin().loginPage("/login").defaultSuccessUrl("/feed").failureUrl("/loginError") //to use forms (web)
                .and()
                .exceptionHandling().accessDeniedPage("/feed")
                .and()
                .httpBasic() //to use http headers REST
                .and()
                .rememberMe()
                .tokenValiditySeconds(2419200)
                .key("tecnocampus")
                .and()
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/");

        http
                .csrf().disable()
                .headers()
                .frameOptions().disable();


    }
}