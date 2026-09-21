package net.dillon.qualityofqueso.screen;

import net.dillon.dillonlib.util.Links;
import net.dillon.qualityofqueso.helper.ModConstants;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import static net.dillon.dillonlib.task.ClientTasks.openLink;

public class ResourcesScreen extends AbstractModScreen {

    public ResourcesScreen(Screen parent) {
        super(parent, Component.translatable("qualityofqueso.title.resources"));
    }

    @Override
    public void widgets() {
        this.createHeader(
                Component.translatable("qualityofqueso.header.mod_resources"),
                this.createWidget(
                        Button.builder(Component.translatable("qualityofqueso.menu.showcase_video"),
                                (button) -> openLink(this, ModConstants.SHOWCASE_VIDEO_LINK, false)
                        ).tooltip(
                                Tooltip.create(Component.translatable("qualityofqueso.menu.showcase_video.tooltip"))
                        ).build()
                ),

                this.createWidget(
                        Button.builder(Component.translatable("qualityofqueso.menu.resource_pack_template"),
                                (button) -> openLink(this, ModConstants.RESOURCE_PACK_TEMPLATE, false)
                        ).tooltip(
                                Tooltip.create(Component.translatable("qualityofqueso.menu.resource_pack_template.tooltip"))
                        ).build()
                )
        );

        this.createHeader(
                Component.translatable("qualityofqueso.header.questions_and_bugs"),
                this.createWidget(
                        Button.builder(Component.translatable("qualityofqueso.menu.ask_questions"),
                                (button) -> openLink(this, Links.DILLONS_DISCORD, false)
                        ).tooltip(
                                Tooltip.create(Component.translatable("qualityofqueso.menu.ask_questions.tooltip"))
                        ).build()
                ),

                this.createWidget(
                        Button.builder(Component.translatable("qualityofqueso.menu.report_bugs"),
                                (button) -> openLink(this, Links.githubIssues("Dillon8775/Quality-of-Queso"), false)
                        ).tooltip(
                                Tooltip.create(Component.translatable("qualityofqueso.menu.report_bugs.tooltip"))
                        ).build()
                )
        );
    }
}