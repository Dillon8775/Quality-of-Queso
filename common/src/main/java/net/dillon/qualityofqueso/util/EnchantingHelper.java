package net.dillon.qualityofqueso.util;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.List;

/**
 * A list of a bunch of enchantment groups for listing in tooltips.
 */
public class EnchantingHelper {
    public static final List<ResourceKey<Enchantment>> ALL_PURPOSE_ENCHANTMENTS = List.of(
            Enchantments.MENDING,
            Enchantments.UNBREAKING,
            Enchantments.VANISHING_CURSE
    );

    public static final List<ResourceKey<Enchantment>> ARMOR_ENCHANTMENTS = List.of(
            Enchantments.AQUA_AFFINITY,
            Enchantments.BLAST_PROTECTION,
            Enchantments.BINDING_CURSE,
            Enchantments.DEPTH_STRIDER,
            Enchantments.FEATHER_FALLING,
            Enchantments.FIRE_PROTECTION,
            Enchantments.FROST_WALKER,
            Enchantments.PROJECTILE_PROTECTION,
            Enchantments.PROTECTION,
            Enchantments.RESPIRATION,
            Enchantments.SOUL_SPEED,
            Enchantments.THORNS,
            Enchantments.SWIFT_SNEAK
    );

    public static final List<ResourceKey<Enchantment>> HELMET_EXCLUSIVE = List.of(
            Enchantments.AQUA_AFFINITY,
            Enchantments.RESPIRATION
    );

    public static final List<ResourceKey<Enchantment>> LEGS_EXCLUSIVE = List.of(
            Enchantments.SWIFT_SNEAK
    );

    public static final List<ResourceKey<Enchantment>> BOOTS_EXCLUSIVE = List.of(
            Enchantments.DEPTH_STRIDER,
            Enchantments.FEATHER_FALLING,
            Enchantments.FROST_WALKER,
            Enchantments.SOUL_SPEED
    );

    public static final List<ResourceKey<Enchantment>> SWORDS = List.of(
            Enchantments.BANE_OF_ARTHROPODS,
            Enchantments.FIRE_ASPECT,
            Enchantments.LOOTING,
            Enchantments.KNOCKBACK,
            Enchantments.SHARPNESS,
            Enchantments.SMITE,
            Enchantments.SWEEPING_EDGE
    );

    public static final List<ResourceKey<Enchantment>> MACES = List.of(
            Enchantments.BREACH,
            Enchantments.DENSITY,
            Enchantments.WIND_BURST
    );

    public static final List<ResourceKey<Enchantment>> TRIDENTS = List.of(
            Enchantments.IMPALING,
            Enchantments.RIPTIDE,
            Enchantments.CHANNELING,
            Enchantments.LOYALTY
    );

    public static final List<ResourceKey<Enchantment>> BOWS = List.of(
            Enchantments.POWER,
            Enchantments.PUNCH,
            Enchantments.FLAME,
            Enchantments.INFINITY
    );

    public static final List<ResourceKey<Enchantment>> CROSSBOWS = List.of(
            Enchantments.MULTISHOT,
            Enchantments.PIERCING,
            Enchantments.QUICK_CHARGE
    );

    public static final List<ResourceKey<Enchantment>> TOOLS = List.of(
            Enchantments.EFFICIENCY,
            Enchantments.FORTUNE,
            Enchantments.SILK_TOUCH
    );

    public static final List<ResourceKey<Enchantment>> FISHING_RODS = List.of(
            Enchantments.LUCK_OF_THE_SEA,
            Enchantments.LURE
    );

    /**
     * @return if an enchantment is in a group.
     */
    public static boolean isEnchantmentInGroup(List<ResourceKey<Enchantment>> group, Object2IntMap.Entry<Holder<Enchantment>> enchantment) {
        for (ResourceKey<Enchantment> e : group) {
            if (enchantment.getKey().is(e)) {
                return true;
            }
        }
        return false;
    }
}