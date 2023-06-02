package spring.boot.rest.api.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import spring.boot.rest.api.dto.request.AuthenticationRequestDto;
import spring.boot.rest.api.dto.request.UserUpdateRequestDto;
import spring.boot.rest.api.dto.response.AuthenticationResponseDto;
import spring.boot.rest.api.dto.response.FileResponseDto;
import spring.boot.rest.api.dto.response.UserResponseDto;
import spring.boot.rest.api.model.*;
import spring.boot.rest.api.repository.UserRepo;
import spring.boot.rest.api.service.EventService;
import spring.boot.rest.api.service.UserService;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;

import static java.lang.String.format;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static spring.boot.rest.api.util.TestConstants.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles(TEST_TEXT)
class UserRestControllerV1Test {

    @Autowired
    private UserService userService;
    @Autowired
    private EventService eventService;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    private User userWithRoleAdmin;
    private User userWithRoleUser;
    private final Faker faker = new Faker();
    private String tokenAdmin;
    private FileResponseDto fileResponseDto;

    @BeforeEach
    public void setUp() throws Exception {
        userService.deleteAll();

        userWithRoleAdmin = userRepo.save(getTestUserRoleAdmin());

        userWithRoleUser = userRepo.save(getTestUserRoleUser());

        tokenAdmin = authenticateRoleAdmin();
    }

    @AfterEach
    public void tearDown() {
        userService.deleteAll();
    }

    @Test
    void whenUpdateUserShouldReturn200() throws Exception {

        UserUpdateRequestDto userUpdateRequestDto = getUserUpdateRequestDto();

        ResultActions resultActions = mockMvc.perform(put(format(TEST_URL_API_V1_USERS_ID, userWithRoleUser.getId()))
                .header(AUTHORIZATION, TEST_TEXT_BEARER + tokenAdmin)
                .contentType(APPLICATION_JSON)
                .content(mapFromObjectToString(userUpdateRequestDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON));

        UserResponseDto userResponseDto = mapFromResultActions(resultActions, UserResponseDto.class);

        assertNotNull(userResponseDto);
        assertEquals(userResponseDto.getId(), userUpdateRequestDto.getId());
        assertEquals(userResponseDto.getFirstName(), userUpdateRequestDto.getFirstName());
        assertEquals(userResponseDto.getLastName(), userUpdateRequestDto.getLastName());
        assertEquals(userResponseDto.getEmail(), userUpdateRequestDto.getEmail());
    }

    @Test
    void whenUpdateUserShouldReturn403JwtExceptionBecauseTokenInvalid() throws Exception {

        UserUpdateRequestDto userUpdateRequestDto = getUserUpdateRequestDto();

        mockMvc.perform(put(format(TEST_URL_API_V1_USERS_ID, userWithRoleUser.getId()))
                .header(AUTHORIZATION, TEST_TEXT_BEARER + getInvalidToken())
                .contentType(APPLICATION_JSON)
                .content(mapFromObjectToString(userUpdateRequestDto)))
                .andExpect(status().isForbidden());
    }

    //TODO: correct a method, should return 403, return 500 is not correct
//    @Test
//    void whenUpdateUserShouldReturn403AccessDeniedExceptionBecauseRoleUser() throws Exception {
//
//        UserUpdateRequestDto userUpdateRequestDto = getUserUpdateRequestDto();
//
//        mockMvc.perform(put(format(TEST_URL_API_V1_USERS_ID, userWithRoleUser.getId()))
//                .header(AUTHORIZATION, TEST_TEXT_BEARER + authenticateRoleUser())
//                .contentType(APPLICATION_JSON)
//                .content(mapFromObjectToString(userUpdateRequestDto)))
//                .andExpect(status().isForbidden());
//    }

    @Test
    void whenFindByIdShouldReturn200() throws Exception {

        ResultActions resultActions = mockMvc.perform(get(format(TEST_URL_API_V1_USERS_ID, userWithRoleUser.getId()))
                .header(AUTHORIZATION, TEST_TEXT_BEARER + tokenAdmin))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON));

        UserResponseDto userResponseDto = mapFromResultActions(resultActions, UserResponseDto.class);

        assertNotNull(userResponseDto);
        assertEquals(userResponseDto.getId(), userWithRoleUser.getId());
        assertEquals(userResponseDto.getFirstName(), userWithRoleUser.getFirstName());
        assertEquals(userResponseDto.getLastName(), userWithRoleUser.getLastName());
        assertEquals(userResponseDto.getEmail(), userWithRoleUser.getEmail());
    }

    @Test
    void whenFindByIdShouldReturn403JwtExceptionBecauseTokenInvalid() throws Exception {

        mockMvc.perform(get(format(TEST_URL_API_V1_USERS_ID, userWithRoleUser.getId()))
                .header(AUTHORIZATION, TEST_TEXT_BEARER + getInvalidToken()))
                .andExpect(status().isForbidden());
    }
    @Test
    void whenFindByIdShouldReturn403AccessDeniedExceptionBecauseRoleUserByNotFindSelf() throws Exception {

        mockMvc.perform(get(format(TEST_URL_API_V1_USERS_ID, userWithRoleAdmin.getId()))
                .header(AUTHORIZATION, TEST_TEXT_BEARER + authenticateRoleUser()))
                .andExpect(status().isForbidden());
    }

    @Test
    void whenFindAllShouldReturn200() throws Exception {

        mockMvc.perform(get(TEST_URL_API_V1_USERS)
                        .header(AUTHORIZATION, TEST_TEXT_BEARER + tokenAdmin))
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(greaterThan(0))))
                .andExpect(jsonPath("$", is(notNullValue())));
    }

    @Test
    void whenFindAllShouldReturn403JwtExceptionBecauseTokenInvalid() throws Exception {

        mockMvc.perform(get(TEST_URL_API_V1_USERS)
                        .header(AUTHORIZATION, TEST_TEXT_BEARER + getInvalidToken()))
                .andExpect(status().isForbidden());
    }

    //TODO: correct a method, should return 403, return 500 is not correct
//    @Test
//    void whenFindAllShouldReturn403AccessDeniedExceptionBecauseRoleUser() throws Exception {
//
//        mockMvc.perform(get(TEST_URL_API_V1_USERS)
//                        .header(AUTHORIZATION, TEST_TEXT_BEARER + authenticateRoleUser()))
//                .andExpect(status().isForbidden());
//    }

    @Test
    void whenDeleteByIdShouldReturn200() throws Exception {

        mockMvc.perform(delete(format(TEST_URL_API_V1_USERS_ID, userWithRoleUser.getId()))
                .header(AUTHORIZATION, TEST_TEXT_BEARER + tokenAdmin))
                .andExpect(status().isOk());
    }

    @Test
    void whenDeleteByIdShouldReturn403JwtExceptionBecauseTokenInvalid() throws Exception {

        mockMvc.perform(delete(format(TEST_URL_API_V1_USERS_ID, userWithRoleUser.getId()))
                .header(AUTHORIZATION, TEST_TEXT_BEARER + getInvalidToken()))
                .andExpect(status().isForbidden());
    }

    //TODO: correct a method, should return 403, return 500 is not correct
//    @Test
//    void whenDeleteByIdShouldReturn403AccessDeniedExceptionBecauseRoleUser() throws Exception {
//
//        mockMvc.perform(delete(format(TEST_URL_API_V1_USERS_ID, userWithRoleUser.getId()))
//                .header(AUTHORIZATION, TEST_TEXT_BEARER + authenticateRoleUser()))
//                .andExpect(status().isOk());
//    }

    @Test
    void whenDeleteAllShouldReturn200() throws Exception {

        mockMvc.perform(delete(TEST_URL_API_V1_FILES)
                .header(AUTHORIZATION, TEST_TEXT_BEARER + tokenAdmin))
                .andExpect(status().isOk());
    }

    @Test
    void whenDeleteAllShouldReturn403JwtExceptionBecauseTokenInvalid() throws Exception {

        mockMvc.perform(delete(TEST_URL_API_V1_FILES)
                .header(AUTHORIZATION, TEST_TEXT_BEARER + getInvalidToken()))
                .andExpect(status().isForbidden());
    }

    //TODO: correct a method, should return 403, return 500 is not correct
//    @Test
//    void whenDeleteAllShouldReturn403AccessDeniedExceptionBecauseRoleUser() throws Exception {
//
//        mockMvc.perform(delete(TEST_URL_API_V1_FILES)
//                .header(AUTHORIZATION, TEST_TEXT_BEARER + authenticateRoleUser()))
//                .andExpect(status().isOk());
//    }

    @Test
    void whenFindFilesShouldReturn200() throws Exception {

        fileResponseDto = mapFromResultActions(getUploadFile(), FileResponseDto.class);

        createEvent();

        mockMvc.perform(get(format(TEST_URL_API_V1_USERS_ID_FILES, userWithRoleUser.getId()))
                .header(AUTHORIZATION, TEST_TEXT_BEARER + authenticateRoleUser()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.size()", is(1)))
                .andExpect(jsonPath("$", is(notNullValue())));
    }

    @Test
    void whenFindFilesShouldReturn403JwtExceptionBecauseTokenInvalid() throws Exception {

        mockMvc.perform(get(format(TEST_URL_API_V1_USERS_ID_FILES, userWithRoleUser.getId()))
                .header(AUTHORIZATION, TEST_TEXT_BEARER + getInvalidToken()))
                .andExpect(status().isForbidden());
    }

    private MockMultipartFile getFile() {
        return new MockMultipartFile("file", "filename.txt", "text/plain", "some text".getBytes());
    }

    private String getInvalidToken() {
        return INVALID_TOKEN;
    }

    private User getTestUserRoleAdmin() {
        return User.builder()
                .firstName(faker.name().firstName())
                .lastName(faker.name().lastName())
                .email(faker.internet().emailAddress())
                .password(passwordEncoder.encode(TEST_TEXT_PASSWORD_USER))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .status(Status.ACTIVE)
                .role(Role.ADMIN).build();
    }

    private User getTestUserRoleUser() {
        return User.builder()
                .firstName(faker.name().firstName())
                .lastName(faker.name().lastName())
                .email(faker.internet().emailAddress())
                .password(passwordEncoder.encode(TEST_TEXT_PASSWORD_USER))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .status(Status.ACTIVE)
                .role(Role.USER).build();
    }

    private <T> T mapFromResultActions(ResultActions resultActions, Class<T> aClass) throws JsonProcessingException, UnsupportedEncodingException {
        return objectMapper.readValue(resultActions.andReturn().getResponse().getContentAsString(), aClass);
    }

    private ResultActions getUploadFile() throws Exception {
        return mockMvc.perform(multipart(TEST_URL_API_V1_FILES_UPLOAD)
                        .file(getFile())
                        .header(AUTHORIZATION, TEST_TEXT_BEARER + tokenAdmin))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(APPLICATION_JSON));
    }

    private String authenticateRoleAdmin() throws Exception {
        MvcResult mvcResponseAuth = mockMvc.perform(post(TEST_TEXT_API_V1_AUTH_AUTHENTICATE)
                        .content(objectMapper.writeValueAsString(AuthenticationRequestDto.builder().email(userWithRoleAdmin.getEmail()).password(TEST_TEXT_PASSWORD_USER).build()))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String contentAsString = mvcResponseAuth.getResponse().getContentAsString();

        AuthenticationResponseDto result = objectMapper.readValue(contentAsString, AuthenticationResponseDto.class);

        return result.getAccessToken();
    }

    private String authenticateRoleUser() throws Exception {
        MvcResult mvcResponseAuth = mockMvc.perform(post(TEST_TEXT_API_V1_AUTH_AUTHENTICATE)
                        .content(objectMapper.writeValueAsString(AuthenticationRequestDto.builder().email(userWithRoleUser.getEmail()).password(TEST_TEXT_PASSWORD_USER).build()))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String contentAsString = mvcResponseAuth.getResponse().getContentAsString();

        AuthenticationResponseDto result = objectMapper.readValue(contentAsString, AuthenticationResponseDto.class);

        return result.getAccessToken();
    }

    private UserUpdateRequestDto getUserUpdateRequestDto() {
        return UserUpdateRequestDto.builder()
                .id(userWithRoleUser.getId())
                .firstName(faker.name().firstName())
                .lastName(faker.name().lastName())
                .email(faker.internet().emailAddress())
                .build();
    }

    private <T> String mapFromObjectToString(T t) throws JsonProcessingException {
        return objectMapper.writeValueAsString(t);
    }

    private void createEvent(){
        eventService.save(Event.builder()
                .name(faker.company().name())
                .user(User.builder().id(userWithRoleUser.getId()).build())
                .file(File.builder().id(fileResponseDto.getId()).build())
                .build());
    }
}