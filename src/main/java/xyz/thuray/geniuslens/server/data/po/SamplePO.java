package xyz.thuray.geniuslens.server.data.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.ser.Serializers;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@TableName("sample")
public class SamplePO extends BasePO {
    private Long functionId;
    private String name;
    // 0: unknown, 1: positive, 2: negative
    private Integer type;
    private String url;
}
