package net.dillon.qualityofqueso.option;

import net.dillon.qualityofqueso.QualityOfQuesoClient;
import net.dillon.qualityofqueso.util.ModTexts;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;

/**
 * Options displayed on {@link ModOptionsScreen}.
 */
@Environment(EnvType.CLIENT)
public class ModListOptions {

    public static final SimpleOption<Boolean> BETTER_GUI_EXIT = new SimpleOption<>("qualityofqueso.options.better_gui_exit", SimpleOption.constantTooltip(Text.translatable("qualityofqueso.options.better_gui_exit.tooltip")),
            (optionText, value) -> !value ? ModTexts.OFF : ModTexts.ON, SimpleOption.BOOLEAN, QualityOfQuesoClient.options().betterGuiExit, value -> QualityOfQuesoClient.options().betterGuiExit = value);

    public static final SimpleOption<Boolean> BETTER_SEARCHING = new SimpleOption<>("qualityofqueso.options.better_searching", SimpleOption.constantTooltip(Text.translatable("qualityofqueso.options.better_searching.tooltip")),
            (optionText, value) -> !value ? ModTexts.OFF : ModTexts.ON, SimpleOption.BOOLEAN, QualityOfQuesoClient.options().betterSearching, value -> QualityOfQuesoClient.options().betterSearching = value);

    public static final SimpleOption<Boolean> CONTAINER_SEARCHING = new SimpleOption<>("qualityofqueso.options.container_searching", SimpleOption.constantTooltip(Text.translatable("qualityofqueso.options.container_searching.tooltip")),
            (optionText, value) -> !value ? ModTexts.OFF : ModTexts.ON, SimpleOption.BOOLEAN, QualityOfQuesoClient.options().containerSearching, value -> QualityOfQuesoClient.options().containerSearching = value);

    public static final SimpleOption<Boolean> CONTAINER_TRANSFERRING = new SimpleOption<>("qualityofqueso.options.container_transferring", SimpleOption.constantTooltip(Text.translatable("qualityofqueso.options.container_transferring.tooltip")),
            (optionText, value) -> !value ? ModTexts.OFF : ModTexts.ON, SimpleOption.BOOLEAN, QualityOfQuesoClient.options().containerTransferring, value -> QualityOfQuesoClient.options().containerTransferring = value);

    public static final SimpleOption<Boolean> SEARCH_INVENTORY = new SimpleOption<>("qualityofqueso.options.search_inventory", SimpleOption.constantTooltip(Text.translatable("qualityofqueso.options.search_inventory.tooltip")),
            (optionText, value) -> !value ? ModTexts.OFF : ModTexts.ON, SimpleOption.BOOLEAN, QualityOfQuesoClient.options().searchInventory, value -> QualityOfQuesoClient.options().searchInventory = value);

    public static final SimpleOption<Boolean> SHOW_CONFIG_BUTTON = new SimpleOption<>("qualityofqueso.options.show_config_button", SimpleOption.constantTooltip(Text.translatable("qualityofqueso.options.show_config_button.tooltip")),
            (optionText, value) -> !value ? ModTexts.OFF : ModTexts.ON, SimpleOption.BOOLEAN, QualityOfQuesoClient.options().showConfigButton, value -> QualityOfQuesoClient.options().showConfigButton = value);
}