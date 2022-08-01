package team.gravityrecode.clientbase.impl.module.movement.flight;

import team.gravityrecode.clientbase.api.eventBus.EventHandler;
import team.gravityrecode.clientbase.api.moduleBase.Module;
import team.gravityrecode.clientbase.impl.event.player.PlayerMoveEvent;
import team.gravityrecode.clientbase.impl.property.BooleanSetting;
import team.gravityrecode.clientbase.impl.property.NumberSetting;
import team.gravityrecode.clientbase.impl.property.mode.Mode;
import team.gravityrecode.clientbase.impl.util.player.MovementUtil;

public class VanillaFlight extends Mode {

    public final NumberSetting speed = new NumberSetting(getOwner(), "Speed", 1.0, 0.1, 2.0, 0.05);
    private final BooleanSetting glide = new BooleanSetting(getOwner(), "Glide", false);
    private final NumberSetting glideSpeed = new NumberSetting(getOwner(), "Glide Speed", 0.25, 0.05, 0.8, 0.05, () -> glide.getValue());

    public VanillaFlight(Module owner, String name) {
        super(owner, name);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        mc.thePlayer.onGround = (mc.gameSettings.keyBindJump.isKeyDown() || mc.gameSettings.keyBindSneak.isKeyDown()) ? false : true;
        if (mc.gameSettings.keyBindJump.isKeyDown())
            event.setY(mc.thePlayer.motionY = speed.getValue());
        else if (mc.gameSettings.keyBindSneak.isKeyDown())
            event.setY(mc.thePlayer.motionY = -speed.getValue());
        else
            event.setY(mc.thePlayer.motionY = glide.getValue() ? -glideSpeed.getValue() : 0);
        if (mc.thePlayer.isMoving()) {
            MovementUtil.setSpeed(event, speed.getValue());
        }
    }
}
