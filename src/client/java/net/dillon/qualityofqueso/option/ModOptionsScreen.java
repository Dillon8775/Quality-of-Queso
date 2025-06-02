package net.dillon.qualityofqueso.option;

import net.dillon.qualityofqueso.QualityOfQueso;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;

public class ModOptionsScreen extends GameOptionsScreen {

    public ModOptionsScreen(Screen parent) {
        super(parent, MinecraftClient.getInstance().options, Text.translatable("qualityofqueso.gui.options.title"));
    }

    @Override
    protected void init() {
        super.init();
        this.body.addSingleOptionEntry(ModListOptions.CLOSE_GUI_MENU_BY_CLICKING_OFF);
        this.body.addSingleOptionEntry(ModListOptions.TYPE_ANYWHERE_TO_SEARCH);
    }

    @Override
    public void close() {
        ModOptions.saveConfig();
        QualityOfQueso.info("Flushed changes.");
        super.close();
    }

    @Override
    protected void addOptions() {
    }
}