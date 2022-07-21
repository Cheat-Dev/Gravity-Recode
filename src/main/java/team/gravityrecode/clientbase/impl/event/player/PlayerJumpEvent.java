package team.gravityrecode.clientbase.impl.event.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.entity.EntityLivingBase;
import team.gravityrecode.clientbase.api.client.Event;

@Getter
@Setter
@AllArgsConstructor
public class PlayerJumpEvent extends Event {
    private EntityLivingBase entity;
    private double motionY;
    private float yaw;
}