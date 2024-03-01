package xyz.thuray.geniuslens.server.data.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@TableName("user")
public class UserPO extends BasePO {
    private String uid;
    private String phone;
    private String nickname;
    private String avatar;
    private String quote;
}
