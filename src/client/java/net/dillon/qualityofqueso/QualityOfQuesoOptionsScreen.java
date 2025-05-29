package net.dillon.qualityofqueso;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;

public class QualityOfQuesoOptionsScreen extends GameOptionsScreen {

    public QualityOfQuesoOptionsScreen(Screen parent) {
        super(parent, MinecraftClient.getInstance().options, Text.translatable("qualityofqueso.gui.options.title"));
    }

    private SimpleOption<?>[] options() {
        return new SimpleOption[]{
        };
    }

    @Override
    protected void init() {
        super.init();
        this.body.addSingleOptionEntry(QuesoListOptions.CLOSE_GUI_MENU_BY_CLICKING_OFF);
        this.body.addSingleOptionEntry(QuesoListOptions.TYPE_ANYWHERE_TO_SEARCH);
//      this.body.addAll(options());
    }

    @Override
    public void close() {
        QuesoOptions.saveConfig();
        QualityOfQueso.LOGGER.info("Flushed changes.");
        super.close();
    }

    @Override
    protected void addOptions() {
    }
}