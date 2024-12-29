package kowi2003.core.client.helpers;

import java.io.IOException;

import javax.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import kowi2003.core.client.animation.Animation;
import kowi2003.core.client.animation.AnimationSerializer;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

public final class AnimationHelper {
    
    private static Gson gson;

    private static Gson getGson() {
        if(gson == null)
            gson = new GsonBuilder().registerTypeAdapter(Animation.class, new AnimationSerializer()).create();
        return gson;
    }

    @Nullable
    public static Animation loadAnimation(ResourceLocation location) {
        var resourceManager = Minecraft.getInstance().getResourceManager();
        var opt = resourceManager.getResource(location);
        if(opt.isEmpty()) return null;

        try {
            return getGson().fromJson(opt.get().openAsReader(), Animation.class);
        }catch(IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

}
