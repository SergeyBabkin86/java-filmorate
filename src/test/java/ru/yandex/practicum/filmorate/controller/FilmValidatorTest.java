package ru.yandex.practicum.filmorate.controller;

import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.utilites.AdaptedGson;

import java.time.LocalDate;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = FilmController.class)
public class FilmValidatorTest {
    @Autowired
    private MockMvc mockMvc;
    private final Gson gson = AdaptedGson.getGson();
    private Film testFilm;

    @BeforeEach
    public void generateTestFilm() {
        testFilm = Film.builder()
                .id(1)
                .name("Battleship Potemkin")
                .description("Description")
                .releaseDate(LocalDate.of(1925, 1, 1))
                .duration(75)
                .build();
    }

    // 1. Case POST
    // 1.0. Post film with valid parameters
    @Test
    public void addFilmWithCorrectParametersTest() throws Exception {
        var mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(testFilm)))
                .andExpect(status().isOk())
                .andReturn();

        Film returnedFilm = gson.fromJson(mvcResult.getResponse().getContentAsString(), Film.class);

        assertEquals(testFilm, returnedFilm);
    }

    // 1.1 Post film with invalid name cases
    @Test
    public void addNewFilmWithBlankNameTest() throws Exception {
        testFilm.setName("");
        var expectedMessage = "Не указано название фильма.";

        var response = mockMvc.perform(MockMvcRequestBuilders.post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(testFilm)))
                .andExpect(MockMvcResultMatchers.status().is5xxServerError()).andReturn();
        var message = Objects.requireNonNull(response.getResolvedException()).getMessage();

        assertTrue(message.contains(expectedMessage));
    }

    @Test
    public void addFilmWithNullNameTest() throws Exception {
        testFilm.setName(null);

        var response = mockMvc.perform(MockMvcRequestBuilders.post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(testFilm)))
                .andExpect(status().is4xxClientError()).andReturn();
        var message = Objects.requireNonNull(response.getResolvedException()).getMessage();

        assertTrue(message.contains("default message [name]]"));
        assertTrue(message.contains("default message [must not be null]]"));
    }

    // 1.2. Post film with invalid description
    @Test
    public void addFilmWithMoreThanTwoHundredsCharsNameTest() throws Exception {
        String filmName = "";
        var sb = new StringBuilder("Abc");
        for (int i = 0; i < 100; i++) {
            filmName = sb.append("Abc").toString();
        }

        var expectedMessage = "Описание фильма более 200 символов.";

        testFilm.setDescription(filmName);

        var response = mockMvc.perform(MockMvcRequestBuilders.post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(testFilm)))
                .andExpect(MockMvcResultMatchers.status().is5xxServerError()).andReturn();
        var message = Objects.requireNonNull(response.getResolvedException()).getMessage();

        assertTrue(message.contains(expectedMessage));
    }

    // 1.3. Post film with invalid release date

    @Test
    public void addFilmWithIncorrectReleaseDateTest() throws Exception {

        var expectedMessage = "Дата релиза не может быть ранее 28 декабря 1895";

        testFilm.setReleaseDate(LocalDate.of(1893, 12, 28));

        var response = mockMvc.perform(MockMvcRequestBuilders.post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(testFilm)))
                .andExpect(MockMvcResultMatchers.status().is5xxServerError()).andReturn();
        var message = Objects.requireNonNull(response.getResolvedException()).getMessage();

        assertTrue(message.contains(expectedMessage));
    }

    // 1.4. Post film with invalid duration
    @Test
    public void addFilmWithNegativeDurationTest() throws Exception {

        var expectedMessage = "Продолжительность фильма отрицательная.";

        testFilm.setDuration(-10);

        var response = mockMvc.perform(MockMvcRequestBuilders.post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(testFilm)))
                .andExpect(MockMvcResultMatchers.status().is5xxServerError()).andReturn();
        var message = Objects.requireNonNull(response.getResolvedException()).getMessage();

        assertTrue(message.contains(expectedMessage));
    }

    // 2. Case PUT
    // 2.0. Put film with valid parameters
    @Test
    public void updateFilmWithCorrectParametersTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(testFilm)))
                .andExpect(status().isOk())
                .andReturn();

        var updTestFilm = generateSecondTestFilm();
        var mvcResult = mockMvc.perform(MockMvcRequestBuilders.put("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(updTestFilm)))
                .andExpect(status().isOk())
                .andReturn();

        var returnedFilm = gson.fromJson(mvcResult.getResponse().getContentAsString(), Film.class);

        assertEquals(updTestFilm, returnedFilm);
    }

    // 2.1 Put film with invalid name cases
    @Test
    public void updateNewFilmWithBlankNameTest() throws Exception {
        testFilm.setName("");
        var expectedMessage = "Не указано название фильма.";

        var response = mockMvc.perform(MockMvcRequestBuilders.put("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(testFilm)))
                .andExpect(MockMvcResultMatchers.status().is5xxServerError()).andReturn();
        var message = Objects.requireNonNull(response.getResolvedException()).getMessage();

        assertTrue(message.contains(expectedMessage));
    }

    @Test
    public void updateFilmWithNullNameTest() throws Exception {
        testFilm.setName(null);

        var response = mockMvc.perform(MockMvcRequestBuilders.put("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(testFilm)))
                .andExpect(status().is4xxClientError()).andReturn();
        var message = Objects.requireNonNull(response.getResolvedException()).getMessage();

        assertTrue(message.contains("default message [name]]"));
        assertTrue(message.contains("default message [must not be null]]"));
    }

    // 2.2. Put film with invalid description
    @Test
    public void updateFilmWithMoreThanTwoHundredsCharsNameTest() throws Exception {
        String filmName = "";
        var sb = new StringBuilder("Abc");
        for (int i = 0; i < 100; i++) {
            filmName = sb.append("Abc").toString();
        }

        var expectedMessage = "Описание фильма более 200 символов.";

        testFilm.setDescription(filmName);

        var response = mockMvc.perform(MockMvcRequestBuilders.post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(testFilm)))
                .andExpect(MockMvcResultMatchers.status().is5xxServerError()).andReturn();
        var message = Objects.requireNonNull(response.getResolvedException()).getMessage();

        assertTrue(message.contains(expectedMessage));
    }

    // 2.3. Put film with invalid release date

    @Test
    public void updateFilmWithIncorrectReleaseDateTest() throws Exception {

        var expectedMessage = "Дата релиза не может быть ранее 28 декабря 1895";

        testFilm.setReleaseDate(LocalDate.of(1893, 12, 28));

        var response = mockMvc.perform(MockMvcRequestBuilders.post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(testFilm)))
                .andExpect(MockMvcResultMatchers.status().is5xxServerError()).andReturn();
        var message = Objects.requireNonNull(response.getResolvedException()).getMessage();

        assertTrue(message.contains(expectedMessage));
    }

    // 2.4. Post film with invalid duration
    @Test
    public void updateFilmWithNegativeDurationTest() throws Exception {

        var expectedMessage = "Продолжительность фильма отрицательная.";

        testFilm.setDuration(-10);

        var response = mockMvc.perform(MockMvcRequestBuilders.post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(testFilm)))
                .andExpect(MockMvcResultMatchers.status().is5xxServerError()).andReturn();
        var message = Objects.requireNonNull(response.getResolvedException()).getMessage();

        assertTrue(message.contains(expectedMessage));
    }

    public static Film generateSecondTestFilm() {
        return Film.builder()
                .id(1)
                .name("Cruiser 'Varyag'")
                .description("Soviet war film directed by Viktor Eisymont.")
                .releaseDate(LocalDate.of(1946, 1, 1))
                .duration(92)
                .build();
    }
}
