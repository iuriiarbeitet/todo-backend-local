package com.dev.tasks.auth.service;

import com.dev.tasks.auth.entity.Activity;
import com.dev.tasks.auth.entity.Role;
import com.dev.tasks.auth.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserDetailsImplTest {

    private UserDetailsImpl userDetails;

    @BeforeEach
    public void setup() {

        Role userRole = new Role();
        userRole.setName("ROLE_USER");

        User testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("testuser@example.com");
        testUser.setPassword("password123");
        testUser.setRoles(Set.of(userRole));

        testUser.activity = new Activity();
        testUser.activity.setActivated(true);

        userDetails = new UserDetailsImpl(testUser);
    }

    @Test
    public void testGetId() {
        assertEquals(1L, userDetails.getId());
    }

    @Test
    public void testGetEmail() {
        assertEquals("testuser@example.com", userDetails.getEmail());
    }

    @Test
    public void testIsActivated() {
        assertTrue(userDetails.isActivated());
    }

    @Test
    public void testGetUsername() {
        assertEquals("testuser", userDetails.getUsername());
    }

    @Test
    public void testGetPassword() {
        assertEquals("password123", userDetails.getPassword());
    }

    @Test
    public void testAuthorities() {
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        assertNotNull(authorities);
        assertEquals(1, authorities.size());
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Test
    public void testIsAccountNonExpired() {
        assertTrue(userDetails.isAccountNonExpired());
    }

    @Test
    public void testIsAccountNonLocked() {
        assertTrue(userDetails.isAccountNonLocked());
    }

    @Test
    public void testIsCredentialsNonExpired() {
        assertTrue(userDetails.isCredentialsNonExpired());
    }

    @Test
    public void testIsEnabled() {
        assertTrue(userDetails.isEnabled());
    }
}
