package net.dillon.qualityofqueso.event.management;

import net.dillon.qualityofqueso.event.QuesoScreen;
import net.dillon.qualityofqueso.helper.ModConstants;
import net.dillon.qualityofqueso.widget.SearchBar;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;

import static net.dillon.qualityofqueso.helper.ManagementHelper.getBarWidth;
import static net.dillon.qualityofqueso.helper.ManagementHelper.isInventoryScreen;
import static net.dillon.qualityofqueso.helper.MethodHelper.*;
import static net.dillon.qualityofqueso.option.OptionInstances.client;

/**
 * Handles searching-related functions.
 */
public class SearchEvents extends ManagementEvents {

    public SearchEvents(QuesoScreen screen) {
        super(screen);
    }

    /**
     * Base method for initializing the {@link SearchBar}.
     */
    public SearchBar initializeSearchField(boolean inventory) {
        return new SearchBar(Minecraft.getInstance().font,
                holder().screen().width / 2 +  getBarWidth(getImageWidth(holder().screen())) / 2 - (inventory ? 60 : 64),
                getTopPos(holder().screen()) + getTitleLabelY(holder().screen()) - 2 + (client().searching().searchBarPosition.top() ? (client().searching().searchBarColor.black() ? -19 : -21) : 0));
    }

    /**
     * @return the {@code searchField namespace} text.
     */
    public String getSearchFieldText() {
        return holder().searchFields().inventory() != null
                ? holder().searchFields().inventory().getValue()
                : holder().searchFields().container() != null ? holder().searchFields().container().getValue() : "";
    }

    /**
     * @return if the screen has a search field present.
     */
    public boolean hasSearchField() {
        return holder().searchFields().container() != null || holder().searchFields().inventory() != null;
    }

    /**
     * @return if the search field has a query in it (in other words, if the field isn't empty).
     */
    public boolean hasSearchQuery() {
        return hasSearchField() && !holder().searchFields().searchText().isEmpty();
    }

    /**
     * @return if the slot searched is filtered (returns true if the slot does <b>not</b> match the current query).
     */
    public boolean isFilteredBySearch(Slot slot, boolean dropping) {
        return this.hasSearchQuery() && !this.search(this.getSearchFieldText(), slot, dropping);
    }

    /**
     * @return the slot count that should be considered for searching/highlighting on the current screen.
     */
    public int getSearchSlotCount() {
        if (isInventoryScreen(holder().screen())) {
            return holder().menu().slots.size();
        }
        int searchSize = getInventorySize();
        if (searchSize > 0) {
            return searchSize;
        }
        if (holder().screen() instanceof AbstractContainerScreen<?>) {
            return holder().menu().slots.size();
        }
        return 0;
    }

    /**
     * @return {@code true} if an query is found from {@code searchQuery}.
     */
    public boolean search(String searchQuery, Slot slot, boolean dropping) {
        ItemStack stack = slot.getItem();

        // Empty slots are never searchable.
        if (stack.isEmpty()) {
            return false;
        }

        // The "search inventory" option only gates container-screen player inventory scanning.
        // InventoryScreen should always keep its own hotbar/include behavior.
        if (!client().management().includingHotbar
                && isHotbarSlot(holder().menu().slots.size(), dropping ? slot.index + 1 : slot.index)
                && (!dropping || !isInventoryScreen(holder().screen()) || slot.index != 45)) {
            boolean applyHotbarFilter = client().accessibility().searchInventory || isInventoryScreen(holder().screen());
            if (applyHotbarFilter) {
                return false;
            }
        }

        if (matchesQuery(searchQuery, stack)) {
            return true;
        }

        if (!ModConstants.SEARCHING_TRANSPORTABLES || !client().buttonDisplayOptions().displaySearchTransportables) {
            return false;
        }

        ItemContainerContents containerContents = stack.get(DataComponents.CONTAINER);
        if (containerContents != null && containerContents.nonEmptyItemCopyStream().anyMatch(contained -> matchesQuery(searchQuery, contained))) {
            return true;
        }

        BundleContents bundleContents = stack.get(DataComponents.BUNDLE_CONTENTS);
        return bundleContents != null && bundleContents.itemCopies().anyMatch(contained -> matchesQuery(searchQuery, contained));
    }

    /**
     * @return {@code true} if {@code stack} matches the provided query syntax.
     */
    public boolean matchesQuery(String searchQuery, ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }

        String itemName = stack.getItemName().getString().toLowerCase();
        String customName = stack.getCustomName() != null ? stack.getCustomName().getString().toLowerCase() : itemName;

        String[] terms = searchQuery.split(",");
        boolean hasPositiveTerm = false;
        for (String rawTerm : terms) {
            String term = rawTerm.trim().toLowerCase();

            // As long as slot doesn't contain whatever is searched (beginning after "!"), return true (slot is available)
            if (!term.startsWith("!")) {
                hasPositiveTerm = true;
            }
            if (term.startsWith("!")) {
                String forbidden = term.substring(1);
                if (itemName.contains(forbidden) || customName.contains(forbidden)) {
                    return false;
                }
                continue; // Continue searching for the rest
            }

            // If tag contains search query and stack is in returned tag, slot is available
            if (term.startsWith("#")) {
                String tagSearch = term.substring(1).toLowerCase();

                // Return false if tag list is empty
                if (stack.tags().toList().isEmpty()) {
                    return false;
                }

                // Then search through all item's tags
                for (TagKey<Item> tag : stack.tags().toList()) {
                    Identifier location = tag.location();

                    if (location.getPath().toLowerCase().contains(tagSearch)
                            || location.toString().toLowerCase().contains(tagSearch)) {
                        return true;
                    }
                }
            }

            // Mod namespace search logic
            if (term.startsWith("@")) {
                String modNamespaceSearch = term.substring(1).toLowerCase();

                Identifier id = BuiltInRegistries.ITEM.getKey(stack.getItem());
                String namespace = id.getNamespace().toLowerCase();

                // If item contains namespace searched, return true
                if (namespace.contains(modNamespaceSearch)) {
                    return true;
                }
            }

            // Enchanted book searching logic
            if (stack.isEnchanted() || stack.is(Items.ENCHANTED_BOOK)) {
                ItemEnchantments enchantments = net.minecraft.world.item.enchantment.EnchantmentHelper.getEnchantmentsForCrafting(stack);
                for (Holder<Enchantment> enchantment : enchantments.keySet()) {
                    String encName = enchantment.value().description().getString();
                    String fullName = encName + " " + enchantments.getLevel(enchantment);

                    // If slot contains enchantments searched, return true (slot is available)
                    if (fullName.toLowerCase().contains(term)) {
                        return true;
                    }
                }
            }

            if (term.startsWith(":")) {
                String query = term.substring(1);
                if (itemName.matches(query) || customName.matches(query)) {
                    return true;
                }
            } else {
                if (itemName.contains(term) || customName.contains(term)) {
                    return true;
                }
            }
        }

        // If stack contains whatever is searched, return true (stack is available)
        return !hasPositiveTerm;
    }
}