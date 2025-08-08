package cy.jdkdigital.camol.common.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cy.jdkdigital.camol.Camol;
import cy.jdkdigital.camol.common.item.CamoItem;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class SimpleCamoCraftingRecipe implements CraftingRecipe
{
    private final ItemStack camoItem;

    public SimpleCamoCraftingRecipe(ItemStack camoItem) {
        this.camoItem = camoItem;
    }

    @Override
    public CraftingBookCategory category() {
        return CraftingBookCategory.MISC;
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        // only 2 items, 1 is the camo item and one is a block item
        return input.ingredientCount() == 2 &&
                input.items().stream().filter(itemStack -> itemStack.is(this.camoItem.getItem())).toList().size() == 1 &&
                input.items().stream().filter(itemStack -> !itemStack.is(Camol.CRAFTING_BLACKLIST) && itemStack.getItem() instanceof BlockItem).toList().size() == 1;
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        var camoItem = input.items().stream().filter(itemStack -> !itemStack.is(this.camoItem.getItem())).toList().getFirst();
        if (camoItem.getItem() instanceof BlockItem blockItem) {
            return CamoItem.getCamoItem(blockItem.getBlock().defaultBlockState(), this.camoItem.copy());
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return this.camoItem.copy();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Camol.SIMPLE_CAMO_CRAFTING.get();
    }

    public static class Serializer implements RecipeSerializer<SimpleCamoCraftingRecipe>
    {
        private static final MapCodec<SimpleCamoCraftingRecipe> CODEC = RecordCodecBuilder.mapCodec(
                builder -> builder.group(
                                ItemStack.CODEC.fieldOf("camo_item").forGetter(recipe -> recipe.camoItem)
                        )
                        .apply(builder, SimpleCamoCraftingRecipe::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, SimpleCamoCraftingRecipe> STREAM_CODEC = StreamCodec.of(
                SimpleCamoCraftingRecipe.Serializer::toNetwork, SimpleCamoCraftingRecipe.Serializer::fromNetwork
        );

        @Override
        public MapCodec<SimpleCamoCraftingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, SimpleCamoCraftingRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        public static SimpleCamoCraftingRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
            return new SimpleCamoCraftingRecipe(ItemStack.STREAM_CODEC.decode(buffer));
        }

        public static void toNetwork(RegistryFriendlyByteBuf buffer, SimpleCamoCraftingRecipe recipe) {
            ItemStack.STREAM_CODEC.encode(buffer, recipe.camoItem);
        }
    }
}
