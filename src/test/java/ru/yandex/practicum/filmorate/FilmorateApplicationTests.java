package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.*;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FilmorateApplicationTests {

    private final UserDbStorage userDbStorage;
    private final FilmDbStorage filmDbStorage;
    private final FriendDbStorage friendsDbStorage;
    private final LikeDbStorage likeDbStorage;
    private final GenreDbStorage genreDbStorage;
    private final MpaDbStorage mpaDbStorage;


    // UserDbStorageTest
    @Test
    @Order(1)
    public void addAndGetUserTest() {
        var user1 = getTestUsers().get(1);
        userDbStorage.addUser(user1);

        assertEquals(user1, userDbStorage.getUser(1));
    }

    @Test
    @Order(2)
    public void updateUserTest() {
        var user1Upd = getTestUsers().get(4);
        userDbStorage.updateUser(user1Upd);

        assertEquals(user1Upd, userDbStorage.getUser(1));
    }

    @Test
    @Order(3)
    public void getUsersTest() {
        var user1 = userDbStorage.getUser(1);

        var user2 = getTestUsers().get(2);
        userDbStorage.addUser(user2);

        Collection<User> userCollection = List.of(user1, user2);

        assertEquals(userCollection, userDbStorage.getUsers());
    }


    // FriendDbStorageTest
    @Test
    @Order(4)
    public void addAndGetFriendsTest() {
        var user1 = userDbStorage.getUser(1);
        var user2 = userDbStorage.getUser(2);

        friendsDbStorage.addFriend(user1.getId(), user2.getId());

        assertEquals(List.of(user2), friendsDbStorage.getFriends(user1.getId()));
    }

    @Test
    @Order(5)
    public void deleteFriendsTest() {
        var user1 = userDbStorage.getUser(1);
        var user2 = userDbStorage.getUser(2);

        friendsDbStorage.deleteFriend(user1.getId(), user2.getId());

        assertEquals(List.of(), friendsDbStorage.getFriends(user1.getId()));
    }

    @Test
    @Order(6)
    public void getCommonFriendsTest() {
        var user1 = userDbStorage.getUser(1);
        var user2 = userDbStorage.getUser(2);
        var user3 = userDbStorage.addUser(getTestUsers().get(3));

        friendsDbStorage.addFriend(user1.getId(), user3.getId());
        friendsDbStorage.addFriend(user2.getId(), user3.getId());

        assertEquals(List.of(user3), friendsDbStorage.getCommonFriends(user1.getId(), user2.getId()));
    }


    // FilmDbStorageTest
    // Note: TestFilmCollection consists of:
    // Key 1 - new film (id=1)
    // Key 2 - new film (id=2)
    // Key 3 - updated film (id=1)
    @Test
    @Order(7)
    public void addAndGetFilmTest() {
        var film1 = getTestFilms().get(1);
        filmDbStorage.addFilm(film1);

        assertEquals(film1, filmDbStorage.getFilm(1));
    }

    @Test
    @Order(8)
    public void updateFilmTest() {
        var film1Upd = getTestFilms().get(3);
        filmDbStorage.updateFilm(film1Upd);

        assertEquals(film1Upd, filmDbStorage.getFilm(1)); // тест методов updateFilm и getFilm
    }

    @Test
    @Order(9)
    public void getFilmsTest() {
        var film1 = filmDbStorage.getFilm(1);
        var film2 = filmDbStorage.addFilm(getTestFilms().get(2));

        Collection<Film> filmCollection = List.of(film1, film2);

        assertEquals(filmCollection, filmDbStorage.getFilms());
    }

    // LikeDbStorageTest
    @Test
    @Order(10)
    public void addLikeTest() {
        var user1 = userDbStorage.getUser(1);
        var film1 = filmDbStorage.getFilm(1);

        var initialLikesQuantity = filmDbStorage.getFilm(film1.getId()).getRate();

        likeDbStorage.addLike(film1.getId(), user1.getId());

        assertEquals(initialLikesQuantity + 1, filmDbStorage.getFilm(film1.getId()).getRate());
    }

    @Test
    @Order(11)
    public void deleteLikeTest() {
        var user1 = userDbStorage.getUser(1);
        var film1 = filmDbStorage.getFilm(1);

        var initialLikesQuantity = filmDbStorage.getFilm(film1.getId()).getRate();

        likeDbStorage.deleteLike(film1.getId(), user1.getId());

        assertEquals(initialLikesQuantity - 1, filmDbStorage.getFilm(film1.getId()).getRate());
    }

    @Test
    @Order(12)
    public void getPopularFilms() {
        var film1 = filmDbStorage.getFilm(1);
        var film2 = filmDbStorage.getFilm(2);

        var film1InitialLikesQuantity = filmDbStorage.getFilm(film1.getId()).getRate(); // rate = 3
        var film2InitialLikesQuantity = filmDbStorage.getFilm(film2.getId()).getRate(); // rate = 2


        if (film1InitialLikesQuantity > film2InitialLikesQuantity) {
            assertEquals(film1.getId(), likeDbStorage.getPopularFilms(1).stream().findAny().get().getId());
        } else {
            assertEquals(film2.getId(), likeDbStorage.getPopularFilms(1).stream().findAny().get().getId());
        }
    }

    // GenreDbStorageTest
    @Test
    @Order(13)
    public void getAllGenresTest() {
        Collection<Genre> genreCollection = List.of(
                Genre.builder().id(1L).name("Комедия").build(),
                Genre.builder().id(2L).name("Драма").build(),
                Genre.builder().id(3L).name("Мультфильм").build(),
                Genre.builder().id(4L).name("Триллер").build(),
                Genre.builder().id(5L).name("Документальный").build(),
                Genre.builder().id(6L).name("Боевик").build());

        assertEquals(genreCollection, genreDbStorage.getAllGenres());
    }

    @Test
    @Order(14)
    public void getGenreTest() {
        var genre = Genre.builder().id(2L).name("Драма").build();

        assertEquals(genre, genreDbStorage.getGenre(2L));
    }

    // MpaDbStorageTests
    @Test // getAllMPA
    @Order(15)
    public void getAllMpaTest() {
        var mpaCollection = List.of(
                MPA.builder().id(1L).name("G").build(),
                MPA.builder().id(2L).name("PG").build(),
                MPA.builder().id(3L).name("PG-13").build(),
                MPA.builder().id(4L).name("R").build(),
                MPA.builder().id(5L).name("NC-17").build());

        assertEquals(mpaCollection, mpaDbStorage.getAllMpa());
    }

    @Test
    @Order(16)
    public void getMPATest() {
        var mpa = MPA.builder().id(3L).name("PG-13").build();

        assertEquals(mpa, mpaDbStorage.getMpa(3L));
    }

    // Note: TestUsersCollection consists of:
    // Key 1 - new user (id=1)
    // Key 2 - new user (id=2)
    // Key 3 - new user (id=3)
    // Key 4 - updated user (id=1)
    private Map<Integer, User> getTestUsers() {
        var testUsersCollection = new HashMap<Integer, User>();
        testUsersCollection.put(1, User.builder()
                .id(1L)
                .email("user1@mail.ru")
                .login("user1Login")
                .name("user1Name")
                .birthday(LocalDate.of(2001, 1, 1))
                .build());

        testUsersCollection.put(2, User.builder()
                .id(1L)
                .email("user2@mail.ru")
                .login("user2Login")
                .name("user2Name")
                .birthday(LocalDate.of(2002, 1, 1))
                .build());

        testUsersCollection.put(3, User.builder()
                .id(3L)
                .email("user3@mail.ru")
                .login("user3Login")
                .name("user3Name")
                .birthday(LocalDate.of(2003, 2, 1))
                .build());

        testUsersCollection.put(4, User.builder()
                .id(1L)
                .email("user1Upd@mail.ru")
                .login("user1UpdLogin")
                .name("user1UpdName")
                .birthday(LocalDate.of(2001, 2, 1))
                .build());

        return testUsersCollection;
    }

    // Note: TestFilmsCollection consists of:
    // Key 1 - new film (id=1)
    // Key 2 - new film (id=2)
    // Key 3 - updated film (id=1)
    private Map<Integer, Film> getTestFilms() {
        var testFilmsCollection = new HashMap<Integer, Film>();
        testFilmsCollection.put(1, Film.builder()
                .id(1L)
                .name("film1Name")
                .description("film1Description")
                .releaseDate(LocalDate.of(2001, 1, 1))
                .duration(100)
                .rate(1)
                .mpa(MPA.builder().id(1).name("G").build())
                .genres(new HashSet<>())
                .build());

        testFilmsCollection.put(2, Film.builder()
                .id(2L)
                .name("film2Name")
                .description("film2Description")
                .releaseDate(LocalDate.of(2002, 1, 1))
                .duration(200)
                .rate(2)
                .mpa(MPA.builder().id(2).name("PG").build())
                .genres(new HashSet<>())
                .build());

        testFilmsCollection.put(3, Film.builder()
                .id(1L)
                .name("film2NameUpd")
                .description("film2DescriptionUpd")
                .releaseDate(LocalDate.of(2001, 2, 1))
                .duration(101)
                .rate(3)
                .mpa(MPA.builder().id(3).name("PG-13").build())
                .genres(new HashSet<>())
                .build());

        return testFilmsCollection;
    }
}
