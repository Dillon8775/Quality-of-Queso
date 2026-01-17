package net.dillon.qualityofqueso.option.screen;

import net.dillon.qualityofqueso.main.QoQ;
import net.dillon.qualityofqueso.util.ModUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractModOptionsScreen extends OptionsSubScreen {

    public AbstractModOptionsScreen(Screen parent, Component title) {
        super(parent, Minecraft.getInstance().options, title);
    }

    /**
     * The list of {@link OptionInstance}s that should be added to the screen.
     */
    protected abstract OptionInstance<?>[] options();

    @Override
    protected void init() {
        super.init();
        if (this.addOptionsByDefault()) {
            this.list.addSmall(this.options());
        }
    }

    @Override
    protected void addFooter() {
        LinearLayout linearLayout = this.layout.addToFooter(LinearLayout.vertical()).spacing(8);
        linearLayout.defaultCellSetting().alignHorizontallyCenter();
        LinearLayout linearLayout2 = linearLayout.addChild(LinearLayout.horizontal().spacing(8));
        linearLayout2.addChild(
                Button.builder(Component.translatable("qualityofqueso.gui.learn_more"),
                        ConfirmLinkScreen.confirmLink(this, "https://modrinth.com/mod/quality-of-queso")).build());
        linearLayout2.addChild(Button.builder(CommonComponents.GUI_DONE, button -> this.onClose()).build());
    }

    @Override
    public void onClose() {
        QoQ.saveAll(this.minecraft);
        ModUtil.info("Saved changes.");
        super.onClose();
    }

    /**
     * @return if all options in the {@link AbstractModOptionsScreen#options()} method should be added by default when calling {@code super.init().}
     */
    protected boolean addOptionsByDefault() {
        return true;
    }

    /**
     * Required method.
     */
    @Override
    protected void addOptions() {
    }
}