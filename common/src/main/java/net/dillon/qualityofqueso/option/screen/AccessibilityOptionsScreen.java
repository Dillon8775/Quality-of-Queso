package net.dillon.qualityofqueso.option.screen;

import com.google.common.collect.ImmutableList;
import net.dillon.qualityofqueso.option.ModListOptions;
import net.dillon.qualityofqueso.option.instance.ModClientOptions;
import net.dillon.qualityofqueso.screen.gui.SearchField;
import net.dillon.qualityofqueso.util.ButtonUtil;
import net.dillon.qualityofqueso.util.ModUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.CommonColors;

import java.util.ArrayList;
import java.util.List;

import static net.dillon.qualityofqueso.util.ModUtil.uoptions;

/**
 * Advanced and technical options.
 */
public class AccessibilityOptionsScreen extends AbstractModOptionsScreen {
    private EditBox blacklistedServersField;
    private EditBox searchBarTextColorField;
    private static boolean SAFE_TO_SAVE_COLOR;
    private static final Component DEFAULT_COLOR_FIELD_TOOLTIP = Component.translatable("qualityofqueso.options.search_bar_text_color.tooltip");
    private static Component COLOR_FIELD_TOOLTIP = DEFAULT_COLOR_FIELD_TOOLTIP;
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
                ModListOptions.ignoreFabricTags(),
        };
    }

    @Override
    protected void init() {
        super.init();
        this.list.addSmall(this.options());

        this.searchBarTextColorField = new EditBox(
                this.font,
                0,
                0,
                150,
                20,
                Component.empty()
        ) {
            @Override
            public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
                if (event.button() == 1 && this.isHovered()) {
                    this.setValue(String.format("#%06X", ModClientOptions.DEFAULT_TEXT_COLOR & 0xFFFFFF));
                    this.setFocused(false);
                    return true;
                }
                return super.mouseClicked(event, doubleClick);
            }
        };
        this.searchBarTextColorField.setMaxLength(7);
        this.searchBarTextColorField.setValue(String.format("#%06X", ModUtil.options().accessibility.searchBarTextColor & 0xFFFFFF));
        this.searchBarTextColorField.setTextColor(CommonColors.GREEN);
        this.searchBarTextColorField.setResponder(this::onColorChanged);

        this.blacklistedServers = new ArrayList<>(uoptions().main.blacklistedServers);
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
        this.blacklistedServersField.setValue(String.join(", ", this.blacklistedServers));
        this.blacklistedServersField.setResponder(this::onTextChanged);

        List<AbstractWidget> widgets = ImmutableList.of(
                ModListOptions.useOldSearchBarTexture().createButton(this.options),
                this.searchBarTextColorField,

                ModListOptions.perpendicularQuickMoving().createButton(this.options),
                ModListOptions.elytraAlarmSoundDelay().createButton(this.options),

                ModListOptions.menuButton().createButton(this.options),
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

    /**
     * Attempts to change the color of the search bar text field.
     */
    private void onColorChanged(String newColor) {
        try {
            SearchField.parseTextColor(newColor);
            this.searchBarTextColorField.setTextColor(CommonColors.GREEN);
            SAFE_TO_SAVE_COLOR = true;
            COLOR_FIELD_TOOLTIP = DEFAULT_COLOR_FIELD_TOOLTIP;
        } catch (IllegalArgumentException | StringIndexOutOfBoundsException e) {
            this.searchBarTextColorField.setTextColor(CommonColors.RED);
            SAFE_TO_SAVE_COLOR = false;
            COLOR_FIELD_TOOLTIP = Component.translatable("qualityofqueso.options.search_bar_text_color.tooltip.error").withStyle(ChatFormatting.RED);
        }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float deltaTicks) {
        super.extractRenderState(graphics, mouseX, mouseY, deltaTicks);
        if (this.blacklistedServersField.isHovered()) {
            ButtonUtil.drawTooltip(Component.translatable("qualityofqueso.options.blacklisted_servers.tooltip"), graphics, this.font, mouseX, mouseY);
        }
        if (this.searchBarTextColorField.isHovered()) {
            ButtonUtil.drawTooltip(COLOR_FIELD_TOOLTIP, graphics, this.font, mouseX, mouseY);
        }
    }

    @Override
    public void onClose() {
        uoptions().main.blacklistedServers.clear();
        uoptions().main.blacklistedServers.addAll(this.blacklistedServers);
        ModUtil.options().accessibility.searchBarTextColor = SAFE_TO_SAVE_COLOR
                ? SearchField.getTextColor(this.searchBarTextColorField.getValue())
                : ModClientOptions.DEFAULT_TEXT_COLOR;
        super.onClose();
    }

    protected boolean addOptionsByDefault() {
        return false;
    }
}
