package team.gravityrecode.clientbase;

import lombok.Getter;
import lombok.Setter;
import me.jinthium.clickgui.MainCGUI;
import me.jinthium.scripting.ScriptManager;
import me.jinthium.shader.ShaderManager;
import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;
import team.gravityrecode.clientbase.api.client.ClientInfo;
import team.gravityrecode.clientbase.api.client.Event;
import team.gravityrecode.clientbase.api.eventBus.PubSub;
import team.gravityrecode.clientbase.api.notifications.NotificationManager;
import team.gravityrecode.clientbase.api.util.MinecraftUtil;
import team.gravityrecode.clientbase.impl.manager.ChangelogManager;
import team.gravityrecode.clientbase.impl.manager.ModuleManager;
import org.lwjgl.opengl.Display;
import team.gravityrecode.clientbase.impl.manager.NotificationManagers;
import team.gravityrecode.clientbase.impl.manager.PropertyManager;
import team.gravityrecode.clientbase.impl.util.Blurrer;
import team.gravityrecode.clientbase.impl.util.foint.Fonts;
import team.gravityrecode.clientbase.impl.util.render.DraggablesManager;
import team.gravityrecode.clientbase.impl.util.render.shaders.BlurUtil;
import viamcp.ViaMCP;

import java.nio.file.Path;
import java.nio.file.Paths;

@Getter
public enum Client implements MinecraftUtil {
    INSTANCE;

    private final PubSub<Event> pubSubEventBus = PubSub.newInstance(System.err::println);
    private final ClientInfo clientInfo = new ClientInfo("Gravity", "3.0-Recode", "Jinthium & That one cheater");
    private final ModuleManager moduleManager = new ModuleManager();
    private final ScriptManager scriptManager = ScriptManager.INSTANCE;
    private final PropertyManager propertyManager = new PropertyManager();
    public final Path clientDir = Paths.get(mc.mcDataDir.getAbsolutePath(), "Gravity");
    public final Path clientDirConfigs = Paths.get(String.valueOf(clientDir), "configs");
    private final ChangelogManager changelogManager = new ChangelogManager();

    @Setter
    private float renderDeltaTime;
    private Blurrer blurrer;
    private MainCGUI mainCGUI;
    private BlurUtil blurUtil;
    private final ShaderManager shaderManager = new ShaderManager();
    private DraggablesManager draggablesManager;
    private DiscordRPC rpc;
    private NotificationManagers notificationManager = new NotificationManagers();

    private final Runnable startGame = () -> {
        ViaMCP.getInstance().start();
        ViaMCP.getInstance().initAsyncSlider();
        rpc = new DiscordRPC();
        String applicationId = "999497605669195796";
        String steamId = "";
        DiscordEventHandlers handlers = new DiscordEventHandlers();
        handlers.ready = (user) -> System.out.println("Ready!");
        rpc.discordInitialize(applicationId, handlers, true, steamId);
        updateRPC("Version: 3.0", "Loading...");
        if (!clientDir.toFile().exists()) {
            System.out.println(clientDir);
            clientDir.toFile().mkdir();
        }
        if (!clientDirConfigs.toFile().exists()) {
            System.out.println(clientDirConfigs);
            clientDirConfigs.toFile().mkdir();
        }
        Fonts.INSTANCE.initFonts.run();
        changelogManager.init();
        draggablesManager = new DraggablesManager();
        shaderManager.init();
        pubSubEventBus.subscribe(propertyManager);
        propertyManager.init();
        scriptManager.init();
        moduleManager.init();
        mainCGUI = new MainCGUI();
        Display.setTitle(clientInfo.getMinecraftTitle());
        blurrer = new Blurrer(false);
        blurUtil = new BlurUtil();
//        if (!draggablesManager.getDraggables().values().isEmpty())
//            draggablesManager.loadDraggableData();
        draggablesManager.loadDraggableData();
    };

    private final Runnable stopGame = () -> {
        if (draggablesManager != null && !draggablesManager.getDraggables().values().isEmpty())
            draggablesManager.saveDraggableData();
    };

    public void updateRPC(String text, String buildtext){
        DiscordRichPresence.Builder builder = new DiscordRichPresence.Builder(buildtext);
        builder.setBigImage("gravitylogo", "");
        builder.setDetails(text);
        rpc.discordUpdatePresence(builder.build());
    }
}
