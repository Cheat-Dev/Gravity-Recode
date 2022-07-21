package team.gravityrecode.clientbase.impl.mainmenu;

import net.minecraft.client.Minecraft;
import team.gravityrecode.clientbase.impl.util.util.render.secondary.RuntimeShader;

public class MainMenuShader {
    RuntimeShader runtimeShader = new RuntimeShader("mainMenu.frag");

    public void run(long initTime){
        runtimeShader.init();
        setUniforms(initTime);
        RuntimeShader.drawQuads();
        runtimeShader.unload();
    }

    public void setUniforms(long initTime){
        runtimeShader.setUniformf("time", ((System.currentTimeMillis() - initTime) / 1000f));
        runtimeShader.setUniformf("resolution", Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
    }
}
