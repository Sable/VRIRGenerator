package natlab.backends.vrirGen;

import natlab.tame.classes.reference.PrimitiveClassReference;
import natlab.tame.valueanalysis.advancedMatrix.AdvancedMatrixValue;
import natlab.tame.valueanalysis.aggrvalue.AggrValue;
import natlab.tame.valueanalysis.components.shape.Shape;

public class VTypeMatrix extends VType {
	public static enum Layout {
		ROW_MAJOR("rowmajor"), COLUMN_MAJOR("colmajor"), STRIDE_MAJOR(
				"stride");

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

	Shape<AggrValue<AdvancedMatrixValue>> shape;
	PrimitiveClassReference type;
	Layout layout;
	String complexity;

	VTypeMatrix(Shape<AggrValue<AdvancedMatrixValue>> shape,
			PrimitiveClassReference type, Layout layout, String complexity) {
		this.shape = shape;
		this.type = type;
		this.layout = layout;
		this.complexity = complexity;

	}

	public String getComplexity() {
		return complexity;
	}

	public void setComplexity(String complexity) {
		this.complexity = complexity;
	}

	public Shape<AggrValue<AdvancedMatrixValue>> getShape() {
		return shape;
	}

	public void setShape(Shape<AggrValue<AdvancedMatrixValue>> shape) {
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
		vTypeXML.append("<vtype ctype= \"" + getComplexity() + "\" name= \""
				+ getVarType() + "\"");
		vTypeXML.append(">\n");
		vTypeXML.append("</vtype>\n");
	}

	private void genArrayXML(StringBuffer vTypeXML) {
		vTypeXML.append("<vtype layout= \"" + getLayoutString()
				+ "\" name=\"array\" ndims=\""
				+ getShape().getDimensions().size() + "\">\n");
		genScalarXML(vTypeXML);
		vTypeXML.append("</vtype>\n");
	}

	public StringBuffer toXML() {
		StringBuffer vTypeXML = new StringBuffer();

		if (shape.isScalar()) {
			genScalarXML(vTypeXML);

		} else {
			genArrayXML(vTypeXML);
		}

		return vTypeXML;
	}
}
