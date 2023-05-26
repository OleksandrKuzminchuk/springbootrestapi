package spring.boot.rest.api.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import spring.boot.rest.api.dto.response.FileResponseDto;
import spring.boot.rest.api.dto.request.FileDownloadRequestDto;
import spring.boot.rest.api.dto.request.FileRenameRequestDto;
import spring.boot.rest.api.mapper.FileMapper;
import spring.boot.rest.api.service.FileService;

import java.util.List;
import java.util.stream.Collectors;

import static spring.boot.rest.api.util.Constants.*;

@Slf4j
@RestController
@RequestMapping(URL_API_V1_FILES)
@PreAuthorize("hasAuthority('read_write_delete:files')")
@RequiredArgsConstructor
public class FileRestControllerV1 {

    private final FileService fileService;
    private final FileMapper fileMapper;

    @PostMapping(URL_UPLOAD)
    public ResponseEntity<FileResponseDto> uploadFile(@RequestParam(FILE) MultipartFile file) {
        log.info("IN [POST] uploadFile() -> uploading file...");
        final var uploadedFile = fileMapper.map(fileService.upload(file));
        log.info("IN [POST] uploadFile() -> file uploaded -> SUCCESSFULLY");
        return new ResponseEntity<>(uploadedFile, HttpStatus.CREATED);
    }

    @PutMapping(URL_ID)
    public ResponseEntity<FileResponseDto> updateName(@PathVariable(ID) Long id,
                                                      @RequestBody FileRenameRequestDto request) {
        log.info("IN updateName() -> updating file...");
        final var updatedFileResponseDto = fileMapper.map(fileService.updateName(id, request.getNewFileName()));
        log.info("IN updateName() -> file updated SUCCESSFULLY");
        return new ResponseEntity<>(updatedFileResponseDto, HttpStatus.OK);
    }

    @PutMapping(URL_ID_UPDATE_CONTENT)
    public ResponseEntity<FileResponseDto> updateFileContent(@PathVariable(ID) Long id,
                                                             @ModelAttribute MultipartFile file) {
        log.info("IN updateFileContent() -> updating file content...");
        final var updatedFileResponseDto = fileMapper.map(fileService.updateFileContent(id, file));
        log.info("IN updateFileContent() -> file content updated SUCCESSFULLY");
        return new ResponseEntity<>(updatedFileResponseDto, HttpStatus.OK);
    }

    @GetMapping(URL_DOWNLOAD)
    @PreAuthorize("hasAuthority('download:file')")
    public ResponseEntity<byte[]> downloadFile(@RequestBody FileDownloadRequestDto request) {
        log.info("IN downloadFile() -> downloading file...");
        final var fileName = request.getLocation()
                .substring(request.getLocation().lastIndexOf(SLASH) + ONE);
        final var fileContent = fileService.download(request.getLocation());
        final var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        final var filename = ATTACHMENT_FILENAME + fileName;
        headers.setContentDispositionFormData(filename, filename);
        headers.setCacheControl(MUST_REVALIDATE_POST_CHECK_0);
        log.info("IN downloadFile() -> file downloaded SUCCESSFULLY");
        return new ResponseEntity<>(fileContent, headers, HttpStatus.OK);
    }

    @GetMapping()
    public ResponseEntity<List<FileResponseDto>> findAll() {
        log.info("IN findAll() -> finding all files...");
        final var files = fileService.findAll().stream().map(fileMapper::map).toList();
        log.info("IN findAll() -> found all files SUCCESSFULLY");
        return new ResponseEntity<>(files, HttpStatus.OK);
    }


    @GetMapping(URL_ID)
    public ResponseEntity<FileResponseDto> findById(@PathVariable(ID) Long id) {
        log.info("IN [GET] findById() file -> find by id - '{}'...", id);
        final var fileResponseDto = fileMapper.map(fileService.findById(id));
        log.info("IN [GET] findById() file -> found by id - '{}' -> SUCCESSFULLY", id);
        return new ResponseEntity<>(fileResponseDto, HttpStatus.OK);
    }

    @DeleteMapping(URL_ID)
    public ResponseEntity<Void> deleteById(@PathVariable(ID) Long id) {
        log.info("IN [DELETE] deleteById() -> deleting the file by id - '{}'...", id);
        fileService.deleteById(id);
        log.info("IN [DELETE] deleteById() -> deleting the file by id - '{}' -> SUCCESSFULLY", id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAll() {
        log.info("IN [DELETE] deleteAll() -> delete all files...");
        fileService.deleteAll();
        log.info("IN [DELETE] deleteAll() -> delete all files -> SUCCESSFULLY");
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
