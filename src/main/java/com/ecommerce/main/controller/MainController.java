package com.ecommerce.main.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class MainController {

    @GetMapping(value = {"/main", "/",  ""})
    public ResponseEntity<String> enter() {
        log.info("Enter main page");
        return ResponseEntity.ok("Enter main page");
    }
}
