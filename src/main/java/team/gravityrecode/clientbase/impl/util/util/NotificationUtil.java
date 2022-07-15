package team.gravityrecode.clientbase.impl.util.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.UtilityClass;

import java.awt.*;

@UtilityClass
public class NotificationUtil {

    private long funny;

    public void sendNotification(MessageType messageType, String title, String content){
        try {
            funny = System.currentTimeMillis();
            SystemTray tray = SystemTray.getSystemTray();
            Image image = Toolkit.getDefaultToolkit().createImage("icon.png");
            TrayIcon trayIcon = new TrayIcon(image, "Tray Demo");
            trayIcon.setImageAutoSize(true);
            trayIcon.setToolTip("System tray icon demo");
            tray.add(trayIcon);
            trayIcon.displayMessage(title, content, messageType.getMessageType());

          if(System.currentTimeMillis() - funny >= 5000)
                tray.remove(trayIcon);

        }catch (AWTException e){
            e.printStackTrace();
        }
    }

    @AllArgsConstructor@Getter
    public enum MessageType{
        NONE(TrayIcon.MessageType.NONE),
        WARNING(TrayIcon.MessageType.WARNING),
        INFO(TrayIcon.MessageType.INFO),
        ERROR(TrayIcon.MessageType.ERROR);

        private final TrayIcon.MessageType messageType;
    }
}
