package natlab.backends.vrirGen;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import natlab.tame.classes.reference.PrimitiveClassReference;

public final class MClassToClassIDMapper {
	private static Map<PrimitiveClassReference, String> typeMap;
	private static Table<PrimitiveClassReference, String, String> vrTypeTable = HashBasedTable
			.create();
	private static Table<PrimitiveClassReference, String, String> getterFuncTable = HashBasedTable
			.create();
	private static Table<PrimitiveClassReference, String, String> vrScalarTypeTable = HashBasedTable
			.create();

	public static void init() {
		typeMap = new HashMap<PrimitiveClassReference, String>();
		typeMap.put(PrimitiveClassReference.INT8, "mxINT8_CLASS");
		typeMap.put(PrimitiveClassReference.INT16, "mxINT16_CLASS");
		typeMap.put(PrimitiveClassReference.INT32, "mxINT32_CLASS");
		typeMap.put(PrimitiveClassReference.SINGLE, "mxSINGLE_CLASS");
		typeMap.put(PrimitiveClassReference.DOUBLE, "mxDOUBLE_CLASS");
		typeMap.put(PrimitiveClassReference.LOGICAL,"mxLOGICAL_CLASS");

		vrTypeTable.put(PrimitiveClassReference.INT8, "REAL", "VrArrayI8");
		vrTypeTable.put(PrimitiveClassReference.INT16, "REAL", "VrArrayI16");
		vrTypeTable.put(PrimitiveClassReference.INT32, "REAL", "VrArrayI32");
		vrTypeTable.put(PrimitiveClassReference.SINGLE, "REAL", "VrArrayF32");
		vrTypeTable.put(PrimitiveClassReference.DOUBLE, "REAL", "VrArrayF64");
		vrTypeTable.put(PrimitiveClassReference.LOGICAL, "REAL", "VrArrayB");
		vrTypeTable.put(PrimitiveClassReference.INT8, "COMPLEX", "VrArrayCI8");
		vrTypeTable
				.put(PrimitiveClassReference.INT16, "COMPLEX", "VrArrayCI16");
		vrTypeTable
				.put(PrimitiveClassReference.INT32, "COMPLEX", "VrArrayCI32");
		vrTypeTable.put(PrimitiveClassReference.SINGLE, "COMPLEX",
				"VrArrayCF32");
		vrTypeTable.put(PrimitiveClassReference.DOUBLE, "COMPLEX",
				"VrArrayCF64");

		getterFuncTable.put(PrimitiveClassReference.DOUBLE, "REAL",
				"getVrArrayF64");
		getterFuncTable.put(PrimitiveClassReference.DOUBLE, "COMPLEX",
				"getVrArrayCF64");
		getterFuncTable.put(PrimitiveClassReference.SINGLE, "REAL",
				"getVrArrayF32");
		getterFuncTable.put(PrimitiveClassReference.SINGLE, "COMPLEX",
				"getVrArrayCF32");
		vrScalarTypeTable.put(PrimitiveClassReference.DOUBLE, "REAL", "double");
		vrScalarTypeTable.put(PrimitiveClassReference.SINGLE, "REAL", "float");
		vrScalarTypeTable.put(PrimitiveClassReference.SINGLE, "COMPLEX",
				"float complex");
		vrScalarTypeTable.put(PrimitiveClassReference.DOUBLE, "COMPLEX",
				"double complex");
		vrScalarTypeTable.put(PrimitiveClassReference.INT8, "REAL", "short");
		vrScalarTypeTable.put(PrimitiveClassReference.INT16, "REAL", "short");
		vrScalarTypeTable.put(PrimitiveClassReference.INT32, "REAL", "int");
		vrScalarTypeTable.put(PrimitiveClassReference.INT64, "REAL", "long");
	}

	public static String getMxType(PrimitiveClassReference type) {
		return typeMap.get(type);
	}

	public static String getFunc(PrimitiveClassReference type, String complexity) {
		return getterFuncTable.get(type, complexity);
	}

	public static String getVrType(PrimitiveClassReference type,
			String complexity) {
		return vrTypeTable.get(type, complexity);
	}

	public static String getVrScalarType(PrimitiveClassReference type,
			String complexity) {
		return vrScalarTypeTable.get(type, complexity);
	}

	public static String getMxComplexity(String complexity) {
		return complexity.equals("REAL") ? "mxREAL" : "mxCOMPLEX";
	}
}
