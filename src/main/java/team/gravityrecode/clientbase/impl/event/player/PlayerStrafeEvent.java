package team.gravityrecode.clientbase.impl.event.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import team.gravityrecode.clientbase.api.client.Event;

@AllArgsConstructor
@Getter
@Setter
public class PlayerStrafeEvent extends Event {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private float strafe, forward, friction, yaw;

    public void setMotion(double speed) {
        mc.thePlayer.motionX = 0;
        mc.thePlayer.motionZ = 0;
        setFriction((float) speed);
    }

    /**
     * Sets motion with legitimate strafe & forward components
     * @param friction - The friction
     */
    public void setMotionLegit(float friction) {
        setFriction(mc.thePlayer.onGround ? friction : friction * 0.43F);
    }

    /**
     * Sets motion with an illegitimate strafe & legitimate forward component
     * @param friction - The friction
     * @param strafeComponent - Strafe component value ranging from 0.0 to 1.0
     */
    public void setMotionPartialStrafe(float friction, float strafeComponent) {
        float remainder = 1F - strafeComponent;
        if (mc.thePlayer.onGround) {
            setMotion(friction);
        } else {
            mc.thePlayer.motionX *= strafeComponent;
            mc.thePlayer.motionZ *= strafeComponent;
            setFriction(friction * remainder);
        }
    }

}
