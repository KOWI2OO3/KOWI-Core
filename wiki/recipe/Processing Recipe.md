# Processing Recipe
Processing recipes are recipes which are defined by the Core and allow developers to easily make custom recipes and recipe types for custom machines or work stations.

Processing recipes allows for recipe of items and/or fluids, while also allowing for a processing duration to be set. This makes making custom recipes for your blocks a lot easier because the processing recipes are also naturally included in the [`Recipe Handler`](Recipe%20Handler.md)

## Registry
To register a custom Processing recipe, use the [`Recipe Registry`](Recipe%20Registry.md).

This can be done either through the [processing recipe type builder](Recipe%20Registry.md#processing-recipe-type-builder) 
or by making a custom processing recipe class. using the builder is useful for simple recipes.
but if you want to create a more complex recipe with for example additional logic or a custom extra property you should make a [processing recipe class](#making-a-processing-recipe).

## Making A Processing Recipe
Creating your own custom processing recipe is no harder than making a class and extending it `ProcessingRecipe<>`.
```java 
public class MyProcessingRecipe extends ProcessingRecipe {

    public MyProcessingRecipe(ProcessingRecipeParams params) {
        // Note: we have to reference the recipe type in the super constructor
        super(ModRecipeTypes.MyProcessingRecipe, params);
    }

    @Override
    public abstract boolean matches(@Nonnull T container, @Nonnull Level level) {
        // In here you have to make your own custom check, or use the RecipeHelper method
    }
}
```
This is minimal implementation of a processing recipe.
**Notes**: 
- *the matches method is left empty as this is where you would define your own custom matching logic.* 
- *The container supplier in the matches is only the item container.*

### Custom Recipe Checking

#### Only Fluid Matching 
If you want to write a custom fluid matching method override, like so.
```java
@Override
public boolean matches(@Nonnull IFluidHandler[] tanks, @Nonnull Level world) {
    // In here you have to make your own custom check, or use the RecipeHelper method
}
```
In this case you may just define the item matches method to always return true, as when it returns false it will never be used as both the item and fluid should match.
```java
// Forcing the item to be true such that only the fluid check is used
@Override
public abstract boolean matches(@Nonnull T container, @Nonnull Level level) { return true; }
```

#### Completely Custom Recipe Matching
Alternatively you may override the `matchesTotal` method to handle all the checking in a single methode, the main reason using this is when the fluid and items somehow correspond to one another.
```java
@Override
public boolean matchesTotal(@Nonnull T container, @Nonnull IFluidHandler[] tanks, @Nonnull Level level) {
    // In here you have to make your own custom check
}
```
**Note**: *if you override this method, the logic in the item and fluid `matches` methods don't matter anymore.*

### Custom Result Item

