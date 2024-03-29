package kowi2003.core.utils;

/**
 * Simple type utility methods to aid in the use of reflection
 * 
 * @author KOWI2003
 */
public class TypeUtils {
    
  /**
	 * checks whether the given value is the class of an primitive data type <br>ex. <i>int, float, boolean</i> etc.
	 * but also the Object class type for example. <i>Integer, Float, Boolean</i> etc.
	 * @param type the value type to check
	 * @return whether the given value is the class of an primitive data type
	 */
	public static boolean isPrimitiveType(Class<?> type) {
		return isNumberType(type) || type == boolean.class || type == String.class || type == Boolean.class;
	}

  /**
   * checkes whether the given value is the class of an number primitive data type <br>ex. <i>int, float, double</i> etc.
   * @param type the value type to check
   * @return whether the given value is the class of an number primitive data type
   */
  public static boolean isNumberType(Class<?> type) {
    
    return isSimpleNumberType(type) || type == byte.class || type == char.class || type == short.class || type == long.class || 
    type == Character.class || type == Short.class || type == Long.class || type == Byte.class;
  }
  
  /**
   * checkes whether the given value is the class of an simple number primitive data type <br>ex. <i>int, float, double</i>
   * @param type the value type to check
   * @return whether the given value is the class of an simple number primitive data type
   */
  public static boolean isSimpleNumberType(Class<?> type) {
    return type == Number.class ||  type == int.class || type == double.class || type == float.class || 
    type == Integer.class || type == Double.class|| type == Float.class;
  }

  /**
   * gets the enum based on the enum class and the enum name value
   * @param <T> the enum type
   * @param enumClass the enum class type
   * @param name the name of the enum value to get
   * @return the enum value gotten from the enum class with the name specified
   */
	@SuppressWarnings("unchecked")
	public static <T extends Enum<T>> Object getEnum(Class<?> enumClass, String name) {
		return Enum.valueOf((Class<T>) enumClass, name);
	}

}
