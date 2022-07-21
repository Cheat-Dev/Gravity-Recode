package team.gravityrecode.clientbase.impl.mainmenu.changelog;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.util.EnumChatFormatting;

import java.awt.*;

@Getter
@AllArgsConstructor
public class Changelog {
    private final String change;
    private final ChangeType changeType;


    @AllArgsConstructor
    @Getter
    public enum ChangeType{
        ADDED(EnumChatFormatting.GREEN),
        REMOVED(EnumChatFormatting.RED),
        FIXED(EnumChatFormatting.GRAY);

        private final EnumChatFormatting color;

        public String getChangePrefix(){
            switch(this){
                case ADDED:
                    return color + "+Added" + EnumChatFormatting.RESET + " ";
                case FIXED:
                    return color + "*Fixed" + EnumChatFormatting.RESET + " ";
                case REMOVED:
                    return color + "-Removed" + EnumChatFormatting.RESET + " ";
            }
            return null;
        }
    }
}
