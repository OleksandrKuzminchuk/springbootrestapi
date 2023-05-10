package spring.boot.rest.api.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import spring.boot.rest.api.dto.FileDTO;
import spring.boot.rest.api.dto.request.FileDownloadRequest;
import spring.boot.rest.api.dto.request.FileRenameRequest;
import spring.boot.rest.api.dto.request.FileUpdateContentRequest;
import spring.boot.rest.api.service.FileService;

import java.util.List;

import static spring.boot.rest.api.util.constant.Constants.URL_API_V1_FILES;

@Slf4j
@RestController
@RequestMapping(URL_API_V1_FILES)
@PreAuthorize("hasAuthority('read_write_delete:files')")
public class FileRestControllerV1 {
    private final FileService fileService;

    @Autowired
    public FileRestControllerV1(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/upload")
    @PreAuthorize("hasAuthority('upload:file')")
    public ResponseEntity<FileDTO> uploadFile(@RequestParam("file") MultipartFile file) {
        log.info("IN [POST] uploadFile() -> uploading file...");
        FileDTO uploadedFile = fileService.upload(file);
        log.info("IN [POST] uploadFile() -> file uploaded -> SUCCESSFULLY");
        return new ResponseEntity<>(uploadedFile, HttpStatus.CREATED);
    }

    @PutMapping("/rename")
    public ResponseEntity<FileDTO> renameFile(@RequestBody FileRenameRequest request) {
        log.info("IN renameFile() -> renaming file...");
        FileDTO updatedFileDTO = fileService.renameFile(request.getExistingS3Secret(), request.getNewFileName());
        log.info("IN renameFile() -> file renamed SUCCESSFULLY");
        return new ResponseEntity<>(updatedFileDTO, HttpStatus.OK);
    }

    @PutMapping("/update-content")
    public ResponseEntity<FileDTO> updateFileContent(@ModelAttribute FileUpdateContentRequest request) {
        log.info("IN updateFileContent() -> updating file content...");
        FileDTO updatedFileDTO = fileService.updateFileContent(request.getExistingS3Secret(), request.getFile());
        log.info("IN updateFileContent() -> file content updated SUCCESSFULLY");
        return new ResponseEntity<>(updatedFileDTO, HttpStatus.OK);
    }

    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadFile(@RequestBody FileDownloadRequest request) {
        log.info("IN downloadFile() -> downloading file...");
        String fileName = request.getS3Secret()
                .substring(request.getS3Secret().lastIndexOf("/") + 1);
        byte[] fileContent = fileService.download(request.getS3Secret());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        String filename = "attachment;filename=" + fileName;
        headers.setContentDispositionFormData(filename, filename);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        log.info("IN downloadFile() -> file downloaded SUCCESSFULLY");
        return new ResponseEntity<>(fileContent, headers, HttpStatus.OK);
    }

    @GetMapping()
    public ResponseEntity<List<FileDTO>> findAll() {
        log.info("IN findAll() -> finding all files...");
        List<FileDTO> files = fileService.findAll();
        log.info("IN findAll() -> found all files SUCCESSFULLY");
        return new ResponseEntity<>(files, HttpStatus.OK);
    }


    @GetMapping("/{id}")
    public ResponseEntity<FileDTO> findById(@PathVariable Long id) {
        log.info("IN [GET] findById() file -> find by id - '{}'...", id);
        FileDTO fileDTO = fileService.findById(id);
        log.info("IN [GET] findById() file -> found by id - '{}' -> SUCCESSFULLY", id);
        return new ResponseEntity<>(fileDTO, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable Long id) {
        log.info("IN [DELETE] deleteById() -> deleting the file by id - '{}'...", id);
        fileService.deleteById(id);
        log.info("IN [DELETE] deleteById() -> deleting the file by id - '{}' -> SUCCESSFULLY", id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteAll() {
        log.info("IN [DELETE] deleteAll() -> delete all files...");
        fileService.deleteAll();
        log.info("IN [DELETE] deleteAll() -> delete all files -> SUCCESSFULLY");
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
