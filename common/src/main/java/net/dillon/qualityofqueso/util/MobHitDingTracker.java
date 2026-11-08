package net.dillon.qualityofqueso.util;

import net.dillon.dillonlib.client.component.SoundBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static net.dillon.dillonlib.task.ClientTasks.isCurrentLevelNull;

/**
 * Tracks the mob hit ding, to play the correct sound upon hitting an entity.
 */
public class MobHitDingTracker {
    private static final long PLAYER_DEATH_CHECK_WINDOW_TICKS = 20L;
    private static final Map<Integer, PendingHit> pendingHitChecks = new HashMap<>();

    private record PendingHit(long checkAtGameTime, long expireAtGameTime, boolean wasPlayer) {}

    /**
     * Queues the hit for the mob hit ding.
     */
    public static void queueHit(int entityId, long checkAtGameTime, boolean wasPlayer) {
        long expireAtGameTime = wasPlayer ? checkAtGameTime + PLAYER_DEATH_CHECK_WINDOW_TICKS : checkAtGameTime;
        pendingHitChecks.put(entityId, new PendingHit(checkAtGameTime, expireAtGameTime, wasPlayer));
    }

    /**
     * Ticks the {@code mob, hit, ding!} sound effect, and plays the correct sound upon the entity dying or just being hit.
     */
    public static void tick(Minecraft minecraft) {
        if (pendingHitChecks.isEmpty() || isCurrentLevelNull() || minecraft.player == null) {
            pendingHitChecks.clear();
            return;
        }

        long currentTime = minecraft.level.getGameTime();
        Iterator<Map.Entry<Integer, PendingHit>> it = pendingHitChecks.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry<Integer, PendingHit> pending = it.next();
            PendingHit pendingHit = pending.getValue();
            if (currentTime < pendingHit.checkAtGameTime()) {
                continue;
            }

            Entity entity = minecraft.level.getEntity(pending.getKey());
            boolean deadOrRemoved = entity == null
                    || entity.isRemoved()
                    || !(entity instanceof LivingEntity living)
                    || living.isDeadOrDying()
                    || living.getHealth() <= 0.0F;

            boolean isPlayer = pendingHit.wasPlayer() || entity instanceof Player;
            if (!isPlayer) {
                SoundBuilder.create(SoundEvents.ARROW_HIT_PLAYER)
                        .volume(0.18F)
                        .buildAndPlay();
            }
            if (deadOrRemoved) {
                SoundBuilder.create(SoundEvents.PLAYER_LEVELUP)
                        .volume(0.32F)
                        .buildAndPlay();
            } else if (isPlayer && currentTime < pendingHit.expireAtGameTime()) {
                continue;
            }
            it.remove();
        }
    }
}