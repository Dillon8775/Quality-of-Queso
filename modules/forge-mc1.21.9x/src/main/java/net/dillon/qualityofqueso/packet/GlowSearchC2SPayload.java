package net.dillon.qualityofqueso.packet;

import net.dillon.qualityofqueso.option.ModCommonOptions;
import net.dillon.qualityofqueso.util.GlowCountdown;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

/**
 * The payload (or packet) for taking in the data required to make item frames glow.
 */
public record GlowSearchC2SPayload(String query, boolean matchCase, boolean clear, int timer, int radius) {

    /**
     * Decoding for glow search packet.
     */
    public GlowSearchC2SPayload(FriendlyByteBuf buf) {
        this(buf.readUtf(), buf.readBoolean(), buf.readBoolean(), buf.readInt(), buf.readInt());
    }

    /**
     * Encoding for glow search packet.
     */
    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(this.query);
        buf.writeBoolean(this.matchCase);
        buf.writeBoolean(this.clear);
        buf.writeInt(this.timer);
        buf.writeInt(this.radius);
    }

    /**
     * Handles the item frame search glow packet.
     */
    public void handle(CustomPayloadEvent.Context context) {
        if (ModCommonOptions.ITEM_FRAME_SEARCHING.get()) {
            ServerPlayer player = context.getSender();
            ServerLevel world = player.level();

            Vec3 playerPos = player.position();

            // Find all nearby item frames
            List<ItemFrame> nearbyFrames = world.getEntitiesOfClass(ItemFrame.class,
                    new AABB(playerPos.add(-radius, -radius, - radius), playerPos.add(radius, radius, radius)),
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

                            String itemName = stack.getItem().getName().getString().toLowerCase();
                            String itemId = ForgeRegistries.ITEMS.getResourceKey(stack.getItem()).toString().toLowerCase();

                            // Check all searched queries (separated by comma)
                            // If item frame has stack, add it to the list to glow
                            for (String term : terms) {
                                String trimmed = term.trim().toLowerCase();
                                if (matchCase ?
                                        itemName.matches(trimmed) || itemId.matches(trimmed) :
                                        itemName.contains(trimmed) || itemId.matches(trimmed)) {
                                    return true;
                                }
                            }

                            // Search by tag
                            // If item in item frame is in the tag searched, add it to the list to glow
                            if (query.startsWith("#")) {
                                String tagSearch = query.substring(1);
                                Registry<Item> itemRegistry = world.registryAccess().lookupOrThrow(Registries.ITEM);

                                for (HolderSet.Named<Item> tag : itemRegistry.getTags().toList()) {
                                    ResourceLocation id = tag.key().location();

                                    if (matchCase ?
                                            id.getPath().toLowerCase().matches(tagSearch) || id.toString().toLowerCase().matches(tagSearch) :
                                            id.getPath().toLowerCase().contains(tagSearch) || id.toString().toLowerCase().contains(tagSearch)) {
                                        if (stack.is(tag.key())) {
                                            return true;
                                        }
                                    }
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
                    ((GlowCountdown)frame).startGlowCountdown(timer * 20);
                }
            }
            if (nearbyFrames.isEmpty()) {
                player.sendSystemMessage(matchCase ?
                        Component.translatable("qualityofqueso.item_frame_searcher.executed.found_none.match_case", searched, query) :
                        Component.translatable("qualityofqueso.item_frame_searcher.executed.found_none", searched, query), false);
                player.playNotifySound(SoundEvents.NOTE_BLOCK_BASS.get(), SoundSource.AMBIENT, 2.0F, 1.0F);
            } else if (clear) {
                player.sendSystemMessage(Component.translatable("qualityofqueso.item_frame_searcher.executed.cleared", searched), false);
                player.playNotifySound(SoundEvents.PLAYER_SPLASH, SoundSource.AMBIENT, 1.0F, 1.0F);
            } else {
                if (timer == 0) {
                    player.sendSystemMessage(matchCase ?
                            Component.translatable("qualityofqueso.item_frame_searcher.executed.without_timer.match_case", searched, query) :
                            Component.translatable("qualityofqueso.item_frame_searcher.executed.without_timer", searched, query));
                } else {
                    player.sendSystemMessage(matchCase ?
                            Component.translatable("qualityofqueso.item_frame_searcher.executed.with_timer.match_case", searched, query, timer) :
                            Component.translatable("qualityofqueso.item_frame_searcher.executed.with_timer", searched, query, timer), false);
                }
                player.playNotifySound(SoundEvents.ARROW_HIT_PLAYER, SoundSource.AMBIENT, 1.0F, 1.0F);
            }
        }
    }
}