package spring.boot.rest.api.util;

public final class Constants {

    private Constants() {
        throw new IllegalStateException(TEXT_UTILITY_CLASS);
    }
    public static final String INVALID_JWT_AUTH_EXCEPTION = "Invalid jwt authentication exception";
    public static final String JWT_TOKEN_IS_EXPIRED_OR_INVALID = "JWT token is expired or invalid";
    public static final String USERNAME_NOT_FOUND_EXCEPTION = "Username not found exception";
    public static final String FAILED_TO_FILES_BY_USER_ID = "Failed to find files by user id - [%d]";

    public static final String DATABASE_OPERATION_ERROR = "Database Operation Error: ";
    public static final String DATABASE_OPERATION_ERROR_FAILED_TO_FIND_USER = DATABASE_OPERATION_ERROR + "Failed to find user by id - %d";
    public static final String DATABASE_OPERATION_ERROR_FAILED_TO_FIND_EVENT = DATABASE_OPERATION_ERROR + "Failed to find event by id - %d";
    public static final String DATABASE_OPERATION_ERROR_FAILED_TO_FIND_FILE = DATABASE_OPERATION_ERROR + "Failed to find file by id - %d";

    public static final String NOT_FOUND_EXCEPTION = "Not found exception";
    public static final String ACCESS_DENIED_EXCEPTION = "Access denied exception";
    public static final String FILE_EXCEPTION = "File exception";
    public static final String NOT_FOUND_USER = "Can't find the user by id - %d";
    public static final String NOT_FOUND_FILE = "Can't find the file by id - %d";
    public static final String NOT_FOUND_EVENT = "Can't find the event by id - %d";
    public static final String FAILED_TO_SAVE_A_EVENT = "Failed to save event: ";
    public static final String FAILED_TO_UPDATE_A_EVENT_BY_ID = "Failed to update a event by id - [%d]: ";
    public static final String FAILED_TO_FIND_A_EVENT_BY_ID = "Failed to find a event by id - [%d]: ";
    public static final String FAILED_TO_FIND_ALL_EVENTS = "Failed to find all events: ";
    public static final String FAILED_TO_DELETE_ALL_EVENTS = "Failed to delete all event: ";
    public static final String FAILED_TO_SAVE_USER = "Failed to save user: ";
    public static final String FAILED_TO_UPDATE_USER = "Failed to update user by id - [%d]: ";
    public static final String FAILED_TO_FIND_ALL_USERS = "Failed to find all users: ";
    public static final String FAILED_TO_DELETE_USER_BY_ID = "Failed to delete user by id - [%d]: ";
    public static final String FAILED_TO_DELETE_ALL_USERS = "Failed to delete all users: ";
    public static final String FAILED_TO_SAVE_FILE = "Failed to save file: ";
    public static final String ERROR_READ_FILE = "An error occurred while reading the file";
    public static final String ERROR_UPLOAD_FILE_TO_AWS_S3 = "An error occurred while uploading the file to AWS S3";
    public static final String ERROR_DOWNLOAD_FILE_WITH_AWS_S3 = "An error occurred while downloading the file with AWS S3";
    public static final String FAILED_TO_UPDATE_FILE = "Failed to update a file";
    public static final String FAILED_TO_FIND_ALL_FILES = "Failed to find all skills: ";
    public static final String FAILED_TO_DELETE_FILE_BY_ID = "Failed to delete skill by id - [%d]: ";
    public static final String FAILED_TO_DELETE_ALL_FILES = "Failed to delete all skills: ";
    public static final String URL_API_V1_USERS = "/api/v1/users";
    public static final String URL_API_V1_FILES = "/api/v1/files";
    public static final String URL_API_V1_EVENTS = "/api/v1/events";
    public static final String SLASH = "/";
    public static final String TEXT_NAME = "name";
    public static final String TEXT_UTILITY_CLASS = "Utility class";
    public static final String TEXT_SPRING = "spring";
    public static final String ACCESS_DENIED_USER_DELETED = "Access denied: user with id - [%d] is DELETED";
    public static final String ACCESS_DENIED = "Access denied";
    public static final String S3_SECRET_KEY = "%s/%s";
    public static final String NOT_IMPLEMENT_EXCEPTION = "The method -> %s does not implement";
    public static final String TEXT_UPDATE = "update()";
    public static final String FAILED_TO_RAED_ALL_BYTES_WHEN_GET_OBJECT_TO_S3_SERVICE = "Failed to read the object all bytes to the method 'getObject'";
    public static final String GENERAL_EXCEPTION = "General exception";

    public static final String RUNTIME_EXCEPTION = "Runtime exception";
    public static final String ERROR_EXCEPTION = "Error exception";
    public static final String FAILED_TO_FIND_TOKEN = "Failed to find token";
    public static final String URL_API_V1_AUTH_LOGOUT = "/api/v1/auth/logout";
    public static final String BEARER = "Bearer ";
    public static final String JWT_TOKEN_EXPIRED_OR_INVALID = "JWT token is expired or invalid";

    public static final String BAD_CREDENTIALS = "Bad credentials";
    public static final String CREATED_AT = "created_at";
    public static final String UPDATED_AT = "updated_at";
    public static final String STATUS = "status";
    public static final String NAME = "name";
    public static final String USER_ID = "user_id";
    public static final String FILE_ID = "file_id";
    public static final String EVENTS = "events";
    public static final String S3_SECRET = "s3_secret";
    public static final String S3_BUCKET = "s3_bucket";
    public static final String LOCATION = "location";
    public static final String FILES = "files";
    public static final String PERMISSION_READ_SELF = "read:self";
    public static final String PERMISSION_DOWNLOAD_FILE = "download:file";
    public static final String PERMISSION_READ_WRITE_DELETE_EVENTS = "read_write_delete:events";
    public static final String PERMISSION_READ_WRITE_DELETE_USERS = "read_write_delete:users";
    public static final String PERMISSION_READ_WRITE_DELETE_FILES = "read_write_delete:files";
    public static final String PERMISSION_MANAGE_USERS = "manage:users";
    public static final String PERMISSION_MANAGE_ROLES = "manage:roles";
    public static final String TOKENS = "tokens";
    public static final String TOKEN = "token";
    public static final String TOKEN_TYPE = "token_type";
    public static final String EXPIRED = "expired";
    public static final String REVOKED = "revoked";
    public static final String USERS = "users";
    public static final String FIRST_NAME = "first_name";
    public static final String LAST_NAME = "last_name";
    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";
    public static final String ROLE = "role";
    public static final String USER = "user";
    public static final String QUERY_FIND_ALL_TOKENS_BY_USER_ID_AND_EXPIRED_REVOKED_FALSE = "SELECT t FROM Token t INNER JOIN User u ON t.user.id = u.id " +
            "WHERE u.id = :userId AND (t.expired = false or t.revoked = false)";
    public static final String URL_UPLOAD = "/upload";
    public static final String URL_API_V1_AUTH = "/api/v1/auth";
    public static final String URL_AUTHENTICATE = "/authenticate";
    public static final String URL_REGISTER = "/register";
    public static final String URL_REFRESH_TOKEN = "/refresh_token";
    public static final String FULL_URL_AUTHENTICATE = URL_API_V1_AUTH + URL_AUTHENTICATE;
    public static final String FULL_URL_REGISTER = URL_API_V1_AUTH + URL_REGISTER;
    public static final String URL_ID = "/{id}";
    public static final String URL_ID_UPDATE_CONTENT = URL_ID + "/update-content";
    public static final String URL_DOWNLOAD = "/download";
    public static final String URL_ID_FILES = URL_ID + "/files";
    public static final String ID = "id";
    public static final String FILE = "file";
    public static final Integer ONE = 1;
    public static final Integer SEVEN = 7;
    public static final String ATTACHMENT_FILENAME = "attachment;filename=";
    public static final String MUST_REVALIDATE_POST_CHECK_0 = "must-revalidate, post-check=0, pre-check=0";
    public static final String USER_IS_DELETED = "User is deleted";
    public static final String FILE_NOT_EXISTS = "File not exists!";
    public static final String FIELD_NAME_FIRST_NAME = "firstName";
}
