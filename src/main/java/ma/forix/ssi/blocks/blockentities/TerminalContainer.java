package ma.forix.ssi.blocks.blockentities;

import ma.forix.ssi.Registration;
import ma.forix.ssi.slot.CustomSlotItemHandler;
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

import java.util.concurrent.atomic.AtomicInteger;

public class TerminalContainer extends AbstractContainerMenu {
    private BlockEntity blockEntity;
    private Player player;
    private IItemHandler playerInventory;

    @SuppressWarnings("removal")
    public TerminalContainer(int windowId, BlockPos pos, Inventory playerInventory, Player player) {
        super(Registration.TERMINAL_CONTAINER.get(), windowId);
        blockEntity = player.getCommandSenderWorld().getBlockEntity(pos);
        this.player = player;
        this.playerInventory = new InvWrapper(playerInventory);

        if (blockEntity != null){
            blockEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(h -> {
                int x = 8;
                int y = 30;
                for (int j = 0; j < 2; j++) {
                    for (int i = 0; i < 8; i++) {
                        addSlot(new CustomSlotItemHandler(h, 8*j+i, x, y));
                        x+=18;
                    }
                    x = 8;
                    y+=18;
                }
//                addSlot(new CustomSlotItemHandler(h, 0, 8, 30));
//                addSlot(new CustomSlotItemHandler(h, 1, 26, 30));
//                addSlot(new CustomSlotItemHandler(h, 2, 8, 48));
//                addSlot(new CustomSlotItemHandler(h, 3, 26, 48));
            });
        }
        layoutPlayerInventorySlots(8, 84);
    }

    public BlockEntity getBlockEntity() {
        return blockEntity;
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
    protected boolean moveItemStackTo(ItemStack pStack, int pStartIndex, int pEndIndex, boolean pReverseDirection) {
        boolean flag = false;
        int i = pStartIndex;
        if (pReverseDirection) {
            i = pEndIndex - 1;
        }

        if (pStack.isStackable()) {
            while(!pStack.isEmpty()) {
                if (pReverseDirection) {
                    if (i < pStartIndex) {
                        break;
                    }
                } else if (i >= pEndIndex) {
                    break;
                }

                Slot slot = this.slots.get(i);
                ItemStack itemstack = slot.getItem();
                if (!itemstack.isEmpty() && ItemStack.isSameItemSameTags(pStack, itemstack)) {
                    int j = itemstack.getCount() + pStack.getCount();
                    int maxSize = Math.min(slot.getMaxStackSize(), pStack.getMaxStackSize());
                    AtomicInteger slots = new AtomicInteger();
                    blockEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent((h) -> {
                        slots.set(h.getSlots());
                    });
                    if (j <= maxSize || pEndIndex <= slots.get()) {
                        ItemStack itemcopy = pStack.copy();
                        if (blockEntity instanceof TerminalBlockEntity be){
                            int finalI = i;
                            be.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent((h) -> {
                                if (pStartIndex != h.getSlots() && !player.level.isClientSide())
                                    h.insertItem(finalI, itemcopy, true);
                            });
                        }
                        System.out.println("1");
                        pStack.setCount(0);
                        itemstack.setCount(j);
                        slot.setChanged();
                        flag = true;

                    } else {
                        ItemStack itemcopy = pStack.copy();
                        if (blockEntity instanceof TerminalBlockEntity be){
                            int finalI1 = i;
                            be.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent((h) -> {
                                if (pStartIndex != h.getSlots() && !player.level.isClientSide())
                                    h.insertItem(finalI1, itemcopy, true);
                            });
                        }
                        System.out.println("2");
                        pStack.shrink(maxSize - itemstack.getCount());
                        itemstack.setCount(maxSize);
                        slot.setChanged();
                        flag = true;
                    }
                }

                if (pReverseDirection) {
                    --i;
                } else {
                    ++i;
                }
            }
        }

        if (!pStack.isEmpty()) {
            if (pReverseDirection) {
                i = pEndIndex - 1;
            } else {
                i = pStartIndex;
            }

            while(true) {
                if (pReverseDirection) {
                    if (i < pStartIndex) {
                        break;
                    }
                } else if (i >= pEndIndex) {
                    break;
                }

                Slot slot1 = this.slots.get(i);
                ItemStack itemstack1 = slot1.getItem();
                if (itemstack1.isEmpty() && slot1.mayPlace(pStack)) {
                    ItemStack itemcopy = pStack.copy();
                    if (blockEntity instanceof TerminalBlockEntity be){
                        int finalI1 = i;
                        be.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent((h) -> {
                            if (pStartIndex != h.getSlots() && !player.level.isClientSide())
                                h.insertItem(finalI1, itemcopy, true);
                        });
                    }
                    System.out.println("3");
                    if (pStack.getCount() > slot1.getMaxStackSize()) {
                        slot1.set(pStack.split(slot1.getMaxStackSize()));
                    } else {
                        slot1.set(pStack.split(pStack.getCount()));
                    }

                    slot1.setChanged();
                    flag = true;
                    break;
                }

                if (pReverseDirection) {
                    --i;
                } else {
                    ++i;
                }
            }
        }

        return flag;
    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(pIndex);
        AtomicInteger storageSlots = new AtomicInteger();
        blockEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent((h) -> {
            storageSlots.set(h.getSlots());
        });
        if (slot != null && slot.hasItem()){
            ItemStack stack = slot.getItem();
            itemstack = stack.copy();
            if (pIndex >= 0 && pIndex < storageSlots.get()){
                ItemStack stackCopy = stack.copy();
                int extractCount = stackCopy.getItem().getMaxStackSize();
                blockEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent((h) -> {
                    System.out.println("quickstack extracting, count: "+stackCopy.getItem().getMaxStackSize());
                    h.extractItem(pIndex, stackCopy.getItem().getMaxStackSize(), false);
                });
                if (!this.moveItemStackTo(stack, storageSlots.get(), storageSlots.get()+36, true)){
                    return ItemStack.EMPTY;
                }
                return ItemStack.EMPTY;
            } else {
                if (!this.moveItemStackTo(stack, 0, storageSlots.get(), false)){
                    return ItemStack.EMPTY;
                }
                return ItemStack.EMPTY;
            }

//            if (stack.isEmpty()){
//                slot.set(ItemStack.EMPTY);
//            } else {
//                slot.setChanged();
//            }
//
//            if (stack.getCount() == itemstack.getCount()){
//                return ItemStack.EMPTY;
//            }
//
//            slot.onTake(player, stack);
        }
        return itemstack;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return stillValid(ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos()), player, Registration.TERMINAL_BLOCK.get());
    }
}
