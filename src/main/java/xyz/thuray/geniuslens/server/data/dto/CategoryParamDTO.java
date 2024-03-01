package xyz.thuray.geniuslens.server.data.dto;

import lombok.Data;
import xyz.thuray.geniuslens.server.data.po.CategoryPO;

@Data
public class CategoryParamDTO {
    private String parent;
    private String name;
    private String description;
    private String cover;

    public static CategoryPO toPO(CategoryParamDTO dto, Long parentId) {
        return CategoryPO.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .parentId(parentId)
                .cover(dto.getCover())
                .build();
    }
}
