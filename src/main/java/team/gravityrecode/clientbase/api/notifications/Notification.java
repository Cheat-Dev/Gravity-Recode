package team.gravityrecode.clientbase.api.notifications;

import lombok.Getter;
import lombok.Setter;
import team.gravityrecode.clientbase.impl.util.client.TimerUtil;
import team.gravityrecode.clientbase.impl.util.render.TranslationUtils;

  /*
    Sorry if code is aids, i was high as fuck when i coded this because ive been smoking weed for the past 6 hours LMAO
  */

@Getter
@Setter
public class Notification {

    private TimerUtil timer = new TimerUtil(), closing = new TimerUtil();
    private String title, text;
    private NotificationType type;
    private long duration;
    private int color;
    private TranslationUtils translate = new TranslationUtils(0.0F, 0.0F);
    private boolean active;

    public Notification(String title, String text, NotificationType type, int color) {
        this.title = title;
        this.text = text;
        this.type = type;
        this.color = color;
        active = true;
    }

    public enum NotificationType {
        /*
        This will get more functionality when someone can get images working
         */
        ALERT, ERROR
    }
}
