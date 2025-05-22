package tiho.boss.cloud.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tiho.boss.cloud.model.File;
import tiho.boss.cloud.model.User;
import tiho.boss.cloud.service.FileService;
import tiho.boss.cloud.service.UserService;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class FileController {

    private static final Logger log = LoggerFactory.getLogger(FileController.class);
    private final FileService fileService;
    private final UserService userService;

    @GetMapping("/list")
    public ResponseEntity<?> listFiles(
            @RequestHeader("auth-token") String token,
            @RequestParam(required = false) Integer limit) {

        User user = userService.findByToken(token);
        log.info("Listing files for user: {}", user.getLogin());

        try {
            List<File> files = fileService.listFiles(user, limit);
            return ResponseEntity.ok(files);
        } catch (Exception e) {
            log.error("Error listing files", e);
            return ResponseEntity.internalServerError().body(Map.of("message", "Internal error", "id", 500));
        }
    }

    @PostMapping(value = "/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadFile(
            @RequestHeader("auth-token") String token,
            @RequestParam("file") MultipartFile file,
            @RequestParam(name = "filename", required = false) String filename) {

        User user = userService.findByToken(token);
        log.info("Uploading file: {}", file.getOriginalFilename());

        try {
            File saved = fileService.storeFile(user, file, filename);
            return ResponseEntity.ok(saved);
        } catch (IOException e) {
            log.error("Upload failed", e);
            return ResponseEntity.status(400).body(Map.of("message", "Upload error", "id", 400));
        }
    }

    @GetMapping("/file")
    public ResponseEntity<?> downloadFile(
            @RequestHeader("auth-token") String token,
            @RequestParam("filename") String filename) {

        User user = userService.findByToken(token);
        log.info("Downloading file: {}", filename);

        try {
            byte[] fileBytes = fileService.loadFile(user, filename);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDisposition(ContentDisposition.attachment().filename(filename).build());
            return new ResponseEntity<>(fileBytes, headers, HttpStatus.OK);
        } catch (IOException e) {
            log.error("Download failed", e);
            return ResponseEntity.status(500).body(Map.of("message", "Download error", "id", 500));
        }
    }

    @DeleteMapping("/file")
    public ResponseEntity<?> deleteFile(
            @RequestHeader("auth-token") String token,
            @RequestParam("filename") String filename) {

        User user = userService.findByToken(token);
        log.info("Deleting file: {}", filename);

        try {
            fileService.deleteFile(user, filename);
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            log.error("Delete failed", e);
            return ResponseEntity.status(500).body(Map.of("message", "Delete error", "id", 500));
        }
    }

    @PutMapping("/file")
    public ResponseEntity<?> renameFile(
            @RequestHeader("auth-token") String token,
            @RequestParam("filename") String oldName,
            @RequestBody Map<String, String> body) {

        User user = userService.findByToken(token);
        String newName = body.get("name");

        if (newName == null || newName.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "New filename is required", "id", 400));
        }

        log.info("Renaming file: {} -> {}", oldName, newName);

        try {
            fileService.renameFile(user, oldName, newName);
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            log.error("Rename failed", e);
            return ResponseEntity.status(500).body(Map.of("message", "Rename error", "id", 500));
        }
    }
}