package team.gravityrecode.clientbase.impl.event.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import team.gravityrecode.clientbase.api.client.Event;

@Getter
@Setter
@AllArgsConstructor
public class PlayerMotionEvent extends Event {

    private double posX, posY, posZ;
    private float yaw, pitch, prevYaw, prevPitch;

    private boolean onGround;

    private EventState eventState;

    public boolean isPre() {
        return getEventState() == EventState.PRE;
    }

    public boolean isUpdate() {
        return getEventState() == EventState.UPDATE;
    }

    public boolean isPost() {
        return getEventState() == EventState.POST;
    }

    public enum EventState {
        UPDATE, PRE, POST
    }
}