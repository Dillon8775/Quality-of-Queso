package net.dillon.qualityofqueso.keybind;

import com.mojang.blaze3d.platform.InputConstants;
import net.blay09.mods.kuma.api.*;
import net.dillon.qualityofqueso.screen.EnderChestPreviewScreen;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

import static net.dillon.qualityofqueso.helper.ModHelper.modEnabled;
import static net.dillon.qualityofqueso.helper.ModHelper.ofQoQ;

/**
 * Keybindings for the {@code Quality of Queso} mod.
 */
public class ModKeyMappings {
    public static final KeyMapping.Category QOQ_KEY_CATEGORY = KeyMapping.Category.register(ofQoQ("quality_of_queso"));

    /**
     * Initializes Quality of Queso keybinds, using the {@link Kuma} API.
     */
    public static void initKeybinds() {
    }

    public static final ManagedKeyMapping LOCK_SLOT = Kuma.createKeyMapping(ofQoQ("lock_slot"))
            .overrideCategory(QOQ_KEY_CATEGORY)
            .withDefault(InputBinding.mouse(InputConstants.MOUSE_BUTTON_MIDDLE, KeyModifiers.of(KeyModifier.ALT)))
            .build();

    public static final ManagedKeyMapping SCROLL_MOVE = Kuma.createKeyMapping(ofQoQ("scroll_move"))
            .overrideCategory(QOQ_KEY_CATEGORY)
            .withDefault(InputBinding.key(InputConstants.KEY_LCONTROL))
            .build();

    public static final ManagedKeyMapping MOVE_TO_CONTAINER = Kuma.createKeyMapping(ofQoQ("move_to_container"))
            .overrideCategory(QOQ_KEY_CATEGORY)
            .withDefault(InputBinding.key(InputConstants.KEY_C, KeyModifiers.of(KeyModifier.CONTROL)))
            .build();

    public static final ManagedKeyMapping MOVE_TO_INVENTORY = Kuma.createKeyMapping(ofQoQ("move_to_inventory"))
            .overrideCategory(QOQ_KEY_CATEGORY)
            .withDefault(InputBinding.key(InputConstants.KEY_I, KeyModifiers.of(KeyModifier.CONTROL)))
            .build();

    public static final ManagedKeyMapping OPEN_SEARCH_ITEM_FRAMES_GUI = Kuma.createKeyMapping(ofQoQ("open_item_frame_search_gui"))
            .overrideCategory(QOQ_KEY_CATEGORY)
            .withDefault(InputBinding.key(InputConstants.KEY_I))
            .handleWorldInput(event -> false)
            .build();

    public static final ManagedKeyMapping OPEN_VISUAL_TIME_GUI = Kuma.createKeyMapping(ofQoQ("open_visual_time_gui"))
            .overrideCategory(QOQ_KEY_CATEGORY)
            .withDefault(InputBinding.key(InputConstants.KEY_V, KeyModifiers.of(KeyModifier.CONTROL)))
            .handleWorldInput(event -> false)
            .build();

    public static final ManagedKeyMapping QUICK_DROP = Kuma.createKeyMapping(ofQoQ("quick_drop"))
            .overrideCategory(QOQ_KEY_CATEGORY)
            .withDefault(InputBinding.key(InputConstants.KEY_Q, KeyModifiers.of(KeyModifier.CONTROL, KeyModifier.ALT)))
            .build();

    public static final ManagedKeyMapping QUICK_EQUIP = Kuma.createKeyMapping(ofQoQ("quick_equip"))
            .overrideCategory(QOQ_KEY_CATEGORY)
            .build();

    public static final ManagedKeyMapping SORT = Kuma.createKeyMapping(ofQoQ("sort"))
            .overrideCategory(QOQ_KEY_CATEGORY)
            .withDefault(InputBinding.key(InputConstants.KEY_S, KeyModifiers.of(KeyModifier.CONTROL)))
            .build();

    public static final ManagedKeyMapping SWAP_ITEMS = Kuma.createKeyMapping(ofQoQ("swap_items"))
            .overrideCategory(QOQ_KEY_CATEGORY)
            .build();

    public static final ManagedKeyMapping VIEW_LAST_KNOWN_ENDER_CHEST = Kuma.createKeyMapping(
                    ofQoQ("view_last_known_ender_chest"))
            .overrideCategory(QOQ_KEY_CATEGORY)
            .withDefault(InputBinding.key(InputConstants.KEY_N, KeyModifiers.of(KeyModifier.CONTROL)))
            .handleWorldInput(event -> {
                Minecraft minecraft =  Minecraft.getInstance();
                if (modEnabled(minecraft) && minecraft.level != null && minecraft.player != null) {
                    Minecraft.getInstance().setScreen(new EnderChestPreviewScreen());
                    return true;
                }
                return false;
            })
            .build();
}