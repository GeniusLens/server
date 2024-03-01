package xyz.thuray.geniuslens.server.data.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("auth")
public class AuthPO extends BasePO {
    private Long userId;
    private String password;
    private String salt;
    private LocalDateTime lastLoginTime;
    private String lastLoginIP;
}
