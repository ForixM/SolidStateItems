package ma.forix.ssi;

import com.mojang.logging.LogUtils;
import ma.forix.ssi.blocks.blockentities.RackScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Ssi.MODID)
public class Ssi {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "ssi";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();
    // Create a Deferred Register to hold Blocks which will all be registered under the "ssi" namespace
//    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    // Create a Deferred Register to hold Items which will all be registered under the "ssi" namespace
//    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    // Creates a new Block with the id "ssi:example_block", combining the namespace and path
//    public static final RegistryObject<Block> rack_block = BLOCKS.register("rack_block", () -> new Block(BlockBehaviour.Properties.of(Material.STONE)));
    // Creates a new BlockItem with the id "ssi:example_block", combining the namespace and path
//    public static final RegistryObject<Item> FEUR_BLOCK_ITEM_WHITE = ITEMS.register("rack_block", () -> new BlockItem(rack_block.get(), new Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS)));


//    public static final RegistryObject<Block> FEUR_BLOCK_BLACK = BLOCKS.register("feur_block_black", () -> new Block(BlockBehaviour.Properties.of(Material.STONE)));
    // Creates a new BlockItem with the id "ssi:example_block", combining the namespace and path
//    public static final RegistryObject<Item> FEUR_BLOCK_ITEM_BLACK = ITEMS.register("feur_block_black", () -> new BlockItem(FEUR_BLOCK_BLACK.get(), new Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS)));

    public Ssi() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register the Deferred Register to the mod event bus so blocks get registered
//        BLOCKS.register(modEventBus);
//        SsiItems.RegisterItems(modEventBus);
//        SsiBlocks.RegisterBlocks(modEventBus);
//        SsiBlockEntities.RegisterBlockEntities(modEventBus);
        Registration.Register(modEventBus);
        // Register the Deferred Register to the mod event bus so items get registered
//        ITEMS.register(modEventBus);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // Some common setup code
        event.enqueueWork(() -> {
            MenuScreens.register(Registration.RACK_CONTAINER.get(), RackScreen::new);
            ItemBlockRenderTypes.setRenderLayer(Registration.RACK_BLOCK.get(), RenderType.translucent());
        });
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            // Some client setup code
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }
    }
}
