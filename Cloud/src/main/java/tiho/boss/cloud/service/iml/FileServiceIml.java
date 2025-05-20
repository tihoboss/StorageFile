package tiho.boss.cloud.service.iml;
import tiho.boss.cloud.model.File;
import tiho.boss.cloud.model.User;
import tiho.boss.cloud.repository.FileRepository;
import tiho.boss.cloud.service.FileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class FileServiceIml implements FileService {
    private final FileRepository fileRepository;
    private final Path fileStorageLocation;
    public FileServiceIml(FileRepository fileRepository,
                                  @Value("${file.upload-dir}") String uploadDir) throws IOException {
        this.fileRepository = fileRepository;
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(this.fileStorageLocation);
    }

    @Override
    public List<File> listFiles(User user) {
        return fileRepository.findByUser(user);
    }
    @Override
    public File storeFile(User user, MultipartFile files) throws IOException {
        String filename = files.getOriginalFilename();
        Path targetLocation = fileStorageLocation.resolve(filename);
        Files.copy(files.getInputStream(), targetLocation);

        File file = new File();
        file.setFilename(filename);
        file.setSize(files.getSize());
        file.setUploadDate(LocalDateTime.now());
        file.setUser(user);

        return fileRepository.save(file);
    }

    @Override
    public void deleteFile(User user, String filename) throws IOException {
        File file = fileRepository.findByUserAndFilename(user, filename)
                .orElseThrow(() -> new IOException("File not found"));

        Path filePath = fileStorageLocation.resolve(filename);
        Files.deleteIfExists(filePath);
        fileRepository.delete(file);
    }

    @Override
    public byte[] loadFile(User user, String filename) throws IOException {
        fileRepository.findByUserAndFilename(user, filename)
                .orElseThrow(() -> new IOException("File not found"));

        Path filePath = fileStorageLocation.resolve(filename);
        return Files.readAllBytes(filePath);
    }
}