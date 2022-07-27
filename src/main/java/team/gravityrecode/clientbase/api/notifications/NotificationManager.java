package team.gravityrecode.clientbase.api.notifications;

import lombok.Getter;
import team.gravityrecode.clientbase.impl.event.render.Render2DEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class NotificationManager {

    /*
    Sorry if code is aids, i was high as fuck when i coded this because ive been smoking weed for the past 6 hours LMAO
     */

    @Getter
    private List<Notification> notificationList = new ArrayList<>();

    public void addNotification(Notification notification) {
        notificationList.add(notification);
    }

    public void removeNotification(int number){
        notificationList.remove(number);
    }
}
