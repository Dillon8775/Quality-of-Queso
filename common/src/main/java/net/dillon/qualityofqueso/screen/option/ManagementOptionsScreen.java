package net.dillon.qualityofqueso.screen.option;

import net.dillon.qualityofqueso.util.ModTexts;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;

import static net.dillon.qualityofqueso.helper.ModHelper.ofQoQ;

public class ManagementOptionsScreen extends AbstractModOptionsScreen {

    public ManagementOptionsScreen(Screen parent) {
        super(parent, Component.translatable("qualityofqueso.gui.title.management_options"));
    }

    @Override
    protected AbstractWidget[] options() {
        return new AbstractWidget[]{
                createOption(ListOptions.transferring()),
                Button.builder(
                        Component.translatable("qualityofqueso.gui.sorting").withColor(ModTexts.ITEM_COLOR),
                        button -> this.minecraft.setScreen(new SortingOptionsScreen(this))
                ).tooltip(Tooltip.create(Component.translatable("qualityofqueso.gui.sorting.tooltip"))).build(),

                createOption(ListOptions.filtering()),
                createOption(ListOptions.quickDrop()),

                createOption(ListOptions.singularMoving()),
                createOption(ListOptions.dragMoving()),

                createOption(ListOptions.swapping()),
                createOption(ListOptions.dragSorting()),

                createOption(ListOptions.layout()),
                Button.builder(
                    Component.translatable("qualityofqueso.gui.locked_slots").withColor(ModTexts.LOCKED_SLOT_TEXT),
                    button -> this.minecraft.setScreen(new LockedSlotsOptionsScreen(this))
                ).tooltip(Tooltip.create(Component.translatable("qualityofqueso.gui.locked_slots.tooltip"))).build(),

                createOption(ListOptions.playSounds()),
                Button.builder(Component.translatable("qualityofqueso.gui.button_display_options"), button -> {
                    this.minecraft.setScreen(new ButtonDisplayOptionsScreen(this));
                }).tooltip(Tooltip.create(Component.translatable("qualityofqueso.gui.button_display_options.tooltip"))).build(),

                Button.builder(Component.translatable("qualityofqueso.gui.configure_keybinds"), button -> {
                    this.openKeybinds();
                }).tooltip(Tooltip.create(Component.translatable("qualityofqueso.gui.keybinds.tooltip"))).build()
        };
    }

    @Override
    protected String youtubeLink() {
        return "https://youtu.be/02wfcgHkPmQ?si=YBNKVa_53pOtWa84&t=231";
    }

    @Override
    protected Component getYouTubeVideoTooltip() {
        return Component.translatable("qualityofqueso.gui.showcase.management.tooltip");
    }

    @Override
    protected void blitYouTubeSprite(GuiGraphics graphics) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, ofQoQ("textures/gui/button/sort/sort_creative_menu.png"), this.youtubeButton.getX() + 10, this.youtubeButton.getY() - 5, 0.0F, 0.0F, 12, 12, 12, 12);
    }
}