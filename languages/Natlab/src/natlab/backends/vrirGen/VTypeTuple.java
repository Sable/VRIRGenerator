package natlab.backends.vrirGen;

import java.util.ArrayList;

public class VTypeTuple extends VType {
	ArrayList<VType> elementList;

	public VTypeTuple() {
		// TODO Auto-generated constructor stub
		elementList = new ArrayList<VType>();
	}

	public void addElement(VType vtype) {
		elementList.add(vtype);
	}

	public ArrayList<VType> getElementList() {
		return elementList;
	}

	public void setElementList(ArrayList<VType> elementList) {
		this.elementList = elementList;
	}

	public StringBuffer toXML() {

		StringBuffer sb = new StringBuffer();
		sb.append(HelperClass.toXMLHead("tupletype"));
		for (VType vtype : elementList) {
			sb.append(vtype.toXML());
		}
		sb.append(HelperClass.toXMLTail());

		return sb;
	}
}
