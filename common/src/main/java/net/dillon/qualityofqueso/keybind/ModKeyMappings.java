package net.dillon.qualityofqueso.keybind;

import com.mojang.blaze3d.platform.InputConstants;
import net.blay09.mods.kuma.api.*;
import net.dillon.qualityofqueso.screen.EnderChestPreviewScreen;
import net.dillon.qualityofqueso.screen.MainMenuScreen;
import net.dillon.qualityofqueso.screen.VisualTimeScreen;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

import static net.dillon.dillonlib.task.ClientTasks.openScreen;
import static net.dillon.qualityofqueso.helper.ModHelper.modEnabled;
import static net.dillon.qualityofqueso.helper.ModHelper.qoqIdentifier;
import static net.dillon.qualityofqueso.option.OptionInstances.mixins;

/**
 * Keybindings for the {@code Quality of Queso} mod.
 */
public class ModKeyMappings {
    public static final KeyMapping.Category QOQ_KEY_CATEGORY = KeyMapping.Category.register(qoqIdentifier("quality_of_queso"));

    /**
     * Initializes Quality of Queso keybinds, using the {@link Kuma} API.
     */
    public static void initKeybinds() {
    }

    public static final ManagedKeyMapping LOCK_SLOT = Kuma.createKeyMapping(qoqIdentifier("lock_slot"))
            .overrideCategory(QOQ_KEY_CATEGORY)
            .withDefault(InputBinding.mouse(InputConstants.MOUSE_BUTTON_MIDDLE, KeyModifiers.of(KeyModifier.ALT)))
            .build();

    public static final ManagedKeyMapping MOVE_TO_CONTAINER = Kuma.createKeyMapping(qoqIdentifier("move_to_container"))
            .overrideCategory(QOQ_KEY_CATEGORY)
            .withDefault(InputBinding.key(InputConstants.KEY_C, KeyModifiers.of(KeyModifier.CONTROL)))
            .build();

    public static final ManagedKeyMapping MOVE_TO_INVENTORY = Kuma.createKeyMapping(qoqIdentifier("move_to_inventory"))
            .overrideCategory(QOQ_KEY_CATEGORY)
            .withDefault(InputBinding.key(InputConstants.KEY_I, KeyModifiers.of(KeyModifier.CONTROL)))
            .build();

    public static final ManagedKeyMapping OPEN_ITEM_FRAME_SEARCH_GUI = Kuma.createKeyMapping(qoqIdentifier("open_item_frame_search_gui"))
            .overrideCategory(QOQ_KEY_CATEGORY)
            .withDefault(InputBinding.key(InputConstants.KEY_I))
            .handleWorldInput(event -> false)
            .build();

    public static final ManagedKeyMapping OPEN_QUALITY_OF_QUESO_MAIN_MENU = Kuma.createKeyMapping(qoqIdentifier("open_quality_of_queso_main_menu"))
            .overrideCategory(QOQ_KEY_CATEGORY)
            .withDefault(new InputBinding(
                    InputConstants.Type.KEYBOARD.getOrCreate(InputConstants.KEY_Q),
                    KeyModifiers.none().addCustomModifier(InputConstants.KEY_TAB)
            ))
            .handleWorldInput(event -> {
                openScreen(new MainMenuScreen(null));
                return true;
            })
            .build();

    public static final ManagedKeyMapping OPEN_VISUAL_TIME_GUI = Kuma.createKeyMapping(qoqIdentifier("open_visual_time_gui"))
            .overrideCategory(QOQ_KEY_CATEGORY)
            .withDefault(InputBinding.key(InputConstants.KEY_V, KeyModifiers.of(KeyModifier.CONTROL)))
            .handleWorldInput(event -> {
                if (mixins().clockManagerMixin) {
                    openScreen(new VisualTimeScreen(null));
                    return true;
                }
                return false;
            })
            .build();

    public static final ManagedKeyMapping QUICK_DROP = Kuma.createKeyMapping(qoqIdentifier("quick_drop"))
            .overrideCategory(QOQ_KEY_CATEGORY)
            .withDefault(InputBinding.key(InputConstants.KEY_Q, KeyModifiers.of(KeyModifier.CONTROL, KeyModifier.ALT)))
            .build();

    public static final ManagedKeyMapping QUICK_EQUIP = Kuma.createKeyMapping(qoqIdentifier("quick_equip"))
            .overrideCategory(QOQ_KEY_CATEGORY)
            .build();

    public static final ManagedKeyMapping SORT = Kuma.createKeyMapping(qoqIdentifier("sort"))
            .overrideCategory(QOQ_KEY_CATEGORY)
            .withDefault(InputBinding.key(InputConstants.KEY_S, KeyModifiers.of(KeyModifier.CONTROL)))
            .build();

    public static final ManagedKeyMapping SWAP_ITEMS = Kuma.createKeyMapping(qoqIdentifier("swap_items"))
            .overrideCategory(QOQ_KEY_CATEGORY)
            .build();

    public static final ManagedKeyMapping VIEW_LAST_KNOWN_ENDER_CHEST = Kuma.createKeyMapping(
                    qoqIdentifier("view_last_known_ender_chest"))
            .overrideCategory(QOQ_KEY_CATEGORY)
            .withDefault(InputBinding.key(InputConstants.KEY_N, KeyModifiers.of(KeyModifier.CONTROL)))
            .handleWorldInput(event -> {
                Minecraft minecraft =  Minecraft.getInstance();
                if (modEnabled(minecraft) && minecraft.level != null && minecraft.player != null) {
                    openScreen(new EnderChestPreviewScreen());
                    return true;
                }
                return false;
            })
            .build();
}