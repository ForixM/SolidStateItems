package ma.forix.ssi.blocks.blockentities;

import ma.forix.ssi.Registration;
import ma.forix.ssi.blocks.Networkable;
import ma.forix.ssi.items.Drive;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
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

    private boolean hasDrive = false;

    @Override
    public void tickServer(Level level){
        super.tickServer(level);
//        ItemStack drive = itemHandler.getStackInSlot(4);
//
//        if (!drive.isEmpty()){
//            processDrive(drive);
//        } else {
//            for (int i = 0; i < 4; i++){
//                itemHandler.setStackInSlot(i, ItemStack.EMPTY);
//            }
//            hasDrive = false;
//        }
    }

    private void processDrive(ItemStack drive){
//        if (!hasDrive){
//            CompoundTag tag = drive.getOrCreateTag();
//            for (String key : tag.getAllKeys()) {
//                try {
//                    int i = Integer.parseInt(key);
//                    ItemStack item = ItemStack.of(tag.getCompound(key));
//                    itemHandler.setStackInSlot(i, item);
//                } catch (Exception e){
//                    System.err.println("error: "+e.getMessage());
//                }
//            }
//            hasDrive = true;
//        } else {
//            CompoundTag tag = new CompoundTag();
//
//            for (int i = 0; i < 4; i++) {
//                ItemStack item = itemHandler.getStackInSlot(i);
//                if (!item.isEmpty()) {
//                    tag.put(Integer.toString(i), item.serializeNBT());
//                }
//            }
//            drive.setTag(tag);
//        }
    }
}
