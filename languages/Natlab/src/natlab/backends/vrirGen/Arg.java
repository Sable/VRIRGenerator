package natlab.backends.vrirGen;

public class Arg {
	private int id;
	private boolean restrict;
	private int aliasGroup;

	@SuppressWarnings("unused")
	private Arg() {
		id = 0;
		restrict = false;
		aliasGroup = Integer.MIN_VALUE;
	}

	public Arg(int id, boolean restrict) {
		this.id = id;
		this.restrict = restrict;
		aliasGroup = Integer.MIN_VALUE;
	}

	public StringBuffer toXML() {
		StringBuffer sb = new StringBuffer();
		String head = "arg id=\"" + id + "\" restrict=\"" + restrict + "\"";
		if (aliasGroup != Integer.MIN_VALUE) {
			head += aliasGroup;
		}
		sb.append(HelperClass.toXML(head));
		sb.append(HelperClass.toXML("/arg"));
		return sb;
	}

	private int getAliasGroup() {
		return aliasGroup;
	}

	private void setAliasGroup(int aliasGroup) {
		this.aliasGroup = aliasGroup;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isRestrict() {
		return restrict;
	}

	public void setRestrict(boolean restrict) {
		this.restrict = restrict;
	}
}
