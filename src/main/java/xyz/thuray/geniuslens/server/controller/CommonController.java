package xyz.thuray.geniuslens.server.controller;

import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import xyz.thuray.geniuslens.server.data.vo.Result;
import xyz.thuray.geniuslens.server.service.CommonService;

@RestController
@RequestMapping("/common")
public class CommonController {
    @Resource
    private CommonService commonService;

    @RequestMapping(value = "/message", method = RequestMethod.GET)
    public Result<?> getMessageList(@RequestParam(required = false) Integer type) {
        return commonService.getMessageList(type);
    }
}
