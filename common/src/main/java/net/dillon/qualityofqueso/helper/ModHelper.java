package net.dillon.qualityofqueso.helper;

import com.mojang.blaze3d.platform.InputConstants;
import net.blay09.mods.balm.Balm;
import net.dillon.dillonlib.annotation.Dill;
import net.dillon.dillonlib.annotation.DillEnv;
import net.dillon.qualityofqueso.event.management.SlotClickedEvents;
import net.dillon.qualityofqueso.option.ModClientOptions;
import net.dillon.qualityofqueso.option.ModCommonOptions;
import net.dillon.qualityofqueso.option.UniversalOptions;
import net.dillon.qualityofqueso.packet.serverbound.ClientPreferencesC2SPacket;
import net.dillon.qualityofqueso.packet.serverbound.ManualItemPickupC2SPacket;
import net.dillon.qualityofqueso.sound.OtherSoundEvents;
import net.dillon.qualityofqueso.timer.Timers;
import net.dillon.qualityofqueso.util.MobHitDingTracker;
import net.dillon.qualityofqueso.widget.management.SwapButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;
import java.util.List;

import static net.dillon.dillonlib.task.ClientTasks.*;
import static net.dillon.qualityofqueso.helper.ModConstants.*;
import static net.dillon.qualityofqueso.helper.SoundHelper.playDingSound;
import static net.dillon.qualityofqueso.option.ClientOptionInstances.client;
import static net.dillon.qualityofqueso.option.ClientOptionInstances.universal;
import static net.dillon.qualityofqueso.option.CommonOptionInstances.common;

/**
 * Utility class for the Quality of Queso mod.
 */
@Dill(env = DillEnv.COMMON)
public class ModHelper {
    private static boolean KEY_ATTACK_WASDOWN;

    /**
     * @return an identifier with the quality of queso namespace.
     */
    public static Identifier qoqIdentifier(String name) {
        return Identifier.fromNamespaceAndPath("qualityofqueso", name);
    }

    /**
     * Checks if any of the mod's features should function.
     */
    public static boolean modEnabled() {
        // If the player is on a server and the server is blacklisted, the mod is not considered enabled, so return false
        if (isOnServer() && isServerBlacklisted()) {
            return false;
        }

        // Otherwise, return if enableMod is enabled
        return common().enableMod;
    }

    /**
     * @return true if a server is blacklisted.
     */
    public static boolean isServerBlacklisted() {
        Minecraft minecraft = getMinecraft();

        // If the current server is null, not blacklisted, return false
        if (minecraft.getCurrentServer() == null) {
            return false;
        } else if (minecraft.getCurrentServer().ip == null) { // If the IP is null, not blacklisted, return false
            return false;
        }

        // Search through the blacklisted servers lists, and see if current IP address is in the list. Return true if present
        return universal().blacklistedServers.contains(minecraft.getCurrentServer().ip);
    }

    /**
     * @return if the player is on a server.
     */
    public static boolean isOnServer() {
        return !(getMinecraft().getCurrentServer() == null);
    }

    /**
     * Sends client options to server, for reference.
     */
    public static void sendClientPreferencesToServer() {
        Balm.networking().sendToServer(new ClientPreferencesC2SPacket(
                client().management().includingHotbar,
                !client().management().includingHotbar || client().accessibility().perpendicularQuickMoving,
                client().display().hideLockInventory ? "UNLOCKED" : client().management().lockInventory.name()
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
     * Ticks all runtime events.
     */
    public static void tickRuntime(Minecraft minecraft) {
        if (client().management().containerFiltering && TRACKED_CONTAINER_COOLDOWN > 0) {
            TRACKED_CONTAINER_COOLDOWN--;
        }

        if (client().lockedSlots().lockedSlots && LOCKED_SLOT_SOUND_COOLDOWN > 0) {
            LOCKED_SLOT_SOUND_COOLDOWN--;
        }

        if (client().sorting().sorting.enabled() && SORT_SOUND_COOLDOWN > 0) {
            SORT_SOUND_COOLDOWN--;
        }

        if (client().management().swapping.enabled() && SwapButton.SWAP_COOLDOWN > 0) {
            SwapButton.SWAP_COOLDOWN--;
        }

        if (getScreen() != null && client().gui().enhancedCursor && ENHANCED_COOLDOWN_SWAP > 0) {
            ENHANCED_COOLDOWN_SWAP--;
        }

        Timers.tick();
        MobHitDingTracker.tick(minecraft);
        ModHelper.tickManualItemPickup(minecraft);
        SlotClickedEvents.tickTradeAllTask();
        SlotClickedEvents.tickBulkCraftTask();
    }

    /**
     * Ticks all game events.
     */
    public static void tickGame() {
        // Tick game playtime
        if (ONE_SECOND_PASSED > 0) {
            ONE_SECOND_PASSED--;
        } else if (!isCurrentLevelNull()) {
            client().dingsAndAlarms().levelPlaytimes.merge(getLevelKey(true), 1L, Long::sum);
            ONE_SECOND_PASSED = DEFAULT_ONE_SECOND_PASSED_COOLDOWN;
        }

        // Tick all game timers
        Timers.tickGameTimer(TIMERS, false);
        Timers.tickGameTimer(STOPWATCHES, true);

        // Play alarm triggered sound
        if (client().dingsAndAlarms().alarmSounds && ALARM_TRIGGERED && ALARM_TRIGGERED_COOLDOWN == 0) {
            playDingSound(OtherSoundEvents.ALARM_RINGING);
            ALARM_TRIGGERED_COOLDOWN = DEFAULT_ALARM_TRIGGERED_COOLDOWN;
        }
    }

    /**
     * Allows the player to manually pickup an item with a locked inventory.
     */
    private static void tickManualItemPickup(Minecraft minecraft) {
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
        if (minecraft.player == null || isCurrentLevelNull()) {
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
        if (!modEnabled()) {
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
     * Saves all configurations.
     */
    public static void saveAndApplyChanges() {
        UniversalOptions.INSTANCE.save();
        ModClientOptions.INSTANCE.save();
        ModCommonOptions.INSTANCE.save();
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
     * @return a list of {@code all} disallowed keys when attempting to auto focus a search bar.
     */
    public static List<Integer> allDisallowedAutoFocusKeys() {
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
                InputConstants.KEY_RGUI,
                InputConstants.KEY_SPACE
        );
    }
}