package team.gravityrecode.clientbase.impl.module.ghost;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.play.client.C02PacketUseEntity;
import org.lwjgl.input.Keyboard;
import team.gravityrecode.clientbase.api.eventBus.EventHandler;
import team.gravityrecode.clientbase.api.moduleBase.Module;
import team.gravityrecode.clientbase.api.moduleBase.ModuleInfo;
import team.gravityrecode.clientbase.impl.event.networking.PacketEvent;
import team.gravityrecode.clientbase.impl.event.player.PlayerMotionEvent;
import team.gravityrecode.clientbase.impl.property.BooleanSetting;
import team.gravityrecode.clientbase.impl.property.NumberSetting;
import team.gravityrecode.clientbase.impl.util.client.Logger;
import team.gravityrecode.clientbase.impl.util.math.MathUtil;

@ModuleInfo(moduleName = "WTap", moduleCategory = Module.ModuleCategory.COMBAT)
public class WTap extends Module {

    public boolean stop;

    @EventHandler
    public void onUpdate(PlayerMotionEvent event) {
        if (stop) {
            if (mc.thePlayer.ticksExisted % 5 == 0)
            mc.gameSettings.keyBindForward.pressed = false;
            if (mc.thePlayer.ticksExisted % 2 == 0) {
                mc.gameSettings.keyBindForward.pressed = Keyboard.isKeyDown(mc.gameSettings.keyBindForward.getKeyCode());
                stop = false;
            }
        }
    }

    @EventHandler
    public void onPacket(PacketEvent event) {
        if (event.getPacket() instanceof C02PacketUseEntity) {
            if (((C02PacketUseEntity) event.getPacket()).getAction() == C02PacketUseEntity.Action.ATTACK) {
                stop = true;
            }
        }
    }
}
