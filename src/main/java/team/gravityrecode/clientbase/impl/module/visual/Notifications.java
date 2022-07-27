package team.gravityrecode.clientbase.impl.module.visual;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import team.gravityrecode.clientbase.Client;
import team.gravityrecode.clientbase.api.eventBus.EventHandler;
import team.gravityrecode.clientbase.api.moduleBase.Module;
import team.gravityrecode.clientbase.api.moduleBase.ModuleInfo;
import team.gravityrecode.clientbase.api.notifications.Notification;
import team.gravityrecode.clientbase.impl.event.render.Render2DEvent;
import team.gravityrecode.clientbase.impl.util.foint.Fonts;
import team.gravityrecode.clientbase.impl.util.render.TranslationUtils;
import team.gravityrecode.clientbase.impl.util.render.secondary.RenderUtils;

import java.awt.*;

/*
    Sorry if code is aids, i was high as fuck when i coded this because ive been smoking weed for the past 6 hours LMAO
  */

@ModuleInfo(moduleName = "Notifications", moduleCategory = Module.ModuleCategory.VISUAL)
public class Notifications extends Module {

    @EventHandler
    public void onRender2D(Render2DEvent event) {
        ScaledResolution scaledResolution = event.getScaledResolution();
        float y = 0;
        float height = 27.5f;
        for (Notification notification : Client.INSTANCE.getNotificationManager().getNotificationList()) {
            if (notification.getTimer().hasElapsed(4000)) {
                Client.INSTANCE.getNotificationManager().removeNotification(0);
                notification.getTimer().reset();
            }
            if (notification.getClosing().hasElapsed(3750)) {
                notification.setActive(false);
                notification.getClosing().reset();
            }
            float yMax = 27;
            TranslationUtils translate = notification.getTranslate();
            float translationFactor = 14.4F / mc.getDebugFPS();
            double translateX = translate.getX();
            float xMax = 110;
            if (notification.isActive())
                translate.interpolate(xMax, y, translationFactor);
            if (!notification.isActive())
                translate.interpolate(-xMax, y, translationFactor);
            Gui.drawRect(0, 0, 0, 0, 0);
            RenderUtils.drawBorderedRect((float) (scaledResolution.getScaledWidth() - translateX), (y + (scaledResolution.getScaledHeight() - yMax)) - 13,
                    scaledResolution.getScaledWidth() + 1, ((y) + (scaledResolution.getScaledHeight() + yMax - height)) - 13,
                    1, new Color(10, 10, 10, 155).getRGB(), notification.getColor());
            Fonts.INSTANCE.getUbuntu_light_small().drawString(notification.getTitle(), scaledResolution.getScaledWidth() - translateX + 2,
                    y + (scaledResolution.getScaledHeight() - yMax + 4.5) - 13, notification.getColor());
            Fonts.INSTANCE.getUbuntu_light_small().drawString(notification.getText(), scaledResolution.getScaledWidth() - translateX + 2,
                    y + (scaledResolution.getScaledHeight() - yMax + 16.5) - 13, -1);
            y -= height;
        }
    }
}
