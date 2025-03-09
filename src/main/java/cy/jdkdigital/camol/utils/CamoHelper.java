package cy.jdkdigital.camol.utils;

import cy.jdkdigital.camol.Camol;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.Map;

public class CamoHelper
{
    public static Map<String, BlockState> CLIENT_CAMO_MAP = new HashMap<>();

    public static BlockState getClientCamoBlockState(BlockPos pos) {
        String posKey = String.valueOf(pos.asLong());
        return CLIENT_CAMO_MAP.getOrDefault(posKey, Blocks.AIR.defaultBlockState());
    }

    public static BlockState getCamoBlockState(Level level, BlockPos pos) {
        String posKey = String.valueOf(pos.asLong());
        if (level instanceof ClientLevel) {
            return CLIENT_CAMO_MAP.getOrDefault(posKey, Blocks.AIR.defaultBlockState());
        }
        var camoMap = level.getChunkAt(pos).getData(Camol.CAMO_BLOCK_MAP);
        if (camoMap.containsKey(posKey)) {
            return camoMap.get(posKey);
        }
        return null;
    }
}