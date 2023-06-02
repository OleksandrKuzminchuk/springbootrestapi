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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import spring.boot.rest.api.dto.request.AuthenticationRequestDto;
import spring.boot.rest.api.dto.request.RegisterRequestDto;
import spring.boot.rest.api.dto.response.AuthenticationResponseDto;
import spring.boot.rest.api.model.Role;
import spring.boot.rest.api.model.Status;
import spring.boot.rest.api.model.User;
import spring.boot.rest.api.repository.UserRepo;
import spring.boot.rest.api.service.UserService;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static spring.boot.rest.api.util.TestConstants.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthenticationRestControllerV1Test {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;
    private final Faker faker = new Faker();
    private String tokenAdmin;
    private User userWithRoleAdmin;
    private RegisterRequestDto registerRequestDto;
    private User userWithRoleUser;


    @BeforeEach
    void setUp() throws Exception {
        userService.deleteAll();

        userWithRoleAdmin = userRepo.save(getTestUserRoleAdmin());

        userWithRoleUser = userRepo.save(getTestUserRoleUser());

        tokenAdmin = authenticateRoleAdmin();

        registerRequestDto = getRegisterRequestDto();
    }

    @AfterEach
    void tearDown() {
        userService.deleteAll();
    }

    @Test
    void whenRegisterUserShouldReturn201() throws Exception {

        ResultActions resultActions = mockMvc.perform(post(TEST_TEXT_API_V1_AUTH_REGISTER)
                .header(AUTHORIZATION, TEST_TEXT_BEARER + tokenAdmin)
                .contentType(APPLICATION_JSON)
                .content(mapFromObjectToString(registerRequestDto)))
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(status().isCreated());

        AuthenticationResponseDto response = mapFromResultActions(resultActions, AuthenticationResponseDto.class);

        assertNotNull(response);
        assertNotNull(response.getAccessToken());
        assertNotNull(response.getRefreshToken());
    }

    @Test
    void whenRegisterUserShouldReturn403JwtExceptionBecauseTokenInvalid() throws Exception {

        mockMvc.perform(post(TEST_TEXT_API_V1_AUTH_REGISTER)
                .header(AUTHORIZATION, TEST_TEXT_BEARER + getInvalidToken())
                .contentType(APPLICATION_JSON)
                .content(mapFromObjectToString(registerRequestDto)))
                .andExpect(status().isForbidden());
    }

    //TODO: correct a method, should return 403, return 500 is not correct : AccessDeniedException
//    @Test
//    void whenRegisterUserShouldReturn403AccessDeniedExceptionBecauseRoleUser() throws Exception {
//
//        mockMvc.perform(post(TEST_TEXT_API_V1_AUTH_REGISTER)
//                .header(AUTHORIZATION, TEST_TEXT_BEARER + authenticateRoleUser())
//                .contentType(APPLICATION_JSON)
//                .content(mapFromObjectToString(registerRequestDto)))
//                .andExpect(status().isForbidden());
//    }

    @Test
    void whenAuthenticateUserByRoleAdminShouldReturn200() throws Exception {

        ResultActions resultActions = mockMvc.perform(post(TEST_TEXT_API_V1_AUTH_AUTHENTICATE)
                .contentType(APPLICATION_JSON)
                .content(getAuthenticationRequestDtoByAdmin()))
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(status().isOk());

        AuthenticationResponseDto response = mapFromResultActions(resultActions, AuthenticationResponseDto.class);

        assertNotNull(response);
        assertNotNull(response.getAccessToken());
        assertNotNull(response.getRefreshToken());
    }

    @Test
    void whenAuthenticateUserByRoleAdminShouldReturn401BecauseEmailIsNotCorrect() throws Exception {

        mockMvc.perform(post(TEST_TEXT_API_V1_AUTH_AUTHENTICATE)
                .contentType(APPLICATION_JSON)
                .content(getAuthenticationRequestDtoNoCorrectEmail()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenAuthenticateUserByRoleAdminShouldReturn401BecausePasswordIsNotCorrect() throws Exception {

        mockMvc.perform(post(TEST_TEXT_API_V1_AUTH_AUTHENTICATE)
                .contentType(APPLICATION_JSON)
                .content(getAuthenticationRequestDtoNoCorrectPassword()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenAuthenticateUserByRoleUserShouldReturn200() throws Exception {

        ResultActions resultActions = mockMvc.perform(post(TEST_TEXT_API_V1_AUTH_AUTHENTICATE)
                        .contentType(APPLICATION_JSON)
                        .content(getAuthenticationRequestDtoByUser()))
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(status().isOk());

        AuthenticationResponseDto response = mapFromResultActions(resultActions, AuthenticationResponseDto.class);

        assertNotNull(response);
        assertNotNull(response.getAccessToken());
        assertNotNull(response.getRefreshToken());
    }

    @Test
    void whenRefreshTokenShouldReturn200() throws Exception {

        ResultActions resultActions = mockMvc.perform(post(TEST_TEXT_API_V1_AUTH_REFRESH_TOKEN)
                .header(AUTHORIZATION, TEST_TEXT_BEARER + tokenAdmin))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON));

        AuthenticationResponseDto response = mapFromResultActions(resultActions, AuthenticationResponseDto.class);

        assertNotNull(response);
        assertNotNull(response.getAccessToken());
        assertNotNull(response.getRefreshToken());
    }

    @Test
    void whenRefreshTokenShouldReturn403JwtExceptionBecauseTokenInvalid() throws Exception {

        mockMvc.perform(post(TEST_TEXT_API_V1_AUTH_REFRESH_TOKEN)
                .header(AUTHORIZATION, TEST_TEXT_BEARER + getInvalidToken()))
                .andExpect(status().isForbidden());
    }

    private RegisterRequestDto getRegisterRequestDto(){
        return RegisterRequestDto.builder()
                .firstName(faker.name().firstName())
                .lastName(faker.name().lastName())
                .email(faker.internet().emailAddress())
                .password(faker.internet().password())
                .role(Role.USER.name())
                .build();
    }

    private <T> String mapFromObjectToString(T t) throws JsonProcessingException {
        return objectMapper.writeValueAsString(t);
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

    private <T> T mapFromResultActions(ResultActions resultActions, Class<T> aClass) throws UnsupportedEncodingException, JsonProcessingException {
        return objectMapper.readValue(resultActions.andReturn().getResponse().getContentAsString(), aClass);
    }

    private String getInvalidToken() {
        return INVALID_TOKEN;
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

    private String getAuthenticationRequestDtoByUser() throws JsonProcessingException {
        return mapFromObjectToString(AuthenticationRequestDto.builder()
                .email(userWithRoleUser.getEmail())
                .password(TEST_TEXT_PASSWORD_USER)
                .build());
    }
    private String getAuthenticationRequestDtoByAdmin() throws JsonProcessingException {
        return mapFromObjectToString(AuthenticationRequestDto.builder()
                .email(userWithRoleAdmin.getEmail())
                .password(TEST_TEXT_PASSWORD_USER)
                .build());
    }
    private String getAuthenticationRequestDtoNoCorrectEmail() throws JsonProcessingException {
        return mapFromObjectToString(AuthenticationRequestDto.builder()
                .email(faker.internet().emailAddress())
                .password(TEST_TEXT_PASSWORD_USER)
                .build());
    }
    private String getAuthenticationRequestDtoNoCorrectPassword() throws JsonProcessingException {
        return mapFromObjectToString(AuthenticationRequestDto.builder()
                .email(userWithRoleAdmin.getEmail())
                .password(faker.internet().password())
                .build());
    }
}