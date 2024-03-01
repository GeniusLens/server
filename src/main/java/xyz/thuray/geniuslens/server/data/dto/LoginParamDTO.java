package xyz.thuray.geniuslens.server.data.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginParamDTO {
    @NotBlank(message = "密码不能为空")
    private String password;
    private String phone;
    private String nickname;
}
