package net.dillon.qualityofqueso.mixin.main;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.dillon.dillonlib.annotation.Dill;
import net.dillon.dillonlib.annotation.DillEnv;
import net.dillon.dillonlib.mixinplugin.Predicated;
import net.dillon.qualityofqueso.main.CommonMain;
import net.dillon.qualityofqueso.util.CustomItemGroup;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.commands.arguments.item.ItemParser;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.dillon.qualityofqueso.option.CommonOptionInstances.common;

@Predicated
@Dill(env = DillEnv.COMMON)
@Mixin(ItemParser.class)
public class ItemParserMixin {

    /**
     * Simplifies item argument parsing.
     */
    @Inject(method = "parse(Lcom/mojang/brigadier/StringReader;)Lnet/minecraft/commands/arguments/item/ItemInput;", at = @At("HEAD"), cancellable = true)
    private void modifyItemParsing(StringReader reader, CallbackInfoReturnable<ItemInput> cir) {
        if (!common().enableMod || CustomItemGroup.PARSING_CUSTOM_ITEM_GROUPS || !common().optimizeItemArgument) {
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