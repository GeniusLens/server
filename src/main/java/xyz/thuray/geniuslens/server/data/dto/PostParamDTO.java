package xyz.thuray.geniuslens.server.data.dto;

import lombok.Data;
import xyz.thuray.geniuslens.server.data.po.PostPO;

import java.util.List;

@Data
public class PostParamDTO {
    private String title;
    private String content;
    private List<String> images;
    private Long functionId;

    public static PostPO toPO(PostParamDTO dto, Long userId) {
        return PostPO.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .images(String.join(",", dto.getImages()))
                .userId(userId)
                .functionId(dto.getFunctionId())
                .likeCount(0)
                .build();
    }
}
