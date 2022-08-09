package team.gravityrecode.clientbase.impl.mainmenu;

import com.viaversion.viabackwards.protocol.protocol1_12_2to1_13.packets.PlayerPacket1_13;
import fr.litarvan.openauth.microsoft.MicrosoftAuthResult;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticationException;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticator;
import fr.litarvan.openauth.microsoft.model.response.MinecraftProfile;
import javazoom.jl.player.Player;
import net.minecraft.client.gui.*;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Session;
import team.gravityrecode.clientbase.Client;
import team.gravityrecode.clientbase.impl.mainmenu.changelog.Changelog;
import team.gravityrecode.clientbase.impl.util.foint.Fonts;

import java.awt.*;
import java.io.IOException;
import java.util.Comparator;
import java.util.Objects;

public class TestMenu extends GuiScreen {

    MainMenuShader mainMenuShader = new MainMenuShader();
    private Player player = null;
    private MicrosoftAuthenticator microsoftAuthenticator;

    long initTime;

    @Override
    public void initGui() {
//        if(player == null){
//            new Thread(() -> {
//                try {
//                    player = new Player(Objects.requireNonNull(this.getClass().getResourceAsStream("/assets/minecraft/pulsabo/funny.mp3")));
//                    player.play();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }).start();
//        }


        initTime = System.currentTimeMillis();
        buttonList.clear();
//        this.buttonList.add(new CustomButton(0, 5, 4, 94, 96, "Singleplayer", new ResourceLocation("pulsabo/images/singleplayer.png")));
//        this.buttonList.add(new CustomButton(1, 5,106, 94, 96, "Multiplayer", new ResourceLocation("pulsabo/images/multiplayer.png")));
//        this.buttonList.add(new CustomButton(2, 5, 208, 94, 96, "Alt Manager", new ResourceLocation("pulsabo/images/altmanager.png")));
//        this.buttonList.add(new CustomButton(3, 5, 310, 94, 97, "Settings", new ResourceLocation("pulsabo/images/settings.png")));
//        this.buttonList.add(new CustomButton(4, 5, 413, 94, 97, "Rage Quit", new ResourceLocation("pulsabo/images/exit.png")));
        this.buttonList.add(new TestButton(0, 15, 15, 95, 26, "Singleplayer", new ResourceLocation("pulsabo/images/singleplayer.png")));
        this.buttonList.add(new TestButton(1, 15,56, 95, 26, "Multiplayer", new ResourceLocation("pulsabo/images/multiplayer.png")));
        this.buttonList.add(new TestButton(2, 15, 97, 95, 26, "Alt Manager", new ResourceLocation("pulsabo/images/altmanager.png")));
        this.buttonList.add(new TestButton(3, 15, 138, 95, 26, "Settings", new ResourceLocation("pulsabo/images/settings.png")));
        this.buttonList.add(new TestButton(4, 15, 179, 95, 26, "Rage Quit", new ResourceLocation("pulsabo/images/exit.png")));
        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        Gui.drawRect(0, 0, 1920, 1080, Color.BLACK.getRGB());

        mainMenuShader.run(initTime);
//        RenderUtil.drawImage(new ResourceLocation("pulsabo/images/gravitychild.png"), 0, 0, this.width, this.height);

        Fonts.INSTANCE.getSourceSansPro().drawString("Changelog v" + Client.INSTANCE.getClientInfo().getClientVersion(),
                this.width - Fonts.INSTANCE.getSourceSansPro().getStringWidth("Changelog v" + Client.INSTANCE.getClientInfo().getClientVersion()) - 2, 2, Color.WHITE.getRGB());
        int y = 5 + Fonts.INSTANCE.getSourceSansProSmall().getHeight();
        Client.INSTANCE.getChangelogManager().getChangelogs().sort(SORT_METHOD);
        for(Changelog changelog : Client.INSTANCE.getChangelogManager().getChangelogs()) {

            Fonts.INSTANCE.getSourceSansPro().drawString(changelog.getChangeType().getChangePrefix() + changelog.getChange()
                    , width - Fonts.INSTANCE.getSourceSansPro().getStringWidth(changelog.getChangeType().getChangePrefix() + changelog.getChange()) - 2, y, Color.WHITE.getRGB());

            y += Fonts.INSTANCE.getSourceSansPro().getHeight() + 2;
        }
        Fonts.INSTANCE.getSourceSansPro().drawString("Current Account: " + mc.getSession().getUsername(), width -
                Fonts.INSTANCE.getSourceSansPro().getStringWidth("Current Account: " + mc.getSession().getUsername()) - 2,
                height - Fonts.INSTANCE.getSourceSansPro().getHeight() - 2, -1);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch(button.id){
            case 0:
                mc.displayGuiScreen(new GuiSelectWorld(this));
                break;
            case 1:
                mc.displayGuiScreen(new GuiMultiplayer(this));
                break;
            case 2:
                try {
                    microsoftAuthenticator = new MicrosoftAuthenticator();
                    MicrosoftAuthResult result = microsoftAuthenticator.loginWithWebview();
                    MinecraftProfile profile = result.getProfile();
                    mc.session = new Session(profile.getName(), profile.getId(), result.getAccessToken(), "microsoft");
                } catch (MicrosoftAuthenticationException e) {
                    e.printStackTrace();
                }
                break;
            case 3:
                mc.displayGuiScreen(new GuiOptions(this, mc.gameSettings));
                break;
            case 4:
                mc.shutdown();
                break;
        }
    }

    private final Comparator<Object> SORT_METHOD = Comparator.comparingDouble(m -> {
        Changelog changelog = (Changelog) m;
        return Fonts.INSTANCE.getSourceSansPro().getStringWidth(changelog.getChangeType() + changelog.getChange());
    }).reversed();

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        if(player != null){
            player.close();
            player = null;
        }
    }
}
