package cy.jdkdigital.camol.common.block.entity;

import cy.jdkdigital.camol.Camol;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class CamoConfiguratorBlockEntity extends BlockEntity
{
    public CamoConfiguratorBlockEntity(BlockPos pos, BlockState blockState) {
        super(Camol.CAMO_CONFIGURATOR_BLOCK_ENTITY.get(), pos, blockState);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
    }
}
