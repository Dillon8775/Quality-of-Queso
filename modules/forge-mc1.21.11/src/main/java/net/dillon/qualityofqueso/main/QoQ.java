package net.dillon.qualityofqueso.main;

import net.dillon.qualityofqueso.debug.ModHudEntries;
import net.dillon.qualityofqueso.option.base.BaseOptions;
import net.dillon.qualityofqueso.option.instance.ModClientOptions;
import net.dillon.qualityofqueso.option.instance.ModCommonOptions;
import net.dillon.qualityofqueso.option.instance.UniversalOptions;
import net.dillon.qualityofqueso.option.screen.ModOptionsScreen;
import net.dillon.qualityofqueso.packet.ServerHandler;
import net.dillon.qualityofqueso.util.ModUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLPaths;
import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.util.*;

@Mod(QoQ.MOD_ID)
public final class QoQ {
    public static final String MOD_ID = "qualityofqueso";
    public static String SAVED_TEXT = "";
	public static String SAVED_ITEM_FRAME_TEXT = "";
    public static Map<Integer, Set<Integer>> SAVED_EXCLUDED_SLOTS = new HashMap<>();
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

    public QoQ(FMLJavaModLoadingContext context) {
        var modBusGroup = context.getModBusGroup();

        if (FMLEnvironment.dist.isClient()) {
            if (ModClientOptions.CLIENT.getInstance() == null) {
                ModClientOptions.CLIENT.setInstance(new ModClientOptions());
            }
            if (UniversalOptions.UNIVERSAL.getInstance() == null) {
                UniversalOptions.UNIVERSAL.setInstance(new UniversalOptions());
            }
            ModHudEntries.initializeDebugHudEntries();

            ModLoadingContext.get().registerExtensionPoint(
                    ConfigScreenHandler.ConfigScreenFactory.class,
                    () -> new ConfigScreenHandler.ConfigScreenFactory(
                            (minecraft, parent) -> new ModOptionsScreen(parent)
                    )
            );
        }
        if (ModCommonOptions.COMMON.getInstance() == null) {
            ModCommonOptions.COMMON.setInstance(new ModCommonOptions());
        }

        FMLCommonSetupEvent.getBus(modBusGroup).addListener(this::commonSetup);
    }

    /**
     * @return {@code true} if the simple keybinds mod is loaded.
     */
    @OnlyIn(Dist.CLIENT)
    public static boolean isSimpleKeybindsLoaded() {
        return ModList.get().isLoaded("simplekeybinds");
    }

    /**
     * Unloads server config and reloads universal one.
     */
    @OnlyIn(Dist.CLIENT)
    public static void unloadServerConfig() {
        ModClientOptions.CLIENT.clearCustomDirectory();
        ModClientOptions.CLIENT.setFileName(BaseOptions.DEFAULT_CLIENT_FILE_NAME);
        ModClientOptions.CLIENT.load();
        ModCommonOptions.COMMON.clearCustomDirectory();
        ModCommonOptions.COMMON.setFileName(BaseOptions.DEFAULT_COMMON_FILE_NAME);
        ModCommonOptions.COMMON.load();
        Minecraft instance = Minecraft.getInstance();
        if (instance.player != null) {
            instance.player.displayClientMessage(Component.translatable("qualityofqueso.unloaded_server_config").withStyle(ChatFormatting.GOLD), false);
        }
        if (uoptions().multiServerConfigs) {
            ModUtil.info("Reverting back to global QoQ config.");
        }
    }

    /**
     * Loads and saves server-specific config.
     */
    @OnlyIn(Dist.CLIENT)
    public static void loadServerConfig() {
        if (!uoptions().multiServerConfigs) {
            return;
        }

        Minecraft instance = Minecraft.getInstance();
        if (instance.getCurrentServer() == null) {
            return;
        } else if (instance.getCurrentServer().ip == null) {
            return;
        }

        String address = instance.getCurrentServer().ip;
        String safe = address.replace(":", "_").replace(".", "-");

        File serverDir = FMLPaths.CONFIGDIR.get()
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
        if (instance.player != null) {
            instance.player.displayClientMessage(Component.translatable(message).withStyle(ChatFormatting.GOLD), false);
        }
        ModUtil.info("Loaded QoQ config for " + address + ".");
    }

    /**
     * Common setup stuff.
     */
    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(ServerHandler::registerGlowSearchPayload);
    }

    /**
     * @return the client-options.
     */
    @OnlyIn(Dist.CLIENT)
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
    @OnlyIn(Dist.CLIENT)
    public static UniversalOptions uoptions() {
        return UniversalOptions.UNIVERSAL.getInstance();
    }

    /**
     * Saves all configurations.
     */
    @OnlyIn(Dist.CLIENT)
    public static void saveAll(Minecraft instance) {
        UniversalOptions.UNIVERSAL.save();
        if (uoptions().multiServerConfigs) {
            CONTINUE = true;
        }
        if (instance.getCurrentServer() != null && instance.getCurrentServer().ip != null) {
            if (isServerBlacklisted(instance) && !uoptions().multiServerConfigs && !UNLOADED) {
                unload(true);
            } else if (!uoptions().multiServerConfigs && !UNLOADED) {
                unload(true);
            }
            if (CONTINUE) {
                if (isServerBlacklisted(instance)) {
                    if (!UNLOADED) {
                        unload(false);
                    }
                } else {
                    if (!LOADED) {
                        loadServerConfig();
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
    @OnlyIn(Dist.CLIENT)
    private static void unload(boolean preventContinuation) {
        unloadServerConfig();
        UNLOADED = true;
        LOADED = false;
        CONTINUE = !preventContinuation;
    }

    /**
	 * Checks if any of the mod's features should function.
	 */
	@OnlyIn(Dist.CLIENT)
    public static boolean modEnabled(Minecraft client) {
		Objects.requireNonNull(client, "\"client\" cannot be null.");

        if (isOnServer(client) && isServerBlacklisted(client)) {
            return false;
        }

		return options().enableMod;
    }

    /**
     * @return true if a server is blacklisted.
     */
    @OnlyIn(Dist.CLIENT)
    public static boolean isServerBlacklisted(Minecraft instance) {
        if (instance.getCurrentServer() == null) {
            return false;
        } else if (instance.getCurrentServer().ip == null) {
            return false;
        }

        return uoptions().blacklistedServers.contains(instance.getCurrentServer().ip);
    }

    /**
     * @return if the player is on a server.
     */
    @OnlyIn(Dist.CLIENT)
    public static boolean isOnServer(Minecraft client) {
        return !client.isSingleplayer() && !(client.getCurrentServer() == null);
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MOD_ID, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
        }
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, value = Dist.DEDICATED_SERVER)
    public static class ServerModEvents {

        @SubscribeEvent
        public static void onServerSetup(FMLDedicatedServerSetupEvent event) {
        }
    }
}
