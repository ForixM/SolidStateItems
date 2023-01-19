package ma.forix.ssi.blocks.blockentities;

import ma.forix.ssi.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

public class TerminalContainer extends AbstractContainerMenu {
    private BlockEntity blockEntity;
    private Player player;
    private IItemHandler playerInventory;

    public TerminalContainer(int windowId, BlockPos pos, Inventory playerInventory, Player player) {
        super(Registration.TERMINAL_CONTAINER.get(), windowId);
        blockEntity = player.getCommandSenderWorld().getBlockEntity(pos);
        this.player = player;
        this.playerInventory = new InvWrapper(playerInventory);

        if (blockEntity != null){
            blockEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(h -> {
                addSlot(new SlotItemHandler(h, 0, 8, 30));
                addSlot(new SlotItemHandler(h, 1, 26, 30));
                addSlot(new SlotItemHandler(h, 2, 8, 48));
                addSlot(new SlotItemHandler(h, 3, 26, 48));
            });
        }
        layoutPlayerInventorySlots(8, 84);
    }

    private void layoutPlayerInventorySlots(int leftCol, int topRow) {
        // Player inventory
        addSlotBox(playerInventory, 9, leftCol, topRow, 9, 18, 3, 18);

        // Hotbar
        topRow += 58;
        addSlotRange(playerInventory, 0, leftCol, topRow, 9, 18);
    }

    private int addSlotRange(IItemHandler handler, int index, int x, int y, int amount, int dx) {
        for (int i = 0 ; i < amount ; i++) {
            addSlot(new SlotItemHandler(handler, index, x, y));
            x += dx;
            index++;
        }
        return index;
    }

    private int addSlotBox(IItemHandler handler, int index, int x, int y, int horAmount, int dx, int verAmount, int dy) {
        for (int j = 0 ; j < verAmount ; j++) {
            index = addSlotRange(handler, index, x, y, horAmount, dx);
            y += dy;
        }
        return index;
    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(pIndex);
        if (slot != null && slot.hasItem()){
            ItemStack stack = slot.getItem();
            itemstack = stack.copy();
            if (pIndex >= 0 && pIndex < 4){
                if (!this.moveItemStackTo(stack, 4, 40, true)){
                    return ItemStack.EMPTY;
                }
            } else {
                if (!this.moveItemStackTo(stack, 0, 4, false)){
                    return ItemStack.EMPTY;
                }
            }

            if (stack.isEmpty()){
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (stack.getCount() == itemstack.getCount()){
                return ItemStack.EMPTY;
            }

            slot.onTake(player, stack);
        }
        return itemstack;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return stillValid(ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos()), player, Registration.TERMINAL_BLOCK.get());
    }
}
