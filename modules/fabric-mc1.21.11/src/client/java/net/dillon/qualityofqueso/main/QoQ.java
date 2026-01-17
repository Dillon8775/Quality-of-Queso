package net.dillon.qualityofqueso.main;

import net.dillon.qualityofqueso.command.ItemFrameSearcherCommand;
import net.dillon.qualityofqueso.debug.ModHudEntries;
import net.dillon.qualityofqueso.keybind.ModKeybinds;
import net.dillon.qualityofqueso.option.base.BaseOptions;
import net.dillon.qualityofqueso.option.instance.ModClientOptions;
import net.dillon.qualityofqueso.option.instance.ModCommonOptions;
import net.dillon.qualityofqueso.option.instance.UniversalOptions;
import net.dillon.qualityofqueso.util.ModUtil;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Environment(EnvType.CLIENT)
public class QoQ implements ClientModInitializer {
    public static String SAVED_TEXT = "";
    public static String SAVED_ITEM_FRAME_TEXT = "";
    private static boolean LOADED = false;
    private static boolean UNLOADED = false;
    private static boolean CONTINUE = true;
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
        if (ModClientOptions.CLIENT.getInstance() == null) {
            ModClientOptions.CLIENT.setInstance(new ModClientOptions());
        }

        ModHudEntries.initializeDebugHudEntries();
        ModKeybinds.initializeKeybinds();
        registerCommands();

        ClientPlayConnectionEvents.JOIN.register((handler, packet, client) -> {
            if (uoptions().multiServerConfigs) {
                loadServerConfig(client);
            }
        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            unloadServerConfig(client);
        });

        ModUtil.info("Quality of Queso has successfully loaded!");
    }

    /**
     * Unloads server config and reloads universal one.
     */
    public static void unloadServerConfig(MinecraftClient client) {
        ModClientOptions.CLIENT.clearCustomDirectory();
        ModClientOptions.CLIENT.setFileName(BaseOptions.DEFAULT_CLIENT_FILE_NAME);
        ModClientOptions.CLIENT.load();
        ModCommonOptions.COMMON.clearCustomDirectory();
        ModCommonOptions.COMMON.setFileName(BaseOptions.DEFAULT_COMMON_FILE_NAME);
        ModCommonOptions.COMMON.load();
        if (client.player != null) {
            client.player.sendMessage(Text.translatable("qualityofqueso.unloaded_server_config").formatted(Formatting.GOLD), false);
        }
        if (uoptions().multiServerConfigs) {
            ModUtil.info("Reverting back to global QoQ config.");
        }
    }

    /**
     * Loads and saves server-specific config.
     */
    public static void loadServerConfig(MinecraftClient client) {
        if (!uoptions().multiServerConfigs) {
            return;
        }

        if (client.getCurrentServerEntry() == null) {
            return;
        } else if (client.getCurrentServerEntry().address == null) {
            return;
        }

        String address = client.getCurrentServerEntry().address;

        // Ignore loading on blacklisted server
        for (String blacklistedServer : uoptions().blacklistedServers) {
            if (client.getCurrentServerEntry().address.equals(blacklistedServer)) {
                return;
            }
        }

        String safe = address.replace(":", "_").replace(".", "-");

        File serverDir = FabricLoader.getInstance()
                .getConfigDir()
                .resolve("qoq/server-configs")
                .toFile();

        String clientServerConfig = safe + "_client.json";
        String commonServerConfig = safe + "_common.json";
        File clientServerFile = new File(serverDir, clientServerConfig);
        File commonServerFile = new File(serverDir, commonServerConfig);

        ModClientOptions cachedClientInstance = ModClientOptions.CLIENT.getInstance();
        ModCommonOptions cachedCommonInstance = ModCommonOptions.COMMON.getInstance();
        final boolean configExists = clientServerFile.exists() && commonServerFile.exists();
        ModClientOptions.CLIENT.setCustomDirectory(serverDir);
        ModClientOptions.CLIENT.setFileName(clientServerConfig);
        ModCommonOptions.COMMON.setCustomDirectory(serverDir);
        ModCommonOptions.COMMON.setFileName(commonServerConfig);
        if (!configExists) {
            ModClientOptions.CLIENT.setInstance(cachedClientInstance);
            ModClientOptions.CLIENT.save();
            ModCommonOptions.COMMON.setInstance(cachedCommonInstance);
            ModCommonOptions.COMMON.save();
            ModUtil.info("Creating new QoQ server config instance... (" + address + ")");
        }
        ModClientOptions.CLIENT.load();
        ModCommonOptions.COMMON.load();

        String message = !configExists ? "qualityofqueso.created_server_config" : "qualityofqueso.loaded_server_config";
        client.player.sendMessage(Text.translatable(message).formatted(Formatting.GOLD), false);
        ModUtil.info("Loaded QoQ config for " + address + ".");
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
        return ModClientOptions.CLIENT.getInstance();
    }

    /**
     * @return the common-options.
     */
    public static ModCommonOptions coptions() {
        return ModCommonOptions.COMMON.getInstance();
    }

    /**
     * @return universal options, unaffected by server configs.
     */
    public static UniversalOptions uoptions() {
        return UniversalOptions.UNIVERSAL.getInstance();
    }

    /**
     * Saves all configurations.
     */
    public static void saveAll(MinecraftClient client) {
        UniversalOptions.UNIVERSAL.save();
        if (uoptions().multiServerConfigs) {
            CONTINUE = true;
        }
        if (client.getCurrentServerEntry() != null && client.getCurrentServerEntry().address != null) {
            if (isServerBlacklisted(client) && !uoptions().multiServerConfigs && !UNLOADED) {
                unload(client, true);
            } else if (!uoptions().multiServerConfigs && !UNLOADED) {
                unload(client, true);
            }
            if (CONTINUE) {
                if (isServerBlacklisted(client)) {
                    if (!UNLOADED) {
                        unload(client, false);
                    }
                } else {
                    if (!LOADED) {
                        loadServerConfig(client);
                        LOADED = true;
                        UNLOADED = false;
                    }
                }
            }
        }
        ModClientOptions.CLIENT.save();
        ModCommonOptions.COMMON.save();
    }

    /**
     * Unloads a config properly.
     */
    private static void unload(MinecraftClient client, boolean preventContinuation) {
        unloadServerConfig(client);
        UNLOADED = true;
        LOADED = false;
        CONTINUE = !preventContinuation;
    }

    /**
     * Checks if any of the mod's features should function.
     */
    public static boolean modEnabled(MinecraftClient client) {
        Objects.requireNonNull(client, "\"client\" cannot be null.");

        if (isOnServer(client) && isServerBlacklisted(client)) {
            return false;
        }

        return options().enableMod;
    }

    /**
     * @return true if a server is blacklisted.
     */
    public static boolean isServerBlacklisted(MinecraftClient client) {
        if (client.getCurrentServerEntry() == null) {
            return false;
        } else if (client.getCurrentServerEntry().address == null) {
            return false;
        }

        return uoptions().blacklistedServers.contains(client.getCurrentServerEntry().address);
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