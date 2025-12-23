package net.dillon.qualityofqueso.main;

import net.dillon.qualityofqueso.option.ModClientOptions;
import net.dillon.qualityofqueso.option.ModCommonOptions;
import net.dillon.qualityofqueso.packet.ServerHandler;
import net.dillon.qualityofqueso.util.ModUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.Tags;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Mod(QoQ.MOD_ID)
public final class QoQ {
    public static final String MOD_ID = "qualityofqueso";
    public static String SAVED_TEXT = "";
	public static String SAVED_ITEM_FRAME_TEXT = "";
	public static final List<Integer> popularKeys = List.of(GLFW.GLFW_KEY_T, GLFW.GLFW_KEY_E);
	public static final List<Integer> allDisallowedKeys = List.of(
			GLFW.GLFW_KEY_1,
			GLFW.GLFW_KEY_2,
			GLFW.GLFW_KEY_3,
			GLFW.GLFW_KEY_4,
			GLFW.GLFW_KEY_5,
			GLFW.GLFW_KEY_6,
			GLFW.GLFW_KEY_7,
			GLFW.GLFW_KEY_8,
			GLFW.GLFW_KEY_9,
			GLFW.GLFW_KEY_ESCAPE,
			GLFW.GLFW_KEY_LEFT_SHIFT,
			GLFW.GLFW_KEY_RIGHT_SHIFT,
			GLFW.GLFW_KEY_LEFT_CONTROL,
			GLFW.GLFW_KEY_RIGHT_CONTROL,
			GLFW.GLFW_KEY_LEFT_ALT,
			GLFW.GLFW_KEY_RIGHT_ALT,
			GLFW.GLFW_KEY_LEFT_SUPER,
			GLFW.GLFW_KEY_RIGHT_SUPER
	);
    public static final List<Integer> disallowedKeys = List.of(
            GLFW.GLFW_KEY_ESCAPE,
            GLFW.GLFW_KEY_LEFT_SHIFT,
            GLFW.GLFW_KEY_RIGHT_SHIFT,
            GLFW.GLFW_KEY_LEFT_CONTROL,
            GLFW.GLFW_KEY_RIGHT_CONTROL,
            GLFW.GLFW_KEY_LEFT_ALT,
            GLFW.GLFW_KEY_RIGHT_ALT,
            GLFW.GLFW_KEY_LEFT_SUPER,
            GLFW.GLFW_KEY_RIGHT_SUPER
    );
    public static final Map<TagKey<Item>, EquipmentSlot> quicklyEquippables = Map.of(
            Tags.Items.ARMORS_HELMETS, EquipmentSlot.HEAD,
            Tags.Items.ARMORS_CHESTPLATES, EquipmentSlot.CHEST,
            Tags.Items.ARMORS_LEGGINGS, EquipmentSlot.LEGS,
            Tags.Items.ARMORS_BOOTS, EquipmentSlot.FEET
    );
    public static final List<Item> shulkerBoxes = List.of(
            Items.SHULKER_BOX,
            Items.WHITE_SHULKER_BOX,
            Items.ORANGE_SHULKER_BOX,
            Items.MAGENTA_SHULKER_BOX,
            Items.LIGHT_BLUE_SHULKER_BOX,
            Items.YELLOW_SHULKER_BOX,
            Items.LIME_SHULKER_BOX,
            Items.PINK_SHULKER_BOX,
            Items.GRAY_SHULKER_BOX,
            Items.LIGHT_GRAY_SHULKER_BOX,
            Items.CYAN_SHULKER_BOX,
            Items.PURPLE_SHULKER_BOX,
            Items.BLUE_SHULKER_BOX,
            Items.BROWN_SHULKER_BOX,
            Items.GREEN_SHULKER_BOX,
            Items.RED_SHULKER_BOX,
            Items.BLACK_SHULKER_BOX
    );

    public QoQ(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();

        if (FMLEnvironment.dist.isClient()) {
            context.registerConfig(ModConfig.Type.CLIENT, ModClientOptions.SPEC);
        }
        context.registerConfig(ModConfig.Type.COMMON, ModCommonOptions.SPEC);

        modEventBus.addListener(this::commonSetup);
    }

    /**
     * Common setup stuff.
     */
    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(ServerHandler::registerGlowSearchPayload);
    }

    /**
     * Saves all configurations.
     */
    public static void saveAll() {
        ModClientOptions.SPEC.save();
        ModCommonOptions.SPEC.save();
    }

    /**
	 * Checks if any of the mod's features should function.
	 */
	@OnlyIn(Dist.CLIENT)
    public static boolean modEnabled(Minecraft client) {
		Objects.requireNonNull(client, "\"client\" cannot be null.");

		if (ModUtil.isOnServer(client)) {
			for (String blacklistedServer : ModClientOptions.BLACKLISTED_SERVERS.get()) {
				if (client.getCurrentServer().ip.equals(blacklistedServer)) {
					return false;
				}
			}
		}
		return ModClientOptions.ENABLE_MOD.get();
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MOD_ID, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
        }
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, value = Dist.DEDICATED_SERVER)
    public static class ServerModEvents {

        @SubscribeEvent
        public static void onServerSetup(FMLDedicatedServerSetupEvent event) {
        }
    }
}
