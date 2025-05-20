package tiho.boss.cloud.dto.request;
import jakarta.validation.constraints.NotBlank;

public record EditFileNameRequest(
        @NotBlank(message = "New filename cannot be blank")
        String filename
) {}