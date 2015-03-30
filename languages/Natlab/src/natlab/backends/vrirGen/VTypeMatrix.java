package natlab.backends.vrirGen;

import natlab.tame.classes.reference.PrimitiveClassReference;
import natlab.tame.valueanalysis.aggrvalue.AggrValue;
import natlab.tame.valueanalysis.basicmatrix.BasicMatrixValue;
import natlab.tame.valueanalysis.components.shape.Shape;

public class VTypeMatrix extends VType {
	public static enum Layout {
		ROW_MAJOR("rowmajor"), COLUMN_MAJOR("colmajor"), STRIDE_MAJOR("stride");

		private String str;

		Layout(String str) {
			this.str = str;
		}

		public String getStr() {
			return str;
		}

		public void setStr(String str) {
			this.str = str;
		}

	}

	Shape<AggrValue<BasicMatrixValue>> shape;
	PrimitiveClassReference type;
	Layout layout;
	String complexity;
	boolean forceArray;

	VTypeMatrix(Shape<AggrValue<BasicMatrixValue>> shape,
			PrimitiveClassReference type, Layout layout, String complexity) {
		this.shape = shape;
		this.type = type;
		this.layout = layout;
		this.complexity = complexity;
		forceArray = false;

	}

	VTypeMatrix(Shape<AggrValue<BasicMatrixValue>> shape,
			PrimitiveClassReference type, Layout layout, String complexity,
			boolean force) {
		this.shape = shape;
		this.type = type;
		this.layout = layout;
		this.complexity = complexity;
		this.forceArray = force;
	}

	public int getComplexity() {
		return complexity.equals("complex") ? 1 : 0;
	}

	public void setComplexity(String complexity) {
		this.complexity = complexity;
	}

	public Shape<AggrValue<BasicMatrixValue>> getShape() {
		return shape;
	}

	public void setShape(Shape<AggrValue<BasicMatrixValue>> shape) {
		this.shape = shape;
	}

	public PrimitiveClassReference getType() {
		return type;
	}

	public void setType(PrimitiveClassReference type) {
		this.type = type;
	}

	public Layout getLayout() {
		return layout;
	}

	public void setLayout(Layout layout) {
		this.layout = layout;
	}

	private String getVarType() {

		return VrirTypeMapper.getType(type.getName());

	}

	private String getLayoutString() {
		return layout.getStr();
	}

	private void genScalarXML(StringBuffer vTypeXML) {
		vTypeXML.append("(" + getVarType() +" :ctype "+ getComplexity() + ")");
	}

	private void genArrayXML(StringBuffer vTypeXML) {
		if (HelperClass.isScalar(shape.getDimensions())) {
			System.out.println("in is scalar in array");
			genScalarXML(vTypeXML);
			return;
		}
		vTypeXML.append("( arraytype :layout " + getLayoutString() + " :ndims "
				+ getShape().getDimensions().size());
		genScalarXML(vTypeXML);
		vTypeXML.append(")");
	}

	public StringBuffer toXML() {
		StringBuffer vTypeXML = new StringBuffer();

		if (shape.isScalar() && !forceArray) {
			genScalarXML(vTypeXML);

		} else {
			genArrayXML(vTypeXML);
		}

		return vTypeXML;
	}

	public StringBuffer toXML(boolean reqScal) {

		StringBuffer vTypeXML = new StringBuffer();
		if (reqScal) {
			genScalarXML(vTypeXML);
			return vTypeXML;
		}
		if (shape.isScalar() && !forceArray) {
			genScalarXML(vTypeXML);

		} else {
			genArrayXML(vTypeXML);
		}

		return vTypeXML;
	}
}
