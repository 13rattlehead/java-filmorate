package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private User user;

    @BeforeEach
    void setUp() {

        user = new User();

        user.setEmail("user@mail.com");
        user.setLogin("login");
        user.setName("Denis");
        user.setBirthday(LocalDate.of(2000, 1, 1));
    }

    @Test
    void shouldCreateUser() throws Exception {

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.email")
                        .value("user@mail.com"));
    }

    @Test
    void shouldReturnBadRequestWhenLoginContainsSpaces()
            throws Exception {

        user.setLogin("log in");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldUpdateUser() throws Exception {

        String response = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        User createdUser =
                objectMapper.readValue(response, User.class);

        createdUser.setName("Updated");

        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createdUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name")
                        .value("Updated"));
    }

    @Test
    void shouldReturnNotFoundWhenUpdateUnknownUser()
            throws Exception {

        user.setId(999L);

        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnAllUsers() throws Exception {

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void shouldReturnUserById() throws Exception {

        String response = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        User createdUser =
                objectMapper.readValue(response, User.class);

        mockMvc.perform(get("/users/" + createdUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id")
                        .value(createdUser.getId()));
    }

    @Test
    void shouldAddFriend() throws Exception {

        String response1 = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        User user1 =
                objectMapper.readValue(response1, User.class);

        User secondUser = new User();

        secondUser.setEmail("second@mail.com");
        secondUser.setLogin("second");
        secondUser.setName("Second");
        secondUser.setBirthday(LocalDate.of(1999, 1, 1));

        String response2 = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(secondUser)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        User user2 =
                objectMapper.readValue(response2, User.class);

        mockMvc.perform(put("/users/" + user1.getId()
                        + "/friends/" + user2.getId()))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnFriends() throws Exception {

        String response1 = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        User user1 =
                objectMapper.readValue(response1, User.class);

        User secondUser = new User();

        secondUser.setEmail("second@mail.com");
        secondUser.setLogin("second");
        secondUser.setName("Second");
        secondUser.setBirthday(LocalDate.of(1999, 1, 1));

        String response2 = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(secondUser)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        User user2 =
                objectMapper.readValue(response2, User.class);

        mockMvc.perform(put("/users/" + user1.getId()
                + "/friends/" + user2.getId()));

        mockMvc.perform(get("/users/" + user1.getId()
                        + "/friends"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}