package ma.forix.ssi.blocks;

import ma.forix.ssi.blocks.blockentities.CraftOrdererBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import org.jetbrains.annotations.Nullable;

public class CraftOrderer extends NetworkableBlock {
    public CraftOrderer() {
        super();
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new CraftOrdererBlockEntity(pPos, pState);
    }
}
