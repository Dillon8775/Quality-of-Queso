package net.dillon.qualityofqueso.widget.management.toggleable;

import net.dillon.dillonlib.client.component.SoundBuilder;
import net.dillon.qualityofqueso.helper.ModConstants;
import net.dillon.qualityofqueso.widget.SearchBar;
import net.dillon.qualityofqueso.widget.management.ManagementParams;
import net.dillon.qualityofqueso.widget.management.ManagementSprites;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;

import static net.dillon.qualityofqueso.option.OptionInstances.client;
import static net.dillon.qualityofqueso.widget.management.ManagementSprites.managementIdentifier;

/**
 * A management button that works with the {@link SearchBar} to search for items inside transportable containers when toggled.
 */
public class SearchTransportablesButton extends ToggleableButton {

    public SearchTransportablesButton(ManagementParams params) {
        super(params, b -> ModConstants.SEARCHING_TRANSPORTABLES = !ModConstants.SEARCHING_TRANSPORTABLES);
    }

    @Override
    protected boolean toggle() {
        return ModConstants.SEARCHING_TRANSPORTABLES;
    }

    @Override
    protected ManagementSprites getManagementSprites() {
        return new ManagementSprites(
                managementIdentifier("transportable/including_transportables"),
                managementIdentifier("transportable/excluding_transportables")
        );
    }

    @Override
    public void playDownSound(SoundManager manager) {
        if (!client().management().playSounds) {
            return;
        }

        SoundBuilder.create(ModConstants.SEARCHING_TRANSPORTABLES ? SoundEvents.SHULKER_BOX_CLOSE : SoundEvents.SHULKER_BOX_OPEN)
                .pitch(1.5F)
                .buildAndPlay();
    }

    @Override
    protected Component getDefaultTooltip() {
        return Component.translatable(
                this.toggle()
                        ? "qualityofqueso.gui.include_transportables"
                        : "qualityofqueso.gui.exclude_transportables"
        );
    }
}