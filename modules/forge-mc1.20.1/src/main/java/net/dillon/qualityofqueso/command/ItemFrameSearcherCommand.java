package net.dillon.qualityofqueso.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.dillon.qualityofqueso.main.QoQ;
import net.dillon.qualityofqueso.packet.GlowSearchC2SPayload;
import net.dillon.qualityofqueso.packet.ServerHandler;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static net.dillon.qualityofqueso.main.QoQ.options;

@Mod.EventBusSubscriber(modid = QoQ.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ItemFrameSearcherCommand {

    /**
     * Registers the {@code /itemframesearcher} command.
     */
    @SubscribeEvent
    public static void register(RegisterClientCommandsEvent clientCommandsEvent) {
        clientCommandsEvent.getDispatcher().register(
                Commands.literal("itemframesearcher")
                        .requires(source -> options().enableMod && source.hasPermission(0))
                        .then(
                                Commands.literal("clear")
                                        .executes(
                                                context -> execute(
                                                        context.getSource(),
                                                        "",
                                                        true,
                                                        0,
                                                        options().itemFrameSearchRadius
                                                )
                                        )
                                        .then(
                                                Commands.argument("radius", IntegerArgumentType.integer(1, 300))
                                                        .executes(
                                                                context -> execute(
                                                                        context.getSource(),
                                                                        "",
                                                                        true,
                                                                        0,
                                                                        IntegerArgumentType.getInteger(context, "radius")
                                                                )
                                                        )
                                        )
                        )
                        .then(
                                Commands.literal("find")
                                        .then(
                                                Commands.argument("item", ItemArgument.item(clientCommandsEvent.getBuildContext()))
                                                        .executes(
                                                                context -> execute(
                                                                        context.getSource(),
                                                                        ItemArgument.getItem(context, "item").getItem().toString(),
                                                                        false,
                                                                        0,
                                                                        options().itemFrameSearchRadius
                                                                )
                                                        )
                                                        .then(
                                                                Commands.argument("radius", IntegerArgumentType.integer(1, 300))
                                                                        .executes(
                                                                                context -> execute(
                                                                                        context.getSource(),
                                                                                        ItemArgument.getItem(context, "item").getItem().toString(),
                                                                                        false,
                                                                                        0,
                                                                                        IntegerArgumentType.getInteger(context, "radius")
                                                                                )
                                                                        )
                                                                        .then(
                                                                                Commands.argument("timer", IntegerArgumentType.integer(0, 180))
                                                                                        .executes(
                                                                                                context -> execute(
                                                                                                        context.getSource(),
                                                                                                        ItemArgument.getItem(context, "item").getItem().toString(),
                                                                                                        false,
                                                                                                        IntegerArgumentType.getInteger(context, "timer"),
                                                                                                        IntegerArgumentType.getInteger(context, "radius")
                                                                                                )
                                                                                        )
                                                                        )
                                                        )
                                        )
                        )
        );
    }

    /**
     * Executes the command and makes respective item frames glow.
     */
    private static int execute(CommandSourceStack context, String query, boolean clear, int timer, int radius) {
        if (clear) {
            context.sendSuccess(() -> Component.translatable("qualityofqueso.item_frame_searcher_command.executed.clear", radius), true);
        } else if (timer == 0) {
            context.sendSuccess(() -> Component.translatable("qualityofqueso.item_frame_searcher_command.executed.without_timer", query, radius), true);
        } else {
            context.sendSuccess(() -> Component.translatable("qualityofqueso.item_frame_searcher_command.executed.with_timer", query, radius, timer), true);
        }
        ServerHandler.sendToServer(new GlowSearchC2SPayload(query, Screen.hasControlDown(), clear, timer, radius));
        return 0;
    }
}