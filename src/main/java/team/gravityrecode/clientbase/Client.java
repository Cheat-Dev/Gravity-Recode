package team.gravityrecode.clientbase;

import lombok.Getter;
import me.jinthium.shader.ShaderManager;
import team.gravityrecode.clientbase.api.client.ClientInfo;
import team.gravityrecode.clientbase.api.client.Event;
import team.gravityrecode.clientbase.api.eventBus.PubSub;
import team.gravityrecode.clientbase.api.util.MinecraftUtil;
import team.gravityrecode.clientbase.impl.manager.ModuleManager;
import org.lwjgl.opengl.Display;
import team.gravityrecode.clientbase.impl.manager.PropertyManager;
import team.gravityrecode.clientbase.impl.util.util.Blurrer;
import team.gravityrecode.clientbase.impl.util.util.render.DraggablesManager;
import team.gravityrecode.clientbase.impl.util.util.render.shaders.BlurUtil;
import viamcp.ViaMCP;

import java.nio.file.Path;
import java.nio.file.Paths;

@Getter
public enum Client implements MinecraftUtil{
    INSTANCE;

    private final PubSub<Event> pubSubEventBus = PubSub.newInstance(System.err::println);
    private final ClientInfo clientInfo = new ClientInfo("Gravity", "3.0-Recode", "Jinthium & That one cheater");
    private final ModuleManager moduleManager = new ModuleManager();
    private final PropertyManager propertyManager = new PropertyManager();
    public final Path clientDir = Paths.get(mc.mcDataDir.getAbsolutePath(), "Gravity");
    public final Path clientDirConfigs = Paths.get(String.valueOf(clientDir), "configs");
    private Blurrer blurrer;
    private BlurUtil blurUtil;
    private final ShaderManager shaderManager = new ShaderManager();
    private final DraggablesManager draggablesManager = new DraggablesManager();

    private final Runnable startGame = () -> {
        ViaMCP.getInstance().start();
        ViaMCP.getInstance().initAsyncSlider();

        if(!clientDir.toFile().exists()) {
            System.out.println(clientDir);
            clientDir.toFile().mkdir();
        }
        if(!clientDirConfigs.toFile().exists()) {
            System.out.println(clientDirConfigs);
            clientDirConfigs.toFile().mkdir();
        }
        shaderManager.init();
        propertyManager.init();
        moduleManager.init();
        Display.setTitle(clientInfo.getMinecraftTitle());
        blurrer = new Blurrer(false);
        blurUtil = new BlurUtil();
        draggablesManager.loadDraggableData();
    };

    private final Runnable stopGame = draggablesManager::saveDraggableData;
}
