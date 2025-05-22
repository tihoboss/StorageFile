package tiho.boss.cloud.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(name = "files")
@Data
@NoArgsConstructor
public class File {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String filename;

    @Column(nullable = false)
    private Long size;

    @Column(nullable = false)
    private String hash;

    @Column(name = "upload_date",nullable = false)
    private LocalDateTime uploadDate;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "file_content", nullable = false, columnDefinition = "bytea")
    @JdbcTypeCode(SqlTypes.BINARY)
    private byte[] fileContent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userID", nullable = false)
    private User user;
}