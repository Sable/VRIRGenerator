package natlab.backends.vrirGen;

import java.util.HashMap;
import java.util.Map;

public class LibFuncMapper {
	private static Map<String, String> libFuncMap = new HashMap<String, String>();
	static {
		libFuncMap = new HashMap<>();
		libFuncMap.put("sqrt", "sqrt");
		libFuncMap.put("sin", "sin");
		libFuncMap.put("asin", "asin");
		libFuncMap.put("cos", "cos");
		libFuncMap.put("acos", "acos");
		libFuncMap.put("atan", "tan");
		libFuncMap.put("abs", "abs");
		libFuncMap.put("pow", "pow");
		libFuncMap.put("log", "loge");
		libFuncMap.put("log10", "log10");
		libFuncMap.put("exp", "expe");
		libFuncMap.put("sqrt", "sqrt");
		libFuncMap.put("power", "pow");
		libFuncMap.put("mpower", "pow");
		libFuncMap.put("mtimes", "mmult");
		libFuncMap.put("trans", "trans");
		libFuncMap.put("plus", "plus");
		libFuncMap.put("minus", "minus");
		libFuncMap.put("times", "mult");
		libFuncMap.put("mrdivide", "mdiv");
		libFuncMap.put("rdivide", "div");
		libFuncMap.put("trans", "trans");
	}

	public static String getFunc(String key) {
		return libFuncMap.get(key);
	}

	@SuppressWarnings("unused")
	private static void putFunc(String key, String val) {
		libFuncMap.put(key, val);
	}

	public static boolean containsFunc(String key) {
		return libFuncMap.containsKey(key);
	}

}
