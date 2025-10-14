package net.dillon.qualityofqueso.option;

import net.dillon.qualityofqueso.util.ModUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

@Environment(EnvType.CLIENT)
public abstract class AbstractModOptionsScreen extends GameOptionsScreen {

    public AbstractModOptionsScreen(Screen parent, Text title) {
        super(parent, MinecraftClient.getInstance().options, title);
    }

    /**
     * The list of {@link SimpleOption}s that should be added to the screen.
     */
    protected abstract SimpleOption<?>[] options();

    @Override
    protected void init() {
        super.init();
        if (this.addOptionsByDefault()) {
            this.body.addAll(this.options());
        }
    }

    @Override
    protected void initFooter() {
        DirectionalLayoutWidget directionalLayoutWidget = this.layout.addFooter(DirectionalLayoutWidget.vertical()).spacing(8);
        directionalLayoutWidget.getMainPositioner().alignHorizontalCenter();
        DirectionalLayoutWidget directionalLayoutWidget2 = directionalLayoutWidget.add(DirectionalLayoutWidget.horizontal().spacing(8));
        directionalLayoutWidget2.add(
                ButtonWidget.builder(Text.translatable("qualityofqueso.gui.learn_more"), button -> {
                    String link = "https://modrinth.com/mod/quality-of-queso";
                    this.client.setScreen(new ConfirmLinkScreen(openInBrowser -> {
                        if (openInBrowser) {
                            Util.getOperatingSystem().open(link);
                        }
                        this.client.setScreen(this);
                    }, link, true));
                }).build()
        );
        directionalLayoutWidget2.add(ButtonWidget.builder(ScreenTexts.DONE, button -> this.close()).build());
    }

    /**
     * Closes the options screen and saves changes.
     */
    @Override
    public void close() {
        ModOptions.OPTIONS.save();
        ModUtil.info("Saved changes.");
        super.close();
    }

    /**
     * @return if all options in the {@link AbstractModOptionsScreen#options()} method should be added by default when calling {@code super.init().}
     */
    protected boolean addOptionsByDefault() {
        return true;
    }

    /**
     * Required method.
     */
    @Override
    protected void addOptions() {
    }
}