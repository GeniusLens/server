package xyz.thuray.geniuslens.server.data.dto;

import lombok.Data;
import xyz.thuray.geniuslens.server.data.po.LoraPO;

import java.util.List;

@Data
public class LoraParamDTO {
    private String name;
    private String description;
    private List<String> images;
    private String avatar;

    public static LoraPO toPO(LoraParamDTO dto, Long userId ) {
        return LoraPO.builder()
                .userId(userId)
                .name(dto.getName())
                .description(dto.getDescription())
                .images(dto.images != null ? String.join(",", dto.images) : null)
                .avatar(dto.getAvatar() != null ? dto.getAvatar() : dto.images.get(0))
                .defaultFlag(false)
                .status(0)
                .build();
    }
}
