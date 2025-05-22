package tiho.boss.cloud.repository;

import tiho.boss.cloud.model.File;
import tiho.boss.cloud.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FileRepository extends JpaRepository<File, Long> {
    List<File> findByUser(User user);
    Optional<File> findByUserAndFilename(User user, String filename);
}