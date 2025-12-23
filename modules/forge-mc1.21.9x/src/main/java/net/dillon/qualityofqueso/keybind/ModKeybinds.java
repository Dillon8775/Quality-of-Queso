package net.dillon.qualityofqueso.keybind;

import com.mojang.blaze3d.platform.InputConstants;
import net.dillon.qualityofqueso.main.QoQ;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

/**
 * Keybindings for the {@code Quality of Queso} mod.
 */
@Mod.EventBusSubscriber(modid = QoQ.MOD_ID, value = Dist.CLIENT)
public class ModKeybinds {
    private static final KeyMapping.Category QOQ = KeyMapping.Category.register(ResourceLocation.fromNamespaceAndPath("qualityofqueso", "quality_of_queso"));

    public static final KeyMapping QUICK_EQUIP = new KeyMapping(
            "qualityofqueso.quick_equip",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_B,
            QOQ
    );

    public static final KeyMapping MOVE_CONTAINER = new KeyMapping(
            "qualityofqueso.move_container",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_C,
            QOQ
    );

    public static final KeyMapping MOVE_INVENTORY = new KeyMapping(
            "qualityofqueso.move_inventory",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_I,
            QOQ
    );

    public static final KeyMapping SWAP_ITEMS = new KeyMapping(
            "qualityofqueso.swap_items",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_S,
            QOQ
    );

    public static final KeyMapping OPEN_SEARCH_ITEM_FRAMES_GUI = new KeyMapping(
            "qualityofqueso.open_item_frame_search_gui",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_I,
            QOQ
    );

    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event) {
        event.register(ModKeybinds.QUICK_EQUIP);
        event.register(ModKeybinds.MOVE_CONTAINER);
        event.register(ModKeybinds.MOVE_INVENTORY);
        event.register(ModKeybinds.OPEN_SEARCH_ITEM_FRAMES_GUI);
    }
}