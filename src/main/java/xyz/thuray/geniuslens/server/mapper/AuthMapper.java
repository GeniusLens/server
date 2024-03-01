package xyz.thuray.geniuslens.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import xyz.thuray.geniuslens.server.data.po.AuthPO;

@Mapper
public interface AuthMapper extends BaseMapper<AuthPO> {
    AuthPO selectByUserPhone(String phone);
}
