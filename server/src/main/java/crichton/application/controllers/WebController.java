package crichton.application.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

//추후 client만들면 삭제
@Controller
public class WebController {

    @GetMapping("/")
    public String test() {
        return "index.html";
    }
}