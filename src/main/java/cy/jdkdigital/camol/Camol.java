package cy.jdkdigital.camol;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import cy.jdkdigital.camol.common.block.CamoConfiguratorBlock;
import cy.jdkdigital.camol.common.block.entity.CamoConfiguratorBlockEntity;
import cy.jdkdigital.camol.common.item.CamoItem;
import cy.jdkdigital.camol.common.recipe.SimpleCamoCraftingRecipe;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.*;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@Mod(Camol.MODID)
public class Camol
{
    public static final String MODID = "camol";
    public static final Logger LOGGER = LogUtils.getLogger();

    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MODID);
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, MODID);
    private static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, MODID);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, MODID);
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(BuiltInRegistries.RECIPE_TYPE, MODID);

    public static final DeferredItem<Item> CAMO_ITEM = registerItem("camo_item", () -> new CamoItem(new Item.Properties()));
    public static final DeferredBlock<Block> CAMO_CONFIGURATOR_BLOCK = registerBlock("camo_configurator", () -> new CamoConfiguratorBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_BLOCK).noOcclusion()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CamoConfiguratorBlockEntity>> CAMO_CONFIGURATOR_BLOCK_ENTITY = registerBlockEntity("camo_configurator", () -> createBlockEntityType(CamoConfiguratorBlockEntity::new, CAMO_CONFIGURATOR_BLOCK.get()));

    public static final Supplier<DataComponentType<BlockState>> BLOCK_COMPONENT = DATA_COMPONENTS.register("block", () -> DataComponentType.<BlockState>builder().persistent(BlockState.CODEC).networkSynchronized(ByteBufCodecs.fromCodec(BlockState.CODEC)).build());

    public static final Supplier<AttachmentType<Map<String, BlockState>>> CAMO_BLOCK_MAP = ATTACHMENT_TYPES.register(
        "camo", () -> AttachmentType.<Map<String, BlockState>>builder(() -> new HashMap<>()).serialize(Codec.unboundedMap(Codec.STRING, BlockState.CODEC)).build()
    );

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> SIMPLE_CAMO_CRAFTING = RECIPE_SERIALIZERS.register("simple_camo_crafting", () -> new SimpleCraftingRecipeSerializer<>(SimpleCamoCraftingRecipe::new));
    public static final DeferredHolder<RecipeType<?>, RecipeType<SimpleCamoCraftingRecipe>> SIMPLE_CAMO_CRAFTING_TYPE = RECIPE_TYPES.register("simple_camo_crafting", () -> new RecipeType<>() {});

    public static final TagKey<Block> CAMO_BLACKLIST = BlockTags.create(ResourceLocation.fromNamespaceAndPath(MODID, "disallowed_camoable_blocks"));
    public static final TagKey<Item> CRAFTING_BLACKLIST = ItemTags.create(ResourceLocation.fromNamespaceAndPath(MODID, "disallowed_camo_blocks"));

    public Camol(IEventBus modEventBus, ModContainer modContainer)
    {
        ITEMS.register(modEventBus);
        BLOCKS.register(modEventBus);
        BLOCK_ENTITIES.register(modEventBus);
        ATTACHMENT_TYPES.register(modEventBus);
        DATA_COMPONENTS.register(modEventBus);
        RECIPE_SERIALIZERS.register(modEventBus);
        RECIPE_TYPES.register(modEventBus);

        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        // somehow handle blockstates, maybe by putting it in different slots in the crafting grid or have a machine for it

        // crafting recipe for camo item
        // model for empty camo item
    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {
        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            ITEMS.getEntries().forEach(itemDeferredHolder -> {
                event.accept(itemDeferredHolder.value());
            });
        }
    }

    private static DeferredItem<Item> registerItem(String name, Supplier<Item> sup) {
        return ITEMS.register(name, sup);
    }

    private static DeferredBlock<Block> registerBlock(String name, Supplier<? extends Block> sup) {
        DeferredBlock<Block> block = BLOCKS.register(name, sup);
//        ITEMS.registerSimpleBlockItem(name, block);
        return block;
    }

    public static <E extends BlockEntity, T extends BlockEntityType<E>> DeferredHolder<BlockEntityType<?>, T> registerBlockEntity(String id, Supplier<T> supplier) {
        return BLOCK_ENTITIES.register(id, supplier);
    }

    public static <E extends BlockEntity> BlockEntityType<E> createBlockEntityType(BlockEntityType.BlockEntitySupplier<E> factory, Block... blocks) {
        return BlockEntityType.Builder.of(factory, blocks).build(null);
    }
}
