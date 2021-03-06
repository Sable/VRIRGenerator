package natlab.backends.vrirGen;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Map;

import ast.Function;
import natlab.backends.vrirGen.WrapperGenFactory.TargetLang;
import natlab.tame.BasicTamerTool;
import natlab.tame.callgraph.SimpleFunctionCollection;
import natlab.tame.callgraph.StaticFunction;
import natlab.tame.classes.reference.PrimitiveClassReference;
import natlab.tame.valueanalysis.ValueAnalysis;
import natlab.tame.valueanalysis.aggrvalue.AggrValue;
import natlab.tame.valueanalysis.basicmatrix.BasicMatrixValue;
import natlab.tame.valueanalysis.value.Args;
import natlab.tame.valueanalysis.value.Res;
import natlab.toolkits.filehandling.GenericFile;
import natlab.toolkits.path.FileEnvironment;

public class MexWrapperGenerator implements WrapperGenerator {
	StaticFunction func;
	ValueAnalysis<AggrValue<BasicMatrixValue>> analysis;
	int graphIndex;

	// private ArrayList<String> headerList = new ArrayList<String>();
	public static void main(String args[]) {
		String fileDir = "capr";
		String fileName = "capacitor.m";
		// Map<String, String> dirMap = DirToEntryPointMapper.getMap();
		// for (String rootDir : DirToEntryPointMapper.getMap().keySet()) {
		// fileDir = rootDir;
		// fileName = dirMap.get(rootDir);
		String fileIn = fileDir + "/" + fileName;
		File file = new File(fileIn);
		GenericFile gFile = GenericFile.create(file.getAbsolutePath());
		String[] inputArgs = null;
		String[] testArgs = Main.getArgs(fileDir, fileName.split("\\.")[0]);
		if (testArgs != null) {
			inputArgs = testArgs;
		} else if (args.length > 0) {
			inputArgs = args;
		} else {
			throw new NullPointerException("arguments not provided");
		}
		FileEnvironment env = new FileEnvironment(gFile); // get path
		SimpleFunctionCollection.convertColonToRange = true;
		BasicTamerTool.setDoIntOk(false);
		ValueAnalysis<AggrValue<BasicMatrixValue>> analysis = BasicTamerTool
				.analyze(inputArgs, env);

		int size = analysis.getNodeList().size();
		WrapperGenerator wrapper = WrapperGenFactory.getWrapperGen(
				TargetLang.MEX, analysis.getMainNode().getFunction(), analysis,
				0);
		String wrapperStr = wrapper.genWrapper();
		file = new File(fileName.split("\\.")[0] + ".cpp");
		System.out.println("file name " + fileIn.split("\\.")[0] + ".cpp");
		try {
			BufferedWriter writer;
			if (!file.exists()) {
				writer = Files.newBufferedWriter(
						Paths.get(file.getAbsolutePath()),
						StandardOpenOption.CREATE);
			} else {
				writer = Files.newBufferedWriter(
						Paths.get(file.getAbsolutePath()),
						StandardOpenOption.TRUNCATE_EXISTING);
			}
			System.out.println(wrapperStr);
			writer.write(wrapperStr);
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// }
	}

	public StaticFunction getFunc() {
		return func;
	}

	public void setFunc(StaticFunction func) {
		this.func = func;
	}

	public MexWrapperGenerator(StaticFunction fun,
			ValueAnalysis<AggrValue<BasicMatrixValue>> analysis, int graphIndex) {
		// TODO Auto-generated constructor stub
		this.func = fun;
		this.analysis = analysis;
		this.graphIndex = graphIndex;
	}

	@Override
	public String genWrapper() {
		// TODO Auto-generated method stub
		StringBuffer sb = new StringBuffer();
		MClassToClassIDMapper.init();
		sb.append(genHeader());
		sb.append(genFuncHead());
		sb.append(genGetterFunctions());
		sb.append(genFuncCall());
		sb.append(genAllocFunction());
		sb.append(genSetterFunc());
		sb.append(genFuncTail());

		return sb.toString();
	}

	private String genFuncTail() {
		return "}\n";
	}

	private String genSetterFunc() {
		Res<AggrValue<BasicMatrixValue>> resVal = analysis.getMainNode()
				.getAnalysis().getResult();
		if (analysis.getMainNode().getAnalysis().getResult().size() <= 0) {
			return "";
		}
		if (analysis.getMainNode().getAnalysis().getResult().size() == 1) {
			if (((BasicMatrixValue) (resVal.get(0).getSingleton())).getShape()
					.isScalar()) {
				if (((BasicMatrixValue) (resVal.get(0).getSingleton()))
						.getMatlabClass()
						.equals(PrimitiveClassReference.DOUBLE)) {
					return "";
				} else {
					return "(*static_cast<"+MClassToClassIDMapper.getVrScalarType(((BasicMatrixValue) (resVal.get(0).getSingleton()))
						.getMatlabClass(),((BasicMatrixValue) (resVal.get(0).getSingleton()))
						.getisComplexInfo().geticType()) + ">(mxGetData(plhs[0]))) = retVal;\n";  
				}
			}
			return "mxSetData(plhs[0],retVal.data);\n";
		}
		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < resVal.size(); i++) {
			if (((BasicMatrixValue) (resVal.get(i).getSingleton())).getShape()
					.isScalar()) {
				continue;
			}
			sb.append("mxSetData(plhs[" + i + "], retVal.ret_data" + i
					+ ".data);\n");

		}
		return sb.toString();

	}

	private String genAllocFunction() {
		if (analysis.getMainNode().getAnalysis().getResult().size() > 1) {
			return genMultAlloc();
		} else {
			return genSingleAlloc();
		}
	}

	private String genAllocStmt(String varName, BasicMatrixValue analysisVal) {
		if (analysisVal.getShape().isScalar()) {
			if (analysisVal.getMatlabClass().equals(
					PrimitiveClassReference.DOUBLE)) {
				return "mxCreateDoubleScalar(" + varName + ")";
			} else {
				return "mxCreateNumericMatrix(1,1,"
						+ MClassToClassIDMapper.getMxType(analysisVal
								.getMatlabClass())
						+ ","
						+ MClassToClassIDMapper.getMxComplexity(analysisVal
								.getisComplexInfo().geticType());
			}
		}
		return "mxCreateNumericArray("

				+ "0,"

				+ "NULL, "
				+ MClassToClassIDMapper.getMxType(analysisVal.getMatlabClass())
				+ ", "
				+ MClassToClassIDMapper.getMxComplexity(analysisVal
						.getisComplexInfo().geticType()) + ")";

	}

	private String genSingleAlloc() {
		if (analysis.getMainNode().getAnalysis().getResult().size() <= 0) {
			return "";
		}
		BasicMatrixValue analysisVal = (BasicMatrixValue) analysis
				.getMainNode().getAnalysis().getResult().get(0).getSingleton();
		if (analysisVal.getShape().isScalar()) {
			if (analysisVal.getMatlabClass().equals(
					PrimitiveClassReference.DOUBLE)) {
				return "plhs[0] = mxCreateDoubleScalar(retVal);\n";
			}
			return "plhs[0]" + genAllocStmt("retVal", analysisVal);

		} else {
			return "plhs[0] = " + genAllocStmt("retVal", analysisVal) + ";\n"
					+ genSetDimStmt("plhs[" + 0 + "]", "retVal") + ";\n";
		}
	}

	private String genSetDimStmt(String mxArrayName, String vrArrayName) {
		String setDimsStr = "mxSetDimensions(";
		setDimsStr += mxArrayName + ", ";
		setDimsStr += vrArrayName + ".dims" + ", ";
		setDimsStr += vrArrayName + ".ndims)";
		return setDimsStr;
	}

	private String genMultAlloc() {
		Res<AggrValue<BasicMatrixValue>> analysisResult = analysis
				.getMainNode().getAnalysis().getResult();
		StringBuffer sb = new StringBuffer();
		String structName = "retVal";
		for (int i = 0; i < analysisResult.size(); i++) {
			sb.append("plhs["
					+ i
					+ "] = "
					+ genAllocStmt(structName + ".ret_data" + i,
							(BasicMatrixValue) analysisResult.get(i)
									.getSingleton()) + ";\n");
			if (!((BasicMatrixValue) analysisResult.get(i).getSingleton())
					.getShape().isScalar()) {
				sb.append(genSetDimStmt("plhs[" + i + "]", structName
						+ ".ret_data" + i)
						+ ";\n");
			}
		}
		return sb.toString();
	}

	private String genGetterFunctions() {
		StringBuffer sb = new StringBuffer();
		Args<AggrValue<BasicMatrixValue>> args = analysis.getMainNode()
				.getAnalysis().getArgs();
		Function func = analysis.getMainNode().getFunction().getAst();
		for (int i = 0; i < args.size(); i++) {
			AggrValue<BasicMatrixValue> arg = args.get(i);
			PrimitiveClassReference type = (PrimitiveClassReference) arg
					.getMatlabClass();
			String complexity = ((BasicMatrixValue) arg).getisComplexInfo()
					.geticType();
			String getStr, typeStr;
			if (((BasicMatrixValue) arg).getShape().isScalar()) {
				typeStr = MClassToClassIDMapper.getVrScalarType(type,
						complexity);
				getStr = typeStr + " "
						+ func.getInputParams().getChild(i).getID() + " = "
						+ "static_cast<" + typeStr + ">(mxGetScalar(rhs[" + i
						+ "]));\n";
			} else {
				typeStr = MClassToClassIDMapper.getVrType(type, complexity);
				getStr = typeStr + " inputData" + i + " = "
						+ MClassToClassIDMapper.getFunc(type, complexity)
						+ "(rhs[" + i + "]);\n";
			}
			sb.append(getStr);
		}
		return sb.toString();
	}

	private String genReturnStr() {
		if (analysis.getMainNode().getAnalysis().getResult().size() > 1) {
			return "struct_" + analysis.getMainNode().getFunction().getName()
					+ "_ret retVal";
		} else {
			if (analysis.getMainNode().getAnalysis().getResult().size() > 0) {
				PrimitiveClassReference type = (PrimitiveClassReference) analysis
						.getMainNode().getAnalysis().getResult().get(0)
						.getSingleton().getMatlabClass();
				String complexity = ((BasicMatrixValue) analysis.getMainNode()
						.getAnalysis().getResult().get(0).getSingleton())
						.getisComplexInfo().geticType();
				if (((BasicMatrixValue) analysis.getMainNode().getAnalysis()
						.getResult().get(0).getSingleton()).getShape()
						.isScalar()) {
					return MClassToClassIDMapper.getVrScalarType(type,
							complexity) + " retVal";
				}
				return MClassToClassIDMapper.getVrType(type, complexity)
						+ " retVal";
			} else {
				return null;
			}
		}
	}

	private String genFuncCall() {
		int numArgs = analysis.getMainNode().getAnalysis().getArgs().size();
		String retStr = genReturnStr();
		String funcStr = (retStr != null ? retStr + " = " : "")
				+ analysis.getMainNode().getFunction().getName() + "(";
		Function func = analysis.getMainNode().getFunction().getAst();
		for (int i = 0; i < numArgs; i++) {
			if (i != 0) {
				funcStr += ",";
			}
			funcStr += func.getInputParams().getChild(i).getID();
		}
		funcStr += ");\n";
		return funcStr;
	}

	private String genFuncHead() {
		return "void mexFunction(int nlhs, mxArray *plhs[], \n int nrhs,const mxArray *prhs[]) \n {\n "
				+ "mxArray ** rhs = const_cast<mxArray**>(prhs);\n";
	}

	private String genHeader() {
		StringBuffer sb = new StringBuffer();
		sb.append("#include<stdlib.h>\n");
		sb.append("#include<stdio.h>\n");
		sb.append("#include<mex.h>\n");
		sb.append("#include\"matrix_ops.hpp\"\n");
		sb.append("#include\"" + analysis.getMainNode().getFunction().getName()
				+ "Impl.hpp\"\n");
		return sb.toString();

	}

}
