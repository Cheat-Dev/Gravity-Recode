package team.gravityrecode.clientbase.impl.event.render;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.ScaledResolution;
import team.gravityrecode.clientbase.api.client.Event;

@Getter
@AllArgsConstructor
public class Render2DEvent extends Event {

    private final ScaledResolution scaledResolution;
    private final float partialTicks;
}
