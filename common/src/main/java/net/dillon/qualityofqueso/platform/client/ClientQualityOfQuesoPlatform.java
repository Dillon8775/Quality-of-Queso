package net.dillon.qualityofqueso.platform.client;

import com.mojang.brigadier.CommandDispatcher;
import net.dillon.dillonlib.client.command.ClientCommandFeedback;
import net.dillon.dillonlib.client.component.PlatformMenuButton;
import net.dillon.dillonlib.platform.client.ClientModPlatform;
import net.dillon.dillonlib.task.ClientTasks;
import net.dillon.qualityofqueso.command.QuesoAlarmCommand;
import net.dillon.qualityofqueso.command.QuesoStopwatchCommand;
import net.dillon.qualityofqueso.command.QuesoTimerCommand;
import net.dillon.qualityofqueso.debug.*;
import net.dillon.qualityofqueso.helper.ButtonHelper;
import net.dillon.qualityofqueso.helper.ModConstants;
import net.dillon.qualityofqueso.helper.ModHelper;
import net.minecraft.client.gui.components.debug.DebugScreenEntry;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;

import java.util.List;
import java.util.function.Function;

public class ClientQualityOfQuesoPlatform extends ClientModPlatform {

    @Override
    public String modId() {
        return ModConstants.MOD_ID;
    }

    @Override
    public void clientRuntimeTick() {
        if (!ModHelper.modEnabled()) {
            return;
        }

        // Tick all runtime events
        ModHelper.tickRuntime(ClientTasks.getMinecraft());
    }

    @Override
    public void clientGameTick() {
        if (!ModHelper.modEnabled()) {
            return;
        }

        // Tick all game events
        ModHelper.tickGame();
    }

    @Override
    public <S> void registerClientCommands(CommandDispatcher<S> dispatcher, Function<S, ClientCommandFeedback> feedback, CommandBuildContext commandRegistryAccess) {
        dispatcher.register(QuesoAlarmCommand.create(feedback));
        dispatcher.register(QuesoStopwatchCommand.create(feedback));
        dispatcher.register(QuesoTimerCommand.create(feedback));
    }

    @Override
    public void registerDebugEntries(WritableRegistry<DebugScreenEntry> registry) {
        Registry.register(registry, ModDebugScreenEntries.DATE, new DateDebugEntry());
        Registry.register(registry, ModDebugScreenEntries.IN_GAME_TIME, new InGameTimeDebugEntry());
        Registry.register(registry, ModDebugScreenEntries.REAL_LIFE_TIME, new RealLifeTimeDebugEntry());
        Registry.register(registry, ModDebugScreenEntries.ALARMS_AND_TIMERS, new AlarmsAndTimersDebugEntry());
        Registry.register(registry, ModDebugScreenEntries.SIMPLE_COORDINATES, new SimpleCoordinatesDebugEntry());
        Registry.register(registry, ModDebugScreenEntries.ROTATION, new RotationDebugEntry());
        Registry.register(registry, ModDebugScreenEntries.ACCURATE_FACING, new AccurateFacingDebugEntry());
        Registry.register(registry, ModDebugScreenEntries.PLAY_TIME, new PlayTimeDebugEntry());
        Registry.register(registry, ModDebugScreenEntries.POTION_EFFECTS, new PotionEffectsDebugEntry());
        ModDebugScreenEntries.createProfile();
    }

    @Override
    public List<PlatformMenuButton> menuButtons() {
        return List.of(
                PlatformMenuButton.builder(ButtonHelper.createMainMenuButton())
                        .build()
        );
    }
}