package net.dillon.qualityofqueso.screen.gui;

import net.dillon.qualityofqueso.option.ModClientOptions;
import net.dillon.qualityofqueso.util.ButtonUtil;
import net.dillon.qualityofqueso.util.ModTexts;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.spongepowered.asm.mixin.Unique;

import java.util.function.Supplier;

/**
 * A representation of a transfer button.
 */
public class TransferButton extends Button {
    private final AbstractContainerMenu screenHandler;
    protected final String searchFieldText;
    protected final Font font;
    private final String buttonName;
    private final Supplier<Boolean> canBeActive;

    /**
     * Constructs a default transfer button.
     */
    public TransferButton(AbstractContainerMenu screenHandler, Font font, String searchFieldText, int x, int y, String buttonName, OnPress onPress) {
        super(x, y, 10, 10, ModTexts.BLANK, onPress, DEFAULT_NARRATION);
        this.screenHandler = screenHandler;
        this.font = font;
        this.searchFieldText = searchFieldText;
        this.buttonName = buttonName;
        this.canBeActive = () -> this.active;
    }

    /**
     * Constructs a default transfer button with a boolean supplier, determining if the button can be active or not.
     */
    public TransferButton(AbstractContainerMenu screenHandler, Font font, String searchFieldText, int x, int y, String buttonName, OnPress onPress, Supplier<Boolean> canBeActive) {
        super(x, y, 10, 10, ModTexts.BLANK, onPress, DEFAULT_NARRATION);
        this.buttonName = buttonName;
        this.screenHandler = screenHandler;
        this.font = font;
        this.searchFieldText = searchFieldText;
        this.canBeActive = canBeActive;
    }

    /**
     * Fully blocks sound and click if button shouldn't be activated.
     */
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!this.canBeActive.get()) {
            return false;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    /**
     * Renders a transfer button texture.
     */
    @Unique
    protected void renderButtonTexture(String id, boolean transferable, AbstractWidget buttonReference, GuiGraphics
            context) {
        String transferableString = !this.screenHandler.getCarried().isEmpty() ?
                "_with_stack.png" : this.searchFieldText.startsWith("!") ?
                "_exclude.png" : this.searchFieldText.startsWith("#") ?
                "_with_tag.png" : ".png";
        String appended = transferable ? transferableString : ".png";
        context.blit(RenderPipelines.GUI_TEXTURED, ResourceLocation.parse("qualityofqueso:textures/gui/" + id + appended), buttonReference.getX() - 1, buttonReference.getY() - 1, 0.0F, 0.0F, 12, 12, 12, 12);
    }

    /**
     * Renders the textures and tooltips for the button.
     */
    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float deltaTicks) {
        this.active = this.canBeActive.get();

        if (this.canBeActive.get()) {
            this.renderButtonTexture(this.isHovered() ?
                    this.buttonName + "_button_hovered" :
                    this.buttonName + "_button", true, this, graphics);
        } else {
            this.renderButtonTexture(this.buttonName + "_button_inactive", true, this, graphics);
        }

        if (this.isHovered()) {
            if (this.active) {
                if (!this.screenHandler.getCarried().isEmpty()) {
                    ButtonUtil.drawTooltip(Component.translatable("qualityofqueso.gui." + this.buttonName + "_button.with_cursor_stack", this.screenHandler.getCarried().getHoverName()), graphics, this.font, mouseX, mouseY);
                } else if (!this.searchFieldText.isEmpty()) {
                    ButtonUtil.drawTooltip(this.searchFieldText.startsWith("#") ?
                            Component.translatable("qualityofqueso.gui." + this.buttonName + "_button.with_search_query.tag", this.searchFieldText.substring(1)) :
                            this.searchFieldText.startsWith("!") ?
                                    Component.translatable("qualityofqueso.gui." + this.buttonName + "_button.with_search_query.exclude", this.searchFieldText.substring(1)) :
                                    Component.translatable("qualityofqueso.gui." + this.buttonName + "_button.with_search_query", this.searchFieldText), graphics, this.font, mouseX, mouseY);
                } else {
                    if (ModClientOptions.HELPFUL_TOOLTIPS.get()) {
                        ButtonUtil.drawTooltip(Component.translatable("qualityofqueso.gui." + this.buttonName + "_button"), graphics, this.font, mouseX, mouseY);
                    }
                }
            }
        }
    }
}