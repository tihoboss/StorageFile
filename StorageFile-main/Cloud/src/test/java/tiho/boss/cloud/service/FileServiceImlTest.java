package tiho.boss.cloud.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;
import tiho.boss.cloud.model.File;
import tiho.boss.cloud.model.User;
import tiho.boss.cloud.repository.FileRepository;
import tiho.boss.cloud.service.iml.FileServiceIml;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FileServiceImlTest {

    @Mock
    private FileRepository fileRepository;

    @InjectMocks
    private FileServiceIml fileService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void storeFile_WithCustomFilename() throws IOException {
        User user = new User();
        MultipartFile mockFile = mock(MultipartFile.class);

        when(mockFile.getOriginalFilename()).thenReturn("original.txt");
        when(mockFile.getBytes()).thenReturn("Hello World".getBytes());
        when(mockFile.getSize()).thenReturn(11L);

        ArgumentCaptor<File> captor = ArgumentCaptor.forClass(File.class);
        when(fileRepository.save(any(File.class))).thenAnswer(invocation -> invocation.getArgument(0));

        File savedFile = fileService.storeFile(user, mockFile, "custom.txt");

        assertEquals("custom.txt", savedFile.getFilename());
        assertEquals(11L, savedFile.getSize());
        assertNotNull(savedFile.getUploadDate());
        assertEquals(user, savedFile.getUser());
        verify(fileRepository).save(captor.capture());
        assertEquals("custom.txt", captor.getValue().getFilename());
    }

    @Test
    void storeFile_WithoutCustomFilename() throws IOException {
        User user = new User();
        MultipartFile mockFile = mock(MultipartFile.class);

        when(mockFile.getOriginalFilename()).thenReturn("default.txt");
        when(mockFile.getBytes()).thenReturn("Hello World".getBytes());
        when(mockFile.getSize()).thenReturn(11L);
        when(fileRepository.save(any(File.class))).thenAnswer(invocation -> invocation.getArgument(0));

        File savedFile = fileService.storeFile(user, mockFile, null);

        assertEquals("default.txt", savedFile.getFilename());
    }

    @Test
    void listFiles_ShouldReturnFilesSortedAndLimited() {
        User user = new User();

        File f1 = new File();
        f1.setUploadDate(LocalDateTime.now().minusDays(2));

        File f2 = new File();
        f2.setUploadDate(LocalDateTime.now());

        File f3 = new File();
        f3.setUploadDate(LocalDateTime.now().minusDays(1));

        List<File> files = List.of(f1, f2, f3);
        when(fileRepository.findByUser(user)).thenReturn(new ArrayList<>(files));

        List<File> result = fileService.listFiles(user, 2);

        assertEquals(2, result.size());
        assertEquals(f2, result.get(0)); // Most recent
        assertEquals(f3, result.get(1));
    }

    @Test
    void renameFile_ShouldRenameSuccessfully() throws IOException {
        User user = new User();
        String oldName = "old.txt";
        String newName = "new.txt";

        File file = new File();
        file.setFilename(oldName);
        file.setUser(user);

        when(fileRepository.findByUserAndFilename(user, oldName)).thenReturn(Optional.of(file));
        when(fileRepository.findByUserAndFilename(user, newName)).thenReturn(Optional.empty());

        fileService.renameFile(user, oldName, newName);

        assertEquals(newName, file.getFilename());
        verify(fileRepository).save(file);
    }

    @Test
    void loadFile_ShouldReturnContent() throws IOException {
        User user = new User();
        String filename = "file.txt";

        File file = new File();
        file.setFileContent("test content".getBytes());

        when(fileRepository.findByUserAndFilename(user, filename)).thenReturn(Optional.of(file));

        byte[] content = fileService.loadFile(user, filename);

        assertArrayEquals("test content".getBytes(), content);
    }

    @Test
    void deleteFile_ShouldDelete() throws IOException {
        User user = new User();
        String filename = "file.txt";
        File file = new File();

        when(fileRepository.findByUserAndFilename(user, filename)).thenReturn(Optional.of(file));

        fileService.deleteFile(user, filename);

        verify(fileRepository).delete(file);
    }
}