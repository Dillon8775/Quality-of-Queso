package net.dillon.qualityofqueso.option;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

/**
 * The base class for registering options on enivronment sides.
 */
public abstract class BaseOptions<T> {
    private final Gson GSON = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).setPrettyPrinting().create();
    private final String fileName;
    private File file;
    protected T instance;

    /**
     * Constructor which initializes the file name and the instance of the options.
     */
    protected BaseOptions(String fileName) {
        this.fileName = fileName;
        this.instance = createDefault();
    }

    /**
     * Returns the type to get the options from.
     */
    protected abstract T createDefault();

    /**
     * Returns the class to get the options from.
     */
    protected abstract Class<T> getConfigClass();

    /**
     * Gets the instance of options.
     */
    public T getInstance() {
        return this.instance;
    }

    /**
     * Loads {@code this} config.
     */
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

    /**
     * Saves {@code this} config.
     */
    public void save() {
        try (FileWriter writer = new FileWriter(getConfigFile())) {
            writer.write(GSON.toJson(this.instance));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets {@code this} config file.
     */
    private File getConfigFile() {
        if (this.file == null) {
            this.file = new File(FabricLoader.getInstance().getConfigDir().toFile(), this.fileName);
        }
        return this.file;
    }
}