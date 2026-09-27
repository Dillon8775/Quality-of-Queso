package net.dillon.qualityofqueso.hud;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

import static net.dillon.qualityofqueso.helper.GuiHelper.getItemHealthPercentage;
import static net.dillon.qualityofqueso.option.OptionInstances.client;

/**
 * Extracts items with low durability.
 */
public class LowItemHealthHudElement extends ModHudElement {

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics) {
        for (int i = 0; i < minecraft.player.getInventory().getContainerSize() - 34; i++) {
            ItemStack item = minecraft.player.getInventory().getItem(i);
            if (getItemHealthPercentage(item) < 0.11F) {
                renderWarningIndicator(graphics, i, 0, null, client().hud().otherElementsY);
            }
        }

        ItemStack offHandItem = minecraft.player.getOffhandItem();
        if (!offHandItem.isEmpty()) {
            if (client().hud().coloredHighlighting && getItemHealthPercentage(offHandItem) < 0.41F) {
                renderHighlightedArmorSlot(HOTBAR_SELECTION_SPRITE, graphics, EquipmentSlot.OFFHAND, false, 0, 1.0F);
            }
            if (client().hud().warningIndicators && getItemHealthPercentage(offHandItem) < 0.11F) {
                renderWarningIndicator(graphics, 0, 0, EquipmentSlot.OFFHAND, client().hud().otherElementsY);
            }
        }
    }
}