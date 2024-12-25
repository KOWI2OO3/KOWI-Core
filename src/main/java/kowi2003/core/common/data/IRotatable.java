package kowi2003.core.common.data;

import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;

/**
 * Represents an object that can be rotated
 * @author KOWI2003
 */
public interface IRotatable {

  Quaternionfc getRotation();
    void setRotation(Quaternionfc rotation);

    /**
     * Rotates the object by the given pitch, yaw and roll, relative to the current rotation
     * @param pitch the pitch to rotate by
     * @param yaw the yaw to rotate by
     * @param roll the roll to rotate by
     */
    default void rotate(float pitch, float yaw, float roll) {
		var localRotation = new Quaternionf(0, 0, 0, 1);
        localRotation.mul(new Quaternionf(new AxisAngle4f(roll, 0, 0, 1)));
		
        localRotation.mul(new Quaternionf(new AxisAngle4f(pitch, 1, 0, 0)));
        localRotation.mul(new Quaternionf(new AxisAngle4f(yaw, 0, 1, 0)));
		localRotation.normalize();
		
		localRotation.mul(getRotation());
		localRotation.normalize(); 
		setRotation(localRotation);
    }

    /**
     * Rotates the object by the given quaternion, relative to the current rotation
     * @param localRotation the quaternion to rotate by
     */
    default void rotate(Quaternionf localRotation) {
		localRotation.normalize();
		localRotation.mul(getRotation());
		localRotation.normalize(); 
		setRotation(localRotation);
	}
}
