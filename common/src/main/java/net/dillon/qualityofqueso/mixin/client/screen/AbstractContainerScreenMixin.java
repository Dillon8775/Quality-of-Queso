package net.dillon.qualityofqueso.mixin.client.screen;

import net.dillon.qualityofqueso.event.QuesoScreenHolder;
import net.dillon.qualityofqueso.event.context.ManagementButtons;
import net.dillon.qualityofqueso.event.context.SearchFields;
import net.dillon.qualityofqueso.event.key.KeyPressedEvents;
import net.dillon.qualityofqueso.event.management.SlotClickedEvents;
import net.dillon.qualityofqueso.event.mouse.MouseClickedEvents;
import net.dillon.qualityofqueso.event.mouse.MouseReleasedEvents;
import net.dillon.qualityofqueso.event.mouse.MouseScrolledEvents;
import net.dillon.qualityofqueso.widget.management.WidgetLayout;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.components.inventory.SlotGroup;
import net.minecraft.client.gui.components.inventory.SlotWidget;
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
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.*;

import static net.dillon.qualityofqueso.helper.ModHelper.modEnabled;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin<T extends AbstractContainerMenu> extends Screen implements MenuAccess<T>, QuesoScreenHolder {
    @Shadow
    @Final
    protected T menu;

    @Shadow
    protected abstract SlotWidget getHoveredSlot();

    @Shadow
    protected int leftPos;
    @Shadow
    protected int topPos;
    @Unique
    private final AbstractContainerScreen<?> screen = (AbstractContainerScreen<?>) (Object) this;

    @Unique
    private EditBox containerSearchField, inventorySearchField;
    @Unique
    private final ManagementButtons managementButtons = new ManagementButtons();
    @Unique
    private final List<GuiEventListener> dynamicButtons = new ArrayList<>();

    @Unique
    private Container container;
    @Unique
    private WidgetLayout widgetLayout;

    @Unique
    private final Set<Integer> excludedSlots = new HashSet<>();
    @Unique
    private boolean excludedAll = false;

    @Unique
    private boolean disableFilteringOnClose = false;

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
    public AbstractContainerScreen<?> screen() {
        return this.screen;
    }

    @Override
    public AbstractContainerMenu menu() {
        return this.screen.getMenu();
    }

    @Override
    public Container container() {
        return this.container;
    }

    @Override
    public SlotWidget getHoveredSlotWidget() {
        return this.getHoveredSlot();
    }

    @Override
    public Set<Integer> excludedSlots() {
        return this.excludedSlots;
    }

    @Override
    public SearchFields searchFields() {
        return new SearchFields(
                this.containerSearchField,
                this.inventorySearchField,
                this.inventorySearchField != null ? this.inventorySearchField.getValue() : this.containerSearchField != null ? this.containerSearchField.getValue() : ""
        );
    }

    @Override
    public ManagementButtons managementButtons() {
        return this.managementButtons;
    }

    @Override
    public List<GuiEventListener> dynamicButtons() {
        return this.dynamicButtons;
    }

    @Override
    public void removeDynamicButton(GuiEventListener widget) {
        this.dynamicButtons.remove(widget);
    }

    @Override
    public void clearDynamicButtons() {
        this.dynamicButtons.clear();
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
    public void setDisableFilteringOnClose(boolean value) {
        this.disableFilteringOnClose = value;
    }

    @Override
    public boolean getDisableFilteringOnClose() {
        return this.disableFilteringOnClose;
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
        if (!modEnabled()) {
            return;
        }

        events().screenInitialized().handle();
    }

    /**
     * Renders the locked slot overlay overtop of slots.
     */
    @Inject(method = "extractContents", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;extractLabels(Lnet/minecraft/client/gui/GuiGraphicsExtractor;)V"))
    private void renderLockedSlotsOverlay(GuiGraphicsExtractor graphics, CallbackInfo ci) {
        if (!modEnabled()) {
            return;
        }

        events().extracting().handleSlotExtraction(graphics);
    }

    /**
     * Grays out any slot which doesn't contain the query name being searched, and renders the lock texture for locked slots.
     */
    @Inject(method = "extractContents", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;extractSlots(Lnet/minecraft/client/gui/GuiGraphicsExtractor;)V", shift = At.Shift.AFTER))
    private void grayOutAndRenderLockTexture(GuiGraphicsExtractor graphics, CallbackInfo ci) {
        if (!modEnabled()) {
            return;
        }

        events().extracting().grayoutSlotsAndExtractLockedIcon(graphics);
    }

    /**
     * Renders widgets, including management buttons, search fields, and the {@link WidgetLayout}.
     */
    @Inject(method = "extractContents", at = @At("TAIL"))
    private void renderAndInitializeWidgets(GuiGraphicsExtractor graphics, CallbackInfo ci) {
        if (!modEnabled()) {
            return;
        }

        events().extracting().handle(graphics);
    }

    /**
     * Handles slot clicking with the help of the {@link SlotClickedEvents} record.
     */
    @Inject(method = "slotClicked", at = @At("HEAD"), cancellable = true)
    private void handleSlotClicked(SlotWidget slotWidget, int slotId, int buttonNum, ContainerInput containerInput, CallbackInfo ci) {
        if (!modEnabled() || slotWidget == null) {
            return;
        }

        events().slotClicked().handle(slotWidget.slot(), slotId, buttonNum, containerInput, ci);
    }

    /**
     * Handles clicking outside the GUI screen, for the {@link WidgetLayout}.
     */
    @Inject(method = "hasClickedOutside", at = @At("HEAD"), cancellable = true)
    private void handleHasClickedOutside(double mx, double my, CallbackInfoReturnable<Boolean> cir) {
        if (!modEnabled()) {
            return;
        }

        events().mouseClicked().handleSearchBar(
                mx,
                my,
                this.searchFields().inventory() != null
                        ? this.searchFields().inventory()
                        : this.searchFields().container(),
                cir
        );
        events().mouseClicked().handleWidgetLayout(
                mx,
                my,
                this.widgetLayout,
                cir
        );
    }

    /**
     * Selects and/or locks slots, and handles other mouse clicking events with the help of the {@link MouseClickedEvents} record.
     */
    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void handleMouseClicking(MouseButtonEvent event, boolean doubleClick, CallbackInfoReturnable<Boolean> cir) {
        if (!modEnabled()) {
            return;
        }

        events().mouseClicked().handle(event, doubleClick, cir);
    }

    /**
     * Handles mouse scrolling events with the help of the {@link MouseScrolledEvents} record.
     */
    @Override
    public boolean mouseScrolled(double x, double y, double scrollX, double scrollY) {
        if (!modEnabled()) {
            return super.mouseScrolled(x, y, scrollX, scrollY);
        }

        // Handle scrolling events
        if (events().mouseScrolled().handle(scrollY)) {
            return true;
        }

        return super.mouseScrolled(x, y, scrollX, scrollY);
    }

    /**
     * Handles mouse releasing events.
     */
    @Inject(method = "mouseReleased", at = @At("HEAD"), cancellable = true)
    private void handleMouseReleasing(MouseButtonEvent event, CallbackInfoReturnable<Boolean> cir) {
        if (!modEnabled()) {
            return;
        }

        events().mouseReleased().trackSlotAndLockOrSelect(event, cir);
    }

    /**
     * Handles mouse-releasing events with the help of the {@link MouseReleasedEvents} record.
     */
    @Inject(method = "handleDoublePress", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;slotClicked(Lnet/minecraft/client/gui/components/inventory/SlotWidget;IILnet/minecraft/world/inventory/ContainerInput;)V", ordinal = 1), cancellable = true, locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private void handleMouseReleased(SlotWidget slotWidget, boolean isDoublePress, boolean hasShiftDown, int button, CallbackInfoReturnable<Boolean> cir, int slotId, Iterator var6, SlotGroup group, Iterator var8, SlotWidget targetWidget, Slot target) {
        if (!modEnabled() || slotWidget == null) {
            return;
        }

        events().mouseReleased().disableHardLockedSlotsOnDoubleClick(slotWidget.slot(), target, cir);
    }

    /**
     * Handles drag clicking events.
     */
    @Inject(method = "mouseDragged", at = @At("HEAD"), cancellable = true)
    private void handleDragClicking(MouseButtonEvent event, double dx, double dy, CallbackInfoReturnable<Boolean> cir) {
        if (!modEnabled()) {
            return;
        }

        events().mouseDragged().handle(event, cir);
    }

    /**
     * Handles all key pressing events.
     */
    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void handleKeyPressing(KeyEvent event, CallbackInfoReturnable<Boolean> cir) {
        if (!modEnabled()) {
            return;
        }

        new KeyPressedEvents(screenAsHolder(), event, cir).handleKeyPressing();
    }

    /**
     * Handles char typing events.
     */
    @Override
    public boolean charTyped(CharacterEvent event) {
        return events().charTyped().handleCharTyped(event, () -> super.charTyped(event));
    }

    /**
     * Handles resizing.
     */
    @Override
    public void resize(int width, int height) {
        if (!modEnabled()) {
            super.resize(width, height);
            return;
        }

        events().screenResized().handleResizing(width, height, this.excludedSlots);
    }

    /**
     * Handles closing events when closing the screen.
     */
    @Inject(method = "onClose", at = @At("HEAD"))
    private void handleOnClose(CallbackInfo ci) {
        if (!modEnabled()) {
            return;
        }

        events().screenClosed().handle();
    }
}