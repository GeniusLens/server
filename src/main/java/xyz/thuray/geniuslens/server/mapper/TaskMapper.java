package xyz.thuray.geniuslens.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import xyz.thuray.geniuslens.server.data.po.TaskPO;
import xyz.thuray.geniuslens.server.data.vo.TaskVO;

import java.util.List;

@Mapper
public interface TaskMapper extends BaseMapper<TaskPO> {
    List<TaskVO> selectAllByUserId(Long userId);
    TaskVO selectAllById(Long id);
}
