package net.dillon.qualityofqueso.packet;

import net.dillon.qualityofqueso.util.GlowCountdown;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class PacketHandling implements ModInitializer {

    @Override
    public void onInitialize() {
        registerGlowSearchPacket();
    }

    /**
     * Registers the {@link CustomPayload} for making item frames glow.
     */
    private static void registerGlowSearchPacket() {
        PayloadTypeRegistry.playC2S().register(
                GlowSearchC2SPayload.ID,
                GlowSearchC2SPayload.CODEC
        );

        ServerPlayNetworking.registerGlobalReceiver(GlowSearchC2SPayload.ID,
                (payload, context) -> {
                    ServerPlayerEntity player = context.player();
                    ServerWorld world = player.getServerWorld();

                    Vec3d playerPos = player.getPos();
                    double radius = payload.radius(); // All item frames within the specified radius will be affected

                    // Find all nearby item frames
                    List<ItemFrameEntity> nearbyFrames = world.getEntitiesByClass(ItemFrameEntity.class,
                            new Box(playerPos.add(-radius, -radius, -radius), playerPos.add(radius, radius, radius)),
                            frame -> {
                        String[] terms = payload.query().split(",");
                        // If payload is clear, all item frames found are added to list no matter their stack.
                        if (payload.clear()) {
                            return true;
                        } else {
                            ItemStack stack = frame.getHeldItemStack();

                            // If item frame stack is empty it is not checked to glow
                            if (stack.isEmpty()) {
                                return false;
                            }

                            String itemName = stack.getItem().getName().getString().toLowerCase();
                            String itemId = Registries.ITEM.getId(stack.getItem()).toString().toLowerCase();

                            // Check all searched queries (separated by comma)
                            // If item frame has stack, add it to the list to glow
                            for (String term : terms) {
                                String trimmed = term.trim().toLowerCase();
                                if (itemName.contains(trimmed) || itemId.contains(trimmed)) {
                                    return true;
                                }
                            }

                            // Search by tag
                            // If item in item frame is in the tag searched, add it to the list to glow
                            if (payload.query().startsWith("#")) {
                                String tagSearch = payload.query().substring(1);
                                RegistryWrapper.WrapperLookup lookup = world.getRegistryManager();
                                RegistryWrapper<Item> itemRegistry = lookup.getOrThrow(RegistryKeys.ITEM);

                                for (TagKey<Item> tagKey : itemRegistry.streamTagKeys().toList()) {
                                    Identifier id = tagKey.id();
                                    if (id.getPath().toLowerCase().contains(tagSearch) || id.toString().toLowerCase().contains(tagSearch)) {
                                        if (stack.isIn(tagKey)) {
                                            return true;
                                        }
                                    }
                                }
                            }

                            // Otherwise return false and don't add to list
                            return false;
                        }
                    });

                    // If item frame is found, make it glow
                    for (ItemFrameEntity frame : nearbyFrames) {
                        frame.setGlowing(!payload.clear());
                        // If payload timer isn't null and not clearing, begin the countdown before the glow effect is removed
                        if (!payload.clear() && payload.timer() != 0) {
                            ((GlowCountdown)frame).startGlowCountdown(payload.timer() * 20);
                        }
                    }
                    if (nearbyFrames.isEmpty()) {
                        player.playSoundToPlayer(SoundEvents.BLOCK_NOTE_BLOCK_BASS.value(), SoundCategory.AMBIENT, 2.0F, 1.0F);
                    } else if (payload.clear()) {
                        player.playSoundToPlayer(SoundEvents.ENTITY_PLAYER_SPLASH, SoundCategory.AMBIENT, 1.0F, 1.0F);
                    } else {
                        player.playSoundToPlayer(SoundEvents.ENTITY_ARROW_HIT_PLAYER, SoundCategory.AMBIENT, 1.0F, 1.0F);
                    }
                }
        );
    }
}