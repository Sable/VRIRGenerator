package natlab.backends.vrirGen;

import java.util.Map;
import java.util.HashMap;

public class DirectoryMapper {
	public static Map<String, String> directoryMap;
	
	public void init(){
		if (directoryMap == null) {
			directoryMap = new HashMap<String, String>();
		} else {
			directoryMap.clear();
		}
		directoryMap.put("adpt", "drv_adpt.m");
		directoryMap.put("arsim", "drv_arsim.m");
		directoryMap.put("bbai", "drv_babai.m");
		directoryMap.put("capr", "drv_capr.m");
		directoryMap.put("clos", "drv_clos.m");
		directoryMap.put("crni", "drv_crni.m");
		directoryMap.put("diff", "drv_diff.m");
		directoryMap.put("lgdr", "drv_lgdr.m");
		directoryMap.put("matmul", "drv_matmul_p.m");
		directoryMap.put("mbrt", "drv_mbrt.m");
		directoryMap.put("mcpi", "drv_mcpi_p.m");
		directoryMap.put("nb1d", "drv_nb1d.m");
		directoryMap.put("nb3d", "drv_nb3d.m");
		directoryMap.put("numprime", "drv_prime.m");
		directoryMap.put("optstop", "drv_osp.m");
		directoryMap.put("quadrature", "drv_quad.m");
		directoryMap.put("scra", "drv_scra.m");
		directoryMap.put("spqr", "drv_spqr.m");
		directoryMap.put("bubble", "drv_bubble.m");
		directoryMap.put("fdtd", "drv_fdtd.m");
		directoryMap.put("fft", "drv_fft.m");
		directoryMap.put("fiff", "drv_fiff.m");
	}
}
