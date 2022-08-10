package team.gravityrecode.clientbase.impl.module.combat;

import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;
import team.gravityrecode.clientbase.api.eventBus.EventHandler;
import team.gravityrecode.clientbase.api.moduleBase.Module;
import team.gravityrecode.clientbase.api.moduleBase.ModuleInfo;
import team.gravityrecode.clientbase.impl.event.networking.PacketEvent;

@ModuleInfo(moduleName = "Dont_Take_knockback_because_you_are_cancelling_packets_that_cause_knockback", moduleCategory = Module.ModuleCategory.COMBAT)
public class Volecity extends Module {

    @EventHandler
    public void a(PacketEvent event){
        if(event.getPacket() instanceof S12PacketEntityVelocity || event.getPacket() instanceof S27PacketExplosion)
            event.setCancelled(true);
    }
}
