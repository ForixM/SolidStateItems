package ma.forix.ssi.blocks.blockentities;

import ma.forix.ssi.items.Drive;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RackBlockEntity extends BlockEntity {

    private final ItemStackHandler itemHandler = createHandler();

    private ItemStackHandler createHandler() {
        return new ItemStackHandler(5){
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }

            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                if (slot == 4){
                    return stack.getItem() instanceof Drive;
                } else {
                    if (getStackInSlot(4).isEmpty())
                        return false;
                }
                return true;
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
        super(SsiBlockEntities.RACK_BLOCK_ENTITY.get(), pPos, pBlockState);
    }

    private boolean hasDrive = false;

    public void tickServer(){
        ItemStack drive = itemHandler.getStackInSlot(4);

        if (!drive.isEmpty()){
            if (!hasDrive){
                CompoundTag tag = drive.getOrCreateTag();
                if (tag.contains("Inventory")){
                    itemHandler.deserializeNBT(tag.getCompound("Inventory"));
                }
                for (String key : tag.getAllKeys()) {
                    try {
                        int i = Integer.parseInt(key);
                        ItemStack item = ItemStack.of(tag.getCompound(key));
                        itemHandler.setStackInSlot(i, item);
                    } catch (Exception e){
                        System.err.println("error: "+e.getMessage());
                    }
                }
                hasDrive = true;
            } else {
                CompoundTag tag = new CompoundTag();

                for (int i = 0; i < 4; i++) {
                    ItemStack item = itemHandler.getStackInSlot(i);
                    if (!item.isEmpty()) {
                        tag.put(Integer.toString(i), item.serializeNBT());
                    }
                }
                drive.setTag(tag);
            }
        } else {
            for (int i = 0; i < 4; i++){
                itemHandler.setStackInSlot(i, ItemStack.EMPTY);
            }

            hasDrive = false;
        }
    }
}
