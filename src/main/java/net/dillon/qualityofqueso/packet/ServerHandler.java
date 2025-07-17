package net.dillon.qualityofqueso.packet;

import net.dillon.qualityofqueso.option.ModServerOptions;
import net.dillon.qualityofqueso.util.GlowCountdown;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public final class ServerHandler implements ModInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger("Quality of QUESO");

    /**
     * Handles server/client conflicts.
     */
    @Override
    public void onInitialize() {
        // Register packet on SERVER environment ONLY IF it should be enabled
        if (FabricLoader.getInstance().getEnvironmentType().equals(EnvType.SERVER)) {
            if (ModServerOptions.SERVER_OPTIONS.getInstance().itemFrameSearchingOnServer) {
                registerGlowSearchPacketReceiver();
                info("Registered glowing packet on SERVER.");
            } else {
                info("Did NOT register glowing packet, \"itemFrameSearchingOnServer\" is disabled. No-one can use this feature unless enabled here on server environment.");
            }
        }
        // Only register packet on DEDICATED SERVER (if the player is in singleplayer or owner has the mod installed)
        else if (!FabricLoader.getInstance().getEnvironmentType().equals(EnvType.SERVER)) {
            registerGlowSearchPacketReceiver();
            info("Registered glowing packet on DEDICATED SERVER.");
        }
    }

    /**
     * Registers the dedicated server-side payload for receiving the packet to make item frames glow.
     */
    private static void registerGlowSearchPacketReceiver() {
        PayloadTypeRegistry.playC2S().register(
                GlowSearchC2SPayload.ID,
                GlowSearchC2SPayload.CODEC
        );

        ServerPlayNetworking.registerGlobalReceiver(GlowSearchC2SPayload.ID,
                (payload, context) -> {
                    ServerPlayerEntity player = context.player();
                    ServerWorld world = player.getWorld();

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

                    int searched = 0;
                    // If item frame is found, make it glow
                    for (ItemFrameEntity frame : nearbyFrames) {
                        boolean alreadyGlowing = frame.isGlowing();
                        frame.setGlowing(!payload.clear());
                        searched++; // Add to search count
                        if (payload.clear() && !alreadyGlowing) {
                            searched--; // If clearing and the frame wasn't already glowing to begin with, subtract it from searched
                        }
                        // If payload timer isn't null and not clearing, begin the countdown before the glow effect is removed
                        if (!payload.clear() && payload.timer() != 0) {
                            ((GlowCountdown)frame).startGlowCountdown(payload.timer() * 20);
                        }
                    }
                    if (nearbyFrames.isEmpty()) {
                        player.sendMessage(Text.translatable("qualityofqueso.item_frame_searcher.executed.found_none", searched, payload.query()), false);
                        player.playSoundToPlayer(SoundEvents.BLOCK_NOTE_BLOCK_BASS.value(), SoundCategory.AMBIENT, 2.0F, 1.0F);
                    } else if (payload.clear()) {
                        player.sendMessage(Text.translatable("qualityofqueso.item_frame_searcher.executed.cleared", searched), false);
                        player.playSoundToPlayer(SoundEvents.ENTITY_PLAYER_SPLASH, SoundCategory.AMBIENT, 1.0F, 1.0F);
                    } else {
                        if (payload.timer() == 0) {
                            player.sendMessage(Text.translatable("qualityofqueso.item_frame_searcher.executed.without_timer", searched, payload.query()), false);
                        } else {
                            player.sendMessage(Text.translatable("qualityofqueso.item_frame_searcher.executed.with_timer", searched, payload.query(), payload.timer()), false);
                        }
                        player.playSoundToPlayer(SoundEvents.ENTITY_ARROW_HIT_PLAYER, SoundCategory.AMBIENT, 1.0F, 1.0F);
                    }
                }
        );
    }

    /**
     * Sends a message to console.
     */
    public static void info(String message) {
        LOGGER.info(message);
    }
}