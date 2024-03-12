package xyz.thuray.geniuslens.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import xyz.thuray.geniuslens.server.data.po.ModelPO;

import java.util.List;

@Mapper
public interface ModelMapper extends BaseMapper<ModelPO> {
    List<ModelPO> selectList();
}
