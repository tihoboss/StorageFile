package tiho.boss.cloud.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record FileResponse(
        @JsonProperty("filename")
        String name,

        @JsonProperty("size")
        long size,

        @JsonProperty("date")
        String uploadDate
) {}