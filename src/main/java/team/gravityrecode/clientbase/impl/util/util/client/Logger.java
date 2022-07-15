package team.gravityrecode.clientbase.impl.util.util.client;

import net.minecraft.util.ChatComponentText;
import team.gravityrecode.clientbase.api.util.MinecraftUtil;

public class Logger implements MinecraftUtil {
    
    public static void print(String message){
        mc.thePlayer.addChatMessage(new ChatComponentText("[Gravity] " + message));
    }
    
    public static void sayShitInChat(String message){
        mc.thePlayer.sendChatMessage(message);
    }
    
    public static void printSysLog(String message){
        System.out.println("[Gravity] " + message);
    }
    
}
