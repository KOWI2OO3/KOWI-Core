# Object Configs

In summary the object configs is a system to use java object classes as a config specification. Allowing for more intuitive ease of use of the forge config system without impeding on compatiblity.

## Implementing a simple config object

### Registering the Config Object
Using a class to define configs can be done as followed.
Imagine we want a config with two values in it, this being the configValue, as an float, and useExperimental, as an boolean. We would define it as followed:
```java
// Defines the config structure
public class CommonConfig {
    
    // Defines a float config value named configValue with default value of 10
    public float configValue = 10;
    
    // Defines a boolean config value named useExperimental with default value of false
    public boolean useExperimental = false; 

}
```
in this simple example we can see two fields which will be part of the config we will define in a later step.

you should make a ``ModConfig`` class which is going to handle the configs
```java
public class ModConfig {

    // The config supplier to allow later use
    public static Supplier<CommonConfig> commonConfig;

    // Registering the configs to the config system
    public static void register() {
        // Registering the common config under the config type Common 
        // with its default values as defined in the class
        commonConfig = ConfigHandler.registerConfig(new CommonConfig(), Type.COMMON);
    }
}
```
here we register the config object to the config handler imported from ``kowi2003.core.config.ConfigHandler``. The registerConfig method will handle all the complicated task of using the config object defined, which in this case is the ``CommonConfig`` and updating its values according to the forge config system.

The register method of the ``ModConfig`` class should be called in the constructor of your mods main class.
```java
// Default (Neo)Forge mod setup
@Mod(Mod.MODID)
public class Mod {
    public static final String MODID = "mod_id";

    // The constructor of your mod
    public Mod() {
        // Calling the register method on the ModConfig class
        ModConfig.register();
    }
}
```
now you have succesfully registered the mod config!

### Using the Config Object
After registering the config object it is ready to be used anywhere in your code. Just simply refer to the config field you defined in the ``ModConfig`` class and call the get method.
```java
var config = ModConfig.commonConfig.get();
```
now you can use this config variable to get the config values. Your IDE will also auto fill the values as the class is refered directly. So you want to access ``useExperimental`` just simply call
```java
config.useExperimental
```

## Complex config objects
### Complex object structre
The config objects can be more complicated than just a few simple primitive values
#### Config sections:
To get sections in your config to keep everything neat and readable you should add fields with objects as their type like so:
```java
public class CommonConfig {
    // From the previous example
    public float configValue = 10;

    // From the previous example
    public boolean useExperimental = false; 

    // creates a subsection called 'section'
    public SubSection section = new SubSection();

    // Defines the subsection
    public static class SubSection {
        // Defines the values in the subsection
        public String subsectionValue = "Hello World";
    } 

}
```
Allowing for sections to be created using other objects. The config will now have a section with the name ``section`` and with the value ``subsectionValue`` in it. 

#### Config lists
You can use lists in your config to allow a dynamic amount of entries to be added like so:
```java
public class CommonConfig {

    // Defines a list of integers with the defualt value being a list with 10, 5 and 7
    public int[] allowedIds = new int[] {10, 5, 7};

    // Defines a list of strings with the default value being an empty list
    public List<String> blockBlacklist = new ArrayList();

    // Defines a list of strings 
    // with the default value being a list with the  value "minecraft:stone" in it
    public List<String> blockWhitelist = List.of("minecraft:stone");
}
```
we just defined a config with three lists in it with certain default values. These lists will be 1-to-1 translated into the config format in the background. 
And again in code you can just call these lists like any other variable.
```java
var config = ModConfig.commonConfig.get();
List<String> whitelist = config.blockWhitelist;
var useStone = list.contains("minecraft:stone"); // true on the default value
```

#### Config enums
Just as expected you can also use enums in your configs like so:
```java
public enum MyEnum {
    Value1, Value2, Value3
}

public class CommonConfig {

    // Defines a enum value with a default value of 'Value1'
    public MyEnum enumValue = MyEnum.Value1;
}
```
Here we defines a custom enum and used it in our config object definition.

### Config annotations
Multiple annotations exists for the config system to more specifically define your config template. 
#### ConfigValue annotation
The config value annotation is used to define a displayname and/or add a description to your config value. The implementation of this looks as followed:
```java
public class CommonConfig {

    // Defining a custom display name which will be used in the forge config spec
    @ConfigValue(displayName="MyCustomDisplayName")
    public float configValue = 10;

    // Defining a description on the value that will be added to the forge config spec
    @ConfigValue(description="Indicating whether to use the experimental features of the mod or not [default:false]")
    public boolean useExperimental = false; 
}
``` 
here we defined a custom display name and description which are used in the config file (and config editor mods). 
The description is added to the file as a comment over the value (and usually used by config editor mods). 
This annotation works on all config value types no matter what type.

<b>Note: a display name can only be used once per section and the display name should never use any odd characters or spaces</b><br>
<i>so use the display name sparingly</i> 

#### ConfigRange annotation
The forge config spec also allows for ranges to be defined, and to do this using config objects we use the config range annotation.
```java
public class CommonConfig {

    // Defining a custom range on the value of 0 up and till 255
    @ConfigRange(min = 0, max = 255)
    public float configValue = 10;
}
```
This defines the the range a property must be in. If the value is outside of the range it will be clamped to be inside again.

<div>
<b>Notes:</b>
<li> the min and max are inclusive meaning a range from 0-255 means that the value can also be 0 or 255
<li> this only works on number values (eg. int, float and double) [on anything else it will just be ignored]
</div>

#### ConfigNotMapped annotation
It's also possible to discard a value from the config spec. To do this we use the ConfigNotMapped annotation as followed:
```java
public class CommonConfig {

    // Defining a simple config value
    public float configValue = 10;

    // Defining a value in the config that will not be mapped to the config spec
    @ConfigNotMapped
    public boolean useExperimental = false; 
}
```
In this example the ``configValue`` is mapped to the config spec but the ``useExperimental`` is not. 
discarding fields is as easy as adding the annotation on top of it.
This annotation works on all config value types no matter what type.

<b>Note: </b>When discarding a field that is an complex object you discard the entire section.
