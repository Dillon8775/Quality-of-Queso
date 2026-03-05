package net.dillon.qualityofqueso.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.dillon.qualityofqueso.packet.GlowSearchC2SPayload;
import net.dillon.qualityofqueso.platform.MultiLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

import static net.dillon.qualityofqueso.util.ModUtil.options;

public class ItemFrameSearcherCommand {
    private static final String ITEM = "item (string)";
    private static final String RADIUS = "radius (1-300)";
    private static final String GLOW_DURATION = "glow duration (seconds, max 180)";

    /**
     * @return the {@code item frame searcher} command.
     */
    public static LiteralArgumentBuilder<CommandSourceStack> getItemFrameSearcherCommand(CommandBuildContext commandBuildContext) {
        return Commands.literal("itemframesearcher")
                .requires(source -> options().enableMod)
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
                                        Commands.argument(RADIUS, IntegerArgumentType.integer(1, 300))
                                                .executes(
                                                        context -> execute(
                                                                context.getSource(),
                                                                "",
                                                                true,
                                                                0,
                                                                IntegerArgumentType.getInteger(context, RADIUS)
                                                        )
                                                )
                                )
                )
                .then(
                        Commands.literal("find")
                                .then(
                                        Commands.argument(ITEM, StringArgumentType.string())
                                                .executes(
                                                        context -> execute(
                                                                context.getSource(),
                                                                StringArgumentType.getString(context, ITEM),
                                                                false,
                                                                0,
                                                                options().itemFrameSearchRadius
                                                        )
                                                )
                                                .then(
                                                        Commands.argument(RADIUS, IntegerArgumentType.integer(1, 300))
                                                                .executes(
                                                                        context -> execute(
                                                                                context.getSource(),
                                                                                StringArgumentType.getString(context, ITEM),
                                                                                false,
                                                                                0,
                                                                                IntegerArgumentType.getInteger(context, RADIUS)
                                                                        )
                                                                )
                                                                .then(
                                                                        Commands.argument(GLOW_DURATION, IntegerArgumentType.integer(0, 180))
                                                                                .executes(
                                                                                        context -> execute(
                                                                                                context.getSource(),
                                                                                                StringArgumentType.getString(context, ITEM),
                                                                                                false,
                                                                                                IntegerArgumentType.getInteger(context, GLOW_DURATION),
                                                                                                IntegerArgumentType.getInteger(context, RADIUS)
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
        MultiLoader.PLATFORM.sendToServer(new GlowSearchC2SPayload(query, Minecraft.getInstance().hasControlDown(), clear, timer, radius));
        return 0;
    }
}