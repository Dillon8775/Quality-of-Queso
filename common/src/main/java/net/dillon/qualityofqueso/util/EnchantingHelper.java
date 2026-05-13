package net.dillon.qualityofqueso.util;

import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.List;

/**
 * A list of a bunch of enchantment groups for listing in tooltips.
 */
public class EnchantingHelper {
    public static final List<Enchantment> ALL_PURPOSE_ENCHANTMENTS = List.of(
            Enchantments.MENDING,
            Enchantments.UNBREAKING,
            Enchantments.VANISHING_CURSE
    );

    public static final List<Enchantment> ARMOR_ENCHANTMENTS = List.of(
            Enchantments.AQUA_AFFINITY,
            Enchantments.BLAST_PROTECTION,
            Enchantments.BINDING_CURSE,
            Enchantments.DEPTH_STRIDER,
            Enchantments.FALL_PROTECTION,
            Enchantments.FIRE_PROTECTION,
            Enchantments.FROST_WALKER,
            Enchantments.PROJECTILE_PROTECTION,
            Enchantments.ALL_DAMAGE_PROTECTION,
            Enchantments.RESPIRATION,
            Enchantments.SOUL_SPEED,
            Enchantments.THORNS,
            Enchantments.SWIFT_SNEAK
    );

    public static final List<Enchantment> HELMET_EXCLUSIVE = List.of(
            Enchantments.AQUA_AFFINITY,
            Enchantments.RESPIRATION
    );

    public static final List<Enchantment> LEGS_EXCLUSIVE = List.of(
            Enchantments.SWIFT_SNEAK
    );

    public static final List<Enchantment> BOOTS_EXCLUSIVE = List.of(
            Enchantments.DEPTH_STRIDER,
            Enchantments.FALL_PROTECTION,
            Enchantments.FROST_WALKER,
            Enchantments.SOUL_SPEED
    );

    public static final List<Enchantment> SWORDS = List.of(
            Enchantments.BANE_OF_ARTHROPODS,
            Enchantments.FIRE_ASPECT,
            Enchantments.MOB_LOOTING,
            Enchantments.KNOCKBACK,
            Enchantments.SHARPNESS,
            Enchantments.SMITE,
            Enchantments.SWEEPING_EDGE
    );

    public static final List<Enchantment> TRIDENTS = List.of(
            Enchantments.IMPALING,
            Enchantments.RIPTIDE,
            Enchantments.CHANNELING,
            Enchantments.LOYALTY
    );

    public static final List<Enchantment> BOWS = List.of(
            Enchantments.POWER_ARROWS,
            Enchantments.PUNCH_ARROWS,
            Enchantments.FLAMING_ARROWS,
            Enchantments.INFINITY_ARROWS
    );

    public static final List<Enchantment> CROSSBOWS = List.of(
            Enchantments.MULTISHOT,
            Enchantments.PIERCING,
            Enchantments.QUICK_CHARGE
    );

    public static final List<Enchantment> TOOLS = List.of(
            Enchantments.BLOCK_EFFICIENCY,
            Enchantments.BLOCK_FORTUNE,
            Enchantments.SILK_TOUCH
    );

    public static final List<Enchantment> FISHING_RODS = List.of(
            Enchantments.FISHING_LUCK,
            Enchantments.FISHING_SPEED
    );

    /**
     * @return if an enchantment is in a group.
     */
    public static boolean isEnchantmentInGroup(List<Enchantment> group, Enchantment enchantment) {
        for (Enchantment e : group) {
            if (enchantment.equals(e)) {
                return true;
            }
        }
        return false;
    }
}