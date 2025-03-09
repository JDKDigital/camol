package cy.jdkdigital.camol.datagen;

import cy.jdkdigital.camol.Camol;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;

import java.util.concurrent.CompletableFuture;

public class RecipeProvider extends net.minecraft.data.recipes.RecipeProvider implements IConditionBuilder
{
    public RecipeProvider(PackOutput gen, CompletableFuture<HolderLookup.Provider> pRegistries) {
        super(gen, pRegistries);
    }

    @Override
    protected void buildRecipes(RecipeOutput pRecipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Camol.CAMO_ITEM, 4)
                .unlockedBy("has_dye", has(Tags.Items.DYES))
                .pattern("R G").pattern(" B ").pattern("W K")
                .define('R', Ingredient.of(Tags.Items.DYES_RED))
                .define('G', Ingredient.of(Tags.Items.DYES_GREEN))
                .define('B', Ingredient.of(Tags.Items.DYES_BLUE))
                .define('W', Ingredient.of(Tags.Items.DYES_BLACK))
                .define('K', Ingredient.of(Tags.Items.DYES_WHITE))
                .save(pRecipeOutput, ResourceLocation.fromNamespaceAndPath(Camol.MODID, "camo_item"));
    }

    private static ResourceLocation prefixedRecipeId(ItemLike item, String prefix) {
        return BuiltInRegistries.ITEM.getKey(item.asItem()).withPath(path -> prefix + path);
    }
}
