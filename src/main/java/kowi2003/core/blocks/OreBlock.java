package kowi2003.core.blocks;

import java.util.List;
import java.util.function.Supplier;

import javax.annotation.Nonnull;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams.Builder;

public class OreBlock extends DefaultBlock {

	Supplier<ItemStack> item;
    
    public OreBlock(Properties builder, Supplier<Item> item) {
		super(builder);
		this.item = () -> item.get() == null ? new ItemStack(this) : new ItemStack(item.get());
	}

    public OreBlock(Properties builder, Supplier<Item> item, SoundType sound) {
		super(builder, sound);
		this.item = () -> item.get() == null ? new ItemStack(this) : new ItemStack(item.get());
	}

    public OreBlock(Properties builder, SoundType sound) {
		super(builder, sound);
		this.item = () -> new ItemStack(this);
	}
	
	public OreBlock(Properties builder) {
		super(builder);
		this.item = () -> new ItemStack(this);
	}

    public void with(Supplier<ItemStack> item) {
		this.item = item;
	}

    @Override
    public List<ItemStack> getDrops(@Nonnull BlockState state, @Nonnull Builder builder) {
		return super.getDrops(state, builder);
	}
	
}
