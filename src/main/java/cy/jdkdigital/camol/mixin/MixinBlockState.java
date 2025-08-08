package cy.jdkdigital.camol.mixin;

import cy.jdkdigital.camol.utils.CamoHelper;
import cy.jdkdigital.camol.utils.CamoPosition;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.extensions.IBlockStateExtension;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BlockState.class)
public abstract class MixinBlockState extends BlockBehaviour.BlockStateBase implements IBlockStateExtension
{
    private MixinBlockState() {
        super(null, null, null);
    }

    @Override
    public BlockState getAppearance(BlockAndTintGetter blockGetter, BlockPos pos, Direction side, @Nullable BlockState queryState, @Nullable BlockPos queryPos) {
        BlockState camoState = CamoHelper.getClientCamoBlockState(pos).state();
        if (camoState != null && !camoState.isAir()) {
            return camoState.getBlock().getAppearance(camoState, blockGetter, pos, side, queryState, queryPos);
        }
        return IBlockStateExtension.super.getAppearance(blockGetter, pos, side, queryState, queryPos);
    }

    @Override
    public VoxelShape getCollisionShape(BlockGetter level, BlockPos pos, CollisionContext context) {
        CamoPosition camo = CamoHelper.getClientCamoBlockState(pos);
        BlockState camoState = camo.state();
        if (camo.camoType().equals("solid") && camoState != null && !camoState.isAir()) {
            return camoState.getBlock().getCollisionShape(camoState, level, pos, context);
        }
        return super.getCollisionShape(level, pos, context);
    }
}
