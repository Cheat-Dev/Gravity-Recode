package team.gravityrecode.clientbase.impl.module.ghost;

import team.gravityrecode.clientbase.api.eventBus.EventHandler;
import team.gravityrecode.clientbase.api.moduleBase.Module;
import team.gravityrecode.clientbase.api.moduleBase.ModuleInfo;
import team.gravityrecode.clientbase.impl.event.player.PlayerMotionEvent;
import team.gravityrecode.clientbase.impl.property.BooleanSetting;
import team.gravityrecode.clientbase.impl.property.NumberSetting;
import team.gravityrecode.clientbase.impl.util.client.Logger;
import team.gravityrecode.clientbase.impl.util.client.TimerUtil;
import team.gravityrecode.clientbase.impl.util.math.MathUtil;

@ModuleInfo(moduleName = "AutoClicker", moduleCategory = Module.ModuleCategory.GHOST)
public class AutoClicker extends Module {

    private BooleanSetting random = new BooleanSetting(this, "Random", true);
    private NumberSetting cps = new NumberSetting(this, "CPS", 12, 1, 20, 1, () -> !random.getValue());
    private NumberSetting maxCPS = new NumberSetting(this, "MaxCPS", 12, 1, 20, 1, () -> random.getValue());
    private NumberSetting minCPS = new NumberSetting(this, "MinCPS", 9, 1, 20, 1, () -> random.getValue());
    TimerUtil attackTimer = new TimerUtil();

    @EventHandler
    public void onUpdate(PlayerMotionEvent event) {
        double min = minCPS.getValue(), max = maxCPS.getValue(), normalCPS = cps.getValue();
        double cps = random.getValue() ? MathUtil.randomDouble(min, max) : normalCPS;
        if (mc.gameSettings.keyBindAttack.isKeyDown() && attackTimer.hasElapsed((long) (1000 / cps)) && event.isPre()) {
            mc.leftClickCounter = 0;
            mc.clickMouse();
            attackTimer.reset();
        }
    }
}
