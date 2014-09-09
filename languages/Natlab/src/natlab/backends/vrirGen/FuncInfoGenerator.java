package natlab.backends.vrirGen;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.sun.org.apache.bcel.internal.generic.FNEG;

import natlab.tame.BasicTamerTool;
import natlab.tame.callgraph.StaticFunction;
import natlab.tame.valueanalysis.ValueAnalysis;
import natlab.tame.valueanalysis.aggrvalue.AggrValue;
import natlab.tame.valueanalysis.basicmatrix.BasicMatrixValue;
import natlab.tame.valueanalysis.components.shape.DimValue;
import natlab.tame.valueanalysis.components.shape.Shape;
import natlab.tame.valueanalysis.value.Args;
import natlab.toolkits.filehandling.GenericFile;
import natlab.toolkits.path.FileEnvironment;

public class FuncInfoGenerator {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String fileDir = "scra/";
		String fileName = "drv_scra.m";
		String fileIn = fileDir + fileName;
		File file = new File(fileIn);
		GenericFile gFile = GenericFile.create(file.getAbsolutePath());
		BasicTamerTool.setDoIntOk(false);
		FileEnvironment env = new FileEnvironment(gFile);
		ValueAnalysis<AggrValue<BasicMatrixValue>> analysis = BasicTamerTool
				.analyze(args, env);
		FuncInfoGenerator.genFuncInfo(fileDir, analysis);

	}

	ValueAnalysis<AggrValue<BasicMatrixValue>> analysis;
	String rootDir;
	Map<String, String> funcArgMap = new HashMap<String, String>();

	FuncInfoGenerator(ValueAnalysis<AggrValue<BasicMatrixValue>> analysis,
			String rootDir) {
		this.analysis = analysis;
		this.rootDir = rootDir;

	}

	public static void genFuncInfo(String rootDir,
			ValueAnalysis<AggrValue<BasicMatrixValue>> analysis) {
		FuncInfoGenerator gen = new FuncInfoGenerator(analysis, rootDir);
		Map<String, String> map = gen.genArgsStrMap();
		gen.writeToFile(gen.toJSONString());

	}

	public Map<String, String> genArgsStrMap() {
		HashSet<StaticFunction> set = new HashSet();
		for (int i = 0; i < analysis.getNodeList().size(); i++) {
			if (!set.contains(analysis.getNodeList().get(i).getFunction())) {
				set.add(analysis.getNodeList().get(i).getFunction());
				StaticFunction func = analysis.getNodeList().get(i)
						.getFunction();
				Args<AggrValue<BasicMatrixValue>> args = analysis.getNodeList()
						.get(i).getAnalysis().getArgs();
				String argStr = "";
				for (int j = 0; j < args.size(); j++) {
					argStr += genArgStr(args.get(j)) + " ";
				}
				funcArgMap.put(func.getName(), argStr);
			}
		}
		return funcArgMap;
	}

	public String genArgStr(AggrValue<BasicMatrixValue> arg) {
		if (arg instanceof BasicMatrixValue) {
			return arg.getMatlabClass() + "&"
					+ genShapeStr(((BasicMatrixValue) arg).getShape()) + "&"
					+ ((BasicMatrixValue) arg).getisComplexInfo().geticType();
		} else {
			return null;
		}
	}

	public String genDataStr(String node, int graphIndex) {
		return HelperClass.getDataType(node, analysis, graphIndex).toString();

	}

	public String genShapeStr(Shape<AggrValue<BasicMatrixValue>> shape) {
		String shapeStr = "";
		boolean flag = false;
		for (DimValue dim : shape.getDimensions()) {
			if (flag) {
				shapeStr += "*";
			} else {
				flag = true;
			}

			shapeStr += dim.hasIntValue() && dim.equalsOne() ? dim
					.getIntValue() : "?";
		}
		return shapeStr;
	}

	public String genComplexityStr() {
		return null;
	}

	public String toJSONString() {
		return (new JSONObject(funcArgMap)).toJSONString();

	}

	public void writeToFile(String str) {
		File file = new File(rootDir + "/inputArgs.json");
		Path path = Paths.get(file.getAbsolutePath());
		try {
			BufferedWriter writer;
			if (!file.exists()) {
				writer = Files.newBufferedWriter(path,
						StandardOpenOption.CREATE_NEW);
			} else {
				writer = Files.newBufferedWriter(path,
						StandardOpenOption.TRUNCATE_EXISTING);
			}
			writer.write(str);
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
