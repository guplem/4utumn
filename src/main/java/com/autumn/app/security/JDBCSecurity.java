package com.autumn.app.security;


import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;


import javax.sql.DataSource;

@EnableWebSecurity
public class JDBCSecurity extends BaseSecurity {
    private DataSource dataSource;

    private static final String USERS_QUERY = "SELECT username, password, enabled from Users where username = ?";

    private static final String AUTHORITIES_QUERY = "SELECT username, role FROM Users WHERE username = ?";

    public JDBCSecurity(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    //Configure authentication manager
    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication()
                .dataSource(dataSource)
                .usersByUsernameQuery(USERS_QUERY)
                .authoritiesByUsernameQuery(AUTHORITIES_QUERY);
    }

}

