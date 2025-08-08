package cy.jdkdigital.camol.utils;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.client.model.pipeline.VertexConsumerWrapper;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class CamoHelper
{
    public static Map<String, CamoPosition> CLIENT_CAMO_MAP = new HashMap<>();

    public static CamoPosition getClientCamoBlockState(BlockPos pos) {
        String posKey = String.valueOf(pos.asLong());
        return CLIENT_CAMO_MAP.getOrDefault(posKey, new CamoPosition("normal", Blocks.AIR.defaultBlockState()));
    }

    public static class SemiTransparentVertexConsumer extends VertexConsumerWrapper
    {
        public SemiTransparentVertexConsumer(VertexConsumer consumer) {
            super(consumer);
        }

        @Override
        public @NotNull VertexConsumer setColor(int color) {
            super.setColor(color & 0x80ffffff);
            return this;
        }
    }
}