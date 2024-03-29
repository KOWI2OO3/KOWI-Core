package kowi2003.core.capability.fluid;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Predicate;

import javax.annotation.Nonnull;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;

/**
 * A special implementation of the IFluidHandler and FluidTank which allows for on changed events to be handled when something changes
 * these actions can be registered through the add and remove listener methods
 * 
 * @author KOWI2003 
 */
public class ChangableFluidHandler extends FluidTank {
    
    private Map<UUID, Consumer<IFluidHandler>> onChange = new HashMap<>();

    public ChangableFluidHandler(int capacity)
    {
        super(capacity);
    }

    public ChangableFluidHandler(int capacity, Predicate<FluidStack> validator)
    {
        super(capacity, validator);
    }
    /**
     * adds an on change listener
     * @param listener the listener to add
     * @return the id of the listener
     */
    public UUID addListener(@Nonnull Consumer<IFluidHandler> listener) {
        var id = UUID.randomUUID();
        onChange.put(id, listener);
        return id;
    }

    /**
     * removes an on change listener
     * @param id the id of the listener, as gotten from the {@link addListener addListener} method
     */
    public void removeListener(@Nonnull UUID id) {
        onChange.remove(id);
    }

    public void clearListeners() {
        onChange.clear();
    }

    @Override
    protected void onContentsChanged() {
        for (var listener : onChange.values())
            listener.accept(this);
    }

}
