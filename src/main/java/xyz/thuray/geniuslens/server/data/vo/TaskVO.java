package xyz.thuray.geniuslens.server.data.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskVO {
    private Long id;
    private String taskId;
    @JsonProperty("status")
    private String statusStr;
    @JsonProperty("statusCode")
    private Integer status;
    private String result;
    @JsonProperty("function")
    private String functionName;
    @JsonProperty("time")
    private String createdAtStr;
    @JsonIgnore
    private LocalDateTime createdAt;
}
