package net.dillon.qualityofqueso.mixin.client.accessor;

import net.minecraft.client.gui.screens.controls.KeyBindsList;
import net.minecraft.client.gui.screens.controls.KeyBindsScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(KeyBindsScreen.class)
public interface KeyBindsScreenAccessor {
    @Accessor("keyBindsList")
    KeyBindsList getKeyBindsList();
}