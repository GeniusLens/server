package xyz.thuray.geniuslens.server.mapper;

import com.baomidou.mybatisplus.core.conditions.interfaces.Func;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import xyz.thuray.geniuslens.server.data.po.FunctionPO;

import java.util.List;

@Mapper
public interface FunctionMapper extends BaseMapper<FunctionPO> {
    List<FunctionPO> selectAll();
    List<FunctionPO> selectByCategoryId(Long categoryId);
    List<FunctionPO> selectByCategoryName(String category);
    List<FunctionPO> selectRecommend();
    FunctionPO selectByName(String name);
}
