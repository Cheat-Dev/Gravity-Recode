package me.jinthium.clientbase.impl.module.movement;

import me.jinthium.clientbase.api.eventBus.EventHandler;
import me.jinthium.clientbase.api.moduleBase.Module;
import me.jinthium.clientbase.api.moduleBase.ModuleInfo;
import me.jinthium.clientbase.impl.event.player.PlayerMotionEvent;

@ModuleInfo(moduleName = "Sprint", moduleCategory = Module.ModuleCategory.MOVEMENT)
public class Sprint extends Module {

    @EventHandler
    public void onPlayerMotionEvent(PlayerMotionEvent event){

    }

}
