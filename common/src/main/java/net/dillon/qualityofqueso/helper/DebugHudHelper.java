package net.dillon.qualityofqueso.helper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static net.dillon.qualityofqueso.helper.ModHelper.*;

/**
 * The holder class for custom debug huds; exclusive to 1.21.1 and below.
 */
public class DebugHudHelper {

    /**
     * Gets all the lines to render for custom debug huds.
     */
    public static void appendDebugHudLines(Minecraft minecraft, List<String> list) {
        if (!modEnabled(minecraft)) {
            return;
        }

        Level level = minecraft.level;
        LocalPlayer localPlayer = minecraft.player;
        Entity entity = minecraft.getCameraEntity();

        if (options().debugHuds.inGameTime && level != null) {
            long time = level.getGameTime() % 24000;
            time = (time + 6000) % 24000;

            int hours = (int) (time / 1000);
            int minutes = (int) ((time % 1000) * 60 / 1000);
            String amPm = hours >= 12 ? "PM" : "AM";
            int totalMinutes = hours * 60 + minutes;

            String description = "Night";
            if (totalMinutes >= 0 && totalMinutes < 60) {
                description = "Midnight";
            } else if (totalMinutes >= 12 * 60 && totalMinutes < 13 * 60) {
                description = "Noon";
            } else if (totalMinutes >= 60 && totalMinutes < 270) {
                description = "Night";
            } else if (totalMinutes >= 270 && totalMinutes < 345) {
                description = "Sunrise";
            } else if (totalMinutes >= 345 && totalMinutes < 480) {
                description = "Morning";
            } else if (totalMinutes >= 480 && totalMinutes < 720) {
                description = "Day";
            } else if (totalMinutes >= 780 && totalMinutes < 1020) {
                description = "Day";
            } else if (totalMinutes >= 1020 && totalMinutes < 1080) {
                description = "Evening";
            } else if (totalMinutes >= 1080 && totalMinutes < 1190) {
                description = "Sunset";
            }

            hours = hours % 12;
            if (hours == 0) {
                hours = 12;
            }

            String formatted = String.format("%d:%02d %s", hours, minutes, amPm);
            list.add(description + " (" + formatted + ", in-game)");
        }

        if (options().debugHuds.realLifeTime) {
            LocalTime time = LocalTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");
            list.add(time.format(formatter));
        }

        if (options().debugHuds.date) {
            LocalDate date = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy");
            list.add(date.format(formatter));
        }

        if (options().debugHuds.simpleCoordinates && level != null && localPlayer != null) {
            String cords = String.format("XYZ: %s / %s / %s", round(localPlayer.getX()), round(localPlayer.getY()), round(localPlayer.getZ()));
            list.add(cords);
        }

        if (options().debugHuds.accurateFacing && entity != null) {
            int northA = -110;
            int northB = -160;
            int southA = -70;
            int southB = -20;

            int northAAbs = Math.abs(northA);
            int northBAbs = Math.abs(northB);
            int southAAbs = Math.abs(southA);
            int southBAbs = Math.abs(southB);

            String facing;
            float yaw = Mth.wrapDegrees(entity.getYRot());
            if (yaw > northAAbs && yaw < northBAbs) {
                facing = "northwest";
            } else if (yaw > southBAbs && yaw < southAAbs) {
                facing = "southwest";
            } else if (yaw > southA && yaw < southB) {
                facing = "southeast";
            } else if (yaw < northA && yaw > northB) {
                facing = "northeast";
            } else {
                facing = switch (entity.getDirection()) {
                    case EAST -> "east";
                    case WEST -> "west";
                    case SOUTH -> "south";
                    default -> "north";
                };
            }

            list.add("Facing: " + facing);
        }

        if (options().debugHuds.fastestFlight && entity instanceof LocalPlayer player && player.getItemBySlot(EquipmentSlot.CHEST).is(Items.ELYTRA)) {
            list.add(String.valueOf(round(Mth.wrapDegrees(entity.getXRot()))));
        }
    }
}