package net.dillon.qualityofqueso;

import net.dillon.qualityofqueso.option.screen.ModOptionsScreen;
import net.dillon.qualityofqueso.util.ModUtil;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = QoQ.MOD_ID, dist = Dist.CLIENT)
public class QoQClient {

    /**
     * Registers client-side events for QoQ.
     */
    public QoQClient(IEventBus modEventBus, ModContainer container) {
        ModUtil.checkClientConfigsAndCrash();

        container.registerExtensionPoint(
                IConfigScreenFactory.class,
                (mc, parent) -> new ModOptionsScreen(parent)
        );
    }
}