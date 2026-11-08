package net.dillon.qualityofqueso.widget.management;

import net.minecraft.resources.Identifier;

import static net.dillon.qualityofqueso.helper.ModHelper.qoqIdentifier;

/**
 * Stores sprites for management buttons.
 */
public record ManagementSprites(Identifier enabled, Identifier disabled) {

    /**
     * @return the sprite based on if the button is enabled/disabled.
     */
    public Identifier get(boolean enabled) {
        return enabled ? this.enabled : this.disabled;
    }

    /**
     * @return the button directory as an identifier.
     */
    public static Identifier managementIdentifier(String id) {
        return qoqIdentifier("textures/gui/sprites/button/" + id + ".png");
    }
}