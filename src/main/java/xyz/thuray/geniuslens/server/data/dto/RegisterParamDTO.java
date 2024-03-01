package xyz.thuray.geniuslens.server.data.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import xyz.thuray.geniuslens.server.annotation.StrongPassword;
import xyz.thuray.geniuslens.server.data.po.UserPO;

@Data
public class RegisterParamDTO {
    @NotEmpty(message = "密码不能为空")
    @StrongPassword
    private String password;
    private String phone;
    private String nickname;

    public static UserPO toUserPO(RegisterParamDTO param) {
        return UserPO.builder()
                .phone(param.getPhone())
                .nickname(param.getNickname())
                .build();
    }
}
