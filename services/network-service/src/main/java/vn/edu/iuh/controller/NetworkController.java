package vn.edu.iuh.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/networks")
@RequiredArgsConstructor
public class NetworkController {
    @GetMapping
    public String hello(){
        return "Network Service Hello";
    }
}
