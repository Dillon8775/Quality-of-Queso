package net.dillon.qualityofqueso.instance;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.dillon.qualityofqueso.instance.management.ManagementInstance;
import net.dillon.qualityofqueso.util.EnchantingHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

import static net.dillon.qualityofqueso.helper.GuiHelper.ofItalicAndGray;
import static net.dillon.qualityofqueso.helper.ManagementHelper.*;
import static net.dillon.qualityofqueso.helper.ModHelper.clientOptionsInstance;
import static net.dillon.qualityofqueso.helper.ModKeyMappingHelper.*;
import static net.dillon.qualityofqueso.util.EnchantingHelper.isEnchantmentInGroup;
import static net.dillon.qualityofqueso.util.ModConstants.MOVE_AMOUNT;

/**
 * Handles tooltip rendering for this mod.
 */
public class ModTooltipInstance extends ManagementInstance {

    public ModTooltipInstance(QuesoScreen screen) {
        super(screen);
    }

    /**
     * @return the original tooltip that is being displayed.
     */
    public List<Component> getOriginalTooltip() {
        return instance().getScreensHoveredSlot() == null ? null : instance().getScreensHoveredSlot().getItem().getTooltipLines(Item.TooltipContext.EMPTY, instance().getMinecraft().player, Minecraft.getInstance().options.advancedItemTooltips ? TooltipFlag.ADVANCED : TooltipFlag.NORMAL);
    }

    /**
     * Displays all item tags on the hovered item.
     */
    public void displayTagsOnItems(GuiGraphics graphics, Font font, int mouseX, int mouseY, CallbackInfo ci) {
        if (!hoveredSlotHasItem(instance().getScreensHoveredSlot())) {
            return;
        }

        ItemStack stack = instance().getScreensHoveredSlot().getItem();
        List<Component> originalTooltip = getOriginalTooltip();

        // Render tags for tag search queries
        String searchQuery = instance().getSearchFields().searchText();
        if (instance().getSearchFields().container() != null || instance().getSearchFields().inventory() != null) {
            // Exit if search query doesn't start with #
            if (!searchQuery.startsWith("#")) {
                return;
            }

            // Loop through item's tags
            for (TagKey<Item> tag : stack.getTags().toList()) {
                // Add each tag to the query hovered
                String location = tag.location().getNamespace().equals("c") ? "fabric:" + tag.location().getPath() : tag.location().toString();
                String tagString = "#" + location;
                originalTooltip.add(1, Component.literal(tagString).withStyle(ChatFormatting.LIGHT_PURPLE));
            }

            // If tags were found in the query add it to the tooltip and render
            // cancel out original method to prevent overlapping tooltips
            if (stack.getTags().toList().isEmpty()) {
                originalTooltip.add(1, Component.translatable("qualityofqueso.gui.no_tags_found").withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.GRAY));
            }
            graphics.setTooltipForNextFrame(font, originalTooltip, Optional.empty(), mouseX, mouseY);
            ci.cancel();
        }
    }

    /**
     * Displays tooltips for singular moving or dropping.
     */
    public void displaySingleMovingTooltips(GuiGraphics graphics, Font font, int mouseX, int mouseY, CallbackInfo ci) {
        // If the user is attempting to lock slots or drag sort (and as long as they don't have "CTRL"/management modifier down, to prevent redundant removal of tooltips)...
        // ...then remove all tooltips, so the user can clearly read and see slots
        if (isExcludingOrLockingSlots() && !hasAnyManagementModifierDown()) {
            ci.cancel();
        }

        // Create variables to determine if the tooltips should be modified, based on quick dropping actions and moving singular items
        Slot hoveredSlot = instance().getScreensHoveredSlot();
        boolean hoveredSlotHasItem = hoveredSlotHasItem(hoveredSlot);

        // Check if the quick drop button itself is hovered
        boolean quickDropHovered = buttonHoveredAndActive(instance().getManagementButtons().quickDrop());

        // Check if any transfer button is hovered (this includes quick drop, transfer container, or transfer inventory)
        boolean buttonHovered = quickDropHovered || buttonHoveredAndActive(instance().getManagementButtons().transferContainer()) || buttonHoveredAndActive(instance().getManagementButtons().transferInventory());

        // Check if a shortcut key is held down
        // If quick drop is hovered, then we only check for if shift is held (to only drop one of each item)
        // Otherwise, check if the quick drop keys are down (CTRL + ALT), OR if CTRL/move single modifier itself is held down, to move singular items
        boolean hasKeyDown = quickDropHovered ? hasDropOnlyOneItemKeyDown() : hasAllQuickDropModifiersDown() || canScrollMoveAndHasScrollModifierDown();
        boolean droppingOne = hoveredSlotHasItem && hasDropOnlyOneItemKeyDown();

        // The boolean expression to modify tooltips based on the conditions above
        // We can modify tooltips if we are attempting to drop one, or if we have a shortcut key down and one of the buttons are hovered/the hovered slot has an item (for quick dropping)
        boolean bl = clientOptionsInstance().getManagementOptions().scrollMoving && (droppingOne || (hasKeyDown && (buttonHovered || hoveredSlotHasItem)));
        instance().setCanMoveOne(bl);
        boolean onlySingleMoveModifierDown = canScrollMoveAndHasScrollModifierDown() && !hasAllQuickDropModifiersDown() && !hasDropOnlyOneItemKeyDown();

        // Create a new tooltip to render
        List<Component> tooltipToRender = new ArrayList<>();

        // If bl is true, and the hovered slot's count is more than 1 (because if you are moving 1 singular, the count must be more than 1), OR if the user isn't hovering over a slot at all, begin modifying tooltips
        if (!isCreativeInventoryScreen(instance().getScreen()) && bl && !onlySingleMoveModifierDown && (hoveredSlot == null || hoveredSlot.getItem().getCount() > 1)) {
            // Create the new tooltip variable
            List<Component> moveAmountTooltip = new ArrayList<>();
            // Determine the translation for the tooltip
            // If the button is hovered, render "Move *count* of each". Otherwise, render "Move *count*"
            String translation = "qualityofqueso.gui.move_amount_each";

            boolean canRender = true;
            // If the quick drop button is hovered, render the drop amount of each stack
            if (quickDropHovered) {
                translation = "qualityofqueso.gui.quick_drop_button.move_amount";
            } else if (droppingOne) { // Otherwise, if we are dropping one, render the drop amount for the singular hovered item
                translation = "qualityofqueso.gui.quick_drop_button.move_amount.single";
                if (MOVE_AMOUNT == 1) {
                    canRender = false;
                }
            }

            // Create temp boolean to determine if we can continue adding tooltips after the fact, if the hovered item is found in the container (for quick dropping only)
            boolean canContinueToAddTooltips = true;
            boolean skip = false;
            // Create ignores locked slots variable
            Component ignoresLockedSlots = Component.translatable("qualityofqueso.gui.move_amount.ignores_locked_slots").withColor(clientOptionsInstance().getLockedSlotOptions().lockedSlotColor);
            // If the quick drop keys are down and the hovered slot has an item, continue through this statement
            if (hasAllQuickDropModifiersDown() && hoveredSlotHasItem(hoveredSlot)) {
                boolean containerScreen = isContainerScreen(instance().getScreen());
                // If no respective item was found in the container for quick dropping, tell the user "none of this item was found", therefor cannot drop.
                if (containerScreen && !shouldButtonBeActive(false, null)) {
                    moveAmountTooltip.add(Component.translatable("qualityofqueso.gui.quick_drop_button.no_items_found", hoveredSlot.getItem().getItemName()).copy()
                            .withStyle(ChatFormatting.RED));
                    canContinueToAddTooltips = false;
                } else { // Then check if we are attempting to drop only one of each item
                    if (hasDropOnlyOneItemKeyDown()) { // Display that item name w/ the move amount
                        moveAmountTooltip.add(Component.translatable("qualityofqueso.gui.quick_drop_button.move_amount.type",
                                getActualMoveAmount(), hoveredSlot.getItem().getItemName()));
                    } else { // Otherwise display that item name, no move amount because we are dropping full stacks
                        moveAmountTooltip.add(Component.translatable("qualityofqueso.gui.quick_drop_button.move_amount.type.all", hoveredSlot.getItem().getItemName()));
                        canContinueToAddTooltips = false;
                    }
                }
            } else { // Otherwise just add the raw translation for a singular move amount for a single item
                if (lockedSlotsInstance().droppingEntireLockedSlotStack()) {
                    translation = "qualityofqueso.gui.quick_drop_button.move_amount.full";
                    skip = true;
                }
                moveAmountTooltip.add(Component.translatable(translation, getActualMoveAmount()));
                if (lockedSlotsInstance().droppingEntireLockedSlotStack()) {
                    moveAmountTooltip.add(ignoresLockedSlots);
                }
            }

            // Cancel out if we cannot render tooltip
            if (!canRender && (!lockedSlotsInstance().isLockedSlot(hoveredSlot.index) || !lockedSlotsInstance().droppingEntireLockedSlotStack())) {
                return;
            }

            // Create helper tooltips for the user to use singular moving
            Component scroll = Component.translatable("qualityofqueso.gui.scroll_to_change_amount");
            Component reset = Component.translatable("qualityofqueso.gui.move_amount.reset");

            // Add those helper tooltips to the rendered tooltip if we can
            if (clientOptionsInstance().getGeneralOptions().tooltips.enabled() && canContinueToAddTooltips && !skip) {
                if (!(instance().getScreensHoveredSlot() != null && instance().getScreensHoveredSlot().hasItem() && (canScrollMoveAndHasScrollModifierDown() && !hasDropOnlyOneItemKeyDown()))) {
                    moveAmountTooltip.add(scroll);
                    moveAmountTooltip.add(reset);
                    moveAmountTooltip.add(ignoresLockedSlots);
                }
            }

            // As long as there was never an original tooltip to render, render the tooltip and return true
            tooltipToRender.addAll(moveAmountTooltip);

            // Create wrapped tooltip
            List<FormattedCharSequence> wrappedTooltip = new ArrayList<>();
            int maxWidth = 200;

            // Wrap the tooltip
            for (Component component : tooltipToRender) {
                wrappedTooltip.addAll(font.split(component, maxWidth));
            }

            // Set the tooltip
            graphics.setTooltipForNextFrame(font, wrappedTooltip, mouseX, mouseY);
            ci.cancel();
        }
    }

    /**
     * Displays all enchantment helper tooltips.
     */
    public void displayEnchantmentHelperTooltips(GuiGraphics graphics, Font font, int mouseX, int mouseY, CallbackInfo ci) {
        // Any further injection here will not be applied if the hovered slot is null or doesn't have an item
        if (!hoveredSlotHasItem(instance().getScreensHoveredSlot())) {
            return;
        }

        List<Component> originalTooltip = getOriginalTooltip();

        // Enchantment helper functionality
        // Goes through all enchantments on an enchanted book and determines what items the enchanted book itself can be applied to
        if (clientOptionsInstance().getMiscOptions().enchantmentHelper && instance().getScreensHoveredSlot().getItem().is(Items.ENCHANTED_BOOK)) {
            ItemEnchantments enchantments = instance().getScreensHoveredSlot().getItem().getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY);
            Set<Component> enchantmentApplicables = new HashSet<>();

            for (Object2IntMap.Entry<Holder<Enchantment>> enchantment : enchantments.entrySet()) {
                if (isEnchantmentInGroup(EnchantingHelper.ALL_PURPOSE_ENCHANTMENTS, enchantment)) {
                    enchantmentApplicables.add(ofItalicAndGray(Component.translatable("qualityofqueso.gui.all_purpose")));
                }
                if (isEnchantmentInGroup(EnchantingHelper.ARMOR_ENCHANTMENTS, enchantment) || enchantment.getKey().is(EnchantmentTags.ARMOR_EXCLUSIVE)) {
                    enchantmentApplicables.add(ofItalicAndGray(Component.translatable("qualityofqueso.gui.armor")));
                }
                if (isEnchantmentInGroup(EnchantingHelper.HELMET_EXCLUSIVE, enchantment)) {
                    enchantmentApplicables.add(ofItalicAndGray(Component.translatable("qualityofqueso.gui.helmets")));
                }
                if (isEnchantmentInGroup(EnchantingHelper.LEGS_EXCLUSIVE, enchantment)) {
                    enchantmentApplicables.add(ofItalicAndGray(Component.translatable("qualityofqueso.gui.leggings")));
                }
                if (isEnchantmentInGroup(EnchantingHelper.BOOTS_EXCLUSIVE, enchantment) || enchantment.getKey().is(EnchantmentTags.BOOTS_EXCLUSIVE)) {
                    enchantmentApplicables.add(ofItalicAndGray(Component.translatable("qualityofqueso.gui.boots")));
                }
                if (isEnchantmentInGroup(EnchantingHelper.SWORDS, enchantment)) {
                    enchantmentApplicables.add(ofItalicAndGray(Component.translatable("qualityofqueso.gui.swords")));
                }
                if (isEnchantmentInGroup(EnchantingHelper.SPEARS, enchantment)) {
                    enchantmentApplicables.add(ofItalicAndGray(Component.translatable("qualityofqueso.gui.spears")));
                }
                if (isEnchantmentInGroup(EnchantingHelper.MACES, enchantment)) {
                    enchantmentApplicables.add(ofItalicAndGray(Component.translatable("qualityofqueso.gui.maces")));
                }
                if (isEnchantmentInGroup(EnchantingHelper.TRIDENTS, enchantment) || enchantment.getKey().is(EnchantmentTags.RIPTIDE_EXCLUSIVE)) {
                    enchantmentApplicables.add(ofItalicAndGray(Component.translatable("qualityofqueso.gui.tridents")));
                }
                if (isEnchantmentInGroup(EnchantingHelper.BOWS, enchantment) || enchantment.getKey().is(EnchantmentTags.BOW_EXCLUSIVE)) {
                    enchantmentApplicables.add(ofItalicAndGray(Component.translatable("qualityofqueso.gui.bows")));
                }
                if (isEnchantmentInGroup(EnchantingHelper.CROSSBOWS, enchantment) || enchantment.getKey().is(EnchantmentTags.CROSSBOW_EXCLUSIVE)) {
                    enchantmentApplicables.add(ofItalicAndGray(Component.translatable("qualityofqueso.gui.crossbows")));
                }
                if (isEnchantmentInGroup(EnchantingHelper.TOOLS, enchantment) || enchantment.getKey().is(EnchantmentTags.MINING_EXCLUSIVE)) {
                    enchantmentApplicables.add(ofItalicAndGray(Component.translatable("qualityofqueso.gui.tools")));
                }
                if (isEnchantmentInGroup(EnchantingHelper.FISHING_RODS, enchantment)) {
                    enchantmentApplicables.add(ofItalicAndGray(Component.translatable("qualityofqueso.gui.fishing_rods")));
                }
            }

            // Determine the index to render the tooltips (if advanced tooltips are on, we need to go back 2)
            int index = instance().getMinecraft().options.advancedItemTooltips ? originalTooltip.size() - 2 : originalTooltip.size();
            // Add the enchantment applicables to the original tooltip
            for (Component c : enchantmentApplicables) {
                originalTooltip.add(index, c);
            }
            // Then add the "can be added to" text at the top of all applicables
            if (!enchantmentApplicables.isEmpty()) {
                originalTooltip.add(index, Component.translatable("qualityofqueso.gui.applicable_on"));
            }

            // Render the tooltip and return true
            graphics.setTooltipForNextFrame(font, originalTooltip, Optional.empty(), mouseX, mouseY);
            ci.cancel();
        }
    }
}