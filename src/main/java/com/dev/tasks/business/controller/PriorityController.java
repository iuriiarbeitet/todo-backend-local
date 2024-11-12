package com.dev.tasks.business.controller;


import com.dev.tasks.business.entity.Priority;
import com.dev.tasks.business.search.PrioritySearchValues;
import com.dev.tasks.business.service.PriorityService;
import com.dev.tasks.business.util.MyLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/priority")

public class PriorityController {

    private final PriorityService priorityService;

    @Autowired
    public PriorityController(PriorityService priorityService) {
        this.priorityService = priorityService;
    }

    @PostMapping("/all")
    public ResponseEntity<List<Priority>> findAll(@RequestBody String email) {

        MyLogger.debugMethodName("PriorityController: findAll() ------------------------------------------- ");

        return ResponseEntity.ok(priorityService.findAll(email));
    }

    @PutMapping("/add")
    public ResponseEntity<?> add(@RequestBody Priority priority) {
        boolean check = priority.getId() != null && priority.getId() != 0;

        MyLogger.debugMethodName("PriorityController: add() ------------------------------------------------ ");

        if (check) {
            return new ResponseEntity<>("redundant param: id MUST be null", HttpStatus.NOT_ACCEPTABLE);
        }
        if (priority.getTitle() == null || priority.getTitle().trim().isEmpty()) {
            return new ResponseEntity<>("missed param: title", HttpStatus.NOT_ACCEPTABLE);
        }
        if (priority.getColor() == null || priority.getColor().trim().isEmpty()) {
            return new ResponseEntity<>("missed param: color", HttpStatus.NOT_ACCEPTABLE);
        }
        return ResponseEntity.ok(priorityService.add(priority));
    }

    @PatchMapping("/update")
    public ResponseEntity<?> update(@RequestBody Priority priority) {
        boolean check = priority.getId() == null || priority.getId() == 0;

        MyLogger.debugMethodName("PriorityController: update() ---------------------------------------------- ");

        if (check) {
            return new ResponseEntity<>("missed param: id", HttpStatus.NOT_ACCEPTABLE);
        }
        if (priority.getTitle() == null || priority.getTitle().trim().isEmpty()) {
            return new ResponseEntity<>("missed param: title", HttpStatus.NOT_ACCEPTABLE);
        }
        if (priority.getColor() == null || priority.getColor().trim().isEmpty()) {
            return new ResponseEntity<>("missed param: color", HttpStatus.NOT_ACCEPTABLE);
        }
        priorityService.update(priority);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/id")
    public ResponseEntity<?> findById(@RequestBody Long id) {

        MyLogger.debugMethodName("PriorityController: findById() -------------------------------------------- ");

        if (id == null || id == 0) {
            return new ResponseEntity<>("missed param: id", HttpStatus.NOT_ACCEPTABLE);
        }
        Priority priority;

        try {
            priority = priorityService.findById(id);
        } catch (NoSuchElementException e) {
           MyLogger.debugMethodName("PriorityController: findById() exception --------");
            return new ResponseEntity<>("id=" + id + " not found", HttpStatus.NOT_ACCEPTABLE);
        }
        return ResponseEntity.ok(priority);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {

        MyLogger.debugMethodName("PriorityController: delete() ---------------------------------------------- ");

        try {
            priorityService.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
           MyLogger.debugMethodName("PriorityController: delete() exception");
            return new ResponseEntity<>("id=" + id + " not found", HttpStatus.NOT_ACCEPTABLE);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/search")
    public ResponseEntity<List<Priority>> search(@RequestBody PrioritySearchValues prioritySearchValues) {

        MyLogger.debugMethodName("PriorityController: search() ----------------------------------------------- ");

        return ResponseEntity.ok(priorityService.find(prioritySearchValues.getTitle(), prioritySearchValues.getEmail()));
    }

}
