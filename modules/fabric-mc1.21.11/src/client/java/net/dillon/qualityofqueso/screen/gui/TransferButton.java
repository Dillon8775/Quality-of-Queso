package net.dillon.qualityofqueso.screen.gui;

import net.dillon.qualityofqueso.keybind.ModKeybinds;
import net.dillon.qualityofqueso.util.ButtonUtil;
import net.dillon.qualityofqueso.util.ModTexts;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.AbstractFurnaceScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FireworksComponent;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static net.dillon.qualityofqueso.main.QoQ.options;
import static net.dillon.qualityofqueso.util.ButtonUtil.*;
import static net.minecraft.text.Text.literal;
import static net.minecraft.text.Text.translatable;

/**
 * A representation of a transfer button.
 */
public class TransferButton extends ButtonWidget {
    private final ScreenHandler screenHandler;
    protected final String searchFieldText;
    protected final TextRenderer textRenderer;
    protected final String buttonName;
    private final Supplier<Boolean> canBeActive;

    /**
     * Constructs a default transfer button.
     */
    public TransferButton(ScreenHandler screenHandler, TextRenderer textRenderer, String searchFieldText, int x, int y, String buttonName, PressAction onPress) {
        super(x, y, 10, 10, ModTexts.BLANK, onPress, DEFAULT_NARRATION_SUPPLIER);
        this.screenHandler = screenHandler;
        this.textRenderer = textRenderer;
        this.searchFieldText = searchFieldText;
        this.buttonName = buttonName;
        this.canBeActive = () -> this.active;
    }

    /**
     * Constructs a default transfer button with a boolean supplier, determining if the button can be active or not.
     */
    public TransferButton(ScreenHandler screenHandler, TextRenderer textRenderer, String searchFieldText, int x, int y, String buttonName, PressAction onPress, Supplier<Boolean> canBeActive) {
        super(x, y, 10, 10, ModTexts.BLANK, onPress, DEFAULT_NARRATION_SUPPLIER);
        this.buttonName = buttonName;
        this.screenHandler = screenHandler;
        this.textRenderer = textRenderer;
        this.searchFieldText = searchFieldText;
        this.canBeActive = canBeActive;
    }

    /**
     * Fully blocks sound and click if button shouldn't be activated.
     */
    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        if (!this.canBeActive.get()) {
            return false;
        }
        return super.mouseClicked(click, doubled);
    }

    /**
     * Renders a transfer button texture.
     */
    @Unique
    protected void renderButtonTexture(String id, boolean transferable, ClickableWidget buttonReference, DrawContext
            context) {
        String transferableString = !this.screenHandler.getCursorStack().isEmpty() ?
                "_with_stack.png" : this.searchFieldText.startsWith("!") ?
                "_exclude.png" : this.searchFieldText.startsWith("#") ?
                "_with_tag.png" : this.searchFieldText.startsWith(":") ?
                "_match.png" : ".png";
        String appended = transferable ? transferableString : ".png";
        Screen screen = MinecraftClient.getInstance().currentScreen;
        if (screen != null) {
            if (isBrewingStandScreen(screen) || isFurnaceScreen(screen)) {
                appended = ".png";
            }
        }
        context.drawTexture(RenderPipelines.GUI_TEXTURED, Identifier.of("qualityofqueso:textures/gui/" + id + appended), buttonReference.getX() - 1, buttonReference.getY() - 1, 0.0F, 0.0F, 12, 12, 12, 12);
        if (MinecraftClient.getInstance().isCtrlPressed()) {
            boolean inventoryButton = this.buttonName.equals("transfer_inventory");
            boolean containerButton = this.buttonName.equals("transfer_container");
            boolean validName = inventoryButton || containerButton;
            if (validName) {
                if (options().showButtonShortcuts) {
                    if (inventoryButton && ModKeybinds.MOVE_INVENTORY.boundKey == ModKeybinds.MOVE_INVENTORY.getDefaultKey()) {
                        ButtonUtil.drawButtonTexture(context, "transfer_inventory_button_shortcut_key", this);
                    } else if (containerButton && ModKeybinds.MOVE_CONTAINER.boundKey == ModKeybinds.MOVE_CONTAINER.getDefaultKey()) {
                        ButtonUtil.drawButtonTexture(context, "transfer_container_button_shortcut_key", this);
                    }
                }
            }
        }
    }

    /**
     * Renders the textures and tooltips for the button.
     */
    @Override
    protected void drawIcon(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        this.active = this.canBeActive.get();

        if (this.canBeActive.get()) {
            this.renderButtonTexture(this.isHovered() ?
                    this.buttonName + "_button_hovered" :
                    this.buttonName + "_button", true, this, context);
        } else {
            this.renderButtonTexture(this.buttonName + "_button_inactive", true, this, context);
        }

        if (this.isHovered()) {
            if (this.active) {
                Screen screen = MinecraftClient.getInstance().currentScreen;
                if (screen != null) {
                    if (isBrewingStandScreen(screen)) {
                        ButtonUtil.drawTooltip(translatable("qualityofqueso.gui." + this.buttonName + "_button.brewing_stand"), context, this.textRenderer, mouseX, mouseY);
                        return;
                    } else if (screen instanceof AbstractFurnaceScreen<?> abstractFurnaceScreen) {
                        ButtonUtil.drawTooltip(translatable("qualityofqueso.gui." + this.buttonName + "_button.furnace", abstractFurnaceScreen.getScreenHandler().getOutputSlot().getStack().getName()), context, this.textRenderer, mouseX, mouseY);
                        return;
                    }
                }

                ItemStack cursorStack = this.screenHandler.getCursorStack();
                if (!cursorStack.isEmpty()) {
                    String tooltip = "_button.with_cursor_stack";
                    net.minecraft.text.Text itemName = cursorStack.getItemName();
                    if (cursorStack.isOf(Items.ENCHANTED_BOOK)) {
                        ItemEnchantmentsComponent enchantments = EnchantmentHelper.getEnchantments(cursorStack);
                        List<String> cursorEnchantments = new ArrayList<>();
                        for (RegistryEntry<Enchantment> enchantment : enchantments.getEnchantments()) {
                            cursorEnchantments.add(getEnchantmentName(enchantment));
                        }
                        String collection = String.join(",", cursorEnchantments);
                        tooltip = "_button.with_cursor_enchantment_stack";
                        itemName = literal(collection);
                    } else if (cursorStack.isOf(Items.FIREWORK_ROCKET)) {
                        FireworksComponent firework = cursorStack.get(DataComponentTypes.FIREWORKS);
                        if (firework != null) {
                            itemName = literal(String.valueOf(firework.flightDuration())).formatted(Formatting.BOLD);
                            tooltip = "_button.with_cursor_firework_stack";
                        }
                    } else if (cursorStack.isOf(Items.POTION) || cursorStack.isOf(Items.SPLASH_POTION) || cursorStack.isOf(Items.LINGERING_POTION)) {
                        tooltip = "_button.with_cursor_potion_stack";
                    } else if (cursorStack.isOf(Items.TIPPED_ARROW)) {
                        tooltip = "_button.with_cursor_tipped_arrow_stack";
                    }
                    ButtonUtil.drawTooltip(translatable("qualityofqueso.gui." + this.buttonName + tooltip, itemName), context, this.textRenderer, mouseX, mouseY);
                } else if (!this.searchFieldText.isEmpty()) {
                    ButtonUtil.drawTooltip(this.searchFieldText.startsWith("#") ?
                            translatable("qualityofqueso.gui." + this.buttonName + "_button.with_search_query.tag", this.searchFieldText.substring(1)) :
                            this.searchFieldText.startsWith("!") ?
                                    translatable("qualityofqueso.gui." + this.buttonName + "_button.with_search_query.exclude", this.searchFieldText.substring(1)) :
                                    this.searchFieldText.startsWith(":") ?
                                            translatable("qualityofqueso.gui." + this.buttonName + "_button.with_search_query.match", this.searchFieldText.substring(1)) :
                                            translatable("qualityofqueso.gui." + this.buttonName + "_button.with_search_query", this.searchFieldText), context, this.textRenderer, mouseX, mouseY);
                } else {
                    if (options().helpfulTooltips) {
                        ButtonUtil.drawTooltip(translatable("qualityofqueso.gui." + this.buttonName + "_button"), context, this.textRenderer, mouseX, mouseY);
                    }
                }
            }
        }
    }
}