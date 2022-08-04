package team.gravityrecode.clientbase.impl.module.movement.flight;

import net.minecraft.block.BlockAir;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import team.gravityrecode.clientbase.api.eventBus.EventHandler;
import team.gravityrecode.clientbase.api.moduleBase.Module;
import team.gravityrecode.clientbase.impl.event.networking.PacketEvent;
import team.gravityrecode.clientbase.impl.event.player.PlayerMotionEvent;
import team.gravityrecode.clientbase.impl.property.mode.Mode;
import team.gravityrecode.clientbase.impl.util.network.PacketUtil;
import team.gravityrecode.clientbase.impl.util.player.MovementUtil;
import team.gravityrecode.clientbase.impl.util.player.PlayerUtil;

public class VulcanFlight extends Mode {
    private double  startingLocationX, startingLocationY, startingLocationZ;
    private int ticksSinceFlag, offGroundTicks, ticks;
    private boolean bool;


    public VulcanFlight(Module owner, String name) {
        super(owner, name);
    }

    @Override
    public void onEnable() {
        super.onEnable();
//        mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - 1 + MovementUtil.getLilypadValue());
        ticksSinceFlag = 0;
        ticks = 0;
        bool = false;
        offGroundTicks = 0;
        startingLocationX = mc.thePlayer.posX;
        startingLocationZ = mc.thePlayer.posZ;
        startingLocationY = mc.thePlayer.posY;
    }

    @EventHandler
    public void a(PlayerMotionEvent event) {

        if(event.isPre()){
            ticks++;
            if (mc.thePlayer.onGround) {
                offGroundTicks = 0;
            } else {
                ++offGroundTicks;
            }


            ticksSinceFlag++;
            if (!(PlayerUtil.getBlockRelativeToPlayer(0, -0.2, 0) instanceof BlockAir) && mc.thePlayer.getDistanceSq(startingLocationX, startingLocationY, startingLocationZ) > 4 * 4) {
                mc.thePlayer.jump();
                ticksSinceFlag = 0;
            }

            if (!(ticksSinceFlag <= 20 && ticksSinceFlag >= 0)) {
                mc.thePlayer.motionY = 0;
                switch (offGroundTicks) {
                    case 1:
                        mc.thePlayer.motionY = mc.gameSettings.keyBindJump.isKeyDown() ? 1 : 0.5;
                        break;
                    case 2:
                        mc.thePlayer.motionY = mc.gameSettings.keyBindSneak.isKeyDown() ? -1 : -0.5;
                        break;
                    case 3:
                        mc.thePlayer.motionY = 0;
                        offGroundTicks = 0;
                        break;
                }
            } else if (ticksSinceFlag >= 4) {
                mc.thePlayer.motionY = 0;
                mc.thePlayer.setPosition(mc.thePlayer.posX, Math.round(event.getPosY() / 0.5) * 0.5, mc.thePlayer.posZ);
            }

            if ((ticksSinceFlag <= 20 && ticksSinceFlag >= 0 && ticksSinceFlag >= 4) || mc.thePlayer.posY % 0.5 == 0) {
                if (PlayerUtil.funny()) {
                    final double mathGround2 = Math.round(event.getPosY() / 0.015625) * 0.015625;
                    MovementUtil.setSpeed(0.2974 - 0.128);

                    mc.thePlayer.setPosition(mc.thePlayer.posX, mathGround2, mc.thePlayer.posZ);

                    event.setPosY(mathGround2);
                    event.setGround(true);
                    mc.thePlayer.onGround = true;
                }
            }

            if (bool || (!PlayerUtil.funny() && ticks > 15)) {
                if (PlayerUtil.funny())
                    PacketUtil.sendPacket(new C03PacketPlayer.C04PacketPlayerPosition((mc.thePlayer.posX + mc.thePlayer.lastTickPosX) / 2, (mc.thePlayer.posY + mc.thePlayer.lastTickPosY) / 2, (mc.thePlayer.posZ + mc.thePlayer.lastTickPosZ) / 2, true));
                MovementUtil.setSpeed(MovementUtil.getBaseMoveSpeed() * 1.5 * 2);
                mc.timer.timerSpeed = 1.2f + mc.thePlayer.hurtTime / 3f;
            } else {
                MovementUtil.setSpeed(MovementUtil.getBaseMoveSpeed());
            }
        }
    }

    @EventHandler
    public void b(PacketEvent event){
        if(event.getPacket() instanceof S08PacketPlayerPosLook){
            S08PacketPlayerPosLook s08 = event.getPacket();
            if (mc.thePlayer.ticksExisted > 20) {
                if (Math.abs(s08.getX() - startingLocationX) + Math.abs(s08.getY() - startingLocationY) + Math.abs(s08.getZ() - startingLocationZ) < 4) {
                    if (PlayerUtil.funny()) event.setCancelled(true);
                    if (!bool) {
                        mc.thePlayer.hurtTime = 9;
                        bool = true;
                    }
                } else {
                    this.getOwner().toggle();
                    this.onDisable();
                }
            }
        }
    }

}
