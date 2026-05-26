package net.dillon.qualityofqueso.helper;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.dillon.qualityofqueso.instance.QuesoScreen;
import net.dillon.qualityofqueso.widget.WidgetLayout;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.CommonColors;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;

import java.util.List;
import java.util.Set;

import static net.dillon.qualityofqueso.helper.ManagementHelper.*;
import static net.dillon.qualityofqueso.helper.MethodHelper.*;
import static net.dillon.qualityofqueso.helper.ModHelper.*;
import static net.dillon.qualityofqueso.util.ModConstants.*;

/**
 * Utility class for GUI-related things.
 */
public class GuiHelper {

    /**
     * @return the GUI width.
     */
    public static int getGuiWidth(GuiGraphics context) {
        return context.guiWidth() / 2;
    }

    /**
     * @return the GUI height.
     */
    public static int getGuiHeight(GuiGraphics context) {
        return context.guiHeight() - 20;
    }

    /**
     * Draws a tooltip on a screen for anything other than a search bar.
     */
    public static void drawTooltip(Component tooltip, GuiGraphics graphics, Font font, int mouseX, int mouseY) {
        drawTooltip(tooltip, graphics,font, mouseX, mouseY, false);
    }

    /**
     * Draws a tooltip in a screen.
     */
    public static void drawTooltip(Component tooltip, GuiGraphics graphics, Font font, int mouseX, int mouseY, boolean searchBar) {
        Screen screen = getCurrentScreen();
        boolean validScreen = isValidScreen(screen) || isOtherValidScreen(screen);
        int x = validScreen ? getTooltipX(graphics, screen, mouseX) : mouseX;
        int y = validScreen ? getTooltipY(CURRENT_CONTAINER, screen, mouseY, searchBar) : mouseY;
        graphics.renderTooltip(font, font.split(tooltip, 200), x, y);
    }

    /**
     * Draws rendered text on the screen.
     */
    @Deprecated
    public static void drawTextTooltip(Component tooltip, GuiGraphics graphics, Font font, Container container, AbstractContainerScreen<?> screen) {
        WidgetLayout widgetLayout = ((QuesoScreen)screen).getWidgetLayout();
        int width = isInventoryScreen(screen)
                && !clientOptionsInstance().getManagementOptions().layout.horizontal()
                && RENDERED_BUTTONS > 0
                && widgetLayout != null && widgetLayout.hasTooManyEffects(Minecraft.getInstance().player)
                ? 125 : 150;
        int x = getTextTooltipX(screen, graphics, font, tooltip, width);
        int y = getTextTooltipY(container, screen);
        graphics.renderTooltip(font, font.split(tooltip, width), x, y);
    }

    /**
     * @return the width to render tooltips.
     */
    public static List<ClientTooltipComponent> getTooltipLines(Font font, Component tooltip, int width) {
        return font.split(tooltip, width).stream()
                .map(ClientTooltipComponent::create)
                .toList();
    }

    /**
     * @return the {@code X-position} for tooltips. Only counts for {@link AbstractContainerScreen}s and {@link InventoryScreen}s.
     */
    public static int getTooltipX(GuiGraphics graphics, Screen screen, int mouseX) {
        if (!clientOptionsInstance().getGeneralOptions().tooltips.ddefault()) {
            return mouseX;
        }
        int x = clientOptionsInstance().getMiscOptions().noRecipeBookShift && screen instanceof InventoryScreen recipeBookScreen && getRecipeBookComponent(recipeBookScreen).isVisible() ? 161 : 84;
        return graphics.guiWidth() / 2 + x;
    }

    /**
     * @return the {@code Y-position} for tooltips. Only counts for {@link AbstractContainerScreen}s, {@link InventoryScreen}s, and secondary screens.
     */
    public static int getTooltipY(Container container, Screen screen, int mouseY, boolean searchBar) {
        if (!clientOptionsInstance().getGeneralOptions().tooltips.ddefault() || (!(screen instanceof AbstractContainerScreen<?>))) {
            return mouseY;
        }

        int containerY = getContainerY(container);
        int y = 0;
        int l;
        if (screen instanceof InventoryScreen inventoryScreen) {
            l = clientOptionsInstance().getManagementOptions().layout.horizontal() ? -12 : 8;
            if (!clientOptionsInstance().getManagementOptions().layout.horizontal()) {
                l += (RENDERED_BUTTONS > 4 ? l * 2 : l);
            }
            y = getTopPos(inventoryScreen) + getTitleLabelY(inventoryScreen) + containerY + l;
        } else if (screen instanceof AbstractContainerScreen<?> abstractContainerScreen) {
            l = clientOptionsInstance().getManagementOptions().layout.horizontal() && abstractContainerScreen instanceof ContainerScreen ? 14 : 36;
            if (!clientOptionsInstance().getManagementOptions().layout.horizontal()) {
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

        if (searchBar && clientOptionsInstance().getManagementOptions().layout.horizontal()) {
            y -= 64;
        }

        if (isOtherValidScreen(screen)) {
            y += 22;
        }
        return y;
    }

    /**
     * @return the x-position for text-rendered tooltips.
     */
    public static int getTextTooltipX(AbstractContainerScreen<?> screen, GuiGraphics graphics, Font font, Component tooltip, int width) {
        int textWidth = 0;
        for (ClientTooltipComponent line : getTooltipLines(font, tooltip, width)) {
            int lineWidth = line.getWidth(font);
            if (lineWidth > textWidth) {
                textWidth = lineWidth;
            }
        }
        int guiWidth = graphics.guiWidth() / 2;
        int x = guiWidth - textWidth - 108;
        if (screen instanceof InventoryScreen recipeBookScreen && getRecipeBookComponent(recipeBookScreen).isVisible()) {
            x += 94;
        }

        int padding = 4;
        int maxX = graphics.guiWidth() - textWidth - padding;

        if (x < padding) {
            x = padding;
        } else if (x > maxX) {
            x = maxX;
        }

        return x;
    }

    /**
     * @return the y-position for text-rendered tooltips.
     */
    public static int getTextTooltipY(Container container, AbstractContainerScreen<?> screen) {
        int y = !clientOptionsInstance().getMiscOptions().noRecipeBookShift && screen instanceof InventoryScreen recipeBookScreen && getRecipeBookComponent(recipeBookScreen).isVisible() ? 82 : -40;
        if (isDropperDispenserOrHopperScreen(screen)) {
            y = -10;
        }
        return getTopPos(screen) + getTitleLabelY(screen) + getContainerY(container) + y;
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
     * @return the item in the given slot.
     */
    public static ItemStack getItemBySlot(Minecraft minecraft, EquipmentSlot slot) {
        return minecraft.player.getItemBySlot(slot);
    }

    /**
     * @return text with italic and gray.
     */
    public static Component ofItalicAndGray(Component text) {
        return text.copy().withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.GRAY);
    }

    /**
     * @return if an item and item components equal an item.
     */
    public static boolean itemMatchesInventoryItem(ItemStack mainStack, ItemStack otherStack) {
        return clientOptionsInstance().getItemCounterOptions().onlyCountMatchingItems ? ItemStack.isSameItemSameComponents(mainStack, otherStack) : otherStack.is(mainStack.getItem());
    }

    /**
     * @return if a armor slot was changed at all.
     */
    public static boolean armorChanged(int slotIndex, ItemStack current) {
        ItemStack previous = LAST_ARMOR_STACKS[slotIndex];
        return !ItemStack.isSameItemSameComponents(previous, current);
    }

    /**
     * @return if the stack is an arrow.
     */
    public static boolean isStackArrow(ItemStack stack) {
        return stack.is(ItemTags.ARROWS);
    }

    /**
     * @return an increased X-value, based on the user's main hand.
     */
    public static int increasedBasedOnHand(Minecraft minecraft, int negIncrease, boolean reverse) {
        if (reverse) {
            return isLeftHanded(minecraft) ? negIncrease : Math.abs(negIncrease);
        } else {
            return isLeftHanded(minecraft) ? Math.abs(negIncrease) : negIncrease;
        }
    }

    /**
     * Plays a generic ding sound.
     */
    public static void playDingSound(Minecraft minecraft) {
        minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.ARROW_HIT_PLAYER, 0.2F, 0.72F));
    }

    /**
     * @return if projectile from either of the player's hand.
     */
    public static ItemStack getProjectileFromActiveHand(Minecraft minecraft) {
        ItemStack offHandItem = minecraft.player.getOffhandItem();
        if (isProjectileWeapon(offHandItem.getItem())) {
            return minecraft.player.getProjectile(offHandItem);
        } else {
            return minecraft.player.getProjectile(minecraft.player.getMainHandItem());
        }
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
     * @return if stack is a projectile weapon.
     */
    public static boolean isProjectileWeapon(Item item) {
        return item instanceof BowItem || item instanceof CrossbowItem;
    }

    /**
     * @return if the arrow count can be displayed at all.
     */
    public static boolean holdingArrowDisplayableProjectileWeapon(Minecraft minecraft, ItemStack stack) {
        return clientOptionsInstance().getItemCounterOptions().arrowCounter && !minecraft.player.isCreative() && (stack.getItem() instanceof BowItem || stack.getItem() instanceof CrossbowItem);
    }

    /**
     * @return if the always show arrow counter is enabled.
     */
    public static boolean isAlwaysShowArrowCounterEnabled(Minecraft minecraft) {
        return clientOptionsInstance().getItemCounterOptions().arrowCounter && clientOptionsInstance().getItemCounterOptions().alwaysShowArrowCounter && !minecraft.player.isCreative();
    }

    /**
     * @return the health percentage (or durability %) of an item.
     */
    public static float getItemHealthPercentage(ItemStack stack) {
        int maxDamage = stack.getMaxDamage();
        return Math.max(0.0F, ((float) maxDamage - stack.getDamageValue()) / maxDamage);
    }

    /**
     * @return the correct sprite to use.
     */
    public static ResourceLocation getHighlightedSlotTexture(Minecraft minecraft, ResourceLocation defaultSprite, ItemStack stack, EquipmentSlot equipmentSlot) {
        float healthPercentage = getItemHealthPercentage(stack);

        if (!clientOptionsInstance().getHudOptions().coloredHighlighting) {
            return defaultSprite;
        } else if (healthPercentage < 0.21F) {
            return SLOT_CRITICAL;
        } else if (healthPercentage < 0.41F) {
            return SLOT_LOW;
        } else if (healthPercentage < 0.61F) {
            return SLOT_AVERAGE;
        } else if (healthPercentage < 1.0F) {
            return SLOT_GOOD;
        } else if ((clientOptionsInstance().getLockedSlotOptions().preventDropping || clientOptionsInstance().getLockedSlotOptions().showLock.inHud()) && isLockedHotbarSlot(minecraft, false) && equipmentSlot == null) {
            return SLOT_LOCKED;
        }
        return defaultSprite;
    }

    /**
     * @return if a hotbar slot is locked.
     */
    public static boolean isLockedHotbarSlot(Minecraft minecraft, boolean checkForEmpty) {
        if (!clientOptionsInstance().getLockedSlotOptions().lockedSlots) {
            return false;
        }

        Set<Integer> lockedPlayerSlots = ContainerHelper.getLockedSlots(false);
        if (checkForEmpty && lockedPlayerSlots.isEmpty()) {
            return false;
        }

        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null || (checkForEmpty && getMainHandStack(player).isEmpty())) {
            return false;
        }

        int selectedHotbarSlot = minecraft.player.getInventory().selected;
        return lockedPlayerSlots.contains(selectedHotbarSlot);
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
     * Draws an equipped stack item.
     */
    public static void drawItem(Minecraft minecraft, GuiGraphics context, ItemStack stack, int x, boolean overlay) {
        drawItem(minecraft, context, stack, x, overlay, 0);
    }

    /**
     * Draws an equipped stack item with a vertical offset.
     */
    public static void drawItem(Minecraft minecraft, GuiGraphics context, ItemStack stack, int x, boolean overlay, int yOffset) {
        int i = getGuiWidth(context);
        int y = getGuiHeight(context) + 1 + yOffset;
        int fx = i + x;
        if (!stack.isEmpty()) {
            context.renderFakeItem(stack, fx, y);
            if (overlay) {
                context.renderItemDecorations(minecraft.font, stack, fx, y, null);
            }
        }
    }

    /**
     * @return the count color to display.
     */
    public static int getCountColor(int count) {
        if (count < 6) {
            return CommonColors.RED;
        } else if (count < 11) {
            return CommonColors.SOFT_RED;
        } else if (count < 21) {
            return CommonColors.YELLOW;
        } else {
            return CommonColors.GREEN;
        }
    }

    /**
     * Iterates through transportable inventories, and returns a count to add.
     */
    public static int iterateTransportablesAndAddCount(ItemStack invStack, ItemStack heldStack, List<Integer> items) {
        int itemInTransportableCount = 0;

        ItemContainerContents container = invStack.get(DataComponents.CONTAINER);
        BundleContents bundleContents = invStack.get(DataComponents.BUNDLE_CONTENTS);

        if (container != null) {
            for (ItemStack containerStack : container.nonEmptyItems()) {
                if (itemMatchesInventoryItem(heldStack, containerStack)) {
                    itemInTransportableCount += containerStack.getCount();
                    items.add(containerStack.getCount());
                }
            }
        }
        if (bundleContents != null) {
            for (ItemStack bundleStack : bundleContents.items()) {
                if (itemMatchesInventoryItem(heldStack, bundleStack)) {
                    itemInTransportableCount += bundleStack.getCount();
                    items.add(bundleStack.getCount());
                }
            }
        }

        return itemInTransportableCount;
    }

    /**
     * Matches spectator GUI movement timing from a tick timer ending.
     */
    public static int getSpectatorAnimationYOffsetFromTicks(int currentTick, int timerEndTick, int animationTicks) {
        if (currentTick <= timerEndTick || animationTicks <= 0) {
            return 0;
        }

        int animationEndTick = timerEndTick + animationTicks;
        float alpha = Mth.clamp((float) (animationEndTick - currentTick) / animationTicks, 0.0F, 1.0F);
        return Mth.floor(22.0F * (1.0F - alpha));
    }

    /**
     * @return if the current tick is before the target tick.
     */
    public static boolean isBeforeTick(int currentTick, int targetTick) {
        return currentTick - targetTick < 0;
    }

    /**
     * @return if the tick is valid for the duration ticks.
     */
    public static boolean isWithinTickWindow(int currentTick, int startTick, int durationTicks) {
        if (durationTicks <= 0) {
            return false;
        }
        int elapsed = currentTick - startTick;
        return elapsed >= 0 && elapsed < durationTicks;
    }

    /**
     * Matches spectator GUI movement timing from a millisecond timer ending.
     */
    @Deprecated
    public static int getSpectatorAnimationYOffsetFromMillis(long currentTimeMillis, long timerEndMillis, long animationMillis) {
        if (currentTimeMillis <= timerEndMillis || animationMillis <= 0L) {
            return 0;
        }

        long animationEndMillis = timerEndMillis + animationMillis;
        float alpha = Mth.clamp((float) (animationEndMillis - currentTimeMillis) / animationMillis, 0.0F, 1.0F);
        return Mth.floor(22.0F * (1.0F - alpha));
    }

    /**
     * @return the display time for armor status.
     */
    public static int getDisplayTimeInTicks() {
        return (int) (clientOptionsInstance().getHudOptions().displayTime * 20);
    }

    /**
     * @return the actual animation time for the armor status.
     */
    public static int getAnimationTimeInTicks(Minecraft minecraft, boolean factorItemCounter) {
        if (!clientOptionsInstance().getHudOptions().animations || (factorItemCounter && isAlwaysShowArrowCounterEnabled(minecraft))) {
            return 0;
        }
        return (int) (clientOptionsInstance().getHudOptions().animationTime * 20);
    }
}