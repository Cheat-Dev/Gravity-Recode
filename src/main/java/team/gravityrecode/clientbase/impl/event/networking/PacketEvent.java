package team.gravityrecode.clientbase.impl.event.networking;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.network.Packet;
import team.gravityrecode.clientbase.api.client.Event;

@AllArgsConstructor@Getter@Setter
public class PacketEvent extends Event {
    private final PacketType packetType;
    private Packet packet;

    public <T extends Packet> T getPacket(){
        return (T)packet;
    }

    public enum PacketType{
        RECEIVING,
        SENDING
    }
}
