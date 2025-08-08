package cy.jdkdigital.camol.event;


import cy.jdkdigital.camol.Camol;
import cy.jdkdigital.camol.client.render.CamoItemRenderer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

@EventBusSubscriber(modid = Camol.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEventHandler
{
    @SubscribeEvent
    public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        event.registerItem(new IClientItemExtensions()
        {
            final BlockEntityWithoutLevelRenderer myRenderer = new CamoItemRenderer();

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return myRenderer;
            }
        }, Camol.CAMO_ITEM.get(), Camol.SOLID_CAMO_ITEM.get());
    }

    @SubscribeEvent
    public static void init(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemProperties.register(Camol.CAMO_ITEM.get(), ResourceLocation.withDefaultNamespace("tuned"), (stack, world, entity, i) -> stack.has(Camol.BLOCK_COMPONENT) ? 1.0F : 0.0F);
            ItemProperties.register(Camol.SOLID_CAMO_ITEM.get(), ResourceLocation.withDefaultNamespace("tuned"), (stack, world, entity, i) -> stack.has(Camol.BLOCK_COMPONENT) ? 1.0F : 0.0F);
        });
    }
}