package kowi2003.core.utils;

import javax.annotation.Nonnull;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * an simple helper class handling fluid serialization and deserialization
 * 
 * @author KOWI2003
 */
public class FluidHelper {
   
    /**
     * Serializes the fluid stack into json format, note that this is almost simular to the codec minecraft supplies 
     * the only difference is that the name of the tags are in lower case, this is just to keep inline with how item stacks
     * are serialized in an recipe
     * @param stack the fluidstack to serialize
     * @return the serialized json element
     */
    @Nonnull
    public static JsonElement serializeFluidStack(FluidStack stack) {
        var json = new JsonObject();
        json.addProperty("fluid", ForgeRegistries.FLUIDS.getKey(stack.getFluid()).toString());
        json.addProperty("amount", stack.getAmount());
        if(stack.hasTag())
            json.addProperty("nbt", stack.getTag().toString());
        return json;
    }

    /**
     * Deserializes a fluid stack from an json object, note that thsi is almost simular to the codec minecraft supplies
     * the only difference is that the name of the tags are in lower case, this is just to keep inline with how item stacks
     * are serialized in an recipe
     * @param json the json to get the fluid stack from
     * @return an nonnull fluid stack, or an empty stack if the parsing was unsuccesfull
     * 
     * @throws JsonSyntaxException if the fluid could not be parsed because the fluid tag was missing or incorrect
     */
    @Nonnull
    public static FluidStack deserializeFluid(JsonObject json) {
		var id = new ResourceLocation(GsonHelper.getAsString(json, "fluid"));
        var fluid = ForgeRegistries.FLUIDS.getValue(id);
		if(fluid == null)
			throw new JsonSyntaxException("Unknown fluid '" + id + "'");
        var amount = GsonHelper.getAsInt(json, "amount", 1);
        var stack = new FluidStack(fluid, amount);

        if(!json.has("nbt"))
            return stack;
        
        try {
            var element = json.get("nbt");
            stack.setTag(TagParser.parseTag(
                element.isJsonObject() ? new Gson().toJson(element) : GsonHelper.convertToString(json, "nbt")
            ));
        }catch(CommandSyntaxException e) {
            throw new JsonSyntaxException("nbt of " + id + " as failed to be parsed");
        }
        return stack;
    }
}
