package xyz.thuray.geniuslens.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import xyz.thuray.geniuslens.server.data.po.CommentPO;
import xyz.thuray.geniuslens.server.data.vo.CommentVO;

import java.util.List;

@Mapper
public interface CommentMapper extends BaseMapper<CommentPO> {
    List<CommentVO> selectListByPostId(Long postId);
}
