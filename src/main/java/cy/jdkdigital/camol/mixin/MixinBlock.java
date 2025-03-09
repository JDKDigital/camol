package cy.jdkdigital.camol.mixin;

import cy.jdkdigital.camol.utils.CamoHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Block.class)
public abstract class MixinBlock
{
    @ModifyVariable(
            method = "shouldRenderFace(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;Lnet/minecraft/core/BlockPos;)Z",
            at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/level/BlockGetter;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"), index = 5
    )
    private static BlockState shouldRenderCamoFace(BlockState value, BlockState state, BlockGetter level, BlockPos pos, Direction side, BlockPos sidePos) {
        if (pos != null && side != null) {
            var camoState = CamoHelper.getClientCamoBlockState(pos.relative(side));
            if (camoState != null && !camoState.isAir()) {
                return camoState;
            }
        }
        return value;
    }
}