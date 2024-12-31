package kowi2003.core.client.registries;

import java.util.HashMap;
import java.util.Map;

import kowi2003.core.client.animation.Animation;
import kowi2003.core.client.animation.IAnimationType;
import kowi2003.core.client.helpers.AnimationHelper;
import kowi2003.core.common.registries.IRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.eventbus.api.IEventBus;

public class AnimationRegister implements IRegistry {
    
    private static final Map<ResourceLocation, Animation> cachedAnimation = new HashMap<>(); 
    
    private String modId = null;

    public AnimationRegister(String modId) {
        this.modId = modId;
    }

    public final IAnimationType registerAnimation(String path) {
        return registerAnimation(modId != null ? new ResourceLocation(modId, "animations/" + path) : new ResourceLocation(path));
    }

    public final IAnimationType registerAnimation(ResourceLocation location) {
        cachedAnimation.put(location, null);
        return () -> cachedAnimation.get(location);
    }

    private final void loadAnimations(final ModelEvent.BakingCompleted event) {
        for (var key : cachedAnimation.keySet())
            cachedAnimation.put(key, AnimationHelper.loadAnimation(key));
    }

    public void register(IEventBus eventBus)
    {
        eventBus.addListener(this::loadAnimations);
    }
}
