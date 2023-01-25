package ma.forix.ssi.blocks.blockentities;

import ma.forix.ssi.Registration;
import ma.forix.ssi.blocks.Networkable;
import ma.forix.ssi.items.Drive;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class TerminalBlockEntity extends Networkable {
    private final ItemStackHandler itemHandler = createHandler();
    private final LazyOptional<IItemHandler> handler = LazyOptional.of(() -> itemHandler);

    public TerminalBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(Registration.TERMINAL_BLOCK_ENTITY.get(), pPos, pBlockState);
    }

    private ItemStackHandler createHandler() {
        return new ItemStackHandler(16){
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }

            @Override
            protected int getStackLimit(int slot, @NotNull ItemStack stack) {
                return super.getStackLimit(slot, stack);
            }

            private ItemStack mergeOrNull(CompoundTag tag, ItemStack stack){
                for (String key : tag.getAllKeys()) {
                    System.out.println("key: "+key+", stack: "+stack);
                    CompoundTag itemTag = tag.getCompound(key);
                    ItemStack istack = getConvertibleTag(key, itemTag);
                    if (istack.sameItem(stack)){
                        int prevCount = itemTag.getInt("count");
                        itemTag.putInt("count", prevCount+stack.getCount());
//                        istack.grow(stack.getCount());
//                        tag.put(key, istack.serializeNBT());
                        return istack;
                    }
                }
                return null;
            }

            @SuppressWarnings("removal")
            @Override
            public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
                System.out.println("item inserted, slot=" + slot + ", stack=" + stack + ", simulate=" + simulate + ", client side: "+level.isClientSide());
                if (!level.isClientSide()) //slot, stack, simulate=true
                {
                    List<RackBlockEntity> racks = network.getType(RackBlockEntity.class);
                    AtomicBoolean workDone = new AtomicBoolean(false);
                    if (!racks.isEmpty()){
                        for (RackBlockEntity rack : racks) {
                            rack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent((h) -> {
                                for (int i = 0; i < h.getSlots(); i++) {
                                    ItemStack drive = h.getStackInSlot(i);
                                    if (!drive.isEmpty()) {
                                        CompoundTag tag = drive.getOrCreateTag();
                                        Set<String> keys = tag.getAllKeys();
                                        ItemStack merged = mergeOrNull(tag, stack);
                                        if (keys.size() < Drive.CAPACITY) {
                                            if (merged == null) {
                                                int index = 0;
                                                while (tag.contains(Integer.toString(index))) {
                                                    index++;
                                                }
                                                ResourceLocation res = Registry.ITEM.getKey(stack.getItem());
                                                CompoundTag itemTag = new CompoundTag();
                                                int prevCount = 0;
                                                if (tag.contains(res.toString())) {
                                                    CompoundTag subTag = tag.getCompound(res.toString());
                                                    prevCount = subTag.getInt("count");
                                                }
                                                itemTag.putInt("count", stack.getCount() + prevCount);
                                                if (stack.getTag() != null)
                                                    itemTag.put("tag", stack.getTag());
                                                tag.put(res.toString(), itemTag);
                                                System.out.println("written tag: " + tag);
                                            }
                                            workDone.set(true);
                                            break;
                                        }
                                    }
                                }
                            });
                            if (workDone.get())
                                break;
                        }
                    }
                }
                //Base code slightly modified
                if (stack.isEmpty())
                    return ItemStack.EMPTY;

                if (!isItemValid(slot, stack))
                    return stack;

                validateSlotIndex(slot);

                ItemStack existing = this.stacks.get(slot);

                if (!existing.isEmpty()){
                    if (!ItemHandlerHelper.canItemStacksStack(stack, existing))
                        return stack;
                }

                if (!simulate){
                    if (existing.isEmpty()){
                        this.stacks.set(slot, stack);
                    } else {
                        existing.grow(stack.getCount());
                    }
                    onContentsChanged(slot);
                }
                return ItemStack.EMPTY;

//                return super.insertItem(slot, stack, simulate);
            }

            @SuppressWarnings("removal")
            @Override
            public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
                if (!level.isClientSide() && !simulate) //slot, amount extracted, simulate=false
                {
                    System.out.println("Item extracted, slot=" + slot + ", amout=" + amount + ", simulate=" + simulate);
                    List<RackBlockEntity> racks = network.getType(RackBlockEntity.class);
                    if (!racks.isEmpty()){
                        for (RackBlockEntity rack : racks) {
                            rack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent((h) -> {
                                for (int i = 0; i < h.getSlots();i++){
                                    ItemStack drive = h.getStackInSlot(i);
                                    if (!drive.isEmpty()) {
                                        CompoundTag tag = drive.getOrCreateTag();
                                        ItemStack toExtract = getStackInSlot(slot);
                                        int am = Math.min(amount, toExtract.getItem().getMaxStackSize());
                                        String key = Registry.ITEM.getKey(toExtract.getItem()).toString();
                                        if (tag.contains(key)) {
                                            CompoundTag itemTag = (CompoundTag) tag.get(key);
                                            int savedCount = itemTag.getInt("count");
                                            if (savedCount <= am) {
                                                tag.remove(key);
                                            } else {
                                                itemTag.putInt("count", (savedCount - am));
                                                tag.put(key, itemTag);
                                            }
                                            break;
                                        }
                                    }
                                }
                            });
                        }
                    }
                }
                if (slot < getSlots())
                    return super.extractItem(slot, amount, simulate);
                else
                    return ItemStack.EMPTY;
            }

            @SuppressWarnings("removal")
            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                if (!level.isClientSide()) {
                    List<RackBlockEntity> racks = network.getType(RackBlockEntity.class);
                    AtomicBoolean returnValue = new AtomicBoolean(false);
                    if (!racks.isEmpty()){
                        for (RackBlockEntity rack : racks) {
                            rack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent((h) -> {
                                for (int i = 0; i < h.getSlots(); i++){
                                    ItemStack drive = h.getStackInSlot(i);
                                    if (!drive.isEmpty()){
                                        CompoundTag tag = drive.getOrCreateTag();
                                        Set<String> keys = tag.getAllKeys();
                                        if (keys.size() < Drive.CAPACITY){
                                            returnValue.set(true);
                                            break;
                                        } else {
                                            for (String key : keys) {
                                                if (key.equals(Registry.ITEM.getKey(stack.getItem()).toString())){
                                                    returnValue.set(true);
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }
                            });
                        }
                    }
                    return returnValue.get();
                }
                return super.isItemValid(slot, stack);
            }
        };
    }

    @SuppressWarnings("removal")
    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
            return handler.cast();
        }
        return super.getCapability(cap, side);
    }

    private ItemStack getConvertibleTag(String id, CompoundTag tag){
        CompoundTag converted = new CompoundTag();
        converted.putString("id", id);
        converted.putByte("Count", (byte)1);
        if (tag.contains("tag")){
            converted.put("tag", tag.get("tag"));
        }

        ItemStack stack = ItemStack.of(converted);
        stack.setCount(tag.getInt("count"));
        return stack;
    }

    @SuppressWarnings("removal")
    @Override
    public void tickServer(Level level) {
        super.tickServer(level);
        List<RackBlockEntity> racks = network.getType(RackBlockEntity.class);
        if (!racks.isEmpty()){
            AtomicInteger counter = new AtomicInteger();
            for (RackBlockEntity rack : racks) {
                rack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent((h) -> {
                    List<ItemStack> items = new ArrayList<>();
                    boolean foundDisk = false;
                    for (int slot = 0; slot < h.getSlots(); slot++){
                        ItemStack drive = h.getStackInSlot(slot);
                        if (!drive.isEmpty()){
                            foundDisk = true;
                            CompoundTag tag = drive.getOrCreateTag();
                            for (String key : tag.getAllKeys()) {
                                ItemStack stack = getConvertibleTag(key, tag.getCompound(key));
                                int i;
                                for (i = 0; i < items.size(); i++) {
                                    if (items.get(i).sameItem(stack)){
                                        items.get(i).grow(stack.getCount());
                                        break;
                                    }
                                }
                                if (i == items.size()){
                                    items.add(stack);
                                }
                            }
                        }
                    }
                    if (!foundDisk){
                        for (int i = 0; i < itemHandler.getSlots(); i++){
                            itemHandler.setStackInSlot(i, ItemStack.EMPTY);
                        }
                    } else {
                        for (int a = counter.get(); a < itemHandler.getSlots(); a++){
                            itemHandler.setStackInSlot(a, ItemStack.EMPTY);
                        }
                        for (ItemStack item : items) {
                            itemHandler.setStackInSlot(counter.getAndIncrement(), item);
                        }
                    }
                });
            }
        } else {
            for (int i = 0; i < itemHandler.getSlots(); i++){
                itemHandler.setStackInSlot(i, ItemStack.EMPTY);
            }
        }
    }
}
