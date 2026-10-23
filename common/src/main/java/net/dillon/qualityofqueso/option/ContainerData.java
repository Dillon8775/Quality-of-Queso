package net.dillon.qualityofqueso.option;

import net.dillon.qualityofqueso.container.data.ContainerSortModes;
import net.dillon.qualityofqueso.container.data.FilteredContainerData;
import net.dillon.qualityofqueso.container.data.StoredEnderChestStack;
import net.dillon.qualityofqueso.helper.ModConstants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Stores tracked containers in a separate file.
 */
public class ContainerData {
    public static final ContainerDataHandler INSTANCE = new ContainerDataHandler();

    /// Mapped to world name -> list of full container data
    public Map<String, List<FilteredContainerData>> filteredContainers = new HashMap<>();

    /// Mapped to world name -> list of containers with their sorting modes
    public Map<String, List<ContainerSortModes>> sortingModes = new HashMap<>();

    /// Mapped to world name -> list of ender chest data
    public Map<String, List<StoredEnderChestStack>> enderChestItems = new HashMap<>();

    public static class ContainerDataHandler extends ModBaseOptionsHandler<ContainerData> {

        protected ContainerDataHandler() {
            super(ModConstants.DEFAULT_CONTAINER_DATA_FILE_NAME);
            this.load();
        }

        @Override
        protected ContainerData createDefault() {
            return new ContainerData();
        }

        @Override
        protected Class<ContainerData> getConfigClass() {
            return ContainerData.class;
        }
    }
}