package com.example.demo.security;

/**
 * SecurityConstants defines the constant values used for JWT authentication and authorization.
 * This includes secret keys, token expiration times, and HTTP header configurations.
 */
public class SecurityConstants {

    /**
     * The secret key used to sign the JWT tokens.
     */
    public static final String SECRET = "oursecretkey";

    /**
     * The expiration time for JWT tokens, in milliseconds (10 days).
     */
    public static final long EXPIRATION_TIME = 864_000_000; // 10 days

    /**
     * The prefix to be added to the JWT token in the Authorization header.
     */
    public static final String TOKEN_PREFIX = "Bearer ";

    /**
     * The HTTP header field used to pass the JWT token in requests.
     */
    public static final String HEADER_STRING = "Authorization";

    /**
     * The endpoint for user sign-up, allowing access without authentication.
     */
    public static final String SIGN_UP_URL = "/api/user/create";
}
