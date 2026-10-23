package net.dillon.qualityofqueso.container.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Stores data for ender chests.
 */
public class StoredEnderChestStack {
    public String item = "";
    public int amount = 0;
    public List<StoredEnderChestStack> containedItems = new ArrayList<>();
}