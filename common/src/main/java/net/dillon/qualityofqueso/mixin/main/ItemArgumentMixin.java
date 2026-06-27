package net.dillon.qualityofqueso.mixin.main;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.dillon.qualityofqueso.main.CommonMain;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.CompletableFuture;

import static net.dillon.qualityofqueso.helper.ModHelper.commonOptionsInstance;

@Mixin(ItemArgument.class)
public class ItemArgumentMixin {

    /**
     * Fixes suggestions bug when typing in speedrunner mod items.
     */
    @Inject(method = "listSuggestions", at = @At("HEAD"), cancellable = true)
    private void fixCommandArgumentSuggestions(CommandContext<SharedSuggestionProvider> context, SuggestionsBuilder builder, CallbackInfoReturnable<CompletableFuture<Suggestions>> cir) {
        if (!commonOptionsInstance().optimizeItemArgument) {
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

    /**
     * Fixes bug where typing in a speedrunner mod item does without the {@code "speedrunnermod:"} namespace, doesn't work.
     */
    @Inject(method = "parse(Lcom/mojang/brigadier/StringReader;)Lnet/minecraft/commands/arguments/item/ItemInput;", at = @At("HEAD"), cancellable = true)
    private void modifyItemParsing(StringReader reader, CallbackInfoReturnable<ItemInput> cir) {
        if (!commonOptionsInstance().optimizeItemArgument) {
            return;
        }

        int cursor = reader.getCursor();
        try {
            Identifier id = Identifier.read(reader); // Read input

            Holder<Item> entry;

            if (BuiltInRegistries.ITEM.containsKey(id)) {
                entry = BuiltInRegistries.ITEM.get(id).orElseThrow();
                cir.setReturnValue(new ItemInput(entry, DataComponentPatch.EMPTY));
            }

            for (int i = 0; i < CommonMain.MOD_IDS.size(); i++) {
                Identifier modId = Identifier.fromNamespaceAndPath(CommonMain.MOD_IDS.stream().toList().get(i), id.getPath());
                if (BuiltInRegistries.ITEM.containsKey(modId)) {
                    entry = BuiltInRegistries.ITEM.get(modId).orElseThrow();
                    cir.setReturnValue(new ItemInput(entry, entry.value().getDefaultInstance().getComponentsPatch()));
                }
            }

        } catch (CommandSyntaxException e) {
            reader.setCursor(cursor); // Reset cursor if failed
        }
    }
}