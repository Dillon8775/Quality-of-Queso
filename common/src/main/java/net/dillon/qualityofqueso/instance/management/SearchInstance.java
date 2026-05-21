package net.dillon.qualityofqueso.instance.management;

import net.dillon.qualityofqueso.instance.QuesoScreen;
import net.dillon.qualityofqueso.widget.gui.SearchBar;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import java.util.Map;

import static net.dillon.qualityofqueso.helper.ManagementHelper.getBarWidth;
import static net.dillon.qualityofqueso.helper.ManagementHelper.isInventoryScreen;
import static net.dillon.qualityofqueso.helper.MethodHelper.*;
import static net.dillon.qualityofqueso.helper.ModHelper.options;

/**
 * Handles searching-related functions.
 */
public class SearchInstance extends ManagementInstance {

    public SearchInstance(QuesoScreen screen) {
        super(screen);
    }

    /**
     * Base method for initializing the {@link SearchBar}.
     */
    public SearchBar initializeSearchField(boolean inventory) {
        return new SearchBar(Minecraft.getInstance().font,
                instance().getScreen().width / 2 +  getBarWidth(getImageWidth(instance().getScreen())) / 2 - (inventory ? 60 : 64),
                getTopPos(instance().getScreen()) + getTitleLabelY(instance().getScreen()) - 2 + (options().searching.searchBarPosition.top() ? (options().searching.searchBarColor.black() ? -19 : -21) : 0));
    }

    /**
     * @return the {@code searchField namespace} text.
     */
    public String getSearchFieldText() {
        return instance().getSearchFields().inventory() != null
                ? instance().getSearchFields().inventory().getValue()
                : instance().getSearchFields().container() != null ? instance().getSearchFields().container().getValue() : "";
    }

    /**
     * @return if the screen has a search field present.
     */
    public boolean hasSearchField() {
        return instance().getSearchFields().container() != null || instance().getSearchFields().inventory() != null;
    }

    /**
     * @return if the search field has a query in it (in other words, if the field isn't empty).
     */
    public boolean hasSearchQuery() {
        return hasSearchField() && !instance().getSearchFields().searchText().isEmpty();
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
        if (isInventoryScreen(instance().getScreen())) {
            return instance().getScreenMenu().slots.size();
        }
        int searchSize = getInventorySize();
        if (searchSize > 0) {
            return searchSize;
        }
        if (instance().getScreen() instanceof AbstractContainerScreen<?>) {
            return instance().getScreenMenu().slots.size();
        }
        return getInventorySize();
    }

    /**
     * Grays out a search, typically from search queries or excluding hotbar.
     */
    public void renderGrayedSlot(GuiGraphics graphics, Slot slot, boolean hotbarOverlay) {
        int color = hotbarOverlay ? -2139062148 : -1275068416;
        graphics.fillGradient(RenderType.guiOverlay(), slot.x, slot.y, slot.x + 16, slot.y + 16, color, color, 0);
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
        if (!options().management.includeHotbar
                && isHotbarSlot(instance().getScreenMenu().slots.size(), dropping ? slot.index + 1 : slot.index)
                && (!dropping || !isInventoryScreen(instance().getScreen()) || slot.index != 45)) {
            boolean applyHotbarFilter = options().accessibility.searchInventory || isInventoryScreen(instance().getScreen());
            if (applyHotbarFilter) {
                return false;
            }
        }

        if (matchesQuery(searchQuery, stack)) {
            return true;
        }

        if (!options().searching.searchTransportables || !options().buttonDisplayOptions.displaySearchTransportables) {
            return false;
        }

        CompoundTag tag = stack.getTagElement("BlockEntityTag");
        if (tag != null && tag.contains("Items", Tag.TAG_LIST)) {
            ListTag itemsTag = tag.getList("Items", Tag.TAG_COMPOUND);

            for (int i = 0; i < itemsTag.size(); i++) {
                ItemStack containedStack = ItemStack.of(itemsTag.getCompound(i));
                if (matchesQuery(searchQuery, containedStack)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @return {@code true} if {@code stack} matches the provided query syntax.
     */
    public boolean matchesQuery(String searchQuery, ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }

        String itemName = stack.getDescriptionId().toLowerCase();
        String displayName = stack.getHoverName().getString().toLowerCase();

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
                if (itemName.contains(forbidden) || displayName.contains(forbidden)) {
                    return false;
                }
                continue; // Continue searching for the rest
            }

            // If tag contains search query and stack is in returned tag, slot is available
            if (term.startsWith("#")) {
                String tagSearch = term.substring(1).toLowerCase();

                // Return false if tag list is empty
                if (stack.getTags().toList().isEmpty()) {
                    return false;
                }

                // Then search through all item's tags
                for (TagKey<Item> tag : stack.getTags().toList()) {
                    ResourceLocation location = tag.location();

                    if (location.getPath().toLowerCase().contains(tagSearch)
                            || location.toString().toLowerCase().contains(tagSearch)) {
                        return true;
                    }
                }
            }

            // Mod namespace search logic
            if (term.startsWith("@")) {
                String modNamespaceSearch = term.substring(1).toLowerCase();

                ResourceLocation id = BuiltInRegistries.ITEM.getKey(stack.getItem());
                String namespace = id.getNamespace().toLowerCase();

                // If item contains namespace searched, return true
                if (namespace.contains(modNamespaceSearch)) {
                    return true;
                }
            }

            // Enchanted book searching logic
            if (stack.isEnchanted() || stack.is(Items.ENCHANTED_BOOK)) {
                for (Map.Entry<Enchantment, Integer> entry : EnchantmentHelper.getEnchantments(stack).entrySet()) {
                    String encName = Component.translatable(entry.getKey().getDescriptionId()).getString();
                    String fullName = encName + " " + entry.getValue();

                    // If slot contains enchantments searched, return true (slot is available)
                    if (fullName.toLowerCase().contains(term)) {
                        return true;
                    }
                }
            }

            if (term.startsWith(":")) {
                String query = term.substring(1);
                if (itemName.matches(query) || displayName.matches(query)) {
                    return true;
                }
            } else {
                if (itemName.contains(term) || displayName.contains(term)) {
                    return true;
                }
            }
        }

        // If stack contains whatever is searched, return true (stack is available)
        return !hasPositiveTerm;
    }
}
