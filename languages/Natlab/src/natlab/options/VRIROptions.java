package natlab.options;

import com.beust.jcommander.Parameter;

;

public class VRIROptions extends Options {
	@Parameter(names = "--vrir", description = "Generate VRIR from given Matlab file")
	protected boolean vrir = false;

	public boolean vrir() {
		return vrir;
	}

	@Parameter(names = "--mex", description = "Generate Mex Wrapper for Matlab file")
	protected boolean mex = false;

	public boolean mex() {
		return mex;
	}

	@Parameter(names = "--argsFile", description = "Name of JSON file containing Input arguments")
	protected String argsFile = "";

	public String argsFile() {
		return argsFile;
	}

	@Parameter(names = "--outFile", description = "Path and name of output file")
	protected String outFile = "";

	public String outFile() {
		return outFile;
	}

}
