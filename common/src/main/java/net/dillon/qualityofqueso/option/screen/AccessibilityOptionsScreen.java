package net.dillon.qualityofqueso.option.screen;

import com.google.common.collect.ImmutableList;
import net.dillon.qualityofqueso.option.ModListOptions;
import net.dillon.qualityofqueso.util.ButtonUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

import static net.dillon.qualityofqueso.util.ModUtil.uoptions;

/**
 * Advanced and technical options.
 */
public class AccessibilityOptionsScreen extends AbstractModOptionsScreen {
    private EditBox blacklistedServersField;
    private List<String> blacklistedServers = new ArrayList<>();

    public AccessibilityOptionsScreen(Screen parent) {
        super(parent, Component.translatable("qualityofqueso.gui.title.accessibility_options"));
    }

    @Override
    protected OptionInstance<?>[] options() {
        return new OptionInstance[]{
                ModListOptions.helpfulTooltips(),
                ModListOptions.preventEFromTyping(),

                ModListOptions.searchInventory(),
                ModListOptions.showButtonShortcuts(),

                ModListOptions.autoCloseRecipeBook(),
                ModListOptions.autoFocusIntoRecipeBook(),

                ModListOptions.onlyCountMatchingItems(),
                ModListOptions.displayTotalWithStacks(),

                ModListOptions.useOldSearchBarTexture(),
                ModListOptions.armorSlotOutlines(),

                ModListOptions.perpendicularQuickMoving(),
                ModListOptions.moveItemsIf(),

                ModListOptions.ignoreFabricTags(),
                ModListOptions.buttonClickSounds()
        };
    }

    @Override
    protected void init() {
        super.init();
        this.list.addSmall(this.options());

        // Initialize the list from current options
        this.blacklistedServers = new ArrayList<>(uoptions().main.blacklistedServers);

        // Create the position and text field
        this.blacklistedServersField = new EditBox(
                this.font,
                0, // x=irrelevant
                0, // y=irrelevant
                150,
                20,
                Component.empty()
        );

        this.blacklistedServersField.setMaxLength(Integer.MAX_VALUE);

        this.blacklistedServersField.setHint(Component.translatable("qualityofqueso.options.blacklisted_servers").withStyle(ChatFormatting.GRAY));
        // Set initial text from current blacklisted servers
        this.blacklistedServersField.setValue(String.join(", ", this.blacklistedServers));

        // Add change listener
        this.blacklistedServersField.setResponder(this::onTextChanged);

        List<AbstractWidget> widgets = ImmutableList.of(
                ModListOptions.elytraAlarmSoundDelay().createButton(this.options),
                ModListOptions.qoqButtons().createButton(this.options),

                ModListOptions.multiServerConfigs().createButton(this.options),
                this.blacklistedServersField
        );
        this.list.addSmall(widgets);
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

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float deltaTicks) {
        super.extractRenderState(graphics, mouseX, mouseY, deltaTicks);
        if (this.blacklistedServersField.isHovered()) {
            ButtonUtil.drawTooltip(Component.translatable("qualityofqueso.options.blacklisted_servers.tooltip"), graphics, this.font, mouseX, mouseY);
        }
    }

    @Override
    public void onClose() {
        uoptions().main.blacklistedServers.clear();
        uoptions().main.blacklistedServers.addAll(this.blacklistedServers);
        super.onClose();
    }

    protected boolean addOptionsByDefault() {
        return false;
    }
}