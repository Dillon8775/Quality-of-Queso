package net.dillon.qualityofqueso.mixin.fabric;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.function.Consumer;

import static net.dillon.qualityofqueso.helper.GuiHelper.acceptDotTooltip;
import static net.dillon.qualityofqueso.helper.GuiHelper.acceptEnhancedDurabilityTooltips;
import static net.dillon.qualityofqueso.option.OptionInstances.client;

@Mixin(ItemStack.class)
public abstract class FabricItemStackMixin {
    @Shadow
    public abstract boolean isDamaged();

    /**
     * Adds enhanced durability tooltips, as long as {@link TooltipFlag} is not {@link TooltipFlag#ADVANCED}.
     */
    @Inject(method = "addDetailsToTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;get(Lnet/minecraft/core/component/DataComponentType;)Ljava/lang/Object;", ordinal = 2))
    private void addDurabilityTooltips(Item.TooltipContext context, TooltipDisplay display, @Nullable Player player, TooltipFlag tooltipFlag, Consumer<Component> builder, CallbackInfo ci) {
        if (!client().enhancedDurabilityTooltips().enableEnhancedDurabilityTooltips) {
            return;
        }

        ItemStack self = (ItemStack)(Object)this;
        if (tooltipFlag.isAdvanced() && this.isDamaged()) {
            return;
        }

        acceptEnhancedDurabilityTooltips(builder, self);
    }

    /**
     * Adds the colored dot tooltips next to the item name.
     */
    @Redirect(method = "getTooltipLines", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z"))
    private <E> boolean addDot(List<Component> lines, E e) {
        ItemStack self = (ItemStack)(Object)this;
        acceptDotTooltip(lines, self);
        return true;
    }

    /**
     * Gives durability tooltips a nice color.
     */
    @WrapOperation(method = "addDetailsToTooltip", at = @At(value = "INVOKE", target = "Ljava/util/function/Consumer;accept(Ljava/lang/Object;)V", ordinal = 4))
    private <T> void advancedDurabilityTooltips(Consumer<Component> builder, T t, Operation<Void> original) {
        if (!client().enhancedDurabilityTooltips().enableEnhancedDurabilityTooltips) {
            // Call original if disabled
            original.call(builder, t);
        }

        ItemStack self = (ItemStack) (Object) this;
        acceptEnhancedDurabilityTooltips(builder, self);
    }
}