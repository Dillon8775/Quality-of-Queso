package net.dillon.qualityofqueso.screen;

import net.dillon.qualityofqueso.util.ModConstants;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ResourcesScreen extends AbstractModScreen {

    public ResourcesScreen(Screen parent) {
        super(parent, Component.translatable("qualityofqueso.gui.title.resources"));
    }

    @Override
    protected void init() {
        super.init();
        AbstractWidget askQuestions = Button.builder(Component.translatable("qualityofqueso.gui.ask_questions"),
                ConfirmLinkScreen.confirmLink(ModConstants.DISCORD_LINK, this, false)
        ).tooltip(
                Tooltip.create(Component.translatable("qualityofqueso.gui.ask_questions.tooltip"))
        ).build();

        AbstractWidget reportBugs = Button.builder(Component.translatable("qualityofqueso.gui.report_bugs"),
                ConfirmLinkScreen.confirmLink("https://github.com/Dillon8775/Quality-of-Queso/issues", this, false)
        ).tooltip(
                Tooltip.create(Component.translatable("qualityofqueso.gui.report_bugs.tooltip"))
        ).build();

        AbstractWidget showcaseVideo = Button.builder(Component.translatable("qualityofqueso.gui.showcase_video"),
                ConfirmLinkScreen.confirmLink(ModConstants.SHOWCASE_VIDEO_LINK, this, false)
        ).tooltip(
                Tooltip.create(Component.translatable("qualityofqueso.gui.showcase_video.tooltip"))
        ).build();

        AbstractWidget resourcePackTemplate = Button.builder(Component.translatable("qualityofqueso.gui.resource_pack_template"),
                ConfirmLinkScreen.confirmLink(ModConstants.RESOURCE_PACK_TEMPLATE, this, false)
        ).tooltip(
                Tooltip.create(Component.translatable("qualityofqueso.gui.resource_pack_template.tooltip"))
        ).build();

        AbstractWidget videoExplanations = Button.builder(Component.translatable("qualityofqueso.gui.video_explanations"),
                ConfirmLinkScreen.confirmLink(ModConstants.VIDEO_EXPLANATIONS_LINK, this, false)
        ).tooltip(
                Tooltip.create(Component.translatable("qualityofqueso.gui.video_explanations.tooltip"))
        ).build();

        AbstractWidget otherQoLMods = Button.builder(Component.translatable("qualityofqueso.gui.other_qol_mods"),
                ConfirmLinkScreen.confirmLink(ModConstants.OTHER_QOL_MODS_LINK, this, false)
        ).tooltip(
                Tooltip.create(Component.translatable("qualityofqueso.gui.other_qol_mods.tooltip"))
        ).build();

        GridLayout gridlayout = new GridLayout();
        gridlayout.defaultCellSetting().paddingHorizontal(5).paddingBottom(4).alignHorizontallyCenter();
        GridLayout.RowHelper rowHelper = gridlayout.createRowHelper(2);

        rowHelper.addChild(askQuestions);
        rowHelper.addChild(reportBugs);
        rowHelper.addChild(showcaseVideo);
        rowHelper.addChild(resourcePackTemplate);
        rowHelper.addChild(videoExplanations);
        rowHelper.addChild(otherQoLMods);

        gridlayout.arrangeElements();
        FrameLayout.alignInRectangle(gridlayout, 0, this.height / 6 - 12, this.width, this.height, 0.5F, 0.0F);
        gridlayout.visitWidgets(this::addRenderableWidget);
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.lastScreen);
    }

    @Override
    protected OptionInstance<?>[] options() {
        return new OptionInstance[]{
        };
    }

    @Override
    protected boolean addList() {
        return false;
    }
}