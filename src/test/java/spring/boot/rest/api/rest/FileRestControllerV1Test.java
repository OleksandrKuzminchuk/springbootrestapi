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
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import spring.boot.rest.api.dto.request.AuthenticationRequestDto;
import spring.boot.rest.api.dto.request.FileDownloadRequestDto;
import spring.boot.rest.api.dto.request.FileRenameRequestDto;
import spring.boot.rest.api.dto.response.AuthenticationResponseDto;
import spring.boot.rest.api.dto.response.FileResponseDto;
import spring.boot.rest.api.model.Role;
import spring.boot.rest.api.model.Status;
import spring.boot.rest.api.model.User;
import spring.boot.rest.api.repository.UserRepo;
import spring.boot.rest.api.service.UserService;


import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
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
@ActiveProfiles("test")
class FileRestControllerV1Test {
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

    @BeforeEach
    public void setUp() throws Exception {
        userService.deleteAll();

        userWithRoleAdmin = userRepo.save(getTestUserRoleAdmin());

        userWithRoleUser = userRepo.save(getTestUserRoleUser());

        tokenAdmin = authenticateRoleAdmin();

        fileResponseDto = getFileResponseDto();
    }

    @AfterEach
    public void tearDown() {
        userService.deleteAll();
    }

    @Test
    void whenUploadFileShouldReturn201Created() {
        assertThat(fileResponseDto.getId()).isNotNull();
        assertThat(fileResponseDto.getName()).isEqualTo(getFile().getOriginalFilename());
        assertThat(fileResponseDto.getLocation()).isNotEmpty();
        assertThat(fileResponseDto.getCreatedAt()).isNotNull();
        assertThat(fileResponseDto.getUpdatedAt()).isNotNull();
        assertThat(fileResponseDto.getStatus()).isEqualTo(Status.ACTIVE);
    }

    @Test
    void whenUploadFileShouldReturn403JwtExceptionBecauseTokenInvalid() throws Exception {

        mockMvc.perform(multipart(TEST_URL_API_V1_FILES_UPLOAD)
                        .file(getFile())
                        .header(AUTHORIZATION, TEST_TEXT_BEARER + getInvalidToken()))
                .andExpect(status().isForbidden());
    }

    //TODO: correct a method, should return 403, return 500 is not correct
//    @Test
//    void whenUploadFileShouldReturn403AccessDeniedExceptionBecauseRoleUser() throws Exception {
//
//        mockMvc.perform(MockMvcRequestBuilders.multipart(TEST_URL_API_V1_FILES_UPLOAD)
//                        .file(getFile())
//                        .header(AUTHORIZATION, TEST_TEXT_BEARER + authenticateRoleUser()))
//                .andExpect(status().isForbidden());
//    }

    @Test
    void whenRenameFileShouldReturn200() throws Exception {

        ResultActions resultActions = mockMvc.perform(put(format(TEST_URL_API_V1_FILES_ID, fileResponseDto.getId()))
                        .header(AUTHORIZATION, TEST_TEXT_BEARER + tokenAdmin)
                        .contentType(APPLICATION_JSON)
                        .content(getFileRenameRequestDtoJson()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON));
        FileResponseDto updatedFileResponseDto = mapFromResultActions(resultActions, FileResponseDto.class);

        assertEquals(updatedFileResponseDto.getId(), fileResponseDto.getId());
        assertNotEquals(updatedFileResponseDto.getName(), fileResponseDto.getName());
        assertNotEquals(updatedFileResponseDto.getLocation(), fileResponseDto.getLocation());
        assertNotNull(updatedFileResponseDto.getUpdatedAt());
        assertEquals(updatedFileResponseDto.getStatus(), fileResponseDto.getStatus());
    }

    @Test
    void whenRenameFileShouldReturn403JwtExceptionBecauseTokenInvalid() throws Exception {

        mockMvc.perform(put(format(TEST_URL_API_V1_FILES_ID, fileResponseDto.getId()))
                        .header(AUTHORIZATION, TEST_TEXT_BEARER + getInvalidToken())
                        .contentType(APPLICATION_JSON)
                        .content(getFileRenameRequestDtoJson()))
                .andExpect(status().isForbidden());
    }

    //TODO: correct a method, should return 403, return 500 is not correct
//    @Test
//    void whenRenameFileShouldReturn403AccessDeniedExceptionBecauseRoleUser() throws Exception {
//
//        ResultActions resultActionsUploadFile = getUploadFile();
//
//        FileResponseDto fileResponseDtoUploadFile = getFileResponseDtoUploadFile(resultActionsUploadFile);
//
//        String fileRenameRequestDtoJson = getFileRenameRequestDtoJson();
//
//        mockMvc.perform(MockMvcRequestBuilders.put(format(TEST_URL_PUT_API_V1_FILES_ID, fileResponseDtoUploadFile.getId()))
//                        .header(AUTHORIZATION, TEST_TEXT_BEARER + authenticateRoleUser())
//                        .contentType(APPLICATION_JSON)
//                        .content(fileRenameRequestDtoJson))
//                .andExpect(status().isForbidden());
//    }

    @Test
    void whenUpdateFileContentShouldReturn200() throws Exception {

        ResultActions resultActions = mockMvc.perform(multipart(format(TEST_URL_API_V1_FILES_ID_UPDATE_CONTENT, fileResponseDto.getId()))
                        .file(getFileUpdateContent())
                        .with(request -> {
                            request.setMethod(TEST_TEXT_PUT);
                            return request;
                        })
                        .header(AUTHORIZATION, TEST_TEXT_BEARER + tokenAdmin))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON));

        FileResponseDto updatedFileResponseDto = mapFromResultActions(resultActions, FileResponseDto.class);

        assertEquals(updatedFileResponseDto.getId(), fileResponseDto.getId());
        assertEquals(updatedFileResponseDto.getName(), fileResponseDto.getName());
        assertEquals(updatedFileResponseDto.getLocation(), fileResponseDto.getLocation());
        assertNotNull(updatedFileResponseDto.getUpdatedAt());
        assertEquals(updatedFileResponseDto.getStatus(), fileResponseDto.getStatus());
    }

    @Test
    void whenUpdateFileContentShouldReturn403JwtExceptionBecauseTokenInvalid() throws Exception {

        mockMvc.perform(multipart(format(TEST_URL_API_V1_FILES_ID_UPDATE_CONTENT, fileResponseDto.getId()))
                        .file(getFileUpdateContent())
                        .with(request -> {
                            request.setMethod(TEST_TEXT_PUT);
                            return request;
                        })
                        .header(AUTHORIZATION, TEST_TEXT_BEARER + getInvalidToken()))
                .andExpect(status().isForbidden());
    }

    //TODO: correct a method, should return 403, return 500 is not correct
//    @Test
//    void whenUpdateFileContentShouldReturn403AccessDeniedExceptionBecauseRoleUser() throws Exception {
//
//        mockMvc.perform(MockMvcRequestBuilders.multipart(format(TEST_URL_PUT_API_V1_FILES_ID_UPDATE_COMTENT, fileResponseDto.getId()))
//                        .file(getFileUpdateContent())
//                        .with(request -> {
//                            request.setMethod(TEST_TEXT_PUT);
//                            return request;
//                        })
//                        .header(AUTHORIZATION, TEST_TEXT_BEARER + authenticateRoleUser()))
//                .andExpect(status().isForbidden());
//    }

    @Test
    void whenDownloadFileShouldReturn200() throws Exception {

        ResultActions resultActions = mockMvc.perform(get(TEST_URL_API_V1_FILES_DOWNLOAD)
                        .header(AUTHORIZATION, TEST_TEXT_BEARER + tokenAdmin)
                        .contentType(APPLICATION_JSON)
                        .content(getFileDownloadRequestDto(fileResponseDto.getLocation())))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM));

        byte[] downloadedFile = resultActions.andReturn().getResponse().getContentAsByteArray();

        assertTrue(downloadedFile.length > 0);
        assertArrayEquals(downloadedFile, getFile().getBytes());
    }

    @Test
    void whenDownloadFileShouldReturn403JwtExceptionBecauseTokenInvalid() throws Exception {

        mockMvc.perform(get(TEST_URL_API_V1_FILES_DOWNLOAD)
                        .header(AUTHORIZATION, TEST_TEXT_BEARER + getInvalidToken())
                        .contentType(APPLICATION_JSON)
                        .content(getFileDownloadRequestDto(fileResponseDto.getLocation())))
                .andExpect(status().isForbidden());
    }

    //TODO: correct a method, should return 403, return 500 is not correct
//    @Test
//    void whenDownloadFileShouldReturn403AccessDeniedExceptionBecauseRoleUser() throws Exception {
//
//        mockMvc.perform(MockMvcRequestBuilders.get(TEST_URL_GET_API_V1_FILES_DOWNLOAD)
//                        .header(AUTHORIZATION, TEST_TEXT_BEARER + authenticateRoleUser())
//                        .contentType(APPLICATION_JSON)
//                        .content(getFileDownloadRequestDto(fileResponseDto.getLocation())))
//                .andExpect(status().isForbidden());
//    }

    @Test
    void whenFindAllFilesShouldReturn200() throws Exception {

        mockMvc.perform(get(TEST_URL_API_V1_FILES)
                        .header(AUTHORIZATION, TEST_TEXT_BEARER + tokenAdmin))
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(greaterThan(0))))
                .andExpect(jsonPath("$", is(notNullValue())));
    }

    @Test
    void whenFindAllFilesShouldReturn403JwtExceptionBecauseTokenInvalid() throws Exception {

        mockMvc.perform(get(TEST_URL_API_V1_FILES)
                        .header(AUTHORIZATION, TEST_TEXT_BEARER + getInvalidToken()))
                .andExpect(status().isForbidden());
    }

    //TODO: correct a method, should return 403, return 500 is not correct
//    @Test
//    void whenFindAllFilesShouldReturn403AccessDeniedExceptionBecauseRoleUser() throws Exception {
//
//        mockMvc.perform(MockMvcRequestBuilders.get(TEST_URL_API_V1_FILES)
//                        .header(AUTHORIZATION, TEST_TEXT_BEARER + authenticateRoleUser()))
//                .andExpect(status().isForbidden());
//    }

    @Test
    void whenFindByIdShouldReturn200() throws Exception {

        ResultActions resultActions = mockMvc.perform(get(format(TEST_URL_API_V1_FILES_ID, fileResponseDto.getId()))
                        .header(AUTHORIZATION, TEST_TEXT_BEARER + tokenAdmin))
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(status().isOk());

        FileResponseDto fileResponseDto = mapFromResultActions(resultActions, FileResponseDto.class);

        assertNotNull(fileResponseDto);
    }

    @Test
    void whenFindByIdShouldReturn403JwtExceptionBecauseTokenInvalid() throws Exception {

        mockMvc.perform(get(format(TEST_URL_API_V1_FILES_ID, fileResponseDto.getId()))
                        .header(AUTHORIZATION, TEST_TEXT_BEARER + getInvalidToken()))
                .andExpect(status().isForbidden());
    }

    //TODO: correct a method, should return 403, return 500 is not correct
//    @Test
//    void whenFindByIdShouldReturn403AccessDeniedExceptionBecauseRoleUser() throws Exception {
//
//        mockMvc.perform(get(format(TEST_URL_API_V1_FILES_ID, fileResponseDto.getId()))
//                        .header(AUTHORIZATION, TEST_TEXT_BEARER + authenticateRoleUser()))
//                .andExpect(status().isForbidden());
//    }

    @Test
    void whenDeleteByIdShouldReturn200() throws Exception {

        mockMvc.perform(delete(format(TEST_URL_API_V1_FILES_ID, fileResponseDto.getId()))
                .header(AUTHORIZATION, TEST_TEXT_BEARER + tokenAdmin))
                .andExpect(status().isOk());
    }

    @Test
    void whenDeleteByIdShouldReturn403JwtExceptionBecauseTokenInvalid() throws Exception {

        mockMvc.perform(delete(format(TEST_URL_API_V1_FILES_ID, fileResponseDto.getId()))
                .header(AUTHORIZATION, TEST_TEXT_BEARER + getInvalidToken()))
                .andExpect(status().isForbidden());
    }

    //TODO: correct a method, should return 403, return 500 is not correct
//    @Test
//    void whenDeleteByIdShouldReturn403AccessDeniedExceptionBecauseRoleUser() throws Exception {
//
//        mockMvc.perform(delete(format(TEST_URL_API_V1_FILES_ID, fileResponseDto.getId()))
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
//                .andExpect(status().isForbidden());
//    }
    private MockMultipartFile getFile() {
        return new MockMultipartFile("file", "filename.txt", "text/plain", "some text".getBytes());
    }

    private MockMultipartFile getFileUpdateContent() {
        return new MockMultipartFile("file", "filename.txt", "text/plain", "some text + added extra text".getBytes());
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

    private String getFileRenameRequestDtoJson() throws JsonProcessingException {
        return objectMapper.writeValueAsString(FileRenameRequestDto.builder().newFileName(TEST_TEXT_RANDOM_FILE_NAME).build());
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

    private FileResponseDto getFileResponseDto() throws Exception {
        return mapFromResultActions(getUploadFile(), FileResponseDto.class);
    }

    private String getFileDownloadRequestDto(String location) throws JsonProcessingException {
        return objectMapper.writeValueAsString(FileDownloadRequestDto.builder().location(location).build());
    }
}