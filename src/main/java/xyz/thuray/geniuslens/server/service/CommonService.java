package xyz.thuray.geniuslens.server.service;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import xyz.thuray.geniuslens.server.data.po.MessagePO;
import xyz.thuray.geniuslens.server.data.vo.MessageVO;
import xyz.thuray.geniuslens.server.data.vo.Result;
import xyz.thuray.geniuslens.server.mapper.MessageMapper;
import xyz.thuray.geniuslens.server.util.UserContext;

import java.util.List;

@Service
@Slf4j
public class CommonService {
    @Resource
    private MessageMapper messageMapper;

    public Result<?> getMessageList(Integer type) {
        Long userId = UserContext.getUserId();
        List<MessagePO> pos = messageMapper.selectMessageList(type, userId);
        List<MessageVO> messageVOList = MessageVO.fromPO(pos);
        log.info("getMessageList: {}", messageVOList);

        return Result.success(messageVOList);
    }
}
