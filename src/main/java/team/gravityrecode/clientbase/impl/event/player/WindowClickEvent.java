package team.gravityrecode.clientbase.impl.event.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import team.gravityrecode.clientbase.api.client.Event;

@Getter
@AllArgsConstructor
public class WindowClickEvent extends Event {

    private final int windowId, slotId, mouseButton, mode;

}
