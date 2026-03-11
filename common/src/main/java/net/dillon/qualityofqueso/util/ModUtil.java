package net.dillon.qualityofqueso.util;

import net.dillon.qualityofqueso.option.base.BaseOptions;
import net.dillon.qualityofqueso.option.instance.ModClientOptions;
import net.dillon.qualityofqueso.option.instance.ModCommonOptions;
import net.dillon.qualityofqueso.option.instance.TrackedContainers;
import net.dillon.qualityofqueso.option.instance.UniversalOptions;
import net.dillon.qualityofqueso.packet.GlowSearchC2SPayload;
import net.dillon.qualityofqueso.platform.MultiLoader;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;

import static net.dillon.qualityofqueso.util.GuiUtil.ARMOR_TIMERS;
import static net.dillon.qualityofqueso.util.GuiUtil.LAST_ARMOR_STACKS;

/**
 * Utility class for the Quality of Queso mod.
 */
public class ModUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger("Quality of Queso");
    public static String SAVED_TEXT = "";
    public static String SAVED_ITEM_FRAME_TEXT = "";
    public static Map<Integer, Set<Integer>> SAVED_EXCLUDED_SLOTS = new HashMap<>();
    public static boolean LOADED = false;
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

    /**
     * Sends a message to console.
     */
    public static void info(String message) {
        LOGGER.info(message);
    }

    /**
     * Sends a {@code warning} message to console.
     */
    public static void warn(String message) {
        LOGGER.warn(message);
    }

    /**
     * @return an identifier with the quality of queso namespace.
     */
    public static Identifier ofQoQ(String name) {
        return Identifier.fromNamespaceAndPath("qualityofqueso", name);
    }

    /**
     * @return if the player is left-handed.
     */
    public static boolean isLeftHanded(Minecraft minecraft) {
        return minecraft.player.getMainArm().getOpposite() == HumanoidArm.RIGHT;
    }

    /**
     * Unloads server config and reloads universal one.
     */
    public static void unloadServerConfig() {
        ModClientOptions.CLIENT.clearCustomDirectory();
        ModClientOptions.CLIENT.setFileName(BaseOptions.DEFAULT_CLIENT_FILE_NAME);
        ModClientOptions.CLIENT.load();
        TrackedContainers.TRACKED_CONTAINERS.clearCustomDirectory();
        TrackedContainers.TRACKED_CONTAINERS.setFileName(BaseOptions.DEFAULT_TRACKED_CONTAINERS_NAME);
        TrackedContainers.TRACKED_CONTAINERS.load();
        ModCommonOptions.COMMON.clearCustomDirectory();
        ModCommonOptions.COMMON.setFileName(BaseOptions.DEFAULT_COMMON_FILE_NAME);
        ModCommonOptions.COMMON.load();
        Minecraft instance = Minecraft.getInstance();
        if (instance.player != null) {
            instance.player.sendOverlayMessage(Component.translatable("qualityofqueso.unloaded_server_config", safeAddress(instance.getCurrentServer().ip)).withStyle(ChatFormatting.GOLD));
        }
        if (uoptions().multiServerConfigs) {
            ModUtil.info("Reverting back to global QoQ config.");
        }
    }

    /**
     * @return a safe address to display in chat.
     */
    private static Component safeAddress(String address) {
        Component text = Component.literal(address).copy().withStyle(ChatFormatting.AQUA);
        for (char c :  address.toCharArray()) {
            if (Character.isDigit(c)) {
                text = text.copy().withStyle(ChatFormatting.OBFUSCATED);
                break;
            }
        }
        return text;
    }

    /**
     * Loads and saves server-specific config.
     */
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

        File serverDir = MultiLoader.PLATFORM.getConfigDir()
                .resolve("qoq/server-configs")
                .toFile();

        String clientServerConfig = safe + "_client.json";
        String trackedContainersServerConfig =  safe + "_tracked-containers.json";
        String commonServerConfig = safe + "_common.json";
        File clientServerFile = new File(serverDir, clientServerConfig);
        File trackedContainersServerFile = new File(serverDir, trackedContainersServerConfig);
        File commonServerFile = new File(serverDir, commonServerConfig);

        ModClientOptions cachedClientInstance = ModClientOptions.CLIENT.getInstance();
        TrackedContainers cachedTrackedContainers = TrackedContainers.TRACKED_CONTAINERS.getInstance();
        ModCommonOptions cachedCommonInstance = ModCommonOptions.COMMON.getInstance();
        final boolean configExists = clientServerFile.exists() && trackedContainersServerFile.exists() && commonServerFile.exists();
        ModClientOptions.CLIENT.setCustomDirectory(serverDir);
        ModClientOptions.CLIENT.setFileName(clientServerConfig);
        TrackedContainers.TRACKED_CONTAINERS.setCustomDirectory(serverDir);
        TrackedContainers.TRACKED_CONTAINERS.setFileName(trackedContainersServerConfig);
        ModCommonOptions.COMMON.setCustomDirectory(serverDir);
        ModCommonOptions.COMMON.setFileName(commonServerConfig);
        if (!configExists) {
            ModClientOptions.CLIENT.setInstance(cachedClientInstance);
            ModClientOptions.CLIENT.save();
            TrackedContainers.TRACKED_CONTAINERS.setInstance(cachedTrackedContainers);
            TrackedContainers.TRACKED_CONTAINERS.save();
            ModCommonOptions.COMMON.setInstance(cachedCommonInstance);
            ModCommonOptions.COMMON.save();
            ModUtil.info("Creating new QoQ server config instance... (" + address + ")");
        }
        ModClientOptions.CLIENT.load();
        TrackedContainers.TRACKED_CONTAINERS.load();
        ModCommonOptions.COMMON.load();

        String message = !configExists ? "qualityofqueso.created_server_config" : "qualityofqueso.loaded_server_config";
        if (instance.player != null) {
            instance.player.sendOverlayMessage(Component.translatable(message, safeAddress(instance.getCurrentServer().ip)).withStyle(ChatFormatting.GOLD));
        }
        ModUtil.info("Loaded QoQ config for " + address + ".");
    }

    /**
     * @return the client-options.
     */
    public static ModClientOptions options() {
        return ModClientOptions.CLIENT.getInstance();
    }

    /**
     * @return tracked containers options.
     */
    public static TrackedContainers trackedContainers() {
        return TrackedContainers.TRACKED_CONTAINERS.getInstance();
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
     * Clears armor HUD timer/cache state.
     */
    public static void resetArmorHudState() {
        Arrays.fill(ARMOR_TIMERS, 0);
        Arrays.fill(LAST_ARMOR_STACKS, null);
    }

    /**
     * Saves all configurations.
     */
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
     * @return if the simple keybinds mod is loaded.
     */
    public static boolean isSimpleKeybindsLoaded() {
        return MultiLoader.PLATFORM.isModLoaded("simplekeybinds");
    }

    /**
     * Unloads a config properly.
     */
    private static void unload(boolean preventContinuation) {
        unloadServerConfig();
        UNLOADED = true;
        LOADED = false;
        CONTINUE = !preventContinuation;
    }

    /**
     * Checks if any of the mod's features should function.
     */
    public static boolean modEnabled(Minecraft client) {
        Objects.requireNonNull(client, "\"client\" cannot be null.");

        if (isOnServer(client) && isServerBlacklisted(client)) {
            return false;
        }

        return options().misc.enableMod;
    }

    /**
     * @return true if a server is blacklisted.
     */
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
    public static boolean isOnServer(Minecraft client) {
        return !client.isSingleplayer() && !(client.getCurrentServer() == null);
    }

    public static void handleGlowPayload(GlowSearchC2SPayload payload, ServerPlayer player) {
        if (coptions().itemFrameSearching) {
            ServerLevel world = player.level();

            Vec3 playerPos = player.position();

            // Find all nearby item frames
            List<ItemFrame> nearbyFrames = world.getEntitiesOfClass(ItemFrame.class,
                    new AABB(playerPos.add(-payload.radius(), -payload.radius(), -payload.radius()), playerPos.add(payload.radius(), payload.radius(), payload.radius())),
                    frame -> {
                        String[] terms = payload.query().split(",");

                        // If payload is clear, all item frames found are added to list no matter their stack.
                        if (payload.clear()) {
                            return true;
                        } else {
                            ItemStack stack = frame.getItem();

                            // If item frame stack is empty it is not checked to glow
                            if (stack.isEmpty()) {
                                return false;
                            }

                            String rawItemName = stack.getHoverName().getString().toLowerCase();

                            // Check all searched queries (separated by comma)
                            // If item frame has stack, add it to the list to glow
                            for (String term : terms) {
                                String trimmed = term.trim().toLowerCase();
                                if (payload.matchCase() ?
                                        rawItemName.matches(trimmed) :
                                        rawItemName.contains(trimmed)) {
                                    return true;
                                }
                            }
                        }

                        // Otherwise return false and don't add to list
                        return false;
                    });

            int searched = 0;
            // If item is found, make it glow
            for (ItemFrame frame : nearbyFrames) {
                boolean alreadyGlowing = frame.isCurrentlyGlowing();
                frame.setGlowingTag(!payload.clear());
                searched++; // Add to search count
                if (payload.clear() && !alreadyGlowing) {
                    searched--; // If clearing and the frame wasn't already glowing to begin with, subtract it from searched
                }
                // If payload timer isn't null and not clearing, begin the countdown before the glow effect is removed
                if (!payload.clear() && payload.timer() != 0) {
                    ((GlowCountdown)frame).startGlowCountdown(payload.timer() * 20);
                }
            }
            if (payload.clear()) {
                player.sendSystemMessage(Component.translatable("qualityofqueso.item_frame_searcher.executed.cleared", searched), false);
                playSound(player, SoundEvents.PLAYER_SPLASH, 1.0F);
            } else if (nearbyFrames.isEmpty()) {
                player.sendSystemMessage(payload.matchCase() ?
                        Component.translatable("qualityofqueso.item_frame_searcher.executed.found_none.match_case", searched, payload.query()) :
                        Component.translatable("qualityofqueso.item_frame_searcher.executed.found_none", searched, payload.query()), false);
                playSound(player, SoundEvents.NOTE_BLOCK_BASS.value(), 2.0F);
            } else {
                if (payload.timer() == 0) {
                    player.sendSystemMessage(payload.matchCase() ?
                            Component.translatable("qualityofqueso.item_frame_searcher.executed.without_timer.match_case", searched, payload.query()) :
                            Component.translatable("qualityofqueso.item_frame_searcher.executed.without_timer", searched, payload.query()));
                } else {
                    player.sendSystemMessage(payload.matchCase() ?
                            Component.translatable("qualityofqueso.item_frame_searcher.executed.with_timer.match_case", searched, payload.query(), payload.timer()) :
                            Component.translatable("qualityofqueso.item_frame_searcher.executed.with_timer", searched, payload.query(), payload.timer()), false);
                }
                playSound(player, SoundEvents.ARROW_HIT_PLAYER, 1.0F);
            }
        }
    }

    /**
     * Plays a sound at the correct position.
     */
    private static void playSound(ServerPlayer player, SoundEvent sound, float volume) {
        player.level().playSound(null, player.blockPosition(), sound, SoundSource.PLAYERS, volume, 1.0F);
    }

    /**
     * Handles removing fog functionality.
     */
    public static void handleFog(Entity entity, FogType fogtype, FogData fogData) {
        if (entity instanceof LivingEntity livingEntity &&
                !livingEntity.hasEffect(MobEffects.BLINDNESS) &&
                !livingEntity.hasEffect(MobEffects.DARKNESS) &&
                !options().misc.fog &&
                fogtype != FogType.WATER &&
                fogtype != FogType.LAVA &&
                fogtype != FogType.POWDER_SNOW) {
            fogData.renderDistanceEnd = Integer.MAX_VALUE;
            fogData.environmentalEnd = Integer.MAX_VALUE;
        }
    }
}
