package xyz.thuray.geniuslens.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import xyz.thuray.geniuslens.server.data.po.LoraPO;

import java.util.List;

@Mapper
public interface LoraMapper extends BaseMapper<LoraPO> {
    List<LoraPO> selectByUserId(Long userId);
    List<LoraPO> selectById(List<Long> ids);
}
