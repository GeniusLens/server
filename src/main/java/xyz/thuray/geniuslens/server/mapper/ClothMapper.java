package xyz.thuray.geniuslens.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import xyz.thuray.geniuslens.server.data.po.ClothPO;

import java.util.List;

@Mapper
public interface ClothMapper extends BaseMapper<ClothPO> {
    List<ClothPO> selectList();
}
