package net.dillon.qualityofqueso.helper;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.dillon.qualityofqueso.option.eum.general.Theme;
import net.dillon.qualityofqueso.screen.HudPositionsScreen;
import net.dillon.qualityofqueso.util.VisualTimeTracker;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.CommonColors;
import net.minecraft.world.Container;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import org.lwjgl.sdl.SDLKeyboard;

import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

import static net.dillon.dillonlib.task.ClientTasks.*;
import static net.dillon.dillonlib.util.Arithmetics.roundToHundredths;
import static net.dillon.qualityofqueso.helper.ManagementHelper.*;
import static net.dillon.qualityofqueso.helper.MethodHelper.*;
import static net.dillon.qualityofqueso.helper.ModConstants.*;
import static net.dillon.qualityofqueso.option.OptionInstances.client;

/**
 * Utility class for GUI-related things.
 */
public class GuiHelper {

    /**
     * Draws a tooltips on a screen for anything other than a search bar.
     */
    public static void drawTooltip(Component tooltip, GuiGraphicsExtractor graphics, Font font, int mouseX, int mouseY) {
        drawTooltip(tooltip, graphics, font, mouseX, mouseY, false);
    }

    /**
     * Draws a tooltips in a screen.
     */
    public static void drawTooltip(Component tooltip, GuiGraphicsExtractor graphics, Font font, int mouseX, int mouseY, boolean searchBar) {
        Screen screen = getScreen();
        boolean validScreen = isValidScreen(screen) || isOtherValidScreen(screen);
        int x = validScreen ? getTooltipX(graphics, screen, mouseX) : mouseX;
        int y = validScreen ? getTooltipY(CURRENT_CONTAINER, screen, mouseY, searchBar) : mouseY;
        graphics.setTooltipForNextFrame(font, font.split(tooltip, 200), x, y);
    }

    /**
     * @return the {@code X-position} for tooltips. Only counts for {@link AbstractContainerScreen}s and {@link InventoryScreen}s.
     */
    public static int getTooltipX(GuiGraphicsExtractor graphics, Screen screen, int mouseX) {
        if (!client().general().tooltips.ddefault()) {
            return mouseX;
        }
        int x = !client().misc().noRecipeBookShift && screen instanceof AbstractRecipeBookScreen<?> recipeBookScreen && getRecipeBookComponent(recipeBookScreen).isVisible() ? 161 : 84;
        return graphics.guiWidth() / 2 + x;
    }

    /**
     * @return the {@code Y-position} for tooltips. Only counts for {@link AbstractContainerScreen}s, {@link InventoryScreen}s, and secondary screens.
     */
    public static int getTooltipY(Container container, Screen screen, int mouseY, boolean searchBar) {
        if (!client().general().tooltips.ddefault() || (!(screen instanceof AbstractContainerScreen<?>))) {
            return mouseY;
        }

        int containerY = getContainerY(container);
        int y = 0;
        int l;
        if (screen instanceof InventoryScreen inventoryScreen) {
            l = client().management().layout.horizontal() ? -12 : 8;
            if (!client().management().layout.horizontal()) {
                l += (RENDERED_BUTTONS > 4 ? l * 2 : l);
            }
            y = getTopPos(inventoryScreen) + getTitleLabelY(inventoryScreen) + containerY + l;
        } else if (screen instanceof AbstractContainerScreen<?> abstractContainerScreen) {
            l = client().management().layout.horizontal() && abstractContainerScreen instanceof ContainerScreen ? 14 : 36;
            if (!client().management().layout.horizontal()) {
                if (RENDERED_BUTTONS > 8) {
                    l += l / 3 + (RENDERED_BUTTONS > 10 ? 12 : 0);
                } else if (RENDERED_BUTTONS > 4) {
                    l += 12;
                    if (RENDERED_BUTTONS > 6) {
                        l += 10;
                    }
                }
            }
            y = getTopPos(abstractContainerScreen) + getTitleLabelY(abstractContainerScreen) + containerY + l;
        }

        if (searchBar && client().management().layout.horizontal()) {
            y -= 64;
        }

        if (isOtherValidScreen(screen)) {
            y += 22;
        }
        return y;
    }

    /**
     * @return the dot tooltips for enhanced durability tooltips.
     */
    private static Component dotTooltip(ItemStack stack) {
        return Component.literal("•")
                .withColor(getDurabilityTooltipColor(stack));
    }

    /**
     * @return the better durability tooltips with colors.
     */
    private static Component enhancedDurabilityTooltip(ItemStack stack) {
        int color = getDurabilityTooltipColor(stack);
        int percentage = (int) ((roundToHundredths(getItemHealthPercentage(stack))) * 100);
        int damage = stack.getMaxDamage() - stack.getDamageValue();

        Component maxItemDurability = Component.literal(String.valueOf(stack.getMaxDamage()))
                .withColor(DEFAULT_LOCKED_SLOT_COLOR);

        Component damageTooltip = Component.literal(String.valueOf(damage))
                .withColor(
                        damage == stack.getMaxDamage()
                                ? DEFAULT_LOCKED_SLOT_COLOR
                                : color
                )
                .copy()
                .append(Component.literal("/")
                        .withColor(CommonColors.WHITE)
                )
                .copy()
                .append(maxItemDurability);

        Component percentageTooltip = Component.literal(String.valueOf(percentage))
                .append("%")
                .withColor(color);

        Component finalTooltip = Component.empty();
        boolean showPercentage = client().enhancedDurabilityTooltips().showPercentage;
        boolean showDamageValue = client().enhancedDurabilityTooltips().showDamageValue;

        if (showDamageValue) {
            finalTooltip = finalTooltip.copy()
                    .append(bracketTooltip(damageTooltip))
                    .append(showPercentage ? " " : "");
        }

        if (showPercentage) {
            finalTooltip = finalTooltip.copy()
                    .append(bracketTooltip(percentageTooltip));
        }

        return finalTooltip;
    }

    /**
     * Accepts the dot durability tooltips.
     */
    public static void acceptDotTooltip(List<Component> lines, ItemStack stack) {
        Component styledHoverName = stack.getStyledHoverName();
        if (!client().enhancedDurabilityTooltips().enableEnhancedDurabilityTooltips || !client().enhancedDurabilityTooltips().showDot) {
            lines.add(styledHoverName);
            return;
        }

        if (!isValidPercentage(stack) || getItemHealthPercentage(stack) >= 1.0F) {
            lines.add(styledHoverName);
            return;
        }

        lines.add(
                stack.getStyledHoverName()
                        .copy()
                        .append(Component.literal(" "))
                        .copy()
                        .append(dotTooltip(stack))
        );
    }

    /**
     * Accepts better durability tooltips.
     */
    public static void acceptEnhancedDurabilityTooltips(Consumer<Component> builder, ItemStack stack) {
        if (!client().enhancedDurabilityTooltips().enableEnhancedDurabilityTooltips || !(client().enhancedDurabilityTooltips().showPercentage || client().enhancedDurabilityTooltips().showDamageValue)) {
            return;
        }

        if (!isValidPercentage(stack)) {
            return;
        }

        builder.accept(GuiHelper.enhancedDurabilityTooltip(stack));
    }

    /**
     * @return a bracketed tooltips.
     */
    private static Component bracketTooltip(Component entry) {
        int gray = CommonColors.LIGHT_GRAY;
        return Component.literal("[")
                .withColor(gray)
                .copy()
                .append(entry)
                .copy()
                .append("]")
                .withColor(gray);
    }

    /**
     * @return if the stack has the durability property.
     */
    private static boolean isValidPercentage(ItemStack stack) {
        return getItemHealthPercentage(stack) > 0.0F && !Float.isNaN(getItemHealthPercentage(stack));
    }

    /**
     * @return the correct tooltips color to use for durability based on item health.
     */
    private static int getDurabilityTooltipColor(ItemStack stack) {
        float healthPercentage = getItemHealthPercentage(stack);

        if (healthPercentage < 0.11F) {
            return CommonColors.RED;
        } else if (healthPercentage < 0.21F) {
            return CommonColors.SOFT_RED;
        } else if (healthPercentage < 0.41F) {
            return CommonColors.YELLOW;
        } else if (healthPercentage < 0.61F) {
            return CommonColors.SOFT_YELLOW;
        } else if (healthPercentage < 0.80F) {
            return SOFT_GREEN;
        }

        return CommonColors.GREEN;
    }

    /**
     * @return if the user is positioning HUD elements.
     */
    public static boolean isPositioningElements() {
        return getMinecraft().gui.screen() instanceof HudPositionsScreen;
    }

    /**
     * @return the player's main hand stack.
     */
    public static ItemStack getMainHandStack(LocalPlayer player) {
        return player.getMainHandItem();
    }

    /**
     * @return the player's off hand stack.
     */
    public static ItemStack getOffHandStack(LocalPlayer player) {
        return player.getOffhandItem();
    }

    /**
     * @return text with italic and gray.
     */
    public static Component ofItalicAndGray(Component text) {
        return text.copy().withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.GRAY);
    }

    /**
     * @return if the player is holding an item in their offhand or mainhand, via a string search.
     */
    public static boolean isHoldingItem(LocalPlayer player, String itemName) {
        String mainHandItem = getMainHandStack(player).getItem().toString();
        String offHandItem = getOffHandStack(player).getItem().toString();

        return mainHandItem.equals(itemName) || offHandItem.equals(itemName);
    }

    /**
     * @return if the player is holding an item via a component search.
     */
    public static boolean isHoldingItem(LocalPlayer player, DataComponentType<?> component) {
        return getMainHandStack(player).has(component) || getOffHandStack(player).has(component);
    }

    /**
     * @return the health percentage (or durability %) of an item.
     */
    public static float getItemHealthPercentage(ItemStack stack) {
        int maxDamage = stack.getMaxDamage();
        return Math.max(0.0F, ((float) maxDamage - stack.getDamageValue()) / maxDamage);
    }

    /**
     * @return the armor hotbar texture.
     */
    public static Identifier getArmorHotbarTexture() {
        return client().general().theme == Theme.TRUE_DARK
                ? ARMOR_HOTBAR_TRUE_DARK
                : ARMOR_HOTBAR;
    }

    /**
     * @return true if the stack has the infinity enchantment.
     */
    public static boolean hasInfinity(ItemStack stack) {
        ItemEnchantments enchantments = stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        if (!(stack.getItem() instanceof BowItem)) {
            return false;
        }
        for (Object2IntMap.Entry<Holder<Enchantment>> enchantment : enchantments.entrySet()) {
            if (enchantment.getKey().is(Enchantments.INFINITY)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Draws the visual time clock on the screen.
     */
    public static void drawVisualTimeClock(GuiGraphicsExtractor graphics, int x, int y, int size, boolean screen) {
        Minecraft minecraft = getMinecraft();
        boolean notOverworld = minecraft.level != null && minecraft.level.dimension() != Level.OVERWORLD;
        if (!isPositioningElements() && !screen && notOverworld) {
            return;
        }

        long visualTime = VisualTimeTracker.getVisualTime(minecraft);
        int clockFrame = Math.floorMod((int) ((visualTime * 64L) / 24000L), 64);
        if (notOverworld) {
            clockFrame = Math.floorMod((int) ((minecraft.level.getGameTime() * 64L) / 20L), 64);
        }
        Identifier clockTexture = Identifier.withDefaultNamespace("textures/item/clock_" + String.format(Locale.ROOT, "%02d", clockFrame) + ".png");
        blitTexture(
                graphics,
                clockTexture,
                x,
                y,
                size,
                size
        );
    }

    /**
     * Auto-focuses an element and records the captured input to add to the {@link EditBox}.
     */
    public static void autoFocusElement(EditBox editBox, KeyEvent event, boolean focus) {
        autoFocusElement(editBox, event, focus, false);
    }

    /**
     * Allows for overriding of focus to always input character typed.
     * @since mc26.3
     */
    public static void autoFocusElement(EditBox editBox, KeyEvent event, boolean focus, boolean overrideFocus) {
        if (editBox == null) {
            return;
        }
        boolean wasFocused = editBox.isFocused();
        editBox.setFocused(focus);

        if (!focus) {
            return;
        }

        Screen screen = getScreen();
        if (screen != null) {
            screen.setFocused(editBox);
        }

        if (!wasFocused || overrideFocus) {
            String input = SDLKeyboard.SDL_GetKeyName(event.keycode());

            if (input == null || input.length() > 1) {
                return;
            }

            if (!event.hasShiftDown()) {
                input = input.toLowerCase();
            }

            editBox.setValue(editBox.getValue() + input);
        }
    }
}