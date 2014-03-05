package natlab.backends.vrirGen;

import java.util.HashMap;
import java.util.Map;

import natlab.tame.classes.reference.PrimitiveClassReference;

public final class MClassToClassIDMapper {
	private static Map<PrimitiveClassReference, String> typeMap;

	public static void init() {
		typeMap = new HashMap<PrimitiveClassReference, String>();
		typeMap.put(PrimitiveClassReference.INT8, "mxINT8_CLASS");
		typeMap.put(PrimitiveClassReference.INT16, "mxINT16_CLASS");
		typeMap.put(PrimitiveClassReference.INT32, "mxINT32_CLASS");
		typeMap.put(PrimitiveClassReference.SINGLE, "mxSINGLE_CLASS");
		typeMap.put(PrimitiveClassReference.DOUBLE, "mxDOUBLE_CLASS");

	}

	public static String get(PrimitiveClassReference type) {
		return typeMap.get(type);
	}

	public static void put(PrimitiveClassReference type, String mxClass) {
		typeMap.put(type, mxClass);
	}

}
