package kowi2003.core.common.recipes.ingredient.FluidValues;

import java.util.Collection;
import java.util.Collections;

import javax.annotation.Nonnull;

import com.google.gson.JsonObject;

import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Allows an direct fluid to be defined as an fluid value instead of some predicate like tags
 * it basically allows for a fluid to be directly registered instead of using tags
 * 
 * @author KOWI2003
 */
public class DirectFluidValue implements FluidValue {
    private final FluidStack fluid;

    public DirectFluidValue(Fluid fluid) {
        this.fluid = new FluidStack(fluid, 1);
    }

    public DirectFluidValue(FluidStack fluid) {
        this.fluid = fluid;
    }

    @Override
    public Collection<FluidStack> getFluids() {
        return Collections.singleton(this.fluid);
    }

    @Nonnull
    @Override
    public JsonObject serialize() {
        var json = new JsonObject();
        json.addProperty("fluid", ForgeRegistries.FLUIDS.getKey(this.fluid.getFluid()).toString());
        return json;
    }
}
