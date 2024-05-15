package com.eep.tickets;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.eep.tickets.controllers.UserController;
import com.eep.tickets.models.Role;
import com.eep.tickets.models.User;
import com.eep.tickets.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // Optional setup if needed
    }

    // GET Tests
    @Test
    void testGetAllUsers() throws Exception {
        User user1 = new User("email1@example.com", "password1", "FirstName1", "LastName1", Role.USER);
        User user2 = new User("email2@example.com", "password2", "FirstName2", "LastName2", Role.ADMIN);
        List<User> users = Arrays.asList(user1, user2);

        when(userService.getAll()).thenReturn(users);

        mockMvc.perform(get("/api/v1/user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("email1@example.com"))
                .andExpect(jsonPath("$[1].email").value("email2@example.com"));
    }

    @Test
    void testGetAllUsersEmpty() throws Exception {
        when(userService.getAll()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/v1/user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void testCreateUser() throws Exception {
        Map<String, String> userMap = Map.of(
                "firstName", "FirstName",
                "lastName", "LastName",
                "email", "email@example.com",
                "password", "password",
                "role", "USER"
        );
        User user = new User("email@example.com", "password", "FirstName", "LastName", Role.USER);
        when(userService.create(any(User.class))).thenReturn(user);

        mockMvc.perform(post("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userMap)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("email@example.com"));
    }

    @Test
    void testCreateUserMissingField() throws Exception {
        String userJson = "{\"email\":\"email@example.com\",\"password\":\"password\",\"firstName\":\"FirstName\"}"; // Missing lastName and role

        mockMvc.perform(post("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isBadRequest());
    }

    // PUT Tests
    @Test
    void testUpdateUser() throws Exception {
        Long userId = 1L;
        User user = new User("email@example.com", "password", "FirstName", "LastName", Role.USER);
        User updatedUser = new User("email_updated@example.com", "password", "FirstName", "LastName", Role.ADMIN);
        when(userService.update(eq(userId), any(User.class))).thenReturn(updatedUser);

        mockMvc.perform(put("/api/v1/user/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("email_updated@example.com"));
    }

    @Test
    void testUpdateUserNotFound() throws Exception {
        Long userId = 1L;
        User user = new User("email@example.com", "password", "FirstName", "LastName", Role.USER);
        when(userService.update(eq(userId), any(User.class))).thenReturn(null);

        mockMvc.perform(put("/api/v1/user/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isNotFound());
    }

    // DELETE Tests
    @Test
    void testDeleteUser() throws Exception {
        Long userId = 1L;
        doNothing().when(userService).delete(userId);

        mockMvc.perform(delete("/api/v1/user/{id}", userId))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteUserNotFound() throws Exception {
        Long userId = 1L;
        doThrow(new RuntimeException("User not found")).when(userService).delete(userId);

        mockMvc.perform(delete("/api/v1/user/{id}", userId))
                .andExpect(status().isNotFound());
    }
}
