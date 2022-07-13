package team.gravityrecode.clientbase.impl.event.keyboard;

import lombok.AllArgsConstructor;
import lombok.Getter;
import team.gravityrecode.clientbase.api.client.Event;

@AllArgsConstructor@Getter
public class KeyboardPressEvent extends Event {
    private final int keyCode;
}
