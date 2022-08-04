package team.gravityrecode.clientbase.impl.event.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import team.gravityrecode.clientbase.api.client.Event;
import team.gravityrecode.clientbase.impl.util.Position;
import team.gravityrecode.clientbase.impl.util.Rotation;
import team.gravityrecode.clientbase.impl.util.network.PacketUtil;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
public class PlayerTeleportEvent extends Event {

    private Position position;

    private Rotation rotation;

    private Set<S08PacketPlayerPosLook.EnumFlags> flags;


    public void handleSilently(double maxDistance) {
        setCancelled(true);

        double x = position.getPosX();
        double y = position.getPosY();
        double z = position.getPosZ();

        float yaw = rotation.getRotationYaw();
        float pitch = rotation.getRotationPitch();

        EntityPlayerSP entityPlayerSP = Minecraft.getMinecraft().thePlayer;

        if (entityPlayerSP.getDistance(x, y, z) >= maxDistance) {
            entityPlayerSP.setPositionAndRotation(x, y, z, yaw, pitch);
        }

        PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(x, y, z, yaw, pitch, false));
    }

    public void trySafeReject(double maxSilentDistance, double offsetY) {
        setCancelled(true);

        double x = position.getPosX();
        double y = position.getPosY();
        double z = position.getPosZ();

        EntityPlayerSP entityPlayerSP = Minecraft.getMinecraft().thePlayer;

        if (entityPlayerSP.getDistance(x, y, z) >= maxSilentDistance) {
            entityPlayerSP.setPosition(x, y, z);
        }

        PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(x, y + offsetY, z, false));
    }

}