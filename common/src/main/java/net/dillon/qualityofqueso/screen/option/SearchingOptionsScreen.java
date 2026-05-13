package net.dillon.qualityofqueso.screen.option;

import net.dillon.qualityofqueso.helper.ModHelper;
import net.dillon.qualityofqueso.widget.gui.ColorField;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import static net.dillon.qualityofqueso.helper.GuiHelper.drawTooltip;
import static net.dillon.qualityofqueso.util.ModConstants.DEFAULT_TRANSPARENT_SEARCH_BAR_TEXT_COLOR;
import static net.dillon.qualityofqueso.util.ModConstants.SEARCH_TEXTURE;

public class SearchingOptionsScreen extends AbstractModOptionsScreen {
    private AbstractWidget searchBarPosition, searchBarColor, underlineText;
    private ColorField searchBarTextColorField;

    public SearchingOptionsScreen(Screen parent) {
        super(parent, Component.translatable("qualityofqueso.gui.title.searching_options"));
    }

    @Override
    protected AbstractWidget[] options() {
        this.searchBarTextColorField = new ColorField(
                this.font,
                ModHelper.options().searching.searchBarTextColor,
                DEFAULT_TRANSPARENT_SEARCH_BAR_TEXT_COLOR,
                Component.translatable("qualityofqueso.options.search_bar_text_color.tooltip")
        );

        this.searchBarPosition = createOption(ListOptions.searchBarPosition());
        this.searchBarColor = createOption(ListOptions.searchBarColor());
        this.underlineText = createOption(ListOptions.underlineText());

        return new AbstractWidget[]{
                createOption(ListOptions.containerSearching()),
                createOption(ListOptions.inventorySearching()),

                createOption(ListOptions.quickSearch()),
                this.searchBarPosition,

                this.searchBarColor,
                this.searchBarTextColorField,

                this.underlineText,
                createOption(ListOptions.saveSearchText()),
        };
    }

    @Override
    protected void activateButtons() {
        boolean searchingEnabled = ModHelper.options().searching.containerSearching || ModHelper.options().searching.inventorySearching;
        this.searchBarPosition.active = searchingEnabled;
        this.searchBarColor.active = searchingEnabled;
        this.searchBarTextColorField.active = false;
        this.underlineText.active = false;
    }

    @Override
    protected String youtubeLink() {
        return "https://youtu.be/02wfcgHkPmQ?si=uBMyxYaKbsZn6WMC&t=90";
    }

    @Override
    protected Component getYouTubeVideoTooltip() {
        return Component.translatable("qualityofqueso.gui.showcase.searching.tooltip");
    }

    @Override
    protected void blitYouTubeSprite(GuiGraphics graphics) {
        graphics.blitSprite(SEARCH_TEXTURE, this.youtubeButton.getX() + 12, this.youtubeButton.getY() - 3, 10, 10);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float deltaTicks) {
        super.render(graphics, mouseX, mouseY, deltaTicks);
        if (this.searchBarTextColorField.isMouseOver(mouseX, mouseY) && !this.searchBarTextColorField.isFocused()) {
            drawTooltip(this.searchBarTextColorField.getCurrentTooltip(), graphics, this.font, mouseX, mouseY);
        }
    }

    @Override
    public void onClose() {
        ModHelper.options().searching.searchBarTextColor = this.searchBarTextColorField.isSafeToSaveColor()
                ? ColorField.getTextColor(this.searchBarTextColorField.getValue())
                : this.searchBarTextColorField.getDefaultTextColor();
        super.onClose();
    }
}