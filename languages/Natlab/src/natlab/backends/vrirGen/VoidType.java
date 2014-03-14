package natlab.backends.vrirGen;

import natlab.backends.x10.codegen.Helper;

public class VoidType extends VType {

	public VoidType() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public StringBuffer toXML() {
		// TODO Auto-generated method stub

		return new StringBuffer(HelperClass.toXML("vtype name=\"void\"") + 
				 HelperClass.toXML("/vtype"));
	}

}
