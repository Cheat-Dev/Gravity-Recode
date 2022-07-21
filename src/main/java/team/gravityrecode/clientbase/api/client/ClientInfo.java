package team.gravityrecode.clientbase.api.client;

import lombok.AllArgsConstructor;
import lombok.Getter;
import team.gravityrecode.clientbase.api.util.MinecraftUtil;

@Getter
@AllArgsConstructor
public class ClientInfo implements MinecraftUtil {
    private final String clientName, clientVersion, clientDevelopers;

    public String getMinecraftTitle(){
        return clientName + " " + clientVersion + " Made By: " + clientDevelopers;
    }

    public String getLoadingTitle(){
        return "Loading Gravity...";
    }
}
