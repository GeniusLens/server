package xyz.thuray.geniuslens.server.data.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("post")
public class PostPO extends BasePO {
    private String title;
    private String content;
    private String images;
    private Long userId;
    private Long functionId;
    private Integer likeCount;
}
