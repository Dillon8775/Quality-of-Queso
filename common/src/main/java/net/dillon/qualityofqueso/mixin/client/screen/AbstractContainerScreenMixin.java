package net.dillon.qualityofqueso.mixin.client.screen;

import net.dillon.qualityofqueso.instance.*;
import net.dillon.qualityofqueso.instance.context.ManagementButtons;
import net.dillon.qualityofqueso.instance.context.SearchFields;
import net.dillon.qualityofqueso.instance.management.ClickSlotInstance;
import net.dillon.qualityofqueso.instance.management.ExtractingInstance;
import net.dillon.qualityofqueso.instance.management.ManagementInstance;
import net.dillon.qualityofqueso.widget.layout.WidgetLayout;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
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
    @Shadow
    @Final
    protected T menu;

    @Shadow
    @Nullable
    protected Slot hoveredSlot;

    @Unique
    private final AbstractContainerScreen<?> screen = (AbstractContainerScreen<?>) (Object) this;

    @Unique
    private EditBox containerSearchField, inventorySearchField;
    @Unique
    private final ManagementButtons managementButtons = new ManagementButtons();

    @Unique
    private Container container;
    @Unique
    private WidgetLayout widgetLayout;

    @Unique
    private final Set<Integer> excludedSlots = new HashSet<>();
    @Unique
    private boolean excludedAll = false;

    @Unique
    private boolean disableMoveMatchingItemsOnClose = false;

    @Unique
    private int lastLockedSlotIndex = -1;
    @Unique
    private int lockDragAction = 0;
    @Unique
    private boolean canMoveOne = false;

    public AbstractContainerScreenMixin(Component title) {
        super(title);
    }

    @Override
    public Minecraft getMinecraft() {
        return this.minecraft;
    }

    @Override
    public AbstractContainerScreen<?> getScreen() {
        return this.screen;
    }

    @Override
    public AbstractContainerMenu getScreenMenu() {
        return this.screen.getMenu();
    }

    @Override
    public Container getCurrentInventory() {
        return this.container;
    }

    @Override
    public Slot getScreensHoveredSlot() {
        return this.hoveredSlot;
    }

    @Override
    public Set<Integer> getExcludedSlots() {
        return this.excludedSlots;
    }

    @Override
    public SearchFields getSearchFields() {
        return new SearchFields(
                this.containerSearchField,
                this.inventorySearchField,
                this.inventorySearchField != null ? this.inventorySearchField.getValue() : this.containerSearchField != null ? this.containerSearchField.getValue() : ""
        );
    }

    @Override
    public ManagementButtons getManagementButtons() {
        return this.managementButtons;
    }

    @Unique
    private ManagementInstance managementInstance() {
        return new ManagementInstance((QuesoScreen) this.screen);
    }

    @Override
    public void setCachedContainer(Container container) {
        this.container = container;
    }

    @Override
    public Container getCachedContainer() {
        return this.container;
    }

    @Override
    public void setCanMoveOne(boolean value) {
        this.canMoveOne = value;
    }

    @Override
    public boolean getCanMoveOne() {
        return this.canMoveOne;
    }

    @Override
    public void setDisableMoveMatchingItemsOnClose(boolean value) {
        this.disableMoveMatchingItemsOnClose = value;
    }

    @Override
    public boolean getDisableMoveMatchingItemsOnClose() {
        return this.disableMoveMatchingItemsOnClose;
    }

    @Override
    public void setLastLockedSlotIndex(int value) {
        this.lastLockedSlotIndex = value;
    }

    @Override
    public int getLastLockedSlotIndex() {
        return this.lastLockedSlotIndex;
    }

    @Override
    public void setLockDragAction(int value) {
        this.lockDragAction = value;
    }

    @Override
    public int getLockDragAction() {
        return this.lockDragAction;
    }

    @Override
    public void setExcludedAll(boolean value) {
        this.excludedAll = value;
    }

    @Override
    public boolean getExcludedAll() {
        return this.excludedAll;
    }

    @Override
    public void setContainerSearchField(EditBox containerSearchField) {
        this.containerSearchField = containerSearchField;
    }

    @Override
    public void setInventorySearchField(EditBox inventorySearchField) {
        this.inventorySearchField = inventorySearchField;
    }

    @Override
    public void setWidgetLayout(WidgetLayout layout) {
        this.widgetLayout = layout;
    }

    @Override
    public WidgetLayout getWidgetLayout() {
        return this.widgetLayout;
    }

    /**
     * Initializes base variables for {@link AbstractContainerScreen}s, including container values, tracked containers, etc.
     */
    @Inject(method = "init", at = @At("TAIL"))
    private void initialize(CallbackInfo ci) {
        if (!modEnabled(this.minecraft)) {
            return;
        }

        ScreenInitInstance screenInitInstance = new ScreenInitInstance(
                (QuesoScreen) this.screen
        );
        screenInitInstance.initializeContainer();
        screenInitInstance.handleTrackedContainers();
        screenInitInstance.initializeSearchFields();
        screenInitInstance.setCurrentContainer();
        screenInitInstance.readdExcludedSlots();
    }

    /**
     * Renders the locked slot overlay overtop of slots.
     */
    @Inject(method = "extractContents", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;extractLabels(Lnet/minecraft/client/gui/GuiGraphicsExtractor;II)V"))
    private void renderLockedSlotsOverlay(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a, CallbackInfo ci) {
        if (!modEnabled(this.minecraft)) {
            return;
        }

        ExtractingInstance extractingInstance = new ExtractingInstance(
                (QuesoScreen) this.screen
        );
        extractingInstance.extractLockedSlotColor(graphics);
    }

    /**
     * Renders widgets, including management buttons, search fields, and the {@link WidgetLayout}.
     */
    @Inject(method = "extractContents", at = @At("TAIL"))
    private void renderAndInitializeWidgets(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a, CallbackInfo ci) {
        if (!modEnabled(this.minecraft)) {
            return;
        }

        ExtractingInstance extractingInstance = new ExtractingInstance(
                (QuesoScreen) this.screen
        );
        extractingInstance.clearWidgets();
        extractingInstance.extractSearchFields(graphics, mouseX, mouseY, a);
        extractingInstance.extractButtons(graphics, mouseX, mouseY, a);
        extractingInstance.extractLockingUnlockingSlots(graphics, mouseX, mouseY);
    }

    /**
     * Grays out any slot which doesn't contain the query name being searched, and renders the lock texture for locked slots.
     */
    @Inject(method = "extractContents", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;extractSlotHighlightFront(Lnet/minecraft/client/gui/GuiGraphicsExtractor;)V", shift = At.Shift.AFTER))
    private void grayOutAndRenderLockTexture(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a, CallbackInfo ci) {
        if (!modEnabled(this.minecraft)) {
            return;
        }

        ExtractingInstance extractingInstance = new ExtractingInstance(
                (QuesoScreen) this.screen
        );
        extractingInstance.grayoutSlotsAndExtractLockedIcon(graphics);
    }

    /**
     * Renders mod tooltips with the help of the {@link ModTooltipInstance} record.
     */
    @Inject(method = "extractTooltip", at = @At("HEAD"), cancellable = true)
    private void modifyExistingAndNewTooltips(GuiGraphicsExtractor graphics, int mouseX, int mouseY, CallbackInfo ci) {
        if (!modEnabled(this.minecraft)) {
            return;
        }

        ModTooltipInstance modTooltips = new ModTooltipInstance(
                (QuesoScreen) this.screen
        );
        modTooltips.displaySingleMovingTooltips(graphics, this.font, mouseX, mouseY, ci);
        modTooltips.displayEnchantmentHelperTooltips(graphics, this.font, mouseX, mouseY, ci);
        modTooltips.displayTagsOnItems(graphics, this.font, mouseX, mouseY, ci);
    }

    /**
     * Handles clicking outside the GUI screen, for the {@link WidgetLayout}.
     */
    @Inject(method = "hasClickedOutside", at = @At("HEAD"), cancellable = true)
    private void handleHasClickedOutside(double mx, double my, int xo, int yo, CallbackInfoReturnable<Boolean> cir) {
        if (!modEnabled(this.minecraft)) {
            return;
        }

        WidgetBoxBoundsInstance widgetBoxBoundsInstance = new WidgetBoxBoundsInstance(
                this.screen
        );
        widgetBoxBoundsInstance.handleClickingOnBox(mx, my, this.widgetLayout, cir);

        SearchBarBoundsInstance searchBarBoundsInstance = new SearchBarBoundsInstance(
                this.screen
        );
        searchBarBoundsInstance.handleClickingOnBox(mx, my,
                this.getSearchFields().inventory() != null ? this.getSearchFields().inventory() : this.getSearchFields().container(),
                cir);
    }

    /**
     * Handles slot clicking with the help of the {@link ClickSlotInstance} record.
     */
    @Inject(method = "slotClicked", at = @At("HEAD"), cancellable = true)
    private void handleSlotClicked(Slot slot, int slotId, int buttonNum, ContainerInput containerInput, CallbackInfo ci) {
        if (!modEnabled(this.minecraft)) {
            return;
        }

        ClickSlotInstance clickSlotInstance = new ClickSlotInstance(
                (QuesoScreen) this.screen
        );
        clickSlotInstance.quickGuiClose(slot, buttonNum);
        clickSlotInstance.handleHardLockedSlots(slot, ci);
        clickSlotInstance.tradeAllForSelectedOffer(slotId, containerInput, ci);
        clickSlotInstance.craftAllForSelectedRecipe(slotId, containerInput, ci);
    }

    /**
     * Handles mouse-releasing events with the help of the {@link MouseReleaseInstance} record.
     */
    @Inject(method = "mouseReleased", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;slotClicked(Lnet/minecraft/world/inventory/Slot;IILnet/minecraft/world/inventory/ContainerInput;)V", ordinal = 0), cancellable = true, locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private void handleMouseReleased(MouseButtonEvent event, CallbackInfoReturnable<Boolean> cir, Slot slot, int xo, int yo, boolean clickedOutside, int slotId, Iterator var7, Slot target) {
        if (!modEnabled(this.minecraft)) {
            return;
        }

        MouseReleaseInstance mouseReleasedInstance = new MouseReleaseInstance(
                (QuesoScreen) this.screen
        );
        mouseReleasedInstance.disableHardLockedSlotsOnDoubleClick(slot, target, cir);
    }

    /**
     * Selects and/or locks slots, and handles other mouse clicking events with the help of the {@link MouseClickInstance} record.
     */
    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void handleMouseClicking(MouseButtonEvent event, boolean doubleClick, CallbackInfoReturnable<Boolean> cir) {
        if (!modEnabled(this.minecraft)) {
            return;
        }

        MouseClickInstance mouseClickInstance = new MouseClickInstance(
                (QuesoScreen) this.screen
        );
        mouseClickInstance.trySelectingOrLockingSlot(event, cir);
        mouseClickInstance.moveOnlyOne(event, cir);
        mouseClickInstance.quickEquipItem(event, cir);
        mouseClickInstance.handleInventorySearchFieldClicking(event, doubleClick);
        mouseClickInstance.handleButtonInactiveSounds();
    }

    /**
     * Handles mouse scrolling events with the help of the {@link MouseScrollInstance} record.
     */
    @Inject(method = "mouseScrolled", at = @At("HEAD"), cancellable = true)
    private void handleMouseScrolling(double x, double y, double scrollX, double scrollY, CallbackInfoReturnable<Boolean> cir) {
        if (!modEnabled(this.minecraft)) {
            return;
        }

        MouseScrollInstance mouseScrollInstance = new MouseScrollInstance(
                (QuesoScreen) this
        );
        mouseScrollInstance.changeSortMode(scrollY);
        mouseScrollInstance.moveHoveredItem(scrollY, cir);
    }

    /**
     * Handles mouse releasing events.
     */
    @Inject(method = "mouseReleased", at = @At("HEAD"), cancellable = true)
    private void handleMouseReleasing(MouseButtonEvent event, CallbackInfoReturnable<Boolean> cir) {
        if (!modEnabled(this.minecraft)) {
            return;
        }

        MouseReleaseInstance mouseReleaseInstance = new MouseReleaseInstance(
                (QuesoScreen) this.screen
        );
        mouseReleaseInstance.trackSlotAndLockOrSelect(event, cir);
    }

    /**
     * Handles drag clicking events.
     */
    @Inject(method = "mouseDragged", at = @At("HEAD"), cancellable = true)
    private void handleDragClicking(MouseButtonEvent event, double dx, double dy, CallbackInfoReturnable<Boolean> cir) {
        if (!modEnabled(this.minecraft)) {
            return;
        }

        MouseDragInstance mouseDragInstance = new MouseDragInstance(
                (QuesoScreen) this.screen
        );
        mouseDragInstance.handleSingularMovingAndLockingOrSelectingSlots(event, cir);
    }

    /**
     * Always quickly moves items if the option is enabled.
     */
    @Redirect(method = {"mouseClicked", "mouseReleased"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/input/MouseButtonEvent;hasShiftDown()Z"))
    private boolean alwaysQuickMove(MouseButtonEvent event) {
        return this.managementInstance().canQuickMove(event);
    }

    /**
     * Handles all key pressing events.
     */
    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void handleKeyPressing(KeyEvent event, CallbackInfoReturnable<Boolean> cir) {
        if (!modEnabled(this.minecraft)) {
            return;
        }

        KeyPressInstance keyPressInstance = new KeyPressInstance(
                (QuesoScreen) this.screen
        );
        keyPressInstance.handleKeyPressing(event, cir);
    }

    /**
     * Handles char typing events.
     */
    @Override
    public boolean charTyped(CharacterEvent event) {
        CharTypedInstance charTypedInstance = new CharTypedInstance(
                this.screen
        );
        return charTypedInstance.handleCharTyped(event, () -> super.charTyped(event));
    }

    /**
     * Handles resizing.
     */
    @Override
    public void resize(int width, int height) {
        if (!modEnabled(this.minecraft)) {
            super.resize(width, height);
            return;
        }

        ResizeInstance resizeInstance = new ResizeInstance(
                this.screen,
                this.getSearchFields()
        );
        resizeInstance.handleResizing(width, height, this.excludedSlots);
    }

    /**
     * Handles closing events when closing the screen.
     */
    @Inject(method = "onClose", at = @At("TAIL"))
    private void handleOnClose(CallbackInfo ci) {
        if (!modEnabled(this.minecraft)) {
            return;
        }

        CloseScreenInstance closeScreenInstance = new CloseScreenInstance(
                (QuesoScreen) this.screen
        );
        closeScreenInstance.putExcludedSlots();
        closeScreenInstance.saveSearchText();
        closeScreenInstance.disableFeatures();
        closeScreenInstance.autoCloseRecipeBook();
        closeScreenInstance.handleTrackedContainers();
    }
}