package com.dev.tasks.business.controller;

import com.dev.tasks.business.entity.Category;
import com.dev.tasks.business.search.CategorySearchValues;
import com.dev.tasks.business.service.CategoryService;
import com.dev.tasks.business.util.MyLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/category")
public class CategoryController {

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping("/all")
    public ResponseEntity<List<Category>> findAll(@RequestBody String email){

        MyLogger.debugMethodName("CategoryController: findAll(email) ----------------------------------- ");

        return ResponseEntity.ok(categoryService.findAll(email));
    }

    @PutMapping("/add")
    public ResponseEntity<?> add(@RequestBody Category category){
        boolean check = category.getId() != null && category.getId() != 0;

        MyLogger.debugMethodName("CategoryController: add(category) ------------------------------------ ");

        if (check) {
            return new ResponseEntity<>("redundant param: id MUST be null", HttpStatus.NOT_ACCEPTABLE);
        }
        if (category.getTitle() == null || category.getTitle().trim().isEmpty()) {
            return new ResponseEntity<>("missed param: title", HttpStatus.NOT_ACCEPTABLE);
        }
        return ResponseEntity.ok(categoryService.add(category));
    }

    @PatchMapping("/update")
    public ResponseEntity<?> update(@RequestBody Category category) {

        boolean check = category.getId() == null || category.getId() == 0;
        MyLogger.debugMethodName("CategoryController: update(category) ---------------------------------- ");
        if (check) {
            return new ResponseEntity<>("missed param: id", HttpStatus.NOT_ACCEPTABLE);
        }
        if (category.getTitle() == null || category.getTitle().trim().isEmpty()) {
            return new ResponseEntity<>("missed param: title", HttpStatus.NOT_ACCEPTABLE);
        }
        categoryService.update(category);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {

        MyLogger.debugMethodName("CategoryController: delete(id) ----------------------------------------- ");

        if (id == null || id == 0) {
            return new ResponseEntity<>("missed param: id", HttpStatus.NOT_ACCEPTABLE);
        }

        try {
            categoryService.delete(id);
        } catch (EmptyResultDataAccessException e) {
            MyLogger.debugMethodName("CategoryController: delete(id) exception ------");
            return new ResponseEntity<>("id=" + id + " not found", HttpStatus.NOT_ACCEPTABLE);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/search")
    public ResponseEntity<List<Category>> search(@RequestBody CategorySearchValues categorySearchValues) {

        MyLogger.debugMethodName("CategoryController: search() ------------------------------------------ ");

        List<Category> list = categoryService.find(categorySearchValues.getTitle(), categorySearchValues.getEmail());

        return ResponseEntity.ok(list);
    }

    @PostMapping("/id")
    public ResponseEntity<?> findById(@RequestBody Long id) {

        MyLogger.debugMethodName("CategoryController: findById() ---------------------------------------- ");

        Category category;

        try {
            category = categoryService.findById(id);
        } catch (NoSuchElementException e) {
            MyLogger.debugMethodName("CategoryController: findById() exception -------");
            return new ResponseEntity<>("id=" + id + " not found", HttpStatus.NOT_ACCEPTABLE);
        }
        return ResponseEntity.ok(category);
    }
}
