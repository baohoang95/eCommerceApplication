package com.example.demo.security;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.auth0.jwt.JWT;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;

/**
 * Filter to verify JWT tokens for authentication.
 * Validates tokens from the Authorization header and sets the security context upon successful verification.
 */
public class JWTAuthenticationValidationFilter extends BasicAuthenticationFilter {

    /**
     * Constructor to initialize the filter with an authentication manager.
     *
     * @param authManager the {@link AuthenticationManager} used for authentication processes
     */
    public JWTAuthenticationValidationFilter(AuthenticationManager authManager) {
        super(authManager);
    }

    /**
     * Filters incoming HTTP requests, checking for a valid JWT token in the Authorization header.
     * If the token is valid, the user is authenticated by setting the security context.
     *
     * @param req   the incoming HTTP request
     * @param res   the HTTP response
     * @param chain the filter chain to pass control to the next filter
     * @throws IOException      if an input or output exception occurs
     * @throws ServletException if a servlet exception occurs
     */
    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        String header = req.getHeader(SecurityConstants.HEADER_STRING);

        // Check if the header is missing or does not start with the expected prefix
        if (header == null || !header.startsWith(SecurityConstants.TOKEN_PREFIX)) {
            chain.doFilter(req, res);
            return;
        }

        // Retrieve authentication information from the token
        UsernamePasswordAuthenticationToken authentication = getAuthentication(req);

        // Set authentication information in the security context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Continue with the filter chain
        chain.doFilter(req, res);
    }

    /**
     * Extracts and validates the JWT token from the request, and returns an authentication token.
     *
     * @param req the incoming HTTP request containing the JWT token in the Authorization header
     * @return a {@link UsernamePasswordAuthenticationToken} if the token is valid; otherwise, null
     */
    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest req) {
        String token = req.getHeader(SecurityConstants.HEADER_STRING);

        // Validate and parse the JWT token
        if (token != null) {
            String user = JWT.require(HMAC512(SecurityConstants.SECRET.getBytes()))
                    .build()
                    .verify(token.replace(SecurityConstants.TOKEN_PREFIX, ""))
                    .getSubject();

            // Return an authentication token if the username is successfully extracted
            if (user != null) {
                return new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
            }
        }
        return null;
    }
}
