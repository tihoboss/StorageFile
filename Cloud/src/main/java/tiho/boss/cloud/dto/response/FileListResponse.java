package tiho.boss.cloud.dto.response;
import java.util.List;

public record FileListResponse(
        List<FileResponse> files,
        int count
) {
    public FileListResponse(List<FileResponse> files) {
        this(files, files.size());
    }
}