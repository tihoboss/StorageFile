package tiho.boss.cloud.service;

import tiho.boss.cloud.model.File;
import tiho.boss.cloud.model.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FileService {
    List<File> listFiles(User user);
    List<File> listFiles(User user, Integer limit);

    File storeFile(User user, MultipartFile file, String filenameOverride) throws IOException;
    void deleteFile(User user, String filename) throws IOException;
    byte[] loadFile(User user, String filename) throws IOException;
    void renameFile(User user, String oldFilename, String newFilename) throws IOException;
}