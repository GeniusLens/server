package xyz.thuray.geniuslens.server.data.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import xyz.thuray.geniuslens.server.data.enums.InferenceStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@TableName("task")
public class TaskPO extends BasePO{
    private String taskId;
    private Integer status;
    private Long functionId;
    private Long userId;
    private Long messageId;
    private String result;
}
