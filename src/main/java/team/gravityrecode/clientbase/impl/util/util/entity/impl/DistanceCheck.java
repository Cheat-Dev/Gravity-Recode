package team.gravityrecode.clientbase.impl.util.util.entity.impl;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import team.gravityrecode.clientbase.impl.property.NumberSetting;
import team.gravityrecode.clientbase.impl.util.util.entity.ICheck;

public final class DistanceCheck implements ICheck {
    private final NumberSetting distance;

    public DistanceCheck(NumberSetting distance) {
        this.distance = distance;
    }

    @Override
    public boolean validate(Entity entity) {
        return Minecraft.getMinecraft().thePlayer.getDistanceToEntity(entity) <= distance.getValue().floatValue();
    }
}

