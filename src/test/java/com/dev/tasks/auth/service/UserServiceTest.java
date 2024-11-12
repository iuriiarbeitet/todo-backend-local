package com.dev.tasks.auth.service;

import static org.junit.jupiter.api.Assertions.*;

import com.dev.tasks.auth.entity.Activity;
import com.dev.tasks.auth.entity.Role;
import com.dev.tasks.auth.entity.User;
import com.dev.tasks.auth.repository.ActivityRepository;
import com.dev.tasks.auth.repository.RoleRepository;
import com.dev.tasks.auth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private RoleRepository roleRepository;

    @MockBean
    private ActivityRepository activityRepository;

    private User testUser;
    private Activity testActivity;
    private Role testRole;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("testuser@example.com");

        testActivity = new Activity();
        testActivity.setUuid("test-uuid");

        testRole = new Role();
        testRole.setName("USER");
    }

    @Test
    void testRegister() {

        userService.register(testUser, testActivity);

        verify(userRepository, times(1)).save(testUser);
        verify(activityRepository, times(1)).save(testActivity);
    }

    @Test
    void testUserExists() {

        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        boolean exists = userService.userExists("testuser", "testuser@example.com");

        assertTrue(exists);
        verify(userRepository, times(1)).existsByUsername("testuser");
    }

    @Test
    void testFindByName() {

        when(roleRepository.findByName("USER")).thenReturn(Optional.of(testRole));

        Optional<Role> role = userService.findByName("USER");

        assertTrue(role.isPresent());
        assertEquals("USER", role.get().getName());
    }

    @Test
    void testFindActivityByUserId() {

        when(activityRepository.findByUserId(1L)).thenReturn(Optional.of(testActivity));

        Optional<Activity> activity = userService.findActivityByUserId(1L);

        assertTrue(activity.isPresent());
        assertEquals("test-uuid", activity.get().getUuid());
    }

    @Test
    void testUpdatePassword() {

        when(userRepository.updatePassword(any(String.class), any(String.class))).thenReturn(1);

        int updatedRows = userService.updatePassword("newpassword", "testuser@example.com");

        assertEquals(1, updatedRows);
        verify(userRepository, times(1)).updatePassword("newpassword", "testuser@example.com");
    }
}

