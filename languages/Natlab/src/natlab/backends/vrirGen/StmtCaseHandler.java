package natlab.backends.vrirGen;

import ast.AssignStmt;
import ast.BreakStmt;
import ast.ContinueStmt;
import ast.ForStmt;
import ast.IfBlock;
import ast.IfStmt;
import ast.Name;
import ast.NameExpr;
import ast.ReturnStmt;
import ast.Stmt;
import ast.WhileStmt;

public class StmtCaseHandler {
	public static void handleAssignStmt(AssignStmt node, VrirXmlGen gen) {
		gen.appendToPrettyCode(toXMLHead("assignstmt"));
		gen.appendToPrettyCode(HelperClass.toXML("lhs"));
		node.getLHS().analyze(gen);
		gen.appendToPrettyCode(HelperClass.toXML("/lhs"));
		gen.appendToPrettyCode(HelperClass.toXML("rhs"));
		node.getRHS().analyze(gen);
		gen.appendToPrettyCode(HelperClass.toXML("/rhs"));
		gen.appendToPrettyCode(toXMLTail());
	}

	public static void handleWhileStmt(WhileStmt node, VrirXmlGen gen) {

		gen.appendToPrettyCode(toXMLHead("whilestmt"));
		gen.appendToPrettyCode(HelperClass.toXML("test"));
		node.getExpr().analyze(gen);

		gen.appendToPrettyCode(HelperClass.toXML("/test"));
		gen.appendToPrettyCode(toListXMLHead(VrirXmlGen.onGPU));
		for (int i = 0; i < node.getStmtList().getNumChild(); i++) {

			node.getStmt(i).analyze(gen);
		}
		gen.appendToPrettyCode(toListXMLTail());
		gen.appendToPrettyCode(toXMLTail());

	}

	public static void handleBreakStmt(BreakStmt node, VrirXmlGen gen) {
		gen.appendToPrettyCode(toXMLHead("breakstmt"));
		gen.appendToPrettyCode(toXMLTail());
	}

	public static void handleContinueStmt(ContinueStmt node, VrirXmlGen gen) {
		gen.appendToPrettyCode(toXMLHead("contstmt"));
		gen.appendToPrettyCode(toXMLTail());
	}

	public static void handleForStmt(ForStmt node, VrirXmlGen gen) {
		gen.appendToPrettyCode(toXMLHead("forstmt"));
		// TODO: Currently on range expressions are handled. Need to also handle
		// DomainExpr and ArrayIterator.
		gen.appendToPrettyCode(HelperClass.toXML("domain"));
		node.getAssignStmt().getRHS().analyze(gen);
		gen.appendToPrettyCode(HelperClass.toXML("/domain"));
		gen.appendToPrettyCode(HelperClass.toXML("itervars"));

		if (node.getAssignStmt().getLHS() instanceof NameExpr) {
			Symbol sym = gen.getSymbol(((NameExpr) ((Object) node
					.getAssignStmt().getLHS())).getVarName());
			if (sym == null) {
				VType vtype = HelperClass.generateVType(gen.getAnalysis(), gen
						.getIndex(), ((NameExpr) ((Object) node.getAssignStmt()
						.getLHS())).getName().getID());
				gen.addToSymTab(vtype, ((NameExpr) ((Object) node
						.getAssignStmt().getLHS())).getName().getVarName());
			}
			sym = gen.getSymbol(((NameExpr) ((Object) node.getAssignStmt()
					.getLHS())).getVarName());

			gen.appendToPrettyCode(sym.toXML());
		}

		gen.appendToPrettyCode(HelperClass.toXML("/itervars"));
		gen.appendToPrettyCode(HelperClass.toXML("body"));
		gen.appendToPrettyCode(toListXMLHead(VrirXmlGen.onGPU));

		for (Stmt stmt : node.getStmtList()) {
			stmt.analyze(gen);
		}

		gen.appendToPrettyCode(toListXMLTail());
		gen.appendToPrettyCode(HelperClass.toXML("/body"));
		gen.appendToPrettyCode(toXMLTail());
	}

	public static void handleReturnStmt(ReturnStmt node, VrirXmlGen gen) {
		gen.setHasReturnStmt(true);
		gen.appendToPrettyCode(toXMLHead("returnstmt"));
		gen.appendToPrettyCode(HelperClass.toXML("exprs"));
		for (Name rvar : gen.getFunctionNode().getOutputParamList()) {
			if (!gen.getSymTab().contains(rvar.getID())) {
				VType vtype = HelperClass.generateVType(gen.getAnalysis(),
						gen.getIndex(), rvar.getID());
				gen.getSymTab().putSymbol(vtype, rvar.getID());
			}
			if (gen.getSymbol(rvar.getID()) != null) {
				gen.appendToPrettyCode(ExprCaseHandler.toXMLHead("name", gen
						.getSymbol(rvar.getID()).getId(), "id"));
			} else {
				throw new NullPointerException("Symbol not found for "
						+ rvar.getID());
			}
			gen.appendToPrettyCode(gen.getSymbol(rvar.getID()).getVtype()
					.toXML());
			gen.appendToPrettyCode(ExprCaseHandler.toXMLTail());
		}

		gen.appendToPrettyCode(HelperClass.toXML("/exprs"));
		gen.appendToPrettyCode(toXMLTail());
	}

	public static void handleIfStmt(IfStmt node, VrirXmlGen gen) {
		for (IfBlock ifblock : node.getIfBlockList()) {

			gen.appendToPrettyCode(toXMLHead("ifstmt"));
			gen.appendToPrettyCode(HelperClass.toXML("test"));

			ifblock.getCondition().analyze(gen);
			gen.appendToPrettyCode(HelperClass.toXML("/test"));
			gen.appendToPrettyCode(HelperClass.toXML("if"));
			gen.appendToPrettyCode(toListXMLHead(false));
			for (Stmt stmt : ifblock.getStmtList()) {

				stmt.analyze(gen);
			}
			gen.appendToPrettyCode(toListXMLTail());
			gen.appendToPrettyCode(HelperClass.toXML("/if"));

		}
		if (node.hasElseBlock()) {
			gen.appendToPrettyCode(HelperClass.toXML("else"));

			for (Stmt stmt : node.getElseBlock().getStmtList()) {
				stmt.analyze(gen);
			}
			gen.appendToPrettyCode(HelperClass.toXML("/else"));
		}
		gen.appendToPrettyCode(toXMLTail());
	}

	public static StringBuffer toListXMLHead(boolean onGpu) {
		StringBuffer buff = new StringBuffer();
		buff.append(HelperClass.toXML("stmt name=\"stmtlist\" onGpu=\""
				+ Boolean.toString(onGpu) + "\""));
		buff.append(HelperClass.toXML("stmts"));
		return buff;
	}

	public static StringBuffer toListXMLTail() {
		return new StringBuffer(HelperClass.toXML("/stmts") + toXMLTail());
	}

	public static StringBuffer toXMLHead(String name) {
		return new StringBuffer(HelperClass.toXML("stmt name=\"" + name + "\""));
	}

	public static StringBuffer toXMLTail() {
		return new StringBuffer(HelperClass.toXML("/stmt"));
	}
}
