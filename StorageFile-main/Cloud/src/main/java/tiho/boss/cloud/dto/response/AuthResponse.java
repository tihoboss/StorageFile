package tiho.boss.cloud.dto.response;
import com.fasterxml.jackson.annotation.JsonProperty;

public record AuthResponse(
        @JsonProperty("auth-token")
        String token,

        String login,

        @JsonProperty("status")
        int status
) {
    public AuthResponse(String token, String login) {
        this(token, login, 200);
    }
}