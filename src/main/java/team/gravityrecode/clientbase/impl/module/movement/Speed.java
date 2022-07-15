package team.gravityrecode.clientbase.impl.module.movement;

import org.lwjgl.input.Keyboard;
import team.gravityrecode.clientbase.api.eventBus.EventHandler;
import team.gravityrecode.clientbase.api.moduleBase.Module;
import team.gravityrecode.clientbase.api.moduleBase.ModuleInfo;
import team.gravityrecode.clientbase.impl.event.player.PlayerMoveEvent;
import team.gravityrecode.clientbase.impl.module.movement.speed.NCP;
import team.gravityrecode.clientbase.impl.module.movement.speed.OldNCP;
import team.gravityrecode.clientbase.impl.property.ModeSetting;
import team.gravityrecode.clientbase.impl.util.util.player.MovementUtil;

@ModuleInfo(moduleName = "Speed", moduleCategory = Module.ModuleCategory.MOVEMENT, moduleKeyBind = Keyboard.KEY_F)
public class Speed extends Module {

    OldNCP oldNCP = new OldNCP(this, "OldNCP");
    NCP ncp = new NCP(this, "NCP");
    public ModeSetting modeSetting = new ModeSetting(this, "Mode", oldNCP, ncp);

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
      oldNCP.onMove(event);
      ncp.onMove(event);
    }

    @Override
    public void onDisable() {
        oldNCP.onDisable();
        ncp.onDisable();
        super.onDisable();
    }
}
