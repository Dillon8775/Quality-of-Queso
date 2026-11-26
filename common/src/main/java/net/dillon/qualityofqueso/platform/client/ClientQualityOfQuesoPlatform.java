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
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;

import java.util.List;
import java.util.function.Function;

public abstract class ClientQualityOfQuesoPlatform extends ClientModPlatform {

    @Override
    public abstract boolean canSendPacket(LocalPlayer localPlayer);

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
        Registry.register(registry, ModDebugScreenEntries.DATE.entry(), new DateDebugEntry());
        Registry.register(registry, ModDebugScreenEntries.IN_GAME_TIME.entry(), new InGameTimeDebugEntry());
        Registry.register(registry, ModDebugScreenEntries.LOCAL_TIME.entry(), new RealLifeTimeDebugEntry());
        Registry.register(registry, ModDebugScreenEntries.ALARMS_AND_TIMERS.entry(), new AlarmsAndTimersDebugEntry());
        Registry.register(registry, ModDebugScreenEntries.SIMPLE_COORDINATES.entry(), new SimpleCoordinatesDebugEntry());
        Registry.register(registry, ModDebugScreenEntries.ROTATION.entry(), new RotationDebugEntry());
        Registry.register(registry, ModDebugScreenEntries.ACCURATE_FACING.entry(), new AccurateFacingDebugEntry());
        Registry.register(registry, ModDebugScreenEntries.PLAY_TIME.entry(), new PlayTimeDebugEntry());
        Registry.register(registry, ModDebugScreenEntries.POTION_EFFECTS.entry(), new PotionEffectsDebugEntry());
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