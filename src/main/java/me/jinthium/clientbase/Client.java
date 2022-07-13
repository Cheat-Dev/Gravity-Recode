package me.jinthium.clientbase;

import lombok.Getter;
import me.jinthium.clientbase.api.client.ClientInfo;
import me.jinthium.clientbase.api.client.Event;
import me.jinthium.clientbase.api.eventBus.PubSub;
import me.jinthium.clientbase.api.util.MinecraftUtil;
import me.jinthium.clientbase.impl.manager.ModuleManager;
import org.checkerframework.checker.units.qual.C;
import org.lwjgl.opengl.Display;
import viamcp.ViaMCP;

@Getter
public enum Client implements MinecraftUtil{
    INSTANCE;

    private final PubSub<Event> pubSubEventBus = PubSub.newInstance(System.err::println);
    private final ClientInfo clientInfo = new ClientInfo("Gravity", "3.0 Recode", "Jinthium & iBbloppa");
    private final ModuleManager moduleManager = new ModuleManager();

    private final Runnable startGame = () -> {
        ViaMCP.getInstance().start();
        ViaMCP.getInstance().initAsyncSlider();
        moduleManager.init();
        Display.setTitle(clientInfo.getMinecraftTitle());
    };
}
