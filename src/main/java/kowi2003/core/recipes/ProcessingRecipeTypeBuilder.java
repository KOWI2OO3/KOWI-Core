package kowi2003.core.recipes;

import java.util.function.Supplier;

import javax.annotation.Nonnull;

import kowi2003.core.recipes.registry.IRecipeTypeInfo;
import kowi2003.core.utils.RecipeHelper;
import net.minecraft.world.Container;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.capability.IFluidHandler;

public final class ProcessingRecipeTypeBuilder<T extends Container> {
        
    private RecipeContent content = RecipeContent.ITEM;
    private int InputStart = 0;
    private int InputCount = 1;
    private int FluidInputStart = 0;
    private int FluidInputCount = 1;
    private Supplier<IRecipeTypeInfo> infoSupplier;

    ProcessingRecipeTypeBuilder() {}
    
    /**
     * Creates a new type builder for any container type
     * @return a new Processing Recipe Type Builder
     */
    @Nonnull
    public static ProcessingRecipeTypeBuilder<Container> of(Supplier<IRecipeTypeInfo> typeInfo) {
        var builder = new ProcessingRecipeTypeBuilder<Container>();
        builder.infoSupplier = typeInfo;
        return builder;
    }

    /**
     * Creates a new type builder for a specific Container type
     * @param <K> the type of the container to make the recipe for 
     * @param containerType the type of the container for the processing recipe [Default: Container.class]
     * @return a new Processing Recipe Type Builder
     */
    @Nonnull
    public static <K extends Container> ProcessingRecipeTypeBuilder<K> of(Supplier<IRecipeTypeInfo> typeInfo, Class<K> containerType) {
        var builder = new ProcessingRecipeTypeBuilder<K>();
        builder.infoSupplier = typeInfo;
        return builder;
    }

    /**
     * Defines the starting index of the recipe's input slots
     * @param start the start index of the container's recipe input
     * @return this builder with the modified value
     */
    @Nonnull
    public ProcessingRecipeTypeBuilder<T> inputSlots(int start) {
        this.InputStart = start;
        return this;
    }

    /**
     * Defines the amount of input slots for this recipe
     * @param count the amount of input slots
     * @return this builder with the modified value
     */
    @Nonnull
    public ProcessingRecipeTypeBuilder<T> inputCount(int count) {
        this.InputCount = count;
        return this;
    }

    /**
     * Defines the structure of inputs, the starting index of the recipe's input slots 
     * and the count of input slots 
     * @param start the start index of the container's recipe input
     * @param count the amount of input slots
     * @return this builder with the modified value
     */
    @Nonnull
    public ProcessingRecipeTypeBuilder<T> input(int start, int count) {
        return this.inputSlots(start).inputCount(count);
    }

    /**
     * Defines the starting fluid index of the recipe's fluid input slots
     * @param start the start fluid index of the fluid container's recipe fluid input
     * @return this builder with the modified value
     */
    @Nonnull
    public ProcessingRecipeTypeBuilder<T> fluidinputSlots(int start) {
        this.FluidInputStart = start;
        return this;
    }

    /**
     * Defines the amount of fluid input slots for this recipe
     * @param count the amount of fluid input slots
     * @return this builder with the modified value
     */
    @Nonnull
    public ProcessingRecipeTypeBuilder<T> fluidinputCount(int count) {
        this.FluidInputCount = count;
        return this;
    }

    /**
     * Defines the structure of fluid inputs, the starting index of the recipe's fluid input slots 
     * and the count of fluid input slots 
     * @param start the start index of the container's recipe input
     * @param count the amount of input slots
     * @return this builder with the modified value
     */
    @Nonnull
    public ProcessingRecipeTypeBuilder<T> fluidinput(int start, int count) {
        return this.fluidinputSlots(start).fluidinputCount(count);
    }

    /**
     * Defines the recipe type's content, which allows fluid, item or both
     * @param content the content type for this recipe
     * @return this builder with the modified value
     */
    @Nonnull
    public ProcessingRecipeTypeBuilder<T> defineConent(RecipeContent content) {
        this.content = content;
        return this;
    }

    /**
     * Creates a factory to be used in registration of the recipe type
     * @return the factory for the processing recipe
     */
    @Nonnull
    @SuppressWarnings("null")
    public ProcessingRecipeFactory<ProcessingRecipeImpl<T>> factory() {
        var inStart = switch(content) { 
            case FLUID -> 0;
            default -> InputStart;
        };
        var fluidInStart = switch(content) { 
            case ITEM -> 0;
            default -> FluidInputStart;
        };
        return (params) -> new ProcessingRecipeImpl<>(infoSupplier.get(), inStart, InputCount, fluidInStart, FluidInputCount, params);
    }

    public static enum RecipeContent {
        BOTH,
        ITEM,
        FLUID
    }
    
    /**
     * Reference implementation of processing recipe to be build by the factory
     */
    static final class ProcessingRecipeImpl<T extends Container> extends ProcessingRecipe<T> {

        private final int startIndex;
        private final int inCount;
        private final int fluidStartIndex;
        private final int fluidInCount;

        public ProcessingRecipeImpl(IRecipeTypeInfo info, int startIndex, int inCount, 
                int fluidStartIndex, int fluidInCount, ProcessingRecipeParams params) {
            super(info, params);
            this.startIndex = startIndex;
            this.inCount = inCount;
            
            this.fluidStartIndex = fluidStartIndex;
            this.fluidInCount = fluidInCount;
        }

        @Override
        public boolean matches(@Nonnull T container, @Nonnull Level level) {
            return inCount == 0 || RecipeHelper.check(container, startIndex, inCount, getProcessingIngredients());
        }

        @Override
        public boolean matches(@Nonnull IFluidHandler[] tanks, @Nonnull Level world) {
            return fluidInCount == 0 || RecipeHelper.check(tanks, fluidStartIndex, fluidInCount, getFluidIngredients());
        }
    }
}
