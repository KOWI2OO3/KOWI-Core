package kowi2003.core.common.entities;

import javax.annotation.Nonnull;

import org.joml.Quaternionf;

import kowi2003.core.common.contraptions.Contraption;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;

public final class CoreEntitySerializers {
    
    public static final EntityDataSerializer<Quaternionf> QUATERNION = new EntityDataSerializer<Quaternionf>() {
        @Override
        public void write(@Nonnull FriendlyByteBuf buffer, @Nonnull Quaternionf quaternion) {
            buffer.writeFloat(quaternion.x);
            buffer.writeFloat(quaternion.y);
            buffer.writeFloat(quaternion.z);
            buffer.writeFloat(quaternion.w);
        }

        @Override
        public Quaternionf read(@Nonnull FriendlyByteBuf buffer) {
			return new Quaternionf(buffer.readFloat(), buffer.readFloat(), buffer.readFloat(), buffer.readFloat());
        }

        @Override
        public Quaternionf copy(@Nonnull Quaternionf quaternion) {
            return new Quaternionf(quaternion);
        }
    };

    public static final EntityDataSerializer<Contraption> CONTRAPTION = new EntityDataSerializer<Contraption>() {
        @Override
        public void write(@Nonnull FriendlyByteBuf buffer, @Nonnull Contraption contraption) {
            buffer.writeNbt(contraption.serializeNBT());
        }

        @Override
        public Contraption read(@Nonnull FriendlyByteBuf buffer) {
			return Contraption.from(buffer.readNbt());
        }

        @Override
        public Contraption copy(@Nonnull Contraption contraption) {
            return Contraption.from(contraption.serializeNBT());
        }
    };

    public static void register() {
		EntityDataSerializers.registerSerializer(QUATERNION);
		EntityDataSerializers.registerSerializer(CONTRAPTION);
	}

}
