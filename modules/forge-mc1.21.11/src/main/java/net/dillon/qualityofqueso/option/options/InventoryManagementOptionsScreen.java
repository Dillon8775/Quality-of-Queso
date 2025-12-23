package net.dillon.qualityofqueso.option.options;

import net.dillon.qualityofqueso.option.AbstractModOptionsScreen;
import net.dillon.qualityofqueso.option.ModListOptions;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class InventoryManagementOptionsScreen extends AbstractModOptionsScreen {

    public InventoryManagementOptionsScreen(Screen parent) {
        super(parent, Component.translatable("qualityofqueso.gui.title.inventory_management_options"));
    }

    @Override
    protected OptionInstance<?>[] options() {
        return new OptionInstance[]{
                ModListOptions.inventoryManagement(),
                ModListOptions.SHORTCUT_KEYS,
                ModListOptions.QUICK_DROP,
                ModListOptions.swapping(),
                ModListOptions.INCLUDE_HOTBAR
        };
    }
}