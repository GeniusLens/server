package xyz.thuray.geniuslens.server.service;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import xyz.thuray.geniuslens.server.data.dto.LoginParamDTO;
import xyz.thuray.geniuslens.server.data.dto.RegisterParamDTO;
import xyz.thuray.geniuslens.server.data.po.AuthPO;
import xyz.thuray.geniuslens.server.data.po.UserPO;
import xyz.thuray.geniuslens.server.data.vo.LoginVO;
import xyz.thuray.geniuslens.server.data.vo.Result;
import xyz.thuray.geniuslens.server.mapper.AuthMapper;
import xyz.thuray.geniuslens.server.mapper.UserMapper;
import xyz.thuray.geniuslens.server.util.JwtUtil;
import xyz.thuray.geniuslens.server.util.UserContext;

@Service
@Slf4j
public class UserService {
    @Resource
    private UserMapper userMapper;
    @Resource
    private AuthMapper authMapper;

    public Result<?> register(RegisterParamDTO param) {
        UserPO userPO = RegisterParamDTO.toUserPO(param);
        userPO.setUid(String.valueOf(1L));
        log.debug("userPO: {}", userPO);
        try{
            userMapper.insert(userPO);
        } catch (DuplicateKeyException e) {
            return Result.fail("该手机号已被注册");
        }
        log.debug("Inserted userPO: {}", userPO);

        // TODO: 对密码进行加密，使用Hash算法
        String encryptedPwd = param.getPassword();
        AuthPO authPO = AuthPO.builder()
                .userId(userPO.getId())
                .password(encryptedPwd)
                .build();
        log.debug("authPO: {}", authPO);
        authMapper.insert(authPO);

        String token = JwtUtil.generateToken(authPO);

        LoginVO loginVO = new LoginVO(token, userPO);

        return Result.success(loginVO);
    }

    public Result<?> login(LoginParamDTO param) {
        AuthPO authPO = authMapper.selectByUserPhone(param.getPhone());
        log.debug("authPO: {}", authPO);
        if (authPO == null) {
            return Result.fail("用户不存在");
        }
        if (!authPO.getPassword().equals(param.getPassword())) {
            return Result.fail("密码错误");
        }

        String token = JwtUtil.generateToken(authPO);
        UserPO userPO = userMapper.selectById(authPO.getUserId());

        LoginVO loginVO = new LoginVO(token, userPO);

        return Result.success(loginVO);
    }

    public Result<?> getUserInfo() {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            return Result.fail("用户未登录");
        }
        UserPO userPO = userMapper.selectById(userId);
        if (userPO == null) {
            return Result.fail("用户不存在");
        }
        return Result.success(userPO);
    }
}
