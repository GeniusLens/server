package xyz.thuray.geniuslens.server.controller;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import xyz.thuray.geniuslens.server.data.dto.PostParamDTO;
import xyz.thuray.geniuslens.server.data.vo.Result;
import xyz.thuray.geniuslens.server.service.CommunityService;

@RestController
@RequestMapping("/community")
public class CommunityController {
    @Resource
    private CommunityService communityService;

    @RequestMapping(value = "/post", method = RequestMethod.POST)
    public Result<?> createPost(@RequestBody @Valid PostParamDTO postParamDTO) {
        return communityService.createPost(postParamDTO);
    }

    @RequestMapping(value = "/post/{id}", method = RequestMethod.GET)
    public Result<?> getPostInfo(@PathVariable Long id) {
        return communityService.getPostInfo(id);
    }

    @RequestMapping(value = "/post/list", method = RequestMethod.GET)
    public Result<?> getPostList() {
        return communityService.getPostList();
    }

    @RequestMapping(value = "/post/{id}", method = RequestMethod.DELETE)
    public Result<?> deletePost(@PathVariable Long id) {
        return communityService.deletePost(id);
    }
}
