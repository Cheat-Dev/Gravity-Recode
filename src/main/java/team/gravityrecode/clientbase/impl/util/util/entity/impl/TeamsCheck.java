package team.gravityrecode.clientbase.impl.util.util.entity.impl;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import team.gravityrecode.clientbase.impl.util.util.entity.ICheck;
import team.gravityrecode.clientbase.impl.util.util.player.PlayerUtil;

public final class TeamsCheck implements ICheck {
    private final boolean teams;

    public TeamsCheck(final boolean teams) {
        this.teams = teams;
    }

    @Override
    public boolean validate(Entity entity) {
        if (entity instanceof EntityPlayer) {
            return !PlayerUtil.isTeammate((EntityPlayer) entity) || !teams;
        }
        return true;
    }
}
