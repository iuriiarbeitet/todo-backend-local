package com.dev.tasks.business.service;

import com.dev.tasks.auth.entity.User;
import com.dev.tasks.auth.repository.UserRepository;
import com.dev.tasks.business.entity.Category;
import com.dev.tasks.business.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class CategoryServiceTest {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private Category testCategory;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUsername("userTest");
        testUser.setEmail("user@example.com");
        testUser.setPassword("password");
        userRepository.save(testUser);

        testCategory = new Category();
        testCategory.setTitle("Test Category");
        testCategory.setUser(testUser);
        categoryRepository.save(testCategory);
    }

    @Test
    void testAddCategory() {
        Category newCategory = new Category();
        newCategory.setTitle("New Category");
        newCategory.setUser(testUser);

        Category savedCategory = categoryService.add(newCategory);

        assertNotNull(savedCategory.getId());
        assertEquals("New Category", savedCategory.getTitle());
        assertEquals(testUser.getId(), savedCategory.getUser().getId());
    }

    @Test
    void testFindAllCategories() {
        List<Category> categories = categoryService.findAll("user@example.com");

        assertFalse(categories.isEmpty());
        assertTrue(categories.stream().anyMatch(c -> c.getTitle().equals("Test Category")));
    }

    @Test
    void testFindCategoryById() {
        Category foundCategory = categoryService.findById(testCategory.getId());

        assertNotNull(foundCategory);
        assertEquals(testCategory.getId(), foundCategory.getId());
    }

    @Test
    void testUpdateCategory() {
        testCategory.setTitle("Updated Category");
        Category updatedCategory = categoryService.update(testCategory);

        assertEquals("Updated Category", updatedCategory.getTitle());
    }

    @Test
    void testDeleteCategory() {
        categoryService.delete(testCategory.getId());

        assertThrows(EntityNotFoundException.class, () -> categoryService.findById(testCategory.getId()));
    }

    @Test
    void testFindCategoryByTitle() {
        List<Category> categories = categoryService.find("Test Category", "user@example.com");

        assertFalse(categories.isEmpty());
        assertEquals("Test Category", categories.get(0).getTitle());
    }
}
