package com.ilionx.timetable.utils;

import static java.nio.charset.StandardCharsets.UTF_8;


import java.util.Base64;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordCreator {

  public static void main(String[] args) {
    BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
    String hashedPassword = bCryptPasswordEncoder.encode("password");
    System.out.printf("`password`: %s%n", hashedPassword);

    String keyData = RandomStringUtils.random(40);
    byte[] encoded = Base64.getEncoder().encode(keyData.getBytes(UTF_8));
    System.out.printf("JWT Hash: %s%n", new String(encoded, UTF_8));
  }
}
