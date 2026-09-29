package net.dillon.qualityofqueso.hud;

import net.dillon.qualityofqueso.helper.ContainerHelper;
import net.minecraft.client.AttackIndicatorStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;

import java.util.Set;

import static net.dillon.dillonlib.task.ClientTasks.*;
import static net.dillon.qualityofqueso.helper.GuiHelper.*;
import static net.dillon.qualityofqueso.helper.ModConstants.*;
import static net.dillon.qualityofqueso.option.OptionInstances.client;

/**
 * Holds common methods for hud elements.
 */
public abstract class ModHudElement {
    protected static final Minecraft minecraft = getMinecraft();
    public static final Identifier HOTBAR_SELECTION_SPRITE = Identifier.withDefaultNamespace("hud/hotbar_selection");
    public static final Identifier HOTBAR_OFFHAND_SPRITE = Identifier.withDefaultNamespace("hud/hotbar_offhand_right");

    /**
     * Extracts the element.
     */
    public abstract void extractRenderState(GuiGraphicsExtractor graphics);

    /**
     * @return if the player is left-handed.
     */
    protected boolean isLeftHanded() {
        return getMinecraft().player.getMainArm().getOpposite() == HumanoidArm.RIGHT;
    }

    /**
     * @return if the arrow counter feature is enabled.
     */
    private static boolean isArrowCounterEnabled() {
        return client().itemCounter().arrowCounter;
    }

    /**
     * @return if the only show arrow counter is enabled.
     */
    protected static boolean isOnlyShowArrowCounterEnabled() {
        return isArrowCounterEnabled() && client().itemCounter().onlyShowArrowCounter && !getMinecraft().player.isCreative();
    }

    /**
     * @return if the always show arrow counter is enabled.
     */
    protected static boolean isAlwaysShowArrowCounterEnabled() {
        return isArrowCounterEnabled() && client().itemCounter().alwaysShowArrowCounter && !getMinecraft().player.isCreative();
    }

    /**
     * @return if the main or offhand has infinity.
     */
    public static boolean mainOrOffHandHasInfinity() {
        ItemStack mainHand = getMainHandStack(minecraft.player);
        ItemStack offHand = getOffHandStack(minecraft.player);
        return hasInfinity(mainHand) || hasInfinity(offHand);
    }

    /**
     * @return the actual animation time for the armor status.
     */
    public static int getAnimationTimeInTicks(boolean factorItemCounter) {
        if (!client().hud().animations || (factorItemCounter && isAlwaysShowArrowCounterEnabled())) {
            return 0;
        }
        return (int) (client().hud().animationTime * 20);
    }

    /**
     * Matches spectator GUI movement timing from a tick timer ending.
     */
    protected int getSpectatorAnimationYOffsetFromTicks(int currentTick, int timerEndTick, int animationTicks) {
        if (currentTick <= timerEndTick || animationTicks <= 0) {
            return 0;
        }

        int animationEndTick = timerEndTick + animationTicks;
        float alpha = Mth.clamp((float) (animationEndTick - currentTick) / animationTicks, 0.0F, 1.0F);
        return Mth.floor(22.0F * (1.0F - alpha));
    }

    /**
     * @return the display time for armor status.
     */
    public static int getDisplayTimeInTicks() {
        return (int) (client().hud().displayTime * 20);
    }

    /**
     * @return the item in the given slot.
     */
    protected ItemStack getItemBySlot(Minecraft minecraft, EquipmentSlot slot) {
        return minecraft.player.getItemBySlot(slot);
    }

    /**
     * @return the x-position that the slot should render under the armor item.
     */
    protected int getArmorBarX(GuiGraphicsExtractor graphics) {
        return getGuiWidth(graphics) + getEquipmentSlotX(EquipmentSlot.HEAD) - 3;
    }

    /**
     * @return the x-position that the highlighted slot should render under the armor item.
     */
    protected int getHighlightedSlotX(GuiGraphicsExtractor graphics, EquipmentSlot slot) {
        return getGuiWidth(graphics) + getEquipmentSlotX(slot) - 4;
    }

    /**
     * @return an increased X-value, based on the user's main hand.
     */
    protected int increasedBasedOnHand(int negIncrease, boolean reverse) {
        if (reverse) {
            return isLeftHanded() ? negIncrease : Math.abs(negIncrease);
        } else {
            return isLeftHanded() ? Math.abs(negIncrease) : negIncrease;
        }
    }

    /**
     * @return the x-pos for each "equipment slot".
     */
    protected int getEquipmentSlotX(EquipmentSlot slot) {
        int base = isLeftHanded() ? -275 : 0;
        if (minecraft.options.attackIndicator().get() == AttackIndicatorStatus.HOTBAR) {
            base += increasedBasedOnHand(isLeftHanded() ? -22 : -24, true);
        }

        if (slot != EquipmentSlot.OFFHAND) {
            base += client().hud().armorStatusPosition[0];
        }

        return switch (slot) {
            case HEAD -> base + 100;
            case CHEST -> base + 120;
            case LEGS -> base + 140;
            case FEET -> base + 160;
            case OFFHAND -> base + (!isLeftHanded() ? -117 : 376);
            default -> 0;
        };
    }

    /**
     * @return the correct sprite to use.
     */
    public static Identifier getHighlightedSlotTexture(Identifier defaultSprite, ItemStack stack, EquipmentSlot equipmentSlot) {
        float healthPercentage = getItemHealthPercentage(stack);

        if (!client().hud().coloredHighlighting) {
            return defaultSprite;
        } else if (healthPercentage < 0.21F) {
            return SLOT_CRITICAL;
        } else if (healthPercentage < 0.41F) {
            return SLOT_LOW;
        } else if (healthPercentage < 0.61F) {
            return SLOT_AVERAGE;
        } else if (healthPercentage < 0.71F) {
            return SLOT_DECENT;
        } else if (healthPercentage < 1.0F) {
            return SLOT_GOOD;
        } else if ((client().lockedSlots().preventDropping || client().lockedSlots().showLock.inHud()) && isLockedHotbarSlot(false) && equipmentSlot == null) {
            return SLOT_LOCKED;
        }
        return defaultSprite;
    }

    /**
     * @return if a hotbar slot is locked.
     */
    public static boolean isLockedHotbarSlot(boolean checkForEmpty) {
        if (!client().lockedSlots().lockedSlots) {
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

        int selectedHotbarSlot = minecraft.player.getInventory().getSelectedSlot();
        return lockedPlayerSlots.contains(selectedHotbarSlot);
    }

    /**
     * Renders the highlighted texture around a slot.
     */
    protected void renderHighlightedArmorSlot(Identifier defaultSprite, GuiGraphicsExtractor graphics, EquipmentSlot slot, boolean warning, int yOffset, float alpha) {
        if (alpha <= 0.0F || !(client().hud().highlightArmor)) {
            return;
        }

        int yModifier = slot != EquipmentSlot.OFFHAND ? client().hud().armorStatusPosition[1] : client().hud().otherElementsY;
        drawSprite(
                graphics,
                warning ? SLOT_CRITICAL : getHighlightedSlotTexture(defaultSprite, getItemBySlot(minecraft, slot), slot),
                getHighlightedSlotX(graphics, slot),
                (getGuiHeight(graphics) - 3 + yOffset) + yModifier,
                24,
                23,
                alpha
        );
    }

    /**
     * Renders the {@code warning texture} around armor items.
     */
    protected void renderWarningIndicator(GuiGraphicsExtractor graphics, int slot, int itemX, EquipmentSlot equipmentSlot, int yOffset) {
        if (!client().hud().warningIndicators) {
            return;
        }

        drawSprite(
                graphics,
                Identifier.withDefaultNamespace("world_list/error_highlighted"),
                getGuiWidth(graphics) + (itemX != 0 ? itemX : equipmentSlot == null ? -78 + (slot * 20) : getEquipmentSlotX(equipmentSlot) + 10),
                getGuiHeight(graphics) + 4 + yOffset,
                16,
                16
        );
    }

    /**
     * Draws an equipped stack item.
     */
    protected void drawItem(GuiGraphicsExtractor context, ItemStack stack, int x, int yModifier, boolean overlay) {
        drawItem(context, stack, x, yModifier, overlay, 0);
    }

    /**
     * Draws an equipped stack item with a vertical offset.
     */
    protected void drawItem(GuiGraphicsExtractor context, ItemStack stack, int x, int yModifier, boolean overlay, int yOffset) {
        int i = getGuiWidth(context);
        int y = getGuiHeight(context) + 1 + yOffset;
        int fx = i + x;
        if (!stack.isEmpty()) {
            context.item(stack, fx, y + yModifier);
            if (overlay) {
                context.itemDecorations(minecraft.font, stack, fx, y + yModifier, null);
            }
        }
    }
}