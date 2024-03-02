package xyz.thuray.geniuslens.server.data.enums;

import lombok.Getter;

@Getter
public enum MessageSender {
    SYSTEM(-1L);

    private final Long value;

    MessageSender(Long value) {
        this.value = value;
    }

}
