package ma.forix.ssi.mixin;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import ma.forix.ssi.blocks.blockentities.TerminalBlockEntity;
import ma.forix.ssi.blocks.blockentities.TerminalContainer;
import net.minecraft.network.TickablePacketListener;
import net.minecraft.network.protocol.PacketUtils;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.network.protocol.game.ServerboundContainerClickPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.network.ServerPlayerConnection;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class MixinServerGamePacketListenerImpl implements ServerPlayerConnection, TickablePacketListener, ServerGamePacketListener {

    @Shadow public ServerPlayer player;

    @Shadow @Final private static Logger LOGGER;

    @Shadow public abstract ServerPlayer getPlayer();

    @Inject(at = @At("HEAD"), method="Lnet/minecraft/server/network/ServerGamePacketListenerImpl;handleContainerClick(Lnet/minecraft/network/protocol/game/ServerboundContainerClickPacket;)V", cancellable = true)
    private void handleContainerClick(ServerboundContainerClickPacket pPacket, CallbackInfo info){
        PacketUtils.ensureRunningOnSameThread(pPacket, this, this.player.getLevel());
        this.player.resetLastActionTime();
        if (this.player.containerMenu.containerId == pPacket.getContainerId()) {
            if (this.player.isSpectator()) {
                this.player.containerMenu.sendAllDataToRemote();
            } else if (!this.player.containerMenu.stillValid(this.player)) {
                LOGGER.debug("Player {} interacted with invalid menu {}", this.player, this.player.containerMenu);
            } else {
                int i = pPacket.getSlotNum();
                if (!this.player.containerMenu.isValidSlotIndex(i)) {
                    LOGGER.debug("Player {} clicked invalid slot index: {}, available slots: {}", this.player.getName(), i, this.player.containerMenu.slots.size());
                } else {
                    boolean flag = pPacket.getStateId() != this.player.containerMenu.getStateId();
                    this.player.containerMenu.suppressRemoteUpdates();
                    this.player.containerMenu.clicked(i, pPacket.getButtonNum(), pPacket.getClickType(), this.player);
                    if (pPacket.getClickType() == ClickType.PICKUP && i >= 0 && this.player.containerMenu instanceof TerminalContainer terminalContainer) {
                        Slot slot = terminalContainer.slots.get(i);
                        ItemStack slotStack = slot.getItem();
                        ItemStack carryStack = terminalContainer.getCarried();
                        TerminalBlockEntity be = (TerminalBlockEntity) terminalContainer.getBlockEntity();
                        be.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent((h) -> {
                            if (i < h.getSlots() && !slotStack.isEmpty() && !carryStack.isEmpty() && !slotStack.sameItem(carryStack)) {
                                this.player.containerMenu.setCarried(ItemStack.EMPTY);
                                this.player.containerMenu.setRemoteCarried(ItemStack.EMPTY);
                            }
                        });
                    }

                    for(Int2ObjectMap.Entry<ItemStack> entry : Int2ObjectMaps.fastIterable(pPacket.getChangedSlots())) {
                        this.player.containerMenu.setRemoteSlotNoCopy(entry.getIntKey(), entry.getValue());
                    }

                    this.player.containerMenu.setRemoteCarried(pPacket.getCarriedItem());
                    this.player.containerMenu.resumeRemoteUpdates();
                    if (flag) {
                        this.player.containerMenu.broadcastFullState();
                    } else {
                        this.player.containerMenu.broadcastChanges();
                    }
                }
            }
        }
        info.cancel();
    }
}
