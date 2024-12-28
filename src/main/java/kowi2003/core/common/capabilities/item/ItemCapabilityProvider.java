package kowi2003.core.common.capabilities.item;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

public record ItemCapabilityProvider<K>(Capability<K> capabilityType, K instance) implements ICapabilityProvider
{
    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return cap == capabilityType ? LazyOptional.of(() -> instance).cast() : LazyOptional.empty();
    }
}
    

