# Item Capabilities

Wanting to make items that can store energy, fluid or items? then you need capabilities, just like how blocks work, but with items it works a bit different. To make it easier the core supports an easy interface to get items that can storge the basics, eg. energy, fluid and/or items.

* Note that all of the interfaces described below are stackable on an item
    * which means an item can contain any combination of the capabilities, for example: energy and fluid or fluid and items or energy and items etc.

## Energy Capability
To get an item that can store energy and being fully compatible with other mods that add item charging or other energy items you can use the core's interface to let everything be handled automatically

For the energy capability your item should extend the ``IItemEnergyStorage`` interface
```java
public static class MyItem extends Item implements IItemEnergyStorage {

    // Standard forge item initializer
    public MyItem(Properties properties) {
        super(properties);
    }

    // Defines the capacity of the item to be 50000 rf
	public int getCapacity() {
        return 50000;
    }
	
    // Defines the maximum amount of energy to be recieved by the item to be 100 rf per tick
	public int getMaxRecieve() {
        return 100;
    }
	
    // Defines the maximum amount of energy to be extracted fromt the item to be 100 rf per tick
	public int getMaxExtract() {
        return 100;
    }
}
```
The interface will automatically handle the item to be an energy item which means you don't need to worry about the capability attaching.

As an optional property you can set the default energy of the item. This is generally the initial energy stored in the item when just created (either through crafting or getting from the creative tabs)
```java
    // Defines the default energy level, the default value is just set to the capacity, 
    // but you can change this to whatever you want
    public int getDefaultEnergy() {
		return getCapacity();
	}
```

## Inventory Capability 
Items that should have an inventory and/or store other items inside of them, like backpacks, should use the ``IItemInventory`` inteface supplied by the core. This interface defines the properties that should be set and will automatically attach the capability to the item.
```java
public static class MyItem extends Item implements IItemInventory {

    // Standard forge item initializer
    public MyItem(Properties properties) {
        super(properties);
    }

    // Defines the size of the inventory as 45
    // as reference a single chest has a size of 9x3 = 27 slots
    public int getSlotCount() {
        return 45;
    }
}
```
In this example we created an item with a 45 slot inventory, Note this is only the capability and not the actual gui, adding the gui must be done manually

## Fluid Container Capability
Making an fluid container item can be done using the fluid container capability supplied by the core. 

```java
public static class MyItem extends Item implements IItemFluidContainer {

    // Standard forge item initializer
    public MyItem(Properties properties) {
        super(properties);
    }

    // Defines the size of the fluid tank in mb
    // as reference a bucket is 1000 mb of fluid
    public int getContainerCapacity() {
        return 5; // Equivalent to 5 buckets of storage
    }
}
```
Here we defined a simple item that can store fluids and yet again is fully compatible with mods that use forge's fluid capability to interact with item tanks. We defined the capacity of the item's tank to be equal to 5 buckets. Note that this capability only adds the code functionality of storing fluids this will <b>not</b> handle any world interactions like picking up and placing the fluids, this must be done seperatly.

### Fluid Filtering
Note this is not the same as a bucket and instead can be used on any and every fluid. but if this is not the desired functionallity then a filter can be applied to the capability to define which fluids are allowed and which are not, this can be achieved by overriding ``isFluidValid`` on the capability.
```java
public static class MyItem extends Item implements IItemFluidContainer {

    // Standard forge item initializer
    public MyItem(Properties properties) {
        super(properties);
    }

    // Defines the size of the fluid tank in mb
    // as reference a bucket is 1000 mb of fluid
    public int getContainerCapacity() {
        return 5; // Equivalent to 5 buckets of storage
    }

    // Defines which fluids can be added to the fluid tank
    // this is only a filter of which fluids are allowed in the first place, 
    // not a check if the fluid matches the current fluid in the tank
    public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
		return stack.getFluid() == Fluids.WATER;
    }
}
```
In this example we defined that only water can be stored in the tank as an simple example of the filter. This concept can ofcourse be extended to a more broad implementation. for example: Imagine a gas canister, gasses are always implemented as a fluid thus they can be in the tank, but for a tank to only allow gasses you could change the filter to:
```java
// Defines that only gasses are valid in this tank
public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
    return stack.getFluid().getFluidType().isLighterThanAir();
}
```
Because forge flips the bucket and inverts the verticies if an fluid is lighter than air, it is considered a gas. so by this definition and the filter, only gasses can be insterted into the tank.

* Note that only one fluid can be in the tank at any given time

## Custom Capability Providers
There is also an easy way to attach custom capabilities to your item using a simular interface based system. Using the ``IAttachItemCapability`` you get access to the capability attaching event which means you can add your own capabilities to the item with your own capability provider if required, without needing to mess around with the actual event. Note that a default item capability provider is also given as part of the core ``ItemCapabilityProvider`` which is an easy to use capability provider to easy add a custom capability to an item.
```java
public static class MyItem extends Item implements IAttachItemCapability {

    // Standard forge item initializer
    public MyItem(Properties properties) {
        super(properties);
    }

    // Allows the modded to attach custom capabilities to an item
    public void gatherCapabilities(ItemStack stack, AttachCapabilitiesEvent<ItemStack> event) {
        event.addCapability(new ResourceLocation(Main.MODID, "my_energy_capability"), 
            new ItemCapabilityProvider<IEnergyStorage>(ForgeCapabilities.ENERGY, 
                new MyItemEnergyStorage(storage, stack)));
    }
}
```
In this example we attach a custom item energy storage capability called ``MyItemEnergyStorage`` to the item, this is attached to the key ``"{MODID}:my_energy_capability"``. Note that this key must be unique on the item and cannot contain any capitalized letters and no special characters.

## Capability Usage
Using the capabilities in handling or usage or any other code where you want access to your capability
is as simple as using forge's capability system, just like any other capability.
Using the forge's capability system will also make your mods compatible with other mods that use it, which is usually all of the big mods.

An example of how to get the capabilities and how to use them follows.
In the item class:
```java
    
    // The minecraft use method is fired when a player right clicks with an item
    @Override
    public InteractionResultHolder<ItemStack> use(@Nonnull Level level, @Nonnull Player player, 
            @Nonnull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
    	
        // Getting the capability optional from forge's capability system
        var energyCapability = stack.getCapability(ForgeCapabilities.ENERGY);

        // Checking to see if the capability exists
        if(energyCapability.isPresent()) {
            // Resolving the capability
            var energyStorage = energyCapability.resolve().get();
            
            // Extracting 1 RF of energy if the energystorage was not null (so when it exists) 
            if(energyStorage != null)
                energyStorage.extractEnergy(1, false);
        }
        
        return super.use(level, player, hand);
    }
```
In this example we use forge's capability system to get the energy capability (which we could apply using the ``IItemEnergyStorage``) 
and extract 1 RF of energy from the item each time the player right clicks with the item in hand

As long as you as modder use forge's capabilities to get any capability you require at a certain point it should stay compatible with other mods. As an example for compatabilty
```java
// In the block entity class

// An method that is called every tick
public  void onTick() {
    // Referencing the block's inventory, which usually in a block entity is a variable
    var stack = inventory.getStackInSlot(0);

    // Getting the item's energy capability
    var energyCapability = stack.getCapability(ForgeCapabilities.ENERGY);
    if(energyCapability.isPresent()) {
        // if the capability is present resolve the optional
        var energyStorage = energyCapability.resolve().get();
        if(energyStorage != null)
            // Adding 1 RF to the item
            energyStorage.receiveEnergy(1, false);
    }

}
```
In this example we charge the item in slot 0 of the block entity with 1 RF per tick, this is fully compatible with other mods that use forge's capabilities. 