package team.gravityrecode.clientbase.impl.event.render;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.entity.EntityLivingBase;
import team.gravityrecode.clientbase.api.client.Event;

@Getter
@AllArgsConstructor
public class RenderNametagEvent extends Event {

    private final EntityLivingBase entity;

}