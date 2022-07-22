package team.gravityrecode.clientbase.impl.module.player;

import lombok.AllArgsConstructor;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.item.*;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import team.gravityrecode.clientbase.Client;
import team.gravityrecode.clientbase.api.eventBus.EventHandler;
import team.gravityrecode.clientbase.api.moduleBase.Module;
import team.gravityrecode.clientbase.api.moduleBase.ModuleInfo;
import team.gravityrecode.clientbase.impl.event.networking.PacketEvent;
import team.gravityrecode.clientbase.impl.event.player.PlayerMotionEvent;
import team.gravityrecode.clientbase.impl.event.player.WindowClickEvent;
import team.gravityrecode.clientbase.impl.event.player.windowclick.WindowClickRequest;
import team.gravityrecode.clientbase.impl.module.combat.Killaura;
import team.gravityrecode.clientbase.impl.property.BooleanSetting;
import team.gravityrecode.clientbase.impl.property.EnumSetting;
import team.gravityrecode.clientbase.impl.property.NumberSetting;
import team.gravityrecode.clientbase.impl.property.interfaces.INameable;
import team.gravityrecode.clientbase.impl.util.util.player.InventoryUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@ModuleInfo(moduleName = "InventoryManager", moduleCategory = Module.ModuleCategory.PLAYER)
public final class InventoryManager extends Module {

    private final EnumSetting<Mode> modeProperty = new EnumSetting<>(this, "Mode", Mode.values());
    private final NumberSetting delayProperty = new NumberSetting(this, "Delay", 150, 0, 500, 50);
    private final BooleanSetting dropItemsProperty = new BooleanSetting(this, "Drop Items", true);
    private final BooleanSetting sortItemsProperty = new BooleanSetting(this, "Sort Items", true);
    private final BooleanSetting autoArmorProperty = new BooleanSetting(this, "Auto Armor", true);
    private final BooleanSetting ignoreItemsWithCustomName = new BooleanSetting(this, "Ignore Custom Name", true);

    private final int[] bestArmorPieces = new int[4];
    private final List<Integer> trash = new ArrayList<>();
    private final int[] bestToolSlots = new int[3];
    private final List<Integer> gappleStackSlots = new ArrayList<>();
    private int bestSwordSlot;
    private int bestBowSlot;

    private final List<WindowClickRequest> clickRequests = new ArrayList<>();

    private boolean serverOpen;
    private boolean clientOpen;

    private int ticksSinceLastClick;

    private boolean nextTickCloseInventory;

    private Killaura aura;

    private boolean isSpoof() {
        return this.modeProperty.getValue() == Mode.SPOOF;
    }

    @EventHandler
    public void a(PacketEvent event) {
        final Packet<?> packet = event.getPacket();

        switch(event.getPacketType()){
            case SENDING:{
                if (packet instanceof C16PacketClientStatus) {
                    final C16PacketClientStatus clientStatus = (C16PacketClientStatus) packet;

                    if (clientStatus.getStatus() == C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT) {
                        this.clientOpen = true;
                        this.serverOpen = true;
                    }
                } else if (packet instanceof C0DPacketCloseWindow) {
                    final C0DPacketCloseWindow packetCloseWindow = (C0DPacketCloseWindow) packet;

                    if (packetCloseWindow.getWindowId() == this.mc.thePlayer.inventoryContainer.windowId) {
                        this.clientOpen = false;
                        this.serverOpen = false;
                    }
                }
                break;
            }
            case RECEIVING:{
                if (packet instanceof S2DPacketOpenWindow) {
                    this.clientOpen = false;
                    this.serverOpen = false;
                }
                break;
            }
        }
    };

    @EventHandler
    public void a(WindowClickEvent event) {
        this.ticksSinceLastClick = 0;
    };

    private boolean dropItem(final List<Integer> listOfSlots) {
        if (this.dropItemsProperty.getValue()) {
            if (!listOfSlots.isEmpty()) {
                int slot = listOfSlots.remove(0);
                InventoryUtils.windowClick(this.mc, slot, 1, InventoryUtils.ClickType.DROP_ITEM);
                return true;
            }
        }
        return false;
    }

    public List<WindowClickRequest> getClickRequests() {
        return clickRequests;
    }

    @EventHandler
    public void a(PlayerMotionEvent event) {
        if (event.isPre()) {
            this.ticksSinceLastClick++;
            // Logger.print("call");

            if (this.ticksSinceLastClick < Math.floor(this.delayProperty.getValue() / 3)) return;

            if (aura.isEnabled() && (this.aura.getTarget() != null || this.aura.isEntityNearby())) {
                if (this.nextTickCloseInventory) {
                    this.nextTickCloseInventory = false;
                }

                this.close();
                return;
            }

            if (this.clientOpen || (this.mc.currentScreen == null && this.modeProperty.getValue() != Mode.WHILE_OPEN)) {
                this.clear();

                for (int slot = InventoryUtils.INCLUDE_ARMOR_BEGIN; slot < InventoryUtils.END; slot++) {
                    final ItemStack stack = this.mc.thePlayer.inventoryContainer.getSlot(slot).getStack();

                    if (stack != null) {
                        if (this.ignoreItemsWithCustomName.getValue() &&
                                stack.hasDisplayName())
                            continue;

                        if (stack.getItem() instanceof ItemSword && InventoryUtils.isBestSword(this.mc.thePlayer, stack)) {
                            this.bestSwordSlot = slot;
                        }
                        else if (stack.getItem() instanceof ItemTool && InventoryUtils.isBestTool(this.mc.thePlayer, stack)) {
                            final int toolType = InventoryUtils.getToolType(stack);
                            if (toolType != -1 && slot != this.bestToolSlots[toolType])
                                this.bestToolSlots[toolType] = slot;
                        }
                        else if (stack.getItem() instanceof ItemArmor && InventoryUtils.isBestArmor(this.mc.thePlayer, stack)) {
                            final ItemArmor armor = (ItemArmor) stack.getItem();

                            final int pieceSlot = this.bestArmorPieces[armor.armorType];

                            if (pieceSlot == -1 || slot != pieceSlot)
                                this.bestArmorPieces[armor.armorType] = slot;
                        }
                        else if (stack.getItem() instanceof ItemBow && InventoryUtils.isBestBow(this.mc.thePlayer, stack)) {
                            if (slot != this.bestBowSlot)
                                this.bestBowSlot = slot;
                        }
                        else if (stack.getItem() instanceof ItemAppleGold) {
                            this.gappleStackSlots.add(slot);
                        }
                        else if (!this.trash.contains(slot) && !isValidStack(stack)) {
                            this.trash.add(slot);
                        }
                    }
                }

                final boolean busy = (!this.trash.isEmpty() && this.dropItemsProperty.getValue()) || this.equipArmor(false) || this.sortItems(false) || !this.clickRequests.isEmpty();

                if (!busy) {
                    if (this.nextTickCloseInventory) {
                        this.close();
                        this.nextTickCloseInventory = false;
                    } else {
                        this.nextTickCloseInventory = true;
                    }
                    return;
                } else {
                    boolean waitUntilNextTick = !this.serverOpen;

                    this.open();

                    if (this.nextTickCloseInventory)
                        this.nextTickCloseInventory = false;

                    if (waitUntilNextTick) return;
                }

                if (!this.clickRequests.isEmpty()) {
                    final WindowClickRequest request = this.clickRequests.remove(0);
                    request.performRequest();
                    request.onCompleted();
                    return;
                }

                if (this.equipArmor(true)) return;
                if (this.dropItem(this.trash)) return;
                this.sortItems(true);
            }
        }
    };

    private boolean sortItems(final boolean moveItems) {
        if (this.sortItemsProperty.getValue()) {
            if (this.bestSwordSlot != -1) {
                if (this.bestSwordSlot != 36) {
                    if (moveItems) {
                        this.putItemInSlot(36, this.bestSwordSlot);
                        this.bestSwordSlot = 36;
                    }

                    return true;
                }
            }

            if (this.bestBowSlot != -1) {
                if (this.bestBowSlot != 38) {
                    if (moveItems) {
                        this.putItemInSlot(38, this.bestBowSlot);
                        this.bestBowSlot = 38;
                    }
                    return true;
                }
            }

            if (!this.gappleStackSlots.isEmpty()) {
                this.gappleStackSlots.sort(Comparator.comparingInt(slot -> this.mc.thePlayer.inventoryContainer.getSlot(slot).getStack().stackSize));

                final int bestGappleSlot = this.gappleStackSlots.get(0);

                if (bestGappleSlot != 37) {
                    if (moveItems) {
                        this.putItemInSlot(37, bestGappleSlot);
                        this.gappleStackSlots.set(0, 37);
                    }
                    return true;
                }
            }

            final int[] toolSlots = {39, 40, 41};

            for (final int toolSlot : this.bestToolSlots) {
                if (toolSlot != -1) {
                    final int type = InventoryUtils.getToolType(this.mc.thePlayer.inventoryContainer.getSlot(toolSlot).getStack());

                    if (type != -1) {
                        if (toolSlot != toolSlots[type]) {
                            if (moveItems) {
                                this.putToolsInSlot(type, toolSlots);
                            }
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    private boolean equipArmor(boolean moveItems) {
        if (this.autoArmorProperty.getValue()) {
            for (int i = 0; i < this.bestArmorPieces.length; i++) {
                final int piece = this.bestArmorPieces[i];

                if (piece != -1) {
                    int armorPieceSlot = i + 5;
                    final ItemStack stack = this.mc.thePlayer.inventoryContainer.getSlot(armorPieceSlot).getStack();
                    if (stack != null)
                        continue;

                    if (moveItems)
                        InventoryUtils.windowClick(this.mc, piece, 0, InventoryUtils.ClickType.SHIFT_CLICK);

                    return true;
                }
            }
        }

        return false;
    }

    private void putItemInSlot(final int slot, final int slotIn) {
        InventoryUtils.windowClick(this.mc, slotIn,
                slot - 36,
                InventoryUtils.ClickType.SWAP_WITH_HOT_BAR_SLOT);
    }

    private void putToolsInSlot(final int tool, final int[] toolSlots) {
        final int toolSlot = toolSlots[tool];

        InventoryUtils.windowClick(this.mc, this.bestToolSlots[tool],
                toolSlot - 36,
                InventoryUtils.ClickType.SWAP_WITH_HOT_BAR_SLOT);
        this.bestToolSlots[tool] = toolSlot;
    }

    private static boolean isValidStack(final ItemStack stack) {
        if (stack.getItem() instanceof ItemBlock && InventoryUtils.isStackValidToPlace(stack)) {
            return true;
        } else if (stack.getItem() instanceof ItemPotion && InventoryUtils.isBuffPotion(stack)) {
            return true;
        } else if (stack.getItem() instanceof ItemFood && InventoryUtils.isGoodFood(stack)) {
            return true;
        } else {
            return InventoryUtils.isGoodItem(stack.getItem());
        }
    }

    @Override
    public void onEnable() {
        if (this.aura == null) {
            this.aura = Client.INSTANCE.getModuleManager().getModule(Killaura.class);
        }

        this.ticksSinceLastClick = 0;

        this.clientOpen = this.mc.currentScreen instanceof GuiInventory;
        this.serverOpen = this.clientOpen;
        super.onEnable();
    }

    @Override
    public void onDisable() {
        this.close();
        this.clear();
        this.clickRequests.clear();
        super.onDisable();
    }

    private void open() {
        if (!this.clientOpen && !this.serverOpen) {
            this.mc.thePlayer.sendQueue.addToSendQueue(new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));
            this.serverOpen = true;
        }
    }

    private void close() {
        if (!this.clientOpen && this.serverOpen) {
            this.mc.thePlayer.sendQueue.addToSendQueue(new C0DPacketCloseWindow(this.mc.thePlayer.inventoryContainer.windowId));
            this.serverOpen = false;
        }
    }

    private void clear() {
        this.trash.clear();
        this.bestBowSlot = -1;
        this.bestSwordSlot = -1;
        this.gappleStackSlots.clear();
        Arrays.fill(this.bestArmorPieces, -1);
        Arrays.fill(this.bestToolSlots, -1);
    }


    @AllArgsConstructor
    private enum Mode implements INameable {
        WHILE_OPEN("In Inventory"),
        SPOOF("Spoof");

        private final String name;

        @Override
        public String getName() {
            return this.name;
        }
    }
}
