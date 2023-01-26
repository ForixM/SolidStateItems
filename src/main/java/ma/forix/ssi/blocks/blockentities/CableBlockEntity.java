package ma.forix.ssi.blocks.blockentities;

import ma.forix.ssi.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class CableBlockEntity extends NetworkableBE {
    public CableBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(Registration.CABLE_BLOCK_ENTITY.get(), pPos, pBlockState);
    }
}
