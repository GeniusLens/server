package xyz.thuray.geniuslens.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import xyz.thuray.geniuslens.server.data.po.MessagePO;

import java.util.List;

@Mapper
public interface MessageMapper extends BaseMapper<MessagePO> {
    List<MessagePO> selectMessageList(Integer type, Long userId);
}
