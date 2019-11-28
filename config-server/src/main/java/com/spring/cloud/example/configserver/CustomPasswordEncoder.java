package com.spring.cloud.example.configserver;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Objects;

/**
 * 解决错误：There is no PasswordEncoder mapped for the id "null"
 */
public class CustomPasswordEncoder implements PasswordEncoder {

    @Override
    public String encode(CharSequence charSequence) {
        return charSequence.toString();
    }

    @Override
    public boolean matches(CharSequence charSequence, String s) {
        return Objects.equals(s, charSequence.toString());
    }

}
