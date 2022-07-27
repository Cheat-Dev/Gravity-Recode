package team.gravityrecode.clientbase.impl.util.client;

import net.minecraft.util.ChatComponentText;
import team.gravityrecode.clientbase.api.util.MinecraftUtil;

public class Logger implements MinecraftUtil {
    
    public static void print(Object message){
        mc.thePlayer.addChatMessage(new ChatComponentText("[Gravity] " + message));
    }
    
    public static void sayShitInChat(Object message){
        mc.thePlayer.sendChatMessage((String) message);
    }
    
    public static void printSysLog(Object message){
        System.out.println("[Gravity] " + message);
    }
    
}
