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
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import spring.boot.rest.api.dto.request.AuthenticationRequestDto;
import spring.boot.rest.api.dto.request.EventCreateRequestDto;
import spring.boot.rest.api.dto.request.EventUpdateRequestDto;
import spring.boot.rest.api.dto.response.AuthenticationResponseDto;
import spring.boot.rest.api.dto.response.EventResponseDto;
import spring.boot.rest.api.dto.response.FileResponseDto;
import spring.boot.rest.api.model.*;
import spring.boot.rest.api.repository.UserRepo;
import spring.boot.rest.api.service.UserService;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;

import static java.lang.String.format;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static spring.boot.rest.api.util.TestConstants.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class EventRestControllerV1Test {

    @Autowired
    private UserService userService;
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
    private EventResponseDto eventResponseDto;

    @BeforeEach
    public void setUp() throws Exception {
        userService.deleteAll();

        userWithRoleAdmin = userRepo.save(getTestUserRoleAdmin());

        userWithRoleUser = userRepo.save(getTestUserRoleUser());

        tokenAdmin = authenticateRoleAdmin();

        fileResponseDto = mapFromResultActions(getUploadFile(), FileResponseDto.class);

        eventResponseDto = createEvent();
    }

    @AfterEach
    public void tearDown() {
        userService.deleteAll();
    }

    @Test
    void whenCreateEventShouldReturn403JwtExceptionBecauseTokenInvalid() throws Exception {

        mockMvc.perform(post(TEST_URL_API_V1_EVENTS)
                        .header(AUTHORIZATION, TEST_TEXT_BEARER + getInvalidToken())
                        .content(mapFromObjectToString(getEventCreateRequestDto()))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
    //TODO: correct a method, should return 403, return 500 is not correct
//    @Test
//    void whenCreateEventShouldReturn403AccessDeniedExceptionBecauseRoleUser() throws Exception {
//
//        mockMvc.perform(post(TEST_URL_API_V1_EVENTS)
//                        .header(AUTHORIZATION, TEST_TEXT_BEARER + authenticateRoleUser())
//                        .content(mapFromObjectToString(getEventCreateRequestDto()))
//                        .contentType(APPLICATION_JSON))
//                .andExpect(status().isForbidden());
//    }

    @Test
    void whenUpdateEventShouldReturn200() throws Exception {

        EventUpdateRequestDto eventUpdateRequestDto = getEventUpdateRequestDto();

        ResultActions resultActions = mockMvc.perform(put(format(TEST_URL_API_V1_EVENTS_ID, eventResponseDto.getId()))
                .header(AUTHORIZATION, TEST_TEXT_BEARER + tokenAdmin)
                .contentType(APPLICATION_JSON)
                .content(mapFromObjectToString(eventUpdateRequestDto)))
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(status().isOk());

        EventResponseDto response = mapFromResultActions(resultActions, EventResponseDto.class);

        assertNotNull(response);
        assertEquals(response.getId(), eventUpdateRequestDto.getId());
        assertEquals(response.getName(), eventUpdateRequestDto.getName());
        assertEquals(response.getUserId(), eventUpdateRequestDto.getUserId());
        assertEquals(response.getFileId(), eventUpdateRequestDto.getFileId());
    }

    @Test
    void whenUpdateEventShouldReturn403JwtExceptionBecauseTokenInvalid() throws Exception {

        EventUpdateRequestDto eventUpdateRequestDto = getEventUpdateRequestDto();

        mockMvc.perform(put(format(TEST_URL_API_V1_EVENTS_ID, eventResponseDto.getId()))
                .header(AUTHORIZATION, TEST_TEXT_BEARER + getInvalidToken())
                .contentType(APPLICATION_JSON)
                .content(mapFromObjectToString(eventUpdateRequestDto)))
                .andExpect(status().isForbidden());
    }

    //TODO: correct a method, should return 403, return 500 is not correct
//    @Test
//    void whenUpdateEventShouldReturn403AccessDeniedExceptionBecauseRoleUser() throws Exception {
//
//        EventUpdateRequestDto eventUpdateRequestDto = getEventUpdateRequestDto();
//
//        mockMvc.perform(put(String.format(TEST_URL_API_V1_EVENTS_ID, eventResponseDto.getId()))
//                        .header(AUTHORIZATION, TEST_TEXT_BEARER + authenticateRoleUser())
//                        .contentType(APPLICATION_JSON)
//                        .content(mapFromObjectToString(eventUpdateRequestDto)))
//                .andExpect(status().isForbidden());
//    }

    @Test
    void whenFindByIdShouldReturn200() throws Exception {

        ResultActions resultActions = mockMvc.perform(get(format(TEST_URL_API_V1_EVENTS_ID, eventResponseDto.getId()))
                .header(AUTHORIZATION, TEST_TEXT_BEARER + tokenAdmin))
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(status().isOk());

        EventResponseDto response = mapFromResultActions(resultActions, EventResponseDto.class);

        assertNotNull(response);
        assertEquals(response.getId(), eventResponseDto.getId());
        assertEquals(response.getName(), eventResponseDto.getName());
        assertEquals(response.getUserId(), eventResponseDto.getUserId());
        assertEquals(response.getFileId(), eventResponseDto.getFileId());
    }

    @Test
    void whenFindByIdShouldReturn403JwtExceptionBecauseTokenInvalid() throws Exception {

        mockMvc.perform(get(format(TEST_URL_API_V1_EVENTS_ID, eventResponseDto.getId()))
                .header(AUTHORIZATION, TEST_TEXT_BEARER + getInvalidToken()))
                .andExpect(status().isForbidden());
    }

    //TODO: correct a method, should return 403, return 500 is not correct
//    @Test
//    void whenFindByIdShouldReturn403AccessDeniedExceptionBecauseRoleUser() throws Exception {
//
//        mockMvc.perform(get(format(TEST_URL_API_V1_EVENTS_ID, eventResponseDto.getId()))
//                        .header(AUTHORIZATION, TEST_TEXT_BEARER + authenticateRoleUser()))
//                .andExpect(status().isForbidden());
//    }

    @Test
    void whenFindAllEventsShouldReturn200() throws Exception {

        mockMvc.perform(get(TEST_URL_API_V1_EVENTS)
                .header(AUTHORIZATION, TEST_TEXT_BEARER + tokenAdmin))
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", greaterThan(0)))
                .andExpect(jsonPath("$", is(notNullValue())));
    }

    @Test
    void whenFindAllEventsShouldReturn403JwtExceptionBecauseTokenInvalid() throws Exception {

        mockMvc.perform(get(TEST_URL_API_V1_EVENTS)
                .header(AUTHORIZATION, TEST_TEXT_BEARER + getInvalidToken()))
                .andExpect(status().isForbidden());
    }

    //TODO: correct a method, should return 403, return 500 is not correct
//    @Test
//    void whenFindAllEventsShouldReturn403AccessDeniedExceptionBecauseRoleUser() throws Exception {
//
//        mockMvc.perform(get(TEST_URL_API_V1_EVENTS)
//                        .header(AUTHORIZATION, TEST_TEXT_BEARER + authenticateRoleUser()))
//                .andExpect(status().isForbidden());
//    }

    @Test
    void whenDeleteByIdShouldReturn200() throws Exception {
        mockMvc.perform(delete(format(TEST_URL_API_V1_EVENTS_ID, eventResponseDto.getId()))
                .header(AUTHORIZATION, TEST_TEXT_BEARER + tokenAdmin))
                .andExpect(status().isOk());
    }

    @Test
    void whenDeleteByIdShouldReturn403JwtExceptionBecauseTokenInvalid() throws Exception {
        mockMvc.perform(delete(format(TEST_URL_API_V1_EVENTS_ID, eventResponseDto.getId()))
                .header(AUTHORIZATION, TEST_TEXT_BEARER + getInvalidToken()))
                .andExpect(status().isForbidden());
    }

    //TODO: correct a method, should return 403, return 500 is not correct
//    @Test
//    void whenDeleteByIdShouldReturn403AccessDeniedExceptionBecauseRoleUser() throws Exception {
//        mockMvc.perform(delete(format(TEST_URL_API_V1_EVENTS_ID, eventResponseDto.getId()))
//                .header(AUTHORIZATION, TEST_TEXT_BEARER + authenticateRoleUser()))
//                .andExpect(status().isForbidden());
//    }

    @Test
    void whenDeleteAllShouldReturn200() throws Exception {
        mockMvc.perform(delete(TEST_URL_API_V1_EVENTS)
                .header(AUTHORIZATION, TEST_TEXT_BEARER + tokenAdmin))
                .andExpect(status().isOk());
    }

    @Test
    void whenDeleteAllShouldReturn403JwtExceptionBecauseTokenInvalid() throws Exception {
        mockMvc.perform(delete(TEST_URL_API_V1_EVENTS)
                .header(AUTHORIZATION, TEST_TEXT_BEARER + getInvalidToken()))
                .andExpect(status().isForbidden());
    }

    //TODO: correct a method, should return 403, return 500 is not correct
//    @Test
//    void whenDeleteAllShouldReturn403AccessDeniedExceptionBecauseRoleUser() throws Exception {
//        mockMvc.perform(delete(TEST_URL_API_V1_EVENTS)
//                .header(AUTHORIZATION, TEST_TEXT_BEARER + authenticateRoleUser()))
//                .andExpect(status().isForbidden());
//    }

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

    private <T> String mapFromObjectToString(T t) throws JsonProcessingException {
        return objectMapper.writeValueAsString(t);
    }

    private EventCreateRequestDto getEventCreateRequestDto(){
        return EventCreateRequestDto.builder()
                .name(faker.company().name())
                .userId(userWithRoleUser.getId())
                .fileId(fileResponseDto.getId())
                .build();
    }

    private EventResponseDto createEvent() throws Exception {

        EventCreateRequestDto eventCreateRequestDto = getEventCreateRequestDto();

        ResultActions resultActions = mockMvc.perform(post(TEST_URL_API_V1_EVENTS)
                        .header(AUTHORIZATION, TEST_TEXT_BEARER + tokenAdmin)
                        .content(mapFromObjectToString(eventCreateRequestDto))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(APPLICATION_JSON));

        EventResponseDto eventResponseDto = mapFromResultActions(resultActions, EventResponseDto.class);

        assertNotNull(eventResponseDto);
        assertEquals(eventResponseDto.getName(), eventCreateRequestDto.getName());
        assertEquals(eventResponseDto.getUserId(), eventCreateRequestDto.getUserId());
        assertEquals(eventResponseDto.getFileId(), eventCreateRequestDto.getFileId());
        assertEquals(Status.ACTIVE, eventResponseDto.getStatus());

        return eventResponseDto;
    }

    private EventUpdateRequestDto getEventUpdateRequestDto(){
        return EventUpdateRequestDto.builder()
                .id(eventResponseDto.getId())
                .name(faker.company().name())
                .userId(userWithRoleAdmin.getId())
                .fileId(fileResponseDto.getId())
                .build();

    }

}