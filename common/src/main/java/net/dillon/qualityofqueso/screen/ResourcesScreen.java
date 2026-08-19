package net.dillon.qualityofqueso.screen;

import net.dillon.qualityofqueso.helper.ModConstants;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;

import static net.dillon.dillonlib.task.ClientTasks.openLink;

public class ResourcesScreen extends AbstractModScreen {

    public ResourcesScreen(Screen parent) {
        super(parent, Component.translatable("qualityofqueso.gui.title.resources"));
    }

    @Override
    protected void init() {
        super.init();

        this.list.addHeader(Component.translatable("qualityofqueso.menu.mod_resources"));
        this.list.addSmall(
                List.of(
                        Button.builder(Component.translatable("qualityofqueso.gui.showcase_video"),
                                (button) -> openLink(this, ModConstants.SHOWCASE_VIDEO_LINK, false)
                        ).tooltip(
                                Tooltip.create(Component.translatable("qualityofqueso.gui.showcase_video.tooltip"))
                        ).build(),

                        Button.builder(Component.translatable("qualityofqueso.gui.resource_pack_template"),
                                (button) -> openLink(this, ModConstants.RESOURCE_PACK_TEMPLATE, false)
                        ).tooltip(
                                Tooltip.create(Component.translatable("qualityofqueso.gui.resource_pack_template.tooltip"))
                        ).build()
                )
        );

        this.list.addHeader(Component.translatable("qualityofqueso.menu.questions_and_bugs"));
        this.list.addSmall(
                List.of(
                        Button.builder(Component.translatable("qualityofqueso.gui.ask_questions"),
                                (button) -> openLink(this, ModConstants.DISCORD_LINK, false)
                        ).tooltip(
                                Tooltip.create(Component.translatable("qualityofqueso.gui.ask_questions.tooltip"))
                        ).build(),

                        Button.builder(Component.translatable("qualityofqueso.gui.report_bugs"),
                                (button) -> openLink(this, "https://github.com/Dillon8775/Quality-of-Queso/issues", false)
                        ).tooltip(
                                Tooltip.create(Component.translatable("qualityofqueso.gui.report_bugs.tooltip"))
                        ).build()
                )
        );
    }
}