package cy.jdkdigital.camol.datagen;

import cy.jdkdigital.camol.Camol;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = Camol.MODID, bus = EventBusSubscriber.Bus.MOD)
public class DataProvider
{
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        if (event.getModContainer().getModId().equals(Camol.MODID)) {
            Data.gatherData(event);
        }
    }

    static class Data
    {
        private static void gatherData(GatherDataEvent event) {
            DataGenerator gen = event.getGenerator();
            PackOutput output = event.getGenerator().getPackOutput();
            CompletableFuture<HolderLookup.Provider> provider = event.getLookupProvider();
            ExistingFileHelper helper = event.getExistingFileHelper();

//            gen.addProvider(event.includeClient(), new LanguageProvider(output, "en_us"));

//            gen.addProvider(event.includeServer(), new RecipeProvider(output, provider));

            BlockTagProvider blockTags = new BlockTagProvider(output, provider, helper);
            gen.addProvider(event.includeServer(), blockTags);
            gen.addProvider(event.includeServer(), new ItemTagProvider(output, provider, blockTags.contentsGetter(), helper));
        }
    }
}
