package team.gravityrecode.clientbase.impl.event.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import team.gravityrecode.clientbase.api.client.Event;

@Getter
@Setter
@AllArgsConstructor
public class PlayerCollideEvent extends Event {
    private AxisAlignedBB axisAlignedBB;
    private final Block block;
    private final Entity collidingEntity;
    private final BlockPos blockPos;
}
