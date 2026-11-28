package net.dillon.qualityofqueso.option;

import com.google.gson.Gson;
import net.dillon.dillonlib.util.BaseOptions;
import net.dillon.qualityofqueso.helper.ModConstants;
import net.dillon.qualityofqueso.util.CustomItemGroup;

import java.util.ArrayList;
import java.util.List;

import static net.dillon.qualityofqueso.util.CustomItemGroup.EXAMPLE_ITEM_GROUP;

/**
 * Stores all user-created custom item groups.
 */
public class CustomItemGroups {
    public static final CustomItemGroupsHandler INSTANCE = new CustomItemGroupsHandler();
    public List<CustomItemGroup> customItemGroups = new ArrayList<>(List.of(EXAMPLE_ITEM_GROUP));

    public static class CustomItemGroupsHandler extends ModBaseOptionsHandler<CustomItemGroups> {

        protected CustomItemGroupsHandler() {
            super(ModConstants.DEFAULT_CUSTOM_ITEM_GROUPS_CONFIG_FILE_NAME);
        }

        @Override
        public boolean handleLoadException(Exception e) {
            e.printStackTrace();
            CustomItemGroup.ERROR_PARSING_FILE = true;
            CustomItemGroup.PARSING_ERROR = e.getMessage();
            return false;
        }

        @Override
        public Gson createGson() {
            return BaseOptions.defaultGsonBuilder()
                    .disableHtmlEscaping()
                    .create();
        }

        @Override
        protected CustomItemGroups createDefault() {
            return new CustomItemGroups();
        }

        @Override
        protected Class<CustomItemGroups> getConfigClass() {
            return CustomItemGroups.class;
        }
    }
}