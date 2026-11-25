package net.dillon.qualityofqueso.debug;

import net.dillon.dillonlib.util.TickValue;
import net.minecraft.client.gui.components.debug.DebugGroups;
import net.minecraft.client.gui.components.debug.DebugScreenDisplayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jspecify.annotations.Nullable;

import static net.dillon.dillonlib.task.ClientTasks.getLevelKey;
import static net.dillon.qualityofqueso.option.OptionInstances.client;

public class PlayTimeDebugEntry extends ModDebugEntry {

    @Override
    public void display(DebugScreenDisplayer displayer, @Nullable Level serverOrClientLevel, @Nullable LevelChunk clientChunk, @Nullable LevelChunk serverChunk) {
        // Return out if the player isn't in a world, or the player is null
        if (serverOrClientLevel == null) {
            return;
        }

        // Add play time fact
        long playtimeInSeconds = client().misc().levelPlaytimes.getOrDefault(getLevelKey(), 0L);
        long playtimeInTicks = new TickValue(playtimeInSeconds).toTicksSeconds();
        displayer.addFactToGroup(
                DebugGroups.MISC,
                "Level Playtime",
                fact -> fact.value(new TickValue(playtimeInTicks).formattedFromTick())
        );
    }
}