package ma.forix.ssi.blocks;

import ma.forix.ssi.Ssi;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class SsiBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Ssi.MODID);
    public static void RegisterBlocks(IEventBus eventBus){
        BLOCKS.register(eventBus);
    }

    public static final RegistryObject<RackBlock> RACK_BLOCK = BLOCKS.register("rack_block", RackBlock::new);
}
