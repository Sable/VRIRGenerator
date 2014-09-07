package natlab.backends.vrirGen;

import java.io.File;

import natlab.tame.BasicTamerTool;
import natlab.tame.valueanalysis.ValueAnalysis;
import natlab.tame.valueanalysis.aggrvalue.AggrValue;
import natlab.tame.valueanalysis.basicmatrix.BasicMatrixValue;
import natlab.toolkits.filehandling.GenericFile;
import natlab.toolkits.path.FileEnvironment;

public class FuncInfoGenerator {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String fileDir = "adpt/";
		String fileName = "adapt.m";
		String fileIn = fileDir + fileName;
		File file = new File(fileIn);
		GenericFile gFile = GenericFile.create(file.getAbsolutePath());
		FileEnvironment env = new FileEnvironment(gFile);
		ValueAnalysis<AggrValue<BasicMatrixValue>> analysis = BasicTamerTool
				.analyze(args, env);
	}

	ValueAnalysis<AggrValue<BasicMatrixValue>> analysis;
	int graphIndex;

	FuncInfoGenerator(ValueAnalysis<AggrValue<BasicMatrixValue>> analysis,
			int graphIndex, String rootDir) {
		this.analysis = analysis;
		this.graphIndex = graphIndex;
	}

	public String genArgStr() {
		for (int i = 0; i < analysis.getNodeList().size(); i++) {
			System.out.println(analysis.getNodeList().get(i).getFunction()
					.getName());
		}
		return null;
	}

	public String genDataStr() {
		return null;
	}

	public String genShapeStr() {
		return null;
	}

	public String genComplexityStr() {
		return null;
	}

}
