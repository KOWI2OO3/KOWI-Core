package kowi2003.core.common.recipes;

import javax.annotation.Nonnull;

import kowi2003.core.common.recipes.ingredient.ProcessingFluidIngredient;
import kowi2003.core.common.recipes.ingredient.ProcessingIngredient;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

/**
 * A builder to somewhat easily create a certain recipe through code
 * 
 * note that this is used in the recipe read and writes and that that is its intended purpose
 * trying to use this builder outside its intended context might lead to unexpected problems with minecrafts recipe registry
 * mainly that the recipe will not be added to the recipe registry as that should be handled by minecrafts systems
 * 
 * @author KOWI2003
 */
public class ProcessingRecipeBuilder<T extends ProcessingRecipe<?>> {
    
  private final ProcessingRecipeFactory<T> factory;
  private final ProcessingRecipeParams params;

  /**
   * Creates a new Processing recipe builder based on the factory and the id given
   * @param factory the recipe factory
   * @param id the id of the recipe
   */
  public ProcessingRecipeBuilder(@Nonnull ProcessingRecipeFactory<T> factory, @Nonnull ResourceLocation id) {
		this.factory = factory;
		params = new ProcessingRecipeParams(id);
	}

  /**
   * Sets the list of ingredients of the recipe
   * @param ingredients a list of the ingredients of the recipe
   * @return this builder with the updated ingredients value
   */
  @Nonnull
  public ProcessingRecipeBuilder<T> withIngredients(@Nonnull ProcessingIngredient... ingredients) {
		return withIngredients(NonNullList.of(ProcessingIngredient.EMPTY, ingredients));
	}
	
  /**
   * Sets the list of ingredients of the recipe
   * @param ingredients a list of the ingredients of the recipe
   * @return this builder with the updated ingredients value
   */
  @Nonnull
	public ProcessingRecipeBuilder<T> withIngredients(NonNullList<ProcessingIngredient> ingredients) {
		params.ingredients = ingredients;
		return this;
	}

  /**
   * Sets the recipe item results
   * @param results the results to set
   * @return this builder with the updated result value
   */
  public ProcessingRecipeBuilder<T> withItemOutputs(@Nonnull ProcessingResult... results) {
		return withItemOutputs(NonNullList.of(ProcessingResult.EMPTY, results));
	}
	
  /**
   * Sets the recipe item results
   * @param results the results to set
   * @return this builder with the updated result value
   */
	public ProcessingRecipeBuilder<T> withItemOutputs(NonNullList<ProcessingResult> results) {
		params.results = results;
		return this;
	}

  /**
   * Sets the list of fluid ingredients of the recipe
   * @param ingredients a list of the fluid ingredients of the recipe
   * @return this builder with the updated fluid ingredients value
   */
  public ProcessingRecipeBuilder<T> withFluidIngredients(@Nonnull ProcessingFluidIngredient... ingredients) {
		return withFluidIngredients(NonNullList.of(ProcessingFluidIngredient.EMPTY, ingredients));
	}

  /**
   * Sets the list of fluid ingredients of the recipe
   * @param ingredients a list of the fluid ingredients of the recipe
   * @return this builder with the updated fluid ingredients value
   */
	public ProcessingRecipeBuilder<T> withFluidIngredients(NonNullList<ProcessingFluidIngredient> ingredients) {
		params.fluidIngredients = ingredients;
		return this;
	}
	
  /**
   * Sets the list of fluid results of the recipe
   * @param ingredients a list of the fluid results of the recipe
   * @return this builder with the updated fluid results value
   */
  public ProcessingRecipeBuilder<T> withFluidOutputs(@Nonnull FluidStack... results) {
    return withFluidOutputs(NonNullList.of(FluidStack.EMPTY, results));
	}
	
	public ProcessingRecipeBuilder<T> withFluidOutputs(NonNullList<FluidStack> results) {
		params.fluidResults = results;
		return this;
	}

  /**
   * Sets the duration of the recipe to the specified amount of ticks
   * @param ticks the ticks the recipe duration should be
   * @return this builder with the updated duration value
   */
  @Nonnull
  public ProcessingRecipeBuilder<T> duration(int ticks) {
		params.processingDuration = ticks;
		return this;
	}

  /**
   * Creates the processing recipe based on the properties supplied in the builder and the factory supplied
   * @return a new processing recipe
   */
  @Nonnull
  public T build() {
		return factory.create(params);
	}
}
