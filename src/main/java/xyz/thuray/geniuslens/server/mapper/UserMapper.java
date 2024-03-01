package xyz.thuray.geniuslens.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import xyz.thuray.geniuslens.server.data.po.UserPO;

@Mapper
public interface UserMapper extends BaseMapper<UserPO> {
    int insert(UserPO userPO);
    UserPO selectById(Long id);
}
