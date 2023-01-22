package ma.forix.ssi.blocks.blockentities;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import ma.forix.ssi.Ssi;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundContainerClickPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class TerminalScreen extends AbstractContainerScreen<TerminalContainer> {

    private final ResourceLocation GUI = new ResourceLocation(Ssi.MODID, "textures/gui/terminal_gui.png");

    public TerminalScreen(TerminalContainer pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void renderBg(PoseStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, GUI);
        int relX = (this.width - this.imageWidth) / 2;
        int relY = (this.height - this.imageHeight) / 2;
        this.blit(matrixStack, relX, relY, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    protected void slotClicked(Slot pSlot, int pSlotId, int pMouseButton, ClickType pType) {
        if (pSlot != null) {
            pSlotId = pSlot.index;
        }
        this.minecraft.gameMode.handleInventoryMouseClick(this.menu.containerId, pSlotId, pMouseButton, pType, this.minecraft.player);
        if (pType == ClickType.PICKUP && pSlotId >= 0) {
            Slot slot = this.minecraft.player.containerMenu.slots.get(pSlotId);
            ItemStack slotStack = slot.getItem();
            ItemStack carryStack = this.minecraft.player.containerMenu.getCarried();
            if (pSlotId >= 0 && pSlotId < 4 && !slotStack.isEmpty() && !carryStack.isEmpty() && !slotStack.sameItem(carryStack)) {
                this.minecraft.player.containerMenu.setCarried(ItemStack.EMPTY);
                this.minecraft.player.containerMenu.setRemoteCarried(ItemStack.EMPTY);
            }
        }

//        super.slotClicked(pSlot, pSlotId, pMouseButton, pType);
    }
}
