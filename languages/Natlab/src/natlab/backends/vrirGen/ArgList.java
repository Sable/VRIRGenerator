package natlab.backends.vrirGen;

import java.util.ArrayList;

public class ArgList {
	private ArrayList<Arg> inList;

	public ArgList() {
		inList = new ArrayList<Arg>();

	}

	public ArgList(ArrayList<Arg> inList) {
		this.inList = inList;

	}

	public StringBuffer toXML() {
		StringBuffer sb = new StringBuffer();
		sb.append(HelperClass.toXML("arglist"));
		for (Arg arg : inList) {
			sb.append(arg.toXML());
		}
		sb.append(HelperClass.toXML("/arglist"));
		return sb;
	}
}
