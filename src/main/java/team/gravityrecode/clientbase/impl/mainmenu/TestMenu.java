package team.gravityrecode.clientbase.impl.mainmenu;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.util.ResourceLocation;
import team.gravityrecode.clientbase.Client;
import team.gravityrecode.clientbase.api.alt.GuiAltManager;
import team.gravityrecode.clientbase.api.moduleBase.Module;
import team.gravityrecode.clientbase.impl.mainmenu.changelog.Changelog;
import team.gravityrecode.clientbase.impl.util.util.foint.Fonts;
import team.gravityrecode.clientbase.impl.util.util.render.RenderUtil;

import java.awt.*;
import java.io.IOException;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicInteger;

public class TestMenu extends GuiScreen {

    long initTime;

    @Override
    public void initGui() {
        initTime = System.currentTimeMillis();
        buttonList.clear();
        this.buttonList.add(new CustomButton(0, 5, 5, 100, 100, "Singleplayer", new ResourceLocation("pulsabo/images/sp.png")));
        this.buttonList.add(new CustomButton(1, 5,114, 100, 100, "Multiplayer", new ResourceLocation("pulsabo/images/mp.png")));
        this.buttonList.add(new CustomButton(2, 5, 223, 100, 100, "Alt Manager", new ResourceLocation("pulsabo/images/key.png")));
        this.buttonList.add(new CustomButton(3, 5, 332, 100, 100, "Settings", new ResourceLocation("pulsabo/images/settings.png")));
        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        Gui.drawRect(0, 0, 1920, 1080, Color.BLACK.getRGB());

        RenderUtil.drawImage(new ResourceLocation("pulsabo/images/gravitychild.png"), 0, 0, this.width, this.height);

        Fonts.INSTANCE.getSourceSansPro().drawString("Changelog v" + Client.INSTANCE.getClientInfo().getClientVersion(),
                this.width - Fonts.INSTANCE.getSourceSansPro().getStringWidth("Changelog v" + Client.INSTANCE.getClientInfo().getClientVersion()) - 2, 2, Color.WHITE.getRGB());
        int y = 5 + Fonts.INSTANCE.getSourceSansProSmall().getHeight();
        Client.INSTANCE.getChangelogManager().getChangelogs().sort(SORT_METHOD);
        for(Changelog changelog : Client.INSTANCE.getChangelogManager().getChangelogs()) {

            Fonts.INSTANCE.getSourceSansPro().drawString(changelog.getChangeType().getChangePrefix() + changelog.getChange()
                    , width - Fonts.INSTANCE.getSourceSansPro().getStringWidth(changelog.getChangeType().getChangePrefix() + changelog.getChange()) - 2, y, Color.WHITE.getRGB());

            y += Fonts.INSTANCE.getSourceSansPro().getHeight() + 2;
        }

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
                mc.displayGuiScreen(new GuiAltManager());
                break;
            case 3:
                mc.displayGuiScreen(new GuiOptions(this, mc.gameSettings));
                break;
        }
    }

    private final Comparator<Object> SORT_METHOD = Comparator.comparingDouble(m -> {
        Changelog changelog = (Changelog) m;
        return Fonts.INSTANCE.getSourceSansPro().getStringWidth(changelog.getChangeType() + changelog.getChange());
    }).reversed();
}
