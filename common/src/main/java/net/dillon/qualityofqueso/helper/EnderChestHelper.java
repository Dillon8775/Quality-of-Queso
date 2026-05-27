package net.dillon.qualityofqueso.helper;

import net.dillon.qualityofqueso.option.ContainerData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.*;

import static net.dillon.qualityofqueso.helper.ContainerHelper.worldKey;
import static net.dillon.qualityofqueso.helper.ModHelper.containerDataInstance;

/**
 * Handles persistent ender chest cache storage in container_data.json.
 */
public class EnderChestHelper {

    /**
     * Saves current ender chest contents to {@link ContainerData} while the ender chest GUI is open.
     */
    public static void persistEnderChestContentsIfOpen(Minecraft minecraft, LocalPlayer player) {
        Screen screen = minecraft.screen;
        if (!(screen instanceof AbstractContainerScreen<?> containerScreen) || !isEnderChestScreen(containerScreen, player)) {
            return;
        }

        Container openContainer = ((ContainerScreen) containerScreen).getMenu().getContainer();
        AbstractContainerMenu menu = containerScreen.getMenu();
        List<ContainerData.StoredEnderChestStack> serialized = new ArrayList<>();

        for (Slot slot : menu.slots) {
            if (slot.container != openContainer) {
                continue;
            }

            ItemStack stack = slot.getItem();
            if (stack == null || stack.isEmpty()) {
                continue;
            }

            ResourceLocation id = BuiltInRegistries.ITEM.getKey(stack.getItem());
            if (id == null) {
                continue;
            }

            ContainerData.StoredEnderChestStack storedStack = new ContainerData.StoredEnderChestStack();
            storedStack.itemId = id.toString();
            storedStack.count = stack.getCount();
            storedStack.components = stack.getOrCreateTag().toString();
            storedStack.containedItems = serializeContainedItems(stack);
            serialized.add(storedStack);
        }

        Map<String, List<ContainerData.StoredEnderChestStack>> byWorld = containerDataInstance().enderChestItems;
        if (byWorld == null) {
            containerDataInstance().enderChestItems = new HashMap<>();
            byWorld = containerDataInstance().enderChestItems;
        }
        List<ContainerData.StoredEnderChestStack> cached = byWorld.getOrDefault(worldKey(), new ArrayList<>());

        if (isSameStoredContents(cached, serialized)) {
            return;
        }

        byWorld.put(worldKey(), serialized);
        ContainerData.INSTANCE.save();
    }

    /**
     * @return persisted ender chest items for the current world/server context.
     */
    public static List<ContainerData.StoredEnderChestStack> getPersistedEnderChestItemsForCurrentWorld() {
        Map<String, List<ContainerData.StoredEnderChestStack>> byWorld = containerDataInstance().enderChestItems;
        if (byWorld == null || byWorld.isEmpty()) {
            return new ArrayList<>();
        }
        return byWorld.getOrDefault(worldKey(), new ArrayList<>());
    }

    /**
     * @return whether the currently open container screen represents an ender chest.
     */
    private static boolean isEnderChestScreen(AbstractContainerScreen<?> containerScreen, LocalPlayer player) {
        if (!(containerScreen instanceof ContainerScreen genericContainerScreen)) {
            return false;
        }

        Container openContainer = genericContainerScreen.getMenu().getContainer();
        if (openContainer == player.getEnderChestInventory()) {
            return true;
        }

        return containerScreen.getTitle().getString().equals(I18n.get("container.enderchest"));
    }

    /**
     * @return if the stored contents are equal.
     */
    private static boolean isSameStoredContents(List<ContainerData.StoredEnderChestStack> current, List<ContainerData.StoredEnderChestStack> updated) {
        if (current.size() != updated.size()) {
            return false;
        }

        for (int i = 0; i < current.size(); i++) {
            ContainerData.StoredEnderChestStack a = current.get(i);
            ContainerData.StoredEnderChestStack b = updated.get(i);
            if (a == null || b == null) {
                return false;
            }

            if (!Objects.equals(a.itemId, b.itemId) || a.count != b.count || !Objects.equals(a.components, b.components)) {
                return false;
            }
            if (!isSameStoredContents(a.containedItems == null ? new ArrayList<>() : a.containedItems, b.containedItems == null ? new ArrayList<>() : b.containedItems)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Serializes container/bundle contents from a stack into persisted entries.
     */
    @Deprecated
    private static List<ContainerData.StoredEnderChestStack> serializeContainedItems(ItemStack stack) {
        return new ArrayList<>();
    }

    /**
     * Adds a contained item template for transportable containers inside ender chests.
     */
    @Deprecated
    private static void addContainedTemplate(List<ContainerData.StoredEnderChestStack> serialized, ItemStack template) {
        ItemStack inner = template;
        if (inner.isEmpty()) {
            return;
        }

        ResourceLocation innerId = BuiltInRegistries.ITEM.getKey(inner.getItem());
        if (innerId == null) {
            return;
        }

        ContainerData.StoredEnderChestStack storedInner = new ContainerData.StoredEnderChestStack();
        storedInner.itemId = innerId.toString();
        storedInner.count = template.getCount();
        storedInner.components = inner.getOrCreateTag().toString();
        storedInner.containedItems = serializeContainedItems(inner);
        serialized.add(storedInner);
    }
}