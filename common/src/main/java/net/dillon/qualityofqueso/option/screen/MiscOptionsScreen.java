package net.dillon.qualityofqueso.option.screen;

import com.google.common.collect.ImmutableList;
import net.dillon.qualityofqueso.option.ModListOptions;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;

public class MiscOptionsScreen extends AbstractModOptionsScreen {
    private AbstractWidget fogButton;

    public MiscOptionsScreen(Screen parent) {
        super(parent, Component.translatable("qualityofqueso.gui.title.misc_options"));
    }

    @Override
    protected OptionInstance<?>[] options() {
        return new OptionInstance[]{
                ModListOptions.elytraAlarm(),
                ModListOptions.minElytraFallDistance(),

                ModListOptions.mobHitDing(),
                ModListOptions.minMobHitDingDistance(),

                ModListOptions.quickEquip(),
                ModListOptions.enchantingHelper(),
        };
    }

    @Override
    protected void init() {
        super.init();
        this.list.addSmall(this.options());
        this.fogButton = Button.builder(Component.translatable("qualityofqueso.gui.fog_options"), button -> {
            this.minecraft.setScreen(new FogOptionsScreen(this));
        }).tooltip(Tooltip.create(Component.translatable("qualityofqueso.gui.fog_options.tooltip"))).build();
        List<AbstractWidget> widgets = ImmutableList.of(
                ModListOptions.quickGuiExit().createButton(this.options),
                this.fogButton,

                ModListOptions.preventRageQuitting().createButton(this.options),
                ModListOptions.alwaysPreventRageQuitting().createButton(this.options),

                ModListOptions.fortniteBattlePass().createButton(this.options)
        );
        this.list.addSmall(widgets);
    }

    protected boolean addOptionsByDefault() {
        return false;
    }
}