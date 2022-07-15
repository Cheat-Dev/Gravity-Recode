package team.gravityrecode.clientbase.impl.util.util.network;
import lombok.Getter;
import net.minecraft.network.play.client.C03PacketPlayer;
import team.gravityrecode.clientbase.api.eventBus.EventHandler;

@Getter
public enum BalanceUtil {
    INSTANCE;

    private long balance, lastPacket;

//    @EventHandler(Priorities.VERY_LOW)
//    private final Listener<WorldLoadEvent> worldLoadEventListener = event ->{
//        balance = lastPacket = 0;
//    };
//
//    @EventHandler(Priorities.VERY_LOW)
//    private final Listener<PacketEvent> packetEventListener = event -> {
//        if(event.getPacket() instanceof C03PacketPlayer && event.getEventState() == PacketEvent.EventState.SENDING){
//            if (lastPacket == 0) lastPacket = System.currentTimeMillis();
//            long delay = System.currentTimeMillis() - lastPacket;
//            balance += event.isCancelled() ? -delay : 50 - delay;
//            lastPacket = System.currentTimeMillis();
//        }
//    };
}