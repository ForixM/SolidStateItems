package ma.forix.ssi.blocks.blockentities;

import ma.forix.ssi.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class CraftOrdererBlockEntity extends NetworkableBE {
    public CraftOrdererBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(Registration.CRAFT_ORDERER_BLOCK_ENTITY.get(), pPos, pBlockState);
    }
}
