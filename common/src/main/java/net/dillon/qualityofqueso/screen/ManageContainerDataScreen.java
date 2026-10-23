package net.dillon.qualityofqueso.screen;

import net.dillon.dillonlib.platform.Platforms;
import net.dillon.dillonlib.screen.BasicDillonLibScreen;
import net.dillon.dillonlib.util.Arithmetics;
import net.dillon.qualityofqueso.helper.ModConstants;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.CommonColors;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static net.dillon.dillonlib.task.ClientTasks.openScreen;
import static net.dillon.qualityofqueso.screen.ManageContainerDataScreen.KbAndMb.bytesToKb;
import static net.dillon.qualityofqueso.screen.ManageContainerDataScreen.KbAndMb.kbToMb;

/**
 * A simple way to manage container data.
 */
public class ManageContainerDataScreen extends BasicDillonLibScreen {
    private final Screen parent;
    protected final KbAndMb containerDataMemory = KbAndMb.init();
    protected final KbAndMb lockedSlotContainerMemory = KbAndMb.init();
    protected final KbAndMb lockedSlotPlayerMemory = KbAndMb.init();
    protected final KbAndMb totalMemory = KbAndMb.init();

    public ManageContainerDataScreen(Screen parent) {
        super(Component.translatable("qualityofqueso.manage_container_data"));
        this.parent = parent;
    }

    @Override
    public void onClose() {
        openScreen(this.parent);
    }

    /**
     * @return the size in a color.
     */
    protected static Component coloredSize(double size) {
        return coloredSize(size, false);
    }

    /**
     * @return the size with blue color.
     */
    protected static Component coloredSize(double size, boolean blue) {
        return Component.literal(String.valueOf(size)).withColor(blue ? ModConstants.DEFAULT_LOCKED_SLOT_COLOR : CommonColors.GREEN);
    }

    @Override
    public void widgets() {
        calculateFileSizes();

        builder().heightCenter().apply();
        builder().heightDown(64).apply();
        builder().widthCenter().apply();
        builder().widthLeft(150).apply();

        this.addRenderableWidget(
                Button.builder(Component.translatable("qualityofqueso.gui.clear_data"), b -> openScreen(new ConfirmClearContainerDataScreen(this)))
                        .bounds(builder().captureWidth(), builder().captureHeight(), 150, 20)
                        .build()
        );

        builder().widthCenter().apply();
        builder().widthRight(5).apply();

        this.addRenderableWidget(
                Button.builder(Component.translatable("gui.done"), b -> openScreen(this.parent))
                        .bounds(builder().captureWidth(), builder().captureHeight(), 150, 20)
                        .build()
        );
    }

    /**
     * Calculates file sizes to display on the screen.
     */
    protected void calculateFileSizes() {
        Path configDir = Platforms.getCommonPlatform()
                .configDir()
                .resolve(ModConstants.DEFAULT_CONFIG_DIR);

        try {
            long containerDataFileSize = Files.size(configDir.resolve(ModConstants.DEFAULT_CONTAINER_DATA_FILE_NAME));
            long lockedContainerSlotFileSize = Files.size(configDir.resolve(ModConstants.DEFAULT_LOCKED_CONTAINER_SLOTS_FILE_NAME));
            long lockedPlayerSlotFileSize = Files.size(configDir.resolve(ModConstants.DEFAULT_LOCKED_PLAYER_SLOTS_FILE_NAME));

            int containerDataMemoryKb = bytesToKb(containerDataFileSize);
            int lockedSlotContainerMemoryKb = bytesToKb(lockedContainerSlotFileSize);
            int lockedSlotPlayerMemoryKb = bytesToKb(lockedPlayerSlotFileSize);

            containerDataMemory.kb = containerDataMemoryKb;
            lockedSlotContainerMemory.kb = lockedSlotContainerMemoryKb;
            lockedSlotPlayerMemory.kb = lockedSlotPlayerMemoryKb;
            totalMemory.kb = containerDataMemoryKb + lockedSlotContainerMemoryKb + lockedSlotPlayerMemoryKb;

            containerDataMemory.mb = kbToMb(containerDataMemory.kb);
            lockedSlotContainerMemory.mb = kbToMb(lockedSlotContainerMemory.kb);
            lockedSlotPlayerMemory.mb = kbToMb(lockedSlotPlayerMemory.kb);
            totalMemory.mb = Arithmetics.roundToTenths(containerDataMemory.mb + lockedSlotContainerMemory.mb + lockedSlotPlayerMemory.mb);
        } catch (IOException o) {
            o.printStackTrace();
        }
    }

    @Override
    protected void drawGraphics(GuiGraphicsExtractor graphics) {
        builder().graphicsHeightTop().apply();
        builder().textTitleGraphicsHeight(graphics).apply();

        builder().graphicsHeightCenter().apply();
        builder().graphicsHeightUp(84).apply();

        builder().textCenterAndGraphicsHeightDown(
                graphics,
                Component.translatable(
                        "qualityofqueso.gui.container_data_memory",
                        coloredSize(containerDataMemory.kb),
                        coloredSize(containerDataMemory.mb, true)
                )
        ).apply();

        builder().textCenterAndGraphicsHeightDown(
                graphics,
                Component.translatable(
                        "qualityofqueso.gui.locked_slot_container_memory",
                        coloredSize(lockedSlotContainerMemory.kb),
                        coloredSize(lockedSlotContainerMemory.mb, true)
                )
        ).apply();

        builder().textCenterAndGraphicsHeightDown(
                graphics,
                Component.translatable(
                        "qualityofqueso.gui.locked_slot_player_memory",
                        coloredSize(lockedSlotPlayerMemory.kb),
                        coloredSize(lockedSlotPlayerMemory.mb, true)
                )
        ).apply();

        builder().textCenterAndGraphicsHeightDown(
                graphics,
                Component.translatable(
                        "qualityofqueso.gui.total_memory",
                        coloredSize(totalMemory.kb),
                        coloredSize(totalMemory.mb, true)
                )
        ).apply();
        builder().graphicsHeightDown(12).apply();

        if (this.totalMemory.mb < 5) {
            builder().textCenterAndGraphicsHeightDownWrapped(graphics, Component.translatable("qualityofqueso.gui.memory_good").withColor(CommonColors.GREEN)).apply();
        } else {
            builder().textCenterAndGraphicsHeightDownWrapped(graphics, Component.translatable("qualityofqueso.gui.monitor_memory").withColor(ModConstants.DEFAULT_LOCKED_SLOT_COLOR)).apply();
        }
    }

    /**
     * Stores file kilobyte and megabyte size.
     */
    public static class KbAndMb {
        public double kb;
        public double mb;

        public KbAndMb(int kb, int mb) {
            this.kb = kb;
            this.mb = mb;
        }

        /**
         * @return bytes to kilobytes as an integer.
         */
        public static int bytesToKb(long bytes) {
            return (int) (bytes / 1024.0D);
        }

        /**
         * @return kilobytes to megabytes.
         */
        public static double kbToMb(double kb) {
            return Arithmetics.roundToTenths(kb / 1000.0D);
        }

        public static KbAndMb init() {
            return new KbAndMb(0, 0);
        }
    }
}