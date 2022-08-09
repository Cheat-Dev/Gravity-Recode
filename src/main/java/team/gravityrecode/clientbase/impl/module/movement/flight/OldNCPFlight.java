package team.gravityrecode.clientbase.impl.module.movement.flight;

import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S01PacketJoinGame;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.MovementInput;
import org.lwjgl.input.Keyboard;
import team.gravityrecode.clientbase.api.eventBus.EventHandler;
import team.gravityrecode.clientbase.api.moduleBase.Module;
import team.gravityrecode.clientbase.impl.event.networking.PacketEvent;
import team.gravityrecode.clientbase.impl.event.player.PlayerMotionEvent;
import team.gravityrecode.clientbase.impl.event.player.PlayerMoveEvent;
import team.gravityrecode.clientbase.impl.property.mode.Mode;
import team.gravityrecode.clientbase.impl.util.client.Logger;
import team.gravityrecode.clientbase.impl.util.client.TimerUtil;
import team.gravityrecode.clientbase.impl.util.network.PacketUtil;
import team.gravityrecode.clientbase.impl.util.player.MovementUtil;

import java.sql.Time;

public class OldNCPFlight extends Mode {

    private boolean doFly;
    private double moveSpeed, y, lastX, lastY, lastZ;
    private final TimerUtil timer = new TimerUtil();
    private int stage;

    public OldNCPFlight(Module owner, String name) {
        super(owner, name);
    }

    @EventHandler
    public void bb(PlayerMoveEvent event){
        if(!doFly){
            MovementUtil.setSpeed(event, 0);
            return;
        }
        MovementUtil.setSpeed(event, MovementUtil.getBaseMoveSpeed() + 0.0005 + moveSpeed);
        if(mc.thePlayer.ticksExisted % 2 == 0)
            moveSpeed += 1e-7;
    }

    @EventHandler
    public void a(PlayerMotionEvent event) {
        if (mc.thePlayer.onGround) {
            timer.reset();
        }

        if(!event.isUpdate()){

            if(doFly) {
                if (mc.thePlayer.ticksExisted % 2 == 0) {
                    event.setPosY(event.getPosY() + 0.0001);
                } else if (mc.thePlayer.ticksExisted % 3 == 0) {
                    event.setPosY(event.getPosY() - 0.0003);
                }
            }
        }

        if(event.isPre()) {
            if(!doFly)
                mc.thePlayer.motionX *= mc.thePlayer.motionZ *= -0.01111111;

            if(mc.thePlayer.hurtTime > 0){
                doFly = true;
            }
            mc.thePlayer.motionY = 0;

            event.setOnGround(false);
            //mc.timer.timerSpeed = Math.max(mc.timer.timerSpeed, 1);
            if (stage == 1) {
                //PacketUtil.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(mc.thePlayer.getPosition().down(), EnumFacing.UP.getIndex(), mc.thePlayer.getHeldItem(), 0, 0.049392F, 0));
                double[] bypassValues = {1.0155550727022, 0.78502770378923, 0.48071087633169, 0.10408037809304, 0.1};
                for (final double i : bypassValues) {
                    PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(lastX, lastY + i, lastZ, false));
                }
                PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(lastX, lastY - 0.215, lastZ, false));
                stage = -1;
            }
            if (stage == 0) {
                //PacketUtil.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(mc.thePlayer.getPosition().down(), EnumFacing.UP.getIndex(), mc.thePlayer.getHeldItem(), 0, 0.049392F, 0));
                double[] bypassValues = {0.41999998688698, 0.7531999805212, 1.00133597911214, 1.16610926093821, 1.24918707874468, 1.24918707874468, 1.1707870772188};
                for (final double i : bypassValues) {
                    PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(lastX, lastY + i, lastZ, false));
                }
                stage = 1;
            }
        }else if(event.isPost()) {
            if(stage >= 2){
                //mc.timer.timerSpeed = Math.max(1.0f, mc.timer.timerSpeed - mc.timer.timerSpeed / 159);
                //mc.timer.timerSpeed = 1.6f;
//                mc.timer.timerSpeed = 0.5f;
//                double x = (mc.thePlayer.posX - mc.thePlayer.lastTickPosX) * MathUtil.randomDouble(0.95 + MovementUtil.getRandomHypixelValuesFloat(), 0.98 + MovementUtil.getRandomHypixelValuesFloat());
//                double z = (mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ) * MathUtil.randomDouble(0.95 + MovementUtil.getRandomHypixelValuesFloat(), 0.98 + MovementUtil.getRandomHypixelValuesFloat());
//                for(int i = 0; i < 2; i++) {
//                    PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX + x, event.getPosY(), mc.thePlayer.posZ + z, true));
//                    mc.thePlayer.setPosition(mc.thePlayer.posX + x, event.getPosY(), mc.thePlayer.posZ + z);
//                }
            }
        }
    }

    @EventHandler
    public void onMove(PacketEvent event) {
        if(event.getPacket() instanceof S01PacketJoinGame){
            stage = 0;
            moveSpeed = 0;
        }


        if (event.getPacket() instanceof S08PacketPlayerPosLook && stage == -1) {
            final S08PacketPlayerPosLook packetPlayerPosLook = event.getPacket();
            y = packetPlayerPosLook.getY();
            Logger.print(y + "");
            PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(packetPlayerPosLook.getX(), packetPlayerPosLook.getY(), packetPlayerPosLook.getZ(), false));
            for(int i = 0; i < 30; i++) {
                PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(packetPlayerPosLook.getX(), packetPlayerPosLook.getY() - 0.0625, packetPlayerPosLook.getZ(), false));
                PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(packetPlayerPosLook.getX(), packetPlayerPosLook.getY(), packetPlayerPosLook.getZ(), false));
            }
            PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(packetPlayerPosLook.getX(), packetPlayerPosLook.getY(), packetPlayerPosLook.getZ(), true));
            mc.thePlayer.motionY = y - mc.thePlayer.posY;
            stage = 2;
            timer.reset();
            event.setCancelled(true);
        }
    }

    @Override
    public void onEnable() {
        moveSpeed = 0;
        lastX = mc.thePlayer.posX;
        lastY = mc.thePlayer.posY;
        y = 0;
        stage = 0;
        timer.reset();
        lastZ = mc.thePlayer.posZ;
        doFly = false;
        super.onEnable();
    }
}
