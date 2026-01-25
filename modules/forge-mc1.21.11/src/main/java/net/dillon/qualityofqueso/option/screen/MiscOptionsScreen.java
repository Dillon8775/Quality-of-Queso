package net.dillon.qualityofqueso.option.screen;

import com.google.common.collect.ImmutableList;
import net.dillon.qualityofqueso.option.ModListOptions;
import net.dillon.qualityofqueso.util.ButtonUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

import static net.dillon.qualityofqueso.main.QoQ.uoptions;

@OnlyIn(Dist.CLIENT)
public class MiscOptionsScreen extends AbstractModOptionsScreen {
    private EditBox blacklistedServersField;
    private List<String> blacklistedServers = new ArrayList<>();

    public MiscOptionsScreen(Screen parent) {
        super(parent, Component.translatable("qualityofqueso.gui.title.misc_options"));
    }

    @Override
    protected OptionInstance<?>[] options() {
        return new OptionInstance[]{
                ModListOptions.enableMod(),
                ModListOptions.fog(),

                ModListOptions.quickEquip(),
                ModListOptions.betterSearching(),

                ModListOptions.preventRageQuitting(),
                ModListOptions.betterGuiExit(),

                ModListOptions.preventEFromTyping(),
                ModListOptions.qoqButtons(),

                ModListOptions.mobHitDing(),
                ModListOptions.minMobHitDingDistance(),

                ModListOptions.helpfulTooltips(),
                ModListOptions.autoCloseRecipeBook(),
        };
    }

    protected OptionInstance<?>[] bottomOptions() {
        return new OptionInstance[]{
                ModListOptions.multiServerConfigs()
        };
    }

    @Override
    protected void init() {
        super.init();
        this.list.addSmall(this.options());

        // Initialize the list from current options
        this.blacklistedServers = new ArrayList<>(uoptions().blacklistedServers);

        // Create the position and text field
        this.blacklistedServersField = new EditBox(
                this.font,
                this.width / 2 - 150,
                70,
                310,
                20,
                Component.empty()
        );

        this.blacklistedServersField.setMaxLength(Integer.MAX_VALUE);

        this.blacklistedServersField.setHint(Component.translatable("qualityofqueso.options.blacklisted_servers").withStyle(ChatFormatting.GRAY));
        // Set initial text from current blacklisted servers
        this.blacklistedServersField.setValue(String.join(", ", this.blacklistedServers));

        // Add change listener
        this.blacklistedServersField.setResponder(this::onTextChanged);

        List<AbstractWidget> widgets = ImmutableList.of(this.blacklistedServersField);
        this.list.addSmall(widgets);
        this.list.addSmall(this.bottomOptions());
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
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float deltaTicks) {
        super.render(graphics, mouseX, mouseY, deltaTicks);
        if (this.blacklistedServersField.isHovered()) {
            ButtonUtil.drawTooltip(Component.translatable("qualityofqueso.options.blacklisted_servers.tooltip"), graphics, this.font, mouseX, mouseY);
        }
    }

    @Override
    public void onClose() {
        uoptions().blacklistedServers.clear();
        uoptions().blacklistedServers.addAll(this.blacklistedServers);
        super.onClose();
    }

    protected boolean addOptionsByDefault() {
        return false;
    }
}