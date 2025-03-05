package cy.jdkdigital.camol.common.item;

import cy.jdkdigital.camol.Camol;
import cy.jdkdigital.camol.network.SyncChunkCamoData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.HashMap;

public class CamoItem extends Item
{
    public CamoItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (context.getItemInHand().has(Camol.BLOCK_COMPONENT) && context.getLevel() instanceof ServerLevel serverLevel && !serverLevel.getBlockState(context.getClickedPos()).is(Camol.CAMO_BLACKLIST)) {
            String posKey = String.valueOf(context.getClickedPos().asLong());
            var chunk = serverLevel.getChunkAt(context.getClickedPos());
            var camoData = new HashMap<>(chunk.getData(Camol.CAMO_BLOCK_MAP));
            if (camoData.containsKey(posKey)) {
                var camoState = camoData.get(posKey);
                Block.popResourceFromFace(serverLevel, context.getClickedPos(), context.getClickedFace(), getCamoItem(camoState));
                camoData.remove(posKey);
            } else {
                var camoState = context.getItemInHand().get(Camol.BLOCK_COMPONENT);
                if (camoState != null) {
                    var placeContext = new BlockPlaceContext(serverLevel, context.getPlayer(), context.getHand(), camoState.getBlock().asItem().getDefaultInstance(), new BlockHitResult(context.getClickLocation(), context.getPlayer().getDirection(), context.getClickedPos().relative(context.getPlayer().getDirection()), false));
                    var placedState = camoState.getBlock().getStateForPlacement(placeContext);
                    camoData.put(posKey, placedState);
                    if (context.getPlayer() == null || !context.getPlayer().hasInfiniteMaterials()) {
                        context.getItemInHand().shrink(1);
                    }
                }
            }
            chunk.setData(Camol.CAMO_BLOCK_MAP, camoData);
            PacketDistributor.sendToPlayersTrackingChunk(serverLevel, new ChunkPos(context.getClickedPos()), new SyncChunkCamoData(camoData, context.getClickedPos()));
        }
        return InteractionResult.sidedSuccess(context.getLevel().isClientSide);
    }

    public static ItemStack getCamoItem(BlockState state) {
        var stack = new ItemStack(Camol.CAMO_ITEM.get());
        stack.set(Camol.BLOCK_COMPONENT, state.getBlock().defaultBlockState());
        return stack;
    }
}
