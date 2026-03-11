package net.dillon.qualityofqueso.mixin.client.util;

import net.dillon.qualityofqueso.keybind.ModKeybinds;
import net.dillon.qualityofqueso.screen.ItemFrameSearchScreen;
import net.dillon.qualityofqueso.sound.ModSoundEvents;
import net.dillon.qualityofqueso.util.ContainerTracker;
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

import static net.dillon.qualityofqueso.keybind.ModKeybinds.OPEN_SEARCH_ITEM_FRAMES_GUI;
import static net.dillon.qualityofqueso.util.ModUtil.*;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    @Shadow
    @Final
    public Options options;
    @Shadow
    public abstract SoundManager getSoundManager();

    /**
     * You don't want to know...
     */
    @Inject(method = "tick", at = @At("TAIL"))
    private void fortniteBattlePass(CallbackInfo ci) {
        if (!modEnabled(Minecraft.getInstance())) {
            return;
        }

        if (ContainerTracker.COOLDOWN > 0) {
            ContainerTracker.COOLDOWN--;
        }

        if (!options().misc.fortniteBattlePass) {
            return;
        }

        Random random = new Random();
        if (this.getSoundManager() != null && random.nextFloat() < 0.01F) {
            this.getSoundManager().play(SimpleSoundInstance.forUI(ModSoundEvents.FORTNITE_BATTLE_PASS, 1.0F, 5.0F));
        }
    }

    /**
     * Makes the {@link ModKeybinds#OPEN_SEARCH_ITEM_FRAMES_GUI} open {@link ItemFrameSearchScreen}.
     */
    @Inject(method = "handleKeybinds", at = @At("TAIL"))
    private void handleKeyPressing(CallbackInfo ci) {
        if (modEnabled(Minecraft.getInstance()) && coptions().itemFrameSearching) {
            while (OPEN_SEARCH_ITEM_FRAMES_GUI.consumeClick()) {
                Minecraft.getInstance().setScreen(new ItemFrameSearchScreen(null));
            }
        }
    }
}