package kowi2003.core.common.registries;

import java.util.Arrays;
import java.util.function.Supplier;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityType.BlockEntitySupplier;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BlockEntityTypeRegister implements IRegistry {
    
        /**
         * Access to the forge register to add block entity types to the game
         */
        private  final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPE_REGISTER;

        public BlockEntityTypeRegister(String modId) {
            BLOCK_ENTITY_TYPE_REGISTER = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, modId);
        }
        
        /**
         * register a block entity type
         * @param <T> the block entity type
         * @param id the id of the block entity type, note only lower case letters and dots and dasses '_' allowed
         * @param blockEntitySupplier the supplier to create the block entity
         * @param blocks the blocks that can have this block entity
         * @return the registry object for this block entity type
         */
        @SuppressWarnings("unchecked") 
        public <T extends BlockEntity> RegistryObject<BlockEntityType<T>> register(String id, BlockEntitySupplier<? extends T> blockEntitySupplier, RegistryObject<Block>... blocks)
        {
            return BLOCK_ENTITY_TYPE_REGISTER.register(id, buildType(id, blockEntitySupplier, blocks));
        }

        /**
         * creates a block entity type but does NOT register it
         * @param <T> the block entity type
         * @param id the id of the block entity type, note only lower case letters and dots and dasses '_' allowed
         * @param blockEntitySupplier the supplier to create the block entity
         * @param blocks the blocks that can have this block entity
         * @return the block entity type
         */
        @SuppressWarnings({ "null", "unchecked" })
        public <T extends BlockEntity> Supplier<BlockEntityType<T>> buildType(String id, BlockEntitySupplier<? extends T> blockEntitySupplier, RegistryObject<Block>... blocks) 
        {
            return () -> {
                var builder = BlockEntityType.Builder.<T>of(blockEntitySupplier, Arrays.stream(blocks).map(RegistryObject::get).toArray(Block[]::new));
                BlockEntityType<T> type = builder.build(null);
                return type;
            };
        }

        /**
         * creates a block entity type but does NOT register it
         * @param <T> the block entity type
         * @param id the id of the block entity type, note only lower case letters and dots and dasses '_' allowed
         * @param blockEntitySupplier the supplier to create the block entity
         * @param blocks the blocks that can have this block entity
         * @return the block entity type
         */
        @SuppressWarnings("null")
        public <T extends BlockEntity> BlockEntityType<T> buildType(String id, BlockEntitySupplier<? extends T> blockEntitySupplier, Block... blocks) {
            var builder = BlockEntityType.Builder.<T>of(blockEntitySupplier, blocks);
            return builder.build(null);
        }

        @Override
        public void register(IEventBus eventBus) {
            BLOCK_ENTITY_TYPE_REGISTER.register(eventBus);
        }
}
