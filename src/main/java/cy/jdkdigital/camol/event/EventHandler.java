package cy.jdkdigital.camol.event;

import cy.jdkdigital.camol.Camol;
import cy.jdkdigital.camol.common.item.CamoItem;
import cy.jdkdigital.camol.network.ClearCamoData;
import cy.jdkdigital.camol.network.SyncChunkCamoData;
import cy.jdkdigital.camol.utils.CamoHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityTravelToDimensionEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.ChunkWatchEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.HashMap;

@EventBusSubscriber(modid = Camol.MODID)
public class EventHandler
{
    @SubscribeEvent
    public static void chunkWatch(ChunkWatchEvent.Sent event) {
        PacketDistributor.sendToPlayersTrackingChunk(event.getLevel(), event.getPos(), new SyncChunkCamoData(event.getChunk().getData(Camol.CAMO_BLOCK_MAP), event.getPos().getWorldPosition()));
    }

    @SubscribeEvent
    public static void playerTick(PlayerTickEvent.Post event) {
        if (event.getEntity().level().isClientSide) {
            ClientEventHandler.shouldBeTransparent = event.getEntity().getMainHandItem().is(Camol.CAMO_ITEM) || event.getEntity().getOffhandItem().is(Camol.CAMO_ITEM);
        }
    }

    @SubscribeEvent
    public static void dimensionChange(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            PacketDistributor.sendToPlayer(player, new ClearCamoData("dimension_change"));
        }
    }

    @SubscribeEvent
    public static void blockBreak(BlockEvent.BreakEvent event) {
        if (event.getLevel() instanceof ServerLevel serverLevel) {
            String posKey = String.valueOf(event.getPos().asLong());
            var chunk = serverLevel.getChunkAt(event.getPos());
            var camoData = new HashMap<>(chunk.getData(Camol.CAMO_BLOCK_MAP));
            if (camoData.containsKey(posKey)) {
                var camoState = camoData.get(posKey);
                Block.popResource(serverLevel, event.getPos(), CamoItem.getCamoItem(camoState));
                camoData.remove(posKey);
                chunk.setData(Camol.CAMO_BLOCK_MAP, camoData);
                PacketDistributor.sendToPlayersTrackingChunk(serverLevel, new ChunkPos(event.getPos()), new SyncChunkCamoData(camoData, event.getPos()));
            }
        }
    }
}
