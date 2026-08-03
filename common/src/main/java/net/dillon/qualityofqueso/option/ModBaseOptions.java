package net.dillon.qualityofqueso.option;

import net.dillon.dillonlib.util.BaseOptions;
import net.dillon.qualityofqueso.util.ModConstants;

public abstract class ModBaseOptions<T> extends BaseOptions<T> {

    public ModBaseOptions(String fileName) {
        super(fileName);
    }

    @Override
    public String configDir() {
        return ModConstants.DEFAULT_CONFIG_DIR;
    }
}