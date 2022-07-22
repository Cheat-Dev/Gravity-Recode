package team.gravityrecode.clientbase.impl.module.movement.flight;

import de.gerrygames.viarewind.utils.math.Vector3d;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import org.lwjgl.util.vector.Vector2f;
import team.gravityrecode.clientbase.api.eventBus.EventHandler;
import team.gravityrecode.clientbase.api.moduleBase.Module;
import team.gravityrecode.clientbase.impl.event.networking.PacketEvent;
import team.gravityrecode.clientbase.impl.event.player.PlayerMotionEvent;
import team.gravityrecode.clientbase.impl.event.player.PlayerStrafeEvent;
import team.gravityrecode.clientbase.impl.property.mode.Mode;
import team.gravityrecode.clientbase.impl.util.util.network.PacketUtil;

public class BlockDropFlight extends Mode {

    private de.gerrygames.viarewind.utils.math.Vector3d position;
    private Vector2f rotation;

    public BlockDropFlight(Module owner, String name) {
        super(owner, name);
    }

    @EventHandler
    public void onStrafe(PlayerStrafeEvent event) {
        event.setMotion(2f);
    }

    @EventHandler
    public void onPacket(PacketEvent event) {
        switch (event.getPacketType()) {
            case RECEIVING:
                if (event.getPacket() instanceof S08PacketPlayerPosLook) {
                    S08PacketPlayerPosLook s08 = event.getPacket();
                    if (mc.thePlayer.ticksExisted > 20) {
                        event.setCancelled(true);
                        this.position = new Vector3d(s08.getX(), s08.getY(), s08.getZ());
                        this.rotation = new Vector2f(s08.getYaw(), s08.getPitch());
                    }
                }
                break;
            case SENDING:
                if (event.getPacket() instanceof C03PacketPlayer) {
                    event.setCancelled(true);
                } else if (event.getPacket() instanceof C02PacketUseEntity) {
                    PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, false));
                }
                break;
        }
    }

    @EventHandler
    public void onMotion(PlayerMotionEvent event) {
        if (event.isPre()) {
            mc.thePlayer.motionY = mc.gameSettings.keyBindJump.isKeyDown() ? 0.42F : mc.gameSettings.keyBindSneak.isKeyDown() ? -0.42F : 0;

            PacketUtil.sendPacketNoEventTimes(new C03PacketPlayer.C06PacketPlayerPosLook(position.getX(), position.getY(), position.getZ(), rotation.getX(), rotation.getY(), false), 3);
        } else if (event.isPost()) {
            PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, rotation.getX(), rotation.getY(), false));

        }
    }

    @Override
    public void onEnable() {
        this.position = new de.gerrygames.viarewind.utils.math.Vector3d(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
        this.rotation = new Vector2f(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch);
        super.onEnable();
    }
}
