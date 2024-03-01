package xyz.thuray.geniuslens.server.data.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@TableName("message")
public class MessagePO extends BasePO {
    private String message;
    private Long senderId;
    private Long receiverId;
    private Integer type;
    private Integer status;
}
