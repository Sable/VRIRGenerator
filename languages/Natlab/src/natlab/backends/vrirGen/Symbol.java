package natlab.backends.vrirGen;

public class Symbol {
	VType vtype;
	String name;
	int id;

	public Symbol(VType vtype, String name, int id) {
		this.vtype = vtype;
		this.name = name;
		this.id = id;
	}

	public VType getVtype() {
		return vtype;
	}

	public void setVtype(VTypeMatrix vtype) {
		this.vtype = vtype;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public StringBuffer toXML() {
		StringBuffer xmlBuff = new StringBuffer();
		xmlBuff.append("<sym id=\"" + getId() + "\" name=\"" + getName()
				+ "\">\n");
		if (getVtype() == null) {
			System.out.println("name of null element" + this.getName());
		}
		xmlBuff.append(getVtype().toXML());
		return xmlBuff.append("</sym>\n");

	}
}
