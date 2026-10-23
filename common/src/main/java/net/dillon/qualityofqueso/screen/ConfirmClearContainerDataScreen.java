package net.dillon.qualityofqueso.screen;

import net.dillon.dillonlib.platform.Platforms;
import net.dillon.dillonlib.screen.LevelSelectorScreen;
import net.dillon.qualityofqueso.helper.ModConstants;
import net.dillon.qualityofqueso.option.ContainerData;
import net.dillon.qualityofqueso.option.LockedContainerSlots;
import net.dillon.qualityofqueso.option.LockedPlayerSlots;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.CommonColors;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static net.dillon.dillonlib.task.ClientTasks.openScreen;
import static net.dillon.qualityofqueso.option.OptionInstances.*;

/**
 * Confirms if you want to clear container data or not.
 */
public class ConfirmClearContainerDataScreen extends ManageContainerDataScreen {
    private final List<Map<String, ?>> allData = List.of(
            containerData().filteredContainers,
            containerData().sortingModes,
            containerData().enderChestItems,
            lockedContainerSlots().lockedContainerSlots,
            lockedPlayerSlots().lockedPlayerSlots
    );
    private boolean clearMarkedContainerData = true;
    private boolean clearLockedContainerSlots = false;
    private boolean clearLockedPlayerSlots = false;
    protected String selectedLevel = "";
    private final Screen parent;

    public ConfirmClearContainerDataScreen(Screen parent) {
        super(parent);
        this.parent = parent;
    }

    /**
     * Removes data from a specified world.
     */
    private boolean removeData(Map.Entry<String, ?> entry) {
        String key = entry.getKey();
        int colonIndex = key.indexOf(':');

        if (colonIndex == -1) {
            return false;
        }

        String worldNameKey = key.substring(colonIndex + 1);
        return worldNameKey.equals(selectedLevel);
    }

    /**
     * @return the level key name for a specified entry.
     */
    private String getLevelName(String key) {
        int colonIndex = key.indexOf(':');

        if (colonIndex == -1) {
            return "";
        }

        return key.substring(colonIndex + 1);
    }

    /**
     * Adds all cached servers to the servers list.
     */
    private void addToServers(Map<String, ?> data, Set<String> servers) {
        data.keySet().forEach(key -> {
            if (!key.startsWith("server:")) {
                return;
            }

            servers.add(getLevelName(key));
        });
    }

    /**
     * @return a set of all stored servers.
     */
    private Set<String> getAllServers() {
        Set<String> servers = new HashSet<>();
        this.allData.forEach(stringMap -> addToServers(stringMap, servers));
        return servers;
    }

    /**
     * @return if any entries are valid to the selected world.
     */
    private boolean dataContainsSelectedLevel(Map<String, ?> data) {
        return dataContainsWorldName(data, selectedLevel);
    }

    /**
     * @return if any entries are valid to the world name.
     */
    private boolean dataContainsWorldName(Map<String, ?> data, String world) {
        return data.entrySet().stream().anyMatch(entry -> getLevelName(entry.getKey()).equals(world));
    }

    /**
     * @return if all selected data is empty.
     */
    private boolean isDataEmpty() {
        int dataSize = 0;

        if (!selectedLevel.isEmpty()) {
            if (clearMarkedContainerData) {

                if (dataContainsSelectedLevel(containerData().filteredContainers) || dataContainsSelectedLevel(containerData().sortingModes) || dataContainsSelectedLevel(containerData().enderChestItems)) {
                    dataSize++;
                }
            }

            if (clearLockedContainerSlots && dataContainsSelectedLevel(lockedContainerSlots().lockedContainerSlots)) {
                dataSize++;
            }

            if (clearLockedPlayerSlots && dataContainsSelectedLevel(lockedPlayerSlots().lockedPlayerSlots)) {
                dataSize++;
            }
        } else {
            if (clearMarkedContainerData) {

                if (!containerData().filteredContainers.isEmpty() || !containerData().sortingModes.isEmpty() || !containerData().enderChestItems.isEmpty()) {
                    dataSize++;
                }
            }

            if (clearLockedContainerSlots && !lockedContainerSlots().lockedContainerSlots.isEmpty()) {
                dataSize++;
            }

            if (clearLockedPlayerSlots && !lockedPlayerSlots().lockedPlayerSlots.isEmpty()) {
                dataSize++;
            }
        }

        return dataSize == 0;
    }

    /**
     * Clears container data.
     */
    private void clearData() {
        try {
            Path configDir = Platforms.getCommonPlatform()
                    .configDir()
                    .resolve(ModConstants.DEFAULT_CONFIG_DIR);

            Path backupDir = Platforms.getCommonPlatform()
                    .configDir()
                    .resolve(ModConstants.BACKUP_CONFIG_DIR);

            Files.createDirectories(backupDir);

            Files.copy(
                    configDir.resolve(ModConstants.DEFAULT_CONTAINER_DATA_FILE_NAME),
                    backupDir.resolve(ModConstants.DEFAULT_CONTAINER_DATA_FILE_NAME),
                    StandardCopyOption.REPLACE_EXISTING
            );

            Files.copy(
                    configDir.resolve(ModConstants.DEFAULT_LOCKED_CONTAINER_SLOTS_FILE_NAME),
                    backupDir.resolve(ModConstants.DEFAULT_LOCKED_CONTAINER_SLOTS_FILE_NAME),
                    StandardCopyOption.REPLACE_EXISTING
            );

            Files.copy(
                    configDir.resolve(ModConstants.DEFAULT_LOCKED_PLAYER_SLOTS_FILE_NAME),
                    backupDir.resolve(ModConstants.DEFAULT_LOCKED_PLAYER_SLOTS_FILE_NAME),
                    StandardCopyOption.REPLACE_EXISTING
            );

            if (selectedLevel.isEmpty()) {
                if (clearMarkedContainerData) {
                    ContainerData.INSTANCE.setInstance(new ContainerData());
                }

                if (clearLockedContainerSlots) {
                    LockedContainerSlots.INSTANCE.setInstance(new LockedContainerSlots());
                }

                if (clearLockedPlayerSlots) {
                    LockedPlayerSlots.INSTANCE.setInstance(new LockedPlayerSlots());
                }
            } else {
                if (clearMarkedContainerData) {
                    containerData().filteredContainers.entrySet().removeIf(this::removeData);
                    containerData().sortingModes.entrySet().removeIf(this::removeData);
                    containerData().enderChestItems.entrySet().removeIf(this::removeData);
                }

                if (clearLockedContainerSlots) {
                    lockedContainerSlots().lockedContainerSlots.entrySet().removeIf(this::removeData);
                }

                if (clearLockedPlayerSlots) {
                    lockedPlayerSlots().lockedPlayerSlots.entrySet().removeIf(this::removeData);
                }
            }

            ContainerData.INSTANCE.save();
            ContainerData.INSTANCE.load();

            LockedContainerSlots.INSTANCE.save();
            LockedContainerSlots.INSTANCE.load();

            LockedPlayerSlots.INSTANCE.save();
            LockedPlayerSlots.INSTANCE.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        openScreen(this.parent);
    }

    @Override
    public void widgets() {
        calculateFileSizes();

        builder().heightCenter().apply();
        builder().heightDown(12).apply();
        builder().widthCenter().apply();
        builder().widthLeft(225).apply();

        this.addRenderableWidget(
                this.createWidget(
                        Button.builder(Component.translatable("dillonlib.confirm"), b -> clearData())
                                .bounds(builder().captureWidth(), builder().captureHeight(), 150, 20)
                                .build(),
                        () -> !this.isDataEmpty(),
                        isActive -> {
                            if (isDataEmpty()) {
                                return Component.translatable("qualityofqueso.gui.no_data_found");
                            } else {
                                return Component.translatable("qualityofqueso.gui.clear_and_backup");
                            }
                        })
        );

        builder().widthRight(160).apply();

        this.addRenderableWidget(
                Button.builder(Component.translatable("qualityofqueso.gui.select_world_or_server"), b ->
                                openScreen(
                                        new LevelSelectorScreen(
                                                this,
                                                Component.translatable("qualityofqueso.manage_container_data_from_world"),
                                                onWorldSelected -> selectedLevel = onWorldSelected,
                                                levelSummary -> {
                                                    String worldId = levelSummary.getLevelId();

                                                    for (Map<String, ?> data : this.allData) {
                                                        if (dataContainsWorldName(data, worldId)) {
                                                            return true;
                                                        }
                                                    }

                                                    return false;
                                                },
                                                this.getAllServers()
                                        )
                                )
                        )
                        .bounds(builder().captureWidth(), builder().captureHeight(), 150, 20)
                        .tooltip(
                                Tooltip.create(Component.translatable("qualityofqueso.gui.select_world_or_server.tooltip"))
                        )
                        .build()
        );

        builder().widthRight(160).apply();

        this.addRenderableWidget(
                Button.builder(Component.translatable("gui.cancel"), b -> openScreen(this.parent))
                        .bounds(builder().captureWidth(), builder().captureHeight(), 150, 20)
                        .build()
        );

        builder().heightCenter().apply();
        builder().heightDown(42).apply();
        builder().widthCenter().apply();
        builder().widthLeft(142).apply();

        this.addRenderableWidget(
                Checkbox.builder(Component.translatable("qualityofqueso.gui.clear_marked_container_data").withColor(CommonColors.LIGHTER_GRAY), getFont())
                        .pos(builder().captureWidth(), builder().captureHeight())
                        .selected(true)
                        .onValueChange((checkbox, value) -> clearMarkedContainerData = value)
                        .build()
        );

        builder().heightDown().apply();

        this.addRenderableWidget(
                Checkbox.builder(Component.translatable("qualityofqueso.gui.clear_locked_container_slots").withColor(CommonColors.LIGHTER_GRAY), getFont())
                        .pos(builder().captureWidth(), builder().captureHeight())
                        .onValueChange((checkbox, value) -> clearLockedContainerSlots = value)
                        .build()
        );

        builder().heightDown().apply();

        this.addRenderableWidget(
                Checkbox.builder(Component.translatable("qualityofqueso.gui.clear_locked_player_slots").withColor(CommonColors.LIGHTER_GRAY), getFont())
                        .pos(builder().captureWidth(), builder().captureHeight())
                        .onValueChange((checkbox, value) -> clearLockedPlayerSlots = value)
                        .build()
        );
    }

    @Override
    protected void drawGraphics(GuiGraphicsExtractor graphics) {
        builder().graphicsHeightTop().apply();
        builder().textTitleGraphicsHeight(graphics).apply();

        builder().graphicsHeightCenter().apply();
        builder().graphicsHeightUp(72).apply();

        double totalKb = 0;
        double totalMb = 0;
        if (clearMarkedContainerData) {
            totalKb += this.containerDataMemory.kb;
            totalMb += this.containerDataMemory.mb;
        }
        if (clearLockedContainerSlots) {
            totalKb += this.lockedSlotContainerMemory.kb;
            totalMb += this.lockedSlotContainerMemory.mb;
        }
        if (clearLockedPlayerSlots) {
            totalKb += this.lockedSlotPlayerMemory.kb;
            totalMb += this.lockedSlotPlayerMemory.mb;
        }

        Component freesUp = Component.translatable("qualityofqueso.gui.frees_up", coloredSize(totalKb), coloredSize(totalMb, true));
        builder().textCenterAndGraphicsHeightDownWrapped(graphics, Component.translatable("qualityofqueso.gui.clear_cached_data", selectedLevel.isEmpty() ? freesUp : "")).apply();

        if (!selectedLevel.isEmpty()) {
            builder().graphicsHeightCenter().apply();
            builder().graphicsHeightUp(90).apply();
            builder().textCenterAndGraphicsHeightDown(graphics, Component.translatable("qualityofqueso.gui.selected_world", selectedLevel).withColor(CommonColors.GREEN));
        }
    }
}