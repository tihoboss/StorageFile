package tiho.boss.cloud.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tiho.boss.cloud.model.File;
import tiho.boss.cloud.model.User;
import tiho.boss.cloud.service.FileService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/files")
public class FileController {
    private static final Logger log = LoggerFactory.getLogger(FileController.class);
    private final FileService fileStorageService;

    public FileController(FileService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @GetMapping
    public ResponseEntity<List<File>> listFiles(@AuthenticationPrincipal User user) {
        log.info("Listing files for user: {}", user.getUsername());
        try {
            List<File> files = fileStorageService.listFiles(user);
            log.debug("Found {} files for user: {}", files.size(), user.getUsername());
            return ResponseEntity.ok(files);
        } catch (Exception e) {
            log.error("Error listing files for user: {}", user.getUsername(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping
    public ResponseEntity<File> uploadFile(
            @AuthenticationPrincipal User user,
            @RequestParam("file") MultipartFile file) throws IOException {
        log.info("File upload attempt. User: {}, File: {}", user.getUsername(), file.getOriginalFilename());
        try {
            File storedFile = fileStorageService.storeFile(user, file);
            log.info("File uploaded successfully: {}", storedFile.getFilename());
            return ResponseEntity.ok(storedFile);
        } catch (IOException e) {
            log.error("File upload failed. User: {}, File: {}", user.getUsername(), file.getOriginalFilename(), e);
            throw e;
        }
    }

    @DeleteMapping("/{filename}")
    public ResponseEntity<Void> deleteFile(
            @AuthenticationPrincipal User user,
            @PathVariable String filename) throws IOException {
        log.info("File deletion request. User: {}, File: {}", user.getUsername(), filename);
        try {
            fileStorageService.deleteFile(user, filename);
            log.info("File deleted successfully: {}", filename);
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            log.error("File deletion failed. User: {}, File: {}", user.getUsername(), filename, e);
            throw e;
        }
    }
}