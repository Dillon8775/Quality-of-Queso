package net.dillon.qualityofqueso.option.screen;

import com.google.common.collect.ImmutableList;
import net.dillon.qualityofqueso.option.ModListOptions;
import net.dillon.qualityofqueso.util.ButtonUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;

import static net.dillon.qualityofqueso.main.QoQ.uoptions;

public class MiscOptionsScreen extends AbstractModOptionsScreen {
    private TextFieldWidget blacklistedServersField;
    private List<String> blacklistedServers = new ArrayList<>();

    public MiscOptionsScreen(Screen parent) {
        super(parent, Text.translatable("qualityofqueso.gui.title.misc_options"));
    }

    @Override
    protected SimpleOption<?>[] options() {
        return new SimpleOption[]{
                ModListOptions.enableMod(),
                ModListOptions.fog(),

                ModListOptions.quickGuiExit(),
                ModListOptions.quickSearch(),

                ModListOptions.preventRageQuitting(),
                ModListOptions.alwaysPreventRageQuitting(),

                ModListOptions.preventEFromTyping(),
                ModListOptions.quickEquip(),

                ModListOptions.mobHitDing(),
                ModListOptions.minMobHitDingDistance(),

                ModListOptions.autoCloseRecipeBook(),
                ModListOptions.helpfulTooltips(),
        };
    }

    protected SimpleOption<?>[] bottomOptions() {
        return new SimpleOption[]{
                ModListOptions.qoqButtons(),
                ModListOptions.multiServerConfigs()
        };
    }

    @Override
    protected void init() {
        super.init();
        this.body.addAll(this.options());

        // Initialize the list from current options
        this.blacklistedServers = new ArrayList<>(uoptions().blacklistedServers);

        // Create and position the text field
        this.blacklistedServersField = new TextFieldWidget(
                this.textRenderer,
                this.width / 2 - 150,
                70,
                310,
                20,
                null
        );

        this.blacklistedServersField.setMaxLength(Integer.MAX_VALUE);

        this.blacklistedServersField.setPlaceholder(Text.translatable("qualityofqueso.options.blacklisted_servers").formatted(Formatting.GRAY));
        // Set initial text from current blacklisted servers
        this.blacklistedServersField.setText(String.join(", ", this.blacklistedServers));

        // Add change listener
        this.blacklistedServersField.setChangedListener(this::onTextChanged);

        List<ClickableWidget> widgets = ImmutableList.of(this.blacklistedServersField);
        this.body.addAll(widgets);
        this.body.addAll(this.bottomOptions());
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
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        if (this.blacklistedServersField.isHovered()) {
            ButtonUtil.drawTooltip(Text.translatable("qualityofqueso.options.blacklisted_servers.tooltip"), context, this.textRenderer, mouseX, mouseY);
        }
    }

    @Override
    public void close() {
        uoptions().blacklistedServers.clear();
        uoptions().blacklistedServers.addAll(this.blacklistedServers);
        super.close();
    }

    protected boolean addOptionsByDefault() {
        return false;
    }
}