package net.dillon.qualityofqueso.screen.option;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import static net.dillon.qualityofqueso.helper.ModHelper.ofQoQ;

public class ButtonDisplayOptionsScreen extends AbstractModOptionsScreen {
    private AbstractWidget craftAll;

    public ButtonDisplayOptionsScreen(Screen parent) {
        super(parent, Component.translatable("qualityofqueso.gui.title.button_display_options"));
    }

    @Override
    protected AbstractWidget[] options() {
        this.craftAll = createOption(ListOptions.displayCraftAll());

        return new AbstractWidget[]{
                createOption(ListOptions.displayIncludeHotbar()),
                createOption(ListOptions.displayMoveMatchingItems()),

                createOption(ListOptions.displaySearchTransportables()),
                createOption(ListOptions.displayAlwaysQuickMove()),

                this.craftAll,
                createOption(ListOptions.displayFillStacks()),

                createOption(ListOptions.displayTradeAll())
        };
    }

    @Override
    protected void activateButtons() {
        this.craftAll.active = false;
    }

    @Override
    protected String youtubeLink() {
        return "https://www.youtube.com/watch?v=dwcWn7q6Q20&t=177s";
    }

    @Override
    protected Component getYouTubeVideoTooltip() {
        return Component.translatable("qualityofqueso.gui.showcase.management.tooltip");
    }

    @Override
    protected void blitYouTubeSprite(GuiGraphics graphics) {
        graphics.blit(ofQoQ("textures/gui/button/fill_stacks/fill_stacks.png"), this.youtubeButton.getX() + 10, this.youtubeButton.getY() - 5, 0.0F, 0.0F, 12, 12, 12, 12);
    }
}