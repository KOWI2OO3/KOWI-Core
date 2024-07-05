package kowi2003.core.recipes.ingredient.FluidValues;

import java.util.Collection;

import javax.annotation.Nonnull;

import com.google.gson.JsonObject;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

public class DynamicFluidValue implements FluidValue {
    
    private final TagKey<Fluid> tag;
    private final FluidStack fluid;

    public DynamicFluidValue(@Nonnull String value) {
        var location = new ResourceLocation(value);
        tag = TagKey.create(Registries.FLUID, location);

        var holder = ForgeRegistries.FLUIDS.getHolder(location).get();
        fluid = (holder == null || holder.value() == null) ? null : new FluidStack(holder.get(), 1);
    }

    @Override
    public Collection<FluidStack> getFluids() {
        // Mapping the values from the tag to the collection
        var list = ForgeRegistries.FLUIDS.tags().getTag(tag).stream().map(fluid -> new FluidStack(fluid, 1)).toList();
        if(fluid != null)
            list.add(fluid);
        return list;
    }

    @Override
    @Nonnull
    public JsonObject serialize() {
        var json = new JsonObject();
        json.addProperty("tag", this.tag.location().toString());
        json.addProperty("fluid", ForgeRegistries.FLUIDS.getKey(this.fluid.getFluid()).toString());
        return json;
    }

    


}
