package xyz.thuray.geniuslens.server.service;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import xyz.thuray.geniuslens.server.data.dto.PostParamDTO;
import xyz.thuray.geniuslens.server.data.po.FunctionPO;
import xyz.thuray.geniuslens.server.data.po.PostPO;
import xyz.thuray.geniuslens.server.data.vo.PostVO;
import xyz.thuray.geniuslens.server.data.vo.Result;
import xyz.thuray.geniuslens.server.mapper.FunctionMapper;
import xyz.thuray.geniuslens.server.mapper.PostMapper;
import xyz.thuray.geniuslens.server.util.LikeFormatUtil;
import xyz.thuray.geniuslens.server.util.TimeFormatUtil;
import xyz.thuray.geniuslens.server.util.UserContext;

import java.util.List;

@Service
@Slf4j
public class CommunityService {
    @Resource
    private PostMapper postMapper;
    @Resource
    private FunctionMapper functionMapper;

    public Result<?> createPost(PostParamDTO postParamDTO) {
//        FunctionPO f = functionMapper.selectById(postParamDTO.getFunctionId());
//        if (f == null) {
//            return Result.fail("function not found");
//        }
        Long userId = UserContext.getUserId();
        PostPO postPO = PostParamDTO.toPO(postParamDTO, userId);
        try {
            postMapper.insert(postPO);
        } catch (Exception e) {
            log.error("create post error", e);
            return Result.fail("create post error");
        }

        return Result.success(postPO);
    }

    public Result<?> getPostInfo(Long postId) {
        PostPO postPO = postMapper.selectById(postId);
        if (postPO == null) {
            return Result.fail("post not found");
        }
        FunctionPO functionPO = functionMapper.selectById(postPO.getFunctionId());
        PostVO postVO = PostVO.fromPO(postPO, functionPO);
        return Result.success(postVO);
    }

    public Result<?> getPostList() {
        List<PostVO> vos = postMapper.selectAll(0, 10);
        vos.forEach(vo -> {
           vo.setTime(TimeFormatUtil.format(vo.getUpdatedAt()));
           vo.setLikeCount(LikeFormatUtil.format(vo.getLikeCount()));
           vo.setImageList(List.of(vo.getImages().split(",")));
           // 在0~5之间随机
           vo.setCardHeight((int) (Math.random() * 6));
        });

        return Result.success(vos);
    }

    public Result<?> deletePost(Long postId) {
        PostPO po = postMapper.selectById(postId);
        po.setDeleted(true);
        try {
            postMapper.updateById(po);
        } catch (Exception e) {
            log.error("delete post error", e);
            return Result.fail("delete post error");
        }
        return Result.success();
    }

    public Result<?> likePost(Long postId) {
        PostPO po = postMapper.selectById(postId);
        Long userId = UserContext.getUserId();
        po.setLikeCount(po.getLikeCount() + 1);
        try {
            postMapper.updateById(po);
        } catch (Exception e) {
            log.error("like post error", e);
            return Result.fail("like post error");
        }
        return Result.success();
    }
}
