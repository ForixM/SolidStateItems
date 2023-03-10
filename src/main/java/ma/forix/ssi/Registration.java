package ma.forix.ssi;

import ma.forix.ssi.blocks.*;
import ma.forix.ssi.blocks.blockentities.*;
import ma.forix.ssi.items.CraftStarter;
import ma.forix.ssi.items.Drive;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class Registration {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Ssi.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Ssi.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCKS_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Ssi.MODID);
    public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, Ssi.MODID);

    public static void Register(IEventBus eventBus){
        BLOCKS.register(eventBus);
        ITEMS.register(eventBus);
        BLOCKS_ENTITIES.register(eventBus);
        CONTAINERS.register(eventBus);
    }

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block){
        RegistryObject<T> registry = BLOCKS.register(name, block);
        ITEMS.register(name, () -> new BlockItem(registry.get(), new Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS)));
        return registry;
    }


    //BLOCKS
    public static final RegistryObject<RackBlock> RACK_BLOCK = registerBlock("rack_block", RackBlock::new);
    public static final RegistryObject<TerminalBlock> TERMINAL_BLOCK = registerBlock("terminal", TerminalBlock::new);
    public static final RegistryObject<Cable> CABLE = registerBlock("cable", Cable::new);
    public static final RegistryObject<CrafterBlock> CRAFTER = registerBlock("crafter", CrafterBlock::new);
    public static final RegistryObject<CraftOrderer> CRAFT_ORDERER = registerBlock("craft_orderer", CraftOrderer::new);


    //ITEMS
    public static final RegistryObject<Drive> DRIVE = ITEMS.register("drive", Drive::new);
    public static final RegistryObject<CraftStarter> CRAFT_STARTER = ITEMS.register("craft_starter", CraftStarter::new);


    //BLOCK ENTITIES
    public static final RegistryObject<BlockEntityType<RackBlockEntity>> RACK_BLOCK_ENTITY = BLOCKS_ENTITIES.register("rack_block",
            () -> BlockEntityType.Builder.of(RackBlockEntity::new, RACK_BLOCK.get()).build(null));
    public static final RegistryObject<BlockEntityType<TerminalBlockEntity>> TERMINAL_BLOCK_ENTITY = BLOCKS_ENTITIES.register("terminal",
            () -> BlockEntityType.Builder.of(TerminalBlockEntity::new, TERMINAL_BLOCK.get()).build(null));
    public static final RegistryObject<BlockEntityType<CableBlockEntity>> CABLE_BLOCK_ENTITY = BLOCKS_ENTITIES.register("cable",
            () -> BlockEntityType.Builder.of(CableBlockEntity::new, CABLE.get()).build(null));
    public static final RegistryObject<BlockEntityType<CrafterBlockEntity>> CRAFTER_BLOCK_ENTITY = BLOCKS_ENTITIES.register("crafter",
            () -> BlockEntityType.Builder.of(CrafterBlockEntity::new, CRAFTER.get()).build(null));

    public static final RegistryObject<BlockEntityType<CraftOrdererBlockEntity>> CRAFT_ORDERER_BLOCK_ENTITY = BLOCKS_ENTITIES.register("craft_orderer",
            () -> BlockEntityType.Builder.of(CraftOrdererBlockEntity::new, CRAFT_ORDERER.get()).build(null));


    //CONTAINERS
    public static final RegistryObject<MenuType<RackContainer>> RACK_CONTAINER = CONTAINERS.register("rack_block", () -> IForgeMenuType.create(((windowId, inv, data) ->
            new RackContainer(windowId, data.readBlockPos(), inv, inv.player))));
    public static final RegistryObject<MenuType<TerminalContainer>> TERMINAL_CONTAINER = CONTAINERS.register("terminal", () -> IForgeMenuType.create(((windowId, inv, data) ->
            new TerminalContainer(windowId, data.readBlockPos(), inv, inv.player))));

}
