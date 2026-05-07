package net.dillon.qualityofqueso.mixin.client.util;

import net.dillon.qualityofqueso.helper.ModHelper;
import net.dillon.qualityofqueso.instance.management.ClickSlotInstance;
import net.dillon.qualityofqueso.keybind.ModKeyMappings;
import net.dillon.qualityofqueso.screen.ItemFrameSearchScreen;
import net.dillon.qualityofqueso.sound.ModSoundEvents;
import net.dillon.qualityofqueso.util.MobHitDingTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

import static net.dillon.qualityofqueso.helper.ModHelper.*;
import static net.dillon.qualityofqueso.keybind.ModKeyMappings.OPEN_SEARCH_ITEM_FRAMES_GUI;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    @Shadow
    @Final
    public Options options;

    @Shadow
    public abstract SoundManager getSoundManager();

    /**
     * Makes the {@link ModKeyMappings#OPEN_SEARCH_ITEM_FRAMES_GUI} open {@link ItemFrameSearchScreen}.
     */
    @Inject(method = "handleKeybinds", at = @At("TAIL"))
    private void handleKeyPressing(CallbackInfo ci) {
        if (modEnabled(Minecraft.getInstance()) && coptions().itemFrameSearching) {
            while (OPEN_SEARCH_ITEM_FRAMES_GUI.isActiveAndDown()) {
                Minecraft.getInstance().setScreen(new ItemFrameSearchScreen(null));
            }
        }
    }

    /**
     * Ticks cooldown and other utilities.
     */
    @Inject(method = "tick", at = @At("TAIL"))
    private void tickModEvents(CallbackInfo ci) {
        if (!modEnabled(Minecraft.getInstance())) {
            return;
        }

        ModHelper.tickCooldowns();

        ClickSlotInstance.tickTradeAllTask();
        ClickSlotInstance.tickCraftAllTask();
        MobHitDingTracker.tick(Minecraft.getInstance());

        if (!options().misc.fortniteBattlePass) {
            return;
        }

        Random random = new Random();
        if (this.getSoundManager() != null && random.nextFloat() < 0.01F) {
            this.getSoundManager().play(SimpleSoundInstance.forUI(ModSoundEvents.FORTNITE_BATTLE_PASS, 1.0F, 5.0F));
        }
    }
}
