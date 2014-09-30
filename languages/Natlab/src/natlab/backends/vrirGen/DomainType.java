package natlab.backends.vrirGen;

public class DomainType extends VType {

	private int ndims;
	private VType[] vtype;

	DomainType(int ndims, VType[] vtype) {
		this.ndims = ndims;
		this.vtype = vtype;
	}

	DomainType() {
		this.ndims = 0;
		vtype = null;
	}

	DomainType(VType vtype[]) {
		this.vtype = vtype;
		this.ndims = 1;
	}

	@Override
	public StringBuffer toXML() {
		// TODO Auto-generated method stub
		StringBuffer genIR = new StringBuffer();
		genIR.append("( domaintype :ndims " + ndims + " ");
		for (int i = 0; i < ndims; i++) {
			genIR.append(vtype[i].toXML());
		}
		genIR.append(")");
		return genIR;
	}

}