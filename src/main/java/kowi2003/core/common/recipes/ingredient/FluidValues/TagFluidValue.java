package kowi2003.core.common.recipes.ingredient.FluidValues;

import java.util.Collection;

import javax.annotation.Nonnull;

import com.google.gson.JsonObject;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Allows an entire tag to be put into a fluid value 
 * using this an test with this fluid value can test for the entire tag's content
 * which can include fluids from other mods
 * 
 * @author KOWI2003
 */
public class TagFluidValue implements FluidValue {

    private final TagKey<Fluid> tag;

    public TagFluidValue(TagKey<Fluid> tag) {
        this.tag = tag;
    }

    @Override
    public Collection<FluidStack> getFluids() {
        // Mapping the values from the tag to the collection
        return ForgeRegistries.FLUIDS.tags().getTag(tag).stream().map(fluid -> new FluidStack(fluid, 1)).toList();
    }

    @Nonnull
    @Override
    public JsonObject serialize() {
        var json = new JsonObject();
        json.addProperty("tag", this.tag.location().toString());
        return json;
    }
    
}
