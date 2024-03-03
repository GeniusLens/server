package xyz.thuray.geniuslens.server.data.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SingleLoraDTO {
    @JsonProperty("userid")
    private String userid;
    @JsonProperty("images_url")
    private String imagesUrl;
    @JsonProperty("additional_prompt")
    private String additionalPrompt;
    @JsonProperty("makeup_transfer")
    private boolean makeupTransfer;
    @JsonProperty("makeup_transfer_ratio")
    private double makeupTransferRatio;
    @JsonProperty("skin_retouching_bool")
    private boolean skinRetouchingBool;

    public static SingleLoraDTO demo(String image) {
        return SingleLoraDTO.builder()
                .userid("liuyifei2")
                .imagesUrl(image)
                .additionalPrompt("")
                .makeupTransfer(true)
                .makeupTransferRatio(0.5)
                .skinRetouchingBool(true)
                .build();
    }

}
