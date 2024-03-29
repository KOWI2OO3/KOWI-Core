package kowi2003.core.config;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import kowi2003.core.Core;
import kowi2003.core.utils.TypeUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.config.ModConfigEvent;

/**
 * Config Handler
 * 
 * Allowing mod makers to use objects for their config instead of needing to hard code all of the spec building and loading of config values
 * Allowing for easier and more intuitive use of the config system
 * 
 * @author KOWI2003 
 */
@Mod.EventBusSubscriber(bus = Bus.MOD, modid = Core.MODID)
public class ConfigHandler {
    
    static final Map<ResourceLocation, Object> CONFIG_REGISTRY = new HashMap<>();

    /**
     * creates a forge config spec based on the class supplied. all of the primitive typed fields in the class will be used to map config values
     * note that when the type of a field is a complex object it will be used as a group and its primitive fields will in turn be used to create the config values
     * @param <T> the type of the config class
     * @param config the default config instance
     * @return the constructed forge config spec
     */
    public static <T> ForgeConfigSpec createForgeConfigSpec(@Nonnull T config) {
        Builder builder = new ForgeConfigSpec.Builder();
        constructBuilder(config, builder);
        return builder.build();
    }
    
    /**
     * registers a config class as a forge config thereby making it fully compatible with other mods using the forge configs and making the entire config object oriented
     * @param <T> the type of the config
     * @param defaultConfig the default config values
     * @param context the modloading context used to register the config into the forge config system
     * @param configType the type of the config, [Server, Client, Common]
     * @return a Config supplier, whenever the config is required get the config from the supplier to make sure its the latest version
     */
	@Nonnull
    @SuppressWarnings({"null", "unchecked"})
    public static <T> Supplier<T> registerConfig(@Nonnull T defaultConfig, @Nonnull ModLoadingContext context, @Nonnull Type configType, @Nullable String filename) {
        var spec = createForgeConfigSpec(defaultConfig);
        var temp = new ResourceLocation(context.getActiveNamespace(), configType.toString().toLowerCase());
        if(filename != null) {
            context.registerConfig(configType, spec, filename);
            temp = new ResourceLocation(context.getActiveNamespace(), filename.toLowerCase());
        }else
            context.registerConfig(configType, spec);
        
        // Making sure the supplier can use the location to regain the config object
        final var location = temp;

        CONFIG_REGISTRY.put(location, defaultConfig);
        return () -> (T)CONFIG_REGISTRY.get(location);
    }

    /**
     * registers a config class as a forge config thereby making it fully compatible with other mods using the forge configs and making the entire config object oriented
     * @param <T> the type of the config
     * @param defaultConfig the default config values
     * @param configType the type of the config, [Server, Client, Common]
     * @param filename the name of the file of where the config should be saved (null for default filename)
     * @return a Config supplier, whenever the config is required get the config from the supplier to make sure its the latest version
     */
	@Nonnull
    @SuppressWarnings("null")
    public static <T> Supplier<T> registerConfig(@Nonnull T defaultConfig, @Nonnull Type configType, @Nullable String filename) {
        return registerConfig(defaultConfig, ModLoadingContext.get(), configType, filename);
    }

     /**
     * registers a config class as a forge config thereby making it fully compatible with other mods using the forge configs and making the entire config object oriented
     * @param <T> the type of the config
     * @param defaultConfig the default config values
     * @param configType the type of the config, [Server, Client, Common]
     * @return a Config supplier, whenever the config is required get the config from the supplier to make sure its the latest version
     */
	@Nonnull
    @SuppressWarnings("null")
    public static <T> Supplier<T> registerConfig(@Nonnull T defaultConfig, Type configType) {
        return registerConfig(defaultConfig, configType, null);
    }

    /**
     * gets the config object from the forge config specification
     * @param <T> the type of the config
     * @param config the default value of the config 
     * @param cnf the mod config to get the values from
     * @return the config object with the custom values
     */
    private static <T> T getConfig(T config, @Nonnull ModConfig cnf) {
        return getConfig(config, cnf, null);
    }

    /**
     * gets the config object from the forge config specification
     * @param <T> the type of the config
     * @param config the default value of the config 
     * @param cnf the mod config to get the values from
     * @param path the path config file
     * @return the config object with the custom values
     */
    private static <T> T getConfig(T config, @Nonnull ModConfig cnf, String path) {
        if(config == null) return config;

        path = path == null ? "" : path;

        for(Field field : config.getClass().getFields()) {
            String fieldName = field.getName();

            var configMeta = field.getAnnotation(ConfigValue.class);
            if(configMeta != null && configMeta.displayName() != null && !configMeta.displayName().isEmpty())
                fieldName = configMeta.displayName();

            String name = (path.isEmpty() ? "" : path + ".") + fieldName;
            Object value = cnf.getConfigData().get(name);
            if(value == null) continue;

            try {
                var type = field.getType();
                if(type == Float.class || type == float.class)
                    field.set(config, (float)(double)value);
                else if(type == Integer.class || type == int.class) 
                    field.set(config, (int)value);
                else if(type == Double.class || type == double.class){
                    if(value instanceof Float)
                        field.set(config, (double)(float)value);
                    else
                        field.set(config, (double)value);
                }
                else if(type == String.class)
                    field.set(config, (String)value);
                else if(type == Boolean.class || type == boolean.class)
                    field.set(config, (boolean)value);
                else if(type == Short.class || type == short.class)
                    field.set(config, (short)value);
                else if(type == Long.class || type == long.class)
                    field.set(config, (long)value);
                else if(type == Character.class || type == char.class)
                    field.set(config, (char)value);
                else if(type.isEnum())
                    field.set(config, TypeUtils.getEnum(type, (String)name));
                else if(type == List.class) {
                    field.set(config, (List<?>)value);
                }else if(type.isArray()) {
                    List<?> list = (List<?>)value;
                    try {
                        field.set(config, list.toArray());
                    }catch(Exception ex) {
                        System.err.println("Failed to load " + name + " from " + cnf.getFileName());
                    }
                }else {
                    var defaultConfigValue = field.get(config);
                    if(defaultConfigValue != null) 
                        field.set(config, getConfig(defaultConfigValue, cnf, name));
                }
            }catch(ClassCastException | IllegalArgumentException | IllegalAccessException ex) { ex.printStackTrace(); }
        }
		return config;
    }

    /**
     * constructs the entire builder for the config spec based on the config class value given
     * @param <T> the type of the config class
     * @param config the default value of the config
     * @param builder the buidler to build the config spec into
     */
    private static <T> void constructBuilder(@Nonnull T config, @Nonnull Builder builder) {
        for(Field field : config.getClass().getFields()) {
            if(field.isAnnotationPresent(ConfigNotMapped.class))
                continue;

            String name = field.getName();

            ConfigValue desc = field.getAnnotation(ConfigValue.class);

            if(desc != null) {
                if(desc.description() != null && !desc.description().isEmpty())
                    builder.comment(desc.description());
                if(desc.displayName() != null && !desc.displayName().isEmpty())
                    name = desc.displayName();
            }

            ConfigRange range = field.getAnnotation(ConfigRange.class);
            
            Class<?> type = field.getType();

            try {
                if(field.get(config) == null) continue;
                
                if(range != null && TypeUtils.isSimpleNumberType(type)) {
                    if(type == int.class || type == Integer.class)
                        builder.defineInRange(name, (int)field.get(config), range.min(), range.max());
                    else if(type == float.class || type == Float.class)
                        builder.defineInRange(name, (double)(float)field.get(config), range.min(), range.max());
                    else if(type == double.class || type == Double.class)
                        builder.defineInRange(name, (double)field.get(config), range.min(), range.max());
                }else if(type == float.class)
                    builder.define(name, (double)(float)field.get(config));
                else if(TypeUtils.isPrimitiveType(type))
                    builder.define(name, field.get(config));
                else if(type == List.class) 
                    builder.defineList(name, (List<?>)field.get(config), e -> true);
                else if(type.isArray())
                    builder.defineList(name, List.of((Array)field.get(config)), e -> true);
                else if(type.isEnum())
                    buildEnum(builder, field, config, name);
                else {
                    builder.push(name);
                    constructBuilder(config, builder);
                    builder.pop();
                }

            }catch(IllegalArgumentException | IllegalAccessException e) { e.printStackTrace(); }
        }
    }

    /**
     * constructs the enum definition in the config spec builder 
     * @param <V> the type of the enum to define 
     * @param builder the config builder to build the definition into
     * @param field the field which contains the enum and has its default value
     * @param config the config to get the field value from
     * @param name the name of the definition
     */
    @SuppressWarnings("unchecked")
    private static <V extends Enum<V>> void buildEnum(Builder builder, Field field, Object config, String name) {
		try {
            builder.defineEnum(name, (V)field.get(config));
        } catch (IllegalArgumentException | IllegalAccessException e) {
            System.err.println("The field [" + field.getName() + "] in the config [" + config.getClass().getSimpleName() + "] is not a valid enum!");
        }
    }

    /**
     * Handling loading and reloading of the mod configs when starting the game and 
     * when modifications to the config file through the game have occured
     * @param config the mod config to handle
     */
    @SuppressWarnings("null")
    private static void handleConfigLoad(ModConfig config) {
        var id = config.getModId();
        var type = config.getType();

        var location = new ResourceLocation(id, type.toString().toLowerCase());
        if(!CONFIG_REGISTRY.containsKey(location)) {
            location = new ResourceLocation(id, config.getFileName().toLowerCase());
            if(!CONFIG_REGISTRY.containsKey(location)) return;
        }

        CONFIG_REGISTRY.put(location, getConfig(CONFIG_REGISTRY.get(location), config));
    }
    
    /**
     * subscribing to the config loading event to trigger the handle 
     * when the config has changed to reflect this ingame
     * @param event the loading event to subscribe to
     */
    @SubscribeEvent
    public static void onConfigLoad(ModConfigEvent.Loading event) {
        handleConfigLoad(event.getConfig());
    }

    /**
     * subscribing to the config reloading event to trigger the handle 
     * when the config has changed to reflect this ingame
     * @param event the reloading event to subscribe to
     */
    @SubscribeEvent
    public static void onConfigLoad(ModConfigEvent.Reloading event) {
        handleConfigLoad(event.getConfig());
    }

    /**
     * Making sure the loading and reloading events are handled whether they are on the mod bus or forge bus
     */
	@Mod.EventBusSubscriber(bus = Bus.FORGE, modid = Core.MODID)
    public static class ForgeEventHook {
        /**
         * subscribing to the config loading event to trigger the handle 
         * when the config has changed to reflect this ingame
         * @param event the loading event to subscribe to
         */
        @SubscribeEvent
        public static void onConfigLoad(ModConfigEvent.Loading event) {
            handleConfigLoad(event.getConfig());
        }

        /**
         * subscribing to the config reloading event to trigger the handle 
         * when the config has changed to reflect this ingame
         * @param event the reloading event to subscribe to
         */
        @SubscribeEvent
        public static void onConfigLoad(ModConfigEvent.Reloading event) {
            handleConfigLoad(event.getConfig());
        }
    }
}
