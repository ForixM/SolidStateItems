package ma.forix.ssi.blocks.blockentities;

import ma.forix.ssi.Registration;
import ma.forix.ssi.blocks.Networkable;
import ma.forix.ssi.items.Drive;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RackBlockEntity extends Networkable {

    private final ItemStackHandler itemHandler = createHandler();

    private ItemStackHandler createHandler() {
        return new ItemStackHandler(8){
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }

            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                return stack.getItem() instanceof Drive;
            }
        };
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
            return handler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void load(CompoundTag tag) {
        if (tag.contains("Inventory")){
            itemHandler.deserializeNBT(tag.getCompound("Inventory"));
        }
        super.load(tag);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        tag.put("Inventory", itemHandler.serializeNBT());
    }

    private final LazyOptional<IItemHandler> handler = LazyOptional.of(() -> itemHandler);

    public RackBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(Registration.RACK_BLOCK_ENTITY.get(), pPos, pBlockState);
    }

    @Override
    public void tickServer(Level level){
        super.tickServer(level);
    }
}
