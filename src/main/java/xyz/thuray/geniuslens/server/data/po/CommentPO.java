package xyz.thuray.geniuslens.server.data.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("comment")
public class CommentPO extends BasePO {
    private Long postId;
    private Long userId;
    private String content;
    private Integer likeCount;
}
