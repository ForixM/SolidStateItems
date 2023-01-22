package ma.forix.ssi.slot;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class CustomSlotItemHandler extends SlotItemHandler {
    public CustomSlotItemHandler(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    @Override
    public void set(@NotNull ItemStack stack) {
        ((IItemHandlerModifiable) this.getItemHandler()).setStackInSlot(getSlotIndex(), stack);
        this.setChanged();
    }

    @Override
    public int getMaxStackSize(@NotNull ItemStack stack) {
        ItemStack maxAdd = stack.copy();
        int maxInput = Integer.MAX_VALUE;
        maxAdd.setCount(maxInput);

        IItemHandler handler = this.getItemHandler();
        ItemStack currentStack = handler.getStackInSlot(getSlotIndex());
        if (handler instanceof IItemHandlerModifiable) {
            IItemHandlerModifiable handlerModifiable = (IItemHandlerModifiable) handler;

            handlerModifiable.setStackInSlot(getSlotIndex(), ItemStack.EMPTY);

            ItemStack remainder = handlerModifiable.insertItem(getSlotIndex(), stack, true);

            handlerModifiable.setStackInSlot(getSlotIndex(), currentStack);

            return maxInput;
        }
        else
        {
            ItemStack remainder = handler.insertItem(getSlotIndex(), maxAdd, true);

            int current = currentStack.getCount();
            int added = maxInput - remainder.getCount();
            return current + added;
        }
    }
}
