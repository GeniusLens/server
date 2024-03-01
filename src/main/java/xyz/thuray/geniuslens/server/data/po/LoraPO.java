package xyz.thuray.geniuslens.server.data.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@TableName("lora")
public class LoraPO extends BasePO {
    private Long userId;
    private String name;
    private String description;
    private String images;
    private String avatar;
    @TableField("is_default")
    private Boolean defaultFlag;
    // 0: 创建未训练
    // 1: 正在训练
    // 2: 训练完成
    // -1: 训练失败
    private int status;
}
