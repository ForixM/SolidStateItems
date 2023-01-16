package ma.forix.ssi.blocks.blockentities;

import ma.forix.ssi.Ssi;
import ma.forix.ssi.blocks.SsiBlocks;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class SsiBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCKS_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Ssi.MODID);
    public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, Ssi.MODID);

    public static void RegisterBlockEntities(IEventBus eventBus){
        BLOCKS_ENTITIES.register(eventBus);
        CONTAINERS.register(eventBus);
    }

    public static final RegistryObject<BlockEntityType<RackBlockEntity>> RACK_BLOCK_ENTITY = BLOCKS_ENTITIES.register("rack_block",
            () -> BlockEntityType.Builder.of(RackBlockEntity::new, SsiBlocks.RACK_BLOCK.get()).build(null));

    public static final RegistryObject<MenuType<RackContainer>> RACK_CONTAINER = CONTAINERS.register("rack_block", () -> IForgeMenuType.create(((windowId, inv, data) ->
            new RackContainer(windowId, data.readBlockPos(), inv, inv.player))));
}
