package net.dillon.qualityofqueso.event.screen;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.dillon.qualityofqueso.event.QuesoScreenHolder;
import net.dillon.qualityofqueso.event.management.ManagementEvents;
import net.dillon.qualityofqueso.util.EnchantingHelper;
import net.dillon.qualityofqueso.widget.management.ManagementButton;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.inventory.SlotWidget;
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

import static net.dillon.dillonlib.task.ClientTasks.*;
import static net.dillon.qualityofqueso.helper.GuiHelper.*;
import static net.dillon.qualityofqueso.helper.ManagementHelper.*;
import static net.dillon.qualityofqueso.helper.ModConstants.MOVE_AMOUNT;
import static net.dillon.qualityofqueso.helper.ModKeyMappingHelper.*;
import static net.dillon.qualityofqueso.option.ClientOptionInstances.client;
import static net.dillon.qualityofqueso.util.EnchantingHelper.isEnchantmentInGroup;

/**
 * Handles tooltips rendering for this mod.
 */
public class TooltipEvents extends ManagementEvents {

    public TooltipEvents(QuesoScreenHolder screen) {
        super(screen);
    }

    /**
     * All tooltips events.
     */
    public void handle(GuiGraphicsExtractor graphics, CallbackInfo ci) {
        displaySingleMovingTooltips(graphics, ci);
        displayEnchantmentHelperTooltips(graphics, ci);
        displayTagsOnItems(graphics, ci);
    }

    /**
     * @return the original tooltips that is being displayed.
     */
    private List<Component> getOriginalTooltip() {
        SlotWidget hoveredSlotWidget = holder().getHoveredSlotWidget();

        if (hoveredSlotWidget == null) {
            return null;
        }

        return hoveredSlotWidget.slot().getItem().getTooltipLines(Item.TooltipContext.EMPTY, getLocalPlayer(), getMinecraft().options.advancedItemTooltips ? TooltipFlag.ADVANCED : TooltipFlag.NORMAL);
    }

    /**
     * Displays all item tags on the hovered item.
     */
    private void displayTagsOnItems(GuiGraphicsExtractor graphics, CallbackInfo ci) {
        SlotWidget hoveredSlotWidget = holder().getHoveredSlotWidget();
        if (!hoveredSlotHasItem(hoveredSlotWidget)) {
            return;
        }

        ItemStack stack = hoveredSlotWidget.slot().getItem();
        List<Component> originalTooltip = getOriginalTooltip();

        // Render tags for tag search queries
        String searchQuery = holder().searchFields().searchText();
        if (holder().searchFields().container() != null || holder().searchFields().inventory() != null) {
            // Exit if search query doesn't start with #
            if (!searchQuery.startsWith("#")) {
                return;
            }

            // Loop through item's tags
            for (TagKey<Item> tag : stack.tags().toList()) {
                // Add each tag to the query hovered
                String location = tag.location().getNamespace().equals("c") ? "fabric:" + tag.location().getPath() : tag.location().toString();
                String tagString = "#" + location;
                originalTooltip.add(1, Component.literal(tagString).withStyle(ChatFormatting.DARK_PURPLE));
            }

            // If tags were found in the query add it to the tooltips and render
            // cancel out original method to prevent overlapping tooltips
            if (stack.tags().toList().isEmpty()) {
                originalTooltip.add(1, Component.translatable("qualityofqueso.gui.no_tags_found").withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.GRAY));
            }
            graphics.setTooltipForNextFrame(getFont(), originalTooltip, Optional.empty(), graphics.mouseX(), graphics.mouseY());
            ci.cancel();
        }
    }

    /**
     * Displays tooltips for singular moving or dropping.
     */
    private void displaySingleMovingTooltips(GuiGraphicsExtractor graphics, CallbackInfo ci) {
        // If the user is attempting to lock slots or drag sort (and as long as they don't have "CTRL"/management modifier down, to prevent redundant removal of tooltips)...
        // ...then remove all tooltips, so the user can clearly read and see slots
        if (isExcludingOrLockingSlots() && !hasAnyManagementModifierDown()) {
            ci.cancel();
        }

        // Create variables to determine if the tooltips should be modified, based on quick dropping actions and moving singular items
        SlotWidget hoveredSlotWidget = holder().getHoveredSlotWidget();
        boolean hoveredSlotHasItem = hoveredSlotHasItem(hoveredSlotWidget);

        // Check if the quick drop button itself is hovered
        boolean quickDropHovered = buttonHoveredAndActive(holder().managementButtons().getQuickDrop());

        // Check if any transfer button is hovered (this includes quick drop, transfer container, or transfer inventory)
        boolean buttonHovered = quickDropHovered || buttonHoveredAndActive(holder().managementButtons().getTransferContainer()) || buttonHoveredAndActive(holder().managementButtons().getTransferInventory());

        // Check if a shortcut key is held down
        // If quick drop is hovered, then we only check for if shift is held (to only drop one of each item)
        // Otherwise, check if the quick drop keys are down (CTRL + ALT), OR if CTRL/move single modifier itself is held down, to move singular items
        boolean hasKeyDown = quickDropHovered ? hasDropOnlyOneItemModifierDown() : hasAllQuickDropModifiersDown() || canScrollMoveAndHasScrollModifierDown();
        boolean droppingOne = hoveredSlotHasItem && hasDropOnlyOneItemModifierDown();

        // The boolean expression to modify tooltips based on the conditions above
        // We can modify tooltips if we are attempting to drop one, or if we have a shortcut key down and one of the buttons are hovered/the hovered slot has an item (for quick dropping)
        boolean modifyTooltips = client().management().scrollMoving && (droppingOne || (hasKeyDown && (buttonHovered || hoveredSlotHasItem)));
        holder().setCanMoveOne(modifyTooltips);
        boolean onlySingleMoveModifierDown = canScrollMoveAndHasScrollModifierDown() && !hasAllQuickDropModifiersDown() && !hasDropOnlyOneItemModifierDown();

        // Create a new tooltips to render
        List<Component> tooltipToRender = new ArrayList<>();

        // If modifyTooltips is true, and the hovered slot's count is more than 1 (because if you are moving 1 singular, the count must be more than 1), OR if the user isn't hovering over a slot at all, begin modifying tooltips
        if (!isCreativeInventoryScreen()
                && modifyTooltips
                && !onlySingleMoveModifierDown
                && (
                hoveredSlotWidget == null
                        || (
                                (hasAltDown() && hoveredSlotWidget.slot().getItem().isStackable()) || hoveredSlotWidget.slot().getItem().count() > 1)
        )) {
            // Create the new tooltips variable
            List<Component> moveAmountTooltip = new ArrayList<>();

            // Determine the translation for the tooltips
            // If the button is hovered, render "Move *count* of each". Otherwise, render "Move *count*"
            Component translation = Component.translatable("qualityofqueso.gui.move_amount_each", getActualMoveAmount());

            // If the quick drop button is hovered, render the drop amount of each stack
            if (quickDropHovered) {
                translation = Component.translatable("qualityofqueso.gui.quick_drop_button.throw_amount", getActualMoveAmount());
            } else if (droppingOne) { // Otherwise, if we are dropping one, render the drop amount for the singular hovered item
                // Stop overload if move amount is one and not a locked slot
                if (MOVE_AMOUNT == 1 && !hasAltDown() && !lockedSlotEvents().isLockedSlot(hoveredSlotWidget.slot().index)) {
                    return;
                }

                translation = Component.translatable("qualityofqueso.gui.quick_drop_button.throw_amount.single", getActualMoveAmount());
            }

            // Create temp boolean to determine if we can continue adding tooltips after the fact, if the hovered item is found in the container (for quick dropping only)
            boolean skipInstructions = false;
            // Create ignores locked slots variable
            Component ignoresLockedSlots = Component.translatable("qualityofqueso.gui.move_amount.ignores_locked_slot").withColor(client().lockedSlots().lockedSlotColor);

            // If the quick drop keys are down and the hovered slot has an item, continue through this statement
            if (hasAllQuickDropModifiersDown() && hoveredSlotHasItem(hoveredSlotWidget)) {
                Slot hoveredSlot = hoveredSlotWidget.slot();
                // Get current container screen
                boolean containerScreen = isContainerScreen();

                // Determine if quick drop button is inactive
                ManagementButton quickDropButton = holder().managementButtons().getQuickDrop();
                boolean quickDropInactive = quickDropButton != null && !quickDropButton.active;

                // If no respective item was found in the container for quick dropping, tell the user "none of this item was found", therefor cannot drop.
                if (containerScreen && quickDropInactive) {
                    moveAmountTooltip.add(Component.translatable("qualityofqueso.gui.quick_drop_button.no_items_found", hoveredSlot.getItem().getItemName()).copy()
                            .withStyle(ChatFormatting.RED));
                    skipInstructions = true;
                }

                // Then check if we are attempting to drop only one of each item
                else {
                    // Determine what container the user is dropping from
                    Component containerType = isInventoryScreen()
                            ? Component.translatable("qualityofqueso.gui.quick_drop_button.throw_amount.inventory")
                            : Component.translatable("qualityofqueso.gui.quick_drop_button.throw_amount.container");

                    if (hasDropOnlyOneItemModifierDown()) { // Display that item name w/ the move amount
                        moveAmountTooltip.add(Component.translatable("qualityofqueso.gui.quick_drop_button.throw_amount.type",
                                getActualMoveAmount(),
                                hoveredSlot.getItem().getItemName(),
                                containerType
                        ));
                    } else { // Otherwise display that item name, no move amount because we are dropping full stacks
                        moveAmountTooltip.add(Component.translatable("qualityofqueso.gui.quick_drop_button.throw_amount.type_all",
                                hoveredSlot.getItem().getItemName(),
                                containerType
                        ));
                        skipInstructions = true;
                    }
                }
            }

            // Otherwise just add the raw translation for a singular move amount for a single item
            else {
                // Add entire stack drop if dropping an entire locked slot stack
                if (lockedSlotEvents().droppingEntireLockedSlotStack(hoveredSlotWidget)) {
                    translation = Component.translatable("qualityofqueso.gui.quick_drop_button.throw_amount.full", getActualMoveAmount());
                    skipInstructions = true;
                }

                // Add the translation
                moveAmountTooltip.add(translation);

                // Add locked slot bypass text if dropping it (doesn't have to be entire stack)
                if (lockedSlotEvents().droppingLockedSlotStack(hoveredSlotWidget)) {
                    moveAmountTooltip.add(ignoresLockedSlots);
                }
            }

            // Add those helper tooltips to the rendered tooltips if we can
            if (client().general().tooltips.enabled() && !skipInstructions) {
                if (!(hoveredSlotHasItem(hoveredSlotWidget) && (canScrollMoveAndHasScrollModifierDown() && !hasDropOnlyOneItemModifierDown()))) {
                    moveAmountTooltip.add(Component.translatable("qualityofqueso.gui.scroll_to_change_amount"));
                    moveAmountTooltip.add(Component.translatable("qualityofqueso.gui.move_amount.reset"));
                }
            }

            // As long as there was never an original tooltips to render, render the tooltips and return true
            tooltipToRender.addAll(moveAmountTooltip);

            // Create wrapped tooltips
            List<FormattedCharSequence> wrappedTooltip = new ArrayList<>();
            int maxWidth = 200;

            // Wrap the tooltips
            for (Component component : tooltipToRender) {
                wrappedTooltip.addAll(getFont().split(component, maxWidth));
            }

            // Set the tooltips
            int x = hoveredSlotHasItem
                    ? graphics.mouseX()
                    : getTooltipX(graphics, holder().screen());
            int y = hoveredSlotHasItem
                    ? graphics.mouseY()
                    : getTooltipY(graphics, holder().getCachedContainer(), holder().screen(), false);

            graphics.setTooltipForNextFrame(getFont(), wrappedTooltip, x, y);
            ci.cancel();
        }
    }

    /**
     * Displays all enchantment helper tooltips.
     */
    private void displayEnchantmentHelperTooltips(GuiGraphicsExtractor graphics, CallbackInfo ci) {
        // Any further injection here will not be applied if the hovered slot is null or doesn't have an item
        if (!hoveredSlotHasItem(holder().getHoveredSlotWidget())) {
            return;
        }

        // Get original tooltip and slots
        List<Component> originalTooltip = getOriginalTooltip();
        SlotWidget hoveredSlotWidget = holder().getHoveredSlotWidget();
        if (hoveredSlotWidget == null) {
            return;
        }
        Slot hoveredSlot = hoveredSlotWidget.slot();

        // Enchantment helper functionality
        // Goes through all enchantments on an enchanted book and determines what items the enchanted book itself can be applied to
        if (client().gui().enchantmentHelper && hoveredSlot.getItem().is(Items.ENCHANTED_BOOK)) {
            ItemEnchantments enchantments = hoveredSlot.getItem().getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY);
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
            int index = getMinecraft().options.advancedItemTooltips ? originalTooltip.size() - 2 : originalTooltip.size();
            // Add the enchantment applicables to the original tooltips
            for (Component c : enchantmentApplicables) {
                originalTooltip.add(index, c);
            }
            // Then add the "can be added to" text at the top of all applicables
            if (!enchantmentApplicables.isEmpty()) {
                originalTooltip.add(index, Component.translatable("qualityofqueso.gui.applicable_on"));
            }

            // Render the tooltips and return true
            graphics.setTooltipForNextFrame(getFont(), originalTooltip, Optional.empty(), graphics.mouseX(), graphics.mouseY());
            ci.cancel();
        }
    }
}