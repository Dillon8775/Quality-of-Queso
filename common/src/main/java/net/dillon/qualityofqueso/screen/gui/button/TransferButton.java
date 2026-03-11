package net.dillon.qualityofqueso.screen.gui.button;

import net.dillon.qualityofqueso.keybind.ModKeybinds;
import net.dillon.qualityofqueso.util.ButtonUtil;
import net.dillon.qualityofqueso.util.HoverSize;
import net.dillon.qualityofqueso.util.ModTexts;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractFurnaceScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.Fireworks;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;
import java.util.List;
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
    protected final String searchFieldText;
    protected final Font font;
    protected final String buttonName;
    private final Supplier<Boolean> canBeActive;

    /**
     * Constructs a default transfer button.
     */
    public TransferButton(AbstractContainerMenu screenHandler, Font font, String searchFieldText, int x, int y, String buttonName, OnPress onPress) {
        super(x, y, 10, 10, ModTexts.BLANK, onPress, DEFAULT_NARRATION);
        this.screenHandler = screenHandler;
        this.font = font;
        this.searchFieldText = searchFieldText;
        this.buttonName = buttonName;
        this.canBeActive = () -> this.active;
    }

    /**
     * Constructs a default transfer button with a boolean supplier, determining if the button can be active or not.
     */
    public TransferButton(AbstractContainerMenu screenHandler, Font font, String searchFieldText, int x, int y, String buttonName, OnPress onPress, Supplier<Boolean> canBeActive) {
        super(x, y, 10, 10, ModTexts.BLANK, onPress, DEFAULT_NARRATION);
        this.buttonName = buttonName;
        this.screenHandler = screenHandler;
        this.font = font;
        this.searchFieldText = searchFieldText;
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
     * Renders a transfer button texture.
     */
    @Unique
    protected void renderButtonTexture(String id, boolean transferable, AbstractWidget buttonReference, GuiGraphics
            context) {
        String transferableString = !this.screenHandler.getCarried().isEmpty() ?
                "_with_stack.png" : this.searchFieldText.startsWith("!") ?
                "_exclude.png" : this.searchFieldText.startsWith("#") ?
                "_with_tag.png" : this.searchFieldText.startsWith(":") ?
                "_match.png" : ".png";
        String appended = transferable ? transferableString : ".png";
        Screen screen = Minecraft.getInstance().screen;
        if (screen != null) {
            if (isBrewingStandScreen(screen) || isFurnaceScreen(screen)) {
                appended = ".png";
            }
        }
        int xy = getTransferButtonXY(this);
        context.blit(RenderPipelines.GUI_TEXTURED, ofQoQ("textures/gui/button/" + id + appended), buttonReference.getX() - 1, buttonReference.getY() - 1, 0.0F, 0.0F, xy, xy, xy, xy);
        if (Minecraft.getInstance().hasControlDown()) {
            boolean inventoryButton = this.buttonName.equals("transfer_inventory");
            boolean containerButton = this.buttonName.equals("transfer_container");
            boolean validName = inventoryButton || containerButton;
            if (validName) {
                if (options().management.showButtonShortcuts) {
                    if (inventoryButton && key(ModKeybinds.MOVE_INVENTORY) == ModKeybinds.MOVE_INVENTORY.getDefaultKey()) {
                        ButtonUtil.drawButtonTexture(context, "shortcut/transfer_inventory_button_shortcut_key", this);
                    } else if (containerButton && key(ModKeybinds.MOVE_CONTAINER) == ModKeybinds.MOVE_CONTAINER.getDefaultKey()) {
                        ButtonUtil.drawButtonTexture(context, "shortcut/transfer_container_button_shortcut_key", this);
                    }
                }
            }
        }
    }

    /**
     * Renders the textures and tooltips for the button.
     */
    @Override
    protected void renderContents(GuiGraphics graphics, int mouseX, int mouseY, float deltaTicks) {
        this.active = this.canBeActive.get();

        if (this.canBeActive.get()) {
            this.renderButtonTexture(this.buttonName + "_button", true, this, graphics);
            if (this.isHovered()) {
                ButtonUtil.drawButtonTexture(graphics, "hovered/basic_hovered", this);
            }
        } else {
            this.renderButtonTexture(this.buttonName + "_button_inactive", true, this, graphics);
        }

        if (this.isHovered() && this.active && options().misc.helpfulTooltips) {
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
            if (!cursorStack.isEmpty()) {
                String tooltip = "_button.with_cursor_stack";
                Component itemName = cursorStack.getItemName();
                if (cursorStack.is(Items.ENCHANTED_BOOK)) {
                    ItemEnchantments enchantments = EnchantmentHelper.getEnchantmentsForCrafting(cursorStack);
                    List<String> cursorEnchantments = new ArrayList<>();
                    for (Holder<Enchantment> enchantment : enchantments.keySet()) {
                        cursorEnchantments.add(getEnchantmentName(enchantment));
                    }
                    String collection = String.join(",", cursorEnchantments);
                    tooltip = "_button.with_cursor_enchantment_stack";
                    itemName = Component.literal(collection);
                } else if (cursorStack.is(Items.FIREWORK_ROCKET)) {
                    Fireworks firework = cursorStack.get(DataComponents.FIREWORKS);
                    if (firework != null) {
                        itemName = Component.literal(String.valueOf(firework.flightDuration())).withStyle(ChatFormatting.BOLD);
                        tooltip = "_button.with_cursor_firework_stack";
                    }
                } else if (cursorStack.is(Items.POTION) || cursorStack.is(Items.SPLASH_POTION) || cursorStack.is(Items.LINGERING_POTION)) {
                    tooltip = "_button.with_cursor_potion_stack";
                } else if (cursorStack.is(Items.TIPPED_ARROW)) {
                    tooltip = "_button.with_cursor_tipped_arrow_stack";
                }
                ButtonUtil.drawTooltip(Component.translatable("qualityofqueso.gui." + this.buttonName + tooltip, itemName), graphics, this.font, mouseX, mouseY);
            } else if (!this.searchFieldText.isEmpty()) {
                ButtonUtil.drawTooltip(this.searchFieldText.startsWith("#") ?
                        Component.translatable("qualityofqueso.gui." + this.buttonName + "_button.with_search_query.tag", this.searchFieldText.substring(1)) :
                        this.searchFieldText.startsWith("!") ?
                                Component.translatable("qualityofqueso.gui." + this.buttonName + "_button.with_search_query.exclude", this.searchFieldText.substring(1)) :
                                this.searchFieldText.startsWith(":") ?
                                        Component.translatable("qualityofqueso.gui." + this.buttonName + "_button.with_search_query.match", this.searchFieldText.substring(1)) :
                                        Component.translatable("qualityofqueso.gui." + this.buttonName + "_button.with_search_query", this.searchFieldText), graphics, this.font, mouseX, mouseY);
            } else {
                if (!this.buttonName.equals(ButtonNames.SORT)) {
                    ButtonUtil.drawTooltip(Component.translatable("qualityofqueso.gui." + this.buttonName + "_button"), graphics, this.font, mouseX, mouseY);
                }
            }
        }
    }

    /**
     * @return the button hover texture size.
     */
    public HoverSize getHoverSize() {
        return HoverSize.BASIC;
    }
}