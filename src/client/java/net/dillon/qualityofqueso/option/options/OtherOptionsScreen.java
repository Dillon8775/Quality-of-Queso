package net.dillon.qualityofqueso.option.options;

import net.dillon.qualityofqueso.main.QualityOfQueso;
import net.dillon.qualityofqueso.option.AbstractModOptionsScreen;
import net.dillon.qualityofqueso.option.ModListOptions;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.OptionListWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class OtherOptionsScreen extends AbstractModOptionsScreen {
    private TextFieldWidget blacklistedServersField;
    private List<String> blacklistedServers = new ArrayList<>();

    public OtherOptionsScreen(Screen parent) {
        super(parent, Text.translatable("qualityofqueso.gui.other_options"));
    }

    @Override
    protected SimpleOption<?>[] options() {
        return new SimpleOption[]{
                ModListOptions.BETTER_SEARCHING,
                ModListOptions.BETTER_GUI_EXIT,
                ModListOptions.QUICK_EQUIP,
                ModListOptions.SHOW_CONFIG_BUTTON
        };
    }

    @Override
    protected void init() {
        this.body = this.layout.addBody(new OptionListWidget(this.client, this.width, this));
        this.body.addSingleOptionEntry(ModListOptions.ENABLE_MOD);
        this.body.addAll(this.options());
        this.refreshWidgetPositions();

        // Initialize the list from current options
        this.blacklistedServers = new ArrayList<>(QualityOfQueso.options().blacklistedServers);

        // Create and position the text field
        this.blacklistedServersField = new TextFieldWidget(
                this.textRenderer,
                this.width / 2 - 150,
                70,
                100,
                20,
                null
        );

        this.blacklistedServersField.setPlaceholder(Text.translatable("qualityofqueso.options.blacklisted_servers"));
        // Set initial text from current blacklisted servers
        this.blacklistedServersField.setText(String.join(", ", this.blacklistedServers));

        // Add change listener
        this.blacklistedServersField.setChangedListener(this::onTextChanged);

        List<ClickableWidget> widgets = new ArrayList<>();
        widgets.add(this.blacklistedServersField);
        this.body.addAll(widgets);
    }

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
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        if (this.blacklistedServersField.isHovered()) {
            context.drawOrderedTooltip(this.textRenderer, this.textRenderer.wrapLines(Text.translatable("qualityofqueso.options.blacklisted_servers.tooltip"), 200), mouseX, mouseY);
        }
    }

    @Override
    public void close() {
        QualityOfQueso.options().blacklistedServers.clear();
        QualityOfQueso.options().blacklistedServers.addAll(this.blacklistedServers);
        super.close();
    }
}