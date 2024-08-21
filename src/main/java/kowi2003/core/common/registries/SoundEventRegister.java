package kowi2003.core.common.registries;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class SoundEventRegister implements IRegistry{
    
    /**
     * Access to the forge register to add menu types to the game
     */
    private final DeferredRegister<SoundEvent> SOUND_EVENT_REGISTER;

    private final String MOD_ID;

    public SoundEventRegister(String modId) {
        SOUND_EVENT_REGISTER = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, modId);
        MOD_ID = modId;
    }

    /**
     * register a sound event
     * @param name the name of the sound event, this is also the name of the sound file
     * @return the registry object for this sound event
     */
    public RegistryObject<SoundEvent> register(String name) {
        return SOUND_EVENT_REGISTER.register(name, () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(MOD_ID, name)));
    }

    @Override
    public void register(IEventBus eventBus) {
        SOUND_EVENT_REGISTER.register(eventBus);
    }
}
