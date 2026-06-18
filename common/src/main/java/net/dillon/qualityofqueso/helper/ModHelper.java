package net.dillon.qualityofqueso.helper;

import net.blay09.mods.balm.Balm;
import net.dillon.qualityofqueso.option.*;
import net.dillon.qualityofqueso.packet.ClientPreferencesC2SPacket;
import net.dillon.qualityofqueso.packet.ManualItemPickupC2SPacket;
import net.dillon.qualityofqueso.platform.MultiLoader;
import net.dillon.qualityofqueso.util.GlowCountdown;
import net.dillon.qualityofqueso.widget.SwapButton;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
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
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static net.dillon.qualityofqueso.util.ModConstants.*;

/**
 * Utility class for the Quality of Queso mod.
 */
public class ModHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger("Quality of Queso");

    public static boolean LOADED = false;
    private static boolean UNLOADED = false;
    private static boolean CONTINUE = true;

    private static boolean KEY_ATTACK_WASDOWN;

    /**
     * @return an identifier with the quality of queso namespace.
     */
    public static Identifier ofQoQ(String name) {
        return Identifier.fromNamespaceAndPath("qualityofqueso", name);
    }

    /**
     * @return the current Minecraft screen.
     */
    public static Screen getCurrentScreen() {
        return Minecraft.getInstance().screen;
    }

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
     * Sends an {@code error} message to the console.
     */
    public static void error(String message) {
        LOGGER.error(message);
    }

    /**
     * Sends a {@code debug} message to the console.
     */
    public static void debug(String message) {
        LOGGER.debug(message);
    }

    /**
     * Rounds the value to the nearest tenths place.
     */
    public static double round(double d) {
        return Math.round(d * 10.0) / 10.0;
    }

    /**
     * Rounds a value to the nearest hundredths place.
     */
    public static double roundBig(double d) {
        return Math.round(d * 100.0) / 100.0;
    }

    /**
     * @return if the player is left-handed.
     */
    public static boolean isLeftHanded(Minecraft minecraft) {
        return minecraft.player.getMainArm().getOpposite() == HumanoidArm.RIGHT;
    }

    /**
     * @return the client-options.
     */
    public static ModClientOptions clientOptionsInstance() {
        return ModClientOptions.INSTANCE.getInstance();
    }

    /**
     * @return the common-options.
     */
    public static ModCommonOptions commonOptionsInstance() {
        return ModCommonOptions.INSTANCE.getInstance();
    }

    /**
     * @return universal options, unaffected by server configs.
     */
    public static UniversalOptions universalOptionsInstance() {
        return UniversalOptions.INSTANCE.getInstance();
    }

    /**
     * @return mixin options, unaffected by server configs.
     */
    public static MixinOptions mixinOptionsInstance() {
        return MixinOptions.INSTANCE.getInstance();
    }

    /**
     * @return tracked containers options.
     */
    public static ContainerData containerDataInstance() {
        return ContainerData.INSTANCE.getInstance();
    }

    /**
     * @return locked player slots, respective to the client-player.
     */
    public static LockedPlayerSlots lockedPlayerSlotsInstance() {
        return LockedPlayerSlots.INSTANCE.getInstance();
    }

    /**
     * @return locked container slots, for each container in a world.
     */
    public static LockedContainerSlots lockedContainerSlotsInstance() {
        return LockedContainerSlots.INSTANCE.getInstance();
    }

    /**
     * Checks if any of the mod's features should function.
     */
    public static boolean modEnabled(Minecraft minecraft) {
        // Ensure minecraft object is not null
        Objects.requireNonNull(minecraft, "\"client\" cannot be null.");

        // If the player is on a server and the server is blacklisted, the mod is not considered enabled, so return false
        if (isOnServer(minecraft) && isServerBlacklisted(minecraft)) {
            return false;
        }

        // Otherwise, return if enableMod is enabled
        return clientOptionsInstance().getGeneralOptions().enableMod;
    }

    /**
     * @return true if a server is blacklisted.
     */
    public static boolean isServerBlacklisted(Minecraft instance) {
        // If the current server is null, not blacklisted, return false
        if (instance.getCurrentServer() == null) {
            return false;
        } else if (instance.getCurrentServer().ip == null) { // If the IP is null, not blacklisted, return false
            return false;
        }

        // Search through the blacklisted servers lists, and see if current IP address is in the list. Return true if present
        return universalOptionsInstance().blacklistedServers.contains(instance.getCurrentServer().ip);
    }

    /**
     * @return if the player is on a server.
     */
    public static boolean isOnServer(Minecraft client) {
        return !client.isSingleplayer() && !(client.getCurrentServer() == null);
    }

    /**
     * Sends client options to server, for reference.
     */
    public static void sendClientPreferencesToServer() {
        Balm.networking().sendToServer(new ClientPreferencesC2SPacket(
                clientOptionsInstance().getManagementOptions().includingHotbar,
                !clientOptionsInstance().getManagementOptions().includingHotbar || clientOptionsInstance().getAccessibilityOptions().perpendicularQuickMoving,
                !clientOptionsInstance().getButtonDisplayOptions().displayLockInventory ? "UNLOCKED" : clientOptionsInstance().getManagementOptions().lockInventory.name()
                ));
    }

    /**
     * Clears armor HUD timer/cache state.
     */
    public static void resetArmorHudState() {
        Arrays.fill(ARMOR_TIMERS, 0);
        Arrays.fill(LAST_ARMOR_STACKS, null);
        CAN_ACTUALLY_RENDER_ARMOR_HOTBAR = false;
    }

    /**
     * Plays a sound at the correct position.
     */
    private static void playSound(ServerPlayer player, SoundEvent sound, float volume) {
        player.level().playSound(null, player.blockPosition(), sound, SoundSource.PLAYERS, volume, 1.0F);
    }

    /**
     * Cancels out fluid FOV change.
     */
    @Deprecated
    public static void cancelFluidFov(FogType state, float fov, CallbackInfoReturnable<Float> cir) {
        if (!clientOptionsInstance().getFovEffectOptions().fluids && (state == FogType.LAVA || state == FogType.WATER)) {
            cir.setReturnValue(fov);
        }
    }

    /**
     * @return an array list of valid equipment/armor slots.
     */
    public static EquipmentSlot[] equipmentSlots() {
        return new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
    }

    /**
     * @return a list of mappings for quickly equippable slots.
     */
    public static Map<TagKey<Item>, EquipmentSlot> quicklyEquippables() {
        return Map.of(
                ItemTags.HEAD_ARMOR, EquipmentSlot.HEAD,
                ItemTags.CHEST_ARMOR, EquipmentSlot.CHEST,
                ItemTags.LEG_ARMOR, EquipmentSlot.LEGS,
                ItemTags.FOOT_ARMOR, EquipmentSlot.FEET
        );
    }

    /**
     * Stops the game if it should stop.
     */
    public static void stop(boolean shouldStop, String configName) {
        if (shouldStop) {
            try {
                Minecraft.getInstance().destroy();
            } catch (NullPointerException e) {
                throw new NullPointerException("Quality of Queso's \"" + configName + "\" configuration file is null. Not sure what happened! Please delete this config file (located in your \".minecraft/config\" directory), and then you can relaunch your game.");
            }
        }
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
     * @return if the passed in effect instance is a beacon effect (or ambient).
     */
    public static boolean canApplyEffect(MobEffectInstance effect) {
        if (!clientOptionsInstance().getFovEffectOptions().potions.enabled()) {
            return false;
        }

        if (clientOptionsInstance().getFovEffectOptions().potions.nonBeacon() && effect.isAmbient()) {
            return false;
        }

        return clientOptionsInstance().getFovEffectOptions().potions.enabled();
    }

    /**
     * Handles all cooldown-related timers.
     */
    public static void tickCooldowns() {
        if (clientOptionsInstance().getManagementOptions().containerFiltering && TRACKED_CONTAINER_COOLDOWN > 0) {
            TRACKED_CONTAINER_COOLDOWN--;
        }

        if (clientOptionsInstance().getLockedSlotOptions().lockedSlots && LOCKED_SLOT_SOUND_COOLDOWN > 0) {
            LOCKED_SLOT_SOUND_COOLDOWN--;
        }

        if (clientOptionsInstance().getSortingOptions().sorting.buttonOrKeyOrKeyOnly() && SORT_SOUND_COOLDOWN > 0) {
            SORT_SOUND_COOLDOWN--;
        }

        if (clientOptionsInstance().getManagementOptions().swapping.buttonOrKeyOrKeyOnly() && SwapButton.SWAP_COOLDOWN > 0) {
            SwapButton.SWAP_COOLDOWN--;
        }
    }

    /**
     * Allows the player to manually pickup an item with a locked inventory.
     */
    public static void tickManualItemPickup(Minecraft minecraft) {
        boolean useDown = minecraft.options.keyAttack.isDown();
        if (!useDown) {
            KEY_ATTACK_WASDOWN = false;
        } else if (!KEY_ATTACK_WASDOWN
                && minecraft.player != null
                && clientOptionsInstance().getManagementOptions().lockInventory.inventoryLocked()
                && minecraft.player.isShiftKeyDown()) {
            ItemEntity target = ModHelper.raycastItemEntity(minecraft);
            if (target != null) {
                Balm.networking().sendToServer(new ManualItemPickupC2SPacket(target.getId()));
                KEY_ATTACK_WASDOWN = true;
            }
        }
    }

    /**
     * @return the most accurate raycasted {@link ItemEntity} to use to manually pickup an item.
     */
    private static ItemEntity raycastItemEntity(Minecraft minecraft) {
        if (minecraft.player == null || minecraft.level == null) {
            return null;
        }

        Vec3 from = minecraft.player.getEyePosition();
        Vec3 to = from.add(minecraft.player.getViewVector(1.0F).scale(1.5));
        AABB bounds = minecraft.player.getBoundingBox().expandTowards(to.subtract(from)).inflate(1.0);

        ItemEntity best = null;
        double bestDistSqr = Double.MAX_VALUE;

        for (ItemEntity item : minecraft.level.getEntitiesOfClass(ItemEntity.class, bounds, ItemEntity::isAlive)) {
            AABB box = item.getBoundingBox().inflate(0.02);
            var hit = box.clip(from, to);
            if (hit.isEmpty()) {
                continue;
            }

            double d = from.distanceToSqr(hit.get());
            if (d < bestDistSqr) {
                bestDistSqr = d;
                best = item;
            }
        }

        return best;
    }

    /**
     * Handles removing fog functionality.
     */
    public static void handleFog(Entity entity, FogType fogtype, FogData fogData) {
        if (!modEnabled(Minecraft.getInstance())) {
            return;
        }

        if (entity instanceof LivingEntity livingEntity &&
                !livingEntity.hasEffect(MobEffects.BLINDNESS) &&
                !livingEntity.hasEffect(MobEffects.DARKNESS) &&
                fogtype != FogType.WATER &&
                fogtype != FogType.LAVA &&
                fogtype != FogType.POWDER_SNOW) {
            // Check overworld fog first
            if (clientOptionsInstance().getFogOptions().overworldFog && entity.level().dimension() == Level.OVERWORLD) {
                float percent = clientOptionsInstance().getFogOptions().overworldFogIntensity;
                float safePercent = Math.max(percent, 25F);
                float distanceScale = 100F / safePercent;
                distanceScale = Math.min(distanceScale, 4F);

                fogData.environmentalEnd *= distanceScale;
            }

            // Then check all fog types
            if (!clientOptionsInstance().getFogOptions().allFog
                    || (!clientOptionsInstance().getFogOptions().overworldFog && entity.level().dimension() == Level.OVERWORLD)
                    || (!clientOptionsInstance().getFogOptions().netherFog && entity.level().dimension() == Level.NETHER)) {
                fogData.renderDistanceEnd = Integer.MAX_VALUE;
                fogData.environmentalEnd = Integer.MAX_VALUE;
            } else if (clientOptionsInstance().getFogOptions().netherFog && entity.level().dimension() == Level.NETHER) { // Check nether fog
                float percent = clientOptionsInstance().getFogOptions().netherFogIntensity;
                float t = (percent - 10F) / 90F;
                float fogEnd = 250F + t * (96F - 250F);

                fogData.renderDistanceEnd = fogEnd;
                fogData.environmentalEnd = fogEnd;
            }
        }
    }

    /**
     * Handles the glow packet.
     */
    public static void handleGlowPacket(ServerPlayer player, String query, boolean matchCase, boolean clear, int timer,
                                        int radius) {
        if (commonOptionsInstance().itemFrameSearching) {
            ServerLevel world = player.level();

            Vec3 playerPos = player.position();

            // Find all nearby item frames
            List<ItemFrame> nearbyFrames = world.getEntitiesOfClass(ItemFrame.class,
                    new AABB(playerPos.add(-radius, -radius, -radius), playerPos.add(radius, radius, radius)),
                    frame -> {
                        String[] terms = query.split(",");

                        // If payload is clear, all item frames found are added to list no matter their stack.
                        if (clear) {
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
                                if (matchCase ?
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
                frame.setGlowingTag(!clear);
                searched++; // Add to search count
                if (clear && !alreadyGlowing) {
                    searched--; // If clearing and the frame wasn't already glowing to begin with, subtract it from searched
                }
                // If payload timer isn't null and not clearing, begin the countdown before the glow effect is removed
                if (!clear && timer != 0) {
                    ((GlowCountdown) frame).startGlowCountdown(timer * 20);
                }
            }
            if (clear) { // Send clear message and play sound
                player.sendSystemMessage(Component.translatable("qualityofqueso.item_frame_searcher.executed.cleared", searched), false);
                playSound(player, SoundEvents.PLAYER_SPLASH, 1.0F);
            } else if (nearbyFrames.isEmpty()) { // Send no item frames found message
                player.sendSystemMessage(matchCase ?
                        Component.translatable("qualityofqueso.item_frame_searcher.executed.found_none.match_case", searched, query) :
                        Component.translatable("qualityofqueso.item_frame_searcher.executed.found_none", searched, query), false);
                playSound(player, SoundEvents.NOTE_BLOCK_BASS.value(), 2.0F);
            } else {
                if (timer == 0) { // Send found item frames with no timer
                    player.sendSystemMessage(matchCase ?
                            Component.translatable("qualityofqueso.item_frame_searcher.executed.without_timer.match_case", searched, query) :
                            Component.translatable("qualityofqueso.item_frame_searcher.executed.without_timer", searched, query));
                } else { // Send found item frames with timer
                    player.sendSystemMessage(matchCase ?
                            Component.translatable("qualityofqueso.item_frame_searcher.executed.with_timer.match_case", searched, query, timer) :
                            Component.translatable("qualityofqueso.item_frame_searcher.executed.with_timer", searched, query, timer), false);
                }
                // Play sound
                playSound(player, SoundEvents.ARROW_HIT_PLAYER, 1.0F);
            }
        }
    }

    /**
     * Saves all configurations.
     */
    public static void saveAndApplyConfigs(Minecraft instance) {
        UniversalOptions.INSTANCE.save();
        // Continue if multi-server configs are enabled
        if (universalOptionsInstance().multiServerConfigs) {
            CONTINUE = true;
        }
        if (instance.getCurrentServer() != null && instance.getCurrentServer().ip != null) {
            // If the server is blacklisted, and multi-server configs are off, the config hasn't already been unloaded, unload it
            if (isServerBlacklisted(instance) && !universalOptionsInstance().multiServerConfigs && !UNLOADED) {
                unload(true);
            } else if (!universalOptionsInstance().multiServerConfigs && !UNLOADED) { // Otherwise, if multi-server configs are off and it hasn't been unloaded, unload it
                unload(true);
            }
            // If we can continue...
            if (CONTINUE) {
                if (isServerBlacklisted(instance)) {
                    // If server is blacklisted and not been unloaded, unload it
                    if (!UNLOADED) {
                        unload(false);
                    }
                } else {
                    // If config hasn't been loaded, load it, set unloaded to false, because it has just been loaded
                    if (!LOADED) {
                        loadServerConfig();
                        LOADED = true;
                        UNLOADED = false;
                    }
                }
            }
        }
        // Save appropriate instances
        ModClientOptions.INSTANCE.save();
        ModCommonOptions.INSTANCE.save();
    }

    /**
     * Loads and saves server-specific config.
     */
    public static void loadServerConfig() {
        // Don't try to load a new config if multi-server configs are disabled
        if (!universalOptionsInstance().multiServerConfigs) {
            return;
        }

        Minecraft instance = Minecraft.getInstance();
        // Ensure server is valid
        if (instance.getCurrentServer() == null) {
            return;
        } else if (instance.getCurrentServer().ip == null) {
            return;
        }

        // Get safe address for the server's IP
        String address = instance.getCurrentServer().ip;
        String safe = address.replace(":", "_").replace(".", "-");
        String safeAndUnderscore = safe + "_";

        // Resolve the multi-server config directory, located in .minecraft/config/qualityofqueso/server-configs/*server-name*
        File serverDir = MultiLoader.getPlatform().getConfigDir()
                .resolve(DEFAULT_SERVER_CONFIG_DIR + safe)
                .toFile();

        // Get config file names
        String clientServerConfig = safeAndUnderscore + DEFAULT_CLIENT_CONFIG_FILE_NAME;
        String commonServerConfig = safeAndUnderscore + DEFAULT_COMMON_CONFIG_FILE_NAME;
        String containerDataServerConfig = safeAndUnderscore + DEFAULT_CONTAINER_DATA_FILE_NAME;
        String lockedContainerSlotsServerConfig = safeAndUnderscore + DEFAULT_LOCKED_CONTAINER_SLOTS_FILE_NAME;
        String lockedPlayerSlotsServerConfig = safeAndUnderscore + DEFAULT_LOCKED_PLAYER_SLOTS_FILE_NAME;

        // Get config files
        File clientServerFile = new File(serverDir, clientServerConfig);
        File commonServerFile = new File(serverDir, commonServerConfig);
        File containerDataServerFile = new File(serverDir, containerDataServerConfig);
        File lockedContainerSlotsServerFile = new File(serverDir, lockedContainerSlotsServerConfig);
        File lockedPlayerSlotsServerFile = new File(serverDir, lockedPlayerSlotsServerConfig);

        // Get the cached instances
        ModClientOptions cachedClientInstance = ModClientOptions.INSTANCE.getInstance();
        ModCommonOptions cachedCommonInstance = ModCommonOptions.INSTANCE.getInstance();
        ContainerData cachedContainerData = ContainerData.INSTANCE.getInstance();
        LockedContainerSlots cachedLockedContainerSlots = LockedContainerSlots.INSTANCE.getInstance();
        LockedPlayerSlots cachedLockedPlayerSlots = LockedPlayerSlots.INSTANCE.getInstance();

        // Determine if all config files exists
        final boolean configExists = clientServerFile.exists()
                && containerDataServerFile.exists()
                && commonServerFile.exists()
                && lockedContainerSlotsServerFile.exists()
                && lockedPlayerSlotsServerFile.exists();

        // Update config instances for the multi-server config
        ModClientOptions.INSTANCE.setCustomDirectory(serverDir);
        ModClientOptions.INSTANCE.setFileName(clientServerConfig);

        ModCommonOptions.INSTANCE.setCustomDirectory(serverDir);
        ModCommonOptions.INSTANCE.setFileName(commonServerConfig);

        ContainerData.INSTANCE.setCustomDirectory(serverDir);
        ContainerData.INSTANCE.setFileName(containerDataServerConfig);

        LockedContainerSlots.INSTANCE.setCustomDirectory(serverDir);
        LockedContainerSlots.INSTANCE.setFileName(lockedContainerSlotsServerConfig);

        LockedPlayerSlots.INSTANCE.setCustomDirectory(serverDir);
        LockedPlayerSlots.INSTANCE.setFileName(lockedPlayerSlotsServerConfig);

        // If the config wasn't present, send created message, and set the instance
        if (!configExists) {
            ModHelper.info("Creating new Quality of Queso server config instance for \"" + address + "\".");

            ModClientOptions.INSTANCE.setInstance(cachedClientInstance);
            ModClientOptions.INSTANCE.save();

            ModCommonOptions.INSTANCE.setInstance(cachedCommonInstance);
            ModCommonOptions.INSTANCE.save();

            ContainerData.INSTANCE.setInstance(cachedContainerData);
            ContainerData.INSTANCE.save();

            LockedContainerSlots.INSTANCE.setInstance(cachedLockedContainerSlots);
            LockedContainerSlots.INSTANCE.save();

            LockedPlayerSlots.INSTANCE.setInstance(cachedLockedPlayerSlots);
            LockedPlayerSlots.INSTANCE.save();
        }

        // Load all instances for new multi-server config
        ModClientOptions.INSTANCE.load();
        ModCommonOptions.INSTANCE.load();

        ContainerData.INSTANCE.load();
        LockedContainerSlots.INSTANCE.load();
        LockedPlayerSlots.INSTANCE.load();

        // Send appropriate message
        String message = !configExists ? "qualityofqueso.created_server_config" : "qualityofqueso.loaded_server_config";
        if (instance.player != null) {
            instance.player.displayClientMessage(Component.translatable(message).withStyle(ChatFormatting.GOLD), false);
        }

        // Log the multi-server config
        ModHelper.info("Successfully loaded Quality of Queso config for \"" + address + "\".");
    }

    /**
     * Unloads server config and reloads universal one.
     */
    public static void unloadServerConfig() {
        // Resets all current config instances to default (or global)
        ModClientOptions.INSTANCE.clearCustomDirectory();
        ModClientOptions.INSTANCE.setFileName(DEFAULT_CLIENT_CONFIG_FILE_NAME);
        ModClientOptions.INSTANCE.load();

        ModCommonOptions.INSTANCE.clearCustomDirectory();
        ModCommonOptions.INSTANCE.setFileName(DEFAULT_COMMON_CONFIG_FILE_NAME);
        ModCommonOptions.INSTANCE.load();

        ContainerData.INSTANCE.clearCustomDirectory();
        ContainerData.INSTANCE.setFileName(DEFAULT_CONTAINER_DATA_FILE_NAME);
        ContainerData.INSTANCE.load();

        LockedContainerSlots.INSTANCE.clearCustomDirectory();
        LockedContainerSlots.INSTANCE.setFileName(DEFAULT_LOCKED_CONTAINER_SLOTS_FILE_NAME);
        LockedContainerSlots.INSTANCE.load();

        LockedPlayerSlots.INSTANCE.clearCustomDirectory();
        LockedPlayerSlots.INSTANCE.setFileName(DEFAULT_LOCKED_PLAYER_SLOTS_FILE_NAME);
        LockedPlayerSlots.INSTANCE.load();

        // Send reset message
        Minecraft instance = Minecraft.getInstance();
        if (instance.player != null) {
            instance.player.displayClientMessage(Component.translatable("qualityofqueso.unloaded_server_config").withStyle(ChatFormatting.GOLD), false);
        }
        // Log the message
        if (universalOptionsInstance().multiServerConfigs) {
            ModHelper.info("Reverting back to global Quality of Queso config.");
        }
    }

    /**
     * @return the list of popular keys used for handling search bars and other features.
     */
    public static List<Integer> popularKeys() {
        return List.of(
                GLFW.GLFW_KEY_T, GLFW.GLFW_KEY_E
        );
    }

    /**
     * @return a list of some disallowed keys, when using certain features.
     */
    @Deprecated
    public static List<Integer> disallowedKeys() {
        return List.of(
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
    }

    /**
     * @return a list of all disallowed keys when using search features.
     */
    public static List<Integer> allDisallowedKeys() {
        return List.of(
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
    }
}