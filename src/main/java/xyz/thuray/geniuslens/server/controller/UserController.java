package xyz.thuray.geniuslens.server.controller;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import xyz.thuray.geniuslens.server.data.dto.LoginParamDTO;
import xyz.thuray.geniuslens.server.data.dto.RegisterParamDTO;
import xyz.thuray.geniuslens.server.data.vo.Result;
import xyz.thuray.geniuslens.server.service.UserService;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Resource
    private UserService userService;

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public Result<?> register(@RequestBody @Valid RegisterParamDTO param) {
        log.info("register param: {}", param);
        return userService.register(param);
    }
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public Result<?> login(@RequestBody @Valid LoginParamDTO param) {
        return userService.login(param);
    }
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public Result<?> info() {
        return userService.getUserInfo();
    }
}
