package spring.boot.rest.api.util.constant;

import spring.boot.rest.api.model.Role;

public final class Constants {

    private Constants() {
        throw new IllegalStateException(TEXT_UTILITY_CLASS);
    }

    public static final String DATABASE_OPERATION_ERROR = "Database Operation Error: ";
    public static final String DATABASE_OPERATION_ERROR_FAILED_TO_FIND_USER = DATABASE_OPERATION_ERROR + "Failed to find user by id - %d";
    public static final String DATABASE_OPERATION_ERROR_FAILED_TO_FIND_EVENT = DATABASE_OPERATION_ERROR + "Failed to find event by id - %d";
    public static final String DATABASE_OPERATION_ERROR_FAILED_TO_FIND_FILE = DATABASE_OPERATION_ERROR + "Failed to find file by id - %d";

    public static final String NOT_FOUND_EXCEPTION = "Not found exception";
    public static final String ACCESS_DENIED_EXCEPTION = "Access denied exception";
    public static final String FILE_EXCEPTION = "File exception";
    public static final String CREATE_TABLES_FILE = "classpath:/db/migration";
    public static final String HIBERNATE_PROPERTIES = "hibernate.properties";
    public static final String APPLICATION_JSON = "application/json";
    public static final String APPLICATION_OCTET_STREAM = "application/octet-stream";
    public static final String NOT_FOUND_USER = "Can't find the user by id - %d";
    public static final String NOT_FOUND_FILE = "Can't find the file by id - %d";
    public static final String NOT_FOUND_EVENT = "Can't find the event by id - %d";
    public static final String FAILED_TO_SAVE_A_EVENT = "Failed to save event: ";
    public static final String FAILED_TO_UPDATE_A_EVENT_BY_ID = "Failed to update a event by id - [%d]: ";
    public static final String FAILED_TO_FIND_A_EVENT_BY_ID = "Failed to find a event by id - [%d]: ";
    public static final String FAILED_TO_FIND_ALL_EVENTS = "Failed to find all events: ";
    public static final String FAILED_TO_DELETE_A_EVENT_BY_ID = "Failed to delete a event by id - [%d]: ";
    public static final String FAILED_TO_DELETE_ALL_EVENTS = "Failed to delete all event: ";
    public static final String FAILED_TO_SAVE_USER = "Failed to save user: ";
    public static final String FAILED_TO_UPDATE_USER = "Failed to update user by id - [%d]: ";
    public static final String FAILED_TO_FIND_USER_BY_ID = "Failed to find user by id - [%d]";
    public static final String FAILED_TO_FIND_ALL_USERS = "Failed to find all users: ";
    public static final String FAILED_TO_DELETE_USER_BY_ID = "Failed to delete user by id - [%d]: ";
    public static final String FAILED_TO_DELETE_ALL_USERS = "Failed to delete all users: ";
    public static final String FAILED_TO_SAVE_FILE = "Failed to save file: ";
    public static final String ERROR_READ_FILE = "An error occurred while reading the file";
    public static final String ERROR_UPLOAD_FILE_TO_AWS_S3 = "An error occurred while uploading the file to AWS S3";
    public static final String ERROR_DOWNLOAD_FILE_WITH_AWS_S3 = "An error occurred while downloading the file with AWS S3";
    public static final String FAILED_TO_UPDATE_FILE = "Failed to update a file";
    public static final String FAILED_TO_FIND_FILE_BY_ID = "Failed to find a skill by id - [%d]: ";
    public static final String FAILED_TO_FIND_ALL_FILES = "Failed to find all skills: ";
    public static final String FAILED_TO_DELETE_FILE_BY_ID = "Failed to delete skill by id - [%d]: ";
    public static final String FAILED_TO_DELETE_ALL_FILES = "Failed to delete all skills: ";
    public static final String PARAMETER_ID = "id";
    public static final String FAILED_DATABASE_CONNECTION = "Failed to establish database connection";
    public static final String NO_CORRECT_REQUEST = "No correct a request";
    public static final String HIBERNATE_CONNECTION_URL = "hibernate.connection.url";
    public static final String HIBERNATE_CONNECTION_USERNAME = "hibernate.connection.username";
    public static final String HIBERNATE_CONNECTION_PASSWORD = "hibernate.connection.password";
    public static final String DATABASE_MIGRATION_SUCCESSFULLY = "Database migration completed successfully!";
    public static final String DATABASE_MIGRATION_FAILED = "Database migration failed: ";
    public static final String REGEX_FOLLOWED_BY_AN_INTEGER = "/\\d+";
    public static final String USER_REST_CONTROLLER_V1 = "UserRestControllerV1";
    public static final String FILE_REST_CONTROLLER_V1 = "FileRestControllerV1";
    public static final String EVENT_REST_CONTROLLER_V1 = "EventRestControllerV1";
    public static final String URL_API_V1_USERS = "/api/v1/users";
    public static final String URL_API_V1_FILES = "/api/v1/files";
    public static final String URL_API_V1_EVENTS = "/api/v1/events";
    public static final String SLASH = "/";
    public static final String DIRECTORY_FILE_PACKAGE = "C:\\Users\\Kuzminchuk_Alexandr\\IdeaProjects\\servlet_http\\src\\main\\resources\\file";
    public static final String IO_EXCEPTION = "Failed to input or output stream: ";
    public static final String TEXT_ID = "id";
    public static final String TEXT_NAME = "name";
    public static final String FILE_HAS_ALREADY_TAKEN = "The file has already taken with the name. Do you want to change the name of file or overwrite it?";
    public static final String TEXT_OVERWRITE = "overwrite";
    public static final String TEXT_TRUE = "true";
    public static final String TEXT_FILE = "file";
    public static final String TEXT_ZERO = "0";
    public static final String TEXT_USER = "user";
    public static final String TEXT_FILE_PATH = "filePath";
    public static final String TEXT_EVENTS = "events";
    public static final String TEXT_CONTENT_DISPOSITION = "Content-Disposition";
    public static final String TEXT_ATTACHMENT_FILENAME = "attachment; filename=";
    public static final String DOUBLE_QUOTES = "\"";
    public static final String FILE_ID_IS_REQUIRED = "File ID is required";
    public static final String USER_ID_IS_REQUIRED = "User ID is required";
    public static final String EVENT_ID_IS_REQUIRED = "Event ID is required";
    public static final String NEW_FILE_NAME_IS_REQUIRED = "New file name is required";
    public static final String FILE_NAME_HAS_ALREADY_TAKEN = "The file name is already taken. Please choose a different name.";
    public static final String FILE_USER_IS_REQUIRED = "User and File are required";
    public static final String TEXT_UTILITY_CLASS = "Utility class";
    public static final Integer NUMBER_1 = 1;
    public static final boolean TRUE = true;
    public static final String TEXT_SPRING = "spring";
    public static final String TEXT_STATUS = "status";
    public static final String TEXT_USER_ID = "userId";
    public static final String TEXT_FILE_ID = "fileId";
    public static final String ACCESS_DENIED_USER_DELETED = "Access denied: user with id - [%d] is DELETED";
    public static final String TEXT_ROLE_ADMIN = "ROLE_" + Role.ADMIN.name();
    public static final String TEXT_ROLE_MODERATOR = "ROLE_" + Role.MODERATOR.name();
    public static final String TEXT_ROLE_USER = "ROLE_" + Role.USER.name();
    public static final String S3_SECRET_KEY = "%s/%s";
    public static final String ERROR_CAN_NOT_FIND_FILE_BY_SECRET_KEY = "Can't find file by secret key - [%s]";
    public static final String FAILED_TO_FIND_FILE_BY_SECRET_KEY = "Failed to find the file by secret key - [%s]";
    public static final String NOT_IMPLEMENT_EXCEPTION = "The method -> %s does not implement";
    public static final String TEXT_UPDATE = "update()";
    public static final String FAILED_TO_RAED_ALL_BYTES_WHEN_GET_OBJECT_TO_S3_SERVICE = "Failed to read the object all bytes to the method 'getObject'";
    public static final String FAILED_TO_FIND_FILES_BY_CREATED_AT = "Failed to find files by created at";

    public static final String GENERAL_EXCEPTION = "General exception";

    public static final String RUNTIME_EXCEPTION = "Runtime exception";
    public static final String ERROR_EXCEPTION = "Error exception";
}
