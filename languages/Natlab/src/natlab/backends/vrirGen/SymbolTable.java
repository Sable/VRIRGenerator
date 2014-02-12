package natlab.backends.vrirGen;

import java.util.HashMap;
import java.util.Map;

public class SymbolTable {

	private Map<String, Symbol> symbolMap;
	private int currentId;

	public int getCurrentId() {
		return currentId;
	}

	public void setCurrentId(int id) {
		this.currentId = id;
	}

	public int getId(String name) {
		return symbolMap.get(name).getId();
	}

	public void incId() {
		currentId++;
	}

	public SymbolTable() {
		symbolMap = new HashMap<String, Symbol>();
		currentId = 0;
	}

	public Symbol getSymbol(String name) {
		return symbolMap.get(name);
	}

	public void putSymbol(VTypeMatrix vtype, String name, int id) {
		symbolMap.put(name, new Symbol(vtype, name, id));
		this.currentId = id;
	}

	public void putSymbol(VType vtype, String name) {
		symbolMap.put(name, new Symbol(vtype, name, getCurrentId()));
		incId();
	}

	public void putSymbol(String name, Symbol sym) {
		symbolMap.put(name, sym);
	}

	public Map<String, Symbol> getSymbolMap() {
		return symbolMap;
	}

	public void setSymbolMap(Map<String, Symbol> symbolMap) {
		this.symbolMap = symbolMap;
	}

	public boolean contains(String name) {
		return symbolMap.containsKey(name);
	}

	public StringBuffer toXML() {
		StringBuffer xmlBuff = new StringBuffer();
		xmlBuff.append("<symtable>\n");
		for (Symbol sym : symbolMap.values()) {

			xmlBuff.append(sym.toXML());
		}
		return xmlBuff.append("</symtable>\n");

	}
}
