package xyz.thuray.geniuslens.server.data.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import xyz.thuray.geniuslens.server.data.po.MessagePO;
import xyz.thuray.geniuslens.server.util.TimeFormatUtil;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageVO {
    private Long id;
    private String message;
    private String sender;
    private String senderAvatar;
    private String time;
    private Integer type;
    private Integer status;

    public static List<MessageVO> fromPO(List<MessagePO> pos) {
        return pos.stream().map(MessageVO::fromPO).collect(Collectors.toList());
    }

    public static MessageVO fromPO(MessagePO po) {
        return MessageVO.builder()
                .id(po.getId())
                .message(po.getMessage())
//                .sender(po.getSender())
//                .senderAvatar(po.getSenderAvatar())
                .time(TimeFormatUtil.format(po.getUpdatedAt()))
                .type(po.getType())
                .status(po.getStatus())
                .build();
    }
}
