package cy.jdkdigital.camol.event;

import cy.jdkdigital.camol.Camol;
import cy.jdkdigital.camol.network.ClearCamoData;
import cy.jdkdigital.camol.network.SyncChunkCamoData;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = Camol.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ModEventHandler
{
    @SubscribeEvent
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            Camol.ITEMS.getEntries().forEach(itemDeferredHolder -> {
                event.accept(itemDeferredHolder.value());
            });
        }
    }

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(Camol.MODID).versioned("1");
        registrar.playToClient(
                SyncChunkCamoData.TYPE,
                SyncChunkCamoData.STREAM_CODEC,
                new DirectionalPayloadHandler<>(
                        SyncChunkCamoData::clientHandle,
                        SyncChunkCamoData::serverHandle
                )
        );
        registrar.playToClient(
                ClearCamoData.TYPE,
                ClearCamoData.STREAM_CODEC,
                new DirectionalPayloadHandler<>(
                        ClearCamoData::clientHandle,
                        ClearCamoData::serverHandle
                )
        );
    }
}
