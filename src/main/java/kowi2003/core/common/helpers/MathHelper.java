package kowi2003.core.common.helpers;

import org.joml.Matrix4f;
import org.joml.Quaterniondc;
import org.joml.Quaternionfc;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4d;
import org.joml.Vector4f;

import net.minecraft.world.phys.Vec3;

public final class MathHelper {
    
    /**
	 * rotated an vector
	 * <blockquote><i>
	 * 	vec = (1, 0, 0)<br>
	 * 	rotation = quaternion rotation from euler (0, 90, 0)<br>
	 * 
	 * 	<b>returns vector (0, 0, 1)</b>
	 * </blockquote></i>
	 * @param vector vector to rotate
	 * @param rotation the rotation to apply to the vector
	 * @return the rotated vector
	 */
	public static Vector3f rotateVector(Vector3fc vector, Quaternionfc rotation) {
		var vec4f = rotation.transform(new Vector4f(vector.x(), vector.y(), vector.z(), 1.0f));
		return new Vector3f(vec4f.x(), vec4f.y(), vec4f.z()); 
	}

    /**
	 * rotated an vector
	 * <blockquote><i>
	 * 	vec = (1, 0, 0)<br>
	 * 	rotation = quaternion rotation from euler (0, 90, 0)<br>
	 * 
	 * 	<b>returns vector (0, 0, 1)</b>
	 * </blockquote></i>
	 * @param vector vector to rotate
	 * @param rotation the rotation to apply to the vector
	 * @return the rotated vector
	 */
	public static Vector3d rotateVector(Vector3dc vector, Quaterniondc rotation) {
		var vec4 = rotation.transform(new Vector4d(vector.x(), vector.y(), vector.z(), 1.0));
		return new Vector3d(vec4.x(), vec4.y(), vec4.z()); 
	}
	
	/**
	 * rotated an vector
	 * <blockquote><i>
	 * 	vec = (1, 0, 0)<br>
	 * 	rotation = quaternion rotation from euler (0, 90, 0)<br>
	 * 
	 * 	<b>returns vector (0, 0, 1)</b>
	 * </blockquote></i>
	 * @param vector vector to rotate
	 * @param rotation the rotation to apply to the vector
	 * @return the rotated vector
	 */
	public static Vec3 rotateVector(Vec3 vector, Quaternionfc rotation) {
		return new Vec3(rotateVector(new Vector3f((float)vector.x, (float)vector.y, (float)vector.z), rotation));
	}

    /**
	 * rotated an vector
	 * <blockquote><i>
	 * 	vec = (1, 0, 0)<br>
	 * 	rotation = quaternion rotation from euler (0, 90, 0)<br>
	 * 
	 * 	<b>returns vector (0, 0, 1)</b>
	 * </blockquote></i>
	 * @param vector vector to rotate
	 * @param rotation the rotation to apply to the vector
	 * @return the rotated vector
	 */
	public static Vec3 rotateVector(Vec3 vector, Quaterniondc rotation) {
		var result = rotateVector(new Vector3d(vector.x, vector.y, vector.z), rotation);
        return new Vec3(result.x, result.y, result.z);
	}
    
    
	/**
	 * rotated an vector
	 * <blockquote><i>
	 * 	vec = (1, 0, 0)<br>
	 * 	origin = (0, 0, 1)<br>
	 * 	rotation = quaternion rotation from euler (0, 90, 0)<br>
	 * 
	 * 	<b>returns vector (0, 0, 2)</b>
	 * </blockquote></i>
	 * @param vector vector to rotate
	 * @param origin the starting point for the rotation, can be seen as an -offset
	 * @param rotation the rotation to apply to the vector
	 * @return the rotated vector
	 */
	public static Vector3f rotateVector(Vector3f vector, Vector3f origin, Quaternionfc rotation) {
		var matrix = new Matrix4f()
            .identity()
            .translate(origin)
            .rotate(rotation);
        var vec4 = matrix.transform(new Vector4f(vector, 1));
		return new Vector3f(vec4.x(), vec4.y(), vec4.z()); 
	}

}
