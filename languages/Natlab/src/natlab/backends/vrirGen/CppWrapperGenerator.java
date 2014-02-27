package natlab.backends.vrirGen;

import java.util.ArrayList;

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
		// sb.append("#include\"" + func.getName() + "_impml.h \"");

		return sb.toString();

	}

}
