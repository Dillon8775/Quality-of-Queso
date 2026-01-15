package net.dillon.qualityofqueso.debug;

import net.dillon.qualityofqueso.main.QoQ;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.CustomizeGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static net.dillon.qualityofqueso.main.QoQ.options;

@Mod.EventBusSubscriber(modid = QoQ.MOD_ID, value = Dist.CLIENT)
public class ModHudEntries {

    @SubscribeEvent
    public static void onDebugText(CustomizeGuiOverlayEvent.DebugText event) {
        ClientLevel level = Minecraft.getInstance().level;
        if (options().showInGameTime && level != null) {
            long time = level.getDayTime() % 24000;

            time = (time + 6000) % 24000;

            int hours = (int) (time / 1000);
            int minutes = (int) ((time % 1000) * 60 / 1000);

            String amPm = hours >= 12 ? "PM" : "AM";
            hours = hours % 12;
            if (hours == 0) hours = 12;

            String formatted = String.format("%d:%02d %s", hours, minutes, amPm);
            event.getLeft().add(formatted + " (in-game)");
        }

        if (options().showIrlTime) {
            LocalTime time = LocalTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");
            event.getRight().add(time.format(formatter));
        }
    }
}