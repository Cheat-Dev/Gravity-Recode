package team.gravityrecode.clientbase.api.client;

import lombok.Getter;
import lombok.Setter;

@Setter@Getter
public class Event {
    private boolean isCancelled;

    public void cancelEvent(){
        isCancelled = true;
    }
}
