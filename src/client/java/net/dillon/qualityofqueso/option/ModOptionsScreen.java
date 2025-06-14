package net.dillon.qualityofqueso.option;

import net.dillon.qualityofqueso.QualityOfQuesoClient;
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
public class ModOptionsScreen extends GameOptionsScreen {

    public ModOptionsScreen(Screen parent) {
        super(parent, MinecraftClient.getInstance().options, Text.translatable(QualityOfQuesoClient.TITLE));
    }

    private static SimpleOption<?>[] options() {
        return new SimpleOption<?>[]{
                ModListOptions.BETTER_GUI_EXIT,
                ModListOptions.BETTER_SEARCHING,
                ModListOptions.CHEST_SEARCH,
                ModListOptions.SEARCH_INVENTORY,
                ModListOptions.SAVE_SEARCH_TEXT,
                ModListOptions.INVENTORY_SORTING,
                ModListOptions.REQUIRE_ALT_TO_SORT,
                ModListOptions.QUICK_EQUIP,
                ModListOptions.SHOW_CONFIG_BUTTON
        };
    }

    @Override
    protected void init() {
        super.init();
        this.body.addAll(options());
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
        ModOptions.saveConfig();
        QualityOfQuesoClient.info("Flushed changes.");
        super.close();
    }

    /**
     * Required method.
     */
    @Override
    protected void addOptions() {
    }
}