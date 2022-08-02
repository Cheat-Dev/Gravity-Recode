package team.gravityrecode.clientbase.impl.module.movement.flight;

import com.sun.media.jfxmedia.control.MediaPlayerOverlay;
import net.minecraft.block.BlockAir;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.AxisAlignedBB;
import team.gravityrecode.clientbase.api.eventBus.EventHandler;
import team.gravityrecode.clientbase.api.moduleBase.Module;
import team.gravityrecode.clientbase.impl.event.networking.PacketEvent;
import team.gravityrecode.clientbase.impl.event.player.PlayerCollideEvent;
import team.gravityrecode.clientbase.impl.event.player.PlayerStrafeEvent;
import team.gravityrecode.clientbase.impl.property.mode.Mode;
import team.gravityrecode.clientbase.impl.util.player.MovementUtil;

public class CollideFlight extends Mode {
    private double startPosY;
    public CollideFlight(Module owner, String name) {
        super(owner, name);
    }

    @EventHandler
    public void a(PlayerCollideEvent event){
        if(event.getBlock() instanceof BlockAir)
            event.setAxisAlignedBB(new AxisAlignedBB(-12.0, -1.0, -12.0, 12.0, 0.0, 12.0).offset(event.getBlockPos().getX(),
                    mc.thePlayer.posY, event.getBlockPos().getZ()));
    }

    @EventHandler
    public void b(PlayerStrafeEvent event){
        if(MovementUtil.isMoving()){
            //mc.thePlayer.motionY = mc.thePlayer.movementInput.jump ? 0.42F : mc.thePlayer.movementInput.sneak ? -0.42F : 0.0F;
            event.setMotion(1.1f);
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
        startPosY = mc.thePlayer.posY;
    }
}
