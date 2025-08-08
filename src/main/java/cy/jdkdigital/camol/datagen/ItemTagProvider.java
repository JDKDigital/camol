package cy.jdkdigital.camol.datagen;

import cy.jdkdigital.camol.Camol;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class ItemTagProvider extends ItemTagsProvider
{
    public ItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> future, CompletableFuture<TagLookup<Block>> provider, ExistingFileHelper helper) {
        super(output, future, provider, Camol.MODID, helper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(Camol.CRAFTING_BLACKLIST).addTag(ItemTags.DOORS).addTag(ItemTags.BEDS);
    }

    @Override
    public String getName() {
        return "Camol Item Tags Provider";
    }
}
