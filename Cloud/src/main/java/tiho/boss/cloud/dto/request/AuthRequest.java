package tiho.boss.cloud.dto.request;

import jakarta.validation.constraints.NotBlank;

public record AuthRequest(
        @NotBlank(message = "Login cannot be blank")
        String login,

        @NotBlank(message = "Password cannot be blank")
        String password
) {}