package com.etf.crm.config;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.security.SecureRandom;

public class SecurityConfig {

    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(10, new SecureRandom());

    public static String encode(String passWord) {
        return encoder.encode(passWord);
    }

    public static boolean matches(String rawPassword, String encodedPassword) {
        return encoder.matches(rawPassword, encodedPassword);
    }
}
