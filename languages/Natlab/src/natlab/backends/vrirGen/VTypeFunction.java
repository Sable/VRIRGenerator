package natlab.backends.vrirGen;

import java.util.ArrayList;

public class VTypeFunction extends VType {
	ArrayList<VType> inType;
	ArrayList<VType> outType;

	public VTypeFunction() {
		// TODO Auto-generated constructor stub
		inType = new ArrayList<>();
		outType = new ArrayList<>();
	}

	public VTypeFunction(ArrayList<VType> intype, ArrayList<VType> outtype) {
		this.inType = intype;
		this.outType = outtype;
	}

	public void addInType(VType vtype) {
		inType.add(vtype);
	}

	public void addOutType(VType vtype) {
		outType.add(vtype);
	}

	public ArrayList<VType> getInType() {
		return inType;
	}

	public void setInType(ArrayList<VType> inType) {
		this.inType = inType;
	}

	public ArrayList<VType> getOutType() {
		return outType;
	}

	public void setOutType(ArrayList<VType> outType) {
		this.outType = outType;
	}

	@Override
	public StringBuffer toXML() {
		// TODO Auto-generated method stub
		StringBuffer sb = new StringBuffer();
		sb.append(HelperClass.toXMLHead("vtype name=\"func\""));
		sb.append(HelperClass.toXMLHead("intypes"));
		for (VType vtype : inType) {

			sb.append(vtype.toXML());
		}
		sb.append(HelperClass.toXMLHead("/intypes"));
		sb.append(HelperClass.toXMLHead("outtypes"));
		if (outType.size() > 0) {
			for (VType vtype : outType) {
				sb.append(vtype.toXML());
			}
		} else {
			sb.append(HelperClass.toXMLHead("vtype name=\"void\" /"));
		}
		sb.append(HelperClass.toXMLHead("/outtypes"));
		sb.append(HelperClass.toXMLHead("/vtype"));
		return sb;
	}

}
