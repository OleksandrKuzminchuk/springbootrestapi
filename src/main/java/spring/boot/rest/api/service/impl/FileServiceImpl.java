package spring.boot.rest.api.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.model.S3Exception;
import spring.boot.rest.api.dto.FileDTO;
import spring.boot.rest.api.dto.FileUpdateDTO;
import spring.boot.rest.api.exception.DatabaseOperationException;
import spring.boot.rest.api.exception.FileException;
import spring.boot.rest.api.exception.NotFoundException;
import spring.boot.rest.api.exception.NotImplementException;
import spring.boot.rest.api.mapper.EventMapper;
import spring.boot.rest.api.mapper.FileMapper;
import spring.boot.rest.api.mapper.UserMapper;
import spring.boot.rest.api.model.File;
import spring.boot.rest.api.model.Status;
import spring.boot.rest.api.repository.EventRepo;
import spring.boot.rest.api.repository.FileRepo;
import spring.boot.rest.api.repository.UserRepo;
import spring.boot.rest.api.aws.S3Properties;
import spring.boot.rest.api.aws.S3Service;
import spring.boot.rest.api.service.BaseService;
import spring.boot.rest.api.service.FileService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static org.springframework.data.domain.Sort.Order.by;
import static spring.boot.rest.api.util.constant.Constants.*;

@Slf4j
@Service
@Transactional
public class FileServiceImpl extends BaseService implements FileService {
    private final S3Service s3Service;
    private final S3Properties s3Properties;

    protected FileServiceImpl(EventRepo eventRepo, UserRepo userRepo, FileRepo fileRepo,
                              EventMapper eventMapper, UserMapper userMapper, FileMapper fileMapper, S3Service s3Service, S3Properties s3Properties) {
        super(eventRepo, userRepo, fileRepo, eventMapper, userMapper, fileMapper);
        this.s3Service = s3Service;
        this.s3Properties = s3Properties;
    }

    @Override
    public FileDTO save(FileDTO fileDTO) {
        try {
            log.info("IN save() file to db -> '{}'...", fileDTO);
            File saveFile = getFileMapper().map(fileDTO);
            File savedFile = getFileRepo().save(saveFile);
            FileDTO savedFileDTO = getFileMapper().map(savedFile);
            log.info("IN save() file to db -> '{}' -> saved SUCCESSFULLY", fileDTO);
            return savedFileDTO;
        } catch (DataAccessException e) {
            log.error("IN save() file to db -> '{}' -> FAILED", fileDTO);
            throw new DatabaseOperationException(FAILED_TO_SAVE_FILE, e);
        }
    }

    @Override
    public FileDTO update(FileUpdateDTO fileUpdateDTO) {
        throw new NotImplementException(format(NOT_IMPLEMENT_EXCEPTION, TEXT_UPDATE));
    }

    @Override
    public FileDTO findById(Long id) {
        log.info("IN findById() file -> find by id - '{}'...", id);
        FileDTO fileDTO = getFileMapper().map(checkIfFileExists(id));
        log.info("IN findById() file -> found by id - '{}' -> SUCCESSFULLY", id);
        return fileDTO;
    }

    @Override
    public FileDTO findByS3Secret(String s3Secret) {
        try {
            log.info("IN findByS3Secret() -> find the file by secret key - '{}'", s3Secret);
            FileDTO fileDTO = getFileMapper().map(getFileRepo().findByS3Secret(s3Secret)
                    .orElseThrow(() -> new NotFoundException(format(ERROR_CAN_NOT_FIND_FILE_BY_SECRET_KEY, s3Secret))));
            log.info("IN findByS3Secret() -> found the file by secret key -> SUCCESSFULLY");
            return fileDTO;
        } catch (DataAccessException e) {
            log.error("IN findByS3Secret() -> FAILED");
            throw new DatabaseOperationException(format(FAILED_TO_FIND_FILE_BY_SECRET_KEY, s3Secret), e);
        }
    }

    @Override
    public List<FileDTO> findAll() {
        try {
            log.info("IN findAll() -> processing...");
            List<FileDTO> filesDTOList = getFileRepo().findAll(Sort.by(by("createdAt"))).stream().map(getFileMapper()::map).collect(Collectors.toList());
            log.info("IN findAll() -> found all SUCCESSFULLY");
            return filesDTOList;
        } catch (DataAccessException e) {
            log.error("IN findAll() -> FAILED");
            throw new DatabaseOperationException(FAILED_TO_FIND_ALL_FILES, e);
        }
    }

    @Override
    public void deleteById(Long id) {
        try {
            log.info("IN deleteById() -> deleting the file by id - '{}'...", id);
            File deleteFile = checkIfFileExists(id);
            deleteFile.setStatus(Status.DELETED);
            getFileRepo().save(deleteFile);
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
            List<File> fileList = getFileRepo().findAll();
            fileList.stream()
                    .filter(file -> file.getStatus().equals(Status.ACTIVE))
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
    public FileDTO upload(MultipartFile file) {
        try {
            log.info("IN upload() file -> '{}'...", file.getOriginalFilename());
            String fileName = file.getOriginalFilename();
            String profileFileId = getUUIDRandomKey();
            String s3Secret = S3_SECRET_KEY.formatted(profileFileId, fileName);

            log.debug("IN upload() file -> uploading to AWS...");
            String url = s3Service.putObject(
                    s3Properties.getCustomer(),
                    s3Secret,
                    file.getBytes()
            );
            log.debug("IN upload() file -> upload to AWS -> SUCCESSFULLY");

            FileDTO uploadedFileDTO = FileDTO.builder()
                    .name(fileName)
                    .s3Secret(s3Secret)
                    .s3Bucket(s3Properties.getCustomer())
                    .location(url)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .status(Status.ACTIVE).build();
            log.debug("IN upload() file -> saving file '{}' to db...", uploadedFileDTO);

            FileDTO savedFileDTO = this.save(uploadedFileDTO);
            log.debug("IN upload() file -> saved file '{}' to db -> SUCCESSFULLY", savedFileDTO);
            log.info("IN upload() file -> uploaded file '{}' -> SUCCESSFULLY", file.getOriginalFilename());
            return savedFileDTO;
        } catch (IOException e) {
            throw new FileException(ERROR_READ_FILE, e);
        } catch (S3Exception e) {
            throw new FileException(ERROR_UPLOAD_FILE_TO_AWS_S3, e);
        }
    }

    @Override
    public byte[] download(String s3Secret) {
        try {
            log.info("IN download() file by s3Secret -> '{}'...", s3Secret);
            FileDTO fileDTO = this.findByS3Secret(s3Secret);
            byte[] fileContent = s3Service.getObject(s3Properties.getCustomer(), fileDTO.getS3Secret());
            log.info("IN download() file by s3Secret -> '{}' -> downloaded SUCCESSFULLY", s3Secret);
            return fileContent;
        } catch (S3Exception e) {
            throw new FileException(ERROR_DOWNLOAD_FILE_WITH_AWS_S3, e);
        }
    }

    @Override
    public FileDTO renameFile(String existingS3Secret, String newFileName) {
        try {
            log.info("IN renameFile() -> processing... -> findByS3Secret()...");
            FileDTO existingFileDTO = this.findByS3Secret(existingS3Secret);

            String newS3Secret = S3_SECRET_KEY.formatted(getUUIDRandomKey(), newFileName);

            log.info("IN renameFile() -> copyObject()...");

            String url = s3Service.copyObject(s3Properties.getCustomer(), existingS3Secret, s3Properties.getCustomer(), newS3Secret);

            log.info("IN renameFile() -> deleteObject()...");

            s3Service.deleteObject(s3Properties.getCustomer(), existingS3Secret);

            existingFileDTO.setName(newFileName);
            existingFileDTO.setS3Secret(newS3Secret);
            existingFileDTO.setLocation(url);
            existingFileDTO.setUpdatedAt(LocalDateTime.now());

            log.info("IN renameFile() -> save()...");

            FileDTO savedFileDTO = this.save(existingFileDTO);

            log.info("IN renameFile() -> renamed the file SUCCESSFULLY");

            return savedFileDTO;
        } catch (S3Exception e) {
            throw new FileException(ERROR_UPLOAD_FILE_TO_AWS_S3, e);
        }
    }

    @Override
    public FileDTO updateFileContent(String existingS3Secret, MultipartFile newFile) {
        try {
            log.info("IN updateFileContent() -> processing... -> putObject()...");
            s3Service.putObject(s3Properties.getCustomer(), existingS3Secret, newFile.getBytes());

            log.info("IN updateFileContent() -> findByS3Secret()...");
            FileDTO existingFileDTO = this.findByS3Secret(existingS3Secret);

            existingFileDTO.setUpdatedAt(LocalDateTime.now());

            log.info("IN updateFileContent() -> save()...");
            FileDTO savedFileDTO = this.save(existingFileDTO);

            log.info("IN updateFileContent() -> updated the file SUCCESSFULLY");

            return savedFileDTO;
        } catch (IOException e) {
            throw new FileException(FAILED_TO_UPDATE_FILE, e);
        } catch (S3Exception e) {
            throw new FileException(ERROR_UPLOAD_FILE_TO_AWS_S3, e);
        }
    }
}
