package ma.forix.ssi.blocks.blockentities;

import ma.forix.ssi.Registration;
import ma.forix.ssi.blocks.CrafterLogic;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class CrafterBlockEntity extends CrafterLogic {
    public CrafterBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(Registration.CRAFTER_BLOCK_ENTITY.get(), pPos, pBlockState);
    }
}
