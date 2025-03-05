package cy.jdkdigital.camol.common.block;

import com.mojang.serialization.MapCodec;
import cy.jdkdigital.camol.common.block.entity.CamoConfiguratorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class CamoConfiguratorBlock extends BaseEntityBlock
{
    public static final MapCodec<CamoConfiguratorBlock> CODEC = simpleCodec(CamoConfiguratorBlock::new);

    public CamoConfiguratorBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new CamoConfiguratorBlockEntity(blockPos, blockState);
    }
}
