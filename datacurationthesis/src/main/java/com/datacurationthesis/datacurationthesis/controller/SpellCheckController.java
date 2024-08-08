package com.datacurationthesis.datacurationthesis.controller;

import com.datacurationthesis.datacurationthesis.dto.SpellCheckResponseBasic;
import com.datacurationthesis.datacurationthesis.service.GreekSpellCkeckerService;
import com.datacurationthesis.datacurationthesis.service.SpellCheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SpellCheckController {

    private final SpellCheckService spellCheckService;

    @Autowired
    public SpellCheckController(SpellCheckService spellCheckService) {
        this.spellCheckService = spellCheckService;
    }

    @GetMapping("/api/spellcheck")
    public SpellCheckResponseBasic spellCheck(@RequestParam("text") String text) {
        return spellCheckService.checkAndSuggest(text);
    }
}
