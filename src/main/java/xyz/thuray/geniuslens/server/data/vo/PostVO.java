package xyz.thuray.geniuslens.server.data.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import xyz.thuray.geniuslens.server.data.po.FunctionPO;
import xyz.thuray.geniuslens.server.data.po.PostPO;
import xyz.thuray.geniuslens.server.util.TimeFormatUtil;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostVO {
    private Long id;
    private String title;
    private String content;
    private List<String> images;
    private FunctionPO function;
    private Long functionId;
    private String time;
    private LocalDateTime updatedAt;
    private String username;
    private String userAvatar;
    private String likeCount;

    public static PostVO fromPO(PostPO po, FunctionPO functionPO) {
        return PostVO.builder()
                .id(po.getId())
                .title(po.getTitle())
                .content(po.getContent())
                .images(List.of(po.getImages().split(",")))
                .function(functionPO)
                .functionId(po.getFunctionId())
                .time(TimeFormatUtil.format(po.getUpdatedAt()))
                .likeCount(po.getLikeCount().toString())
                .build();
    }
}
