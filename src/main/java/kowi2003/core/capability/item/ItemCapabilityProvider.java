package kowi2003.core.capability.item;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

/**
 * A forge capability provider for item stack. Forge itself does not supply
 * a default capability provider. Using this class you can attach capabilities to itemstacks
 * 
 * Note: this class itself is not enough to add attach capabilities
 * 
 * @author KOWI2003
 */
public class ItemCapabilityProvider<T> implements ICapabilityProvider {

    Capability<T> capabilityType;
    T capability;
    
    /**
     * creates a new capability provider for a specific capability type
     * @param capabilityType the type of capability (for forge these are located under the ForgeCapabilities class)
     * @param capability the capability implementation that should be provided by the provider
     */
    public ItemCapabilityProvider(Capability<T> capabilityType, T capability) {
        this.capabilityType = capabilityType;
        this.capability = capability;
    }

    @Override
    public <K> @NotNull LazyOptional<K> getCapability(@NotNull Capability<K> cap, @Nullable Direction side) {
        return cap == capabilityType ? LazyOptional.of(this::getCapability).cast() : LazyOptional.empty();
    }

    T getCapability() { return capability; }

}
