package natlab.backends.vrirGen;

import natlab.tame.callgraph.StaticFunction;
import natlab.tame.valueanalysis.ValueAnalysis;
import natlab.tame.valueanalysis.aggrvalue.AggrValue;
import natlab.tame.valueanalysis.basicmatrix.BasicMatrixValue;

public class WrapperGenFactory {
	public enum TargetLang {
		MEX("C++");
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
			StaticFunction func,ValueAnalysis<AggrValue<BasicMatrixValue>> analysis,int graphIndex) {
		switch (lang) {
		case MEX:
			return new MexWrapperGenerator(func, analysis,graphIndex);
		}
		System.err.println("Only C++ is currently supported ");
		return null;
	}
}
