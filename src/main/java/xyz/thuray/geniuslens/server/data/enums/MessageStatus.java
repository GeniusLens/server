package xyz.thuray.geniuslens.server.data.enums;

import lombok.Getter;

@Getter
public enum MessageStatus {
    UNREAD(0),
    READ(1);

    private final Integer value;

    MessageStatus(Integer value) {
        this.value = value;
    }
}
