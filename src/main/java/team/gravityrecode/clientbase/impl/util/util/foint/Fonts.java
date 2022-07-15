package team.gravityrecode.clientbase.impl.util.util.foint;

import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import team.gravityrecode.clientbase.api.util.MinecraftUtil;

import java.awt.*;

@Getter
public enum Fonts implements MinecraftUtil {
    INSTANCE;

    private MCFontRenderer sourceSansPro, ubuntu_light;

    public void initFonts(){
        try{
            sourceSansPro = new MCFontRenderer(fontFromTTF("SourceSansPro-Regular.ttf", 18), true, true);
            ubuntu_light = new MCFontRenderer(fontFromTTF("Ubuntu-Light.ttf", 30), true, true);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }


    private Font fontFromTTF(String fontName, float fontSize) {
        System.out.println("Loading font: " + fontName + " with size: " + fontSize + " and type: " + 0);
        Font output = null;
        try {
            output = Font.createFont(0, mc.getResourceManager().getResource(new ResourceLocation("pulsabo/fonts/" + fontName)).getInputStream());
            output = output.deriveFont(fontSize);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output;
    }

}