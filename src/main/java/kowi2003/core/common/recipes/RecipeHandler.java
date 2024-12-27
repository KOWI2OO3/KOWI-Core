package kowi2003.core.common.recipes;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import kowi2003.core.common.capabilities.fluid.ChangableFluidHandler;
import kowi2003.core.common.capabilities.inventory.ChangableItemStackHandler;
import kowi2003.core.common.misc.functions.NonnullSupplier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;

/**
 * The automatic recipe handler used to handle any kind of recipe in a generic and consise manner.
 * using interfaces and lambda's to make the generic recipe handler work for any recipe based system.
 * 
 * this recipe handler also supports any kind of recipe. 
 * And if the inventory and fluid tanks supplied allow for listeners (use {@link kowi2003.core.capability.inventory.ChangableItemStackHandler ChangableItemStackHandler} and 
 * {@link kowi2003.core.capability.fluid.ChangableFluidHandler ChangableFluidHandler}) the handler uses a more efficient 'update on change' based checking rather than a more generic
 * tick based checking system (polling)
 * 
 * @author KOWI2003
 */
public class RecipeHandler<K extends Recipe<Container>> implements INBTSerializable<CompoundTag>, AutoCloseable {

    // Information Suppliers
    @Nonnull private Function<K, Float> duration;
    @Nonnull private NonnullSupplier<IItemHandlerModifiable> inventorySupplier;
    @Nonnull private NonnullSupplier<IFluidHandler[]> tanksSupplier;

    // Action hooks
    @Nonnull private ICheck<K> checker;
    @Nonnull private ICondition condition;
    @Nonnull private ITick ticker;
    @Nonnull private IFinish<K> finish;

    // Internal Logic gatherers
    private float counter = 0;
    private float maxCounter = -1;
    @Nonnull private Supplier<Float> multiplier = () -> 1f;
	@Nullable private K currentRecipe = null;

    // Type definitions
    @Nonnull private RecipeType<K> recipeType;

    // Logic type handling trackers
    private boolean checkedListeners = false;
    private boolean usesListeners = false;
    @Nullable private UUID inventoryListenerId;
    @Nullable private UUID[] tankListenerId;

    @SuppressWarnings("null")
    public RecipeHandler(@Nonnull RecipeType<K> type, @Nullable Supplier<Float> speedMultiplier,
            @Nullable NonnullSupplier<IFluidHandler[]> tanksSupplier,
            @Nullable NonnullSupplier<IItemHandlerModifiable> inventorySupplier,
            @Nonnull ICheck<K> checker, ICondition condition, @Nullable ITick ticker, @Nullable IFinish<K> finish, 
            @Nullable Function<K, Float> duration,
            @Nonnull NonnullSupplier<Level> levelSupplier) {
        this.recipeType = type;
        this.checker = checker;
        this.inventorySupplier = inventorySupplier == null ? () -> new ItemStackHandler(0) : inventorySupplier;
        this.tanksSupplier = tanksSupplier == null ? () -> null : tanksSupplier;
        this.multiplier = speedMultiplier == null ? this.multiplier : speedMultiplier;
        this.duration = duration == null ? (recipe) -> 0f : duration;
        this.condition = condition == null ? () -> true : condition;
        this.ticker = ticker == null ? () -> {} : ticker;
        this.finish = finish == null ? (recipe) -> {} : finish;

        // Checking Logic type handling
        var inv = this.inventorySupplier.get();
        if(inv instanceof ChangableItemStackHandler handler)
            inventoryListenerId = handler.addListener((slot, inventory) -> onChange(levelSupplier.get()));
            
        if(tanksSupplier != null) {
            var tanks = this.tanksSupplier.get();
            tankListenerId = new UUID[tanks.length];
            for (int i = 0; i < tanks.length; i++) {
                var tank = tanks[i];
                if(tank instanceof ChangableFluidHandler chanable)
                    tankListenerId[i] = chanable.addListener((fluidTank) -> onChange(levelSupplier.get()));
            }
        }
    }

    /**
     * Is run each tick to handle the recipes and check whether a recipe is valid
     * @param level the level to use in the recipe checking
     */
    public void tick(@Nonnull Level level) {
        if(!condition.check()) {
            reset();
            return;
        }

        // Without support for listeners check recipe state each tick
        if(!usesListeners())
            onChange(level);

        if(currentRecipe != null && !isRecipeValid(inventorySupplier.get(), tanksSupplier.get(), level)) {
            reset();
            return;
        }

        counter -= getMultiplier();
        ticker.tick();
        if(counter <= 0 && currentRecipe != null) {
            finish.finish(currentRecipe);
            reset(false);
            onChange(level);
        }
    }

    /**
     * Should be triggered on change, this change by default means changes to the inventory and fluid tanks 
     * but can be called externally for other external changes, like when the energy of a machine reaches normal levels
     * @param level the level to use in the recipe checking
     */
    public void onChange(@Nonnull Level level) {
        if(!condition.check()) {
            reset();
            return;
        }

        if(currentRecipe != null)
            return;

        reset();
        var recipe = checkRecipes(inventorySupplier.get(), tanksSupplier.get(), level);
        if(recipe.isEmpty()) return;
        currentRecipe = recipe.get();
        maxCounter = counter = duration.apply(currentRecipe);
    }

    /**
     * Tries to get the recipe that is valid for the current contents of the inventory and the fluid tanks
     * 
     * Note: this method gets the first valid recipe, if multiple recipes exist that are valid only the first one is handled
     * @param inv the inventory to check
     * @param tanks the array of fluid tanks to check, array may be empty  
     * @param level the level to get the recipe from (because of datapacks)
     * @return an optional which potentially contain the recipe which corresponds to the contents of the inventory and tanks
     */
    public Optional<K> checkRecipes(IItemHandlerModifiable inv, @Nonnull IFluidHandler[] tanks, @Nonnull Level level) {
        // Requires a special null check because, we need to call a different method to get all recipes without checking the inventory
        var recipes = inv != null ? level.getRecipeManager().getRecipesFor(recipeType, new RecipeWrapper(inv), level) : level.getRecipeManager().getAllRecipesFor(recipeType);

        // Makes sure the inv is actually set to non null even if its empty
        inv = inv == null ? new ItemStackHandler(0) : inv;

		for (K recipe : recipes)
            if(checker.checkRecipe(recipe, inv, tanks))
                return Optional.of(recipe);
        return Optional.empty();
    }

    /**
     * cCecks to see whether the recipe that is currently being handled is still valid
     * @param inv the inventory to check
     * @param tanks the array of fluid tanks to check, array may be empty  
     * @param level the level to get the recipe from (because of datapacks)
     * @return whether the current recipe is valid or not
     */
    @SuppressWarnings("null")
    private boolean isRecipeValid(IItemHandlerModifiable inv, @Nonnull IFluidHandler[] tanks, @Nonnull Level level) {
        if(inv == null)
            inv = new ItemStackHandler(0);

        return currentRecipe == null || (currentRecipe.matches(new RecipeWrapper(inv), level) && checker.checkRecipe(currentRecipe, inv, tanks));
    }

    /**
     * Resets all of the recipe specific data, to prepare for a next recipe to be handled
     */
    private void reset() {
        reset(true);
    }

    /**
     * Resets all of the recipe specific data, or just resetting the counter.
     * whether it resets all of the recipe specific data or just the counter is based on the given resetRecipe parameter
     * @param resetRecipe whether to reset all of the recipe specific data or just the counter
     */
    private void reset(boolean resetRecipe) {
        if(resetRecipe) {
            currentRecipe = null;
            maxCounter = -1;
        }
        counter = Math.max(maxCounter, 0);
    }

    /**
     * Gets the progress of the recipe on a scale of 0-1 (will also be 0 when no recipe is being handled)
     * @return the progress of the recipe (0-1)
     */
    public float getProgress() {
        if(maxCounter == -1 || maxCounter == 0)
            return 0;
        return 1 -(counter/maxCounter);
    }

    /**
     * Gets the speed multiplier from the supplier given on creation
     * @return the speed multiplier
     */
    public float getMultiplier() {
        return multiplier.get();
    }

    /**
     * The recipe handler is active if and only if it is handling a recipe
     * @return whether a recipe is being handled
     */
    public boolean isActive() {
        return maxCounter > 0;
    }

    public K getCurrentRecipe() {
        return currentRecipe;
    }

    /**
     * checks to see if all the listeners have been applied, and if so use an event based update instead of a polling bases update check
     * @return whether to use listeners
     */
    private boolean usesListeners() {
        if(!checkedListeners) {
            if(inventoryListenerId != null && tankListenerId != null) {
                for (var id : tankListenerId) {
                    if(id == null)
                        break;
                }

                usesListeners = true;
            }
            checkedListeners = true;
        }
        return usesListeners;
    }

    public CompoundTag serializeNBT(CompoundTag tag) {
        tag.putFloat("progress", counter);
        tag.putFloat("maxProgress", maxCounter);
        return tag;
    }

    @Override
    public CompoundTag serializeNBT() {
        return serializeNBT(new CompoundTag());
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        counter = tag.getFloat("progress");
        maxCounter = tag.getFloat("maxProgress");
    }

    /**
     * Cleaning up some of the listeners used in the recipe handling
     * @throws Exception the java exception that could be thrown, not that this implementation does not throw an exception
     */
    @Override
    @SuppressWarnings("null")
    public void close() throws Exception {
        var inv = inventorySupplier.get();
        if(inventoryListenerId != null && inv instanceof ChangableItemStackHandler changable)
            changable.removeListener(inventoryListenerId);

        var tanks = tanksSupplier.get();
        if(tanks != null && tankListenerId != null) {
            for (int i = 0; i < tanks.length; i++) {
                var tank = tanks[i];
                if(tankListenerId[i] != null && tank instanceof ChangableFluidHandler changable)
                    changable.removeListener(tankListenerId[i]);
            }
        }
    }

/**
 * An simple interface to check for a certain external recipe condition to be met.
 * for example checking the energy level of a machine.
 */
public static interface ICondition {

    /**
     * used to check whether a external conditions are met to allow for the recipe to be handled
     * Note that this is still used each tick to check if the recipe is still valid
     * @return
     */
    boolean check();
}

/**
 * An simple interface to add a function when the recipe finishes.
 * for example for some external system to be set.
 */
public static interface IFinish<K extends Recipe<?>> {

    /**
     * Triggers on a finishing a recipe
     * @param recipe the recipe that has just been finished
     */
    void finish(@Nonnull K recipe);
}

/**
 * An simple interface to run some action on each tick of the recipe being handled
 * for example draining energy from the machine
 */
public static interface ITick {

    /**
     * Triggers each tick a recipe is being handled
     */
    void tick();
}

/**
 * An simple interface to check whether a recipe is viable for a certain inverntory (and fluid tanks)
 */
public static interface ICheck<K extends Recipe<?>> {

    /**
     * Used to check if a recipe is valid for a given inventory and fluid tanks,
     * usually this just uses the recipe's match method, but can be extended to do additional checks
     * 
     * Note that this method should only check the recipe and not for output space or other check, use the {@link ICondition ICondition} for this purpose
     * @param recipe the recipe that is being checked
     * @param inventory the inventory to check
     * @param tanks the array of fluid tanks to check, the array may be empty
     * @return whether the recipe was valid
     */
    boolean checkRecipe(@Nonnull K recipe, @Nonnull IItemHandlerModifiable inventory, @Nonnull IFluidHandler[] tanks);
}

}
