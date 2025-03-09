package cy.jdkdigital.camol.common.recipe;

import cy.jdkdigital.camol.Camol;
import cy.jdkdigital.camol.common.item.CamoItem;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class SimpleCamoCraftingRecipe implements CraftingRecipe
{
    public SimpleCamoCraftingRecipe(CraftingBookCategory category) {
    }

    @Override
    public CraftingBookCategory category() {
        return CraftingBookCategory.MISC;
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        // only 2 items, 1 is the camo item and one is a block item
        return input.ingredientCount() == 2 &&
                input.items().stream().filter(itemStack -> itemStack.is(Camol.CAMO_ITEM)).toList().size() == 1 &&
                input.items().stream().filter(itemStack -> !itemStack.is(Camol.CRAFTING_BLACKLIST) && itemStack.getItem() instanceof BlockItem).toList().size() == 1;
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        var camoItem = input.items().stream().filter(itemStack -> !itemStack.is(Camol.CAMO_ITEM)).toList().getFirst();
        if (camoItem.getItem() instanceof BlockItem blockItem) {
            return CamoItem.getCamoItem(blockItem.getBlock().defaultBlockState());
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return Camol.CAMO_ITEM.asItem().getDefaultInstance();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Camol.SIMPLE_CAMO_CRAFTING.get();
    }
}
