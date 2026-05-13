package net.dillon.qualityofqueso.registry;

import net.dillon.qualityofqueso.sound.ModSoundEvents;
import net.dillon.qualityofqueso.util.ModConstants;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class ForgeSoundEvents {
    private static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(Registries.SOUND_EVENT, ModConstants.MOD_ID);
    private static final Supplier<SoundEvent> FORTNITE_BATTLE_PASS = SOUND_EVENTS.register(
            "fortnite_battle_pass",
            () -> SoundEvent.createVariableRangeEvent(ModSoundEvents.FORTNITE_BATTLE_PASS_ID)
    );

    private ForgeSoundEvents() {
    }

    public static void register(IEventBus modEventBus) {
        SOUND_EVENTS.register(modEventBus);
    }

    public static void bindCommonReferences() {
        ModSoundEvents.bindSounds(FORTNITE_BATTLE_PASS.get());
    }
}