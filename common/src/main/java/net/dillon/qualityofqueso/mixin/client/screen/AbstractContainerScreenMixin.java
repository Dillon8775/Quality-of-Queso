package net.dillon.qualityofqueso.mixin.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.dillon.qualityofqueso.instance.*;
import net.dillon.qualityofqueso.instance.context.ManagementButtons;
import net.dillon.qualityofqueso.instance.context.SearchFields;
import net.dillon.qualityofqueso.instance.management.ClickSlotInstance;
import net.dillon.qualityofqueso.instance.management.ExtractingInstance;
import net.dillon.qualityofqueso.instance.management.ManagementInstance;
import net.dillon.qualityofqueso.mixin.client.accessor.AbstractContainerScreenAccessor;
import net.dillon.qualityofqueso.util.SearchSyncHelper;
import net.dillon.qualityofqueso.widget.layout.WidgetLayout;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static net.dillon.qualityofqueso.helper.ModHelper.modEnabled;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin<T extends AbstractContainerMenu> extends Screen implements MenuAccess<T>, QuesoScreen, WidgetHandler {
    @Shadow @Final protected T menu;
    @Shadow @Nullable protected Slot hoveredSlot;

    @Unique private final AbstractContainerScreen<?> screen = (AbstractContainerScreen<?>) (Object) this;
    @Unique private EditBox containerSearchField, inventorySearchField;
    @Unique private final ManagementButtons managementButtons = new ManagementButtons();
    @Unique private Container container;
    @Unique private WidgetLayout widgetLayout;
    @Unique private final Set<Integer> excludedSlots = new HashSet<>();
    @Unique private boolean excludedAll = false;
    @Unique private boolean disableMoveMatchingItemsOnClose = false;
    @Unique private int lastLockedSlotIndex = -1;
    @Unique private int lockDragAction = 0;
    @Unique private boolean canMoveOne = false;

    public AbstractContainerScreenMixin(Component title) {
        super(title);
    }

    @Override
    public void tick() {
        super.tick();
        if (modEnabled(this.minecraft)) {
            SearchSyncHelper.tick(this.containerSearchField != null ? this.containerSearchField : this.inventorySearchField);
        }
    }

    @Override
    public boolean charTyped(char ch, int scancode) {
        CharTypedInstance charTypedInstance = new CharTypedInstance(this.screen);
        boolean result = charTypedInstance.handleCharTyped(ch, scancode, () -> super.charTyped(ch, scancode));
        if (modEnabled(this.minecraft) && result) {
            SearchSyncHelper.updateExternal(this.getSearchFields().searchText());
        }
        return result;
    }

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void handleKeyPressing(int keycode, int scancode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (!modEnabled(this.minecraft)) {
            return;
        }

        KeyPressInstance keyPressInstance = new KeyPressInstance((QuesoScreen) this.screen);
        keyPressInstance.handleKeyPressing(keycode, scancode, modifiers, cir);

        if (!cir.isCancelled()) {
            EditBox active = this.containerSearchField != null ? this.containerSearchField : this.inventorySearchField;
            if (active != null && active.isFocused()) {
                SearchSyncHelper.updateExternal(active.getValue());
            }
        }
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void initialize(CallbackInfo ci) {
        if (!modEnabled(this.minecraft)) return;
        ScreenInitInstance screenInitInstance = new ScreenInitInstance((QuesoScreen) this.screen);
        screenInitInstance.initializeContainer();
        screenInitInstance.handleTrackedContainers();
        screenInitInstance.initializeSearchFields();
        screenInitInstance.setCurrentContainer();
        screenInitInstance.readdExcludedSlots();
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void renderAndInitializeWidgets(GuiGraphics graphics, int mouseX, int mouseY, float a, CallbackInfo ci) {
        if (!modEnabled(this.minecraft)) return;
        ExtractingInstance extractingInstance = new ExtractingInstance((QuesoScreen) this.screen);
        extractingInstance.clearWidgets();
        extractingInstance.extractSearchFields(graphics, mouseX, mouseY, a);
        extractingInstance.extractButtons(graphics, mouseX, mouseY, a);
        extractingInstance.extractLockingUnlockingSlots(graphics, mouseX, mouseY);
        RenderSystem.enableDepthTest();
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;renderLabels(Lnet/minecraft/client/gui/GuiGraphics;II)V ", shift = At.Shift.AFTER))
    private void grayOutAndRenderLockTexture(GuiGraphics graphics, int mouseX, int mouseY, float a, CallbackInfo ci) {
        if (!modEnabled(this.minecraft)) return;
        ExtractingInstance extractingInstance = new ExtractingInstance((QuesoScreen) this.screen);
        extractingInstance.grayoutSlotsAndExtractLockedIcon(graphics, mouseX, mouseY);
    }

    @Inject(method = "renderSlot", at = @At("HEAD"))
    private void renderLockedSlotTintPerSlot(GuiGraphics graphics, Slot slot, CallbackInfo ci) {
        if (!modEnabled(this.minecraft)) return;
        ExtractingInstance extractingInstance = new ExtractingInstance((QuesoScreen) this.screen);
        extractingInstance.renderLockedSlotOverlayAtSlot(graphics, slot);
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void renderLockedSlotIconsAndPreserveCursorLayer(GuiGraphics graphics, int mouseX, int mouseY, float a, CallbackInfo ci) {
        if (!modEnabled(this.minecraft)) return;
        ExtractingInstance extractingInstance = new ExtractingInstance((QuesoScreen) this.screen);
        extractingInstance.extractLockedSlotIcon(graphics);
        ItemStack carried = this.menu.getCarried();
        if (!carried.isEmpty()) {
            graphics.pose().pushPose();
            graphics.pose().translate(0.0F, 0.0F, 600.0F);
            ((AbstractContainerScreenAccessor) this.screen).invokeRenderFloatingItem(graphics, carried, mouseX - 8, mouseY - 8, null);
            graphics.pose().popPose();
        }
    }

    @Inject(method = "renderTooltip", at = @At("HEAD"), cancellable = true)
    private void modifyExistingAndNewTooltips(GuiGraphics graphics, int mouseX, int mouseY, CallbackInfo ci) {
        if (!modEnabled(this.minecraft)) return;
        ModTooltipInstance modTooltips = new ModTooltipInstance((QuesoScreen) this.screen);
        modTooltips.displaySingleMovingTooltips(graphics, this.font, mouseX, mouseY, ci);
        modTooltips.displayEnchantmentHelperTooltips(graphics, this.font, mouseX, mouseY, ci);
        modTooltips.displayTagsOnItems(graphics, this.font, mouseX, mouseY, ci);
    }

    @Inject(method = "hasClickedOutside", at = @At("HEAD"), cancellable = true)
    private void handleHasClickedOutside(double mx, double my, int xo, int yo, int idk, CallbackInfoReturnable<Boolean> cir) {
        if (!modEnabled(this.minecraft)) return;
        WidgetBoxBoundsInstance widgetBoxBoundsInstance = new WidgetBoxBoundsInstance(this.screen);
        widgetBoxBoundsInstance.handleClickingOnBox(mx, my, this.widgetLayout, cir);
        SearchBarBoundsInstance searchBarBoundsInstance = new SearchBarBoundsInstance(this.screen);
        searchBarBoundsInstance.handleClickingOnBox(mx, my, this.getSearchFields().inventory() != null ? this.getSearchFields().inventory() : this.getSearchFields().container(), cir);
    }

    @Inject(method = "slotClicked", at = @At("HEAD"), cancellable = true)
    private void handleSlotClicked(Slot slot, int slotId, int buttonNum, ClickType containerInput, CallbackInfo ci) {
        if (!modEnabled(this.minecraft)) return;
        ClickSlotInstance clickSlotInstance = new ClickSlotInstance((QuesoScreen) this.screen);
        clickSlotInstance.quickGuiClose(slot, buttonNum);
        clickSlotInstance.handleHardLockedSlots(slot, ci);
        clickSlotInstance.tradeAllForSelectedOffer(slotId, containerInput, ci);
    }

    @Inject(method = "mouseReleased", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;slotClicked(Lnet/minecraft/world/inventory/Slot;IILnet/minecraft/world/inventory/ClickType;)V", ordinal = 0), cancellable = true, locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private void handleMouseReleased(double mouseX, double mouseY, int bl, CallbackInfoReturnable<Boolean> cir, Slot slot, int xo, int yo, boolean clickedOutside, int slotId, Iterator var7, Slot target) {
        if (!modEnabled(this.minecraft)) return;
        MouseReleaseInstance mouseReleasedInstance = new MouseReleaseInstance((QuesoScreen) this.screen);
        mouseReleasedInstance.disableHardLockedSlotsOnDoubleClick(slot, target, cir);
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void handleMouseClicking(double mouseX, double mouseY, int bl, CallbackInfoReturnable<Boolean> cir) {
        if (!modEnabled(this.minecraft)) return;
        MouseClickInstance mouseClickInstance = new MouseClickInstance((QuesoScreen) this.screen);
        mouseClickInstance.trySelectingOrLockingSlot(bl, cir);
        mouseClickInstance.moveOnlyOne(mouseX, mouseY, bl, cir);
        mouseClickInstance.quickEquipItem(bl, cir);
        mouseClickInstance.handleInventorySearchFieldClicking(mouseX, mouseY, bl);
        mouseClickInstance.handleButtonInactiveSounds(mouseX, mouseY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (!modEnabled(this.minecraft)) return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
        MouseScrollInstance mouseScrollInstance = new MouseScrollInstance(this.minecraft, this.screen, this.getManagementButtons());
        mouseScrollInstance.changeSortMode(mouseX, mouseY, scrollY);
        mouseScrollInstance.setMoveAmount(this.hoveredSlot, mouseX, mouseY, scrollY);
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Inject(method = "mouseReleased", at = @At("HEAD"), cancellable = true)
    private void handleMouseReleasing(double mouseX, double mouseY, int bl, CallbackInfoReturnable<Boolean> cir) {
        if (!modEnabled(this.minecraft)) return;
        MouseReleaseInstance mouseReleaseInstance = new MouseReleaseInstance((QuesoScreen) this.screen);
        mouseReleaseInstance.trackSlotAndLockOrSelect(bl, cir);
    }

    @Inject(method = "mouseDragged", at = @At("HEAD"), cancellable = true)
    private void handleDragClicking(double mouseX, double mouseY, int bl, double dx, double dy, CallbackInfoReturnable<Boolean> cir) {
        if (!modEnabled(this.minecraft)) return;
        MouseDragInstance mouseDragInstance = new MouseDragInstance((QuesoScreen) this.screen);
        mouseDragInstance.handleSingularMovingAndLockingOrSelectingSlots(bl, cir);
    }

    @Redirect(method = {"mouseClicked", "mouseReleased"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;hasShiftDown()Z"))
    private boolean alwaysQuickMove(double mouseX, double mouseY, int bl) {
        return new ManagementInstance((QuesoScreen) this.screen).canQuickMove(bl);
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        if (!modEnabled(minecraft)) {
            super.resize(minecraft, width, height);
            return;
        }
        ResizeInstance resizeInstance = new ResizeInstance(this.screen, this.getSearchFields());
        resizeInstance.handleResizing(width, height, this.excludedSlots);
    }

    @Inject(method = "onClose", at = @At("TAIL"))
    private void handleOnClose(CallbackInfo ci) {
        if (!modEnabled(this.minecraft)) return;
        CloseScreenInstance closeScreenInstance = new CloseScreenInstance((QuesoScreen) this.screen);
        closeScreenInstance.putExcludedSlots();
        closeScreenInstance.saveSearchText();
        closeScreenInstance.disableFeatures();
        closeScreenInstance.autoCloseRecipeBook();
        closeScreenInstance.handleTrackedContainers();
    }

    @Override public Minecraft getMinecraft() { return this.minecraft; }
    @Override public AbstractContainerScreen<?> getScreen() { return this.screen; }
    @Override public AbstractContainerMenu getScreenMenu() { return this.screen.getMenu(); }
    @Override public Container getCurrentInventory() { return this.container; }
    @Override public Slot getScreensHoveredSlot() { return this.hoveredSlot; }
    @Override public Set<Integer> getExcludedSlots() { return this.excludedSlots; }
    @Override public SearchFields getSearchFields() { return new SearchFields(this.containerSearchField, this.inventorySearchField, this.inventorySearchField != null ? this.inventorySearchField.getValue() : this.containerSearchField != null ? this.containerSearchField.getValue() : ""); }
    @Override public ManagementButtons getManagementButtons() { return this.managementButtons; }
    @Override public void setCachedContainer(Container container) { this.container = container; }
    @Override public Container getCachedContainer() { return this.container; }
    @Override public void setCanMoveOne(boolean value) { this.canMoveOne = value; }
    @Override public boolean getCanMoveOne() { return this.canMoveOne; }
    @Override public void setDisableMoveMatchingItemsOnClose(boolean value) { this.disableMoveMatchingItemsOnClose = value; }
    @Override public boolean getDisableMoveMatchingItemsOnClose() { return this.disableMoveMatchingItemsOnClose; }
    @Override public void setLastLockedSlotIndex(int value) { this.lastLockedSlotIndex = value; }
    @Override public int getLastLockedSlotIndex() { return this.lastLockedSlotIndex; }
    @Override public void setLockDragAction(int value) { this.lockDragAction = value; }
    @Override public int getLockDragAction() { return this.lockDragAction; }
    @Override public void setExcludedAll(boolean value) { this.excludedAll = value; }
    @Override public boolean getExcludedAll() { return this.excludedAll; }
    @Override public void setContainerSearchField(EditBox containerSearchField) { this.containerSearchField = containerSearchField; }
    @Override public void setInventorySearchField(EditBox inventorySearchField) { this.inventorySearchField = inventorySearchField; }
    @Override public void setWidgetLayout(WidgetLayout layout) { this.widgetLayout = layout; }
    @Override public WidgetLayout getWidgetLayout() { return this.widgetLayout; }
}