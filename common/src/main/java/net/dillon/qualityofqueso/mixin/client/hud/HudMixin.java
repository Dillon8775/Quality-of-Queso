package net.dillon.qualityofqueso.mixin.client.hud;

import net.dillon.qualityofqueso.helper.EnderChestHelper;
import net.dillon.qualityofqueso.hud.*;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.dillon.qualityofqueso.helper.ModHelper.modEnabled;
import static net.dillon.qualityofqueso.hud.ModHudElement.getHighlightedSlotTexture;

@Mixin(Hud.class)
public class HudMixin {
    @Shadow @Final
    private Minecraft minecraft;
    @Unique
    private final ArmorStatusHudElement armorStatusHudElement = new ArmorStatusHudElement();
    @Unique
    private final VisualClockHudElement visualClockHudElement = new VisualClockHudElement();
    @Unique
    private final ItemCounterHudElement itemCounterHudElement = new ItemCounterHudElement();
    @Unique
    private final LowItemHealthHudElement lowItemHealthHudElement = new LowItemHealthHudElement();
    @Unique
    private final LockedHotbarSlotsHudElement lockedHotbarSlotsHudElement = new LockedHotbarSlotsHudElement();

    /**
     * Extracts the {@link ArmorStatusHudElement} and {@link VisualClockHudElement}.
     */
    @Inject(method = "extractItemHotbar", at = @At("HEAD"))
    private void extractAtHead(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (!modEnabled()) {
            return;
        }

        this.armorStatusHudElement.extractRenderState(graphics);
        this.itemCounterHudElement.extractRenderState(graphics);
        this.visualClockHudElement.extractRenderState(graphics);

        EnderChestHelper.persistEnderChestContentsIfOpen(minecraft.player);
    }

    /**
     * Extracts the {@link LockedHotbarSlotsHudElement} and {@link LowItemHealthHudElement}.
     */
    @Inject(method = "extractItemHotbar", at = @At("TAIL"))
    private void extractAtTail(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (!modEnabled()) {
            return;
        }

        this.lockedHotbarSlotsHudElement.extractRenderState(graphics);
        this.lowItemHealthHudElement.extractRenderState(graphics);
    }

    /**
     * Redirects the {@code highlighted slot} to use the colored highlighting of slots.
     */
    @ModifyArg(method = "extractItemHotbar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blitSprite(Lcom/mojang/renderpearl/api/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V", ordinal = 1), index = 1)
    private Identifier modifyHighlightedSlot(Identifier original) {
        if (!modEnabled() || minecraft.player == null) {
            return original;
        }

        return getHighlightedSlotTexture(ModHudElement.HOTBAR_SELECTION_SPRITE, minecraft.player.getInventory().getItem(minecraft.player.getInventory().getSelectedSlot()), null);
    }
}