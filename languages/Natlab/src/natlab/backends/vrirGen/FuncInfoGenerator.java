package natlab.backends.vrirGen;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

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
		String fileDir = "adpt/";
		String fileName = "drv_adpt.m";
		String fileIn = fileDir + fileName;
		File file = new File(fileIn);
		GenericFile gFile = GenericFile.create(file.getAbsolutePath());
		FileEnvironment env = new FileEnvironment(gFile);
		ValueAnalysis<AggrValue<BasicMatrixValue>> analysis = BasicTamerTool
				.analyze(args, env);
		FuncInfoGenerator gen = new FuncInfoGenerator(analysis, fileDir);
		Map<String, String> map = gen.genArgsStr();
		gen.writeToFile(map);
	}

	ValueAnalysis<AggrValue<BasicMatrixValue>> analysis;
	String rootDir;

	FuncInfoGenerator(ValueAnalysis<AggrValue<BasicMatrixValue>> analysis,
			String rootDir) {
		this.analysis = analysis;
		this.rootDir = rootDir;

	}

	public Map<String, String> genArgsStr() {
		HashSet<StaticFunction> set = new HashSet();
		HashMap funcArgMap = new HashMap<String, String>();
		for (int i = 0; i < analysis.getNodeList().size(); i++) {
			if (!set.contains(analysis.getNodeList().get(i).getFunction())) {
				set.add(analysis.getNodeList().get(i).getFunction());
				StaticFunction func = analysis.getNodeList().get(i)
						.getFunction();
				System.out.println("function Name " + func.getName());
				Args<AggrValue<BasicMatrixValue>> args = analysis.getNodeList()
						.get(i).getAnalysis().getArgs();
				String argStr = "%";
				for (int j = 0; j < args.size(); j++) {
					argStr += " " + genArgStr(args.get(j));
				}
				argStr += "\n";
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
			shapeStr += dim.hasIntValue() ? dim.getIntValue() : "?";
		}
		return shapeStr;
	}

	public String genComplexityStr() {
		return null;
	}

	public void writeToFile(Map<String, String> funcMap) {
		for (String func : funcMap.keySet()) {
			File file = new File(rootDir + func + ".m");
			if (file.exists()) {

				Path path = Paths.get(file.getAbsolutePath());
				try {
					RandomAccessFile writer = (new RandomAccessFile(file, "rw"));
					writer.seek(0);
					writer.writeChars(funcMap.get(func));
					writer.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		}
	}

}
