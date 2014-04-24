package natlab.backends.vrirGen;

import java.util.HashMap;
import java.util.Map;

public class OperatorMapper {
	static Map<String, String> opMap;

	public static void initMap() {
		opMap = new HashMap<String, String>();
		opMap.put("plus", "plus");
		opMap.put("minus", "minus");
		opMap.put("rdivide", "div");
		opMap.put("mtimes", "mmult");
		opMap.put("times", "mult");
		opMap.put("mrdivide", "mrdiv");
		opMap.put("mldivide", "mldiv");
		opMap.put("and", "and");
		opMap.put("or", "or");
		// TODO : to be entered
		opMap.put("eq", "eq");
		opMap.put("le", "leq");
		opMap.put("ge", "geq");
		opMap.put("lt", "lt");
		opMap.put("gt", "gt");
		opMap.put("le", "lt");
		// TODO: need to check all divisions and then add them
		// TODO: check equivalents for Unary operators
		opMap.put("uplus", "");
		opMap.put("uminus", "negate");
		opMap.put("not", "not");
	}

	public static boolean isOperator(String name) {
		return opMap.containsKey(name);
	}

	public static void add(String key, String value) {
		opMap.put(key, value);
	}

	public static void remove(String key, String value) {
		opMap.remove(key);
	}

	public static String get(String key) {
		return opMap.get(key);
	}

}
