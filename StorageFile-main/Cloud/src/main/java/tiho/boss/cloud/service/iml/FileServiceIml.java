package tiho.boss.cloud.service.iml;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tiho.boss.cloud.model.File;
import tiho.boss.cloud.model.User;
import tiho.boss.cloud.repository.FileRepository;
import tiho.boss.cloud.service.FileService;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FileServiceIml implements FileService {

    private final FileRepository fileRepository;

    @Override
    public List<File> listFiles(User user) {
        return fileRepository.findByUser(user);
    }

    @Override
    public List<File> listFiles(User user, Integer limit) {
        if (limit != null && limit <= 0) {
            throw new IllegalArgumentException("Limit must be positive");
        }
        List<File> files = fileRepository.findByUser(user);
        files.sort(Comparator.comparing(File::getUploadDate).reversed());

        if (limit != null && limit < files.size()) {
            return files.subList(0, limit);
        }

        return files;
    }

    @Override
    public File storeFile(User user, MultipartFile multipartFile, String filenameOverride) throws IOException {
        String filename = (filenameOverride != null && !filenameOverride.isBlank())
                ? filenameOverride
                : multipartFile.getOriginalFilename();

        if (filename == null || filename.isBlank()) {
            throw new IOException("Filename is required");
        }

        byte[] content = multipartFile.getBytes();
        long size = multipartFile.getSize();
        String hash = calculateSHA256(content);

        File file = new File();
        file.setFilename(filename);
        file.setSize(size);
        file.setUploadDate(LocalDateTime.now());
        file.setFileContent(content);
        file.setUser(user);
        file.setHash(hash);

        return fileRepository.save(file);
    }

    @Override
    public void deleteFile(User user, String filename) throws IOException {
        File file = fileRepository.findByUserAndFilename(user, filename)
                .orElseThrow(() -> new IOException("File not found: " + filename));

        fileRepository.delete(file);
    }

    @Override
    public byte[] loadFile(User user, String filename) throws IOException {
        File file = fileRepository.findByUserAndFilename(user, filename)
                .orElseThrow(() -> new IOException("File not found: " + filename));
        return file.getFileContent();
    }

    @Override
    public void renameFile(User user, String oldFilename, String newFilename) throws IOException {
        File file = fileRepository.findByUserAndFilename(user, oldFilename)
                .orElseThrow(() -> new IOException("File not found: " + oldFilename));

        if (fileRepository.findByUserAndFilename(user, newFilename).isPresent()) {
            throw new IOException("File with name " + newFilename + " already exists");
        }
        if (newFilename == null || newFilename.isBlank()) {
            throw new IOException("New filename must not be empty");
        }

        file.setFilename(newFilename);
        fileRepository.save(file);
    }

    private String calculateSHA256(byte[] data) throws IOException {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(data);
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IOException("SHA-256 algorithm not available", e);
        }
    }
}