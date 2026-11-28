package net.dillon.qualityofqueso.mixin.main;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.dillon.dillonlib.annotation.Dill;
import net.dillon.dillonlib.annotation.DillEnv;
import net.dillon.dillonlib.mixinplugin.Predicated;
import net.dillon.qualityofqueso.util.CustomItemGroup;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.commands.suggestions.SuggestionContext;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.CompletableFuture;

import static net.dillon.qualityofqueso.option.CommonOptionInstances.common;

@Predicated
@Dill(env = DillEnv.COMMON)
@Mixin(ItemArgument.class)
public class ItemArgumentMixin {

    /**
     * Simplifies item suggestions.
     */
    @Inject(method = "listSuggestions", at = @At("HEAD"), cancellable = true)
    private void fixCommandArgumentSuggestions(CommandContext<? extends SuggestionContext> context, SuggestionsBuilder builder, CallbackInfoReturnable<CompletableFuture<Suggestions>> cir) {
        if (!common().enableMod || CustomItemGroup.PARSING_CUSTOM_ITEM_GROUPS || !common().optimizeItemArgument) {
            return;
        }

        String input = builder.getRemaining();
        boolean hasNamespace = input.contains(":");
        boolean foundAny = false;

        // Optimized argument
        for (Identifier id : BuiltInRegistries.ITEM.keySet()) {
            if (hasNamespace) {
                // Full namespaced search
                if (id.toString().contains(input)) {
                    builder.suggest(id.toString());
                    foundAny = true;
                }
            } else {
                // Shorthand search (no namespace)
                if (id.getPath().contains(input)) {
                    builder.suggest(id.getPath());
                    foundAny = true;
                }
            }
        }

        // Default fallback
        if (!foundAny) {
            for (Identifier id : BuiltInRegistries.ITEM.keySet()) {
                if (hasNamespace) {
                    // User typed namespace, but nothing matched = fallback to prefix suggestions
                    if (id.getNamespace().contains(input)) {
                        builder.suggest(id.toString());
                    }
                } else {
                    // User typed only a partial name = suggest full fallback with namespace
                    builder.suggest(id.toString());
                }
            }
        }

        // Set return value
        cir.setReturnValue(builder.buildFuture());
    }
}