package team.gravityrecode.clientbase.impl.util.entity.impl;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.Entity;
import team.gravityrecode.clientbase.impl.util.entity.ICheck;

public final class AliveCheck implements ICheck {
    @Override
    public boolean validate(Entity entity) {
       // if(Minecraft.getMinecraft().isSingleplayer()) return entity.isEntityAlive();
        return entity.isEntityAlive() || Minecraft.getMinecraft().getCurrentServerData().serverIP.contains("mineplex");
    }

    private boolean isOnTab(Entity entity){
        for (NetworkPlayerInfo info : Minecraft.getMinecraft().getNetHandler().getPlayerInfoMap()) {
            if (info.getGameProfile().getName().toLowerCase().contains(entity.getName().toLowerCase())) {
                return true;
            }
        }
        return false;
    }
}

