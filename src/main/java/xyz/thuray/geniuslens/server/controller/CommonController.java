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

    @RequestMapping(value = "/message/read", method = RequestMethod.POST)
    public Result<?> readMessage(@RequestParam Long messageId) {
        return commonService.readMessage(messageId);
    }

    @RequestMapping(value = "/cloth", method = RequestMethod.GET)
    public Result<?> getClothList() {
        return commonService.getClothList();
    }

    @RequestMapping(value = "/model", method = RequestMethod.GET)
    public Result<?> getModelList() {
        return commonService.getModelList();
    }
}
