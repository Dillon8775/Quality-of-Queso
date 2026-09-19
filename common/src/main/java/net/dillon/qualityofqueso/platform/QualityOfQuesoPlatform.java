package net.dillon.qualityofqueso.platform;

import com.mojang.brigadier.CommandDispatcher;
import net.blay09.mods.balm.Balm;
import net.dillon.dillonlib.platform.ModPlatform;
import net.dillon.dillonlib.platform.Platforms;
import net.dillon.dillonlib.platform.info.Platform;
import net.dillon.dillonlib.platform.info.Release;
import net.dillon.qualityofqueso.command.ItemFrameSearcherCommand;
import net.dillon.qualityofqueso.helper.ModConstants;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;

public abstract class QualityOfQuesoPlatform extends ModPlatform {

    /**
     * Adds all mod ids to a list, for the ItemArgument.
     */
    public abstract void addModIds();

    @Override
    public void registerCommonCommands(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext commandRegistryAccess) {
        dispatcher.register(ItemFrameSearcherCommand.itemFrameSearcherCommand(commandRegistryAccess));
    }

    @Override
    public String modId() {
        return ModConstants.MOD_ID;
    }

    @Override
    public String modVersion() {
        return Platforms.getCommonPlatform().commonModVersion(ModConstants.MOD_ID);
    }

    @Override
    public Release release() {
        return Release.STABLE;
    }

    @Override
    public Platform platform() {
        return Balm.platform().name().equals("fabric") ? Platform.FABRIC : Platform.NEOFORGE;
    }
}