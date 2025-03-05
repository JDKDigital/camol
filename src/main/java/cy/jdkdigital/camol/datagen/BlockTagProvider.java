package cy.jdkdigital.camol.datagen;

import cy.jdkdigital.camol.Camol;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class BlockTagProvider extends BlockTagsProvider
{
    public BlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, ExistingFileHelper helper) {
        super(output, provider, Camol.MODID, helper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        var BLACKLIST = tag(Camol.CAMO_BLACKLIST);
    }

    @Override
    public String getName() {
        return "Camol Block Tags Provider";
    }
}
