package net.dillon.qualityofqueso.option;

import net.dillon.dillonlib.util.BaseOptions;
import net.dillon.qualityofqueso.helper.ModConstants;

public abstract class ModBaseOptionsHandler<T> extends BaseOptions<T> {

    public ModBaseOptionsHandler(String fileName) {
        super(fileName);
    }

    @Override
    public String configDir() {
        return ModConstants.DEFAULT_CONFIG_DIR;
    }
}