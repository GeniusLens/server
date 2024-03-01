package xyz.thuray.geniuslens.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import xyz.thuray.geniuslens.server.data.po.SamplePO;

import java.util.List;

@Mapper
public interface SampleMapper extends BaseMapper<SamplePO> {
    int insertBatch(List<SamplePO> list);
    List<SamplePO> selectAllByFunctionId(Long functionId, Integer type);
}
