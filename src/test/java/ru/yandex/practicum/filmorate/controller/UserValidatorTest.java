package ru.yandex.practicum.filmorate.controller;

import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.utilites.AdaptedGson;

import java.time.LocalDate;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserValidatorTest {
    @Autowired
    private MockMvc mockMvc;
    private final Gson gson = AdaptedGson.getGson();
    private User testUser;

    @BeforeEach
    public void generateTestUser() {
        testUser = User.builder()
                .id(1)
                .email("mail@email.ru")
                .login("login")
                .name("Sergey")
                .birthday(LocalDate.of(1986, 8, 1))
                .build();
    }

    // 1. Case POST
    // 1.0. Post user with valid parameters
    @Test
    public void addUserWithCorrectParametersTest() throws Exception {
        var mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(testUser)))
                .andExpect(status().isOk())
                .andReturn();

        User returnedUser = gson.fromJson(mvcResult.getResponse().getContentAsString(), User.class);

        assertEquals(testUser, returnedUser);
    }

    // 1.1. Post user with invalid email cases
    @Test
    public void addUserWithBlankEmailTest() throws Exception {
        testUser.setEmail("");
        var expectedMessage = "Указан пустой email.";

        var response = mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(testUser)))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError()).andReturn();
        var message = Objects.requireNonNull(response.getResolvedException()).getMessage();

        assertTrue(message.contains(expectedMessage));
    }

    @Test
    public void addUserWithNullEmailTest() throws Exception {
        testUser.setEmail(null);

        var response = mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(testUser)))
                .andExpect(MockMvcResultMatchers.status().is5xxServerError()).andReturn();
        var message = Objects.requireNonNull(response.getResolvedException()).getMessage();

        assertTrue(message.contains("default message [email]]"));
        assertTrue(message.contains("default message [must not be null]]"));
    }

    @Test
    public void addUserWithIncorrectEmailTest() throws Exception {
        testUser.setEmail("incorrectEmail.ru");
        var expectedMessage = String.format("Email '%s' не содержит символ '@'.", testUser.getEmail());

        var response = mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(testUser)))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError()).andReturn();
        var message = Objects.requireNonNull(response.getResolvedException()).getMessage();

        assertTrue(message.contains(expectedMessage));
    }

    // 1.2. Post user with invalid login cases
    @Test
    public void addUserWithNullLoginTest() throws Exception {
        testUser.setLogin(null);

        var response = mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(testUser)))
                .andExpect(MockMvcResultMatchers.status().is5xxServerError()).andReturn();
        var message = Objects.requireNonNull(response.getResolvedException()).getMessage();

        assertTrue(message.contains("default message [login]]"));
        assertTrue(message.contains("default message [must not be null]]"));
    }

    @Test
    public void addUserWithBlankLoginTest() throws Exception {
        testUser.setLogin("");
        var expectedMessage = "Логин не указан.";

        var response = mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(testUser)))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError()).andReturn();
        var message = Objects.requireNonNull(response.getResolvedException()).getMessage();

        assertTrue(message.contains(expectedMessage));
    }

    @Test
    public void addUserWithSpacedLoginTest() throws Exception {
        testUser.setLogin("log in");
        var expectedMessage = "Логин содержит пробелы.";

        var response = mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(testUser)))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError()).andReturn();
        var message = Objects.requireNonNull(response.getResolvedException()).getMessage();

        assertTrue(message.contains(expectedMessage));
    }

    // 1.3. Post user with invalid birthday cases
    @Test
    public void addUserWithFutureBirthdayTest() throws Exception {
        testUser.setBirthday(LocalDate.of(2023, 8, 1));
        var expectedMessage = "Дата рождения не может быть в будущем.";

        var response = mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(testUser)))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError()).andReturn();
        var message = Objects.requireNonNull(response.getResolvedException()).getMessage();

        assertTrue(message.contains(expectedMessage));
    }

    // 2. Case PUT
    // 2.0. Put user with valid parameters
    @Test
    public void updateUserWithCorrectParametersTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(testUser)))
                .andExpect(status().isOk())
                .andReturn();

        var updTestUser = generateSecondTestUser();
        var mvcResult = mockMvc.perform(MockMvcRequestBuilders.put("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(updTestUser)))
                .andExpect(status().isOk())
                .andReturn();

        var returnedUser = gson.fromJson(mvcResult.getResponse().getContentAsString(), User.class);

        assertEquals(updTestUser, returnedUser);
    }

    // 2.1. Put user with invalid email cases
    @Test
    public void updateUserWithBlankEmailTest() throws Exception {
        testUser.setEmail("");
        var expectedMessage = "Указан пустой email.";

        var response = mockMvc.perform(MockMvcRequestBuilders.put("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(testUser)))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError()).andReturn();
        var message = Objects.requireNonNull(response.getResolvedException()).getMessage();

        assertTrue(message.contains(expectedMessage));
    }

    @Test
    public void updateUserWithNullEmailTest() throws Exception {
        testUser.setEmail(null);

        var response = mockMvc.perform(MockMvcRequestBuilders.put("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(testUser)))
                .andExpect(MockMvcResultMatchers.status().is5xxServerError()).andReturn();
        var message = Objects.requireNonNull(response.getResolvedException()).getMessage();

        assertTrue(message.contains("default message [email]]"));
        assertTrue(message.contains("default message [must not be null]]"));
    }

    @Test
    public void updateUserWithIncorrectEmailTest() throws Exception {
        testUser.setEmail("incorrectEmail.ru");
        var expectedMessage = String.format("Email '%s' не содержит символ '@'.", testUser.getEmail());

        var response = mockMvc.perform(MockMvcRequestBuilders.put("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(testUser)))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError()).andReturn();
        var message = Objects.requireNonNull(response.getResolvedException()).getMessage();

        assertTrue(message.contains(expectedMessage));
    }

    // 2.2. Put user with invalid login cases
    @Test
    public void updateUserWithNullLoginTest() throws Exception {
        testUser.setLogin(null);

        var response = mockMvc.perform(MockMvcRequestBuilders.put("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(testUser)))
                .andExpect(MockMvcResultMatchers.status().is5xxServerError()).andReturn();
        var message = Objects.requireNonNull(response.getResolvedException()).getMessage();

        assertTrue(message.contains("default message [login]]"));
        assertTrue(message.contains("default message [must not be null]]"));
    }

    @Test
    public void updateUserWithBlankLoginTest() throws Exception {
        testUser.setLogin("");
        var expectedMessage = "Логин не указан.";

        var response = mockMvc.perform(MockMvcRequestBuilders.put("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(testUser)))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError()).andReturn();
        var message = Objects.requireNonNull(response.getResolvedException()).getMessage();

        assertTrue(message.contains(expectedMessage));
    }

    @Test
    public void updateUserWithSpacedLoginTest() throws Exception {
        testUser.setLogin("log in");
        var expectedMessage = "Логин содержит пробелы.";

        var response = mockMvc.perform(MockMvcRequestBuilders.put("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(testUser)))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError()).andReturn();
        var message = Objects.requireNonNull(response.getResolvedException()).getMessage();

        assertTrue(message.contains(expectedMessage));
    }

    // 2.3. Put user with invalid birthday cases
    @Test
    public void updateUserWithFutureBirthdayTest() throws Exception {
        testUser.setBirthday(LocalDate.of(2023, 8, 1));
        var expectedMessage = "Дата рождения не может быть в будущем.";

        var response = mockMvc.perform(MockMvcRequestBuilders.put("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(testUser)))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError()).andReturn();
        var message = Objects.requireNonNull(response.getResolvedException()).getMessage();

        assertTrue(message.contains(expectedMessage));
    }

    public static User generateSecondTestUser() {
        return User.builder()
                .id(1)
                .email("updmail@email.ru")
                .login("updatedLogin")
                .name("Olga")
                .birthday(LocalDate.of(1991, 7, 5))
                .build();
    }
}