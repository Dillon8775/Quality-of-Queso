package net.dillon.qualityofqueso.option;

import net.dillon.qualityofqueso.QualityOfQuesoClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class ModOptionsScreen extends GameOptionsScreen {

    public ModOptionsScreen(Screen parent) {
        super(parent, MinecraftClient.getInstance().options, Text.translatable("qualityofqueso.gui.options.title"));
    }

    /**
     * All {@code Quality of Queso options.}
     */
    private static SimpleOption<?>[] options() {
        return new SimpleOption<?>[]{
                ModListOptions.BETTER_GUI_EXIT,
                ModListOptions.BETTER_SEARCHING,
                ModListOptions.SHOW_CONFIG_BUTTON
        };
    }

    /**
     * Add all options.
     */
    @Override
    protected void init() {
        super.init();
        this.body.addAll(options());
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