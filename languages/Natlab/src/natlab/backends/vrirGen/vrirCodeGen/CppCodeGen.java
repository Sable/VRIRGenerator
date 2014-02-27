package natlab.backends.vrirGen.vrirCodeGen;

public class CppCodeGen {
	static {
		// System.setProperty(
		// "java.library.path",
		// System.getProperty("java.library.path")
		// +
		// "/home/2012/sjagda/mclab/languages/Natlab/src/natlab/backends/vrirGen/vrirCodeGen/jni/");
		System.loadLibrary("CppCode");
	}

	public native String genCode(String str);

	public static void main(String args[]) {
		CppCodeGen gen = new CppCodeGen();

	}



}
