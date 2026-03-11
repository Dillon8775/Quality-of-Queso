package net.dillon.qualityofqueso.screen.gui.button;

import net.dillon.qualityofqueso.util.HoverSize;
import net.minecraft.client.gui.Font;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.inventory.AbstractContainerMenu;

import static net.dillon.qualityofqueso.util.ModUtil.options;

/**
 * A button for toggling searching transportables.
 */
public class SearchTransportablesButton extends ToggleableButton {


    public SearchTransportablesButton(AbstractContainerMenu screenHandler, Font font, String searchFieldText, int x, int y, String buttonName, OnPress onPress) {
        super(screenHandler, font, searchFieldText, x, y, buttonName, onPress,
                "transportable/include_transportables_button",
                "transportable/exclude_transportables_button",
                new String[]{"qualityofqueso.gui.transportable/include_transportables", "qualityofqueso.gui.transportable/exclude_transportables"}
        );
    }

    @Override
    boolean option() {
        return options().searching.searchTransportables;
    }

    @Override
    public HoverSize getHoverSize() {
        return HoverSize.BIG;
    }

    @Override
    public void playDownSound(SoundManager manager) {
        manager.play(SimpleSoundInstance.forUI(
                options().searching.searchTransportables ? SoundEvents.SHULKER_BOX_CLOSE : SoundEvents.SHULKER_BOX_OPEN,
                1.0F
        ));
    }
}