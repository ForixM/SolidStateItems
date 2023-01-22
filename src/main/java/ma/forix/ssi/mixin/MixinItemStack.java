package ma.forix.ssi.mixin;

import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(ItemStack.class)
public abstract class MixinItemStack extends net.minecraftforge.common.capabilities.CapabilityProvider<ItemStack> {
    protected MixinItemStack(Class<ItemStack> baseClass) {
        super(baseClass);
    }

    @Shadow public abstract Item getItem();

    @Shadow private int count;

    @Shadow @Nullable private CompoundTag tag;

    @Inject(at = @At("HEAD"), method = "Lnet/minecraft/world/item/ItemStack;save(Lnet/minecraft/nbt/CompoundTag;)Lnet/minecraft/nbt/CompoundTag;", cancellable = true)
    private void save(CompoundTag pCompoundTag, CallbackInfoReturnable<CompoundTag> info){
        ResourceLocation resourcelocation = Registry.ITEM.getKey(getItem());
        pCompoundTag.putString("id", resourcelocation == null ? "minecraft:air" : resourcelocation.toString());
        pCompoundTag.putInt("Count", count);
        if (this.tag != null) {
            pCompoundTag.put("tag", tag.copy());
        }

        CompoundTag cnbt = this.serializeCaps();
        if (cnbt != null && !cnbt.isEmpty()) {
            pCompoundTag.put("ForgeCaps", cnbt);
        }
        info.setReturnValue(pCompoundTag);
    }
}
