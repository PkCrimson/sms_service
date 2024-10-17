package com.side.auth.utils;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class CodeGenerator {

    public static String GenerateSmsCode() {
        StringBuilder code = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 4; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }
}
