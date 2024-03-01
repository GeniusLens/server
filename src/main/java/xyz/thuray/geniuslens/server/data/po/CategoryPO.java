package xyz.thuray.geniuslens.server.data.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("category")
public class CategoryPO extends BasePO {
    private String name;
    private String description;
    // 0: root
    private Long parentId;
    private String cover;
}
