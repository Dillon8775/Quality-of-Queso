package net.dillon.qualityofqueso.option;

import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import static net.fabricmc.fabric.impl.resource.loader.ModResourcePackUtil.GSON;

public abstract class BaseOptions<T> {
    private final String fileName;
    private File file;
    protected T instance;

    protected BaseOptions(String fileName) {
        this.fileName = fileName;
        this.instance = createDefault();
    }

    protected abstract T createDefault();
    protected abstract Class<T> getConfigClass();

    public T getInstance() {
        return instance;
    }

    public void load() {
        File configFile = getConfigFile();
        if (!configFile.exists()) {
            this.instance = createDefault();
        } else {
            try (FileReader reader = new FileReader(configFile)) {
                this.instance = GSON.fromJson(reader, getConfigClass());
            } catch (Exception e) {
                e.printStackTrace();
                this.instance = createDefault();
            }
        }
        save();
    }

    public void save() {
        try (FileWriter writer = new FileWriter(getConfigFile())) {
            writer.write(GSON.toJson(instance));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private File getConfigFile() {
        if (file == null) {
            file = new File(FabricLoader.getInstance().getConfigDir().toFile(), fileName);
        }
        return file;
    }
}