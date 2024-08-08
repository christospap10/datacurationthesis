package com.datacurationthesis.datacurationthesis.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/organizer")
    public String getOrganizerPage() {
        return "organizer";
    }

}
