package xyz.thuray.geniuslens.server.data.enums;

import lombok.Getter;

@Getter
public enum MessageType {
    LIKE(1),
    COMMENT(2),
    SYSTEM(3);

    private final int value;

    MessageType(int value) {
        this.value = value;
    }

}
