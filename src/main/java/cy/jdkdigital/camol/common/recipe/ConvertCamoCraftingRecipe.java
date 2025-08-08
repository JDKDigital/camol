package cy.jdkdigital.camol.common.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cy.jdkdigital.camol.Camol;
import cy.jdkdigital.camol.common.item.CamoItem;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class ConvertCamoCraftingRecipe implements CraftingRecipe
{
    private final ItemStack camoInput;
    private final ItemStack camoOutput;

    public ConvertCamoCraftingRecipe(ItemStack camoInput, ItemStack camoOutput) {
        this.camoInput = camoInput;
        this.camoOutput = camoOutput;
    }

    @Override
    public CraftingBookCategory category() {
        return CraftingBookCategory.MISC;
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        // only 1 item, the camo item input
        return input.ingredientCount() == 1 &&
                input.items().stream().filter(itemStack -> itemStack.is(this.camoInput.getItem())).toList().size() == 1;
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        var camoItem = input.items().stream().filter(itemStack -> itemStack.is(this.camoInput.getItem())).toList().getFirst();
        if (camoItem.has(Camol.BLOCK_COMPONENT)) {
            return CamoItem.getCamoItem(camoItem.get(Camol.BLOCK_COMPONENT), this.camoOutput.copy());
        }
        return this.camoOutput.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 1;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return this.camoOutput.copy();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Camol.CONVERT_CAMO_CRAFTING.get();
    }

    public static class Serializer implements RecipeSerializer<ConvertCamoCraftingRecipe>
    {
        private static final MapCodec<ConvertCamoCraftingRecipe> CODEC = RecordCodecBuilder.mapCodec(
                builder -> builder.group(
                                ItemStack.CODEC.fieldOf("camo_input").forGetter(recipe -> recipe.camoInput),
                                ItemStack.CODEC.fieldOf("camo_output").forGetter(recipe -> recipe.camoOutput)
                        )
                        .apply(builder, ConvertCamoCraftingRecipe::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, ConvertCamoCraftingRecipe> STREAM_CODEC = StreamCodec.of(
                ConvertCamoCraftingRecipe.Serializer::toNetwork, ConvertCamoCraftingRecipe.Serializer::fromNetwork
        );

        @Override
        public MapCodec<ConvertCamoCraftingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ConvertCamoCraftingRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        public static ConvertCamoCraftingRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
            return new ConvertCamoCraftingRecipe(ItemStack.STREAM_CODEC.decode(buffer), ItemStack.STREAM_CODEC.decode(buffer));
        }

        public static void toNetwork(RegistryFriendlyByteBuf buffer, ConvertCamoCraftingRecipe recipe) {
            ItemStack.STREAM_CODEC.encode(buffer, recipe.camoInput);
            ItemStack.STREAM_CODEC.encode(buffer, recipe.camoOutput);
        }
    }
}
