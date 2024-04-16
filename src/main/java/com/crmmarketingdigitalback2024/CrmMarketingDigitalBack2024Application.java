package com.crmmarketingdigitalback2024;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sound.midi.Soundbank;

@SpringBootApplication
public class CrmMarketingDigitalBack2024Application {

    public static void main(String[] args) {
        SpringApplication.run(CrmMarketingDigitalBack2024Application.class, args);
    }

//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
//    @Bean
//    public CommandLineRunner createPassword(){
//        return args -> {
//            System.out.println(passwordEncoder.encode("Tommy1104"));
//            System.out.println(passwordEncoder.encode("user"));
//        };
//    }

}
