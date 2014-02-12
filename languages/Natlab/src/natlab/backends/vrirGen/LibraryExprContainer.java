package natlab.backends.vrirGen;

import java.util.HashSet;
import java.util.Set;

public class LibraryExprContainer {

	static Set<String> libSet;

	public static void init() {
		libSet = new HashSet<String>();
		libSet.add("sqrt");
		libSet.add("log2");
		libSet.add("loge");
		libSet.add("log10");
		libSet.add("exp10");
		libSet.add("sin");
		libSet.add("asin");
		libSet.add("cos");
		libSet.add("acos");
		libSet.add("tan");
		libSet.add("atan");
		libSet.add("atan2");
		libSet.add("pow");
		libSet.add("abs");
	}

	public static boolean isLibExpr(String funcName) {
		return libSet.contains(funcName);
	}
}
