package natlab.backends.vrirGen;

import java.util.HashMap;
import java.util.Map;

public class VrirTypeMapper {
	private static Map<String, String> typeMap;

	public static Map<String, String> getTypeMap() {
		return typeMap;
	}

	public static void setTypeMap(Map<String, String> typeMap) {
		VrirTypeMapper.typeMap = typeMap;
	}

	public static void initTypeMap() {
		typeMap = new HashMap<String, String>();
		typeMap.put("double", "float64");
		typeMap.put("single", "float32");
		typeMap.put("char", null);
		typeMap.put("int8", null);
		typeMap.put("int16", null);
		typeMap.put("int32", "int32");
		typeMap.put("int64", "int64");
		typeMap.put("uint8", null);
		typeMap.put("uint16", null);
		typeMap.put("uint32", null);
		typeMap.put("uint64", null);
		typeMap.put("logical", "boolean");

	}

	public static String getType(String key) {
		return typeMap.get(key);
	}

	public static void putType(String key, String value) {
		typeMap.put(key, value);
	}

	public static boolean contains(String key) {
		return typeMap.containsKey(key);
	}
}
