package me.jinthium.clientbase.impl.event.keyboard;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.jinthium.clientbase.api.client.Event;

@AllArgsConstructor@Getter
public class KeyboardPressEvent extends Event {
    private final int keyCode;
}
