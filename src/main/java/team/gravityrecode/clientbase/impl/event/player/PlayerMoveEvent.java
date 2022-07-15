package team.gravityrecode.clientbase.impl.event.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import team.gravityrecode.clientbase.api.client.Event;

@Getter
@Setter
@AllArgsConstructor
public class PlayerMoveEvent extends Event {

    private double x, y, z;
}
