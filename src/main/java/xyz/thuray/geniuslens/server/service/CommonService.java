package xyz.thuray.geniuslens.server.service;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import xyz.thuray.geniuslens.server.data.enums.MessageStatus;
import xyz.thuray.geniuslens.server.data.po.ClothPO;
import xyz.thuray.geniuslens.server.data.po.MessagePO;
import xyz.thuray.geniuslens.server.data.vo.MessageVO;
import xyz.thuray.geniuslens.server.data.vo.Result;
import xyz.thuray.geniuslens.server.mapper.ClothMapper;
import xyz.thuray.geniuslens.server.mapper.MessageMapper;
import xyz.thuray.geniuslens.server.util.UserContext;

import java.util.List;

@Service
@Slf4j
public class CommonService {
    @Resource
    private MessageMapper messageMapper;
    @Resource
    private ClothMapper clothMapper;

    public Result<?> getMessageList(Integer type) {
        Long userId = UserContext.getUserId();
        List<MessagePO> pos = messageMapper.selectMessageList(type, userId);
        List<MessageVO> messageVOList = MessageVO.fromPO(pos);
        log.info("getMessageList: {}", messageVOList);

        return Result.success(messageVOList);
    }

    public Result<?> readMessage(Long messageId) {
        Long userId = UserContext.getUserId();
        MessagePO messagePO = messageMapper.selectById(messageId);
        if (messagePO == null) {
            return Result.fail("消息不存在");
        }
        messagePO.setStatus(MessageStatus.READ.getValue());
        messageMapper.updateById(messagePO);

        return Result.success();
    }

    public Result<?> getClothList() {
        List<ClothPO> pos = clothMapper.selectList();

        return Result.success(pos);
    }
}
