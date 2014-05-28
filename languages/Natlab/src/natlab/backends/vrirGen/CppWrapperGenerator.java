package natlab.backends.vrirGen;

import natlab.tame.callgraph.StaticFunction;
import natlab.tame.classes.reference.PrimitiveClassReference;

public class CppWrapperGenerator extends WrapperGenerator {

	// private ArrayList<String> headerList = new ArrayList<String>();
	public static void main(String args[]) {
		CppWrapperGenerator cpp = new CppWrapperGenerator(null);

		System.out.println("output:\n" + cpp.genWrapper());
	}

	public StaticFunction getFunc() {
		return func;
	}

	public void setFunc(StaticFunction func) {
		this.func = func;
	}

	public CppWrapperGenerator(StaticFunction fun) {
		// TODO Auto-generated constructor stub
		super(fun);

	}

	@Override
	public String genWrapper() {
		// TODO Auto-generated method stub
		StringBuffer sb = new StringBuffer();
		sb.append(genHeader());
		sb.append("void mexFunction(int nlhs, mxArray *plhs[], \n int nrhs,const mxArray *prhs[]) \n {\n ");
		sb.append(func.getName() + "(");
		if (func.getAst().getInputParamList().getNumChild() > 0) {
			sb.append("prhs[0]");
			for (int i = 1; i < func.getAst().getInputParamList().getNumChild(); i++) {
				sb.append(",prhs[" + i + "]");
			}
		}
		if (func.getAst().getOutputParamList().getNumChild() > 0) {
			sb.append(',');
			sb.append("plhs[0]");
			for (int i = 1; i < func.getAst().getOutputParamList()
					.getNumChild(); i++) {
				sb.append(",plhs[" + i + "]");
			}
		}
		sb.append(");\n");
		sb.append("}\n");

		return sb.toString();

	}

	private String genAllocStmt(PrimitiveClassReference type,
			boolean complexity, int ndims, int[] dims, String arrName) {
		StringBuffer sb = new StringBuffer();

		sb.append("mxCreateNumericArray(" + MClassToClassIDMapper.get(type)
				+ "," + " )\n");

		return sb.toString();
	}

	private String genHeader() {
		StringBuffer sb = new StringBuffer();
		sb.append("#include<stdlib.h>\n");
		sb.append("#include<stdio.h>\n");
		sb.append("#include<mex.h>\n");
		sb.append("#include\"matrix_ops.hpp\"\n");
		// sb.append("#include\"" + func.getName() + "_impml.h \"");

		return sb.toString();

	}

}
