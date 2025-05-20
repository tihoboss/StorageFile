package tiho.boss.cloud.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "files")
@Getter
@Setter
public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String filename;

    @Column(nullable = false)
    private LocalDateTime date;

    @Lob
    @Column(nullable = false, columnDefinition = "bytea")
    private byte[] file_content;

    @Column(nullable = false)
    private Long size;

    @ManyToOne
    @JoinColumn(name = "user_username", referencedColumnName = "username")
    private User user;

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public void setUploadDate(LocalDateTime date) {
        this.date = date;
    }

    public void setUser(User user) {
        this.user = user;
    }
}