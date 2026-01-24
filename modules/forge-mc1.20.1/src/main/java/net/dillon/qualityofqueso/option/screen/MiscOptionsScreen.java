package net.dillon.qualityofqueso.option.screen;

import net.dillon.qualityofqueso.option.ModListOptions;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MiscOptionsScreen extends AbstractModOptionsScreen {

    public MiscOptionsScreen(Screen parent) {
        super(parent, Component.translatable("qualityofqueso.gui.title.misc_options"));
    }

    @Override
    protected OptionInstance<?>[] options() {
        return new OptionInstance[]{
                ModListOptions.enableMod(),
                ModListOptions.fog(),

                ModListOptions.armorStatus(),
                ModListOptions.itemCount(),

                ModListOptions.quickEquip(),
                ModListOptions.betterSearching(),

                ModListOptions.preventRageQuitting(),
                ModListOptions.betterGuiExit(),

                ModListOptions.preventEFromTyping(),
                ModListOptions.qoqButtons(),

                ModListOptions.showInGameTime(),
                ModListOptions.showInGameTime(),

                ModListOptions.mobHitDing(),
                ModListOptions.minMobHitDingDistance(),

                ModListOptions.helpfulTooltips(),
                ModListOptions.multiServerConfigs(),
        };
    }
}