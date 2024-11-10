package com.dev.tasks.business.controller;

import com.dev.tasks.business.entity.Stat;
import com.dev.tasks.business.service.StatService;
import com.dev.tasks.business.util.MyLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatController {

    private final StatService statService;

    @Autowired
    public StatController(StatService statService) {
        this.statService = statService;
    }

    @PostMapping("/stat")
    public ResponseEntity<Stat> findByEmail(@RequestBody String email) {

        MyLogger.debugMethodName("StatController: findById() ------------------------------------------------- ");

        return ResponseEntity.ok(statService.findStat(email));
    }
}
