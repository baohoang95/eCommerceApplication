package com.example.demo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

/**
 * Configures web security settings, including authentication and authorization rules,
 * JWT filters, and session management.
 * <p>
 * This configuration defines the security filters and permissions for various HTTP requests.
 * It also sets up JWT-based authentication, including the login and validation filters.
 * </p>
 */
@EnableWebSecurity
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    private UserDetailsServiceImpl userDetailsService;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    /**
     * Constructor to initialize the security configuration with the necessary
     * {@link UserDetailsServiceImpl} and {@link BCryptPasswordEncoder}.
     *
     * @param userDetailsService   the user details service for loading user information
     * @param bCryptPasswordEncoder the password encoder used for encoding passwords
     */
    public WebSecurityConfiguration(UserDetailsServiceImpl userDetailsService,
                                    BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userDetailsService = userDetailsService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    /**
     * Configures HTTP security, including request authorization, JWT filters, and session management.
     * <p>
     * The configuration disables CSRF protection, allows POST requests to the sign-up URL without authentication,
     * and applies JWT authentication and validation filters. It also sets the session management policy to stateless.
     * </p>
     *
     * @param http the {@link HttpSecurity} object for configuring HTTP security
     * @throws Exception if an error occurs during security configuration
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable().authorizeRequests()
                .antMatchers(HttpMethod.POST, SecurityConstants.SIGN_UP_URL).permitAll() // Allow sign-up without authentication
                .anyRequest().authenticated() // Require authentication for other requests
                .and()
                .addFilter(new JWTAuthenticationFilter(authenticationManager())) // Add JWT authentication filter
                .addFilter(new JWTAuthenticationValidationFilter(authenticationManager())) // Add JWT validation filter
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS); // Stateless session management

        // Unauthorized requests will receive a 401 HTTP status code
        http.exceptionHandling().authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));
    }

    /**
     * Configures web security to ignore specific URLs, such as H2 console endpoints.
     *
     * @param web the {@link WebSecurity} object for configuring web security
     * @throws Exception if an error occurs during web security configuration
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/h2/**"); // Ignore security for H2 console
    }

    /**
     * Creates and returns an {@link AuthenticationManager} bean.
     * <p>
     * This method is required for enabling authentication via JWT in the security configuration.
     * </p>
     *
     * @return the {@link AuthenticationManager} bean
     * @throws Exception if an error occurs while creating the authentication manager
     */
    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    /**
     * Configures the authentication manager with the custom {@link UserDetailsServiceImpl}
     * and password encoder for authenticating users.
     *
     * @param auth the {@link AuthenticationManagerBuilder} for configuring authentication settings
     * @throws Exception if an error occurs while configuring authentication
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder); // Set user details service and password encoder
    }
}
