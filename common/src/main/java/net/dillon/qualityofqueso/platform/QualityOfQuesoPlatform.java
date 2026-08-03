package net.dillon.qualityofqueso.platform;

import com.mojang.brigadier.CommandDispatcher;
import net.blay09.mods.balm.Balm;
import net.dillon.dillonlib.platform.ModPlatform;
import net.dillon.dillonlib.platform.Platforms;
import net.dillon.dillonlib.platform.info.LogoWidth;
import net.dillon.dillonlib.platform.info.PlatformName;
import net.dillon.dillonlib.platform.info.PlatformRelease;
import net.dillon.qualityofqueso.command.ItemFrameSearcherCommand;
import net.dillon.qualityofqueso.helper.ModHelper;
import net.dillon.qualityofqueso.util.ModConstants;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

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
    public @NotNull Logger logger() {
        return ModHelper.LOGGER;
    }

    @Override
    public String modVersion() {
        return Platforms.getCommonPlatform().commonModVersion(ModConstants.MOD_ID);
    }

    @Override
    public @NotNull PlatformName platformName() {
        return Balm.platform().name().equals("fabric") ? PlatformName.FABRIC : PlatformName.NEOFORGE;
    }

    @Override
    public @NotNull PlatformRelease platformRelease() {
        return PlatformRelease.STABLE;
    }

    @Override
    public @NotNull LogoWidth logoWidth() {
        return LogoWidth.LONG_PATCH;
    }
}