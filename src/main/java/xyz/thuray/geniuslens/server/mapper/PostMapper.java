package xyz.thuray.geniuslens.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import xyz.thuray.geniuslens.server.data.po.PostPO;
import xyz.thuray.geniuslens.server.data.vo.PostVO;

import java.util.List;

@Mapper
public interface PostMapper extends BaseMapper<PostPO> {
    List<PostVO> selectAll(Integer offset, Integer limit);
}
