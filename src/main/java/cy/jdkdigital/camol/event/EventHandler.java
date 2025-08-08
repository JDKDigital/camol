package cy.jdkdigital.camol.event;

import cy.jdkdigital.camol.Camol;
import cy.jdkdigital.camol.common.item.CamoItem;
import cy.jdkdigital.camol.network.ClearCamoData;
import cy.jdkdigital.camol.network.SyncChunkCamoData;
import cy.jdkdigital.camol.utils.CamoPosition;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.ChunkWatchEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.HashMap;
import java.util.Map;

@EventBusSubscriber(modid = Camol.MODID)
public class EventHandler
{
    @SubscribeEvent
    public static void chunkWatch(ChunkWatchEvent.Sent event) {
        var camoMap = new HashMap<>(event.getChunk().getData(Camol.CAMO_BLOCK_MAP));
        // Update new map with old chunk data
        boolean hasUpdate = false;
        for (Map.Entry<String, BlockState> entry : event.getChunk().getData(Camol.OLD_CAMO_BLOCK_MAP).entrySet()) {
            if (!entry.getValue().isAir()) {
                camoMap.put(entry.getKey(), new CamoPosition("normal", entry.getValue()));
            }
            hasUpdate = true;
        }
        if (hasUpdate) {
            event.getChunk().setData(Camol.CAMO_BLOCK_MAP, camoMap);
            event.getChunk().removeData(Camol.OLD_CAMO_BLOCK_MAP);
        }
        PacketDistributor.sendToPlayersTrackingChunk(event.getLevel(), event.getPos(), new SyncChunkCamoData(camoMap, event.getPos().getWorldPosition()));
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
            var camoMap = new HashMap<>(chunk.getData(Camol.CAMO_BLOCK_MAP));
            if (camoMap.containsKey(posKey)) {
                var camoPosition = camoMap.get(posKey);
                if (!camoPosition.state().isAir()) {
                    Block.popResource(serverLevel, event.getPos(), CamoItem.getCamoItem(camoPosition.state(), camoPosition.camoType().equals("solid")));
                }
                camoMap.put(posKey, new CamoPosition("normal", Blocks.AIR.defaultBlockState()));
                chunk.setData(Camol.CAMO_BLOCK_MAP, camoMap);
                PacketDistributor.sendToPlayersTrackingChunk(serverLevel, new ChunkPos(event.getPos()), new SyncChunkCamoData(camoMap, event.getPos()));
            }
        }
    }
}
