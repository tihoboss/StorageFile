package tiho.boss.cloud.control;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import tiho.boss.cloud.controller.FileController;
import tiho.boss.cloud.model.File;
import tiho.boss.cloud.model.User;
import tiho.boss.cloud.service.FileService;
import tiho.boss.cloud.service.UserService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class FileControllerTest {

    @Mock
    private FileService fileService;

    @Mock
    private UserService userService;

    @InjectMocks
    private FileController fileController;

    private User testUser;
    private File testFile;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testUser = new User();
        testUser.setLogin("testuser");

        testFile = new File();
        testFile.setFilename("test.txt");
        testFile.setSize(100L);
        testFile.setUploadDate(LocalDateTime.now());
    }

    @Test
    void listFiles_Success() {
        when(userService.findByToken(anyString())).thenReturn(testUser);
        when(fileService.listFiles(any(User.class), anyInt()))
                .thenReturn(Collections.singletonList(testFile));

        ResponseEntity<?> response = fileController.listFiles("valid-token", 10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof List);
        verify(fileService, times(1)).listFiles(testUser, 10);
    }

    @Test
    void listFiles_InternalError() {
        when(userService.findByToken(anyString())).thenReturn(testUser);
        when(fileService.listFiles(any(User.class), anyInt()))
                .thenThrow(new RuntimeException("DB error"));

        ResponseEntity<?> response = fileController.listFiles("valid-token", 10);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        assertEquals("Internal error", ((Map<?, ?>) response.getBody()).get("message"));
    }

    @Test
    void uploadFile_Success() throws IOException {
        MockMultipartFile multipartFile = new MockMultipartFile(
                "file", "test.txt", "text/plain", "content".getBytes());

        when(userService.findByToken(anyString())).thenReturn(testUser);
        when(fileService.storeFile(any(User.class), any(MultipartFile.class), anyString()))
                .thenReturn(testFile);

        ResponseEntity<?> response = fileController.uploadFile(
                "valid-token", multipartFile, "custom-name.txt");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof File);
        verify(fileService, times(1))
                .storeFile(testUser, multipartFile, "custom-name.txt");
    }

    @Test
    void downloadFile_Success() throws IOException {
        when(userService.findByToken(anyString())).thenReturn(testUser);
        when(fileService.loadFile(any(User.class), anyString()))
                .thenReturn("content".getBytes());

        ResponseEntity<?> response = fileController.downloadFile(
                "valid-token", "test.txt");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("content", new String((byte[]) response.getBody()));
    }

    @Test
    void deleteFile_Success() throws IOException {
        when(userService.findByToken(anyString())).thenReturn(testUser);
        doNothing().when(fileService).deleteFile(any(User.class), anyString());

        ResponseEntity<?> response = fileController.deleteFile(
                "valid-token", "test.txt");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(fileService, times(1)).deleteFile(testUser, "test.txt");
    }

    @Test
    void renameFile_Success() throws IOException {
        when(userService.findByToken(anyString())).thenReturn(testUser);
        doNothing().when(fileService).renameFile(any(User.class), anyString(), anyString());

        ResponseEntity<?> response = fileController.renameFile(
                "valid-token", "old.txt", Map.of("name", "new.txt"));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(fileService, times(1)).renameFile(testUser, "old.txt", "new.txt");
    }

    @Test
    void renameFile_MissingName() {
        ResponseEntity<?> response = fileController.renameFile(
                "valid-token", "old.txt", Map.of("wrong", "value"));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("New filename is required",
                ((Map<?, ?>) response.getBody()).get("message"));
    }
}