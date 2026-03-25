package net.dillon.qualityofqueso.option.screen;

import net.dillon.qualityofqueso.option.ModListOptions;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class HudOptionsScreen extends AbstractModOptionsScreen {

    public HudOptionsScreen(Screen parent) {
        super(parent, Component.translatable("qualityofqueso.gui.title.hud_options"));
    }

    @Override
    protected OptionInstance<?>[] options() {
        return new OptionInstance[]{
                ModListOptions.armorStatus(),
                ModListOptions.armorHotbar()
        };
    }

    protected OptionInstance<?>[] bottomOptions() {
        return new OptionInstance[]{
                ModListOptions.warningIndicators()
        };
    }

    @Override
    protected void init() {
        super.init();
        this.list.addSmall(this.options());
        List<AbstractWidget> options = new ArrayList<>(List.of(
                Button.builder(Component.translatable("qualityofqueso.gui.item_count_options"), button -> {
                    this.minecraft.setScreen(new ItemCountOptionsScreen(this));
                }).tooltip(Tooltip.create(Component.translatable("qualityofqueso.gui.item_count_options.tooltip"))).build(),
                ModListOptions.coloredHighlighting().createButton(this.options)
        ));
        this.list.addSmall(options);
        this.list.addSmall(this.bottomOptions());
    }

    @Override
    protected boolean addOptionsByDefault() {
        return false;
    }
}