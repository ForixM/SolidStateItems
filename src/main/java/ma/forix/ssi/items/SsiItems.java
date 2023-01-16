package ma.forix.ssi.items;

import ma.forix.ssi.Ssi;
import ma.forix.ssi.blocks.SsiBlocks;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class SsiItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Ssi.MODID);
    public static void RegisterItems(IEventBus eventBus){
        ITEMS.register(eventBus);
    }

    //Block Items
    public static final RegistryObject<Item> RACK_BLOCK = ITEMS.register("rack_block", () -> new BlockItem(SsiBlocks.RACK_BLOCK.get(), new Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS)));

    //Items
    public static final RegistryObject<Drive> DRIVE = ITEMS.register("drive", Drive::new);
}
