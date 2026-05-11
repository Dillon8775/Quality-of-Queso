package net.dillon.qualityofqueso.mixin.client.accessor;

import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.debug.DebugOptionsScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(DebugOptionsScreen.class)
public interface DebugOptionsScreenAccessor {
    @Accessor("searchBox")
    EditBox getSearchBox();
}