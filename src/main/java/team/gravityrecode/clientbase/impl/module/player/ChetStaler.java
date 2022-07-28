package team.gravityrecode.clientbase.impl.module.player;

import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.item.ItemStack;
import team.gravityrecode.clientbase.api.eventBus.EventHandler;
import team.gravityrecode.clientbase.api.moduleBase.Module;
import team.gravityrecode.clientbase.api.moduleBase.ModuleInfo;
import team.gravityrecode.clientbase.impl.event.player.PlayerMotionEvent;
import team.gravityrecode.clientbase.impl.util.client.TimerUtil;

@ModuleInfo(moduleName = "ChetStaler", moduleCategory = Module.ModuleCategory.PLAYER)
public class ChetStaler extends Module {

    private TimerUtil timer = new TimerUtil();

    @EventHandler
    public void onUpdate(PlayerMotionEvent event) {
        if (this.mc.currentScreen instanceof GuiChest) {
            GuiChest chest = (GuiChest) this.mc.currentScreen;
            boolean titleCheck = chest.lowerChestInventory.getDisplayName().getUnformattedText().contains("Chest") ||
                    chest.lowerChestInventory.getDisplayName().getUnformattedText().contains("Contai") ||
                    chest.lowerChestInventory.getDisplayName().getUnformattedText().contains("Crate") ||
                    chest.lowerChestInventory.getDisplayName().getUnformattedText().equalsIgnoreCase("LOW");
            if (titleCheck) {
                if (this.isChestEmpty(chest)) {
                    this.mc.thePlayer.closeScreen();
                }
                for (int index = 0; index < chest.lowerChestInventory.getSizeInventory(); ++index) {
                    final ItemStack stack = chest.lowerChestInventory.getStackInSlot(index);
                    if (stack != null && timer.hasElapsed(25)) {
                        this.mc.playerController.windowClick(chest.inventorySlots.windowId, index, 0, 1, this.mc.thePlayer);
                        timer.reset();
                    }
                }
            }
        }
    }

    private boolean isChestEmpty(final GuiChest chest) {
        for (int index = 0; index <= chest.lowerChestInventory.getSizeInventory(); ++index) {
            final ItemStack stack = chest.lowerChestInventory.getStackInSlot(index);
            if (stack != null) {
                return false;
            }
        }
        return true;
    }
}
