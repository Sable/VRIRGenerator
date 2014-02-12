package natlab.backends.vrirGen;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import natlab.tame.AdvancedTamerTool;
import natlab.tame.callgraph.StaticFunction;
import natlab.tame.tamerplus.analysis.AnalysisEngine;
import natlab.tame.tamerplus.transformation.TransformationEngine;
import natlab.tame.valueanalysis.ValueAnalysis;
import natlab.tame.valueanalysis.ValueFlowMap;
import natlab.tame.valueanalysis.advancedMatrix.AdvancedMatrixValue;
import natlab.tame.valueanalysis.aggrvalue.AggrValue;
import natlab.toolkits.filehandling.GenericFile;
import natlab.toolkits.path.FileEnvironment;
import ast.ASTNode;
import ast.Function;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		/**
		 * This main method is just for testing, doesn't follow the convention
		 * when passing a file to a program, please replace "fileDir and fileIn"
		 * below with your real testing file directory and its name, and you can
		 * pass the type info of the input argument to the program, currently,
		 * the type info is composed like double&3*3&REAL.
		 */
		String fileDir = "/home/2012/sjagda/mclab/";
		// String fileName = "drv_mbrt.m";
		// String fileDir = File.separator + "home" + File.separator
		// + "2012" + "sjagda" + File.separator + "mclab"
		// + File.separator + "mbrt" + File.separator;
		String fileName = "simple.m";
		String fileIn = fileDir + fileName;
		GenericFile gFile = GenericFile.create(fileIn);
		FileEnvironment env = new FileEnvironment(gFile); // get path
															// environment obj
		AdvancedTamerTool tool = new AdvancedTamerTool();
		ValueAnalysis<AggrValue<AdvancedMatrixValue>> analysis = tool.analyze(
				args, env);

		int size = analysis.getNodeList().size();

		StringBuffer genXML = new StringBuffer();
		VrirXmlGen.genModuleXMLHead(genXML, fileName.split("\\.")[0]);
		genXML.append(HelperClass.toXML("fns"));
		OperatorMapper.initMap();
		VrirTypeMapper.initTypeMap();
		LibraryExprContainer.init();
		HashSet<StaticFunction> funcSet = new HashSet<StaticFunction>();
		for (int i = 0; i < size; i++) {
			StringBuffer sb;
			/*
			 * type inference.
			 */

			ValueFlowMap<AggrValue<AdvancedMatrixValue>> currentOutSet = analysis
					.getNodeList().get(i).getAnalysis().getCurrentOutSet();

			/*
			 * tamer plus analysis.
			 */
			StaticFunction function = analysis.getNodeList().get(i)
					.getFunction();
			System.out.println("Analysis function  " + function.getName());
			if (!funcSet.contains(function)) {
				// TamerPlusUtils.debugMode();
				// System.out.println("tamer pretty print: \n"+function.getAst().getPrettyPrinted());

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

				System.out.println("\ntamer plus analysis result: \n"
						+ fTree.getPrettyPrinted() + "\n");

				sb = VrirXmlGen.generateVrir((Function) fTree, remainingVars,
						analysis, currentOutSet, i, size, analysisEngine);
				genXML.append(sb);

			}
			funcSet.add(function);
		}
		genXML.append(HelperClass.toXML("/fns"));

		VrirXmlGen.genModuleXMLTail(genXML);
		System.out.println(" print the generated VRIR in XML format  .\n");
		 System.err.println(genXML);

		try {
			BufferedWriter buffer = Files.newBufferedWriter(
					Paths.get(fileName.split("\\.")[0] + ".xml"),
					Charset.forName("US-ASCII"));
			buffer.write(genXML.toString());
			buffer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
