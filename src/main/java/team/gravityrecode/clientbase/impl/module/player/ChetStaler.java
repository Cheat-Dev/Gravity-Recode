package team.gravityrecode.clientbase.impl.module.player;

import lombok.Getter;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemGlassBottle;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import org.apache.commons.lang3.RandomUtils;
import team.gravityrecode.clientbase.Client;
import team.gravityrecode.clientbase.api.eventBus.EventHandler;
import team.gravityrecode.clientbase.api.moduleBase.Module;
import team.gravityrecode.clientbase.api.moduleBase.ModuleInfo;
import team.gravityrecode.clientbase.api.notification.Notification;
import team.gravityrecode.clientbase.impl.event.player.PlayerMotionEvent;
import team.gravityrecode.clientbase.impl.property.BooleanSetting;
import team.gravityrecode.clientbase.impl.property.NumberSetting;
import team.gravityrecode.clientbase.impl.util.client.TimerUtil;

@Getter
@ModuleInfo(moduleName = "ChetStaler", moduleCategory = Module.ModuleCategory.PLAYER)
public class ChetStaler extends Module {
        private final TimerUtil timer = new TimerUtil();
        private final NumberSetting delay = new NumberSetting(this, "Delay", 80, 0, 1000, 1);
        private final NumberSetting randomMax = new NumberSetting(this, "Random Max", 50, 0, 1000, 1);
        private final NumberSetting randomMin = new NumberSetting(this, "Random Min", 0, 0, 1000, 1);
        private final BooleanSetting random = new BooleanSetting(this, "Randomization", false);
        private final BooleanSetting chestCheck = new BooleanSetting(this, "ChestCheck", true);
        private final BooleanSetting silent = new BooleanSetting(this, "Silent", false);
        private final BooleanSetting aim = new BooleanSetting(this, "Aim", true);


        @EventHandler
        public void onUpdate(PlayerMotionEvent event) {
            if(!event.isUpdate()) return;
            if (mc.currentScreen instanceof GuiChest) {
                GuiChest chest = (GuiChest) mc.currentScreen;

                if (chestCheck.getValue() && !isInvalidChest(chest)) {
                    return;
                }

                boolean full = true;
                ItemStack[] arrayOfItemStack;
                int j = (arrayOfItemStack = mc.thePlayer.inventory.mainInventory).length;
                for (int i = 0; i < j; i++) {
                    ItemStack item = arrayOfItemStack[i];
                    if (item == null) {
                        full = false;
                        break;
                    }
                }


                boolean containsItems = false;
                if (!full) {
                    for (int index = 0; index < chest.lowerChestInventory.getSizeInventory(); index++) {
                        ItemStack stack = chest.lowerChestInventory.getStackInSlot(index);
                        if (stack != null && !isBad(stack)) {
                            containsItems = true;
                            break;
                        }
                    }
                    if (containsItems) {
                        for (int index = 0; index < chest.lowerChestInventory.getSizeInventory(); index++) {
                            ItemStack stack = chest.lowerChestInventory.getStackInSlot(index);
                            if (stack != null && timer.hasElapsed(delay.getValue().intValue() +
                                    (random.getValue() ? RandomUtils.nextInt(randomMin.getValue().intValue(), randomMax.getValue().intValue()) : 0)) && !isBad(stack)) {
                                mc.playerController.windowClick(chest.inventorySlots.windowId, index, 0, 1, mc.thePlayer);
                                timer.reset();
                            }
                        }
                    } else {
                        mc.thePlayer.closeScreen();

                        if (silent.getValue()) {
                            Client.INSTANCE.getNotificationManager().addNotification(Notification.Type.SUCCESS, "Finished stealing chest.", 1000);
                        }
                    }
                }
            }
        };

        private boolean isBad(ItemStack item) {
            return item != null &&
                    ((item.getItem().getUnlocalizedName().contains("tnt")) ||
                            (item.getItem().getUnlocalizedName().contains("stick")) ||
                            (item.getItem().getUnlocalizedName().contains("egg") && !item.getItem().getUnlocalizedName().contains("leg")) ||
                            (item.getItem().getUnlocalizedName().contains("string")) ||
                            (item.getItem().getUnlocalizedName().contains("flint")) ||
                            (item.getItem().getUnlocalizedName().contains("compass")) ||
                            (item.getItem().getUnlocalizedName().contains("feather")) ||
                            (item.getItem().getUnlocalizedName().contains("snow")) ||
                            (item.getItem().getUnlocalizedName().contains("fish")) ||
                            (item.getItem().getUnlocalizedName().contains("enchant")) ||
                            (item.getItem().getUnlocalizedName().contains("exp")) ||
                            (item.getItem().getUnlocalizedName().contains("shears")) ||
                            (item.getItem().getUnlocalizedName().contains("anvil")) ||
                            (item.getItem().getUnlocalizedName().contains("torch")) ||
                            (item.getItem().getUnlocalizedName().contains("seeds")) ||
                            (item.getItem().getUnlocalizedName().contains("leather")) ||
                            ((item.getItem() instanceof ItemGlassBottle)) ||
                            (item.getItem().getUnlocalizedName().contains("piston")) ||
                            ((item.getItem().getUnlocalizedName().contains("potion"))
                                    && (isBadPotion(item))));
        }

        private boolean isBadPotion(ItemStack stack) {
            if (stack != null && stack.getItem() instanceof ItemPotion) {
                final ItemPotion potion = (ItemPotion) stack.getItem();
                if (ItemPotion.isSplash(stack.getItemDamage())) {
                    for (final Object o : potion.getEffects(stack)) {
                        final PotionEffect effect = (PotionEffect) o;
                        if (effect.getPotionID() == Potion.poison.getId() || effect.getPotionID() == Potion.harm.getId() || effect.getPotionID() == Potion.moveSlowdown.getId() || effect.getPotionID() == Potion.weakness.getId()) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }

        public boolean isInvalidChest(GuiScreen screen) {
            GuiChest chest = (GuiChest) screen;
            return chest.lowerChestInventory.getName().contains(I18n.format("container.chest"))/*Pattern.compile(Pattern.quote(chest.lowerChestInventory.getName()), Pattern.CASE_INSENSITIVE).matcher("chest").find()*/;
        }
}
