package main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@EnableAutoConfiguration
@RestController
public class App {

    private static final String hello = "Hello World!";

    @RequestMapping("/api")
    public String home(){
        return hello;
    }
    public static void main(String[] args){
        System.out.println(hello);
        SpringApplication.run(App.class,args);
    }
}



