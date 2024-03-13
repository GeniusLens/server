package xyz.thuray.geniuslens.server.data.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentVO {
    private Long id;
    private String userName;
    private String userAvatar;
    private String content;
    private String time;
    @JsonIgnore
    private LocalDateTime createdAt;
    private Integer likeCount;
}
