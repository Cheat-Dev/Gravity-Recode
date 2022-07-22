package team.gravityrecode.clientbase.impl.module.movement.flight;

import de.gerrygames.viarewind.utils.math.Vector3d;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.MovementInput;
import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector2f;
import team.gravityrecode.clientbase.api.eventBus.EventHandler;
import team.gravityrecode.clientbase.api.moduleBase.Module;
import team.gravityrecode.clientbase.impl.event.networking.PacketEvent;
import team.gravityrecode.clientbase.impl.event.player.PlayerMotionEvent;
import team.gravityrecode.clientbase.impl.event.player.PlayerMoveEvent;
import team.gravityrecode.clientbase.impl.event.player.PlayerStrafeEvent;
import team.gravityrecode.clientbase.impl.property.mode.Mode;
import team.gravityrecode.clientbase.impl.util.util.network.PacketUtil;
import team.gravityrecode.clientbase.impl.util.util.player.MovementUtil;

public class OldNCPFlight extends Mode {

    private boolean doFly;
    private double moveSpeed;

    public OldNCPFlight(Module owner, String name) {
        super(owner, name);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if(MovementUtil.isMoving()){
            MovementInput movementInput = mc.thePlayer.movementInput;
            if (!Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
                if (!(mc.thePlayer.fallDistance > 2F)) {
                    if (MovementUtil.isMovingOnGround()) {
                        mc.timer.timerSpeed = 0.9855F;
                        // MovementUtil.damage();
                        event.setY(mc.thePlayer.motionY = MovementUtil.getJumpHeight(0.42F));
                        moveSpeed = 0.6145522F; //movementSpeed = MovementUtil.getBaseMoveSpeed() * 2.11;
                        doFly = true;
                    } else {
                        event.setY(mc.thePlayer.motionY = mc.thePlayer.ticksExisted % 2 == 0 ? 0.0001 : -0.0001);
                        if (doFly) {
                            moveSpeed = 1f + (10 / 10.5F);
                            double timer1 = 9F;
                            mc.timer.timerSpeed = (float) timer1;
                            doFly = false;
                        } else {
                            mc.timer.timerSpeed = Math.max(4f, mc.timer.timerSpeed - (mc.timer.timerSpeed / 1000));
                            moveSpeed *= 0.985;
                        }
                        if (mc.thePlayer.isCollidedHorizontally || !mc.thePlayer.isMoving()) {
                            moveSpeed = MovementUtil.getBaseMoveSpeed();
                        }
                    }
                    MovementUtil.setSpeed(event, Math.max(MovementUtil.getBaseMoveSpeed(), moveSpeed));
                } else {
                    MovementUtil.setSpeed(event, 5.3);
                    double newSpeed = 5.3 * 0.425f;
                    event.setY(movementInput.jump ? newSpeed : movementInput.sneak ? -newSpeed : -0.1535);
                    mc.thePlayer.motionY = 0;
                }
            } else {
                if(MovementUtil.isMovingOnGround()) {
                    event.setY(mc.thePlayer.motionY = 0.42F);
                } else {
                    event.setY(mc.thePlayer.ticksExisted % 4 == 0 ? -0.000988 : 0.000988);
                    MovementUtil.setSpeed(event, MovementUtil.getBaseMoveSpeed());
                }
                mc.thePlayer.motionY = 0;
            }
        }
    }

    @Override
    public void onEnable() {
        moveSpeed = 0;
        doFly = false;
        super.onEnable();
    }
}
