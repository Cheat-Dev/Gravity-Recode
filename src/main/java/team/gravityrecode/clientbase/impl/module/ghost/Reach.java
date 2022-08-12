package team.gravityrecode.clientbase.impl.module.ghost;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import team.gravityrecode.clientbase.api.eventBus.EventHandler;
import team.gravityrecode.clientbase.api.moduleBase.Module;
import team.gravityrecode.clientbase.api.moduleBase.ModuleInfo;
import team.gravityrecode.clientbase.impl.event.player.PlayerMotionEvent;
import team.gravityrecode.clientbase.impl.property.BooleanSetting;
import team.gravityrecode.clientbase.impl.property.NumberSetting;
import team.gravityrecode.clientbase.impl.util.client.Logger;
import team.gravityrecode.clientbase.impl.util.math.MathUtil;
import team.gravityrecode.clientbase.impl.util.player.RotationUtil;

@ModuleInfo(moduleName = "Reach", moduleCategory = Module.ModuleCategory.COMBAT)
public class Reach extends Module {

    public BooleanSetting randomize = new BooleanSetting(this, "Randomize", true);
    public NumberSetting reach = new NumberSetting(this, "Reach", 3.7, 3.0, 6.0, 0.1, () -> !randomize.getValue());
    public NumberSetting maxReach = new NumberSetting(this, "Max Reach", 3.7, 3, 6.0, 0.1, () -> randomize.getValue());
    public NumberSetting minReach = new NumberSetting(this, "Min Reach", 3.4, 3.0, 6.0, 0.1, () -> randomize.getValue());
    public float mainReach;

    @EventHandler
    public void onUpdate(PlayerMotionEvent event) {
        mainReach = randomize.getValue() ? MathUtil.randomFloat(minReach.getValue().floatValue(), maxReach.getValue().floatValue()) : reach.getValue().floatValue();
    }
}
