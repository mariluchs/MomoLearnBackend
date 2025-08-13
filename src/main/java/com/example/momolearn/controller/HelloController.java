package com.example.momolearn.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;


@RestController //Klasse ist ein REST-Controller
public class HelloController {

    @GetMapping("/hello") //Mapping für GET-Anfragen auf /hello
    public String hello() {
        return "Hallo von SpringBoot"; //Antwort für GET-Anfrage
    }
}
    

