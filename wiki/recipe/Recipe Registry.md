# Recipe Registry
A simpler way of registring recipe types

The Recipe Registry is meant to easily create new and custom recipe types using custom serializers. these serializers are in turn used by minecraft to read the recipes from the data packs. 

The registry allows easy creation of new recipe types allowing developers to focus on the important parts of their mods without needing to worry about the internal logic of recipe registry and [recipe handling](Recipe%20Handling.md).

## Instatiating Registry
Simple registry of recipe types uses the `RecipeRegistry` class to register recipes types directly. 
For the registry you must create an instance of the `RecipeRegistry` class by supplying your mod id to it.
```java 
static final RecipeRegistry REGISTRY = new RecipeRegistry(Reference.MODID); 
```
this is assuming your mod id is comes from some general class called `Reference`. This tends to differ for each mod, but the gist is that you supply the `RecipeRegisty` with your registered mod id.

## Usage
When you have created your own custom instance of the recipe registry you can start registring your custom recipe types.
There are three different register method in the registry.
All of them returning an implementation of the `IRecipeTypeInfo` interface. This interface exposes access to the different parts of the recipe type.

### Simple Register
The first of the methods is a simple register method taking 2 arguments, the first being the id of the recipe type and the second being the supplier for the serializer.
```java
/**
 * Registers a recipe type using the given suppliers to the internal forge recipe registers. 
 * @param <T> the type of the recipe of the recipe type
 * @param id the id of the recipe type
 * @param serializerSupplier the supplier to give the serializer to the register
 * @return a recipe type info object which contains the necessary info about the recipe type
 */
public <T extends Recipe<?>> IRecipeTypeInfo register(
        @Nonnull ResourceLocation id, 
        @Nonnull Supplier<RecipeSerializer<T>> serializerSupplier)
```
This is the method is used second most commonly for registring any custom recipe type. the actual usage of the method is really straight forward:
```java
final IRecipeTypeInfo myRecipeType = REGISTRY.register(new ResourceLocation(Reference.MODID, "myRecipeType"), () -> myRecipeSerializer);
```
This registers the recipe type and returns its corresponding recipe type info, which in turn can be used to get the actual recipe type to be used in [recipe handling](Recipe%20Handling.md).

### Processing Recipe Registering
The Core also supplies its own extendable recipe object, [Processing Recipe](Processing%20Recipe.md). This can be registered by a simple factory.
```java
/**
 * Registers a processing recipe type using the given factory to the internal forge recipe registers. 
 * @param <T> the type of the recipe of the recipe type
 * @param id the id of the recipe type
 * @param recipeFactory the processing recipe factory to build the recipe type from
 * @return a recipe type info object which contains the necessary info about the recipe type
 */
public <T extends ProcessingRecipe<?>> IRecipeTypeInfo register(
        @Nonnull ResourceLocation id, 
        @Nonnull ProcessingRecipeFactory<T> recipeFactory)
```
This method simply aids in registring custom processing recipes and streamlines the process of using the processing recipes. The method returns a `IRecipeTypeInfo` this contains all relevant information about the recipe type. Which in turn can be used in the [recipe handling](Recipe%20Handling.md) to actually use these recipes.

### Complete Control
This register method is used when developers want the complete control over recipe type, this however is mostly unnecessary. 
```java
/**
 * Registers a recipe type using the given suppliers to the internal forge recipe registers. 
 * @param <T> the type of the recipe of the recipe type
 * @param id the id of the recipe type
 * @param serializerSupplier the supplier to give the serializer to the register
 * @param typeSupplier the supplier for the actual Recipe Type
 * @param registerType whether to register the actual Recipe Type, usefull for handling already existing recipe types
 * @return a recipe type info object which contains the necessary info about the recipe type
 */
public <T extends Recipe<?>> IRecipeTypeInfo register(
        @Nonnull ResourceLocation id, 
        @Nonnull Supplier<RecipeSerializer<T>> serializerSupplier, 
        @Nonnull Supplier<RecipeType<T>> typeSupplier, 
        boolean registerType);
```
this method is very rarely used tho, its meant to aid in the unique situations where the developer needs to specify all the details of the recipe type or when the developer whishes to create a dummy `IRecipeTypeInfo`.
This method's use is really straightforward:
```java
final IRecipeTypeInfo myRecipeType = REGISTRY.register(new ResourceLocation(Reference.MODID, "myRecipeType"), () -> myRecipeSerializer, () -> myRecipeType, true);
```
Alternatively it can be used to define existing recipe types, this can be achieved by setting the last parameter to `false` and supplying an existing recipe type.
This however is not a common or recommended usecase and is only to be used when requiring dummy data.


## Usage Example
```java
public class ModRecipeTypes {
        // Creating the custom instance of the recipe registry
        static final RecipeRegistry REGISTRY = new RecipeRegistry(Reference.MODID);

        // Registring the custom recipe types, this is assuming you have a class myRecipeSerializer 
        // which is a recipe serializer extended from the minecraft RecipeSerializer class

        // Using the first method to create a custom recipe
        static final IRecipeTypeInfo myRecipeType = REGISTRY.register(new ResourceLocation(Reference.MODID, "myRecipeType"),  myRecipeSerializer::new);

        // Using the second method to create a custom processing recipe
        static final IRecipeTypeInfo myProcessingRecipeType = REGISTRY.register(new ResourceLocation(Reference.MODID, "myProcessingRecipeType"),  myProcessingRecipe::new);


        // Using the second method to create a custom processing recipe using the builder
        static final IRecipeTypeInfo myBuiltProcessingRecipeType = REGISTRY.register(new ResourceLocation(Core.MODID, "myBuiltProcessingRecipeType"),  
                ProcessingRecipeTypeBuilder
                .of(() -> ModRecipeTypes.myBuiltProcessingRecipeType)
                .defineConent(RecipeContent.BOTH)
                .inputSlots(2)
                .factory()
        );
}
```
For the `myProcessingRecipeType` you require a custom [`ProcessingRecipe`](Processing%20Recipe.md) class which allows for more complex recipes to be created. 

Want a easy way to make simple recipe, the [`ProcessingRecipeTypeBuilder`](#processing-recipe-type-builder) can be used to make a simple processing recipe type. This may be limiting for more complex recipes but is a simple way to add recipes for simple custom machines or work stations.

# Processing Recipe Type Builder
The processing recipe type builder is a builder to make simple processing recipe types without needing to create a custom extended class of a ProcessingRecipe. 
The builder can be created by: 
```java
var builder = ProcessingRecipeTypeBuilder.of(() -> ModRecipeTypes.myRecipeType);
```
This creates a new builder for a recipe type variable defined in [`ModRecipeTypes`](#usage-example) class.

```java
var builder = ProcessingRecipeTypeBuilder.of(() -> ModRecipeTypes.myRecipeType, Inventory.class);
```
This is an alteration which will create a builder which makes a recipe type which is specific for an certain container, in this case the `Inventory` container.

## Builder Properties

### Builder Content Type
The builder can create recipes which check either, items, fluids or both. This can be achieved by calling `defineContent`
```java
builder.defineConent(RecipeContent.BOTH);
builder.defineConent(RecipeContent.ITEM);
builder.defineConent(RecipeContent.FLUID);
```
This property is optional as the builder has RecipeContent set to ITEM by default.

### Inputs
#### Input Count
The builder can define the amount of inputs for the recipe by using `inputCount` or `fluidInputCount`, for items or fluids respectively.
```java
// Defines that the recipe has 5 item input slots
builder.inputCount(5);

// Defines that the recipe has 3 fluid input 'slots' (tanks)
builder.fluidinputCount(3);
```
Note that the default count is 1, which means if you want to count to be 1 you don't have to define this property.

#### Input Starting Index
The builder can define from which index in the container the input slots starts, default value is 0.
This means that the input slots for a builder where it starts from index 2 and a count of 4 has the input slots at index 2 up and until 6.
```java
// Defines that the recipe item input slots start at index 2
builder.inputSlots(2);

// Defines that the recipe fluid input slots start at index 1
builder.fluidinputSlots(1);
```
Note that the default start index is 0, which means if you want to start at index 0 you don't have to define this property.

#### Packed Input Definition
Alternatively if you want to define both properties you can use the combined `input` and `fluidInput` methods for items and fluids respectively. Which defines both the start index and count of the input. (first start index, then count)
```java
// Defines that the recipe item input slots start at index 2
// and that the recipe has 5 item input slots
builder.input(2, 5);

// Defines that the recipe fluid input slots start at index 1
// and that the recipe has 3 fluid input 'slots' (tanks)
builder.fluidinputSlots(1, 3);
```
In this way it can be a bit more compact and readable to define a simple recipe type.