package tiho.boss.cloud.dto.response;

public record ErrorResponse(
        String message,
        int status
) {}