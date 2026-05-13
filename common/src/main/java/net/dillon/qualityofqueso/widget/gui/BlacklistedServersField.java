package net.dillon.qualityofqueso.widget.gui;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

import static net.dillon.qualityofqueso.helper.ModHelper.uoptions;

/**
 * A search field, for handling blacklisted servers.
 */
@Deprecated
public class BlacklistedServersField extends EditBox {
    private final List<String> blacklistedServers = new ArrayList<>(uoptions().main.blacklistedServers);

    public BlacklistedServersField(Font font) {
        super(font, 0, 0, 150, 20, Component.empty());
        this.setMaxLength(Integer.MAX_VALUE);
        this.setHint(Component.translatable("qualityofqueso.options.blacklisted_servers").withStyle(ChatFormatting.GRAY));
        this.setValue(String.join(", ", this.blacklistedServers));
        this.setResponder(this::onTextChanged);
    }

    /**
     * @return the current list for blacklisted servers.
     */
    public List<String> getBlacklistedServers() {
        return this.blacklistedServers;
    }

    /**
     * Clears and writes the new blacklisted servers to the {@code blacklisted servers option.}
     */
    private void onTextChanged(String newText) {
        // Clear the current list
        this.blacklistedServers.clear();

        // Split by comma and trim each entry
        String[] servers = newText.split(",");
        for (String server : servers) {
            String trimmed = server.trim();
            if (!trimmed.isEmpty()) {
                this.blacklistedServers.add(trimmed);
            }
        }
    }
}