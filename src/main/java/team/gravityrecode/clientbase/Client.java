package team.gravityrecode.clientbase;

import lombok.Getter;
import team.gravityrecode.clientbase.api.client.ClientInfo;
import team.gravityrecode.clientbase.api.client.Event;
import team.gravityrecode.clientbase.api.eventBus.PubSub;
import team.gravityrecode.clientbase.api.util.MinecraftUtil;
import team.gravityrecode.clientbase.impl.manager.ModuleManager;
import org.lwjgl.opengl.Display;
import viamcp.ViaMCP;

@Getter
public enum Client implements MinecraftUtil{
    INSTANCE;

    private final PubSub<Event> pubSubEventBus = PubSub.newInstance(System.err::println);
    private final ClientInfo clientInfo = new ClientInfo("Gravity", "One", "Jinthium & That one cheater");
    private final ModuleManager moduleManager = new ModuleManager();

    private final Runnable startGame = () -> {
        ViaMCP.getInstance().start();
        ViaMCP.getInstance().initAsyncSlider();
        moduleManager.init();
        Display.setTitle(clientInfo.getMinecraftTitle());
    };
}
