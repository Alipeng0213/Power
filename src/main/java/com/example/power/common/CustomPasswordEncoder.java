package com.example.power.common;

import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @Description TODO
 * @Author hy
 * @Date 2022/7/10
 */
public class CustomPasswordEncoder implements PasswordEncoder {

    @Override
    public String encode(CharSequence charSequence) {
        return charSequence.toString();
    }

    @Override
    public boolean matches(CharSequence charSequence, String s) {
        return s.equals(charSequence.toString());
    }

}
