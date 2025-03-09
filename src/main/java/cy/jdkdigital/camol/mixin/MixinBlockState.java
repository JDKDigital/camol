package cy.jdkdigital.camol.mixin;

import cy.jdkdigital.camol.utils.CamoHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
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
        BlockState camoState = CamoHelper.getClientCamoBlockState(pos);
        if (camoState != null && !camoState.isAir()) {
            return camoState.getBlock().getAppearance(camoState, blockGetter, pos, side, queryState, queryPos);
        }
        return getBlock().getAppearance(this.asState(), blockGetter, pos, side, queryState, queryPos);
    }
}
