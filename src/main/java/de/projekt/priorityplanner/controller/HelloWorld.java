package de.projekt.priorityplanner.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mia")
public class HelloWorld {

    @GetMapping("/hello")
    public String hello(){
        return "hello world";
    }

    @GetMapping("/hello2/{s}")
    public String hello2(@PathVariable("s") String s){
        return "hello " + s;
    }

    @GetMapping(value="/hello3", produces="application/json")
    public String hello3(@RequestParam(name="name", defaultValue = "world") String s){
        return "hello " + s;
    }

}

