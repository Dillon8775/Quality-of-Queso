package net.dillon.qualityofqueso.screen.gui.button;

import net.dillon.qualityofqueso.util.HoverSize;
import net.minecraft.client.gui.Font;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.inventory.AbstractContainerMenu;

import static net.dillon.qualityofqueso.util.ModUtil.options;

/**
 * A button for toggling searching transportables.
 */
public class SearchTransportablesButton extends ToggleableButton {
    private final HoverSize hoverSize;

    public SearchTransportablesButton(HoverSize hoverSize, AbstractContainerMenu screenHandler, Font font, String searchFieldText, String buttonName, OnPress onPress) {
        super(screenHandler, font, searchFieldText, buttonName, onPress);
        this.hoverSize = hoverSize;
    }

    @Override
    protected String onTextureId() {
        return "transportable/including_transportables";
    }

    @Override
    protected String offTextureId() {
        return "transportable/excluding_transportables";
    }

    @Override
    protected boolean option() {
        return options().searching.searchTransportables;
    }

    @Override
    protected Component getTooltipToRender() {
        return Component.translatable(
                this.option()
                        ? "qualityofqueso.gui.include_transportables"
                        : "qualityofqueso.gui.exclude_transportables");
    }

    @Override
    public HoverSize getHoverSize() {
        return this.hoverSize;
    }

    @Override
    public void playDownSound(SoundManager manager) {
        manager.play(SimpleSoundInstance.forUI(
                options().searching.searchTransportables ? SoundEvents.SHULKER_BOX_CLOSE : SoundEvents.SHULKER_BOX_OPEN,
                1.0F
        ));
    }
}