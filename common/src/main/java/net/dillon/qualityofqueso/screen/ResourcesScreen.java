package net.dillon.qualityofqueso.screen;

import net.dillon.qualityofqueso.util.ModConstants;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ResourcesScreen extends AbstractModScreen {

    public ResourcesScreen(Screen parent) {
        super(parent, Component.translatable("qualityofqueso.gui.title.resources"));
    }

    @Override
    protected AbstractWidget[] options() {
        return new AbstractWidget[]{
                Button.builder(Component.translatable("qualityofqueso.gui.ask_questions"),
                        ConfirmLinkScreen.confirmLink(this, ModConstants.DISCORD_LINK, false)
                ).tooltip(
                        Tooltip.create(Component.translatable("qualityofqueso.gui.ask_questions.tooltip"))
                ).build(),
                Button.builder(Component.translatable("qualityofqueso.gui.report_bugs"),
                        ConfirmLinkScreen.confirmLink(this, "https://github.com/Dillon8775/Quality-of-Queso/issues", false)
                ).tooltip(
                        Tooltip.create(Component.translatable("qualityofqueso.gui.report_bugs.tooltip"))
                ).build(),

                Button.builder(Component.translatable("qualityofqueso.gui.showcase_video"),
                        ConfirmLinkScreen.confirmLink(this, ModConstants.SHOWCASE_VIDEO_LINK, false)
                ).tooltip(
                        Tooltip.create(Component.translatable("qualityofqueso.gui.showcase_video.tooltip"))
                ).build(),
                Button.builder(Component.translatable("qualityofqueso.gui.resource_pack_template"),
                        ConfirmLinkScreen.confirmLink(this, ModConstants.RESOURCE_PACK_TEMPLATE, false)
                ).tooltip(
                        Tooltip.create(Component.translatable("qualityofqueso.gui.resource_pack_template.tooltip"))
                ).build(),

                Button.builder(Component.translatable("qualityofqueso.gui.video_explanations"),
                        ConfirmLinkScreen.confirmLink(this, ModConstants.VIDEO_EXPLANATIONS_LINK, false)
                ).tooltip(
                        Tooltip.create(Component.translatable("qualityofqueso.gui.video_explanations.tooltip"))
                ).build(),
                Button.builder(Component.translatable("qualityofqueso.gui.other_qol_mods"),
                        ConfirmLinkScreen.confirmLink(this, ModConstants.OTHER_QOL_MODS_LINK, false)
                ).tooltip(
                        Tooltip.create(Component.translatable("qualityofqueso.gui.other_qol_mods.tooltip"))
                ).build()
        };
    }
}