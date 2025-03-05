package cy.jdkdigital.camol.utils;

import cy.jdkdigital.camol.Camol;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class CamoHelper
{
    public static BlockState getCamoBlockState(Level level, BlockPos pos) {
        String posKey = String.valueOf(pos.asLong());
        var camoMap = level.getData(Camol.CAMO_BLOCK_MAP);
        if (camoMap.containsKey(posKey)) {
            return camoMap.get(posKey);
        }
        return null;
    }
}