package net.dillon.qualityofqueso.widget.management;

/**
 * A management button that swaps inventories with corresponding slots.
 */
public class SwapButton extends ManagementButton {
    private static final int DEFAULT_SWAP_COOLDOWN = 20;
    public static int SWAP_COOLDOWN = DEFAULT_SWAP_COOLDOWN;

    public SwapButton(ManagementParams params) {
        super(
                params,
                "swap",
                NO_APPENDED_TOOLTIP,
                false,
                b -> params.managementEvents().transferEvents().trySwap()
        );
    }

    /**
     * Resets the swapping cooldown.
     */
    public static void resetCooldown() {
        SWAP_COOLDOWN = DEFAULT_SWAP_COOLDOWN;
    }

    @Override
    protected boolean canBeActive() {
        return managementEvents.transferEvents().canSwap();
    }
}