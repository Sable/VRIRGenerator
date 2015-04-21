package natlab.backends.vrirGen;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import natlab.backends.vrirGen.WrapperGenFactory.TargetLang;
import natlab.options.Options;
import natlab.options.VRIROptions;
import natlab.tame.BasicTamerTool;
import natlab.tame.callgraph.SimpleFunctionCollection;
import natlab.tame.callgraph.StaticFunction;
import natlab.tame.tamerplus.analysis.AnalysisEngine;
import natlab.tame.tamerplus.transformation.TransformationEngine;
import natlab.tame.valueanalysis.ValueAnalysis;
import natlab.tame.valueanalysis.ValueFlowMap;
import natlab.tame.valueanalysis.aggrvalue.AggrValue;
import natlab.tame.valueanalysis.basicmatrix.BasicMatrixValue;
import natlab.toolkits.filehandling.GenericFile;
import natlab.toolkits.path.FileEnvironment;
import ast.ASTNode;
import ast.Function;

public class VRIRGenerator {
	public static boolean DEBUG = false;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		/**
		 * This main method is just for testing, doesn't follow the convention
		 * when passing a file to a program, please replace "fileDir and fileIn"
		 * below with your real testing file directory and its name, and you can
		 * pass the type info of the input argument to the program, currently,
		 * the type info is composed like double&3*3&REAL.
		 */

		String fileDir = "matmul";
		String fileName = "matmul_p.m";
		// Map<String, String> dirMap = DirToEntryPointMapper.getMap();
		// for (String rootDir : DirToEntryPointMapper.getMap().keySet()) {
		// fileDir = rootDir;
		// fileName = dirMap.get(rootDir);

		String fileIn = fileDir + "/" + fileName;
		File file = new File(fileIn);
		GenericFile gFile = GenericFile.create(file.getAbsolutePath());
		String[] inputArgs = null;
		String[] testArgs = VRIRGenerator.getArgs(fileDir,
				fileName.split("\\.")[0]);
		if (testArgs != null) {
			inputArgs = testArgs;
		} else if (args.length > 0) {
			inputArgs = args;
		} else {
			throw new NullPointerException("arguments not provided");
		}

		FileEnvironment env = new FileEnvironment(gFile); // get path
		SimpleFunctionCollection.convertColonToRange = true;
		BasicTamerTool.setDoIntOk(true);

		ValueAnalysis<AggrValue<BasicMatrixValue>> analysis = BasicTamerTool
				.analyze(inputArgs, env);
		int size = analysis.getNodeList().size();
		WrapperGenerator wrapper = WrapperGenFactory.getWrapperGen(
				TargetLang.MEX, analysis.getMainNode().getFunction(), analysis,
				0);
		String glueCode = wrapper.genWrapper();
		StringBuffer genXML = new StringBuffer();
		VrirXmlGen.genModuleXMLHead(genXML, fileName.split("\\.")[0]);
		genXML.append(HelperClass.toXMLHead("fns"));
		OperatorMapper.initMap();
		VrirTypeMapper.initTypeMap();
		HashSet<StaticFunction> funcSet = new HashSet<StaticFunction>();
		for (int i = 0; i < size; i++) {
			StringBuffer sb;
			/*
			 * type inference.
			 */
			ValueFlowMap<AggrValue<BasicMatrixValue>> currentOutSet = analysis
					.getNodeList().get(i).getAnalysis().getCurrentOutSet();

			/*
			 * tamer plus analysis.
			 */
			StaticFunction function = analysis.getNodeList().get(i)
					.getFunction();
			if (DEBUG) {
				System.out.println("Analysis function  " + function.getName());
			}
			if (!funcSet.contains(function)) {
				if (function.getAst().getVarName().equals("pForFunc")) {
					continue;
				}
				TransformationEngine transformationEngine = TransformationEngine
						.forAST(function.getAst());

				AnalysisEngine analysisEngine = transformationEngine
						.getAnalysisEngine();
				@SuppressWarnings("rawtypes")
				ASTNode fTree = transformationEngine
						.getTIRToMcSAFIRWithoutTemp().getTransformedTree();
				Set<String> remainingVars = analysisEngine
						.getTemporaryVariablesRemovalAnalysis()
						.getRemainingVariablesNames();
				if (DEBUG) {
					System.out.println("\ntamer plus analysis result: \n"
							+ fTree.getPrettyPrinted() + "\n");
				}
				try {
					sb = VrirXmlGen.generateVrir((Function) fTree,
							remainingVars, analysis, currentOutSet, i, size,
							analysisEngine);
					genXML.append(sb);
				} catch (RuntimeException e) {
					System.out
							.println("did not work for " + function.getName());
					System.out.println(fTree.getPrettyPrinted());
					e.printStackTrace();
					System.exit(0);
				}
			}
			funcSet.add(function);
		}
		genXML.append(HelperClass.toXMLTail());

		VrirXmlGen.genModuleXMLTail(genXML);
		if (DEBUG) {
			System.out.println(" print the generated VRIR in XML format  .\n");
		}
		System.out.println(genXML);
		try {
			BufferedWriter buffer = Files.newBufferedWriter(
					Paths.get(fileName.split("\\.")[0] + ".vrir"),
					Charset.forName("US-ASCII"));
			buffer.write(genXML.toString());
			buffer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// }

	}

	public static void init() {
		OperatorMapper.initMap();
		VrirTypeMapper.initTypeMap();
	}

	public static ValueAnalysis<AggrValue<BasicMatrixValue>> getTamerAnalysis(
			FileEnvironment env, String[] args) {
		return BasicTamerTool.analyze(args, env);
	}

	public static boolean isParallelFunction(StaticFunction function) {
		if (function.getAst().getVarName().equals("pForFunc")) {
			return true;
		}
		return false;
	}

	public static TransformationEngine getTransformationEngine(
			StaticFunction function) {
		return TransformationEngine.forAST(function.getAst());
	}

	public static String genMexWrapper(
			ValueAnalysis<AggrValue<BasicMatrixValue>> analysis) {
		WrapperGenerator wrapper = WrapperGenFactory.getWrapperGen(
				TargetLang.MEX, analysis.getMainNode().getFunction(), analysis,
				0);
		return wrapper.genWrapper();
	}

	public static String generateVrir(FileEnvironment env, String args[]) {
		ValueAnalysis<AggrValue<BasicMatrixValue>> analysis = getTamerAnalysis(
				env, args);
		init();
		WrapperGenerator wrapper = WrapperGenFactory.getWrapperGen(
				TargetLang.MEX, analysis.getMainNode().getFunction(), analysis,
				0);
		StringBuffer genXML = new StringBuffer();
		VrirXmlGen.genModuleXMLHead(genXML, env.getMainFile().toString());

		genXML.append(HelperClass.toXMLHead("fns"));
		int size = analysis.getNodeList().size();
		HashSet<StaticFunction> funcSet = new HashSet<StaticFunction>();
		for (int i = 0; i < size; i++) {
			StringBuffer sb;
			StaticFunction function = analysis.getNodeList().get(i)
					.getFunction();
			if (DEBUG) {
				System.out.println("Analysis function  " + function.getName());
			}
			if (funcSet.contains(function)) {
				continue;
			}
			if (isParallelFunction(function)) {
				continue;
			}
			TransformationEngine transformationEngine = getTransformationEngine(function);
			AnalysisEngine analysisEngine = transformationEngine
					.getAnalysisEngine();
			@SuppressWarnings("rawtypes")
			ASTNode fTree = transformationEngine.getTIRToMcSAFIRWithoutTemp()
					.getTransformedTree();
			Set<String> remainingVars = analysisEngine
					.getTemporaryVariablesRemovalAnalysis()
					.getRemainingVariablesNames();
			if (DEBUG) {
				System.out.println("\ntamer plus analysis result: \n"
						+ fTree.getPrettyPrinted() + "\n");
			}
			try {
				ValueFlowMap<AggrValue<BasicMatrixValue>> currentOutSet = analysis
						.getNodeList().get(i).getAnalysis().getCurrentOutSet();
				sb = VrirXmlGen.generateVrir((Function) fTree, remainingVars,
						analysis, currentOutSet, i, size, analysisEngine);
				genXML.append(sb);
			} catch (RuntimeException e) {
				System.out.println("Error while compiling function :"
						+ function.getName());
				System.out.println(fTree.getPrettyPrinted());
				e.printStackTrace();

			}

		}
		genXML.append(HelperClass.toXMLTail());
		VrirXmlGen.genModuleXMLTail(genXML);
		return genXML.toString();
	}

	public static void compile(VRIROptions options) {
		FileEnvironment env = new FileEnvironment(options);
		String args[] = null;
		if (options.arguments() != null || options.arguments().length() > 0) {
			args = options.arguments().split(" ");
		}
		if (options.vrir()) {
			if (DEBUG) {
				System.out.println(" print the generated VRIR in XML format.");
			}
			String genXML = generateVrir(env, args);
			System.out.println(genXML);
			writeToFile(Paths.get(options.main().split("\\.")[0] + ".vrir"),
					genXML.toString());
		} else if (options.mex()) {
			if (DEBUG) {
				System.out.println("print the generated Mex Function.");
			}
			String genMex = generateMex(env, args);
			System.out.println(genMex);
			writeToFile(Paths.get(options.main().split("\\.")[0] + ".cpp"),
					genMex.toString());
		}

	}

	public static String generateMex(FileEnvironment env, String[] args) {
		ValueAnalysis<AggrValue<BasicMatrixValue>> analysis = getTamerAnalysis(
				env, args);
		return genMexWrapper(analysis);
	}

	public static void writeToFile(Path filePath, String content) {
		try {

			BufferedWriter buffer = Files.newBufferedWriter(filePath,
					Charset.forName("US-ASCII"));
			buffer.write(content);
			buffer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String[] getArgs(String rootDir, String funcName) {
		JSONParser parser = new JSONParser();
		try {
			JSONObject obj = (JSONObject) parser.parse(new FileReader(new File(
					rootDir + "/inputArgs.json")));
			return ((String) obj.get(funcName)).trim().split(" ");

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}