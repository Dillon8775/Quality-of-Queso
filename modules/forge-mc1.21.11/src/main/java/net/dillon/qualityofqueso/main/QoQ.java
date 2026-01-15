package net.dillon.qualityofqueso.main;

import net.dillon.qualityofqueso.debug.ModHudEntries;
import net.dillon.qualityofqueso.option.CommonOptions;
import net.dillon.qualityofqueso.option.ModClientOptions;
import net.dillon.qualityofqueso.packet.ServerHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static net.dillon.qualityofqueso.util.ModUtil.isOnServer;

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
            ItemTags.HEAD_ARMOR, EquipmentSlot.HEAD,
            ItemTags.CHEST_ARMOR, EquipmentSlot.CHEST,
            ItemTags.LEG_ARMOR, EquipmentSlot.LEGS,
            ItemTags.FOOT_ARMOR, EquipmentSlot.FEET
    );

    public QoQ(FMLJavaModLoadingContext context) {
        var modBusGroup = context.getModBusGroup();

        if (FMLEnvironment.dist.isClient()) {
            if (ModClientOptions.CLIENT_OPTIONS.getInstance() == null) {
                ModClientOptions.CLIENT_OPTIONS.setInstance(new ModClientOptions());
            }
            ModHudEntries.initializeDebugHudEntries();
        }
        if (CommonOptions.COMMON_OPTIONS.getInstance() == null) {
            CommonOptions.COMMON_OPTIONS.setInstance(new CommonOptions());
        }

        FMLCommonSetupEvent.getBus(modBusGroup).addListener(this::commonSetup);
    }

    /**
     * Common setup stuff.
     */
    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(ServerHandler::registerGlowSearchPayload);
    }

    /**
     * @return the client-options.
     */
    public static ModClientOptions options() {
        return ModClientOptions.CLIENT_OPTIONS.getInstance();
    }

    /**
     * @return the common-options.
     */
    public static CommonOptions coptions() {
        return CommonOptions.COMMON_OPTIONS.getInstance();
    }

    /**
     * Saves all configurations.
     */
    @OnlyIn(Dist.CLIENT)
    public static void saveAll() {
        ModClientOptions.CLIENT_OPTIONS.save();
        CommonOptions.COMMON_OPTIONS.save();
    }

    /**
	 * Checks if any of the mod's features should function.
	 */
	@OnlyIn(Dist.CLIENT)
    public static boolean modEnabled(Minecraft client) {
		Objects.requireNonNull(client, "\"client\" cannot be null.");

		if (isOnServer(client)) {
			for (String blacklistedServer : options().blacklistedServers) {
				if (client.getCurrentServer().ip.equals(blacklistedServer)) {
					return false;
				}
			}
		}
		return options().enableMod;
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
