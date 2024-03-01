package xyz.thuray.geniuslens.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import xyz.thuray.geniuslens.server.data.po.CategoryPO;

import java.util.List;

@Mapper
public interface CategoryMapper extends BaseMapper<CategoryPO> {
    CategoryPO selectByName(String name);
    List<CategoryPO> selectAllParent();
    List<CategoryPO> selectByParentName(String name);
    List<CategoryPO> selectAll();
}
