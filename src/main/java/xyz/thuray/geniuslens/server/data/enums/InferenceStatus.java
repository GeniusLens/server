package xyz.thuray.geniuslens.server.data.enums;

import lombok.Getter;

@Getter
public enum InferenceStatus {
    PENDING(1),
    PROCESSING(2),
    FINISHED(3),
    FAILED(4);

    private final Integer value;

    InferenceStatus(Integer value) {
        this.value = value;
    }

}
