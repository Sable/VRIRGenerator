package natlab.backends.vrirGen;

import java.util.Map;
import java.util.HashMap;

public class DirToEntryPointMapper {
	private static Map<String, String> dirToEntryPointMap = null;

	private static void init() {
		if (dirToEntryPointMap != null) {
			return;
		}
		dirToEntryPointMap = new HashMap<String, String>();
		dirToEntryPointMap.put("adpt", "adapt.m");
		dirToEntryPointMap.put("bbai", "babai.m");
		dirToEntryPointMap.put("bubble", "bubble.m");
		dirToEntryPointMap.put("clos", "closure.m");
		dirToEntryPointMap.put("diff", "diffraction.m");
		dirToEntryPointMap.put("crni", "crnich.m");
		dirToEntryPointMap.put("fdtd", "fdtd.m");
		dirToEntryPointMap.put("fft", "fft_four1.m");
		dirToEntryPointMap.put("fiff", "finediff.m");
		dirToEntryPointMap.put("matmul", "matmul_p.m");
		dirToEntryPointMap.put("mbrt", "mandelbrot.m");
		dirToEntryPointMap.put("mcpi", "mcpi_p.m");
		dirToEntryPointMap.put("nb1d", "nbody1d.m");
		dirToEntryPointMap.put("nb3d", "nbody3d.m");
		dirToEntryPointMap.put("numprime", "prime_total.m");
		dirToEntryPointMap.put("optstop", "osp.m");
		dirToEntryPointMap.put("quadrature", "quad_par.m");
		dirToEntryPointMap.put("capr", "capacitor.m");
		// dirToEntryPointMap.put("scra", "scra.m");
		dirToEntryPointMap.put("spqr", "spqr.m");
	}

	public static Map<String, String> getMap() {
		if (dirToEntryPointMap == null) {
			init();
		}
		return dirToEntryPointMap;
	}

	public static void setMap(Map<String, String> dirToEntryPointMap) {
		DirToEntryPointMapper.dirToEntryPointMap = dirToEntryPointMap;
	}

}
