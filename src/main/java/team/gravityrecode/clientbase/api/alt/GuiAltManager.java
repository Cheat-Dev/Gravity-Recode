package team.gravityrecode.clientbase.api.alt;

import com.mojang.authlib.Agent;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import fr.litarvan.openauth.microsoft.MicrosoftAuthResult;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticationException;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticator;
import fr.litarvan.openauth.microsoft.model.response.MinecraftProfile;
import lombok.SneakyThrows;
import net.minecraft.client.gui.*;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Session;
import org.apache.commons.lang3.RandomUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import team.gravityrecode.clientbase.Client;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.net.Proxy;

public class GuiAltManager extends GuiScreen {
    public Alt selectedAlt = null;
    private int offset;
    private PasswordField password;
    private AltLoginThread thread;
    private UsernameField username;

    @SneakyThrows
    @Override
    protected void actionPerformed(GuiButton button) {
        switch (button.id) {
            case 0:
                String data = (String) Toolkit.getDefaultToolkit().getSystemClipboard()
                        .getData(DataFlavor.stringFlavor);
                String[] credentials = data.split(":");
                    this.username.setText(credentials[0]);
                    this.password.setText(credentials[1]);
                this.thread = new AltLoginThread(this.username.getText(), this.password.getText());
                this.thread.start();
                break;
            case 1:
                this.username.setText("gravity" + RandomUtils.nextInt(999, 999999));
                this.password.setText("");
                this.thread = new AltLoginThread(this.username.getText(), this.password.getText());
                this.thread.start();
                break;
            case 2:
                this.mc.displayGuiScreen(new GuiMultiplayer(this));
                break;
            case 3:
                this.mc.displayGuiScreen(null);
                break;
            case 4:
                MicrosoftAuthenticator authenticator = new MicrosoftAuthenticator();
                try {

                    MicrosoftAuthResult result = authenticator.loginWithWebview();
                    MinecraftProfile profile = result.getProfile();
                    mc.session = new Session(profile.getName(), profile.getId(), result.getAccessToken(), "microsoft");
                } catch (MicrosoftAuthenticationException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    public void drawScreen(int par1, int par2, float par3) {
        this.mc.getTextureManager().bindTexture(new ResourceLocation("gravity/gravitymainmenu.jpeg"));
        Gui.drawScaledCustomSizeModalRect(0, 0, 0, 0, width, height, width, height, width, height);
        this.username.drawTextBox();
        this.password.drawTextBox();
        FontRenderer font = mc.fontRendererObj;
        if (this.thread != null) {
            this.drawCenteredString(font, this.thread.getStatus(), width / 2, 98, -1);
        }
        this.drawCenteredString(font, "Username: " + mc.getSession().getUsername(),
                width / 2, 108, -1);
        super.drawScreen(par1, par2, par3);
    }

    @SneakyThrows
    @Override
    protected void keyTyped(char character, int key) {
        switch (character) {
            case '\t':
                if (!this.username.isFocused() && !this.password.isFocused()) {
                    this.username.setFocused(true);
                } else {
                    this.username.setFocused(this.password.isFocused());
                    this.password.setFocused(!this.username.isFocused());
                }
                break;
            case '\r':
                this.actionPerformed((GuiButton) this.buttonList.get(0));
                break;
        }
        this.username.textboxKeyTyped(character, key);
        this.password.textboxKeyTyped(character, key);
        super.keyTyped(character, key);
    }

    @Override
    public void initGui() {
        Client.INSTANCE.updateRPC("Version: 3.0", "Alt Manager!");
        this.username = new UsernameField(this.mc.fontRendererObj, width / 2 - 90, 120, 180, 20);
        this.password = new PasswordField(this.mc.fontRendererObj, width / 2 - 90, 150, 180, 20);
        this.buttonList.add(new GuiButton(0, width / 2 - 90, 180, 180, 20, "Clipboard"));
        this.buttonList.add(new GuiButton(1, width / 2 - 90, 210, 180, 20, "Offline Account"));
        this.buttonList.add(new GuiButton(2, width / 2 - 90, 240, 180, 20, "Multiplayer"));
        this.buttonList.add(new GuiButton(3, width / 2 - 90, 270, 180, 20, "Cancel"));
        this.buttonList.add(new GuiButton(4, width / 2 - 90, 300, 180, 20, "Microhard"));
        this.username.setFocused(true);
        Keyboard.enableRepeatEvents(true);

    }

    @Override
    public void updateScreen() {
        this.username.updateCursorCounter();
        this.password.updateCursorCounter();
    }

    @SneakyThrows
    @Override
    protected void mouseClicked(int x2, int y2, int button) {
        this.username.mouseClicked(x2, y2, button);
        this.password.mouseClicked(x2, y2, button);
        super.mouseClicked(x2, y2, button);
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    private Session createSession(String username, String password) {
        YggdrasilAuthenticationService service = new YggdrasilAuthenticationService(Proxy.NO_PROXY, "");
        YggdrasilUserAuthentication auth = (YggdrasilUserAuthentication) service
                .createUserAuthentication(Agent.MINECRAFT);
        auth.setUsername(username);
        auth.setPassword(password);
        try {
            auth.logIn();
            return new Session(auth.getSelectedProfile().getName(), auth.getSelectedProfile().getId().toString(),
                    auth.getAuthenticatedToken(), "mojang");
        } catch (AuthenticationException localAuthenticationException) {
            localAuthenticationException.printStackTrace();
            return null;
        }
    }

    public void prepareScissorBox(float x2, float y2, float x22, float y22) {
        ScaledResolution scale = new ScaledResolution(this.mc);
        int factor = scale.getScaleFactor();
        GL11.glScissor((int) (x2 * (float) factor), (int) (((float) scale.getScaledHeight() - y22) * (float) factor),
                (int) ((x22 - x2) * (float) factor), (int) ((y22 - y2) * (float) factor));
    }
}