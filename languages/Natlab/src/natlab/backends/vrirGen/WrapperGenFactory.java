package natlab.backends.vrirGen;

import natlab.tame.callgraph.StaticFunction;

public class WrapperGenFactory {
	public enum TargetLang {
		Cpp("C++");
		private String str;

		public String getStr() {
			return str;
		}

		TargetLang(String str) {
			this.str = str;
		}
	}

	public WrapperGenFactory() {
		// TODO Auto-generated constructor stub
	}

	public static WrapperGenerator getWrapperGen(TargetLang lang,
			StaticFunction func) {
		switch (lang) {
		case Cpp:
			return new CppWrapperGenerator(func);

		}
		System.err.println("Only C++ is currently supported ");
		return null;
	}
}
