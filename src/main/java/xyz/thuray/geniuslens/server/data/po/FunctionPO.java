package xyz.thuray.geniuslens.server.data.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("function")
public class FunctionPO extends BasePO {
    private Long categoryId;
    private String name;
    private String description;
    private String url;
    private String type;
}
