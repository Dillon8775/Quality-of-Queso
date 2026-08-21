package net.dillon.qualityofqueso.helper;

import com.mojang.blaze3d.platform.InputConstants;
import net.blay09.mods.balm.Balm;
import net.dillon.dillonlib.platform.Platforms;
import net.dillon.qualityofqueso.option.*;
import net.dillon.qualityofqueso.packet.ClientPreferencesC2SPacket;
import net.dillon.qualityofqueso.packet.ManualItemPickupC2SPacket;
import net.dillon.qualityofqueso.util.GlowCountdown;
import net.dillon.qualityofqueso.widget.SwapButton;
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
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static net.dillon.dillonlib.task.ClientTasks.getScreen;
import static net.dillon.qualityofqueso.helper.ModConstants.*;
import static net.dillon.qualityofqueso.option.OptionInstances.*;

/**
 * Utility class for the Quality of Queso mod.
 */
public class ModHelper {
    public static boolean LOADED = false;
    private static boolean UNLOADED = false;
    private static boolean CONTINUE = true;

    private static boolean KEY_ATTACK_WASDOWN;

    /**
     * @return an identifier with the quality of queso namespace.
     */
    public static Identifier qoqIdentifier(String name) {
        return Identifier.fromNamespaceAndPath("qualityofqueso", name);
    }

    /**
     * @return if the player is left-handed.
     */
    public static boolean isLeftHanded(Minecraft minecraft) {
        return minecraft.player.getMainArm().getOpposite() == HumanoidArm.RIGHT;
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
        return client().general().enableMod;
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
        return universal().blacklistedServers.contains(instance.getCurrentServer().ip);
    }

    /**
     * @return if the player is on a server.
     */
    public static boolean isOnServer(Minecraft client) {
        return !(client.getCurrentServer() == null);
    }

    /**
     * Sends client options to server, for reference.
     */
    public static void sendClientPreferencesToServer() {
        Balm.networking().sendToServer(new ClientPreferencesC2SPacket(
                client().management().includingHotbar,
                !client().management().includingHotbar || client().accessibility().perpendicularQuickMoving,
                !client().buttonDisplayOptions().displayLockInventory ? "UNLOCKED" : client().management().lockInventory.name()
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
    public static void cancelFluidFov(FogType state, float fov, CallbackInfoReturnable<Float> cir) {
        if (!client().fovEffects().fluids && (state == FogType.LAVA || state == FogType.WATER)) {
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
     * Stops the game if it should stop.
     */
    public static void stop(boolean shouldStop, String configName) {
        if (shouldStop) {
            try {
                Minecraft.getInstance().stop();
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
        if (!client().fovEffects().potions.enabled()) {
            return false;
        }

        if (client().fovEffects().potions.nonBeacon() && effect.isAmbient()) {
            return false;
        }

        return client().fovEffects().potions.enabled();
    }

    /**
     * Handles all cooldown-related timers.
     */
    public static void tickCooldowns() {
        if (client().management().containerFiltering && TRACKED_CONTAINER_COOLDOWN > 0) {
            TRACKED_CONTAINER_COOLDOWN--;
        }

        if (client().lockedSlots().lockedSlots && LOCKED_SLOT_SOUND_COOLDOWN > 0) {
            LOCKED_SLOT_SOUND_COOLDOWN--;
        }

        if (client().sorting().sorting.any() && SORT_SOUND_COOLDOWN > 0) {
            SORT_SOUND_COOLDOWN--;
        }

        if (client().management().swapping.any() && SwapButton.SWAP_COOLDOWN > 0) {
            SwapButton.SWAP_COOLDOWN--;
        }

        if (getScreen() != null && client().misc().enhancedCursor && ENHANCED_COOLDOWN_SWAP > 0) {
            ENHANCED_COOLDOWN_SWAP--;
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
                && client().management().lockInventory.inventoryLocked()
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
            if (client().fog().overworldFog && entity.level().dimension() == Level.OVERWORLD) {
                float percent = client().fog().overworldFogIntensity;
                float safePercent = Math.max(percent, 25F);
                float distanceScale = 100F / safePercent;
                distanceScale = Math.min(distanceScale, 4F);

                fogData.environmentalEnd *= distanceScale;
            }

            // Then check all fog types
            if (!client().fog().allFog
                    || (!client().fog().overworldFog && entity.level().dimension() == Level.OVERWORLD)
                    || (!client().fog().netherFog && entity.level().dimension() == Level.NETHER)) {
                fogData.renderDistanceEnd = Integer.MAX_VALUE;
                fogData.environmentalEnd = Integer.MAX_VALUE;
            } else if (client().fog().netherFog && entity.level().dimension() == Level.NETHER) { // Check nether fog
                float percent = client().fog().netherFogIntensity;
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
        if (common().itemFrameSearching) {
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
        if (universal().multiServerConfigs) {
            CONTINUE = true;
        }
        if (instance.getCurrentServer() != null && instance.getCurrentServer().ip != null) {
            // If the server is blacklisted, and multi-server configs are off, the config hasn't already been unloaded, unload it
            if (isServerBlacklisted(instance) && !universal().multiServerConfigs && !UNLOADED) {
                unload(true);
            } else if (!universal().multiServerConfigs && !UNLOADED) { // Otherwise, if multi-server configs are off and it hasn't been unloaded, unload it
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
        if (!universal().multiServerConfigs) {
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
        File serverDir = Platforms.getCommonPlatform().configDir()
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
            LOGGER.info("Creating new Quality of Queso server config instance for \"{}\".", address);

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
            instance.player.sendSystemMessage(Component.translatable(message).withStyle(ChatFormatting.GOLD));
        }

        // Log the multi-server config
        LOGGER.info("Successfully loaded Quality of Queso config for \"{}\".", address);
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
            instance.player.sendSystemMessage(Component.translatable("qualityofqueso.unloaded_server_config").withStyle(ChatFormatting.GOLD));
        }
        // Log the message
        if (universal().multiServerConfigs) {
            LOGGER.info("Reverting back to global Quality of Queso config.");
        }
    }

    /**
     * @return the list of popular keys used for handling search bars and other features.
     */
    public static List<Integer> popularKeys() {
        return List.of(
                InputConstants.KEY_T, InputConstants.KEY_E
        );
    }

    /**
     * @return a list of some disallowed keys, when using certain features.
     */
    @Deprecated
    public static List<Integer> disallowedKeys() {
        return List.of(
                InputConstants.KEY_ESCAPE,
                InputConstants.KEY_LSHIFT,
                InputConstants.KEY_RSHIFT,
                InputConstants.KEY_LCONTROL,
                InputConstants.KEY_RCONTROL,
                InputConstants.KEY_LALT,
                InputConstants.KEY_RALT,
                InputConstants.KEY_LGUI,
                InputConstants.KEY_RGUI
        );
    }

    /**
     * @return a list of all disallowed keys when using search features.
     */
    public static List<Integer> allDisallowedKeys() {
        return List.of(
                InputConstants.KEY_1,
                InputConstants.KEY_2,
                InputConstants.KEY_3,
                InputConstants.KEY_4,
                InputConstants.KEY_5,
                InputConstants.KEY_6,
                InputConstants.KEY_7,
                InputConstants.KEY_8,
                InputConstants.KEY_9,
                InputConstants.KEY_ESCAPE,
                InputConstants.KEY_LSHIFT,
                InputConstants.KEY_RSHIFT,
                InputConstants.KEY_LCONTROL,
                InputConstants.KEY_RCONTROL,
                InputConstants.KEY_LALT,
                InputConstants.KEY_RALT,
                InputConstants.KEY_LGUI,
                InputConstants.KEY_RGUI
        );
    }
}