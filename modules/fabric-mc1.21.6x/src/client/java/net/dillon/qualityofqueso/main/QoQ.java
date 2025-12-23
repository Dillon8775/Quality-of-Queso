package net.dillon.qualityofqueso.main;

import net.dillon.qualityofqueso.command.ItemFrameSearcherCommand;
import net.dillon.qualityofqueso.keybind.ModKeybinds;
import net.dillon.qualityofqueso.option.ModClientOptions;
import net.dillon.qualityofqueso.option.CommonOptions;
import net.dillon.qualityofqueso.util.ModUtil;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Environment(EnvType.CLIENT)
public class QoQ implements ClientModInitializer {
    public static String SAVED_TEXT = "";
    public static String SAVED_ITEM_FRAME_TEXT = "";
    public static final List<Integer> popularKeys = List.of(GLFW.GLFW_KEY_T, GLFW.GLFW_KEY_E);
    public static final List<Integer> allDisallowedKeys = List.of(
            GLFW.GLFW_KEY_1,
            GLFW.GLFW_KEY_2,
            GLFW.GLFW_KEY_3,
            GLFW.GLFW_KEY_4,
            GLFW.GLFW_KEY_5,
            GLFW.GLFW_KEY_6,
            GLFW.GLFW_KEY_7,
            GLFW.GLFW_KEY_8,
            GLFW.GLFW_KEY_9,
            GLFW.GLFW_KEY_ESCAPE,
            GLFW.GLFW_KEY_LEFT_SHIFT,
            GLFW.GLFW_KEY_RIGHT_SHIFT,
            GLFW.GLFW_KEY_LEFT_CONTROL,
            GLFW.GLFW_KEY_RIGHT_CONTROL,
            GLFW.GLFW_KEY_LEFT_ALT,
            GLFW.GLFW_KEY_RIGHT_ALT,
            GLFW.GLFW_KEY_LEFT_SUPER,
            GLFW.GLFW_KEY_RIGHT_SUPER
    );
    public static final List<Integer> disallowedKeys = List.of(
            GLFW.GLFW_KEY_ESCAPE,
            GLFW.GLFW_KEY_LEFT_SHIFT,
            GLFW.GLFW_KEY_RIGHT_SHIFT,
            GLFW.GLFW_KEY_LEFT_CONTROL,
            GLFW.GLFW_KEY_RIGHT_CONTROL,
            GLFW.GLFW_KEY_LEFT_ALT,
            GLFW.GLFW_KEY_RIGHT_ALT,
            GLFW.GLFW_KEY_LEFT_SUPER,
            GLFW.GLFW_KEY_RIGHT_SUPER
    );
    public static final Map<TagKey<Item>, EquipmentSlot> quicklyEquippables = Map.of(
            ItemTags.HEAD_ARMOR, EquipmentSlot.HEAD,
            ItemTags.CHEST_ARMOR, EquipmentSlot.CHEST,
            ItemTags.LEG_ARMOR, EquipmentSlot.LEGS,
            ItemTags.FOOT_ARMOR, EquipmentSlot.FEET
    );

    @Override
    public void onInitializeClient() {
        ModKeybinds.init();
        registerCommands();
        ModUtil.info("Quality of Queso has successfully loaded!");
    }

    /**
     * Registers the {@code Quality of Queso} commands.
     */
    private static void registerCommands() {
        if (coptions().itemFrameSearching) {
            CommandRegistrationCallback.EVENT.register((commandDispatcher, commandRegistryAccess, registrationEnvironment) -> {
                ItemFrameSearcherCommand.register(commandDispatcher, commandRegistryAccess);
            });
        }
    }

    /**
     * @return the client-options.
     */
    public static ModClientOptions options() {
        return ModClientOptions.CLIENT_OPTIONS.getInstance();
    }

    /**
     * @return the common-options.
     */
    public static CommonOptions coptions() {
        return CommonOptions.COMMON_OPTIONS.getInstance();
    }

    /**
     * Saves all configurations.
     */
    public static void saveAll() {
        ModClientOptions.CLIENT_OPTIONS.save();
        CommonOptions.COMMON_OPTIONS.save();
    }

    /**
     * Checks if any of the mod's features should function.
     */
    public static boolean modEnabled(MinecraftClient client) {
        Objects.requireNonNull(client, "\"client\" cannot be null.");

        if (isOnServer(client)) {
            for (String blacklistedServer : options().blacklistedServers) {
                if (client.getCurrentServerEntry().address.equals(blacklistedServer)) {
                    return false;
                }
            }
        }
        return options().enableMod;
    }

    /**
     * Checks if the {@code Flashback mod} is loaded.
     */
    public static boolean isFlashbackLoaded() {
        return FabricLoader.getInstance().isModLoaded("flashback");
    }

    /**
     * @return if the player is on a server.
     */
    public static boolean isOnServer(MinecraftClient client) {
        return !client.isInSingleplayer() && !(client.getCurrentServerEntry() == null);
    }
}