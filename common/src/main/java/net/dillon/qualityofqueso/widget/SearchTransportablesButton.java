package net.dillon.qualityofqueso.widget;

import net.dillon.qualityofqueso.util.ModConstants;
import net.minecraft.client.gui.Font;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.inventory.AbstractContainerMenu;

import static net.dillon.qualityofqueso.helper.ModHelper.clientOptionsInstance;

/**
 * A button for toggling searching transportables.
 */
public class SearchTransportablesButton extends ToggleableButton {

    public SearchTransportablesButton(AbstractContainerMenu screenHandler, Font font, String searchFieldText, String buttonName, OnPress onPress) {
        super(screenHandler, font, searchFieldText, buttonName, onPress);
    }

    @Override
    public void playDownSound(SoundManager manager) {
        if (!clientOptionsInstance().getManagementOptions().playSounds) {
            return;
        }

        manager.play(SimpleSoundInstance.forUI(
                ModConstants.SEARCHING_TRANSPORTABLES ? SoundEvents.SHULKER_BOX_CLOSE : SoundEvents.SHULKER_BOX_OPEN,
                1.0F
        ));
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
        return ModConstants.SEARCHING_TRANSPORTABLES;
    }

    @Override
    protected Component getTooltipToRender() {
        return Component.translatable(
                this.option()
                        ? "qualityofqueso.gui.include_transportables"
                        : "qualityofqueso.gui.exclude_transportables");
    }
}