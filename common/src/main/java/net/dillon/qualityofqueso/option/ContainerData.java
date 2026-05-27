package net.dillon.qualityofqueso.option;

import net.dillon.qualityofqueso.util.ModConstants;

import java.util.*;

/**
 * Stores tracked containers in a separate file.
 */
public class ContainerData {
    public static final ModOptionsHandler INSTANCE = new ModOptionsHandler();
    public Set<String> itemFilteredContainers = new HashSet<>();
    public Set<String> tagFilteredContainers = new HashSet<>();
    public Map<String, String> containerFilteringModes = new HashMap<>();
    public Map<String, String> containerSortingModes = new HashMap<>();
    public Map<String, List<String>> containerFilterItems = new HashMap<>();
    public Map<String, List<StoredEnderChestStack>> enderChestItems = new HashMap<>();

    public static class StoredEnderChestStack {
        public String itemId = "";
        public int count = 0;
        public String components = "";
        public List<StoredEnderChestStack> containedItems = new ArrayList<>();
    }

    public static class ModOptionsHandler extends BaseOptions<ContainerData> {

        protected ModOptionsHandler() {
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