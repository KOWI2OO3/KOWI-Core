package kowi2003.core.common.capabilities.inventory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;

import javax.annotation.Nonnull;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;


/**
 * A special implementation of the ItemHandler and ItemStackHandler which allows for on changed events to be handled when something changes
 * these actions can be registered through the add and remove listener methods
 * 
 * @author KOWI2003 
 */
public class ChangableItemStackHandler extends ItemStackHandler {

    private Map<UUID, BiConsumer<Integer, IItemHandlerModifiable>> onChange = new HashMap<>();

    public ChangableItemStackHandler()
    {
        super(1);
    }

    public ChangableItemStackHandler(int size)
    {
        super(size);
    }

    public ChangableItemStackHandler(NonNullList<ItemStack> stacks)
    {
        super(stacks);
    }

    /**
     * adds an on change listener
     * @param listener the listener to add
     * @return the id of the listener
     */
    public UUID addListener(@Nonnull BiConsumer<Integer, IItemHandlerModifiable> listener) {
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
    protected void onContentsChanged(int slot) {
        for (var listener : onChange.values())
            listener.accept(slot, this);
    }

}
