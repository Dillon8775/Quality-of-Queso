package net.dillon.qualityofqueso.screen.gui.button;

import net.dillon.qualityofqueso.keybind.ModKeybinds;
import net.dillon.qualityofqueso.util.ButtonUtil;
import net.dillon.qualityofqueso.util.HoverSize;
import net.dillon.qualityofqueso.util.ModTexts;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractFurnaceScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Unique;

import java.util.function.Supplier;

import static net.dillon.qualityofqueso.util.AccessorUtil.key;
import static net.dillon.qualityofqueso.util.ButtonUtil.*;
import static net.dillon.qualityofqueso.util.ModUtil.ofQoQ;
import static net.dillon.qualityofqueso.util.ModUtil.options;

/**
 * A representation of a transfer button.
 */
public class TransferButton extends Button {
    private final AbstractContainerMenu screenHandler;
    private final boolean transferrableButton;
    protected final String searchFieldText;
    protected final Font font;
    protected final String resourceLocation;
    protected final String buttonName;
    protected final Supplier<Boolean> canBeActive;

    /**
     * Constructs a default transfer button.
     */
    public TransferButton(AbstractContainerMenu screenHandler, Font font, String searchFieldText, int x, int y, String resourceLocation, String buttonName, boolean transferrableButton, OnPress onPress) {
        super(x, y, 10, 10, ModTexts.BLANK, onPress, DEFAULT_NARRATION);
        this.screenHandler = screenHandler;
        this.font = font;
        this.searchFieldText = searchFieldText;
        this.transferrableButton = transferrableButton;
        this.resourceLocation = resourceLocation;
        this.buttonName = buttonName;
        this.canBeActive = () -> this.active;
    }

    /**
     * Constructs a default transfer button with a boolean supplier, determining if the button can be active or not.
     */
    public TransferButton(AbstractContainerMenu screenHandler, Font font, String searchFieldText, int x, int y, String resourceLocation, String buttonName, boolean transferrableButton, OnPress onPress, Supplier<Boolean> canBeActive) {
        super(x, y, 10, 10, ModTexts.BLANK, onPress, DEFAULT_NARRATION);
        this.screenHandler = screenHandler;
        this.font = font;
        this.searchFieldText = searchFieldText;
        this.resourceLocation = resourceLocation;
        this.buttonName = buttonName;
        this.transferrableButton = transferrableButton;
        this.canBeActive = canBeActive;
    }

    /**
     * Fully blocks sound and click if button shouldn't be activated.
     */
    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean isDouble) {
        if (!this.canBeActive.get()) {
            return false;
        }
        return super.mouseClicked(event, isDouble);
    }

    @Override
    public void playDownSound(SoundManager manager) {
    }

    /**
     * @return the appended texture ID to use for the button.
     */
    protected String getAppendedTexture() {
        String transferableString = !this.screenHandler.getCarried().isEmpty() ?
                "_with_stack.png" : this.searchFieldText.startsWith("!") ?
                "_exclude.png" : this.searchFieldText.startsWith("#") ?
                "_with_tag.png" : this.searchFieldText.startsWith(":") ?
                "_match.png" : ".png";
        String appended = this.transferrableButton ? transferableString : ".png";
        Screen screen = Minecraft.getInstance().screen;

        if (screen == null) {
            return appended;
        }

        if (isBrewingStandScreen(screen) || isFurnaceScreen(screen)) {
            appended = ".png";
        }
        return appended;
    }

    /**
     * Renders the button texture.
     */
    private void renderButtonTexture(String id, AbstractWidget buttonReference, GuiGraphicsExtractor graphics) {
        int xy = getTransferButtonXY(this);
        graphics.blit(RenderPipelines.GUI_TEXTURED, ofQoQ("textures/gui/button/" + id + this.getAppendedTexture()), buttonReference.getX() - 1, buttonReference.getY() - 1, 0.0F, 0.0F, xy, xy, xy, xy);
        this.renderHoveredTexture(graphics);
    }

    /**
     * Renders the hovered button texture.
     */
    protected void renderHoveredTexture(GuiGraphicsExtractor graphics) {
        if (!this.canBeActive.get() || !this.isHovered()) {
            return;
        }

        String name;
        switch (this.getHoverSize()) {
            case BIG -> name = "big_hovered";
            default -> name = "basic_hovered";
        }
        ButtonUtil.drawButtonTexture(graphics, "hovered/" + name, this);
    }

    /**
     * Renders a transfer button texture.
     */
    @Unique
    protected void renderBaseButtonTexture(String id, AbstractWidget buttonReference, GuiGraphicsExtractor graphics) {
        this.renderButtonTexture(id, buttonReference, graphics);

        if (!Minecraft.getInstance().hasControlDown()) {
            return;
        }

        boolean inventoryButton = this.buttonName.equals("transfer_inventory");
        boolean containerButton = this.buttonName.equals("transfer_container");
        boolean validName = inventoryButton || containerButton;

        if (!validName || !options().accessibility.showButtonShortcuts) {
            return;
        }

        if (inventoryButton && key(ModKeybinds.MOVE_INVENTORY) == ModKeybinds.MOVE_INVENTORY.getDefaultKey()) {
            ButtonUtil.drawButtonTexture(graphics, "shortcut/transfer_inventory_button_shortcut_key", this);
        } else if (containerButton && key(ModKeybinds.MOVE_CONTAINER) == ModKeybinds.MOVE_CONTAINER.getDefaultKey()) {
            ButtonUtil.drawButtonTexture(graphics, "shortcut/transfer_container_button_shortcut_key", this);
        }
    }

    /**
     * @return the appended tooltip to use to render.
     */
    protected String getAppendedTooltip() {
        return "";
    }

    /**
     * @return the appended tooltip to use to draw.
     */
    protected Component getTooltipToRender() {
        return Component.translatable("qualityofqueso.gui." + this.buttonName + "_button" + this.getAppendedTooltip());
    }

    /**
     * Renders the textures and tooltips for the button.
     */
    @Override
    protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float deltaTicks) {
        this.active = this.canBeActive.get();

        this.renderBaseButtonTexture(this.resourceLocation + this.buttonName + (this.canBeActive.get() ? "_button" : "_button_inactive"), this, graphics);

        if (!this.isHovered() || !this.active || !options().accessibility.helpfulTooltips) {
            return;
        }

        Screen screen = Minecraft.getInstance().screen;
        if (screen != null) {
            if (isBrewingStandScreen(screen)) {
                ButtonUtil.drawTooltip(Component.translatable("qualityofqueso.gui." + this.buttonName + "_button.brewing_stand"), graphics, this.font, mouseX, mouseY);
                return;
            } else if (screen instanceof AbstractFurnaceScreen<?> abstractFurnaceScreen) {
                ButtonUtil.drawTooltip(Component.translatable("qualityofqueso.gui." + this.buttonName + "_button.furnace", abstractFurnaceScreen.getMenu().getResultSlot().getItem().getHoverName()), graphics, this.font, mouseX, mouseY);
                return;
            }
        }
        ItemStack cursorStack = this.screenHandler.getCarried();
        if (this.transferrableButton && !cursorStack.isEmpty()) {
            ButtonUtil.drawTooltip(Component.translatable("qualityofqueso.gui." + this.buttonName + "_button.with_cursor_stack",
                            cursorStack.getItemName(),
                            Minecraft.getInstance().hasShiftDown()
                                    ? Component.translatable("qualityofqueso.gui.transfer_button.matching_cursor_stack")
                                    : Component.translatable("qualityofqueso.gui.transfer_button.match_cursor_stack")),
                    graphics, this.font, mouseX, mouseY);
        } else if (this.transferrableButton && !this.searchFieldText.isEmpty()) {
            ButtonUtil.drawTooltip(this.searchFieldText.startsWith("#") ?
                    Component.translatable("qualityofqueso.gui." + this.buttonName + "_button.with_search_query.tag" + this.getAppendedTooltip(), this.searchFieldText.substring(1)) :
                    this.searchFieldText.startsWith("!") ?
                            Component.translatable("qualityofqueso.gui." + this.buttonName + "_button.with_search_query.exclude" + this.getAppendedTooltip(), this.searchFieldText.substring(1)) :
                            this.searchFieldText.startsWith(":") ?
                                    Component.translatable("qualityofqueso.gui." + this.buttonName + "_button.with_search_query.match" + this.getAppendedTooltip(), this.searchFieldText.substring(1)) :
                                    Component.translatable("qualityofqueso.gui." + this.buttonName + "_button.with_search_query" + this.getAppendedTooltip(), this.searchFieldText), graphics, this.font, mouseX, mouseY);
        } else {
            ButtonUtil.drawTooltip(this.getTooltipToRender(), graphics, this.font, mouseX, mouseY);
        }
    }

    /**
     * @return the button hover texture size.
     */
    public HoverSize getHoverSize() {
        return HoverSize.BASIC;
    }
}