package spring.boot.rest.api.util;

public final class TestConstants {
    private TestConstants() {
    }

    public static final String TEST_TEXT_BEARER = "Bearer ";
    public static final String TEST_URL_API_V1_FILES =  "/api/v1/files";
    public static final String TEST_URL_API_V1_USERS =  "/api/v1/users";
    public static final String TEST_URL_API_V1_EVENTS =  "/api/v1/events";
    public static final String TEST_URL_API_V1_FILES_UPLOAD = TEST_URL_API_V1_FILES + "/upload";
    public static final String TEST_TEXT_PASSWORD_USER = "testPassword";
    public static final String TEST_TEXT_API_V1_AUTH = "/api/v1/auth";
    public static final String TEST_TEXT_API_V1_AUTH_AUTHENTICATE = TEST_TEXT_API_V1_AUTH + "/authenticate";
    public static final String TEST_TEXT_API_V1_AUTH_REGISTER = TEST_TEXT_API_V1_AUTH + "/register";
    public static final String TEST_TEXT_API_V1_AUTH_REFRESH_TOKEN = TEST_TEXT_API_V1_AUTH + "/refresh_token";
    public static final String TEST_URL_API_V1_FILES_ID = TEST_URL_API_V1_FILES + "/%d";
    public static final String TEST_URL_API_V1_USERS_ID = TEST_URL_API_V1_USERS + "/%d";
    public static final String TEST_URL_API_V1_EVENTS_ID = TEST_URL_API_V1_EVENTS + "/%d";
    public static final String TEST_URL_API_V1_FILES_ID_UPDATE_CONTENT = TEST_URL_API_V1_FILES + "/%d/update-content";
    public static final String TEST_URL_API_V1_FILES_DOWNLOAD = TEST_URL_API_V1_FILES + "/download";
    public static final String TEST_URL_API_V1_USERS_ID_FILES = TEST_URL_API_V1_USERS_ID + "/files";
    public static final String TEST_TEXT_RANDOM_FILE_NAME = "random";
    public static final String TEST_TEXT_PUT = "PUT";
    public static final String INVALID_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0MUBnbWFpbC5jb20iLCJpYXQiOjE2ODQ1MDk4NjIsImV4cCI6MTY4NDUxMzQ2Mn0.EH9Aa-hR5EsoN4li4Qcce3Niclll4bQghXpOqoutiVY";
}
