package net.dillon.qualityofqueso.option;

/**
 * Getters for all Quality of Queso's option instances.
 */
public class OptionInstances {

    /**
     * @return the client-options.
     */
    public static ModClientOptions client() {
        return ModClientOptions.INSTANCE.getInstance();
    }

    /**
     * @return the common-options.
     */
    public static ModCommonOptions common() {
        return ModCommonOptions.INSTANCE.getInstance();
    }

    /**
     * @return universal options, unaffected by server configs.
     */
    public static UniversalOptions universal() {
        return UniversalOptions.INSTANCE.getInstance();
    }

    /**
     * @return mixin options, unaffected by server configs.
     */
    public static MixinOptions mixins() {
        return MixinOptions.INSTANCE.getInstance();
    }

    /**
     * @return tracked containers options.
     */
    public static ContainerData containerData() {
        return ContainerData.INSTANCE.getInstance();
    }

    /**
     * @return locked player slots, respective to the client-player.
     */
    public static LockedPlayerSlots lockedPlayerSlots() {
        return LockedPlayerSlots.INSTANCE.getInstance();
    }

    /**
     * @return locked container slots, for each container in a world.
     */
    public static LockedContainerSlots lockedContainerSlots() {
        return LockedContainerSlots.INSTANCE.getInstance();
    }
}