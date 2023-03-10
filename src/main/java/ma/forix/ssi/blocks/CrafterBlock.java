package ma.forix.ssi.blocks;

import ma.forix.ssi.blocks.blockentities.CrafterBlockEntity;
import ma.forix.ssi.blocks.blockentities.RackBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import org.jetbrains.annotations.Nullable;

public class CrafterBlock extends NetworkableBlock {
    public CrafterBlock() {
        super();
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new CrafterBlockEntity(pPos, pState);
    }

//    @Nullable
//    @Override
//    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
//        if (level.isClientSide()) {
//            return null;
//        }
//        return (lvl, pos, blockState, t) -> {
//            if (t instanceof CrafterBlockEntity tile){
//                tile.tickServer(level);
//            }
//        };
//    }
}
