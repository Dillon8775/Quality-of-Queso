package net.dillon.qualityofqueso.option;

import net.minecraftforge.common.ForgeConfigSpec;

public class ModCommonOptions {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.BooleanValue ITEM_FRAME_SEARCHING;

    static {
        BUILDER.push("Server-side config for Quality of Queso");

        ITEM_FRAME_SEARCHING = BUILDER
                .comment("Search for an item frame with a specific item inside of it and make it glow.")
                .define("itemFrameSearchingOnServer", true);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}