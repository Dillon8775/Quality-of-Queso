package net.dillon.qualityofqueso.option.eum.effects;

import com.mojang.serialization.Codec;
import net.dillon.qualityofqueso.option.eum.OptionEnum;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;

public enum PotionEffects implements StringRepresentable, OptionEnum {
    DEFAULT("default", "qualityofqueso.options.fov_effects.potion_effects.default"),
    NON_BEACON("non_beacon", "qualityofqueso.options.fov_effects.potion_effects.non_beacon"),
    OFF("off", "qualityofqueso.options.fov_effects.potion_effects.off");

    public static final Codec<PotionEffects> CODEC = StringRepresentable.fromEnum(PotionEffects::values);
    
    private final String name;
    private final Component translationKey;

    PotionEffects(final String name, final String translationKey) {
        
        this.name = name;
        this.translationKey = Component.translatable(translationKey);
    }

    public boolean enabled() {
        return this != OFF;
    }

    public boolean nonBeacon() {
        return this == NON_BEACON;
    }

    public String getSerializedName() {
        return this.name;
    }

    @Override
    public Component getTranslationKey() {
        return this.translationKey;
    }
}