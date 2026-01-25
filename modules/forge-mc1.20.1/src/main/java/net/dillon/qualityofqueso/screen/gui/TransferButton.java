package net.dillon.qualityofqueso.screen.gui;

import net.dillon.qualityofqueso.keybind.ModKeybinds;
import net.dillon.qualityofqueso.util.ButtonUtil;
import net.dillon.qualityofqueso.util.ModTexts;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractFurnaceScreen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static net.dillon.qualityofqueso.main.QoQ.options;
import static net.dillon.qualityofqueso.util.ButtonUtil.*;

/**
 * A representation of a transfer button.
 */
public class TransferButton extends Button {
    private final AbstractContainerMenu screenHandler;
    protected final Supplier<String> searchFieldText;
    protected final Font font;
    protected final String buttonName;
    private final Supplier<Boolean> canBeActive;

    /**
     * Constructs a default transfer button.
     */
    public TransferButton(AbstractContainerMenu screenHandler, Font font, Supplier<String> searchFieldText, int x, int y, String buttonName, OnPress onPress) {
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
    public TransferButton(AbstractContainerMenu screenHandler, Font font, Supplier<String> searchFieldText, int x, int y, String buttonName, OnPress onPress, Supplier<Boolean> canBeActive) {
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
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!this.canBeActive.get()) {
            return false;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    /**
     * Renders a transfer button texture.
     */
    @Unique
    protected void renderButtonTexture(String id, int mouseX, int mouseY, boolean transferable, AbstractWidget buttonReference, GuiGraphics
            context) {
        String transferableString = !this.screenHandler.getCarried().isEmpty() ?
                "_with_stack.png" : this.searchFieldText.get().startsWith("!") ?
                "_exclude.png" : this.searchFieldText.get().startsWith("#") ?
                "_with_tag.png" : this.searchFieldText.get().startsWith(":") ?
                "_match.png" : ".png";
        String appended = transferable ? transferableString : ".png";
        Screen screen = Minecraft.getInstance().screen;
        if (screen != null) {
            if (isBrewingStandScreen(screen) || isFurnaceScreen(screen)) {
                appended = ".png";
            }
        }
        context.blit(ResourceLocation.parse("qualityofqueso:textures/gui/" + id + appended), buttonReference.getX() - 1, buttonReference.getY() - 1, 0.0F, 0.0F, 12, 12, 12, 12);
        if (Screen.hasControlDown()) {
            boolean inventoryButton = this.buttonName.equals("transfer_inventory");
            boolean containerButton = this.buttonName.equals("transfer_container");
            boolean validName = inventoryButton || containerButton;
            if (validName) {
                if (options().showButtonShortcuts) {
                    if (inventoryButton && ModKeybinds.MOVE_INVENTORY.getKey() == ModKeybinds.MOVE_INVENTORY.getDefaultKey()) {
                        ButtonUtil.drawButtonTexture(context, "transfer_inventory_button_shortcut_key", this);
                    } else if (containerButton && ModKeybinds.MOVE_CONTAINER.getKey() == ModKeybinds.MOVE_CONTAINER.getDefaultKey()) {
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
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float deltaTicks) {
        this.active = this.canBeActive.get();

        if (this.canBeActive.get()) {
            this.renderButtonTexture(this.isMouseOver(mouseX, mouseY) ?
                    this.buttonName + "_button_hovered" :
                    this.buttonName + "_button", mouseX, mouseY, true, this, graphics);
        } else {
            this.renderButtonTexture(this.buttonName + "_button_inactive", mouseX, mouseY, true, this, graphics);
        }

        if (this.isMouseOver(mouseX, mouseY)) {
            if (this.active) {
                Screen screen = Minecraft.getInstance().screen;
                if (screen != null) {
                    if (isBrewingStandScreen(screen)) {
                        ButtonUtil.drawTooltip(Component.translatable("qualityofqueso.gui." + this.buttonName + "_button.brewing_stand"), graphics, this.font, mouseX, mouseY);
                        return;
                    } else if (screen instanceof AbstractFurnaceScreen<?> abstractFurnaceScreen) {
                        ButtonUtil.drawTooltip(Component.translatable("qualityofqueso.gui." + this.buttonName + "_button.furnace", abstractFurnaceScreen.getMenu().getSlot(2).getItem().getHoverName()), graphics, this.font, mouseX, mouseY);
                        return;
                    }
                }
                ItemStack cursorStack = this.screenHandler.getCarried();
                if (!cursorStack.isEmpty()) {
                    String tooltip = "_button.with_cursor_stack";
                    Component itemName = cursorStack.getHoverName();
                    if (cursorStack.is(Items.ENCHANTED_BOOK)) {
                        List<String> cursorEnchantments = new ArrayList<>();
                        for (Map.Entry<Enchantment, Integer> cursorEntry : EnchantmentHelper.getEnchantments(cursorStack).entrySet()) {
                            cursorEnchantments.add(getEnchantmentName(cursorEntry));
                        }
                        String collection = String.join(",", cursorEnchantments);
                        tooltip = "_button.with_cursor_enchantment_stack";
                        itemName = Component.literal(collection);
                    } else if (cursorStack.is(Items.FIREWORK_ROCKET)) {
                        CompoundTag tag = cursorStack.getTag();
                        if (tag != null && tag.contains("Fireworks")) {
                            CompoundTag fireworks = tag.getCompound("Fireworks");
                            if (fireworks.contains("Flight")) {
                                int flight = fireworks.getByte("Flight");
                                itemName = Component.literal(String.valueOf(flight)).withStyle(ChatFormatting.BOLD);
                                tooltip = "_button.with_cursor_firework_stack";
                            }
                        }
                    } else if (cursorStack.is(Items.POTION) || cursorStack.is(Items.SPLASH_POTION) || cursorStack.is(Items.LINGERING_POTION)) {
                        tooltip = "_button.with_cursor_potion_stack";
                    } else if (cursorStack.is(Items.TIPPED_ARROW)) {
                        tooltip = "_button.with_cursor_tipped_arrow_stack";
                    }
                    ButtonUtil.drawTooltip(Component.translatable("qualityofqueso.gui." + this.buttonName + tooltip, itemName), graphics, this.font, mouseX, mouseY);
                } else if (!this.searchFieldText.get().isEmpty()) {
                    ButtonUtil.drawTooltip(this.searchFieldText.get().startsWith("#") ?
                            Component.translatable("qualityofqueso.gui." + this.buttonName + "_button.with_search_query.tag", this.searchFieldText.get().substring(1)) :
                            this.searchFieldText.get().startsWith("!") ?
                                    Component.translatable("qualityofqueso.gui." + this.buttonName + "_button.with_search_query.exclude", this.searchFieldText.get().substring(1)) :
                                    this.searchFieldText.get().startsWith(":") ?
                                            Component.translatable("qualityofqueso.gui." + this.buttonName + "_button.with_search_query.match", this.searchFieldText.get().substring(1)) :
                                            Component.translatable("qualityofqueso.gui." + this.buttonName + "_button.with_search_query", this.searchFieldText), graphics, this.font, mouseX, mouseY);
                } else {
                    if (options().helpfulTooltips) {
                        ButtonUtil.drawTooltip(Component.translatable("qualityofqueso.gui." + this.buttonName + "_button"), graphics, this.font, mouseX, mouseY);
                    }
                }
            }
        }
    }
}