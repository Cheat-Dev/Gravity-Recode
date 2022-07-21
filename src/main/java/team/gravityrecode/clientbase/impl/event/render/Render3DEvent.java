package team.gravityrecode.clientbase.impl.event.render;

import lombok.AllArgsConstructor;
import lombok.Getter;
import team.gravityrecode.clientbase.api.client.Event;

@Getter
@AllArgsConstructor
public class Render3DEvent extends Event {
    private final float partialTicks;
}