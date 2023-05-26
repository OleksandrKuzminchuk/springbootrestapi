package spring.boot.rest.api.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.model.S3Exception;
import spring.boot.rest.api.aws.S3Properties;
import spring.boot.rest.api.aws.S3Service;
import spring.boot.rest.api.exception.DatabaseOperationException;
import spring.boot.rest.api.exception.FileException;
import spring.boot.rest.api.exception.NotFoundException;
import spring.boot.rest.api.exception.NotImplementException;
import spring.boot.rest.api.model.Event;
import spring.boot.rest.api.model.File;
import spring.boot.rest.api.model.Status;
import spring.boot.rest.api.model.User;
import spring.boot.rest.api.repository.FileRepo;
import spring.boot.rest.api.repository.UserRepo;
import spring.boot.rest.api.service.FileService;
import spring.boot.rest.api.service.UserService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static java.lang.String.format;
import static org.springframework.data.domain.Sort.Order.by;
import static spring.boot.rest.api.model.Status.ACTIVE;
import static spring.boot.rest.api.util.Constants.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
    private final S3Properties s3Properties;
    private final S3Service s3Service;
    private final FileRepo fileRepo;
    private final UserService userService;
    private final UserRepo userRepo;


    @Override
    public File save(File file) {
        try {
            log.info("IN save() file to db -> '{}'...", file);
            final var savedFile = fileRepo.save(file);
            log.info("IN save() file to db -> '{}' -> saved SUCCESSFULLY", file);
            return savedFile;
        } catch (DataAccessException e) {
            log.error("IN save() file to db -> '{}' -> FAILED", file);
            throw new DatabaseOperationException(FAILED_TO_SAVE_FILE, e);
        }
    }

    @Override
    public File update(File file) {
        throw new NotImplementException(format(NOT_IMPLEMENT_EXCEPTION, TEXT_UPDATE));
    }

    @Override
    public File findById(Long id) {
        log.info("IN findById() file -> find by id - '{}'...", id);
        final var file = checkIfFileExists(id);
        log.info("IN findById() file -> found by id - '{}' -> SUCCESSFULLY", id);
        return file;
    }

    @Override
    public List<File> findAll() {
        try {
            log.info("IN findAll() -> processing...");
            final var filesList = fileRepo.findAll(Sort.by(by("createdAt")));
            log.info("IN findAll() -> found all SUCCESSFULLY");
            return filesList;
        } catch (DataAccessException e) {
            log.error("IN findAll() -> FAILED");
            throw new DatabaseOperationException(FAILED_TO_FIND_ALL_FILES, e);
        }
    }

    @Override
    public void deleteById(Long id) {
        try {
            log.info("IN deleteById() -> deleting the file by id - '{}'...", id);
            final var deleteFile = checkIfFileExists(id);
            deleteFile.setStatus(Status.DELETED);
            fileRepo.save(deleteFile);
            log.info("IN deleteById() -> deleting the file by id - '{}' -> SUCCESSFULLY", id);
        } catch (DataAccessException e) {
            log.error("IN deleteById() -> deleting the file by id - '{}' -> FAILED", id);
            throw new DatabaseOperationException(format(FAILED_TO_DELETE_FILE_BY_ID, id), e);
        }
    }

    @Override
    public void deleteAll() {
        try {
            log.info("IN deleteAll() -> delete all files...");
            final var fileList = this.findAll();
            fileList.stream()
                    .filter(file -> file.getStatus().equals(ACTIVE))
                    .forEach(file -> {
                        file.setStatus(Status.DELETED);
                        this.deleteById(file.getId());
                    });
            log.info("IN deleteAll() -> delete all files -> SUCCESSFULLY");
        } catch (DataAccessException e) {
            log.error("IN deleteAll() -> delete all files -> FAILED");
            throw new DatabaseOperationException(FAILED_TO_DELETE_ALL_FILES, e);
        }
    }

    @Override
    public File upload(MultipartFile file) {
        try {
            return processingUploadFile(file);
        } catch (IOException e) {
            throw new FileException(ERROR_READ_FILE, e);
        } catch (S3Exception e) {
            throw new FileException(ERROR_UPLOAD_FILE_TO_AWS_S3, e);
        }
    }

    @Override
    public byte[] download(String location) {
        try {
            return processingDownloadFile(location);
        } catch (S3Exception e) {
            throw new FileException(ERROR_DOWNLOAD_FILE_WITH_AWS_S3, e);
        }
    }

    @Override
    public File updateName(Long id, String newFileName) {
        try {
            return processingUpdateName(id, newFileName);
        } catch (S3Exception e) {
            throw new FileException(ERROR_UPLOAD_FILE_TO_AWS_S3, e);
        }
    }

    @Override
    public File updateFileContent(Long id, MultipartFile newFile) {
        try {
            return processingUpdateFileContent(id, newFile);
        } catch (IOException e) {
            throw new FileException(FAILED_TO_UPDATE_FILE, e);
        } catch (S3Exception e) {
            throw new FileException(ERROR_UPLOAD_FILE_TO_AWS_S3, e);
        }
    }

    public File checkIfFileExists(Long id) {
        try {
            log.info("IN isExistsFile(id) file -> by id - '{}'...", id);
            final var foundFile = fileRepo.findById(id)
                    .orElseThrow(() -> new NotFoundException(format(NOT_FOUND_FILE, id)));
            log.info("IN isExistsFile(id) file -> by id - '{}' -> found SUCCESSFULLY", id);
            return foundFile;
        }catch (DataAccessException e){
            log.error("IN isExistsFile(id) file -> by id - '{}' -> FAILED", id, e);
            throw new DatabaseOperationException(format(DATABASE_OPERATION_ERROR_FAILED_TO_FIND_FILE, id), e);
        }
    }

    private File processingUpdateFileContent(Long id, MultipartFile newFile) throws IOException {
        log.info("IN updateFileContent() -> processing...");

        final var existFile = this.checkIfFileExists(id);

        s3Service.putObject(s3Properties.getCustomer(), existFile.getS3Secret(), newFile.getBytes());

        existFile.setUpdatedAt(LocalDateTime.now());

        final var savedFile = this.save(existFile);

        log.info("IN updateFileContent() -> updated the file SUCCESSFULLY");

        return savedFile;
    }

    private File processingUploadFile(MultipartFile file) throws IOException {
        log.info("IN upload() file -> '{}'...", file.getOriginalFilename());
        final var fileName = file.getOriginalFilename();
        final var profileFileId = getUUIDRandomKey();
        final var s3Secret = S3_SECRET_KEY.formatted(profileFileId, fileName);

        log.debug("IN upload() file -> uploading to AWS...");
        final var url = s3Service.putObject(
                s3Properties.getCustomer(),
                s3Secret,
                file.getBytes()
        );
        log.debug("IN upload() file -> upload to AWS -> SUCCESSFULLY");

        final var uploadedFile = File.builder()
                .name(fileName)
                .s3Secret(s3Secret)
                .s3Bucket(s3Properties.getCustomer())
                .location(url)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .status(ACTIVE).build();
        log.debug("IN upload() file -> saving file '{}' to db...", uploadedFile);

        final var savedFile = this.save(uploadedFile);
        log.debug("IN upload() file -> saved file '{}' to db -> SUCCESSFULLY", savedFile);
        log.info("IN upload() file -> uploaded file '{}' -> SUCCESSFULLY", file.getOriginalFilename());
        return savedFile;
    }

    private File processingUpdateName(Long id, String newFileName) {
        log.info("IN updateName() -> processing...");
        final var existingFile = this.findById(id);

        final var newS3Secret = S3_SECRET_KEY.formatted(getUUIDRandomKey(), newFileName);

        log.info("IN updateName() -> copyObject()...");

        final var url = s3Service.copyObject(s3Properties.getCustomer(), existingFile.getS3Secret(), s3Properties.getCustomer(), newS3Secret);

        log.info("IN updateName() -> deleteObject()...");

        s3Service.deleteObject(s3Properties.getCustomer(), existingFile.getS3Secret());

        existingFile.setName(newFileName);
        existingFile.setS3Secret(newS3Secret);
        existingFile.setLocation(url);
        existingFile.setUpdatedAt(LocalDateTime.now());

        log.info("IN updateName() -> save()...");

        final var savedFile = this.save(existingFile);

        log.info("IN updateName() -> renamed the file SUCCESSFULLY");

        return savedFile;
    }

    private byte[] processingDownloadFile(String location) {
        log.info("IN download() file by location -> '{}'...", location);
        final var allFiles = this.findAll();
        final var userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        final Optional<User> isExistsUser = Optional.ofNullable(userRepo.findByEmail(userEmail).orElseThrow(() -> new NotFoundException("User not exists")));

        if (!userService.currentUserIsModeratorOrAdmin()) {
            if (isExistsUser.isPresent() && Objects.equals(isExistsUser.get().getStatus(), ACTIVE)) {
                final var file = isExistsUser.get().getEvents()
                        .stream()
                        .map(Event::getFile)
                        .filter(f -> Objects.equals(f.getLocation(),location) && Objects.equals(f.getStatus(), ACTIVE))
                        .findFirst()
                        .orElseThrow(() -> new NotFoundException(FILE_NOT_EXISTS));
                final var fileContent = s3Service.getObject(s3Properties.getCustomer(), file.getS3Secret());
                log.info("IN download() file by location -> '{}' -> downloaded SUCCESSFULLY", location);
                return fileContent;
            }else {
                throw new NotFoundException(USER_IS_DELETED);
            }
        }else {
                final var file = allFiles
                        .stream()
                        .filter(f -> Objects.equals(f.getLocation(), location))
                        .findFirst()
                        .orElseThrow(() -> new NotFoundException("File not exists!"));
                final var fileContent = s3Service.getObject(s3Properties.getCustomer(), file.getS3Secret());
                log.info("IN download() file by location -> '{}' -> downloaded SUCCESSFULLY", location);
                return fileContent;
        }
    }

    private String getUUIDRandomKey(){
        return UUID.randomUUID().toString();
    }
}
