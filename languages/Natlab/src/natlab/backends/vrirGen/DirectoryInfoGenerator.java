package natlab.backends.vrirGen;

import java.io.File;
import java.util.Map;
import java.util.HashMap;

import natlab.tame.BasicTamerTool;
import natlab.tame.valueanalysis.ValueAnalysis;
import natlab.tame.valueanalysis.aggrvalue.AggrValue;
import natlab.tame.valueanalysis.basicmatrix.BasicMatrixValue;
import natlab.toolkits.filehandling.GenericFile;
import natlab.toolkits.path.FileEnvironment;

public class DirectoryInfoGenerator {
	private static Map<String, String> dirToDriverMap;

	public static void initMap() {
		if (dirToDriverMap == null) {
			dirToDriverMap = new HashMap<String, String>();
		} else {
			dirToDriverMap.clear();
		}
		dirToDriverMap.put("adpt", "drv_adpt.m");
		dirToDriverMap.put("arsim", "drv_arsim.m");
		dirToDriverMap.put("bbai", "drv_babai.m");
		dirToDriverMap.put("capr", "drv_capr.m");
		dirToDriverMap.put("clos", "drv_clos.m");
		dirToDriverMap.put("crni", "drv_crni.m");
		dirToDriverMap.put("diff", "drv_diff.m");
		dirToDriverMap.put("lgdr", "drv_lgdr.m");
		dirToDriverMap.put("matmul", "drv_matmul_p.m");
		dirToDriverMap.put("mbrt", "drv_mbrt.m");
		dirToDriverMap.put("mcpi", "drv_mcpi_p.m");
		dirToDriverMap.put("nb1d", "drv_nb1d.m");
		dirToDriverMap.put("nb3d", "drv_nb3d.m");
		dirToDriverMap.put("numprime", "drv_prime.m");
		dirToDriverMap.put("optstop", "drv_osp.m");
		dirToDriverMap.put("quadrature", "drv_quad.m");
		dirToDriverMap.put("scra", "drv_scra.m");
		dirToDriverMap.put("spqr", "drv_spqr.m");
		dirToDriverMap.put("bubble", "drv_bubble.m");
		dirToDriverMap.put("fdtd", "drv_fdtd.m");
		dirToDriverMap.put("fft", "drv_fft.m");
		dirToDriverMap.put("fiff", "drv_fiff.m");
	}

	public void walk(String[] args) {
		for (String dir : dirToDriverMap.keySet()) {
			String fileDir = dir;
			String fileName = dirToDriverMap.get(dir);
			String fileIn = fileDir + "/" + fileName;
			File file = new File(fileIn);
			GenericFile gFile = GenericFile.create(file.getAbsolutePath());
			FileEnvironment env = new FileEnvironment(gFile);
			ValueAnalysis<AggrValue<BasicMatrixValue>> analysis = null;
			try {
				analysis = BasicTamerTool.analyze(args, env);
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
			BasicTamerTool.setDoIntOk(false);
			FuncInfoGenerator.genFuncInfo(fileDir, analysis);
			System.out.println("fileDir " + fileDir);
		}
	}

	public static void main(String[] args) {
		if (args.length <= 0) {
			throw new UnsupportedOperationException("args array cannot be null");
		}
		initMap();
		(new DirectoryInfoGenerator()).walk(args);

	}

}
